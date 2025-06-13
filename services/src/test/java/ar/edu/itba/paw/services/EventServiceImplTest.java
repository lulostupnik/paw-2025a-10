package ar.edu.itba.paw.services;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.enums.SortDirection;
import ar.edu.itba.paw.models.enums.SortFieldEvent;
import ar.edu.itba.paw.models.exceptions.CityNotFoundException;
import ar.edu.itba.paw.models.exceptions.EventNotFoundException;
import ar.edu.itba.paw.models.exceptions.InvalidException;
import ar.edu.itba.paw.models.exceptions.InvalidPaginationParamsException;
import ar.edu.itba.paw.models.exceptions.RatingNotFoundException;
import ar.edu.itba.paw.models.exceptions.UserNotFoundException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

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
    private static final int ATTENDEES = 0;
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
    private static final Event EVENT_NO_LIMIT = new Event(EVENT_ID, USER, EVENT_DATE, DESCRIPTION, IMAGE_ID, CITY, TITLE, TIME, ADDRESS, null);
    private static final List<User> USERS = List.of(USER);
    private static final List<Event> EVENTS = List.of(EVENT);
    private static final Page<Event> EVENTS_PAGE = new Page<Event>(EVENTS, 1, 1);
    private static final EventResponse REPLY = new EventResponse(RESPONSE_ID, USER, EVENT, DESCRIPTION, TIMESTAMP);
    private static final String INTEREST = "interesting";
    private static final PageParams PAGE_1_DEFAULT = new PageParams(1, 2);
    private static final int STATISTICS_CREATED_EVENTS_COUNT = 2;
    private static final int STATISTICS_ATTENDED_EVENTS_COUNT = 2;
    private static final int STATISTICS_ATTENDEE_COUNTRY_COUNT = 2;
    private static final String STATISTICS_ATTENDEE_COUNTRY = "cuntry";
    private static final EventWithStatistics EVENT_WITH_STATISTICS = new EventWithStatistics(EVENT, STATISTICS_CREATED_EVENTS_COUNT, STATISTICS_ATTENDED_EVENTS_COUNT, STATISTICS_ATTENDEE_COUNTRY, STATISTICS_ATTENDEE_COUNTRY_COUNT, true, true);
    private static final CountryAttendeeCount COUNTRY_ATTENDEE_COUNT = new CountryAttendeeCount(COUNTRY_NAME, STATISTICS_ATTENDEE_COUNTRY_COUNT);
    private static final double AVERAGE_RATING = 5.0;
    private static final double RATING_VALUE = 5.0;
    private static final int RATING_COUNT = 2;
    private static final List<EventResponse> REPLIES = List.of(REPLY);
    private static final Page<EventResponse> REPLY_PAGE = new Page<>(REPLIES, 1, 1);
    private static final Rating RATING = new Rating(USER, EVENT, RATING_VALUE);

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
            imageService.createImage(eq(IMAGE_DATA))
        ).thenReturn(IMAGE_ID);
        when(
            eventDao.create(
                eq(USER), 
                eq(CITY), 
                eq(EVENT_DATE), 
                eq(DESCRIPTION), 
                eq(IMAGE_ID), 
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
            IMAGE_DATA, 
            DESCRIPTION, 
            TITLE, 
            TIME, 
            ADDRESS, 
            LIMIT
        );

        assertNotNull(event);
        assertEquals(EVENT, event);
    }
    @Test(expected = RuntimeException.class)
    public void testCreateEventUserNotFound(){
        when(
            cityService.findCityByName(eq(CITY_NAME))
        ).thenReturn(Optional.of(CITY));
        when(
            userService.findUserByEmail(eq(EMAIL))
        ).thenReturn(Optional.empty());

        eventService.createEvent(
            EMAIL, 
            CITY_NAME, 
            EVENT_DATE, 
            IMAGE_DATA, 
            DESCRIPTION, 
            TITLE, 
            TIME, 
            ADDRESS, 
            LIMIT
        );
    }
    @Test(expected = RuntimeException.class)
    public void testCreateEventCityNotFound(){
        when(
            cityService.findCityByName(eq(CITY_NAME))
        ).thenReturn(Optional.empty());

        eventService.createEvent(
            EMAIL, 
            CITY_NAME, 
            EVENT_DATE, 
            IMAGE_DATA, 
            DESCRIPTION, 
            TITLE, 
            TIME, 
            ADDRESS, 
            LIMIT
        );
    }

    @Test
    public void testReplyToEvent(){
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
        ).thenReturn(new Page<>(USERS, 1, 2));

        eventService.replyToEvent(EMAIL, EVENT_ID, DESCRIPTION);
    }
    @Test
    public void testReplyToEventNoReplies(){
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
        ).thenReturn(new Page<User>(List.of(), 1, 0));

        eventService.replyToEvent(EMAIL, EVENT_ID, DESCRIPTION);
    }
    @Test(expected = UserNotFoundException.class)
    public void testReplyToEventNoUser(){
        when(
            eventDao.findById(EVENT_ID)
        ).thenReturn(Optional.of(EVENT));
        when(
            userService.findUserByEmail(EMAIL)
        ).thenReturn(Optional.empty());

        eventService.replyToEvent(EMAIL, EVENT_ID, DESCRIPTION);
    }
    @Test(expected = EventNotFoundException.class)
    public void testReplyToEventNoEvent(){
        when(
            eventDao.findById(EVENT_ID)
        ).thenReturn(Optional.empty());

        eventService.replyToEvent(EMAIL, EVENT_ID, DESCRIPTION);
    }

    @Test
    public void testFindEventById(){
        when(
            eventDao.findById(eq(EVENT_ID))
        ).thenReturn(Optional.of(EVENT));

        Optional<Event> maybeEvent = eventService.findEventById(EVENT_ID);

        assertNotNull(maybeEvent);
        assertTrue(maybeEvent.isPresent());
        assertEquals(EVENT, maybeEvent.get());
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
    public void testFindEventWithStatisticsDeletedEvent(){
        when(
            eventDao.findById(eq(EVENT_ID))
        ).thenReturn(Optional.of(EVENT));
        EVENT.setDeleted(true);
    
        Optional<EventWithStatistics> event = eventService.findEventWithStatistics(USER, EVENT_ID);

        assertNotNull(event);
        assertFalse(event.isPresent());    
        EVENT.setDeleted(false);
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

    @Test
    public void testSearchEventsMissingQuery(){
        when(
            eventDao.findAll(eq(PAGE_1_DEFAULT))
        ).thenReturn(EVENTS_PAGE);

        Page<Event> page = eventService.searchEvents(null, PAGE_1_DEFAULT);

        assertNotNull(page);
        assertEquals(EVENTS_PAGE, page);
    }
    @Test
    public void testSearchEventsEmptyQuery(){
        when(
            eventDao.findAll(eq(PAGE_1_DEFAULT))
        ).thenReturn(EVENTS_PAGE);

        Page<Event> page = eventService.searchEvents("", PAGE_1_DEFAULT);

        assertNotNull(page);
        assertEquals(EVENTS_PAGE, page);
    }
    @Test
    public void testSearchEventsQuery(){
        when(
            eventDao.search(eq(DESCRIPTION), eq(PAGE_1_DEFAULT))
        ).thenReturn(EVENTS_PAGE);

        Page<Event> page = eventService.searchEvents(DESCRIPTION, PAGE_1_DEFAULT);

        assertNotNull(page);
        assertEquals(EVENTS_PAGE, page);
    }

    @Test
    public void findEvents(){
        when(
            eventDao.findByUserId(eq(USER_ID), eq(PAGE_1_DEFAULT))
        ).thenReturn(EVENTS_PAGE);

        Page<Event> page = eventService.findEvents(USER_ID, PAGE_1_DEFAULT);

        assertNotNull(page);
        assertEquals(EVENTS_PAGE, page);
    }

    @Test
    public void testCreateEventAttendanceIdLimitNotExceeded(){
        when(
            eventDao.findById(eq(EVENT_ID))
        ).thenReturn(Optional.of(EVENT));
        when(
            userService.findUserById(eq(USER_ID))
        ).thenReturn(Optional.of(USER));
        when(
            attendanceDao.exists(eq(USER), eq(EVENT))
        ).thenReturn(false);

        eventService.createEventAttendance(USER_ID, EVENT_ID);

        //TODO asserts
    }
    @Test(expected = InvalidException.class)
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
    @Test
    public void testCreateEventAttendanceIdNoLimit(){
        when(
            eventDao.findById(eq(EVENT_ID))
        ).thenReturn(Optional.of(EVENT_NO_LIMIT));
        when(
            userService.findUserById(eq(USER_ID))
        ).thenReturn(Optional.of(USER));
        when(
            attendanceDao.exists(eq(USER), eq(EVENT_NO_LIMIT))
        ).thenReturn(false);

        eventService.createEventAttendance(USER_ID, EVENT_ID);

        //assertEquals(ATTENDEES + 1, EVENT_NO_LIMIT.getAttendeesCount());
        //TODO Asserts
    }
    @Test(expected = InvalidException.class)
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
    @Test(expected = InvalidException.class)
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
    public void testCreateEventAttendanceEmailLimitNotExceeded(){
        when(
            userService.findUserByEmail(eq(EMAIL))
        ).thenReturn(Optional.of(USER));
        when(
            eventDao.findById(eq(EVENT_ID))
        ).thenReturn(Optional.of(EVENT));
        when(
            userService.findUserById(eq(USER_ID))
        ).thenReturn(Optional.of(USER));
        when(
            attendanceDao.exists(eq(USER), eq(EVENT))
        ).thenReturn(false);

        eventService.createEventAttendance(EMAIL, EVENT_ID);

        //assertEquals(ATTENDEES + 1, EVENT.getAttendeesCount());
        //TODO Asserts
    }
    @Test(expected = InvalidException.class)
    public void testCreateEventAttendanceEmailLimitExceeded(){
        when(
            userService.findUserByEmail(eq(EMAIL))
        ).thenReturn(Optional.of(USER));
        when(
            eventDao.findById(eq(EVENT_ID))
        ).thenReturn(Optional.of(EVENT_FULL));
        when(
            userService.findUserById(eq(USER_ID))
        ).thenReturn(Optional.of(USER));
        when(
            attendanceDao.exists(eq(USER), eq(EVENT_FULL))
        ).thenReturn(false);

        eventService.createEventAttendance(EMAIL, EVENT_ID);
    }
    @Test
    public void testCreateEventAttendanceEmailNoLimit(){
        when(
            userService.findUserByEmail(eq(EMAIL))
        ).thenReturn(Optional.of(USER));        
        when(
            eventDao.findById(eq(EVENT_ID))
        ).thenReturn(Optional.of(EVENT_NO_LIMIT));
        when(
            userService.findUserById(eq(USER_ID))
        ).thenReturn(Optional.of(USER));
        when(
            attendanceDao.exists(eq(USER), eq(EVENT_NO_LIMIT))
        ).thenReturn(false);

        eventService.createEventAttendance(EMAIL, EVENT_ID);

        //assertEquals(ATTENDEES + 1, EVENT_NO_LIMIT.getAttendeesCount());
        //TODO Asserts
    }
    @Test(expected = InvalidException.class)
    public void testCreateEventAttendanceEmailAlreadyGoing(){
        when(
            userService.findUserByEmail(eq(EMAIL))
        ).thenReturn(Optional.of(USER));
        when(
            eventDao.findById(eq(EVENT_ID))
        ).thenReturn(Optional.of(EVENT));
        when(
            userService.findUserById(eq(USER_ID))
        ).thenReturn(Optional.of(USER));
        when(
            attendanceDao.exists(eq(USER), eq(EVENT))
        ).thenReturn(true);

        eventService.createEventAttendance(EMAIL, EVENT_ID);
    }
    @Test(expected = InvalidException.class)
    public void testCreateEventEmailAttendancePast(){
        when(
            userService.findUserByEmail(eq(EMAIL))
        ).thenReturn(Optional.of(USER));
        when(
            eventDao.findById(eq(EVENT_ID))
        ).thenReturn(Optional.of(EVENT_PAST));
        when(
            userService.findUserById(eq(USER_ID))
        ).thenReturn(Optional.of(USER));

        eventService.createEventAttendance(EMAIL, EVENT_ID);
    }
    @Test(expected = EventNotFoundException.class)
    public void testCreateEventAttendanceEmailEventNotFound(){
        when(
            userService.findUserByEmail(eq(EMAIL))
        ).thenReturn(Optional.of(USER));
        when(
            eventDao.findById(eq(EVENT_ID))
        ).thenReturn(Optional.empty());

        eventService.createEventAttendance(EMAIL, EVENT_ID);
    }
    @Test(expected = UserNotFoundException.class)
    public void testCreateEventAttendanceEmailUserNotFound(){
        when(
            userService.findUserByEmail(eq(EMAIL))
        ).thenReturn(Optional.empty());

        eventService.createEventAttendance(EMAIL, EVENT_ID);
    }

    @Test
    public void testDeleteEventAttendanceId(){
        when(
            eventDao.findById(eq(EVENT_ID))
        ).thenReturn(Optional.of(EVENT));

        eventService.deleteEventAttendance(USER_ID, EVENT_ID);
    }
    @Test(expected = InvalidException.class)
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
    public void testDeleteEventAttendanceEmail(){
        when(
            userService.findUserByEmail(eq(EMAIL))
        ).thenReturn(Optional.of(USER));
        when(
            eventDao.findById(eq(EVENT_ID))
        ).thenReturn(Optional.of(EVENT));

        eventService.deleteEventAttendance(EMAIL, EVENT_ID);
    }
    @Test(expected = NoSuchElementException.class)
    public void testDeleteEventAttendanceEmailUserNotFound(){
        when(
            userService.findUserByEmail(eq(EMAIL))
        ).thenReturn(Optional.empty());

        eventService.deleteEventAttendance(EMAIL, EVENT_ID);
    }

    @Test
    public void testRateEvent(){
        when(
            eventDao.findById(eq(EVENT_ID))
        ).thenReturn(Optional.of(EVENT));

        eventService.rateEvent(USER, EVENT_ID, ATTENDEES);
    }
    @Test(expected = EventNotFoundException.class)
    public void testRateEventNotFound(){
        when(
            eventDao.findById(eq(EVENT_ID))
        ).thenReturn(Optional.empty());

        eventService.rateEvent(USER, EVENT_ID, ATTENDEES);
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
    public void testFindRatingByUserAndEvent(){
        when(
            ratingDao.findRatingByUserAndEvent(eq(USER_ID), eq(EVENT_ID))
        ).thenReturn(Optional.of(RATING));

        Optional<Rating> rating = eventService.findRatingByUserAndEvent(USER_ID, EVENT_ID);

        assertNotNull(rating);
        assertTrue(rating.isPresent());
        assertEquals(AVERAGE_RATING, rating.get().getRating(), 0.1);
        assertEquals(RATING, rating.get());
    }
    @Test
    public void testFindRatingByUserAndEventNotFound(){
        when(
            ratingDao.findRatingByUserAndEvent(eq(USER_ID), eq(EVENT_ID))
        ).thenReturn(Optional.empty());

        Optional<Rating> rating = eventService.findRatingByUserAndEvent(USER_ID, EVENT_ID);

        assertNotNull(rating);
        assertFalse(rating.isPresent());
    }

    @Test
    public void testCountRatingsByEvent(){
        when(
            ratingDao.countRatingsByEvent(eq(EVENT_ID))
        ).thenReturn(RATING_COUNT);

        int ratings = eventService.countRatingsByEvent(EVENT_ID);

        assertEquals(RATING_COUNT, ratings);
    }

    @Test
    public void testFindRatingsAverageByEvent(){
        when(
            ratingDao.findRatingsAverageByEvent(eq(EVENT_ID))
        ).thenReturn(Optional.of(AVERAGE_RATING));
        
        Optional<Double> rating = eventService.findRatingsAverageByEvent(EVENT_ID);
        
        assertNotNull(rating);
        assertTrue(rating.isPresent());
        assertEquals(AVERAGE_RATING, rating.get(), 0.1);
    }
    @Test
    public void testFindRatingsAverageByEventNoRating(){
        when(
            ratingDao.findRatingsAverageByEvent(eq(EVENT_ID))
        ).thenReturn(Optional.empty());
        
        Optional<Double> rating = eventService.findRatingsAverageByEvent(EVENT_ID);
        
        assertNotNull(rating);
        assertFalse(rating.isPresent());
    }

    @Test
    public void testCountEventAttendees(){
        when(
            eventDao.findById(eq(EVENT_ID))
        ).thenReturn(Optional.of(EVENT));

        int attendees = eventService.countEventAttendees(EVENT_ID);

        assertEquals(0, attendees);
    }
    @Test(expected = EventNotFoundException.class)
    public void testCountEventAttendeesNotFound(){
        when(
            eventDao.findById(eq(EVENT_ID))
        ).thenReturn(Optional.empty());

        eventService.countEventAttendees(EVENT_ID);
    }

    @Test
    public void testFindEventsByAttendee(){
        when(
            eventDao.findAllEventsByAttendee(eq(USER_ID), eq(PAGE_1_DEFAULT))
        ).thenReturn(EVENTS_PAGE);

        Page<Event> events = eventService.findEventsByAttendee(USER_ID, PAGE_1_DEFAULT);

        assertNotNull(events);
        assertEquals(EVENTS_PAGE, events);
    }

    @Test
    public void testFindUpcomingEventsByAttendee(){
        when(
            eventDao.findAllWithFilters(
                eq(USER_ID), 
                eq(null),
                eq(null),
                any(SortDirection.class),
                eq(null),
                any(LocalDate.class),
                eq(null),
                eq(null),
                eq(true),
                eq(false),
                eq(PAGE_1_DEFAULT)
            )
        ).thenReturn(EVENTS_PAGE);

        Page<Event> events = eventService.findUpcomingEventsByAttendee(USER_ID, PAGE_1_DEFAULT);

        assertNotNull(events);
        assertEquals(EVENTS_PAGE, events);
    }

    @Test
    public void testFindFinishedEventsByAttendee(){
        when(
            eventDao.findAllWithFilters(
                eq(USER_ID), 
                eq(null),
                eq(null),
                any(SortDirection.class),
                eq(null),
                eq(null),
                any(LocalDate.class),
                eq(null),
                eq(true),
                eq(false),
                eq(PAGE_1_DEFAULT)
            )
        ).thenReturn(EVENTS_PAGE);

        Page<Event> events = eventService.findFinishedEventsByAttendee(USER_ID, PAGE_1_DEFAULT);

        assertNotNull(events);
        assertEquals(EVENTS_PAGE, events);
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
        ).thenReturn(new Page<>(List.of(), 1, 0));
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
    public void testFindTopEvents(){
        when(
            eventDao.findTop(eq(PAGE_1_DEFAULT))
        ).thenReturn(EVENTS_PAGE);

        List<Event> events = eventService.findTopEvents(2);

        assertNotNull(events);
        assertEquals(EVENTS, events);
    }
    @Test(expected = InvalidPaginationParamsException.class)
    public void testFindTopEventsWrongLimit(){
        eventService.findTopEvents(0);
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
    public void testSearchEventsWithFiltersNoUser(){
        when(
            eventDao.findAllWithFilters(
                eq(null),
                eq(TITLE),
                any(SortFieldEvent.class),
                any(SortDirection.class),
                eq(CITY_NAME),
                any(LocalDate.class),
                any(LocalDate.class),
                eq(INTEREST),
                eq(true),
                eq(false),
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
        when(
            eventDao.findAllWithFilters(
                eq(USER_ID),
                eq(TITLE),
                eq(SortFieldEvent.from(null)),
                eq(SortDirection.from(null)),
                eq(CITY_NAME),
                any(LocalDate.class),
                any(LocalDate.class),
                eq(INTEREST),
                eq(true),
                eq(false),
                eq(PAGE_1_DEFAULT)
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
    @Test
    public void testSearchEventsWithFiltersNotUpcomingPast(){
        when(
            eventDao.findAllWithFilters(
                eq(USER_ID),
                eq(TITLE),
                eq(SortFieldEvent.from(null)),
                eq(SortDirection.from(null)),
                eq(CITY_NAME),
                any(LocalDate.class),
                any(LocalDate.class),
                eq(INTEREST),
                eq(true),
                eq(false),
                eq(PAGE_1_DEFAULT)
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
            true,
            false,
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
                eq(USER_ID),
                eq(TITLE),
                eq(SortFieldEvent.from(null)),
                eq(SortDirection.from(null)),
                eq(CITY_NAME),
                any(LocalDate.class),
                any(LocalDate.class),
                eq(INTEREST),
                eq(true),
                eq(false),
                eq(PAGE_1_DEFAULT)
            )
        ).thenReturn(EVENTS_PAGE);

        Page<Event> page = eventService.searchEventsWithFilters(
            TITLE,
            USER,
            SortFieldEvent.from(""),
            SortDirection.from(""),
            CITY_NAME,
            LocalDate.now().plusDays(-10),
            LocalDate.now(),
            INTEREST,
            true,
            true,
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
                eq(USER_ID),
                eq(TITLE),
                eq(SortFieldEvent.from(null)),
                eq(SortDirection.from(null)),
                eq(CITY_NAME),
                any(LocalDate.class),
                any(LocalDate.class),
                eq(INTEREST),
                eq(true),
                eq(false),
                eq(PAGE_1_DEFAULT)
            )
        ).thenReturn(EVENTS_PAGE);

        Page<Event> page = eventService.searchEventsWithFilters(
            TITLE,
            USER,
            SortFieldEvent.from(""),
            SortDirection.from(""),
            CITY_NAME,
            LocalDate.now().plusDays(10),
            LocalDate.now().plusDays(-10),
            INTEREST,
            true,
            true,
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
                eq(USER_ID),
                eq(TITLE),
                eq(SortFieldEvent.from(null)),
                eq(SortDirection.from(null)),
                eq(CITY_NAME),
                any(LocalDate.class),
                any(LocalDate.class),
                eq(INTEREST),
                eq(true),
                eq(false),
                eq(PAGE_1_DEFAULT)
            )
        ).thenReturn(EVENTS_PAGE);

        Page<Event> page = eventService.searchEventsWithFilters(
            TITLE,
            USER,
            SortFieldEvent.from(""),
            SortDirection.from(""),
            CITY_NAME,
            null,
            null,
            INTEREST,
            true,
            true,
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

        eventService.updateEvent(
            EVENT_ID, 
            "CITY_NAME", 
            EVENT_DATE.plusDays(1), 
            null, 
            "DESCRIPTION", 
            "TITLE", 
            TIME.plusSeconds(10), 
            "ADDRESS", 
            null
        );

        assertEquals("CITY_NAME", newEvent.getCity().getName());   
        assertEquals(EVENT_DATE.plusDays(1), newEvent.getDate()); 
        assertEquals(TIME.plusSeconds(10), newEvent.getTime()); 
        assertEquals("DESCRIPTION", newEvent.getDescription());    
        assertEquals("TITLE", newEvent.getTitle());     
        assertEquals("ADDRESS", newEvent.getAddress());  
        assertNull(newEvent.getAttendeesLimit()); 
    }
    @Test
    public void testUpdateEventImage(){
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
            cityService.findCityByName(eq(CITY_NAME))
        ).thenReturn(Optional.of(CITY));
        when(
            imageService.createImage(eq(IMAGE_DATA))
        ).thenReturn(IMAGE_ID + 1);

        eventService.updateEvent(
            EVENT_ID, 
            CITY_NAME, 
            EVENT_DATE, 
            IMAGE_DATA, 
            DESCRIPTION, 
            TITLE, 
            TIME, 
            ADDRESS, 
            LIMIT
        );

        assertEquals(IMAGE_ID + 1, newEvent.getFlyerImageId());
    }
    @Test
    public void testUpdateEventEmptyImage(){
        Event newEvent = new Event(
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
            cityService.findCityByName(eq(CITY_NAME))
        ).thenReturn(Optional.of(CITY));

        eventService.updateEvent(
            EVENT_ID, 
            CITY_NAME, 
            EVENT_DATE, 
            new byte[0], 
            DESCRIPTION, 
            TITLE, 
            TIME, 
            ADDRESS, 
            LIMIT
        );

        assertEquals(IMAGE_ID, newEvent.getFlyerImageId());
    }
    @Test(expected = CityNotFoundException.class)
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
            null, 
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
            null, 
            DESCRIPTION, 
            TITLE, 
            TIME, 
            ADDRESS, 
            LIMIT
        );
    }

    @Test
    public void testDeleteEvent(){
        when(
            eventDao.findById(eq(EVENT_ID))
        ).thenReturn(Optional.of(EVENT));

        eventService.deleteEvent(EVENT_ID, DESCRIPTION);

        assertTrue(EVENT.isDeleted());
        EVENT.setDeleted(false);
        assertEquals(DESCRIPTION, EVENT.getDeletionMessage());
        EVENT.setDeletionMessage(null);
    }
    @Test
    public void testDeleteEventEmptyMessage(){
        when(
            eventDao.findById(eq(EVENT_ID))
        ).thenReturn(Optional.of(EVENT));

        eventService.deleteEvent(EVENT_ID, "");

        assertTrue(EVENT.isDeleted());
        EVENT.setDeleted(false);
        assertNull(EVENT.getDeletionMessage());
    }
    @Test
    public void testDeleteEventMissingMessage(){
        when(
            eventDao.findById(eq(EVENT_ID))
        ).thenReturn(Optional.of(EVENT));

        eventService.deleteEvent(EVENT_ID, null);

        assertTrue(EVENT.isDeleted());
        EVENT.setDeleted(false);
        assertNull(EVENT.getDeletionMessage());
    }
    @Test(expected = EventNotFoundException.class)
    public void testDeleteEventMissing(){
        when(
            eventDao.findById(eq(EVENT_ID))
        ).thenReturn(Optional.empty());

        eventService.deleteEvent(EVENT_ID, DESCRIPTION);
    }

    @Test
    public void testDeleteEventResponse(){
        eventService.deleteEventResponse(REPLY, DESCRIPTION);

        assertTrue(REPLY.isDeleted());
        REPLY.setDeleted(false);
        assertEquals(DESCRIPTION, REPLY.getDeletionMessage());
        REPLY.setDeletionMessage(null);
    }

    @Test
    public void testCountEventResponses(){
        when(
            replyDao.countByEventId(eq(EVENT_ID))
        ).thenReturn(ATTENDEES);

        int replies = eventService.countEventResponses(EVENT_ID);

        assertEquals(ATTENDEES, replies);
    }

    @Test
    public void testFindEventResponses(){
        when(
            replyDao.listAllByEventId(eq(EVENT_ID), eq(PAGE_1_DEFAULT))
        ).thenReturn(REPLY_PAGE);

        Page<EventResponse> replies = eventService.findEventResponses(
            EVENT_ID, 
            PAGE_1_DEFAULT
        );

        assertNotNull(replies);
        assertEquals(REPLY_PAGE, replies);
    }

    @Test
    public void testFindEventResponseById(){
        when(
            replyDao.findById(eq(RESPONSE_ID))
        ).thenReturn(Optional.of(REPLY));

        Optional<EventResponse> maybeReply = eventService.findEventResponseById(RESPONSE_ID);

        assertNotNull(maybeReply);
        assertTrue(maybeReply.isPresent());
        assertEquals(REPLY, maybeReply.get());
    }

    @Test
    public void testFindJourneyEvents(){
        when(
            eventDao.findAllWithFilters(
                eq(USER_ID), 
                eq(null), 
                eq(SortFieldEvent.DATE),
                eq(SortDirection.ASC), 
                eq(null), 
                eq(EVENT_DATE_PAST), 
                any(LocalDate.class), 
                eq(null), 
                eq(true), 
                eq(false), 
                eq(PAGE_1_DEFAULT)
            )
        ).thenReturn(EVENTS_PAGE);

        Page<Event> events = eventService.findJourneyEvents(
            new Journey(USER, EVENT_DATE_PAST, EVENT_DATE, UNI, DESCRIPTION), 
            PAGE_1_DEFAULT
        );

        assertNotNull(events);
        assertEquals(EVENTS_PAGE, events);
    }

    @Test
    public void testFindCreatedByJourney(){
        when(
            eventDao.findAllWithFilters(
                eq(USER_ID), 
                eq(null), 
                any(SortFieldEvent.class), 
                any(SortDirection.class), 
                eq(null), 
                eq(EVENT_DATE_PAST), 
                any(LocalDate.class),
                eq(null), 
                eq(false), 
                eq(true), 
                any(PageParams.class)
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
    public void testFindAttendedByJourney(){
        when(
            eventDao.findAllWithFilters(
                eq(USER_ID), 
                eq(null), 
                any(SortFieldEvent.class), 
                any(SortDirection.class), 
                eq(null), 
                eq(EVENT_DATE_PAST), 
                any(LocalDate.class),
                eq(null), 
                eq(true), 
                eq(false),  
                any(PageParams.class)
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
    public void testCountEventsCreatedByUser(){
        when(
            eventDao.countEventsCreatedByUser(eq(USER_ID))
        ).thenReturn(ATTENDEES);

        int events = eventService.countEventsCreatedByUser(USER_ID);

        assertEquals(ATTENDEES, events);
    }

    @Test
    public void testCountEventsAttendedByUser(){
        when(
            attendanceDao.countEventsAttendedByUser(eq(USER_ID))
        ).thenReturn(ATTENDEES);

        int events = eventService.countEventsAttendedByUser(USER_ID);

        assertEquals(ATTENDEES, events);
    }

    @Test
    public void testSendEventReminders(){
        when(
            eventDao.findAllBetweenDates(
                any(LocalDate.class), 
                any(LocalDate.class), 
                any(PageParams.class)
            )
        ).thenReturn(new Page<>(EVENTS, 1, 2));
        when(
            attendanceDao.findAttendeesByEventId(
                eq(EVENT_ID), 
                any(PageParams.class)
            )
        ).thenReturn(new Page<>(USERS, 1, 2));

        eventService.sendEventReminders();
    }
    @Test
    public void testSendEventRemindersNoAttendees(){
        when(
            eventDao.findAllBetweenDates(
                any(LocalDate.class), 
                any(LocalDate.class), 
                any(PageParams.class)
            )
        ).thenReturn(EVENTS_PAGE);
        when(
            attendanceDao.findAttendeesByEventId(
                eq(EVENT_ID), 
                any(PageParams.class)
            )
        ).thenReturn(new Page<>(List.of(), 1, 0));

        eventService.sendEventReminders();
    }
    @Test
    public void testSendEventRemindersNoEvents(){
        when(
            eventDao.findAllBetweenDates(
                any(LocalDate.class), 
                any(LocalDate.class), 
                any(PageParams.class)
            )
        ).thenReturn(new Page<>(List.of(), 1, 0));

        eventService.sendEventReminders();
    }
}