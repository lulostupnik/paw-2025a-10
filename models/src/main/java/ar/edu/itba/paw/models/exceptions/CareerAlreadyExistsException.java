package ar.edu.itba.paw.models.exceptions;

public class CareerAlreadyExistsException extends IllegalArgumentException {
    public CareerAlreadyExistsException(String name) {
        super(String.format("Career with name %s already exists", name));
    }
}
