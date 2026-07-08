package ar.edu.itba.paw.models.exceptions;

public class InvalidPaginationParamsException extends BusinessException {
    public InvalidPaginationParamsException(String message) {
        super("exception.InvalidPaginationParamsException", BusinessException.BAD_REQUEST);
    }
}
