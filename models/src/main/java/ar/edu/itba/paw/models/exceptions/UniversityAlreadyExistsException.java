package ar.edu.itba.paw.models.exceptions;

public class UniversityAlreadyExistsException extends RuntimeException {
    public UniversityAlreadyExistsException(String message) {
        super(message);
    }
}
