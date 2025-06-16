package ar.edu.itba.paw.models.exceptions;

public class UniversityNotFoundException extends RuntimeException {
    public UniversityNotFoundException(String message) {
        super(message);
    }
    public UniversityNotFoundException() {
        super("University not found");
    }
    public UniversityNotFoundException(String message, String universityName) {
        super(String.format("%s: %s", message, universityName));
    }


}
