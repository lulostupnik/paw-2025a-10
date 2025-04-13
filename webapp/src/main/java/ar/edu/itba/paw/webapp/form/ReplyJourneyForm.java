package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.validation.ImageSize;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class ReplyJourneyForm {

    @Size(min = 2, max = 2047)
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
