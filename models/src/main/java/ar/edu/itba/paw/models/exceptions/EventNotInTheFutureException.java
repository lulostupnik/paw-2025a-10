package ar.edu.itba.paw.models.exceptions;

public class EventNotInTheFutureException extends InvalidException {
    public EventNotInTheFutureException(long id) {
        super(String.format("Event with id %d is not in the future", id));
    }
}
