package ar.edu.itba.paw.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import ar.edu.itba.paw.models.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import ar.edu.itba.paw.interfaces.persistence.JourneyDao;
import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.interfaces.services.InterestService;
import ar.edu.itba.paw.interfaces.services.JourneyResponseService;
import ar.edu.itba.paw.interfaces.services.UniversityService;
import ar.edu.itba.paw.interfaces.services.UserService;

@RunWith(MockitoJUnitRunner.class)
public class JourneyServiceImplTest {

    private static final long USER_ID = 0;
    private static final long USER_ID2 = 1;
    private static final long UNI_ID = 2;
    private static final long CITY_ID = 3;
    private static final long CAREER_ID = 4;
    private static final long IMAGE_ID = 5;
    private static final long JOURNEY_ID = 6;
    private static final long JOURNEY_ID2 = 7;
    private static final long INTEREST_ID = 8;
    private static final long REPLY_ID = 9;

    private static final String UNI_NAME = "uni";
    private static final String UNI_ABBR = "uni";
    private static final String EMAIL = "mail1";
    private static final String EMAIL2 = "mail2";
    private static final String USERNAME = "user1";
    private static final String FIRSTNAME = "user";
    private static final String LASTNAME = "user";
    private static final String CITY_NAME = "citi";
    private static final String COUNTRY_NAME = "cuntry";
    private static final String CAREER_NAME = "career";

    private static final City CITY = new City(CITY_NAME, COUNTRY_NAME, CITY_ID);
    private static final University UNI = new University(UNI_ID, UNI_NAME, UNI_ABBR, CITY);
    private static final Career CAREER = new Career(CAREER_ID, CAREER_NAME);
    private static final Locale LOCALE = Locale.of("en");
    private static final User USER = new User(USER_ID, EMAIL, USERNAME, FIRSTNAME, LASTNAME, UNI, CAREER, IMAGE_ID, LOCALE, false);
    private static final User USER2 = new User(USER_ID2, EMAIL2, USERNAME, FIRSTNAME, LASTNAME, UNI, CAREER, IMAGE_ID, LOCALE, false);

    private static final LocalDate START_DATE = LocalDate.now();
    private static final LocalDate END_DATE = START_DATE.plusDays(10);
    private static final String DESCRIPTION = "desc";
    private static final Journey JOURNEY = new Journey(JOURNEY_ID, USER, START_DATE, END_DATE, UNI, DESCRIPTION);
    private static final Journey JOURNEY2 = new Journey(JOURNEY_ID2, USER2, START_DATE, END_DATE, UNI, DESCRIPTION);
    private static final List<Journey> JOURNEYS = List.of(JOURNEY);
    private static final Page<Journey> JOURNEY_PAGE = new Page<Journey>(JOURNEYS, 1, 1);

    private static final LocalDateTime REPLY_TIMESTAMP = LocalDateTime.now();
    private static final JourneyResponse REPLY = new JourneyResponse(REPLY_ID, USER_ID, USERNAME, JOURNEY_ID, DESCRIPTION, REPLY_TIMESTAMP);
    private static final List<JourneyResponse> REPLIES = List.of(REPLY);

    @InjectMocks
    JourneyServiceImpl journeyService;

    @Mock
    JourneyDao journeyDao;

    @Mock
    UserService userService;
    @Mock
    EmailService emailService;
    @Mock
    UniversityService uniService;
    @Mock
    JourneyResponseService responseService;
    @Mock
    InterestService interestService;
    @Mock
    UserDao userDao; //TODO No

    
    @Test
    public void testCreateJourney(){
        Mockito.when(
            uniService.findByName(Mockito.eq(UNI_NAME))
        ).thenReturn(Optional.of(UNI));
        Mockito.when(
            journeyDao.findOverlappingJourney(USER_ID, START_DATE, END_DATE)
        ).thenReturn(Optional.empty());
        Mockito.when(
            journeyDao.create(USER, UNI, START_DATE, END_DATE, DESCRIPTION)
        ).thenReturn(JOURNEY);

        Journey journey = journeyService.createJourney(USER, UNI_NAME, START_DATE, END_DATE, DESCRIPTION);

        assertNotNull(journey);
        assertEquals(JOURNEY, journey);
    }
    @Test(expected = RuntimeException.class)
    public void testCreateJourneyOverlapping(){
        Mockito.when(
            uniService.findByName(Mockito.eq(UNI_NAME))
        ).thenReturn(Optional.of(UNI));
        Mockito.when(
            journeyDao.findOverlappingJourney(USER_ID, START_DATE, END_DATE)
        ).thenReturn(Optional.of(JOURNEY));

        journeyService.createJourney(USER, UNI_NAME, START_DATE, END_DATE, DESCRIPTION);
    }
    @Test(expected = RuntimeException.class)
    public void testCreateJourneyNoUni(){
        Mockito.when(
            uniService.findByName(Mockito.eq(UNI_NAME))
        ).thenReturn(Optional.empty());

        journeyService.createJourney(USER, UNI_NAME, START_DATE, END_DATE, DESCRIPTION);
    }
    @Test(expected = RuntimeException.class)
    public void testCreateJourneyMissingStartDate(){
        journeyService.createJourney(USER, UNI_NAME, null, END_DATE, DESCRIPTION);
    }
    @Test(expected = RuntimeException.class)
    public void testCreateJourneyMissingEndDate(){
        journeyService.createJourney(USER, UNI_NAME, START_DATE, null, DESCRIPTION);
    }
    @Test(expected = RuntimeException.class)
    public void testCreateJourneyFlippedDates(){
        journeyService.createJourney(USER, UNI_NAME, END_DATE, START_DATE, DESCRIPTION);
    }
    @Test(expected = RuntimeException.class)
    public void testCreateJourneyStartsBeforeNow(){
        journeyService.createJourney(USER, UNI_NAME, START_DATE.plusDays(-1), START_DATE, DESCRIPTION);
    }

    @Test
    public void testReplyToJourney(){
        Mockito.when(
            journeyDao.findById(Mockito.eq(JOURNEY_ID))
        ).thenReturn(Optional.of(JOURNEY));
        Mockito.when(
            userService.findByEmail(Mockito.eq(EMAIL))
        ).thenReturn(Optional.of(USER));

        journeyService.replyToJourney(EMAIL, JOURNEY_ID, DESCRIPTION);
    }
    @Test(expected = RuntimeException.class)
    public void testReplyToJourneyUserNotFound(){
        Mockito.when(
            journeyDao.findById(Mockito.eq(JOURNEY_ID))
        ).thenReturn(Optional.of(JOURNEY));
        Mockito.when(
            userService.findByEmail(Mockito.eq(EMAIL))
        ).thenReturn(Optional.empty());

        journeyService.replyToJourney(EMAIL, JOURNEY_ID, DESCRIPTION);
    }
    @Test(expected = RuntimeException.class)
    public void testReplyToJourneyNotFound(){
        Mockito.when(
            journeyDao.findById(Mockito.eq(JOURNEY_ID))
        ).thenReturn(Optional.empty());

        journeyService.replyToJourney(EMAIL, JOURNEY_ID, DESCRIPTION);
    }

    @Test
    public void testGetAllJourneys(){
        Mockito.when(
            journeyDao.listAll()
        ).thenReturn(JOURNEYS);

        List<Journey> journeys = journeyService.getAllJourneys();

        assertNotNull(journeys);
        assertEquals(JOURNEYS, journeys);
    }
    @Test
    public void testGetAllJourneysPagedMissingQuery(){
        Mockito.when(
            journeyDao.listAll(Mockito.eq(1), Mockito.eq(2))
        ).thenReturn(JOURNEY_PAGE);

        Page<Journey> page = journeyService.getAllJourneys(null, new PageParams(1,2));

        assertNotNull(page);
        assertEquals(JOURNEY_PAGE, page);
    }
    @Test
    public void testGetAllJourneysPagedEmptyQuery(){
        Mockito.when(
            journeyDao.listAll(Mockito.eq(1), Mockito.eq(2))
        ).thenReturn(JOURNEY_PAGE);

        Page<Journey> page = journeyService.getAllJourneys("",new PageParams(1,2));

        assertNotNull(page);
        assertEquals(JOURNEY_PAGE, page);
    }
    @Test
    public void testGetAllJourneysPagedQuery(){
        Mockito.when(
            journeyDao.searchJourneys(Mockito.eq(DESCRIPTION), Mockito.eq(1), Mockito.eq(2))
        ).thenReturn(JOURNEY_PAGE);

        Page<Journey> page = journeyService.getAllJourneys(DESCRIPTION, new PageParams(1,2));

        assertNotNull(page);
        assertEquals(JOURNEY_PAGE, page);
    }

    @Test
    public void testGetJourneyById(){
        Mockito.when(
            journeyDao.findById(Mockito.eq(JOURNEY_ID))
        ).thenReturn(Optional.of(JOURNEY));

        Optional<Journey> maybeJourney = journeyService.getJourneyById(JOURNEY_ID);

        assertNotNull(maybeJourney);
        assertTrue(maybeJourney.isPresent());
        assertEquals(JOURNEY, maybeJourney.get());
    }

    @Test
    public void testGetJourneyByEmail(){
        Mockito.when(
            userService.findByEmail(Mockito.eq(EMAIL))
        ).thenReturn(Optional.of(USER));
        Mockito.when(
            journeyDao.findByUserId(Mockito.eq(USER_ID))
        ).thenReturn(Optional.of(JOURNEY));

        Optional<Journey> maybeJourney = journeyService.getJourneyByEmail(EMAIL);

        assertNotNull(maybeJourney);
        assertTrue(maybeJourney.isPresent());
        assertEquals(JOURNEY, maybeJourney.get());
    }
    @Test(expected = RuntimeException.class)
    public void testGetJourneyByEmailUserNotFound(){
        Mockito.when(
            userService.findByEmail(Mockito.eq(EMAIL))
        ).thenReturn(Optional.empty());

        journeyService.getJourneyByEmail(EMAIL);
    }

    @Test
    public void testGetAllJourneysFilteredWithQuery(){
        Mockito.when(
            journeyDao.searchJourneys(Mockito.eq(DESCRIPTION), Mockito.eq(1), Mockito.eq(2))
        ).thenReturn(JOURNEY_PAGE);

        Page<Journey> page = journeyService.getAllJourneys(DESCRIPTION, null, null, null, null, null, new PageParams(1,2));

        assertNotNull(page);
        assertEquals(JOURNEY_PAGE, page);
    }
    @Test
    public void testGetAllJourneysFilteredEmptyQuery(){
        Mockito.when(
            journeyDao.findByFilters(Mockito.eq(USER_ID), Mockito.eq(CITY_ID), Mockito.eq(START_DATE), Mockito.eq(END_DATE), Mockito.eq(INTEREST_ID), Mockito.eq(1), Mockito.eq(2))
        ).thenReturn(JOURNEY_PAGE);

        Page<Journey> page = journeyService.getAllJourneys("", USER, CITY_ID, START_DATE, END_DATE, INTEREST_ID, new PageParams(1,2));

        assertNotNull(page);
        assertEquals(JOURNEY_PAGE, page);
    }
    @Test
    public void testGetAllJourneysFilteredMissingQuery(){
        Mockito.when(
            journeyDao.findByFilters(Mockito.eq(null), Mockito.eq(CITY_ID), Mockito.eq(START_DATE), Mockito.eq(END_DATE), Mockito.eq(INTEREST_ID), Mockito.eq(1), Mockito.eq(2))
        ).thenReturn(JOURNEY_PAGE);

        Page<Journey> page = journeyService.getAllJourneys(null, null, CITY_ID, START_DATE, END_DATE, INTEREST_ID, new PageParams(1,2));

        assertNotNull(page);
        assertEquals(JOURNEY_PAGE, page);
    }

    @Test
    public void testUserHasJourney(){
        Mockito.when(
            userService.findByEmail(Mockito.eq(EMAIL))
        ).thenReturn(Optional.of(USER));
        Mockito.when(
            journeyDao.findByUserId(Mockito.eq(USER_ID))
        ).thenReturn(Optional.of(JOURNEY));

        boolean hasJourney = journeyService.userHasJourney(EMAIL);
        
        assertTrue(hasJourney);
    }
    @Test
    public void testUserHasJourneyNoJourney(){
        Mockito.when(
            userService.findByEmail(Mockito.eq(EMAIL))
        ).thenReturn(Optional.of(USER));
        Mockito.when(
            journeyDao.findByUserId(Mockito.eq(USER_ID))
        ).thenReturn(Optional.empty());

        boolean hasJourney = journeyService.userHasJourney(EMAIL);
        
        assertFalse(hasJourney);
    }
    @Test
    public void testUserHasJourneyWrongUser(){
        Mockito.when(
            userService.findByEmail(Mockito.eq(EMAIL))
        ).thenReturn(Optional.empty());

        boolean hasJourney = journeyService.userHasJourney(EMAIL);
        
        assertFalse(hasJourney);
    }

    @Test
    public void testGetRecommendedJourneysWithEmail(){
        Mockito.when(
            userService.findByEmail(Mockito.eq(EMAIL))
        ).thenReturn(Optional.of(USER));
        Mockito.when(
            journeyDao.findByUserId(Mockito.eq(USER_ID))
        ).thenReturn(Optional.of(JOURNEY));
        Mockito.when(
            journeyDao.getRecommendedJourneys(Mockito.eq(EMAIL), Mockito.eq(1), Mockito.eq(2))
        ).thenReturn(JOURNEY_PAGE);

        List<Journey> journeys = journeyService.getRecommendedJourneys(EMAIL, 2);

        assertNotNull(journeys);
        assertEquals(JOURNEYS, journeys);
    }
    @Test
    public void testGetRecommendedJourneysWithUserNoJourneysButJourneyInCity(){
        Mockito.when(
            userService.findByEmail(Mockito.eq(EMAIL))
        ).thenReturn(Optional.of(USER));
        Mockito.when(
            journeyDao.findByUserId(Mockito.eq(USER_ID))
        ).thenReturn(Optional.empty());
        Mockito.when(
            journeyDao.findByOriginCity(Mockito.eq(CITY_ID), Mockito.eq(1), Mockito.eq(2))
        ).thenReturn(JOURNEY_PAGE);

        List<Journey> journeys = journeyService.getRecommendedJourneys(EMAIL, 2);

        assertNotNull(journeys);
        assertEquals(JOURNEYS, journeys);
    }
    @Test
    public void testGetRecommendedJourneysWithUserNoJourneysNoJourneyInCity(){
        Mockito.when(
            userService.findByEmail(Mockito.eq(EMAIL))
        ).thenReturn(Optional.of(USER));
        Mockito.when(
            journeyDao.findByUserId(Mockito.eq(USER_ID))
        ).thenReturn(Optional.empty());
        Mockito.when(
            journeyDao.findByOriginCity(Mockito.eq(CITY_ID), Mockito.eq(1), Mockito.eq(2))
        ).thenReturn(new Page<Journey>(List.of(), 1, 2));
        Mockito.when(
            journeyDao.listAll(Mockito.eq(1), Mockito.eq(2))
        ).thenReturn(JOURNEY_PAGE);

        List<Journey> journeys = journeyService.getRecommendedJourneys(EMAIL, 2);

        assertNotNull(journeys);
        assertEquals(JOURNEYS, journeys);
    }
    @Test
    public void testGetRecommendedJourneysWrongUser(){
        Mockito.when(
            userService.findByEmail(Mockito.eq(EMAIL))
        ).thenReturn(Optional.empty());
        Mockito.when(
            journeyDao.listAll(Mockito.eq(1), Mockito.eq(2))
        ).thenReturn(JOURNEY_PAGE);

        List<Journey> journeys = journeyService.getRecommendedJourneys(EMAIL, 2);

        assertNotNull(journeys);
        assertEquals(JOURNEYS, journeys);
    }
    @Test(expected = RuntimeException.class)
    public void testGetRecommendedJourneysWrongLimit(){
        journeyService.getRecommendedJourneys(EMAIL, 0);
    }

    @Test
    public void testGetJourneysByUser(){
        Mockito.when(
            journeyDao.getJourneysByUser(Mockito.eq(EMAIL))
        ).thenReturn(JOURNEYS);

        List<Journey> journeys = journeyService.getJourneysByUser(EMAIL);
        
        assertNotNull(journeys);
        assertEquals(JOURNEYS, journeys);
    }

    @Test
    public void testGetJourneyResponses(){
        Mockito.when(
            responseService.listAllFromJourney(Mockito.eq(JOURNEY_ID))
        ).thenReturn(REPLIES);

        List<JourneyResponse> replies = journeyService.getJourneyResponses(JOURNEY_ID);
        
        assertNotNull(replies);
        assertEquals(REPLIES, replies);
    }

    @Test
    public void testGetOthersJourneysID(){
        Mockito.when(
            journeyDao.getOthersJourneys(Mockito.eq(USER_ID))
        ).thenReturn(JOURNEYS);

        List<Journey> journeys = journeyService.getOthersJourneys(USER_ID);

        assertNotNull(journeys);
        assertEquals(JOURNEYS, journeys);
    }
    @Test
    public void testGetOthersJourneysMail(){
        Mockito.when(
            userService.findByEmail(Mockito.eq(EMAIL))
        ).thenReturn(Optional.of(USER));
        Mockito.when(
            journeyDao.getOthersJourneys(Mockito.eq(USER_ID))
        ).thenReturn(JOURNEYS);

        List<Journey> journeys = journeyService.getOthersJourneys(EMAIL);

        assertNotNull(journeys);
        assertEquals(JOURNEYS, journeys);
    }
    @Test(expected = RuntimeException.class)
    public void testGetOthersJourneysWrongMail(){
        Mockito.when(
            userService.findByEmail(Mockito.eq(EMAIL))
        ).thenReturn(Optional.empty());

        journeyService.getOthersJourneys(EMAIL);
    }

    @Test
    public void testUpdateJourneyDates(){
        Mockito.when(
            journeyDao.findById(Mockito.eq(JOURNEY_ID))
        ).thenReturn(Optional.of(JOURNEY)); 
        Mockito.when(
            journeyDao.findOverlappingJourney(Mockito.eq(USER_ID), Mockito.eq(START_DATE), Mockito.eq(END_DATE))
        ).thenReturn(Optional.empty());

        journeyService.updateJourneyDates(JOURNEY_ID, START_DATE, END_DATE);
    }
    @Test(expected = RuntimeException.class)
    public void testUpdateJourneyDatesOverlappingOther(){
        Mockito.when(
            journeyDao.findById(Mockito.eq(JOURNEY_ID))
        ).thenReturn(Optional.of(JOURNEY)); 
        Mockito.when(
            journeyDao.findOverlappingJourney(Mockito.eq(USER_ID), Mockito.eq(START_DATE), Mockito.eq(END_DATE))
        ).thenReturn(Optional.of(JOURNEY2));

        journeyService.updateJourneyDates(JOURNEY_ID, START_DATE, END_DATE);
    }
    @Test
    public void testUpdateJourneyDatesOverlappingItself(){
        Mockito.when(
            journeyDao.findById(Mockito.eq(JOURNEY_ID))
        ).thenReturn(Optional.of(JOURNEY)); 
        Mockito.when(
            journeyDao.findOverlappingJourney(Mockito.eq(USER_ID), Mockito.eq(START_DATE), Mockito.eq(END_DATE))
        ).thenReturn(Optional.of(JOURNEY));

        journeyService.updateJourneyDates(JOURNEY_ID, START_DATE, END_DATE);
    }
    @Test(expected = RuntimeException.class)
    public void testUpdateJourneyDatesMissingJourney(){
        Mockito.when(
            journeyDao.findById(Mockito.eq(JOURNEY_ID))
        ).thenReturn(Optional.empty()); 

        journeyService.updateJourneyDates(JOURNEY_ID, START_DATE, END_DATE);
    }
    @Test(expected = RuntimeException.class)
    public void testUpdateJourneyDatesMissingDates(){
        journeyService.updateJourneyDates(JOURNEY_ID, null, null);
    }
    @Test(expected = RuntimeException.class)
    public void testUpdateJourneyDatesDatesFlipped(){
        journeyService.updateJourneyDates(JOURNEY_ID, END_DATE, START_DATE);
    }
    @Test(expected = RuntimeException.class)
    public void testUpdateJourneyDatesBeforeNow(){
        journeyService.updateJourneyDates(JOURNEY_ID, START_DATE.plusDays(-100), END_DATE);
    }

    @Test
    public void testUpdateJourneyDescription(){
        Mockito.when(
            journeyDao.findById(Mockito.eq(JOURNEY_ID))
        ).thenReturn(Optional.of(JOURNEY));
        
        journeyService.updateJourneyDescription(JOURNEY_ID, DESCRIPTION);
    }
    @Test(expected = RuntimeException.class)
    public void testUpdateJourneyDescriptionMissingJourney(){
        Mockito.when(
            journeyDao.findById(Mockito.eq(JOURNEY_ID))
        ).thenReturn(Optional.empty());
        
        journeyService.updateJourneyDescription(JOURNEY_ID, DESCRIPTION);
    }

    @Test
    public void testUpdateDestinationName(){
        Mockito.when(
            uniService.findByName(Mockito.eq(UNI_NAME))
        ).thenReturn(Optional.of(UNI));

        journeyService.updateJourneyDestination(JOURNEY_ID, UNI_NAME);
    }
    @Test(expected = RuntimeException.class)
    public void testUpdateDestinationNameMissing(){
        Mockito.when(
            uniService.findByName(Mockito.eq(UNI_NAME))
        ).thenReturn(Optional.empty());

        journeyService.updateJourneyDestination(JOURNEY_ID, UNI_NAME);
    }
    
    @Test
    public void testUpdateDestinationID(){
        Mockito.when(
            uniService.findById(Mockito.eq(UNI_ID))
        ).thenReturn(Optional.of(UNI));

        journeyService.updateJourneyDestination(JOURNEY_ID, UNI_ID);
    }
    @Test(expected = RuntimeException.class)
    public void testUpdateDestinationIdMissing(){
        Mockito.when(
            uniService.findById(Mockito.eq(UNI_ID))
        ).thenReturn(Optional.empty());

        journeyService.updateJourneyDestination(JOURNEY_ID, UNI_ID);
    }

    @Test
    public void testDelete(){
        Mockito.when(
            journeyDao.findById(Mockito.eq(JOURNEY_ID))
        ).thenReturn(Optional.of(JOURNEY));
        
        journeyService.delete(JOURNEY_ID, DESCRIPTION);
    }
    @Test(expected = IllegalArgumentException.class)
    public void testDeleteNotFound(){
        Mockito.when(
            journeyDao.findById(Mockito.eq(JOURNEY_ID))
        ).thenReturn(Optional.empty());
        
        journeyService.delete(JOURNEY_ID, DESCRIPTION);
    }

    @Test
    public void testIsJourneyOwnedByUser(){
        Mockito.when(
            journeyDao.findById(Mockito.eq(JOURNEY_ID))
        ).thenReturn(Optional.of(JOURNEY));

        boolean isOwner = journeyService.isJourneyOwnedByUser(EMAIL, JOURNEY_ID);

        assertTrue(isOwner);
    }
    @Test
    public void testIsJourneyOwnedByUserNotOwned(){
        Mockito.when(
            journeyDao.findById(Mockito.eq(JOURNEY_ID2))
        ).thenReturn(Optional.of(JOURNEY2));

        boolean isOwner = journeyService.isJourneyOwnedByUser(EMAIL, JOURNEY_ID2);

        assertFalse(isOwner);
    }
    @Test
    public void testIsJourneyOwnedByUserMissingJourney(){
        Mockito.when(
            journeyDao.findById(Mockito.eq(JOURNEY_ID))
        ).thenReturn(Optional.empty());

        boolean isOwner = journeyService.isJourneyOwnedByUser(EMAIL, JOURNEY_ID);

        assertFalse(isOwner);
    }

    @Test
    public void testEditJourney(){
        Mockito.when(
            uniService.findByName(Mockito.eq(UNI_NAME))
        ).thenReturn(Optional.of(UNI));

        journeyService.editJourney(JOURNEY_ID, UNI_NAME, START_DATE, END_DATE, DESCRIPTION);
    }
    @Test(expected = RuntimeException.class)
    public void testEditJourneyMissingUni(){
        Mockito.when(
            uniService.findByName(Mockito.eq(UNI_NAME))
        ).thenReturn(Optional.empty());

        journeyService.editJourney(JOURNEY_ID, UNI_NAME, START_DATE, END_DATE, DESCRIPTION);
    }

    @Test
    public void testUserHasJourneyUser(){
        Mockito.when(
            journeyDao.findByUserId(Mockito.eq(USER_ID))
        ).thenReturn(Optional.of(JOURNEY));

        boolean hasJourney = journeyService.userHasJourney(USER);

        assertTrue(hasJourney);
    }
}
