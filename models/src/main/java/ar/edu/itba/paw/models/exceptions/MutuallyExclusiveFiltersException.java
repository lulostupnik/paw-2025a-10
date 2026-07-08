package ar.edu.itba.paw.models.exceptions;

public class MutuallyExclusiveFiltersException extends BusinessException {
    public MutuallyExclusiveFiltersException(String... filterNames) {
        super("exception.MutuallyExclusiveFiltersException", BusinessException.BAD_REQUEST);
    }
}
