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

    Optional<Event> findById(long id);
    Page<Event> findRecommended(long userId, PageParams pageParams);
    Page<Event> findTop(PageParams pageParams);
    Page<Event> findTopByUser(long userId, PageParams pageParams);
    Page<Event> findByUserEmail(String email, PageParams pageParams);
    Page<Event> findAll(PageParams pageParams);
    Page<Event> findAllWithFilters(Long userId, String search,
                                   SortFieldEvent sortBy, SortDirection direction, String destination,
                                   LocalDate startDate, LocalDate endDate, String interest,
                                   boolean isAttending, boolean isCreator, PageParams pageParams);
    Page<Event> search(String search, PageParams pageParams);
    Optional<CountryAttendeeCount> findTopAttendeeCountry(long eventId);

    Page<Event> findAllEventsByAttendee(long userId, PageParams pageParams);

    int countEventsCreatedByUser(long userId);

    Page<Event> findAllBetweenDates(LocalDate startDate, LocalDate endDate, PageParams pageParams);


    //Optional<EventWithUserInfo> findEventWithUserInfo(long userId, long eventId);

    // todo: puede llegar a tener sentido tener el siguiente método? Mepa que no, siempre que quieras ver el limite supongo que vas a tener ya el evento
    // Optional<Integer> findAttendanceLimitById(long eventId);


}
