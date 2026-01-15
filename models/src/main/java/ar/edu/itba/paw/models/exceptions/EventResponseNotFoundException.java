package ar.edu.itba.paw.models.exceptions;

public class EventResponseNotFoundException extends NotFoundException {
    public EventResponseNotFoundException(String message) {
        super(message);
    }
    public EventResponseNotFoundException() {
        super("Event response not found");
    }

    public EventResponseNotFoundException(long id) {
        super(String.format("Event response with id %d not found", id));
    }
}
