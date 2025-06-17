package ar.edu.itba.paw.models.exceptions;

public class CareerNotFoundException extends RuntimeException {
    public CareerNotFoundException(String CareerName) {
        super(String.format("Career with name %s not found", CareerName));
    }
    public CareerNotFoundException(Long id) {
        super(String.format("Career with id %d not found", id));
    }
}