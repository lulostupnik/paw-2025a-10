package ar.edu.itba.paw.models.exceptions;

public class CityNotFoundException extends BusinessException {

    public CityNotFoundException(Long cityId) {
        super("exception.CityNotFoundException", BusinessException.NOT_FOUND);
    }
    public CityNotFoundException(String cityName) {
        super("exception.CityNotFoundException", BusinessException.NOT_FOUND);
    }
}