package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.*;

import java.util.List;

public interface EventAttendanceDao {
    void attend(long userId, long eventId);
    void cancel(long userId, long eventId);
    boolean isAttending(long userId, long eventId);
    List<User> getAttendees(long eventId);
    Page<User> getAttendees(long eventId, PageParams pageParams);
    int getAttendeesCount(long eventId);
    List<Event> getAttendingEvents(long userId);
    Page<Event> getAttendingEvents(long userId, PageParams pageParams);

}