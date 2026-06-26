package ar.edu.itba.paw.models.exceptions;

public class ImageNotFoundException extends CustomRuntimeException {
    public ImageNotFoundException(String message) {
        super("exception.ImageNotFoundException", CustomRuntimeException.NOT_FOUND);
    }
}
