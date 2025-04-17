package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.EventResponse;
import ar.edu.itba.paw.models.JourneyResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface EventResponseDao {
    EventResponse create(long userId, String username, long eventId, String message, LocalDateTime dateTime);
    List<EventResponse> listAllFromEvent(long eventId);

}
