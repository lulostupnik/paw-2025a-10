package ar.edu.itba.paw.models.exceptions;

public class InvalidDateException extends BusinessException {

    public InvalidDateException() {
        super("exception.InvalidDateException", BusinessException.BAD_REQUEST);
    }
}
