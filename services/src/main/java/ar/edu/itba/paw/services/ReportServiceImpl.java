package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.*;
import ar.edu.itba.paw.interfaces.services.EventService;
import ar.edu.itba.paw.interfaces.services.JourneyService;
import ar.edu.itba.paw.interfaces.services.ReportService;
import ar.edu.itba.paw.models.*;
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

    @Transactional
    @Override
    public Report createReport(User reporter, String reportType, long targetId, String reason, String description) {
        return switch (reportType) {
            case "JOURNEY" -> {
                Journey journey = journeyService.getJourneyById(targetId).orElseThrow(() -> new JourneyNotFoundException("Journey not found with id: " + targetId));
                yield reportDao.create(journey.getUser(), reporter, journey, description, reason);
            }
            case "EVENT" -> {
                Event event = eventService.findEventById(targetId).orElseThrow(() -> new EventNotFoundException("Event not found with id: " + targetId));
                yield reportDao.create(event.getUser(), reporter, event, description, reason);
            }
            case "JOURNEY_RESPONSE" -> {
                JourneyResponse jr = journeyService.findJourneyResponseById(targetId).orElseThrow(() -> new JourneyResponseNotFoundException("Journey response not found with id: " + targetId));
                yield reportDao.create(jr.getUser(), reporter, jr, description, reason);
            }
            case "EVENT_RESPONSE" -> {
                EventResponse er = eventService.findEventResponseById(targetId).orElseThrow(() -> new EventResponseNotFoundException("Event response not found with id: " + targetId));
                yield reportDao.create(er.getUser(), reporter, er, description, reason);
            }
            default -> throw new IllegalArgumentException("Unknown report type");
        };
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
                .orElseThrow(() -> new ReportNotFoundException("Report not found with id: " + reportId));
        //Si el status es dismissed, no se si haria un delete logico
        //Porque me gustaria que se pueda ver el historial de reportes
        report.setStatus(status);
        return report;

    }


}
