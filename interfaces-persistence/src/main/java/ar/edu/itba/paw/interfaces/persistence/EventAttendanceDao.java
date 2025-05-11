package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.*;
import java.util.List;
 
public interface EventAttendanceDao {
    void create(long userId, long eventId);
    void delete(long userId, long eventId);
    boolean exists(long userId, long eventId);
    int countByEventId(long eventId);


    // Esto va en este DAO?
    Page<Event> findAllEventsByAttendee(long userId, PageParams pageParams);
    Page<User> findAllAttendeesByEventId(long eventId, PageParams pageParams);

    List<User> findAllAttendeesByEventId(long eventId);

}