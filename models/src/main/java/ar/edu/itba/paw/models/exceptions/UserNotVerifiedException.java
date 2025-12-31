package ar.edu.itba.paw.models.exceptions;

public class UserNotVerifiedException extends RuntimeException{
    public UserNotVerifiedException(String email) {
        super(String.format("User with email %s not found", email));
    }

}
