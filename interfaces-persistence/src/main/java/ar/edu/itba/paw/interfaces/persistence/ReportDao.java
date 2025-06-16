package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.enums.ReportReason;
import ar.edu.itba.paw.models.enums.ReportStatus;
import java.util.Optional;

public interface ReportDao {
    Report create(User reportedUser, User reportingUser, String description, ReportReason reason);
    Report create(User reportedUser, User reportingUser, Journey journey, String description, ReportReason reason);
    Report create(User reportedUser, User reportingUser, Event event, String description, ReportReason reason);
    Report create(User reportedUser, User reportingUser, EventResponse eventResponse, String description, ReportReason reason);
    Report create(User reportedUser, User reportingUser, JourneyResponse eventResponse, String description, ReportReason reason);
    Optional<Report> findById(Long id);
    Page<Report> findByUserPaginated(User user, PageParams params);
    long countReportsAgainstUser(User reportedUser);
    Page<Report> findAllPaginated(PageParams params);
    Page<Report> findByStatusPaginated(ReportStatus status, PageParams params);
    Page<Report> findAll(String search, PageParams params);
}