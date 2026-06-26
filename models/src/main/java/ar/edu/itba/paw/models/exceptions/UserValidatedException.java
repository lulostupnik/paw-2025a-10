package ar.edu.itba.paw.models.exceptions;

public class UserValidatedException extends CustomRuntimeException {

    public UserValidatedException() {
        super("exception.UserValidatedException", CustomRuntimeException.BAD_REQUEST);
    }
    public UserValidatedException(String email) {
        super("exception.UserValidatedException", CustomRuntimeException.BAD_REQUEST);
    }
}
