package ar.edu.itba.paw.persistence;


import ar.edu.itba.paw.interfaces.persistence.ReportDao;
import ar.edu.itba.paw.models.*;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static ar.edu.itba.paw.persistence.HibernateDaoUtils.fetchPageByIds;

@Repository
public class ReportHibernateDao implements ReportDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Report create(User reportedUser, User reportingUser, String description, String reason) {
        final Report report = new Report(reportedUser, reportingUser, description, reason);
        em.persist(report);
        return report;
    }

    @Override
    public Report create(User reportedUser, User reportingUser, Journey journey, String description, String reason) {
        final Report report = new Report(reportedUser, reportingUser, journey, description, reason);
        em.persist(report);
        return report;
    }

    @Override
    public Report create(User reportedUser, User reportingUser, Event event, String description, String reason) {
        final Report report = new Report(reportedUser, reportingUser, event, description, reason);
        em.persist(report);
        return report;
    }

    @Override
    public Optional<Report> findById(Long id) {
        return Optional.ofNullable(em.find(Report.class, id));
    }


    @Override
    public boolean hasUserReportedTarget(User reportingUser, User reportedUser) {
        final TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(r) FROM Report r WHERE r.reportingUser = :reportingUser AND r.reportedUser = :reportedUser AND r.deleted = false",
                Long.class
        );
        query.setParameter("reportingUser", reportingUser);
        query.setParameter("reportedUser", reportedUser);
        return query.getSingleResult() > 0;
    }

    @Override
    public boolean hasUserReportedJourney(User reportingUser, Journey journey) {
        final TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(r) FROM Report r WHERE r.reportingUser = :reportingUser AND r.journey = :journey AND r.deleted = false",
                Long.class
        );
        query.setParameter("reportingUser", reportingUser);
        query.setParameter("journey", journey);
        return query.getSingleResult() > 0;
    }

    @Override
    public boolean hasUserReportedEvent(User reportingUser, Event event) {
        final TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(r) FROM Report r WHERE r.reportingUser = :reportingUser AND r.event = :event AND r.deleted = false",
                Long.class
        );
        query.setParameter("reportingUser", reportingUser);
        query.setParameter("event", event);
        return query.getSingleResult() > 0;
    }

    @Override
    public long countReportsAgainstUser(User reportedUser) {
        final TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(r) FROM Report r WHERE r.reportedUser = :reportedUser AND r.deleted = false",
                Long.class
        );
        query.setParameter("reportedUser", reportedUser);
        return query.getSingleResult();
    }


    @Override
    public Page<Report> findAllPaginated(PageParams params) {
        final String countSql = """
                SELECT COUNT(*)
                FROM reports r
                WHERE r.deleted = false
                """;
        final String idSql = """
                SELECT r.id
                FROM reports r
                WHERE r.deleted = false
                """;

        final String jpqlFetch = """
                FROM Report r
                WHERE r.id IN :ids
                """;

        return fetchPageByIds(
                em,
                countSql,
                idSql,
                Map.of(),
                jpqlFetch,
                Report.class,
                params,
                Map.of()
        );
    }

    @Override
    public Page<Report> findByStatusPaginated(ReportStatus status, PageParams params) {
        final String countSql = """
                SELECT COUNT(*)
                FROM reports r
                WHERE r.status = :status AND r.deleted = false
                """;
        final String idSql = """
                SELECT r.id
                FROM reports r
                WHERE r.status = :status AND r.deleted = false
                """;
        final String jpqlFetch = """
                FROM Report r
                WHERE r.id IN :ids
                """;
        return fetchPageByIds(
                em,
                countSql,
                idSql,
                Map.of("status", status),
                jpqlFetch,
                Report.class,
                params,
                Map.of()
        );
    }

    @Override
    public void delete(Report report) {
        report.markAsDeleted();
        em.merge(report);
    }

}