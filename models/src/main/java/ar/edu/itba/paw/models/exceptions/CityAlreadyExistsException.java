package ar.edu.itba.paw.models.exceptions;

public class CityAlreadyExistsException extends CustomRuntimeException{
    public CityAlreadyExistsException(String name, String country) {
        super("exception.CityAlreadyExistsException", CustomRuntimeException.CONFLICT);
    }
}
