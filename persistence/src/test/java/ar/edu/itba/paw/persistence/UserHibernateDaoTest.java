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
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

@Transactional
@Rollback
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
        TestUtils.deleteUsers(jdbcTemplate);

        final User user = userDao.create(TestUtils.USER_1_MAIL, TestUtils.USER_1_NAME, TestUtils.USER_FIRSTNAME, TestUtils.USER_LASTNAME, TestUtils.UNI_1, TestUtils.CAREER_1, TestUtils.IMAGE_1_ID, TestUtils.USER_PASSWORD, Locale.of(TestUtils.USER_LOCALE), true);
        em.flush();

        TestUtils.assertEqualsUser(user, Map.of("id", user.getId()));
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.USER_TABLE));
    }
    @Test(expected = PersistenceException.class)
    public void testCreateUserNoMail(){
        userDao.create(null, TestUtils.USER_NEW1_NAME, TestUtils.USER_FIRSTNAME, TestUtils.USER_LASTNAME, TestUtils.UNI_1, TestUtils.CAREER_1, TestUtils.IMAGE_1_ID, TestUtils.USER_PASSWORD, Locale.of(TestUtils.USER_LOCALE), true);
        em.flush();
    }
    @Test(expected = PersistenceException.class)
    public void testCreateUserNoUsername(){
        userDao.create(TestUtils.USER_NEW1_MAIL, null, TestUtils.USER_FIRSTNAME, TestUtils.USER_LASTNAME, TestUtils.UNI_1, TestUtils.CAREER_1, TestUtils.IMAGE_1_ID, TestUtils.USER_PASSWORD, Locale.of(TestUtils.USER_LOCALE), true);
        em.flush();
    }
    @Test(expected = PersistenceException.class)
    public void testCreateUserNoFirstName(){
        userDao.create(TestUtils.USER_NEW1_MAIL, TestUtils.USER_NEW1_NAME, null, TestUtils.USER_LASTNAME, TestUtils.UNI_1, TestUtils.CAREER_1, TestUtils.IMAGE_1_ID, TestUtils.USER_PASSWORD, Locale.of(TestUtils.USER_LOCALE), true);
        em.flush();
    }
    @Test(expected = PersistenceException.class)
    public void testCreateUserNoLastName(){
        userDao.create(TestUtils.USER_NEW1_MAIL, TestUtils.USER_NEW1_NAME, TestUtils.USER_FIRSTNAME, null, TestUtils.UNI_1, TestUtils.CAREER_1, TestUtils.IMAGE_1_ID, TestUtils.USER_PASSWORD, Locale.of(TestUtils.USER_LOCALE), true);
        em.flush();
    }
    @Test(expected = PersistenceException.class)
    public void testCreateUserInvalidUniversity(){
        userDao.create(TestUtils.USER_NEW1_MAIL, TestUtils.USER_NEW1_NAME, TestUtils.USER_FIRSTNAME, TestUtils.USER_LASTNAME, new University(1034234123l, null, null, null), TestUtils.CAREER_1, TestUtils.IMAGE_1_ID, TestUtils.USER_PASSWORD, Locale.of(TestUtils.USER_LOCALE), true);
        em.flush();
    }
    @Test(expected = PersistenceException.class)
    public void testCreateUserInvalidCareer(){
        userDao.create(TestUtils.USER_NEW1_MAIL, TestUtils.USER_NEW1_NAME, TestUtils.USER_FIRSTNAME, TestUtils.USER_LASTNAME, TestUtils.UNI_1, new Career((long)1313423,null), TestUtils.IMAGE_1_ID, TestUtils.USER_PASSWORD, Locale.of(TestUtils.USER_LOCALE), true);
        em.flush();
    }
    @Test(expected = PersistenceException.class)
    public void testCreateUserInvalidPic(){
        userDao.create(TestUtils.USER_NEW1_MAIL, TestUtils.USER_NEW1_NAME, TestUtils.USER_FIRSTNAME, TestUtils.USER_LASTNAME, TestUtils.UNI_1, TestUtils.CAREER_1, 123123123, TestUtils.USER_PASSWORD, Locale.of(TestUtils.USER_LOCALE), true);
        em.flush();
    }
    @Test(expected = PersistenceException.class)
    public void testCreateUserNoPassword(){
        userDao.create(TestUtils.USER_NEW1_MAIL, TestUtils.USER_NEW1_NAME, TestUtils.USER_FIRSTNAME, TestUtils.USER_LASTNAME, TestUtils.UNI_1, TestUtils.CAREER_1, TestUtils.IMAGE_1_ID, null, Locale.of(TestUtils.USER_LOCALE), true);
        em.flush();
    }

    @Test
    public void testFindUserById(){
        Optional<User> maybeUser = userDao.findById(TestUtils.USER_1_ID);

        assertNotNull(maybeUser);
        assertTrue(maybeUser.isPresent());
        TestUtils.assertEqualsUser(TestUtils.USER_1, maybeUser.get());
    }
    @Test
    public void testFindUserByIdMissing(){
        final Optional<User> maybeUser = userDao.findById(12341234);

        assertNotNull(maybeUser);
        assertFalse(maybeUser.isPresent());
    }
    @Test
    public void testFindUserByEmail(){
        final Optional<User> maybeUser = userDao.findByEmail(TestUtils.USER_1_MAIL);

        assertNotNull(maybeUser);
        assertTrue(maybeUser.isPresent());
        TestUtils.assertEqualsUser(TestUtils.USER_1, maybeUser.get());
    }
    @Test
    public void testFindUserByEmailMissing(){
        final Optional<User> maybeUser = userDao.findByEmail(TestUtils.USER_FAKE_MAIL);

        assertNotNull(maybeUser);
        assertFalse(maybeUser.isPresent());
    }

    @Test
    public void testExistsByUsernameDoesExist(){
        final boolean exists = userDao.existsByUsername(TestUtils.USER_1_NAME);

        assertTrue(exists);
    }
    @Test
    public void testExistsByUsernameDoesNotExist(){
        final boolean exists = userDao.existsByUsername(TestUtils.USER_FAKE_NAME);

        assertFalse(exists);
    }
    @Test
    public void testExistsByEmailDoesExist(){
        final boolean exists = userDao.existsByEmail(TestUtils.USER_1_MAIL);

        assertTrue(exists);
    }
    @Test
    public void testExistsByEmailDoesNotExist(){
        final boolean exists = userDao.existsByEmail(TestUtils.USER_FAKE_MAIL);

        assertFalse(exists);
    }

    @Test
    public void testFindAllPage1(){
        Page<User> page1 = userDao.findAll(TestUtils.PAGE_1_DEFAULT);

        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(4, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(2, page1.getContent().size());
        for (User u : page1.getContent()){
            TestUtils.assertEqualsUser(TestUtils.USER_DATA.get(u.getId()), u);
        }
    }
    @Test
    public void testFindAllPage2(){
        Page<User> page2 = userDao.findAll(TestUtils.PAGE_2_DEFAULT);

        assertNotNull(page2);
        assertEquals(2, page2.getCurrentPage());
        assertEquals(4, page2.getTotalPages());
        assertNotNull(page2.getContent());
        assertEquals(2, page2.getContent().size());
        for (User u : page2.getContent()){
            TestUtils.assertEqualsUser(TestUtils.USER_DATA.get(u.getId()), u);
        }
    }
    @Test
    public void testFindAllWrongPage(){
        Page<User> page2 = userDao.findAll(TestUtils.PAGE_2_BIG);

        assertNotNull(page2);
        assertEquals(2, page2.getCurrentPage());
        assertEquals(1, page2.getTotalPages());
        assertNotNull(page2.getContent());
        assertEquals(0, page2.getContent().size());
    }

    @Test
    public void testSearchPage1(){
        Page<User> page1 = userDao.search(TestUtils.USER_FIRSTNAME, TestUtils.PAGE_1_DEFAULT);

        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(4, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(TestUtils.PAGE_SIZE_DEFAULT, page1.getContent().size());
        for (User u : page1.getContent()){
            TestUtils.assertEqualsUser(TestUtils.USER_DATA.get(u.getId()), u);
        }
    }
    @Test
    public void testSearchPage2(){
        Page<User> page2 = userDao.search(TestUtils.USER_FIRSTNAME, TestUtils.PAGE_2_DEFAULT);

        assertNotNull(page2);
        assertEquals(2, page2.getCurrentPage());
        assertEquals(4, page2.getTotalPages());
        assertNotNull(page2.getContent());
        assertEquals(TestUtils.PAGE_SIZE_DEFAULT, page2.getContent().size());
        for (User u : page2.getContent()){
            TestUtils.assertEqualsUser(TestUtils.USER_DATA.get(u.getId()), u);
        }
    }
    @Test
    public void testSearchPageWrongPage(){
        Page<User> page2 = userDao.search(TestUtils.USER_1_NAME, TestUtils.PAGE_2_DEFAULT);

        assertEquals(2, page2.getCurrentPage());
        assertEquals(1, page2.getTotalPages());
        assertNotNull(page2.getContent());
        assertEquals(0, page2.getContent().size());
    }

    @Test
    public void testFindAverageRatingForCreatedEvents(){
        Optional<Double> rating = userDao.findAverageRatingForCreatedEvents(TestUtils.USER_1_ID);

        assertNotNull(rating);
        assertTrue(rating.isPresent());
        assertEquals(TestUtils.USER_1_CREATED_EVENTS_RATING, rating.get(), 0.1);
    }
    @Test
    public void testFindAverageRatingForCreatedEventsNoRating(){
        Optional<Double> rating = userDao.findAverageRatingForCreatedEvents(TestUtils.USER_3_ID);

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
        Optional<Double> rating = userDao.findAverageRatingForAttendedEvents(TestUtils.USER_1_ID);

        //average rating of event 1 and 2 (all of them, not just my ratings)
        assertNotNull(rating);
        assertTrue(rating.isPresent());
        assertEquals(TestUtils.USER_1_ATTENDED_EVENTS_RATING, rating.get(), 0.1);
    }
    @Test
    public void testFindAverageRatingForAttendedEventsNoRating(){
        Optional<Double> rating = userDao.findAverageRatingForAttendedEvents(TestUtils.USER_4_ID);

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