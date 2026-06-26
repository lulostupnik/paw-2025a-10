package ar.edu.itba.paw.models.exceptions;

public class CityNotFoundException extends CustomRuntimeException {

    public CityNotFoundException(Long cityId) {
        super("exception.CityNotFoundException", CustomRuntimeException.NOT_FOUND);
    }
    public CityNotFoundException(String cityName) {
        super("exception.CityNotFoundException", CustomRuntimeException.NOT_FOUND);
    }
}