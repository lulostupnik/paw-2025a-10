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
    void updateData(long cityId, LocalDate date, String description, String title, LocalTime time, String address, Integer attendeesLimit, long eventId/*, long userId*/);


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



    /**
     * getRecommendedEvents:
     * Returns a page of recommended events for a user based on their city (through their university).
     *
     * The query:
     * - Excludes events created by the user.
     * - Excludes deleted events
     * - Only includes events located in the same city as the user.
     * - Indicates whether the user is attending (is_attending) and whether they are the owner (is_owner).
     * - Orders the results by:
     *   1. Events that are not full (i.e., still have available spots).
     *   2. Events the user is not attending.
     *   3. Events not created by the user.
     *   4. Higher number of attendees.
     *   5. Soonest event date.
     *
     * Supports pagination using LIMIT and OFFSET.
     */

    Page<UserEvent> getRecommendedEvents(long userId, int page, int size);

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
    Page<Event> getTopEvents(int page, int size);                           //top events
    /**
     * getTopUserEvents:
     * Same as getTopEvents but orders the results by:
     * 1. Events that are not full.
     * 2. Events the user is not attending.
     * 3. Events not created by the user.
     * 4. Higher number of attendees.
     * 5. Soonest event date.
     */
    Page<UserEvent> getTopUserEvents(long userId, final int page, final int size);
    List<Event> getFullEvents();
    List<Event> getMyEvents(long userId);
    List<Event> getOthersEvents(long userId);
    List<UserEvent> getEventsWithAttendanceStatus(long userId);


    //PAGE
    Page<Event> getOthersEvents(long userId, int pageNumber, int pageSize);
    Page<Event> getMyEvents(long userId, int pageNumber, int pageSize);
    Page<Event> getEvents(String email, int pageNumber, int pageSize);
    Page<Event> listAll(int pageNumber, int pageSize);
    Page<UserEvent> getEventsWithAttendanceStatus(long userId, int page, int size);
    Page<UserEvent> getEventsWithAttendanceStatus(String search, long userId, int page, int size);
    Page<Event> searchEvents(String search, int pageNumber, int pageSize);
}