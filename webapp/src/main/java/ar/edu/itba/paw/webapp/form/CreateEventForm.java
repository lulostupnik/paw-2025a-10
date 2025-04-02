package ar.edu.itba.paw.webapp.form;

import java.time.LocalDate;
import java.util.Date;

import javax.validation.constraints.Email;
//import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;


public class CreateEventForm {
    @Size(min = 2, max = 100)
    private String city;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date date;


    private MultipartFile flyer;

    @Size(min = 2, max = 200)
    private String description;

    @Email
    @Size(min = 2, max = 100)
    private String email;

   public String getEmail() {
        return email;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public MultipartFile getFlyer() {
        return flyer;
    }

    public void setFlyer(MultipartFile flyer) {
        this.flyer = flyer;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


}
