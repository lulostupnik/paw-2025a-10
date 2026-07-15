package ar.edu.itba.paw.models.exceptions;

public class EventNotFoundException extends BusinessException {

    public EventNotFoundException() {
        super("exception.EventNotFoundException", BusinessException.NOT_FOUND);
    }
}
