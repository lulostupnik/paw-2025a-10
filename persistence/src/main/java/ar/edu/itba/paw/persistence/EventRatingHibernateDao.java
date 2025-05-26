package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.EventRatingDao;
import ar.edu.itba.paw.models.Event;
import ar.edu.itba.paw.models.PageParams;
import ar.edu.itba.paw.models.Rating;
import ar.edu.itba.paw.models.User;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Repository
public class EventRatingHibernateDao implements EventRatingDao {
    @PersistenceContext
    private EntityManager em;
    @Override
    public Rating rateEvent(User user, Event event, double rating) {
        Rating newRating = new Rating(user, event, rating);
        em.persist(newRating);
        return newRating;
    }

    @Override
    public Optional<Double> findRatingByUserAndEvent(long userId, long eventId) {
        return em.createQuery(
                "SELECT r.rating FROM Rating r WHERE r.user.id = :userId AND r.event.id = :eventId", Double.class)
                .setParameter("userId", userId)
                .setParameter("eventId", eventId)
                .getResultList()
                .stream()
                .findFirst();
    }

    @Override
    public int countRatingsByEvent(long eventId) {
        return em.createQuery(
                "SELECT COUNT(r) FROM Rating r WHERE r.event.id = :eventId", Long.class)
                .setParameter("eventId", eventId)
                .getSingleResult()
                .intValue();
    }

    @Override
    public Optional<Double> findRatingsAverageByEvent(long eventId) {
        return Optional.ofNullable(  em.createQuery(
                "SELECT AVG(r.rating) FROM Rating r WHERE r.event.id = :eventId", Double.class)
                .setParameter("eventId", eventId)
                .getSingleResult()
        );
    }
}
