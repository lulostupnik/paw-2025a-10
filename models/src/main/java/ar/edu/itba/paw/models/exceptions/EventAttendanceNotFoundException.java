package ar.edu.itba.paw.models.exceptions;

public class EventAttendanceNotFoundException extends NotFoundException {
    public EventAttendanceNotFoundException(String message) {
        super(message);
    }

    public EventAttendanceNotFoundException() {
        super("Event attendance not found");
    }

    public EventAttendanceNotFoundException(long userId, long eventId) {
        super(String.format("Attendance for user %d at event %d not found", userId, eventId));
    }
}
