package ar.edu.itba.paw.models.exceptions;

public class JourneyResponseNotFoundException extends CustomRuntimeException {
    public JourneyResponseNotFoundException(long id) {
        super("exception.JourneyResponseNotFoundException", CustomRuntimeException.NOT_FOUND);
    }

    public JourneyResponseNotFoundException(long journeyId, long responseId) {
        super("exception.JourneyResponseNotFoundException", CustomRuntimeException.NOT_FOUND);
    }
}
