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
import java.util.List;
import java.util.Optional;


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
    public EventServiceImpl(final UserService userService,final  EventResponseDao eventResponseDao,
                            final EventDao eventDao,final EmailService emailService, final ImageService imageService,
                            final CityService cityService,final  EventAttendanceDao eventAttendanceDao,final  UserDao userDao) {
        this.userDao = userDao;
        this.userService = userService;
        this.eventResponseDao = eventResponseDao;
        this.eventDao = eventDao;
        this.emailService = emailService;
        this.imageService = imageService;
        this.cityService = cityService;
        this.eventAttendanceDao = eventAttendanceDao;
    }

    @Override
    @Transactional
    public Event createEvent(final String email, final  String cityName, final LocalDate date, final byte[] flyer, final  String description, final  String title, final LocalTime time, final String address, final  Integer attendeesLimit) {

        LOGGER.debug("Creating event for user {}", email);
        City city = cityService.findByName(cityName).orElseThrow(() ->{
            LOGGER.error("City not found {}", cityName);
            return new RuntimeException("City not found");}
        );
        User user = userService.findByEmail(email).orElseThrow(()-> {
                LOGGER.error("User not found {}", email);
                return new RuntimeException("User not found");}
        );
        long flyerImageId = imageService.storeImage(flyer);
        Event event = eventDao.create(user, city, date, description, flyerImageId, title, time, address, attendeesLimit);
        LOGGER.info("Event {} created", event.getId());
        eventAttendanceDao.create(user.getId(), event.getId());
        eventDao.incrementAttendeesCount(event.getId());
        return event;
    }

    @Override
    @Transactional
    public void replyToEvent(final String email, final long eventId, final String message) {
        LOGGER.debug("Replying to event {}", eventId);
        Event event = eventDao.findById(eventId).orElseThrow(() -> {
            LOGGER.error("Event not found {}", eventId);
            return new RuntimeException("Event not found");}
        );
        User user = userService.findByEmail(email).orElseThrow(()->{
                LOGGER.error("User not found {}", email);
                return new RuntimeException("User not found");});
        eventResponseDao.create(user.getId(), user.getUsername(), eventId, message, LocalDateTime.now());
        LOGGER.info("Event response {} created", eventId);
        emailService.answerEventNotification(
                userDao.findAllEventResponders(eventId),
                message,
                user,
                event
                );
        LOGGER.info("Email notification sent for the event {}", eventId);
    }

    @Override
    public Optional<Event> getEventById(final long id){
        LOGGER.debug("Getting event by id {}", id);
        return eventDao.findById(id);
    }


    @Override
    public Optional<EventWithStatistics> findEventWithStatistics(final User user, final long eventId) {
        LOGGER.debug("Getting event with statistics by id {}", eventId);

        String topAttendeeCountry = null;
        int topAttendeeCountryCount = 0;
        boolean isAttending = false;
        boolean isCreator = false;
        Event event;

        if(user == null){
            Optional<Event> maybeEvent = eventDao.findById(eventId);
            if(maybeEvent.isEmpty()){
                LOGGER.warn("Event not found {}", eventId);
                return Optional.empty();
            }
            event = maybeEvent.get();
        } else {
            Optional<EventWithUserInfo> maybeEventWithUserInfo = eventDao.findEventWithUserInfo(user.getId(), eventId);
            if(maybeEventWithUserInfo.isEmpty()){
                LOGGER.warn("Event not found {}", eventId);
                return Optional.empty();
            }

            event = maybeEventWithUserInfo.get().getEvent();
            isAttending = maybeEventWithUserInfo.get().isAttending();
            isCreator = maybeEventWithUserInfo.get().isCreator();
        }

        int createdEventsCount = eventDao.countEventsCreatedByUser(event.getUser().getId());
        int attendedEventsCount = eventDao.countEventsAttendedByUser(event.getUser().getId());
        Optional<CountryAttendeeCount> maybeCountryAttendeeCount = eventDao.findTopAttendeeCountry(event.getId());

        if(maybeCountryAttendeeCount.isPresent()){
            topAttendeeCountry = maybeCountryAttendeeCount.get().getCountryName();
            topAttendeeCountryCount = maybeCountryAttendeeCount.get().getCount();
        }

        return Optional.of(new EventWithStatistics(event, createdEventsCount, attendedEventsCount, topAttendeeCountry, topAttendeeCountryCount, isAttending, isCreator));
    }



    @Override
    public Page<Event> getAllEventsSearch(final String search,final PageParams pageParams) {
        LOGGER.debug("Getting all events with search {}", search);
        if (search == null || search.isEmpty()) {
            return eventDao.findAll(pageParams);
        }
        return eventDao.search(search, pageParams);
    }


    @Override
    public Page<Event> getAllEvents(final String email,final PageParams pageParams) {
        LOGGER.debug("Getting all events for user {}", email);
        return eventDao.findByUserEmail(email, pageParams);
    }


    @Override
    @Transactional
    public void attendEvent(final long userId,final  long eventId) {
        LOGGER.debug("User {} is attending event {}", userId, eventId);
        futureEvent(eventId);
        if(eventAttendanceDao.exists(userId, eventId)){
            LOGGER.warn("User {} is already attending event {}", userId, eventId);
            return;
        }
        Optional<Integer> limit = eventDao.findAttendanceLimitById(eventId);
        if(limit.isEmpty()){
            eventAttendanceDao.create(userId, eventId);
            return;
        }
        if (eventAttendanceDao.countByEventId(eventId) >= limit.get()) {
            LOGGER.warn("Event attendance limit of {} reached", limit.get());
            return;
        }
        eventAttendanceDao.create(userId, eventId);
        eventDao.incrementAttendeesCount(eventId);
        LOGGER.info("User {} is now attending event {}", userId, eventId);
    }

    private void futureEvent(final long eventId){
        Event event = eventDao.findById(eventId).orElseThrow(() ->{
                LOGGER.info("Event not found with id: {}", eventId);
                return new IllegalArgumentException("Event not found with id: " + eventId);});

        if (!event.getIsFuture()) {
            LOGGER.info("Event (id {}) is not in the future", eventId);
            throw new InvalidException("Event (id " + eventId + ") is not in the future");
        }
    }

    @Override
    @Transactional
    public void attendEvent(final String email,final  long eventId) {
        long userId = userService.findByEmail(email).orElseThrow(
                () -> {
                    LOGGER.warn("User not found {}", email);
                    return new RuntimeException("User not found");
                }
        ).getId();
        attendEvent(userId, eventId);
        LOGGER.info("User {} is now attending event {}", userId, eventId);
    }

    @Override
    @Transactional
    public void cancelAttendance(final long userId,final  long eventId) {
        LOGGER.debug("User {} is canceling attendance for event {}", userId, eventId);
        futureEvent(eventId);
        eventAttendanceDao.delete(userId, eventId);
        LOGGER.info("User {} has canceled attendance for event {}", userId, eventId);
    }

    @Override
    @Transactional
    public void cancelAttendance(final String email,final  long eventId) {
        LOGGER.debug("User {} is canceling attendance for event {}", email, eventId);
        long userId = userService.findByEmail(email).orElseThrow().getId();
        cancelAttendance(userId, eventId);
        LOGGER.info("User {} has canceled attendance for event {}", userId, eventId);
    }

    @Override
    public boolean isUserAttending(final long userId,final  long eventId) {
        LOGGER.debug("Checking if user {} is attending event {}", userId, eventId);
        return eventAttendanceDao.exists(userId, eventId);
    }

    @Override
    public int getEventAttendeesCount(final long eventId) {
        LOGGER.debug("Getting attendees count for event {}", eventId);
        return eventAttendanceDao.countByEventId(eventId);
    }

    @Override
    public Page<Event> getUserAttendingEvents(final long userId, final PageParams pageParams) {
        return eventDao.findAllEventsByAttendee(userId, pageParams);
    }

    @Override
    public List<Event> getRecommendedEvents(final long userId, final  int limit) {
        LOGGER.debug("Getting recommended events for user {} with limit {}", userId, limit);
        if (limit <= 0) {
            LOGGER.warn("Limit must be greater than 0");
            throw new IllegalArgumentException("Limit must be greater than 0");
        }
        List<Event> events = eventDao.findRecommended(userId, new PageParams(1, limit)).getContent();
        if (events.isEmpty()) {
            LOGGER.warn("No recommended events found for user {}. Falling back to top events.", userId);
            events = eventDao.findTopByUser(userId,new PageParams(1, limit)).getContent();
        }
        return events;
    }

    @Override
    public List<Event> getTopEvents(final int limit){
        LOGGER.debug("Getting top events");
        if (limit <= 0) {
            LOGGER.warn("Limit must be greater than 0");
            throw new IllegalArgumentException("Limit must be greater than 0");
        }
        return eventDao.findTop(new PageParams(1, limit)).getContent();
    }

    @Override
    public boolean isEventOwnedByUser(final String email, final long eventID) {
        LOGGER.debug("Checking for event ownership of event {} by user {}", eventID, email);
        Optional<Event> event = eventDao.findById(eventID);
        return event.isPresent() && event.get().getUser().getEmail().equals(email);
    }

    @Override
    public Page<Event> getEventsPage(final String search, final User user, final SortFieldEvent sortBy,final  SortDirection direction, final String destination, final LocalDate startDate, final LocalDate endDate, final String interest,
                                     final boolean isPast,final  boolean isUpcoming,final  boolean attending,
                                     final PageParams pageParams) {

        LOGGER.debug("Getting events with search {}, user {}, sortBy {}, direction {}, destination {}, startDate {}, endDate {}, interest {}, isPast {}, isUpcoming {}, attending {}",search,user,sortBy,direction,destination,startDate,endDate,interest,isPast,isUpcoming,attending);
        return eventDao.findAllWithFilters(user == null ? null : user.getId(), search, sortBy, direction, destination, startDate, endDate, interest,
                isPast, isUpcoming, attending, pageParams);
    }

    @Override
    @Transactional
    public void editEvent(final long eventId, final String cityName,final  LocalDate date,final  byte[] flyer, final String description,
                          final String title, final LocalTime time, final String address, final Integer attendeesLimit) {
        LOGGER.debug("Editing event {}", eventId);
        Event currentEvent = eventDao.findById(eventId)
                .orElseThrow(() ->{
                    LOGGER.warn("Event not found {}", eventId);
                    return new IllegalArgumentException("Event not found");}
                );

        long resolvedCityId = cityService.findByName(cityName).orElseThrow(() -> {
            LOGGER.warn("City not found {}", cityName);
            return new RuntimeException("City not found");}
        ).getId();

        long flyerImageId = currentEvent.getFlyerImageId();
        boolean changeImage = flyer != null && flyer.length > 0;
        if(changeImage){
            flyerImageId = imageService.storeImage(flyer);
        }

        eventDao.update(resolvedCityId, date, description,
                title, time, address, attendeesLimit, eventId, flyerImageId);

        if(changeImage) {
            imageService.deleteImage(currentEvent.getFlyerImageId());
            LOGGER.info("Flyer image {} deleted", currentEvent.getFlyerImageId());
        }
        // hacer void ?

        LOGGER.info("Event {} updated", eventId);
    }

    @Override
    @Transactional
    public void delete(final long id, final String message) {
        LOGGER.debug("Deleting event {}", id);
        Event event = eventDao.findById(id).orElseThrow(() -> {
            LOGGER.warn("Event not found {}", id);
            return new RuntimeException("Event not found");});
        if(message != null && !message.isEmpty()){
            eventDao.updateDeletionMessage(id, message);
            emailService.sendEventDeletionNotification(event,message);
        }
        eventResponseDao.deleteAllByEventId(id);
        eventDao.delete(id);
    }

    @Override
    @Transactional
    public void deleteResponse(final long id, final String message) {
        LOGGER.debug("Deleting event response {}", id);
        EventResponse deletedComment = findEventResponseById(id)
                .orElseThrow(() ->{
                    LOGGER.error("Event response not found {}", id);
                    return new IllegalArgumentException("Event response doesn't exist");});

        Event event = eventDao.findById(deletedComment.getEventId())
                .orElseThrow(() ->  {
                    LOGGER.error("Event from event response not found {}", deletedComment.getEventId());
                    return new IllegalStateException("Event from event response doesn't exist");});


        User commentAuthor = userService.findById(deletedComment.getUserId())
                .orElseThrow(() -> {
                    LOGGER.error("User from event response not found {}", deletedComment.getUserId());
                    return new IllegalArgumentException("User from event response doesn't exist");}
                );

        emailService.sendEventCommentDeletionNotification(deletedComment,event,commentAuthor, message );
        LOGGER.info("Email notification sent for the event response {}", id);
        eventResponseDao.updateDeletionMessage(id, message);
        LOGGER.info("Event response {} updated", id);
        eventResponseDao.delete(id);
        LOGGER.info("Event response {} deleted", id);
    }

    @Override
    public int getResponseCount(final long eventId){
        LOGGER.debug("Getting response count for event {}", eventId);
        return eventResponseDao.countByEventId(eventId);
    }



    @Override
    public long getEventIdByResponseId(final long eventResponseId) {
        LOGGER.debug("Getting event id by response id {}", eventResponseId);
        return eventResponseDao.findEventIdById(eventResponseId);
    }

    @Override
    public Page<EventResponse> listAllResponseFromEvent(final long eventId, final PageParams pageParams) {
        LOGGER.debug("Getting all responses for event {} with pageParams {}", eventId, pageParams);
        return eventResponseDao.listAllByEventId(eventId,pageParams);
    }

    @Override
    public Optional<EventResponse> findEventResponseById(final long id){
        LOGGER.debug("Getting event response by id {}", id);
        return eventResponseDao.findById(id);
    }


    @Override
    @Scheduled(cron = "0 0 12 * * ?")
    @Transactional(readOnly = true)
    public void sendEventReminders(){
        LOGGER.info("Starting scheduled task: sending reminder emails for upcoming events");
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);

        List<Event> upcomingEvents = eventDao.findAllBetweenDates(today, tomorrow);

        LOGGER.info("Found {} events occurring in the next 24 hours", upcomingEvents.size());

        for (Event event : upcomingEvents) {
            emailService.sendEventReminderNotification(event, userService.getEventAttendees(event.getId()));
        }

        LOGGER.info("Completed scheduled task: sent reminder emails for upcoming events");

    }
}
