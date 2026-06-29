package ar.edu.itba.paw.webapp.form;

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

    @NotNull
    @ExistingCity
    private Long cityId;


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

    public Long getCityId() {
        return cityId;
    }

    public void setCityId(Long cityId) {
        this.cityId = cityId;
    }
}
