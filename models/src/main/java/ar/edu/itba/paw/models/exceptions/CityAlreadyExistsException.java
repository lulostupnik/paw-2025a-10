package ar.edu.itba.paw.models.exceptions;

public class CityAlreadyExistsException extends IllegalArgumentException{
    public CityAlreadyExistsException(String message) {
        super(message);
    }

    public CityAlreadyExistsException() {
        super("City already exists");
    }
}
