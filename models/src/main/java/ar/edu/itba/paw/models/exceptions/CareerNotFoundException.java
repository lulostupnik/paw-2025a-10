package ar.edu.itba.paw.models.exceptions;

public class CareerNotFoundException extends BusinessException {
    public CareerNotFoundException(String CareerName) {
        super("exception.CareerNotFoundException", BusinessException.NOT_FOUND);
    }
    public CareerNotFoundException(Long id) {
        super("exception.CareerNotFoundException", BusinessException.NOT_FOUND);
    }
}