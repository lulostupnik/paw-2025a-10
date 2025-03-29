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

    public String getName() {
        return name;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public long getId() {
        return id;
    }

    @Override
    public String toString() {
        if(abbreviation == null){
            return name;
        }
        return name + " (" + abbreviation + ")";
    }
}