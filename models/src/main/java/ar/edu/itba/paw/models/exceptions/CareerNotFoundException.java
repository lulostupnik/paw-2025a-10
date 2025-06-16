package ar.edu.itba.paw.models.exceptions;

public class CareerNotFoundException extends RuntimeException {
    public CareerNotFoundException(String message) {
        super(message);
    }
    public CareerNotFoundException() {
        super("Career not found");
    }
    public CareerNotFoundException(String message, String careerName) {
        super(String.format("%s: %s", message,careerName));  }
}