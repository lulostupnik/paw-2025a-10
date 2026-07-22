package ar.edu.itba.paw.models.exceptions;

public class InvalidTokenException extends BusinessException {

    public InvalidTokenException() {
        super("exception.InvalidTokenException", BusinessException.UNAUTHORIZED);
    }
}
