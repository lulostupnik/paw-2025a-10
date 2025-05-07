package ar.edu.itba.paw.webapp.form;


import ar.edu.itba.paw.webapp.validation.ValidFilterDateRange;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@ValidFilterDateRange
public class FilterJourneyForm {

    private Long destination;

    private Long interests;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    public void setInterests(Long interests) {
        this.interests = interests;
    }

    public Long getDestination() {
        return destination;
    }

    public void setDestination(Long destination) {
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



    public Long getInterests() {
        return interests;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{destination: \"");
        sb.append(destination);
        sb.append("\", interests: \"");
        sb.append(interests);
        sb.append("\", startDate: \"");
        sb.append(startDate);
        sb.append("\", endDate: \"");
        sb.append(endDate);
        sb.append("\"}");
        return sb.toString();
    }
}
