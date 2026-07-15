package ar.edu.itba.paw.models.exceptions;

public class JourneyNotFoundException extends BusinessException {

    public JourneyNotFoundException() {
        super("exception.JourneyNotFoundException", BusinessException.NOT_FOUND);
    }
}
