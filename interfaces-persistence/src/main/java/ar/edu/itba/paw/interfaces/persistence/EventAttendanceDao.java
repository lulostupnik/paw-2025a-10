package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.*;

public interface EventAttendanceDao {
    EventAttendance create(long userId, long eventId);
    void delete(long userId, long eventId);
    boolean exists(long userId, long eventId);
    EventAttendance create(User user, Event event);
    void delete(User user, Event event);
    boolean exists(User user, Event event);

    Page<User> findAttendeesByEventId(long eventId, PageParams pageParams);

    int countEventsAttendedByUser(long userId);


}