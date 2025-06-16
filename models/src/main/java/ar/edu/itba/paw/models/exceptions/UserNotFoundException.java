package ar.edu.itba.paw.models.exceptions;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String message) {
        super(message);
    }
    public UserNotFoundException(long id) {
        super(String.format("User with id %d not found", id));
    }
    public UserNotFoundException(String email, String username) {
        super(String.format("User with email %s or username %s not found", email, username));
    }

}
