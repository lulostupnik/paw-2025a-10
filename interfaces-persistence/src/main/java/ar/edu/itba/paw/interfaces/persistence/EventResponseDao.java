package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.EventResponse;
import ar.edu.itba.paw.models.JourneyResponse;

public interface EventResponseDao {
    EventResponse create(long userId, long eventId, String message);

}
