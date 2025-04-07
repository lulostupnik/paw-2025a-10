package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.*;
import ar.edu.itba.paw.interfaces.services.CityService;
import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.interfaces.services.EventService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

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
    public Event createEvent(String email, String cityName, Date date, byte[] flyer, String description, String username, String firstname, String lastname, String originUniversity, String career, byte[] profilePicture) {
        City city = cityDao.findByName(cityName).orElseThrow(() -> new RuntimeException("City not found"));
        User user = userService.findByEmail(email).orElseGet(() -> userService.createUser(email, username, firstname, lastname, originUniversity, career, profilePicture, new String[]{}));

        long flyerImageId = imageDao.saveImage(flyer);

        return eventDao.create(user, city, date, description, flyerImageId);
    }

    @Override
    public void replyToEvent(String email, String username, String firstname, String lastname, String originUniversity, String career, byte[] profilePicture, long eventId, String message) {
        Event event = eventDao.findById(eventId).orElseThrow(() -> new RuntimeException("Event not found"));
        long userId = userService.findByEmail(email).orElseGet(() -> userService.createUser(email, username, firstname, lastname, originUniversity, career, profilePicture, new String[]{})).getId();
        eventResponseDao.create(userId, eventId, message);
        //@TODO cambiar locale
        emailService.answerEventMail(email,event.getUser().getEmail(), firstname, lastname, username, career, originUniversity, message, Locale.ENGLISH, profilePicture);
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
