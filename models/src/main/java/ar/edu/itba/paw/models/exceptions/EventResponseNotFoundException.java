package ar.edu.itba.paw.models.exceptions;

public class EventResponseNotFoundException extends RuntimeException {
    public EventResponseNotFoundException(String message) {
        super(message);
    }
    public  EventResponseNotFoundException() {
        super("Event response not found");
    }

    public EventResponseNotFoundException(long id) {
        super(String.format("Event with id %d not found", id));
    }

}
