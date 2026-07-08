package ar.edu.itba.paw.models.exceptions;

public class JourneyResponseNotFoundException extends BusinessException {
    public JourneyResponseNotFoundException(long id) {
        super("exception.JourneyResponseNotFoundException", BusinessException.NOT_FOUND);
    }

    public JourneyResponseNotFoundException(long journeyId, long responseId) {
        super("exception.JourneyResponseNotFoundException", BusinessException.NOT_FOUND);
    }
}
