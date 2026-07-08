package ar.edu.itba.paw.models.exceptions;

public class UserAlreadyAttendingException extends BusinessException {
    public UserAlreadyAttendingException(long id, long eventId) {
        super("exception.UserAlreadyAttendingException", BusinessException.CONFLICT);
    }
}
