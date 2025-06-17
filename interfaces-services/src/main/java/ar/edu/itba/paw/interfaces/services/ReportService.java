package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.enums.ReportReason;
import ar.edu.itba.paw.models.enums.ReportStatus;
import java.util.Optional;

public interface ReportService {
    Report createReportForJourney(User reportingUser,long journeyId, String description, ReportReason reason);

    Report createReportForEvent(User reportingUser,long eventId, String description, ReportReason reason);

    Report createReportForEventResponse(User reportingUser,long responseId, String description, ReportReason reason);

    Report createReportForJourneyResponse(User reportingUser,long responseId, String description, ReportReason reason);

    Optional<Report> findById(Long id);

    Page<Report> findByUserPaginated(User user, PageParams params);

    long countReportsAgainstUser(User reportedUser);

    Page<Report> findAllPaginated(PageParams params);

    Page<Report> findByStatusPaginated(ReportStatus status, PageParams params);

    void delete(Report report);

    void deleteById(Long id);

    Page<Report> findAll(String search, PageParams params);

    Report updateReportStatus(long reportId, ReportStatus status);
}
