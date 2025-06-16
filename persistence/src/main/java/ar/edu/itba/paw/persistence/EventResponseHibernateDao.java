package ar.edu.itba.paw.persistence;


import ar.edu.itba.paw.interfaces.persistence.EventResponseDao;
import ar.edu.itba.paw.models.*;
import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.math.BigInteger;
import java.util.Map;
import java.util.Optional;

import static ar.edu.itba.paw.persistence.HibernateDaoUtils.fetchPageByIds;

@Repository
public class EventResponseHibernateDao implements EventResponseDao {

    @PersistenceContext
    private EntityManager em;


    @Override
    public Optional<EventResponse> findById(final long id) {
        TypedQuery<EventResponse> query = em.createQuery("""
        FROM EventResponse er
        WHERE er.id = :id AND er.deleted = FALSE
    """, EventResponse.class);
        query.setParameter("id", id);

        return query.getResultList().stream().findFirst();
    }

    @Override
    public EventResponse create( User user, Event event, String message) {
        final EventResponse eventResponse = new EventResponse(user, event, message);
        em.persist(eventResponse);
        return eventResponse;
    }

    @Override
    public int countByEventId(long eventId) {
        final String sql = """
        SELECT COUNT(*)
        FROM event_responses
        WHERE event_id = :eventId AND deleted = FALSE
    """;

        return ((BigInteger)em.createNativeQuery(sql)
                .setParameter("eventId", eventId)
                .getSingleResult()).intValue();
    }

    @Override
    public Page<EventResponse> listAllByEventId(final long eventId, final PageParams pageParams) {
        final String countSql = """
        SELECT COUNT(*)
        FROM event_responses
        WHERE event_id = :eventId AND deleted = FALSE
    """;

        final String idSql = """
        SELECT er.id
        FROM event_responses er
        WHERE er.event_id = :eventId AND er.deleted = FALSE
        ORDER BY er.date_time
    """;

        final String jpqlFetch = """
        FROM EventResponse er
        WHERE er.id IN :ids
        ORDER BY er.dateTime
    """;

        return fetchPageByIds(
                em,
                countSql,
                idSql,
                Map.of("eventId", eventId),
                jpqlFetch,
                EventResponse.class,
                pageParams,
                Map.of()
        );
    }


    @Override
    public Page<User> findRespondersByEventId(final long eventId, final PageParams pageParams) {
        final String countSql = """
        SELECT COUNT(DISTINCT er.user_id)
        FROM event_responses er
        WHERE er.event_id = :eventId AND er.deleted = FALSE
    """;

        final String idSql = """
        SELECT DISTINCT er.user_id
        FROM event_responses er
        WHERE er.event_id = :eventId AND er.deleted = FALSE
        ORDER BY er.user_id
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
    
}
