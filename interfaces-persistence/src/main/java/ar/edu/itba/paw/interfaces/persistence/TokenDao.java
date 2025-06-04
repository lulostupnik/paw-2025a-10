package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Token;
import ar.edu.itba.paw.models.User;

import java.time.LocalDateTime;
import java.util.Optional;

public interface TokenDao {

    Token create(User user, String token, LocalDateTime expirationDate);

    Optional<Token> findByToken(String token);

    Optional<Token> findByUserId(long userId);

    void deleteByToken(Token token);

    void deleteExpiredTokens();
}
