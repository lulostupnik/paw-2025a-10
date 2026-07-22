package ar.edu.itba.paw.webapp.form;

import java.time.LocalDate;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import ar.edu.itba.paw.webapp.validation.ExistingUniversity;
import ar.edu.itba.paw.webapp.validation.FutureDate;
import ar.edu.itba.paw.webapp.validation.ValidDateRange;
import org.springframework.format.annotation.DateTimeFormat;

@ValidDateRange
public class CreateJourneyForm implements DateRangeForm {

    @NotNull
    @FutureDate
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @NotNull
    @FutureDate
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @NotNull
    @ExistingUniversity
    private Long destinationUniversityId;

    @Size(min = 2, max = 2047)
    @NotNull
    private String description;
    
    public LocalDate getStartDate(){
        return startDate;
    }

    public void setStartDate(LocalDate startDate){
        this.startDate = startDate;
    }

    public LocalDate getEndDate(){
        return endDate;
    }

    public void setEndDate(LocalDate endDate){
        this.endDate = endDate;
    }
    public Long getDestinationUniversityId(){
        return destinationUniversityId;
    }

    public void setDestinationUniversityId(Long destinationUniversityId){
        this.destinationUniversityId = destinationUniversityId;
    }

    public String getDescription(){
        return description;
    }

    public void setDescription(String description){
        this.description = description;
    }

    @Override
    public String toString() {
        return "{startDate: \"" +
                startDate +
                "\", endDate: \"" +
                endDate +
                "\", destinationUniversityId: \"" +
                destinationUniversityId +
                "\", description: \"" +
                description +
                "\"}";
    }

}
