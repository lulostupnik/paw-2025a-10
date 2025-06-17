package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.validation.ValidFilterDateRange;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

@ValidFilterDateRange
public class FilterJourneyForm {

    private String destination;

    private String interests;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    private boolean isMyDestination;

    private boolean isUpcoming;

    private boolean isPast;

    private boolean isOngoing;

    public boolean getIsOngoing() {
        return isOngoing;
    }


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

    public void setIsOngoing(boolean isOngoing) {
        this.isOngoing = isOngoing;
    }
    public void setInterests(String interests) {
        this.interests = interests;
    }

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



    public String getInterests() {
        return interests;
    }


    @Override
    public String toString() {
        return "{destination: \"" +
                destination +
                "\", interests: \"" +
                interests +
                "\", startDate: \"" +
                startDate +
                "\", endDate: \"" +
                endDate +
                "\", isMyDestination: \"" +
                isMyDestination +
                "\", isUpcoming: \"" +
                isUpcoming +
                "\", isPast: \"" +
                isPast +
                "\", isOngoing: \"" +
                isOngoing +
                "\"}";
    }
}
