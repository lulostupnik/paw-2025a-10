package ar.edu.itba.paw.models.exceptions;

public class UniversityNotFoundException extends BusinessException {

    public UniversityNotFoundException() {
        super("exception.UniversityNotFoundException", BusinessException.NOT_FOUND);
    }
}
