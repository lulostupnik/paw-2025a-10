package ar.edu.itba.paw.models.exceptions;

public class InvalidReferenceException extends BusinessException {

    public InvalidReferenceException() {
        super("exception.InvalidReferenceException", BusinessException.BAD_REQUEST);
    }
}
