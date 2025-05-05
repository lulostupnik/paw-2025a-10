package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.validation.ExistingCity;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class CreateUniversityForm {

    @Size(max = 50)
    @NotNull
    @NotEmpty
    private String name;

    @Size(max = 50)
    @NotNull
    @NotEmpty
    private String abbreviation;

    @Size(max = 50)
    @NotNull
    @NotEmpty
    @ExistingCity
    private String cityName;

    public CreateUniversityForm() {
    }

    public CreateUniversityForm(String name, String abbreviation, String cityName) {
        this.name = name;
        this.abbreviation = abbreviation;
        this.cityName = cityName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String getCity() {
        return cityName;
    }

    public void setCity(String cityName) {
        this.cityName = cityName;
    }
}
