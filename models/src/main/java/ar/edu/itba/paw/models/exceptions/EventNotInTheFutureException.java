package ar.edu.itba.paw.models.exceptions;

public class EventNotInTheFutureException extends BusinessException {
    public EventNotInTheFutureException(long id) {
        super("exception.EventNotInTheFutureException", BusinessException.BAD_REQUEST);
    }
}
