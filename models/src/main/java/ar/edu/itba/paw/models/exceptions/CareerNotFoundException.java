package ar.edu.itba.paw.models.exceptions;

public class CareerNotFoundException extends BusinessException {

    public CareerNotFoundException() {
        super("exception.CareerNotFoundException", BusinessException.NOT_FOUND);
    }
}
