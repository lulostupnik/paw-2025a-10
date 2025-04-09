package ar.edu.itba.paw.webapp.form;

import java.time.LocalDate;
import java.util.Date;

import javax.validation.constraints.Email;
//import javax.validation.constraints.Pattern;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import ar.edu.itba.paw.webapp.validation.FutureDate;
import ar.edu.itba.paw.webapp.validation.ImageSize;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;


public class CreateEventForm {
    @Size(min = 2, max = 100)
    private String city;

    @FutureDate
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date date;

    @NotNull
    @ImageSize() // 2MB
    private MultipartFile flyer;

    @NotNull
    @ImageSize() // 2MB
    private MultipartFile profilePicture;

    @Size(min = 2, max = 200)
    private String description;

    @Email
    @Size(min = 2, max = 100)
    private String email;

    @Size(min = 2, max = 100)
    private String university;

    @Size(min = 2, max = 50)
    private String career;

    @Size(min = 2, max = 50)
    private String username;

    @Size(min = 2, max = 100)
    private String firstName;

    @Size(min = 2, max = 100)
    private String lastName;

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

    public String getUniversity() {
        return university;
    }
    public void setUniversity(String university) {
        this.university = university;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }


   public String getEmail() {
        return email;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public MultipartFile getFlyer() {
        return flyer;
    }

    public void setFlyer(MultipartFile flyer) {
        this.flyer = flyer;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public MultipartFile getProfilePicture() {
        return profilePicture;
    }
    public void setProfilePicture(MultipartFile profilePicture) {
        this.profilePicture = profilePicture;
    }

}
