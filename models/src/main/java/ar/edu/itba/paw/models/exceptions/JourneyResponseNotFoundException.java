package ar.edu.itba.paw.models.exceptions;

public class JourneyResponseNotFoundException extends BusinessException {

    public JourneyResponseNotFoundException() {
        super("exception.JourneyResponseNotFoundException", BusinessException.NOT_FOUND);
    }
}
