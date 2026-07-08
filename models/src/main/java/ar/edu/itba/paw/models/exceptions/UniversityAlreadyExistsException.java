package ar.edu.itba.paw.models.exceptions;

public class UniversityAlreadyExistsException extends BusinessException {
    public UniversityAlreadyExistsException(String name, String city) {
        super("exception.UniversityAlreadyExistsException", BusinessException.CONFLICT);
    }

}
