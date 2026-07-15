package ar.edu.itba.paw.models.exceptions;

public class EventResponseNotFoundException extends BusinessException {

    public EventResponseNotFoundException() {
        super("exception.EventResponseNotFoundException", BusinessException.NOT_FOUND);
    }
}
