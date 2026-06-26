package ar.edu.itba.paw.models.exceptions;

public class EventNotFoundException extends CustomRuntimeException {

    public EventNotFoundException(long id) {
        super("exception.EventNotFoundException", CustomRuntimeException.NOT_FOUND);
    }
}
