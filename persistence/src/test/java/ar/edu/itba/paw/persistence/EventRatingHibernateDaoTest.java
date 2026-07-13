package ar.edu.itba.paw.persistence;

import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.sql.DataSource;

import ar.edu.itba.paw.models.Event;
import ar.edu.itba.paw.models.Page;
import ar.edu.itba.paw.models.Rating;
import ar.edu.itba.paw.models.User;
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
public class EventRatingHibernateDaoTest {

    @Autowired
    private DataSource ds;

    @Autowired
    private EventRatingHibernateDao rateDao;

    @PersistenceContext
    private EntityManager em;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp(){
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Test
    public void testRateEvent(){
        Rating rating = rateDao.rateEvent(
            USER_3, 
            EVENT_1, 
            EVENT_1_USER_1_RATING
        );
        em.flush();

        assertNotNull(rating);
        assertEqualsEvent(EVENT_1, rating.getEvent());
        assertEqualsUser(USER_3, rating.getUser());
        assertEquals(EVENT_1_USER_1_RATING, rating.getRating(), 0.1);
        assertTrue(rating.getId() > 1);

        assertEquals(
            TOTAL_RATINGS + 1,
            JdbcTestUtils.countRowsInTable(jdbcTemplate, RATING_TABLE)
        );
        assertEquals(
            1,
            JdbcTestUtils.countRowsInTableWhere(
                jdbcTemplate, RATING_TABLE,
                "id = " + rating.getId() + " AND user_id = " + USER_3_ID + " AND event_id = " + EVENT_1_ID + " AND rating = " + EVENT_1_USER_1_RATING
            )
        );
    }
    @Test(expected = PersistenceException.class)
    public void testRateEventRatingTooHigh(){
        rateDao.rateEvent(USER_3, EVENT_1, 6);
        em.flush();
    }
    @Test(expected = PersistenceException.class)
    public void testRateEventRatingTooLow(){
        rateDao.rateEvent(USER_3, EVENT_1, -1.0);
        em.flush();
    }
    @Test(expected = PersistenceException.class)
    public void testRateEventMissingEvent(){
        rateDao.rateEvent(
            USER_3, 
            new Event(
                12341234l, 
                null, 
                null, 
                null, 
                0l, 
                null, 
                null, 
                null, 
                null, 
                null), 
            3
        );
        em.flush();
    }
    @Test(expected = PersistenceException.class)
    public void testRateEventMissingUser(){
        rateDao.rateEvent(
            new User(
                12341234l, 
                null, 
                null, 
                null, 
                null, 
                null, 
                null,
                    0L,
                null, 
                false, 
                false), 
            EVENT_1, 
            6
        );
        em.flush();
    }

    @Test
    public void testFindRatingByUserAndEvent(){
        Optional<Rating> maybeRating = rateDao.findRatingByUserAndEvent(
            USER_1_ID, EVENT_1_ID
        );

        assertNotNull(maybeRating);
        assertTrue(maybeRating.isPresent());
        assertEquals(EVENT_1_USER_1_RATING, maybeRating.get().getRating(), 0.1);
        assertEqualsUser(USER_1, maybeRating.get().getUser());
        assertEqualsEvent(EVENT_1, maybeRating.get().getEvent());

    }
    @Test
    public void testFindRatingByUserAndEventNoRating(){
        Optional<Rating> maybeRating = rateDao.findRatingByUserAndEvent(
            USER_3_ID, EVENT_1_ID
        );

        assertNotNull(maybeRating);
        assertFalse(maybeRating.isPresent());
    }
    @Test
    public void testFindRatingByUserAndEventMissingUser(){
        Optional<Rating> maybeRating = rateDao.findRatingByUserAndEvent(
            12341234l, EVENT_1_ID
        );

        assertNotNull(maybeRating);
        assertFalse(maybeRating.isPresent());
    }
    @Test
    public void testFindRatingByUserAndEventMissingEvent(){
        Optional<Rating> maybeRating = rateDao.findRatingByUserAndEvent(
            USER_1_ID, 12341234l
        );

        assertNotNull(maybeRating);
        assertFalse(maybeRating.isPresent());
    }

    @Test
    public void testFindById(){
        Optional<Rating> rating = rateDao.findById(1);

        assertNotNull(rating);
        assertTrue(rating.isPresent());
        assertEqualsRating(RATING_1, rating.get());
    }
    @Test
    public void testFindByIdMissing(){
        Optional<Rating> rating = rateDao.findById(123412341);

        assertNotNull(rating);
        assertTrue(rating.isEmpty());
    }

    @Test
    public void testFindByEventId(){
        Page<Rating> ratingPage = rateDao.findByEventId(EVENT_1_ID, PAGE_1_BIG);

        assertNotNull(ratingPage);
        assertEquals(1, ratingPage.getCurrentPage());
        assertEquals(1, ratingPage.getTotalPages());
        assertEquals(EVENT_1_RATING_COUNT, ratingPage.getTotalElements());
        assertNotNull(ratingPage.getContent());
        assertEquals(EVENT_1_RATING_COUNT, ratingPage.getContent().size());
        assertEqualsRating(RATING_2, ratingPage.getContent().get(0));
        assertEqualsRating(RATING_1, ratingPage.getContent().get(1));
    }
    @Test
    public void testFindByEventIdMissing(){
        Page<Rating> ratingPage = rateDao.findByEventId(12341234, PAGE_1_BIG);

        assertNotNull(ratingPage);
        assertEquals(1, ratingPage.getCurrentPage());
        assertEquals(0, ratingPage.getTotalPages());
        assertEquals(0, ratingPage.getTotalElements());
        assertNotNull(ratingPage.getContent());
        assertEquals(0, ratingPage.getContent().size());
    }

    @Test
    public void testDelete(){
        rateDao.delete(RATING_1_ID);
        em.flush();

        assertEquals(
            TOTAL_RATINGS - 1,
            JdbcTestUtils.countRowsInTable(jdbcTemplate, RATING_TABLE)
        );
        assertEquals(
            0,
            JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, RATING_TABLE, "id = " + RATING_1_ID)
        );
    }
    @Test(expected = NoResultException.class)
    public void testDeleteMissing(){
        rateDao.delete(123412341);
    }

    @Test
    public void testCountRatingsByEvent(){
        int ratings = rateDao.countRatingsByEvent(EVENT_1_ID);

        assertEquals(EVENT_1_RATING_COUNT, ratings);
    }
    @Test
    public void testCountRatingsByEventNoRatings(){
        int ratings = rateDao.countRatingsByEvent(EVENT_OLDER_ID);

        assertEquals(0, ratings);
    }
    @Test
    public void testCountRatingsByEventMissingEvent(){
        int ratings = rateDao.countRatingsByEvent(12341234l);

        assertEquals(0, ratings);
    }
}
