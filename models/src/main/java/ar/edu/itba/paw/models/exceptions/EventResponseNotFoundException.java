package ar.edu.itba.paw.models.exceptions;

import ar.edu.itba.paw.models.EventResponse;

public class EventResponseNotFoundException extends RuntimeException {
    public EventResponseNotFoundException(String message) {
        super(message);
    }
    public  EventResponseNotFoundException() {
        super("Event response not found");
    }

}
