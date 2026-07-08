package ar.edu.itba.paw.models.exceptions;

public class RatingNotFoundException extends BusinessException {
    public RatingNotFoundException(Long userId, long eventId) {
        super("exception.RatingNotFoundException", BusinessException.NOT_FOUND);
    }

    public RatingNotFoundException(long id) {
        super("exception.RatingNotFoundException", BusinessException.NOT_FOUND);
    }

    public RatingNotFoundException(long eventId, long ratingId, boolean forEvent) {
        super("exception.RatingNotFoundException", BusinessException.NOT_FOUND);
    }
}
