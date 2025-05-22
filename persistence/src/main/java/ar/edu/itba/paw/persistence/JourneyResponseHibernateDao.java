package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.JourneyDao;
import ar.edu.itba.paw.interfaces.persistence.JourneyResponseDao;
import ar.edu.itba.paw.models.*;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import java.util.Map;
import java.util.Optional;

import static ar.edu.itba.paw.persistence.HibernateDaoUtils.fetchPageByIds;


@Repository
public class JourneyResponseHibernateDao implements JourneyResponseDao {

    @PersistenceContext
    private EntityManager em;


    @Override
    public Optional<JourneyResponse> findById(final long id) {
        TypedQuery<JourneyResponse> query = em.createQuery("""
        FROM JourneyResponse jr
        WHERE jr.id = :id AND jr.deleted = FALSE
    """, JourneyResponse.class);
        query.setParameter("id", id);

        return query.getResultList().stream().findFirst();
    }

    @Override
    public JourneyResponse create(User user, Journey journey, String message) {
        final JourneyResponse journeyResponse = new JourneyResponse(user, journey, message);
        em.persist(journeyResponse);
        return journeyResponse;
    }

    @Override
    public int countByJourneyId(long journeyId) {
        final String sql = """
        SELECT COUNT(*)
        FROM journey_responses
        WHERE journey_id = :journeyId AND deleted = FALSE
    """;

        return em.createNativeQuery(sql)
                .setParameter("journeyId", journeyId)
                .getFirstResult();
    }


    @Override
    public Page<JourneyResponse> findAllByJourneyId(final long journeyId, final PageParams pageParams) {
        final String countSql = """
        SELECT COUNT(*)
        FROM journey_responses
        WHERE journey_id = :journeyId AND deleted = FALSE
    """;

        final String idSql = """
        SELECT jr.id
        FROM journey_responses jr
        WHERE jr.journey_id = :journeyId AND jr.deleted = FALSE
        ORDER BY jr.date_time
    """;

        final String jpqlFetch = """
        FROM JourneyResponse jr
        WHERE jr.id IN :ids
        ORDER BY jr.dateTime
    """;

        return fetchPageByIds(
                em,
                countSql,
                idSql,
                Map.of("journeyId", journeyId),
                jpqlFetch,
                JourneyResponse.class,
                pageParams,
                Map.of()
        );
    }
}
