package ar.edu.itba.paw.services;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.enums.SortDirection;
import ar.edu.itba.paw.models.enums.SortFieldEvent;
import ar.edu.itba.paw.models.exceptions.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

import ar.edu.itba.paw.interfaces.persistence.EventAttendanceDao;
import ar.edu.itba.paw.interfaces.persistence.EventDao;
import ar.edu.itba.paw.interfaces.persistence.EventRatingDao;
import ar.edu.itba.paw.interfaces.persistence.EventResponseDao;
import ar.edu.itba.paw.interfaces.services.CityService;
import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.interfaces.services.ImageService;
import ar.edu.itba.paw.interfaces.services.UserService;

@RunWith(MockitoJUnitRunner.class)
public class EventServiceImplTest {

    private static final long EVENT_ID = 0;
    private static final long USER_ID = 1;
    private static final long USER_2_ID = 7;
    private static final long UNI_ID = 2;
    private static final long CITY_ID = 3;
    private static final long IMAGE_ID = 4;
    private static final long CAREER_ID = 5;
    private static final long RESPONSE_ID = 6;
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
    private static final User USER_2 = new User(USER_2_ID, EMAIL, USERNAME, FIRSTNAME, LASTNAME, UNI, CAREER, IMAGE_ID, LOCALE, BLOCKED, true);
    private static final Event EVENT = new Event(EVENT_ID, USER, EVENT_DATE, DESCRIPTION, IMAGE_ID, CITY, TITLE, TIME, ADDRESS, LIMIT);
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
    private static final EventWithStatistics EVENT_WITH_STATISTICS = new EventWithStatistics(EVENT, STATISTICS_CREATED_EVENTS_COUNT, STATISTICS_ATTENDED_EVENTS_COUNT, STATISTICS_ATTENDEE_COUNTRY, STATISTICS_ATTENDEE_COUNTRY_COUNT, true, true);
    private static final CountryAttendeeCount COUNTRY_ATTENDEE_COUNT = new CountryAttendeeCount(COUNTRY_NAME, STATISTICS_ATTENDEE_COUNTRY_COUNT);
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


//
//    @Test
//    public void testCreateEvent(){
//        when(
//            cityService.findCityByName(eq(CITY_NAME))
//        ).thenReturn(Optional.of(CITY));
//        when(
//            userService.findUserByEmail(eq(EMAIL))
//        ).thenReturn(Optional.of(USER));
//        when(
//            imageService.createImage(eq(IMAGE_DATA))
//        ).thenReturn(IMAGE_ID);
//        when(
//            eventDao.create(
//                eq(USER),
//                eq(CITY),
//                eq(EVENT_DATE),
//                eq(DESCRIPTION),
//                eq(IMAGE_ID),
//                eq(TITLE),
//                eq(TIME),
//                eq(ADDRESS),
//                eq(LIMIT)
//            )
//        ).thenReturn(EVENT);
//
//        Event event = eventService.createEvent(
//            EMAIL,
//            CITY_NAME,
//            EVENT_DATE,
//            IMAGE_DATA,
//            DESCRIPTION,
//            TITLE,
//            TIME,
//            ADDRESS,
//            LIMIT
//        );
//
//        assertNotNull(event);
//        assertEquals(EVENT, event);
//    }
//    @Test(expected = UserNotFoundException.class)
//    public void testCreateEventUserNotFound(){
//        when(
//            cityService.findCityByName(eq(CITY_NAME))
//        ).thenReturn(Optional.of(CITY));
//        when(
//            userService.findUserByEmail(eq(EMAIL))
//        ).thenReturn(Optional.empty());
//
//        eventService.createEvent(
//            EMAIL,
//            CITY_NAME,
//            EVENT_DATE,
//            IMAGE_DATA,
//            DESCRIPTION,
//            TITLE,
//            TIME,
//            ADDRESS,
//            LIMIT
//        );
//    }
//    @Test(expected = CityNotFoundException.class)
//    public void testCreateEventCityNotFound(){
//        when(
//            cityService.findCityByName(eq(CITY_NAME))
//        ).thenReturn(Optional.empty());
//
//        eventService.createEvent(
//            EMAIL,
//            CITY_NAME,
//            EVENT_DATE,
//            IMAGE_DATA,
//            DESCRIPTION,
//            TITLE,
//            TIME,
//            ADDRESS,
//            LIMIT
//        );
//    }

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
        ).thenReturn(new Page<>(USERS, 1, 2, 2));
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
            eventDao.findById(EVENT_ID)
        ).thenReturn(Optional.of(EVENT));
        when(
            userService.findUserByEmail(EMAIL)
        ).thenReturn(Optional.empty());

        eventService.createEventResponse(EMAIL, EVENT_ID, DESCRIPTION);
    }
    @Test(expected = EventNotFoundException.class)
    public void testCreateEventNoEventResponse(){
        when(
            eventDao.findById(EVENT_ID)
        ).thenReturn(Optional.empty());

        eventService.createEventResponse(EMAIL, EVENT_ID, DESCRIPTION);
    }

    @Test
    public void testFindEventWithStatistics(){
        when(
            eventDao.findById(eq(EVENT_ID))
        ).thenReturn(Optional.of(EVENT));
        when(
            userService.findUserById(eq(USER_ID))
        ).thenReturn(Optional.of(USER));
        when(
            attendanceDao.exists(eq(USER), eq(EVENT))
        ).thenReturn(true);
        when(
            eventDao.countEventsCreatedByUser(eq(USER_ID))
        ).thenReturn(STATISTICS_CREATED_EVENTS_COUNT);
        when(
            attendanceDao.countEventsAttendedByUser(eq(USER_ID))
        ).thenReturn(STATISTICS_ATTENDED_EVENTS_COUNT);
        when(
            eventDao.findTopAttendeeCountry(eq(EVENT_ID))
        ).thenReturn(Optional.of(COUNTRY_ATTENDEE_COUNT));

        Optional<EventWithStatistics> event = eventService.findEventWithStatistics(USER, EVENT_ID);

        assertNotNull(event);
        assertTrue(event.isPresent());
        assertEquals(EVENT_WITH_STATISTICS.isCreator(), event.get().isCreator());
        assertEquals(EVENT_WITH_STATISTICS.isAttending(), event.get().isAttending());
        assertEquals(EVENT_WITH_STATISTICS.getAttendedEventsCount(), event.get().getAttendedEventsCount());
        assertEquals(EVENT_WITH_STATISTICS.getCreatedEventsCount(), event.get().getCreatedEventsCount());
        assertEquals(EVENT_WITH_STATISTICS.getEvent(), event.get().getEvent());
        assertEquals(EVENT_WITH_STATISTICS.getTopAttendeeCountry(), event.get().getTopAttendeeCountry());
        assertEquals(EVENT_WITH_STATISTICS.getTopAttendeeCountryCount(), event.get().getTopAttendeeCountryCount());
    }
    @Test
    public void testFindEventWithStatisticsNotCreated(){
        when(
            eventDao.findById(eq(EVENT_ID))
        ).thenReturn(Optional.of(EVENT));
        when(
            userService.findUserById(eq(USER_2_ID))
        ).thenReturn(Optional.of(USER_2));
        when(
            attendanceDao.exists(eq(USER_2), eq(EVENT))
        ).thenReturn(true);
        when(
            eventDao.countEventsCreatedByUser(eq(USER_ID))
        ).thenReturn(STATISTICS_CREATED_EVENTS_COUNT);
        when(
            attendanceDao.countEventsAttendedByUser(eq(USER_ID))
        ).thenReturn(STATISTICS_ATTENDED_EVENTS_COUNT);
        when(
            eventDao.findTopAttendeeCountry(eq(EVENT_ID))
        ).thenReturn(Optional.of(COUNTRY_ATTENDEE_COUNT));

        Optional<EventWithStatistics> event = eventService.findEventWithStatistics(USER_2, EVENT_ID);

        assertNotNull(event);
        assertTrue(event.isPresent());
        assertFalse(event.get().isCreator());
        assertEquals(EVENT_WITH_STATISTICS.isAttending(), event.get().isAttending());
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
            userService.findUserById(eq(USER_ID))
        ).thenReturn(Optional.of(USER));
        when(
            attendanceDao.exists(eq(USER), eq(EVENT))
        ).thenReturn(true);
        when(
            eventDao.countEventsCreatedByUser(eq(USER_ID))
        ).thenReturn(STATISTICS_CREATED_EVENTS_COUNT);
        when(
            attendanceDao.countEventsAttendedByUser(eq(USER_ID))
        ).thenReturn(STATISTICS_ATTENDED_EVENTS_COUNT);
        when(
            eventDao.findTopAttendeeCountry(eq(EVENT_ID))
        ).thenReturn(Optional.empty());

        Optional<EventWithStatistics> event = eventService.findEventWithStatistics(USER, EVENT_ID);

        assertNotNull(event);
        assertTrue(event.isPresent());
        assertEquals(EVENT_WITH_STATISTICS.getAttendedEventsCount(), event.get().getAttendedEventsCount());
        assertEquals(EVENT_WITH_STATISTICS.getCreatedEventsCount(), event.get().getCreatedEventsCount());
        assertEquals(EVENT_WITH_STATISTICS.getEvent(), event.get().getEvent());
        assertNull(event.get().getTopAttendeeCountry());
    }

    @Test
    public void testFindEventWithStatisticsNotAttending(){
        when(
            eventDao.findById(eq(EVENT_ID))
        ).thenReturn(Optional.of(EVENT));
        when(
            userService.findUserById(eq(USER_ID))
        ).thenReturn(Optional.of(USER));
        when(
            attendanceDao.exists(eq(USER), eq(EVENT))
        ).thenReturn(false);

        eventService.findEventWithStatistics(USER, EVENT_ID);

        Optional<EventWithStatistics> event = eventService.findEventWithStatistics(USER, EVENT_ID);

        assertNotNull(event);
        assertTrue(event.isPresent());
        assertEquals(0, event.get().getAttendedEventsCount());
        assertEquals(0, event.get().getCreatedEventsCount());
        assertEquals(EVENT_WITH_STATISTICS.getEvent(), event.get().getEvent());
        assertNull(event.get().getTopAttendeeCountry());
    }
    @Test(expected = UserNotFoundException.class)
    public void testFindEventWithStatisticsUserNotFound(){
        when(
            eventDao.findById(eq(EVENT_ID))
        ).thenReturn(Optional.of(EVENT));
        when(
            userService.findUserById(eq(USER_ID))
        ).thenReturn(Optional.empty());

        eventService.findEventWithStatistics(USER, EVENT_ID);
    }
    @Test
    public void testFindEventWithStatisticsEventNotFound(){
        when(
            eventDao.findById(eq(EVENT_ID))
        ).thenReturn(Optional.empty());

        Optional<EventWithStatistics> event = eventService.findEventWithStatistics(USER, EVENT_ID);

        assertNotNull(event);
        assertFalse(event.isPresent());
    }
    @Test
    public void testFindEventWithStatisticsMissingUser(){
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

        Optional<EventWithStatistics> event = eventService.findEventWithStatistics(null, EVENT_ID);

        assertNotNull(event);
        assertTrue(event.isPresent());
        assertFalse(event.get().isAttending());
        assertFalse(event.get().isCreator());
        assertEquals(EVENT_WITH_STATISTICS.getAttendedEventsCount(), event.get().getAttendedEventsCount());
        assertEquals(EVENT_WITH_STATISTICS.getCreatedEventsCount(), event.get().getCreatedEventsCount());
        assertEquals(EVENT_WITH_STATISTICS.getEvent(), event.get().getEvent());
        assertEquals(EVENT_WITH_STATISTICS.getTopAttendeeCountry(), event.get().getTopAttendeeCountry());
        assertEquals(EVENT_WITH_STATISTICS.getTopAttendeeCountryCount(), event.get().getTopAttendeeCountryCount());
    }
    @Test
    public void testFindEventWithStatisticsMissingUserNoTopCountry(){
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

        Optional<EventWithStatistics> event = eventService.findEventWithStatistics(null, EVENT_ID);

        assertNotNull(event);
        assertTrue(event.isPresent());
        assertFalse(event.get().isAttending());
        assertFalse(event.get().isCreator());
        assertEquals(EVENT_WITH_STATISTICS.getAttendedEventsCount(), event.get().getAttendedEventsCount());
        assertEquals(EVENT_WITH_STATISTICS.getCreatedEventsCount(), event.get().getCreatedEventsCount());
        assertEquals(EVENT_WITH_STATISTICS.getEvent(), event.get().getEvent());
        assertNull(event.get().getTopAttendeeCountry());
    }
    @Test
    public void testFindEventWithStatisticsMissingUserNoEvent(){
        when(
            eventDao.findById(eq(EVENT_ID))
        ).thenReturn(Optional.empty());

        Optional<EventWithStatistics> event = eventService.findEventWithStatistics(null, EVENT_ID);

        assertNotNull(event);
        assertFalse(event.isPresent());
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

//    @Test
//    public void testFindUpcomingEventsByAttendee(){
//        when(
//            eventDao.findAllWithFilters(
//                eq(USER_ID),
//                eq(null),
//                eq(null),
//                any(SortDirection.class),
//                eq(null),
//                any(LocalDate.class),
//                eq(null),
//                any(LocalTime.class),
//                eq(null),
//                eq(null),
//                eq(true),
//                eq(false),
//                    eq(null),
//                    eq(null),
//                    eq(null),
//                    eq(false),
//                    eq(PAGE_1_DEFAULT)
//                    )
//        ).thenReturn(EVENTS_PAGE);
//
//        Page<Event> events = eventService.findUpcomingEventsByAttendee(USER_ID, PAGE_1_DEFAULT);
//
//        assertNotNull(events);
//        assertEquals(EVENTS_PAGE, events);
//    }
//
//    @Test
//    public void testFindFinishedEventsByAttendee(){
//        when(
//            eventDao.findAllWithFilters(
//                eq(USER_ID),
//                eq(null),
//                eq(null),
//                any(SortDirection.class),
//                eq(null),
//                eq(null),
//                any(LocalDate.class),
//                    any(),
//                    any(),
//                    eq(null),
//                eq(true),
//                eq(false),
//                    eq(null),
//                    eq(null),
//                    eq(null),
//                    eq(false),
//                eq(PAGE_1_DEFAULT)
//            )
//        ).thenReturn(EVENTS_PAGE);
//
//        Page<Event> events = eventService.findFinishedEventsByAttendee(USER_ID, PAGE_1_DEFAULT);
//
//        assertNotNull(events);
//        assertEquals(EVENTS_PAGE, events);
//    }

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

//    @Test
//    public void testSearchEventsWithFiltersNoUser(){
//        when(
//            eventDao.findAllWithFilters(
//                eq(null),
//                eq(TITLE),
//                any(SortFieldEvent.class),
//                any(SortDirection.class),
//                eq(CITY_NAME),
//                any(LocalDate.class),
//                any(LocalDate.class),
//                any(),
//                any(),
//                eq(INTEREST),
//                eq(true),
//                eq(false),
//                eq(PAGE_1_DEFAULT)
//            )
//        ).thenReturn(EVENTS_PAGE);
//
//        Page<Event> page = eventService.searchEventsWithFilters(
//            TITLE,
//            null,
//            SortFieldEvent.from("attendees"),
//            SortDirection.from("desc"),
//            CITY_NAME,
//            EVENT_DATE,
//            EVENT_DATE,
//            INTEREST,
//            false,
//            true,
//            true,
//            PAGE_1_DEFAULT
//        );
//
//        assertNotNull(page);
//        assertEquals(EVENTS_PAGE, page);
//    }
//
//    @Test
//    public void testSearchEventsWithFilters(){
//        when(
//            eventDao.findAllWithFilters(
//                eq(USER_ID),
//                eq(TITLE),
//                any(SortFieldEvent.class),
//                any(SortDirection.class),
//                eq(CITY_NAME),
//                any(LocalDate.class),
//                any(LocalDate.class),
//                any(),
//                any(),
//                eq(INTEREST),
//                eq(true),
//                eq(false),
//                eq(PAGE_1_DEFAULT)
//            )
//        ).thenReturn(EVENTS_PAGE);
//
//        Page<Event> page = eventService.searchEventsWithFilters(
//            TITLE,
//            USER_ID,
//            SortFieldEvent.from(""),
//            SortDirection.from(""),
//            CITY_NAME,
//            EVENT_DATE,
//            EVENT_DATE,
//            INTEREST,
//            false,
//            true,
//            true,
//            PAGE_1_DEFAULT
//        );
//
//        assertNotNull(page);
//        assertEquals(EVENTS_PAGE, page);
//    }
//    @Test
//    public void testSearchEventsWithFiltersNotUpcomingPast(){
//        when(
//            eventDao.findAllWithFilters(
//                eq(USER_ID),
//                eq(TITLE),
//                eq(SortFieldEvent.from(null)),
//                eq(SortDirection.from(null)),
//                eq(CITY_NAME),
//                any(LocalDate.class),
//                any(LocalDate.class),
//                any(),
//                any(LocalTime.class),
//                eq(INTEREST),
//                eq(true),
//                eq(false),
//                eq(PAGE_1_DEFAULT)
//            )
//        ).thenReturn(EVENTS_PAGE);
//
//        Page<Event> page = eventService.searchEventsWithFilters(
//            TITLE,
//            USER_ID,
//            SortFieldEvent.from(""),
//            SortDirection.from(""),
//            CITY_NAME,
//            EVENT_DATE,
//            EVENT_DATE,
//            INTEREST,
//            true,
//            false,
//            true,
//            PAGE_1_DEFAULT
//        );
//
//        assertNotNull(page);
//        assertEquals(EVENTS_PAGE, page);
//    }
//    @Test
//    public void testSearchEventsWithFiltersNotUpcomingPastDateChanges(){
//        when(
//            eventDao.findAllWithFilters(
//                eq(USER_ID),
//                eq(TITLE),
//                eq(SortFieldEvent.from(null)),
//                eq(SortDirection.from(null)),
//                eq(CITY_NAME),
//                any(LocalDate.class),
//                any(LocalDate.class),
//                any(LocalTime.class),
//                any(LocalTime.class),
//                eq(INTEREST),
//                eq(true),
//                eq(false),
//                eq(PAGE_1_DEFAULT)
//            )
//        ).thenReturn(EVENTS_PAGE);
//
//        Page<Event> page = eventService.searchEventsWithFilters(
//            TITLE,
//            USER_ID,
//            SortFieldEvent.from(""),
//            SortDirection.from(""),
//            CITY_NAME,
//            LocalDate.now().plusDays(-10),
//            LocalDate.now(),
//            INTEREST,
//            true,
//            true,
//            true,
//            PAGE_1_DEFAULT
//        );
//
//        assertNotNull(page);
//        assertEquals(EVENTS_PAGE, page);
//    }
//    @Test
//    public void testSearchEventsWithFiltersNotUpcomingPastNoDateChanges(){
//        when(
//            eventDao.findAllWithFilters(
//                eq(USER_ID),
//                eq(TITLE),
//                eq(SortFieldEvent.from(null)),
//                eq(SortDirection.from(null)),
//                eq(CITY_NAME),
//                any(LocalDate.class),
//                any(LocalDate.class),
//                any(),
//                any(),
//                eq(INTEREST),
//                eq(true),
//                eq(false),
//                eq(PAGE_1_DEFAULT)
//            )
//        ).thenReturn(EVENTS_PAGE);
//
//        Page<Event> page = eventService.searchEventsWithFilters(
//            TITLE,
//            USER_ID,
//            SortFieldEvent.from(""),
//            SortDirection.from(""),
//            CITY_NAME,
//            LocalDate.now().plusDays(10),
//            LocalDate.now().plusDays(-10),
//            INTEREST,
//            true,
//            true,
//            true,
//            PAGE_1_DEFAULT
//        );
//
//        assertNotNull(page);
//        assertEquals(EVENTS_PAGE, page);
//    }
//    @Test
//    public void testSearchEventsWithFiltersNotUpcomingPastNoDates(){
//        when(
//            eventDao.findAllWithFilters(
//                eq(USER_ID),
//                eq(TITLE),
//                eq(SortFieldEvent.from(null)),
//                eq(SortDirection.from(null)),
//                eq(CITY_NAME),
//                any(LocalDate.class),
//                any(LocalDate.class),
//                any(LocalTime.class),
//                any(LocalTime.class),
//                eq(INTEREST),
//                eq(true),
//                eq(false),
//                eq(PAGE_1_DEFAULT)
//            )
//        ).thenReturn(EVENTS_PAGE);
//
//        Page<Event> page = eventService.searchEventsWithFilters(
//            TITLE,
//            USER_ID,
//            SortFieldEvent.from(""),
//            SortDirection.from(""),
//            CITY_NAME,
//            null,
//            null,
//            INTEREST,
//            true,
//            true,
//            true,
//            PAGE_1_DEFAULT
//        );
//
//        assertNotNull(page);
//        assertEquals(EVENTS_PAGE, page);
//    }
//
//    @Test
//    public void testUpdateEvent(){
//        Event newEvent = new Event(
//            EVENT_ID,
//            USER,
//            EVENT_DATE,
//            DESCRIPTION,
//            IMAGE_ID,
//            CITY,
//            TITLE,
//            TIME,
//            ADDRESS,
//            null
//        );
//        when(
//            eventDao.findById(eq(EVENT_ID))
//        ).thenReturn(Optional.of(newEvent));
//        when(
//            cityService.findCityByName(eq("CITY_NAME"))
//        ).thenReturn(Optional.of(new City("CITY_NAME", COUNTRY)));
//
//        Event event = eventService.updateEvent(
//            EVENT_ID,
//            "CITY_NAME",
//            EVENT_DATE.plusDays(1),
//            null,
//            "DESCRIPTION",
//            "TITLE",
//            TIME.plusSeconds(10),
//            "ADDRESS",
//            null
//        );
//
//        assertEquals("CITY_NAME", event.getCity().getName());
//        assertEquals(EVENT_DATE.plusDays(1), event.getDate());
//        assertEquals(TIME.plusSeconds(10), event.getTime());
//        assertEquals("DESCRIPTION", event.getDescription());
//        assertEquals("TITLE", event.getTitle());
//        assertEquals("ADDRESS", event.getAddress());
//        assertNull(event.getAttendeesLimit());
//    }
//    @Test
//    public void testUpdateEventImage(){
//        Event newEvent = new Event(
//            EVENT_ID,
//            USER,
//            EVENT_DATE,
//            DESCRIPTION,
//            IMAGE_ID,
//            CITY,
//            TITLE,
//            TIME,
//            ADDRESS,
//            null
//        );
//        when(
//            eventDao.findById(eq(EVENT_ID))
//        ).thenReturn(Optional.of(newEvent));
//        when(
//            cityService.findCityByName(eq(CITY_NAME))
//        ).thenReturn(Optional.of(CITY));
//        when(
//            imageService.createImage(eq(IMAGE_DATA))
//        ).thenReturn(IMAGE_ID + 1);
//
//        eventService.updateEvent(
//            EVENT_ID,
//            CITY_NAME,
//            EVENT_DATE,
//            IMAGE_DATA,
//            DESCRIPTION,
//            TITLE,
//            TIME,
//            ADDRESS,
//            LIMIT
//        );
//
//        assertEquals(IMAGE_ID + 1, newEvent.getFlyerImageId());
//    }
//    @Test
//    public void testUpdateEventEmptyImage(){
//        Event newEvent = new Event(
//            USER,
//            EVENT_DATE,
//            DESCRIPTION,
//            IMAGE_ID,
//            CITY,
//            TITLE,
//            TIME,
//            ADDRESS,
//            null
//        );
//        when(
//            eventDao.findById(eq(EVENT_ID))
//        ).thenReturn(Optional.of(newEvent));
//        when(
//            cityService.findCityByName(eq(CITY_NAME))
//        ).thenReturn(Optional.of(CITY));
//
//        eventService.updateEvent(
//            EVENT_ID,
//            CITY_NAME,
//            EVENT_DATE,
//            new byte[0],
//            DESCRIPTION,
//            TITLE,
//            TIME,
//            ADDRESS,
//            LIMIT
//        );
//
//        assertEquals(IMAGE_ID, newEvent.getFlyerImageId());
//    }
//    @Test(expected = CityNotFoundException.class)
//    public void testUpdateEventMissingCity(){
//        when(
//            eventDao.findById(eq(EVENT_ID))
//        ).thenReturn(Optional.of(EVENT));
//        when(
//            cityService.findCityByName(eq(CITY_NAME))
//        ).thenReturn(Optional.empty());
//
//        eventService.updateEvent(
//            EVENT_ID,
//            CITY_NAME,
//            EVENT_DATE,
//            null,
//            DESCRIPTION,
//            TITLE,
//            TIME,
//            ADDRESS,
//            LIMIT
//        );
//    }
//    @Test(expected = EventNotFoundException.class)
//    public void testUpdateEventMissingEvent(){
//        when(
//            eventDao.findById(eq(EVENT_ID))
//        ).thenReturn(Optional.empty());
//
//        eventService.updateEvent(
//            EVENT_ID,
//            CITY_NAME,
//            EVENT_DATE,
//            null,
//            DESCRIPTION,
//            TITLE,
//            TIME,
//            ADDRESS,
//            LIMIT
//        );
//    }

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
    public void testDeleteEventResponse(){
        EventResponse resp = new EventResponse(USER, EVENT, ADDRESS);
        eventService.deleteEventResponse(resp, DESCRIPTION);

        assertTrue(resp.isDeleted());
        assertEquals(DESCRIPTION, resp.getDeletionMessage());
    }
//
//    @Test
//    public void testFindCreatedByJourney(){
//        when(
//            eventDao.findAllWithFilters(
//                eq(USER_ID),
//                eq(null),
//                any(SortFieldEvent.class),
//                any(SortDirection.class),
//                eq(null),
//                eq(EVENT_DATE_PAST),
//                any(LocalDate.class),
//                eq(null),
//                any(),
//                any(),
//                eq(false),
//                eq(true),
//                    eq(null),
//                    eq(null),
//                    eq(null),
//                    eq(false),
//                any(PageParams.class)
//            )
//        ).thenReturn(EVENTS_PAGE);
//
//        Page<Event> events = eventService.findCreatedByJourney(
//            new Journey(USER, EVENT_DATE_PAST, EVENT_DATE, null, null),
//            PAGE_1_DEFAULT
//        );
//
//        assertNotNull(events);
//        assertEquals(EVENTS_PAGE, events);
//    }
//
//
//    @Test
//    public void testFindAttendedByJourney(){
//        when(
//            eventDao.findAllWithFilters(
//                eq(USER_ID),
//                eq(null),
//                any(SortFieldEvent.class),
//                any(SortDirection.class),
//                eq(null),
//                eq(EVENT_DATE_PAST),
//                any(LocalDate.class),
//                eq(null),
//                any(),
//                any(),
//                eq(true),
//                eq(false),
//                    eq(null),
//                    eq(null),
//                    eq(null),
//                    eq(false),
//                any(PageParams.class)
//            )
//        ).thenReturn(EVENTS_PAGE);
//
//        Page<Event> events = eventService.findAttendedByJourney(
//            new Journey(USER, EVENT_DATE_PAST, EVENT_DATE, null, null),
//            PAGE_1_DEFAULT
//        );
//
//        assertNotNull(events);
//        assertEquals(EVENTS_PAGE, events);
//    }

}