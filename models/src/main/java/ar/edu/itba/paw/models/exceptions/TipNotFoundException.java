package ar.edu.itba.paw.models.exceptions;

public class TipNotFoundException extends RuntimeException {
    public TipNotFoundException(String message) {
        super(message);
    }
    public TipNotFoundException() {
        super("Tip not found");
    }
}
