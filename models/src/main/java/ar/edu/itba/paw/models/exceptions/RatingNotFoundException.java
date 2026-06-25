package ar.edu.itba.paw.models.exceptions;

public class RatingNotFoundException extends NotFoundException {
    public RatingNotFoundException(Long userId, long eventId) {
        super(String.format("Rating not found for user with id %d and event with id %d", userId, eventId));
    }

    public RatingNotFoundException(long id) {
        super(String.format("Rating with id %d not found", id));
    }

    public RatingNotFoundException(long eventId, long ratingId, boolean forEvent) {
        super(String.format("Rating with id %d not found for event with id %d", ratingId, eventId));
    }
}
