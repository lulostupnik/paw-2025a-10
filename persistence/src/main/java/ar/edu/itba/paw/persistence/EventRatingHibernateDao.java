package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.EventRatingDao;
import ar.edu.itba.paw.models.Event;
import ar.edu.itba.paw.models.Page;
import ar.edu.itba.paw.models.PageParams;
import ar.edu.itba.paw.models.Rating;
import ar.edu.itba.paw.models.User;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Map;
import java.util.Optional;

import static ar.edu.itba.paw.persistence.HibernateDaoUtils.fetchPageByIds;

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
    public Optional<Rating> findById(long ratingId) {
        return Optional.ofNullable(em.find(Rating.class, ratingId));
    }

    @Override
    public Page<Rating> findByEventId(long eventId, PageParams pageParams) {
        final String countSql = """
            SELECT COUNT(*)
            FROM ratings
            WHERE event_id = :eventId
        """;

        final String idSql = """
            SELECT r.id
            FROM ratings r
            WHERE r.event_id = :eventId
            ORDER BY r.id DESC
        """;

        final String jpqlFetch = """
            FROM Rating r
            WHERE r.id IN :ids
            ORDER BY r.id DESC
        """;

        return fetchPageByIds(
                em,
                countSql,
                idSql,
                Map.of("eventId", eventId),
                jpqlFetch,
                Rating.class,
                pageParams,
                Map.of()
        );
    }

    @Override
    public void delete(long ratingId) {
        Rating rating = em.createQuery("FROM Rating r WHERE r.id = :ratingId", Rating.class)
                .setParameter("ratingId", ratingId)
                .getSingleResult();
        em.remove(rating);
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
