package ar.edu.itba.paw.models.exceptions;

public class EventIsFullException extends RuntimeException {

    public EventIsFullException(long id) {
        super(String.format("Event with id %d is full", id));
    }
}
