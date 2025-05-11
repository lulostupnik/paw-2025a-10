package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.EventResponse;
import ar.edu.itba.paw.models.Page;
import ar.edu.itba.paw.models.PageParams;
import java.time.LocalDateTime;
import java.util.Optional;

public interface EventResponseDao {
    EventResponse create(long userId, String username, long eventId, String message, LocalDateTime dateTime);
    int countByEventId(long eventId);
    Page<EventResponse> listAllByEventId(long eventId, PageParams pageParams);
    void delete(long id);
    void updateDeletionMessage(long id, String message);
    void deleteAllByEventId(long eventId);
    Optional<EventResponse> findById(long id);
    Optional<EventResponse> findByIdDeletedOrNot(long id);

    long findEventIdById(long id);



}
