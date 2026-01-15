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
    Event createEvent(long userId, String cityName, LocalDate date, byte[] flyer, String description, String title, LocalTime time, String address, Integer attendeesLimit);
    Event updateEvent(long eventId,
                     String cityName,
                     LocalDate date,
                     byte[] flyer,
                     String description,
                     String title,
                     LocalTime time,
                     String address,
                     Integer attendeesLimit);
    void deleteEvent(long id, String message);

    Optional<Event> findEventById(long id);

    Optional<EventWithStatistics> findEventWithStatistics(User user, long eventId);

    Page<Event> searchEvents(String search, PageParams pageParams);
    Page<Event> findEvents(long userId, PageParams pageParams);

    EventAttendance createEventAttendance(long userId, long eventId);

    void deleteEventAttendance(long userId, long eventId);

    Rating rateEvent(User user, long eventId, double rating);
    Rating rateEvent(long userId, long eventId, double rating);
    Rating updateEventRating(User user, long eventId, double rating);
    Rating updateEventRating(long userId, long eventId, double rating);
    Optional<Rating> findRatingByUserAndEvent(long userId, long eventId);
    Optional<Rating> findRatingById(long ratingId);
    Page<Rating> findRatingsByEventId(long eventId, PageParams pageParams);
    void deleteRating(long ratingId);
    int countRatingsByEvent(long eventId);

//    Page<Event> findUpcomingEventsByAttendee(long userId,PageParams pageParams);
//    Page<Event> findFinishedEventsByAttendee(long userId, PageParams pageParams);
    List<Event> findRecommendedEvents(long userId, int limit);
    List<Event> findTopEvents(int limit);
    boolean isEventOwnedByUser(String email, long eventId);
    boolean isUserEventAttendee(long userId, long eventId);
    boolean isRatingOwnedByUser(long ratingId, long userId);

    Page<Event> searchEventsWithFilters(String search, Long userId,
                                        SortFieldEvent sortBy, SortDirection direction, String destination, LocalDate startDate, LocalDate endDate, String interest,
                                        boolean isPast, boolean isUpcoming, boolean attending, Long attendedByUserId,
                                        String university, Integer minRating, Boolean hasCapacity,
                                        PageParams pageParams);

    EventResponse createEventResponse(String email, long eventId, String message);
    EventResponse createEventResponse(long userId, long eventId, String message);
    void deleteEventResponse(EventResponse eventResponse, String message);
    void deleteEventResponse(long responseId, String message);
    Page<EventResponse> findEventResponses(long eventId, PageParams pageParams);
    Optional<EventResponse> findEventResponseById(long id);

    Page<Event> findCreatedByJourney(Journey journey, PageParams pageParams);
    Page<Event> findAttendedByJourney(Journey journey, PageParams pageParams);

    void sendEventReminders();

    Page<User> findEventAttendees(long eventId, PageParams pageParams);

    int countEventsCreatedByUser(long userId);
    int countEventsAttendedByUser(long userId);

    Optional<EventWithUserInfo> findEventWithUserInfo(long userId, long eventId);
}

