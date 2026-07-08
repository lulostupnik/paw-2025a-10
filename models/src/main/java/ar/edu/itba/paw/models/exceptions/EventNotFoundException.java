package ar.edu.itba.paw.models.exceptions;

public class EventNotFoundException extends BusinessException {

    public EventNotFoundException(long id) {
        super("exception.EventNotFoundException", BusinessException.NOT_FOUND);
    }
}
