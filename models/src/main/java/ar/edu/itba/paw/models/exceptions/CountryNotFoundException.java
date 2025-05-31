package ar.edu.itba.paw.models.exceptions;

public class CountryNotFoundException extends RuntimeException {
    public CountryNotFoundException(String message) {
        super(message);
    }
    public CountryNotFoundException() {
        super("Country not found");
    }
}
