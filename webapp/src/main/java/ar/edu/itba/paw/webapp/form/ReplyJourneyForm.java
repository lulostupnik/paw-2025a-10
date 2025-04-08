package ar.edu.itba.paw.webapp.form;

import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class ReplyJourneyForm {

    @Size(min = 2, max = 100)
    @Email
    private String email;

    @Size(min = 2, max = 100)
    private String firstName;
    @NotNull
    private MultipartFile profilePicture;
    @Size(min = 2, max = 100)
    private String lastName;
    @Size(min=2, max = 50)
    private String username;

    @Size(min=2, max = 50)
    private String originUniversity;

    @Size(min =2, max = 50)
    private String career;

    @Size(min = 2, max = 2047)
    private String message;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getOriginUniversity() {
        return originUniversity;
    }

    public void setOriginUniversity(String university) {
        this.originUniversity = university;
    }

    public String getCareer() {
        return career;
    }

    public void setCareer(String career) {
        this.career = career;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    public MultipartFile getProfilePicture() {
        return profilePicture;
    }
    public void setProfilePicture(MultipartFile profilePicture) {
        this.profilePicture = profilePicture;
    }
}
