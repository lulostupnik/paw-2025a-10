package ar.edu.itba.paw.webapp.form;

import javax.validation.constraints.NotNull;
import ar.edu.itba.paw.webapp.validation.ImageNotEmpty;
import ar.edu.itba.paw.webapp.validation.ImageSize;
import org.springframework.web.multipart.MultipartFile;

// ¿Usar lombok?
public class UploadImageForm {

    // @FileSize(min = 1, max = 1024 * 1024)
    @NotNull
    @ImageSize
    @ImageNotEmpty
    private MultipartFile image;

    public MultipartFile getImage() {
        return image;
    }

    public void setImage(MultipartFile image) {
        this.image = image;
    }

}
