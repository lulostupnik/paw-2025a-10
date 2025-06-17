package ar.edu.itba.paw.models.exceptions;

public class JourneyResponseNotFoundException extends RuntimeException {
    public JourneyResponseNotFoundException(long id) {
        super(String.format("Journey response with id %d not found", id));
    }
}
