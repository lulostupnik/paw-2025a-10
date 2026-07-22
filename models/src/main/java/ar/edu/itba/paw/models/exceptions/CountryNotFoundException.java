package ar.edu.itba.paw.models.exceptions;

public class CountryNotFoundException extends BusinessException {

    public CountryNotFoundException() {
        super("exception.CountryNotFoundException", BusinessException.NOT_FOUND);
    }
}
