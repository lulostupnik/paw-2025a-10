package ar.edu.itba.paw.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

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
import ar.edu.itba.paw.models.UserRating;
import ar.edu.itba.paw.models.exceptions.InvalidReferenceException;
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
    private static final LocalDateTime TOKEN_EXPIRATION = LocalDateTime.now().plusDays(1);
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
            universityService.findById(eq(UNI_ID))
        ).thenReturn(Optional.of(UNIVERSITY));
        when(
            careerService.findCareerById(eq(CAREER_ID))
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
            tokenService.issueUserToken(USER)
        ).thenReturn(TOKEN_VALUE);

        User user = userService.createUser(
            EMAIL,
            USERNAME,
            FIRSTNAME,
            LASTNAME,
            UNI_ID,
            CAREER_ID,
            // IMAGE.getData(),
            List.of(INTEREST_ID),
            PASSWORD,
            LOCALE
        );

        assertNotNull(user);
        assertEquals(USER, user);
    }

    @Test
    public void testCreateUserDeduplicatesInterestIds(){
        when(
            universityService.findById(eq(UNI_ID))
        ).thenReturn(Optional.of(UNIVERSITY));
        when(
            careerService.findCareerById(eq(CAREER_ID))
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
            tokenService.issueUserToken(USER)
        ).thenReturn(TOKEN_VALUE);

        userService.createUser(
            EMAIL,
            USERNAME,
            FIRSTNAME,
            LASTNAME,
            UNI_ID,
            CAREER_ID,
            Arrays.asList(INTEREST_ID, INTEREST_ID, null, NEW_IMAGE_ID, NEW_IMAGE_ID),
            PASSWORD,
            LOCALE
        );

        verify(interestService).createUserInterests(eq(List.of(INTEREST_ID, NEW_IMAGE_ID)), eq(USER_ID));
    }

    @Test
    public void testCreateUserSendsValidationEmailAfterCommit(){
        when(
            universityService.findById(eq(UNI_ID))
        ).thenReturn(Optional.of(UNIVERSITY));
        when(
            careerService.findCareerById(eq(CAREER_ID))
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
            tokenService.issueUserToken(USER)
        ).thenReturn(TOKEN_VALUE);

        TransactionSynchronizationManager.initSynchronization();
        try {
            userService.createUser(
                EMAIL,
                USERNAME,
                FIRSTNAME,
                LASTNAME,
                UNI_ID,
                CAREER_ID,
                List.of(INTEREST_ID),
                PASSWORD,
                LOCALE
            );

            verify(emailService, never()).sendValidationEmail(any(), any());
            TransactionSynchronizationManager.getSynchronizations().forEach(TransactionSynchronization::afterCommit);
            verify(emailService).sendValidationEmail(any(), eq(TOKEN_VALUE));
        } finally {
            TransactionSynchronizationManager.clearSynchronization();
        }
    }

    @Test(expected = InvalidReferenceException.class)
    public void testCreateUserMissingCareer(){
        when(
            universityService.findById(eq(UNI_ID))
        ).thenReturn(Optional.of(UNIVERSITY));
        when(
            careerService.findCareerById(eq(CAREER_ID))
        ).thenReturn(Optional.empty());

        userService.createUser(
            EMAIL,
            USERNAME,
            FIRSTNAME,
            LASTNAME,
            UNI_ID,
            CAREER_ID,
            // IMAGE.getData(),
            List.of(INTEREST_ID),
            PASSWORD,
            LOCALE
        );
    }
    @Test(expected = InvalidReferenceException.class)
    public void testCreateUserMissingUniversity(){
        when(
            universityService.findById(eq(UNI_ID))
        ).thenReturn(Optional.empty());

        userService.createUser(
            EMAIL,
            USERNAME,
            FIRSTNAME,
            LASTNAME,
            UNI_ID,
            CAREER_ID,
            // IMAGE.getData(),
            List.of(INTEREST_ID),
            PASSWORD,
            LOCALE
        );
    }

    @Test
    public void testVerifyUser(){
        final User unverified = new User(USER_ID, EMAIL, USERNAME, FIRSTNAME, LASTNAME, UNIVERSITY, CAREER, IMAGE.getId(), LOCALE, false, false);
        when(
            userDao.findById(eq(USER_ID))
        ).thenReturn(Optional.of(unverified));

        userService.verifyUser(USER_ID);

        assertTrue(unverified.isValidated());
    }
    @Test()
    public void testVerifyUserAlreadyValidated(){
        when(
            userDao.findById(eq(USER_ID))
        ).thenReturn(Optional.of(USER));

        userService.verifyUser(USER_ID);

        assertTrue(USER.isValidated());
    }
    @Test(expected = UserNotFoundException.class)
    public void testVerifyUserNotFound(){
        when(
            userDao.findById(eq(USER_ID))
        ).thenReturn(Optional.empty());

        userService.verifyUser(USER_ID);
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

    @Test
    public void testBlockUserSendsEmailAfterCommit(){
        User newUser = new User(EMAIL, USERNAME, FIRSTNAME, LASTNAME, UNIVERSITY, CAREER, CAREER_ID, LOCALE, false);
        when(userDao.findById(eq(USER_ID))).thenReturn(Optional.of(newUser));

        TransactionSynchronizationManager.initSynchronization();
        try {
            userService.blockUser(USER_ID);

            verify(emailService, never()).sendUserBlockedNotification(any());
            TransactionSynchronizationManager.getSynchronizations().forEach(TransactionSynchronization::afterCommit);
            verify(emailService).sendUserBlockedNotification(any());
        } finally {
            TransactionSynchronizationManager.clearSynchronization();
        }
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

    @Test
    public void testUnblockUserSendsEmailAfterCommit(){
        User newUser = new User(EMAIL, USERNAME, FIRSTNAME, LASTNAME, UNIVERSITY, CAREER, CAREER_ID, LOCALE, false);
        newUser.setBlocked(true);
        when(userDao.findById(eq(USER_ID))).thenReturn(Optional.of(newUser));

        TransactionSynchronizationManager.initSynchronization();
        try {
            userService.unblockUser(USER_ID);

            verify(emailService, never()).sendUserUnblockedNotification(any());
            TransactionSynchronizationManager.getSynchronizations().forEach(TransactionSynchronization::afterCommit);
            verify(emailService).sendUserUnblockedNotification(any());
        } finally {
            TransactionSynchronizationManager.clearSynchronization();
        }
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
    public void testGetUserRating(){
        when(
            userDao.findById(eq(USER_ID))
        ).thenReturn(Optional.of(USER));
        when(
            userDao.findAverageRatingForCreatedEvents(eq(USER_ID))
        ).thenReturn(Optional.of(RATING));
        when(
            userDao.findAverageRatingForAttendedEvents(eq(USER_ID))
        ).thenReturn(Optional.empty());

        UserRating rating = userService.getUserRating(USER_ID);

        assertNotNull(rating);
        assertEquals(USER_ID, rating.getUserId());
        assertEquals(RATING, rating.getCreatedEventsRating(), 0.1);
        assertNull(rating.getAttendedEventsRating());
    }
    @Test(expected = UserNotFoundException.class)
    public void testGetUserRatingUserNotFound(){
        when(
            userDao.findById(eq(USER_ID))
        ).thenReturn(Optional.empty());

        userService.getUserRating(USER_ID);
    }


    @Test
    public void testInitiatePasswordResetUserNotValidated(){
        when(
            userDao.findByEmail(eq(EMAIL))
        ).thenReturn(Optional.of(USER_NOT_VALIDATED));

        userService.initiatePasswordReset(EMAIL);

        verify(emailService, never()).sendForgotPassEmail(any(), any());
    }
    @Test
    public void testInitiatePasswordResetUnknownEmail(){
        when(
            userDao.findByEmail(eq(EMAIL))
        ).thenReturn(Optional.empty());

        userService.initiatePasswordReset(EMAIL);

        verify(emailService, never()).sendForgotPassEmail(any(), any());
    }

    @Test
    public void testInitiatePasswordResetSendsEmailAfterCommit(){
        when(userDao.findByEmail(eq(EMAIL))).thenReturn(Optional.of(USER));
        when(tokenService.issueUserToken(USER)).thenReturn(TOKEN_VALUE);

        TransactionSynchronizationManager.initSynchronization();
        try {
            userService.initiatePasswordReset(EMAIL);

            verify(emailService, never()).sendForgotPassEmail(any(), any());
            TransactionSynchronizationManager.getSynchronizations().forEach(TransactionSynchronization::afterCommit);
            verify(emailService).sendForgotPassEmail(any(), eq(TOKEN_VALUE));
        } finally {
            TransactionSynchronizationManager.clearSynchronization();
        }
    }

    @Test
    public void testResendVerificationEmail(){
        when(
            userDao.findByEmail(eq(EMAIL))
        ).thenReturn(Optional.of(USER_NOT_VALIDATED));
        when(
            tokenService.issueUserToken(USER_NOT_VALIDATED)
        ).thenReturn(TOKEN_VALUE);

        userService.resendVerificationEmail(EMAIL);

        verify(emailService).sendValidationEmail(any(), eq(TOKEN_VALUE));
    }

    @Test
    public void testResendVerificationEmailSendsEmailAfterCommit(){
        when(
            userDao.findByEmail(eq(EMAIL))
        ).thenReturn(Optional.of(USER_NOT_VALIDATED));
        when(
            tokenService.issueUserToken(USER_NOT_VALIDATED)
        ).thenReturn(TOKEN_VALUE);

        TransactionSynchronizationManager.initSynchronization();
        try {
            userService.resendVerificationEmail(EMAIL);

            verify(emailService, never()).sendValidationEmail(any(), eq(TOKEN_VALUE));
            TransactionSynchronizationManager.getSynchronizations().forEach(TransactionSynchronization::afterCommit);
            verify(emailService).sendValidationEmail(any(), eq(TOKEN_VALUE));
        } finally {
            TransactionSynchronizationManager.clearSynchronization();
        }
    }

    @Test(expected = UserValidatedException.class)
    public void testResendVerificationEmailAlreadyValidated(){
        when(
            userDao.findByEmail(eq(EMAIL))
        ).thenReturn(Optional.of(USER));

        userService.resendVerificationEmail(EMAIL);
    }

    @Test(expected = UserNotFoundException.class)
    public void testResendVerificationEmailUserNotFound(){
        when(
            userDao.findByEmail(eq(EMAIL))
        ).thenReturn(Optional.empty());

        userService.resendVerificationEmail(EMAIL);
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
            universityService.findById(eq(UNI_ID))
        ).thenReturn(Optional.of(UNIVERSITY));
        when(
            careerService.findCareerById(eq(CAREER_ID))
        ).thenReturn(Optional.of(CAREER));

        userService.patchUser(
            USER_ID,
            USERNAME,
            FIRSTNAME,
            LASTNAME,
            UNI_ID,
            CAREER_ID,
            null,
            null
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
            careerService.findCareerById(eq(CAREER_ID))
        ).thenReturn(Optional.empty());

        userService.patchUser(
            USER_ID,
            null,
            null,
            null,
            null,
            CAREER_ID,
            null,
            null
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
            universityService.findById(eq(UNI_ID))
        ).thenReturn(Optional.empty());

        userService.patchUser(
            USER_ID,
            null,
            null,
            null,
            UNI_ID,
            null,
            null,
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

        userService.patchUser(USER_ID, USERNAME, FIRSTNAME, LASTNAME, UNI_ID, CAREER_ID, null, null);
    }

    @Test
    public void testPatchUserSetsPassword(){
        User u = new User(USER_ID, EMAIL, USERNAME, FIRSTNAME, LASTNAME, UNIVERSITY, CAREER, IMAGE_ID, LOCALE, false, true);
        when(userDao.findById(eq(USER_ID))).thenReturn(Optional.of(u));
        when(passwordEncoder.encode(eq(PASSWORD))).thenReturn("ENCODED");

        userService.patchUser(USER_ID, null, null, null, null, null, PASSWORD, null);

        assertEquals("ENCODED", u.getPassword());
    }

    @Test
    public void testPatchUserSetsBlocked(){
        User u = new User(USER_ID, EMAIL, USERNAME, FIRSTNAME, LASTNAME, UNIVERSITY, CAREER, IMAGE_ID, LOCALE, false, true);
        when(userDao.findById(eq(USER_ID))).thenReturn(Optional.of(u));

        userService.patchUser(USER_ID, null, null, null, null, null, null, true);

        assertTrue(u.isBlocked());
        verify(emailService).sendUserBlockedNotification(any());
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
        verify(imageService).deleteImage(IMAGE_ID);
    }
    @Test
    public void testUpdateProfilePictureNoPreviousPicture(){
        User u = new User(
            USER_ID,
            EMAIL,
            GARBAGE,
            GARBAGE,
            GARBAGE,
            null,
            null,
            null,
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
        verify(imageService, never()).deleteImage(anyLong());
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
