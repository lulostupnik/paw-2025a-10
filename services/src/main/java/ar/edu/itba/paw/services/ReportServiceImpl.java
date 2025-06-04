package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.*;
import ar.edu.itba.paw.interfaces.services.EventService;
import ar.edu.itba.paw.interfaces.services.JourneyService;
import ar.edu.itba.paw.interfaces.services.ReportService;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.enums.ReportReason;
import ar.edu.itba.paw.models.enums.ReportStatus;
import ar.edu.itba.paw.models.exceptions.*;
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
        Journey journey = journeyService.getJourneyById(journeyId).orElseThrow(() -> new JourneyNotFoundException("Journey not found with id: " + journeyId));
        return reportDao.create(journey.getUser(), reportingUser, journey, description, reason);    }

    @Override
    @Transactional
    public Report createReportForEvent(User reportingUser, long eventId, String description, ReportReason reason) {
        Event event = eventService.findEventById(eventId).orElseThrow(() -> new EventNotFoundException("Event not found with id: " + eventId));
        return reportDao.create(event.getUser(), reportingUser, event, description, reason);    }

    @Override
    @Transactional
    public Report createReportForEventResponse(User reportingUser, long responseId, String description, ReportReason reason) {
        EventResponse eventResponse = eventService.findEventResponseById(responseId)
                .orElseThrow(() -> new EventResponseNotFoundException("Event response not found with id: " + responseId));

        return reportDao.create(eventResponse.getUser(), reportingUser, eventResponse, description, reason);     }

    @Override
    @Transactional
    public Report createReportForJourneyResponse(User reportingUser, long responseId, String description, ReportReason reason) {
        JourneyResponse journeyResponse = journeyService.findJourneyResponseById(responseId)
                .orElseThrow(() -> new JourneyResponseNotFoundException("Journey response not found with id: " + responseId));

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
        reportDao.delete(report);
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        reportDao.deleteById(id);
    }

    @Override
    public Page<Report> findAll(String search, PageParams params) {
        return reportDao.findAll(search, params);
    }

    @Transactional
    @Override
    public Report updateReportStatus(long reportId, ReportStatus status) {
        Report report = reportDao.findById(reportId)
                .orElseThrow(() -> new ReportNotFoundException("Report not found with id: " + reportId));
        //Si el status es dismissed, no se si haria un delete logico
        //Porque me gustaria que se pueda ver el historial de reportes
        report.setStatus(status);
        return report;

    }


}
