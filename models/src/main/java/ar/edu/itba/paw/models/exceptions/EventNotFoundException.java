package ar.edu.itba.paw.models.exceptions;

public class EventNotFoundException extends NotFoundException {

    public EventNotFoundException(long id) {
        super(String.format("Event with id %d not found", id));
    }
}
