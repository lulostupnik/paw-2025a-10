package ar.edu.itba.paw.models.exceptions;


public abstract class BadRequestException extends RuntimeException {

    protected BadRequestException(String message) {
        super(message);
    }
}
