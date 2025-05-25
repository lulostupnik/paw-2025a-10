package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.enums.ReportStatus;

import java.util.Optional;

public interface ReportService {
    Report createReport(User reportedUser, User reportingUser, String description, String reason);

    Report createReport(User reportedUser, User reportingUser, Journey journey, String description, String reason);

    Report createReport(User reportedUser, User reportingUser, Event event, String description, String reason);

    Optional<Report> findById(Long id);

    Page<Report> findByUserPaginated(User user, PageParams params);

    long countReportsAgainstUser(User reportedUser);

    Page<Report> findAllPaginated(PageParams params);

    Page<Report> findByStatusPaginated(ReportStatus status, PageParams params);

    void delete(Report report);

    void deleteById(Long id);

    Page<Report> findAll(String search, PageParams params);

    void updateReportStatus(Report report, ReportStatus status);
}
