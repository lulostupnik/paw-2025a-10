package ar.edu.itba.paw.models.exceptions;

public class RatingNotFoundException extends BusinessException {

    public RatingNotFoundException() {
        super("exception.RatingNotFoundException", BusinessException.NOT_FOUND);
    }
}
