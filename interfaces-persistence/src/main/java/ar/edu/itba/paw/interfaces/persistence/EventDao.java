package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface EventDao {

    // CREATE
    Event create(User user, City city, LocalDate date, String description, long flyerImageId, String title, LocalTime time, String address, Integer attendeesLimit);


    // UPDATE
    void updateData(long cityId, LocalDate date, String description, String title, LocalTime time, String address, Integer attendeesLimit, long eventId, long flyerImageId/*, long userId*/);


    // DELETE
    void delete(long id);
    void deletionMessage(long id, String message);


    //GET (ONE)
    Optional<Event> findById(long eventId);

    //Optional<Event> findByUserId(long userId);
    Optional<Integer> getEventAttendanceLimit(long eventId);


    //LIST
    List<Event> listByQuery(Long cityId, LocalDate date);
    List<Event> listAll();
    List<Event> getEvents(String email);
    List<Event> findAllBetweenDates(LocalDate startDate, LocalDate endDate);


    Optional<EventWithStatistics> findEventWithStatistics(long eventId);

    Page<Event> getRecommendedEvents(long userId, PageParams pageParams);

    /**
     * getTopEvents:
     * Returns a page of upcoming events ordered by general popularity and availability.
     *
     * The query:
     * - Only includes non-deleted events with a date on or after today.
     * - Orders the results by:
     *   1. Events that are not full (i.e., attendees_count < attendees_limit).
     *   2. Higher number of attendees.
     *   3. Soonest event date.
     * Supports pagination using LIMIT and OFFSET.
     */
    Page<Event> getTopEvents(PageParams pageParams);                           //top events
    /**
     * getTopUserEvents:
     * Same as getTopEvents but orders the results by:
     * 1. Events that are not full.
     * 2. Events the user is not attending.
     * 3. Events not created by the user.
     * 4. Higher number of attendees.
     * 5. Soonest event date.
     */
    Page<Event> getTopUserEvents(long userId, PageParams pageParams);
    List<Event> getFullEvents();
    List<Event> getMyEvents(long userId);
    List<Event> getOthersEvents(long userId);
    List<Event> getEventsWithAttendanceStatus(long userId);


    //PAGE
    Page<Event> getOthersEvents(long userId, PageParams pageParams);
    Page<Event> getMyEvents(long userId, PageParams pageParams);
    Page<Event> getEvents(String email, PageParams pageParams);
    Page<Event> listAll(PageParams pageParams);
//    Page<UserEvent> getEventsWithAttendanceStatus(long userId, PageParams pageParams);
//    Page<UserEvent> getEventsWithAttendanceStatus(String search, long userId, PageParams pageParams);
    Page<Event> getEventsWithAttendanceStatus(Long userId, String search,
                                              String sortBy, String direction, String destination,
                                              LocalDate startDate, LocalDate endDate, String interest,
                                              boolean isPast, boolean isUpcoming, boolean attending, PageParams pageParams);
    Page<Event> searchEvents(String search, PageParams pageParams);
}