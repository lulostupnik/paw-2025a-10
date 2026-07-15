package ar.edu.itba.paw.models.exceptions;

public class UserValidatedException extends BusinessException {

    public UserValidatedException() {
        super("exception.UserValidatedException", BusinessException.BAD_REQUEST);
    }
}
