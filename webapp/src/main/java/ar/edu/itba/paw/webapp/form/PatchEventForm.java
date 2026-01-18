package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.validation.ExistingCity;
import ar.edu.itba.paw.webapp.validation.FutureDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.NumberFormat;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalTime;

public class PatchEventForm {

    @Size(min = 2, max = 100)
    @ExistingCity
    private String city;

    @Size(max = 50)
    private String title;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @FutureDate
    private LocalDate date;

    @Size(min = 2, max = 2047)
    private String description;

    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime time;

    @Size(max = 255)
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
}
