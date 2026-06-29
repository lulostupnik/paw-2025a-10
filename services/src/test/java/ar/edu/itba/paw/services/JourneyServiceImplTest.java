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
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import ar.edu.itba.paw.interfaces.persistence.JourneyDao;
import ar.edu.itba.paw.interfaces.persistence.JourneyResponseDao;
import ar.edu.itba.paw.interfaces.persistence.TipDao;
import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.interfaces.services.InterestService;
import ar.edu.itba.paw.interfaces.services.UniversityService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Career;
import ar.edu.itba.paw.models.City;
import ar.edu.itba.paw.models.Country;
import ar.edu.itba.paw.models.Journey;
import ar.edu.itba.paw.models.JourneyResponse;
import ar.edu.itba.paw.models.Page;
import ar.edu.itba.paw.models.PageParams;
import ar.edu.itba.paw.models.Tip;
import ar.edu.itba.paw.models.University;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.enums.SortDirection;
import ar.edu.itba.paw.models.enums.SortFieldJourney;
import ar.edu.itba.paw.models.exceptions.InvalidDateException;
import ar.edu.itba.paw.models.exceptions.InvalidPaginationParamsException;
import ar.edu.itba.paw.models.exceptions.InvalidReferenceException;
import ar.edu.itba.paw.models.exceptions.JourneyNotFoundException;
import ar.edu.itba.paw.models.exceptions.MutuallyExclusiveFiltersException;
import ar.edu.itba.paw.models.exceptions.TipNotFoundException;
import ar.edu.itba.paw.models.exceptions.UserNotFoundException;
import ar.edu.itba.paw.models.exceptions.UserWithActiveJourneyException;

@RunWith(MockitoJUnitRunner.class)
public class JourneyServiceImplTest {

    private static final long USER_ID = 0;
    private static final long USER_ID_2 = 1;
    private static final long UNI_ID = 2;
    private static final long CITY_ID = 3;
    private static final long CAREER_ID = 4;
    private static final long IMAGE_ID = 5;
    private static final long JOURNEY_ID = 6;
    private static final long REPLY_ID = 7;
    private static final long TIP_ID = 8;

    private static final String UNI_NAME = "uni";
    private static final String UNI_ABBR = "uni";
    private static final String EMAIL = "mail1";
    private static final String EMAIL_2 = "mail2";
    private static final String USERNAME = "user1";
    private static final String FIRSTNAME = "user";
    private static final String LASTNAME = "user";
    private static final String CITY_NAME = "citi";
    private static final String COUNTRY_NAME = "cuntry";
    private static final String CAREER_NAME = "career";
    private static final String INTEREST_NAME = "interesting";
    private static final Country COUNTRY = new Country(COUNTRY_NAME, "ARG");

    private static final City CITY = new City(CITY_NAME, COUNTRY, CITY_ID);
    private static final University UNI = new University(UNI_ID, UNI_NAME, UNI_ABBR, CITY);
    private static final Career CAREER = new Career(CAREER_ID, CAREER_NAME);
    private static final Locale LOCALE = Locale.of("en");
    private static final User USER = new User(USER_ID, EMAIL, USERNAME, FIRSTNAME, LASTNAME, UNI, CAREER, IMAGE_ID, LOCALE, false, true);
    private static final List<User> USERS = List.of(USER);

    private static final LocalDate START_DATE = LocalDate.now();
    private static final LocalDate END_DATE = START_DATE.plusDays(10);
    private static final String DESCRIPTION = "desc";
    private static final Journey JOURNEY = new Journey(JOURNEY_ID, USER, START_DATE, END_DATE, UNI, DESCRIPTION);
    private static final Journey JOURNEY_DELETED = new Journey(JOURNEY_ID, USER, START_DATE, END_DATE, UNI, DESCRIPTION, true);
    private static final List<Journey> JOURNEYS = List.of(JOURNEY);
    private static final Page<Journey> JOURNEY_PAGE = new Page<Journey>(JOURNEYS, 1, 1, 1);
    
    private static final JourneyResponse JOURNEY_REPLY = new JourneyResponse(USER, JOURNEY, DESCRIPTION);
    private static final List<JourneyResponse> REPLIES = List.of(JOURNEY_REPLY);
    private static final Page<JourneyResponse> REPLY_PAGE = new Page<>(REPLIES, 1, 1, 1);

    private static final Long USER_ID_WITH_JOURNEY = USER_ID_2;
    private static final User USER_WITH_JOURNEY = new User(USER_ID_2, EMAIL_2, USERNAME, FIRSTNAME, LASTNAME, UNI, CAREER, JOURNEY, IMAGE_ID, LOCALE, false, true);
    private static final User USER_WITH_JOURNEY_DELETED = new User(USER_ID_2, EMAIL_2, USERNAME, FIRSTNAME, LASTNAME, UNI, CAREER, JOURNEY_DELETED, IMAGE_ID, LOCALE, false, true);

    private static final LocalDateTime REPLY_TIMESTAMP = LocalDateTime.now();
    private static final JourneyResponse REPLY = new JourneyResponse(REPLY_ID, USER, JOURNEY, DESCRIPTION, REPLY_TIMESTAMP);

    private static final Tip TIP = new Tip(TIP_ID, DESCRIPTION, DESCRIPTION, JOURNEY, REPLY_TIMESTAMP);
    private static final List<Tip> TIPS = List.of(TIP);
    private static final Page<Tip> TIP_PAGE = new Page<>(TIPS, 1, 1, 1);
    private static final PageParams PAGE_1_DEFAULT = new PageParams(1, 2);

    @InjectMocks
    JourneyServiceImpl journeyService;

    @Mock
    JourneyDao journeyDao;
    @Mock
    JourneyResponseDao replyDao;
    @Mock
    TipDao tipDao;

    @Mock
    UserService userService;
    @Mock
    EmailService emailService;
    @Mock
    UniversityService uniService;
    @Mock
    InterestService interestService;
    @Mock
    UserDao userDao;


    @Test
    public void testCreateJourney(){
        when(
            userService.findUserById(eq(USER_ID))
        ).thenReturn(Optional.of(USER));
        when(
            uniService.findById(eq(UNI_ID))
        ).thenReturn(Optional.of(UNI));
        when(
            journeyDao.create(
                eq(USER), 
                eq(UNI), 
                eq(START_DATE), 
                eq(END_DATE), 
                eq(DESCRIPTION)
            )
        ).thenReturn(JOURNEY);

        Journey journey = journeyService.createJourney(
            USER_ID,
            UNI_ID,
            START_DATE, 
            END_DATE, 
            DESCRIPTION
        );

        assertNotNull(journey);
        assertEquals(JOURNEY, journey);
    }
    @Test()
    public void testCreateJourneyWithDeletedJourney(){
        User u = new User(USER_ID, EMAIL, USERNAME, FIRSTNAME, LASTNAME, UNI, CAREER, JOURNEY_DELETED, IMAGE_ID, LOCALE, false, true);
        when(
            userService.findUserById(eq(USER_ID))
        ).thenReturn(Optional.of(u));
        when(
            uniService.findById(eq(UNI_ID))
        ).thenReturn(Optional.of(UNI));
        when(
            journeyDao.create(
                eq(USER), 
                eq(UNI), 
                eq(START_DATE), 
                eq(END_DATE), 
                eq(DESCRIPTION)
            )
        ).thenReturn(JOURNEY);

        Journey journey = journeyService.createJourney(
            USER_ID,
            UNI_ID,
            START_DATE, 
            END_DATE, 
            DESCRIPTION
        );

        assertNotNull(journey);
        assertEquals(JOURNEY, journey);
    }
    @Test(expected = UserWithActiveJourneyException.class)
    public void testCreateJourneyWithActiveJourney(){
        when(
            userService.findUserById(eq(USER_ID))
        ).thenReturn(Optional.of(USER_WITH_JOURNEY));
        when(
            uniService.findById(eq(UNI_ID))
        ).thenReturn(Optional.of(UNI));

        journeyService.createJourney(
            USER_ID,
            UNI_ID,
            START_DATE, 
            END_DATE, 
            DESCRIPTION
        );
    }
    @Test(expected = InvalidReferenceException.class)
    public void testCreateJourneyNoUni(){
        when(
            userService.findUserById(eq(USER_ID))
        ).thenReturn(Optional.of(USER));
        when(
            uniService.findById(eq(UNI_ID))
        ).thenReturn(Optional.empty());

        journeyService.createJourney(
            USER_ID,
            UNI_ID,
            START_DATE, 
            END_DATE, 
            DESCRIPTION
        );
    }
    @Test(expected = InvalidDateException.class)
    public void testCreateJourneyMissingStartDate(){
        when(
            userService.findUserById(eq(USER_ID))
        ).thenReturn(Optional.of(USER));

        journeyService.createJourney(
            USER_ID,
            UNI_ID,
            null, 
            END_DATE, 
            DESCRIPTION
        );
    }
    @Test(expected = InvalidDateException.class)
    public void testCreateJourneyMissingEndDate(){
        when(
            userService.findUserById(eq(USER_ID))
        ).thenReturn(Optional.of(USER));

        journeyService.createJourney(
            USER_ID,
            UNI_ID,
            START_DATE, 
            null, 
            DESCRIPTION
        );
    }
    @Test(expected = InvalidDateException.class)
    public void testCreateJourneyFlippedDates(){
        when(
            userService.findUserById(eq(USER_ID))
        ).thenReturn(Optional.of(USER));

        journeyService.createJourney(
            USER_ID,
            UNI_ID,
            END_DATE, 
            START_DATE, 
            DESCRIPTION
        );
    }
    @Test(expected = InvalidDateException.class)
    public void testCreateJourneyStartsBeforeNow(){
        when(
            userService.findUserById(eq(USER_ID))
        ).thenReturn(Optional.of(USER));

        journeyService.createJourney(
            USER_ID,
            UNI_ID,
            START_DATE.plusDays(-1), 
            START_DATE, 
            DESCRIPTION
        );
    }
    @Test(expected = UserNotFoundException.class)
    public void testCreateJourneyMissingUser(){
        when(
            userService.findUserById(eq(USER_ID))
        ).thenReturn(Optional.empty());

        journeyService.createJourney(
            USER_ID,
            UNI_ID,
            START_DATE.plusDays(-1), 
            START_DATE, 
            DESCRIPTION
        );
    }

    @Test
    public void testCreateJourneyResponse(){
        when(
            journeyDao.findById(eq(JOURNEY_ID))
        ).thenReturn(Optional.of(JOURNEY));
        when(
            userService.findUserByEmail(eq(EMAIL))
        ).thenReturn(Optional.of(USER));
        when(
            replyDao.findRespondersByJourneyId(
                eq(JOURNEY_ID), 
                any(PageParams.class)
            )
        ).thenReturn(new Page<>(USERS, 1, 1, 1));
        when(
            replyDao.create(eq(USER), eq(JOURNEY), eq(DESCRIPTION))
        ).thenReturn(REPLY);

        JourneyResponse reply = journeyService.createJourneyResponse(EMAIL, JOURNEY_ID, DESCRIPTION);

        assertNotNull(reply);
        assertEquals(USER, reply.getUser());
        assertEquals(JOURNEY, reply.getJourney());
        assertEquals(DESCRIPTION, reply.getMessage());
    }
    @Test
    public void testCreateJourneyResponseNoMails(){
        when(
            journeyDao.findById(eq(JOURNEY_ID))
        ).thenReturn(Optional.of(JOURNEY));
        when(
            userService.findUserByEmail(eq(EMAIL))
        ).thenReturn(Optional.of(USER));
        when(
            replyDao.findRespondersByJourneyId(
                eq(JOURNEY_ID), 
                any(PageParams.class)
            )
        ).thenReturn(new Page<>(List.of(), 1, 1, 2))
        .thenReturn(new Page<>(List.of(), 2, 1, 2));
        when(
            replyDao.create(eq(USER), eq(JOURNEY), eq(DESCRIPTION))
        ).thenReturn(REPLY);

        JourneyResponse reply = journeyService.createJourneyResponse(EMAIL, JOURNEY_ID, DESCRIPTION);

        assertNotNull(reply);
        assertEquals(USER, reply.getUser());
        assertEquals(JOURNEY, reply.getJourney());
        assertEquals(DESCRIPTION, reply.getMessage());
    }
    @Test(expected = UserNotFoundException.class)
    public void testCreateJourneyResponseUserNotFound(){
        when(
            journeyDao.findById(eq(JOURNEY_ID))
        ).thenReturn(Optional.of(JOURNEY));
        when(
            userService.findUserByEmail(eq(EMAIL))
        ).thenReturn(Optional.empty());

        journeyService.createJourneyResponse(EMAIL, JOURNEY_ID, DESCRIPTION);
    }
    @Test(expected = JourneyNotFoundException.class)
    public void testCreateJourneyResponseNotFound(){
        when(
            journeyDao.findById(eq(JOURNEY_ID))
        ).thenReturn(Optional.empty());

        journeyService.createJourneyResponse(EMAIL, JOURNEY_ID, DESCRIPTION);
    }

    @Test
    public void testCreateJourneyResponseUserId(){
        when(
            journeyDao.findById(eq(JOURNEY_ID))
        ).thenReturn(Optional.of(JOURNEY));
        when(
            userService.findUserById(eq(USER_ID))
        ).thenReturn(Optional.of(USER));
        when(
            replyDao.findRespondersByJourneyId(
                eq(JOURNEY_ID), 
                any(PageParams.class)
            )
        ).thenReturn(new Page<>(List.of(USER), 1, 1, 2))
        .thenReturn(new Page<>(List.of(), 2, 1, 2));
        when(
            replyDao.create(eq(USER), eq(JOURNEY), eq(DESCRIPTION))
        ).thenReturn(REPLY);

        JourneyResponse reply = journeyService.createJourneyResponse(USER_ID, JOURNEY_ID, DESCRIPTION);

        assertNotNull(reply);
        assertEquals(USER, reply.getUser());
        assertEquals(JOURNEY, reply.getJourney());
        assertEquals(DESCRIPTION, reply.getMessage());
    }
    @Test(expected = UserNotFoundException.class)
    public void testCreateJourneyResponseUserIdUserNotFound(){
        when(
            journeyDao.findById(eq(JOURNEY_ID))
        ).thenReturn(Optional.of(JOURNEY));
        when(
            userService.findUserById(eq(USER_ID))
        ).thenReturn(Optional.empty());

        journeyService.createJourneyResponse(USER_ID, JOURNEY_ID, DESCRIPTION);
    }
    @Test(expected = JourneyNotFoundException.class)
    public void testCreateJourneyResponseUserIdJourneyNotFound(){
        when(
            journeyDao.findById(eq(JOURNEY_ID))
        ).thenReturn(Optional.empty());

        journeyService.createJourneyResponse(USER_ID, JOURNEY_ID, DESCRIPTION);
    }

    @Test
    public void testFindJourneysQuery(){
        when(
            journeyDao.search(
                eq(EMAIL),
                eq(null),   //excludeUserId
                any(),      //destinationCityId
                eq(null),   //orderBy
                eq(null),   //direction
                eq(null),   //city
                eq(null),   //university
                eq(null),   //startDate
                eq(null),   //endDate
                eq(null),   //interest
                eq(false),  //isUpcoming
                eq(false),  //isPast
                any(PageParams.class)
            )
        ).thenReturn(JOURNEY_PAGE);

        Page<Journey> journeys = journeyService.findJourneys(EMAIL, PAGE_1_DEFAULT);

        assertNotNull(journeys);
        assertEquals(JOURNEY_PAGE, journeys);
    }
    @Test
    public void testFindJourneysEmptyQuery(){
        when(
            journeyDao.findAll(any(PageParams.class))
        ).thenReturn(JOURNEY_PAGE);

        Page<Journey> journeys = journeyService.findJourneys("", PAGE_1_DEFAULT);

        assertNotNull(journeys);
        assertEquals(JOURNEY_PAGE, journeys);
    }
    @Test
    public void testFindJourneysMissingQuery(){
        when(
            journeyDao.findAll(any(PageParams.class))
        ).thenReturn(JOURNEY_PAGE);

        Page<Journey> journeys = journeyService.findJourneys(null, PAGE_1_DEFAULT);

        assertNotNull(journeys);
        assertEquals(JOURNEY_PAGE, journeys);
    }

    @Test
    public void testFindJourneyById(){
        when(
            journeyDao.findById(eq(JOURNEY_ID))
        ).thenReturn(Optional.of(JOURNEY));

        Optional<Journey> maybeJourney = journeyService.findJourneyById(JOURNEY_ID);

        assertNotNull(maybeJourney);
        assertEquals(JOURNEY, maybeJourney.get());
    }

    @Test
    public void testFindJourneysCurrentNoDateChange(){
        when(
            journeyDao.search(
                eq(DESCRIPTION),
                eq(USER_ID_2),
                any(),
                eq(SortFieldJourney.END_DATE),
                eq(SortDirection.DESC),
                eq(CITY_NAME),
                eq(UNI_NAME),
                eq(START_DATE),
                any(LocalDate.class),
                eq(INTEREST_NAME),
                eq(false),
                eq(false),
                eq(PAGE_1_DEFAULT)
            )
        ).thenReturn(JOURNEY_PAGE);

        Page<Journey> page = journeyService.findJourneys(
            DESCRIPTION,
            USER_ID_WITH_JOURNEY,
            null,
            SortFieldJourney.from("end_date"),
            SortDirection.from("desc"),
            CITY_NAME,
            UNI_NAME,
            START_DATE,
            END_DATE,
            INTEREST_NAME,
            false,
            false,
            true,
            PAGE_1_DEFAULT
        );

        assertNotNull(page);
        assertEquals(JOURNEY_PAGE, page);
    }
    @Test(expected = MutuallyExclusiveFiltersException.class)
    public void testFindJourneysMultiTimeFilter(){
        journeyService.findJourneys(
            DESCRIPTION,
            USER_ID_WITH_JOURNEY,
            null,
            SortFieldJourney.from("end_date"),
            SortDirection.from("desc"),
            CITY_NAME,
            UNI_NAME,
            START_DATE,
            END_DATE,
            INTEREST_NAME,
            true,
            true,
            true,
            PAGE_1_DEFAULT
        );
    }
    @Test
    public void testFindJourneysCurrentDateChange(){        
        when(
            journeyDao.search(
                eq(DESCRIPTION),
                eq(USER_ID_2),
                any(),
                eq(SortFieldJourney.END_DATE),
                eq(SortDirection.DESC),
                eq(CITY_NAME),
                eq(UNI_NAME),
                any(LocalDate.class),
                any(LocalDate.class),
                eq(INTEREST_NAME),
                eq(false),
                eq(false),
                eq(PAGE_1_DEFAULT)
            )
        ).thenReturn(JOURNEY_PAGE);

        Page<Journey> page = journeyService.findJourneys(
            DESCRIPTION,
            USER_ID_WITH_JOURNEY,
            null,
            SortFieldJourney.from("end_date"),
            SortDirection.from("desc"),
            CITY_NAME,
            UNI_NAME,
            LocalDate.now().plusDays(10),
            LocalDate.now().minusDays(10),
            INTEREST_NAME,
            false,
            false,
            true,
            PAGE_1_DEFAULT
        );

        assertNotNull(page);
        assertEquals(JOURNEY_PAGE, page);
    }
    @Test
    public void testFindJourneysCurrentNoDates(){
        when(
            journeyDao.search(
                eq(DESCRIPTION),
                eq(USER_ID_2),
                any(),
                eq(SortFieldJourney.END_DATE),
                eq(SortDirection.DESC),
                eq(CITY_NAME),
                eq(UNI_NAME),
                any(LocalDate.class),
                any(LocalDate.class),
                eq(INTEREST_NAME),
                eq(false),
                eq(false),
                eq(PAGE_1_DEFAULT)
            )
        ).thenReturn(JOURNEY_PAGE);

        Page<Journey> page = journeyService.findJourneys(
            DESCRIPTION,
            USER_ID_WITH_JOURNEY,
            null,
            SortFieldJourney.from("end_date"),
            SortDirection.from("desc"),
            CITY_NAME,
            UNI_NAME,
            null,
            null,
            INTEREST_NAME,
            false,
            false,
            true,
            PAGE_1_DEFAULT
        );

        assertNotNull(page);
        assertEquals(JOURNEY_PAGE, page);
    }
    @Test
    public void testFindJourneysPast(){
        when(
            journeyDao.search(
                eq(DESCRIPTION),
                eq(USER_ID_2),
                any(),
                eq(SortFieldJourney.END_DATE),
                eq(SortDirection.DESC),
                eq(CITY_NAME),
                eq(UNI_NAME),
                eq(START_DATE),
                any(LocalDate.class),
                eq(INTEREST_NAME),
                eq(false),
                eq(true),
                eq(PAGE_1_DEFAULT)
            )
        ).thenReturn(JOURNEY_PAGE);

        Page<Journey> page = journeyService.findJourneys(
            DESCRIPTION,
            USER_ID_WITH_JOURNEY,
            null,
            SortFieldJourney.from("end_date"),
            SortDirection.from("desc"),
            CITY_NAME,
            UNI_NAME,
            START_DATE,
            END_DATE,
            INTEREST_NAME,
            true,
            false,
            false,
            PAGE_1_DEFAULT
        );

        assertNotNull(page);
        assertEquals(JOURNEY_PAGE, page);
    }
    @Test
    public void testFindJourneysPastNoDateChange(){
        when(
            journeyDao.search(
                eq(DESCRIPTION),
                eq(USER_ID_2),
                any(),
                eq(SortFieldJourney.END_DATE),
                eq(SortDirection.DESC),
                eq(CITY_NAME),
                eq(UNI_NAME),
                eq(START_DATE),
                any(LocalDate.class),
                eq(INTEREST_NAME),
                eq(false),
                eq(true),
                eq(PAGE_1_DEFAULT)
            )
        ).thenReturn(JOURNEY_PAGE);

        Page<Journey> page = journeyService.findJourneys(
            DESCRIPTION,
            USER_ID_WITH_JOURNEY,
            null,
            SortFieldJourney.from("end_date"),
            SortDirection.from("desc"),
            CITY_NAME,
            UNI_NAME,
            START_DATE,
            LocalDate.now().minusDays(10),
            INTEREST_NAME,
            true,
            false,
            false,
            PAGE_1_DEFAULT
        );

        assertNotNull(page);
        assertEquals(JOURNEY_PAGE, page);
    }
    @Test
    public void testFindJourneysPastNoDate(){
        when(
            journeyDao.search(
                eq(DESCRIPTION),
                eq(USER_ID_2),
                any(),
                eq(SortFieldJourney.END_DATE),
                eq(SortDirection.DESC),
                eq(CITY_NAME),
                eq(UNI_NAME),
                eq(START_DATE),
                any(LocalDate.class),
                eq(INTEREST_NAME),
                eq(false),
                eq(true),
                eq(PAGE_1_DEFAULT)
            )
        ).thenReturn(JOURNEY_PAGE);

        Page<Journey> page = journeyService.findJourneys(
            DESCRIPTION,
            USER_ID_WITH_JOURNEY,
            null,
            SortFieldJourney.from("end_date"),
            SortDirection.from("desc"),
            CITY_NAME,
            UNI_NAME,
            START_DATE,
            null,
            INTEREST_NAME,
            true,
            false,
            false,
            PAGE_1_DEFAULT
        );

        assertNotNull(page);
        assertEquals(JOURNEY_PAGE, page);
    }
    @Test
    public void testFindJourneysUpcomingNoDateChange(){
        when(
            journeyDao.search(
                eq(DESCRIPTION),
                eq(USER_ID_2),
                any(),
                eq(SortFieldJourney.END_DATE),
                eq(SortDirection.DESC),
                eq(CITY_NAME),
                eq(UNI_NAME),
                any(LocalDate.class),
                eq(END_DATE),
                eq(INTEREST_NAME),
                eq(true),
                eq(false),
                eq(PAGE_1_DEFAULT)
            )
        ).thenReturn(JOURNEY_PAGE);

        Page<Journey> page = journeyService.findJourneys(
            DESCRIPTION,
            USER_ID_WITH_JOURNEY,
            null,
            SortFieldJourney.from("end_date"),
            SortDirection.from("desc"),
            CITY_NAME,
            UNI_NAME,
            START_DATE,
            END_DATE,
            INTEREST_NAME,
            false,
            true,
            false,
            PAGE_1_DEFAULT
        );

        assertNotNull(page);
        assertEquals(JOURNEY_PAGE, page);
    }
    @Test
    public void testFindJourneysUpcoming(){
        when(
            journeyDao.search(
                eq(DESCRIPTION),
                eq(USER_ID_2),
                any(),
                eq(SortFieldJourney.END_DATE),
                eq(SortDirection.DESC),
                eq(CITY_NAME),
                eq(UNI_NAME),
                any(LocalDate.class),
                eq(END_DATE),
                eq(INTEREST_NAME),
                eq(true),
                eq(false),
                eq(PAGE_1_DEFAULT)
            )
        ).thenReturn(JOURNEY_PAGE);

        Page<Journey> page = journeyService.findJourneys(
            DESCRIPTION,
            USER_ID_WITH_JOURNEY,
            null,
            SortFieldJourney.from("end_date"),
            SortDirection.from("desc"),
            CITY_NAME,
            UNI_NAME,
            LocalDate.now().plusDays(10),
            END_DATE,
            INTEREST_NAME,
            false,
            true,
            false,
            PAGE_1_DEFAULT
        );

        assertNotNull(page);
        assertEquals(JOURNEY_PAGE, page);
    }
    @Test
    public void testFindJourneysUpcomingNoDate(){
        when(
            journeyDao.search(
                eq(DESCRIPTION),
                eq(USER_ID_2),
                any(),
                eq(SortFieldJourney.END_DATE),
                eq(SortDirection.DESC),
                eq(CITY_NAME),
                eq(UNI_NAME),
                any(LocalDate.class),
                eq(END_DATE),
                eq(INTEREST_NAME),
                eq(true),
                eq(false),
                eq(PAGE_1_DEFAULT)
            )
        ).thenReturn(JOURNEY_PAGE);

        Page<Journey> page = journeyService.findJourneys(
            DESCRIPTION,
            USER_ID_WITH_JOURNEY,
            null,
            SortFieldJourney.from("end_date"),
            SortDirection.from("desc"),
            CITY_NAME,
            UNI_NAME,
            null,
            END_DATE,
            INTEREST_NAME,
            false,
            true,
            false,
            PAGE_1_DEFAULT
        );

        assertNotNull(page);
        assertEquals(JOURNEY_PAGE, page);
    }
    @Test
    public void testFindJourneysUpcomingNoDateCheck(){
        when(
            journeyDao.search(
                eq(DESCRIPTION),
                eq(USER_ID_2),
                any(),
                eq(SortFieldJourney.END_DATE),
                eq(SortDirection.DESC),
                eq(CITY_NAME),
                eq(UNI_NAME),
                eq(START_DATE),
                eq(END_DATE),
                eq(INTEREST_NAME),
                eq(false),
                eq(false),
                eq(PAGE_1_DEFAULT)
            )
        ).thenReturn(JOURNEY_PAGE);

        Page<Journey> page = journeyService.findJourneys(
            DESCRIPTION,
            USER_ID_WITH_JOURNEY,
            null,
            SortFieldJourney.from("end_date"),
            SortDirection.from("desc"),
            CITY_NAME,
            UNI_NAME,
            START_DATE,
            END_DATE,
            INTEREST_NAME,
            false,
            false,
            false,
            PAGE_1_DEFAULT
        );

        assertNotNull(page);
        assertEquals(JOURNEY_PAGE, page);
    }
    @Test
    public void testFindJourneysNotMyDestination(){
        when(
            journeyDao.search(
                eq(DESCRIPTION),
                eq(USER_ID_2),
                any(),
                eq(SortFieldJourney.END_DATE),
                eq(SortDirection.DESC),
                eq(CITY_NAME),
                eq(UNI_NAME),
                any(LocalDate.class),
                eq(END_DATE),
                eq(INTEREST_NAME),
                eq(true),
                eq(false),
                eq(PAGE_1_DEFAULT)
            )
        ).thenReturn(JOURNEY_PAGE);

        Page<Journey> page = journeyService.findJourneys(
            DESCRIPTION,
            USER_ID_WITH_JOURNEY,
            null,
            SortFieldJourney.from("end_date"),
            SortDirection.from("desc"),
            CITY_NAME,
            UNI_NAME,
            START_DATE,
            END_DATE,
            INTEREST_NAME,
            false,
            true,
            false,
            PAGE_1_DEFAULT
        );

        assertNotNull(page);
        assertEquals(JOURNEY_PAGE, page);
    }
    @Test
    public void testFindJourneysMissingUser(){
        when(
            journeyDao.search(
                eq(DESCRIPTION),
                eq(null),
                any(),
                eq(SortFieldJourney.END_DATE),
                eq(SortDirection.DESC),
                eq(CITY_NAME),
                eq(UNI_NAME),
                any(LocalDate.class),
                eq(END_DATE),
                eq(INTEREST_NAME),
                eq(true),
                eq(false),
                any(PageParams.class)
            )
        ).thenReturn(JOURNEY_PAGE);
        
        Page<Journey> page = journeyService.findJourneys(
            DESCRIPTION,
            null,
            null,
            SortFieldJourney.from("end_date"),
            SortDirection.from("desc"),
            CITY_NAME,
            UNI_NAME,
            START_DATE,
            END_DATE,
            INTEREST_NAME,
            false,
            true,
            false,
            PAGE_1_DEFAULT
        );
    
        assertNotNull(page);
        assertEquals(JOURNEY_PAGE, page);
    }

    @Test
    public void testExistsByUserEmail(){
        when(
            userService.findUserByEmail(eq(EMAIL))
        ).thenReturn(Optional.of(USER_WITH_JOURNEY));

        boolean hasJourney = journeyService.existsByUserEmail(EMAIL);

        assertTrue(hasJourney);
    }
    @Test(expected = UserNotFoundException.class)
    public void testExistsByUserEmailWrongUser(){
        when(
            userService.findUserByEmail(eq(EMAIL))
        ).thenReturn(Optional.empty());

        boolean hasJourney = journeyService.existsByUserEmail(EMAIL);

        assertFalse(hasJourney);
    }



    @Test
    public void testFindRecommendedJourneysWithEmail(){
        when(
            userService.findUserByEmail(eq(EMAIL))
        ).thenReturn(Optional.of(USER_WITH_JOURNEY));
        when(
            journeyDao.findRecommended(eq(EMAIL), eq(PAGE_1_DEFAULT))
        ).thenReturn(JOURNEY_PAGE);

        List<Journey> journeys = journeyService.findRecommendedJourneys(EMAIL, 2);

        assertNotNull(journeys);
        assertEquals(JOURNEYS, journeys);
    }
    @Test
    public void testFindRecommendedJourneysWithUserNoJourneysButJourneyInCity(){
        when(
            userService.findUserByEmail(eq(EMAIL))
        ).thenReturn(Optional.of(USER));
        when(
            journeyDao.findByOriginCity(eq(CITY_ID), eq(PAGE_1_DEFAULT))
        ).thenReturn(JOURNEY_PAGE);

        List<Journey> journeys = journeyService.findRecommendedJourneys(EMAIL, 2);

        assertNotNull(journeys);
        assertEquals(JOURNEYS, journeys);
    }
    @Test
    public void testFindRecommendedJourneysWithUserNoJourneysNoJourneyInCity(){
        when(
            userService.findUserByEmail(eq(EMAIL))
        ).thenReturn(Optional.of(USER));
        when(
            journeyDao.findByOriginCity(eq(CITY_ID), eq(PAGE_1_DEFAULT))
        ).thenReturn(new Page<>(List.of(), 1, 1, 1));
        when(
            journeyDao.findAll(eq(PAGE_1_DEFAULT))
        ).thenReturn(JOURNEY_PAGE);

        List<Journey> journeys = journeyService.findRecommendedJourneys(EMAIL, 2);

        assertNotNull(journeys);
        assertEquals(JOURNEYS, journeys);
    }
    @Test
    public void testFindRecommendedJourneysWrongUser(){
        when(
            userService.findUserByEmail(eq(EMAIL))
        ).thenReturn(Optional.of(USER)).thenReturn(Optional.empty());
        when(
            journeyDao.findAll(any(PageParams.class))
        ).thenReturn(JOURNEY_PAGE);

        List<Journey> journeys = journeyService.findRecommendedJourneys(EMAIL, 2);

        assertNotNull(journeys);
        assertEquals(JOURNEYS, journeys);
    }
    @Test(expected = InvalidPaginationParamsException.class)
    public void testFindRecommendedJourneysWrongLimit(){
        journeyService.findRecommendedJourneys(EMAIL, 0);
    }

    @Test
    public void testDeleteJourney(){
        Journey newJourney = new Journey(USER, START_DATE, END_DATE, UNI, DESCRIPTION);
        when(
            journeyDao.findById(eq(JOURNEY_ID))
        ).thenReturn(Optional.of(newJourney));

        journeyService.deleteJourney(JOURNEY_ID, DESCRIPTION);

        assertTrue(newJourney.isDeleted());
        assertEquals(DESCRIPTION, newJourney.getDeletionMessage());
    }
    @Test
    public void testDeleteJourneyEmptyMessage(){
        Journey newJourney = new Journey(USER, START_DATE, END_DATE, UNI, DESCRIPTION);
        when(
            journeyDao.findById(eq(JOURNEY_ID))
        ).thenReturn(Optional.of(newJourney));

        journeyService.deleteJourney(JOURNEY_ID, "");

        assertTrue(newJourney.isDeleted());
        assertNull(newJourney.getDeletionMessage());
    }
    @Test
    public void testDeleteJourneyMissingMessage(){
        Journey newJourney = new Journey(USER, START_DATE, END_DATE, UNI, DESCRIPTION);
        when(
            journeyDao.findById(eq(JOURNEY_ID))
        ).thenReturn(Optional.of(newJourney));

        journeyService.deleteJourney(JOURNEY_ID, null);

        assertTrue(newJourney.isDeleted());
        assertNull(newJourney.getDeletionMessage());
    }
    @Test
    public void testDeleteJourneyMissingJourney(){
        Journey newJourney = new Journey(USER, START_DATE, END_DATE, UNI, DESCRIPTION);
        when(
            journeyDao.findById(eq(JOURNEY_ID))
        ).thenReturn(Optional.empty());

        journeyService.deleteJourney(JOURNEY_ID, DESCRIPTION);

        assertFalse(newJourney.isDeleted());
    }

    @Test
    public void testIsJourneyOwnedByUserByEmail(){
        when(journeyDao.findById(eq(JOURNEY_ID))).thenReturn(Optional.of(JOURNEY));

        boolean owned = journeyService.isJourneyOwnedByUser(EMAIL, JOURNEY_ID);

        assertTrue(owned);
    }
    @Test
    public void testIsJourneyOwnedByUserByEmailNotOwned(){
        when(journeyDao.findById(eq(JOURNEY_ID))).thenReturn(Optional.of(JOURNEY));

        boolean owned = journeyService.isJourneyOwnedByUser(EMAIL_2, JOURNEY_ID);

        assertFalse(owned);
    }
    @Test
    public void testIsJourneyOwnedByUserByEmailMissingJourney(){
        when(journeyDao.findById(eq(JOURNEY_ID))).thenReturn(Optional.empty());

        boolean owned = journeyService.isJourneyOwnedByUser(EMAIL_2, JOURNEY_ID);

        assertFalse(owned);
    }

    @Test
    public void testIsJourneyOwnedByUserObject(){
        boolean owned = journeyService.isJourneyOwnedByUser(JOURNEY, USER);

        assertTrue(owned);
    }
    @Test
    public void testIsJourneyOwnedByUserObjectNotOwned(){
        boolean owned = journeyService.isJourneyOwnedByUser(JOURNEY, USER_WITH_JOURNEY);

        assertFalse(owned);
    }
    @Test
    public void testIsJourneyOwnedByUserObjectMissingUser(){
        boolean owned = journeyService.isJourneyOwnedByUser(JOURNEY, null);

        assertFalse(owned);
    }
    @Test
    public void testIsJourneyOwnedByUserObjectMissingJourney(){
        boolean owned = journeyService.isJourneyOwnedByUser(null, USER);

        assertFalse(owned);
    }
    @Test
    public void testIsJourneyOwnedByUserObjectNoJourneyUser(){
        boolean owned = journeyService.isJourneyOwnedByUser(
            new Journey(null, START_DATE, END_DATE, UNI, DESCRIPTION), 
            USER
        );

        assertFalse(owned);
    }
    @Test
    public void testIsJourneyOwnedByUserObjectNoJourneyUserId(){
        boolean owned = journeyService.isJourneyOwnedByUser(
            new Journey(
                new User(
                    EMAIL, 
                    USERNAME, 
                    FIRSTNAME, 
                    LASTNAME, 
                    UNI, 
                    CAREER, 
                    CAREER_ID, 
                    LOCALE, 
                    false), 
                START_DATE, 
                END_DATE, 
                UNI, 
                DESCRIPTION),
            USER
        );

        assertFalse(owned);
    }
    @Test
    public void testIsJourneyOwnedByUserObjectNoUserId(){
        boolean owned = journeyService.isJourneyOwnedByUser(
            JOURNEY, 
            new User(
                EMAIL, 
                USERNAME, 
                FIRSTNAME, 
                LASTNAME, 
                UNI, 
                CAREER, 
                CAREER_ID, 
                LOCALE, 
                false
            )
        );

        assertFalse(owned);
    }

    @Test
    public void testFindJourneyByUserId(){
        when(
            userService.findUserById(eq(USER_ID))
        ).thenReturn(Optional.of(USER_WITH_JOURNEY));

        Optional<Journey> maybeJourney = journeyService.findJourneyByUserId(USER_ID);

        assertTrue(maybeJourney.isPresent());
    }
    @Test
    public void testFindJourneyByUserIdNoJourney(){
        when(
            userService.findUserById(eq(USER_ID))
        ).thenReturn(Optional.of(USER));

        Optional<Journey> maybeJourney = journeyService.findJourneyByUserId(USER_ID);

        assertTrue(maybeJourney.isEmpty());
    }
    @Test(expected = UserNotFoundException.class)
    public void testFindJourneyByUserIdNoUser(){
        when(
            userService.findUserById(eq(USER_ID))
        ).thenReturn(Optional.empty());

        journeyService.findJourneyByUserId(USER_ID);
    }

    @Test
    public void testUpdateJourney(){
        Journey newJourney = new Journey(
            JOURNEY_ID, 
            USER, 
            null, 
            null, 
            null, 
            null
        );
        when(
            journeyDao.findById(eq(JOURNEY_ID))
        ).thenReturn(Optional.of(newJourney));
        when(
            uniService.findById(eq(UNI_ID))
        ).thenReturn(Optional.of(UNI));

        journeyService.updateJourney(
            JOURNEY_ID, 
            UNI_ID,
            START_DATE, 
            END_DATE, 
            DESCRIPTION
        );

        assertEquals(DESCRIPTION, newJourney.getDescription());
        assertEquals(START_DATE, newJourney.getStartDate());
        assertEquals(END_DATE, newJourney.getEndDate());
        assertEquals(UNI, newJourney.getDestinationUniversity());
    }
    @Test(expected = InvalidReferenceException.class)
    public void testUpdateJourneyMissingUni(){
        when(
            journeyDao.findById(eq(JOURNEY_ID))
        ).thenReturn(Optional.of(JOURNEY));
        when(
            uniService.findById(eq(UNI_ID))
        ).thenReturn(Optional.empty());

        journeyService.updateJourney(
            JOURNEY_ID, 
            UNI_ID,
            START_DATE, 
            END_DATE, 
            DESCRIPTION
        );
    }
    @Test(expected = JourneyNotFoundException.class)
    public void testUpdateJourneyMissingJourney(){
        when(
            journeyDao.findById(eq(JOURNEY_ID))
        ).thenReturn(Optional.empty());

        journeyService.updateJourney(
            JOURNEY_ID, 
            UNI_ID,
            START_DATE, 
            END_DATE, 
            DESCRIPTION
        );
    }

    @Test
    public void testPatchJourney(){
        Journey j = new Journey(USER, null, null, null, null);
        when(
            journeyDao.findById(eq(JOURNEY_ID))
        ).thenReturn(Optional.of(j));
        when(
            uniService.findById(eq(UNI_ID))
        ).thenReturn(Optional.of(UNI));

        journeyService.patchJourney(JOURNEY_ID, UNI_ID, START_DATE, END_DATE, DESCRIPTION);

        assertNotNull(j.getDestinationUniversity());
        assertEquals(UNI_NAME, j.getDestinationUniversity().getName());
        assertEquals(START_DATE, j.getStartDate());
        assertEquals(END_DATE, j.getEndDate());
        assertEquals(DESCRIPTION, j.getDescription());
    }
    @Test
    public void testPatchJourneyNoPatches(){
        Journey j = new Journey(USER, START_DATE, END_DATE, UNI, DESCRIPTION);
        when(
            journeyDao.findById(eq(JOURNEY_ID))
        ).thenReturn(Optional.of(j));

        journeyService.patchJourney(JOURNEY_ID, null, null, null, null);

        assertNotNull(j.getDestinationUniversity());
        assertEquals(UNI_NAME, j.getDestinationUniversity().getName());
        assertEquals(START_DATE, j.getStartDate());
        assertEquals(END_DATE, j.getEndDate());
        assertEquals(DESCRIPTION, j.getDescription());
    }
    @Test(expected = InvalidReferenceException.class)
    public void testPatchJourneyMissingUni(){
        Journey j = new Journey(USER, null, null, null, null);
        when(
            journeyDao.findById(eq(JOURNEY_ID))
        ).thenReturn(Optional.of(j));
        when(
            uniService.findById(eq(UNI_ID))
        ).thenReturn(Optional.empty());

        journeyService.patchJourney(JOURNEY_ID, UNI_ID, START_DATE, END_DATE, DESCRIPTION);
    }
    @Test(expected = JourneyNotFoundException.class)
    public void testPatchJourneyMissingJourney(){

        when(
            journeyDao.findById(eq(JOURNEY_ID))
        ).thenReturn(Optional.empty());

        journeyService.patchJourney(JOURNEY_ID, UNI_ID, START_DATE, END_DATE, DESCRIPTION);
    }

    @Test
    public void testFindJourneyResponseById(){
        when(
            replyDao.findById(eq(REPLY_ID))
        ).thenReturn(Optional.of(REPLY));

        Optional<JourneyResponse> maybeResponse = journeyService.findJourneyResponseById(REPLY_ID);

        assertNotNull(maybeResponse);
        assertEquals(REPLY, maybeResponse.get());
    }
    @Test
    public void testFindJourneyResponseByIdJourneyID(){
        when(
            replyDao.findById(eq(REPLY_ID))
        ).thenReturn(Optional.of(REPLY));

        Optional<JourneyResponse> maybeResponse = journeyService.findJourneyResponseById(JOURNEY_ID, REPLY_ID);

        assertNotNull(maybeResponse);
        assertEquals(REPLY, maybeResponse.get());
    }
    @Test
    public void testFindJourneyResponseByIdJourneyIDNotRelated(){
        when(
            replyDao.findById(eq(REPLY_ID))
        ).thenReturn(Optional.of(REPLY));

        Optional<JourneyResponse> maybeResponse = journeyService.findJourneyResponseById(123123, REPLY_ID);

        assertNotNull(maybeResponse);
        assertTrue(maybeResponse.isEmpty());
    }
    @Test
    public void testFindJourneyResponseByIdJourneyIDMissingResponse(){
        when(
            replyDao.findById(eq(REPLY_ID))
        ).thenReturn(Optional.empty());

        Optional<JourneyResponse> maybeResponse = journeyService.findJourneyResponseById(JOURNEY_ID, REPLY_ID);

        assertNotNull(maybeResponse);
        assertTrue(maybeResponse.isEmpty());
    }

    @Test
    public void testDeleteJourneyJourneyResponse(){
        JourneyResponse newReply = new JourneyResponse(USER, JOURNEY, CAREER_NAME);
        when(
            replyDao.findById(eq(REPLY_ID))
        ).thenReturn(Optional.of(newReply));

        journeyService.deleteJourneyResponse(REPLY_ID, DESCRIPTION);

        assertTrue(newReply.isDeleted());
        assertEquals(DESCRIPTION, newReply.getDeletionMessage());
    }
    @Test
    public void testDeleteJourneyJourneyResponseMissingJourney(){
        JourneyResponse newReply = new JourneyResponse(USER, JOURNEY, CAREER_NAME);
        when(
            replyDao.findById(eq(REPLY_ID))
        ).thenReturn(Optional.empty());

        journeyService.deleteJourneyResponse(REPLY_ID, DESCRIPTION);

        assertFalse(newReply.isDeleted());
        assertNull(newReply.getDeletionMessage());
    }
    @Test
    public void testDeleteJourneyJourneyResponseWithJourneyID(){
        JourneyResponse newReply = new JourneyResponse(USER, JOURNEY, CAREER_NAME);
        when(
            replyDao.findById(eq(REPLY_ID))
        ).thenReturn(Optional.of(newReply));

        journeyService.deleteJourneyResponse(JOURNEY_ID, REPLY_ID, DESCRIPTION);

        assertTrue(newReply.isDeleted());
        assertEquals(DESCRIPTION, newReply.getDeletionMessage());
    }
    @Test
    public void testDeleteJourneyJourneyResponseWithJourneyIDMissingJourney(){
        JourneyResponse newReply = new JourneyResponse(USER, JOURNEY, CAREER_NAME);
        when(
            replyDao.findById(eq(REPLY_ID))
        ).thenReturn(Optional.empty());

        journeyService.deleteJourneyResponse(REPLY_ID, DESCRIPTION);

        assertFalse(newReply.isDeleted());
        assertNull(newReply.getDeletionMessage());
    }

    @Test
    public void testFindJourneyResponses(){
        when(
            journeyDao.findById(eq(JOURNEY_ID))
        ).thenReturn(Optional.of(JOURNEY));
        when(
            replyDao.findAllByJourneyId(
                eq(JOURNEY_ID), 
                any(PageParams.class)
            )
        ).thenReturn(REPLY_PAGE);

        Page<JourneyResponse> replies = journeyService.findJourneyResponses(
            JOURNEY_ID,
            PAGE_1_DEFAULT
        );

        assertNotNull(replies);
        assertEquals(REPLY_PAGE, replies);
    }
    @Test(expected = JourneyNotFoundException.class)
    public void testFindJourneyResponsesMissingJourney(){
        when(
            journeyDao.findById(eq(JOURNEY_ID))
        ).thenReturn(Optional.empty());

        journeyService.findJourneyResponses(
            JOURNEY_ID,
            PAGE_1_DEFAULT
        );
    }

    @Test
    public void testFindTipsByJourneyId(){
        when(
            journeyDao.findById(eq(JOURNEY_ID))
        ).thenReturn(Optional.of(JOURNEY));
        when(
            tipDao.findByJourneyId(eq(JOURNEY_ID), any(PageParams.class))
        ).thenReturn(TIP_PAGE);

        Page<Tip> tips = journeyService.findTipsByJourneyId(JOURNEY_ID, PAGE_1_DEFAULT);

        assertNotNull(tips);
        assertEquals(TIP_PAGE, tips);
    }
    @Test(expected=JourneyNotFoundException.class)
    public void testFindTipsByJourneyIdMissingJourney(){
        when(
            journeyDao.findById(eq(JOURNEY_ID))
        ).thenReturn(Optional.empty());

        journeyService.findTipsByJourneyId(JOURNEY_ID, PAGE_1_DEFAULT);
    }

    @Test
    public void testCreateTip(){
        when(
            journeyDao.findById(eq(JOURNEY_ID))
        ).thenReturn(Optional.of(JOURNEY));
        when(tipDao.create(eq(JOURNEY), eq(DESCRIPTION), eq(DESCRIPTION)))
                .thenReturn( TIP);

        Tip tip = journeyService.createTip(JOURNEY_ID, DESCRIPTION, DESCRIPTION);
        assertNotNull(tip);
        assertEquals(DESCRIPTION, tip.getTitle());
        assertEquals(DESCRIPTION, tip.getContent());
        assertEquals(JOURNEY, tip.getJourney());
        assertNotNull(tip.getDateTime());
    }

    @Test(expected = JourneyNotFoundException.class)
    public void testCreateTipMissingJourney(){
        when(
            journeyDao.findById(eq(JOURNEY_ID))
        ).thenReturn(Optional.empty());

        journeyService.createTip(JOURNEY_ID, DESCRIPTION, DESCRIPTION);
    }

    @Test
    public void testUpdateTip(){
        Tip newTip = new Tip(TIP_ID, null, null, JOURNEY, REPLY_TIMESTAMP);
        when(
            tipDao.findById(eq(TIP_ID))
        ).thenReturn(Optional.of(newTip));

        Tip updated = journeyService.updateTip(JOURNEY_ID, TIP_ID, DESCRIPTION, DESCRIPTION);

        assertNotNull(updated);
        assertEquals(DESCRIPTION, updated.getContent());
        assertEquals(DESCRIPTION, updated.getTitle());
    }
    @Test(expected = TipNotFoundException.class)
    public void testUpdateTipMissing(){
        when(
            tipDao.findById(eq(TIP_ID))
        ).thenReturn(Optional.empty());

        journeyService.updateTip(JOURNEY_ID, TIP_ID, DESCRIPTION, DESCRIPTION);
    }

    @Test
    public void testPatchTip(){
        Tip newTip = new Tip(TIP_ID, null, null, JOURNEY, REPLY_TIMESTAMP);
        when(
            tipDao.findById(eq(TIP_ID))
        ).thenReturn(Optional.of(newTip));

        Tip patched = journeyService.patchTip(JOURNEY_ID, TIP_ID, CITY_NAME, UNI_NAME);

        assertEquals(UNI_NAME, patched.getContent());
        assertEquals(CITY_NAME, patched.getTitle());
    }
    @Test
    public void testPatchTipNoPatches(){
        Tip newTip = new Tip(TIP_ID, CITY_NAME, UNI_NAME, JOURNEY, REPLY_TIMESTAMP);
        when(
            tipDao.findById(eq(TIP_ID))
        ).thenReturn(Optional.of(newTip));

        Tip patched = journeyService.patchTip(JOURNEY_ID, TIP_ID, null, null);

        assertEquals(UNI_NAME, patched.getContent());
        assertEquals(CITY_NAME, patched.getTitle());
    }
    @Test(expected = TipNotFoundException.class)
    public void testPatchTipMissingTipo(){
        when(
            tipDao.findById(eq(TIP_ID))
        ).thenReturn(Optional.empty());

        journeyService.patchTip(JOURNEY_ID, TIP_ID, null, null);
    }

    @Test(expected = TipNotFoundException.class)
    public void testDeleteTipMissing(){
        when(
            tipDao.findById(eq(TIP_ID))
        ).thenReturn(Optional.empty());

        journeyService.deleteTip(JOURNEY_ID, TIP_ID);
    }

    @Test
    public void testFindTipById(){
        when(
            tipDao.findById(eq(TIP_ID))
        ).thenReturn(Optional.of(TIP));

        Optional<Tip> maybeTip = journeyService.findTipById(TIP_ID);

        assertNotNull(maybeTip);
        assertTrue(maybeTip.isPresent());
        assertEquals(TIP, maybeTip.get());
    }

    @Test
    public void testFindTipByIDWithJourneyID(){
        when(
            tipDao.findById(eq(TIP_ID))
        ).thenReturn(Optional.of(TIP));

        Optional<Tip> maybeTip = journeyService.findTipById(JOURNEY_ID, TIP_ID);

        assertNotNull(maybeTip);
        assertTrue(maybeTip.isPresent());
    }
    @Test
    public void testFindTipByIDWithJourneyIDDifferent(){
        when(
            tipDao.findById(eq(TIP_ID))
        ).thenReturn(Optional.of(TIP));

        Optional<Tip> maybeTip = journeyService.findTipById(12341234, TIP_ID);

        assertNotNull(maybeTip);
        assertTrue(maybeTip.isEmpty());
    }
    @Test
    public void testFindTipByIDWithJourneyIDMissing(){
        when(
            tipDao.findById(eq(TIP_ID))
        ).thenReturn(Optional.empty());

        Optional<Tip> maybeTip = journeyService.findTipById(JOURNEY_ID, TIP_ID);

        assertNotNull(maybeTip);
        assertTrue(maybeTip.isEmpty());
    }

    @Test
    public void testIsTipOwnedByUserIdEmail(){
        when(
            tipDao.findById(eq(TIP_ID))
        ).thenReturn(Optional.of(TIP));

        boolean isOwned = journeyService.isTipOwnedByUser(JOURNEY_ID, TIP_ID, EMAIL);

        assertTrue(isOwned);
    }
    @Test
    public void testIsTipOwnedByUserIdEmailNotOwned(){
        when(
            tipDao.findById(eq(TIP_ID))
        ).thenReturn(Optional.of(TIP));

        boolean isOwned = journeyService.isTipOwnedByUser(JOURNEY_ID, TIP_ID, "EMAIL");

        assertFalse(isOwned);
    }
    @Test(expected = TipNotFoundException.class)
    public void testIsTipOwnedByUserIdEmailNotFound(){
        when(
            tipDao.findById(eq(TIP_ID))
        ).thenReturn(Optional.empty());

        journeyService.isTipOwnedByUser(JOURNEY_ID, TIP_ID, "EMAIL");
    }
}