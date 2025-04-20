package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.*;
import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.interfaces.services.EventService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
public class EventServiceImpl implements EventService {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventServiceImpl.class);

    private final UserService userService;
    private final EventResponseDao eventResponseDao;
    private final EmailService emailService;
    private final EventDao eventDao;
    private final ImageDao imageDao;
    private final CityDao cityDao;
    private final EventAttendanceDao eventAttendanceDao;

    @Autowired
    public EventServiceImpl(UserService userService, EventResponseDao eventResponseDao, EventDao eventDao, EmailService emailService, ImageDao imageDao, CityDao cityDao, EventAttendanceDao eventAttendanceDao) {
        this.userService = userService;
        this.eventResponseDao = eventResponseDao;
        this.eventDao = eventDao;
        this.emailService = emailService;
        this.imageDao = imageDao;
        this.cityDao = cityDao;
        this.eventAttendanceDao = eventAttendanceDao;
    }

    @Transactional
    @Override
    public Event createEvent(String email, String cityName, Date date, byte[] flyer, String description, String title, LocalTime time, String address, int attendeesLimit) {
        LOGGER.debug("Creating event for user {}", email);

        LOGGER.debug("Looking for city {}", cityName);
        City city = cityDao.findByName(cityName).orElseThrow(() -> new RuntimeException("City not found"));

        LOGGER.debug("Looking for user {}", email);
        User user = userService.findByEmail(email).orElseThrow(()-> new RuntimeException("User not found"));

        LOGGER.debug("Saving event image");
        long flyerImageId = imageDao.saveImage(flyer);

        LOGGER.info("Event data is valid, commiting new event to persistance");
        return eventDao.create(user, city, date, description, flyerImageId, title, time, address, attendeesLimit);
    }

    @Transactional
    @Override
    public void replyToEvent(String email, long eventId, String message) {
        LOGGER.debug("Replying to event {}", eventId);

        LOGGER.debug("Looking for event {}", eventId);
        Event event = eventDao.findById(eventId).orElseThrow(() -> new RuntimeException("Event not found"));

        //parche temporal buscar por username
        LOGGER.debug("Looking for user {}", email);
        User user = userService.findByEmail(email).orElseThrow(()-> new RuntimeException("User not found"));

        LOGGER.info("Event reply is valid, commiting new reply to persistence");
        eventResponseDao.create(user.getId(), user.getUsername(),eventId, message, LocalDateTime.now());

        LOGGER.info("Sending email notification to event owner");
        //@TODO cambiar locale
        emailService.answerEventMail(email,event.getUser().getEmail(), user.getFirstname(),
                user.getLastname(),user.getUsername(),user.getCareer().getName(), user.getUniversity().getName(),
                message, user.getLocale(),
                imageDao.getImageById(user.getProfilePictureId()).orElseThrow(() -> new RuntimeException("Image not found")).getData());
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Event> getEventById(long id){
        return eventDao.findById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Event> getAllEvents() {
        return eventDao.listAll();
    }

    @Transactional
    @Override
    public void attendEvent(long userId, long eventId) {
        if(eventAttendanceDao.isAttending(userId, eventId)){
            LOGGER.debug("User {} is already attending event {}", userId, eventId);
            return;
        }
        int limit = eventDao.getEventAttendanceLimit(eventId);
        if(limit == 0){
            eventAttendanceDao.attend(userId, eventId);
            return;
        }
        if (eventAttendanceDao.getAttendeesCount(eventId) >= limit) {
            LOGGER.debug("Event attendance limit of {} reached", limit);
            return;
        }
        eventAttendanceDao.attend(userId, eventId);
    }

    @Transactional
    @Override
    public void attendEvent(String email, long eventId) {
        long userId = userService.findByEmail(email).orElseThrow().getId();
        attendEvent(userId, eventId);
    }

    @Transactional
    @Override
    public void cancelAttendance(long userId, long eventId) {
        eventAttendanceDao.cancel(userId, eventId);
    }

    @Transactional
    @Override
    public void cancelAttendance(String email, long eventId) {
        long userId = userService.findByEmail(email).orElseThrow().getId();
        cancelAttendance(userId, eventId);
    }

    @Transactional(readOnly = true)
    @Override
    public boolean isUserAttending(long userId, long eventId) {
        return eventAttendanceDao.isAttending(userId, eventId);
    }

    @Transactional(readOnly = true)
    @Override
    public boolean isUserAttending(String email, long eventId) {
        long userId = userService.findByEmail(email).orElseThrow().getId();
        return isUserAttending(userId, eventId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<User> getEventAttendees(long eventId) {
        return eventAttendanceDao.getAttendees(eventId);
    }

    @Transactional(readOnly = true)
    @Override
    public int getEventAttendeesCount(long eventId) {
        return eventAttendanceDao.getAttendeesCount(eventId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Event> getUserAttendingEvents(long userId) {
        return eventAttendanceDao.getAttendingEvents(userId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Event> getUserAttendingEvents(String userEmail) {
        long userId = userService.findByEmail(userEmail).orElseThrow().getId();
        return getUserAttendingEvents(userId);
    }

    @Transactional(readOnly = true)
    public List<EventResponse> getEventResponses(long eventId){
        return eventResponseDao.listAllFromEvent(eventId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Event> getRecommendedEvents(String email){
        return eventDao.getRecommendedEvents(email);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Event> getTopEvents(){
        LOGGER.debug("Getting top events");
        return eventDao.getTopEvents();
    }

    @Transactional(readOnly = true)
    @Override
    public CursorPage<EventResponse, LocalDateTime> getEventResponses(long eventId, LocalDateTime cursor, int limit) {
        return eventResponseDao.getEventsForUser(eventId, cursor, limit);
    }

    @Transactional(readOnly = true)
    @Override
    public CursorPage<Event, Long> getAllEvents(Long cursor, int limit) {
        return eventDao.listAll(cursor, limit);
    }

    @Transactional(readOnly = true)
    @Override
    public CursorPage<Event, Long> listByCity(City city, Long cursor, int limit) {
        return eventDao.listByCity(city, cursor, limit);
    }

    @Transactional(readOnly = true)
    @Override
    public CursorPage<User, Long> getEventAttendees(long eventId, Long cursor, int limit) {
        return eventAttendanceDao.getAttendees(eventId, cursor, limit);
    }

    @Transactional(readOnly = true)
    @Override
    public CursorPage<Event, Long> getUserAttendingEvents(long userId, Long cursor, int limit) {
        return eventAttendanceDao.getAttendingEvents(userId, cursor, limit);
    }

    @Transactional(readOnly = true)
    @Override
    public CursorPage<Event, Long> getUserAttendingEvents(String userEmail, Long cursor, int limit) {
        long userId = userService.findByEmail(userEmail).orElseThrow().getId();
        return getUserAttendingEvents(userId, cursor, limit);
    }

    @Transactional(readOnly=true)
    @Override
    public Boolean isEventOwnedByUser(String email, long eventID) {
        LOGGER.debug("Checking for event ownership of event {} by user {}", eventID, email);
        Optional<Event> event = eventDao.findById(eventID);
        return event.isPresent() && event.get().getUser().getEmail().equals(email);
    }
}
