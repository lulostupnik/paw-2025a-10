package ar.edu.itba.paw.models.exceptions;

public class UserHasNoJourneyException extends CustomRuntimeException {
    public UserHasNoJourneyException(long userId) {
        super("exception.UserHasNoJourneyException", CustomRuntimeException.BAD_REQUEST);
    }
}
