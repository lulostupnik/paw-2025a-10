package ar.edu.itba.paw.models.exceptions;

public class RatingNotFoundException extends CustomRuntimeException {
    public RatingNotFoundException(Long userId, long eventId) {
        super("exception.RatingNotFoundException", CustomRuntimeException.NOT_FOUND);
    }

    public RatingNotFoundException(long id) {
        super("exception.RatingNotFoundException", CustomRuntimeException.NOT_FOUND);
    }

    public RatingNotFoundException(long eventId, long ratingId, boolean forEvent) {
        super("exception.RatingNotFoundException", CustomRuntimeException.NOT_FOUND);
    }
}
