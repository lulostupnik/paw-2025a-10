package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.TipDao;
import ar.edu.itba.paw.models.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static ar.edu.itba.paw.persistence.HibernateDaoUtils.fetchPageByIds;

public class TipHibernateDao implements TipDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(TipHibernateDao.class);

    @PersistenceContext
    private EntityManager em;


    @Override
    public void createTip(Journey journey, String title, String content) {
        LOGGER.debug("Creating tip for journey: {}", journey);
        Tip tip = new Tip(journey, title, content);
        em.persist(tip);
        LOGGER.debug("Tip created with ID: {}", tip.getId());
    }

    @Override
    public void deleteTip(long tipId) {
        Tip tip = em.createQuery("FROM Tip t WHERE t.id = :tipId", Tip.class)
                .setParameter("tipId", tipId)
                .getSingleResult();
        em.remove(tip);  //query throws NoResultException -> can't be null
    }

    @Override
    public Optional<Tip> findTipById(long tipId) {
        return Optional.ofNullable(em.find(Tip.class, tipId));
    }

    @Override
    public Page<Tip> findTipsByJourneyId(long journeyId, PageParams pageParams) {
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
