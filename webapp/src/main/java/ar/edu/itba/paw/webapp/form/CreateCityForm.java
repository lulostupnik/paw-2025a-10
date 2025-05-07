package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.validation.ExistingCountry;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class CreateCityForm {
    @Size(max = 50)
    @NotNull
    @NotEmpty
    private String name;


    @Size(max = 50)
    @NotNull
    @NotEmpty
    @ExistingCountry
    private String country;

    public CreateCityForm(){

    }

    public CreateCityForm(String name, String country) {
        this.name = name;
        this.country = country;

    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getCountry() {
        return country;
    }
    public void setCountry(String country) {
        this.country = country;
    }

}
