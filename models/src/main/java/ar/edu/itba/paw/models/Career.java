package ar.edu.itba.paw.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Career {
    private final long id;
    private final String name;

    @Override
    public String toString() {
        return name;
    }

}
