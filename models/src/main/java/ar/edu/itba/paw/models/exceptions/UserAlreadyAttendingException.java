package ar.edu.itba.paw.models.exceptions;

public class UserAlreadyAttendingException extends BusinessException {

    public UserAlreadyAttendingException() {
        super("exception.UserAlreadyAttendingException", BusinessException.CONFLICT);
    }
}
