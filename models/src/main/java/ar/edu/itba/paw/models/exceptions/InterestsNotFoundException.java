package ar.edu.itba.paw.models.exceptions;

public class InterestsNotFoundException extends CustomRuntimeException {
    public InterestsNotFoundException(String message) {
        super("exception.InterestsNotFoundException", CustomRuntimeException.NOT_FOUND);
    }
    public InterestsNotFoundException() {
        super("exception.InterestsNotFoundException", CustomRuntimeException.NOT_FOUND);
    }

    public InterestsNotFoundException(long id) {
        super("exception.InterestsNotFoundException", CustomRuntimeException.NOT_FOUND);
    }
}
