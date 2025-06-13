package ar.edu.itba.paw.models.exceptions;

public class CareerAlreadyExistsException extends IllegalArgumentException {
    public CareerAlreadyExistsException(String message) {
        super(message);
    }

    public CareerAlreadyExistsException() {
        super("Career already exists");
    }
}
