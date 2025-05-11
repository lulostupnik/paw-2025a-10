package ar.edu.itba.paw.models;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Interest {
    private final long id;
    private final String name;
    @Override
    public String toString() {
        return name;
    }

}
