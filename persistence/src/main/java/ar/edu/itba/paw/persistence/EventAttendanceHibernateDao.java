package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.EventAttendanceDao;
import ar.edu.itba.paw.models.*;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import java.math.BigInteger;
import java.util.Map;

import static ar.edu.itba.paw.persistence.HibernateDaoUtils.fetchPageByIds;

@Repository
public class EventAttendanceHibernateDao implements EventAttendanceDao {
    @PersistenceContext
    private EntityManager em;

    @Override
    public void create(long userId, long eventId) {
        final Event event = em.find(Event.class, eventId);
        final User user = em.find(User.class, userId);

        create(user, event);
    }

    @Override
    public void delete(long userId, long eventId) {
        final Event event = em.find(Event.class, eventId);
        final User user = em.find(User.class, userId);

      delete(user, event);

    }

    @Override
    public boolean exists(long userId, long eventId) {
        final Event event = em.find(Event.class, eventId);
        final User user = em.find(User.class, userId);
        return exists(user, event);
    }

    @Override
    public void create(User user, Event event) {
        if (event != null && user != null) {
            EventAttendance attendance = new EventAttendance(user, event);
            em.persist(attendance);
        }
    }

    @Override
    public void delete(User user, Event event) {
        if (event != null && user != null) {
            EventAttendance attendance = em.createQuery("FROM EventAttendance ea WHERE ea.user = :user AND ea.event = :event", EventAttendance.class)
                    .setParameter("user", user)
                    .setParameter("event", event)
                    .getSingleResult();

            em.remove(attendance);  //query throws NoResultException -> can't be null
        }
    }

    @Override
    public boolean exists(User user, Event event) {
        if (event != null && user != null) {
            return em.createQuery("SELECT COUNT(ea) FROM EventAttendance ea WHERE ea.user = :user AND ea.event = :event", Long.class)
                    .setParameter("user", user)
                    .setParameter("event", event)
                    .getSingleResult() > 0;
        }
        return false;
    }

   /* @Override
    public Page<EventAttendance> listAllByEventId(long eventId, PageParams pageParams) {
        final String countSql = """
                SELECT COUNT(*)
                FROM event_attendances
                WHERE event_id = :eventId
            """;
        final String idSql = """
                SELECT ea.user_id
                FROM event_attendances ea
                WHERE ea.event_id = :eventId
                ORDER BY ea.user_id
            """;
        final String jpqlFetch = """
                FROM EventAttendance ea
                WHERE ea.event.id = :eventId AND ea.user.id IN :ids
                ORDER BY ea.user.id
            """;

        return fetchPageByIds(
                em,
                countSql,
                idSql,
                Map.of("eventId", eventId),
                jpqlFetch,
                EventAttendance.class,
                pageParams,
                Map.of("eventId", eventId)
        );
    }

    @Override
    public Page<EventAttendance> listAllByUserId(long userId, PageParams pageParams) {
        final String countSql = """
                SELECT COUNT(*)
                FROM event_attendances
                WHERE user_id = :userId
            """;
        final String idSql = """
                SELECT ea.event_id
                FROM event_attendances ea
                WHERE ea.user_id = :userId
                ORDER BY ea.event_id
            """;
        final String jpqlFetch = """
                FROM EventAttendance ea
                WHERE ea.user.id = :userId AND ea.event.id IN :ids
                ORDER BY ea.event.id
            """;
        return fetchPageByIds(
                em,
                countSql,
                idSql,
                Map.of("userId", userId),
                jpqlFetch,
                EventAttendance.class,
                pageParams,
                Map.of("userId", userId)
        );
    }

    @Override
    public int countAttendantsByEventId(long eventId) {
        final String sql = """
                SELECT COUNT(*)
                FROM event_attendances
                WHERE event_id = :eventId
            """;

        return ((BigInteger)em.createNativeQuery(sql)
                .setParameter("eventId", eventId)
                .getSingleResult()).intValue();
    }*/


    @Override
    public Page<User> findAttendeesByEventId(final long eventId, final PageParams pageParams) {
        final String countSql = """
        SELECT COUNT(*)
        FROM event_attendances ea
        WHERE ea.event_id = :eventId
    """;

        final String idSql = """
        SELECT ea.user_id
        FROM event_attendances ea
        WHERE ea.event_id = :eventId
        ORDER BY ea.user_id
    """;

        final String jpqlFetch = """
        FROM User u
        WHERE u.id IN :ids
        ORDER BY u.id
    """;

        return fetchPageByIds(
                em,
                countSql,
                idSql,
                Map.of("eventId", eventId),
                jpqlFetch,
                User.class,
                pageParams,
                Map.of()
        );
    }


    @Override
    public int countEventsAttendedByUser(long userId) {
        final String sql = """
        SELECT COUNT(*)
        FROM event_attendances ea
        JOIN events e ON ea.event_id = e.id
        WHERE ea.user_id = :userId AND e.deleted = FALSE
        """;

        Query countQuery = em.createNativeQuery(sql);
        countQuery.setParameter("userId", userId);
        return ((Number) countQuery.getSingleResult()).intValue();
    }

}
