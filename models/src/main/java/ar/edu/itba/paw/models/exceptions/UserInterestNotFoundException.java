package ar.edu.itba.paw.models.exceptions;

public class UserInterestNotFoundException extends NotFoundException {
    public UserInterestNotFoundException(String message) {
        super(message);
    }

    public UserInterestNotFoundException() {
        super("User interest not found");
    }

    public UserInterestNotFoundException(long userId, long interestId) {
        super(String.format("Interest %d not found for user %d", interestId, userId));
    }
}
