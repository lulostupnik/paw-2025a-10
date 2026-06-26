package ar.edu.itba.paw.models.exceptions;

public class CountryNotFoundException extends CustomRuntimeException {
    public CountryNotFoundException(String message, String countryName) {
        super("exception.CountryNotFoundException", CustomRuntimeException.NOT_FOUND);
    }
}
