package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Image;
import java.util.Optional;

public interface ImageDao {
    long saveImage(byte[] imageData);
    Optional<Image> getImageById(long id);
    void deleteImage(long id);
}
