package ar.edu.itba.paw.models.exceptions;

public class CareerAlreadyExistsException extends CustomRuntimeException {
    public CareerAlreadyExistsException(String name) {
        super("exception.CareerAlreadyExistsException", CustomRuntimeException.CONFLICT);
    }
}
