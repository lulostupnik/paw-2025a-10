package ar.edu.itba.paw.models.exceptions;

public class RatingNotFoundException extends RuntimeException {
    public RatingNotFoundException(String message) {
        super(message);
    }
    public RatingNotFoundException() {
        super("Rating not found");
    }
}
