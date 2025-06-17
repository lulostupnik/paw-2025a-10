package ar.edu.itba.paw.models.exceptions;

public class UserAlreadyAttendingException extends InvalidException {
    public UserAlreadyAttendingException(long id, long eventId) {
        super(String.format("User %d is already attending event with id %d", id, eventId));
    }
}
