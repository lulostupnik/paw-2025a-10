package ar.edu.itba.paw.webapp.form;

import java.time.LocalDate;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import ar.edu.itba.paw.webapp.validation.ExistingUniversity;
import ar.edu.itba.paw.webapp.validation.FutureDate;
import ar.edu.itba.paw.webapp.validation.ValidDateRange;
import org.springframework.format.annotation.DateTimeFormat;

@ValidDateRange
public class CreateJourneyForm {

    @NotNull
    @FutureDate
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @NotNull
    @FutureDate
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @Size(min = 2, max = 100)
    @NotNull
    @ExistingUniversity
    private String destinationUniversity;

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
    public String getDestinationUniversity(){
        return destinationUniversity;
    }

    public void setDestinationUniversity(String destination){
        this.destinationUniversity = destination;
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
                "\", destinationUniversity: \"" +
                destinationUniversity +
                "\", description: \"" +
                description +
                "\"}";
    }

}
