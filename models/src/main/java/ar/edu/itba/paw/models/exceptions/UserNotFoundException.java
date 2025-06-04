package ar.edu.itba.paw.models.exceptions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
    public UserNotFoundException(long id) {
        super(String.format("User with id %d not found", id));
    }

}
