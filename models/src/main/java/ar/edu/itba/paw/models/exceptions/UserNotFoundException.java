package ar.edu.itba.paw.models.exceptions;

public class UserNotFoundException extends NotFoundException {
    public UserNotFoundException(long id) {
        super(String.format("User with id %d not found", id));
    }
    public UserNotFoundException(String email) {
        super(String.format("User with email %s not found", email));
    }
}
