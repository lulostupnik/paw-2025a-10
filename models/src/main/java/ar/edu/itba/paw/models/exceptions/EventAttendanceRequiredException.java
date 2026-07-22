package ar.edu.itba.paw.models.exceptions;

public class EventAttendanceRequiredException extends BusinessException {

    public EventAttendanceRequiredException() {
        super("exception.EventAttendanceRequiredException", BusinessException.FORBIDDEN);
    }
}
