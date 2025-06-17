package ar.edu.itba.paw.models.exceptions;

public class EventNotFoundException extends RuntimeException {

    public EventNotFoundException(long id) {
        super(String.format("Event with id %d not found", id));
    }
}
