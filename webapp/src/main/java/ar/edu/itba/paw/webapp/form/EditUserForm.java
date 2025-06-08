package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.validation.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@ValidUpdateUser
public class EditUserForm {

    private long userId;

//    @Email
//    @Size(min = 2, max = 100)
//    @NotNull
//    private String email;

    @Size(min = 2, max = 50)
    @NotNull
    private String username;

    @Size(min = 2, max = 100)
    @NotNull
    private String firstName;

    @Size(min = 2, max = 100)
    @NotNull
    private String lastName;

    @Size(max = 100)
    @NotNull
    @ExistingUniversity
    private String originUniversity;

    @Size(max = 100)
    @NotNull
    @ExistingCareer
    private String career;

    // Getters and setters
    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

//    public String getEmail() {
//        return email;
//    }
//
//    public void setEmail(String email) {
//        this.email = email;
//    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getOriginUniversity() {
        return originUniversity;
    }

    public void setOriginUniversity(String originUniversity) {
        this.originUniversity = originUniversity;
    }

    public String getCareer() {
        return career;
    }

    public void setCareer(String career) {
        this.career = career;
    }

    @Override
    public String toString() {
        return "EditUserForm{" +
                "userId=" + userId +
//                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", university='" + originUniversity + '\'' +
                ", career='" + career + '\'' +
                '}';
    }
}