package ar.edu.itba.paw.models.exceptions;

public class EventAttendanceRequiredException extends BusinessException {
    public EventAttendanceRequiredException(long userId, long eventId) {
        super("exception.EventAttendanceRequiredException", BusinessException.FORBIDDEN);
    }
}
