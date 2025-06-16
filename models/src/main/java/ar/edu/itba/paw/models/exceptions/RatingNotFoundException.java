package ar.edu.itba.paw.models.exceptions;

public class RatingNotFoundException extends RuntimeException {
    public RatingNotFoundException(Long id, long eventId) {
        super(String.format("Rating not found for user with id %d and event with id %d", id, eventId));

    }
}
