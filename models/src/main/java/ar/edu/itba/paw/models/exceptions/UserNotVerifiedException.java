package ar.edu.itba.paw.models.exceptions;

public class UserNotVerifiedException extends BusinessException {

    public UserNotVerifiedException() {
        super("exception.UserNotVerifiedException", BusinessException.FORBIDDEN);
    }
}
