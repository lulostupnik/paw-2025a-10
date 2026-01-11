package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.TipDao;
import ar.edu.itba.paw.models.*;
import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Map;
import java.util.Optional;

import static ar.edu.itba.paw.persistence.HibernateDaoUtils.fetchPageByIds;

@Repository
public class TipHibernateDao implements TipDao {


    @PersistenceContext
    private EntityManager em;


    @Override
    public Tip create(Journey journey, String title, String content) {
        Tip tip = new Tip(journey, title, content);
        em.persist(tip);
        return tip;
    }

    @Override
    public void delete(long tipId) {
        Tip tip = em.createQuery("FROM Tip t WHERE t.id = :tipId", Tip.class)
                .setParameter("tipId", tipId)
                .getSingleResult();
        em.remove(tip);
    }
    @Override
    public void deleteByJourney(long journeyId) {
        em.createQuery("DELETE FROM Tip t WHERE t.journey.id = :journeyId")
                .setParameter("journeyId", journeyId)
                .executeUpdate();
    }


    @Override
    public Optional<Tip> findById(long tipId) {
        return Optional.ofNullable(em.find(Tip.class, tipId));
    }

    @Override
    public Page<Tip> findByJourneyId(long journeyId, PageParams pageParams) {
        final String countSql = """
        SELECT COUNT(*)
        FROM tips
        WHERE journey_id = :journeyId
    """;

        final String idSql = """
        SELECT t.id
        FROM tips t
        WHERE t.journey_id = :journeyId
        ORDER BY t.date_time
    """;

        final String jpqlFetch = """
        FROM Tip t
        WHERE t.id IN :ids
        ORDER BY t.dateTime
    """;

        return fetchPageByIds(
                em,
                countSql,
                idSql,
                Map.of("journeyId", journeyId),
                jpqlFetch,
                Tip.class,
                pageParams,
                Map.of()
        );
    }
}
