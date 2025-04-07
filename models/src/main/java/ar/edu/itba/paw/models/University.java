package ar.edu.itba.paw.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class University{
    private final long id;
    private final String name;
    private final String abbreviation;

    @Override
    public String toString() {
        return name + " (" + abbreviation + ")";
    }
}