package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.ImageService;
import ar.edu.itba.paw.models.Image;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/images")
public class ImageController {

    private final ImageService imageService;

    @Autowired
    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }



    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getImage(@PathVariable Long id) {
        try {
            Image image = imageService.getImage(id).orElseThrow(); // Luego lanzar una excepción personalizada: ImageNotFoundException

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=\"image" + id + "\"")
                    .body(image.getData());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
