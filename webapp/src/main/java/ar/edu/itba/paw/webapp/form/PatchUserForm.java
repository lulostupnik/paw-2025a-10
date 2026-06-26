package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.validation.ExistingCareer;
import ar.edu.itba.paw.webapp.validation.ExistingUniversity;

import javax.validation.constraints.Size;

public class PatchUserForm {

    @Size(min = 2, max = 50)
    private String username;

    @Size(min = 2, max = 100)
    private String firstName;

    @Size(min = 2, max = 100)
    private String lastName;

    @Size(max = 100)
    @ExistingUniversity
    private String originUniversity;

    @Size(max = 100)
    @ExistingCareer
    private String career;

    @Size(min = 8, max = 72)
    private String password;

    private Boolean verified;

    private Boolean blocked;

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public Boolean getBlocked() {
        return blocked;
    }

    public void setBlocked(Boolean blocked) {
        this.blocked = blocked;
    }
}
