package ar.edu.itba.paw.models.exceptions;

public class EventAttendanceNotFoundException extends BusinessException {
    public EventAttendanceNotFoundException(String message) {
        super("exception.EventAttendanceNotFoundException", BusinessException.NOT_FOUND);
    }

    public EventAttendanceNotFoundException() {
        super("exception.EventAttendanceNotFoundException", BusinessException.NOT_FOUND);
    }

    public EventAttendanceNotFoundException(long userId, long eventId) {
        super("exception.EventAttendanceNotFoundException", BusinessException.NOT_FOUND);
    }
}
