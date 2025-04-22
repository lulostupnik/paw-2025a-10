package ar.edu.itba.paw.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class EventWithAttendeesDTO {
    private final Event event;
    private final List<User> attendees;
}
