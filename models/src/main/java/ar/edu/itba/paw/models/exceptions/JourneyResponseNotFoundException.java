package ar.edu.itba.paw.models.exceptions;

public class JourneyResponseNotFoundException extends NotFoundException {
    public JourneyResponseNotFoundException(long id) {
        super(String.format("Journey response with id %d not found", id));
    }

    public JourneyResponseNotFoundException(long journeyId, long responseId) {
        super(String.format("Journey response with id %d not found for journey with id %d", responseId, journeyId));
    }
}
