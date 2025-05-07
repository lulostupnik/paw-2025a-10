package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface EventService {
    Event createEvent(String email, String cityName, LocalDate date, byte[] flyer, String description, String title, LocalTime time, String address, Integer attendeesLimit);
    void replyToEvent(String email,long eventId, String message);
    Optional<Event> getEventById(long id);
    List<Event> getAllEvents();  // will be deprecated
    Page<Event> getAllEvents(PageParams pageParams);

    Page<Event> getAllEventsSearch(String search, PageParams pageParams);

    List<Event> getAllEvents(String email);
    Page<Event> getAllEvents(String email, PageParams pageParams);
    void attendEvent(String email, long eventId);
    void attendEvent(long userId, long eventId);
    void cancelAttendance(String email, long eventId);
    void cancelAttendance(long userId, long eventId);
    boolean isUserAttending(String email, long eventId);
    boolean isUserAttending(long userId, long eventId);
    List<User> getEventAttendees(long eventId);
    Page<User> getEventAttendees(long eventId, PageParams pageParams);
    int getEventAttendeesCount(long eventId);
    List<Event> getUserAttendingEvents(String userEmail);
    Page<Event> getUserAttendingEvents(long userId, PageParams pageParams);
    List<Event> getUserAttendingEvents(long userId);
    List<EventResponse> getEventResponses(long eventId);
    List<UserEvent> getRecommendedEvents(long userId, int limit);
    List<Event> getTopEvents(int limit);
    Boolean isEventOwnedByUser(String email, long eventID);
    boolean isEventFull(long eventId);
    List<Event> getFullEvents();

    List<UserEvent> getEventsWithAttendanceStatus(long userId);
    List<UserEvent> getEventsWithAttendanceStatus(String email);

    Page<UserEvent> getEventsPageWithAttendanceStatus(String search,User user, PageParams pageParams );
    void editEvent(long eventId,
                          String cityName,
                          LocalDate date,
                          byte[] flyer,
                          String description,
                          String title,
                          LocalTime time,
                          String address,
                          Integer attendeesLimit);

    void delete(long id, String message);
}
