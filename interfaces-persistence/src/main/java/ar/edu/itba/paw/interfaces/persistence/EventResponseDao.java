package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.EventResponse;
import ar.edu.itba.paw.models.JourneyResponse;

import java.util.List;

public interface EventResponseDao {
    EventResponse create(long userId, long eventId, String message);
    List<EventResponse> listAllFromEvent(long eventId);

}
