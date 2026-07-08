package ar.edu.itba.paw.models.exceptions;

public class TipNotFoundException extends BusinessException {
    public TipNotFoundException(long id) {
        super("exception.TipNotFoundException", BusinessException.NOT_FOUND);
    }

    public TipNotFoundException(long journeyId, long tipId) {
        super("exception.TipNotFoundException", BusinessException.NOT_FOUND);
    }
}
