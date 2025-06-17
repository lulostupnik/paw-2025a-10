package ar.edu.itba.paw.models.exceptions;

public class UserValidatedException extends RuntimeException {

    public UserValidatedException() {}
    public UserValidatedException(String email) {
        super(String.format("User with email %s already validated", email));
    }
}
