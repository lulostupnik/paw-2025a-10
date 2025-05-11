package ar.edu.itba.paw.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.itba.paw.models.Event;
import ar.edu.itba.paw.models.Page;
import ar.edu.itba.paw.models.User;

@Transactional
@Rollback
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class EventAttendanceJdbcDaoTest {

    @Autowired
    private DataSource ds;

    @Autowired
    private EventAttendanceJdbcDao attendanceDao;

    private JdbcTemplate jdbcTemplate;

    private static User USER_1;
    private static User USER_2;
    private static User USER_3;
    private static User USER_4;
    private static Event EVENT_1;
    private static Event EVENT_2;
    private static Event EVENT_3;

    @Before
    public void setUp(){
        jdbcTemplate = new JdbcTemplate(ds);

        USER_1 = jdbcTemplate.queryForObject(TestUtils.USER_SELECT_BY_EMAIL, TestUtils.USER_ROW_MAPPER, TestUtils.USER_1_MAIL);
        USER_2 = jdbcTemplate.queryForObject(TestUtils.USER_SELECT_BY_EMAIL, TestUtils.USER_ROW_MAPPER, TestUtils.USER_2_MAIL);
        USER_3 = jdbcTemplate.queryForObject(TestUtils.USER_SELECT_BY_EMAIL, TestUtils.USER_ROW_MAPPER, TestUtils.USER_3_MAIL);
        USER_4 = jdbcTemplate.queryForObject(TestUtils.USER_SELECT_BY_EMAIL, TestUtils.USER_ROW_MAPPER, TestUtils.USER_4_MAIL);
        EVENT_1 = jdbcTemplate.queryForObject(TestUtils.EVENT_SELECT_BY_TITLE, TestUtils.EVENT_ROW_MAPPER, TestUtils.EVENT_TITLE_DEFAULT);
        EVENT_2 = jdbcTemplate.queryForObject(TestUtils.EVENT_SELECT_BY_TITLE, TestUtils.EVENT_ROW_MAPPER, TestUtils.EVENT_TITLE_2);
        EVENT_3 = jdbcTemplate.queryForObject(TestUtils.EVENT_SELECT_BY_TITLE, TestUtils.EVENT_ROW_MAPPER, TestUtils.EVENT_TITLE_3);
    }

    @Test
    public void testCreate(){
        attendanceDao.create(USER_3.getId(), EVENT_2.getId());

        assertEquals(TestUtils.TOTAL_EVENT_ATTENDANCES + 1, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.EVENT_ATTENDANCE_TABLE));
        assertTrue(jdbcTemplate.queryForObject(TestUtils.EVENT_ATTENDANCE_EXISTS, Boolean.class, USER_3.getId(), EVENT_2.getId()));
        assertEquals(TestUtils.EVENT_2_ATTENDEES + 1, jdbcTemplate.queryForObject(TestUtils.EVENT_GET_ATTENDEES_COUNT_BY_ID, Long.class, EVENT_2.getId()).longValue());
        assertEquals(TestUtils.EVENT_2_ATTENDEES + 1, jdbcTemplate.queryForObject(TestUtils.EVENT_GET_ATTENDEES_BY_ID, Long.class, EVENT_2.getId()).longValue());
        assertEquals(TestUtils.USER_3_ATTENDANCES + 1, jdbcTemplate.queryForObject(TestUtils.USER_GET_ATTENDANCES_COUNT_BY_ID, Long.class, USER_3.getId()).longValue());
    }
    @Test(expected = DataAccessException.class)
    public void testCreateDuplicated(){
        attendanceDao.create(USER_2.getId(), EVENT_1.getId());
    }
    @Test(expected = DataAccessException.class)
    public void testCreateWrongUser(){
        attendanceDao.create(12341243, EVENT_1.getId());
    }
    @Test(expected = DataAccessException.class)
    public void testCreateWrongEvent(){
        attendanceDao.create(USER_2.getId(), 1231234);
    }

    @Test
    public void testDelete(){
        attendanceDao.delete(USER_2.getId(), EVENT_1.getId());

        assertEquals(TestUtils.TOTAL_EVENT_ATTENDANCES - 1, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.EVENT_ATTENDANCE_TABLE));
        assertEquals(TestUtils.EVENT_1_ATTENDEES - 1, jdbcTemplate.queryForObject(TestUtils.EVENT_GET_ATTENDEES_COUNT_BY_ID, Long.class, EVENT_1.getId()).longValue());
        assertEquals(TestUtils.EVENT_1_ATTENDEES - 1, jdbcTemplate.queryForObject(TestUtils.EVENT_GET_ATTENDEES_BY_ID, Long.class, EVENT_1.getId()).longValue());
        assertEquals(TestUtils.USER_2_ATTENDANCES - 1, jdbcTemplate.queryForObject(TestUtils.USER_GET_ATTENDANCES_COUNT_BY_ID, Long.class, USER_2.getId()).longValue());
        assertFalse(jdbcTemplate.queryForObject(TestUtils.EVENT_ATTENDANCE_EXISTS, Boolean.class, USER_2.getId(), EVENT_1.getId()));
    }
    @Test
    public void testDeleteNotParticipating(){
        attendanceDao.delete(USER_3.getId(), EVENT_2.getId());

        assertEquals(TestUtils.TOTAL_EVENT_ATTENDANCES, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.EVENT_ATTENDANCE_TABLE));
        assertEquals(TestUtils.EVENT_2_ATTENDEES, jdbcTemplate.queryForObject(TestUtils.EVENT_GET_ATTENDEES_COUNT_BY_ID, Integer.class, EVENT_2.getId()).intValue());
        assertEquals(TestUtils.EVENT_2_ATTENDEES, jdbcTemplate.queryForObject(TestUtils.EVENT_GET_ATTENDEES_BY_ID, Integer.class, EVENT_2.getId()).intValue());
        assertEquals(TestUtils.USER_3_ATTENDANCES, jdbcTemplate.queryForObject(TestUtils.USER_GET_ATTENDANCES_COUNT_BY_ID, Integer.class, EVENT_2.getId()).intValue());
    }   
    @Test
    public void testDeleteWrongEvent(){
        int rowsBefore = JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.EVENT_ATTENDANCE_TABLE);

        attendanceDao.delete(USER_2.getId(), 12341234);

        assertEquals(rowsBefore, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.EVENT_ATTENDANCE_TABLE));
        assertEquals(TestUtils.USER_2_ATTENDANCES, jdbcTemplate.queryForObject(TestUtils.USER_GET_ATTENDANCES_COUNT_BY_ID, Integer.class, USER_2.getId()).intValue());
    }  
    @Test
    public void testDeleteWrongUser(){
        int rowsBefore = JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.EVENT_ATTENDANCE_TABLE);

        attendanceDao.delete(1241234, EVENT_1.getId());
        
        assertEquals(rowsBefore, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.EVENT_ATTENDANCE_TABLE));
        assertEquals(TestUtils.EVENT_1_ATTENDEES, jdbcTemplate.queryForObject(TestUtils.EVENT_GET_ATTENDEES_COUNT_BY_ID, Integer.class, EVENT_1.getId()).intValue());
        assertEquals(TestUtils.EVENT_1_ATTENDEES, jdbcTemplate.queryForObject(TestUtils.EVENT_GET_ATTENDEES_BY_ID, Integer.class, EVENT_1.getId()).intValue());
    }  

    @Test
    public void testExistsFalse(){
        boolean attending = attendanceDao.exists(USER_3.getId(), EVENT_2.getId());

        assertFalse(attending);
    }
    @Test
    public void testExistsTrue(){
        boolean attending = attendanceDao.exists(USER_1.getId(), EVENT_1.getId());

        assertTrue(attending);
    }
    @Test
    public void testExistsMissingEvent(){
        boolean attending = attendanceDao.exists(USER_1.getId(), 12341234);

        assertFalse(attending);
    }
    @Test
    public void testExistsMissingUser(){
        boolean attending = attendanceDao.exists(1241234, EVENT_1.getId());

        assertFalse(attending);
    }

    @Test
    public void testFindAllAttendeesByEventId(){
        List<User> attendees = attendanceDao.findAllAttendeesByEventId(EVENT_1.getId());

        assertNotNull(attendees);
        assertEquals(TestUtils.EVENT_1_ATTENDEES, attendees.size());
        Map<Long, User> userData = Map.of(USER_1.getId(), USER_1, USER_2.getId(), USER_2, USER_3.getId(), USER_3);
        for (User user : attendees){
            TestUtils.assertEqualsUser(userData.get(user.getId()), user);
        }
    }
    @Test
    public void testFindAllAttendeesNoAttendeesByEventId(){
        List<User> attendees = attendanceDao.findAllAttendeesByEventId(EVENT_3.getId());

        assertNotNull(attendees);
        assertEquals(0, attendees.size());
    }
    @Test
    public void testFindAllAttendeesByEventIdMissingEvent(){
        List<User> attendees = attendanceDao.findAllAttendeesByEventId(412341234);

        assertNotNull(attendees);
        assertEquals(0, attendees.size());
    }

    @Test
    public void testFindAllAttendeesByEventIdCount(){
        int attendees = attendanceDao.countByEventId(EVENT_1.getId());

        assertNotNull(attendees);
        assertEquals(TestUtils.EVENT_1_ATTENDEES, attendees);
    }
    @Test
    public void testFindAllAttendeesCountNoAttendeesByEventId(){
        int attendees = attendanceDao.countByEventId(EVENT_3.getId());

        assertNotNull(attendees);
        assertEquals(0, attendees);
    }
    @Test
    public void testFindAllAttendeesByEventIdCountMissingEvent(){
        int attendees = attendanceDao.countByEventId(412341234);

        assertNotNull(attendees);
        assertEquals(0, attendees);
    }

    @Test
    public void testFindAllEventsByAttendee(){
        List<Event> events = attendanceDao.getAttendingEvents(USER_1.getId());
        
        assertEquals(TestUtils.USER_1_ATTENDANCES - 1, events.size());
        TestUtils.assertEqualsEvent(EVENT_2, events.getFirst());
    }
    @Test
    public void testFindAllByAttendee(){
        List<Event> events = attendanceDao.getAttendingEvents(USER_4.getId());
        
        assertEquals(0, events.size());
    }
    @Test
    public void testFindAllEventsByAttendeeWrongUser(){
        List<Event> events = attendanceDao.getAttendingEvents(12341234);
        
        assertEquals(0, events.size());
    }

    @Test
    public void testFindAllAttendeesByEventIdPaged(){
        Page<User> page1 = attendanceDao.findAllAttendeesByEventId(EVENT_1.getId(), TestUtils.PAGE_1_DEFAULT);
        Page<User> page2 = attendanceDao.findAllAttendeesByEventId(EVENT_1.getId(), TestUtils.PAGE_2_DEFAULT);

        assertNotNull(page1);
        assertNotNull(page2);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(2, page2.getCurrentPage());
        assertEquals(2, page1.getTotalPages());
        assertEquals(2, page2.getTotalPages());
        assertNotNull(page1.getContent());
        assertNotNull(page2.getContent());
        assertEquals(2, page1.getContent().size());
        assertEquals(1, page2.getContent().size());
    }
    @Test
    public void testFindAllAttendeesPagedNoAttendeesByEventId(){
        Page<User> attendees = attendanceDao.findAllAttendeesByEventId(EVENT_3.getId(), TestUtils.PAGE_1_DEFAULT);

        assertNotNull(attendees);
        assertEquals(1, attendees.getCurrentPage());
        assertEquals(0, attendees.getTotalPages());
        assertNotNull(attendees.getContent());
        assertEquals(0, attendees.getContent().size());
    }
    @Test
    public void testFindAllAttendeesByEventIdPagedMissingEvent(){
        Page<User> attendees = attendanceDao.findAllAttendeesByEventId(412341234, TestUtils.PAGE_1_DEFAULT);

        assertNotNull(attendees);
        assertEquals(1, attendees.getCurrentPage());
        assertEquals(0, attendees.getTotalPages());
        assertNotNull(attendees.getContent());
        assertEquals(0, attendees.getContent().size());
    }

    // @Test
    // public void testFindAllEventsByAttendeePaged(){
    //     Page<Event> page1 = attendanceDao.findAllEventsByAttendee(USER_1.getId(), TestUtils.PAGE_1_SINGLE);
    //     Page<Event> page2 = attendanceDao.findAllEventsByAttendee(USER_1.getId(), TestUtils.PAGE_2_SINGLE);
        
    //     assertNotNull(page1);
    //     assertNotNull(page2);
    //     assertEquals(1, page1.getCurrentPage());
    //     assertEquals(2, page2.getCurrentPage());
    //     assertEquals(1, page1.getTotalPages());
    //     assertEquals(1, page2.getTotalPages());
    //     assertNotNull(page1.getContent());
    //     assertNotNull(page2.getContent());
    //     assertEquals(1, page1.getContent().size());
    //     assertEquals(0, page2.getContent().size());    
    // }
    // @Test
    // public void testFindAllEventsByAttendeePagedWrongUser(){
    //     Page<Event> events = attendanceDao.findAllEventsByAttendee(12341234, TestUtils.PAGE_1_SINGLE);
        
    //     assertNotNull(events);
    //     assertEquals(1, events.getCurrentPage());
    //     assertEquals(0, events.getTotalPages());
    //     assertNotNull(events.getContent());
    //     assertEquals(0, events.getContent().size());    
    // }
}
