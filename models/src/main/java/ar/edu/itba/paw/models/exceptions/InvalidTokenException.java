package ar.edu.itba.paw.models.exceptions;

public class InvalidTokenException extends CustomRuntimeException {

    public InvalidTokenException(String token) {
        super("exception.InvalidTokenException", CustomRuntimeException.UNAUTHORIZED);
    }
}
