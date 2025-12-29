package ar.edu.itba.paw.models.exceptions;

public class UserNotVerifiedException extends RuntimeException {

    public UserNotVerifiedException() {
        super();
    }

    public UserNotVerifiedException(String message) {
        super(message);
    }
}
