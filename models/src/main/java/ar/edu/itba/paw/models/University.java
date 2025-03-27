package ar.edu.itba.paw.models;

public class University{
    private final String name;
    private final String abbreviation;

    public University(String name, String abbreviation){
        this.name = name;
        this.abbreviation = abbreviation;
    }

    public University(String name){
        this.name = name;
        this.abbreviation = null;
    }

    public String getName() {
        return name;
    }

    public String getAbbreviation() {
        return abbreviation;
    }
}