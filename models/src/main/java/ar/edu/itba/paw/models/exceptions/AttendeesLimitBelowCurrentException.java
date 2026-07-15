package ar.edu.itba.paw.models.exceptions;

public class AttendeesLimitBelowCurrentException extends BusinessException {

    public AttendeesLimitBelowCurrentException() {
        super("exception.AttendeesLimitBelowCurrentException", BusinessException.CONFLICT);
    }
}
