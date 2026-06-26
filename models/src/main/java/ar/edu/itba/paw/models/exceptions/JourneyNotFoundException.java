package ar.edu.itba.paw.models.exceptions;

public class JourneyNotFoundException extends CustomRuntimeException {
    public JourneyNotFoundException(long id) {
        super("exception.JourneyNotFoundException", CustomRuntimeException.NOT_FOUND);
    }
}
