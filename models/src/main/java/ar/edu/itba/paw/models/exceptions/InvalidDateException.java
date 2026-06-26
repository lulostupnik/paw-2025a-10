package ar.edu.itba.paw.models.exceptions;

public class InvalidDateException extends CustomRuntimeException {
    public InvalidDateException(String message) {
        super("exception.InvalidDateException", CustomRuntimeException.BAD_REQUEST);
    }
    public InvalidDateException() {
        super("exception.InvalidDateException", CustomRuntimeException.BAD_REQUEST);
    }
}
