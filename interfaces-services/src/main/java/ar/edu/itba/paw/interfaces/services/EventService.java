package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Event;
import ar.edu.itba.paw.models.Journey;

import java.util.Date;

public interface EventService {
    Event createEvent(String email, String username, String firstname, String lastname, String originUniversity, String career, long profilePictureId,
                        String destinationUniversity, String destinationCity, Date date, String description);

    void replyToEvent(String email, String username, String firstname, String lastname, String originUniversity, String career, long profilePictureId,
                        long eventId, String message);
}
