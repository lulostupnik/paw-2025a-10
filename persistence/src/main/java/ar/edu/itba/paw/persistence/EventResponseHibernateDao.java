package ar.edu.itba.paw.persistence;


import ar.edu.itba.paw.interfaces.persistence.EventResponseDao;
import ar.edu.itba.paw.models.EventResponse;
import ar.edu.itba.paw.models.Page;
import ar.edu.itba.paw.models.PageParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.Map;

import static ar.edu.itba.paw.persistence.HibernateDaoUtils.fetchPageByIds;

@Repository
public class EventResponseHibernateDao implements EventResponseDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventResponseHibernateDao.class);

    @PersistenceContext
    private EntityManager em;

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
