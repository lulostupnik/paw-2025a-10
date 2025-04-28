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
public class ImageServiceImpl implements ImageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageServiceImpl.class);

    private final ImageDao imageDao;
    private final CacheManager cacheManager;

    @Autowired
    public ImageServiceImpl(final ImageDao imageDao, final CacheManager cacheManager) {
        this.imageDao = imageDao;
        this.cacheManager = cacheManager;
    }

    @Transactional
    @Override
    public long storeImage(byte[] imageData) {
        LOGGER.debug("Storing image of size {}", imageData.length);
        long imageId = imageDao.saveImage(imageData);

        Image image = new Image(imageId, imageData);
        Cache cache = cacheManager.getCache("images");
        if (cache != null) {
            cache.put(imageId, Optional.of(image));
        } else {
            LOGGER.warn("Cache 'images' not found, skipping cache operation");
        }

        return imageId;
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "images", key = "#id")
    @Override
    public Optional<Image> getImage(Long id) {
        LOGGER.debug("Getting image {}", id);
        return imageDao.getImageById(id);
    }

    @Transactional
    @CacheEvict(value = "images", key = "#id")
    @Override
    public void deleteImage(Long id) {
        LOGGER.debug("Deleting image {}", id);
        imageDao.deleteImage(id);
    }

    @Transactional
    @CacheEvict(value = "images", key = "#id")
    @Override
    public void updateImage(Long id, byte[] newContent) {
        LOGGER.debug("Updating image {}", id);
        imageDao.updateImage(id, newContent);
    }


}


