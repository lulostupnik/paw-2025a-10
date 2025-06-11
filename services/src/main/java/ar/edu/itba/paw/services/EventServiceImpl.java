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
import ar.edu.itba.paw.models.exceptions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
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
    private final EventRatingDao eventRatingDao;

    @Autowired
    public EventServiceImpl(final UserService userService,final  EventResponseDao eventResponseDao,
                            final EventDao eventDao,final EmailService emailService, final ImageService imageService,
                            final CityService cityService,final  EventAttendanceDao eventAttendanceDao,
                            final EventRatingDao eventRatingDao) {
        this.userService = userService;
        this.eventResponseDao = eventResponseDao;
        this.eventDao = eventDao;
        this.emailService = emailService;
        this.imageService = imageService;
        this.cityService = cityService;
        this.eventAttendanceDao = eventAttendanceDao;
        this.eventRatingDao = eventRatingDao;
    }

    @Override
    @Transactional
    public Event createEvent(final String email, final  String cityName, final LocalDate date, final byte[] flyer, final  String description, final  String title, final LocalTime time, final String address, final  Integer attendeesLimit) {

        LOGGER.debug("Creating event for user {}", email);
        City city = cityService.findCityByName(cityName).orElseThrow(() ->{
            LOGGER.error("City not found {}", cityName);
            return new CityNotFoundException("City not found");}
        );
        User user = userService.findUserByEmail(email).orElseThrow(()-> {
                LOGGER.error("User not found {}", email);
                return new UserNotFoundException("User not found");}
        );
        long flyerImageId = imageService.createImage(flyer);
        Event event = eventDao.create(user, city, date, description, flyerImageId, title, time, address, attendeesLimit); //fixme: reemplazar por new Event
        LOGGER.info("Event {} created", event.getId());
        eventAttendanceDao.create(user, event);
        return event;
    }

    // todo: check
    @Override
    @Transactional
    public void replyToEvent(final String email, final long eventId, final String message) {
        LOGGER.debug("Replying to event {}", eventId);
        Event event = eventDao.findById(eventId).orElseThrow(() -> {
            LOGGER.error("Event not found {}", eventId);
            return new EventNotFoundException("Event not found");
        });

        User responder = userService.findUserByEmail(email).orElseThrow(() -> {
            LOGGER.error("User not found {}", email);
            return new UserNotFoundException("User not found");
        });

        eventResponseDao.create(responder, event, message);
        LOGGER.info("Event response {} created", eventId);

        int page = 1;
        int pageSize = 50;
        Page<User> respondersPage;

        EmailUser emailResponder = new EmailUser(responder);
        EmailEvent emailEvent = new EmailEvent(event);

        do {
            respondersPage = eventResponseDao.findRespondersByEventId(
                    eventId,
                    new PageParams(page, pageSize)
            );

           
            List<EmailUser> responders = respondersPage.getContent().stream()
                    .map(EmailUser::new)
                    .toList();


            if (!responders.isEmpty()) {
                emailService.answerEventNotification(
                        responders,
                        message,
                        emailResponder,
                        emailEvent
                );
            }

            page++;
        } while (page <= respondersPage.getTotalPages());

        LOGGER.info("Email notifications sent to all responders for event {}", eventId);

        emailService.answerEventOwnerNotification(message, emailResponder, emailEvent);
        LOGGER.info("Email notifications sent to event owner for event {}", eventId);

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

        int createdEventsCount = eventDao.countEventsCreatedByUser(event.getUser().getId());
        int attendedEventsCount = eventAttendanceDao.countEventsAttendedByUser(event.getUser().getId());



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
            return new EventNotFoundException("Event not found");
        });
        User user = userService.findUserById(userId).orElseThrow(() -> {
            LOGGER.warn("User not found {}", userId);
            return new UserNotFoundException("User not found");
        });
        if(! event.getIsFuture()){
            LOGGER.info("Event (id {}) is not in the future", eventId);
            throw new InvalidException("Event (id " + eventId + ") is not in the future");
        }
        if (eventAttendanceDao.exists(user, event)){
            LOGGER.info("User {} is already attending event (id {})", userId, eventId);
            throw new InvalidException("User " + userId +" is already attending event (id "+ eventId+")");
        }

        if (event.getAttendeesLimit() == null || event.getAttendeesCount() < event.getAttendeesLimit()) {
            eventAttendanceDao.create(user, event);
            //fixme: crear el attendance aca de verdad
            event.setAttendeesCount(event.getAttendeesCount()+1); //fixme: ni idea
            LOGGER.info("User {} is now attending event {}", userId, eventId);
            return;
        }
        LOGGER.warn("User {} trying to attend a full event ({})", userId, eventId);
        throw new InvalidException("Event (id " + eventId + ") is already full");

    }


    @Override
    @Transactional
    public void createEventAttendance(final String email, final  long eventId) {
        long userId = userService.findUserByEmail(email).orElseThrow(
                () -> {
                    LOGGER.warn("User not found {}", email);
                    return new UserNotFoundException("User not found");
                }
        ).getId();
        createEventAttendance(userId, eventId);
    }

    @Override
    @Transactional
    public void deleteEventAttendance(final long userId, final  long eventId) {
        LOGGER.debug("User {} is canceling attendance for event {}", userId, eventId);
        Event event = eventDao.findById(eventId).orElseThrow(()->{
            LOGGER.warn("Event not found {}", eventId);
            return new EventNotFoundException("Event not found");
        });
        if(! event.getIsFuture()){
            LOGGER.info("Event (id {}) is not in the future", eventId);
            throw new InvalidException("Event (id " + eventId + ") is not in the future");
        }
        eventAttendanceDao.delete(userId, eventId);
        LOGGER.info("User {} has canceled attendance for event {}", userId, eventId);
    }

    @Override
    @Transactional
    public void deleteEventAttendance(final String email, final  long eventId) {
        LOGGER.debug("User {} is canceling attendance for event {}", email, eventId);
        long userId = userService.findUserByEmail(email).orElseThrow().getId();
        deleteEventAttendance(userId, eventId);
    }

    @Override
    @Transactional
    public void rateEvent(User user, long eventId, double rating) {
        Event event = eventDao.findById(eventId).orElseThrow(() -> {
            LOGGER.warn("Event not found {}", eventId);
            return new EventNotFoundException("Event not found");
        });
        eventRatingDao.rateEvent(user, event, rating);
    }

    @Transactional
    @Override
    public void updateEventRating(User user, long eventId, double value) {
        Rating rating = eventRatingDao.findRatingByUserAndEvent(user.getId(), eventId)
                .orElseThrow(() -> {
                    LOGGER.warn("Rating not found for user {} and event {}", user.getId(), eventId);
                    return new RatingNotFoundException("Rating not found");
                });
        rating.setRating(value);
    }

    @Override
    public Optional<Rating> findRatingByUserAndEvent(long userId, long eventId) {
        return eventRatingDao.findRatingByUserAndEvent(userId, eventId);
    }

    @Override
    public int countRatingsByEvent(long eventId) {
        return eventRatingDao.countRatingsByEvent(eventId);
    }

    @Override
    public Optional<Double> findRatingsAverageByEvent(long eventId) {
        return eventRatingDao.findRatingsAverageByEvent(eventId);
    }


    @Override
    public int countEventAttendees(final long eventId) {
       return findEventById(eventId)
                .orElseThrow(() -> {
                    LOGGER.warn("Event not found {}", eventId);
                    return new EventNotFoundException("Event not found");
                }).getAttendeesCount();
    }

    @Override
    public Page<Event> findEventsByAttendee(final long userId, final PageParams pageParams) {
        return eventDao.findAllEventsByAttendee(userId, pageParams);
    }

    @Override
    public Page<Event> findUpcomingEventsByAttendee(long userId, PageParams pageParams) {
        return eventDao.findAllWithFilters(
                userId,
                null, // searchTerm
                null, // sortBy
                SortDirection.DESC, // direction
                null, // destination
                LocalDate.now(), // startDate
                null, // endDate
                null, // interest
                true, // attending
                false, // isCreator
                pageParams
        );
    }

    @Override
    public Page<Event> findFinishedEventsByAttendee(long userId, PageParams pageParams) {
        return  eventDao.findAllWithFilters(
                userId,
                null, // searchTerm
                null, // sortBy
                SortDirection.DESC, // direction
                null, // destination
                null, // startDate
                LocalDate.now().minusDays(1), // endDate
                null, // interest
                true, // attending
                false, // isCreator
                pageParams
        );
    }

    @Override
    public List<Event> findRecommendedEvents(final long userId, final  int limit) {
        LOGGER.debug("Getting recommended events for user {} with limit {}", userId, limit);
        if (limit <= 0) {
            LOGGER.warn("Limit must be greater than 0");
            throw new InvalidPaginationParamsException("Limit must be greater than 0");
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
            throw new InvalidPaginationParamsException("Limit must be greater than 0");
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

        LocalDate adjustedStartDate = startDate;
        LocalDate adjustedEndDate = endDate;

        if (isUpcoming) {
            adjustedStartDate = ensureStartDateForUpcomingEvents(startDate);
        }
        if (isPast) {
            adjustedEndDate = capEndDateForPastEvents(endDate);
        }

        return eventDao.findAllWithFilters(
                user == null ? null : user.getId(),
                search,
                sortBy,
                direction,
                destination,
                adjustedStartDate,
                adjustedEndDate,
                interest,
                attending,
                false,
                pageParams
        );

    }

    @Override
    @Transactional
    public void updateEvent(final long eventId, final String cityName, final  LocalDate date, final  byte[] flyer, final String description,
                            final String title, final LocalTime time, final String address, final Integer attendeesLimit) {
        LOGGER.debug("Editing event {}", eventId);
        Event currentEvent = eventDao.findById(eventId)
                .orElseThrow(() ->{
                    LOGGER.warn("Event not found {}", eventId);
                    return new EventNotFoundException("Event not found");}
                );

        City resolvedCity = cityService.findCityByName(cityName).orElseThrow(() -> {
            LOGGER.warn("City not found {}", cityName);
            return new CityNotFoundException("City not found");}
        );

        long flyerImageId = currentEvent.getFlyerImageId();
        boolean changeImage = flyer != null && flyer.length > 0;

        if(changeImage){
            imageService.deleteImage(flyerImageId);
            LOGGER.info("Flyer image {} deleted", flyerImageId);
            currentEvent.setFlyerImageId(imageService.createImage(flyer));
        }

        currentEvent.setTitle(title);
        currentEvent.setDescription(description);
        currentEvent.setTime(time);
        currentEvent.setAddress(address);
        currentEvent.setAttendeesLimit(attendeesLimit);
        currentEvent.setCity(resolvedCity);
        currentEvent.setDate(date);

        LOGGER.info("Event {} updated", eventId);
    }

    @Override
    @Transactional
    public void deleteEvent(final long id, final String message) {
        LOGGER.debug("Deleting event {}", id);
        Event event = eventDao.findById(id).orElseThrow(() -> {
            LOGGER.warn("Event not found {}", id);
            return new EventNotFoundException("Event not found");});
        if(message != null && !message.isEmpty()){
            event.setDeletionMessage(message);
            emailService.sendEventDeletionNotification(new EmailEvent(event),message);
        }
        event.setDeleted(true); //todo check
        //fixme: borrar las responses tmb
    }

    @Override
    @Transactional
    public void deleteEventResponse(final EventResponse eventResponse, final String message) {
        LOGGER.debug("Deleting event response {}", eventResponse);

        //        EventResponse deletedComment = findEventResponseById(eventResponse)
//                .orElseThrow(() ->{
//                    LOGGER.error("Event response not found {}", eventResponse);
//                    return new EventResponseNotFoundException("Event response doesn't exist");});

        Event event = eventResponse.getEvent();

        User commentAuthor = eventResponse.getUser();
        emailService.sendEventCommentDeletionNotification(eventResponse,new EmailEvent(event),new EmailUser(commentAuthor), message );
        LOGGER.info("Email notification sent for the event response {}", eventResponse);
        eventResponse.setDeletionMessage(message);
        LOGGER.info("Event response {} updated", eventResponse);
        eventResponse.setDeleted(true);
        LOGGER.info("Event response {} deleted", eventResponse);
    }

    @Override
    public int countEventResponses(final long eventId){
        LOGGER.debug("Getting response count for event {}", eventId);
        return eventResponseDao.countByEventId(eventId);
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

    @Override
    @Transactional(readOnly = true)
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
        boolean isAttending = eventAttendanceDao.exists(userService.findUserById(userId).orElseThrow(()-> {
            LOGGER.error("User not found {}", userId);
            return new UserNotFoundException("User not found");
        }), event);

        return Optional.of(new EventWithUserInfo(event, isAttending, isCreator));
    }


    @Override
    public Page<Event> findJourneyEvents(final Journey journey, final PageParams pageParams){
        return eventDao.findAllWithFilters(
                journey.getUser().getId(),
                null,
                SortFieldEvent.DATE,
                SortDirection.ASC,
                null,
                journey.getStartDate(),
                journey.getEndDate() != null && journey.getEndDate().isBefore(LocalDate.now())
                        ? journey.getEndDate() : LocalDate.now().minusDays(1),
                null,
                true,
                false,
                pageParams
        );
    }


    @Override
    public Page<Event> findCreatedByJourney(final Journey journey, final PageParams pageParams){
        return eventDao.findAllWithFilters(
                journey.getUser().getId(),
                null,
                SortFieldEvent.DATE,
                SortDirection.ASC,
                null,
                journey.getStartDate(),
                capEndDateForPastEvents(journey.getEndDate()),
                null,
                false,
                true,
                pageParams
        );
    }

    @Override
    public Page<Event> findAttendedByJourney(final Journey journey, final PageParams pageParams){
        return eventDao.findAllWithFilters(
                journey.getUser().getId(),
                null,
                SortFieldEvent.DATE,
                SortDirection.ASC,
                null,
                journey.getStartDate(),
                capEndDateForPastEvents(journey.getEndDate()),
                null,
                true,
                false,
                pageParams
        );
    }

    private LocalDate capEndDateForPastEvents(LocalDate endDate) {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        return endDate == null || endDate.isAfter(yesterday) ? yesterday : endDate;
    }

    private LocalDate ensureStartDateForUpcomingEvents(LocalDate startDate) {
        LocalDate today = LocalDate.now();
        return startDate == null || startDate.isBefore(today) ? today : startDate;
    }

//
//    @Override
//    @Scheduled(cron = "0 0 12 * * ?")
//    @Transactional(readOnly = true)
//    public void sendEventReminders(){
//        LOGGER.info("Starting scheduled task: sending reminder emails for upcoming events");
//        LocalDate today = LocalDate.now();
//        LocalDate tomorrow = today.plusDays(1);
//
//        // CHANGE TO USE PAGES:
//        List<Event> upcomingEvents = eventDao.findAllBetweenDates(today, tomorrow);
//
//        LOGGER.info("Found {} events occurring in the next 24 hours", upcomingEvents.size());
//
//        for (Event event : upcomingEvents) {
//            // CHANGE HERE:
//            // List<User> eventAttendees = ???;
//            // emailService.sendEventReminderNotification(event, attendees);
//        }
//
//        LOGGER.info("Completed scheduled task: sent reminder emails for upcoming events");
//
//    }


    @Override
    @Scheduled(cron = "0 0 12 * * ?")
    public void sendEventReminders() {
        LOGGER.info("Starting scheduled task: sending reminder emails for upcoming events");
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);

        int eventPage = 1;
        int eventPageSize = 50;
        Page<Event> eventsPage;
        int totalEventsProcessed = 0;

        do {
            eventsPage = eventDao.findAllBetweenDates(
                    today,
                    tomorrow,
                    new PageParams(eventPage, eventPageSize)
            );

            List<Event> events = eventsPage.getContent();
            LOGGER.debug("Processing page {} of {} with {} events", eventPage, eventsPage.getTotalPages(), events.size());

            for (Event event : events) {
                sendRemindersForEvent(event);
                totalEventsProcessed++;
            }

            eventPage++;
        } while (eventPage <= eventsPage.getTotalPages());

        LOGGER.info("Completed scheduled task: sent reminder emails for {} upcoming events", totalEventsProcessed);
    }

    @Override
    public int countEventsCreatedByUser(long userId) {
        return eventDao.countEventsCreatedByUser(userId);
    }

    @Override
    public int countEventsAttendedByUser(long userId) {
        return eventAttendanceDao.countEventsAttendedByUser(userId);
    }

    private void sendRemindersForEvent(Event event) {
        LOGGER.debug("Processing reminders for event: {} (ID: {})", event.getTitle(), event.getId());

        int attendeePage = 1;
        int attendeePageSize = 100;
        Page<User> attendeesPage;
        int totalAttendees = 0;

        EmailEvent emailEvent = new EmailEvent(event);

        do {
            attendeesPage = eventAttendanceDao.findAttendeesByEventId(
                    event.getId(),
                    new PageParams(attendeePage, attendeePageSize)
            );

            List<EmailUser> attendees = attendeesPage.getContent().stream().map(EmailUser::new).toList();



            if (!attendees.isEmpty()) {
                emailService.sendEventReminderNotification(emailEvent, attendees);
                totalAttendees += attendees.size();
            }

            attendeePage++;
        } while (attendeePage <= attendeesPage.getTotalPages());

        LOGGER.debug("Sent reminders to {} attendees for event: {}", totalAttendees, event.getTitle());
    }
}
