package ar.edu.itba.paw.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class EventWithStatistics {
    private final Event event;
    private final int createdEventsCount;
    private final int attendedEventsCount;
    private final String topAttendeeCountry;
    private final int topAttendeeCountryCount;
}
