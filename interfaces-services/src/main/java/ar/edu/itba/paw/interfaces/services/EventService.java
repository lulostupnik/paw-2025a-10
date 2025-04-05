package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Event;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface EventService {
    Event createEvent(String email, String city, Date date, byte[] flyer, String description);

    void replyToEvent(String email, String username, String firstname, String lastname, String originUniversity, String career, long profilePictureId,
                        long eventId, String message);

    /*
    void createEvent(String email, String username, String firstname, String lastname, String originUniversity, String career, byte[] profilePicture, Date date, byte[] flyer, String description);
    */

    Optional<Event> getEventById(long id);

    List<Event> getAllEvents();
}
