package ar.edu.itba.paw.models.exceptions;

public class CityNotFoundException extends RuntimeException {


    public CityNotFoundException(Long cityId) {
        super(String.format("City not found with id: %d", cityId));
    }
    public CityNotFoundException(String cityName) {
        super(String.format("City not found with name: %s", cityName));
    }
}