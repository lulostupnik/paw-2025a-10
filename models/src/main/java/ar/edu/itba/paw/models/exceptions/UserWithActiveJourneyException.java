package ar.edu.itba.paw.models.exceptions;

public class UserWithActiveJourneyException extends BusinessException{
    public UserWithActiveJourneyException(final Long id) {
        super("exception.UserWithActiveJourneyException", BusinessException.CONFLICT);}

}

