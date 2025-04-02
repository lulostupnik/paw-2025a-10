package ar.edu.itba.paw.models;

public class City {
    private final long id;
    private final String name;
    private final String country;

    public City(String name, String country, long id) {
        this.name = name;
        this.country = country;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getCountry() {
        return country;
    }
    public long getId() {
        return id;
    }


}
