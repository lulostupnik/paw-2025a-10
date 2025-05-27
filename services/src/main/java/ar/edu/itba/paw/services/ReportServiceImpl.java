package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.*;
import ar.edu.itba.paw.interfaces.services.EventService;
import ar.edu.itba.paw.interfaces.services.JourneyService;
import ar.edu.itba.paw.interfaces.services.ReportService;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.enums.ReportStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
@Transactional(readOnly = true)
public class ReportServiceImpl implements ReportService {
    private final ReportDao reportDao;
    //Hay que cambiarlos a servicios pero nada por ahoar asi
    private final JourneyDao journeyDao;
    private final EventDao eventDao;
    private final EventResponseDao eventResponseDao;
    private final JourneyResponseDao journeyResponseDao;

    @Autowired
    public ReportServiceImpl(final ReportDao reportDao, final JourneyDao journeyDao,
                             final EventDao eventDao, final EventResponseDao eventResponseDao
                            , final JourneyResponseDao journeyResponseDao) {
        this.reportDao = reportDao;
        this.journeyDao = journeyDao;
        this.eventDao = eventDao;
        this.eventResponseDao = eventResponseDao;
        this.journeyResponseDao = journeyResponseDao;
    }

    @Transactional
    @Override
    public Report createReportForJourney(User reportingUser, long journeyId, String description, String reason) {
        Journey journey = journeyDao.findById(journeyId)
                .orElseThrow(() -> new IllegalArgumentException("Journey not found with id: " + journeyId));

        return reportDao.create(journey.getUser(), reportingUser,journey,description,reason); // Assuming `reportDao.save` persists and returns the entity
    }

    @Transactional
    @Override
    public Report createReportForEvent(User reportingUser, long eventId, String description, String reason) {
        Event event = eventDao.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found with id: " + eventId));

        return reportDao.create(event.getUser(), reportingUser, event, description, reason);
    }

    @Transactional
    @Override
    public Report createReportForEventResponse(User reportingUser, long responseId, String description, String reason) {
        EventResponse eventResponse = eventResponseDao.findById(responseId)
                .orElseThrow(() -> new IllegalArgumentException("Event response not found with id: " + responseId));

        return reportDao.create(eventResponse.getUser(), reportingUser, eventResponse.getEvent(), description, reason);
    }

    @Transactional
    @Override
    public Report createReportForJourneyResponse(User reportingUser, long responseId, String description, String reason) {
        JourneyResponse journeyResponse = journeyResponseDao.findById(responseId)
                .orElseThrow(() -> new IllegalArgumentException("Journey response not found with id: " + responseId));

        return reportDao.create(journeyResponse.getUser(), reportingUser, journeyResponse.getJourney(), description, reason);
    }


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
                .orElseThrow(() -> new IllegalArgumentException("Report not found with id: " + reportId));
        //Si el status es dismissed, no se si haria un delete logico
        //Porque me gustaria que se pueda ver el historial de reportes
        report.setStatus(status);
        return report;

    }


}
