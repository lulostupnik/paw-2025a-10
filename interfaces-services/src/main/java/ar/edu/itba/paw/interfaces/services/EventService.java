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
    void updateEvent(long eventId,
                     String cityName,
                     LocalDate date,
                     byte[] flyer,
                     String description,
                     String title,
                     LocalTime time,
                     String address,
                     Integer attendeesLimit);
    void deleteEvent(long id, String message);

    void replyToEvent(String email,long eventId, String message);

    Optional<Event> findEventById(long id);

    Optional<EventWithStatistics> findEventWithStatistics(User user, long eventId);

    Page<Event> searchEvents(String search, PageParams pageParams);
    Page<Event> findEvents(long userId, PageParams pageParams);

    void createEventAttendance(String email, long eventId);
    void createEventAttendance(long userId, long eventId);

    void deleteEventAttendance(String email, long eventId);
    void deleteEventAttendance(long userId, long eventId);

    void rateEvent(User user, long eventId, double rating);
    void updateEventRating(User user, long eventId, double rating);
    Optional<Rating> findRatingByUserAndEvent(long userId, long eventId);
    int countRatingsByEvent(long eventId);

    Page<Event> findEventsByAttendee(long userId, PageParams pageParams);
    Page<Event> findUpcomingEventsByAttendee(long userId,PageParams pageParams);
    Page<Event> findFinishedEventsByAttendee(long userId, PageParams pageParams);
    List<Event> findRecommendedEvents(long userId, int limit);
    List<Event> findTopEvents(int limit);
    boolean isEventOwnedByUser(String email, long eventID);

    Page<Event> searchEventsWithFilters(String search, User user,
                                        SortFieldEvent sortBy, SortDirection direction, String destination, LocalDate startDate, LocalDate endDate, String interest,
                                        boolean isPast, boolean isUpcoming, boolean attending,
                                        PageParams pageParams );

    void deleteEventResponse(EventResponse eventResponse, String message);
    int countEventResponses(long eventId);
    Page<EventResponse> findEventResponses(long eventId, PageParams pageParams);
    Optional<EventResponse> findEventResponseById(long id);

    Page<Event> findJourneyEvents(Journey journey, PageParams pageParams);

    Page<Event> findCreatedByJourney(Journey journey, PageParams pageParams);

    Page<Event> findAttendedByJourney(Journey journey, PageParams pageParams);

    void sendEventReminders();

    int countEventsCreatedByUser(long userId);
    int countEventsAttendedByUser(long userId);

    Optional<EventWithUserInfo> findEventWithUserInfo(long userId, long eventId);
}


/*
public interface EventService {
    // Creation and modification
    Event createEvent(String email, String cityName, LocalDate date, byte[] flyer, String description, String title, LocalTime time, String address, Integer attendeesLimit); // Same
    void updateEvent(long eventId, String cityName, LocalDate date, byte[] flyer, String description, String title, LocalTime time, String address, Integer attendeesLimit); // Old: editEvent
    void deleteEvent(long eventId, String message); // Old: delete

    // Event responses
    void createEventResponse(String email, long eventId, String message); // Old: replyToEvent
    void deleteEventResponse(long responseId, String message); // Old: deleteResponse

    // Attendance management
    void createEventAttendance(String email, long eventId); // Old: attendEvent(String, long)
    void createEventAttendance(long userId, long eventId); // Old: attendEvent(long, long)
    void deleteEventAttendance(String email, long eventId); // Old: cancelAttendance(String, long)
    void deleteEventAttendance(long userId, long eventId); // Old: cancelAttendance(long, long)

    // Finders for single entities
    Optional<Event> findEventById(long eventId); // Old: getEventById
    Optional<EventWithStatistics> findEventWithStatistics(User user, long eventId); // Same
    Optional<EventResponse> findEventResponseById(long responseId); // Same

    // Collections/Pagination
    Page<Event> findEvents(String search, PageParams pageParams); // Old: getAllEventsSearch
    Page<Event> findEventsByUser(String email, PageParams pageParams); // Old: getAllEvents
    Page<Event> findEvents(String search, User user, SortFieldEvent sortBy, SortDirection direction,
                          String destination, LocalDate startDate, LocalDate endDate, String interest,
                          boolean isPast, boolean isUpcoming, boolean attending,
                          PageParams pageParams); // Old: getEventsPage
    Page<Event> findEventsByAttendee(long userId, PageParams pageParams); // Old: getUserAttendingEvents
    Page<EventResponse> findEventResponses(long eventId, PageParams pageParams); // Old: listAllResponseFromEvent

    // Lists of events
    List<Event> findRecommendedEvents(long userId, int limit); // Old: getRecommendedEvents
    List<Event> findTopEvents(int limit); // Old: getTopEvents

    // Boolean checks
    boolean isEventAttendedByUser(long userId, long eventId); // Old: isUserAttending
    boolean isEventOwnedByUser(String email, long eventId); // Same, just renamed parameter ID to eventId

    // Counts and IDs
    int countEventAttendees(long eventId); // Old: getEventAttendeesCount
    int countEventResponses(long eventId); // Old: getResponseCount
    long findEventIdByResponseId(long responseId); // Old: getEventIdByResponseId

    // Scheduled operations
    void sendEventReminders(); // Same
}
 */