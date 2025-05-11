package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Image;
import java.util.Optional;

public interface ImageDao {
    long create(byte[] imageData);
    Optional<Image> findById(long id);
    void delete(long id);
}
