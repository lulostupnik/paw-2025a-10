package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.validation.InterestNotExists;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class CreateInterestForm {

    @Size(max = 50)
    @NotNull
    @NotEmpty
    @InterestNotExists
    private String name;

    public CreateInterestForm(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

}
