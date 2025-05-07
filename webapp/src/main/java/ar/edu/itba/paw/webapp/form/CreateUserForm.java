package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.validation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@PasswordsMatch
public class CreateUserForm {

    @Email
    @Size(min = 2, max = 100)
    @EmailNotInUse
    private String email;

    @Size(min = 2, max = 100)
    private String university;

    @Size(min = 2, max = 50)
    private String career;

    @Size(min = 2, max = 50)
    @UsernameNotInUse
    private String username;

    @Size(min = 2, max = 100)
    private String firstName;

    @Size(min = 8, max = 100)
    private String password;

    @Size(min = 8, max = 100)
    private String confirmPassword;

    @Size(min = 2, max = 100)
    private String lastName;

    @NotNull
    @ImageSize() // 2MB
    @ContentType({"image/jpeg", "image/jpg", "image/png"})
    @ImageNotEmpty
    private MultipartFile profilePicture;

    @Size(min = 2, max = 100)
    private String originUniversity;

    @NotNull
    @ValidInterest
    private long[] interests;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCareer() {
        return career;
    }

    public void setCareer(String career) {
        this.career = career;
    }

    public String getUniversity() {
        return university;
    }

    public void setUniversity(String university) {
        this.university = university;
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

    public MultipartFile getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(MultipartFile profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getOriginUniversity() {
        return originUniversity;
    }

    public void setOriginUniversity(String originUniversity) {
        this.originUniversity = originUniversity;
    }

    public long[] getInterests() {
        return interests;
    }

    public void setInterests(long[] interests) {
        this.interests = interests;
    }
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public String getConfirmPassword() {
        return confirmPassword;
    }
    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
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
        sb.append("\", university: \"");
        sb.append(university);        
        sb.append("\", originUniversity: \"");
        sb.append(originUniversity);
        sb.append("\", career: \"");
        sb.append(career);
        if(interests != null) {
            sb.append("\", interests: {");
            for (int i = 0; i < interests.length; i++) {
                sb.append("\"");
                sb.append(interests[i]);
                sb.append("\"");
                if (i + 1 != interests.length) {
                    sb.append(", ");
                }
            }
            sb.append("}");
        }
        sb.append(" profilePictureSize: ");
        sb.append(profilePicture == null || profilePicture.isEmpty() ? 0 : profilePicture.getSize());
        sb.append("}");
        return sb.toString();
    }
}
