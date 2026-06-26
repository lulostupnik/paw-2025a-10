package ar.edu.itba.paw.models.exceptions;

public class InvalidPaginationParamsException extends CustomRuntimeException {
    public InvalidPaginationParamsException(String message) {
        super("exception.InvalidPaginationParamsException", CustomRuntimeException.BAD_REQUEST);
    }
}
