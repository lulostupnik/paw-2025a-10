package ar.edu.itba.paw.webapp.form;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import javax.validation.constraints.*;
//import javax.validation.constraints.Pattern;

import ar.edu.itba.paw.webapp.validation.ContentType;
import ar.edu.itba.paw.webapp.validation.FutureDate;
import ar.edu.itba.paw.webapp.validation.ImageSize;
import ar.edu.itba.paw.webapp.validation.ValidAttendeesLimit;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.web.multipart.MultipartFile;


public class CreateEventForm {
    @Size(min = 2, max = 100)
    private String city;

    @Size(max = 100)
    @NotNull
    @NotEmpty
    private String title;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull
    @FutureDate
    private LocalDate date;

    @NotNull
    @ImageSize() // 2MB
    @ContentType({"image/jpeg", "image/jpg", "image/png"})
    private MultipartFile flyer;

    @Size(min = 2, max = 2047)
    private String description;

    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime time;

    @Size(max=255)
    private String address;

    @ValidAttendeesLimit
    @NumberFormat
    private Integer attendeesLimit;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
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
    public LocalTime getTime() {
        return time;
    }
    public void setTime(LocalTime time) {
        this.time = time;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public Integer getAttendeesLimit() {
        return attendeesLimit;
    }
    public void setAttendeesLimit(Integer attendeesLimit) {
        this.attendeesLimit = attendeesLimit;
    }  
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("{city: \"");
        sb.append(city);
        sb.append("\", date: \"");
        sb.append(date);
        sb.append("\", time: \"");
        sb.append(time == null ? "All-day" : time);
        sb.append("\", description: \"");
        sb.append(description);
        sb.append("\", profilePictureSize: ");
        sb.append(flyer == null || flyer.isEmpty() ? 0 : flyer.getSize());
        sb.append(", title: \"");
        sb.append(title);
        sb.append("\", address: \"");
        sb.append(address);
        sb.append("\", attendeesLimit: ");
        sb.append(attendeesLimit != null ? attendeesLimit : "\"No limit\"");
        sb.append("}");
        return sb.toString();
    }
}
