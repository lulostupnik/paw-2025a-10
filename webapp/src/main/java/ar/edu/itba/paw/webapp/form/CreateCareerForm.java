package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.validation.CareerNotExists;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class CreateCareerForm {


    @Size(max = 50)
    @NotNull
    @NotEmpty
    @CareerNotExists
    private String name;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }


}
