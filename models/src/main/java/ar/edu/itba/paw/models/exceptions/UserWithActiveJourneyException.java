package ar.edu.itba.paw.models.exceptions;

public class UserWithActiveJourneyException extends CustomRuntimeException{
    public UserWithActiveJourneyException(final Long id) {
        super("exception.UserWithActiveJourneyException", CustomRuntimeException.CONFLICT);}

}

