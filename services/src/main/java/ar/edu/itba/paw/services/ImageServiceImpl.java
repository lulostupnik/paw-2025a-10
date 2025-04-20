package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.ImageDao;
import ar.edu.itba.paw.interfaces.services.ImageService;
import ar.edu.itba.paw.models.Image;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ImageServiceImpl implements ImageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageServiceImpl.class);

    private final ImageDao imageDao;

    @Autowired
    public ImageServiceImpl(final ImageDao imageDao) {
        this.imageDao = imageDao;
    }

    @Transactional
    @Override
    public long storeImage(byte[] imageData) { // ¿Cambiar a InputStream?
        LOGGER.debug("Storing image of size {}", imageData.length);
        return imageDao.saveImage(imageData);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Image> getImage(Long id) {
        LOGGER.debug("Getting image {}", id);
        return imageDao.getImageById(id);
    }

    @Transactional
    @Override
    public void deleteImage(Long id) {
        LOGGER.debug("Deleting image {}", id);
        imageDao.deleteImage(id);
    }

}


