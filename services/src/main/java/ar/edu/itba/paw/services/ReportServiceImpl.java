package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.*;
import ar.edu.itba.paw.interfaces.services.EventService;
import ar.edu.itba.paw.interfaces.services.JourneyService;
import ar.edu.itba.paw.interfaces.services.ReportService;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.enums.ReportReason;
import ar.edu.itba.paw.models.enums.ReportStatus;
import ar.edu.itba.paw.models.exceptions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;


@Service
@Transactional(readOnly = true)
public class ReportServiceImpl implements ReportService {
    private final ReportDao reportDao;
    private final JourneyService journeyService;
    private final EventService eventService;
    private static final Logger LOGGER = LoggerFactory.getLogger(ReportServiceImpl.class);
    @Autowired
    public ReportServiceImpl(final ReportDao reportDao, final JourneyService journeyService,
                             final EventService eventService) {
        this.reportDao = reportDao;
        this.journeyService = journeyService;
        this.eventService = eventService;
    }

    @Override
    @Transactional
    public Report createReportForJourney(User reportingUser, long journeyId, String description, ReportReason reason) {
        LOGGER.debug("Creating report for journey {} by user {}", journeyId, reportingUser.getId());
        Journey journey = journeyService.getJourneyById(journeyId).orElseThrow(() -> new JourneyNotFoundException(journeyId));
        return reportDao.create(journey.getUser(), reportingUser, journey, description, reason);    }

    @Override
    @Transactional
    public Report createReportForEvent(User reportingUser, long eventId, String description, ReportReason reason) {
        LOGGER.debug("Creating report for event {} by user {}", eventId, reportingUser.getId());
        Event event = eventService.findEventById(eventId).orElseThrow(() -> new EventNotFoundException(eventId));
        return reportDao.create(event.getUser(), reportingUser, event, description, reason);    }

    @Override
    @Transactional
    public Report createReportForEventResponse(User reportingUser, long responseId, String description, ReportReason reason) {
        LOGGER.debug("Creating report for event response {} by user {}", responseId, reportingUser.getId());
        EventResponse eventResponse = eventService.findEventResponseById(responseId)
                .orElseThrow(() -> new EventResponseNotFoundException("Event response not found with id: " + responseId));

        return reportDao.create(eventResponse.getUser(), reportingUser, eventResponse, description, reason);     }

    @Override
    @Transactional
    public Report createReportForJourneyResponse(User reportingUser, long responseId, String description, ReportReason reason) {
        LOGGER.debug("Creating report for journey response {} by user {}", responseId, reportingUser.getId());
        JourneyResponse journeyResponse = journeyService.findJourneyResponseById(responseId)
                .orElseThrow(() -> new JourneyResponseNotFoundException(responseId));

        return reportDao.create(journeyResponse.getUser(), reportingUser, journeyResponse, description, reason);    }

    @Override
    public Optional<Report> findById(Long id) {
        return reportDao.findById(id);
    }

    @Override
    public Page<Report> findByUserPaginated(User user, PageParams params) {
        return reportDao.findByUserPaginated(user, params);
    }

    @Override
    public long countReportsAgainstUser(User reportedUser) {
        return reportDao.countReportsAgainstUser(reportedUser);
    }

    @Override
    public Page<Report> findAllPaginated(PageParams params) {
        return reportDao.findAllPaginated(params);
    }

    @Override
    public Page<Report> findByStatusPaginated(ReportStatus status, PageParams params) {
        return reportDao.findByStatusPaginated(status, params);
    }

    @Transactional
    @Override
    public void delete(Report report) {
        report.setDeleted(true);
        LOGGER.info("Report with id: " + report.getId() + " has been marked as deleted.");
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        Report report = reportDao.findById(id).orElseThrow(() -> {
            LOGGER.error("Report not found with id: " + id);
            return new ReportNotFoundException("Report not found with id: " + id);
                });
        report.setDeleted(true);
        LOGGER.info("Report with id: " + id + " has been marked as deleted.");
    }

    @Override
    public Page<Report> findAll(String search, PageParams params) {
        LOGGER.debug("Searching all reports with term '{}'", search);
        return reportDao.findAll(search, params);
    }

    @Transactional
    @Override
    public Report updateReportStatus(long reportId, ReportStatus status) {
        LOGGER.debug("Updating report {} to status {}", reportId, status);
        Report report = reportDao.findById(reportId)
                .orElseThrow(() -> new ReportNotFoundException(reportId));
        report.setStatus(status);
        return report;

    }


}
