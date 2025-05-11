package ar.edu.itba.paw.models.enums;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum SortFieldEvent {

    DATE("date"),
    ATTENDEES("attendees");

    private final String param;

    SortFieldEvent(String param) {
        this.param = param;
    }

    public String getParam() {
        return param;
    }
    private static final Map<String, SortFieldEvent> BY_PARAM =
            Stream.of(values()).collect(Collectors.toMap(SortFieldEvent::getParam, f -> f));

    public static SortFieldEvent from(String value) {
        if (value == null || value.isBlank()) {
            return DATE;
        }
        return BY_PARAM.getOrDefault(value.toLowerCase(), DATE);
    }

}
