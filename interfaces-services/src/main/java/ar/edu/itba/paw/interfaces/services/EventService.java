package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface EventService {
    Event createEvent(String email, String cityName, LocalDate date, byte[] flyer, String description, String title, LocalTime time, String address, Integer attendeesLimit);
    void replyToEvent(String email,long eventId, String message);
    Optional<Event> getEventById(long id);
    List<Event> getAllEvents();
    Page<Event> getAllEvents(int page, int size);
    List<Event> getAllEvents(String email);
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
//    List<User> getEventResponders(long eventId);
    List<UserEvent> getRecommendedEvents(String email);
    List<Event> getTopEvents();
    Boolean isEventOwnedByUser(String email, long eventID);
    boolean isEventFull(long eventId);
    List<Event> getFullEvents();

    List<UserEvent> getEventsWithAttendanceStatus(long userId);
    List<UserEvent> getEventsWithAttendanceStatus(String email);

    void delete(long id, String message);
}
