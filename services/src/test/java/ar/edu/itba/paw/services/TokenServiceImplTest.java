package ar.edu.itba.paw.services;

import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import ar.edu.itba.paw.interfaces.persistence.TokenDao;
import ar.edu.itba.paw.models.Token;
import ar.edu.itba.paw.models.User;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class TokenServiceImplTest {

    private static final long TOKEN_ID = 1;
    private static final String TOKEN_VALUE = "token";
    private static final LocalDateTime TOKEN_EXPIRATION = LocalDateTime.now().plusDays(1);

    private static final long OWNER_ID = 7;
    private static final User TOKEN_OWNER = new User(OWNER_ID, TOKEN_VALUE, TOKEN_VALUE, TOKEN_VALUE, TOKEN_VALUE, null, null, null, null, false, true);
    private static final Token TOKEN = new Token(TOKEN_ID, TOKEN_OWNER, TOKEN_VALUE, TOKEN_EXPIRATION);

    @InjectMocks
    private TokenServiceImpl tokenService;

    @Mock
    private TokenDao tokenDao;

    @Test
    public void testUserTokenControlCreatesToken(){
        User user = new User(TOKEN_VALUE, TOKEN_VALUE, TOKEN_VALUE, TOKEN_VALUE, null, null, TOKEN_ID, null, false);

        String rawToken = tokenService.issueUserToken(user);

        assertNotNull(rawToken);
        assertTrue(rawToken.length() > 10);
        assertNotNull(user.getToken());
        assertEquals(user, user.getToken().getUser());
        assertNotNull(user.getToken().getExpirationDate());
        assertNotEquals(rawToken, user.getToken().getToken());   // stored hashed, never the raw
        assertTrue(user.getToken().getToken().length() <= 100);
    }

    @Test
    public void testUserTokenControlRefreshesExistingToken(){
        User user = new User(TOKEN_VALUE, TOKEN_VALUE, TOKEN_VALUE, TOKEN_VALUE, null, null, TOKEN_ID, null, false);
        Token existingToken = new Token(user, TOKEN_VALUE, TOKEN_EXPIRATION);
        user.setToken(existingToken);

        String rawToken = tokenService.issueUserToken(user);

        assertSame(existingToken, user.getToken());              // reuses the same row
        assertNotEquals(TOKEN_VALUE, existingToken.getToken());  // refreshed with a new hash
        assertNotEquals(rawToken, existingToken.getToken());     // stored hashed, never the raw
    }

    @Test
    public void testGetByTokenHashesBeforeLookup(){
        when(tokenDao.findByToken(anyString())).thenReturn(Optional.of(TOKEN));

        Optional<Token> maybeToken = tokenService.getByToken(TOKEN_VALUE);

        assertTrue(maybeToken.isPresent());
        assertEquals(TOKEN, maybeToken.get());
        verify(tokenDao).findByToken(argThat(queried -> !TOKEN_VALUE.equals(queried)));   // looked up by hash
    }

    @Test
    public void testIsTokenValidValid() {
        final Token token = new Token(TOKEN_ID, TOKEN_OWNER, TOKEN_VALUE, LocalDateTime.now().plusDays(1));

        assertTrue(tokenService.isTokenValid(token, OWNER_ID));
    }

    @Test
    public void testIsTokenValidExpired() {
        final Token token = new Token(TOKEN_ID, TOKEN_OWNER, TOKEN_VALUE, LocalDateTime.now().minusDays(1));

        assertFalse(tokenService.isTokenValid(token, OWNER_ID));
    }

    @Test
    public void testIsTokenValidWrongUser() {
        final Token token = new Token(TOKEN_ID, TOKEN_OWNER, TOKEN_VALUE, LocalDateTime.now().plusDays(1));

        assertFalse(tokenService.isTokenValid(token, OWNER_ID + 999));
    }

}
