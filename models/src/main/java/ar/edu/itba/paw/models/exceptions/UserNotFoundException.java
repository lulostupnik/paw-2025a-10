package ar.edu.itba.paw.models.exceptions;

public class UserNotFoundException extends CustomRuntimeException {
    public UserNotFoundException(long id) {
        super("exception.UserNotFoundException", CustomRuntimeException.NOT_FOUND);
    }
    public UserNotFoundException(String email) {
        super("exception.UserNotFoundException", CustomRuntimeException.NOT_FOUND);
    }
}
