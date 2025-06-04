package ar.edu.itba.paw.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.sql.DataSource;

import ar.edu.itba.paw.models.EventAttendance;
import ar.edu.itba.paw.models.Page;
import ar.edu.itba.paw.models.User;
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

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class EventAttendanceHibernateDaoTest {

    @Autowired
    private DataSource ds;

    @Autowired
    private EventAttendanceHibernateDao attendanceDao;

    @PersistenceContext
    private EntityManager em;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp(){
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Test
    public void testCreate(){
        attendanceDao.create(TestUtils.USER_3_ID, TestUtils.EVENT_2_ID);
        em.flush();

        assertEquals(TestUtils.TOTAL_EVENT_ATTENDANCES + 1, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.EVENT_ATTENDANCE_TABLE));
        assertTrue(jdbcTemplate.queryForObject(TestUtils.EVENT_ATTENDANCE_EXISTS, Boolean.class, TestUtils.USER_3_ID, TestUtils.EVENT_2_ID));
        assertEquals(TestUtils.EVENT_2_ATTENDEES + 1, jdbcTemplate.queryForObject(TestUtils.EVENT_GET_ATTENDEES_BY_ID, Long.class, TestUtils.EVENT_2_ID).longValue());
        assertEquals(TestUtils.USER_3_ATTENDANCES + 1, jdbcTemplate.queryForObject(TestUtils.USER_GET_ATTENDANCES_COUNT_BY_ID, Long.class, TestUtils.USER_3_ID).longValue());
    }
    @Test(expected = PersistenceException.class)
    public void testCreateDuplicated(){
        attendanceDao.create(TestUtils.USER_2_ID, TestUtils.EVENT_1_ID);
        em.flush();
    }
    @Test
    public void testCreateWrongUser(){
        int rowsBefore = JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.EVENT_ATTENDANCE_TABLE);

        attendanceDao.create(12341243, TestUtils.EVENT_1_ID);
        em.flush();

        assertEquals(rowsBefore, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.EVENT_ATTENDANCE_TABLE));
        assertEquals(TestUtils.EVENT_1_ATTENDEES, jdbcTemplate.queryForObject(TestUtils.EVENT_GET_ATTENDEES_BY_ID, Integer.class, TestUtils.EVENT_1_ID).intValue());
    }
    @Test
    public void testCreateWrongEvent(){
        int rowsBefore = JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.EVENT_ATTENDANCE_TABLE);

        attendanceDao.create(TestUtils.USER_2_ID, 1231234);
        em.flush();

        assertEquals(rowsBefore, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.EVENT_ATTENDANCE_TABLE));
        assertEquals(TestUtils.USER_2_ATTENDANCES, jdbcTemplate.queryForObject(TestUtils.USER_GET_ATTENDANCES_COUNT_BY_ID, Integer.class, TestUtils.USER_2_ID).intValue());
    }

    @Test
    public void testDelete(){
        attendanceDao.delete(TestUtils.USER_2_ID, TestUtils.EVENT_1_ID);
        em.flush();

        assertEquals(TestUtils.TOTAL_EVENT_ATTENDANCES - 1, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.EVENT_ATTENDANCE_TABLE));
        assertEquals(TestUtils.EVENT_1_ATTENDEES - 1, jdbcTemplate.queryForObject(TestUtils.EVENT_GET_ATTENDEES_BY_ID, Long.class, TestUtils.EVENT_1_ID).longValue());
        assertEquals(TestUtils.USER_2_ATTENDANCES - 1, jdbcTemplate.queryForObject(TestUtils.USER_GET_ATTENDANCES_COUNT_BY_ID, Long.class, TestUtils.USER_2_ID).longValue());
        assertFalse(jdbcTemplate.queryForObject(TestUtils.EVENT_ATTENDANCE_EXISTS, Boolean.class, TestUtils.USER_2_ID, TestUtils.EVENT_1_ID));
    }
    @Test(expected = NoResultException.class)
    public void testDeleteNotParticipating(){
        attendanceDao.delete(TestUtils.USER_3_ID, TestUtils.EVENT_2_ID);
        em.flush();

        assertEquals(TestUtils.TOTAL_EVENT_ATTENDANCES, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.EVENT_ATTENDANCE_TABLE));
        assertEquals(TestUtils.EVENT_2_ATTENDEES, jdbcTemplate.queryForObject(TestUtils.EVENT_GET_ATTENDEES_BY_ID, Integer.class, TestUtils.EVENT_2_ID).intValue());
        assertEquals(TestUtils.USER_3_ATTENDANCES, jdbcTemplate.queryForObject(TestUtils.USER_GET_ATTENDANCES_COUNT_BY_ID, Integer.class, TestUtils.EVENT_2_ID).intValue());
    }
    @Test
    public void testDeleteWrongEvent(){
        int rowsBefore = JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.EVENT_ATTENDANCE_TABLE);

        attendanceDao.delete(TestUtils.USER_2_ID, 12341234);
        em.flush();

        assertEquals(rowsBefore, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.EVENT_ATTENDANCE_TABLE));
        assertEquals(TestUtils.USER_2_ATTENDANCES, jdbcTemplate.queryForObject(TestUtils.USER_GET_ATTENDANCES_COUNT_BY_ID, Integer.class, TestUtils.USER_2_ID).intValue());
    }
    @Test
    public void testDeleteWrongUser(){
        int rowsBefore = JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.EVENT_ATTENDANCE_TABLE);

        attendanceDao.delete(1241234, TestUtils.EVENT_1_ID);
        em.flush();

        assertEquals(rowsBefore, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.EVENT_ATTENDANCE_TABLE));
        assertEquals(TestUtils.EVENT_1_ATTENDEES, jdbcTemplate.queryForObject(TestUtils.EVENT_GET_ATTENDEES_BY_ID, Integer.class, TestUtils.EVENT_1_ID).intValue());
    }

    @Test
    public void testExistsFalse(){
        boolean attending = attendanceDao.exists(TestUtils.USER_3_ID, TestUtils.EVENT_2_ID);

        assertFalse(attending);
    }
    @Test
    public void testExistsTrue(){
        boolean attending = attendanceDao.exists(TestUtils.USER_1_ID, TestUtils.EVENT_1_ID);

        assertTrue(attending);
    }
    @Test
    public void testExistsMissingEvent(){
        boolean attending = attendanceDao.exists(TestUtils.USER_1_ID, 12341234);

        assertFalse(attending);
    }
    @Test
    public void testExistsMissingUser(){
        boolean attending = attendanceDao.exists(1241234, TestUtils.EVENT_1_ID);

        assertFalse(attending);
    }

    @Test
    public void testListAllByEventId(){
        Page<EventAttendance> attendances = attendanceDao.listAllByEventId(TestUtils.EVENT_1_ID, TestUtils.PAGE_1_BIG);

        assertNotNull(attendances);
        assertEquals(1, attendances.getCurrentPage());
        assertEquals(1, attendances.getTotalPages());
        assertEquals(TestUtils.EVENT_1_ATTENDEES, attendances.getContent().size());
    }
    @Test
    public void testListAllByEventIdMissingEvent(){
        Page<EventAttendance> attendances = attendanceDao.listAllByEventId(12341234l, TestUtils.PAGE_1_BIG);

        assertNotNull(attendances);
        assertEquals(1, attendances.getCurrentPage());
        assertEquals(0, attendances.getTotalPages());
        assertEquals(0, attendances.getContent().size());
    }

    @Test
    public void testListAllByUserId(){
        Page<EventAttendance> attendances = attendanceDao.listAllByUserId(TestUtils.USER_1_ID, TestUtils.PAGE_1_BIG);

        assertNotNull(attendances);
        assertEquals(1, attendances.getCurrentPage());
        assertEquals(1, attendances.getTotalPages());
        assertEquals(TestUtils.USER_1_ATTENDANCES, attendances.getContent().size());
    }
    @Test
    public void testListAllByUserIdMissingEvent(){
        Page<EventAttendance> attendances = attendanceDao.listAllByUserId(12341234l, TestUtils.PAGE_1_BIG);

        assertNotNull(attendances);
        assertEquals(1, attendances.getCurrentPage());
        assertEquals(0, attendances.getTotalPages());
        assertEquals(0, attendances.getContent().size());
    }

    @Test
    public void testCountAttendeesByEventIdCount(){
        int attendees = attendanceDao.countAttendantsByEventId(TestUtils.EVENT_1_ID);

        assertNotNull(attendees);
        assertEquals(TestUtils.EVENT_1_ATTENDEES, attendees);
    }
    @Test
    public void testCountAttendeesCountNoAttendeesByEventId(){
        int attendees = attendanceDao.countAttendantsByEventId(TestUtils.EVENT_3_ID);

        assertNotNull(attendees);
        assertEquals(0, attendees);
    }
    @Test
    public void testCountAttendeesByEventIdCountMissingEvent(){
        int attendees = attendanceDao.countAttendantsByEventId(412341234);

        assertNotNull(attendees);
        assertEquals(0, attendees);
    }

    @Test
    public void testFindAttendeesByEventId(){
        Page<User> attendees = attendanceDao.findAttendeesByEventId(TestUtils.EVENT_1_ID, TestUtils.PAGE_1_BIG);

        assertNotNull(attendees);
        assertEquals(1, attendees.getCurrentPage());
        assertEquals(1, attendees.getTotalPages());
        assertEquals(TestUtils.EVENT_1_ATTENDEES, attendees.getContent().size());
    }
    @Test
    public void testFindAttendeesByEventIdMissingEvent(){
        Page<User> attendees = attendanceDao.findAttendeesByEventId(12341234l, TestUtils.PAGE_1_BIG);

        assertNotNull(attendees);
        assertEquals(1, attendees.getCurrentPage());
        assertEquals(0, attendees.getTotalPages());
        assertEquals(0, attendees.getContent().size());
    }

    @Test
    public void testCountEventsAttendedByUser(){
        int events = attendanceDao.countEventsAttendedByUser(TestUtils.USER_1_ID);

        assertEquals(TestUtils.USER_1_ATTENDANCES, events);
    }
    @Test
    public void testCountEventsAttendedByUserMissingUser(){
        int events = attendanceDao.countEventsAttendedByUser(12341234l);

        assertEquals(0, events);
    }
}
