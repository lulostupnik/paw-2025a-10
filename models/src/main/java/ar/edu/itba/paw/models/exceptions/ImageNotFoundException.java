package ar.edu.itba.paw.models.exceptions;

public class ImageNotFoundException extends BusinessException {

    public ImageNotFoundException() {
        super("exception.ImageNotFoundException", BusinessException.NOT_FOUND);
    }
}
