package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.validation.EmailInUse;
import ar.edu.itba.paw.webapp.validation.EmailNotInUse;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class EmailForm {
    @Email
    @EmailInUse
    @NotNull
    @NotEmpty
    private String email;



    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "EmailForm{" +
                "email='" + email + '\'' +
                '}';
    }
}
