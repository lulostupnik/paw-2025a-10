package ar.edu.itba.paw.persistence;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.sql.DataSource;

import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.persistence.config.TestConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;
import static ar.edu.itba.paw.persistence.TestUtils.*;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class UserHibernateDaoTest {

    @Autowired
    private DataSource ds;

    @Autowired
    private UserHibernateDao userDao;
        
    @PersistenceContext
    private EntityManager em;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp(){
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Test
    public void testCreateUser(){
        deleteUsers(jdbcTemplate);

        final User user = userDao.create(
            USER_1_MAIL,
            USER_1_NAME, 
            USER_FIRSTNAME, 
            USER_LASTNAME, 
            UNI_1, 
            CAREER_1, 
            IMAGE_1_ID, 
            USER_PASSWORD, 
            Locale.of(USER_LOCALE), 
            true
        );
        em.flush();

        assertEqualsUser(user, Map.of("id", user.getId()));
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, USER_TABLE));
    }
    @Test(expected = PersistenceException.class)
    public void testCreateUserNoMail(){
        userDao.create(
            null, 
            USER_NEW1_NAME, 
            USER_FIRSTNAME, 
            USER_LASTNAME, 
            UNI_1, 
            CAREER_1, 
            IMAGE_1_ID, 
            USER_PASSWORD, 
            Locale.of(USER_LOCALE), 
            true
        );
        em.flush();
    }
    @Test(expected = PersistenceException.class)
    public void testCreateUserNoUsername(){
        userDao.create(
            USER_NEW1_MAIL, 
            null, 
            USER_FIRSTNAME, 
            USER_LASTNAME, 
            UNI_1, 
            CAREER_1, 
            IMAGE_1_ID, 
            USER_PASSWORD, 
            Locale.of(USER_LOCALE), 
            true
        );
        em.flush();
    }
    @Test(expected = PersistenceException.class)
    public void testCreateUserNoFirstName(){
        userDao.create(
            USER_NEW1_MAIL,
            USER_NEW1_NAME, 
            null, 
            USER_LASTNAME, 
            UNI_1, 
            CAREER_1, 
            IMAGE_1_ID, 
            USER_PASSWORD, 
            Locale.of(USER_LOCALE), 
            true
        );
        em.flush();
    }
    @Test(expected = PersistenceException.class)
    public void testCreateUserNoLastName(){
        userDao.create(
            USER_NEW1_MAIL, 
            USER_NEW1_NAME, 
            USER_FIRSTNAME, 
            null, 
            UNI_1, 
            CAREER_1, 
            IMAGE_1_ID, 
            USER_PASSWORD, 
            Locale.of(USER_LOCALE), 
            true
        );
        em.flush();
    }
    @Test(expected = PersistenceException.class)
    public void testCreateUserInvalidUniversity(){
        userDao.create(
            USER_NEW1_MAIL, 
            USER_NEW1_NAME, 
            USER_FIRSTNAME, 
            USER_LASTNAME, 
            new University(
                1034234123l, 
                null, 
                null, 
                null), 
            CAREER_1, 
            IMAGE_1_ID, 
            USER_PASSWORD, 
            Locale.of(USER_LOCALE), 
            true
        );
        em.flush();
    }
    @Test(expected = PersistenceException.class)
    public void testCreateUserInvalidCareer(){
        userDao.create(
            USER_NEW1_MAIL, 
            USER_NEW1_NAME,
            USER_FIRSTNAME, 
            USER_LASTNAME, 
            UNI_1, 
            new Career(1313423l, null), 
            IMAGE_1_ID, 
            USER_PASSWORD, 
            Locale.of(USER_LOCALE), 
            true
        );
        em.flush();
    }
    @Test(expected = PersistenceException.class)
    public void testCreateUserInvalidPic(){
        userDao.create(
            USER_NEW1_MAIL, 
            USER_NEW1_NAME, 
            USER_FIRSTNAME, 
            USER_LASTNAME, 
            UNI_1, 
            CAREER_1, 
            123123123l, 
            USER_PASSWORD, 
            Locale.of(USER_LOCALE), 
            true
        );
        em.flush();
    }
    @Test(expected = PersistenceException.class)
    public void testCreateUserNoPassword(){
        userDao.create(
            USER_NEW1_MAIL, 
            USER_NEW1_NAME, 
            USER_FIRSTNAME, 
            USER_LASTNAME, 
            UNI_1, 
            CAREER_1, 
            IMAGE_1_ID, 
            null,
            Locale.of(USER_LOCALE), 
            true
        );
        em.flush();
    }

    @Test
    public void testFindUserById(){
        Optional<User> maybeUser = userDao.findById(USER_1_ID);

        assertNotNull(maybeUser);
        assertTrue(maybeUser.isPresent());
        assertEqualsUser(USER_1, maybeUser.get());
    }
    @Test
    public void testFindUserByIdMissing(){
        final Optional<User> maybeUser = userDao.findById(12341234l);

        assertNotNull(maybeUser);
        assertFalse(maybeUser.isPresent());
    }

    @Test
    public void testFindUserByEmail(){
        final Optional<User> maybeUser = userDao.findByEmail(USER_1_MAIL);

        assertNotNull(maybeUser);
        assertTrue(maybeUser.isPresent());
        assertEqualsUser(USER_1, maybeUser.get());
    }
    @Test
    public void testFindUserByEmailMissing(){
        final Optional<User> maybeUser = userDao.findByEmail(USER_FAKE_MAIL);

        assertNotNull(maybeUser);
        assertFalse(maybeUser.isPresent());
    }

    @Test
    public void testExistsByUsernameExists(){
        final boolean exists = userDao.existsByUsername(USER_1_NAME);

        assertTrue(exists);
    }
    @Test
    public void testExistsByUsernameDoesNotExist(){
        final boolean exists = userDao.existsByUsername(USER_FAKE_NAME);

        assertFalse(exists);
    }

    @Test
    public void testExistsByEmailExists(){
        final boolean exists = userDao.existsByEmail(USER_1_MAIL);

        assertTrue(exists);
    }
    @Test
    public void testExistsByEmailDoesNotExist(){
        final boolean exists = userDao.existsByEmail(USER_FAKE_MAIL);

        assertFalse(exists);
    }

    @Test
    public void testFindAllPage1(){
        Page<User> page1 = userDao.findUsers(
            null,
            PAGE_1_DEFAULT,
            null,
            null,
            null,
            null,
            null
        );

        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(4, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(2, page1.getContent().size());
        page1.getContent().forEach((u) ->
            assertEqualsUser(USER_DATA.get(u.getId()), u)
        );
    }
    @Test
    public void testFindAllPage2(){
        Page<User> page2 = userDao.findUsers(
            null,
            PAGE_2_DEFAULT,
            null,
            null,
            null,
            null,
            null
        );

        assertNotNull(page2);
        assertEquals(2, page2.getCurrentPage());
        assertEquals(4, page2.getTotalPages());
        assertNotNull(page2.getContent());
        assertEquals(2, page2.getContent().size());
        page2.getContent().forEach((u) ->
            assertEqualsUser(USER_DATA.get(u.getId()), u)
        );
    }
    @Test
    public void testFindAllWrongPage(){
        Page<User> page2 = userDao.findUsers(
            "",
            PAGE_2_BIG,
            null,
            null,
            null,
            null,
            null
        );

        assertNotNull(page2);
        assertEquals(2, page2.getCurrentPage());
        assertEquals(1, page2.getTotalPages());
        assertNotNull(page2.getContent());
        assertEquals(0, page2.getContent().size());
    }
    @Test
    public void testSearchPage1(){
        Page<User> page1 = userDao.findUsers(
            USER_FIRSTNAME, 
            PAGE_1_DEFAULT,
            null,
            null,
            null,
            null,
            null
        );

        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(4, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(PAGE_SIZE_DEFAULT, page1.getContent().size());
        page1.getContent().forEach((u) ->
            assertEqualsUser(USER_DATA.get(u.getId()), u)
        );
    }
    @Test
    public void testSearchPage2(){
        Page<User> page2 = userDao.findUsers(
            USER_FIRSTNAME, 
            PAGE_2_DEFAULT,
            null,
            null,
            null,
            null,
            null
        );

        assertNotNull(page2);
        assertEquals(2, page2.getCurrentPage());
        assertEquals(4, page2.getTotalPages());
        assertNotNull(page2.getContent());
        assertEquals(PAGE_SIZE_DEFAULT, page2.getContent().size());
        page2.getContent().forEach((u) ->
            assertEqualsUser(USER_DATA.get(u.getId()), u)
        );
    }
    @Test
    public void testSearchPageWrongPage(){
        Page<User> page2 = userDao.findUsers(
            USER_1_NAME, 
            PAGE_2_DEFAULT,
            null,
            null,
            null,
            null,
            null
        );

        assertEquals(2, page2.getCurrentPage());
        assertEquals(1, page2.getTotalPages());
        assertNotNull(page2.getContent());
        assertEquals(0, page2.getContent().size());
    }
    @Test
    public void testFindUsersWithFilters(){
        Page<User> page1 = userDao.findUsers(
            USER_FIRSTNAME, 
            PAGE_1_DEFAULT,
            EVENT_1_ID,
            UNIVERSITY_1_ID,
            CAREER_1_ID,
            INTEREST_1_ID,
            false
        );

        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(1, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(1, page1.getContent().size());
        assertEqualsUser(page1.getContent().getFirst());
    }

    @Test
    public void testFindAverageRatingForCreatedEvents(){
        Optional<Double> rating = userDao.findAverageRatingForCreatedEvents(USER_1_ID);

        assertNotNull(rating);
        assertTrue(rating.isPresent());
        assertEquals(USER_1_CREATED_EVENTS_RATING, rating.get(), 0.1);
    }
    @Test
    public void testFindAverageRatingForCreatedEventsNoRating(){
        Optional<Double> rating = userDao.findAverageRatingForCreatedEvents(USER_3_ID);

        assertNotNull(rating);
        assertFalse(rating.isPresent());
    }
    @Test
    public void testFindAverageRatingForCreatedEventsWrongUserId(){
        Optional<Double> rating = userDao.findAverageRatingForCreatedEvents(12341234l);

        assertNotNull(rating);
        assertFalse(rating.isPresent());
    }

    @Test
    public void testFindAverageRatingForAttendedEvents(){
        Optional<Double> rating = userDao.findAverageRatingForAttendedEvents(USER_1_ID);

        //average rating of event 1 and 2 (all of them, not just my ratings)
        assertNotNull(rating);
        assertTrue(rating.isPresent());
        assertEquals(USER_1_ATTENDED_EVENTS_RATING, rating.get(), 0.1);
    }
    @Test
    public void testFindAverageRatingForAttendedEventsNoRating(){
        Optional<Double> rating = userDao.findAverageRatingForAttendedEvents(USER_4_ID);

        assertNotNull(rating);
        assertFalse(rating.isPresent());
    }
    @Test
    public void testFindAverageRatingForAttendedEventsWrongUserId(){
        Optional<Double> rating = userDao.findAverageRatingForAttendedEvents(12341234l);

        assertNotNull(rating);
        assertFalse(rating.isPresent());
    }
}