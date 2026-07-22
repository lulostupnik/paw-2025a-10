package ar.edu.itba.paw.models.exceptions;

public class CityAlreadyExistsException extends BusinessException {

    public CityAlreadyExistsException() {
        super("exception.CityAlreadyExistsException", BusinessException.CONFLICT);
    }
}
