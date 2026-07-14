package ar.edu.itba.paw.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import ar.edu.itba.paw.interfaces.persistence.EventAttendanceDao;
import ar.edu.itba.paw.interfaces.persistence.EventDao;
import ar.edu.itba.paw.interfaces.persistence.EventRatingDao;
import ar.edu.itba.paw.interfaces.persistence.EventResponseDao;
import ar.edu.itba.paw.interfaces.services.CityService;
import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.interfaces.services.ImageService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Career;
import ar.edu.itba.paw.models.City;
import ar.edu.itba.paw.models.Country;
import ar.edu.itba.paw.models.CountryAttendeeCount;
import ar.edu.itba.paw.models.Event;
import ar.edu.itba.paw.models.EventAttendance;
import ar.edu.itba.paw.models.EventResponse;
import ar.edu.itba.paw.models.EventWithStatistics;
import ar.edu.itba.paw.models.Image;
import ar.edu.itba.paw.models.Journey;
import ar.edu.itba.paw.models.Page;
import ar.edu.itba.paw.models.PageParams;
import ar.edu.itba.paw.models.Rating;
import ar.edu.itba.paw.models.University;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.enums.SortDirection;
import ar.edu.itba.paw.models.enums.SortFieldEvent;
import ar.edu.itba.paw.models.exceptions.AttendeesLimitBelowCurrentException;
import ar.edu.itba.paw.models.exceptions.EventAttendanceNotFoundException;
import ar.edu.itba.paw.models.exceptions.EventAttendanceRequiredException;
import ar.edu.itba.paw.models.exceptions.EventIsFullException;
import ar.edu.itba.paw.models.exceptions.EventNotFoundException;
import ar.edu.itba.paw.models.exceptions.EventNotInTheFutureException;
import ar.edu.itba.paw.models.exceptions.EventNotOccurredException;
import ar.edu.itba.paw.models.exceptions.EventResponseNotFoundException;
import ar.edu.itba.paw.models.exceptions.InvalidPaginationParamsException;
import ar.edu.itba.paw.models.exceptions.InvalidReferenceException;
import ar.edu.itba.paw.models.exceptions.MutuallyExclusiveFiltersException;
import ar.edu.itba.paw.models.exceptions.RatingNotFoundException;
import ar.edu.itba.paw.models.exceptions.UserAlreadyAttendingException;
import ar.edu.itba.paw.models.exceptions.UserNotFoundException;

@RunWith(MockitoJUnitRunner.class)
public class EventServiceImplTest {

    private static final long EVENT_ID = 0;
    private static final long EVENT_2_ID = 013245;
    private static final long USER_ID = 1;
    private static final long USER_2_ID = 7;
    private static final long UNI_ID = 2;
    private static final long CITY_ID = 3;
    private static final long IMAGE_ID = 4;
    private static final long IMAGE_2_ID = 42345;
    private static final long CAREER_ID = 5;
    private static final long RESPONSE_ID = 6;
    private static final long RATING_ID = 63245;
    private static final String EMAIL = "email";
    private static final String USERNAME = "username";
    private static final String FIRSTNAME = "name";
    private static final String LASTNAME = "name";
    private static final String UNI_NAME = "uni";
    private static final String UNI_ABBR = "abbr";
    private static final String CITY_NAME = "citi";
    private static final String COUNTRY_NAME = "cuntry";
    private static final String CAREER_NAME = "career";
    private static final LocalDate EVENT_DATE = LocalDate.now().plusDays(10);
    private static final LocalDate EVENT_DATE_PAST = LocalDate.now().plusDays(-10);
    private static final byte[] IMAGE_DATA = new byte[]{100, 100};
    private static final Image IMAGE = new Image(IMAGE_ID, IMAGE_DATA);
    private static final String DESCRIPTION = "description";
    private static final String TITLE = "title";
    private static final LocalTime TIME = LocalTime.now();
    private static final LocalDateTime TIMESTAMP = LocalDateTime.now();
    private static final String ADDRESS = "address";
    private static final int LIMIT = 10;
    private static final Locale LOCALE = Locale.of("en");
    private static final boolean BLOCKED = false;
    private static final Career CAREER = new Career(CAREER_ID, CAREER_NAME);
    private static final Country COUNTRY = new Country(COUNTRY_NAME, "ARG");
    private static final City CITY = new City(CITY_NAME, COUNTRY, CITY_ID);
    private static final University UNI = new University(UNI_ID, UNI_NAME, UNI_ABBR, CITY);
    private static final User USER = new User(USER_ID, EMAIL, USERNAME, FIRSTNAME, LASTNAME, UNI, CAREER, IMAGE_ID, LOCALE, BLOCKED, true);
    private static final Event EVENT = new Event(EVENT_ID, USER, EVENT_DATE, DESCRIPTION, IMAGE_ID, CITY, TITLE, TIME, ADDRESS, LIMIT);
    private static final Event EVENT_NO_IMAGE = new Event(EVENT_ID, USER, EVENT_DATE, DESCRIPTION, null, CITY, TITLE, TIME, ADDRESS, LIMIT);
    private static final Event EVENT_NO_LIMIT = new Event(EVENT_ID, USER, EVENT_DATE, DESCRIPTION, IMAGE_ID, CITY, TITLE, TIME, ADDRESS, null);
    private static final Event EVENT_PAST = new Event(EVENT_ID, USER, EVENT_DATE_PAST, DESCRIPTION, IMAGE_ID, CITY, TITLE, TIME, ADDRESS, LIMIT);
    private static final Event EVENT_FULL = new Event(EVENT_ID, USER, EVENT_DATE, DESCRIPTION, IMAGE_ID, CITY, TITLE, TIME, ADDRESS, 0);
    private static final List<User> USERS = List.of(USER);
    private static final List<Event> EVENTS = List.of(EVENT);
    private static final Page<Event> EVENTS_PAGE = new Page<Event>(EVENTS, 1, 1, 1);
    private static final EventResponse REPLY = new EventResponse(RESPONSE_ID, USER, EVENT, DESCRIPTION, TIMESTAMP);
    private static final String INTEREST = "interesting";
    private static final PageParams PAGE_1_DEFAULT = new PageParams(1, 2);
    private static final int STATISTICS_CREATED_EVENTS_COUNT = 2;
    private static final int STATISTICS_ATTENDED_EVENTS_COUNT = 2;
    private static final int STATISTICS_ATTENDEE_COUNTRY_COUNT = 2;
    private static final Long STATISTICS_ATTENDEE_COUNTRY_ID = 7L;
    private static final CountryAttendeeCount COUNTRY_ATTENDEE_COUNT = new CountryAttendeeCount(STATISTICS_ATTENDEE_COUNTRY_ID, COUNTRY_NAME, STATISTICS_ATTENDEE_COUNTRY_COUNT);
    private static final EventWithStatistics EVENT_WITH_STATISTICS = new EventWithStatistics(EVENT, STATISTICS_CREATED_EVENTS_COUNT, STATISTICS_ATTENDED_EVENTS_COUNT, COUNTRY_ATTENDEE_COUNT);
    private static final double RATING_VALUE = 5.0;

    @InjectMocks
    EventServiceImpl eventService;

    @Mock
    EventDao eventDao;
    @Mock
    EventResponseDao replyDao;
    @Mock
    EventRatingDao ratingDao;
    @Mock
    EventAttendanceDao attendanceDao;

    @Mock
    UserService userService;
    @Mock
    EmailService emailService;
    @Mock
    ImageService imageService;
    @Mock
    CityService cityService;

    @Test
    public void testCreateEvent(){
        when(
            cityService.findCityById(eq(CITY_ID))
        ).thenReturn(Optional.of(CITY));
        when(
            userService.findUserById(eq(USER_ID))
        ).thenReturn(Optional.of(USER));
        when(
            eventDao.create(
                eq(USER),
                eq(CITY),
                eq(EVENT_DATE),
                eq(DESCRIPTION),
                eq(null),
                eq(TITLE),
                eq(TIME),
                eq(ADDRESS),
                eq(LIMIT)
            )
        ).thenReturn(EVENT);

        Event event = eventService.createEvent(
            USER_ID,
            CITY_ID,
            EVENT_DATE,
            DESCRIPTION,
            TITLE,
            TIME,
            ADDRESS,
            LIMIT
        );

        assertNotNull(event);
        assertEquals(EVENT, event);
    }
    @Test
    public void testCreateEventWithUserID(){
        when(
            cityService.findCityById(eq(CITY_ID))
        ).thenReturn(Optional.of(CITY));
        when(
            userService.findUserById(eq(USER_ID))
        ).thenReturn(Optional.of(USER));
        when(
            eventDao.create(
                eq(USER),
                eq(CITY),
                eq(EVENT_DATE),
                eq(DESCRIPTION),
                eq(null),
                eq(TITLE),
                eq(TIME),
                eq(ADDRESS),
                eq(LIMIT)
            )
        ).thenReturn(EVENT);

        Event event = eventService.createEvent(
            USER_ID,
            CITY_ID,
            EVENT_DATE,
            DESCRIPTION,
            TITLE,
            TIME,
            ADDRESS,
            LIMIT
        );

        assertNotNull(event);
        assertEquals(EVENT, event);
    }
    @Test(expected = UserNotFoundException.class)
    public void testCreateEventUserNotFound(){
        when(
            userService.findUserById(eq(USER_ID))
        ).thenReturn(Optional.empty());

        eventService.createEvent(
            USER_ID,
            CITY_ID,
            EVENT_DATE,
            DESCRIPTION,
            TITLE,
            TIME,
            ADDRESS,
            LIMIT
        );
    }
    @Test(expected = UserNotFoundException.class)
    public void testCreateEventWithUserIDUserNotFound(){
        when(
            userService.findUserById(eq(USER_ID))
        ).thenReturn(Optional.empty());

        eventService.createEvent(
            USER_ID,
            CITY_ID,
            EVENT_DATE,
            DESCRIPTION,
            TITLE,
            TIME,
            ADDRESS,
            LIMIT
        );
    }
    @Test(expected = InvalidReferenceException.class)
    public void testCreateEventCityNotFound(){
        when(
            cityService.findCityById(eq(CITY_ID))
        ).thenReturn(Optional.empty());
        when(
            userService.findUserById(eq(USER_ID))
        ).thenReturn(Optional.of(USER));

        eventService.createEvent(
            USER_ID,
            CITY_ID,
            EVENT_DATE,
            DESCRIPTION,
            TITLE,
            TIME,
            ADDRESS,
            LIMIT
        );
    }

    @Test
    public void testCreateEventResponse(){
        when(
            eventDao.findById(EVENT_ID)
        ).thenReturn(Optional.of(EVENT));
        when(
            userService.findUserById(USER_ID)
        ).thenReturn(Optional.of(USER));
        when(
            replyDao.findRespondersByEventId(
                eq(EVENT_ID),
                any(PageParams.class)
            )
        ).thenReturn(new Page<>(USERS, 1, 1, 60))
        .thenReturn(new Page<>(USERS, 2, 1, 60));
        when(
            replyDao.create(eq(USER), eq(EVENT), eq(DESCRIPTION))
        ).thenReturn(REPLY);

        EventResponse response = eventService.createEventResponse(USER_ID, EVENT_ID, DESCRIPTION);

        assertNotNull(response);
        assertEquals(EVENT, response.getEvent());
        assertEquals(USER, response.getUser());
        assertEquals(DESCRIPTION, response.getMessage());
    }

    @Test
    public void testCreateEventResponseSendsEmailsAfterCommit(){
        when(eventDao.findById(EVENT_ID)).thenReturn(Optional.of(EVENT));
        when(userService.findUserById(USER_ID)).thenReturn(Optional.of(USER));
        when(replyDao.findRespondersByEventId(eq(EVENT_ID), any(PageParams.class)))
                .thenReturn(new Page<>(USERS, 1, 1, 1));
        when(replyDao.create(eq(USER), eq(EVENT), eq(DESCRIPTION))).thenReturn(REPLY);

        TransactionSynchronizationManager.initSynchronization();
        try {
            eventService.createEventResponse(USER_ID, EVENT_ID, DESCRIPTION);

            verify(emailService, never()).answerEventNotification(any(), eq(DESCRIPTION), any(), any());
            verify(emailService, never()).answerEventOwnerNotification(eq(DESCRIPTION), any(), any());
            TransactionSynchronizationManager.getSynchronizations().forEach(TransactionSynchronization::afterCommit);
            verify(emailService).answerEventNotification(any(), eq(DESCRIPTION), any(), any());
            verify(emailService).answerEventOwnerNotification(eq(DESCRIPTION), any(), any());
        } finally {
            TransactionSynchronizationManager.clearSynchronization();
        }
    }
    @Test
    public void testCreateEventResponseWithUserID(){
        when(
            eventDao.findById(EVENT_ID)
        ).thenReturn(Optional.of(EVENT));
        when(
            userService.findUserById(USER_ID)
        ).thenReturn(Optional.of(USER));
        when(
            replyDao.findRespondersByEventId(
                eq(EVENT_ID),
                any(PageParams.class)
            )
        ).thenReturn(new Page<>(USERS, 1, 2, 2));
        when(
            replyDao.create(eq(USER), eq(EVENT), eq(DESCRIPTION))
        ).thenReturn(REPLY);

        EventResponse response = eventService.createEventResponse(USER_ID, EVENT_ID, DESCRIPTION);

        assertNotNull(response);
        assertEquals(EVENT, response.getEvent());
        assertEquals(USER, response.getUser());
        assertEquals(DESCRIPTION, response.getMessage());
    }
    @Test(expected = UserNotFoundException.class)
    public void testCreateEventResponseWithUserIDMissingUser(){
        when(
            userService.findUserById(USER_ID)
        ).thenReturn(Optional.empty());

        eventService.createEventResponse(USER_ID, EVENT_ID, DESCRIPTION);
    }
    @Test
    public void testCreateEventResponseNoReplies(){
        when(
            eventDao.findById(EVENT_ID)
        ).thenReturn(Optional.of(EVENT));
        when(
            userService.findUserById(USER_ID)
        ).thenReturn(Optional.of(USER));
        when(
            replyDao.findRespondersByEventId(
                eq(EVENT_ID),
                any(PageParams.class)
            )
        ).thenReturn(new Page<User>(List.of(), 1, 1, 0));
        when(
            replyDao.create(eq(USER), eq(EVENT), eq(DESCRIPTION))
        ).thenReturn(REPLY);

        EventResponse response = eventService.createEventResponse(USER_ID, EVENT_ID, DESCRIPTION);

        assertNotNull(response);
        assertEquals(EVENT, response.getEvent());
        assertEquals(USER, response.getUser());
        assertEquals(DESCRIPTION, response.getMessage());
    }
    @Test(expected = UserNotFoundException.class)
    public void testCreateEventResponseNoUser(){
        when(
            userService.findUserById(USER_ID)
        ).thenReturn(Optional.empty());

        eventService.createEventResponse(USER_ID, EVENT_ID, DESCRIPTION);
    }
    @Test(expected = EventNotFoundException.class)
    public void testCreateEventNoEventResponse(){
        when(
            userService.findUserById(USER_ID)
        ).thenReturn(Optional.of(USER));
        when(
            eventDao.findById(EVENT_ID)
        ).thenReturn(Optional.empty());

        eventService.createEventResponse(USER_ID, EVENT_ID, DESCRIPTION);
    }

    @Test
    public void testFindEventById(){
        when(
            eventDao.findById(eq(EVENT_ID))
        ).thenReturn(Optional.of(EVENT));

        Optional<Event> maybeEvent = eventService.findEventById(EVENT_ID);

        assertTrue(maybeEvent.isPresent());
        assertEquals(EVENT_ID, maybeEvent.get().getId().longValue());
    }

    @Test
    public void testFindEventWithStatistics(){
        when(
            eventDao.findById(eq(EVENT_ID))
        ).thenReturn(Optional.of(EVENT));
        when(
            eventDao.countEventsCreatedByUser(eq(USER_ID))
        ).thenReturn(STATISTICS_CREATED_EVENTS_COUNT);
        when(
            attendanceDao.countEventsAttendedByUser(eq(USER_ID))
        ).thenReturn(STATISTICS_ATTENDED_EVENTS_COUNT);
        when(
            eventDao.findTopAttendeeCountry(eq(EVENT_ID))
        ).thenReturn(Optional.of(COUNTRY_ATTENDEE_COUNT));

        Optional<EventWithStatistics> event = eventService.findEventWithStatistics(EVENT_ID);

        assertNotNull(event);
        assertTrue(event.isPresent());
        assertEquals(EVENT_WITH_STATISTICS.getAttendedEventsCount(), event.get().getAttendedEventsCount());
        assertEquals(EVENT_WITH_STATISTICS.getCreatedEventsCount(), event.get().getCreatedEventsCount());
        assertEquals(EVENT_WITH_STATISTICS.getEvent(), event.get().getEvent());
        assertEquals(EVENT_WITH_STATISTICS.getTopAttendeeCountry(), event.get().getTopAttendeeCountry());
    }
    @Test
    public void testFindEventWithStatisticsNoTopCountry(){
        when(
            eventDao.findById(eq(EVENT_ID))
        ).thenReturn(Optional.of(EVENT));
        when(
            eventDao.countEventsCreatedByUser(eq(USER_ID))
        ).thenReturn(STATISTICS_CREATED_EVENTS_COUNT);
        when(
            attendanceDao.countEventsAttendedByUser(eq(USER_ID))
        ).thenReturn(STATISTICS_ATTENDED_EVENTS_COUNT);
        when(
            eventDao.findTopAttendeeCountry(eq(EVENT_ID))
        ).thenReturn(Optional.empty());

        Optional<EventWithStatistics> event = eventService.findEventWithStatistics(EVENT_ID);

        assertNotNull(event);
        assertTrue(event.isPresent());
        assertEquals(EVENT_WITH_STATISTICS.getAttendedEventsCount(), event.get().getAttendedEventsCount());
        assertEquals(EVENT_WITH_STATISTICS.getCreatedEventsCount(), event.get().getCreatedEventsCount());
        assertEquals(EVENT_WITH_STATISTICS.getEvent(), event.get().getEvent());
        assertNull(event.get().getTopAttendeeCountry());
    }
    @Test
    public void testFindEventWithStatisticsEventNotFound(){
        when(
            eventDao.findById(eq(EVENT_ID))
        ).thenReturn(Optional.empty());

        Optional<EventWithStatistics> event = eventService.findEventWithStatistics(EVENT_ID);

        assertNotNull(event);
        assertFalse(event.isPresent());
    }

    @Test()
    public void testCreateEventAttendanceId(){
        when(
            eventDao.findByIdForUpdate(eq(EVENT_ID))
        ).thenReturn(Optional.of(EVENT));
        when(
            userService.findUserById(eq(USER_ID))
        ).thenReturn(Optional.of(USER));
        when(
            attendanceDao.exists(eq(USER), eq(EVENT))
        ).thenReturn(false);
        when(
            attendanceDao.create(eq(USER), eq(EVENT))
        ).thenReturn(new EventAttendance(USER, EVENT));

        EventAttendance eventAttendance = eventService.createEventAttendance(USER_ID, EVENT_ID);

        assertNotNull(eventAttendance);
    }
    @Test()
    public void testCreateEventAttendanceIdNoLimit(){
        when(
            eventDao.findByIdForUpdate(eq(EVENT_ID))
        ).thenReturn(Optional.of(EVENT_NO_LIMIT));
        when(
            userService.findUserById(eq(USER_ID))
        ).thenReturn(Optional.of(USER));
        when(
            attendanceDao.exists(eq(USER), eq(EVENT_NO_LIMIT))
        ).thenReturn(false);
        when(
            attendanceDao.create(eq(USER), eq(EVENT_NO_LIMIT))
        ).thenReturn(new EventAttendance(USER, EVENT_NO_LIMIT));
        
        EventAttendance eventAttendance = eventService.createEventAttendance(USER_ID, EVENT_ID);

        assertNotNull(eventAttendance);
    }
    @Test(expected = EventIsFullException.class)
    public void testCreateEventAttendanceIdLimitExceeded(){
        when(
            eventDao.findByIdForUpdate(eq(EVENT_ID))
        ).thenReturn(Optional.of(EVENT_FULL));
        when(
            userService.findUserById(eq(USER_ID))
        ).thenReturn(Optional.of(USER));
        when(
            attendanceDao.exists(eq(USER), eq(EVENT_FULL))
        ).thenReturn(false);

        eventService.createEventAttendance(USER_ID, EVENT_ID);
    }
    @Test()
    public void testCreateEventAttendanceOneBelowLimit(){
        Event event = mock(Event.class);
        when(event.getIsFuture()).thenReturn(true);
        when(event.getAttendeesLimit()).thenReturn(5);
        when(event.getAttendeesCount()).thenReturn(4);
        when(
            eventDao.findByIdForUpdate(eq(EVENT_ID))
        ).thenReturn(Optional.of(event));
        when(
            userService.findUserById(eq(USER_ID))
        ).thenReturn(Optional.of(USER));
        when(
            attendanceDao.exists(eq(USER), eq(event))
        ).thenReturn(false);
        when(
            attendanceDao.create(eq(USER), eq(event))
        ).thenReturn(new EventAttendance(USER, EVENT));

        EventAttendance eventAttendance = eventService.createEventAttendance(USER_ID, EVENT_ID);

        assertNotNull(eventAttendance);
        verify(attendanceDao).create(eq(USER), eq(event));
    }
    @Test(expected = EventIsFullException.class)
    public void testCreateEventAttendanceAtLimit(){
        Event event = mock(Event.class);
        when(event.getIsFuture()).thenReturn(true);
        when(event.getAttendeesLimit()).thenReturn(5);
        when(event.getAttendeesCount()).thenReturn(5);
        when(
            eventDao.findByIdForUpdate(eq(EVENT_ID))
        ).thenReturn(Optional.of(event));
        when(
            userService.findUserById(eq(USER_ID))
        ).thenReturn(Optional.of(USER));
        when(
            attendanceDao.exists(eq(USER), eq(event))
        ).thenReturn(false);

        eventService.createEventAttendance(USER_ID, EVENT_ID);
    }
    @Test(expected = EventIsFullException.class)
    public void testCreateEventAttendanceAboveLimit(){
        Event event = mock(Event.class);
        when(event.getIsFuture()).thenReturn(true);
        when(event.getAttendeesLimit()).thenReturn(5);
        when(event.getAttendeesCount()).thenReturn(6);
        when(
            eventDao.findByIdForUpdate(eq(EVENT_ID))
        ).thenReturn(Optional.of(event));
        when(
            userService.findUserById(eq(USER_ID))
        ).thenReturn(Optional.of(USER));
        when(
            attendanceDao.exists(eq(USER), eq(event))
        ).thenReturn(false);

        eventService.createEventAttendance(USER_ID, EVENT_ID);
    }
    @Test(expected = UserAlreadyAttendingException.class)
    public void testCreateEventAttendanceAlreadyGoing(){
        when(
            eventDao.findByIdForUpdate(eq(EVENT_ID))
        ).thenReturn(Optional.of(EVENT));
        when(
            userService.findUserById(eq(USER_ID))
        ).thenReturn(Optional.of(USER));
        when(
            attendanceDao.exists(eq(USER), eq(EVENT))
        ).thenReturn(true);

        eventService.createEventAttendance(USER_ID, EVENT_ID);
    }
    @Test(expected = EventNotInTheFutureException.class)
    public void testCreateEventAttendancePast(){
        when(
            eventDao.findByIdForUpdate(eq(EVENT_ID))
        ).thenReturn(Optional.of(EVENT_PAST));
        when(
            userService.findUserById(eq(USER_ID))
        ).thenReturn(Optional.of(USER));

        eventService.createEventAttendance(USER_ID, EVENT_ID);
    }
    @Test(expected = UserNotFoundException.class)
    public void testCreateEventAttendanceUserNotFound(){
        when(
            eventDao.findByIdForUpdate(eq(EVENT_ID))
        ).thenReturn(Optional.of(EVENT));
        when(
            userService.findUserById(eq(USER_ID))
        ).thenReturn(Optional.empty());

        eventService.createEventAttendance(USER_ID, EVENT_ID);
    }
    @Test(expected = EventNotFoundException.class)
    public void testCreateEventAttendanceEventNotFound(){
        when(
            eventDao.findByIdForUpdate(eq(EVENT_ID))
        ).thenReturn(Optional.empty());

        eventService.createEventAttendance(USER_ID, EVENT_ID);
    }

    @Test
    public void testFindEventAttendance(){
        when(
            attendanceDao.findById(eq(USER_ID), eq(EVENT_ID))
        ).thenReturn(Optional.empty());

        Optional<EventAttendance> maybeAttendance = eventService.findEventAttendance(USER_ID, EVENT_ID);

        assertNotNull(maybeAttendance);
    }

    @Test(expected = EventAttendanceNotFoundException.class)
    public void testDeleteEventAttendanceNotAttending(){
        when(
            eventDao.findById(eq(EVENT_ID))
        ).thenReturn(Optional.of(EVENT));
        when(
            attendanceDao.exists(eq(USER_ID), eq(EVENT_ID))
        ).thenReturn(false);

        eventService.deleteEventAttendance(USER_ID, EVENT_ID);
    }
    @Test(expected = EventNotInTheFutureException.class)
    public void testDeleteEventAttendanceIdPast(){
        when(
            eventDao.findById(eq(EVENT_ID))
        ).thenReturn(Optional.of(EVENT_PAST));

        eventService.deleteEventAttendance(USER_ID, EVENT_ID);
    }
    @Test(expected = EventNotFoundException.class)
    public void testDeleteEventAttendanceIdEventNotFound(){
        when(
            eventDao.findById(eq(EVENT_ID))
        ).thenReturn(Optional.empty());

        eventService.deleteEventAttendance(USER_ID, EVENT_ID);
    }

    @Test
    public void testRateEvent(){
        when(
            userService.findUserById(eq(USER_ID))
        ).thenReturn(Optional.of(USER));
        when(
            eventDao.findById(eq(EVENT_ID))
        ).thenReturn(Optional.of(EVENT_PAST));
        when(
            attendanceDao.exists(eq(USER_ID), eq(EVENT_ID))
        ).thenReturn(true);
        when(
            ratingDao.rateEvent(USER, EVENT_PAST, RATING_VALUE)
        ).thenReturn(new Rating(USER, EVENT_PAST, RATING_VALUE));

        Rating rating = eventService.rateEvent(USER_ID, EVENT_ID, RATING_VALUE);

        assertNotNull(rating);
    }
    @Test(expected = EventNotOccurredException.class)
    public void testRateEventFutureEvent(){
        when(
            userService.findUserById(eq(USER_ID))
        ).thenReturn(Optional.of(USER));
        when(
            eventDao.findById(eq(EVENT_ID))
        ).thenReturn(Optional.of(EVENT));

        eventService.rateEvent(USER_ID, EVENT_ID, RATING_VALUE);
    }
    @Test(expected = EventAttendanceRequiredException.class)
    public void testRateEventUserNotAttendee(){
        when(
            userService.findUserById(eq(USER_ID))
        ).thenReturn(Optional.of(USER));
        when(
            eventDao.findById(eq(EVENT_ID))
        ).thenReturn(Optional.of(EVENT_PAST));
        when(
            attendanceDao.exists(eq(USER_ID), eq(EVENT_ID))
        ).thenReturn(false);

        eventService.rateEvent(USER_ID, EVENT_ID, RATING_VALUE);
    }
    @Test(expected = EventNotFoundException.class)
    public void testRateEventMissing(){
        when(
            userService.findUserById(eq(USER_ID))
        ).thenReturn(Optional.of(USER));
        when(
            eventDao.findById(eq(EVENT_ID))
        ).thenReturn(Optional.empty());

        eventService.rateEvent(USER_ID, EVENT_ID, RATING_VALUE);
    }

    @Test
    public void testRateEventUserId(){
        when(
            userService.findUserById(eq(USER_ID))
        ).thenReturn(Optional.of(USER));
        when(
            eventDao.findById(eq(EVENT_ID))
        ).thenReturn(Optional.of(EVENT_PAST));
        when(
            attendanceDao.exists(eq(USER_ID), eq(EVENT_ID))
        ).thenReturn(true);
        when(
            ratingDao.rateEvent(USER, EVENT_PAST, RATING_VALUE)
        ).thenReturn(new Rating(USER, EVENT_PAST, RATING_VALUE));

        Rating rating = eventService.rateEvent(USER_ID, EVENT_ID, RATING_VALUE);

        assertNotNull(rating);
    }
    @Test(expected = UserNotFoundException.class)
    public void testRateEventUserIdMissing(){
        when(
            userService.findUserById(eq(USER_ID))
        ).thenReturn(Optional.empty());

        eventService.rateEvent(USER_ID, EVENT_ID, RATING_VALUE);
    }

    @Test
    public void testUpdateEventRating(){
        Rating newRating = new Rating(USER, EVENT, 2);
        when(
            ratingDao.findById(eq(RATING_ID))
        ).thenReturn(Optional.of(newRating));

        eventService.updateEventRating(EVENT_ID, RATING_ID, RATING_VALUE);

        assertEquals(RATING_VALUE, newRating.getRating(), 0.1);
    }
    @Test(expected = RatingNotFoundException.class)
    public void testUpdateEventRatingNotFound(){
        when(
            ratingDao.findById(eq(RATING_ID))
        ).thenReturn(Optional.empty());

        eventService.updateEventRating(EVENT_ID, RATING_ID, RATING_VALUE);
    }
    @Test(expected = RatingNotFoundException.class)
    public void testUpdateEventRatingWrongEvent(){
        Rating newRating = new Rating(USER, EVENT, 2);
        when(
            ratingDao.findById(eq(RATING_ID))
        ).thenReturn(Optional.of(newRating));

        // RATING_ID belongs to EVENT (id EVENT_ID); asking for it under EVENT_2_ID must not honor the URN.
        eventService.updateEventRating(EVENT_2_ID, RATING_ID, RATING_VALUE);
    }

    @Test
    public void testFindRatingById(){
        when(
            ratingDao.findById(eq(RATING_ID))
        ).thenReturn(Optional.of(new Rating(USER, EVENT, RATING_VALUE)));

        Optional<Rating> maybeRating = eventService.findRatingById(EVENT_ID, RATING_ID);

        assertNotNull(maybeRating);
        assertTrue(maybeRating.isPresent());
    }
    @Test
    public void testFindRatingByIdDifferentEvent(){
        when(
            ratingDao.findById(eq(RATING_ID))
        ).thenReturn(Optional.of(new Rating(USER, EVENT, RATING_VALUE)));

        Optional<Rating> maybeRating = eventService.findRatingById(EVENT_2_ID, RATING_ID);

        assertNotNull(maybeRating);
        assertTrue(maybeRating.isEmpty());
    }
    @Test
    public void testFindRatingByIdMissingRating(){
        when(
            ratingDao.findById(eq(RATING_ID))
        ).thenReturn(Optional.empty());

        Optional<Rating> maybeRating = eventService.findRatingById(EVENT_ID, RATING_ID);

        assertNotNull(maybeRating);
        assertTrue(maybeRating.isEmpty());
    }

    @Test
    public void testFindRatingsByEventId(){
        when(
            eventDao.findById(eq(EVENT_ID))
        ).thenReturn(Optional.of(EVENT));
        when(
            ratingDao.findByEventId(eq(EVENT_ID), eq(PAGE_1_DEFAULT))
        ).thenReturn(new Page<>(List.of(), 1, 1, 1));

        Page<Rating> ratings = eventService.findRatingsByEventId(EVENT_ID, PAGE_1_DEFAULT);

        assertNotNull(ratings);
    }
    @Test(expected = EventNotFoundException.class)
    public void testFindRatingsByEventIdNotFound(){
        when(
            eventDao.findById(eq(EVENT_ID))
        ).thenReturn(Optional.empty());

        eventService.findRatingsByEventId(EVENT_ID, PAGE_1_DEFAULT);
    }

    @Test(expected=RatingNotFoundException.class)
    public void testDeleteRatingMissing(){
        when(
            ratingDao.findById(eq(RATING_ID))
        ).thenReturn(Optional.empty());

        eventService.deleteRating(EVENT_ID, RATING_ID);
    }
    @Test
    public void testDeleteRating(){
        Rating rating = new Rating(USER, EVENT, 2);
        when(
            ratingDao.findById(eq(RATING_ID))
        ).thenReturn(Optional.of(rating));

        eventService.deleteRating(EVENT_ID, RATING_ID);

        verify(ratingDao).delete(RATING_ID);
    }

    @Test
    public void testCountRatingsByEvent(){
        when(
            ratingDao.countRatingsByEvent(eq(EVENT_ID))
        ).thenReturn(1);

        int count = eventService.countRatingsByEvent(EVENT_ID);

        assertEquals(1, count);
    }

    private Page<Event> findRecommendedEvents(final long userId, final PageParams pageParams) {
        return eventService.searchEventsWithFilters(
            null,
            userId,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            pageParams
        );
    }

    @Test
    public void testFindRecommendedEvents(){
        when(
            eventDao.findRecommended(eq(USER_ID), eq(PAGE_1_DEFAULT))
        ).thenReturn(EVENTS_PAGE);

        Page<Event> userEvents = findRecommendedEvents(USER_ID, PAGE_1_DEFAULT);

        assertNotNull(userEvents);
        assertEquals(EVENTS_PAGE, userEvents);
    }

    @Test
    public void testFindRecommendedEventsFallsBackToTopEventsOfUser(){
        when(
            eventDao.findRecommended(eq(USER_ID), eq(PAGE_1_DEFAULT))
        ).thenReturn(new Page<>(List.of(), 1, 1, 0));
        when(
            eventDao.findTopByUser(eq(USER_ID), eq(PAGE_1_DEFAULT))
        ).thenReturn(EVENTS_PAGE);

        Page<Event> userEvents = findRecommendedEvents(USER_ID, PAGE_1_DEFAULT);

        assertNotNull(userEvents);
        assertEquals(EVENTS_PAGE, userEvents);
    }

    @Test
    public void testFindRecommendedEventsFallsBackToAllEvents(){
        when(
            eventDao.findRecommended(eq(USER_ID), eq(PAGE_1_DEFAULT))
        ).thenReturn(new Page<>(List.of(), 1, 1, 0));
        when(
            eventDao.findTopByUser(eq(USER_ID), eq(PAGE_1_DEFAULT))
        ).thenReturn(new Page<>(List.of(), 1, 1, 0));
        when(
            eventDao.findAll(eq(PAGE_1_DEFAULT))
        ).thenReturn(EVENTS_PAGE);

        Page<Event> userEvents = findRecommendedEvents(USER_ID, PAGE_1_DEFAULT);

        assertNotNull(userEvents);
        assertEquals(EVENTS_PAGE, userEvents);
    }

    @Test
    public void testFindRecommendedEventsDoesNotFallBackWhenPageIsEmptyButCollectionIsNot(){
        when(
            eventDao.findRecommended(eq(USER_ID), eq(PAGE_1_DEFAULT))
        ).thenReturn(new Page<>(List.of(), 2, 1, 1));

        Page<Event> userEvents = findRecommendedEvents(USER_ID, PAGE_1_DEFAULT);

        assertNotNull(userEvents);
        assertTrue(userEvents.getContent().isEmpty());
        verify(eventDao, never()).findTopByUser(anyLong(), any(PageParams.class));
    }

    @Test
    public void testIsEventOwnedByUser(){
        when(
            eventDao.findById(eq(EVENT_ID))
        ).thenReturn(Optional.of(EVENT));

        boolean isOwned = eventService.isEventOwnedByUser(EMAIL, EVENT_ID);

        assertTrue(isOwned);
    }
    @Test
    public void testIsEventOwnedByUserNotOwned(){
        when(
            eventDao.findById(eq(EVENT_ID))
        ).thenReturn(Optional.of(EVENT));

        boolean isOwned = eventService.isEventOwnedByUser("EMAIL", EVENT_ID);

        assertFalse(isOwned);
    }
    @Test
    public void testIsEventOwnedByUserMissingEvent(){
        when(
            eventDao.findById(eq(EVENT_ID))
        ).thenReturn(Optional.empty());

        boolean isOwned = eventService.isEventOwnedByUser(EMAIL, EVENT_ID);

        assertFalse(isOwned);
    }

    @Test
    public void testIsUserEventAttendee(){
        when(attendanceDao.exists(USER_ID, EVENT_ID)).thenReturn(true);

        boolean isAttending = eventService.isUserEventAttendee(USER_ID, EVENT_ID);

        assertTrue(isAttending);
    }

    @Test
    public void testIsRatingOwnedByUser(){
        when(
            ratingDao.findById(eq(RATING_ID))
        ).thenReturn(Optional.of(new Rating(USER, EVENT, RATING_VALUE)));

        boolean isOwner = eventService.isRatingOwnedByUser(EVENT_ID, RATING_ID, USER_ID);

        assertTrue(isOwner);
    }
    @Test
    public void testIsRatingOwnedByUserNotOwner(){
        when(
            ratingDao.findById(eq(RATING_ID))
        ).thenReturn(Optional.of(new Rating(USER, EVENT, RATING_VALUE)));

        boolean isOwner = eventService.isRatingOwnedByUser(EVENT_ID, RATING_ID, USER_2_ID);

        assertFalse(isOwner);
    }
    @Test
    public void testIsRatingOwnedByUserNoRating(){
        when(
            ratingDao.findById(eq(RATING_ID))
        ).thenReturn(Optional.empty());

        boolean isOwner = eventService.isRatingOwnedByUser(EVENT_ID, RATING_ID, USER_ID);

        assertFalse(isOwner);
    }

    @Test
    public void testSearchEventsWithFiltersNoUser(){
        when(
            eventDao.findAllWithFilters(
                any(),
                any(),
                any(SortFieldEvent.class),
                any(SortDirection.class),
                any(),
                any(LocalDate.class),
                any(LocalDate.class),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                eq(PAGE_1_DEFAULT)
            )
        ).thenReturn(EVENTS_PAGE);

        Page<Event> page = eventService.searchEventsWithFilters(
            TITLE,
            null,
            null,
            SortFieldEvent.from("attendees"),
            SortDirection.from("desc"),
            CITY_NAME,
            EVENT_DATE,
            EVENT_DATE,
            INTEREST,
            null,
            UNI_NAME,
            (int)RATING_VALUE,
            true,
            null,
            PAGE_1_DEFAULT
        );

        assertNotNull(page);
        assertEquals(EVENTS_PAGE, page);
    }

    @Test
    public void testSearchEventsWithFiltersUsesDefaultSortWhenNotProvided(){
        when(
            eventDao.findAllWithFilters(
                any(),
                any(),
                eq(SortFieldEvent.DATE),
                eq(SortDirection.ASC),
                any(),
                any(LocalDate.class),
                any(LocalDate.class),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                eq(PAGE_1_DEFAULT)
            )
        ).thenReturn(EVENTS_PAGE);

        Page<Event> page = eventService.searchEventsWithFilters(
            TITLE,
            null,
            USER_ID,
            null,
            null,
            CITY_NAME,
            EVENT_DATE,
            EVENT_DATE,
            INTEREST,
            null,
            UNI_NAME,
            (int) RATING_VALUE,
            true,
            null,
            PAGE_1_DEFAULT
        );

        assertNotNull(page);
        assertEquals(EVENTS_PAGE, page);
    }

    @Test
    public void testSearchRecommendedEvents(){
        when(
            eventDao.findRecommended(eq(USER_ID), eq(PAGE_1_DEFAULT))
        ).thenReturn(EVENTS_PAGE);

        Page<Event> page = eventService.searchEventsWithFilters(
            null,
            USER_ID,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            PAGE_1_DEFAULT
        );

        assertNotNull(page);
        assertEquals(EVENTS_PAGE, page);
    }

    @Test(expected = MutuallyExclusiveFiltersException.class)
    public void testSearchRecommendedEventsWithExclusiveFilter(){
        eventService.searchEventsWithFilters(
            null,
            USER_ID,
            null,
            SortFieldEvent.DATE,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            PAGE_1_DEFAULT
        );
    }

    @Test
    public void testSearchTopEvents(){
        when(
            eventDao.findTop(eq(PAGE_1_DEFAULT))
        ).thenReturn(EVENTS_PAGE);

        Page<Event> page = eventService.searchEventsWithFilters(
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            true,
            PAGE_1_DEFAULT
        );

        assertNotNull(page);
        assertEquals(EVENTS_PAGE, page);
        verify(eventDao, times(1)).findTop(PAGE_1_DEFAULT);
    }

    @Test(expected = MutuallyExclusiveFiltersException.class)
    public void testSearchTopEventsWithExclusiveFilter(){
        eventService.searchEventsWithFilters(
            TITLE,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            true,
            PAGE_1_DEFAULT
        );
    }

    @Test
    public void testSearchEventsWithFilters(){
        when(
            eventDao.findAllWithFilters(
                any(),
                any(),
                any(SortFieldEvent.class),
                any(SortDirection.class),
                any(),
                any(LocalDate.class),
                any(LocalDate.class),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                eq(PAGE_1_DEFAULT)
            )
        ).thenReturn(EVENTS_PAGE);

        Page<Event> page = eventService.searchEventsWithFilters(
            TITLE,
            null,
            USER_ID,
            SortFieldEvent.from(""),
            SortDirection.from(""),
            CITY_NAME,
            EVENT_DATE,
            EVENT_DATE,
            INTEREST,
            null,
            UNI_NAME,
            (int)RATING_VALUE,
            true,
            null,
            PAGE_1_DEFAULT
        );

        assertNotNull(page);
        assertEquals(EVENTS_PAGE, page);
    }
    @Test
    public void testSearchEventsWithFiltersNotUpcomingPast(){
        when(
            eventDao.findAllWithFilters(
                any(),
                any(),
                any(SortFieldEvent.class),
                any(SortDirection.class),
                any(),
                any(LocalDate.class),
                any(LocalDate.class),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                eq(PAGE_1_DEFAULT)
            )
        ).thenReturn(EVENTS_PAGE);

        Page<Event> page = eventService.searchEventsWithFilters(
            TITLE,
            null,
            USER_ID,
            SortFieldEvent.from(""),
            SortDirection.from(""),
            CITY_NAME,
            EVENT_DATE,
            EVENT_DATE,
            INTEREST,
            null,
            UNI_NAME,
            (int)RATING_VALUE,
            true,
            null,
            PAGE_1_DEFAULT
        );

        assertNotNull(page);
        assertEquals(EVENTS_PAGE, page);
    }
    @Test
    public void testSearchEventsWithFiltersNotUpcomingPastDateChanges(){
        when(
            eventDao.findAllWithFilters(
                any(),
                any(),
                any(SortFieldEvent.class),
                any(SortDirection.class),
                any(),
                any(LocalDate.class),
                any(LocalDate.class),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                eq(PAGE_1_DEFAULT)
            )
        ).thenReturn(EVENTS_PAGE);

        Page<Event> page = eventService.searchEventsWithFilters(
            TITLE,
            null,
            USER_ID,
            SortFieldEvent.from(""),
            SortDirection.from(""),
            CITY_NAME,
            LocalDate.now().plusDays(-10),
            LocalDate.now(),
            INTEREST,
            null,
            UNI_NAME,
            (int)RATING_VALUE,
            true,
            null,
            PAGE_1_DEFAULT
        );

        assertNotNull(page);
        assertEquals(EVENTS_PAGE, page);
    }
    @Test
    public void testSearchEventsWithFiltersNotUpcomingPastNoDateChanges(){
        when(
            eventDao.findAllWithFilters(
                any(),
                any(),
                any(SortFieldEvent.class),
                any(SortDirection.class),
                any(),
                any(LocalDate.class),
                any(LocalDate.class),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                eq(PAGE_1_DEFAULT)
            )
        ).thenReturn(EVENTS_PAGE);

        Page<Event> page = eventService.searchEventsWithFilters(
            TITLE,
            null,
            USER_ID,
            SortFieldEvent.from(""),
            SortDirection.from(""),
            CITY_NAME,
            LocalDate.now().plusDays(10),
            LocalDate.now().plusDays(-10),
            INTEREST,
            null,
            UNI_NAME,
            (int)RATING_VALUE,
            true,
            null,
            PAGE_1_DEFAULT
        );

        assertNotNull(page);
        assertEquals(EVENTS_PAGE, page);
    }
    @Test
    public void testSearchEventsWithFiltersNotUpcomingPastNoDates(){
        when(
            eventDao.findAllWithFilters(
                any(),
                any(),
                any(SortFieldEvent.class),
                any(SortDirection.class),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                eq(PAGE_1_DEFAULT)
            )
        ).thenReturn(EVENTS_PAGE);

        Page<Event> page = eventService.searchEventsWithFilters(
            TITLE,
            null,
            USER_ID,
            SortFieldEvent.from(""),
            SortDirection.from(""),
            CITY_NAME,
            null,
            null,
            INTEREST,
            null,
            UNI_NAME,
            (int)RATING_VALUE,
            true,
            null,
            PAGE_1_DEFAULT
        );

        assertNotNull(page);
        assertEquals(EVENTS_PAGE, page);
    }

    @Test
    public void testUpdateEvent(){
        Event newEvent = new Event(
            EVENT_ID,
            USER,
            EVENT_DATE,
            DESCRIPTION,
            IMAGE_ID,
            CITY,
            TITLE,
            TIME,
            ADDRESS,
            null
        );
        when(
            eventDao.findByIdForUpdate(eq(EVENT_ID))
        ).thenReturn(Optional.of(newEvent));
        when(
            cityService.findCityById(eq(CITY_ID))
        ).thenReturn(Optional.of(new City("CITY_NAME", COUNTRY)));

        Event event = eventService.patchEvent(
            EVENT_ID,
            CITY_ID,
            EVENT_DATE.plusDays(1),
            "DESCRIPTION",
            "TITLE",
            TIME.plusSeconds(10),
            "ADDRESS",
            null,
            null,
            null
        );

        assertEquals("CITY_NAME", event.getCity().getName());
        assertEquals(EVENT_DATE.plusDays(1), event.getDate());
        assertEquals(TIME.plusSeconds(10), event.getTime());
        assertEquals("DESCRIPTION", event.getDescription());
        assertEquals("TITLE", event.getTitle());
        assertEquals("ADDRESS", event.getAddress());
        assertNull(event.getAttendeesLimit());
    }
    @Test(expected = AttendeesLimitBelowCurrentException.class)
    public void testUpdateEventAttendeesLimitBelowCurrent(){
        Event fullEvent = mock(Event.class);
        when(fullEvent.getAttendeesCount()).thenReturn(5);
        when(
            eventDao.findByIdForUpdate(eq(EVENT_ID))
        ).thenReturn(Optional.of(fullEvent));
        when(
            cityService.findCityById(eq(CITY_ID))
        ).thenReturn(Optional.of(CITY));

        eventService.patchEvent(
            EVENT_ID,
            CITY_ID,
            EVENT_DATE,
            DESCRIPTION,
            TITLE,
            TIME,
            ADDRESS,
            1,
            null,
            null
        );
    }
    @Test(expected = InvalidReferenceException.class)
    public void testUpdateEventMissingCity(){
        when(
            eventDao.findByIdForUpdate(eq(EVENT_ID))
        ).thenReturn(Optional.of(EVENT));
        when(
            cityService.findCityById(eq(CITY_ID))
        ).thenReturn(Optional.empty());

        eventService.patchEvent(
            EVENT_ID,
            CITY_ID,
            EVENT_DATE,
            DESCRIPTION,
            TITLE,
            TIME,
            ADDRESS,
            LIMIT,
            null,
            null
        );
    }
    @Test(expected = EventNotFoundException.class)
    public void testUpdateEventMissingEvent(){
        when(
            eventDao.findByIdForUpdate(eq(EVENT_ID))
        ).thenReturn(Optional.empty());

        eventService.patchEvent(
            EVENT_ID,
            CITY_ID,
            EVENT_DATE,
            DESCRIPTION,
            TITLE,
            TIME,
            ADDRESS,
            LIMIT,
            null,
            null
        );
    }

    @Test
    public void testDeleteEvent(){
        Event newEvent = new Event(USER, EVENT_DATE, DESCRIPTION, IMAGE_ID, CITY, TITLE, TIME, ADDRESS, null);
        when(
            eventDao.findById(eq(EVENT_ID))
        ).thenReturn(Optional.of(newEvent));

        eventService.deleteEvent(EVENT_ID, DESCRIPTION);

        assertTrue(newEvent.isDeleted());
        assertEquals(DESCRIPTION, newEvent.getDeletionMessage());
    }

    @Test
    public void testDeleteEventSendsEmailAfterCommit(){
        Event newEvent = new Event(USER, EVENT_DATE, DESCRIPTION, IMAGE_ID, CITY, TITLE, TIME, ADDRESS, null);
        when(eventDao.findById(eq(EVENT_ID))).thenReturn(Optional.of(newEvent));

        TransactionSynchronizationManager.initSynchronization();
        try {
            eventService.deleteEvent(EVENT_ID, DESCRIPTION);

            verify(emailService, never()).sendEventDeletionNotification(any(), eq(DESCRIPTION));
            TransactionSynchronizationManager.getSynchronizations().forEach(TransactionSynchronization::afterCommit);
            verify(emailService).sendEventDeletionNotification(any(), eq(DESCRIPTION));
        } finally {
            TransactionSynchronizationManager.clearSynchronization();
        }
    }
    @Test
    public void testPatchEventDeletedTrue(){
        Event newEvent = new Event(USER, EVENT_DATE, DESCRIPTION, IMAGE_ID, CITY, TITLE, TIME, ADDRESS, null);
        when(
            eventDao.findByIdForUpdate(eq(EVENT_ID))
        ).thenReturn(Optional.of(newEvent));
        when(
            eventDao.findById(eq(EVENT_ID))
        ).thenReturn(Optional.of(newEvent));

        eventService.patchEvent(EVENT_ID, null, null, null, null, null, null, null, true, DESCRIPTION);

        assertTrue(newEvent.isDeleted());
        assertEquals(DESCRIPTION, newEvent.getDeletionMessage());
    }
    @Test
    public void testPatchEventDeletedFalseIsNoOp(){
        Event newEvent = new Event(USER, EVENT_DATE, DESCRIPTION, IMAGE_ID, CITY, TITLE, TIME, ADDRESS, null);
        when(
            eventDao.findByIdForUpdate(eq(EVENT_ID))
        ).thenReturn(Optional.of(newEvent));

        eventService.patchEvent(EVENT_ID, null, null, null, null, null, null, null, false, DESCRIPTION);

        assertFalse(newEvent.isDeleted());
    }
    @Test
    public void testPatchEventDeletedNullIsNoOp(){
        Event newEvent = new Event(USER, EVENT_DATE, DESCRIPTION, IMAGE_ID, CITY, TITLE, TIME, ADDRESS, null);
        when(
            eventDao.findByIdForUpdate(eq(EVENT_ID))
        ).thenReturn(Optional.of(newEvent));

        eventService.patchEvent(EVENT_ID, null, null, null, null, null, null, null, null, null);

        assertFalse(newEvent.isDeleted());
    }
    @Test
    public void testDeleteEventEmptyMessage(){
        Event newEvent = new Event(USER, EVENT_DATE, DESCRIPTION, IMAGE_ID, CITY, TITLE, TIME, ADDRESS, null);
        when(
            eventDao.findById(eq(EVENT_ID))
        ).thenReturn(Optional.of(newEvent));

        eventService.deleteEvent(EVENT_ID, "");

        assertTrue(newEvent.isDeleted());
        assertNull(newEvent.getDeletionMessage());
    }
    @Test
    public void testDeleteEventMissingMessage(){
        Event newEvent = new Event(USER, EVENT_DATE, DESCRIPTION, IMAGE_ID, CITY, TITLE, TIME, ADDRESS, null);
        when(
            eventDao.findById(eq(EVENT_ID))
        ).thenReturn(Optional.of(newEvent));

        eventService.deleteEvent(EVENT_ID, null);

        assertTrue(newEvent.isDeleted());
        assertNull(newEvent.getDeletionMessage());
    }
    @Test
    public void testDeleteEventMissing(){
        when(
            eventDao.findById(eq(EVENT_ID))
        ).thenReturn(Optional.empty());

        eventService.deleteEvent(EVENT_ID, DESCRIPTION);

        assertFalse(EVENT.isDeleted());
    }

    @Test
    public void testDeleteEventResponseSendsEmailAfterCommit(){
        EventResponse resp = new EventResponse(USER, EVENT, ADDRESS);
        when(
            replyDao.findById(eq(RESPONSE_ID))
        ).thenReturn(Optional.of(resp));

        TransactionSynchronizationManager.initSynchronization();
        try {
            eventService.deleteEventResponse(EVENT_ID, RESPONSE_ID, DESCRIPTION);

            verify(emailService, never()).sendEventCommentDeletionNotification(eq(resp), any(), any(), eq(DESCRIPTION));
            TransactionSynchronizationManager.getSynchronizations().forEach(TransactionSynchronization::afterCommit);
            verify(emailService).sendEventCommentDeletionNotification(eq(resp), any(), any(), eq(DESCRIPTION));
        } finally {
            TransactionSynchronizationManager.clearSynchronization();
        }
    }

    @Test
    public void testDeleteEventResponseID(){
        EventResponse resp = new EventResponse(USER, EVENT, ADDRESS);
        when(
            replyDao.findById(eq(RESPONSE_ID))
        ).thenReturn(Optional.of(resp));

        eventService.deleteEventResponse(EVENT_ID, RESPONSE_ID, DESCRIPTION);

        assertTrue(resp.isDeleted());
        assertEquals(DESCRIPTION, resp.getDeletionMessage());
    }
    @Test(expected = EventResponseNotFoundException.class)
    public void testDeleteEventResponseIDMissing(){
        when(
            replyDao.findById(eq(RESPONSE_ID))
        ).thenReturn(Optional.empty());

        eventService.deleteEventResponse(EVENT_ID, RESPONSE_ID, DESCRIPTION);
    }
    @Test
    public void testPatchEventResponseDeletedTrue(){
        EventResponse resp = new EventResponse(USER, EVENT, ADDRESS);
        when(
            replyDao.findById(eq(RESPONSE_ID))
        ).thenReturn(Optional.of(resp));

        eventService.patchEventResponse(EVENT_ID, RESPONSE_ID, true, DESCRIPTION);

        assertTrue(resp.isDeleted());
        assertEquals(DESCRIPTION, resp.getDeletionMessage());
    }
    @Test
    public void testPatchEventResponseDeletedFalseIsNoOp(){
        eventService.patchEventResponse(EVENT_ID, RESPONSE_ID, false, DESCRIPTION);

        verify(replyDao, never()).findById(RESPONSE_ID);
    }
    @Test
    public void testPatchEventResponseDeletedNullIsNoOp(){
        eventService.patchEventResponse(EVENT_ID, RESPONSE_ID, null, null);

        verify(replyDao, never()).findById(RESPONSE_ID);
    }

    @Test
    public void testFindEventResponses(){
        when(
            replyDao.listAllByEventId(eq(EVENT_ID), eq(PAGE_1_DEFAULT))
        ).thenReturn(new Page<>(List.of(), 1, 1, 1));

        Page<EventResponse> replies = eventService.findEventResponses(EVENT_ID, PAGE_1_DEFAULT);

        assertNotNull(replies);
    }

    @Test
    public void testFindEventResponseByIdOnly(){
        when(
            replyDao.findById(eq(RESPONSE_ID))
        ).thenReturn(Optional.of(new EventResponse(USER, EVENT, DESCRIPTION)));

        Optional<EventResponse> maybeReply = eventService.findEventResponseById(RESPONSE_ID);

        assertNotNull(maybeReply);
        assertTrue(maybeReply.isPresent());
    }

    @Test
    public void testFindEventResponseById(){
        when(
            replyDao.findById(eq(RESPONSE_ID))
        ).thenReturn(Optional.of(new EventResponse(USER, EVENT, DESCRIPTION)));

        Optional<EventResponse> maybeReply = eventService.findEventResponseById(EVENT_ID, RESPONSE_ID);

        assertNotNull(maybeReply);
        assertTrue(maybeReply.isPresent());
    }
    @Test
    public void testIsEventResponseOwnedByUser(){
        when(
            replyDao.findById(eq(RESPONSE_ID))
        ).thenReturn(Optional.of(REPLY));

        assertTrue(eventService.isEventResponseOwnedByUser(EVENT_ID, RESPONSE_ID, USER_ID));
    }
    @Test
    public void testIsEventResponseOwnedByUserOtherUser(){
        when(
            replyDao.findById(eq(RESPONSE_ID))
        ).thenReturn(Optional.of(REPLY));

        assertFalse(eventService.isEventResponseOwnedByUser(EVENT_ID, RESPONSE_ID, USER_ID + 1));
    }
    @Test
    public void testFindEventResponseByIdDifferentEvent(){
        when(
            replyDao.findById(eq(RESPONSE_ID))
        ).thenReturn(Optional.of(new EventResponse(USER, EVENT, DESCRIPTION)));

        Optional<EventResponse> maybeReply = eventService.findEventResponseById(EVENT_2_ID, RESPONSE_ID);

        assertNotNull(maybeReply);
        assertTrue(maybeReply.isEmpty());
    }
    @Test
    public void testFindEventResponseByIdMissing(){
        when(
            replyDao.findById(eq(RESPONSE_ID))
        ).thenReturn(Optional.empty());

        Optional<EventResponse> maybeReply = eventService.findEventResponseById(EVENT_2_ID, RESPONSE_ID);

        assertNotNull(maybeReply);
        assertTrue(maybeReply.isEmpty());
    }

    @Test
    public void testFindEventAttendances(){
        when(
            attendanceDao.findByEventId(eq(EVENT_ID), eq(PAGE_1_DEFAULT))
        ).thenReturn(new Page<>(List.of(), 1,1 ,1));

        Page<EventAttendance> attendance = eventService.findEventAttendances(EVENT_ID, PAGE_1_DEFAULT);

        assertNotNull(attendance);
    }

    @Test
    public void testCountEventsCreatedByUser(){
        when(
            eventDao.countEventsCreatedByUser(eq(USER_ID))
        ).thenReturn(1);

        int events = eventService.countEventsCreatedByUser(USER_ID);

        assertEquals(1, events);
    }

    @Test
    public void testCountEventsAttendedByUser(){
        when(
            attendanceDao.countEventsAttendedByUser(eq(USER_ID))
        ).thenReturn(1);

        int events = eventService.countEventsAttendedByUser(USER_ID);

        assertEquals(1, events);
    }

    @Test
    public void testSendEventRemindersPaginatesEvents(){
        when(
            eventDao.findAllBetweenDates(any(LocalDate.class), any(LocalDate.class), any(PageParams.class))
        ).thenReturn(new Page<>(EVENTS, 1, 1, 2))
        .thenReturn(new Page<>(EVENTS, 2, 1, 2));
        when(
            attendanceDao.findAttendeesByEventId(eq(EVENT_ID), any(PageParams.class))
        ).thenReturn(new Page<>(USERS, 1, 1, 1));

        eventService.sendEventReminders();

        verify(eventDao, times(2)).findAllBetweenDates(any(LocalDate.class), any(LocalDate.class), any(PageParams.class));
        verify(emailService, times(2)).sendEventReminderNotification(any(), any());
    }
    @Test
    public void testSendEventRemindersPaginatesAttendees(){
        when(
            eventDao.findAllBetweenDates(any(LocalDate.class), any(LocalDate.class), any(PageParams.class))
        ).thenReturn(new Page<>(EVENTS, 1, 1, 1));
        when(
            attendanceDao.findAttendeesByEventId(eq(EVENT_ID), any(PageParams.class))
        ).thenReturn(new Page<>(USERS, 1, 1, 2))
        .thenReturn(new Page<>(USERS, 2, 1, 2));

        eventService.sendEventReminders();

        verify(emailService, times(2)).sendEventReminderNotification(any(), any());
    }
    @Test
    public void testSendEventRemindersNoAttendees(){
        when(
            eventDao.findAllBetweenDates(any(LocalDate.class), any(LocalDate.class), any(PageParams.class))
        ).thenReturn(new Page<>(EVENTS, 1, 1, 1));
        when(
            attendanceDao.findAttendeesByEventId(eq(EVENT_ID), any(PageParams.class))
        ).thenReturn(new Page<>(List.of(), 1, 1, 0));

        eventService.sendEventReminders();

        verify(emailService, never()).sendEventReminderNotification(any(), any());
    }
    @Test
    public void testSendEventRemindersNoEvents(){
        when(
            eventDao.findAllBetweenDates(any(LocalDate.class), any(LocalDate.class), any(PageParams.class))
        ).thenReturn(new Page<>(List.of(), 1, 1, 0));

        eventService.sendEventReminders();

        verify(attendanceDao, never()).findAttendeesByEventId(eq(EVENT_ID), any(PageParams.class));
        verify(emailService, never()).sendEventReminderNotification(any(), any());
    }
    @Test
    public void testSendEventRemindersQueriesTodayAndTomorrow(){
        ArgumentCaptor<LocalDate> fromCaptor = ArgumentCaptor.forClass(LocalDate.class);
        ArgumentCaptor<LocalDate> toCaptor = ArgumentCaptor.forClass(LocalDate.class);
        when(
            eventDao.findAllBetweenDates(fromCaptor.capture(), toCaptor.capture(), any(PageParams.class))
        ).thenReturn(new Page<>(List.of(), 1, 1, 0));

        eventService.sendEventReminders();

        assertEquals(LocalDate.now(), fromCaptor.getValue());
        assertEquals(LocalDate.now().plusDays(1), toCaptor.getValue());
    }

    @Test
    public void testGetEventFlyer(){
        when(
            eventDao.findById(eq(EVENT_ID))
        ).thenReturn(Optional.of(EVENT));
        when(
            imageService.findImage(eq(IMAGE_ID))
        ).thenReturn(Optional.of(IMAGE));

        Optional<Image> maybeImage = eventService.getEventFlyer(EVENT_ID);

        assertTrue(maybeImage.isPresent());
    }
    @Test
    public void testGetEventFlyerNoFlyer(){
        when(
            eventDao.findById(eq(EVENT_ID))
        ).thenReturn(Optional.of(EVENT_NO_IMAGE));

        Optional<Image> maybeImage = eventService.getEventFlyer(EVENT_ID);

        assertTrue(maybeImage.isEmpty());
    }
    @Test(expected = EventNotFoundException.class)
    public void testGetEventFlyerNoEvent(){
        when(
            eventDao.findById(eq(EVENT_ID))
        ).thenReturn(Optional.empty());

        eventService.getEventFlyer(EVENT_ID);
    }

    @Test
    public void testUpdateEventFlyer(){
        Event oldEvent = new Event(USER, EVENT_DATE, DESCRIPTION, IMAGE_ID, CITY, TITLE, TIME, ADDRESS, LIMIT);
        when(
            eventDao.findById(eq(EVENT_ID))
        ).thenReturn(Optional.of(oldEvent));
        when(
            imageService.createImage(eq(IMAGE_DATA))
        ).thenReturn(IMAGE_2_ID);

        eventService.updateEventFlyer(EVENT_ID, IMAGE_DATA);

        assertEquals(IMAGE_2_ID, oldEvent.getFlyerImageId().longValue());
        verify(imageService).deleteImage(IMAGE_ID);
    }
    @Test
    public void testUpdateEventFlyerNoImage(){
        Event oldEvent = new Event(USER, EVENT_DATE, DESCRIPTION, null, CITY, TITLE, TIME, ADDRESS, LIMIT);
        when(
            eventDao.findById(eq(EVENT_ID))
        ).thenReturn(Optional.of(oldEvent));
        when(
            imageService.createImage(eq(IMAGE_DATA))
        ).thenReturn(IMAGE_2_ID);

        eventService.updateEventFlyer(EVENT_ID, IMAGE_DATA);

        assertEquals(IMAGE_2_ID, oldEvent.getFlyerImageId().longValue());
        verify(imageService, never()).deleteImage(anyLong());
    }
    @Test(expected=EventNotFoundException.class)
    public void testUpdateEventFlyerNoEvent(){
        when(
            eventDao.findById(eq(EVENT_ID))
        ).thenReturn(Optional.empty());

        eventService.updateEventFlyer(EVENT_ID, IMAGE_DATA);
    }
}
