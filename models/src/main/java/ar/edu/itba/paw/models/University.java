package ar.edu.itba.paw.models;

public class University{
    private final long id;
    private final String name;
    private final String abbreviation;

    public University(long id, String name, String abbreviation){
        this.id = id;
        this.name = name;
        this.abbreviation = abbreviation;
    }

    public University(String name, String abbreviation){
        this.name = name;
        this.abbreviation = abbreviation;
        this.id = 0; // FIXME
    }

    public University(String name){
        this.name = name;
        this.abbreviation = null;
        this.id = 0; // FIXME
    }

    public String getName() {
        return name;
    }

    public String getAbbreviation() {
        return abbreviation;
    }
}