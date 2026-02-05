package ar.edu.itba.paw.services;

import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import ar.edu.itba.paw.models.exceptions.InvalidTokenException;
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
    private static final User USER = new User(TOKEN_VALUE, TOKEN_VALUE, TOKEN_VALUE, TOKEN_VALUE, null, null, TOKEN_ID, TOKEN_VALUE, null, false);
    private static final Token TOKEN = new Token(TOKEN_ID, USER, TOKEN_VALUE, TOKEN_EXPIRATION);
    
    @InjectMocks
    private TokenServiceImpl tokenService;

    @Mock
    private TokenDao tokenDao;

    @Test
    public void testUserControlToken(){
        Token token = tokenService.userTokenControl(USER);

        assertNotNull(token);
        assertNotNull(token.getToken());
        assertTrue(token.getToken().length() > 10);
        assertNotNull(token.getExpirationDate());
        assertEquals(USER, token.getUser());
    }    
    @Test
    public void testUserControlTokenUserHasTokenNotExpired(){
        User newUser = new User(
            TOKEN_VALUE, 
            TOKEN_VALUE, 
            TOKEN_VALUE, 
            TOKEN_VALUE, 
            null, 
            null, 
            TOKEN_ID, 
            null, 
            false
        );
        Token newToken = new Token(newUser, TOKEN_VALUE, TOKEN_EXPIRATION);
        newUser.setToken(newToken);

        Token token = tokenService.userTokenControl(newUser);

        assertNotNull(token);
        assertEquals(newToken, token);
    }  
    @Test
    public void testUserControlTokenUserHasTokenWithoutExpiration(){
        User newUser = new User(
            TOKEN_VALUE, 
            TOKEN_VALUE, 
            TOKEN_VALUE, 
            TOKEN_VALUE, 
            null, 
            null, 
            TOKEN_ID, 
            null, 
            false
        );
        Token newToken = new Token(newUser, TOKEN_VALUE, null);
        newUser.setToken(newToken);

        Token token = tokenService.userTokenControl(newUser);

        assertNotNull(token);
        assertEquals(newToken, token);
    }  
    @Test
    public void testUserControlTokenUserHasTokenExpired(){
        User newUser = new User(
            TOKEN_VALUE, 
            TOKEN_VALUE, 
            TOKEN_VALUE, 
            TOKEN_VALUE, 
            null, 
            null, 
            TOKEN_ID, 
            null, 
            false
        );
        Token newToken = new Token(
            newUser, 
            TOKEN_VALUE, 
            TOKEN_EXPIRATION.plusDays(-10)
        );
        newUser.setToken(newToken);

        Token token = tokenService.userTokenControl(newUser);

        assertNotNull(token);
        assertNotNull(token.getToken());
        assertNotEquals(TOKEN_VALUE, token.getToken());
        assertTrue(token.getToken().length() > 10);
        assertNotNull(token.getExpirationDate());
        assertNotEquals(TOKEN_EXPIRATION, token.getExpirationDate());
    }  

    @Test
    public void testGetByToken(){
        when(
            tokenDao.findByToken(eq(TOKEN_VALUE))
        ).thenReturn(Optional.of(TOKEN));

        Optional<Token> maybeToken = tokenService.getByToken(TOKEN_VALUE);

        assertNotNull(maybeToken);
        assertEquals(TOKEN, maybeToken.get());
    }

    @Test(expected = InvalidTokenException.class)
    public void testCheckTokenValidityInvalid() {
        when(tokenDao.findByToken(eq(TOKEN_VALUE)))
                .thenReturn(Optional.empty());

        tokenService.checkTokenValidity(TOKEN_VALUE);
    }
    @Test(expected = InvalidTokenException.class)
    public void testCheckTokenValidityExpired() {
        when(tokenDao.findByToken(eq(TOKEN_VALUE)))
                .thenReturn(Optional.of(new Token(USER, TOKEN_VALUE, LocalDateTime.now().minusDays(10))));

        tokenService.checkTokenValidity(TOKEN_VALUE);
    }

}
