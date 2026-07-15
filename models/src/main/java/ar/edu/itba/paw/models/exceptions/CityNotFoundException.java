package ar.edu.itba.paw.models.exceptions;

public class CityNotFoundException extends BusinessException {

    public CityNotFoundException() {
        super("exception.CityNotFoundException", BusinessException.NOT_FOUND);
    }
}
