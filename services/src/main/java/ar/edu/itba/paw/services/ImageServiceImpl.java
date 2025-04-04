package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.ImageDao;
import ar.edu.itba.paw.interfaces.services.ImageService;
import ar.edu.itba.paw.models.Image;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ImageServiceImpl implements ImageService {

    private final ImageDao imageDao;

    @Autowired
    public ImageServiceImpl(final ImageDao imageDao) {
        this.imageDao = imageDao;
    }

    // ¿Agregar Transactional?
    @Override
    public long storeImage(byte[] imageData) { // ¿Cambiar a InputStream?
        return imageDao.saveImage(imageData);
    }

    // ¿Agregar Transactional?
    @Override
    public Optional<Image> getImage(Long id) {
        return imageDao.getImageById(id);
    }

    // ¿Agregar Transactional?
    @Override
    public void deleteImage(Long id) {
        imageDao.deleteImage(id);
    }

}


