package ar.edu.itba.paw.persistence;


import ar.edu.itba.paw.interfaces.persistence.EventResponseDao;
import ar.edu.itba.paw.models.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import static ar.edu.itba.paw.persistence.HibernateDaoUtils.fetchPageByIds;

@Repository
public class EventResponseHibernateDao implements EventResponseDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventResponseHibernateDao.class);

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

        return em.createNativeQuery(sql)
                .setParameter("eventId", eventId)
                .getFirstResult();
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
                pageParams
        );
    }

}
