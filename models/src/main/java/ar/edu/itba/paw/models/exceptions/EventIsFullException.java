package ar.edu.itba.paw.models.exceptions;

public class EventIsFullException extends BusinessException {

    public EventIsFullException() {
        super("exception.EventIsFullException", BusinessException.CONFLICT);
    }
}
