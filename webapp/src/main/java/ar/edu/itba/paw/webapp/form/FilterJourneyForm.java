package ar.edu.itba.paw.webapp.form;


import ar.edu.itba.paw.webapp.validation.ValidFilterDateRange;
import org.springframework.format.annotation.DateTimeFormat;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@ValidFilterDateRange
public class FilterJourneyForm {

    private String destination;

    private String interests;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

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
