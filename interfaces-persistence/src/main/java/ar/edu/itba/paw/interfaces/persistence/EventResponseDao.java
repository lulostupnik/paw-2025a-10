package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.*;

import java.time.LocalDateTime;
import java.util.Optional;

public interface EventResponseDao {
    Optional<EventResponse> findById(long id);

    EventResponse create(User user, Event event, String message);
    int countByEventId(long eventId);
    Page<EventResponse> listAllByEventId(long eventId, PageParams pageParams);
    Page<User> findRespondersByEventId(long eventId, PageParams pageParams);

//    void delete(long id);
//    void updateDeletionMessage(long id, String message);
//    void deleteAllByEventId(long eventId);
//    Optional<EventResponse> findById(long id);
//
//    long findEventIdById(long id);



}
