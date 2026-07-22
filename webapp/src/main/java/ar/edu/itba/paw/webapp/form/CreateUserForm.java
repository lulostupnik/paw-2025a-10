package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.validation.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

public class CreateUserForm {

    @Email
    @Size(min = 2, max = 100)
    @EmailNotInUse
    @NotNull
    private String email;

    @NotNull
    @ExistingCareer
    private Long careerId;

    @Size(min = 2, max = 50)
    @UsernameNotInUse
    @NotNull
    private String username;

    @Size(min = 2, max = 100)
    @NotNull
    private String firstName;

    @Size(min = 8, max = 100)
    @NotNull
    private String password;

    @Size(min = 2, max = 100)
    @NotNull
    private String lastName;

    @NotNull
    @ExistingUniversity
    private Long universityId;

    @ValidInterest
    private List<Long> interestIds;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getCareerId() {
        return careerId;
    }

    public void setCareerId(Long careerId) {
        this.careerId = careerId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public Long getUniversityId() {
        return universityId;
    }

    public void setUniversityId(Long universityId) {
        this.universityId = universityId;
    }

    public List<Long> getInterestIds() {
        return interestIds;
    }

    public void setInterestIds(List<Long> interestIds) {
        this.interestIds = interestIds;
    }
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("{email: \"");
        sb.append(email);
        sb.append("\", username: \"");
        sb.append(username);
        sb.append("\", firstName: \"");
        sb.append(firstName);
        sb.append("\", lastName: \"");
        sb.append(lastName);
        sb.append("\", universityId: \"");
        sb.append(universityId);
        sb.append("\", careerId: \"");
        sb.append(careerId);
        if(interestIds != null) {
            sb.append("\", interestIds: {");
            for(Long interestId : interestIds) {
                sb.append(interestId);
                sb.append(", ");
            }
            sb.append("}");
        }
        sb.append("}");
        return sb.toString();
    }
}
