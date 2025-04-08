package ar.edu.itba.paw.webapp.form;

import java.time.LocalDate;

import javax.validation.constraints.Email;
//import javax.validation.constraints.Pattern;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import ar.edu.itba.paw.webapp.validation.ImageSize;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

public class CreateJourneyForm {

    @Size(min = 2, max = 100)
    private String firstName;

    @Size(min = 2, max = 100)
    private String lastName;

    @Size(min = 6, max = 100)
    //@Pattern(regexp = "[a-zA-Z0-9]+$")
    @Email
    private String email;

    @NotNull
    @ImageSize() // 1KB
    private MultipartFile profilePicture;

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

    @Size(min = 2, max = 50)
    private String career;

    @Size(min = 2, max = 50)
    private String username;

    @NotNull
    private String[] interests;

    public String getCareer() {
        return career;
    }
    public void setCareer(String career) {
        this.career = career;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public MultipartFile getProfilePicture() {
        return profilePicture;
    }
    public void setProfilePicture(MultipartFile profilePicture) {
        this.profilePicture = profilePicture;
    }

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

    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstname) {
        this.firstName = firstname;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastname) {
        this.lastName = lastname;
    }
    public String[] getInterests() {
        return interests;
    }
    public void setInterests(String[] interests) {
        this.interests = interests;
    }
}
