package ar.edu.itba.paw.webapp.form;

import java.util.Date;

//import javax.validation.constraints.Pattern;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import ar.edu.itba.paw.webapp.validation.ImageSize;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;


public class CreateEventForm {
    @Size(min = 2, max = 100)
    private String city;

    @Size(max = 100)
    private String title;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date date;

    @NotNull
    @ImageSize() // 2MB
    private MultipartFile flyer;

    @Size(min = 2, max = 200)
    private String description;

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
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("{city: \"");
        sb.append(city);
        sb.append("\", date: \"");
        sb.append(date);
        sb.append("\", description: \"");
        sb.append(description);
        sb.append("\", profilePictureSize: ");
        sb.append(flyer == null || flyer.isEmpty() ? 0 : flyer.getSize());
        sb.append("}");
        return sb.toString();
    }
}
