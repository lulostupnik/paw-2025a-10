package ar.edu.itba.paw.models;

import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class EventCreatorDTO {
    private final Event event;
    private final boolean isAttending;
    // private final boolean limitReached; ¿?
}
