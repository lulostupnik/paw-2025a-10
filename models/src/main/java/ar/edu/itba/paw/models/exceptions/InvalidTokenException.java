package ar.edu.itba.paw.models.exceptions;

public class InvalidTokenException extends RuntimeException {

    public InvalidTokenException(String token) {
        super(String.format("Invalid token: %s", token));
    }
}
