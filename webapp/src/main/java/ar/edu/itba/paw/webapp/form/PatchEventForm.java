package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.validation.ExistingCity;
import ar.edu.itba.paw.webapp.validation.FutureDate;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalTime;

public class PatchEventForm {

    @ExistingCity
    private Long cityId;

    @Size(max = 50)
    private String title;

    @FutureDate
    private LocalDate date;

    @Size(min = 2, max = 2047)
    private String description;

    private LocalTime time;

    @Size(max = 255)
    private String address;

    @Max(1000)
    @Min(1)
    private Integer attendeesLimit;

    @AssertTrue
    private Boolean deleted;

    @Size(max = 1000)
    private String deletionMessage;

    public boolean hasContentChanges() {
        return cityId != null || title != null || date != null || description != null
                || time != null || address != null || attendeesLimit != null;
    }

    public Long getCityId() {
        return cityId;
    }

    public void setCityId(Long cityId) {
        this.cityId = cityId;
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

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public String getDeletionMessage() {
        return deletionMessage;
    }

    public void setDeletionMessage(String deletionMessage) {
        this.deletionMessage = deletionMessage;
    }
}
