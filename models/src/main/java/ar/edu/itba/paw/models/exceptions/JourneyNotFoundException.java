package ar.edu.itba.paw.models.exceptions;

public class JourneyNotFoundException extends RuntimeException {
    public JourneyNotFoundException(String message) {
        super(message);
    }
}
