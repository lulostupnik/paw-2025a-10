package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Image;

import java.util.List;

public interface ImageService {
    public Image storeImage(byte[] imageData);
    public Image getImage(Long id);
    // public List<Image> getAllImages();
    // public void deleteImage(Long id);
}

