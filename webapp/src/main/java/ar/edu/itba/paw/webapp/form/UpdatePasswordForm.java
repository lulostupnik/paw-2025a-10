package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.validation.PasswordsMatch;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@PasswordsMatch
public class UpdatePasswordForm {
    @Size(min = 8, max = 100)
    @NotNull
    private String password;

    @NotNull
    @Size(min = 8, max = 100)
    private String confirmPassword;

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

}
