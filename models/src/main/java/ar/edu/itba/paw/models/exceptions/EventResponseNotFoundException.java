package ar.edu.itba.paw.models.exceptions;

public class EventResponseNotFoundException extends BusinessException {
    public EventResponseNotFoundException(String message) {
        super("exception.EventResponseNotFoundException", BusinessException.NOT_FOUND);
    }
    public EventResponseNotFoundException() {
        super("exception.EventResponseNotFoundException", BusinessException.NOT_FOUND);
    }

    public EventResponseNotFoundException(long id) {
        super("exception.EventResponseNotFoundException", BusinessException.NOT_FOUND);
    }

    public EventResponseNotFoundException(long eventId, long responseId) {
        super("exception.EventResponseNotFoundException", BusinessException.NOT_FOUND);
    }
}
