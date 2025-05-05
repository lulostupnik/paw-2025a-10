package ar.edu.itba.paw.models;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Interest {
    private final Long id;
    private final String name;
    @Override
    public String toString() {
        return name;
    }

    public String toJSON() {
        return "{ \"id\": " + id + ", \"name\": \"" + name + "\" }";
    }
}
