package ar.edu.itba.paw.models.exceptions;


public abstract class NotFoundException extends RuntimeException {

    protected NotFoundException(String message) {
        super(message);
    }
}
