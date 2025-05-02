package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.EventResponse;
import ar.edu.itba.paw.models.Page;

import java.time.LocalDateTime;
import java.util.List;

public interface EventResponseDao {
    EventResponse create(long userId, String username, long eventId, String message, LocalDateTime dateTime);
    List<EventResponse> listAllFromEvent(long eventId);
    long getCount(long eventId);
    Page<EventResponse> listAllFromEvent(long eventId, int page, int size);
//    List<User> listAllUsersResponders(long eventId); //@todo no se usa el final, podria borrar
//    List<User> listAllUsersRespondersMinusUsers(long eventId, List<Long> user_ids);
    void delete(long id);
//    List<EmailRecipient> listAllEmailsRespondersMinusUsers(long eventId, List<Long> userIds);
    long getEventIdByResponseId(long eventId);
    void deletionMessage(long id, String message);
}
