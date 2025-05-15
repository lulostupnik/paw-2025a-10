package ar.edu.itba.paw.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.exceptions.ExpiredTokenException;
import ar.edu.itba.paw.models.exceptions.InvalidTokenException;
import ar.edu.itba.paw.models.exceptions.UserValidatedException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.models.UserAuthInfo;
import ar.edu.itba.paw.interfaces.services.CareerService;
import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.interfaces.services.ImageService;
import ar.edu.itba.paw.interfaces.services.InterestService;
import ar.edu.itba.paw.interfaces.services.UniversityService;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceImplTest {

    private static final String EMAIL = "email";
    private static final String USERNAME = "username";
    private static final String FIRSTNAME = "name";
    private static final String LASTNAME = "name";
    private static final University UNIVERSITY = new University((long)0, "cool", null, null);
    private static final Career CAREER = new Career((long)0, null);
    private static final Image IMAGE = new Image(0, new byte[0]);
    private static final String PASSWORD = "null";
    private static final String ROLE = "admin";
    private static final Locale LOCALE = Locale.of("en");
    private static final long USER_ID = 0;
    private static final Interest INTEREST = new Interest((long)0, "name");
    private static final User USER = new User(USER_ID, EMAIL, USERNAME, FIRSTNAME, LASTNAME, UNIVERSITY, CAREER, IMAGE.getId(), LOCALE, false, List.of());
    private static final PageParams PAGE_1_DEFAULT = new PageParams(1, 2);
    private static final String TOKEN = "null";
    private static final UserAuthInfo USER_AUTH_INFO= new UserAuthInfo(EMAIL, PASSWORD, ROLE, false, true);

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

    @Test
    public void testCreateUser(){
        Mockito.when(
            universityService.findByName(Mockito.eq(UNIVERSITY.getName()))
        ).thenReturn(Optional.of(UNIVERSITY));
        Mockito.when(
            careerService.findCareerByName(Mockito.eq(CAREER.getName()))
        ).thenReturn(Optional.of(CAREER));
        Mockito.when(
            imageService.createImage(Mockito.eq(IMAGE.getData()))
        ).thenReturn(IMAGE.getId());
        Mockito.when(
            passwordEncoder.encode(Mockito.eq(PASSWORD))
        ).thenReturn(PASSWORD);
        Mockito.when(
            userDao.create(Mockito.eq(EMAIL), Mockito.eq(USERNAME), Mockito.eq(FIRSTNAME), Mockito.eq(LASTNAME), Mockito.eq(UNIVERSITY), Mockito.eq(CAREER), Mockito.eq(IMAGE.getId()), Mockito.eq(PASSWORD), Mockito.eq(LOCALE), Mockito.anyString(), Mockito.any(LocalDate.class))
        ).thenReturn(USER);

        User user = userService.createUser(EMAIL, USERNAME, FIRSTNAME, LASTNAME, UNIVERSITY.getName(), CAREER.getName(), IMAGE.getData(), List.of(INTEREST.getName()), PASSWORD, LOCALE);

        assertNotNull(user);
        assertEquals(USER, user);
    }
    @Test(expected = RuntimeException.class)
    public void testCreateUserMissingCareer(){
        Mockito.when(
            universityService.findByName(Mockito.eq(UNIVERSITY.getName()))
        ).thenReturn(Optional.of(UNIVERSITY));
        Mockito.when(
            careerService.findCareerByName(Mockito.eq(CAREER.getName()))
        ).thenReturn(Optional.empty());
        
        userService.createUser(EMAIL, USERNAME, FIRSTNAME, LASTNAME, UNIVERSITY.getName(), CAREER.getName(), IMAGE.getData(), List.of(INTEREST.getName()) , PASSWORD, LOCALE);
    }
    @Test(expected = RuntimeException.class)
    public void testCreateUserMissingUniversity(){
        Mockito.when(
            universityService.findByName(Mockito.eq(UNIVERSITY.getName()))
        ).thenReturn(Optional.empty());        

        userService.createUser(EMAIL, USERNAME, FIRSTNAME, LASTNAME, UNIVERSITY.getName(), CAREER.getName(), IMAGE.getData(), List.of(INTEREST.getName()), PASSWORD, LOCALE);
    }


    @Test
    public void testVerifyEmailToken(){
        Mockito.when(
            userDao.existsByTokenExpired(Mockito.eq(TOKEN))
        ).thenReturn(false);
        Mockito.when(
            userDao.findValidatedByTokenNotExpired(Mockito.eq(TOKEN))
        ).thenReturn(Optional.of(false));
        Mockito.when(
            userDao.updateValidationAndFindAuthInfoByToken(Mockito.eq(TOKEN))
        ).thenReturn(Optional.of(USER_AUTH_INFO));

        userService.verifyEmailToken(TOKEN);
    }
    @Test(expected = RuntimeException.class)
    public void testVerifyEmailTokenInvalidToken(){
        Mockito.when(
            userDao.existsByTokenExpired(Mockito.eq(TOKEN))
        ).thenReturn(false);
        Mockito.when(
            userDao.findValidatedByTokenNotExpired(Mockito.eq(TOKEN))
        ).thenReturn(Optional.of(false));
        Mockito.when(
            userDao.updateValidationAndFindAuthInfoByToken(Mockito.eq(TOKEN))
        ).thenReturn(Optional.empty());

        userService.verifyEmailToken(TOKEN);
    }
    @Test(expected = InvalidTokenException.class)
    public void testVerifyEmailTokenAlreadyValidated(){
        Mockito.when(
            userDao.existsByTokenExpired(Mockito.eq(TOKEN))
        ).thenReturn(false);
        Mockito.when(
            userDao.findValidatedByTokenNotExpired(Mockito.eq(TOKEN))
        ).thenReturn(Optional.of(true));

        userService.verifyEmailToken(TOKEN);
    }
    @Test(expected = IllegalStateException.class)
    public void testVerifyEmailTokenExpiredToken(){
        Mockito.when(
            userDao.existsByTokenExpired(Mockito.eq(TOKEN))
        ).thenReturn(true);

        userService.verifyEmailToken(TOKEN);
    }


    @Test(expected = ExpiredTokenException.class)
    public void testCheckPasswordTokenValidityExpired(){
        Mockito.when(
            userDao.existsByTokenExpired(Mockito.eq(TOKEN))
        ).thenReturn(true);
        Mockito.when(
            userDao.findByToken(Mockito.eq(TOKEN))
        ).thenReturn(Optional.of(USER));

        userService.checkPasswordTokenValidity(TOKEN);
    }
    @Test(expected =  InvalidTokenException.class)
    public void testCheckPasswordTokenValidityResetFailed(){
        Mockito.when(
            userDao.existsByTokenExpired(Mockito.eq(TOKEN))
        ).thenReturn(false);
        Mockito.when(
            userDao.existsByTokenNotExpired(Mockito.eq(TOKEN))
        ).thenReturn(false);

        userService.checkPasswordTokenValidity(TOKEN);
    }


    @Test
    public void testBlockUser(){
        Mockito.when(
            userDao.findById(Mockito.eq(USER_ID))
        ).thenReturn(Optional.of(USER));

        userService.blockUser(USER_ID);
    }
    @Test(expected = IllegalStateException.class)
    public void testBlockUserNotFound(){
        Mockito.when(
            userDao.findById(Mockito.eq(USER_ID))
        ).thenReturn(Optional.empty());

        userService.blockUser(USER_ID);
    }

    @Test
    public void testUnblockUser(){
        Mockito.when(
            userDao.findById(Mockito.eq(USER_ID))
        ).thenReturn(Optional.of(USER));

        userService.unblockUser(USER_ID);
    }
    @Test(expected = IllegalStateException.class)
    public void testUnblockUserNotFound(){
        Mockito.when(
            userDao.findById(Mockito.eq(USER_ID))
        ).thenReturn(Optional.empty());

        userService.unblockUser(USER_ID);
    }

    @Test
    public void testRefreshToken(){
        Mockito.when(
            userDao.findByToken(Mockito.eq(TOKEN))
        ).thenReturn(Optional.of(USER));

        userService.refreshToken(TOKEN);
    }
    @Test(expected = RuntimeException.class)
    public void testRefreshTokenUserNotFound(){
        Mockito.when(
            userDao.findByToken(Mockito.eq(TOKEN))
        ).thenReturn(Optional.empty());

        userService.refreshToken(TOKEN);
    }
    @Test
    public void testRefreshPasswordToken(){
        Mockito.when(
            userDao.findByToken(Mockito.eq(TOKEN))
        ).thenReturn(Optional.of(USER));

        userService.refreshPasswordToken(TOKEN);
    }
    @Test(expected = RuntimeException.class)
    public void testRefreshTokenPassUserNotFound(){
        Mockito.when(
            userDao.findByToken(Mockito.eq(TOKEN))
        ).thenReturn(Optional.empty());

        userService.refreshPasswordToken(TOKEN);
    }

    @Test
    public void testInitiatePasswordReset(){
        Mockito.when(
            userDao.findByEmail(Mockito.eq(EMAIL))
        ).thenReturn(Optional.of(USER));
        Mockito.when(
            userDao.findValidationStatusByEmail(Mockito.eq(EMAIL))
        ).thenReturn(true);

        userService.initiatePasswordReset(EMAIL);
    }
    @Test(expected = UserValidatedException.class)
    public void testInitiatePasswordResetUserNotValid(){
        Mockito.when(
            userDao.findByEmail(Mockito.eq(EMAIL))
        ).thenReturn(Optional.of(USER));
        Mockito.when(
            userDao.findValidationStatusByEmail(Mockito.eq(EMAIL))
        ).thenReturn(false);

        userService.initiatePasswordReset(EMAIL);
    }
    @Test(expected = RuntimeException.class)
    public void testInitiatePasswordResetUserNotFound(){
        Mockito.when(
            userDao.findByEmail(Mockito.eq(EMAIL))
        ).thenReturn(Optional.empty());

        userService.initiatePasswordReset(EMAIL);
    }
}