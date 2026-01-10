package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.utils.Constants;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class UploadImageForm {

    @NotNull
    @Size(max = Constants.MAX_IMAGE_SIZE)
    private byte[] imageData;

    public byte[] getImageData() {
        return imageData;
    }

    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }
}
