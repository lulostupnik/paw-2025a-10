package ar.edu.itba.paw.models.exceptions;

public class UserWithActiveJourneyException extends BusinessException {

    public UserWithActiveJourneyException() {
        super("exception.UserWithActiveJourneyException", BusinessException.CONFLICT);
    }
}
