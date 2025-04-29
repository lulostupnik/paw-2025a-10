package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.*;
import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.interfaces.services.EventService;
import ar.edu.itba.paw.interfaces.services.ImageService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
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
    private final ImageService imageService;
    private final CityDao cityDao;
    private final EventAttendanceDao eventAttendanceDao;
    private final UserDao userDao;

    @Autowired
    public EventServiceImpl(UserService userService,UserDao userDao, EventResponseDao eventResponseDao, EventDao eventDao, EmailService emailService, ImageDao imageDao, CityDao cityDao, EventAttendanceDao eventAttendanceDao, ImageService imageService) {
        this.userDao = userDao;
        this.userService = userService;
        this.eventResponseDao = eventResponseDao;
        this.eventDao = eventDao;
        this.emailService = emailService;
        this.imageDao = imageDao;
        this.cityDao = cityDao;
        this.eventAttendanceDao = eventAttendanceDao;
        this.imageService = imageService;
    }

    @Transactional
    @Override
    public Event createEvent(String email, String cityName, LocalDate date, byte[] flyer, String description, String title, LocalTime time, String address, Integer attendeesLimit) {
        LOGGER.debug("Creating event for user {}", email);

        LOGGER.debug("Looking for city {}", cityName);
        City city = cityDao.findByName(cityName).orElseThrow(() -> new RuntimeException("City not found"));

        LOGGER.debug("Looking for user {}", email);
        User user = userService.findByEmail(email).orElseThrow(()-> new RuntimeException("User not found"));

        LOGGER.debug("Saving event image");
        long flyerImageId = imageDao.saveImage(flyer);

        LOGGER.info("Event data is valid, commiting new event to persistance");
        Event event = eventDao.create(user, city, date, description, flyerImageId, title, time, address, attendeesLimit);

        // Automatically add the creator to the attendees list
        LOGGER.debug("Adding event creator to attendees list");
        eventAttendanceDao.attend(user.getId(), event.getId());

        return event;
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

//        emailService.answerEventMail(
//                event.getUser(),
//                message,
//                user,
//                event
//        );
//        //hago copia de la lista y un add.
//        LOGGER.info("Notifying all commenters in event about a new comment");

        emailService.answerEventNotification(
                userDao.listEventRespondersMinusUsers(eventId/*, new ArrayList<>(List.of(user.getId(), event.getUser().getId()))*/),
                message,
                user,
                event
                );
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "eventsById", key = "#id")
    @Override
    public Optional<Event> getEventById(long id){
        return eventDao.findById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Event> getAllEvents() {
        return eventDao.listAll();
    }

    @Transactional(readOnly = true)
    @Override
    public List<Event> getAllEvents(String email) {
        return eventDao.getEvents(email);
    }

    @Transactional
    @CacheEvict(value = "eventsById", key = "#eventId")
    @Override
    public void attendEvent(long userId, long eventId) {
        if(eventAttendanceDao.isAttending(userId, eventId)){
            LOGGER.debug("User {} is already attending event {}", userId, eventId);
            return;
        }
        Optional<Integer> limit = eventDao.getEventAttendanceLimit(eventId);
        if(limit.isEmpty()){
            eventAttendanceDao.attend(userId, eventId);
            return;
        }
        if (eventAttendanceDao.getAttendeesCount(eventId) >= limit.get()) {
            LOGGER.debug("Event attendance limit of {} reached", limit.get());
            return;
        }
        eventAttendanceDao.attend(userId, eventId);
    }

    @Transactional
    @CacheEvict(value = "eventsById", key = "#eventId") // todo: ver que onda esto
    @Override
    public void attendEvent(String email, long eventId) {
        long userId = userService.findByEmail(email).orElseThrow().getId();
        attendEvent(userId, eventId);
    }

    @Transactional
    @CacheEvict(value = "eventsById", key = "#eventId")
    @Override
    public void cancelAttendance(long userId, long eventId) {
        eventAttendanceDao.cancel(userId, eventId);
    }

    @Transactional
    @CacheEvict(value = "eventsById", key = "#eventId")
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
    @Override
    public List<EventResponse> getEventResponses(long eventId){
        return eventResponseDao.listAllFromEvent(eventId);
    }
//    //@TODO cache ?
//    @Transactional(readOnly = true)
//    @Override
//    public List<User> getEventResponders(long eventId){
//        return eventResponseDao.listAllUsersResponders(eventId);
//    }


    // FIXME: Agregarle cacheable?
    @Transactional(readOnly = true)
    @Override
    public List<UserEvent> getRecommendedEvents(String email){
        List<UserEvent> events = eventDao.getRecommendedEvents(email);
        if(events.isEmpty()){
            eventDao.getTopEvents().forEach((event)->{
                UserEvent ue = new UserEvent(event,false);
                events.add(ue);
            });
        }
        return events;
    }

    // FIXME: ¿Agregarle cacheable?
    @Transactional(readOnly = true)
    @Override
    public List<Event> getTopEvents(){
        LOGGER.debug("Getting top events");
        return eventDao.getTopEvents();
    }

    @Transactional(readOnly=true)
    @Override
    public Boolean isEventOwnedByUser(String email, long eventID) {
        LOGGER.debug("Checking for event ownership of event {} by user {}", eventID, email);
        Optional<Event> event = eventDao.findById(eventID);
        return event.isPresent() && event.get().getUser().getEmail().equals(email);
    }

    @Transactional(readOnly=true)
    @Override
    public boolean isEventFull(long eventId) {
        Optional<Integer> limit = eventDao.getEventAttendanceLimit(eventId);
        return limit.isPresent() && eventAttendanceDao.getAttendeesCount(eventId) >= limit.get();
    }

    @Transactional(readOnly = true)
    @Override
    public List<Event> getFullEvents() {
        return eventDao.getFullEvents();
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserEvent> getEventsWithAttendanceStatus(long userId) {

        return eventDao.getEventsWithAttendanceStatus(userId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserEvent> getEventsWithAttendanceStatus(String email) {
        long userId = userService.findByEmail(email).orElseThrow().getId();
        return getEventsWithAttendanceStatus(userId);
    }


    //@TODO checkear cache
    //@TODO CHECKEAR: hay unos argumentos que estan bien en null (atendeesLimit, description).  medio que no tiene sentido/poco claro.
    @Transactional
    @CacheEvict(value = "eventsById", key = "#eventId")
    @Override
    public void editEvent(long eventId,
                          String cityName,
                          LocalDate date,
                          Optional<byte[]> flyer,
                          String description,
                          String title,
                          LocalTime time,
                          String address,
                          Integer attendeesLimit) {

        // 1. Load the existing event
        Event currentEvent = eventDao.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));

        // 3. Resolve final values
        long resolvedCityId = cityDao.findByName(cityName).orElseThrow(() -> new RuntimeException("City not found")).getId();

       eventDao.updateData(
                resolvedCityId,
                date,
                description,
                title,
                time,
                address,
                attendeesLimit,
                eventId //hacer void
        );

        flyer.ifPresent(content -> {
            imageService.updateImage(currentEvent.getFlyerImageId(), content);
        });
    }




}
