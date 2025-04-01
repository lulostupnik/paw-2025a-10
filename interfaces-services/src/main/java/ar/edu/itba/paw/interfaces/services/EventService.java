package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Event;
import ar.edu.itba.paw.models.Journey;

import javax.validation.OverridesAttribute;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface EventService {
    Event createEvent(String email, String city, Date date, String description);

    void replyToEvent(String email, String username, String firstname, String lastname, String originUniversity, String career, long profilePictureId,
                        long eventId, String message);

    Optional<Event> getEventById(long id);

    List<Event> getAllEvents();
}
