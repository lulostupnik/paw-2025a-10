package ar.edu.itba.paw.models.exceptions;

public class InvalidTokenException extends BusinessException {

    public InvalidTokenException(String token) {
        super("exception.InvalidTokenException", BusinessException.UNAUTHORIZED);
    }
}
