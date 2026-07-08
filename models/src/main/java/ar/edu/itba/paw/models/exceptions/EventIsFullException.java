package ar.edu.itba.paw.models.exceptions;

public class EventIsFullException extends BusinessException {

    public EventIsFullException(long id) {
        super("exception.EventIsFullException", BusinessException.CONFLICT);
    }
}
