package ar.edu.itba.paw.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class EventWithAttendanceStatus {
    private final Event event;
    private final boolean isAttending;
}