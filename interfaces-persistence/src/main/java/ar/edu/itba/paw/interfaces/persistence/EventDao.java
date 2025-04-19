package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface EventDao {

    Event create(User user, City city, Date date, String description, long flyerImageId, String title);
    List<Event> listByQuery(Long cityId, Date date);
    //Optional<Event> findByUserId(long userId);
    Optional<Event> findById(long eventId);
    List<Event> listAll();
    List<Event> getRecommendedEvents(String email);
    List<Event> getTopEvents();

    CursorPage<Event, Long> listAll(Long cursor, int limit);
    CursorPage<Event, Long> listByCity(City city, Long cursor, int limit);
}
