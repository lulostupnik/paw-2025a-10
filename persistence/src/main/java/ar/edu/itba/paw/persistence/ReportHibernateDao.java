package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.ReportDao;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.enums.ReportReason;
import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Map;
import java.util.Optional;

import static ar.edu.itba.paw.persistence.HibernateDaoUtils.fetchPageByIds;

@Repository
public class ReportHibernateDao implements ReportDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Report create(User reportedUser, User reportingUser, String description, ReportReason reason) {
        final Report report = new Report(reportedUser, reportingUser, description, reason);
        em.persist(report);
        return report;
    }

    @Override
    public Report create(User reportedUser, User reportingUser, Journey journey, String description, ReportReason reason) {
        final Report report = new Report(reportedUser, reportingUser, journey, description, reason);
        em.persist(report);
        return report;
    }

    @Override
    public Report create(User reportedUser, User reportingUser, Event event, String description, ReportReason reason) {
        final Report report = new Report(reportedUser, reportingUser, event, description, reason);
        em.persist(report);
        return report;
    }

    @Override
    public Report create(User reportedUser, User reportingUser, JourneyResponse journeyResponse, String description, ReportReason reason) {
        final Report report = new Report(reportedUser, reportingUser, journeyResponse, description, reason);
        em.persist(report);
        return report;
    }

    @Override
    public Report create(User reportedUser, User reportingUser, EventResponse eventResponse, String description, ReportReason reason) {
        final Report report = new Report(reportedUser, reportingUser, eventResponse, description, reason);
        em.persist(report);
        return report;
    }



    @Override
    public Optional<Report> findById(long id) {
        return Optional.ofNullable(em.find(Report.class, id));
    }

    @Override
    public void hardDeleteByJourneyId(final long journeyId) {
        em.createQuery("""
        DELETE FROM Report r
        WHERE r.journey.id = :journeyId
           OR r.journeyResponse.id IN (SELECT jr.id FROM JourneyResponse jr WHERE jr.journey.id = :journeyId)
    """)
                .setParameter("journeyId", journeyId)
                .executeUpdate();
    }




    @Override
    public Page<Report> findAll(String search, PageParams params) {
        final String pattern = HibernateDaoUtils.likePattern(search);

        final String countSql = """
        SELECT COUNT(*)
        FROM reports r
        JOIN users u1 ON r.reported_user_id = u1.id
        JOIN users u2 ON r.reporting_user_id = u2.id
        WHERE r.deleted = false AND (
            LOWER(r.reason) LIKE :pattern OR
            LOWER(r.description) LIKE :pattern OR
            LOWER(u1.username) LIKE :pattern OR
            LOWER(u2.username) LIKE :pattern
        )
    """;

        final String idSql = """
        SELECT r.id
        FROM reports r
        JOIN users u1 ON r.reported_user_id = u1.id
        JOIN users u2 ON r.reporting_user_id = u2.id
        WHERE r.deleted = false AND (
            LOWER(r.reason) LIKE :pattern OR
            LOWER(r.description) LIKE :pattern OR
            LOWER(u1.username) LIKE :pattern OR
            LOWER(u2.username) LIKE :pattern
        )
    """;

        final String jpqlFetch = """
        FROM Report r
        WHERE r.id IN :ids
    """;

        return fetchPageByIds(
                em,
                countSql,
                idSql,
                Map.of("pattern", pattern),
                jpqlFetch,
                Report.class,
                params,
                Map.of()
        );
    }


}