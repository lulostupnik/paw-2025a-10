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
        City city = cityService.findCityByName(cityName).orElseThrow(() ->{
            LOGGER.error("City not found {}", cityName);
            return new RuntimeException("City not found");}
        );
        User user = userService.findUserByEmail(email).orElseThrow(()-> {
                LOGGER.error("User not found {}", email);
                return new RuntimeException("User not found");}
        );
        long flyerImageId = imageService.createImage(flyer);
        Event event = eventDao.create(user, city, date, description, flyerImageId, title, time, address, attendeesLimit); //fixme: reemplazar por new Event
        LOGGER.info("Event {} created", event.getId());
        eventAttendanceDao.create(user.getId(), event.getId());
//        eventDao.incrementAttendeesCount(event.getId()); @TODO esto? el modelo se crea con 1.
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
        User user = userService.findUserByEmail(email).orElseThrow(()->{
                LOGGER.error("User not found {}", email);
                return new RuntimeException("User not found");});
        event.getResponses().add(new EventResponse(user,event, message));
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
    public Optional<Event> findEventById(final long id){
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
            Optional<EventWithUserInfo> maybeEventWithUserInfo = findEventWithUserInfo(user.getId(), eventId);
            if(maybeEventWithUserInfo.isEmpty()){
                LOGGER.warn("Event not found {}", eventId);
                return Optional.empty();
            }

            event = maybeEventWithUserInfo.get().getEvent();
            isAttending = maybeEventWithUserInfo.get().isAttending();
            isCreator = maybeEventWithUserInfo.get().isCreator();
        }

       // int createdEventsCount = eventDao.countEventsCreatedByUser(event.getUser().getId());
//        int attendedEventsCount = eventDao.countEventsAttendedByUser(event.getUser().getId());

        int createdEventsCount = event.getUser().getEvents().size();
        int attendedEventsCount = event.getUser().getAttendedEvents().size(); //@todo check. esto tiene los suyos (supongo)
        //int attendedEventsCount = event.getUser().get

        Optional<CountryAttendeeCount> maybeCountryAttendeeCount = eventDao.findTopAttendeeCountry(event.getId());

        if(maybeCountryAttendeeCount.isPresent()){
            topAttendeeCountry = maybeCountryAttendeeCount.get().getCountryName();
            topAttendeeCountryCount = maybeCountryAttendeeCount.get().getCount();
        }

        return Optional.of(new EventWithStatistics(event, createdEventsCount, attendedEventsCount, topAttendeeCountry, topAttendeeCountryCount, isAttending, isCreator));
    }



    @Override
    public Page<Event> searchEvents(final String search, final PageParams pageParams) {
        LOGGER.debug("Getting all events with search {}", search);
        if (search == null || search.isEmpty()) {
            return eventDao.findAll(pageParams);
        }
        return eventDao.search(search, pageParams);
    }


    @Override
    public Page<Event> findEvents(final String email, final PageParams pageParams) {
        LOGGER.debug("Getting all events for user {}", email);
        return eventDao.findByUserEmail(email, pageParams);
    }


    @Override
    @Transactional
    public void createEventAttendance(final long userId, final  long eventId) {
        LOGGER.debug("User {} is attending event {}", userId, eventId);
        Event event = eventDao.findById(eventId).orElseThrow(() -> {
            LOGGER.warn("Event not found {}", eventId);
            return new RuntimeException("Event not found");
        });
        if(! event.getIsFuture()){
            LOGGER.info("Event (id {}) is not in the future", eventId);
            throw new InvalidException("Event (id " + eventId + ") is not in the future");
        }
        if(event.hasUserAttending(userId)){
            LOGGER.warn("User {} is already attending event {}", userId, eventId);
            return;
        }
        if (event.getAttendeesLimit() == null || event.getAttendeesCount() < event.getAttendeesLimit()) {
            User user = userService.findUserById(userId).orElseThrow(() -> {
                LOGGER.warn("User not found {}", userId);
                return new RuntimeException("User not found");
            });
            event.getAttendees().add( user ); // fixme: revisar logica del total de attending users
            event.setAttendeesCount(event.getAttendeesCount()+1); //fixme: ni idea
        }
        LOGGER.info("User {} is now attending event {}", userId, eventId);
    }


    @Override
    @Transactional
    public void createEventAttendance(final String email, final  long eventId) {
        long userId = userService.findUserByEmail(email).orElseThrow(
                () -> {
                    LOGGER.warn("User not found {}", email);
                    return new RuntimeException("User not found");
                }
        ).getId();
        createEventAttendance(userId, eventId);
        LOGGER.info("User {} is now attending event {}", userId, eventId);
    }

    @Override
    @Transactional
    public void deleteEventAttendance(final long userId, final  long eventId) {
        LOGGER.debug("User {} is canceling attendance for event {}", userId, eventId);
        Event event = eventDao.findById(eventId).orElseThrow(()->{
            LOGGER.warn("Event not found {}", eventId);
            return new RuntimeException("Event not found");
        });
        if(! event.getIsFuture()){
            LOGGER.info("Event (id {}) is not in the future", eventId);
            throw new InvalidException("Event (id " + eventId + ") is not in the future");
        }
        event.getAttendees().remove( //fixme: Cambiar por funcion en el modelo
                userService.findUserById(userId).orElseThrow(() -> {
                    LOGGER.warn("User not found {}", userId);
                    return new RuntimeException("User not found");
                })
        );

        LOGGER.info("User {} has canceled attendance for event {}", userId, eventId);
    }

    @Override
    @Transactional
    public void deleteEventAttendance(final String email, final  long eventId) {
        LOGGER.debug("User {} is canceling attendance for event {}", email, eventId);
        long userId = userService.findUserByEmail(email).orElseThrow().getId();
        deleteEventAttendance(userId, eventId);
        LOGGER.info("User {} has canceled attendance for event {}", userId, eventId);
    }

    @Override
    public boolean isEventAttendedByUser(final long userId, final  long eventId) {
        LOGGER.debug("Checking if user {} is attending event {}", userId, eventId);
        return eventAttendanceDao.exists(userId, eventId);
    }

    @Override
    public int countEventAttendees(final long eventId) {
       return findEventById(eventId)
                .orElseThrow(() -> {
                    LOGGER.warn("Event not found {}", eventId);
                    return new IllegalArgumentException("Event not found");
                }).getAttendeesCount();
    }

    @Override
    public Page<Event> findEventsByAttendee(final long userId, final PageParams pageParams) {
        return eventDao.findAllEventsByAttendee(userId, pageParams);
    }

    @Override
    public List<Event> findRecommendedEvents(final long userId, final  int limit) {
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
    public List<Event> findTopEvents(final int limit){
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
    public Page<Event> searchEventsWithFilters(final String search, final User user, final SortFieldEvent sortBy, final  SortDirection direction, final String destination, final LocalDate startDate, final LocalDate endDate, final String interest,
                                               final boolean isPast, final  boolean isUpcoming, final  boolean attending,
                                               final PageParams pageParams) {

        LOGGER.debug("Getting events with search {}, user {}, sortBy {}, direction {}, destination {}, startDate {}, endDate {}, interest {}, isPast {}, isUpcoming {}, attending {}",search,user,sortBy,direction,destination,startDate,endDate,interest,isPast,isUpcoming,attending);
        return eventDao.findAllWithFilters(user == null ? null : user.getId(), search, sortBy, direction, destination, startDate, endDate, interest,
                isPast, isUpcoming, attending, pageParams);
    }

    @Override
    @Transactional
    public void updateEvent(final long eventId, final String cityName, final  LocalDate date, final  byte[] flyer, final String description,
                            final String title, final LocalTime time, final String address, final Integer attendeesLimit) {
        LOGGER.debug("Editing event {}", eventId);
        Event currentEvent = eventDao.findById(eventId)
                .orElseThrow(() ->{
                    LOGGER.warn("Event not found {}", eventId);
                    return new IllegalArgumentException("Event not found");}
                );

        City resolvedCity = cityService.findCityByName(cityName).orElseThrow(() -> {
            LOGGER.warn("City not found {}", cityName);
            return new RuntimeException("City not found");}
        );

        long flyerImageId = currentEvent.getFlyerImageId();
        boolean changeImage = flyer != null && flyer.length > 0;

        if(changeImage){
            imageService.deleteImage(currentEvent.getFlyerImageId());
            LOGGER.info("Flyer image {} deleted", currentEvent.getFlyerImageId());
            currentEvent.setFlyerImageId(imageService.createImage(flyer));
        }
//
//        eventDao.update(resolvedCityId, date, description,
//                title, time, address, attendeesLimit, eventId, flyerImageId);

        currentEvent.setTitle(title);
        currentEvent.setDescription(description);
        currentEvent.setTime(time);
        currentEvent.setAddress(address);
        currentEvent.setAttendeesLimit(attendeesLimit);
        currentEvent.setEventCity(resolvedCity);
        currentEvent.setDate(date);
//        currentEvent.withTitle(title).  @todo preguntar.
//                withDescription(description).
//                withTime(time).
//                withAddress(address).
//                withAttendeesLimit(attendeesLimit).
//                withEventCity(resolvedCity).
//                withDate(date);
        //fixme @TODO podemos hacer un void userDao.upate(Event event) . o no
        LOGGER.info("Event {} updated", eventId);
    }

    @Override
    @Transactional
    public void deleteEvent(final long id, final String message) {
        LOGGER.debug("Deleting event {}", id);
        Event event = eventDao.findById(id).orElseThrow(() -> {
            LOGGER.warn("Event not found {}", id);
            return new RuntimeException("Event not found");});
        if(message != null && !message.isEmpty()){
            event.setDeletionMessage(message);
//            eventDao.updateDeletionMessage(id, message);
            emailService.sendEventDeletionNotification(event,message);
        }
        event.setResponses(List.of()); // creo que no hace falta
        event.setDeleted(true); //todo check
    }

    @Override
    @Transactional
    public void deleteEventResponse(final long id, final String message) {
        LOGGER.debug("Deleting event response {}", id);
        EventResponse deletedComment = findEventResponseById(id)
                .orElseThrow(() ->{
                    LOGGER.error("Event response not found {}", id);
                    return new IllegalArgumentException("Event response doesn't exist");});

        Event event = deletedComment.getEvent();

        User commentAuthor = deletedComment.getUser();
        emailService.sendEventCommentDeletionNotification(deletedComment,event,commentAuthor, message );
        LOGGER.info("Email notification sent for the event response {}", id);
        deletedComment.setDeletionMessage(message);
        LOGGER.info("Event response {} updated", id);
        deletedComment.setDeleted(true);
        LOGGER.info("Event response {} deleted", id);
    }

    @Override
    public int countEventResponses(final long eventId){
        LOGGER.debug("Getting response count for event {}", eventId);
        return eventDao.findById(eventId).orElseThrow(()->{
            LOGGER.warn("Event not found {}", eventId);
            return new RuntimeException("Event not found");}
        ).getResponses().size();
    }



    @Override
    public long findEventIdByResponseId(final long eventResponseId) { //fixme: mover esta búsqueda al eventDao
        LOGGER.debug("Getting event id by response id {}", eventResponseId);
        return eventResponseDao.findEventIdById(eventResponseId);
    }

    @Override
    public Page<EventResponse> findEventResponses(final long eventId, final PageParams pageParams) { //fixme: mover esta búsqueda al eventDao (o paginar aca)
        LOGGER.debug("Getting all responses for event {} with pageParams {}", eventId, pageParams);
        return eventResponseDao.listAllByEventId(eventId,pageParams);
    }

    @Override
    public Optional<EventResponse> findEventResponseById(final long id){ // fixme: mover esta búsqueda al eventDao
        LOGGER.debug("Getting event response by id {}", id);
        return eventResponseDao.findById(id);
    }
    @Transactional(readOnly = true)
    @Override
    public Optional<EventWithUserInfo> findEventWithUserInfo(long userId, long eventId) {
        Optional<Event> eventOpt = eventDao.findById(eventId);

        if (eventOpt.isEmpty()) {
            return Optional.empty();
        }

        Event event = eventOpt.get();
        if (event.isDeleted()) {
            return Optional.empty();
        }

        boolean isCreator = event.getUser().getId() == userId;
        boolean isAttending = event.getAttendees().stream()
                .anyMatch(u -> u.getId() == userId);

        return Optional.of(new EventWithUserInfo(event, isAttending, isCreator));
    }

    @Override
    public Optional<Integer> findAttendanceLimitById(final long eventId) {
        Event event = eventDao.findById(eventId).orElseThrow(() -> {
            LOGGER.error("Event not found {}", eventId);
            return new RuntimeException("Event not found");}
        );

        return Optional.ofNullable(event.getAttendeesLimit());

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
            emailService.sendEventReminderNotification(event, userService.findEventAttendees(event.getId()));
        }

        LOGGER.info("Completed scheduled task: sent reminder emails for upcoming events");

    }

}
