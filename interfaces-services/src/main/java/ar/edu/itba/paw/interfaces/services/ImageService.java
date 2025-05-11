package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Image;
import java.util.Optional;

public interface ImageService {
    long storeImage(byte[] imageData);
    Optional<Image> getImage(long id);
    void deleteImage(long id);
}

