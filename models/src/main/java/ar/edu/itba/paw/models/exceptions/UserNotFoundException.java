package ar.edu.itba.paw.models.exceptions;

public class UserNotFoundException extends BusinessException {
    public UserNotFoundException(long id) {
        super("exception.UserNotFoundException", BusinessException.NOT_FOUND);
    }
    public UserNotFoundException(String email) {
        super("exception.UserNotFoundException", BusinessException.NOT_FOUND);
    }
}
