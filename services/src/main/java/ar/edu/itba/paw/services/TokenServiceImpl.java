package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.TokenDao;
import ar.edu.itba.paw.interfaces.services.TokenService;
import ar.edu.itba.paw.models.Token;
import ar.edu.itba.paw.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.token.Sha512DigestUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class TokenServiceImpl implements TokenService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TokenServiceImpl.class);
    private final TokenDao tokenDao;
    private static final Integer TOKEN_DURATION_DAYS = 1;


    @Autowired
    public TokenServiceImpl(final TokenDao tokenDao) {
        this.tokenDao = tokenDao;
    }


    @Transactional
    @Override
    public String userTokenControl(User user) {
        final String rawToken = generateToken();
        final String hashedToken = hashToken(rawToken);
        Token token = user.getToken();
        if (token != null) {
            token.setToken(hashedToken);
            token.setExpirationDate(generateTokenExpirationDate());
            LOGGER.info("Token was refreshed for user {}", user.getId());
        } else {
            token = new Token(user, hashedToken, generateTokenExpirationDate());
            user.setToken(token);
            LOGGER.info("Token was created for user {}", user.getId());
        }
        return rawToken;
    }

    @Override
    public Optional<Token> getByToken(String token) {
        return tokenDao.findByToken(hashToken(token));
    }

    @Transactional
    @Override
    public void delete(Token token) {
        tokenDao.deleteByToken(token);
        LOGGER.info("Token deleted for user {}", token.getUser().getId());
    }

    @Override
    public boolean isTokenValid(final Token token, final long userId) {
        return !token.isExpired() && token.getUser().getId().longValue() == userId;
    }

    @Transactional
    @Override
    @Scheduled(cron = "0 0 0 * * ?")
    public void deleteExpiredTokens() {
        tokenDao.deleteExpiredTokens();
    }

    private static LocalDateTime generateTokenExpirationDate() {
        return LocalDateTime.now().plusDays(TOKEN_DURATION_DAYS);
    }

    private static String generateToken() {
        return UUID.randomUUID().toString().substring(0, 32);
    }

    private static String hashToken(final String rawToken) {
        return Sha512DigestUtils.shaHex(rawToken);
    }

}
