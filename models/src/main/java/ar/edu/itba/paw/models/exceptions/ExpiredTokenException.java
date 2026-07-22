package ar.edu.itba.paw.models.exceptions;

public class ExpiredTokenException extends BusinessException {

    public ExpiredTokenException() {
        super("exception.ExpiredTokenException", BusinessException.UNAUTHORIZED);
    }
}
