package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface EventDao {

    //CREATE
    Event create(User user, City city, LocalDate date, String description, long flyerImageId, String title, LocalTime time, String address, Integer attendeesLimit);


    //UPDATE


    //GET (ONE)
    Optional<Event> findById(long eventId);

    //Optional<Event> findByUserId(long userId);
    Optional<Integer> getEventAttendanceLimit(long eventId);


    //LIST
    List<Event> listByQuery(Long cityId, LocalDate date);

    List<Event> listAll();

    List<Event> getEvents(String email);

    List<UserEvent> getRecommendedEvents(String email);

    List<Event> getTopEvents();

    List<Event> getFullEvents();

    void delete(long id);

    void deletionMessage(long id, String message);

    List<Event> getMyEvents(long userId);

    List<Event> getOthersEvents(long userId);

    List<UserEvent> getEventsWithAttendanceStatus(long userId);

    //PAGE
    Page<Event> getOthersEvents(long userId, int pageNumber, int pageSize);

    Page<Event> getMyEvents(long userId, int pageNumber, int pageSize);

    Page<Event> getEvents(String email, int pageNumber, int pageSize);

    Page<Event> listAll(int pageNumber, int pageSize);

    void updateData(long cityid, LocalDate date, String description, String title, LocalTime time, String address, Integer attendeesLimit, long eventId/*, long userId*/);

    Page<Event> searchEvents(String search, int pageNumber, int pageSize);
}