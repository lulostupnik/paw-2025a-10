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

    void incrementAttendeesCount(long eventId);

    void update(long cityId, LocalDate date, String description, String title, LocalTime time, String address, Integer attendeesLimit, long eventId, long flyerImageId);

    Optional<EventWithUserInfo> findEventWithUserInfo(long userId, long eventId);

    void delete(long id);
    void updateDeletionMessage(long id, String message);
    Optional<Event> findById(long id);
    Optional<Integer> findAttendanceLimitById(long eventId);
    List<Event> findAllBetweenDates(LocalDate startDate, LocalDate endDate);
    Page<Event> findRecommended(long userId, PageParams pageParams);
    Page<Event> findTop(PageParams pageParams);
    Page<Event> findTopByUser(long userId, PageParams pageParams);
    Page<Event> findByUserEmail(String email, PageParams pageParams);
    Page<Event> findAll(PageParams pageParams);
    Page<Event> findAllWithFilters(Long userId, String search,
                                   SortFieldEvent sortBy, SortDirection direction, String destination,
                                   LocalDate startDate, LocalDate endDate, String interest,
                                   boolean isPast, boolean isUpcoming, boolean isAttending, PageParams pageParams);
    Page<Event> search(String search, PageParams pageParams);

    int countEventsCreatedByUser(long userId);

    Optional<CountryAttendeeCount> findTopAttendeeCountry(long eventId);

    int countEventsAttendedByUser(long userId);

    Page<Event> findAllEventsByAttendee(long userId, PageParams pageParams);


}
