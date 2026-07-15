package ar.edu.itba.paw.models.exceptions;

public class CareerAlreadyExistsException extends BusinessException {

    public CareerAlreadyExistsException() {
        super("exception.CareerAlreadyExistsException", BusinessException.CONFLICT);
    }
}
