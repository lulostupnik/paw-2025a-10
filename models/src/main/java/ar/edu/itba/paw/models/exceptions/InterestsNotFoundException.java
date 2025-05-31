package ar.edu.itba.paw.models.exceptions;

public class InterestsNotFoundException extends RuntimeException {
    public InterestsNotFoundException(String message) {
        super(message);
    }
    public InterestsNotFoundException() {
        super("Interests not found");
    }
}
