package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.validation.CareerNotExists;
import ar.edu.itba.paw.webapp.validation.ExistingCity;
import ar.edu.itba.paw.webapp.validation.UniversityNotExists;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class CreateUniversityForm {

    @Size(max = 50)
    @NotNull
    @NotEmpty
    @UniversityNotExists
    private String name;

    @Size(max = 50)
    @NotNull
    @NotEmpty
    private String abbreviation;

    @Size(max = 50)
    @NotNull
    @NotEmpty
    @ExistingCity
    private String city;


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
        return city;
    }

    public void setCity(String cityName) {
        this.city = cityName;
    }
}
