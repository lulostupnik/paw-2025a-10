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
        StringBuilder sb = new StringBuilder();
        sb.append(name);
        sb.append(" (");
        sb.append(abbreviation);
        sb.append(")");
        return sb.toString();
    }

}