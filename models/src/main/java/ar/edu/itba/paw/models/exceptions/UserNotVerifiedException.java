package ar.edu.itba.paw.models.exceptions;

public class UserNotVerifiedException extends CustomRuntimeException {

    public UserNotVerifiedException() {
        super("exception.UserNotVerifiedException", CustomRuntimeException.FORBIDDEN);
    }

    public UserNotVerifiedException(String message) {
        super("exception.UserNotVerifiedException", CustomRuntimeException.FORBIDDEN);
    }
}
