package ar.edu.itba.paw.models.exceptions;

public class EventNotInTheFutureException extends CustomRuntimeException {
    public EventNotInTheFutureException(long id) {
        super("exception.EventNotInTheFutureException", CustomRuntimeException.BAD_REQUEST);
    }
}
