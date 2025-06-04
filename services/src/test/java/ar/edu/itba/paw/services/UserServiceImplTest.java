package ar.edu.itba.paw.services;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.exceptions.CareerNotFoundException;
import ar.edu.itba.paw.models.exceptions.InvalidTokenException;
import ar.edu.itba.paw.models.exceptions.UniversityNotFoundException;
import ar.edu.itba.paw.models.exceptions.UserNotFoundException;
import ar.edu.itba.paw.models.exceptions.UserValidatedException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.services.CareerService;
import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.interfaces.services.ImageService;
import ar.edu.itba.paw.interfaces.services.InterestService;
import ar.edu.itba.paw.interfaces.services.TokenService;
import ar.edu.itba.paw.interfaces.services.UniversityService;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceImplTest {

    private static final String EMAIL = "email";
    private static final String USERNAME = "username";
    private static final String FIRSTNAME = "name";
    private static final String LASTNAME = "name";
    private static final University UNIVERSITY = new University((long)0, "cool", null, null);
    private static final Career CAREER = new Career((long)0, null);
    private static final Image IMAGE = new Image((long)0, new byte[0]);
    private static final String PASSWORD = "null";
    private static final Locale LOCALE = Locale.of("en");
    private static final long USER_ID = 0;
    private static final long EVENT_ID = 1;
    private static final Interest INTEREST = new Interest((long)0, "name");
    private static final User USER = new User(USER_ID, EMAIL, USERNAME, FIRSTNAME, LASTNAME, UNIVERSITY, CAREER, IMAGE.getId(), LOCALE, false, true);
    private static final User USER_NOT_VALIDATED = new User(USER_ID, EMAIL, USERNAME, FIRSTNAME, LASTNAME, UNIVERSITY, CAREER, IMAGE.getId(), LOCALE, false, false);
    private static final PageParams PAGE_1_DEFAULT = new PageParams(1, 2);
    private static final String TOKEN_VALUE = "null";
    private static final LocalDateTime TOKEN_EXPIRATION = LocalDateTime.now();
    private static final Token TOKEN = new Token(USER_NOT_VALIDATED, TOKEN_VALUE, TOKEN_EXPIRATION);
    private static final List<User> USERS = List.of(USER);
    private static final Page<User> USER_PAGE = new Page<>(USERS, 1,1);
    private static final double RATING = 5.0;

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserDao userDao;
    @Mock
    private UniversityService universityService;
    @Mock
    private ImageService imageService;
    @Mock
    private CareerService careerService;
    @Mock
    private InterestService interestService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private EmailService emailService;
    @Mock
    private TokenService tokenService;

    @Test
    public void testCreateUser(){
        when(
            universityService.findByName(eq(UNIVERSITY.getName()))
        ).thenReturn(Optional.of(UNIVERSITY));
        when(
            careerService.findCareerByName(eq(CAREER.getName()))
        ).thenReturn(Optional.of(CAREER));
        when(
            imageService.createImage(eq(IMAGE.getData()))
        ).thenReturn(IMAGE.getId());
        when(
            passwordEncoder.encode(eq(PASSWORD))
        ).thenReturn(PASSWORD);
        when(
            userDao.create(
                eq(EMAIL), 
                eq(USERNAME), 
                eq(FIRSTNAME), 
                eq(LASTNAME), 
                eq(UNIVERSITY), 
                eq(CAREER), 
                eq(IMAGE.getId()), 
                eq(PASSWORD), 
                eq(LOCALE), 
                any(Boolean.class)
            )
        ).thenReturn(USER);
        when(
            tokenService.userTokenControl(USER)
        ).thenReturn(TOKEN);

        User user = userService.createUser(
            EMAIL, 
            USERNAME, 
            FIRSTNAME, 
            LASTNAME, 
            UNIVERSITY.getName(), 
            CAREER.getName(), 
            IMAGE.getData(), 
            List.of(INTEREST.getName()), 
            PASSWORD, 
            LOCALE
        );

        assertNotNull(user);
        assertEquals(USER, user);
    }
    @Test(expected = CareerNotFoundException.class)
    public void testCreateUserMissingCareer(){
        when(
            universityService.findByName(eq(UNIVERSITY.getName()))
        ).thenReturn(Optional.of(UNIVERSITY));
        when(
            careerService.findCareerByName(eq(CAREER.getName()))
        ).thenReturn(Optional.empty());

        userService.createUser(
            EMAIL, 
            USERNAME, 
            FIRSTNAME, 
            LASTNAME, 
            UNIVERSITY.getName(), 
            CAREER.getName(), 
            IMAGE.getData(), 
            List.of(INTEREST.getName()), 
            PASSWORD, 
            LOCALE
        );
    }
    @Test(expected = UniversityNotFoundException.class)
    public void testCreateUserMissingUniversity(){
        when(
            universityService.findByName(eq(UNIVERSITY.getName()))
        ).thenReturn(Optional.empty());

        userService.createUser(
            EMAIL, 
            USERNAME, 
            FIRSTNAME, 
            LASTNAME,
            UNIVERSITY.getName(), 
            CAREER.getName(), 
            IMAGE.getData(), 
            List.of(INTEREST.getName()),
            PASSWORD, 
            LOCALE
        );
    }

    @Test
    public void testVerifyUser(){
        when(
            tokenService.getByToken(eq(TOKEN_VALUE))
        ).thenReturn(Optional.of(
            new Token(
                new User(
                    EMAIL, 
                    USERNAME, 
                    FIRSTNAME, 
                    LASTNAME, 
                    UNIVERSITY, 
                    CAREER, 
                    USER_ID, 
                    LOCALE, 
                    false), 
                TOKEN_VALUE, 
                TOKEN_EXPIRATION
            )
        ));

        User user = userService.verifyUser(TOKEN_VALUE);

        assertTrue(user.isValidated());
    }
    @Test(expected = UserValidatedException.class)
    public void testVerifyUserAlreadyValidated(){
        when(
            tokenService.getByToken(eq(TOKEN_VALUE))
        ).thenReturn(Optional.of(new Token(USER, TOKEN_VALUE, TOKEN_EXPIRATION)));

        userService.verifyUser(TOKEN_VALUE);
    }
    @Test(expected = InvalidTokenException.class)
    public void testVerifyUserTokenNotFound(){
        when(
            tokenService.getByToken(eq(TOKEN_VALUE))
        ).thenReturn(Optional.empty());

        userService.verifyUser(TOKEN_VALUE);
    }

    @Test
    public void testUpdatePassword(){
        User newUser = new User(
            EMAIL, 
            USERNAME, 
            FIRSTNAME, 
            LASTNAME, 
            UNIVERSITY, 
            CAREER, 
            IMAGE.getId(), 
            "PASSWORD", 
            LOCALE, 
            true
        );
        when(
            userDao.findById(eq(USER_ID))
        ).thenReturn(Optional.of(newUser));
        when(
            passwordEncoder.encode(eq(PASSWORD))
        ).thenReturn(PASSWORD);

        userService.updatePassword(USER_ID, PASSWORD);

        assertEquals(PASSWORD, newUser.getPassword());
    }
    @Test(expected = UserNotFoundException.class)
    public void testUpdatePasswordMissingUser(){
        when(
            userDao.findById(eq(USER_ID))
        ).thenReturn(Optional.empty());

        userService.updatePassword(USER_ID, PASSWORD);
    }

    @Test
    public void testFindUserByEmail(){
        when(
            userDao.findByEmail(eq(EMAIL))
        ).thenReturn(Optional.of(USER));

        Optional<User> maybeUser = userService.findUserByEmail(EMAIL);

        assertNotNull(maybeUser);
        assertEquals(USER, maybeUser.get());
    }

    @Test
    public void testFindUserById(){
        when(
            userDao.findById(eq(USER_ID))
        ).thenReturn(Optional.of(USER));

        Optional<User> maybeUser = userService.findUserById(USER_ID);

        assertNotNull(maybeUser);
        assertEquals(USER, maybeUser.get());
    }

    @Test
    public void testUserExistsByUsername(){
        when(
            userDao.existsByUsername(eq(USERNAME))
        ).thenReturn(true);

        boolean exists = userService.existsByUsername(USERNAME);

        assertTrue(exists);
    }

    @Test
    public void testUserExistsByEmail(){
        when(
            userDao.existsByEmail(eq(EMAIL))
        ).thenReturn(true);

        boolean exists = userService.existsByEmail(EMAIL);

        assertTrue(exists);
    }

    @Test
    public void testFindUsersQuery(){
        when(
            userDao.search(eq(USERNAME), any(PageParams.class))
        ).thenReturn(USER_PAGE);

        Page<User> users = userService.findUsers(USERNAME, PAGE_1_DEFAULT);

        assertNotNull(users);
        assertEquals(USER_PAGE, users);
    }
    @Test
    public void testFindUsersEmptyQuery(){
        when(
            userDao.findAll(any(PageParams.class))
        ).thenReturn(USER_PAGE);

        Page<User> users = userService.findUsers("", PAGE_1_DEFAULT);

        assertNotNull(users);
        assertEquals(USER_PAGE, users);
    }
    @Test
    public void testFindUsersMissingQuery(){
        when(
            userDao.findAll(any(PageParams.class))
        ).thenReturn(USER_PAGE);

        Page<User> users = userService.findUsers(null, PAGE_1_DEFAULT);

        assertNotNull(users);
        assertEquals(USER_PAGE, users);
    }

    @Test
    public void testBlockUser(){
        when(
            userDao.findById(eq(USER_ID))
        ).thenReturn(Optional.of(USER));

        userService.blockUser(USER_ID);
    }
    @Test(expected = UserNotFoundException.class)
    public void testBlockUserNotFound(){
        when(
            userDao.findById(eq(USER_ID))
        ).thenReturn(Optional.empty());

        userService.blockUser(USER_ID);
    }

    @Test
    public void testUnblockUser(){
        when(
            userDao.findById(eq(USER_ID))
        ).thenReturn(Optional.of(USER));

        userService.unblockUser(USER_ID);
    }
    @Test(expected = UserNotFoundException.class)
    public void testUnblockUserNotFound(){
        when(
            userDao.findById(eq(USER_ID))
        ).thenReturn(Optional.empty());

        userService.unblockUser(USER_ID);
    }

    @Test
    public void testCheckTokenValidity(){
        when(
            tokenService.getByToken(eq(TOKEN_VALUE))
        ).thenReturn(Optional.of(TOKEN));

        userService.checkTokenValidity(TOKEN_VALUE);
    }
    @Test(expected = InvalidTokenException.class)
    public void testCheckTokenValidityInvalid(){
        when(
            tokenService.getByToken(eq(TOKEN_VALUE))
        ).thenReturn(Optional.empty());

        userService.checkTokenValidity(TOKEN_VALUE);
    }

    @Test
    public void testFindAverageRatingForCreatedEvents(){
        when(
            userDao.findAverageRatingForCreatedEvents(eq(USER_ID))
        ).thenReturn(Optional.of(RATING));

        Optional<Double> maybeRating = userService.findAverageRatingForCreatedEvents(USER_ID);

        assertNotNull(maybeRating);
        assertEquals(RATING, maybeRating.get(), 0.1);
    }

    @Test
    public void testFindAverageRatingForAttendedEvents(){
        when(
            userDao.findAverageRatingForAttendedEvents(eq(USER_ID))
        ).thenReturn(Optional.of(RATING));

        Optional<Double> maybeRating = userService.findAverageRatingForAttendedEvents(USER_ID);

        assertNotNull(maybeRating);
        assertEquals(RATING, maybeRating.get(), 0.1);
    }

    @Test
    public void testFindEventAttendees(){
        when(
            userDao.findAllAttendeesByEventId(eq(EVENT_ID), any(PageParams.class))
        ).thenReturn(USER_PAGE);

        Page<User> attendees = userService.findEventAttendees(EVENT_ID, PAGE_1_DEFAULT);

        assertNotNull(attendees);
        assertEquals(USER_PAGE, attendees);
    }

    @Test
    public void testResetPassword(){
        User newUser = new User(
            null, 
            null, 
            null, 
            null, 
            null, 
            null, 
            0, 
            "PASSWORD", 
            LOCALE, 
            true
        );
        when(
            tokenService.getByToken(eq(TOKEN_VALUE))
        ).thenReturn(Optional.of(new Token(newUser, TOKEN_VALUE, TOKEN_EXPIRATION)));
        when(
            passwordEncoder.encode(PASSWORD)
        ).thenReturn(PASSWORD);

        userService.resetPassword(TOKEN_VALUE, PASSWORD);

        assertEquals(PASSWORD, newUser.getPassword());
    }
    @Test(expected = InvalidTokenException.class)
    public void testResetPasswordMissingToken(){
        User newUser = new User(
            null, 
            null, 
            null, 
            null, 
            null, 
            null, 
            0, 
            "PASSWORD", 
            LOCALE, 
            true
        );
        when(
            tokenService.getByToken(eq(TOKEN_VALUE))
        ).thenReturn(Optional.empty());

        userService.resetPassword(TOKEN_VALUE, PASSWORD);

        assertEquals(PASSWORD, newUser.getPassword());
    }

    @Test
    public void testInitiatePasswordReset(){
        when(
            userDao.findByEmail(eq(EMAIL))
        ).thenReturn(Optional.of(USER));
        when(
            tokenService.userTokenControl(eq(USER))
        ).thenReturn(TOKEN);

        userService.initiatePasswordReset(EMAIL);
    }
    @Test(expected = UserValidatedException.class)
    public void testInitiatePasswordResetUserNotValidated(){
        when(
            userDao.findByEmail(eq(EMAIL))
        ).thenReturn(Optional.of(USER_NOT_VALIDATED));

        userService.initiatePasswordReset(EMAIL);
    }
    @Test(expected = RuntimeException.class)
    public void testInitiatePasswordResetUserNotFound(){
        when(
            userDao.findByEmail(eq(EMAIL))
        ).thenReturn(Optional.empty());

        userService.initiatePasswordReset(EMAIL);
    }
}