package ar.edu.itba.paw.models.exceptions;

public class UserNotFoundException extends BusinessException {

    public UserNotFoundException() {
        super("exception.UserNotFoundException", BusinessException.NOT_FOUND);
    }
}
