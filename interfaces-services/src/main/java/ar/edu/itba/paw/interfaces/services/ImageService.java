package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Image;
import java.util.Optional;

public interface ImageService {
    long createImage(byte[] imageData);
    Optional<Image> findImage(long id);
    void deleteImage(long id);
}

