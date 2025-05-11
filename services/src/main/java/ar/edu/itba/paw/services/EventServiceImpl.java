package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.*;
import ar.edu.itba.paw.interfaces.services.*;
import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.interfaces.services.EventService;
import ar.edu.itba.paw.interfaces.services.ImageService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.enums.SortDirection;
import ar.edu.itba.paw.models.enums.SortFieldEvent;
import ar.edu.itba.paw.models.exceptions.InvalidException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventServiceImpl.class);
    private final EventResponseDao eventResponseDao;
    private final UserService userService;
    private final EmailService emailService;
    private final EventDao eventDao;
    private final ImageService imageService;
    private final CityService cityService;
    private final EventAttendanceDao eventAttendanceDao;
    private final UserDao userDao;

    @Autowired
    public EventServiceImpl(UserService userService, EventResponseDao eventResponseDao,
                            EventDao eventDao, EmailService emailService, ImageService imageService,
                            CityService cityService, EventAttendanceDao eventAttendanceDao, UserDao userDao) {
        this.userDao = userDao;
        this.userService = userService;
        this.eventResponseDao = eventResponseDao;
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

        long flyerImageId = imageService.storeImage(flyer);

        Event event = eventDao.create(user, city, date, description, flyerImageId, title, time, address, attendeesLimit);

        // Automatically add the creator to the attendees list
        eventAttendanceDao.create(user.getId(), event.getId());

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
        eventResponseDao.create(user.getId(), user.getUsername(), eventId, message, LocalDateTime.now());
        LOGGER.info("Sending email notification for the event {}", eventId);

        emailService.answerEventNotification(
                userDao.findAllEventResponders(eventId),
                message,
                user,
                event
                );
    }

    @Override
    public Optional<Event> getEventById(long id){
        return eventDao.findById(id);
    }


    @Override
    public Optional<EventWithStatistics> findEventWithStatistics(User user, long eventId) {
        if(user == null){
            return eventDao.findEventWithStatistics(null, eventId);
        }
        return eventDao.findEventWithStatistics(user.getId(), eventId);
    }



    @Override
    public Page<Event> getAllEventsSearch(String search,PageParams pageParams) {
        LOGGER.debug("Getting all events with search {}", search);
        if (search == null || search.isEmpty()) {
            return eventDao.findAll(pageParams);
        }
        return eventDao.search(search, pageParams);
    }


    @Override
    public Page<Event> getAllEvents(String email, PageParams pageParams) {
        return eventDao.findByUserEmail(email, pageParams);
    }



    @Transactional
    @Override
    public void attendEvent(long userId, long eventId) {
        futureEvent(eventId);
        if(eventAttendanceDao.exists(userId, eventId)){
            LOGGER.debug("User {} is already attending event {}", userId, eventId);
            return;
        }
        Optional<Integer> limit = eventDao.findAttendanceLimitById(eventId);
        if(limit.isEmpty()){
            eventAttendanceDao.create(userId, eventId);
            return;
        }
        if (eventAttendanceDao.countByEventId(eventId) >= limit.get()) {
            LOGGER.debug("Event attendance limit of {} reached", limit.get());
            return;
        }
        eventAttendanceDao.create(userId, eventId);
    }

    private void futureEvent(long eventId){
        Event event = eventDao.findById(eventId).orElseThrow(() ->
                new IllegalArgumentException("Event not found with id: " + eventId));

        if (!event.getIsFuture()) {
            throw new InvalidException("Event (id " + eventId + ") is not in the future");
        }
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
        futureEvent(eventId);
        eventAttendanceDao.delete(userId, eventId);
    }

    @Transactional
    @Override
    public void cancelAttendance(String email, long eventId) {
        long userId = userService.findByEmail(email).orElseThrow().getId();
        cancelAttendance(userId, eventId);
    }

    @Override
    public boolean isUserAttending(long userId, long eventId) {
        return eventAttendanceDao.exists(userId, eventId);
    }

    @Override
    public List<User> getEventAttendees(long eventId) {
        return eventAttendanceDao.findAllAttendeesByEventId(eventId);
    }

    @Override
    public Page<User> getEventAttendees(long eventId, PageParams pageParams) {
        return eventAttendanceDao.findAllAttendeesByEventId(eventId, pageParams);
    }
    @Override
    public int getEventAttendeesCount(long eventId) {
        return eventAttendanceDao.countByEventId(eventId);
    }

    @Override
    public Page<Event> getUserAttendingEvents(long userId, PageParams pageParams) {
        return eventAttendanceDao.findAllEventsByAttendee(userId, pageParams);
    }

    @Override
    public List<Event> getRecommendedEvents(long userId, int limit) {
        if (limit <= 0) {
            throw new IllegalArgumentException("Limit must be greater than 0");
        }
        LOGGER.debug("Fetching recommended events for user id: {}, with limit: {}", userId, limit);
        List<Event> events = eventDao.findRecommended(userId, new PageParams(1, limit)).getContent();
        if (events.isEmpty()) {
            LOGGER.debug("No recommended events found for user {}. Falling back to top events.", userId);
            events = eventDao.findTopByUser(userId,new PageParams(1, limit)).getContent();
        } else {
            LOGGER.debug("Found {} recommended events for user {}", events.size(), userId);
        }

        return events;
    }

    @Override
    public List<Event> getTopEvents(int limit){
        LOGGER.debug("Getting top events");
        if (limit <= 0) {
            throw new IllegalArgumentException("Limit must be greater than 0");
        }
        return eventDao.findTop(new PageParams(1, limit)).getContent();
    }

    @Override
    public boolean isEventOwnedByUser(String email, long eventID) {
        LOGGER.debug("Checking for event ownership of event {} by user {}", eventID, email);
        Optional<Event> event = eventDao.findById(eventID);
        return event.isPresent() && event.get().getUser().getEmail().equals(email);
    }

    @Override
    public Page<Event> getEventsPage(String search, User user, SortFieldEvent sortBy, SortDirection direction, String destination, LocalDate startDate, LocalDate endDate, String interest,
                                     boolean isPast, boolean isUpcoming, boolean attending,
                                     PageParams pageParams) {

        return eventDao.findAllWithFilters(user == null ? null : user.getId(), search, sortBy, direction, destination, startDate, endDate, interest,
                isPast, isUpcoming, attending, pageParams);
    }


    @Transactional
    @Override
    public void editEvent(long eventId, String cityName, LocalDate date, byte[] flyer, String description,
                          String title, LocalTime time, String address, Integer attendeesLimit) {

        Event currentEvent = eventDao.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));

        long resolvedCityId = cityService.findByName(cityName).orElseThrow(() -> new RuntimeException("City not found")).getId();

        long flyerImageId = currentEvent.getFlyerImageId();
        if(flyer != null && flyer.length > 0) {
            flyerImageId = imageService.storeImage(flyer);
            imageService.deleteImage(currentEvent.getFlyerImageId());
        }
        // hacer void ?
        eventDao.update(resolvedCityId, date, description,
                title, time, address, attendeesLimit, eventId, flyerImageId
       );


    }


    @Transactional
    @Override
    public void delete(long id, String message) {
        LOGGER.debug("Deleting event {}", id);
        Event event = eventDao.findById(id).orElseThrow(() -> new RuntimeException("Event not found"));
        if(message != null && !message.isEmpty()){
            eventDao.updateDeletionMessage(id, message);
            emailService.sendEventDeletionNotification(event,message);
        }
        eventResponseDao.deleteAllByEventId(id);
        eventDao.delete(id);
    }


    @Transactional
    @Override
    public void deleteResponse(long id, String message) {

        EventResponse deletedComment = findEventResponseById(id)
                .orElseThrow(() ->
                    new IllegalArgumentException("Event response doesn't exist"));

        Event event = eventDao.findById(deletedComment.getEventId())
                .orElseThrow(() ->  new IllegalStateException("Event from event response doesn't exist"));


        User commentAuthor = userService.findById(deletedComment.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User from event response doesn't exist"));

        emailService.sendEventCommentDeletionNotification(deletedComment,event,commentAuthor, message );

        eventResponseDao.updateDeletionMessage(id, message);
        eventResponseDao.delete(id);
    }

    @Override
    public int getResponseCount(long eventId){
        return eventResponseDao.countByEventId(eventId);
    }



    @Override
    public long getEventIdByResponseId(long eventResponseId) {
        return eventResponseDao.findEventIdById(eventResponseId);
    }

    @Override
    public Page<EventResponse> listAllResponseFromEvent(long eventId, PageParams pageParams) {
        return eventResponseDao.listAllByEventId(eventId,pageParams);
    }

    @Override
    public Optional<EventResponse> findEventResponseById(long id){
        return eventResponseDao.findById(id);
    }



    @Scheduled(cron = "0 0 12 * * ?")
    @Transactional(readOnly = true)
    public void sendEventReminders(){
        LOGGER.info("Starting scheduled task: sending reminder emails for upcoming events");

        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);

        List<Event> upcomingEvents = eventDao.findAllBetweenDates(today, tomorrow);

        LOGGER.info("Found {} events occurring in the next 24 hours", upcomingEvents.size());

        for (Event event : upcomingEvents) {
            emailService.sendEventReminderNotification(event, eventAttendanceDao.findAllAttendeesByEventId(event.getId()));
        }

        LOGGER.info("Completed scheduled task: sent reminder emails for upcoming events");

    }
}
