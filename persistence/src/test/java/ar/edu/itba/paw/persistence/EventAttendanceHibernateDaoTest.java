package ar.edu.itba.paw.persistence;

import static org.junit.Assert.*;

import java.util.Optional;

import static ar.edu.itba.paw.persistence.TestUtils.*;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.sql.DataSource;

import ar.edu.itba.paw.models.EventAttendance;
import ar.edu.itba.paw.models.Page;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.exceptions.EventNotFoundException;
import ar.edu.itba.paw.models.exceptions.UserNotFoundException;
import ar.edu.itba.paw.persistence.config.TestConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.NonNull;
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
        attendanceDao.create(USER_3_ID, EVENT_2_ID);
        em.flush();

        assertEquals(
            TOTAL_EVENT_ATTENDANCES + 1, 
            JdbcTestUtils.countRowsInTable(jdbcTemplate, EVENT_ATTENDANCE_TABLE)
        );
        assertTrue(jdbcTemplate.queryForObject(
            EVENT_ATTENDANCE_EXISTS, 
            Boolean.class, 
            USER_3_ID, 
            EVENT_2_ID)
        );
        assertEquals(
            EVENT_2_ATTENDEES + 1, 
            jdbcTemplate.queryForObject(
                EVENT_GET_ATTENDEES_BY_ID, 
                Long.class, 
                EVENT_2_ID
            ).longValue()
        );
        assertEquals(
            USER_3_ATTENDANCES + 1, 
            jdbcTemplate.queryForObject(
                USER_GET_ATTENDANCES_COUNT_BY_ID, 
                Long.class, 
                USER_3_ID
            ).longValue()
        );
    }
    @Test(expected = PersistenceException.class)
    public void testCreateDuplicated(){
        attendanceDao.create(USER_2_ID, EVENT_1_ID);
        em.flush();
    }
    @Test(expected = UserNotFoundException.class)
    public void testCreateWrongUser(){
        int rowsBefore = JdbcTestUtils.countRowsInTable(jdbcTemplate, EVENT_ATTENDANCE_TABLE);

        attendanceDao.create(12341243, EVENT_1_ID);
        em.flush();

        assertEquals(
            rowsBefore, 
            JdbcTestUtils.countRowsInTable(
                jdbcTemplate, 
                EVENT_ATTENDANCE_TABLE
            )
        );
        assertEquals(
            EVENT_1_ATTENDEES, 
            jdbcTemplate.queryForObject(
                EVENT_GET_ATTENDEES_BY_ID, 
                Integer.class, 
                EVENT_1_ID
            ).intValue()
        );
    }
    @Test(expected = EventNotFoundException.class)
    public void testCreateWrongEvent(){
        int rowsBefore = JdbcTestUtils.countRowsInTable(jdbcTemplate, EVENT_ATTENDANCE_TABLE);

        attendanceDao.create(USER_2_ID, 1231234);
        em.flush();

        assertEquals(
            rowsBefore, 
            JdbcTestUtils.countRowsInTable(
                jdbcTemplate, 
                EVENT_ATTENDANCE_TABLE
            )
        );
        assertEquals(
            USER_2_ATTENDANCES, 
            jdbcTemplate.queryForObject(
                USER_GET_ATTENDANCES_COUNT_BY_ID, 
                Integer.class, 
                USER_2_ID
            ).intValue()
        );
    }

    @Test
    public void testDelete(){
        attendanceDao.delete(USER_2_ID, EVENT_1_ID);
        em.flush();

        assertEquals(
            TOTAL_EVENT_ATTENDANCES - 1, 
            JdbcTestUtils.countRowsInTable(jdbcTemplate, EVENT_ATTENDANCE_TABLE)
        );
        assertEquals(
            EVENT_1_ATTENDEES - 1, 
            jdbcTemplate.queryForObject(
                EVENT_GET_ATTENDEES_BY_ID, 
                Long.class, 
                EVENT_1_ID
            ).longValue()
        );
        assertEquals(
            USER_2_ATTENDANCES - 1, 
            jdbcTemplate.queryForObject(
                USER_GET_ATTENDANCES_COUNT_BY_ID, 
                Long.class, 
                USER_2_ID
            ).longValue()
        );
        assertFalse(jdbcTemplate.queryForObject(
            EVENT_ATTENDANCE_EXISTS, 
            Boolean.class, 
            USER_2_ID, 
            EVENT_1_ID
        ));
    }
    @Test(expected = NoResultException.class)
    public void testDeleteNotParticipating(){
        attendanceDao.delete(USER_3_ID, EVENT_2_ID);
        em.flush();

        assertEquals(
            TOTAL_EVENT_ATTENDANCES, 
            JdbcTestUtils.countRowsInTable(jdbcTemplate, EVENT_ATTENDANCE_TABLE)
        );
        assertEquals(
            EVENT_2_ATTENDEES, 
            jdbcTemplate.queryForObject(
                EVENT_GET_ATTENDEES_BY_ID, 
                Integer.class, 
                EVENT_2_ID
            ).intValue()
        );
        assertEquals(
            USER_3_ATTENDANCES, 
            jdbcTemplate.queryForObject(
                USER_GET_ATTENDANCES_COUNT_BY_ID, 
                Integer.class, 
                EVENT_2_ID
            ).intValue()
        );
    }
    @Test
    public void testDeleteWrongEvent(){
        int rowsBefore = JdbcTestUtils.countRowsInTable(jdbcTemplate, EVENT_ATTENDANCE_TABLE);

        attendanceDao.delete(USER_2_ID, 12341234);
        em.flush();

        assertEquals(
            rowsBefore, 
            JdbcTestUtils.countRowsInTable(jdbcTemplate, EVENT_ATTENDANCE_TABLE)
        );
        assertEquals(
            USER_2_ATTENDANCES, 
            jdbcTemplate.queryForObject(
                USER_GET_ATTENDANCES_COUNT_BY_ID, 
                Integer.class, 
                USER_2_ID
            ).intValue()
        );
    }
    @Test
    public void testDeleteWrongUser(){
        int rowsBefore = JdbcTestUtils.countRowsInTable(jdbcTemplate, EVENT_ATTENDANCE_TABLE);

        attendanceDao.delete(1241234, EVENT_1_ID);
        em.flush();

        assertEquals(
            rowsBefore, 
            JdbcTestUtils.countRowsInTable(jdbcTemplate, EVENT_ATTENDANCE_TABLE)
        );
        assertEquals(
            EVENT_1_ATTENDEES, 
            jdbcTemplate.queryForObject(
                EVENT_GET_ATTENDEES_BY_ID, 
                Integer.class, 
                EVENT_1_ID
            ).intValue()
        );
    }

    @Test
    public void testExistsFalse(){
        boolean attending = attendanceDao.exists(USER_3_ID, EVENT_2_ID);

        assertFalse(attending);
    }
    @Test
    public void testExistsTrue(){
        boolean attending = attendanceDao.exists(USER_1_ID, EVENT_1_ID);

        assertTrue(attending);
    }
    @Test
    public void testExistsMissingEvent(){
        boolean attending = attendanceDao.exists(USER_1_ID, 12341234);

        assertFalse(attending);
    }
    @Test
    public void testExistsMissingUser(){
        boolean attending = attendanceDao.exists(1241234, EVENT_1_ID);

        assertFalse(attending);
    }

    @Test
    public void testFindAttendeesByEventId(){
        Page<User> attendees = attendanceDao.findAttendeesByEventId(
            EVENT_1_ID, PAGE_1_BIG
        );

        assertNotNull(attendees);
        assertEquals(1, attendees.getCurrentPage());
        assertEquals(1, attendees.getTotalPages());
        assertEquals(EVENT_1_ATTENDEES, attendees.getContent().size());
    }
    @Test
    public void testFindAttendeesByEventIdMissingEvent(){
        Page<User> attendees = attendanceDao.findAttendeesByEventId(
            12341234l, PAGE_1_BIG
        );

        assertNotNull(attendees);
        assertEquals(1, attendees.getCurrentPage());
        assertEquals(0, attendees.getTotalPages());
        assertEquals(0, attendees.getContent().size());
    }

    @Test
    public void testCountEventsAttendedByUser(){
        int events = attendanceDao.countEventsAttendedByUser(USER_1_ID);

        assertEquals(USER_1_ATTENDANCES, events);
    }
    @Test
    public void testCountEventsAttendedByUserMissingUser(){
        int events = attendanceDao.countEventsAttendedByUser(12341234l);

        assertEquals(0, events);
    }

    @Test
    public void testFindById(){
        Optional<EventAttendance> attendance = attendanceDao.findById(USER_1_ID, EVENT_1_ID);

        assertNotNull(attendance);
        assertTrue(attendance.isPresent());
        assertEqualsUser(USER_1);
        assertEqualsEvent(EVENT_1);
        assertNotNull(attendance.get().getId());
    }
    @Test
    public void testFindByIdNotAttending(){
        Optional<EventAttendance> attendance = attendanceDao.findById(USER_4_ID, EVENT_1_ID);

        assertNotNull(attendance);
        assertFalse(attendance.isPresent());
    }

    @Test
    public void testFindByEventId(){
        Page<EventAttendance> attendance = attendanceDao.findByEventId(EVENT_1_ID, PAGE_1_BIG);

        assertNotNull(attendance);
        assertEquals(PAGE_SIZE_BIG, attendance.getPageSize());
        assertEquals(1, attendance.getCurrentPage());
        assertEquals(1, attendance.getTotalPages());
        assertEquals(EVENT_1_ATTENDEES, attendance.getTotalElements());
        assertNotNull(attendance.getContent());
        assertEquals(EVENT_1_ATTENDEES, attendance.getContent().size());
    }
    @Test
    public void testFindByEventIdNoAttendance(){
        Page<EventAttendance> attendance = attendanceDao.findByEventId(EVENT_3_ID, PAGE_1_BIG);

        assertNotNull(attendance);
        assertEquals(PAGE_SIZE_BIG, attendance.getPageSize());
        assertEquals(1, attendance.getCurrentPage());
        assertEquals(0, attendance.getTotalPages());
        assertEquals(EVENT_3_ATTENDEES, attendance.getTotalElements());
    }
}