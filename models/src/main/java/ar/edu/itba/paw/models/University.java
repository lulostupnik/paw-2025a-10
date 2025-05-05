package ar.edu.itba.paw.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class University{
    private final long id;
    private final String name;
    private final String abbreviation;
    private final City city;

    @Override
    public String toString() {
        return name + " (" + abbreviation + ")";
    }

    public String toJSON() {
        return "{ \"id\": " + id + ", \"name\": \"" + name + "\", \"abbreviation\": \"" + abbreviation + "\", \"city\": \"" + city.getName() + "\" }";
    }
}