package ar.edu.itba.paw.models.exceptions;

public class JourneyNotFoundException extends NotFoundException {
    public JourneyNotFoundException(long id) {
        super(String.format("Journey with id %d not found", id));
    }
}
