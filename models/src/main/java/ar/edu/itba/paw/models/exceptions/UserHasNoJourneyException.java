package ar.edu.itba.paw.models.exceptions;

public class UserHasNoJourneyException extends BusinessException {
    public UserHasNoJourneyException(long userId) {
        super("exception.UserHasNoJourneyException", BusinessException.BAD_REQUEST);
    }
}
