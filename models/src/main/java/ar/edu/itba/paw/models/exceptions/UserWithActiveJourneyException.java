package ar.edu.itba.paw.models.exceptions;

public class UserWithActiveJourneyException extends IllegalArgumentException{
    private static final String MESSAGE = "User with active journey";

    public UserWithActiveJourneyException() {
        super(MESSAGE);
    }

    public UserWithActiveJourneyException(final String message) {
        super(message);
    }

    public UserWithActiveJourneyException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public UserWithActiveJourneyException(final Throwable cause) {
        super(MESSAGE, cause);
    }

}

