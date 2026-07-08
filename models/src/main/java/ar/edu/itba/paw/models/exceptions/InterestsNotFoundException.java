package ar.edu.itba.paw.models.exceptions;

public class InterestsNotFoundException extends BusinessException {
    public InterestsNotFoundException(String message) {
        super("exception.InterestsNotFoundException", BusinessException.NOT_FOUND);
    }
    public InterestsNotFoundException() {
        super("exception.InterestsNotFoundException", BusinessException.NOT_FOUND);
    }

    public InterestsNotFoundException(long id) {
        super("exception.InterestsNotFoundException", BusinessException.NOT_FOUND);
    }
}
