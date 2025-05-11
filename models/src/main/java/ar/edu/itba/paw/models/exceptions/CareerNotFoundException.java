package ar.edu.itba.paw.models.exceptions;

public class CareerNotFoundException extends RuntimeException {
    public CareerNotFoundException(String message) {
        super(message);
    }
    public CareerNotFoundException() {}
}