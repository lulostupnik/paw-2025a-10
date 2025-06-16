package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.validation.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalTime;

@CurrentAttendees
public class EditEventForm {

    @PositiveOrZero
    private long id;

    @Size(min = 2, max = 100)
    @NotNull
    @ExistingCity
    private String city;

    @Size(max = 50)
    @NotNull
    @NotEmpty
    private String title;


    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @FutureDate
    private LocalDate date;

    @ImageSize()
    @NotNull
    @ContentType({"image/jpeg", "image/jpg", "image/png"})
    private MultipartFile flyer;

    @Size(min = 2, max = 2047)
    @NotNull
    private String description;

    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime time;

    @Size(max=255)
    private String address;

    @NumberFormat
    @Max(1000)
    @Min(1)
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

    public long getId() {
        return id;
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
    public void setId(long id) {
        this.id = id;
    }
    @Override
    public String toString(){
        return "{city: \"" +
                city +
                "\", date: \"" +
                date +
                "\", time: \"" +
                (time == null ? "All-day" : time) +
                "\", description: \"" +
                description +
                "\", profilePictureSize: " +
                (flyer == null || flyer.isEmpty() ? 0 : flyer.getSize()) +
                ", title: \"" +
                title +
                "\", address: \"" +
                address +
                "\", attendeesLimit: " +
                (attendeesLimit != null ? attendeesLimit : "\"No limit\"") +
                "}";
    }
}
