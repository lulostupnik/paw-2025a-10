package ar.edu.itba.paw.models.exceptions;

public class UserWithActiveJourneyException extends RuntimeException{
    public UserWithActiveJourneyException(final Long id) {
        super(String.format("User with id %d has an active journey", id));}

}

