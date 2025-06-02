package ar.edu.itba.paw.persistence;


import ar.edu.itba.paw.interfaces.persistence.ReportDao;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.enums.ReportStatus;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
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
                Map.of("status", status.toString()),
                jpqlFetch,
                Report.class,
                params,
                Map.of()
        );
    }

    @Override
    public Page<Report> findByUserPaginated(User user, PageParams params) {
        final String countSql = """
                SELECT COUNT(*)
                FROM reports r
                WHERE r.reporting_user_id = :user AND r.deleted = false
                """;
        final String idSql = """
                SELECT r.id
                FROM reports r
                WHERE r.reporting_user_id = :user AND r.deleted = false
                """;
        final String jpqlFetch = """
                FROM Report r
                WHERE r.id IN :ids
                """;
        return fetchPageByIds(
                em,
                countSql,
                idSql,
                Map.of("user", user),
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

    @Override
    public void deleteById(Long id) {
        final Report report = em.find(Report.class, id);
        if (report != null) {
            report.markAsDeleted();
            em.merge(report);
        }
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