package ar.edu.itba.paw.webapp.form;

import javax.validation.constraints.Size;

public class PatchInterestForm {

    @Size(max = 50)
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
