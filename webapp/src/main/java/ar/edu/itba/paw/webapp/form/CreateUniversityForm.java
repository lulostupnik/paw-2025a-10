package ar.edu.itba.paw.webapp.form;

public class CreateUniversityForm {
    private String name;
    private String abbreviation;
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

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }
}
