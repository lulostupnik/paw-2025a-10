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
@Transactional(readOnly = true)
public class ImageServiceImpl implements ImageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageServiceImpl.class);

    private final ImageDao imageDao;

    @Autowired
    public ImageServiceImpl(final ImageDao imageDao) {
        this.imageDao = imageDao;
    }

    @Override
    @Transactional
    public long createImage(final byte[] imageData) {
        LOGGER.debug("Storing image of size {}", imageData.length);
        long imageId = imageDao.create(imageData);

        LOGGER.info("Image {} stored", imageId);
        return imageId;
    }

    @Override
    public Optional<Image> findImage(final long id) {
        LOGGER.debug("Getting image {}", id);
        return imageDao.findById(id);
    }

    @Override
    @Transactional
    public void deleteImage(final long id) {
        imageDao.delete(id);
        LOGGER.info("Image {} deleted", id);
    }


}


