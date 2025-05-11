package ar.edu.itba.paw.models.exceptions;

import java.util.logging.Logger;

public class CareerNotFoundException extends RuntimeException {
    public CareerNotFoundException(String message) {
        super(message);
    }
    public CareerNotFoundException() {
        super("Career not found");
    }
}