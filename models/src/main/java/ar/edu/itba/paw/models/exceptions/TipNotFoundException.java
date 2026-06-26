package ar.edu.itba.paw.models.exceptions;

public class TipNotFoundException extends CustomRuntimeException {
    public TipNotFoundException(long id) {
        super("exception.TipNotFoundException", CustomRuntimeException.NOT_FOUND);
    }

    public TipNotFoundException(long journeyId, long tipId) {
        super("exception.TipNotFoundException", CustomRuntimeException.NOT_FOUND);
    }
}
