package ar.edu.itba.paw.models.exceptions;

public class TipNotFoundException extends NotFoundException {
    public TipNotFoundException(long id) {
        super(String.format("Tip with id %d not found", id));
    }
}
