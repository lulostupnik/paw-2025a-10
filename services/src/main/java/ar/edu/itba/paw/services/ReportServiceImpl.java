package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.*;
import ar.edu.itba.paw.interfaces.services.EventService;
import ar.edu.itba.paw.interfaces.services.JourneyService;
import ar.edu.itba.paw.interfaces.services.ReportService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.enums.ReportReason;
import ar.edu.itba.paw.models.enums.ReportStatus;
import ar.edu.itba.paw.models.enums.ReportType;
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
    private final UserService userService;
    private static final Logger LOGGER = LoggerFactory.getLogger(ReportServiceImpl.class);
    @Autowired
    public ReportServiceImpl(final ReportDao reportDao, final JourneyService journeyService,
                             final EventService eventService, final UserService userService) {
        this.reportDao = reportDao;
        this.journeyService = journeyService;
        this.eventService = eventService;
        this.userService = userService;
    }


    @Override
    @Transactional
    public Report createReport(
            ReportType type,
            long reportingUserId,
            long targetId,
            String description,
            ReportReason reason
    ) {
        Optional<User> maybeUser = userService.findUserById(reportingUserId);
        if(maybeUser.isPresent()){
            User reportingUser = maybeUser.get();
            return switch (type) {
                case JOURNEY -> createReportForJourney(reportingUser, targetId, description, reason);
                case EVENT -> createReportForEvent(reportingUser, targetId, description, reason);
                case JOURNEY_RESPONSE -> createReportForJourneyResponse(reportingUser, targetId, description, reason);
                case EVENT_RESPONSE -> createReportForEventResponse(reportingUser, targetId, description, reason);
            };
        }
        throw new UserNotFoundException();

    }


    @Override
    @Transactional
    public Report createReportForJourney(User reportingUser, long journeyId, String description, ReportReason reason) {
        LOGGER.debug("Creating report for journey {} by user {}", journeyId, reportingUser.getId());
        Journey journey = journeyService.findJourneyById(journeyId).orElseThrow(() -> new InvalidReferenceException());
        Report report = reportDao.create(journey.getUser(), reportingUser, journey, description, reason);
        LOGGER.info("Report for journey created, report: {}", report);
        return report;
    }

    @Override
    @Transactional
    public Report createReportForEvent(User reportingUser, long eventId, String description, ReportReason reason) {
        LOGGER.debug("Creating report for event {} by user {}", eventId, reportingUser.getId());
        Event event = eventService.findEventById(eventId).orElseThrow(() -> new InvalidReferenceException());
        Report report =  reportDao.create(event.getUser(), reportingUser, event, description, reason);
        LOGGER.info("Report for event created, report: {}", report);
        return report;
    }

    @Override
    @Transactional
    public Report createReportForEventResponse(User reportingUser, long responseId, String description, ReportReason reason) {
        LOGGER.debug("Creating report for event response {} by user {}", responseId, reportingUser.getId());
        EventResponse eventResponse = eventService.findEventResponseById(responseId)
                .orElseThrow(() -> new InvalidReferenceException());

        Report report = reportDao.create(eventResponse.getUser(), reportingUser, eventResponse, description, reason);
        LOGGER.info("Report for event response created, report: {}", report);
        return report;
    }

    @Override
    @Transactional
    public Report createReportForJourneyResponse(User reportingUser, long responseId, String description, ReportReason reason) {
        LOGGER.debug("Creating report for journey response {} by user {}", responseId, reportingUser.getId());
        JourneyResponse journeyResponse = journeyService.findJourneyResponseById(responseId)
                .orElseThrow(() -> new InvalidReferenceException());
        Report report =reportDao.create(journeyResponse.getUser(), reportingUser, journeyResponse, description, reason);
        LOGGER.info("Report for journey response created, reponse: {}", report);
        return  report; }

    @Override
    public Optional<Report> findById(long id) {
        return reportDao.findById(id);
    }


    @Transactional
    @Override
    public void deleteById(long id) {
        Optional<Report> maybeReport = reportDao.findById(id);
        if (maybeReport.isEmpty()) {
            LOGGER.info("Report with id {} not found.", id);
            return;
        }

        maybeReport.get().setDeleted(true);
        LOGGER.info("Report with id {} has been marked as deleted.", id);
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
                .orElseThrow(() -> new ReportNotFoundException());
        report.setStatus(status);
        LOGGER.info("Report with id {} updated to status {}", reportId, status);
        return report;

    }


}
