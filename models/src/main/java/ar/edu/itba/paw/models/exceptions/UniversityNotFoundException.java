package ar.edu.itba.paw.models.exceptions;

public class UniversityNotFoundException extends RuntimeException {
    public UniversityNotFoundException(String message) {
        super(message);
    }
    public UniversityNotFoundException() {
        super("University not found");
    }

}
