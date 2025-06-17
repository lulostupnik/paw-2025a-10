package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.EventRatingDao;
import ar.edu.itba.paw.models.Event;
import ar.edu.itba.paw.models.Rating;
import ar.edu.itba.paw.models.User;
import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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
    public Optional<Rating> findRatingByUserAndEvent(long userId, long eventId) {
        return em.createQuery(
                "FROM Rating r WHERE r.user.id = :userId AND r.event.id = :eventId", Rating.class)
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

}
