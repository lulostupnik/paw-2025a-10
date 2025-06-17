package ar.edu.itba.paw.models.exceptions;

public class UserHasNoJourneyException extends InvalidException {
    public UserHasNoJourneyException(long userId) {
        super(String.format("User with id %d has no journey", userId));
    }
}
