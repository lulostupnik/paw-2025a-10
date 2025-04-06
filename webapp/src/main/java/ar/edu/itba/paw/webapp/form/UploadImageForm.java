package ar.edu.itba.paw.webapp.form;

import java.time.LocalDate;

import javax.validation.constraints.Email;
//import javax.validation.constraints.Pattern;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

// ¿Usar lombok?
public class UploadImageForm {

    // @FileSize(min = 1, max = 1024 * 1024)
    @NotNull
    private MultipartFile image;

    public MultipartFile getImage() {
        return image;
    }

    public void setImage(MultipartFile image) {
        this.image = image;
    }

}
