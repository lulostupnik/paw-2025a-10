package ar.edu.itba.paw.models.exceptions;

public class CityAlreadyExistsException extends IllegalArgumentException{
    public CityAlreadyExistsException(String name, String country) {
        super(String.format("City with name %s in country %s already exists", name, country));
    }
}
