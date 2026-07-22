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

        assertEquals(
            TOTAL_REPORTS + 1,
            JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, REPORT_TABLE, "deleted = FALSE")
        );
        assertEquals(
            1,
            JdbcTestUtils.countRowsInTableWhere(
                jdbcTemplate, REPORT_TABLE,
                "id = " + report.getId()
                    + " AND reported_user_id = " + USER_1_ID
                    + " AND reporting_user_id = " + USER_2_ID
                    + " AND description = '" + REPORT_USER_DESC + "'"
                    + " AND reason = '" + HARASSMENT.name() + "'"
                    + " AND status = '" + ReportStatus.PENDING.name() + "'"
                    + " AND deleted = FALSE"
                    + " AND journey_id IS NULL AND event_id IS NULL"
                    + " AND event_response_id IS NULL AND journey_response_id IS NULL"
            )
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
                0L,
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
                    0L,
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

        assertEquals(
            TOTAL_REPORTS + 1,
            JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, REPORT_TABLE, "deleted = FALSE")
        );
        assertEquals(
            1,
            JdbcTestUtils.countRowsInTableWhere(
                jdbcTemplate, REPORT_TABLE,
                "id = " + report.getId()
                    + " AND reported_user_id = " + USER_1_ID
                    + " AND reporting_user_id = " + USER_2_ID
                    + " AND description = '" + REPORT_JOURNEY_DESC + "'"
                    + " AND reason = '" + HARASSMENT.name() + "'"
                    + " AND status = '" + ReportStatus.PENDING.name() + "'"
                    + " AND deleted = FALSE"
                    + " AND journey_id = " + JOURNEY_1_ID
                    + " AND event_id IS NULL"
                    + " AND event_response_id IS NULL AND journey_response_id IS NULL"
            )
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
                    0L,
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
                    0L,
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

        assertEquals(
            TOTAL_REPORTS + 1,
            JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, REPORT_TABLE, "deleted = FALSE")
        );
        assertEquals(
            1,
            JdbcTestUtils.countRowsInTableWhere(
                jdbcTemplate, REPORT_TABLE,
                "id = " + report.getId()
                    + " AND reported_user_id = " + USER_1_ID
                    + " AND reporting_user_id = " + USER_2_ID
                    + " AND description = '" + REPORT_JOURNEY_RESPONSE_DESC + "'"
                    + " AND reason = '" + HARASSMENT.name() + "'"
                    + " AND status = '" + ReportStatus.PENDING.name() + "'"
                    + " AND deleted = FALSE"
                    + " AND journey_response_id = " + JOURNEY_RESPONSE_1_ID
                    + " AND journey_id IS NULL AND event_id IS NULL"
                    + " AND event_response_id IS NULL"
            )
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
                    0L,
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
                    0L,
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

        assertEquals(
            TOTAL_REPORTS + 1,
            JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, REPORT_TABLE, "deleted = FALSE")
        );
        assertEquals(
            1,
            JdbcTestUtils.countRowsInTableWhere(
                jdbcTemplate, REPORT_TABLE,
                "id = " + report.getId()
                    + " AND reported_user_id = " + USER_1_ID
                    + " AND reporting_user_id = " + USER_2_ID
                    + " AND description = '" + REPORT_EVENT_DESC + "'"
                    + " AND reason = '" + HARASSMENT.name() + "'"
                    + " AND status = '" + ReportStatus.PENDING.name() + "'"
                    + " AND deleted = FALSE"
                    + " AND event_id = " + EVENT_1_ID
                    + " AND journey_id IS NULL"
                    + " AND event_response_id IS NULL AND journey_response_id IS NULL"
            )
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
                    0L,
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
                    0L,
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
                0L,
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

        assertEquals(
            TOTAL_REPORTS + 1,
            JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, REPORT_TABLE, "deleted = FALSE")
        );
        assertEquals(
            1,
            JdbcTestUtils.countRowsInTableWhere(
                jdbcTemplate, REPORT_TABLE,
                "id = " + report.getId()
                    + " AND reported_user_id = " + USER_1_ID
                    + " AND reporting_user_id = " + USER_2_ID
                    + " AND description = '" + REPORT_EVENT_RESPONSE_DESC + "'"
                    + " AND reason = '" + HARASSMENT.name() + "'"
                    + " AND status = '" + ReportStatus.PENDING.name() + "'"
                    + " AND deleted = FALSE"
                    + " AND event_response_id = " + EVENT_RESPONSE_1_ID
                    + " AND journey_id IS NULL AND event_id IS NULL"
                    + " AND journey_response_id IS NULL"
            )
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
                    0L,
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
                    0L,
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

    @Test
    public void testHardDeleteByJourneyId(){
        jdbcTemplate.update(
            "INSERT INTO reports(id, reported_user_id, reporting_user_id, journey_id, event_id, event_response_id, journey_response_id, description, reason, deleted) " +
            "VALUES (100, 3, 1, 1, null, null, null, 'by journey', 'HARASSMENT', FALSE)");
        jdbcTemplate.update(
            "INSERT INTO reports(id, reported_user_id, reporting_user_id, journey_id, event_id, event_response_id, journey_response_id, description, reason, deleted) " +
            "VALUES (101, 3, 1, null, null, null, 1, 'by response', 'HARASSMENT', FALSE)");

        assertEquals(2, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, REPORT_TABLE, "id IN (100, 101)"));

        reportDao.hardDeleteByJourneyId(1);
        em.flush();

        assertEquals(0, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, REPORT_TABLE, "id IN (100, 101)"));
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, REPORT_TABLE, "id = 2"));
    }
}
