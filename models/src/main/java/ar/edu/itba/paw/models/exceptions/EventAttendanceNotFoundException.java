package ar.edu.itba.paw.models.exceptions;

public class EventAttendanceNotFoundException extends CustomRuntimeException {
    public EventAttendanceNotFoundException(String message) {
        super("exception.EventAttendanceNotFoundException", CustomRuntimeException.NOT_FOUND);
    }

    public EventAttendanceNotFoundException() {
        super("exception.EventAttendanceNotFoundException", CustomRuntimeException.NOT_FOUND);
    }

    public EventAttendanceNotFoundException(long userId, long eventId) {
        super("exception.EventAttendanceNotFoundException", CustomRuntimeException.NOT_FOUND);
    }
}
