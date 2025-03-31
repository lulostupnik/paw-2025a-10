package ar.edu.itba.paw.webapp.form;

import java.time.LocalDate;

import javax.validation.constraints.Email;
//import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

public class CreateJourneyForm {

    @Size(min = 6, max = 100)
    //@Pattern(regexp = "[a-zA-Z0-9]+$")
    @Email
    private String email;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @Size(min = 2, max = 100)
    private String destinationCity;

    @Size(min = 2, max = 100)
    private String destinationUniversity;

    @Size(min = 2, max = 2047)
    private String description;

    public String getEmail(){
        return email;
    }

    public void setEmail(String email){
        this.email = email;
    }
    
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

    public String getDestinationCity(){
        return destinationCity;
    }

    public void setDestinationCity(String destination){
        this.destinationCity = destination;
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
}
