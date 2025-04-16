package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Event;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface EventService {
    Event createEvent(String email, String cityName, Date date, byte[] flyer, String description);
    void replyToEvent(String email,long eventId, String message);
    Optional<Event> getEventById(long id);
    List<Event> getAllEvents();
}
