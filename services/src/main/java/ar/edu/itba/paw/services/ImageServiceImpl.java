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
    public ImageServiceImpl(ImageDao imageDao) {
        this.imageDao = imageDao;
    }

    @Override
    public Image storeImage(byte[] imageData) {
        // No sabía si era mejor usar un MultipartFile o un byte[] -> creo que usar MultipartFile estaría violando la separación de capas
        // todo: ver de cambiar a un stream de bytes -> creo que sería mejor porque no tendría que cargar todo en memoria
        return imageDao.saveImage(imageData);
    }

    @Override
    public Image getImage(Long id) {
        return imageDao.getImageById(id).orElseThrow(); // (Lanza NoSuchElementException)
    }

    /*
    @Override
    public void deleteImage(Long id) {
        imageDao.deleteImage(id);
    }
    */

    /*
    @Override
    public List<Image> getAllImages() {
        return imageDao.getAllImages();
    }
    */

}


