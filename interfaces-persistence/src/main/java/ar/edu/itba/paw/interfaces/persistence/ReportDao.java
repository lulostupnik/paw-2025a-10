package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.enums.ReportReason;
import java.util.Optional;

public interface ReportDao {
    Report create(User reportedUser, User reportingUser, String description, ReportReason reason);
    Report create(User reportedUser, User reportingUser, Journey journey, String description, ReportReason reason);
    Report create(User reportedUser, User reportingUser, Event event, String description, ReportReason reason);
    Report create(User reportedUser, User reportingUser, EventResponse eventResponse, String description, ReportReason reason);
    Report create(User reportedUser, User reportingUser, JourneyResponse eventResponse, String description, ReportReason reason);
    Optional<Report> findById(Long id);
    void hardDeleteByJourneyId(long journeyId);
    Page<Report> findAll(String search, PageParams params);
}