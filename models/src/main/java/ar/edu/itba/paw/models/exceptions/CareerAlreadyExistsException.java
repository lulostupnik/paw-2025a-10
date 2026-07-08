package ar.edu.itba.paw.models.exceptions;

public class CareerAlreadyExistsException extends BusinessException {
    public CareerAlreadyExistsException(String name) {
        super("exception.CareerAlreadyExistsException", BusinessException.CONFLICT);
    }
}
