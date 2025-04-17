package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.EventResponse;
import ar.edu.itba.paw.models.JourneyResponse;

import java.time.LocalDate;
import java.util.List;

public interface EventResponseDao {
    EventResponse create(long userId, String username, long eventId, String message, LocalDate date);
    List<EventResponse> listAllFromEvent(long eventId);

}
