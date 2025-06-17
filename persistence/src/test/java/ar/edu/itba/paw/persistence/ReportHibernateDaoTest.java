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
        Report report = reportDao.create(TestUtils.USER_1, TestUtils.USER_2, TestUtils.REPORT_USER_DESC, TestUtils.HARASSMENT);
        em.flush();

        assertNotNull(report);
        TestUtils.assertEqualsReport(new Report(null, TestUtils.USER_1, TestUtils.USER_2, TestUtils.REPORT_USER_DESC, TestUtils.HARASSMENT, null, null, null, null, false, ReportStatus.PENDING), report);
    }
    @Test(expected = PersistenceException.class)
    public void testCreateGenericReportInvalidReportedUser(){
        reportDao.create(new User(12341234l, null, null, null, null, null, null, 0, null, false, false), TestUtils.USER_2, TestUtils.REPORT_USER_DESC, TestUtils.HARASSMENT);
        em.flush();
    }
    @Test(expected = PersistenceException.class)
    public void testCreateGenericReportInvalidReportingUser(){
        reportDao.create(TestUtils.USER_1, new User(12341234l, null, null, null, null, null, null, 0, null, false, false), TestUtils.REPORT_USER_DESC, TestUtils.HARASSMENT);
        em.flush();
    }
    @Test(expected = PersistenceException.class)
    public void testCreateGenericReportMissingReportedUser(){
        reportDao.create(null, TestUtils.USER_2, TestUtils.REPORT_USER_DESC, TestUtils.HARASSMENT);
        em.flush();
    }
    @Test(expected = PersistenceException.class)
    public void testCreateGenericReportMissingReportingUser(){
        reportDao.create(TestUtils.USER_2, null, TestUtils.REPORT_USER_DESC, TestUtils.HARASSMENT);
        em.flush();
    }
    @Test(expected = PersistenceException.class)
    public void testCreateGenericReportMissingDescription(){
        reportDao.create(TestUtils.USER_2, TestUtils.USER_1, null, TestUtils.HARASSMENT);
        em.flush();
    }
    @Test(expected = PersistenceException.class)
    public void testCreateGenericReportMissingReason(){
        reportDao.create(TestUtils.USER_2, TestUtils.USER_1, TestUtils.REPORT_USER_DESC, null);
        em.flush();
    }

    @Test
    public void testCreateJourneyReport(){
        Report report = reportDao.create(TestUtils.USER_1, TestUtils.USER_2, TestUtils.JOURNEY_1, TestUtils.REPORT_JOURNEY_DESC, TestUtils.HARASSMENT);
        em.flush();

        assertNotNull(report);
        TestUtils.assertEqualsReport(new Report(null, TestUtils.USER_1, TestUtils.USER_2, TestUtils.REPORT_JOURNEY_DESC, TestUtils.HARASSMENT, TestUtils.JOURNEY_1, null, null, null, false, ReportStatus.PENDING), report);
    }
    @Test(expected = PersistenceException.class)
    public void testCreateJourneyReportInvalidReportedUser(){
        reportDao.create(new User(12341234l, null, null, null, null, null, null, 0, null, false, false), TestUtils.USER_2, TestUtils.JOURNEY_1, TestUtils.REPORT_JOURNEY_DESC, TestUtils.HARASSMENT);
        em.flush();
    }
    @Test(expected = PersistenceException.class)
    public void testCreateJourneyReportInvalidReportingUser(){
        reportDao.create(TestUtils.USER_1, new User(12341234l, null, null, null, null, null, null, 0, null, false, false), TestUtils.JOURNEY_1, TestUtils.REPORT_JOURNEY_DESC, TestUtils.HARASSMENT);
        em.flush();
    }
    @Test(expected = PersistenceException.class)
    public void testCreateJourneyReportMissingReportedUser(){
        reportDao.create(null, TestUtils.USER_2, TestUtils.JOURNEY_1, TestUtils.REPORT_JOURNEY_DESC, TestUtils.HARASSMENT);
        em.flush();
    }
    @Test(expected = PersistenceException.class)
    public void testCreateJourneyReportMissingReportingUser(){
        reportDao.create(TestUtils.USER_2, null, TestUtils.JOURNEY_1, TestUtils.REPORT_JOURNEY_DESC, TestUtils.HARASSMENT);
        em.flush();
    }
    @Test(expected = PersistenceException.class)
    public void testCreateJourneyReportMissingDescription(){
        reportDao.create(TestUtils.USER_2, TestUtils.USER_1, TestUtils.JOURNEY_1, null, TestUtils.HARASSMENT);
        em.flush();
    }
    @Test(expected = PersistenceException.class)
    public void testCreateJourneyReportMissingReason(){
        reportDao.create(TestUtils.USER_2, TestUtils.USER_1, TestUtils.JOURNEY_1, TestUtils.REPORT_JOURNEY_DESC, null);
        em.flush();
    }
    @Test(expected = PersistenceException.class)
    public void testCreateJourneyReportInvalidJourney(){
        reportDao.create(TestUtils.USER_2, TestUtils.USER_1, new Journey(12341234l, null, null, null, null, null), TestUtils.REPORT_JOURNEY_DESC, TestUtils.HARASSMENT);
        em.flush();
    }

    @Test
    public void testCreateJourneyResponseReport(){
        Report report = reportDao.create(TestUtils.USER_1, TestUtils.USER_2, TestUtils.JOURNEY_RESPONSE_1, TestUtils.REPORT_JOURNEY_RESPONSE_DESC, TestUtils.HARASSMENT);
        em.flush();

        assertNotNull(report);
        TestUtils.assertEqualsReport(new Report(null, TestUtils.USER_1, TestUtils.USER_2, TestUtils.REPORT_JOURNEY_RESPONSE_DESC, TestUtils.HARASSMENT, null, null, null, TestUtils.JOURNEY_RESPONSE_1, false, ReportStatus.PENDING), report);
    }
    @Test(expected = PersistenceException.class)
    public void testCreateJourneyResponseReportInvalidReportedUser(){
        reportDao.create(new User(12341234l, null, null, null, null, null, null, 0, null, false, false), TestUtils.USER_2, TestUtils.JOURNEY_RESPONSE_1, TestUtils.REPORT_JOURNEY_RESPONSE_DESC, TestUtils.HARASSMENT);
        em.flush();
    }
    @Test(expected = PersistenceException.class)
    public void testCreateJourneyResponseReportInvalidReportingUser(){
        reportDao.create(TestUtils.USER_1, new User(12341234l, null, null, null, null, null, null, 0, null, false, false), TestUtils.JOURNEY_RESPONSE_1, TestUtils.REPORT_JOURNEY_RESPONSE_DESC, TestUtils.HARASSMENT);
        em.flush();
    }
    @Test(expected = PersistenceException.class)
    public void testCreateJourneyResponseReportMissingReportedUser(){
        reportDao.create(null, TestUtils.USER_2, TestUtils.JOURNEY_RESPONSE_1, TestUtils.REPORT_JOURNEY_RESPONSE_DESC, TestUtils.HARASSMENT);
        em.flush();
    }
    @Test(expected = PersistenceException.class)
    public void testCreateJourneyResponseReportMissingReportingUser(){
        reportDao.create(TestUtils.USER_2, null, TestUtils.JOURNEY_RESPONSE_1, TestUtils.REPORT_JOURNEY_RESPONSE_DESC, TestUtils.HARASSMENT);
        em.flush();
    }
    @Test(expected = PersistenceException.class)
    public void testCreateJourneyResponseReportMissingDescription(){
        reportDao.create(TestUtils.USER_2, TestUtils.USER_1, TestUtils.JOURNEY_RESPONSE_1, null, TestUtils.HARASSMENT);
        em.flush();
    }
    @Test(expected = PersistenceException.class)
    public void testCreateJourneyResponseReportMissingReason(){
        reportDao.create(TestUtils.USER_2, TestUtils.USER_1, TestUtils.JOURNEY_RESPONSE_1, TestUtils.REPORT_JOURNEY_RESPONSE_DESC, null);
        em.flush();
    }

    @Test
    public void testCreateEventReport(){
        Report report = reportDao.create(TestUtils.USER_1, TestUtils.USER_2, TestUtils.EVENT_1, TestUtils.REPORT_EVENT_DESC, TestUtils.HARASSMENT);
        em.flush();

        assertNotNull(report);
        TestUtils.assertEqualsReport(new Report(null, TestUtils.USER_1, TestUtils.USER_2, TestUtils.REPORT_EVENT_DESC, TestUtils.HARASSMENT, null, TestUtils.EVENT_1, null, null, false, ReportStatus.PENDING), report);
    }
    @Test(expected = PersistenceException.class)
    public void testCreateEventReportInvalidReportedUser(){
        reportDao.create(new User(12341234l, null, null, null, null, null, null, 0, null, false, false), TestUtils.USER_2, TestUtils.EVENT_1, TestUtils.REPORT_EVENT_DESC, TestUtils.HARASSMENT);
        em.flush();
    }
    @Test(expected = PersistenceException.class)
    public void testCreateEventReportInvalidReportingUser(){
        reportDao.create(TestUtils.USER_1, new User(12341234l, null, null, null, null, null, null, 0, null, false, false), TestUtils.EVENT_1, TestUtils.REPORT_EVENT_DESC, TestUtils.HARASSMENT);
        em.flush();
    }
    @Test(expected = PersistenceException.class)
    public void testCreateEventReportMissingReportedUser(){
        reportDao.create(null, TestUtils.USER_2, TestUtils.EVENT_1, TestUtils.REPORT_EVENT_DESC, TestUtils.HARASSMENT);
        em.flush();
    }
    @Test(expected = PersistenceException.class)
    public void testCreateEventReportMissingReportingUser(){
        reportDao.create(TestUtils.USER_2, null, TestUtils.EVENT_1, TestUtils.REPORT_EVENT_DESC, TestUtils.HARASSMENT);
        em.flush();
    }
    @Test(expected = PersistenceException.class)
    public void testCreateEventReportMissingDescription(){
        reportDao.create(TestUtils.USER_2, TestUtils.USER_1, TestUtils.EVENT_1, null, TestUtils.HARASSMENT);
        em.flush();
    }
    @Test(expected = PersistenceException.class)
    public void testCreateEventReportMissingReason(){
        reportDao.create(TestUtils.USER_2, TestUtils.USER_1, TestUtils.EVENT_1, TestUtils.REPORT_EVENT_DESC, null);
        em.flush();
    }
    @Test(expected = PersistenceException.class)
    public void testCreateEventReportInvalidEvent(){
        reportDao.create(TestUtils.USER_2, TestUtils.USER_1, new Event(12341234l, null, null, null, 0, null, null, null, null, null), TestUtils.REPORT_EVENT_DESC, TestUtils.HARASSMENT);
        em.flush();
    }

    @Test
    public void testCreateEventResponseReport(){
        Report report = reportDao.create(TestUtils.USER_1, TestUtils.USER_2, TestUtils.EVENT_RESPONSE_1, TestUtils.REPORT_EVENT_RESPONSE_DESC, TestUtils.HARASSMENT);
        em.flush();

        assertNotNull(report);
        TestUtils.assertEqualsReport(new Report(null, TestUtils.USER_1, TestUtils.USER_2, TestUtils.REPORT_EVENT_RESPONSE_DESC, TestUtils.HARASSMENT, null, null, TestUtils.EVENT_RESPONSE_1, null, false, ReportStatus.PENDING), report);
    }
    @Test(expected = PersistenceException.class)
    public void testCreateEventResponseReportInvalidReportedUser(){
        reportDao.create(new User(12341234l, null, null, null, null, null, null, 0, null, false, false), TestUtils.USER_2, TestUtils.EVENT_RESPONSE_1, TestUtils.REPORT_EVENT_RESPONSE_DESC, TestUtils.HARASSMENT);
        em.flush();
    }
    @Test(expected = PersistenceException.class)
    public void testCreateEventResponseReportInvalidReportingUser(){
        reportDao.create(TestUtils.USER_1, new User(12341234l, null, null, null, null, null, null, 0, null, false, false), TestUtils.EVENT_RESPONSE_1, TestUtils.REPORT_EVENT_RESPONSE_DESC, TestUtils.HARASSMENT);
        em.flush();
    }
    @Test(expected = PersistenceException.class)
    public void testCreateEventResponseReportMissingReportedUser(){
        reportDao.create(null, TestUtils.USER_2, TestUtils.EVENT_RESPONSE_1, TestUtils.REPORT_EVENT_RESPONSE_DESC, TestUtils.HARASSMENT);
        em.flush();
    }
    @Test(expected = PersistenceException.class)
    public void testCreateEventResponseReportMissingReportingUser(){
        reportDao.create(TestUtils.USER_2, null, TestUtils.EVENT_RESPONSE_1, TestUtils.REPORT_EVENT_RESPONSE_DESC, TestUtils.HARASSMENT);
        em.flush();
    }
    @Test(expected = PersistenceException.class)
    public void testCreateEventResponseReportMissingDescription(){
        reportDao.create(TestUtils.USER_2, TestUtils.USER_1, TestUtils.EVENT_RESPONSE_1, null, TestUtils.HARASSMENT);
        em.flush();
    }
    @Test(expected = PersistenceException.class)
    public void testCreateEventResponseReportMissingReason(){
        reportDao.create(TestUtils.USER_2, TestUtils.USER_1, TestUtils.EVENT_RESPONSE_1, TestUtils.REPORT_EVENT_RESPONSE_DESC, null);
        em.flush();
    }

    @Test
    public void testFindById(){
        Optional<Report> maybeReport = reportDao.findById(TestUtils.REPORT_USER_ID);

        assertNotNull(maybeReport);
        assertTrue(maybeReport.isPresent());
        TestUtils.assertEqualsReport(TestUtils.REPORT_USER, maybeReport.get());
    }
    @Test
    public void testFindByIdMissing(){
        Optional<Report> maybeReport = reportDao.findById(12341234l);

        assertNotNull(maybeReport);
        assertFalse(maybeReport.isPresent());
    }

    @Test
    public void testCountReportsAgainstUser(){
        long reports = reportDao.countReportsAgainstUser(TestUtils.USER_2);

        assertEquals(TestUtils.USER_2_REPORTS, reports);
    }
    @Test
    public void testCountReportsAgainstUserNoReports(){
        long reports = reportDao.countReportsAgainstUser(TestUtils.USER_1);

        assertEquals(0, reports);
    }
    @Test
    public void testCountReportsAgainstUserMissing(){
        long reports = reportDao.countReportsAgainstUser(new User(12341234l, null, null, null, null, null, null, 0, null, false, false));

        assertEquals(0, reports);
    }

    @Test
    public void testFindAllPaginated(){
        Page<Report> reports = reportDao.findAllPaginated(TestUtils.PAGE_1_BIG);
        
        assertNotNull(reports);
        assertEquals(1, reports.getCurrentPage());
        assertEquals(1, reports.getTotalPages());
        assertEquals(TestUtils.TOTAL_REPORTS, reports.getContent().size());
    }
    @Test
    public void testFindAllPaginatedNoReports(){
        JdbcTestUtils.deleteFromTables(jdbcTemplate, TestUtils.REPORT_TABLE);

        Page<Report> reports = reportDao.findAllPaginated(TestUtils.PAGE_1_BIG);
        
        assertNotNull(reports);
        assertEquals(1, reports.getCurrentPage());
        assertEquals(0, reports.getTotalPages());
        assertEquals(0, reports.getContent().size());
    }
    
    @Test
    public void testFindByStatusPaginated(){
        Page<Report> reports = reportDao.findByStatusPaginated(ReportStatus.PENDING, TestUtils.PAGE_1_BIG);

        assertNotNull(reports);
        assertEquals(1, reports.getCurrentPage());
        assertEquals(1, reports.getTotalPages());
        assertEquals(TestUtils.REPORTS_PENDING, reports.getContent().size());
        for (Report r : reports.getContent()){
            TestUtils.assertEqualsReport(TestUtils.REPORT_PENDING_DATA.get(r.getId()), r);
        }
    }
    @Test
    public void testFindByStatusPaginatedResolved(){
        Page<Report> reports = reportDao.findByStatusPaginated(ReportStatus.RESOLVED, TestUtils.PAGE_1_BIG);

        assertNotNull(reports);
        assertEquals(1, reports.getCurrentPage());
        assertEquals(1, reports.getTotalPages());
        assertEquals(TestUtils.REPORTS_RESOLVED, reports.getContent().size());
        TestUtils.assertEqualsReport(TestUtils.REPORT_USER_RESOLVED, reports.getContent().getFirst());
    }
    @Test
    public void testFindByStatusPaginatedUnderReview(){
        Page<Report> reports = reportDao.findByStatusPaginated(ReportStatus.UNDER_REVIEW, TestUtils.PAGE_1_BIG);

        assertNotNull(reports);
        assertEquals(1, reports.getCurrentPage());
        assertEquals(1, reports.getTotalPages());
        assertEquals(TestUtils.REPORTS_UNDER_REVIEW, reports.getContent().size());
        TestUtils.assertEqualsReport(TestUtils.REPORT_USER_UNDER_REVIEW, reports.getContent().getFirst());
    }
    @Test
    public void testFindByStatusPaginatedDismissed(){
        Page<Report> reports = reportDao.findByStatusPaginated(ReportStatus.DISMISSED, TestUtils.PAGE_1_BIG);

        assertNotNull(reports);
        assertEquals(1, reports.getCurrentPage());
        assertEquals(1, reports.getTotalPages());
        assertEquals(TestUtils.REPORTS_DISMISSED, reports.getContent().size());
        TestUtils.assertEqualsReport(TestUtils.REPORT_USER_DISMISSED, reports.getContent().getFirst());
    }
    @Test
    public void testFindByStatusPaginatedNoReports(){
        JdbcTestUtils.deleteFromTables(jdbcTemplate, TestUtils.REPORT_TABLE);

        Page<Report> reports = reportDao.findByStatusPaginated(ReportStatus.PENDING, TestUtils.PAGE_1_BIG);

        assertNotNull(reports);
        assertEquals(1, reports.getCurrentPage());
        assertEquals(0, reports.getTotalPages());
        assertEquals(0, reports.getContent().size());
    }

    @Test
    public void testFindByUserPaginated(){
        Page<Report> reports = reportDao.findByUserPaginated(TestUtils.USER_1, TestUtils.PAGE_1_BIG);

        assertNotNull(reports);
        assertEquals(1, reports.getCurrentPage());
        assertEquals(1, reports.getTotalPages());
        assertEquals(TestUtils.USER_1_REPORTS_AUTHORED, reports.getContent().size());
    }
    @Test
    public void testFindByUserPaginatedNoReports(){
        Page<Report> reports = reportDao.findByUserPaginated(TestUtils.USER_2, TestUtils.PAGE_1_BIG);

        assertNotNull(reports);
        assertEquals(1, reports.getCurrentPage());
        assertEquals(0, reports.getTotalPages());
        assertEquals(0, reports.getContent().size());
    }

    @Test
    public void testFindAllByDescOrReason(){
        Page<Report> reports = reportDao.findAll(TestUtils.REPORT_EVENT_DESC, TestUtils.PAGE_1_BIG);

        assertNotNull(reports);
        assertEquals(1, reports.getCurrentPage());
        assertEquals(1, reports.getTotalPages());
        assertEquals(1, reports.getContent().size());
        TestUtils.assertEqualsReport(TestUtils.REPORT_EVENT, reports.getContent().getFirst());
    }
    @Test
    public void testFindAllByReportingUsername(){
        Page<Report> reports = reportDao.findAll(TestUtils.REPORTING_USER.getUsername(), TestUtils.PAGE_1_BIG);

        assertNotNull(reports);
        assertEquals(1, reports.getCurrentPage());
        assertEquals(1, reports.getTotalPages());
        assertEquals(TestUtils.USER_1_REPORTS_AUTHORED, reports.getContent().size());
    }
    @Test
    public void testFindAllByReportedUsername(){
        Page<Report> reports = reportDao.findAll(TestUtils.REPORT_USER_REPORTED_USER.getUsername(), TestUtils.PAGE_1_BIG);

        assertNotNull(reports);
        assertEquals(1, reports.getCurrentPage());
        assertEquals(1, reports.getTotalPages());
        assertEquals(TestUtils.USER_3_REPORTS, reports.getContent().size());
    }

}