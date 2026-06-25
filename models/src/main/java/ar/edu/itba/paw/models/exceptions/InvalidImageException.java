package ar.edu.itba.paw.models.exceptions;

public class InvalidImageException extends RuntimeException {
    public InvalidImageException() {
        super("Invalid image");
    }

    public InvalidImageException(String message) {
        super(message);
    }
}
