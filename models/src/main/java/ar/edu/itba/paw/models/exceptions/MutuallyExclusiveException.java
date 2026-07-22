package ar.edu.itba.paw.models.exceptions;

public class MutuallyExclusiveException extends BusinessException {

    public MutuallyExclusiveException() {
        super("exception.MutuallyExclusiveException", BusinessException.BAD_REQUEST);
    }
}
