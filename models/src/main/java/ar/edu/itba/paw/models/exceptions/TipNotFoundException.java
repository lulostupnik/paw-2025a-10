package ar.edu.itba.paw.models.exceptions;

public class TipNotFoundException extends NotFoundException {
    public TipNotFoundException(long id) {
        super(String.format("Tip with id %d not found", id));
    }

    public TipNotFoundException(long journeyId, long tipId) {
        super(String.format("Tip with id %d not found for journey with id %d", tipId, journeyId));
    }
}
