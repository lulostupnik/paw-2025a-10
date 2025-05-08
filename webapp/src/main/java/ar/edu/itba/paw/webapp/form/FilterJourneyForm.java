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

    private boolean isMyDestination;

    private boolean isUpcoming;

    private boolean isPast;

    public boolean getIsMyDestination() {
        return isMyDestination;
    }
    public void setIsMyDestination(boolean isMyDestination) {
        this.isMyDestination = isMyDestination;
    }

    public boolean getIsUpcoming() {
        return isUpcoming;
    }

    public void setIsUpcoming(boolean isUpcoming) {
        this.isUpcoming = isUpcoming;
    }

    public boolean getIsPast() {
        return isPast;
    }

    public void setIsPast(boolean isPast) {
        this.isPast = isPast;
    }

    public Long getDestination() {
        return destination;
    }

    public void setDestination(Long destination) {
        this.destination = destination;
    }

    public Long getInterests() {
        return interests;
    }

    public void setInterests(Long interests) {
        this.interests = interests;
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
        sb.append("\", isMyDestination: \"");
        sb.append(isMyDestination);
        sb.append("\", isUpcoming: \"");
        sb.append(isUpcoming);
        sb.append("\", isPast: \"");
        sb.append(isPast);
        sb.append("\"}");
        return sb.toString();
    }
}
