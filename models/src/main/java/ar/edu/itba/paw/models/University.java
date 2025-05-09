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
        return "{"
                + "\"id\": " + id + ", "
                + "\"name\": \"" + escapeJson(name) + "\", "
                + "\"abbreviation\": \"" + escapeJson(abbreviation) + "\", "
                + "\"city\": \"" + escapeJson(city.getName()) + "\""
                + "}";
    }

    private String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

}