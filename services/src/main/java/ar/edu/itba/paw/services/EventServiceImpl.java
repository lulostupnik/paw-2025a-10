package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.EventDao;
import ar.edu.itba.paw.interfaces.persistence.EventResponseDao;
import ar.edu.itba.paw.interfaces.services.CityService;
import ar.edu.itba.paw.interfaces.services.EventService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class EventServiceImpl implements EventService {
    // private final JourneyDao journeyDao;
    private final UserService userService;
    //private final CityService cityService;
    private final EventResponseDao eventResponseDao;
    private final EventDao eventDao;

    @Autowired
    public EventServiceImpl(UserService userService, CityService cityService, EventResponseDao eventResponseDao, EventDao eventDao) {
        this.userService = userService;
        this.eventResponseDao = eventResponseDao;
        //this.cityService = cityService;
        this.eventDao = eventDao;
    }

    @Override
    public Event createEvent(String email, String cityName, Date date, byte[] flyer, String description){
        Optional<User> user = userService.findByEmail(email);
        if (user.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        //FIXME: City is not being created yet, therefore as we are not passing a city to the create it will break
//        Optional<City> city = cityService.findByName("Buenos Aires");
//        if (city.isEmpty()) {
//            throw new RuntimeException("City not found");
//        }

        //FIXME: Image is missing, 1 as a placeholder
        return eventDao.create(user.get(), new City(null, null, 1), date, description, 1);
    }

    @Override
    public void replyToEvent(String email, String username, String firstname, String lastname, String originUniversity, String career, long profilePictureId, long eventId, String message) {
        eventDao.findById(eventId).orElseThrow(()-> new RuntimeException("Event not found")).getId();

        Optional<Event> maybeEvent = eventDao.findById(eventId);
        if (maybeEvent.isEmpty()) {
            throw new RuntimeException("Event not found");
        }
        long userId = userService.findByEmail(email).orElseGet(() -> userService.createUser(email, username, firstname, lastname, originUniversity, career, profilePictureId)).getId();
        eventResponseDao.create(userId, eventId, message);
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
