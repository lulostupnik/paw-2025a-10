package ar.edu.itba.paw.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import ar.edu.itba.paw.interfaces.persistence.UserDao;
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
    private static final University UNIVERSITY = new University(0, "cool", null, null);
    private static final Career CAREER = new Career((long)0, null);
    private static final Image IMAGE = new Image((long)0, new byte[0]);
    private static final String PASSWORD = "null";
    private static final String ROLE = "admin";
    private static final Locale LOCALE = Locale.of("en");
    private static final long USER_ID = 0;
    private static final boolean BLOCKED = false;
    private static final Interest INTEREST = new Interest((long)0, "name");
    private static final User USER = new User(USER_ID, EMAIL, USERNAME, FIRSTNAME, LASTNAME, UNIVERSITY, CAREER, IMAGE.getId(), LOCALE, false);
    private static final PageParams PAGE_1_DEFAULT = new PageParams(1, 2);

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
            careerService.findByName(Mockito.eq(CAREER.getName()))
        ).thenReturn(Optional.of(CAREER));
        Mockito.when(
            imageService.storeImage(Mockito.eq(IMAGE.getData()))
        ).thenReturn(IMAGE.getId());
        Mockito.when(
            passwordEncoder.encode(Mockito.eq(PASSWORD))
        ).thenReturn(PASSWORD);
        Mockito.when(
            userDao.create(Mockito.eq(EMAIL), Mockito.eq(USERNAME), Mockito.eq(FIRSTNAME), Mockito.eq(LASTNAME), Mockito.eq(UNIVERSITY), Mockito.eq(CAREER), Mockito.eq(IMAGE.getId()), Mockito.eq(PASSWORD), Mockito.eq(LOCALE))
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
            careerService.findByName(Mockito.eq(CAREER.getName()))
        ).thenReturn(Optional.empty());
        
        userService.createUser(EMAIL, USERNAME, FIRSTNAME, LASTNAME, UNIVERSITY.getName(), CAREER.getName(), IMAGE.getData(), List.of(INTEREST.getName()) , PASSWORD, LOCALE);
    }
    @Test(expected = RuntimeException.class)
    public void testCreateUserMissingUniversity(){
        Mockito.when(
            universityService.findByName(Mockito.eq(UNIVERSITY.getName()))
        ).thenReturn(Optional.empty());        

        User user = userService.createUser(EMAIL, USERNAME, FIRSTNAME, LASTNAME, UNIVERSITY.getName(), CAREER.getName(), IMAGE.getData(), List.of(INTEREST.getName()), PASSWORD, LOCALE);

        assertNotNull(user);
        assertEquals(USER, user);
    }

    @Test
    public void testFindByEmail(){
        Mockito.when(
            userDao.findByEmail(Mockito.eq(EMAIL))
        ).thenReturn(Optional.of(USER));

        Optional<User> maybeUser = userService.findByEmail(EMAIL);

        assertNotNull(maybeUser);
        assertTrue(maybeUser.isPresent());
        assertEquals(USER, maybeUser.get());
    }
    @Test
    public void testFindByEmailMissing(){
        Mockito.when(
            userDao.findByEmail(Mockito.eq(EMAIL))
        ).thenReturn(Optional.empty());

        Optional<User> maybeUser = userService.findByEmail(EMAIL);

        assertNotNull(maybeUser);
        assertFalse(maybeUser.isPresent());
    }

    @Test
    public void testFindByEmailWithPass(){
        Mockito.when(
            userDao.findByEmailWithPass(Mockito.eq(EMAIL))
        ).thenReturn(Optional.of(new UserPassword(EMAIL, PASSWORD, ROLE, BLOCKED)));

        Optional<UserPassword> maybeUser = userService.findByEmailWithPass(EMAIL);

        assertNotNull(maybeUser);
        assertTrue(maybeUser.isPresent());
        assertEquals(PASSWORD, maybeUser.get().getPassword());
    }
    @Test
    public void testFindByEmailWithPassMissing(){
        Mockito.when(
            userDao.findByEmailWithPass(Mockito.eq(EMAIL))
        ).thenReturn(Optional.empty());

        Optional<UserPassword> maybeUser = userService.findByEmailWithPass(EMAIL);

        assertNotNull(maybeUser);
        assertFalse(maybeUser.isPresent());
    }

    @Test
    public void testFindById(){
        Mockito.when(
            userDao.findById(Mockito.eq(USER_ID))
        ).thenReturn(Optional.of(USER));

        Optional<User> maybeUser = userService.findById(USER_ID);

        assertNotNull(maybeUser);
        assertTrue(maybeUser.isPresent());
        assertEquals(USER, maybeUser.get());
    }
    @Test
    public void testFindByIdMissing(){
        Mockito.when(
            userDao.findById(Mockito.eq(USER_ID))
        ).thenReturn(Optional.empty());

        Optional<User> maybeUser = userService.findById(USER_ID);

        assertNotNull(maybeUser);
        assertFalse(maybeUser.isPresent());
    }

    @Test
    public void testFindByUsername(){
        Mockito.when(
            userDao.findByUsername(Mockito.eq(USERNAME))
        ).thenReturn(Optional.of(USER));

        Optional<User> maybeUser = userService.findByUsername(USERNAME);

        assertNotNull(maybeUser);
        assertTrue(maybeUser.isPresent());
        assertEquals(USER, maybeUser.get());
    }
    @Test
    public void testFindByUsernameMissing(){
        Mockito.when(
            userDao.findByUsername(Mockito.eq(USERNAME))
        ).thenReturn(Optional.empty());

        Optional<User> maybeUser = userService.findByUsername(USERNAME);

        assertNotNull(maybeUser);
        assertFalse(maybeUser.isPresent());
    }

    @Test
    public void testExistsByUsername(){
        Mockito.when(
            userDao.existsByUsername(Mockito.eq(EMAIL))
        ).thenReturn(true);

        boolean exists = userService.existsByUsername(EMAIL);

        assertTrue(exists);
    }
    @Test
    public void testExistsByUsernameMissing(){
        Mockito.when(
            userDao.existsByUsername(Mockito.eq(USERNAME))
        ).thenReturn(false);

        boolean exists = userService.existsByUsername(USERNAME);

        assertFalse(exists);
    }

    @Test
    public void testExistsByEmail(){
        Mockito.when(
            userDao.existsByEmail(Mockito.eq(EMAIL))
        ).thenReturn(true);

        boolean exists = userService.existsByEmail(EMAIL);

        assertTrue(exists);
    }
    @Test
    public void testExistsByEmailMissing(){
        Mockito.when(
            userDao.existsByEmail(Mockito.eq(EMAIL))
        ).thenReturn(false);

        boolean exists = userService.existsByEmail(EMAIL);

        assertFalse(exists);
    }

    @Test
    public void testUpdateProfilePicture(){
        Mockito.when(
            imageService.storeImage(Mockito.eq(IMAGE.getData()))
        ).thenReturn(IMAGE.getId());

        userService.updateProfilePicture(USER_ID, IMAGE.getData());
    }

    @Test
    public void testUpdateProfileInfo(){
        userService.updateProfileInfo(USER_ID, FIRSTNAME, LASTNAME, USERNAME);
    }

    @Test
    public void testUpdateLocale(){
        userService.updateLocale(Mockito.eq(USER_ID), Mockito.eq(LOCALE));
    }
    @Test(expected = DataAccessException.class)
    public void testUpdateLocaleWrongLocale(){
        Mockito.doThrow(new DataIntegrityViolationException("")).when(userDao).updateLocale(USER_ID, LOCALE);
        userService.updateLocale(USER_ID, LOCALE);
    }

    @Test
    public void testUpdateUniversityNameFound(){
        userService.updateUniversity(USER_ID, UNIVERSITY.getName());
    }
    @Test
    public void testUpdateUniversityIdFound(){
        userService.updateUniversity(USER_ID, UNIVERSITY.getId());
    }

    @Test
    public void testUpdateCareerNameFound(){
        userService.updateCareer(USER_ID, CAREER.getName());
    }
    @Test
    public void testUpdateCareerIdFound(){
        userService.updateCareer(USER_ID, CAREER.getId());
    }

    @Test
    public void testGetProfilePictureData(){
        Mockito.when(
            imageService.getImage(Mockito.eq(IMAGE.getId()))
        ).thenReturn(Optional.of(IMAGE));

        byte[] image = userService.getProfilePictureData(USER);

        assertNotNull(image);
        assertEquals(IMAGE.getData(), image);
    }
    @Test(expected = IllegalStateException.class)
    public void testGetProfilePictureDataMissing(){
        Mockito.when(
            imageService.getImage(Mockito.eq(IMAGE.getId()))
        ).thenReturn(Optional.empty());

        userService.getProfilePictureData(USER);
    }

    @Test
    public void testGetAllUsers(){
        Mockito.when(
            userDao.getAllUsers()
        ).thenReturn(List.of(USER));

        List<User> users = userService.getAllUsers();

        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals(USER, users.getFirst());
    }
    @Test
    public void testGetAllUsersNoUsers(){
        Mockito.when(
            userDao.getAllUsers()
        ).thenReturn(List.of());

        List<User> users = userService.getAllUsers();

        assertNotNull(users);
        assertEquals(0, users.size());
    }

    @Test
    public void testGetAllUsersPaged(){
        Page<User> testPage = new Page<User>(List.of(USER), 1, 1);
        Mockito.when(
            userDao.getAllUsers(Mockito.eq(PAGE_1_DEFAULT))
        ).thenReturn(testPage);

        Page<User> users = userService.getAllUsers(null, PAGE_1_DEFAULT);

        assertNotNull(users);
        assertEquals(testPage, users);
    }
    @Test
    public void testGetAllUsersPagedEmptySearch(){
        Page<User> testPage = new Page<User>(List.of(USER), 1, 1);
        Mockito.when(
            userDao.getAllUsers(Mockito.eq(PAGE_1_DEFAULT))
        ).thenReturn(testPage);

        Page<User> users = userService.getAllUsers("", PAGE_1_DEFAULT);

        assertNotNull(users);
        assertEquals(testPage, users);
    }
    @Test
    public void testGetAllUsersPagedNoUsers(){
        Page<User> testPage = new Page<User>(List.of(), 1, 0);
        Mockito.when(
            userDao.getAllUsers(Mockito.eq(PAGE_1_DEFAULT))
        ).thenReturn(testPage);

        Page<User> users = userService.getAllUsers(null, PAGE_1_DEFAULT);

        assertNotNull(users);
        assertEquals(testPage, users);
    }
    @Test
    public void testGetAllUsersPagedEmptySearchNoUsers(){
        Page<User> testPage = new Page<User>(List.of(), 1, 0);
        Mockito.when(
            userDao.getAllUsers(Mockito.eq(PAGE_1_DEFAULT))
        ).thenReturn(testPage);

        Page<User> users = userService.getAllUsers("", PAGE_1_DEFAULT);

        assertNotNull(users);
        assertEquals(testPage, users);
    }

    @Test
    public void testGetAllUsersPagedSearch(){
        Page<User> testPage = new Page<User>(List.of(USER), 1, 1);
        Mockito.when(
            userDao.searchUsers(Mockito.eq(FIRSTNAME), Mockito.eq(PAGE_1_DEFAULT))
        ).thenReturn(testPage);

        Page<User> users = userService.getAllUsers(FIRSTNAME, PAGE_1_DEFAULT);

        assertNotNull(users);
        assertEquals(testPage, users);
    }
    @Test
    public void testGetAllUsersPagedSearchNoUsers(){
        Page<User> testPage = new Page<User>(List.of(), 1, 1);
        Mockito.when(
            userDao.searchUsers(Mockito.eq(FIRSTNAME), Mockito.eq(PAGE_1_DEFAULT))
        ).thenReturn(testPage);

        Page<User> users = userService.getAllUsers(FIRSTNAME, PAGE_1_DEFAULT);

        assertNotNull(users);
        assertEquals(testPage, users);
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
}
