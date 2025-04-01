package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.EventDao;
import ar.edu.itba.paw.interfaces.services.EventService;
import ar.edu.itba.paw.interfaces.services.UniversityService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.spec.ECField;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class EventServiceImpl implements EventService {
    // private final JourneyDao journeyDao;
    private final UserService userService;
    private final EventDao eventDao;

    @Autowired
    public EventServiceImpl(UserService userService, EventDao eventDao) {
        this.userService = userService;
        this.eventDao = eventDao;
    }

    @Override
    public Event createEvent(String email, String city, Date date, String description){
        Optional<User> user = userService.findByEmail(email);
        if (user.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        //FIXME: City is not implemented
        //Optional<City> city = cityService.findByName(cityName);
        //if (city.isEmpty()) {
        //    throw new RuntimeException("City not found");
        //}

        //FIXME: Image is missing, sequential as a placeholder
        return eventDao.create(user.get(), null, date, description, new java.util.concurrent.atomic.AtomicInteger(1).getAndIncrement());
    }

    @Override
    public void replyToEvent(String email, String username, String firstname, String lastname, String originUniversity, String career, long profilePictureId, long eventId, String message) {

    }

    @Override
    public Optional<Event> getEventById(long id){
        return eventDao.findById(id);
    }

    @Override
    public List<Event> getAllEvents() {
        return eventDao.listAll();
    }
}
