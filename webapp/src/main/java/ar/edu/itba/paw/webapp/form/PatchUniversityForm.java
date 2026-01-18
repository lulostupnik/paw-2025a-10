package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.validation.ExistingCity;

import javax.validation.constraints.Size;

public class PatchUniversityForm {

    @Size(max = 50)
    private String name;

    @Size(max = 50)
    private String abbreviation;

    @Size(max = 50)
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

    public void setCity(String city) {
        this.city = city;
    }
}
