package ar.edu.itba.paw.webapp.form;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class CreateCareerForm {


    @Size(max = 50)
    @NotNull
    @NotEmpty
    private String name;

    public CreateCareerForm() {
    }
    public CreateCareerForm(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }


}
