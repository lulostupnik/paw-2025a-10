package ar.edu.itba.paw.interfaces.persistence;
import ar.edu.itba.paw.models.*;
import java.util.Optional;

public interface ReportDao {
    Report create(User reportedUser, User reportingUser, String description, String reason);
    Report create(User reportedUser, User reportingUser, Journey journey, String description, String reason);
    Report create(User reportedUser, User reportingUser, Event event, String description, String reason);
    Optional<Report> findById(Long id);
    boolean hasUserReportedTarget(User reportingUser, User reportedUser);
    boolean hasUserReportedJourney(User reportingUser, Journey journey);
    boolean hasUserReportedEvent(User reportingUser, Event event);
    long countReportsAgainstUser(User reportedUser);
    Page<Report> findAllPaginated(PageParams params);
    Page<Report> findByStatusPaginated(ReportStatus status, PageParams params);
    void delete(Report report);
}