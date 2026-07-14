package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.enums.SortDirection;
import ar.edu.itba.paw.models.enums.SortFieldEvent;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

public interface EventService {
    Event createEvent(long userId, long cityId, LocalDate date, String description, String title, LocalTime time, String address, Integer attendeesLimit);
    void deleteEvent(long id, String message);
    Event patchEvent(long id,
                     Long cityId,
                     LocalDate date,
                     String description,
                     String title,
                     LocalTime time,
                     String address,
                     Integer attendeesLimit,
                     Boolean deleted,
                     String deletionMessage);

    Optional<Event> findEventById(long id);

    Optional<EventWithStatistics> findEventWithStatistics(long eventId);

    EventAttendance createEventAttendance(long userId, long eventId);
    Optional<EventAttendance> findEventAttendance(long userId, long eventId);
    void deleteEventAttendance(long userId, long eventId);

    Rating rateEvent(long userId, long eventId, double rating);
    Rating updateEventRating(long eventId, long ratingId, double rating);
    Optional<Rating> findRatingById(long eventId, long ratingId);
    Page<Rating> findRatingsByEventId(long eventId, PageParams pageParams);
    void deleteRating(long eventId, long ratingId);
    int countRatingsByEvent(long eventId);

    boolean isEventOwnedByUser(String email, long eventId);
    boolean isUserEventAttendee(long userId, long eventId);
    boolean isRatingOwnedByUser(long eventId, long ratingId, long userId);

    Page<Event> searchEventsWithFilters(String search, Long recommendedForUser, Long creatorId,
                                        SortFieldEvent sortBy, SortDirection direction, String destination, LocalDate startDate, LocalDate endDate, String interest,
                                        Long attendedByUserId,
                                        String university, Integer minRating, Boolean hasCapacity, Boolean top, PageParams pageParams);

    EventResponse createEventResponse(long userId, long eventId, String message);
    void deleteEventResponse(EventResponse eventResponse, String message);
    void deleteEventResponse(long eventId, long responseId, String message);
    void patchEventResponse(long eventId, long responseId, Boolean deleted, String deletionMessage);
    Page<EventResponse> findEventResponses(long eventId, PageParams pageParams);
    Optional<EventResponse> findEventResponseById(long id);
    Optional<EventResponse> findEventResponseById(long eventId, long responseId);
    boolean isEventResponseOwnedByUser(long eventId, long responseId, long userId);

    void sendEventReminders();

    Page<EventAttendance> findEventAttendances(long eventId, PageParams pageParams);

    int countEventsCreatedByUser(long userId);
    int countEventsAttendedByUser(long userId);

    Optional<Image> getEventFlyer(long eventId);
    Image updateEventFlyer(long eventId, byte[] flyer);
}
