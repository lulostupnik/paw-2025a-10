package ar.edu.itba.paw.models;

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

    public String toJSON() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"name\":\"");
        sb.append(escapeJson(name));
        sb.append("\", \"id\":");
        sb.append(id);
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
