package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.validation.ExistingUniversity;

import javax.validation.constraints.Size;
import java.time.LocalDate;

public class PatchJourneyForm {

    private LocalDate startDate;

    private LocalDate endDate;

    @Size(min = 2, max = 100)
    @ExistingUniversity
    private String destinationUniversity;

    @Size(min = 2, max = 2047)
    private String description;

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getDestinationUniversity() {
        return destinationUniversity;
    }

    public void setDestinationUniversity(String destinationUniversity) {
        this.destinationUniversity = destinationUniversity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
