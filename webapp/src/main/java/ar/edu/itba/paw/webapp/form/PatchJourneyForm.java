package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.validation.ExistingUniversity;
import ar.edu.itba.paw.webapp.validation.FutureDate;
import ar.edu.itba.paw.webapp.validation.ValidDateRange;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@ValidDateRange
public class PatchJourneyForm implements DateRangeForm {

    @FutureDate
    private LocalDate startDate;

    @FutureDate
    private LocalDate endDate;

    @ExistingUniversity
    private Long destinationUniversityId;

    @Size(min = 2, max = 2047)
    private String description;

    @AssertTrue
    private Boolean deleted;

    @Size(max = 1000)
    private String deletionMessage;

    public boolean hasContentChanges() {
        return startDate != null || endDate != null || destinationUniversityId != null || description != null;
    }

    @Override
    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    @Override
    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Long getDestinationUniversityId() {
        return destinationUniversityId;
    }

    public void setDestinationUniversityId(Long destinationUniversityId) {
        this.destinationUniversityId = destinationUniversityId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
