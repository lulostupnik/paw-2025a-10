package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.validation.ContentType;
import ar.edu.itba.paw.webapp.validation.ImageNotEmpty;
import ar.edu.itba.paw.webapp.validation.ImageSize;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

public class EditPictureForm {
    @NotNull
    @ImageSize()
    @ContentType({"image/jpeg", "image/jpg", "image/png"})
    @ImageNotEmpty
    private MultipartFile picture;

    public MultipartFile getPicture() {
        return picture;
    }

    public void setPicture(MultipartFile picture) {
        this.picture = picture;
    }
}
