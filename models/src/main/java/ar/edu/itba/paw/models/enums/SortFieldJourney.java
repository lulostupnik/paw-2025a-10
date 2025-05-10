package ar.edu.itba.paw.models.enums;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum SortFieldJourney {

    START_DATE("start_date"),
    END_DATE("end_date"),
    RESPONDERS("responders");

    private final String param;

    SortFieldJourney(String param) {
        this.param = param;
    }

    public String getParam() {
        return param;
    }
    private static final Map<String, SortFieldJourney> BY_PARAM =
            Stream.of(values()).collect(Collectors.toMap(SortFieldJourney::getParam, f -> f));

    public static SortFieldJourney from(String value) {
        if (value == null || value.isBlank()) {
            return START_DATE;
        }
        return BY_PARAM.getOrDefault(value.toLowerCase(), START_DATE);
    }
}
