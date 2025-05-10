package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.validation.EmailInUse;
import ar.edu.itba.paw.webapp.validation.EmailNotInUse;

import javax.validation.constraints.Email;

public class EmailForm {
    @Email
    @EmailInUse
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
