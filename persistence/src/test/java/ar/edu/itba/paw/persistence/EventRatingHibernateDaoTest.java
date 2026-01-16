package ar.edu.itba.paw.persistence;

import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;

import ar.edu.itba.paw.models.Event;
import ar.edu.itba.paw.models.Rating;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistence.config.TestConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;
import static ar.edu.itba.paw.persistence.TestUtils.*;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class EventRatingHibernateDaoTest {

    @Autowired
    private EventRatingHibernateDao rateDao;

    @PersistenceContext
    private EntityManager em;

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
