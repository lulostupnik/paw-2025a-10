package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.CursorPage;
import ar.edu.itba.paw.models.EventResponse;
import ar.edu.itba.paw.models.JourneyResponse;
import ar.edu.itba.paw.models.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface EventResponseDao {
    EventResponse create(long userId, String username, long eventId, String message, LocalDateTime dateTime);
    List<EventResponse> listAllFromEvent(long eventId);
    List<User> listAllUsersResponders(long eventId); //@todo no se usa el final, podria borrar
//    List<User> listAllUsersRespondersMinusUsers(long eventId, List<Long> user_ids);
    String[] listAllEmailsRespondersMinusUsers(long eventId, List<Long> userIds);
}
