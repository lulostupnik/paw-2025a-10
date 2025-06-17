package ar.edu.itba.paw.models.exceptions;

public class InvalidPaginationParamsException extends RuntimeException {
    public InvalidPaginationParamsException(String message) {
        super(message);
    }
}
