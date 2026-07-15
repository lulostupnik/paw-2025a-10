package ar.edu.itba.paw.models.exceptions;

public class TipNotFoundException extends BusinessException {

    public TipNotFoundException() {
        super("exception.TipNotFoundException", BusinessException.NOT_FOUND);
    }
}
