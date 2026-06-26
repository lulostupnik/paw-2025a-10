package ar.edu.itba.paw.models.exceptions;

public class EventResponseNotFoundException extends CustomRuntimeException {
    public EventResponseNotFoundException(String message) {
        super("exception.EventResponseNotFoundException", CustomRuntimeException.NOT_FOUND);
    }
    public EventResponseNotFoundException() {
        super("exception.EventResponseNotFoundException", CustomRuntimeException.NOT_FOUND);
    }

    public EventResponseNotFoundException(long id) {
        super("exception.EventResponseNotFoundException", CustomRuntimeException.NOT_FOUND);
    }

    public EventResponseNotFoundException(long eventId, long responseId) {
        super("exception.EventResponseNotFoundException", CustomRuntimeException.NOT_FOUND);
    }
}
