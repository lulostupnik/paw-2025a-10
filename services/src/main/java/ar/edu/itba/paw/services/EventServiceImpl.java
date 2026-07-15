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

import static ar.edu.itba.paw.services.AfterCommitExecutor.runAfterCommit;
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
    public EventServiceImpl(final UserService userService, final EventResponseDao eventResponseDao,
                            final EventDao eventDao, final EmailService emailService, final ImageService imageService,
                            final CityService cityService, final EventAttendanceDao eventAttendanceDao,
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
    public Event createEvent(final long userId, final long cityId, final LocalDate date, final String description, final String title, final LocalTime time, final String address, final Integer attendeesLimit) {
        LOGGER.debug("Creating event for user {}", userId);
        User user = userService.findUserById(userId).orElseThrow(() -> {
            LOGGER.error("User not found {}", userId);
            return new UserNotFoundException();
        });
        return createEventInternal(user, cityId, date, description, title, time, address, attendeesLimit);
    }

    private Event createEventInternal(final User user, final long cityId, final LocalDate date, final String description, final String title, final LocalTime time, final String address, final Integer attendeesLimit) {
        City city = cityService.findCityById(cityId).orElseThrow(() ->{
            LOGGER.error("City not found {}", cityId);
            return new InvalidReferenceException();}
        );
        Event event = eventDao.create(user, city, date, description, null, title, time, address, attendeesLimit);
        LOGGER.info("Event {} created", event.getId());
        eventAttendanceDao.create(user, event);
        return event;
    }

    @Override
    @Transactional
    public EventResponse createEventResponse(final long userId, final long eventId, final String message) {
        LOGGER.debug("Replying to event {}", eventId);
        User responder = userService.findUserById(userId).orElseThrow(() -> {
            LOGGER.error("User not found {}", userId);
            return new UserNotFoundException();
        });
        return createEventResponseInternal(responder, eventId, message);
    }

    private EventResponse createEventResponseInternal(final User responder, final long eventId, final String message) {
        Event event = eventDao.findById(eventId).orElseThrow(() -> {
            LOGGER.error("Event not found {}", eventId);
            return new EventNotFoundException();
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
                List<EmailUser> respondersSnapshot = List.copyOf(responders);
                runAfterCommit(() -> emailService.answerEventNotification(
                        respondersSnapshot,
                        message,
                        emailResponder,
                        emailEvent
                ));
            }

            page++;
        } while (page <= respondersPage.getTotalPages());

        LOGGER.info("Email notifications sent to all responders for event {}", eventId);

        runAfterCommit(() -> emailService.answerEventOwnerNotification(message, emailResponder, emailEvent));
        LOGGER.info("Email notifications sent to event owner for event {}", eventId);
        return eventResponse;
    }

    @Override
    public Optional<Event> findEventById(final long id){
        LOGGER.debug("Getting event by id {}", id);
        return eventDao.findById(id);
    }

    @Override
    public Optional<EventWithStatistics> findEventWithStatistics(final long eventId) {
        LOGGER.debug("Getting event with statistics by id {}", eventId);

        Event event;

        Optional<Event> maybeEvent = eventDao.findById(eventId);
        if(maybeEvent.isEmpty()){
            LOGGER.warn("Event not found {}", eventId);
            return Optional.empty();
        }
        event = maybeEvent.get();

        int createdEventsCount = eventDao.countEventsCreatedByUser(event.getUser().getId());
        int attendedEventsCount = eventAttendanceDao.countEventsAttendedByUser(event.getUser().getId());

        CountryAttendeeCount topAttendeeCountry = eventDao.findTopAttendeeCountry(event.getId()).orElse(null);

        return Optional.of(new EventWithStatistics(event, createdEventsCount, attendedEventsCount, topAttendeeCountry));
    }



    @Override
    @Transactional
    public EventAttendance createEventAttendance(final long userId, final  long eventId) {
        Event event = eventDao.findByIdForUpdate(eventId).orElseThrow(() -> {
            LOGGER.warn("Event not found {}", eventId);
            return new EventNotFoundException();
        });
        User user = userService.findUserById(userId).orElseThrow(() -> {
            LOGGER.warn("User not found {}", userId);
            return new UserNotFoundException();
        });
        if(! event.getIsFuture()){
            LOGGER.info("Event (id {}) is not in the future", eventId);
            throw new EventNotInTheFutureException();
        }
        if (eventAttendanceDao.exists(user, event)){
            LOGGER.info("User {} is already attending event (id {})", userId, eventId);
            throw new UserAlreadyAttendingException();
        }

        if (event.getAttendeesLimit() == null || event.getAttendeesCount() < event.getAttendeesLimit()) {
            EventAttendance attendance = eventAttendanceDao.create(user, event);
            LOGGER.info("User {} is now attending event {}", userId, eventId);
            return attendance ;
        }
        LOGGER.warn("User {} trying to attend a full event ({})", userId, eventId);
        throw new EventIsFullException();

    }

    @Override
    public Optional<EventAttendance> findEventAttendance(final long userId, final long eventId) {
        return eventAttendanceDao.findById(userId, eventId);
    }

    @Override
    @Transactional
    public void deleteEventAttendance(final long userId, final  long eventId) {
        LOGGER.debug("User {} is canceling attendance for event {}", userId, eventId);
        Event event = eventDao.findById(eventId).orElseThrow(()-> new EventNotFoundException());
        if(! event.getIsFuture()){
            throw new EventNotInTheFutureException();
        }
        if (!eventAttendanceDao.exists(userId, eventId)) {
            throw new EventAttendanceNotFoundException();
        }
        eventAttendanceDao.delete(userId, eventId);
        LOGGER.info("User {} has canceled attendance for event {}", userId, eventId);
    }

    @Override
    @Transactional
    public Rating rateEvent(long userId, long eventId, double rating) {
        LOGGER.debug("User {} is rating event {} with {}", userId, eventId, rating);
        User user = userService.findUserById(userId).orElseThrow(() -> {
            LOGGER.warn("User not found {}", userId);
            return new UserNotFoundException();
        });
        Event event = eventDao.findById(eventId).orElseThrow(() -> {
            LOGGER.warn("Event not found {}", eventId);
            return new EventNotFoundException();
        });
        if (event.getIsFuture()) {
            LOGGER.warn("User {} attempted to rate future event {}", userId, eventId);
            throw new EventNotOccurredException();
        }
        if (!isUserEventAttendee(userId, eventId)) {
            LOGGER.warn("User {} attempted to rate unattended event {}", userId, eventId);
            throw new EventAttendanceRequiredException();
        }
        return eventRatingDao.rateEvent(user, event, rating);
    }

    @Transactional
    @Override
    public Rating updateEventRating(long eventId, long ratingId, double value) {
        Rating rating = findRatingById(eventId, ratingId)
                .orElseThrow(() -> {
                    LOGGER.warn("Rating {} not found for event {}", ratingId, eventId);
                    return new RatingNotFoundException();
                });
        rating.setRating(value);
        return rating;
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
        eventDao.findById(eventId).orElseThrow(() -> new EventNotFoundException());
        return eventRatingDao.findByEventId(eventId, pageParams);
    }

    @Override
    @Transactional
    public void deleteRating(long eventId, long ratingId) {
        LOGGER.debug("Deleting rating {} for event {}", ratingId, eventId);
        findRatingById(eventId, ratingId).orElseThrow(() -> new RatingNotFoundException());
        eventRatingDao.delete(ratingId);
        LOGGER.info("Rating {} deleted", ratingId);
    }

    @Override
    public int countRatingsByEvent(long eventId) {
        return eventRatingDao.countRatingsByEvent(eventId);
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
    public Page<Event> searchEventsWithFilters(final String search, final Long recommendedForUser, final Long creatorId, final SortFieldEvent sortBy, final SortDirection direction, final String destination, final LocalDate startDate, final LocalDate endDate, final String interest,
                                              Long attendedByUserId,
                                               String university, Integer minRating, Boolean hasCapacity, final Boolean top, final PageParams pageParams) {
        LOGGER.debug("Getting events with search {}, recommendedForUser {}, creatorId {}, sortBy {}, direction {}, destination {}, startDate {}, endDate {}, interest {}, attendedByUserId {}", search, recommendedForUser, creatorId, sortBy, direction, destination, startDate, endDate, interest, attendedByUserId);

        if (recommendedForUser != null) {
            validateRecommendedEventsFilters(search, creatorId, sortBy, direction, destination, startDate, endDate, interest, attendedByUserId, university, minRating, hasCapacity, top);
            return findRecommendedEvents(recommendedForUser, pageParams);
        }

        if (Boolean.TRUE.equals(top)) {
            validateTopEventsFilters(search, creatorId, sortBy, direction, destination, startDate, endDate, interest, attendedByUserId, university, minRating, hasCapacity);
            return eventDao.findTop(pageParams);
        }

        final SortFieldEvent effectiveSortBy = sortBy == null ? SortFieldEvent.DATE : sortBy;
        final SortDirection effectiveDirection = direction == null ? SortDirection.ASC : direction;

        return eventDao.findAllWithFilters(
                creatorId,
                search,
                effectiveSortBy,
                effectiveDirection,
                destination,
                startDate,
                endDate,
                null,
                null,
                interest,
                attendedByUserId,
                university,
                minRating,
                hasCapacity,
                pageParams
        );

    }


    private Page<Event> findRecommendedEvents(final long userId, final PageParams pageParams) {
        final Page<Event> recommended = eventDao.findRecommended(userId, pageParams);
        if (recommended.getTotalElements() > 0) {
            return recommended;
        }

        LOGGER.debug("No recommended events for user {}, falling back to top events", userId);
        final Page<Event> topEvents = eventDao.findTopByUser(userId, pageParams);
        if (topEvents.getTotalElements() > 0) {
            return topEvents;
        }

        LOGGER.debug("No top events for user {}, falling back to all events", userId);
        return eventDao.findAll(pageParams);
    }

    private void validateRecommendedEventsFilters(final String search, final Long creatorId, final SortFieldEvent sortBy, final SortDirection direction, final String destination,
                                                  final LocalDate startDate, final LocalDate endDate, final String interest, final Long attendedByUserId,
                                                  final String university, final Integer minRating, final Boolean hasCapacity, final Boolean top) {
        if (search != null || creatorId != null || sortBy != null || direction != null || destination != null
                || startDate != null || endDate != null || interest != null || attendedByUserId != null
                || university != null || minRating != null || hasCapacity != null || Boolean.TRUE.equals(top)) {
            throw new MutuallyExclusiveFiltersException();
        }
    }

    private void validateTopEventsFilters(final String search, final Long creatorId, final SortFieldEvent sortBy, final SortDirection direction, final String destination,
                                          final LocalDate startDate, final LocalDate endDate, final String interest, final Long attendedByUserId,
                                          final String university, final Integer minRating, final Boolean hasCapacity) {
        if (search != null || creatorId != null || sortBy != null || direction != null || destination != null
                || startDate != null || endDate != null || interest != null || attendedByUserId != null
                || university != null || minRating != null || hasCapacity != null) {
            throw new MutuallyExclusiveFiltersException();
        }
    }

    @Override
    @Transactional
    public Event patchEvent(final long id, final Long cityId, final LocalDate date, final String description,
                            final String title, final LocalTime time, final String address, final Integer attendeesLimit,
                            final Boolean deleted, final String deletionMessage) {
        LOGGER.debug("Patching event {}", id);
        Event currentEvent = eventDao.findByIdForUpdate(id).orElseThrow(() -> new EventNotFoundException());

        if (cityId != null) {
            City resolvedCity = cityService.findCityById(cityId).orElseThrow(() -> new InvalidReferenceException());
            currentEvent.setCity(resolvedCity);
        }
        if (attendeesLimit != null) {
            if (currentEvent.getAttendeesCount() > attendeesLimit) {
                throw new AttendeesLimitBelowCurrentException();
            }
            currentEvent.setAttendeesLimit(attendeesLimit);
        }
        if (title != null) {
            currentEvent.setTitle(title);
        }
        if (description != null) {
            currentEvent.setDescription(description);
        }
        if (time != null) {
            currentEvent.setTime(time);
        }
        if (address != null) {
            currentEvent.setAddress(address);
        }
        if (date != null) {
            currentEvent.setDate(date);
        }
        if (Boolean.TRUE.equals(deleted)) {
            deleteEvent(id, deletionMessage);
        }

        LOGGER.info("Event {} patched", id);
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
            runAfterCommit(() -> emailService.sendEventDeletionNotification(new EmailEvent(event), message));
        }
        event.setDeleted(true);
        LOGGER.info("Event {} deleted", id);
    }

    private void deleteEventResponse(final EventResponse eventResponse, final String message) {
        LOGGER.debug("Deleting event response {}", eventResponse);
        if(eventResponse.isDeleted()){
            LOGGER.info("Event response {} already deleted", eventResponse);
            return;
        }
        Event event = eventResponse.getEvent();
        User commentAuthor = eventResponse.getUser();
        runAfterCommit(() -> emailService.sendEventCommentDeletionNotification(
                eventResponse,
                new EmailEvent(event),
                new EmailUser(commentAuthor),
                message
        ));
        LOGGER.info("Email notification sent for the event response {}", eventResponse);
        eventResponse.setDeletionMessage(message);
        LOGGER.info("Event response {} updated", eventResponse);
        eventResponse.setDeleted(true);
        LOGGER.info("Event response {} deleted", eventResponse);
    }

    @Override
    @Transactional
    public void patchEventResponse(final long eventId, final long responseId, final Boolean deleted, final String deletionMessage) {
        LOGGER.debug("Patching event response {} for event {}", responseId, eventId);

        if (Boolean.TRUE.equals(deleted)) {
            deleteEventResponse(eventId, responseId, deletionMessage);
        }
    }

    @Override
    @Transactional
    public void deleteEventResponse(final long eventId, final long responseId, final String message) {
        LOGGER.debug("Deleting event response {} for event {}", responseId, eventId);
        EventResponse eventResponse = findEventResponseById(eventId, responseId)
                .orElseThrow(() -> {
                    LOGGER.warn("Event response {} not found for event {}", responseId, eventId);
                    return new EventResponseNotFoundException();
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
    public boolean isEventResponseOwnedByUser(final long eventId, final long responseId, final long userId) {
        LOGGER.debug("Checking if response {} for event {} is owned by user {}", responseId, eventId, userId);
        Optional<EventResponse> response = findEventResponseById(eventId, responseId);
        return response.isPresent() && response.get().getUser().getId() == userId;
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
    public Page<EventAttendance> findEventAttendances(long eventId, PageParams pageParams) {
        LOGGER.debug("Getting attendances for event {} with pageParams {}", eventId, pageParams);
        return eventAttendanceDao.findByEventId(eventId, pageParams);
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
                .orElseThrow(() -> new EventNotFoundException())
                .getFlyerImageId();
        if (flyerId == null) {
            return Optional.empty();
        }
        return imageService.findImage(flyerId);
    }

    @Override
    @Transactional
    public Image updateEventFlyer(long eventId, byte[] flyer) {
        LOGGER.debug("Updating flyer for event {}", eventId);
        Event event = eventDao.findById(eventId).orElseThrow(() -> new EventNotFoundException());
        Long oldFlyerImageId = event.getFlyerImageId();
        if (oldFlyerImageId != null) {
            imageService.deleteImage(oldFlyerImageId);
            LOGGER.info("Old flyer image {} deleted for event {}", oldFlyerImageId, eventId);
        }
        long newFlyerImageId = imageService.createImage(flyer);
        event.setFlyerImageId(newFlyerImageId);
        LOGGER.info("New flyer image {} created for event {}", newFlyerImageId, eventId);
        return new Image(newFlyerImageId, flyer);
    }
}
