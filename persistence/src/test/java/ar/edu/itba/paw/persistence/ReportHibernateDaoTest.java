package ar.edu.itba.paw.persistence;

import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.sql.DataSource;

import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.enums.ReportStatus;
import ar.edu.itba.paw.persistence.config.TestConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;
import static ar.edu.itba.paw.persistence.TestUtils.*;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class ReportHibernateDaoTest {

    @Autowired
    private DataSource ds;

    @Autowired
    private ReportHibernateDao reportDao;
        
    @PersistenceContext
    private EntityManager em;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp(){
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Test
    public void testCreateGenericReport(){
        Report report = reportDao.create(
            USER_1, 
            USER_2, 
            REPORT_USER_DESC, 
            HARASSMENT
        );
        em.flush();

        assertNotNull(report);
        assertEqualsReport(
            new Report(
                null, 
                USER_1, 
                USER_2, 
                REPORT_USER_DESC, 
                HARASSMENT, 
                null, 
                null, 
                null, 
                null, 
                false, 
                ReportStatus.PENDING), 
            report
        );
    }
    @Test(expected = PersistenceException.class)
    public void testCreateGenericReportInvalidReportedUser(){
        reportDao.create(
            new User(
                12341234l, 
                null, 
                null, 
                null, 
                null, 
                null, 
                null, 
                0, 
                null, 
                false, 
                false), 
            USER_2, 
            REPORT_USER_DESC, 
            HARASSMENT
        );
        em.flush();
    }
    @Test(expected = PersistenceException.class)
    public void testCreateGenericReportInvalidReportingUser(){
        reportDao.create(
            USER_1, 
            new User(
                12341234l, 
                null, 
                null, 
                null, 
                null, 
                null, 
                null, 
                0, 
                null, 
                false, 
                false), 
            REPORT_USER_DESC, 
            HARASSMENT
        );
        em.flush();
    }
    @Test(expected = PersistenceException.class)
    public void testCreateGenericReportMissingReportedUser(){
        reportDao.create(
            null, 
            USER_2, 
            REPORT_USER_DESC, 
            HARASSMENT
        );
        em.flush();
    }
    @Test(expected = PersistenceException.class)
    public void testCreateGenericReportMissingReportingUser(){
        reportDao.create(
            USER_2, 
            null, 
            REPORT_USER_DESC, 
            HARASSMENT
        );
        em.flush();
    }
    @Test(expected = PersistenceException.class)
    public void testCreateGenericReportMissingDescription(){
        reportDao.create(
            USER_2, 
            USER_1, 
            null,
            HARASSMENT
        );
        em.flush();
    }
    @Test(expected = PersistenceException.class)
    public void testCreateGenericReportMissingReason(){
        reportDao.create(
            USER_2, 
            USER_1, 
            REPORT_USER_DESC, 
            null
        );
        em.flush();
    }

    @Test
    public void testCreateJourneyReport(){
        Report report = reportDao.create(
            USER_1, 
            USER_2, 
            JOURNEY_1, 
            REPORT_JOURNEY_DESC, 
            HARASSMENT
        );
        em.flush();

        assertNotNull(report);
        assertEqualsReport(
            new Report(
                null, 
                USER_1, 
                USER_2, 
                REPORT_JOURNEY_DESC, 
                HARASSMENT, 
                JOURNEY_1, 
                null, 
                null, 
                null, 
                false, 
                ReportStatus.PENDING), 
            report
        );
    }
    @Test(expected = PersistenceException.class)
    public void testCreateJourneyReportInvalidReportedUser(){
        reportDao.create(
            new User(
                12341234l, 
                null, 
                null, 
                null, 
                null, 
                null, 
                null, 
                0, 
                null, 
                false, 
                false), 
            USER_2, 
            JOURNEY_1, 
            REPORT_JOURNEY_DESC, 
            HARASSMENT
        );
        em.flush();
    }
    @Test(expected = PersistenceException.class)
    public void testCreateJourneyReportInvalidReportingUser(){
        reportDao.create(
            USER_1, 
            new User(
                12341234l, 
                null, 
                null, 
                null, 
                null, 
                null, 
                null, 
                0, 
                null, 
                false, 
                false), 
            JOURNEY_1, 
            REPORT_JOURNEY_DESC, 
            HARASSMENT
        );
        em.flush();
    }
    @Test(expected = PersistenceException.class)
    public void testCreateJourneyReportMissingReportedUser(){
        reportDao.create(
            null, 
            USER_2, 
            JOURNEY_1, 
            REPORT_JOURNEY_DESC, 
            HARASSMENT
        );
        em.flush();
    }
    @Test(expected = PersistenceException.class)
    public void testCreateJourneyReportMissingReportingUser(){
        reportDao.create(
            USER_2, 
            null, 
            JOURNEY_1, 
            REPORT_JOURNEY_DESC, 
            HARASSMENT
        );
        em.flush();
    }
    @Test(expected = PersistenceException.class)
    public void testCreateJourneyReportMissingDescription(){
        reportDao.create(
            USER_2, 
            USER_1, 
            JOURNEY_1, 
            null, 
            HARASSMENT
        );
        em.flush();
    }
    @Test(expected = PersistenceException.class)
    public void testCreateJourneyReportMissingReason(){
        reportDao.create(
            USER_2, 
            USER_1, 
            JOURNEY_1, 
            REPORT_JOURNEY_DESC, 
            null
        );
        em.flush();
    }
    @Test(expected = PersistenceException.class)
    public void testCreateJourneyReportInvalidJourney(){
        reportDao.create(
            USER_2, 
            USER_1, 
            new Journey(
                12341234l, 
                null, 
                null, 
                null, 
                null, 
                null), 
            REPORT_JOURNEY_DESC, 
            HARASSMENT
        );
        em.flush();
    }

    @Test
    public void testCreateJourneyResponseReport(){
        Report report = reportDao.create(
            USER_1, 
            USER_2, 
            JOURNEY_RESPONSE_1, 
            REPORT_JOURNEY_RESPONSE_DESC, 
            HARASSMENT
        );
        em.flush();

        assertNotNull(report);
        assertEqualsReport(
            new Report(
                null, 
                USER_1, 
                USER_2, 
                REPORT_JOURNEY_RESPONSE_DESC, 
                HARASSMENT, 
                null, 
                null, 
                null, 
                JOURNEY_RESPONSE_1, 
                false, 
                ReportStatus.PENDING), 
            report
        );
    }
    @Test(expected = PersistenceException.class)
    public void testCreateJourneyResponseReportInvalidReportedUser(){
        reportDao.create(
            new User(
                12341234l, 
                null, 
                null, 
                null, 
                null, 
                null, 
                null, 
                0, 
                null, 
                false, 
                false), 
            USER_2, 
            JOURNEY_RESPONSE_1, 
            REPORT_JOURNEY_RESPONSE_DESC, 
            HARASSMENT
        );
        em.flush();
    }
    @Test(expected = PersistenceException.class)
    public void testCreateJourneyResponseReportInvalidReportingUser(){
        reportDao.create(
            USER_1, 
            new User(
                12341234l, 
                null, 
                null, 
                null, 
                null, 
                null, 
                null, 
                0, 
                null, 
                false, 
                false), 
            JOURNEY_RESPONSE_1, 
            REPORT_JOURNEY_RESPONSE_DESC, 
            HARASSMENT
        );
        em.flush();
    }
    @Test(expected = PersistenceException.class)
    public void testCreateJourneyResponseReportMissingReportedUser(){
        reportDao.create(
            null, 
            USER_2, 
            JOURNEY_RESPONSE_1, 
            REPORT_JOURNEY_RESPONSE_DESC, 
            HARASSMENT
        );
        em.flush();
    }
    @Test(expected = PersistenceException.class)
    public void testCreateJourneyResponseReportMissingReportingUser(){
        reportDao.create(
            USER_2, 
            null, 
            JOURNEY_RESPONSE_1, 
            REPORT_JOURNEY_RESPONSE_DESC, 
            HARASSMENT
        );
        em.flush();
    }
    @Test(expected = PersistenceException.class)
    public void testCreateJourneyResponseReportMissingDescription(){
        reportDao.create(
            USER_2, 
            USER_1, 
            JOURNEY_RESPONSE_1, 
            null, 
            HARASSMENT
        );
        em.flush();
    }
    @Test(expected = PersistenceException.class)
    public void testCreateJourneyResponseReportMissingReason(){
        reportDao.create(
            USER_2, 
            USER_1, 
            JOURNEY_RESPONSE_1, 
            REPORT_JOURNEY_RESPONSE_DESC, 
            null
        );
        em.flush();
    }

    @Test
    public void testCreateEventReport(){
        Report report = reportDao.create(
            USER_1, 
            USER_2, 
            EVENT_1, 
            REPORT_EVENT_DESC, 
            HARASSMENT
        );
        em.flush();

        assertNotNull(report);
        assertEqualsReport(
            new Report(
                null, 
                USER_1, 
                USER_2, 
                REPORT_EVENT_DESC, 
                HARASSMENT, 
                null, 
                EVENT_1, 
                null, 
                null, 
                false, 
                ReportStatus.PENDING), 
            report
        );
    }
    @Test(expected = PersistenceException.class)
    public void testCreateEventReportInvalidReportedUser(){
        reportDao.create(
            new User(
                12341234l, 
                null, 
                null, 
                null, 
                null, 
                null, 
                null, 
                0, 
                null, 
                false, 
                false), 
            USER_2, 
            EVENT_1, 
            REPORT_EVENT_DESC, 
            HARASSMENT
        );
        em.flush();
    }
    @Test(expected = PersistenceException.class)
    public void testCreateEventReportInvalidReportingUser(){
        reportDao.create(
            USER_1, 
            new User(
                12341234l, 
                null, 
                null, 
                null, 
                null, 
                null, 
                null, 
                0, 
                null, 
                false, 
                false), 
            EVENT_1, 
            REPORT_EVENT_DESC, 
            HARASSMENT
        );
        em.flush();
    }
    @Test(expected = PersistenceException.class)
    public void testCreateEventReportMissingReportedUser(){
        reportDao.create(
            null, 
            USER_2, 
            EVENT_1, 
            REPORT_EVENT_DESC, 
            HARASSMENT
        );
        em.flush();
    }
    @Test(expected = PersistenceException.class)
    public void testCreateEventReportMissingReportingUser(){
        reportDao.create(
            USER_2, 
            null, 
            EVENT_1, 
            REPORT_EVENT_DESC, 
            HARASSMENT
        );
        em.flush();
    }
    @Test(expected = PersistenceException.class)
    public void testCreateEventReportMissingDescription(){
        reportDao.create(
            USER_2, 
            USER_1, 
            EVENT_1, 
            null, 
            HARASSMENT
        );
        em.flush();
    }
    @Test(expected = PersistenceException.class)
    public void testCreateEventReportMissingReason(){
        reportDao.create(
            USER_2, 
            USER_1, 
            EVENT_1, 
            REPORT_EVENT_DESC, 
            null
        );
        em.flush();
    }
    @Test(expected = PersistenceException.class)
    public void testCreateEventReportInvalidEvent(){
        reportDao.create(
            USER_2, 
            USER_1, 
            new Event(
                12341234l, 
                null, 
                null, 
                null, 
                0, 
                null, 
                null, 
                null, 
                null, 
                null), 
            REPORT_EVENT_DESC, 
            HARASSMENT
        );
        em.flush();
    }

    @Test
    public void testCreateEventResponseReport(){
        Report report = reportDao.create(
            USER_1, 
            USER_2, 
            EVENT_RESPONSE_1, 
            REPORT_EVENT_RESPONSE_DESC, 
            HARASSMENT
        );
        em.flush();

        assertNotNull(report);
        assertEqualsReport(
            new Report(
                null, 
                USER_1, 
                USER_2, 
                REPORT_EVENT_RESPONSE_DESC, 
                HARASSMENT, 
                null, 
                null, 
                EVENT_RESPONSE_1, 
                null, 
                false, 
                ReportStatus.PENDING), 
            report
        );
    }
    @Test(expected = PersistenceException.class)
    public void testCreateEventResponseReportInvalidReportedUser(){
        reportDao.create(
            new User(
                12341234l, 
                null, 
                null, 
                null, 
                null, 
                null, 
                null, 
                0, 
                null, 
                false, 
                false), 
            USER_2, 
            EVENT_RESPONSE_1, 
            REPORT_EVENT_RESPONSE_DESC, 
            HARASSMENT
        );
        em.flush();
    }
    @Test(expected = PersistenceException.class)
    public void testCreateEventResponseReportInvalidReportingUser(){
        reportDao.create(
            USER_1, 
            new User(
                12341234l, 
                null, 
                null, 
                null, 
                null, 
                null, 
                null, 
                0, 
                null, 
                false, 
                false), 
            EVENT_RESPONSE_1, 
            REPORT_EVENT_RESPONSE_DESC, 
            HARASSMENT
        );
        em.flush();
    }
    @Test(expected = PersistenceException.class)
    public void testCreateEventResponseReportMissingReportedUser(){
        reportDao.create(
            null, 
            USER_2, 
            EVENT_RESPONSE_1, 
            REPORT_EVENT_RESPONSE_DESC, 
            HARASSMENT
        );
        em.flush();
    }
    @Test(expected = PersistenceException.class)
    public void testCreateEventResponseReportMissingReportingUser(){
        reportDao.create(
            USER_2, 
            null, 
            EVENT_RESPONSE_1, 
            REPORT_EVENT_RESPONSE_DESC, 
            HARASSMENT
        );
        em.flush();
    }
    @Test(expected = PersistenceException.class)
    public void testCreateEventResponseReportMissingDescription(){
        reportDao.create(
            USER_2, 
            USER_1, 
            EVENT_RESPONSE_1, 
            null, 
            HARASSMENT
        );
        em.flush();
    }
    @Test(expected = PersistenceException.class)
    public void testCreateEventResponseReportMissingReason(){
        reportDao.create(
            USER_2,
            USER_1, 
            EVENT_RESPONSE_1, 
            REPORT_EVENT_RESPONSE_DESC, 
            null
        );
        em.flush();
    }

    @Test
    public void testFindById(){
        Optional<Report> maybeReport = reportDao.findById(REPORT_USER_ID);

        assertNotNull(maybeReport);
        assertTrue(maybeReport.isPresent());
        assertEqualsReport(REPORT_USER, maybeReport.get());
    }
    @Test
    public void testFindByIdMissing(){
        Optional<Report> maybeReport = reportDao.findById(12341234l);

        assertNotNull(maybeReport);
        assertFalse(maybeReport.isPresent());
    }

    @Test
    public void testCountReportsAgainstUser(){
        long reports = reportDao.countReportsAgainstUser(USER_2);

        assertEquals(USER_2_REPORTS, reports);
    }
    @Test
    public void testCountReportsAgainstUserNoReports(){
        long reports = reportDao.countReportsAgainstUser(USER_1);

        assertEquals(0, reports);
    }
    @Test
    public void testCountReportsAgainstUserMissing(){
        long reports = reportDao.countReportsAgainstUser(
            new User(
                12341234l, 
                null, 
                null, 
                null, 
                null, 
                null, 
                null, 
                0, 
                null, 
                false, 
                false
            )
        );

        assertEquals(0, reports);
    }

    @Test
    public void testFindAllPaginated(){
        Page<Report> reports = reportDao.findAllPaginated(PAGE_1_BIG);
        
        assertNotNull(reports);
        assertEquals(1, reports.getCurrentPage());
        assertEquals(1, reports.getTotalPages());
        assertEquals(TOTAL_REPORTS, reports.getContent().size());
    }
    @Test
    public void testFindAllPaginatedNoReports(){
        JdbcTestUtils.deleteFromTables(jdbcTemplate, REPORT_TABLE);

        Page<Report> reports = reportDao.findAllPaginated(PAGE_1_BIG);
        
        assertNotNull(reports);
        assertEquals(1, reports.getCurrentPage());
        assertEquals(0, reports.getTotalPages());
        assertEquals(0, reports.getContent().size());
    }
    
    @Test
    public void testFindByStatusPaginated(){
        Page<Report> reports = reportDao.findByStatusPaginated(ReportStatus.PENDING, PAGE_1_BIG);

        assertNotNull(reports);
        assertEquals(1, reports.getCurrentPage());
        assertEquals(1, reports.getTotalPages());
        assertEquals(REPORTS_PENDING, reports.getContent().size());
        for (Report r : reports.getContent()){
            assertEqualsReport(REPORT_PENDING_DATA.get(r.getId()), r);
        }
    }
    @Test
    public void testFindByStatusPaginatedResolved(){
        Page<Report> reports = reportDao.findByStatusPaginated(ReportStatus.RESOLVED, PAGE_1_BIG);

        assertNotNull(reports);
        assertEquals(1, reports.getCurrentPage());
        assertEquals(1, reports.getTotalPages());
        assertEquals(REPORTS_RESOLVED, reports.getContent().size());
        assertEqualsReport(REPORT_USER_RESOLVED, reports.getContent().getFirst());
    }
    @Test
    public void testFindByStatusPaginatedUnderReview(){
        Page<Report> reports = reportDao.findByStatusPaginated(
            ReportStatus.UNDER_REVIEW, PAGE_1_BIG
        );

        assertNotNull(reports);
        assertEquals(1, reports.getCurrentPage());
        assertEquals(1, reports.getTotalPages());
        assertEquals(REPORTS_UNDER_REVIEW, reports.getContent().size());
        assertEqualsReport(REPORT_USER_UNDER_REVIEW, reports.getContent().getFirst());
    }
    @Test
    public void testFindByStatusPaginatedDismissed(){
        Page<Report> reports = reportDao.findByStatusPaginated(
            ReportStatus.DISMISSED, PAGE_1_BIG
        );

        assertNotNull(reports);
        assertEquals(1, reports.getCurrentPage());
        assertEquals(1, reports.getTotalPages());
        assertEquals(REPORTS_DISMISSED, reports.getContent().size());
        assertEqualsReport(REPORT_USER_DISMISSED, reports.getContent().getFirst());
    }
    @Test
    public void testFindByStatusPaginatedNoReports(){
        JdbcTestUtils.deleteFromTables(jdbcTemplate, REPORT_TABLE);

        Page<Report> reports = reportDao.findByStatusPaginated(ReportStatus.PENDING, PAGE_1_BIG);

        assertNotNull(reports);
        assertEquals(1, reports.getCurrentPage());
        assertEquals(0, reports.getTotalPages());
        assertEquals(0, reports.getContent().size());
    }

    @Test
    public void testFindByUserPaginated(){
        Page<Report> reports = reportDao.findByUserPaginated(USER_1, PAGE_1_BIG);

        assertNotNull(reports);
        assertEquals(1, reports.getCurrentPage());
        assertEquals(1, reports.getTotalPages());
        assertEquals(USER_1_REPORTS_AUTHORED, reports.getContent().size());
    }
    @Test
    public void testFindByUserPaginatedNoReports(){
        Page<Report> reports = reportDao.findByUserPaginated(USER_2, PAGE_1_BIG);

        assertNotNull(reports);
        assertEquals(1, reports.getCurrentPage());
        assertEquals(0, reports.getTotalPages());
        assertEquals(0, reports.getContent().size());
    }

    @Test
    public void testFindAllByDescOrReason(){
        Page<Report> reports = reportDao.findAll(REPORT_EVENT_DESC, PAGE_1_BIG);

        assertNotNull(reports);
        assertEquals(1, reports.getCurrentPage());
        assertEquals(1, reports.getTotalPages());
        assertEquals(1, reports.getContent().size());
        assertEqualsReport(REPORT_EVENT, reports.getContent().getFirst());
    }
    @Test
    public void testFindAllByReportingUsername(){
        Page<Report> reports = reportDao.findAll(
            REPORTING_USER.getUsername(), PAGE_1_BIG
        );

        assertNotNull(reports);
        assertEquals(1, reports.getCurrentPage());
        assertEquals(1, reports.getTotalPages());
        assertEquals(USER_1_REPORTS_AUTHORED, reports.getContent().size());
    }
    @Test
    public void testFindAllByReportedUsername(){
        Page<Report> reports = reportDao.findAll(
            REPORT_USER_REPORTED_USER.getUsername(), PAGE_1_BIG
        );

        assertNotNull(reports);
        assertEquals(1, reports.getCurrentPage());
        assertEquals(1, reports.getTotalPages());
        assertEquals(USER_3_REPORTS, reports.getContent().size());
    }

}