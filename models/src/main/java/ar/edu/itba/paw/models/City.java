package ar.edu.itba.paw.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class City {
    private final String name;
    private final String country; // podría ser un Country en vez de un String
    private final long id;

    @Override
    public String toString() {
        return name + ", " + country;
    }

    public String toJSON() {
        return "{\"name\":\"" + name + "\",\"country\":\"" + country + "\",\"id\":" + id + "}";
    }
}
