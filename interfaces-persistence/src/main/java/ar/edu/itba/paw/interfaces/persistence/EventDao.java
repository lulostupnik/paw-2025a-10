package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.enums.SortDirection;
import ar.edu.itba.paw.models.enums.SortFieldEvent;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

public interface EventDao {
    Event create(User user, City city, LocalDate date, String description, Long flyerImageId, String title, LocalTime time, String address, Integer attendeesLimit);

    Optional<Event> findById(long id);
    Optional<Event> findByIdForUpdate(long id);
    Page<Event> findRecommended(long userId, PageParams pageParams);
    Page<Event> findTop(PageParams pageParams);
    Page<Event> findTopByUser(long userId, PageParams pageParams);
    Page<Event> findByUserId(long userId, PageParams pageParams);
    Page<Event> findAll(PageParams pageParams);
    Page<Event> findAllWithFilters(Long creatorId, String search,
                                   SortFieldEvent sortBy, SortDirection direction, String destination,
                                   LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime,
                                   String interest, Long attendedByUserId,
                                   String university, Integer minRating, Boolean hasCapacity, PageParams pageParams);
    Page<Event> search(String search, PageParams pageParams);
    Optional<CountryAttendeeCount> findTopAttendeeCountry(long eventId);

    int countEventsCreatedByUser(long userId);

    Page<Event> findAllBetweenDates(LocalDate startDate, LocalDate endDate, PageParams pageParams);

}
