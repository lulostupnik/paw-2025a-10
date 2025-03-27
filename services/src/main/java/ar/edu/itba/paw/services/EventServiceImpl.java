package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.services.EventService;
import ar.edu.itba.paw.interfaces.services.UniversityService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Event;
import ar.edu.itba.paw.models.Journey;
import ar.edu.itba.paw.models.University;
import ar.edu.itba.paw.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.spec.ECField;
import java.util.Date;
@Service
public class EventServiceImpl implements EventService {
    // private final JourneyDao journeyDao;
    private final UserService userService;
    private final UniversityService universityService;

    @Autowired
    public EventServiceImpl(UserService userService, UniversityService universityService) {
        this.userService = userService;
        this.universityService = universityService;
    }

    @Override
    public Event createEvent(String email, String username, String firstname, String lastname, String originUniversity, String career, long profilePictureId, String destinationUniversity, String destinationCity, Date date, String description) {
        return null;
    }

    @Override
    public void replyToEvent(String email, String username, String firstname, String lastname, String originUniversity, String career, long profilePictureId, long eventId, String message) {

    }
}
