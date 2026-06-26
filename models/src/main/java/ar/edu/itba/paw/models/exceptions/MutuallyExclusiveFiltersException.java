package ar.edu.itba.paw.models.exceptions;

public class MutuallyExclusiveFiltersException extends CustomRuntimeException {
    public MutuallyExclusiveFiltersException(String... filterNames) {
        super("exception.MutuallyExclusiveFiltersException", CustomRuntimeException.BAD_REQUEST);
    }
}
