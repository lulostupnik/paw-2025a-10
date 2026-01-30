package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.EventAttendanceDao;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.exceptions.EventNotFoundException;
import ar.edu.itba.paw.models.exceptions.UserNotFoundException;
import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Map;
import java.util.Optional;
import static ar.edu.itba.paw.persistence.HibernateDaoUtils.fetchPageByIds;

@Repository
public class EventAttendanceHibernateDao implements EventAttendanceDao {
    @PersistenceContext
    private EntityManager em;

    @Override
    public EventAttendance create(long userId, long eventId) {
        final Event event = em.find(Event.class, eventId);
        final User user = em.find(User.class, userId);
        if (event == null) {
            throw new EventNotFoundException( eventId );
        }
        if (user == null) {
            throw new UserNotFoundException(userId);
        }

        return create(user, event);
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
    public Optional<EventAttendance> findById(long userId, long eventId) {
        return em.createQuery(
                "FROM EventAttendance ea WHERE ea.user.id = :userId AND ea.event.id = :eventId",
                EventAttendance.class
        )
                .setParameter("userId", userId)
                .setParameter("eventId", eventId)
                .getResultStream()
                .findFirst();
    }

    @Override
    public EventAttendance create(User user, Event event) {
        EventAttendance attendance = new EventAttendance(user, event);
        em.persist(attendance);
        return attendance;
    }

    @Override
    public void delete(User user, Event event) {
        if (event != null && user != null) {
            EventAttendance attendance = em.createQuery("FROM EventAttendance ea WHERE ea.user = :user AND ea.event = :event", EventAttendance.class)
                    .setParameter("user", user)
                    .setParameter("event", event)
                    .getSingleResult();

            em.remove(attendance);
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
