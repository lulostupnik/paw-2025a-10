package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface EventDao {

    Event create(User user, City city, LocalDate date, String description, long flyerImageId, String title, LocalTime time, String address, Integer attendeesLimit);
    List<Event> listByQuery(Long cityId, LocalDate date);
    //Optional<Event> findByUserId(long userId);
    Optional<Event> findById(long eventId);
    List<Event> listAll();
    List<Event> getEvents(String email);
    List<Event> getRecommendedEvents(String email);
    List<Event> getTopEvents();
    Optional<Integer> getEventAttendanceLimit(long eventId);
    List<Event> getFullEvents();


    List<Event> getMyEvents(long userId);
    List<Event> getOthersEvents(long userId);

    List<EventWithAttendanceStatus> getEventsWithAttendanceStatus(long userId);
}
