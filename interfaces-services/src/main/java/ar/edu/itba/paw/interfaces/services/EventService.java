package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Event;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface EventService {
    Event createEvent(String email, String cityName, Date date, byte[] flyer, String description, String username, String firstname, String lastname, String originUniversity, String career, byte[] profilePicture);
    void replyToEvent(String email, String username, String firstname, String lastname, String originUniversity, String career, byte[] profilePictureId,
                        long eventId, String message);

    Optional<Event> getEventById(long id);

    List<Event> getAllEvents();
}
