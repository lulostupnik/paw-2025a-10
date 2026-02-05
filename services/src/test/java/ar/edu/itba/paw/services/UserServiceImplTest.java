package ar.edu.itba.paw.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

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
import ar.edu.itba.paw.models.Career;
import ar.edu.itba.paw.models.Image;
import ar.edu.itba.paw.models.Interest;
import ar.edu.itba.paw.models.Page;
import ar.edu.itba.paw.models.PageParams;
import ar.edu.itba.paw.models.Token;
import ar.edu.itba.paw.models.University;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.exceptions.InvalidReferenceException;
import ar.edu.itba.paw.models.exceptions.InvalidTokenException;
import ar.edu.itba.paw.models.exceptions.UserNotFoundException;
import ar.edu.itba.paw.models.exceptions.UserValidatedException;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceImplTest {

    private static final String EMAIL = "email";
    private static final String USERNAME = "username";
    private static final String FIRSTNAME = "name";
    private static final String LASTNAME = "name";
    private static final String GARBAGE = "asdfasdfasujh";
    private static final String UNI_NAME = "uni";
    private static final String CAREER_NAME = "career";
    private static final long USER_ID = 0;
    private static final long UNI_ID = 2;
    private static final long CAREER_ID = 3;
    private static final long IMAGE_ID = 4;
    private static final long INTEREST_ID = 5;
    private static final long NEW_IMAGE_ID = 6;

    private static final University UNIVERSITY = new University(UNI_ID, UNI_NAME, null, null);
    private static final Career CAREER = new Career(CAREER_ID, CAREER_NAME);
    private static final Image IMAGE = new Image(IMAGE_ID, new byte[0]);
    private static final String PASSWORD = "null";
    private static final Locale LOCALE = Locale.of("en");

    private static final Interest INTEREST = new Interest(INTEREST_ID, "name");
    private static final User USER = new User(USER_ID, EMAIL, USERNAME, FIRSTNAME, LASTNAME, UNIVERSITY, CAREER, IMAGE.getId(), LOCALE, false, true);
    private static final User USER_NOT_VALIDATED = new User(USER_ID, EMAIL, USERNAME, FIRSTNAME, LASTNAME, UNIVERSITY, CAREER, IMAGE.getId(), LOCALE, false, false);
    private static final User USER_NO_PICTURE = new User(USER_ID, EMAIL, USERNAME, FIRSTNAME, LASTNAME, UNIVERSITY, CAREER, null, LOCALE, false, false);
    private static final PageParams PAGE_1_DEFAULT = new PageParams(1, 2);
    private static final String TOKEN_VALUE = "null";
    private static final LocalDateTime TOKEN_EXPIRATION = LocalDateTime.now();
    private static final Token TOKEN = new Token(USER_NOT_VALIDATED, TOKEN_VALUE, TOKEN_EXPIRATION);
    private static final List<User> USERS = List.of(USER);
    private static final Page<User> USER_PAGE = new Page<>(USERS, 1, 1, 1);
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
                eq(null), 
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
            // IMAGE.getData(),
            List.of(INTEREST.getName()), 
            PASSWORD, 
            LOCALE
        );

        assertNotNull(user);
        assertEquals(USER, user);
    }
    @Test(expected = InvalidReferenceException.class)
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
            // IMAGE.getData(),
            List.of(INTEREST.getName()), 
            PASSWORD, 
            LOCALE
        );
    }
    @Test(expected = InvalidReferenceException.class)
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
            // IMAGE.getData(),
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
    @Test()
    public void testVerifyUserAlreadyValidated(){
        when(
            tokenService.getByToken(eq(TOKEN_VALUE))
        ).thenReturn(Optional.of(new Token(USER, TOKEN_VALUE, TOKEN_EXPIRATION)));

        User user = userService.verifyUser(TOKEN_VALUE);

        assertNotNull(user);
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
            userDao.findUsers(
                eq(USERNAME), 
                any(PageParams.class),
                eq(null),
                eq(UNI_ID),
                eq(CAREER_ID),
                eq(INTEREST_ID),
                eq(false)
            )
        ).thenReturn(USER_PAGE);

        Page<User> users = userService.findUsers(USERNAME, PAGE_1_DEFAULT, null, UNI_ID, CAREER_ID, INTEREST_ID, false);

        assertNotNull(users);
        assertEquals(USER_PAGE, users);
    }

    @Test
    public void testBlockUser(){
        User newUser = new User(EMAIL, USERNAME, FIRSTNAME, LASTNAME, UNIVERSITY, CAREER, CAREER_ID, LOCALE, false);
        when(
            userDao.findById(eq(USER_ID))
        ).thenReturn(Optional.of(newUser));

        userService.setBlockedStatus(USER_ID, true);

        assertTrue(newUser.isBlocked());
    }
    @Test(expected = UserNotFoundException.class)
    public void testBlockUserNotFound(){
        when(
            userDao.findById(eq(USER_ID))
        ).thenReturn(Optional.empty());

        userService.setBlockedStatus(USER_ID, true);
    }

    @Test
    public void testUnblockUser(){
        User newUser = new User(EMAIL, USERNAME, FIRSTNAME, LASTNAME, UNIVERSITY, CAREER, CAREER_ID, LOCALE, false);
        newUser.setBlocked(true);
        when(
            userDao.findById(eq(USER_ID))
        ).thenReturn(Optional.of(newUser));

        userService.setBlockedStatus(USER_ID, false);

        assertFalse(newUser.isBlocked());
    }
    @Test(expected = UserNotFoundException.class)
    public void testUnblockUserNotFound(){
        when(
            userDao.findById(eq(USER_ID))
        ).thenReturn(Optional.empty());

        userService.setBlockedStatus(USER_ID, false);
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
    public void testResetPassword(){
        User newUser = new User(
            null, 
            null, 
            null, 
            null, 
            null, 
            null, 
            0L,
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
            0L,
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

    @Test(expected = UserValidatedException.class)
    public void testInitiatePasswordResetUserNotValidated(){
        when(
            userDao.findByEmail(eq(EMAIL))
        ).thenReturn(Optional.of(USER_NOT_VALIDATED));

        userService.initiatePasswordReset(EMAIL);
    }
    @Test(expected = UserNotFoundException.class)
    public void testInitiatePasswordResetUserNotFound(){
        when(
            userDao.findByEmail(eq(EMAIL))
        ).thenReturn(Optional.empty());

        userService.initiatePasswordReset(EMAIL);
    }

    @Test
    public void testUpdateUser(){
        User u = new User(
            USER_ID, 
            EMAIL, 
            GARBAGE, 
            GARBAGE, 
            GARBAGE, 
            null, 
            null, 
            IMAGE_ID, 
            LOCALE, 
            false, 
            false
        );
        when(
            userDao.findById(eq(USER_ID))
        ).thenReturn(Optional.of(u));
        when(
            universityService.findByName(eq(UNI_NAME))
        ).thenReturn(Optional.of(UNIVERSITY));
        when(
            careerService.findCareerByName(eq(CAREER_NAME))
        ).thenReturn(Optional.of(CAREER));

        userService.updateUser(
            USER_ID, 
            USERNAME, 
            FIRSTNAME, 
            LASTNAME, 
            UNI_NAME, 
            CAREER_NAME
        );

        assertEquals(USERNAME, u.getUsername());
        assertEquals(FIRSTNAME, u.getFirstname());
        assertEquals(LASTNAME, u.getLastname());
        assertEquals(UNI_NAME, u.getUniversity().getName());
        assertEquals(CAREER_NAME, u.getCareer().getName());
    }
    @Test(expected = InvalidReferenceException.class)
    public void testUpdateUserMissingCareer(){
        User u = new User(
            USER_ID, 
            EMAIL, 
            GARBAGE, 
            GARBAGE, 
            GARBAGE, 
            null, 
            null, 
            IMAGE_ID, 
            LOCALE, 
            false, 
            false
        );
        when(
            userDao.findById(eq(USER_ID))
        ).thenReturn(Optional.of(u));
        when(
            universityService.findByName(eq(UNI_NAME))
        ).thenReturn(Optional.of(UNIVERSITY));
        when(
            careerService.findCareerByName(eq(CAREER_NAME))
        ).thenReturn(Optional.empty());

        userService.updateUser(
            USER_ID, 
            USERNAME, 
            FIRSTNAME, 
            LASTNAME, 
            UNI_NAME, 
            CAREER_NAME
        );
    }
    @Test(expected = InvalidReferenceException.class)
    public void testUpdateUserMissingUni(){
        User u = new User(
            USER_ID, 
            EMAIL, 
            GARBAGE, 
            GARBAGE, 
            GARBAGE, 
            null, 
            null, 
            IMAGE_ID, 
            LOCALE, 
            false, 
            false
        );
        when(
            userDao.findById(eq(USER_ID))
        ).thenReturn(Optional.of(u));
        when(
            universityService.findByName(eq(UNI_NAME))
        ).thenReturn(Optional.empty());

        userService.updateUser(
            USER_ID, 
            USERNAME,
            FIRSTNAME, 
            LASTNAME, 
            UNI_NAME, 
            CAREER_NAME
        );
    }
    @Test(expected = UserNotFoundException.class)
    public void testUpdateUserMissingUser(){
        when(
            userDao.findById(eq(USER_ID))
        ).thenReturn(Optional.empty());

        userService.updateUser(
            USER_ID, 
            USERNAME, 
            FIRSTNAME, 
            LASTNAME, 
            UNI_NAME, 
            CAREER_NAME
        );
    }

    @Test
    public void testPatchUser(){
        User u = new User(
            USER_ID, 
            EMAIL, 
            GARBAGE, 
            GARBAGE, 
            GARBAGE, 
            null, 
            null, 
            IMAGE_ID, 
            LOCALE, 
            false, 
            false
        );
        when(
            userDao.findById(eq(USER_ID))
        ).thenReturn(Optional.of(u));
        when(
            universityService.findByName(eq(UNI_NAME))
        ).thenReturn(Optional.of(UNIVERSITY));
        when(
            careerService.findCareerByName(eq(CAREER_NAME))
        ).thenReturn(Optional.of(CAREER));

        userService.patchUser(
            USER_ID, 
            USERNAME, 
            FIRSTNAME, 
            LASTNAME, 
            UNI_NAME, 
            CAREER_NAME
        );

        assertEquals(USERNAME, u.getUsername());
        assertEquals(FIRSTNAME, u.getFirstname());
        assertEquals(LASTNAME, u.getLastname());
        assertEquals(UNI_NAME, u.getUniversity().getName());
        assertEquals(CAREER_NAME, u.getCareer().getName());
    }
    @Test(expected = InvalidReferenceException.class)
    public void testPatchUserMissingCareer(){
        User u = new User(
            USER_ID, 
            EMAIL, 
            GARBAGE, 
            GARBAGE, 
            GARBAGE, 
            null, 
            null, 
            IMAGE_ID, 
            LOCALE, 
            false, 
            false
        );
        when(
            userDao.findById(eq(USER_ID))
        ).thenReturn(Optional.of(u));
        when(
            careerService.findCareerByName(eq(CAREER_NAME))
        ).thenReturn(Optional.empty());

        userService.patchUser(
            USER_ID, 
            null, 
            null, 
            null, 
            null, 
            CAREER_NAME
        );
    }
    @Test(expected = InvalidReferenceException.class)
    public void testPatchUserMissingUni(){
        User u = new User(
            USER_ID, 
            EMAIL, 
            GARBAGE, 
            GARBAGE, 
            GARBAGE, 
            null, 
            null, 
            IMAGE_ID, 
            LOCALE, 
            false, 
            false
        );
        when(
            userDao.findById(eq(USER_ID))
        ).thenReturn(Optional.of(u));
        when(
            universityService.findByName(eq(UNI_NAME))
        ).thenReturn(Optional.empty());

        userService.patchUser(
            USER_ID, 
            null, 
            null, 
            null, 
            UNI_NAME, 
            null
        );
    }
    @Test
    public void testPatchUserNoUpdates(){
        User u = new User(
            USER_ID, 
            EMAIL, 
            USERNAME, 
            FIRSTNAME, 
            LASTNAME, 
            UNIVERSITY, 
            CAREER, 
            IMAGE_ID, 
            LOCALE, 
            false, 
            false
        );
        when(
            userDao.findById(eq(USER_ID))
        ).thenReturn(Optional.of(u));

        userService.patchUser(
            USER_ID, 
            null, 
            null, 
            null, 
            null, 
            null
        );

        assertEquals(USERNAME, u.getUsername());
        assertEquals(FIRSTNAME, u.getFirstname());
        assertEquals(LASTNAME, u.getLastname());
        assertEquals(UNI_NAME, u.getUniversity().getName());
        assertEquals(CAREER_NAME, u.getCareer().getName());
    }
    @Test(expected = UserNotFoundException.class)
    public void testPatchUserMissingUser(){
        when(userDao.findById(eq(USER_ID))).thenReturn(Optional.empty());

        userService.patchUser(USER_ID, USERNAME, FIRSTNAME, LASTNAME, EMAIL, CAREER_NAME);
    }

    @Test
    public void testUpdateProfilePicture(){
        User u = new User(
            USER_ID, 
            EMAIL, 
            GARBAGE, 
            GARBAGE, 
            GARBAGE, 
            null, 
            null, 
            IMAGE_ID, 
            LOCALE, 
            false, 
            false
        );
        when(
            userDao.findById(eq(USER_ID))
        ).thenReturn(Optional.of(u));
        when(
            imageService.createImage(any())
        ).thenReturn(NEW_IMAGE_ID);

        userService.updateProfilePicture(USER_ID, new byte[0]);

        assertEquals(NEW_IMAGE_ID, u.getProfilePictureId().longValue());
    }
    @Test(expected = UserNotFoundException.class)
    public void testUpdateProfilePictureUserNotFound(){
        when(
            userDao.findById(eq(USER_ID))
        ).thenReturn(Optional.empty());

        userService.updateProfilePicture(USER_ID, new byte[0]);
    }   

    @Test
    public void testGetProfilePicture(){
        when(
            userDao.findById(eq(USER_ID))
        ).thenReturn(Optional.of(USER));
        when(
            imageService.findImage(eq(IMAGE_ID))
        ).thenReturn(Optional.of(IMAGE));
        
        Optional<Image> maybeImage = userService.getProfilePicture(USER_ID);

        assertNotNull(maybeImage);
        assertTrue(maybeImage.isPresent());
        assertEquals(IMAGE_ID, maybeImage.get().getId().longValue());
    }
    @Test
    public void testGetProfilePictureMissingPicture(){
        when(
            userDao.findById(eq(USER_ID))
        ).thenReturn(Optional.of(USER));
        when(
            imageService.findImage(eq(IMAGE_ID))
        ).thenReturn(Optional.empty());
        
        Optional<Image> maybeImage = userService.getProfilePicture(USER_ID);

        assertNotNull(maybeImage);
        assertTrue(maybeImage.isEmpty());
    }
    @Test
    public void testGetProfilePictureNoPicture(){
        when(
            userDao.findById(eq(USER_ID))
        ).thenReturn(Optional.of(USER_NO_PICTURE));
        
        Optional<Image> maybeImage = userService.getProfilePicture(USER_ID);

        assertNotNull(maybeImage);
        assertTrue(maybeImage.isEmpty());
    }
    @Test(expected=UserNotFoundException.class)
    public void testGetProfilePictureMissingUser(){
        when(
            userDao.findById(eq(USER_ID))
        ).thenReturn(Optional.empty());

        userService.getProfilePicture(USER_ID);
    }
}