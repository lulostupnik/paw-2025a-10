package ar.edu.itba.paw.models.exceptions;

public class InvalidImageException extends CustomRuntimeException {

    public InvalidImageException() {
        super("exception.InvalidImageException", CustomRuntimeException.BAD_REQUEST);
    }

    public InvalidImageException(final String messageKey) {
        super(messageKey, CustomRuntimeException.BAD_REQUEST);
    }
}
