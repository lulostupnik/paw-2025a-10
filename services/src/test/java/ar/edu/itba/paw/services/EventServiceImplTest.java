package ar.edu.itba.paw.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Optional;

import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.enums.SortDirection;
import ar.edu.itba.paw.models.enums.SortFieldEvent;
import ar.edu.itba.paw.models.exceptions.InvalidException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import ar.edu.itba.paw.interfaces.persistence.EventAttendanceDao;
import ar.edu.itba.paw.interfaces.persistence.EventDao;
import ar.edu.itba.paw.interfaces.persistence.EventResponseDao;
import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.services.CityService;
import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.interfaces.services.ImageService;
import ar.edu.itba.paw.interfaces.services.UserService;

@RunWith(MockitoJUnitRunner.class)
public class EventServiceImplTest {

    private static final long EVENT_ID = 0;
    private static final long USER_ID = 1;
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
    private static final byte[] IMAGE_DATA = new byte[0];
    private static final String DESCRIPTION = "description";
    private static final String TITLE = "title";
    private static final LocalTime TIME = LocalTime.now();
    private static final LocalDateTime TIMESTAMP = LocalDateTime.now();
    private static final String ADDRESS = "address";
    private static final int LIMIT = 10;
    private static final int ATTENDEES = 8;
    private static final Locale LOCALE = Locale.of("en");
    private static final boolean BLOCKED = false;
    private static final Career CAREER = new Career(CAREER_ID, CAREER_NAME);
    private static final City CITY = new City(CITY_NAME, COUNTRY_NAME, CITY_ID);
    private static final University UNI = new University(UNI_ID, UNI_NAME, UNI_ABBR, CITY);
    private static final User USER = new User(USER_ID, EMAIL, USERNAME, FIRSTNAME, LASTNAME, UNI, CAREER, IMAGE_ID, LOCALE, BLOCKED);
    private static final Event EVENT = new Event(EVENT_ID, USER, EVENT_DATE, DESCRIPTION, IMAGE_ID, CITY, TITLE, Optional.of(TIME), ADDRESS, Optional.of(LIMIT), ATTENDEES);
    private static final List<User> USERS = List.of(USER);
    private static final List<Event> EVENTS = List.of(EVENT);
    private static final Page<Event> EVENTS_PAGE = new Page<Event>(EVENTS, 1, 1);
    private static final EventResponse RESPONSE = new EventResponse(RESPONSE_ID, USER_ID, USERNAME, EVENT_ID, DESCRIPTION, TIMESTAMP);
    private static final String INTEREST = "interesting";
    private static final PageParams PAGE_1_DEFAULT = new PageParams(1, 2);
    private static final int STATISTICS_CREATED_EVENTS_COUNT = 2;
    private static final int STATISTICS_ATTENDED_EVENTS_COUNT = 2;
    private static final int STATISTICS_ATTENDEE_COUNTRY_COUNT = 2;
    private static final String STATISTICS_ATTENDEE_COUNTRY = "cuntry";
    private static final EventWithStatistics EVENT_WITH_STATISTICS = new EventWithStatistics(EVENT, STATISTICS_CREATED_EVENTS_COUNT, STATISTICS_ATTENDED_EVENTS_COUNT, STATISTICS_ATTENDEE_COUNTRY, STATISTICS_ATTENDEE_COUNTRY_COUNT, true, true);
    private static final EventWithUserInfo EVENT_WITH_USER_INFO = new EventWithUserInfo(EVENT, true, true);
    private static final CountryAttendeeCount COUNTRY_ATTENDEE_COUNT = new CountryAttendeeCount(COUNTRY_NAME, STATISTICS_ATTENDEE_COUNTRY_COUNT);

    @InjectMocks
    EventServiceImpl eventService;

    @Mock
    EventDao eventDao;
    
    @Mock
    UserService userService;

    @Mock
    EmailService emailService;
    @Mock
    ImageService imageService;
    @Mock
    CityService cityService;
    @Mock
    EventAttendanceDao attendanceDao;
    @Mock
    UserDao userDao;
    @Mock
    EventResponseDao replyDao;

    @Test
    public void testCreateEvent(){
        Mockito.when(
            cityService.findCityByName(Mockito.eq(CITY_NAME))
        ).thenReturn(Optional.of(CITY));
        Mockito.when(
            userService.findUserByEmail(Mockito.eq(EMAIL))
        ).thenReturn(Optional.of(USER));
        Mockito.when(
            imageService.createImage(Mockito.eq(IMAGE_DATA))
        ).thenReturn(IMAGE_ID);
        Mockito.when(
            eventDao.create(Mockito.eq(USER), Mockito.eq(CITY), Mockito.eq(EVENT_DATE), Mockito.eq(DESCRIPTION), Mockito.eq(IMAGE_ID), Mockito.eq(TITLE), Mockito.eq(TIME), Mockito.eq(ADDRESS), Mockito.eq(LIMIT))
        ).thenReturn(EVENT);

        Event event = eventService.createEvent(EMAIL, CITY_NAME, EVENT_DATE, IMAGE_DATA, DESCRIPTION, TITLE, TIME, ADDRESS, LIMIT);

        assertNotNull(event);
        assertEquals(EVENT, event);
    }
    @Test(expected = RuntimeException.class)
    public void testCreateEventUserNotFound(){
        Mockito.when(
            cityService.findCityByName(Mockito.eq(CITY_NAME))
        ).thenReturn(Optional.of(CITY));
        Mockito.when(
            userService.findUserByEmail(Mockito.eq(EMAIL))
        ).thenReturn(Optional.empty());

        eventService.createEvent(EMAIL, CITY_NAME, EVENT_DATE, IMAGE_DATA, DESCRIPTION, TITLE, TIME, ADDRESS, LIMIT);
    }
    @Test(expected = RuntimeException.class)
    public void testCreateEventCityNotFound(){
        Mockito.when(
            cityService.findCityByName(Mockito.eq(CITY_NAME))
        ).thenReturn(Optional.empty());

        eventService.createEvent(EMAIL, CITY_NAME, EVENT_DATE, IMAGE_DATA, DESCRIPTION, TITLE, TIME, ADDRESS, LIMIT);
    }

    @Test
    public void testReplyToEvent(){
        Mockito.when(
            eventDao.findById(EVENT_ID)
        ).thenReturn(Optional.of(EVENT));
        Mockito.when(
            userService.findUserByEmail(EMAIL)
        ).thenReturn(Optional.of(USER));
        Mockito.when(
            userDao.findAllEventResponders(Mockito.eq(EVENT_ID))
        ).thenReturn(USERS);

        eventService.replyToEvent(EMAIL, EVENT_ID, DESCRIPTION);
    }
    @Test(expected = RuntimeException.class)
    public void testReplyToEventNoUser(){
        Mockito.when(
            eventDao.findById(EVENT_ID)
        ).thenReturn(Optional.of(EVENT));
        Mockito.when(
            userService.findUserByEmail(EMAIL)
        ).thenReturn(Optional.empty());

        eventService.replyToEvent(EMAIL, EVENT_ID, DESCRIPTION);
    }
    @Test(expected = RuntimeException.class)
    public void testReplyToEventNoEvent(){
        Mockito.when(
            eventDao.findById(EVENT_ID)
        ).thenReturn(Optional.empty());

        eventService.replyToEvent(EMAIL, EVENT_ID, DESCRIPTION);
    }

    @Test
    public void testFindEventById(){
        Mockito.when(
            eventDao.findById(Mockito.eq(EVENT_ID))
        ).thenReturn(Optional.of(EVENT));

        Optional<Event> maybeEvent = eventService.findEventById(EVENT_ID);

        assertNotNull(maybeEvent);
        assertTrue(maybeEvent.isPresent());
        assertEquals(EVENT, maybeEvent.get());
    }

    @Test
    public void testFindEventWithStatistics(){
        Mockito.when(
            eventDao.findEventWithUserInfo(Mockito.eq(USER_ID), Mockito.eq(EVENT_ID))
        ).thenReturn(Optional.of(EVENT_WITH_USER_INFO));
        Mockito.when(
            eventDao.countEventsCreatedByUser(Mockito.eq(USER_ID))
        ).thenReturn(STATISTICS_CREATED_EVENTS_COUNT);
        Mockito.when(
            eventDao.countEventsAttendedByUser(Mockito.eq(USER_ID))
        ).thenReturn(STATISTICS_ATTENDED_EVENTS_COUNT);
        Mockito.when(
            eventDao.findTopAttendeeCountry(Mockito.eq(EVENT_ID)) 
        ).thenReturn(Optional.of(COUNTRY_ATTENDEE_COUNT));

        Optional<EventWithStatistics> event = eventService.findEventWithStatistics(USER, EVENT_ID);

        assertNotNull(event);
        assertTrue(event.isPresent());
        assertEquals(EVENT_WITH_STATISTICS.getAttendedEventsCount(), event.get().getAttendedEventsCount());
        assertEquals(EVENT_WITH_STATISTICS.getCreatedEventsCount(), event.get().getCreatedEventsCount());
        assertEquals(EVENT_WITH_STATISTICS.getEvent(), event.get().getEvent());
        assertEquals(EVENT_WITH_STATISTICS.getTopAttendeeCountry(), event.get().getTopAttendeeCountry());
        assertEquals(EVENT_WITH_STATISTICS.getTopAttendeeCountryCount(), event.get().getTopAttendeeCountryCount());
    }
    @Test
    public void testFindEventWithStatisticsNoEventUser(){
        Mockito.when(
            eventDao.findEventWithUserInfo(Mockito.eq(USER_ID), Mockito.eq(EVENT_ID))
        ).thenReturn(Optional.empty());

        Optional<EventWithStatistics> event = eventService.findEventWithStatistics(USER, EVENT_ID);

        assertNotNull(event);
        assertFalse(event.isPresent());
    }
        @Test
    public void testFindEventWithStatisticsNoUser(){
        Mockito.when(
            eventDao.findById(Mockito.eq(EVENT_ID))
        ).thenReturn(Optional.of(EVENT));

        Optional<EventWithStatistics> event = eventService.findEventWithStatistics(null, EVENT_ID);

        assertNotNull(event);
        assertTrue(event.isPresent());
    }
    @Test
    public void testFindEventWithStatisticsNoUserNoEvent(){
        Mockito.when(
            eventDao.findById(Mockito.eq(EVENT_ID))
        ).thenReturn(Optional.empty());

        Optional<EventWithStatistics> event = eventService.findEventWithStatistics(null, EVENT_ID);

        assertNotNull(event);
        assertFalse(event.isPresent());
    }

    @Test
    public void testFindEventsPagedEmail(){
        Mockito.when(
            eventDao.findByUserEmail(Mockito.eq(EMAIL), Mockito.eq(PAGE_1_DEFAULT))
        ).thenReturn(EVENTS_PAGE);

        Page<Event> page = eventService.findEvents(EMAIL, PAGE_1_DEFAULT);

        assertNotNull(page);
        assertEquals(EVENTS_PAGE, page);
    }    

    @Test
    public void testSearchEventsMissingQuery(){
        Mockito.when(
            eventDao.findAll(Mockito.eq(PAGE_1_DEFAULT))
        ).thenReturn(EVENTS_PAGE);

        Page<Event> page = eventService.searchEvents(null, PAGE_1_DEFAULT);

        assertNotNull(page);
        assertEquals(EVENTS_PAGE, page);
    }
    @Test
    public void testSearchEventsEmptyQuery(){
        Mockito.when(
            eventDao.findAll(Mockito.eq(PAGE_1_DEFAULT))
        ).thenReturn(EVENTS_PAGE);

        Page<Event> page = eventService.searchEvents("", PAGE_1_DEFAULT);

        assertNotNull(page);
        assertEquals(EVENTS_PAGE, page);
    }
    @Test
    public void testSearchEventsQuery(){
        Mockito.when(
            eventDao.search(Mockito.eq(DESCRIPTION), Mockito.eq(PAGE_1_DEFAULT))
        ).thenReturn(EVENTS_PAGE);

        Page<Event> page = eventService.searchEvents(DESCRIPTION, PAGE_1_DEFAULT);

        assertNotNull(page);
        assertEquals(EVENTS_PAGE, page);
    }


    @Test
    public void testCreateEventAttendanceIdLimitNotExceeded(){
        Mockito.when(
            eventDao.findById(Mockito.eq(EVENT_ID))
        ).thenReturn(Optional.of(EVENT));
        Mockito.when(
            attendanceDao.exists(Mockito.eq(USER_ID), Mockito.eq(EVENT_ID))
        ).thenReturn(false);
        Mockito.when(
            eventDao.findAttendanceLimitById(Mockito.eq(EVENT_ID))
        ).thenReturn(Optional.of(LIMIT));
        Mockito.when(
            attendanceDao.countByEventId(Mockito.eq(EVENT_ID))
        ).thenReturn(ATTENDEES);

        eventService.createEventAttendance(USER_ID, EVENT_ID);
    }
    @Test(expected = InvalidException.class)
    public void testCreateEventAttendanceIdLimitExceeded(){
        Mockito.when(
            eventDao.findById(Mockito.eq(EVENT_ID))
        ).thenReturn(Optional.of(EVENT));
        Mockito.when(
            attendanceDao.exists(Mockito.eq(USER_ID), Mockito.eq(EVENT_ID))
        ).thenReturn(false);
        Mockito.when(
            eventDao.findAttendanceLimitById(Mockito.eq(EVENT_ID))
        ).thenReturn(Optional.of(LIMIT));
        Mockito.when(
            attendanceDao.countByEventId(Mockito.eq(EVENT_ID))
        ).thenReturn(LIMIT);

        eventService.createEventAttendance(USER_ID, EVENT_ID);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateEventAttendanceIdAttending(){
        Mockito.when(
            eventDao.findById(Mockito.eq(EVENT_ID))
        ).thenReturn(Optional.empty());

        eventService.createEventAttendance(USER_ID, EVENT_ID);
    }
    @Test(expected = InvalidException.class)
    public void testCreateEventAttendanceIdNotFuture(){
        Mockito.when(
            eventDao.findById(Mockito.eq(EVENT_ID))
        ).thenReturn(Optional.of(new Event(0l, null, LocalDate.now().plusDays(-1), null, 0l, null, null ,Optional.empty(), null, null, 0)));

        eventService.createEventAttendance(USER_ID, EVENT_ID);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeleteEventAttendanceIdNotFound(){
        Mockito.when(
            eventDao.findById(Mockito.eq(EVENT_ID))
        ).thenReturn(Optional.empty());

        eventService.deleteEventAttendance(USER_ID, EVENT_ID);
    }
    @Test(expected = NoSuchElementException.class)
    public void testDeleteEventAttendanceEmailNotFound(){
        Mockito.when(
            userService.findUserByEmail(Mockito.eq(EMAIL))
        ).thenReturn(Optional.empty());

        eventService.deleteEventAttendance(EMAIL, EVENT_ID);
    }

    
    @Test
    public void testFindRecommendedEvents(){
        Mockito.when(
            eventDao.findRecommended(Mockito.eq(USER_ID), Mockito.eq(PAGE_1_DEFAULT))
        ).thenReturn(EVENTS_PAGE);

        List<Event> userEvents = eventService.findRecommendedEvents(USER_ID, 2);

        assertNotNull(userEvents);
        assertEquals(EVENTS, userEvents);
    }
    @Test
    public void testFindRecommendedEventsMissing(){
        Mockito.when(
            eventDao.findRecommended(Mockito.eq(USER_ID), Mockito.eq(PAGE_1_DEFAULT))
        ).thenReturn(new Page<>(List.of(), 1, 0));
        Mockito.when(
            eventDao.findTopByUser(Mockito.eq(USER_ID), Mockito.eq(PAGE_1_DEFAULT))
        ).thenReturn(EVENTS_PAGE);

        List<Event> userevents = eventService.findRecommendedEvents(USER_ID, 2);

        assertNotNull(userevents);
        assertEquals(EVENTS, userevents);
    }
    @Test(expected = IllegalArgumentException.class)
    public void testFindRecommendedEventsWrongLimit(){
        eventService.findRecommendedEvents(USER_ID, 0);
    }

    @Test
    public void testFindTopEvents(){
        Mockito.when(
            eventDao.findTop(Mockito.eq(PAGE_1_DEFAULT))
        ).thenReturn(EVENTS_PAGE);

        List<Event> events = eventService.findTopEvents(2);

        assertNotNull(events);
        assertEquals(EVENTS, events);
    }
    @Test(expected = IllegalArgumentException.class)
    public void testFindTopEventsWrongLimit(){
        eventService.findTopEvents(0);
    }

    @Test
    public void testIsEventOwnedByUser(){
        Mockito.when(
            eventDao.findById(Mockito.eq(EVENT_ID))
        ).thenReturn(Optional.of(EVENT));

        boolean isOwned = eventService.isEventOwnedByUser(EMAIL, EVENT_ID);

        assertTrue(isOwned);
    }
    @Test
    public void testIsEventOwnedByUserNotOwned(){
        Mockito.when(
            eventDao.findById(Mockito.eq(EVENT_ID))
        ).thenReturn(Optional.of(EVENT));

        boolean isOwned = eventService.isEventOwnedByUser("EMAIL", EVENT_ID);

        assertFalse(isOwned);
    }
    @Test
    public void testIsEventOwnedByUserMissingEvent(){
        Mockito.when(
            eventDao.findById(Mockito.eq(EVENT_ID))
        ).thenReturn(Optional.empty());

        boolean isOwned = eventService.isEventOwnedByUser(EMAIL, EVENT_ID);

        assertFalse(isOwned);
    }

    @Test
    public void testSearchEventsWithFiltersNoUser(){
        Mockito.when(
            eventDao.findAllWithFilters(
                Mockito.eq(null),
                Mockito.eq(TITLE),
                Mockito.eq(SortFieldEvent.ATTENDEES),
                Mockito.eq(SortDirection.DESC),
                Mockito.eq(CITY_NAME),
                Mockito.eq(EVENT_DATE),
                Mockito.eq(EVENT_DATE),
                Mockito.eq(INTEREST),
                Mockito.eq(false),
                Mockito.eq(true),
                Mockito.eq(true), 
                Mockito.eq(PAGE_1_DEFAULT)
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
            false, 
            true, 
            true, 
            PAGE_1_DEFAULT
        );

        assertNotNull(page);
        assertEquals(EVENTS_PAGE, page);
    }
    @Test
    public void testSearchEventsWithFilters(){
        Mockito.when(
            eventDao.findAllWithFilters(
                Mockito.eq(USER_ID),
                Mockito.eq(TITLE),
                Mockito.eq(SortFieldEvent.from(null)),
                Mockito.eq(SortDirection.from(null)),
                Mockito.eq(CITY_NAME),
                Mockito.eq(EVENT_DATE),
                Mockito.eq(EVENT_DATE),
                Mockito.eq(INTEREST),
                Mockito.eq(false),
                Mockito.eq(true),
                Mockito.eq(true), 
                Mockito.eq(PAGE_1_DEFAULT)
            )
        ).thenReturn(EVENTS_PAGE);

        Page<Event> page = eventService.searchEventsWithFilters(
            TITLE, 
            USER, 
            SortFieldEvent.from(""), 
            SortDirection.from(""), 
            CITY_NAME, 
            EVENT_DATE, 
            EVENT_DATE, 
            INTEREST, 
            false, 
            true, 
            true, 
            PAGE_1_DEFAULT
        );

        assertNotNull(page);
        assertEquals(EVENTS_PAGE, page);
    }

    @Test(expected = RuntimeException.class)
    public void testUpdateEventMissingCity(){
        Mockito.when(
            eventDao.findById(Mockito.eq(EVENT_ID))
        ).thenReturn(Optional.of(EVENT));
        Mockito.when(
            cityService.findCityByName(Mockito.eq(CITY_NAME))
        ).thenReturn(Optional.empty());

        eventService.updateEvent(EVENT_ID, CITY_NAME, EVENT_DATE, null, DESCRIPTION, TITLE, TIME, ADDRESS, LIMIT);
    }
    @Test(expected = IllegalArgumentException.class)
    public void testUpdateEventMissingEvent(){
        Mockito.when(
            eventDao.findById(Mockito.eq(EVENT_ID))
        ).thenReturn(Optional.empty());

        eventService.updateEvent(EVENT_ID, CITY_NAME, EVENT_DATE, null, DESCRIPTION, TITLE, TIME, ADDRESS, LIMIT);
    }

    @Test(expected = RuntimeException.class)
    public void testDeleteEventMissing(){
        Mockito.when(
            eventDao.findById(Mockito.eq(EVENT_ID))
        ).thenReturn(Optional.empty());

        eventService.deleteEvent(EVENT_ID, DESCRIPTION);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeleteEventResponseNoUser(){
        Mockito.when(
            replyDao.findById(Mockito.eq(RESPONSE_ID))
        ).thenReturn(Optional.of(RESPONSE));
        Mockito.when(
            eventDao.findById(Mockito.eq(EVENT_ID))
        ).thenReturn(Optional.of(EVENT));
        Mockito.when(
            userService.findUserById(Mockito.eq(USER_ID))
        ).thenReturn(Optional.empty());

        eventService.deleteEventResponse(RESPONSE_ID, DESCRIPTION);
    }
    @Test(expected = IllegalStateException.class)
    public void testDeleteEventResponseNoEvent(){
        Mockito.when(
            replyDao.findById(Mockito.eq(RESPONSE_ID))
        ).thenReturn(Optional.of(RESPONSE));
        Mockito.when(
            eventDao.findById(Mockito.eq(EVENT_ID))
        ).thenReturn(Optional.empty());

        eventService.deleteEventResponse(RESPONSE_ID, DESCRIPTION);
    }
    @Test(expected = IllegalArgumentException.class)
    public void testDeleteEventResponseNoResponse(){
        Mockito.when(
            replyDao.findById(Mockito.eq(RESPONSE_ID))
        ).thenReturn(Optional.empty());

        eventService.deleteEventResponse(RESPONSE_ID, DESCRIPTION);
    }


}