package ar.edu.itba.paw.models.exceptions;

public class ImageNotFoundException extends BusinessException {
    public ImageNotFoundException(String message) {
        super("exception.ImageNotFoundException", BusinessException.NOT_FOUND);
    }
}
