package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.*;
import ar.edu.itba.paw.interfaces.services.CityService;
import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.interfaces.services.EventService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class EventServiceImpl implements EventService {
    // private final JourneyDao journeyDao;
    private final UserService userService;
    //private final CityService cityService;
    private final EventResponseDao eventResponseDao;
    private final EmailService emailService;
    private final EventDao eventDao;
    private final ImageDao imageDao;
    private final UserDao userDao;
    private final CityDao cityDao;

    @Autowired
    public EventServiceImpl(UserService userService, CityService cityService, EventResponseDao eventResponseDao, EventDao eventDao, EmailService emailService, ImageDao imageDao, UserDao userDao, CityDao cityDao) {
        this.userService = userService;
        this.eventResponseDao = eventResponseDao;
        //this.cityService = cityService;
        this.eventDao = eventDao;
        this.emailService = emailService;
        this.imageDao = imageDao;
        this.userDao = userDao;
        this.cityDao = cityDao;
    }

    @Override
    public Event createEvent(String email, String cityName, Date date, byte[] flyer, String description, String firstname, String lastname, String username, String originUniversity, String career, long profilePictureId) {
        City city = cityDao.findByName(cityName).orElseThrow(() -> new RuntimeException("City not found"));
        Optional<User> maybeUser = userService.findByEmail(email);
        User user = maybeUser.orElseGet(() -> userService.createUser(email, username, firstname, lastname, originUniversity, career, profilePictureId));
        // long flyerImageId = imageDao.saveImage(flyer);
        long flyerImageId = 1; // FIXME

        return eventDao.create(user, city, date, description, flyerImageId);
    }

    @Override
    public void replyToEvent(String email, String username, String firstname, String lastname, String originUniversity, String career, long profilePictureId, long eventId, String message) {
        Optional<Event> maybeEvent = eventDao.findById(eventId);
        if (maybeEvent.isEmpty()) {
            throw new RuntimeException("Event not found");
        }
        Event event = maybeEvent.get();
        long userId = userService.findByEmail(email).orElseGet(() -> userService.createUser(email, username, firstname, lastname, originUniversity, career, profilePictureId)).getId();
        eventResponseDao.create(userId, eventId, message);
        //@TODO cambiar locale
        emailService.answerEventMail(email,event.getUser().getEmail(), firstname, lastname, username, career, originUniversity, message, Locale.ENGLISH);
    }

    // FIXME: Mepa que esto debería ser transaccional
    @Override
    public Event createEvent(String email, String cityName, Date date, byte[] flyer, String description, String username, String firstname, String lastname, String originUniversity, String career, byte[] profilePicture) {
        City city = cityDao.findByName(cityName).orElseThrow(() -> new RuntimeException("City not found"));
        User user = userService.findByEmail(email).orElseGet(() -> userService.createUser(email, username, firstname, lastname, originUniversity, career, profilePicture));
        // long profilePictureId = imageDao.saveImage(profilePicture);
        // long profilePictureId = 1; // FIXME
        long flyerImageId = imageDao.saveImage(flyer);
        // long flyerImageId = 1; // FIXME

        return eventDao.create(user, city, date, description, flyerImageId);
    }

    @Override
    public void replyToEvent(String email, String username, String firstname, String lastname, String originUniversity, String career, byte[] profilePictureId, long eventId, String message) {
        Event event = eventDao.findById(eventId).orElseThrow(() -> new RuntimeException("Event not found"));
        long userId = userService.findByEmail(email).orElseGet(() -> userService.createUser(email, username, firstname, lastname, originUniversity, career, profilePictureId)).getId();
        eventResponseDao.create(userId, eventId, message);
        //@TODO cambiar locale
        emailService.answerEventMail(email,event.getUser().getEmail(), firstname, lastname, username, career, originUniversity, message, Locale.ENGLISH);
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
