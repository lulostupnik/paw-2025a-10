package ar.edu.itba.paw.persistance;

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
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.itba.paw.models.Event;
import ar.edu.itba.paw.models.Page;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistence.EventAttendanceJdbcDao;

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

    private static User USER1;
    private static User USER2;
    private static User USER3;
    private static long EVENT1_ID;
    private static long EVENT2_ID;
    private static long EVENT3_ID;

    @Before
    public void setUp(){
        jdbcTemplate = new JdbcTemplate(ds);
        insert = new SimpleJdbcInsert(ds).withTableName(ATTENDANCE_TABLE);
        jdbcTemplate.execute("INSERT INTO users(email, username, firstname, lastname, university, career_id, profile_picture_id, language, roles, blocked) VALUES('user1@mail.com', 'user1', 'user', '1', (SELECT id FROM universities WHERE abbreviation = 'ITBA'), (SELECT id FROM careers WHERE name = 'career 1'), (SELECT id FROM images LIMIT 1), 'es', 'user', FALSE)");
        jdbcTemplate.execute("INSERT INTO users(email, username, firstname, lastname, university, career_id, profile_picture_id, language, roles, blocked) VALUES('user2@mail.com', 'user2', 'user', '2', (SELECT id FROM universities WHERE abbreviation = 'ITBA'), (SELECT id FROM careers WHERE name = 'career 1'), (SELECT id FROM images LIMIT 1), 'en', 'user', FALSE)");
        jdbcTemplate.execute("INSERT INTO users(email, username, firstname, lastname, university, career_id, profile_picture_id, language, roles, blocked) VALUES('user3@mail.com', 'user3', 'user', '3', (SELECT id FROM universities WHERE abbreviation = 'ITBA'), (SELECT id FROM careers WHERE name = 'career 1'), (SELECT id FROM images LIMIT 1), 'en', 'user', FALSE)");
        jdbcTemplate.execute("INSERT INTO events(user_id, city_id, event_date, attendees_limit, title, deleted) VALUES((SELECT id FROM users WHERE lastname = 1), (SELECT id FROM cities LIMIT 1), CURRENT_DATE, 2, '1', FALSE)");
        jdbcTemplate.execute("INSERT INTO events(user_id, city_id, event_date, attendees_limit, title, deleted) VALUES((SELECT id FROM users WHERE lastname = 2), (SELECT id FROM cities LIMIT 1), CURRENT_DATE, 10, '2', FALSE)");
        jdbcTemplate.execute("INSERT INTO events(user_id, city_id, event_date, title, deleted, event_time) VALUES((SELECT id FROM users WHERE lastname = 2), (SELECT id FROM cities LIMIT 1), CURRENT_DATE, '3', FALSE, '20:00:00')");

        USER1 = jdbcTemplate.query("SELECT id FROM users WHERE lastname = '1'", (rs, n) -> new User(rs.getLong("id"), null, null, null, null, null, null, 0, null, false)).stream().findFirst().get();
        USER2 = jdbcTemplate.query("SELECT id FROM users WHERE lastname = '2'", (rs, n) -> new User(rs.getLong("id"), null, null, null, null, null, null, 0, null, false)).stream().findFirst().get();
        USER3 = jdbcTemplate.query("SELECT id FROM users WHERE lastname = '3'", (rs, n) -> new User(rs.getLong("id"), null, null, null, null, null, null, 0, null, false)).stream().findFirst().get();
        EVENT1_ID = jdbcTemplate.queryForObject("SELECT id FROM events WHERE title = '1'", Long.class);
        EVENT2_ID = jdbcTemplate.queryForObject("SELECT id FROM events WHERE title = '2'", Long.class);
        EVENT3_ID = jdbcTemplate.queryForObject("SELECT id FROM events WHERE title = '3'", Long.class);
    }

    @Test
    public void testAttend(){
        attendanceDao.attend(USER2.getId(), EVENT1_ID);

        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, ATTENDANCE_TABLE));
        assertEquals(EVENT1_ID, jdbcTemplate.queryForObject("SELECT event_id FROM event_attendances WHERE user_id = ?", Long.class, USER2.getId()).longValue());
        assertEquals(1, jdbcTemplate.queryForObject("SELECT attendees_count FROM events WHERE id = ?", Long.class, EVENT1_ID).longValue());
    }
    @Test(expected = DataAccessException.class)
    public void testAttendDuplicated(){
        insert.execute(Map.of("event_id", EVENT1_ID ,"user_id", USER2.getId()));

        attendanceDao.attend(USER2.getId(), EVENT1_ID);
    }
    @Test(expected = DataAccessException.class)
    public void testAttendWrongUser(){
        attendanceDao.attend(12341243, EVENT1_ID);
    }
    @Test(expected = DataAccessException.class)
    public void testAttendWrongEvent(){
        attendanceDao.attend(USER2.getId(), 1231234);
    }

    @Test
    public void testCancel(){
        insert.execute(Map.of("event_id", EVENT1_ID ,"user_id", USER2.getId()));
        jdbcTemplate.update("UPDATE events SET attendees_count = attendees_count + 1 WHERE id = ?", EVENT1_ID);
        attendanceDao.cancel(USER2.getId(), EVENT1_ID);

        assertEquals(0, JdbcTestUtils.countRowsInTable(jdbcTemplate, ATTENDANCE_TABLE));
        assertEquals(0, jdbcTemplate.queryForObject("SELECT attendees_count FROM events WHERE id = ?", Long.class, EVENT1_ID).longValue());
    }
    @Test
    public void testCancelNotParticipating(){
        attendanceDao.cancel(USER2.getId(), EVENT1_ID);

        assertEquals(0, JdbcTestUtils.countRowsInTable(jdbcTemplate, ATTENDANCE_TABLE));
    }   
    @Test
    public void testCancelWrongEvent(){
        //TODO assert
        attendanceDao.cancel(USER2.getId(), 12341234);
    }  
    @Test
    public void testCancelWrongUser(){
        //TODO assert
        attendanceDao.cancel(1241234, EVENT1_ID);
    }  

    @Test
    public void testIsAttendingFalse(){
        boolean attending = attendanceDao.isAttending(USER1.getId(), EVENT1_ID);

        assertFalse(attending);
    }
    @Test
    public void testIsAttendingTrue(){
        insert.execute(Map.of("event_id", EVENT1_ID ,"user_id", USER1.getId()));

        boolean attending = attendanceDao.isAttending(USER1.getId(), EVENT1_ID);

        assertTrue(attending);
    }
    @Test
    public void testIsAttendingMissingEvent(){
        insert.execute(Map.of("event_id", EVENT1_ID ,"user_id", USER1.getId()));

        boolean attending = attendanceDao.isAttending(USER1.getId(), 12341234);

        assertFalse(attending);
    }
    @Test
    public void testIsAttendingMissingUser(){
        insert.execute(Map.of("event_id", EVENT1_ID ,"user_id", USER1.getId()));

        boolean attending = attendanceDao.isAttending(1241234, EVENT1_ID);

        assertFalse(attending);
    }

    @Test
    public void testGetAttendees(){
        insert.execute(Map.of("event_id", EVENT1_ID ,"user_id", USER1.getId()));
        insert.execute(Map.of("event_id", EVENT1_ID ,"user_id", USER2.getId()));

        List<User> attendees = attendanceDao.getAttendees(EVENT1_ID);

        assertNotNull(attendees);
        assertEquals(2, attendees.size());
    }
    @Test
    public void testGetAttendeesNoAttendees(){
        List<User> attendees = attendanceDao.getAttendees(EVENT1_ID);

        assertNotNull(attendees);
        assertEquals(0, attendees.size());
    }
    @Test
    public void testGetAttendeesMissingEvent(){
        List<User> attendees = attendanceDao.getAttendees(412341234);

        assertNotNull(attendees);
        assertEquals(0, attendees.size());
    }

    @Test
    public void testGetAttendeesCount(){
        insert.execute(Map.of("event_id", EVENT1_ID ,"user_id", USER1.getId()));
        insert.execute(Map.of("event_id", EVENT1_ID ,"user_id", USER2.getId()));
        jdbcTemplate.update("UPDATE events SET attendees_count = attendees_count + 2 WHERE id = ?", EVENT1_ID);
        int attendees = attendanceDao.getAttendeesCount(EVENT1_ID);

        assertNotNull(attendees);
        assertEquals(2, attendees);
    }
    @Test
    public void testGetAttendeesCountNoAttendees(){
        int attendees = attendanceDao.getAttendeesCount(EVENT1_ID);

        assertNotNull(attendees);
        assertEquals(0, attendees);
    }
    @Test
    public void testGetAttendeesCountMissingEvent(){
        int attendees = attendanceDao.getAttendeesCount(412341234);

        assertNotNull(attendees);
        assertEquals(0, attendees);
    }

    @Test
    public void testGetAttendingEvents(){
        insert.execute(Map.of("event_id", EVENT3_ID ,"user_id", USER1.getId()));
        insert.execute(Map.of("event_id", EVENT2_ID ,"user_id", USER1.getId()));

        List<Event> events = attendanceDao.getAttendingEvents(USER1.getId());
        
        assertEquals(2, events.size());
    }
    @Test
    public void testGetAttendingEventsNoAttending(){
        List<Event> events = attendanceDao.getAttendingEvents(USER1.getId());
        
        assertEquals(0, events.size());
    }
    @Test
    public void testGetAttendingEventsWrongUser(){
        insert.execute(Map.of("event_id", EVENT3_ID ,"user_id", USER1.getId()));

        List<Event> events = attendanceDao.getAttendingEvents(12341234);
        
        assertEquals(0, events.size());
    }

    @Test
    public void testGetAttendeesPaged(){
        insert.execute(Map.of("event_id", EVENT1_ID ,"user_id", USER1.getId()));
        insert.execute(Map.of("event_id", EVENT1_ID ,"user_id", USER2.getId()));
        insert.execute(Map.of("event_id", EVENT1_ID ,"user_id", USER3.getId()));

        Page<User> page1 = attendanceDao.getAttendees(EVENT1_ID, 1, 2);
        Page<User> page2 = attendanceDao.getAttendees(EVENT1_ID, 2, 2);

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
    public void testGetAttendeesPagedNoAttendees(){
        Page<User> attendees = attendanceDao.getAttendees(EVENT1_ID, 1, 2);

        assertNotNull(attendees);
        assertEquals(1, attendees.getCurrentPage());
        assertEquals(0, attendees.getTotalPages());
        assertNotNull(attendees.getContent());
        assertEquals(0, attendees.getContent().size());
    }
    @Test
    public void testGetAttendeesPagedMissingEvent(){
        Page<User> attendees = attendanceDao.getAttendees(412341234, 1, 2);

        assertNotNull(attendees);
        assertEquals(1, attendees.getCurrentPage());
        assertEquals(0, attendees.getTotalPages());
        assertNotNull(attendees.getContent());
        assertEquals(0, attendees.getContent().size());
    }

    @Test
    public void testGetAttendingEventsPaged(){
        insert.execute(Map.of("event_id", EVENT3_ID ,"user_id", USER1.getId()));
        insert.execute(Map.of("event_id", EVENT2_ID ,"user_id", USER1.getId()));

        Page<Event> page1 = attendanceDao.getAttendingEvents(USER1.getId(), 1, 1);
        Page<Event> page2 = attendanceDao.getAttendingEvents(USER1.getId(), 2, 1);
        
        assertNotNull(page1);
        assertNotNull(page2);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(2, page2.getCurrentPage());
        assertEquals(2, page1.getTotalPages());
        assertEquals(2, page2.getTotalPages());
        assertNotNull(page1.getContent());
        assertNotNull(page2.getContent());
        assertEquals(1, page1.getContent().size());
        assertEquals(1, page2.getContent().size());    
    }
    @Test
    public void testGetAttendingEventsNoAttendingPaged(){
        Page<Event> events = attendanceDao.getAttendingEvents(USER1.getId(), 1, 1);
        
        assertNotNull(events);
        assertEquals(1, events.getCurrentPage());
        assertEquals(0, events.getTotalPages());
        assertNotNull(events.getContent());
        assertEquals(0, events.getContent().size());
    }
    @Test
    public void testGetAttendingEventsWrongUserPaged(){
        insert.execute(Map.of("event_id", EVENT3_ID ,"user_id", USER1.getId()));

        Page<Event> events = attendanceDao.getAttendingEvents(12341234, 1, 1);
        
        assertNotNull(events);
        assertEquals(1, events.getCurrentPage());
        assertEquals(0, events.getTotalPages());
        assertNotNull(events.getContent());
        assertEquals(0, events.getContent().size());    }
}
