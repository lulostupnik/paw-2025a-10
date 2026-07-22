package ar.edu.itba.paw.models.exceptions;

public class InterestsNotFoundException extends BusinessException {

    public InterestsNotFoundException() {
        super("exception.InterestsNotFoundException", BusinessException.NOT_FOUND);
    }
}
