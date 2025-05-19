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
import ar.edu.itba.paw.models.enums.SortDirection;
import ar.edu.itba.paw.models.enums.SortFieldJourney;
import ar.edu.itba.paw.models.exceptions.InvalidException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import ar.edu.itba.paw.interfaces.persistence.JourneyDao;
import ar.edu.itba.paw.interfaces.persistence.JourneyResponseDao;
import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.interfaces.services.InterestService;
import ar.edu.itba.paw.interfaces.services.UniversityService;
import ar.edu.itba.paw.interfaces.services.UserService;

@RunWith(MockitoJUnitRunner.class)
public class JourneyServiceImplTest {

    private static final long USER_ID = 0;
    private static final long UNI_ID = 2;
    private static final long CITY_ID = 3;
    private static final long CAREER_ID = 4;
    private static final long IMAGE_ID = 5;
    private static final long JOURNEY_ID = 6;
    private static final long INTEREST_ID = 8;
    private static final long REPLY_ID = 9;

    private static final String UNI_NAME = "uni";
    private static final String UNI_ABBR = "uni";
    private static final String EMAIL = "mail1";
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
    private static final User USER = new User(USER_ID, EMAIL, USERNAME, FIRSTNAME, LASTNAME, UNI, CAREER, IMAGE_ID, LOCALE, false, List.of());
    private static final Interest INTEREST = new Interest(INTEREST_ID, INTEREST_NAME);
    private static final List<Interest> INTERESTS = List.of(INTEREST);

    private static final LocalDate START_DATE = LocalDate.now();
    private static final LocalDate END_DATE = START_DATE.plusDays(10);
    private static final String DESCRIPTION = "desc";
    private static final Journey JOURNEY = new Journey(JOURNEY_ID, USER, START_DATE, END_DATE, UNI, DESCRIPTION);
    private static final List<Journey> JOURNEYS = List.of(JOURNEY);
    private static final Page<Journey> JOURNEY_PAGE = new Page<Journey>(JOURNEYS, 1, 1);

    private static final LocalDateTime REPLY_TIMESTAMP = LocalDateTime.now();
    private static final JourneyResponse REPLY = new JourneyResponse(REPLY_ID, USER, JOURNEY, DESCRIPTION, REPLY_TIMESTAMP);

    private static final PageParams PAGE_1_DEFAULT = new PageParams(1, 2);

    @InjectMocks
    JourneyServiceImpl journeyService;

    @Mock
    JourneyDao journeyDao;
    @Mock
    JourneyResponseDao replyDao;

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
        Mockito.when(
            uniService.findByName(Mockito.eq(UNI_NAME))
        ).thenReturn(Optional.of(UNI));
        Mockito.when(
            journeyDao.create(USER, UNI, START_DATE, END_DATE, DESCRIPTION)
        ).thenReturn(JOURNEY);

        Journey journey = journeyService.createJourney(USER, UNI_NAME, START_DATE, END_DATE, DESCRIPTION);

        assertNotNull(journey);
        assertEquals(JOURNEY, journey);
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
    public void testCreateJourneyResponse(){
        Mockito.when(
            journeyDao.findById(Mockito.eq(JOURNEY_ID))
        ).thenReturn(Optional.of(JOURNEY));
        Mockito.when(
            userService.findUserByEmail(Mockito.eq(EMAIL))
        ).thenReturn(Optional.of(USER));
        Mockito.when(
            interestService.findInterestsByUserId(Mockito.eq(USER_ID))
        ).thenReturn(INTERESTS);

        journeyService.createJourneyResponse(EMAIL, JOURNEY_ID, DESCRIPTION);
    }
    @Test(expected = RuntimeException.class)
    public void testCreateJourneyResponseUserNotFound(){
        Mockito.when(
            journeyDao.findById(Mockito.eq(JOURNEY_ID))
        ).thenReturn(Optional.of(JOURNEY));
        Mockito.when(
            userService.findUserByEmail(Mockito.eq(EMAIL))
        ).thenReturn(Optional.empty());

        journeyService.createJourneyResponse(EMAIL, JOURNEY_ID, DESCRIPTION);
    }
    @Test(expected = RuntimeException.class)
    public void testCreateJourneyResponseNotFound(){
        Mockito.when(
            journeyDao.findById(Mockito.eq(JOURNEY_ID))
        ).thenReturn(Optional.empty());

        journeyService.createJourneyResponse(EMAIL, JOURNEY_ID, DESCRIPTION);
    }



//    @Test
//    public void testGetJourneyByEmail(){
//        Mockito.when(
//            userService.findUserByEmail(Mockito.eq(EMAIL))
//        ).thenReturn(Optional.of(USER));
//        Mockito.when(
//            journeyDao.findByUserId(Mockito.eq(USER_ID))
//        ).thenReturn(Optional.of(JOURNEY));
//
//        Optional<Journey> maybeJourney = journeyService.getJourneyByEmail(EMAIL);
//
//        assertNotNull(maybeJourney);
//        assertTrue(maybeJourney.isPresent());
//        assertEquals(JOURNEY, maybeJourney.get());
//    }
//    @Test(expected = RuntimeException.class)
//    public void testGetJourneyByEmailUserNotFound(){
//        Mockito.when(
//            userService.findUserByEmail(Mockito.eq(EMAIL))
//        ).thenReturn(Optional.empty());
//
//        journeyService.getJourneyByEmail(EMAIL);
//    }
//
//    @Test
//    public void testFindJourneys(){
//        Mockito.when(
//            journeyDao.search(
//                Mockito.eq(DESCRIPTION),
//                Mockito.eq(USER_ID),
//                Mockito.eq(SortFieldJourney.END_DATE),
//                Mockito.eq(SortDirection.DESC),
//                Mockito.eq(UNI_NAME),
//                Mockito.eq(START_DATE),
//                Mockito.eq(END_DATE),
//                Mockito.eq(INTEREST_NAME),
//                Mockito.eq(false),
//                Mockito.eq(true),
//                Mockito.eq(true),
//                Mockito.eq(false),
//                Mockito.eq(PAGE_1_DEFAULT)
//            )
//        ).thenReturn(JOURNEY_PAGE);
//        Mockito.when(
//            journeyDao.findByUserId(Mockito.eq(USER_ID))
//        ).thenReturn(Optional.of(JOURNEY));
//
//        Page<Journey> page = journeyService.findJourneys(
//            DESCRIPTION,
//            USER,
//            SortFieldJourney.from("end_date"),
//            SortDirection.from("desc"),
//            UNI_NAME,
//            START_DATE,
//            END_DATE,
//            INTEREST_NAME,
//            false,
//            true,
//            true,
//            false,
//            PAGE_1_DEFAULT
//        );
//
//        assertNotNull(page);
//        assertEquals(JOURNEY_PAGE, page);
//    }
//    @Test(expected = InvalidException.class)
//    public void testFindJourneysUserHasNoJourneys(){
//        Mockito.when(
//            journeyDao.findByUserId(Mockito.eq(USER_ID))
//        ).thenReturn(Optional.empty());
//
//        Page<Journey> page = journeyService.findJourneys(
//            DESCRIPTION,
//            USER,
//            SortFieldJourney.from("end_date"),
//            SortDirection.from("desc"),
//            UNI_NAME,
//            START_DATE,
//            END_DATE,
//            INTEREST_NAME,
//            false,
//            true,
//            true,
//            false,
//            PAGE_1_DEFAULT
//        );
//
//        assertNotNull(page);
//        assertEquals(JOURNEY_PAGE, page);
//    }
    @Test
    public void testFindJourneysNoUser(){
        Mockito.when(
            journeyDao.search(
                Mockito.eq(DESCRIPTION), 
                Mockito.eq(null),
                Mockito.eq(SortFieldJourney.from(null)),
                Mockito.eq(SortDirection.from(null)),
                Mockito.eq(UNI_NAME),
                Mockito.eq(START_DATE),
                Mockito.eq(END_DATE),
                Mockito.eq(INTEREST_NAME),
                Mockito.eq(false),
                Mockito.eq(true),
                Mockito.eq(true),
                Mockito.eq(false),
                Mockito.eq(PAGE_1_DEFAULT)
            )
        ).thenReturn(JOURNEY_PAGE);

        Page<Journey> page = journeyService.findJourneys(
            DESCRIPTION, 
            null, 
            SortFieldJourney.from(""), 
            SortDirection.from(""), 
            UNI_NAME, 
            START_DATE, 
            END_DATE, 
            INTEREST_NAME, 
            false, 
            true, 
            true, 
            false, 
            PAGE_1_DEFAULT
        );

        assertNotNull(page);
        assertEquals(JOURNEY_PAGE, page);
    }

//    @Test
//    public void testExistsByUserEmail(){
//        Mockito.when(
//            userService.findUserByEmail(Mockito.eq(EMAIL))
//        ).thenReturn(Optional.of(USER));
//        Mockito.when(
//            journeyDao.findByUserId(Mockito.eq(USER_ID))
//        ).thenReturn(Optional.of(JOURNEY));
//
//        boolean hasJourney = journeyService.existsByUserEmail(EMAIL);
//
//        assertTrue(hasJourney);
//    }
//    @Test
//    public void testExistsByUserEmail2(){
//        Mockito.when(
//            userService.findUserByEmail(Mockito.eq(EMAIL))
//        ).thenReturn(Optional.of(USER));
//        Mockito.when(
//            journeyDao.findByUserId(Mockito.eq(USER_ID))
//        ).thenReturn(Optional.empty());
//
//        boolean hasJourney = journeyService.existsByUserEmail(EMAIL);
//
//        assertFalse(hasJourney);
//    }
    @Test(expected = RuntimeException.class)
    public void testUserHasJourneyWrongUser(){
        Mockito.when(
            userService.findUserByEmail(Mockito.eq(EMAIL))
        ).thenReturn(Optional.empty());

        boolean hasJourney = journeyService.existsByUserEmail(EMAIL);
        
        assertFalse(hasJourney);
    }

//    @Test
//    public void testFindRecommendedJourneysWithEmail(){
//        Mockito.when(
//            userService.findUserByEmail(Mockito.eq(EMAIL))
//        ).thenReturn(Optional.of(USER));
//        Mockito.when(
//            journeyDao.findByUserId(Mockito.eq(USER_ID))
//        ).thenReturn(Optional.of(JOURNEY));
//        Mockito.when(
//            journeyDao.findRecommended(Mockito.eq(EMAIL), Mockito.eq(PAGE_1_DEFAULT))
//        ).thenReturn(JOURNEY_PAGE);
//
//        List<Journey> journeys = journeyService.findRecommendedJourneys(EMAIL, 2);
//
//        assertNotNull(journeys);
//        assertEquals(JOURNEYS, journeys);
//    }
//    @Test
//    public void testFindRecommendedJourneysWithUserNoJourneysButJourneyInCity(){
//        Mockito.when(
//            userService.findUserByEmail(Mockito.eq(EMAIL))
//        ).thenReturn(Optional.of(USER));
//        Mockito.when(
//            journeyDao.findByUserId(Mockito.eq(USER_ID))
//        ).thenReturn(Optional.empty());
//        Mockito.when(
//            journeyDao.findByOriginCity(Mockito.eq(CITY_ID), Mockito.eq(PAGE_1_DEFAULT))
//        ).thenReturn(JOURNEY_PAGE);
//
//        List<Journey> journeys = journeyService.findRecommendedJourneys(EMAIL, 2);
//
//        assertNotNull(journeys);
//        assertEquals(JOURNEYS, journeys);
//    }
//    @Test
//    public void testFindRecommendedJourneysWithUserNoJourneysNoJourneyInCity(){
//        Mockito.when(
//            userService.findUserByEmail(Mockito.eq(EMAIL))
//        ).thenReturn(Optional.of(USER));
//        Mockito.when(
//            journeyDao.findByUserId(Mockito.eq(USER_ID))
//        ).thenReturn(Optional.empty());
//        Mockito.when(
//            journeyDao.findByOriginCity(Mockito.eq(CITY_ID), Mockito.eq(PAGE_1_DEFAULT))
//        ).thenReturn(new Page<Journey>(List.of(), 1, 2));
//        Mockito.when(
//            journeyDao.findAll(Mockito.eq(PAGE_1_DEFAULT))
//        ).thenReturn(JOURNEY_PAGE);
//
//        List<Journey> journeys = journeyService.findRecommendedJourneys(EMAIL, 2);
//
//        assertNotNull(journeys);
//        assertEquals(JOURNEYS, journeys);
//    }
    @Test(expected = RuntimeException.class)
    public void testFindRecommendedJourneysWrongUser(){
        Mockito.when(
            userService.findUserByEmail(Mockito.eq(EMAIL))
        ).thenReturn(Optional.empty());

        List<Journey> journeys = journeyService.findRecommendedJourneys(EMAIL, 2);

        assertNotNull(journeys);
        assertEquals(JOURNEYS, journeys);
    }
    @Test(expected = RuntimeException.class)
    public void testFindRecommendedJourneysWrongLimit(){
        journeyService.findRecommendedJourneys(EMAIL, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeleteJourneyNotFound(){
        Mockito.when(
            journeyDao.findById(Mockito.eq(JOURNEY_ID))
        ).thenReturn(Optional.empty());
        
        journeyService.deleteJourney(JOURNEY_ID, DESCRIPTION);
    }

    @Test(expected = RuntimeException.class)
    public void testUpdateJourneyMissingUni(){
        Mockito.when(
            uniService.findByName(Mockito.eq(UNI_NAME))
        ).thenReturn(Optional.empty());

        journeyService.updateJourney(JOURNEY_ID, UNI_NAME, START_DATE, END_DATE, DESCRIPTION);
    }

//    @Test
//    public void testUserHasJourneyUser(){
//        Mockito.when(
//            journeyDao.findByUserId(Mockito.eq(USER_ID))
//        ).thenReturn(Optional.of(JOURNEY));
//
//        boolean hasJourney = journeyService.existsByUser(USER);
//
//        assertTrue(hasJourney);
//    }


    @Test
    public void testDeleteJourneyJourneyResponse(){
        Mockito.when(
            replyDao.findById(Mockito.eq(REPLY_ID))
        ).thenReturn(Optional.of(REPLY));
        Mockito.when(
            journeyDao.findById(Mockito.eq(JOURNEY_ID))
        ).thenReturn(Optional.of(JOURNEY));
        Mockito.when(
            userService.findUserById(Mockito.eq(USER_ID))
        ).thenReturn(Optional.of(USER));

        journeyService.deleteJourneyResponse(REPLY_ID, DESCRIPTION);
    }
    @Test(expected = IllegalArgumentException.class)
    public void testDeleteJourneyJourneyResponseNoUser(){
        Mockito.when(
            replyDao.findById(Mockito.eq(REPLY_ID))
        ).thenReturn(Optional.of(REPLY));
        Mockito.when(
            journeyDao.findById(Mockito.eq(JOURNEY_ID))
        ).thenReturn(Optional.of(JOURNEY));
        Mockito.when(
            userService.findUserById(Mockito.eq(USER_ID))
        ).thenReturn(Optional.empty());

        journeyService.deleteJourneyResponse(REPLY_ID, DESCRIPTION);
    }
    @Test(expected = IllegalStateException.class)
    public void testDeleteJourneyJourneyResponseNoJourney(){
        Mockito.when(
            replyDao.findById(Mockito.eq(REPLY_ID))
        ).thenReturn(Optional.of(REPLY));
        Mockito.when(
            journeyDao.findById(Mockito.eq(JOURNEY_ID))
        ).thenReturn(Optional.empty());

        journeyService.deleteJourneyResponse(REPLY_ID, DESCRIPTION);
    }
    @Test(expected = IllegalArgumentException.class)
    public void testDeleteJourneyJourneyResponseNoReply(){
        Mockito.when(
            replyDao.findById(Mockito.eq(REPLY_ID))
        ).thenReturn(Optional.empty());

        journeyService.deleteJourneyResponse(REPLY_ID, DESCRIPTION);
    }

}