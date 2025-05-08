package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.validation.ValidEventFilterDateRange;
import ar.edu.itba.paw.webapp.validation.ValidFilterDateRange;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@ValidEventFilterDateRange
public class FilterEventForm {
    private Long destination;

    private Long interests;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    private boolean isPast;

    private boolean isUpcoming;

    private boolean attending;

    public Boolean getIsPast() {
        return isPast;
    }
    public void setIsPast(Boolean isPast) {
        this.isPast = isPast;
    }
    public Boolean getIsUpcoming() {
        return isUpcoming;
    }
    public void setIsUpcoming(Boolean isUpcoming) {
        this.isUpcoming = isUpcoming;
    }
    public Boolean getAttending() {
        return attending;
    }
    public void setAttending(Boolean attending) {
        this.attending = attending;
    }

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
