package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.enums.SortDirection;
import ar.edu.itba.paw.models.enums.SortFieldEvent;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface EventDao {
    Event create(User user, City city, LocalDate date, String description, long flyerImageId, String title, LocalTime time, String address, Integer attendeesLimit);
    void updateData(long cityId, LocalDate date, String description, String title, LocalTime time, String address, Integer attendeesLimit, long eventId, long flyerImageId/*, long userId*/);
    void delete(long id);
    void deletionMessage(long id, String message);
    Optional<Event> findById(long eventId);
    Optional<Integer> getEventAttendanceLimit(long eventId);
    List<Event> findAllBetweenDates(LocalDate startDate, LocalDate endDate);
    Optional<EventWithStatistics> findEventWithStatistics(Long userId, long eventId);
    Page<Event> getRecommendedEvents(long userId, PageParams pageParams);
    Page<Event> getTopEvents(PageParams pageParams);                           //top events
    Page<Event> getTopUserEvents(long userId, PageParams pageParams);
    Page<Event> getOthersEvents(long userId, PageParams pageParams);
    Page<Event> getMyEvents(long userId, PageParams pageParams);
    Page<Event> getEvents(String email, PageParams pageParams);
    Page<Event> listAll(PageParams pageParams);
//    Page<UserEvent> getEventsWithAttendanceStatus(long userId, PageParams pageParams);
//    Page<UserEvent> getEventsWithAttendanceStatus(String search, long userId, PageParams pageParams);
    Page<Event> getEvents(Long userId, String search,
                          SortFieldEvent sortBy, SortDirection direction, String destination,
                          LocalDate startDate, LocalDate endDate, String interest,
                          boolean isPast, boolean isUpcoming, boolean attending, PageParams pageParams);
    Page<Event> searchEvents(String search, PageParams pageParams);
}