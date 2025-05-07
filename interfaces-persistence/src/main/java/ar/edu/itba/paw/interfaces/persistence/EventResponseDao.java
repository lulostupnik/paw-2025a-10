package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.EventResponse;
import ar.edu.itba.paw.models.Page;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventResponseDao {
    EventResponse create(long userId, String username, long eventId, String message, LocalDateTime dateTime);
    List<EventResponse> listAllFromEvent(long eventId);
    int getCount(long eventId);
    Page<EventResponse> listAllFromEvent(long eventId, int page, int size);
    void delete(long id);
    long getEventIdByResponseId(long eventId);
    void deletionMessage(long id, String message);
    void deleteByEventId(long eventId);
    Optional<EventResponse> findById(long responseId);
    Optional<EventResponse> findByIdDeletedOrNotDeleted(long responseId);


}
