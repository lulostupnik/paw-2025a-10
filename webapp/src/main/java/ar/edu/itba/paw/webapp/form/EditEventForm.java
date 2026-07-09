package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.validation.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.NumberFormat;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalTime;

public class EditEventForm {

    @NotNull
    @ExistingCity
    private Long cityId;

    @Size(max = 50)
    @NotNull
    @NotEmpty
    private String title;


    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @FutureDate
    private LocalDate date;

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

    public Long getCityId() {
        return cityId;
    }

    public void setCityId(Long cityId) {
        this.cityId = cityId;
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
        return "{cityId: \"" +
                cityId +
                "\", date: \"" +
                date +
                "\", time: \"" +
                (time == null ? "All-day" : time) +
                "\", description: \"" +
                description +
                "\", title: \"" +
                title +
                "\", address: \"" +
                address +
                "\", attendeesLimit: " +
                (attendeesLimit != null ? attendeesLimit : "\"No limit\"") +
                "}";
    }
}
