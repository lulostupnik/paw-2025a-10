package ar.edu.itba.paw.models.exceptions;

public class UniversityAlreadyExistsException extends BusinessException {

    public UniversityAlreadyExistsException() {
        super("exception.UniversityAlreadyExistsException", BusinessException.CONFLICT);
    }
}
