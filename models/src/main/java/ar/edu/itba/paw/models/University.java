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

    public String toJSON() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"id\":");
        sb.append(id);
        sb.append(",");
        sb.append("\"name\":\"");
        sb.append(escapeJson(name));
        sb.append("\",");
        sb.append("\"abbreviation\":\"");
        sb.append(escapeJson(abbreviation));
        sb.append("\",");
        sb.append("\"city\":\"");
        sb.append(escapeJson(city != null ? city.getName() : null));
        sb.append("\"");
        sb.append("}");
        return sb.toString();
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