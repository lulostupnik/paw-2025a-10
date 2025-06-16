package ar.edu.itba.paw.models.exceptions;

public class CountryNotFoundException extends RuntimeException {
    public CountryNotFoundException(String message, String countryName) {
        super(String.format("%s: %s", message, countryName));
    }
}
