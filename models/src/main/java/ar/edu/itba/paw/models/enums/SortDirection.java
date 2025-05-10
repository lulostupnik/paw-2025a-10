package ar.edu.itba.paw.models.enums;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum SortDirection {

    ASC("asc"),
    DESC("desc");

    private final String param;

    SortDirection(String param) { this.param = param; }

    public String getParam() { return param; }

    private static final Map<String, SortDirection> BY_PARAM = Stream.of(values()).collect(Collectors.toMap(SortDirection::getParam, d -> d));


    public static SortDirection from(String value) {
        if (value == null || value.isBlank()) {
            return ASC;
        }
        return BY_PARAM.getOrDefault(value.toLowerCase(), ASC);
    }
}
