package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.ImageDao;
import ar.edu.itba.paw.interfaces.services.ImageService;
import ar.edu.itba.paw.models.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ImageServiceImpl implements ImageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageServiceImpl.class);

    private final ImageDao imageDao;
    private final CacheManager cacheManager;

    @Autowired
    public ImageServiceImpl(final ImageDao imageDao, final CacheManager cacheManager) {
        this.imageDao = imageDao;
        this.cacheManager = cacheManager;
    }

    @Override
    @Transactional
    public long storeImage(final byte[] imageData) {
        LOGGER.debug("Storing image of size {}", imageData.length);
        long imageId = imageDao.create(imageData);

        Image image = new Image(imageId, imageData);
        Cache cache = cacheManager.getCache("images");
        if (cache != null) {
            cache.put(imageId, image);
        } else {
            LOGGER.warn("Cache 'images' not found, skipping cache operation");
        }
        LOGGER.info("Image {} stored", imageId);
        return imageId;
    }

    @Override
    @Cacheable(value = "images", key = "#id")
    public Optional<Image> getImage(final long id) {
        LOGGER.debug("Getting image {}", id);
        return imageDao.findById(id);
    }

    @Override
    @Transactional
    @CacheEvict(value = "images", key = "#id")
    public void deleteImage(final long id) {
        LOGGER.debug("Deleting image {}", id);
        imageDao.delete(id);
        LOGGER.info("Image {} deleted", id);
    }


}


