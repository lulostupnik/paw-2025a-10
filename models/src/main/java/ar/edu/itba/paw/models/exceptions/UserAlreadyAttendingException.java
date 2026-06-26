package ar.edu.itba.paw.models.exceptions;

public class UserAlreadyAttendingException extends CustomRuntimeException {
    public UserAlreadyAttendingException(long id, long eventId) {
        super("exception.UserAlreadyAttendingException", CustomRuntimeException.CONFLICT);
    }
}
