package ar.edu.itba.paw.webapp.form;


import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Size;

import java.time.LocalDate;

public class FilterJourneyForm {

    @Size(min = 2, max = 100)
    private String destination;

    public void setInterests(String interests) {
        this.interests = interests;
    }

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    public String getInterests() {
        return interests;
    }

    @Size(min = 2, max = 100)
    private String interests;

    public String getInterest() {
        return interests;
    }
}
