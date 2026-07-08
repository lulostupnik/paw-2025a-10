package ar.edu.itba.paw.models.exceptions;

public class UserInterestNotFoundException extends BusinessException {
    public UserInterestNotFoundException(String message) {
        super("exception.UserInterestNotFoundException", BusinessException.NOT_FOUND);
    }

    public UserInterestNotFoundException() {
        super("exception.UserInterestNotFoundException", BusinessException.NOT_FOUND);
    }

    public UserInterestNotFoundException(long userId, long interestId) {
        super("exception.UserInterestNotFoundException", BusinessException.NOT_FOUND);
    }
}
