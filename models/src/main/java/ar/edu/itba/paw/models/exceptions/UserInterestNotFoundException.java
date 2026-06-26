package ar.edu.itba.paw.models.exceptions;

public class UserInterestNotFoundException extends CustomRuntimeException {
    public UserInterestNotFoundException(String message) {
        super("exception.UserInterestNotFoundException", CustomRuntimeException.NOT_FOUND);
    }

    public UserInterestNotFoundException() {
        super("exception.UserInterestNotFoundException", CustomRuntimeException.NOT_FOUND);
    }

    public UserInterestNotFoundException(long userId, long interestId) {
        super("exception.UserInterestNotFoundException", CustomRuntimeException.NOT_FOUND);
    }
}
