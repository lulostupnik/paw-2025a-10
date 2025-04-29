package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.EventResponse;
import java.time.LocalDateTime;
import java.util.List;

public interface EventResponseService {
    EventResponse create(long userId, String username, long eventId, String message, LocalDateTime dateTime);
    void delete(long id,String message);
    long getEventIdByResponseId(long eventId);
    List<EventResponse> listAllFromEvent(long eventId);

}
