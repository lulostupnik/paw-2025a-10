package ar.edu.itba.paw.models.exceptions;

public class CityAlreadyExistsException extends BusinessException{
    public CityAlreadyExistsException(String name, String country) {
        super("exception.CityAlreadyExistsException", BusinessException.CONFLICT);
    }
}
