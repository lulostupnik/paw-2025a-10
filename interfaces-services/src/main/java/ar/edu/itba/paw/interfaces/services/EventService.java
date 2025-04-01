package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Event;
import ar.edu.itba.paw.models.Journey;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.OverridesAttribute;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface EventService {
    Event createEvent(String email, String city, Date date, MultipartFile flyer, String description);

    void replyToEvent(String email, String username, String firstname, String lastname, String originUniversity, String career, long profilePictureId,
                        long eventId, String message);

    Optional<Event> getEventById(long id);

    List<Event> getAllEvents();
}
