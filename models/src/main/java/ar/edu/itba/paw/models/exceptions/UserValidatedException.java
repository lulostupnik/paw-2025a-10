package ar.edu.itba.paw.models.exceptions;

public class UserValidatedException extends RuntimeException {
    public UserValidatedException(String message) {
        super(message);
    }
    public UserValidatedException() {}
}
