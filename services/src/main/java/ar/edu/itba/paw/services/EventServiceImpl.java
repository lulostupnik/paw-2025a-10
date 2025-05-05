package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.*;
import ar.edu.itba.paw.interfaces.services.*;
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
    private final EventResponseService eventResponseService;
    private final EmailService emailService;
    private final EventDao eventDao;
    private final ImageService imageService;
    private final CityService cityService;
    private final EventAttendanceDao eventAttendanceDao;
    private final UserDao userDao;

    @Autowired
    public EventServiceImpl(UserService userService, EventResponseService eventResponseService,
                            EventDao eventDao, EmailService emailService, ImageService imageService,
                            CityService cityService, EventAttendanceDao eventAttendanceDao, UserDao userDao) {
        this.userDao = userDao;
        this.userService = userService;
        this.eventResponseService = eventResponseService;
        this.eventDao = eventDao;
        this.emailService = emailService;
        this.imageService = imageService;
        this.cityService = cityService;
        this.eventAttendanceDao = eventAttendanceDao;
    }

    @Transactional
    @Override
    public Event createEvent(String email, String cityName, LocalDate date, byte[] flyer, String description, String title, LocalTime time, String address, Integer attendeesLimit) {
        LOGGER.debug("Creating event for user {}", email);

        LOGGER.debug("Looking for city {}", cityName);
        City city = cityService.findByName(cityName).orElseThrow(() -> new RuntimeException("City not found"));

        LOGGER.debug("Looking for user {}", email);
        User user = userService.findByEmail(email).orElseThrow(()-> new RuntimeException("User not found"));

        LOGGER.debug("Saving event image");
        long flyerImageId = imageService.storeImage(flyer);

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

        LOGGER.debug("Looking for user {}", email);
        User user = userService.findByEmail(email).orElseThrow(()-> new RuntimeException("User not found"));

        LOGGER.info("Event reply is valid, commiting new reply to persistence");
        eventResponseService.create(user.getId(), user.getUsername(),eventId, message, LocalDateTime.now());

        LOGGER.info("Sending email notification for the event"); //@TODO mejorar

        emailService.answerEventNotification(
                userDao.listEventRespondersMinusUsers(eventId),
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
    public Page<Event> getAllEvents(int page, int size){
        return eventDao.listAll(page, size);
    }



    @Transactional(readOnly = true)
    @Override
    public Page<Event> getAllEventsSearch(String search,int page, int size) {
        LOGGER.debug("Getting all events with search {}", search);
        if (search == null || search.isEmpty()) {
            return eventDao.listAll(page, size);
        }
        return eventDao.searchEvents(search,page, size);
    }




    @Transactional(readOnly = true)
    @Override
    public List<Event> getAllEvents(String email) {
        return eventDao.getEvents(email);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<Event> getAllEvents(String email, int page, int size) {
        return eventDao.getEvents(email, page, size);
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
    public Page<User> getEventAttendees(long eventId, int page, int size) {
        return eventAttendanceDao.getAttendees(eventId, page, size);
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
    public Page<Event> getUserAttendingEvents(long userId, int page, int size) {
//        long userId = userService.findByEmail(userEmail).orElseThrow().getId();
        return eventAttendanceDao.getAttendingEvents(userId, page, size);
    }

    @Transactional(readOnly = true)
    @Override
    public List<EventResponse> getEventResponses(long eventId){
        return eventResponseService.listAllFromEvent(eventId);
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
    public List<UserEvent> getRecommendedEvents(String email, int limit) {
        if (limit <= 0) {
            throw new IllegalArgumentException("Limit must be greater than 0");
        }
        LOGGER.debug("Fetching recommended events for user email: {}, with limit: {}", email, limit);
        List<UserEvent> events = eventDao.getRecommendedEvents(email, 1, limit).getContent();
        if (events.isEmpty()) {
            LOGGER.debug("No recommended events found for user {}. Falling back to top events.", email);
            eventDao.getTopEvents(1, limit).getContent().forEach(event -> {
                LOGGER.debug("Adding top event fallback: {}", event.getTitle());
                UserEvent ue = new UserEvent(event, false);
                events.add(ue);
            });
        } else {
            LOGGER.debug("Found {} recommended events for user {}", events.size(), email);
        }

        return events;
    }


    // FIXME: ¿Agregarle cacheable?
    @Transactional(readOnly = true)
    @Override
    public List<Event> getTopEvents(int limit){
        LOGGER.debug("Getting top events");
        if (limit <= 0) {
            throw new IllegalArgumentException("Limit must be greater than 0");
        }
        return eventDao.getTopEvents(1, limit).getContent();
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
    public Page<UserEvent> getEventsPageWithAttendanceStatus(long userId, int page, int size) {

        return eventDao.getEventsWithAttendanceStatus(userId, page,size);
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
                          byte[] flyer,
                          String description,
                          String title,
                          LocalTime time,
                          String address,
                          Integer attendeesLimit) {

        // 1. Load the existing event
        Event currentEvent = eventDao.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));

        // 3. Resolve final values
        long resolvedCityId = cityService.findByName(cityName).orElseThrow(() -> new RuntimeException("City not found")).getId();

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

            imageService.updateImage(currentEvent.getFlyerImageId(), flyer);
    }





    @Transactional
    @Override
    public void delete(long id, String message) {
        LOGGER.debug("Deleting event {}", id);
        eventDao.deletionMessage(id, message);
        //emailService.deleteEmail()
        eventDao.delete(id);
    }

}
