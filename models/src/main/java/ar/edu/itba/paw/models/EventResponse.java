package ar.edu.itba.paw.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class EventResponse {
    private final long userId;
    private final long eventId;
    private final String message;
}
