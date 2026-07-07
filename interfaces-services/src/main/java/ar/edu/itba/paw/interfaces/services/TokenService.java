package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Token;
import ar.edu.itba.paw.models.User;
import java.util.Optional;

public interface TokenService {


    String userTokenControl(User user);

    Optional<Token> getByToken(String token);

    void delete(Token token);

    void deleteExpiredTokens();

    boolean isTokenValid(Token token, long userId);

}
