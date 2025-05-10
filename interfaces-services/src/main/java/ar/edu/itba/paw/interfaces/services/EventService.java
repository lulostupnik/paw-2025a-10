package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.enums.SortDirection;
import ar.edu.itba.paw.models.enums.SortFieldEvent;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface EventService {
    Event createEvent(String email, String cityName, LocalDate date, byte[] flyer, String description, String title, LocalTime time, String address, Integer attendeesLimit);
    void replyToEvent(String email,long eventId, String message);
    Optional<Event> getEventById(long id);
    Page<Event> getAllEvents(PageParams pageParams);
    Optional<EventWithStatistics> findEventWithStatistics(User user, long eventId);
    Page<Event> getAllEventsSearch(String search, PageParams pageParams);
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
    Page<Event> getUserAttendingEvents(long userId, PageParams pageParams);
    List<Event> getRecommendedEvents(long userId, int limit);
    List<Event> getTopEvents(int limit);
    boolean isEventOwnedByUser(String email, long eventID);
    boolean isEventFull(long eventId);
    boolean isEventFull(Event event);
    Page<Event> getEventsPage(String search, User user,
                              SortFieldEvent sortBy, SortDirection direction, String destination, LocalDate startDate, LocalDate endDate, String interest,
                              boolean isPast, boolean isUpcoming, boolean attending,
                              PageParams pageParams );
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
    void deleteResponse(long id, String message);
    long getEventIdByResponseId(long responseId);
    int getResponseCount(long eventId);
    Page<EventResponse> listAllResponseFromEvent(long eventId, PageParams pageParams);
    Optional<EventResponse> findEventResponseById(long id);
    void sendEventReminders();
}
