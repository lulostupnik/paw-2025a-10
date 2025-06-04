package ar.edu.itba.paw.models.exceptions;

public class InvalidImageException extends RuntimeException {
    public InvalidImageException(String message) {
        super(message);
    }
    public InvalidImageException() {
        super("Invalid image");
    }
}
