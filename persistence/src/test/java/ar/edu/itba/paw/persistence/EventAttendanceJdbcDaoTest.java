package ar.edu.itba.paw.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import ar.edu.itba.paw.models.PageParams;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.itba.paw.models.Event;
import ar.edu.itba.paw.models.Page;
import ar.edu.itba.paw.models.User;

@SuppressWarnings("null")
@Transactional
@Rollback
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class EventAttendanceJdbcDaoTest {

    private static final String ATTENDANCE_TABLE = "event_attendances";

    @Autowired
    private DataSource ds;

    @Autowired
    private EventAttendanceJdbcDao attendanceDao;

    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert insert;

    private static User USER_1;
    private static User USER_2;
    private static User USER_3;
    private static long EVENT1_ID;
    private static long EVENT2_ID;
    private static long EVENT3_ID;

    @Before
    public void setUp(){
        jdbcTemplate = new JdbcTemplate(ds);
        insert = new SimpleJdbcInsert(ds).withTableName(ATTENDANCE_TABLE);
        jdbcTemplate.execute("INSERT INTO events(user_id, city_id, event_date, attendees_limit, title, deleted) VALUES((SELECT id FROM users WHERE username = 'user1'), (SELECT id FROM cities LIMIT 1), CURRENT_DATE, 2, '1', FALSE)");
        jdbcTemplate.execute("INSERT INTO events(user_id, city_id, event_date, attendees_limit, title, deleted) VALUES((SELECT id FROM users WHERE username = 'user2'), (SELECT id FROM cities LIMIT 1), CURRENT_DATE, 10, '2', FALSE)");
        jdbcTemplate.execute("INSERT INTO events(user_id, city_id, event_date, title, deleted, event_time) VALUES((SELECT id FROM users WHERE username = 'user3'), (SELECT id FROM cities LIMIT 1), CURRENT_DATE, '3', FALSE, '20:00:00')");

        USER_1 = jdbcTemplate.queryForObject(TestUtils.USER_SELECT_BY_EMAIL, TestUtils.USER_ROW_MAPPER, TestUtils.USER_1_MAIL);
        USER_2 = jdbcTemplate.queryForObject(TestUtils.USER_SELECT_BY_EMAIL, TestUtils.USER_ROW_MAPPER, TestUtils.USER_2_MAIL);
        USER_3 = jdbcTemplate.queryForObject(TestUtils.USER_SELECT_BY_EMAIL, TestUtils.USER_ROW_MAPPER, TestUtils.USER_3_MAIL);
        EVENT1_ID = jdbcTemplate.queryForObject("SELECT id FROM events WHERE title = '1'", Long.class);
        EVENT2_ID = jdbcTemplate.queryForObject("SELECT id FROM events WHERE title = '2'", Long.class);
        EVENT3_ID = jdbcTemplate.queryForObject("SELECT id FROM events WHERE title = '3'", Long.class);
    }

    @Test
    public void testCreate(){
        attendanceDao.create(USER_2.getId(), EVENT1_ID);

        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, ATTENDANCE_TABLE));
        assertEquals(EVENT1_ID, jdbcTemplate.queryForObject("SELECT event_id FROM event_attendances WHERE user_id = ?", Long.class, USER_2.getId()).longValue());
        assertEquals(1, jdbcTemplate.queryForObject("SELECT attendees_count FROM events WHERE id = ?", Long.class, EVENT1_ID).longValue());
    }
    @Test(expected = DataAccessException.class)
    public void testCreateDuplicated(){
        insert.execute(Map.of("event_id", EVENT1_ID ,"user_id", USER_2.getId()));

        attendanceDao.create(USER_2.getId(), EVENT1_ID);
    }
    @Test(expected = DataAccessException.class)
    public void testCreateWrongUser(){
        attendanceDao.create(12341243, EVENT1_ID);
    }
    @Test(expected = DataAccessException.class)
    public void testCreateWrongEvent(){
        attendanceDao.create(USER_2.getId(), 1231234);
    }

    @Test
    public void testDelete(){
        insert.execute(Map.of("event_id", EVENT1_ID ,"user_id", USER_2.getId()));
        jdbcTemplate.update("UPDATE events SET attendees_count = attendees_count + 1 WHERE id = ?", EVENT1_ID);
        attendanceDao.delete(USER_2.getId(), EVENT1_ID);

        assertEquals(0, JdbcTestUtils.countRowsInTable(jdbcTemplate, ATTENDANCE_TABLE));
        assertEquals(0, jdbcTemplate.queryForObject("SELECT attendees_count FROM events WHERE id = ?", Long.class, EVENT1_ID).longValue());
    }
    @Test
    public void testDeleteNotParticipating(){
        attendanceDao.delete(USER_2.getId(), EVENT1_ID);

        assertEquals(0, JdbcTestUtils.countRowsInTable(jdbcTemplate, ATTENDANCE_TABLE));
    }   
    @Test
    public void testDeleteWrongEvent(){
        //TODO assert
        attendanceDao.delete(USER_2.getId(), 12341234);
    }  
    @Test
    public void testDeleteWrongUser(){
        //TODO assert
        attendanceDao.delete(1241234, EVENT1_ID);
    }  

    @Test
    public void testExistsFalse(){
        boolean attending = attendanceDao.exists(USER_1.getId(), EVENT1_ID);

        assertFalse(attending);
    }
    @Test
    public void testExistsTrue(){
        insert.execute(Map.of("event_id", EVENT1_ID ,"user_id", USER_1.getId()));

        boolean attending = attendanceDao.exists(USER_1.getId(), EVENT1_ID);

        assertTrue(attending);
    }
    @Test
    public void testExistsMissingEvent(){
        insert.execute(Map.of("event_id", EVENT1_ID ,"user_id", USER_1.getId()));

        boolean attending = attendanceDao.exists(USER_1.getId(), 12341234);

        assertFalse(attending);
    }
    @Test
    public void testExistsMissingUser(){
        insert.execute(Map.of("event_id", EVENT1_ID ,"user_id", USER_1.getId()));

        boolean attending = attendanceDao.exists(1241234, EVENT1_ID);

        assertFalse(attending);
    }

    @Test
    public void testFindAllAttendeesByEventId(){
        insert.execute(Map.of("event_id", EVENT1_ID ,"user_id", USER_1.getId()));
        insert.execute(Map.of("event_id", EVENT1_ID ,"user_id", USER_2.getId()));

        List<User> attendees = attendanceDao.findAllAttendeesByEventId(EVENT1_ID);

        assertNotNull(attendees);
        assertEquals(2, attendees.size());
    }
    @Test
    public void testFindAllAttendeesNoAttendeesByEventId(){
        List<User> attendees = attendanceDao.findAllAttendeesByEventId(EVENT1_ID);

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
        insert.execute(Map.of("event_id", EVENT1_ID ,"user_id", USER_1.getId()));
        insert.execute(Map.of("event_id", EVENT1_ID ,"user_id", USER_2.getId()));
        jdbcTemplate.update("UPDATE events SET attendees_count = attendees_count + 2 WHERE id = ?", EVENT1_ID);
        int attendees = attendanceDao.countByEventId(EVENT1_ID);

        assertNotNull(attendees);
        assertEquals(2, attendees);
    }
    @Test
    public void testFindAllAttendeesCountNoAttendeesByEventId(){
        int attendees = attendanceDao.countByEventId(EVENT1_ID);

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
        insert.execute(Map.of("event_id", EVENT3_ID ,"user_id", USER_1.getId()));
        insert.execute(Map.of("event_id", EVENT2_ID ,"user_id", USER_1.getId()));

        List<Event> events = attendanceDao.getAttendingEvents(USER_1.getId());
        
        assertEquals(2, events.size());
    }
    @Test
    public void testFindAllByAttendee(){
        List<Event> events = attendanceDao.getAttendingEvents(USER_1.getId());
        
        assertEquals(0, events.size());
    }
    @Test
    public void testFindAllEventsByAttendeeWrongUser(){
        insert.execute(Map.of("event_id", EVENT3_ID ,"user_id", USER_1.getId()));

        List<Event> events = attendanceDao.getAttendingEvents(12341234);
        
        assertEquals(0, events.size());
    }

    @Test
    public void testFindAllAttendeesByEventIdPaged(){
        insert.execute(Map.of("event_id", EVENT1_ID ,"user_id", USER_1.getId()));
        insert.execute(Map.of("event_id", EVENT1_ID ,"user_id", USER_2.getId()));
        insert.execute(Map.of("event_id", EVENT1_ID ,"user_id", USER_3.getId()));

        Page<User> page1 = attendanceDao.findAllAttendeesByEventId(EVENT1_ID, new PageParams(1, 2));
        Page<User> page2 = attendanceDao.findAllAttendeesByEventId(EVENT1_ID, new PageParams(2, 2));

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
        Page<User> attendees = attendanceDao.findAllAttendeesByEventId(EVENT1_ID, new PageParams(1, 2));

        assertNotNull(attendees);
        assertEquals(1, attendees.getCurrentPage());
        assertEquals(0, attendees.getTotalPages());
        assertNotNull(attendees.getContent());
        assertEquals(0, attendees.getContent().size());
    }
    @Test
    public void testFindAllAttendeesByEventIdPagedMissingEvent(){
        Page<User> attendees = attendanceDao.findAllAttendeesByEventId(412341234,new PageParams( 1, 2));

        assertNotNull(attendees);
        assertEquals(1, attendees.getCurrentPage());
        assertEquals(0, attendees.getTotalPages());
        assertNotNull(attendees.getContent());
        assertEquals(0, attendees.getContent().size());
    }


}
