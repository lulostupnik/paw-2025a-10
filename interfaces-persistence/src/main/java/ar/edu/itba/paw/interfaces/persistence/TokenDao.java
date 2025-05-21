package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Token;
import ar.edu.itba.paw.models.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

public interface TokenDao {

    Token create(User user, String token, LocalDateTime expirationDate);

    Optional<Token> findByToken(String token);

    Optional<Token> findByUserId(long userId);

    boolean existsByToken(String token);

    boolean existsByTokenAndNotExpired(String token);

    void deleteByToken(String token);

    void deleteByUserId(long userId);

    void updateExpiration(String token, LocalDateTime newExpirationDate);
}
