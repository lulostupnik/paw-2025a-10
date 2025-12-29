package ar.edu.itba.paw.webapp.form;

import javax.validation.constraints.NotNull;

public class ValidateUserForm {

    @NotNull
    private String validationToken;

    public String getValidationToken() {
        return validationToken;
    }

    public void setValidationToken(String validationToken) {
        this.validationToken = validationToken;
    }
}
