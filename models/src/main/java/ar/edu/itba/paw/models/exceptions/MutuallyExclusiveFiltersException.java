package ar.edu.itba.paw.models.exceptions;

public class MutuallyExclusiveFiltersException extends BusinessException {

    public MutuallyExclusiveFiltersException() {
        super("exception.MutuallyExclusiveFiltersException", BusinessException.BAD_REQUEST);
    }
}
