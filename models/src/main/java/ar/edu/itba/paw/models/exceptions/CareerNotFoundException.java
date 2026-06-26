package ar.edu.itba.paw.models.exceptions;

public class CareerNotFoundException extends CustomRuntimeException {
    public CareerNotFoundException(String CareerName) {
        super("exception.CareerNotFoundException", CustomRuntimeException.NOT_FOUND);
    }
    public CareerNotFoundException(Long id) {
        super("exception.CareerNotFoundException", CustomRuntimeException.NOT_FOUND);
    }
}