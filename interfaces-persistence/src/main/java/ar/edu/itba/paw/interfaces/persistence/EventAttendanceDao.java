package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.*;

public interface EventAttendanceDao {
    void create(long userId, long eventId);
    void delete(long userId, long eventId);
    boolean exists(long userId, long eventId);
    void create(User user, Event event);
    void delete(User user, Event event);
    boolean exists(User user, Event event);
    Page<EventAttendance> listAllByEventId(long eventId, PageParams pageParams);
    Page<EventAttendance> listAllByUserId(long userId, PageParams pageParams);
    int countAttendantsByEventId(long eventId);
    Page<User> findAttendeesByEventId(long eventId, PageParams pageParams);

}