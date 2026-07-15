package ar.edu.itba.paw.models.exceptions;

public class EventNotOccurredException extends BusinessException {

    public EventNotOccurredException() {
        super("exception.EventNotOccurredException", BusinessException.BAD_REQUEST);
    }
}
