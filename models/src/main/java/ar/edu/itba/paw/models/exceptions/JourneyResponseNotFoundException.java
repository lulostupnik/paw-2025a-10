package ar.edu.itba.paw.models.exceptions;

public class JourneyResponseNotFoundException extends RuntimeException {
    public JourneyResponseNotFoundException(String message) {
        super(message);
    }
    public JourneyResponseNotFoundException(){
        super("Journey response not found");
    }
}
