package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.*;
import java.util.Optional;

public interface EventResponseDao {
    Optional<EventResponse> findById(long id);

    EventResponse create(User user, Event event, String message);
    Page<EventResponse> listAllByEventId(long eventId, PageParams pageParams);
    Page<User> findRespondersByEventId(long eventId, PageParams pageParams);

}
