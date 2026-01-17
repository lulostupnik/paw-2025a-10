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
    public Event createEvent(final String email, final String cityName, final LocalDate date, final String description, final String title, final LocalTime time, final String address, final Integer attendeesLimit) {

        LOGGER.debug("Creating event for user {}", email);
        User user = userService.findUserByEmail(email).orElseThrow(()-> {
                LOGGER.error("User not found {}", email);
                return new UserNotFoundException(email);}
        );
        return createEventInternal(user, cityName, date, description, title, time, address, attendeesLimit);
    }

    @Override
    @Transactional
    public Event createEvent(final long userId, final String cityName, final LocalDate date, final String description, final String title, final LocalTime time, final String address, final Integer attendeesLimit) {
        LOGGER.debug("Creating event for user {}", userId);
        User user = userService.findUserById(userId).orElseThrow(() -> {
            LOGGER.error("User not found {}", userId);
            return new UserNotFoundException(userId);
        });
        return createEventInternal(user, cityName, date, description, title, time, address, attendeesLimit);
    }

    private Event createEventInternal(final User user, final String cityName, final LocalDate date, final String description, final String title, final LocalTime time, final String address, final Integer attendeesLimit) {
        City city = cityService.findCityByName(cityName).orElseThrow(() ->{
            LOGGER.error("City not found {}", cityName);
            return new InvalidReferenceException("City", cityName);}
        );
        Event event = eventDao.create(user, city, date, description, null, title, time, address, attendeesLimit);
        LOGGER.info("Event {} created", event.getId());
        eventAttendanceDao.create(user, event);
        return event;
    }

    @Override
    @Transactional
    public EventResponse createEventResponse(final String email, final long eventId, final String message) {
        LOGGER.debug("Replying to event {}", eventId);
        User responder = userService.findUserByEmail(email).orElseThrow(() -> {
            LOGGER.error("User not found {}", email);
            return new UserNotFoundException(email);
        });
        return createEventResponseInternal(responder, eventId, message);
    }

    @Override
    @Transactional
    public EventResponse createEventResponse(final long userId, final long eventId, final String message) {
        LOGGER.debug("Replying to event {}", eventId);
        User responder = userService.findUserById(userId).orElseThrow(() -> {
            LOGGER.error("User not found {}", userId);
            return new UserNotFoundException(userId);
        });
        return createEventResponseInternal(responder, eventId, message);
    }

    private EventResponse createEventResponseInternal(final User responder, final long eventId, final String message) {
        Event event = eventDao.findById(eventId).orElseThrow(() -> {
            LOGGER.error("Event not found {}", eventId);
            return new EventNotFoundException(eventId);
        });

        EventResponse eventResponse = eventResponseDao.create(responder, event, message);
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
        return eventResponse;
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
    public Page<Event> findEvents(final long userId, final PageParams pageParams) {
        LOGGER.debug("Getting all events for user {}", userId);
        return eventDao.findByUserId(userId, pageParams);
    }


    @Override
    @Transactional
    public EventAttendance createEventAttendance(final long userId, final  long eventId) {
        Event event = eventDao.findById(eventId).orElseThrow(() -> {
            LOGGER.warn("Event not found {}", eventId);
            return new EventNotFoundException(eventId);
        });
        User user = userService.findUserById(userId).orElseThrow(() -> {
            LOGGER.warn("User not found {}", userId);
            return new UserNotFoundException(userId);
        });
        if(! event.getIsFuture()){
            LOGGER.info("Event (id {}) is not in the future", eventId);
            throw new EventNotInTheFutureException(eventId);
        }
        if (eventAttendanceDao.exists(user, event)){
            LOGGER.info("User {} is already attending event (id {})", userId, eventId);
            throw new UserAlreadyAttendingException(userId, eventId);
        }

        if (event.getAttendeesLimit() == null || event.getAttendeesCount() < event.getAttendeesLimit()) {
            EventAttendance attendance = eventAttendanceDao.create(user, event);
            LOGGER.info("User {} is now attending event {}", userId, eventId);
            return attendance ;
        }
        LOGGER.warn("User {} trying to attend a full event ({})", userId, eventId);
        throw new EventIsFullException(eventId);

    }

    @Override
    @Transactional
    public void deleteEventAttendance(final long userId, final  long eventId) {
        LOGGER.debug("User {} is canceling attendance for event {}", userId, eventId);
        Event event = eventDao.findById(eventId).orElseThrow(()->{
            LOGGER.warn("Event not found {}", eventId);
            return new EventNotFoundException(eventId);
        });
        if(! event.getIsFuture()){
            LOGGER.info("Event (id {}) is not in the future", eventId);
            throw new EventNotInTheFutureException(eventId);
        }
        eventAttendanceDao.delete(userId, eventId);
        LOGGER.info("User {} has canceled attendance for event {}", userId, eventId);
    }

    @Override
    @Transactional
    public Rating rateEvent(User user, long eventId, double rating) {
        Event event = eventDao.findById(eventId).orElseThrow(() -> {
            LOGGER.warn("Event not found {}", eventId);
            return new EventNotFoundException(eventId);
        });
        return eventRatingDao.rateEvent(user, event, rating);
    }

    @Override
    @Transactional
    public Rating rateEvent(long userId, long eventId, double rating) {
        User user = userService.findUserById(userId).orElseThrow(() -> {
            LOGGER.warn("User not found {}", userId);
            return new UserNotFoundException(userId);
        });
        return rateEvent(user, eventId, rating);
    }

    @Transactional
    @Override
    public Rating updateEventRating(User user, long eventId, double value) {
        Rating rating = eventRatingDao.findRatingByUserAndEvent(user.getId(), eventId)
                .orElseThrow(() -> {
                    LOGGER.warn("Rating not found for user {} and event {}", user.getId(), eventId);
                    return new RatingNotFoundException(user.getId(), eventId);
                });
        rating.setRating(value);
        return rating;
    }

    @Transactional
    @Override
    public Rating updateEventRating(long userId, long eventId, double value) {
        User user = userService.findUserById(userId).orElseThrow(() -> {
            LOGGER.warn("User not found {}", userId);
            return new UserNotFoundException(userId);
        });
        return updateEventRating(user, eventId, value);
    }

    @Override
    public Optional<Rating> findRatingByUserAndEvent(long userId, long eventId) {
        return eventRatingDao.findRatingByUserAndEvent(userId, eventId);
    }

    @Override
    public Optional<Rating> findRatingById(long eventId, long ratingId) {
        Optional<Rating> maybeRating = eventRatingDao.findById(ratingId);
        if (maybeRating.isPresent() && maybeRating.get().getEvent().getId() != eventId) {
            LOGGER.warn("Rating {} does not belong to event {}", ratingId, eventId);
            return Optional.empty();
        }
        return maybeRating;
    }

    @Override
    public Page<Rating> findRatingsByEventId(long eventId, PageParams pageParams) {
        eventDao.findById(eventId).orElseThrow(() -> new EventNotFoundException(eventId));
        return eventRatingDao.findByEventId(eventId, pageParams);
    }

    @Override
    @Transactional
    public void deleteRating(long eventId, long ratingId) {
        LOGGER.debug("Deleting rating {} for event {}", ratingId, eventId);
        findRatingById(eventId, ratingId).orElseThrow(() -> new RatingNotFoundException(eventId, ratingId, true));
        eventRatingDao.delete(ratingId);
        LOGGER.info("Rating {} deleted", ratingId);
    }

    @Override
    public int countRatingsByEvent(long eventId) {
        return eventRatingDao.countRatingsByEvent(eventId);
    }

//
//    @Override
//    public Page<Event> findUpcomingEventsByAttendee(long userId, PageParams pageParams) {
//        return eventDao.findAllWithFilters(
//                userId,
//                null, // searchTerm
//                null, // sortBy
//                SortDirection.DESC, // direction
//                null, // destination
//                LocalDate.now(), // startDate
//                null, // endDate
//                LocalTime.now(),
//                null,
//                null, // interest
//                true, // attending
//                false, // isCreator
//                pageParams
//        );
//    }
//
//    @Override
//    public Page<Event> findFinishedEventsByAttendee(long userId, PageParams pageParams) {
//        return  eventDao.findAllWithFilters(
//                userId,
//                null, // searchTerm
//                null, // sortBy
//                SortDirection.DESC, // direction
//                null, // destination
//                null, // startDate
//                LocalDate.now(), // endDate
//                null,
//                LocalTime.now(),
//                null, // interest
//                true, // attending
//                false, // isCreator
//                pageParams
//        );
//    }

    @Override
    public List<Event> findRecommendedEvents(final long userId, final  int limit) {
        LOGGER.debug("Getting recommended events for user {} with limit {}", userId, limit);
        if (limit <= 0) {
            throw new InvalidPaginationParamsException("Limit must be greater than 0");
        }
        List<Event> events = eventDao.findRecommended(userId, new PageParams(1, limit)).getContent();
        if (events.isEmpty()) {
            LOGGER.warn("No recommended events found for user {}. Falling back to top events.", userId);
            events = eventDao.findTopByUser(userId,new PageParams(1, limit)).getContent();
        }
        if(events.isEmpty()){
            LOGGER.warn("No top events found for user {}. Falling back to any events.", userId);
            events = eventDao.findAll(new PageParams(1, limit)).getContent();
        }
        return events;
    }

    @Override
    public List<Event> findTopEvents(final int limit){
        if (limit <= 0) {
            throw new InvalidPaginationParamsException("Limit must be greater than 0");
        }
        return eventDao.findTop(new PageParams(1, limit)).getContent();
    }

    @Override
    public boolean isEventOwnedByUser(final String email, final long eventId) {
        LOGGER.debug("Checking for event ownership of event {} by user {}", eventId, email);
        Optional<Event> event = eventDao.findById(eventId);
        return event.isPresent() && event.get().getUser().getEmail().equals(email);
    }

    @Override
    public boolean isUserEventAttendee(final long userId, final long eventId) {
        LOGGER.debug("Checking if user {} is attendee of event {}", userId, eventId);
        return eventAttendanceDao.exists(userId, eventId);
    }

    @Override
    public boolean isRatingOwnedByUser(final long eventId, final long ratingId, final long userId) {
        LOGGER.debug("Checking if rating {} for event {} is owned by user {}", ratingId, eventId, userId);
        Optional<Rating> rating = findRatingById(eventId, ratingId);
        return rating.isPresent() && rating.get().getUser().getId() == userId;
    }

    @Override
    public Page<Event> searchEventsWithFilters(final String search, final Long userId, final SortFieldEvent sortBy, final SortDirection direction, final String destination, final LocalDate startDate, final LocalDate endDate, final String interest,
                                               final boolean isPast, final boolean isUpcoming, final boolean attending, Long attendedByUserId,
                                               String university, Integer minRating, Boolean hasCapacity, final PageParams pageParams) {
        LOGGER.debug("Getting events with search {}, userId {}, sortBy {}, direction {}, destination {}, startDate {}, endDate {}, interest {}, isPast {}, isUpcoming {}, attending {}", search, userId, sortBy, direction, destination, startDate, endDate, interest, isPast, isUpcoming, attending);

        LocalDate adjustedStartDate = startDate;
        LocalDate adjustedEndDate = endDate;
        LocalTime startTime = null;
        LocalTime endTime = null;

        if (isUpcoming) {
            adjustedStartDate = ensureStartDateForUpcomingEvents(startDate);
            if (adjustedStartDate.equals(LocalDate.now())) {
                startTime = LocalTime.now();
            }
        }
        if (isPast) {
            adjustedEndDate = capEndDateForPastEvents(endDate);
            if (adjustedEndDate.equals(LocalDate.now())) {
                endTime = LocalTime.now();
            }
        }

        return eventDao.findAllWithFilters(
                userId,
                search,
                sortBy,
                direction,
                destination,
                adjustedStartDate,
                adjustedEndDate,
                startTime,
                endTime,
                interest,
                attending,
                false,
                attendedByUserId,
                university,
                minRating,
                hasCapacity,
                pageParams
        );

    }

    @Override
    @Transactional
    public Event updateEvent(final long eventId, final String cityName, final LocalDate date, final String description,
                            final String title, final LocalTime time, final String address, final Integer attendeesLimit) {
        LOGGER.debug("Editing event {}", eventId);
        Event currentEvent = eventDao.findById(eventId)
                .orElseThrow(() ->{
                    LOGGER.warn("Event not found {}", eventId);
                    return new EventNotFoundException(eventId);}
                );

        City resolvedCity = cityService.findCityByName(cityName).orElseThrow(() -> {
            LOGGER.warn("City not found {}", cityName);
            return new InvalidReferenceException("City", cityName);}
        );

        currentEvent.setTitle(title);
        currentEvent.setDescription(description);
        currentEvent.setTime(time);
        currentEvent.setAddress(address);
        currentEvent.setAttendeesLimit(attendeesLimit);
        currentEvent.setCity(resolvedCity);
        currentEvent.setDate(date);

        LOGGER.info("Event {} updated", eventId);
        return currentEvent;
    }

    @Override
    @Transactional
    public void deleteEvent(final long id, final String message) {
        LOGGER.debug("Deleting event {}", id);
        Optional<Event> maybeEvent = eventDao.findById(id);
        if (maybeEvent.isEmpty()) {
            LOGGER.info("Event not found {}", id);
            return;
        }
        Event event = maybeEvent.get();
        if(message != null && !message.isEmpty()){
            event.setDeletionMessage(message);
            emailService.sendEventDeletionNotification(new EmailEvent(event),message);
        }
        event.setDeleted(true);
        LOGGER.info("Event {} deleted", id);
    }

    @Override
    @Transactional
    public void deleteEventResponse(final EventResponse eventResponse, final String message) {
        LOGGER.debug("Deleting event response {}", eventResponse);
        if(eventResponse.isDeleted()){
            LOGGER.info("Event response {} already deleted", eventResponse);
            return;
        }
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
    @Transactional
    public void deleteEventResponse(final long eventId, final long responseId, final String message) {
        LOGGER.debug("Deleting event response {} for event {}", responseId, eventId);
        EventResponse eventResponse = findEventResponseById(eventId, responseId)
                .orElseThrow(() -> {
                    LOGGER.warn("Event response {} not found for event {}", responseId, eventId);
                    return new EventResponseNotFoundException(eventId, responseId);
                });
        deleteEventResponse(eventResponse, message);
    }

    @Override
    public Page<EventResponse> findEventResponses(final long eventId, final PageParams pageParams) {
        LOGGER.debug("Getting all responses for event {} with pageParams {}", eventId, pageParams);
        return eventResponseDao.listAllByEventId(eventId, pageParams);
    }

    @Override
    public Optional<EventResponse> findEventResponseById(final long id){
        LOGGER.debug("Getting event response by id {}", id);
        return eventResponseDao.findById(id);
    }

    @Override
    public Optional<EventResponse> findEventResponseById(final long eventId, final long responseId) {
        LOGGER.debug("Getting event response by id {} for event {}", responseId, eventId);
        Optional<EventResponse> maybeResponse = eventResponseDao.findById(responseId);
        if (maybeResponse.isPresent() && maybeResponse.get().getEvent().getId() != eventId) {
            LOGGER.warn("Event response {} does not belong to event {}", responseId, eventId);
            return Optional.empty();
        }
        return maybeResponse;
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
            return new UserNotFoundException(userId);
        }), event);

        return Optional.of(new EventWithUserInfo(event, isAttending, isCreator));
    }

    @Override
    public Page<Event> findCreatedByJourney(final Journey journey, final PageParams pageParams){
        LocalDate cappedEndDate = capEndDateForPastEvents(journey.getEndDate());
        LocalTime endTime = null;

        if (cappedEndDate.equals(LocalDate.now())) {
            endTime = LocalTime.now();
        }

        return eventDao.findAllWithFilters(
                journey.getUser().getId(),
                null,
                SortFieldEvent.DATE,
                SortDirection.ASC,
                null,
                journey.getStartDate(),
                cappedEndDate,
                null,
                endTime,
                null,
                false,
                true,
                null,
                null,
                null,
                false,
                pageParams
        );
    }

    @Override
    public Page<Event> findAttendedByJourney(final Journey journey, final PageParams pageParams){
        LocalDate cappedEndDate = capEndDateForPastEvents(journey.getEndDate());
        LocalTime endTime = null;

        if (cappedEndDate.equals(LocalDate.now())) {
            endTime = LocalTime.now();
        }

        return eventDao.findAllWithFilters(
                journey.getUser().getId(),
                null,
                SortFieldEvent.DATE,
                SortDirection.ASC,
                null,
                journey.getStartDate(),
                cappedEndDate,
                null,
                endTime,
                null,
                true,
                false,
                null,
                null,
                null,
                false,
                pageParams
        );
    }

    private LocalDate capEndDateForPastEvents(LocalDate endDate) {
        LocalDate yesterday = LocalDate.now();
        return endDate == null || endDate.isAfter(yesterday) ? yesterday : endDate;
    }

    private LocalDate ensureStartDateForUpcomingEvents(LocalDate startDate) {
        LocalDate today = LocalDate.now();
        return startDate == null || startDate.isBefore(today) ? today : startDate;
    }

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
    public Page<User> findEventAttendees(long eventId, PageParams pageParams) {
        LOGGER.debug("Getting attendees for event {} with pageParams {}", eventId, pageParams);
        return eventAttendanceDao.findAttendeesByEventId(eventId, pageParams);
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

    @Override
    public Optional<Image> getEventFlyer(long eventId) {
        LOGGER.debug("Getting flyer for event {}", eventId);
        Long flyerId = eventDao.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(eventId))
                .getFlyerImageId();
        if (flyerId == null) {
            return Optional.empty();
        }
        return imageService.findImage(flyerId);
    }

    @Override
    @Transactional
    public void updateEventFlyer(long eventId, byte[] flyer) {
        LOGGER.debug("Updating flyer for event {}", eventId);
        Event event = eventDao.findById(eventId).orElseThrow(() -> new EventNotFoundException(eventId));
        Long oldFlyerImageId = event.getFlyerImageId();
        if (oldFlyerImageId != null) {
            imageService.deleteImage(oldFlyerImageId);
            LOGGER.info("Old flyer image {} deleted for event {}", oldFlyerImageId, eventId);
        }
        long newFlyerImageId = imageService.createImage(flyer);
        event.setFlyerImageId(newFlyerImageId);
        LOGGER.info("New flyer image {} created for event {}", newFlyerImageId, eventId);
    }
}
