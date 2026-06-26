package ar.edu.itba.paw.models.exceptions;

public class EventIsFullException extends CustomRuntimeException {

    public EventIsFullException(long id) {
        super("exception.EventIsFullException", CustomRuntimeException.CONFLICT);
    }
}
