package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Event;
import ar.edu.itba.paw.models.EventResponse;
import ar.edu.itba.paw.models.User;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface EventService {
    Event createEvent(String email, String cityName, Date date, byte[] flyer, String description);
    void replyToEvent(String email,long eventId, String message);
    Optional<Event> getEventById(long id);
    List<Event> getAllEvents();
    void attendEvent(String email, long eventId);
    void attendEvent(long userId, long eventId);
    void cancelAttendance(String email, long eventId);
    void cancelAttendance(long userId, long eventId);
    boolean isUserAttending(String email, long eventId);
    boolean isUserAttending(long userId, long eventId);
    List<User> getEventAttendees(long eventId);
    int getEventAttendeesCount(long eventId);
    List<Event> getUserAttendingEvents(String userEmail);
    List<Event> getUserAttendingEvents(long userId);
    List<EventResponse> getEventResponses(long eventId);
    List<Event> getRecommendedEvents(String email);
    List<Event> getTopEvents();
}
