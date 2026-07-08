package ar.edu.itba.paw.models.exceptions;

public class UniversityNotFoundException extends BusinessException {

    public UniversityNotFoundException(long id) {
        super("exception.UniversityNotFoundException", BusinessException.NOT_FOUND);
    }

    public UniversityNotFoundException(String universityName) {
        super("exception.UniversityNotFoundException", BusinessException.NOT_FOUND);
    }
}
