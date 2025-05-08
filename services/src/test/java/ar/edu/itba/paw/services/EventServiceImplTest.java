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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import ar.edu.itba.paw.interfaces.persistence.EventAttendanceDao;
import ar.edu.itba.paw.interfaces.persistence.EventDao;
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
    private static final LocalDate EVENT_DATE = LocalDate.now();
    private static final byte[] IMAGE_DATA = new byte[0];
    private static final byte[] IMAGE_DATA2 = new byte[]{1,2,3,4,5};
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
    private static final Page<User> USERS_PAGE = new Page<User>(USERS, 1, 1);
    private static final EventResponse RESPONSE = new EventResponse(RESPONSE_ID, USER_ID, USERNAME, EVENT_ID, DESCRIPTION, TIMESTAMP);
    private static final List<EventResponse> RESPONSES = List.of(RESPONSE);
    private static final PageParams PAGE_PARAMS = new PageParams(1, 2);

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

    @Test
    public void testCreateEvent(){
        Mockito.when(
            cityService.findByName(Mockito.eq(CITY_NAME))
        ).thenReturn(Optional.of(CITY));
        Mockito.when(
            userService.findByEmail(Mockito.eq(EMAIL))
        ).thenReturn(Optional.of(USER));
        Mockito.when(
            imageService.storeImage(Mockito.eq(IMAGE_DATA))
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
            cityService.findByName(Mockito.eq(CITY_NAME))
        ).thenReturn(Optional.of(CITY));
        Mockito.when(
            userService.findByEmail(Mockito.eq(EMAIL))
        ).thenReturn(Optional.empty());

        eventService.createEvent(EMAIL, CITY_NAME, EVENT_DATE, IMAGE_DATA, DESCRIPTION, TITLE, TIME, ADDRESS, LIMIT);
    }
    @Test(expected = RuntimeException.class)
    public void testCreateEventCityNotFound(){
        Mockito.when(
            cityService.findByName(Mockito.eq(CITY_NAME))
        ).thenReturn(Optional.empty());

        eventService.createEvent(EMAIL, CITY_NAME, EVENT_DATE, IMAGE_DATA, DESCRIPTION, TITLE, TIME, ADDRESS, LIMIT);
    }

    @Test
    public void testReplyToEvent(){
        Mockito.when(
            eventDao.findById(EVENT_ID)
        ).thenReturn(Optional.of(EVENT));
        Mockito.when(
            userService.findByEmail(EMAIL)
        ).thenReturn(Optional.of(USER));
        Mockito.when(
            userDao.listEventRespondersMinusUsers(Mockito.eq(EVENT_ID))
        ).thenReturn(USERS);

        eventService.replyToEvent(EMAIL, EVENT_ID, DESCRIPTION);
    }
    @Test(expected = RuntimeException.class)
    public void testReplyToEventNoUser(){
        Mockito.when(
            eventDao.findById(EVENT_ID)
        ).thenReturn(Optional.of(EVENT));
        Mockito.when(
            userService.findByEmail(EMAIL)
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
    public void testGetEventById(){
        Mockito.when(
            eventDao.findById(Mockito.eq(EVENT_ID))
        ).thenReturn(Optional.of(EVENT));

        Optional<Event> maybeEvent = eventService.getEventById(EVENT_ID);

        assertNotNull(maybeEvent);
        assertTrue(maybeEvent.isPresent());
        assertEquals(EVENT, maybeEvent.get());
    }

    @Test
    public void testGetAllEvents(){
        Mockito.when(
            eventDao.listAll()
        ).thenReturn(EVENTS);

        List<Event> events = eventService.getAllEvents();

        assertNotNull(events);
        assertEquals(EVENTS, events);
    }

    @Test
    public void testGetAllEventsPaged(){
        Mockito.when(
            eventDao.listAll(Mockito.eq(1), Mockito.eq(2))
        ).thenReturn(EVENTS_PAGE);

        Page<Event> page = eventService.getAllEvents(PAGE_PARAMS);

        assertNotNull(page);
        assertEquals(EVENTS_PAGE, page);
    } 

    @Test
    public void testGetAllEventsPagedEmail(){
        Mockito.when(
            eventDao.getEvents(Mockito.eq(EMAIL), Mockito.eq(1), Mockito.eq(2))
        ).thenReturn(EVENTS_PAGE);

        Page<Event> page = eventService.getAllEvents(EMAIL, PAGE_PARAMS);

        assertNotNull(page);
        assertEquals(EVENTS_PAGE, page);
    }    

    @Test
    public void testGetAllEventsSearchMissingQuery(){
        Mockito.when(
            eventDao.listAll(Mockito.eq(1), Mockito.eq(2))
        ).thenReturn(EVENTS_PAGE);

        Page<Event> page = eventService.getAllEventsSearch(null, PAGE_PARAMS);

        assertNotNull(page);
        assertEquals(EVENTS_PAGE, page);
    }
    @Test
    public void testGetAllEventsSearchEmptyQuery(){
        Mockito.when(
            eventDao.listAll(Mockito.eq(1), Mockito.eq(2))
        ).thenReturn(EVENTS_PAGE);

        Page<Event> page = eventService.getAllEventsSearch("", PAGE_PARAMS);

        assertNotNull(page);
        assertEquals(EVENTS_PAGE, page);
    }
    @Test
    public void testGetAllEventsSearchQuery(){
        Mockito.when(
            eventDao.searchEvents(Mockito.eq(DESCRIPTION), Mockito.eq(1), Mockito.eq(2))
        ).thenReturn(EVENTS_PAGE);

        Page<Event> page = eventService.getAllEventsSearch(DESCRIPTION, PAGE_PARAMS);

        assertNotNull(page);
        assertEquals(EVENTS_PAGE, page);
    }

    @Test
    public void testGetAllEventsEmail(){
        Mockito.when(
            eventDao.getEvents(Mockito.eq(EMAIL))
        ).thenReturn(EVENTS);

        List<Event> events = eventService.getAllEvents(EMAIL);

        assertNotNull(events);
        assertEquals(EVENTS, events);
    }

    @Test
    public void testGetAllEventsEmailPaged(){
        Mockito.when(
            eventDao.getEvents(Mockito.eq(EMAIL), Mockito.eq(1), Mockito.eq(2))
        ).thenReturn(EVENTS_PAGE);

        Page<Event> page = eventService.getAllEvents(EMAIL, PAGE_PARAMS);

        assertNotNull(page);
        assertEquals(EVENTS_PAGE, page);
    }

    @Test
    public void testAttendEventIdLimitNotExceeded(){
        Mockito.when(
            attendanceDao.isAttending(Mockito.eq(USER_ID), Mockito.eq(EVENT_ID))
        ).thenReturn(false);
        Mockito.when(
            eventDao.getEventAttendanceLimit(Mockito.eq(EVENT_ID))
        ).thenReturn(Optional.of(LIMIT));
        Mockito.when(
            attendanceDao.getAttendeesCount(Mockito.eq(EVENT_ID))
        ).thenReturn(ATTENDEES);

        eventService.attendEvent(USER_ID, EVENT_ID);
    }
    @Test
    public void testAttendEventIdLimitExceeded(){
        Mockito.when(
            attendanceDao.isAttending(Mockito.eq(USER_ID), Mockito.eq(EVENT_ID))
        ).thenReturn(false);
        Mockito.when(
            eventDao.getEventAttendanceLimit(Mockito.eq(EVENT_ID))
        ).thenReturn(Optional.of(LIMIT));
        Mockito.when(
            attendanceDao.getAttendeesCount(Mockito.eq(EVENT_ID))
        ).thenReturn(LIMIT);

        eventService.attendEvent(USER_ID, EVENT_ID);
    }
    @Test
    public void testAttendEventIdNoLimit(){
        Mockito.when(
            attendanceDao.isAttending(Mockito.eq(USER_ID), Mockito.eq(EVENT_ID))
        ).thenReturn(false);
        Mockito.when(
            eventDao.getEventAttendanceLimit(Mockito.eq(EVENT_ID))
        ).thenReturn(Optional.empty());

        eventService.attendEvent(USER_ID, EVENT_ID);
    }
    @Test
    public void testAttendEventIdAttending(){
        Mockito.when(
            attendanceDao.isAttending(Mockito.eq(USER_ID), Mockito.eq(EVENT_ID))
        ).thenReturn(true);

        eventService.attendEvent(USER_ID, EVENT_ID);
    }

    @Test
    public void testAttendEventEmailLimitNotExceeded(){
        Mockito.when(
            userService.findByEmail(Mockito.eq(EMAIL))
        ).thenReturn(Optional.of(USER));
        Mockito.when(
            attendanceDao.isAttending(Mockito.eq(USER_ID), Mockito.eq(EVENT_ID))
        ).thenReturn(false);
        Mockito.when(
            eventDao.getEventAttendanceLimit(Mockito.eq(EVENT_ID))
        ).thenReturn(Optional.of(LIMIT));
        Mockito.when(
            attendanceDao.getAttendeesCount(Mockito.eq(EVENT_ID))
        ).thenReturn(ATTENDEES);

        eventService.attendEvent(EMAIL, EVENT_ID);
    }
    @Test
    public void testAttendEventEmailLimitExceeded(){
        Mockito.when(
            userService.findByEmail(Mockito.eq(EMAIL))
        ).thenReturn(Optional.of(USER));
        Mockito.when(
            attendanceDao.isAttending(Mockito.eq(USER_ID), Mockito.eq(EVENT_ID))
        ).thenReturn(false);
        Mockito.when(
            eventDao.getEventAttendanceLimit(Mockito.eq(EVENT_ID))
        ).thenReturn(Optional.of(LIMIT));
        Mockito.when(
            attendanceDao.getAttendeesCount(Mockito.eq(EVENT_ID))
        ).thenReturn(LIMIT);

        eventService.attendEvent(EMAIL, EVENT_ID);
    }
    @Test
    public void testAttendEventEmailNoLimit(){
        Mockito.when(
            userService.findByEmail(Mockito.eq(EMAIL))
        ).thenReturn(Optional.of(USER));
        Mockito.when(
            attendanceDao.isAttending(Mockito.eq(USER_ID), Mockito.eq(EVENT_ID))
        ).thenReturn(false);
        Mockito.when(
            eventDao.getEventAttendanceLimit(Mockito.eq(EVENT_ID))
        ).thenReturn(Optional.empty());

        eventService.attendEvent(EMAIL, EVENT_ID);
    }
    @Test
    public void testAttendEventEmailAttending(){
        Mockito.when(
            userService.findByEmail(Mockito.eq(EMAIL))
        ).thenReturn(Optional.of(USER));
        Mockito.when(
            attendanceDao.isAttending(Mockito.eq(USER_ID), Mockito.eq(EVENT_ID))
        ).thenReturn(true);

        eventService.attendEvent(EMAIL, EVENT_ID);
    }
    @Test(expected = NoSuchElementException.class)
    public void testAttendEventEmailNotFound(){
        Mockito.when(
            userService.findByEmail(Mockito.eq(EMAIL))
        ).thenReturn(Optional.empty());

        eventService.attendEvent(EMAIL, EVENT_ID);
    }

    @Test
    public void testCancelAttendanceId(){
        eventService.cancelAttendance(USER_ID, EVENT_ID);
    }
    @Test
    public void testCancelAttendanceEmail(){
        Mockito.when(
            userService.findByEmail(Mockito.eq(EMAIL))
        ).thenReturn(Optional.of(USER));
        eventService.cancelAttendance(EMAIL, EVENT_ID);
    }
    @Test(expected = NoSuchElementException.class)
    public void testCancelAttendanceEmailNotFound(){
        Mockito.when(
            userService.findByEmail(Mockito.eq(EMAIL))
        ).thenReturn(Optional.empty());
        eventService.cancelAttendance(EMAIL, EVENT_ID);
    }

    @Test
    public void testIsUserAttendingId(){
        Mockito.when(
            attendanceDao.isAttending(Mockito.eq(USER_ID), Mockito.eq(EVENT_ID))
        ).thenReturn(true);

        boolean attending = eventService.isUserAttending(USER_ID, EVENT_ID);

        assertTrue(attending);
    }
    @Test
    public void testIsUserAttendingEmail(){
        Mockito.when(
            attendanceDao.isAttending(Mockito.eq(USER_ID), Mockito.eq(EVENT_ID))
        ).thenReturn(true);
        Mockito.when(
            userService.findByEmail(Mockito.eq(EMAIL))
        ).thenReturn(Optional.of(USER));

        boolean attending = eventService.isUserAttending(EMAIL, EVENT_ID);

        assertTrue(attending);
    }
    @Test(expected = NoSuchElementException.class)
    public void testIsUserAttendingEmailNotFound(){
        Mockito.when(
            userService.findByEmail(Mockito.eq(EMAIL))
        ).thenReturn(Optional.empty());

        boolean attending = eventService.isUserAttending(EMAIL, EVENT_ID);

        assertTrue(attending);
    }

    @Test
    public void testGetAttendees(){
        Mockito.when(
            attendanceDao.getAttendees(Mockito.eq(EVENT_ID))
        ).thenReturn(USERS);

        List<User> attending = eventService.getEventAttendees(EVENT_ID);

        assertNotNull(attending);
        assertEquals(USERS, attending);
    }

    @Test
    public void testGetAttendeesPaged(){
        Mockito.when(
            attendanceDao.getAttendees(Mockito.eq(EVENT_ID), Mockito.eq(1), Mockito.eq(2))
        ).thenReturn(USERS_PAGE);

        Page<User> attending = eventService.getEventAttendees(EVENT_ID, PAGE_PARAMS);

        assertNotNull(attending);
        assertEquals(USERS_PAGE, attending);
    }

    @Test
    public void testGetEventAttendeesCount(){
        Mockito.when(
            attendanceDao.getAttendeesCount(Mockito.eq(EVENT_ID))
        ).thenReturn(ATTENDEES);

        int attendees = eventService.getEventAttendeesCount(EVENT_ID);

        assertEquals(ATTENDEES, attendees);
    }

    @Test
    public void testGetUserAttendingEventsId(){
        Mockito.when(
            attendanceDao.getAttendingEvents(Mockito.eq(USER_ID))
        ).thenReturn(EVENTS);

        List<Event> events = eventService.getUserAttendingEvents(USER_ID);

        assertNotNull(events);
        assertEquals(EVENTS, events);
    }

    @Test
    public void testGetUserAttendingEventsEmail(){
        Mockito.when(
            userService.findByEmail(Mockito.eq(EMAIL))
        ).thenReturn(Optional.of(USER));
        Mockito.when(
            attendanceDao.getAttendingEvents(Mockito.eq(USER_ID))
        ).thenReturn(EVENTS);

        List<Event> events = eventService.getUserAttendingEvents(EMAIL);

        assertNotNull(events);
        assertEquals(EVENTS, events);
    }
    @Test(expected = NoSuchElementException.class)
    public void testGetUserAttendingEventsEmailNotFound(){
        Mockito.when(
            userService.findByEmail(Mockito.eq(EMAIL))
        ).thenReturn(Optional.empty());

        eventService.getUserAttendingEvents(EMAIL);
    }

    @Test
    public void testGetUserAttendingEventsPaged(){
        Mockito.when(
            attendanceDao.getAttendingEvents(Mockito.eq(USER_ID), Mockito.eq(1), Mockito.eq(2))
        ).thenReturn(EVENTS_PAGE);

        Page<Event> page = eventService.getUserAttendingEvents(USER_ID, PAGE_PARAMS);

        assertNotNull(page);
        assertEquals(EVENTS_PAGE, page);
    }

//    @Test
//    public void testGetEventResponses(){
//        Mockito.when(
//            responseService.listAllResponseFromEvent(Mockito.eq(EVENT_ID))
//        ).thenReturn(RESPONSES);
//
//        List<EventResponse> responses = eventService.getEventResponses(EVENT_ID);
//
//        assertNotNull(responses);
//        assertEquals(RESPONSES, responses);
//    }

    @Test
    public void testGetRecommendedEvents(){
        Mockito.when(
            eventDao.getRecommendedEvents(Mockito.eq(USER_ID), Mockito.eq(1), Mockito.eq(2))
        ).thenReturn(EVENTS_PAGE);

        List<Event> userevents = eventService.getRecommendedEvents(USER_ID, 2);

        assertNotNull(userevents);
        assertEquals(EVENTS, userevents);
    }
    @Test
    public void testGetRecommendedEventsMissing(){
        Mockito.when(
            eventDao.getRecommendedEvents(Mockito.eq(USER_ID), Mockito.eq(1), Mockito.eq(2))
        ).thenReturn(new Page<>(List.of(), 1, 0));
        Mockito.when(
            eventDao.getTopUserEvents(Mockito.eq(USER_ID), Mockito.eq(1), Mockito.eq(2))
        ).thenReturn(EVENTS_PAGE);

        List<Event> userevents = eventService.getRecommendedEvents(USER_ID, 2);

        assertNotNull(userevents);
        assertEquals(EVENTS, userevents);
    }
    @Test(expected = IllegalArgumentException.class)
    public void testGetRecommendedEventsWrongLimit(){
        eventService.getRecommendedEvents(USER_ID, 0);
    }

    @Test
    public void testGetTopEvents(){
        Mockito.when(
            eventDao.getTopEvents(Mockito.eq(1), Mockito.eq(2))
        ).thenReturn(EVENTS_PAGE);

        List<Event> events = eventService.getTopEvents(2);

        assertNotNull(events);
        assertEquals(EVENTS, events);
    }
    @Test(expected = IllegalArgumentException.class)
    public void testGetTopEventsWrongLimit(){
        eventService.getTopEvents(0);
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
    public void testIsEventFull(){
        Mockito.when(
            eventDao.getEventAttendanceLimit(Mockito.eq(EVENT_ID))
        ).thenReturn(Optional.of(LIMIT));
        Mockito.when(
            attendanceDao.getAttendeesCount(Mockito.eq(EVENT_ID))
        ).thenReturn(ATTENDEES);

        boolean isFull = eventService.isEventFull(EVENT_ID);

        assertFalse(isFull);
    }    
    @Test
    public void testIsEventFullActuallyFull(){
        Mockito.when(
            eventDao.getEventAttendanceLimit(Mockito.eq(EVENT_ID))
        ).thenReturn(Optional.of(ATTENDEES));
        Mockito.when(
            attendanceDao.getAttendeesCount(Mockito.eq(EVENT_ID))
        ).thenReturn(ATTENDEES);

        boolean isFull = eventService.isEventFull(EVENT_ID);

        assertTrue(isFull);
    }   
    @Test
    public void testIsEventFullNoLimit(){
        Mockito.when(
            eventDao.getEventAttendanceLimit(Mockito.eq(EVENT_ID))
        ).thenReturn(Optional.empty());

        boolean isFull = eventService.isEventFull(EVENT_ID);

        assertFalse(isFull);
    }

    @Test
    public void testGetFullEvents(){
        Mockito.when(
            eventDao.getFullEvents()
        ).thenReturn(EVENTS);

        List<Event> events = eventService.getFullEvents();

        assertNotNull(events);
        assertEquals(EVENTS, events);
    }

//    @Test
//    public void testGetEventsPageWithAttendanceStatus(){
//        Mockito.when(
//            eventDao.getEventsWithAttendanceStatus(Mockito.eq(USER_ID), Mockito.eq(DESCRIPTION), Mockito.eq(1), Mockito.eq(2))
//        ).thenReturn(EVENTS_PAGE);
//
//        Page<Event> page = eventService.getEventsPageWithAttendanceStatus(DESCRIPTION, USER, PAGE_PARAMS);
//
//        assertNotNull(page);
//        assertEquals(USEREVENTS_PAGE, page);
//    }
//    @Test
//    public void testGetEventsPageWithAttendanceStatusNoUser(){
//        Mockito.when(
//            eventDao.getEventsWithAttendanceStatus(Mockito.eq(null), Mockito.eq(DESCRIPTION), Mockito.eq(1), Mockito.eq(2))
//        ).thenReturn(USEREVENTS_PAGE);
//
//        Page<Event> page = eventService.getEventsPageWithAttendanceStatus(DESCRIPTION, null, PAGE_PARAMS);
//
//        assertNotNull(page);
//        assertEquals(USEREVENTS_PAGE, page);
//    }
//
//    @Test
//    public void testGetEventsWithAttendanceStatusId(){
//        Mockito.when(
//            eventDao.getEventsWithAttendanceStatus(Mockito.eq(USER_ID))
//        ).thenReturn(USEREVENTS);
//
//        List<UserEvent> userevents = eventService.getEventsWithAttendanceStatus(USER_ID);
//
//        assertNotNull(userevents);
//        assertEquals(USEREVENTS, userevents);
//    }
//    @Test
//    public void testGetEventsWithAttendanceStatusEmail(){
//        Mockito.when(
//            userService.findByEmail(Mockito.eq(EMAIL))
//        ).thenReturn(Optional.of(USER));
//        Mockito.when(
//            eventDao.getEventsWithAttendanceStatus(Mockito.eq(USER_ID))
//        ).thenReturn(USEREVENTS);
//
//        List<UserEvent> userevents = eventService.getEventsWithAttendanceStatus(EMAIL);
//
//        assertNotNull(userevents);
//        assertEquals(USEREVENTS, userevents);
//    }
//    @Test(expected = NoSuchElementException.class)
//    public void testGetEventsWithAttendanceStatusEmailNotFound(){
//        Mockito.when(
//            userService.findByEmail(Mockito.eq(EMAIL))
//        ).thenReturn(Optional.empty());
//
//        eventService.getEventsWithAttendanceStatus(EMAIL);
//    }

    @Test
    public void testEditEvent(){
        Mockito.when(
            eventDao.findById(Mockito.eq(EVENT_ID))
        ).thenReturn(Optional.of(EVENT));
        Mockito.when(
            cityService.findByName(Mockito.eq(CITY_NAME))
        ).thenReturn(Optional.of(CITY));

        eventService.editEvent(EVENT_ID, CITY_NAME, EVENT_DATE, IMAGE_DATA2, DESCRIPTION, TITLE, TIME, ADDRESS, LIMIT);
    }
    @Test
    public void testEditEventEmptyImage(){
        Mockito.when(
            eventDao.findById(Mockito.eq(EVENT_ID))
        ).thenReturn(Optional.of(EVENT));
        Mockito.when(
            cityService.findByName(Mockito.eq(CITY_NAME))
        ).thenReturn(Optional.of(CITY));

        eventService.editEvent(EVENT_ID, CITY_NAME, EVENT_DATE, IMAGE_DATA, DESCRIPTION, TITLE, TIME, ADDRESS, LIMIT);
    }
    @Test
    public void testEditEventMissingImage(){
        Mockito.when(
            eventDao.findById(Mockito.eq(EVENT_ID))
        ).thenReturn(Optional.of(EVENT));
        Mockito.when(
            cityService.findByName(Mockito.eq(CITY_NAME))
        ).thenReturn(Optional.of(CITY));

        eventService.editEvent(EVENT_ID, CITY_NAME, EVENT_DATE, null, DESCRIPTION, TITLE, TIME, ADDRESS, LIMIT);
    }
    @Test(expected = RuntimeException.class)
    public void testEditEventMissingCity(){
        Mockito.when(
            eventDao.findById(Mockito.eq(EVENT_ID))
        ).thenReturn(Optional.of(EVENT));
        Mockito.when(
            cityService.findByName(Mockito.eq(CITY_NAME))
        ).thenReturn(Optional.empty());

        eventService.editEvent(EVENT_ID, CITY_NAME, EVENT_DATE, null, DESCRIPTION, TITLE, TIME, ADDRESS, LIMIT);
    }
    @Test(expected = IllegalArgumentException.class)
    public void testEditEventMissingEvent(){
        Mockito.when(
            eventDao.findById(Mockito.eq(EVENT_ID))
        ).thenReturn(Optional.empty());

        eventService.editEvent(EVENT_ID, CITY_NAME, EVENT_DATE, null, DESCRIPTION, TITLE, TIME, ADDRESS, LIMIT);
    }

    @Test
    public void testDelete(){
        Mockito.when(
            eventDao.findById(Mockito.eq(EVENT_ID))
        ).thenReturn(Optional.of(EVENT));

        eventService.delete(EVENT_ID, DESCRIPTION);
    }    
    @Test(expected = RuntimeException.class)
    public void testDeleteMissing(){
        Mockito.when(
            eventDao.findById(Mockito.eq(EVENT_ID))
        ).thenReturn(Optional.empty());

        eventService.delete(EVENT_ID, DESCRIPTION);
    }
}
