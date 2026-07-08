package ar.edu.itba.paw.models.exceptions;

public class InvalidImageException extends BusinessException {

    public InvalidImageException() {
        super("exception.InvalidImageException", BusinessException.BAD_REQUEST);
    }

    public InvalidImageException(final String messageKey) {
        super(messageKey, BusinessException.BAD_REQUEST);
    }
}
