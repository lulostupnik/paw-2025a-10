package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Image;

import java.util.Optional;

public interface ImageDao {
    Image saveImage(byte[] imageData);
    Optional<Image> getImageById(long id);
    // Para el caso especifico de fotos de perfil podríamos hacer que el id coincida con el del usuario
}
