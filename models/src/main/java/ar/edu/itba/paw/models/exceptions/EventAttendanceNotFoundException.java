package ar.edu.itba.paw.models.exceptions;

public class EventAttendanceNotFoundException extends BusinessException {

    public EventAttendanceNotFoundException() {
        super("exception.EventAttendanceNotFoundException", BusinessException.NOT_FOUND);
    }
}
