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
        Rating rating = rateDao.rateEvent(TestUtils.USER_3, TestUtils.EVENT_1, TestUtils.EVENT_1_USER_1_RATING);
        em.flush();

        assertNotNull(rating);
        TestUtils.assertEqualsEvent(TestUtils.EVENT_1, rating.getEvent());
        TestUtils.assertEqualsUser(TestUtils.USER_3, rating.getUser());
        assertEquals(TestUtils.EVENT_1_USER_1_RATING, rating.getRating(), 0.1);
        assertTrue(rating.getId() > 1);
    }
    @Test(expected = PersistenceException.class)
    public void testRateEventRatingTooHigh(){
        rateDao.rateEvent(TestUtils.USER_3, TestUtils.EVENT_1, 6);
        em.flush();
    }
    @Test(expected = PersistenceException.class)
    public void testRateEventRatingTooLow(){
        rateDao.rateEvent(TestUtils.USER_3, TestUtils.EVENT_1, -1.0);
        em.flush();
    }
    @Test(expected = PersistenceException.class)
    public void testRateEventMissingEvent(){
        rateDao.rateEvent(TestUtils.USER_3, new Event(12341234l, null, null, null, 0l, null, null, null, null, null), 3);
        em.flush();
    }
    @Test(expected = PersistenceException.class)
    public void testRateEventMissingUser(){
        rateDao.rateEvent(new User(12341234l, null, null, null, null, null, null, 0, null, false, false), TestUtils.EVENT_1, 6);
        em.flush();
    }

    @Test
    public void testFindRatingByUserAndEvent(){
        Optional<Rating> maybeRating = rateDao.findRatingByUserAndEvent(TestUtils.USER_1_ID, TestUtils.EVENT_1_ID);

        assertNotNull(maybeRating);
        assertTrue(maybeRating.isPresent());
        assertEquals(TestUtils.EVENT_1_USER_1_RATING, maybeRating.get().getRating(), 0.1);
        TestUtils.assertEqualsUser(TestUtils.USER_1, maybeRating.get().getUser());
        TestUtils.assertEqualsEvent(TestUtils.EVENT_1, maybeRating.get().getEvent());

    }
    @Test
    public void testFindRatingByUserAndEventNoRating(){
        Optional<Rating> maybeRating = rateDao.findRatingByUserAndEvent(TestUtils.USER_3_ID, TestUtils.EVENT_1_ID);

        assertNotNull(maybeRating);
        assertFalse(maybeRating.isPresent());
    }
    @Test
    public void testFindRatingByUserAndEventMissingUser(){
        Optional<Rating> maybeRating = rateDao.findRatingByUserAndEvent(12341234l, TestUtils.EVENT_1_ID);

        assertNotNull(maybeRating);
        assertFalse(maybeRating.isPresent());
    }
    @Test
    public void testFindRatingByUserAndEventMissingEvent(){
        Optional<Rating> maybeRating = rateDao.findRatingByUserAndEvent(TestUtils.USER_1_ID, 12341234l);

        assertNotNull(maybeRating);
        assertFalse(maybeRating.isPresent());
    }

    @Test
    public void testCountRatingsByEvent(){
        int ratings = rateDao.countRatingsByEvent(TestUtils.EVENT_1_ID);

        assertEquals(TestUtils.EVENT_1_RATING_COUNT, ratings);
    }
    @Test
    public void testCountRatingsByEventNoRatings(){
        int ratings = rateDao.countRatingsByEvent(TestUtils.EVENT_OLDER_ID);

        assertEquals(0, ratings);
    }
    @Test
    public void testCountRatingsByEventMissingEvent(){
        int ratings = rateDao.countRatingsByEvent(12341234l);

        assertEquals(0, ratings);
    }

//    @Test
//    public void testFindRatingsAverageByEvent(){
//        Optional<Double> maybeEventRating = rateDao.findRatingsAverageByEvent(TestUtils.EVENT_1_ID);
//
//        assertNotNull(maybeEventRating);
//        assertTrue(maybeEventRating.isPresent());
//        assertEquals(TestUtils.EVENT_1_RATING, maybeEventRating.get(), 0.1);
//    }
//    @Test
//    public void testFindRatingsAverageByEventNoRatings(){
//        Optional<Double> maybeEventRating = rateDao.findRatingsAverageByEvent(TestUtils.EVENT_OLDER_ID);
//
//        assertNotNull(maybeEventRating);
//        assertFalse(maybeEventRating.isPresent());
//    }
//    @Test
//    public void testFindRatingsAverageByEventMissingEvent(){
//        Optional<Double> maybeEventRating = rateDao.findRatingsAverageByEvent(12341234l);
//
//        assertNotNull(maybeEventRating);
//        assertFalse(maybeEventRating.isPresent());
//    }

}
