package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.CursorPage;
import ar.edu.itba.paw.models.Event;
import ar.edu.itba.paw.models.Page;
import ar.edu.itba.paw.models.User;
import java.util.List;

public interface EventAttendanceDao {
    void attend(long userId, long eventId);
    void cancel(long userId, long eventId);
    boolean isAttending(long userId, long eventId);
    List<User> getAttendees(long eventId);
    Page<User> getAttendees(long eventId, int pageNumber, int pageSize);
    int getAttendeesCount(long eventId);
    List<Event> getAttendingEvents(long userId);
    Page<Event> getAttendingEvents(long userId, int pageNumber, int pageSize);

}