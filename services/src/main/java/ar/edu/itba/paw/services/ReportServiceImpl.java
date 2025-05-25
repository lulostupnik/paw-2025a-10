package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.ReportDao;
import ar.edu.itba.paw.interfaces.services.ReportService;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.enums.ReportStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ReportServiceImpl implements ReportService {
    private final ReportDao reportDao;

    @Autowired
    public ReportServiceImpl(final ReportDao reportDao) {
        this.reportDao = reportDao;
    }

    @Override
    public Report createReport(User reportedUser, User reportingUser, String description, String reason) {
        return reportDao.create(reportedUser, reportingUser, description, reason);
    }

    @Override
    public Report createReport(User reportedUser, User reportingUser, Journey journey, String description, String reason) {
        return reportDao.create(reportedUser, reportingUser, journey, description, reason);
    }

    @Override
    public Report createReport(User reportedUser, User reportingUser, Event event, String description, String reason) {
        return reportDao.create(reportedUser, reportingUser, event, description, reason);
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

    @Override
    public void delete(Report report) {
        reportDao.delete(report);
    }

    @Override
    public void deleteById(Long id) {
        reportDao.deleteById(id);
    }

    @Override
    public Page<Report> findAll(String search, PageParams params) {
        return reportDao.findAll(search, params);
    }

    @Override
    public void updateReportStatus(Report report, ReportStatus status) {
        report.setStatus(status);
    }
}
