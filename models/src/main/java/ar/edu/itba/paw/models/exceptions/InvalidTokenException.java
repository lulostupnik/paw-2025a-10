package ar.edu.itba.paw.models.exceptions;

public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(String message) {
        super(message);
    }
    public InvalidTokenException(String message, String token) {
        super(String.format("%s: %s", message, token));
    }
}
