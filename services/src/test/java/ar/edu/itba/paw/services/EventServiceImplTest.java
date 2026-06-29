package ar.edu.itba.paw.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

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
import ar.edu.itba.paw.models.exceptions.EventAttendanceNotFoundException;
import ar.edu.itba.paw.models.exceptions.EventIsFullException;
import ar.edu.itba.paw.models.exceptions.EventNotFoundException;
import ar.edu.itba.paw.models.exceptions.EventNotInTheFutureException;
import ar.edu.itba.paw.models.exceptions.EventResponseNotFoundException;
import ar.edu.itba.paw.models.exceptions.InvalidPaginationParamsException;
import ar.edu.itba.paw.models.exceptions.InvalidReferenceException;
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
    private static final String STATISTICS_ATTENDEE_COUNTRY = "cuntry";
    private static final Long STATISTICS_ATTENDEE_COUNTRY_ID = 7L;
    private static final EventWithStatistics EVENT_WITH_STATISTICS = new EventWithStatistics(EVENT, STATISTICS_CREATED_EVENTS_COUNT, STATISTICS_ATTENDED_EVENTS_COUNT, STATISTICS_ATTENDEE_COUNTRY, STATISTICS_ATTENDEE_COUNTRY_ID, STATISTICS_ATTENDEE_COUNTRY_COUNT);
    private static final CountryAttendeeCount COUNTRY_ATTENDEE_COUNT = new CountryAttendeeCount(STATISTICS_ATTENDEE_COUNTRY_ID, COUNTRY_NAME, STATISTICS_ATTENDEE_COUNTRY_COUNT);
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
            cityService.findCityByName(eq(CITY_NAME))
        ).thenReturn(Optional.of(CITY));
        when(
            userService.findUserByEmail(eq(EMAIL))
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
            EMAIL,
            CITY_NAME,
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
            cityService.findCityByName(eq(CITY_NAME))
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
            CITY_NAME,
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
            userService.findUserByEmail(eq(EMAIL))
        ).thenReturn(Optional.empty());

        eventService.createEvent(
            EMAIL,
            CITY_NAME,
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
            CITY_NAME,
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
            cityService.findCityByName(eq(CITY_NAME))
        ).thenReturn(Optional.empty());
        when(
            userService.findUserByEmail(eq(EMAIL))
        ).thenReturn(Optional.of(USER));

        eventService.createEvent(
            EMAIL,
            CITY_NAME,
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
            userService.findUserByEmail(EMAIL)
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

        EventResponse response = eventService.createEventResponse(EMAIL, EVENT_ID, DESCRIPTION);

        assertNotNull(response);
        assertEquals(EVENT, response.getEvent());
        assertEquals(USER, response.getUser());
        assertEquals(DESCRIPTION, response.getMessage());
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
            userService.findUserByEmail(EMAIL)
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

        EventResponse response = eventService.createEventResponse(EMAIL, EVENT_ID, DESCRIPTION);

        assertNotNull(response);
        assertEquals(EVENT, response.getEvent());
        assertEquals(USER, response.getUser());
        assertEquals(DESCRIPTION, response.getMessage());
    }
    @Test(expected = UserNotFoundException.class)
    public void testCreateEventResponseNoUser(){
        when(
            userService.findUserByEmail(EMAIL)
        ).thenReturn(Optional.empty());

        eventService.createEventResponse(EMAIL, EVENT_ID, DESCRIPTION);
    }
    @Test(expected = EventNotFoundException.class)
    public void testCreateEventNoEventResponse(){
        when(
            userService.findUserByEmail(EMAIL)
        ).thenReturn(Optional.of(USER));
        when(
            eventDao.findById(EVENT_ID)
        ).thenReturn(Optional.empty());

        eventService.createEventResponse(EMAIL, EVENT_ID, DESCRIPTION);
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
        assertEquals(EVENT_WITH_STATISTICS.getTopAttendeeCountryCount(), event.get().getTopAttendeeCountryCount());
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

    @Test
    public void testSearchEvents(){
        when(
            eventDao.search(eq(TITLE), eq(PAGE_1_DEFAULT))
        ).thenReturn(EVENTS_PAGE);

        Page<Event> events = eventService.searchEvents(TITLE, PAGE_1_DEFAULT);

        assertNotNull(events);
    }
    @Test
    public void testSearchEventsEmptySearch(){
        when(
            eventDao.findAll(eq(PAGE_1_DEFAULT))
        ).thenReturn(EVENTS_PAGE);

        Page<Event> events = eventService.searchEvents("", PAGE_1_DEFAULT);

        assertNotNull(events);
    }
    @Test
    public void testSearchEventsMissingSearch(){
        when(
            eventDao.findAll(eq(PAGE_1_DEFAULT))
        ).thenReturn(EVENTS_PAGE);

        Page<Event> events = eventService.searchEvents(null, PAGE_1_DEFAULT);

        assertNotNull(events);
    }

    @Test
    public void testFindEvents(){
        when(
            eventDao.findByUserId(eq(USER_ID), eq(PAGE_1_DEFAULT))
        ).thenReturn(EVENTS_PAGE);

        Page<Event> events = eventService.findEvents(USER_ID, PAGE_1_DEFAULT);

        assertNotNull(events);
    }

    @Test()
    public void testCreateEventAttendanceId(){
        when(
            eventDao.findById(eq(EVENT_ID))
        ).thenReturn(Optional.of(EVENT));
        when(
            userService.findUserById(eq(USER_ID))
        ).thenReturn(Optional.of(USER));
        when(
            attendanceDao.exists(eq(USER), eq(EVENT_FULL))
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
            eventDao.findById(eq(EVENT_ID))
        ).thenReturn(Optional.of(EVENT_NO_LIMIT));
        when(
            userService.findUserById(eq(USER_ID))
        ).thenReturn(Optional.of(USER));
        when(
            attendanceDao.exists(eq(USER), eq(EVENT_FULL))
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
            eventDao.findById(eq(EVENT_ID))
        ).thenReturn(Optional.of(EVENT_FULL));
        when(
            userService.findUserById(eq(USER_ID))
        ).thenReturn(Optional.of(USER));
        when(
            attendanceDao.exists(eq(USER), eq(EVENT_FULL))
        ).thenReturn(false);

        eventService.createEventAttendance(USER_ID, EVENT_ID);
    }
    @Test(expected = UserAlreadyAttendingException.class)
    public void testCreateEventAttendanceAlreadyGoing(){
        when(
            eventDao.findById(eq(EVENT_ID))
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
            eventDao.findById(eq(EVENT_ID))
        ).thenReturn(Optional.of(EVENT_PAST));
        when(
            userService.findUserById(eq(USER_ID))
        ).thenReturn(Optional.of(USER));

        eventService.createEventAttendance(USER_ID, EVENT_ID);
    }
    @Test(expected = UserNotFoundException.class)
    public void testCreateEventAttendanceUserNotFound(){
        when(
            eventDao.findById(eq(EVENT_ID))
        ).thenReturn(Optional.of(EVENT));
        when(
            userService.findUserById(eq(USER_ID))
        ).thenReturn(Optional.empty());

        eventService.createEventAttendance(USER_ID, EVENT_ID);
    }
    @Test(expected = EventNotFoundException.class)
    public void testCreateEventAttendanceEventNotFound(){
        when(
            eventDao.findById(eq(EVENT_ID))
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

    // @Test
    // public void testDeleteEventAttendance(){
    //     when(
    //         eventDao.findById(eq(EVENT_ID))
    //     ).thenReturn(Optional.of(EVENT));
    //     when(
    //         attendanceDao.exists(eq(USER_ID), eq(EVENT_ID))
    //     ).thenReturn(true);

    //     eventService.deleteEventAttendance(USER_ID, EVENT_ID);
    // }
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
            eventDao.findById(eq(EVENT_ID))
        ).thenReturn(Optional.of(EVENT));
        when(
            ratingDao.rateEvent(USER, EVENT, RATING_VALUE)
        ).thenReturn(new Rating(USER, EVENT, RATING_VALUE));

        Rating rating = eventService.rateEvent(USER, EVENT_ID, RATING_VALUE);

        assertNotNull(rating);
    }
    @Test(expected = EventNotFoundException.class)
    public void testRateEventMissing(){
        when(
            eventDao.findById(eq(EVENT_ID))
        ).thenReturn(Optional.empty());

        eventService.rateEvent(USER, EVENT_ID, RATING_VALUE);
    }

    @Test
    public void testRateEventUserId(){
        when(
            userService.findUserById(eq(USER_ID))
        ).thenReturn(Optional.of(USER));
        when(
            eventDao.findById(eq(EVENT_ID))
        ).thenReturn(Optional.of(EVENT));
        when(
            ratingDao.rateEvent(USER, EVENT, RATING_VALUE)
        ).thenReturn(new Rating(USER, EVENT, RATING_VALUE));

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
            ratingDao.findRatingByUserAndEvent(
                eq(USER_ID),
                eq(EVENT_ID)
            )
        ).thenReturn(Optional.of(newRating));

        eventService.updateEventRating(USER, EVENT_ID, RATING_VALUE);

        assertEquals(RATING_VALUE, newRating.getRating(), 0.1);
    }
    @Test(expected = RatingNotFoundException.class)
    public void testUpdateEventRatingNotFound(){
        when(
            ratingDao.findRatingByUserAndEvent(
                eq(USER_ID),
                eq(EVENT_ID)
            )
        ).thenReturn(Optional.empty());

        eventService.updateEventRating(USER, EVENT_ID, RATING_VALUE);
    }

    @Test
    public void testUpdateEventRatingUserId(){
        Rating newRating = new Rating(USER, EVENT, 2);
        when(
            userService.findUserById(eq(USER_ID))
        ).thenReturn(Optional.of(USER));
        when(
            ratingDao.findRatingByUserAndEvent(
                eq(USER_ID),
                eq(EVENT_ID)
            )
        ).thenReturn(Optional.of(newRating));

        eventService.updateEventRating(USER_ID, EVENT_ID, RATING_VALUE);

        assertEquals(RATING_VALUE, newRating.getRating(), 0.1);
    }
    @Test(expected = UserNotFoundException.class)
    public void testUpdateEventRatingUserIdNotFound(){
        when(
            userService.findUserById(eq(USER_ID))
        ).thenReturn(Optional.empty());

        eventService.updateEventRating(USER_ID, EVENT_ID, RATING_VALUE);
    }

    @Test
    public void testFindRatingByUserAndEvent(){
        when(
            ratingDao.findRatingByUserAndEvent(eq(USER_ID), eq(EVENT_ID))
        ).thenReturn(Optional.empty());

        Optional<Rating> maybeRating = eventService.findRatingByUserAndEvent(USER_ID, EVENT_ID);

        assertNotNull(maybeRating);
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
    public void testCountRatingsByEvent(){
        when(
            ratingDao.countRatingsByEvent(eq(EVENT_ID))
        ).thenReturn(1);

        int count = eventService.countRatingsByEvent(EVENT_ID);

        assertEquals(1, count);
    }

    @Test
    public void testFindRecommendedEvents(){
        when(
            eventDao.findRecommended(eq(USER_ID), eq(PAGE_1_DEFAULT))
        ).thenReturn(EVENTS_PAGE);

        List<Event> userEvents = eventService.findRecommendedEvents(USER_ID, PAGE_1_DEFAULT.getSize());

        assertNotNull(userEvents);
        assertEquals(EVENTS, userEvents);
    }
    @Test
    public void testFindRecommendedEventsMissing(){
        when(
            eventDao.findRecommended(eq(USER_ID), eq(PAGE_1_DEFAULT))
        ).thenReturn(new Page<>(List.of(), 1, 1, 0));
        when(
            eventDao.findTopByUser(eq(USER_ID), eq(PAGE_1_DEFAULT))
        ).thenReturn(EVENTS_PAGE);

        List<Event> userevents = eventService.findRecommendedEvents(USER_ID, 2);

        assertNotNull(userevents);
        assertEquals(EVENTS, userevents);
    }
    @Test(expected = InvalidPaginationParamsException.class)
    public void testFindRecommendedEventsWrongLimit(){
        eventService.findRecommendedEvents(USER_ID, 0);
    }
    @Test
    public void testFindRecommendedEventsNoEventsTop(){
        when(
            eventDao.findRecommended(eq(USER_ID), eq(PAGE_1_DEFAULT))
        ).thenReturn(new Page<>(List.of(), 1, 1, 0));
        when(
            eventDao.findTopByUser(eq(USER_ID), eq(PAGE_1_DEFAULT))
        ).thenReturn(new Page<>(List.of(), 1, 1, 0));
        when(
            eventDao.findAll(any(PageParams.class))
        ).thenReturn(EVENTS_PAGE);

        List<Event> userevents = eventService.findRecommendedEvents(USER_ID, 2);

        assertNotNull(userevents);
        assertEquals(EVENTS, userevents);
    }

    @Test
    public void testFindTopEvents(){
        when(
            eventDao.findTop(PAGE_1_DEFAULT)
        ).thenReturn(EVENTS_PAGE);

        List<Event> topEvents = eventService.findTopEvents(PAGE_1_DEFAULT.getSize());

        assertNotNull(topEvents);
    }
    @Test(expected = InvalidPaginationParamsException.class)
    public void testFindTopEventsInvalidPagination(){
        eventService.findTopEvents(-10);
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
            PAGE_1_DEFAULT
        );

        assertNotNull(page);
        assertEquals(EVENTS_PAGE, page);
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
            eventDao.findById(eq(EVENT_ID))
        ).thenReturn(Optional.of(newEvent));
        when(
            cityService.findCityByName(eq("CITY_NAME"))
        ).thenReturn(Optional.of(new City("CITY_NAME", COUNTRY)));

        Event event = eventService.updateEvent(
            EVENT_ID,
            "CITY_NAME",
            EVENT_DATE.plusDays(1),
            "DESCRIPTION",
            "TITLE",
            TIME.plusSeconds(10),
            "ADDRESS",
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
    @Test(expected = InvalidReferenceException.class)
    public void testUpdateEventMissingCity(){
        when(
            eventDao.findById(eq(EVENT_ID))
        ).thenReturn(Optional.of(EVENT));
        when(
            cityService.findCityByName(eq(CITY_NAME))
        ).thenReturn(Optional.empty());

        eventService.updateEvent(
            EVENT_ID,
            CITY_NAME,
            EVENT_DATE,
            DESCRIPTION,
            TITLE,
            TIME,
            ADDRESS,
            LIMIT
        );
    }
    @Test(expected = EventNotFoundException.class)
    public void testUpdateEventMissingEvent(){
        when(
            eventDao.findById(eq(EVENT_ID))
        ).thenReturn(Optional.empty());

        eventService.updateEvent(
            EVENT_ID,
            CITY_NAME,
            EVENT_DATE,
            DESCRIPTION,
            TITLE,
            TIME,
            ADDRESS,
            LIMIT
        );
    }

    @Test(expected = EventNotFoundException.class)
    public void testPatchEventMissing(){
        when(
            eventDao.findById(eq(EVENT_ID))
        ).thenReturn(Optional.empty());

        eventService.patchEvent(EVENT_ID, CITY_NAME, EVENT_DATE, DESCRIPTION, TITLE, TIME, ADDRESS, null);
    }
    @Test
    public void testPatchEvent(){
        Event oldEvent = new Event(EVENT_ID, USER, EVENT_DATE, DESCRIPTION, null, CITY, TITLE, TIME, ADDRESS, LIMIT);
        when(
            eventDao.findById(eq(EVENT_ID))
        ).thenReturn(Optional.of(oldEvent));
        when(
            cityService.findCityByName("CITY_NAME")
        ).thenReturn(Optional.of(new City("CITY_NAME", COUNTRY)));

        Event patched = eventService.patchEvent(EVENT_ID, "CITY_NAME", EVENT_DATE_PAST, "DESCRIPTION", "TITLE", TIME.plusHours(1), "ADDRESS", 1);

        assertNotNull(patched);
        assertEquals(EVENT_ID, patched.getId().longValue());
        assertEquals("CITY_NAME", patched.getCity().getName());
        assertEquals(EVENT_DATE_PAST, patched.getDate());
        assertEquals("DESCRIPTION", patched.getDescription());
        assertEquals("TITLE", patched.getTitle());
        assertEquals(TIME.plusHours(1), patched.getTime());
        assertEquals("ADDRESS", patched.getAddress());
        assertEquals(1, patched.getAttendeesLimit().intValue());
    }
    @Test
    public void testPatchEventNoPatches(){
        Event oldEvent = new Event(EVENT_ID, USER, EVENT_DATE, DESCRIPTION, null, CITY, TITLE, TIME, ADDRESS, LIMIT);
        when(
            eventDao.findById(eq(EVENT_ID))
        ).thenReturn(Optional.of(oldEvent));

        Event patched = eventService.patchEvent(EVENT_ID, null, null, null, null, null, null, null);

        assertNotNull(patched);
        assertEquals(EVENT_ID, patched.getId().longValue());
        assertEquals(CITY_NAME, patched.getCity().getName());
        assertEquals(EVENT_DATE, patched.getDate());
        assertEquals(DESCRIPTION, patched.getDescription());
        assertEquals(TITLE, patched.getTitle());
        assertEquals(TIME, patched.getTime());
        assertEquals(ADDRESS, patched.getAddress());
        assertEquals(LIMIT, patched.getAttendeesLimit().intValue());
    }
    @Test(expected = InvalidReferenceException.class)
    public void testPatchEventMissingCity(){
        when(
            eventDao.findById(eq(EVENT_ID))
        ).thenReturn(Optional.of(EVENT));
        when(
            cityService.findCityByName(CITY_NAME)
        ).thenReturn(Optional.empty());

        eventService.patchEvent(EVENT_ID, CITY_NAME, EVENT_DATE, DESCRIPTION, TITLE, TIME, ADDRESS, LIMIT);
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
    public void testDeleteEventResponse(){
        EventResponse resp = new EventResponse(USER, EVENT, ADDRESS);
        eventService.deleteEventResponse(resp, DESCRIPTION);

        assertTrue(resp.isDeleted());
        assertEquals(DESCRIPTION, resp.getDeletionMessage());
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
    public void testFindCreatedByJourney(){
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

        Page<Event> events = eventService.findCreatedByJourney(
            new Journey(USER, EVENT_DATE_PAST, EVENT_DATE, null, null),
            PAGE_1_DEFAULT
        );

        assertNotNull(events);
        assertEquals(EVENTS_PAGE, events);
    }
    @Test
    public void testFindCreatedByJourneyOlder(){
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

        Page<Event> events = eventService.findCreatedByJourney(
            new Journey(USER, EVENT_DATE_PAST, LocalDate.now().minusDays(10), null, null),
            PAGE_1_DEFAULT
        );

        assertNotNull(events);
        assertEquals(EVENTS_PAGE, events);
    }

    @Test
    public void testFindAttendedByJourney(){
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

        Page<Event> events = eventService.findAttendedByJourney(
            new Journey(USER, EVENT_DATE_PAST, EVENT_DATE, null, null),
            PAGE_1_DEFAULT
        );

        assertNotNull(events);
        assertEquals(EVENTS_PAGE, events);
    }
    @Test
    public void testFindAttendedByJourneyOlder(){
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

        Page<Event> events = eventService.findAttendedByJourney(
            new Journey(USER, EVENT_DATE_PAST, LocalDate.now().minusDays(10), null, null),
            PAGE_1_DEFAULT
        );

        assertNotNull(events);
        assertEquals(EVENTS_PAGE, events);
    }
    @Test
    public void testFindAttendedByJourneyNoEndDate(){
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

        Page<Event> events = eventService.findAttendedByJourney(
            new Journey(USER, EVENT_DATE_PAST, null, null, null),
            PAGE_1_DEFAULT
        );

        assertNotNull(events);
        assertEquals(EVENTS_PAGE, events);
    }

    @Test
    public void testFindEventAttendees(){
        when(
            attendanceDao.findAttendeesByEventId(eq(EVENT_ID), eq(PAGE_1_DEFAULT))
        ).thenReturn(new Page<>(List.of(), 1,1 ,1));

        Page<User> attendees = eventService.findEventAttendees(EVENT_ID, PAGE_1_DEFAULT);

        assertNotNull(attendees);
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
    }
    @Test(expected=EventNotFoundException.class)
    public void testUpdateEventFlyerNoEvent(){
        when(
            eventDao.findById(eq(EVENT_ID))
        ).thenReturn(Optional.empty());

        eventService.updateEventFlyer(EVENT_ID, IMAGE_DATA);
    }
}
