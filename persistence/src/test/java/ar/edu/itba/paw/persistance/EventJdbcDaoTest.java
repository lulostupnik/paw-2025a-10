package ar.edu.itba.paw.persistance;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.itba.paw.models.City;
import ar.edu.itba.paw.models.Event;
import ar.edu.itba.paw.models.Page;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.UserEvent;
import ar.edu.itba.paw.persistence.EventJdbcDao;

@SuppressWarnings("null")
@Transactional
@Rollback
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class EventJdbcDaoTest {

    private static final String EVENT_TABLE = "events";
    private static final String EVENT_TITLE = "warm event";
    private static final String EVENT_DESCRIPTION = "cool event";
    private static final String EVENT_ADDRESS = "cool place";
    private static final LocalTime EVENT_TIME = LocalTime.now().withNano(0);
    private static final LocalDate EVENT_DATE = LocalDate.now().plusDays(7);
    private static final LocalDate LATER_DATE = EVENT_DATE.plusDays(10);
    private static final int EVENT_ATTENDANCE_LIMIT = 10;
    private static final int EVENT_ATTENDANCE = 5;
    private static final String USER1_EMAIL = "user1@mail.com";
    private static final String EVENT_ATTENDANCE_TABLE = "event_attendances";

    private static final RowMapper<Event> EVENT_ROW_MAPPER = (rs, n) -> 
        new Event(
            rs.getLong("id"), 
            new User(rs.getLong("user_id"), null, null, null, null, null, null, 0, null, false), 
            rs.getDate("event_date").toLocalDate(), 
            rs.getString("description"), 
            rs.getLong("flyer_image_id"), 
            new City(null, null, rs.getLong("city_id")), 
            rs.getString("title"), 
            Optional.ofNullable(rs.getTime("event_time") != null ? rs.getTime("event_time").toLocalTime() : null), 
            rs.getString("address"), 
            Optional.ofNullable(rs.getInt("attendees_limit") == 0 ? null : rs.getInt("attendees_limit")), 
            rs.getInt("attendees_count")
        );

    private static User USER1;
    private static User USER2;
    private static User USER3;
    private static City CITY1;
    private static City CITY2;
    private static long IMAGE_ID1;
    private static long IMAGE_ID2;

    @Autowired
    private DataSource ds;

    @Autowired
    private EventJdbcDao eventDao;

    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert insert;
    private SimpleJdbcInsert insertAttendance;

    private long insertEvent(){
        return insertEvent(Map.of());
    }
    
    @SuppressWarnings("unchecked")
    private long insertEvent(Map<String, Object> overrides){
        HashMap<String, Object> params = new HashMap<>();
        params.put("user_id", ((User)overrides.getOrDefault("user", USER1)).getId());
        params.put("city_id", ((City)overrides.getOrDefault("city", CITY1)).getId());
        params.put("event_date", Date.valueOf((LocalDate)overrides.getOrDefault("date", EVENT_DATE)));
        Optional<LocalTime> time = (Optional<LocalTime>)overrides.getOrDefault("time", Optional.of(EVENT_TIME));
        params.put("event_time", time.isPresent() ? Time.valueOf(time.get()) : null);
        params.put("address", ((Optional<String>)overrides.getOrDefault("address", Optional.of(EVENT_ADDRESS))).orElse(null));
        params.put("attendees_limit", ((Optional<Integer>)overrides.getOrDefault("limit", Optional.of(EVENT_ATTENDANCE_LIMIT))).orElse(null));
        params.put("attendees_count", ((Integer)overrides.getOrDefault("willAttend", EVENT_ATTENDANCE)));
        params.put("description", ((Optional<String>)overrides.getOrDefault("description", Optional.of(EVENT_DESCRIPTION))).orElse(null));
        params.put("title", overrides.getOrDefault("title", EVENT_TITLE));
        params.put("flyer_image_id", overrides.getOrDefault("image", IMAGE_ID1));
        params.put("deleted", overrides.getOrDefault("deleted", false));
        params.put("deleted_message", overrides.getOrDefault("deletedMessage", null));

        long key = insert.executeAndReturnKey(params).longValue();
        if (overrides.get("attending") != null){
            insertAttendance.execute(Map.of("user_id", ((User)overrides.get("attending")).getId(), "event_id", key));
        }
        return key;
    }

    private void assertEqualsEvent(Event event){
        assertEqualsEvent(event, Map.of());
    }
    
    @SuppressWarnings("unchecked")
    private void assertEqualsEvent(Event event, Map<String, Object> overrides){
        assertNotNull(event);
        assertEquals(((User)overrides.getOrDefault("user", USER1)).getId(), event.getUser().getId());
        assertEquals(((City)overrides.getOrDefault("city", CITY1)).getId(), event.getEventCity().getId());
        assertEquals(overrides.getOrDefault("date", EVENT_DATE), event.getDate());
        assertEquals(overrides.getOrDefault("time", Optional.of(EVENT_TIME)), event.getTime());
        assertEquals(((Optional<String>)overrides.getOrDefault("address", Optional.of(EVENT_ADDRESS))).orElse(null), event.getAddress());
        assertEquals(overrides.getOrDefault("limit", Optional.of(EVENT_ATTENDANCE_LIMIT)), event.getAttendeesLimit());
        assertEquals(((Optional<String>)overrides.getOrDefault("description", Optional.of(EVENT_DESCRIPTION))).orElse(null), event.getDescription());
        assertEquals(overrides.getOrDefault("title", EVENT_TITLE), event.getTitle());
        assertEquals(overrides.getOrDefault("image", IMAGE_ID1), event.getFlyerImageId());
    }

    @Before
    public void setUp(){
        jdbcTemplate = new JdbcTemplate(ds);
        insert = new SimpleJdbcInsert(ds).withTableName(EVENT_TABLE).usingGeneratedKeyColumns("id");
        insertAttendance = new SimpleJdbcInsert(ds).withTableName(EVENT_ATTENDANCE_TABLE);

        jdbcTemplate.execute("INSERT INTO countries(name, code) VALUES('Argentina', 'AR')");
        jdbcTemplate.execute("INSERT INTO countries(name, code) VALUES('Estados Unidos', 'US')");
        jdbcTemplate.execute("INSERT INTO cities(name, deleted, country_id) VALUES('Buenos Aires', FALSE, (SELECT id FROM countries WHERE code = 'AR'))");
        jdbcTemplate.execute("INSERT INTO cities(name, deleted, country_id) VALUES('Boston', FALSE, (SELECT id FROM countries WHERE code = 'US'))");
        jdbcTemplate.execute("INSERT INTO images(content) VALUES('ffffffff')");
        jdbcTemplate.execute("INSERT INTO images(content) VALUES('ffffffffffffff')");
        jdbcTemplate.execute("INSERT INTO careers(name, deleted) VALUES('a', FALSE)");
        jdbcTemplate.execute("INSERT INTO universities(name, abbreviation, deleted, city_id) VALUES('Instituto muy largo', 'ITBA', FALSE, (SELECT id FROM cities WHERE name = 'Buenos Aires'))");
        jdbcTemplate.execute("INSERT INTO users(email, username, firstname, lastname, university, career_id, profile_picture_id, language, roles, blocked) VALUES('user1@mail.com', 'user1', 'user', '1', (SELECT id FROM universities WHERE abbreviation = 'ITBA'), (SELECT id FROM careers WHERE name = 'a'), (SELECT id FROM images LIMIT 1), 'es', 'user', FALSE)");
        jdbcTemplate.execute("INSERT INTO users(email, username, firstname, lastname, university, career_id, profile_picture_id, language, roles, blocked) VALUES('user2@mail.com', 'user2', 'user', '2', (SELECT id FROM universities WHERE abbreviation = 'ITBA'), (SELECT id FROM careers WHERE name = 'a'), (SELECT id FROM images LIMIT 1), 'en', 'user', FALSE)");
        jdbcTemplate.execute("INSERT INTO users(email, username, firstname, lastname, university, career_id, profile_picture_id, language, roles, blocked) VALUES('user3@mail.com', 'user3', 'user', '3', (SELECT id FROM universities WHERE abbreviation = 'ITBA'), (SELECT id FROM careers WHERE name = 'a'), (SELECT id FROM images LIMIT 1), 'en', 'user', FALSE)");

        USER1 = jdbcTemplate.query("SELECT id FROM users WHERE lastname = '1'", (rs, n) -> new User(rs.getLong("id"), null, null, null, null, null, null, 0, null, false)).stream().findFirst().get();
        USER2 = jdbcTemplate.query("SELECT id FROM users WHERE lastname = '2'", (rs, n) -> new User(rs.getLong("id"), null, null, null, null, null, null, 0, null, false)).stream().findFirst().get();
        USER3 = jdbcTemplate.query("SELECT id FROM users WHERE lastname = '3'", (rs, n) -> new User(rs.getLong("id"), null, null, null, null, null, null, 0, null, false)).stream().findFirst().get();
        CITY1 = jdbcTemplate.query("SELECT id FROM cities WHERE name = 'Buenos Aires'", (rs, n) -> new City(null, null, rs.getLong("id"))).stream().findFirst().get();
        CITY2 = jdbcTemplate.query("SELECT id FROM cities WHERE name = 'Boston'", (rs, n) -> new City(null, null, rs.getLong("id"))).stream().findFirst().get();
        IMAGE_ID1 = jdbcTemplate.queryForObject("SELECT id FROM images LIMIT 1", Long.class).longValue();
        IMAGE_ID2 = jdbcTemplate.queryForObject("SELECT id FROM images WHERE id != ? LIMIT 1", Long.class, IMAGE_ID1).longValue();
    }

    @Test
    public void testCreate(){
        Event event = eventDao.create(USER1, CITY1, EVENT_DATE, EVENT_DESCRIPTION, IMAGE_ID1, EVENT_TITLE, EVENT_TIME, EVENT_ADDRESS, EVENT_ATTENDANCE_LIMIT);

        assertEqualsEvent(event);
    }
    @Test
    public void testCreateNoAddress(){
        Event event = eventDao.create(USER1, CITY1, EVENT_DATE, EVENT_DESCRIPTION, IMAGE_ID1, EVENT_TITLE, EVENT_TIME, null, EVENT_ATTENDANCE_LIMIT);

        assertEqualsEvent(event, Map.of("address", Optional.empty()));
    }
    @Test
    public void testCreateNoAddressNoLimit(){
        Event event = eventDao.create(USER1, CITY1, EVENT_DATE, EVENT_DESCRIPTION, IMAGE_ID1, EVENT_TITLE, EVENT_TIME, null, null);

        assertEqualsEvent(event, Map.of("address", Optional.empty(), "limit", Optional.empty()));
    }
    @Test
    public void testCreateNoAddressNoLimitNoTime(){
        Event event = eventDao.create(USER1, CITY1, EVENT_DATE, EVENT_DESCRIPTION, IMAGE_ID1, EVENT_TITLE, null, null, null);

        assertEqualsEvent(event, Map.of("address", Optional.empty(), "limit", Optional.empty(), "time", Optional.empty()));
    }
    @Test(expected = DataAccessException.class)
    public void testCreateWrongUser(){
        eventDao.create(new User(12341234, null, null, null, null, null, null, 0, null, false), CITY1, EVENT_DATE, EVENT_DESCRIPTION, 0, EVENT_TITLE, EVENT_TIME, null, EVENT_ATTENDANCE_LIMIT);
    }
    @Test(expected = DataAccessException.class)
    public void testCreateWrongCity(){
        eventDao.create(USER1, new City(null, null, 12341234), EVENT_DATE, EVENT_DESCRIPTION, IMAGE_ID1, EVENT_TITLE, EVENT_TIME, null, EVENT_ATTENDANCE_LIMIT);
    }
    @Test
    public void testCreateNoDescription(){
        Event event = eventDao.create(USER1, CITY1, EVENT_DATE, null, IMAGE_ID1, EVENT_TITLE, EVENT_TIME, EVENT_ADDRESS, EVENT_ATTENDANCE_LIMIT);

        assertEqualsEvent(event, Map.of("description", Optional.empty()));
    }
    @Test(expected = DataAccessException.class)
    public void testCreateWrongImage(){
        eventDao.create(USER1, CITY1, EVENT_DATE, EVENT_DESCRIPTION, 12341234, EVENT_TITLE, EVENT_TIME, EVENT_ADDRESS, EVENT_ATTENDANCE_LIMIT);
    }

    @Test
    public void testListByQueryAll(){
        insertEvent();
        insertEvent();
        insertEvent();
        insertEvent(Map.of("deleted", true));

        List<Event> results = eventDao.listByQuery(null, null);

        assertNotNull(results);
        assertEquals(3, results.size());
        for (Event e : results){
            assertEqualsEvent(e);
        }
    }
    @Test
    public void testListByQueryFilterCity(){
        insertEvent();
        insertEvent();
        insertEvent(Map.of("city", CITY2));
        insertEvent(Map.of("deleted", true));

        List<Event> results = eventDao.listByQuery(CITY1.getId(), null);

        assertNotNull(results);
        assertEquals(2, results.size());
        for (Event e : results){
            assertEqualsEvent(e);
        }
    }
    @Test
    public void testListByQueryFilterDate(){
        insertEvent();
        insertEvent();
        insertEvent(Map.of("date", EVENT_DATE.plusDays(-10)));
        insertEvent(Map.of("deleted", true));

        List<Event> results = eventDao.listByQuery(null, EVENT_DATE);

        assertNotNull(results);
        assertEquals(2, results.size());
        for (Event e : results){
            assertEqualsEvent(e);
        }
    }
    @Test
    public void testListByQueryFilterBoth(){
        insertEvent();
        insertEvent(Map.of("city", CITY2));
        insertEvent(Map.of("date", EVENT_DATE.plusDays(-10)));
        insertEvent(Map.of("deleted", true));

        List<Event> results = eventDao.listByQuery(CITY1.getId(), EVENT_DATE);

        assertNotNull(results);
        assertEquals(1, results.size());
        for (Event e : results){
            assertEqualsEvent(e);
        }
    }
    @Test
    public void testListByQueryInvalidCity(){
        insertEvent();
        insertEvent(Map.of("city", CITY2));
        insertEvent(Map.of("date", EVENT_DATE.plusDays(-10)));
        insertEvent(Map.of("deleted", true));

        List<Event> results = eventDao.listByQuery((long)12341234, EVENT_DATE);

        assertNotNull(results);
        assertEquals(0, results.size());
    }
    @Test
    public void testFindById(){
        long id = insertEvent();
        insertEvent(Map.of("title", "anotherEvent"));
        insertEvent(Map.of("title", "oneMore"));
        insertEvent(Map.of("deleted", true));

        Optional<Event> maybeEvent = eventDao.findById(id);

        assertNotNull(maybeEvent);
        assertTrue(maybeEvent.isPresent());
        assertEquals(id, maybeEvent.get().getId());
        assertEqualsEvent(maybeEvent.get());
    }
    @Test
    public void testFindByIdWrongId(){
        insertEvent();
        insertEvent(Map.of("title", "anotherEvent"));
        insertEvent(Map.of("title", "oneMore"));
        insertEvent(Map.of("deleted", true));

        Optional<Event> maybeEvent = eventDao.findById(12341234);

        assertNotNull(maybeEvent);
        assertFalse(maybeEvent.isPresent());
    }
    @Test
    public void testFindByIdNoEvents(){
        Optional<Event> maybeEvent = eventDao.findById(1);

        assertNotNull(maybeEvent);
        assertFalse(maybeEvent.isPresent());
    }    
    @Test
    public void testFindByIdDeleted(){
        long id = insertEvent(Map.of("deleted", true));

        Optional<Event> maybeEvent = eventDao.findById(id);

        assertNotNull(maybeEvent);
        assertFalse(maybeEvent.isPresent());
    }

    @Test
    public void testListAll(){
        Map<String, Object> event1 = Map.of();
        Map<String, Object> event2 = Map.of("title", "anotherEvent");
        Map<String, Object> event3 = Map.of("title", "oneMore", "date", EVENT_DATE.plusDays(10));
        long id1 = insertEvent(event1);
        long id2 = insertEvent(event2);
        long id3 = insertEvent(event3);
        Map<Long, Map<String, Object>> eventInfo = Map.of(id1, event1, id2, event2, id3, event3);
        
        List<Event> events = eventDao.listAll();
        
        assertNotNull(events);
        assertEquals(3, events.size());
        for (Event e : events) {
            assertEqualsEvent(e, eventInfo.get(e.getId()));
        }
    }
    @Test
    public void testListAllNoEvents(){
        List<Event> events = eventDao.listAll();
        
        assertNotNull(events);
        assertEquals(0, events.size());
    }

    @Test
    public void testGetEvents(){
        //get events by user email
        Map<String, Object> event1 = Map.of();
        Map<String, Object> event2 = Map.of("title", "Another cool event", "date", EVENT_DATE.plusDays(40), "limit", Optional.empty());
        long id1 = insertEvent(event1);
        long id2 = insertEvent(event2);
        Map<Long, Map<String, Object>> eventInfo = Map.of(id1, event1, id2, event2);
        insertEvent(Map.of("user", USER2));
        insertEvent(Map.of("user", USER2));
        
        List<Event> userEvents = eventDao.getEvents(USER1_EMAIL);
    
        assertNotNull(userEvents);
        assertEquals(2, userEvents.size());
        for (Event e : userEvents){
            assertEqualsEvent(e, eventInfo.get(e.getId()));
        }
    }
    @Test
    public void testGetEventsNoEvents(){
        //get events by user email    
        List<Event> userEvents = eventDao.getEvents(USER1_EMAIL);
    
        assertNotNull(userEvents);
        assertEquals(0, userEvents.size());
    }
    @Test
    public void testGetEventsWrongMail(){
        //get events by user email    
        insertEvent();

        List<Event> userEvents = eventDao.getEvents("USER1_EMAIL");
    
        assertNotNull(userEvents);
        assertEquals(0, userEvents.size());
    }

    @Test
    public void testGetTopEvents(){
        List<Long> ids = new ArrayList<>();
        //not full, 20, soon
        ids.add(insertEvent(Map.of("limit", Optional.of(30), "willAttend", 20)));
        //not full, 20, later
        ids.add(insertEvent(Map.of("limit", Optional.of(30), "willAttend", 20, "date", LATER_DATE)));
        //not full, 10, soon
        ids.add(insertEvent(Map.of("limit", Optional.of(30), "willAttend", 10)));
        //not full, 10, later
        ids.add(insertEvent(Map.of("limit", Optional.of(30), "willAttend", 10, "date", LATER_DATE)));
        //full, 20, soon
        ids.add(insertEvent(Map.of("limit", Optional.of(20), "willAttend", 20)));
        //full, 20, later
        ids.add(insertEvent(Map.of("limit", Optional.of(20), "willAttend", 20, "date", LATER_DATE)));
        //full, 10, soon
        ids.add(insertEvent(Map.of("limit", Optional.of(10), "willAttend", 10)));
        //full, 10, later
        ids.add(insertEvent(Map.of("limit", Optional.of(10), "willAttend", 10, "date", LATER_DATE)));

        Page<Event> events = eventDao.getTopEvents(1, 100);

        assertNotNull(events);
        assertNotNull(events.getContent());
        for (int i = 0; i < ids.size(); i++){
            assertEquals(ids.get(i).longValue(), events.getContent().get(i).getId());
        }
    }
    @Test
    public void testGetTopEventsNoEvents(){
        insertEvent(Map.of("deleted", true));
        insertEvent(Map.of("date", EVENT_DATE.plusDays(-100)));

        Page<Event> page1 = eventDao.getTopEvents(1,3);

        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(0, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(0, page1.getContent().size());
    }

    @Test
    public void testGetTopUserEvents(){
        List<Long> ids = new ArrayList<>();
        //00 not full, not attending, 20, soon
        ids.add(insertEvent(Map.of("limit", Optional.of(30), "willAttend", 20)));
        //01 not full, not attending, 20, later
        ids.add(insertEvent(Map.of("limit", Optional.of(30), "willAttend", 20, "date", LATER_DATE)));
        //02 not full, not attending, 10, soon
        ids.add(insertEvent(Map.of("limit", Optional.of(30), "willAttend", 10)));
        //03 not full, not attending, 10, later
        ids.add(insertEvent(Map.of("limit", Optional.of(30), "willAttend", 10, "date", LATER_DATE)));
        //04 not full, attending, 20, soon
        ids.add(insertEvent(Map.of("limit", Optional.of(30), "willAttend", 20, "attending", USER2)));
        //05 not full, attending, 20, later
        ids.add(insertEvent(Map.of("limit", Optional.of(30), "willAttend", 20, "attending", USER2, "date", LATER_DATE)));
        //06 not full, attending, 10, soon
        ids.add(insertEvent(Map.of("limit", Optional.of(30), "willAttend", 10, "attending", USER2)));
        //07 not full, attending, 10, later
        ids.add(insertEvent(Map.of("limit", Optional.of(30), "willAttend", 10, "attending", USER2, "date", LATER_DATE)));
        //08 full, 20, not attending, soon
        ids.add(insertEvent(Map.of("limit", Optional.of(20), "willAttend", 20)));
        //09 full, 20, not attending, later
        ids.add(insertEvent(Map.of("limit", Optional.of(20), "willAttend", 20, "date", LATER_DATE)));
        //10 full, 10, not attending, soon
        ids.add(insertEvent(Map.of("limit", Optional.of(10), "willAttend", 10)));
        //11 full, 10, not attending, later
        ids.add(insertEvent(Map.of("limit", Optional.of(10), "willAttend", 10, "date", LATER_DATE)));
        //12 full, 20, attending, soon
        ids.add(insertEvent(Map.of("limit", Optional.of(20), "willAttend", 20, "attending", USER2)));
        //13 full, 20, attending, later
        ids.add(insertEvent(Map.of("limit", Optional.of(20), "willAttend", 20, "attending", USER2, "date", LATER_DATE)));
        //14 full, 10, attending, soon
        ids.add(insertEvent(Map.of("limit", Optional.of(10), "willAttend", 10, "attending", USER2)));
        //15 full, 10, attending, later
        ids.add(insertEvent(Map.of("limit", Optional.of(10), "willAttend", 10, "attending", USER2, "date", LATER_DATE)));

        Page<UserEvent> events = eventDao.getTopUserEvents(USER2.getId(), 1, 100);

        assertNotNull(events);
        assertNotNull(events.getContent());
        assertEquals(16, events.getContent().size());
        for (int i = 0; i < ids.size(); i++){
            assertEquals(ids.get(i).longValue(), events.getContent().get(i).getEvent().getId());
        }
    }
    @Test
    public void testGetTopUserEventsNoEvents(){
        insertEvent(Map.of("deleted", true));
        insertEvent(Map.of("date", EVENT_DATE.plusDays(-100)));

        Page<UserEvent> page1 = eventDao.getTopUserEvents(USER2.getId(), 1,3);

        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(0, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(0, page1.getContent().size());
    }

    @Test
    public void testGetEventAttendanceLimit(){
        long id = insertEvent();

        Optional<Integer> limit = eventDao.getEventAttendanceLimit(id);

        assertNotNull(limit);
        assertTrue(limit.isPresent());
        assertEquals(EVENT_ATTENDANCE_LIMIT, limit.get().intValue());
    }
    @Test
    public void testGetEventAttendanceLimitNoLimit(){
        long id = insertEvent(Map.of("limit", Optional.empty()));

        Optional<Integer> limit = eventDao.getEventAttendanceLimit(id);

        assertNotNull(limit);
        assertFalse(limit.isPresent());
    }
    @Test(expected = DataAccessException.class)
    public void testGetEventAttendanceLimitWrongEvent(){
        insertEvent();

        eventDao.getEventAttendanceLimit(12341234);
    }

    @Test
    public void testGetFullEvents(){
        List<Event> emptyList = eventDao.getFullEvents();

        assertEquals(0, emptyList.size());
    }

    @Test
    public void testDelete(){
        long id1 = insertEvent();
        insertEvent(Map.of("deleted", true));
        int rowsBefore = JdbcTestUtils.countRowsInTable(jdbcTemplate, EVENT_TABLE);

        eventDao.delete(id1);

        assertEquals(rowsBefore, JdbcTestUtils.countRowsInTable(jdbcTemplate, EVENT_TABLE));
        assertEquals(0, jdbcTemplate.queryForObject("SELECT COUNT(*) FROM events WHERE deleted=FALSE", Integer.class).intValue());
    }
    @Test
    public void testDeleteDeleted(){
        insertEvent();
        long id2 = insertEvent(Map.of("deleted", true));
        int rowsBefore = JdbcTestUtils.countRowsInTable(jdbcTemplate, EVENT_TABLE);

        eventDao.delete(id2);

        assertEquals(rowsBefore, JdbcTestUtils.countRowsInTable(jdbcTemplate, EVENT_TABLE));
        assertEquals(1, jdbcTemplate.queryForObject("SELECT COUNT(*) FROM events WHERE deleted=FALSE", Integer.class).intValue());
    }
    @Test
    public void testDeleteWrongId(){
        insertEvent();
        insertEvent(Map.of("deleted", true));
        int rowsBefore = JdbcTestUtils.countRowsInTable(jdbcTemplate, EVENT_TABLE);

        eventDao.delete(12341234);

        assertEquals(rowsBefore, JdbcTestUtils.countRowsInTable(jdbcTemplate, EVENT_TABLE));
        assertEquals(1, jdbcTemplate.queryForObject("SELECT COUNT(*) FROM events WHERE deleted=FALSE", Integer.class).intValue());
    }

    @Test
    public void testDeletionMessage(){
        insertEvent();
        long id = insertEvent(Map.of("deleted", true));

        eventDao.deletionMessage(id, "DELETED");

        assertEquals("DELETED", jdbcTemplate.queryForObject("SELECT deleted_message FROM events WHERE id = ?", String.class, id));
    }
    @Test
    public void testDeletionMessageWrongId(){
        insertEvent();
        long id = insertEvent(Map.of("deleted", true));

        eventDao.deletionMessage(123123, "DELETED");

        assertEquals(null, jdbcTemplate.queryForObject("SELECT deleted_message FROM events WHERE id = ?", String.class, id));
    }

    @Test
    public void testGetMyEvents(){
        insertEvent();
        insertEvent();
        insertEvent(Map.of("title", "deleted event", "deleted", true));
        insertEvent(Map.of("user", USER2));
        insertEvent(Map.of("user", USER3));

        List<Event> events = eventDao.getMyEvents(USER1.getId());

        assertNotNull(events);
        assertEquals(2, events.size());
        for (Event e : events){
            assertEqualsEvent(e);
        }
    }
    @Test
    public void testGetMyEventsNoEvents(){
        insertEvent(Map.of("deleted", true));
        insertEvent(Map.of("user", USER2));
        insertEvent(Map.of("user", USER3));

        List<Event> events = eventDao.getMyEvents(USER1.getId());

        assertNotNull(events);
        assertEquals(0, events.size());
    }

    @Test
    public void testGetOthersEvents(){
        insertEvent();
        insertEvent();
        insertEvent(Map.of("title", "deleted event", "deleted", true));
        insertEvent(Map.of("user", USER2));
        insertEvent(Map.of("user", USER3, "date", EVENT_DATE.plusDays(1)));
        insertEvent(Map.of("user", USER3, "deleted", true, "title", "DELETED"));

        List<Event> events = eventDao.getOthersEvents(USER1.getId());

        assertNotNull(events);
        assertEquals(2, events.size());
        assertEqualsEvent(events.get(0), Map.of("user", USER3, "date", EVENT_DATE.plusDays(1)));
        assertEqualsEvent(events.get(1), Map.of("user", USER2));
    }
    @Test
    public void testGetOthersEventsNoEvents(){
        insertEvent();
        insertEvent();
        insertEvent(Map.of("title", "deleted event", "deleted", true));
        insertEvent(Map.of("user", USER3, "deleted", true, "title", "DELETED"));

        List<Event> events = eventDao.getOthersEvents(USER1.getId());

        assertNotNull(events);
        assertEquals(0, events.size());
    }

    @Test
    public void testGetOthersEventsPaged(){
        Map<String, Object> event1 = Map.of("user", USER2);
        Map<String, Object> event2 = Map.of("user", USER3, "date", EVENT_DATE.plusDays(1));
        Map<String, Object> event3 = Map.of("user", USER3, "date", EVENT_DATE.plusDays(2));
        insertEvent();
        insertEvent();
        insertEvent(Map.of("title", "deleted event", "deleted", true));
        long id1 = insertEvent(event1);
        long id2 = insertEvent(event2);
        long id3 = insertEvent(event3);
        insertEvent(Map.of("user", USER3, "deleted", true, "title", "DELETED"));
        Map<Long, Map<String, Object>> eventInfo = Map.of(id1, event1, id2, event2, id3, event3);

        Page<Event> page1 = eventDao.getOthersEvents(USER1.getId(), 1, 2);
        Page<Event> page2 = eventDao.getOthersEvents(USER1.getId(), 2, 2);

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
        List<Event> events = new ArrayList<>();
        events.addAll(page1.getContent());
        events.addAll(page2.getContent());
        for (Event e : events){
            assertEqualsEvent(e, eventInfo.get(e.getId()));
        }
    }
    @Test
    public void testGetOthersEventsPagedNoEvents(){
        insertEvent();
        insertEvent();
        insertEvent(Map.of("title", "deleted event", "deleted", true));
        insertEvent(Map.of("user", USER3, "deleted", true, "title", "DELETED"));

        Page<Event> page1 = eventDao.getOthersEvents(USER1.getId(), 1, 2);

        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(0, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(0, page1.getContent().size());
    }

    @Test
    public void testGetMyEventsPaged(){
        insertEvent();
        insertEvent();
        insertEvent();
        insertEvent(Map.of("title", "deleted event", "deleted", true));
        insertEvent(Map.of("user", USER2));
        insertEvent(Map.of("user", USER3));

        Page<Event> page1 = eventDao.getMyEvents(USER1.getId(), 1, 2);
        Page<Event> page2 = eventDao.getMyEvents(USER1.getId(), 2, 2);

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
        List<Event> events = new ArrayList<>();
        events.addAll(page1.getContent());
        events.addAll(page2.getContent());
        for (Event e : events){
            assertEqualsEvent(e);
        }
    }
    @Test
    public void testGetMyEventsPagedNoEvents(){
        insertEvent(Map.of("title", "deleted event", "deleted", true));
        insertEvent(Map.of("user", USER2));
        insertEvent(Map.of("user", USER3));

        Page<Event> page1 = eventDao.getMyEvents(USER1.getId(), 1, 2);

        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(0, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(0, page1.getContent().size());
    }

    @Test
    public void testGetEventsPaged(){
        //get events by user email
        Map<String, Object> event1 = Map.of();
        Map<String, Object> event2 = Map.of("title", "Another cool event", "date", EVENT_DATE.plusDays(40), "limit", Optional.empty());
        Map<String, Object> event3 = Map.of("title", "Also a cool event", "date", EVENT_DATE.plusDays(50), "limit", Optional.of(50), "time", Optional.empty());
        long id1 = insertEvent(event1);
        long id2 = insertEvent(event2);
        long id3 = insertEvent(event3);
        Map<Long, Map<String, Object>> eventInfo = Map.of(id1, event1, id2, event2, id3, event3);
        insertEvent(Map.of("user", USER2));
        insertEvent(Map.of("user", USER2));
        
        Page<Event> page1 = eventDao.getEvents(USER1_EMAIL, 1, 2);
        Page<Event> page2 = eventDao.getEvents(USER1_EMAIL, 2, 2);
    
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
        List<Event> events = new ArrayList<>();
        events.addAll(page1.getContent());
        events.addAll(page2.getContent());
        for (Event e : events){
            assertEqualsEvent(e, eventInfo.get(e.getId()));
        }
    }
    @Test
    public void testGetEventsNoEventsPaged(){
        //get events by user email    
        Page<Event> userEvents = eventDao.getEvents(USER1_EMAIL, 1, 2);
    
        assertNotNull(userEvents);
        assertEquals(1, userEvents.getCurrentPage());
        assertEquals(0, userEvents.getTotalPages());
        assertNotNull(userEvents.getContent());
        assertEquals(0, userEvents.getContent().size());

    }
    @Test
    public void testGetEventsWrongMailPaged(){
        //get events by user email    
        insertEvent();

        Page<Event> userEvents = eventDao.getEvents("USER1_EMAIL", 1, 2);
    
        assertNotNull(userEvents);
        assertEquals(1, userEvents.getCurrentPage());
        assertEquals(0, userEvents.getTotalPages());
        assertNotNull(userEvents.getContent());
        assertEquals(0, userEvents.getContent().size());
    }

    @Test
    public void testListAllPaged(){
        Map<String, Object> event1 = Map.of();
        Map<String, Object> event2 = Map.of("title", "anotherEvent");
        Map<String, Object> event3 = Map.of("title", "oneMore", "date", EVENT_DATE.plusDays(10));
        long id1 = insertEvent(event1);
        long id2 = insertEvent(event2);
        long id3 = insertEvent(event3);
        Map<Long, Map<String, Object>> eventInfo = Map.of(id1, event1, id2, event2, id3, event3);
        
        Page<Event> page1 = eventDao.listAll(1, 2);
        Page<Event> page2 = eventDao.listAll(2, 2);
        
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
        List<Event> events = new ArrayList<>();
        events.addAll(page1.getContent());
        events.addAll(page2.getContent());
        for (Event e : events){
            assertEqualsEvent(e, eventInfo.get(e.getId()));
        }
    }
    @Test
    public void testListAllNoEventsPaged(){
        Page<Event> events = eventDao.listAll(1, 2);
        
        assertNotNull(events);
        assertEquals(1, events.getCurrentPage());
        assertEquals(0, events.getTotalPages());
        assertNotNull(events.getContent());
        assertEquals(0, events.getContent().size());
    }

    @Test
    public void testSearchEventsPaged(){
        insertEvent();
        insertEvent();
        insertEvent();
        insertEvent(Map.of("deleted", true));
        insertEvent(Map.of("title", "another one"));
        insertEvent(Map.of("title", "another one"));

        Page<Event> page1 = eventDao.searchEvents(EVENT_TITLE.substring(0, 4), 1, 2);
        Page<Event> page2 = eventDao.searchEvents(EVENT_TITLE.substring(0, 4), 2, 2);

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
        List<Event> events = new ArrayList<>();
        events.addAll(page1.getContent());
        events.addAll(page2.getContent());
        for (Event e : events){
            assertEqualsEvent(e);
        }
    }
    @Test
    public void testSearchEventsPagedEmptyQuery(){
        insertEvent();
        insertEvent();
        insertEvent(Map.of("deleted", true));
        insertEvent(Map.of("title", "another one"));
        insertEvent(Map.of("title", "another one"));

        Page<Event> page1 = eventDao.searchEvents("", 1, 2);
        Page<Event> page2 = eventDao.searchEvents("", 2, 2);

        assertNotNull(page1);
        assertNotNull(page2);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(2, page2.getCurrentPage());
        assertEquals(2, page1.getTotalPages());
        assertEquals(2, page2.getTotalPages());
        assertNotNull(page1.getContent());
        assertNotNull(page2.getContent());
        assertEquals(2, page1.getContent().size());
        assertEquals(2, page2.getContent().size());
    }
    @Test
    public void testSearchEventsPagedWrongSearch(){
        insertEvent();
        insertEvent();
        insertEvent();
        insertEvent(Map.of("deleted", true));
        insertEvent(Map.of("title", "another one"));
        insertEvent(Map.of("title", "another one"));

        Page<Event> page1 = eventDao.searchEvents("EVENT_TITLE", 1, 2);

        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(0, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(0, page1.getContent().size());
    }
    @Test
    public void testSearchEventsPagedNoEvents(){
        Page<Event> page1 = eventDao.searchEvents("", 1, 2);

        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(0, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(0, page1.getContent().size());
    }

    @Test
    public void testGetEventsWithAttendanceStatusPaged(){
        Map<String, Object> event1 = Map.of("user", USER2, "attending", USER1);
        Map<String, Object> event2 = Map.of("user", USER2, "title", "another one", "attending", USER1);
        Map<String, Object> event3 = Map.of("user", USER2, "title", "best one");
        long id1 = insertEvent(event1);
        insertEvent(Map.of("user", USER2, "deleted", true));
        long id2 = insertEvent(event2);
        long id3 = insertEvent(event3);
        Map<Long, Map<String, Object>> eventInfo = Map.of(id1, event1, id2, event2, id3, event3);

        Page<UserEvent> page1 = eventDao.getEventsWithAttendanceStatus(USER1.getId(), 1, 2);
        Page<UserEvent> page2 = eventDao.getEventsWithAttendanceStatus(USER1.getId(), 2, 2);
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
        List<UserEvent> events = new ArrayList<>();
        events.addAll(page1.getContent());
        events.addAll(page2.getContent());
        for (UserEvent e : events){
            assertEquals(eventInfo.get(e.getEvent().getId()).get("attending") == USER1, e.isAttending());
            assertEqualsEvent(e.getEvent(), eventInfo.get(e.getEvent().getId()));
        }
    }
    @Test
    public void testGetEventsWithAttendanceStatus(){
        Map<String, Object> event1 = Map.of("user", USER2, "attending", USER1);
        Map<String, Object> event2 = Map.of("user", USER2, "title", "another one", "attending", USER1);
        Map<String, Object> event3 = Map.of("user", USER2, "title", "best one");
        long id1 = insertEvent(event1);
        insertEvent(Map.of("user", USER2, "deleted", true));
        long id2 = insertEvent(event2);
        long id3 = insertEvent(event3);
        Map<Long, Map<String, Object>> eventInfo = Map.of(id1, event1, id2, event2, id3, event3);

        List<UserEvent> events = eventDao.getEventsWithAttendanceStatus(USER1.getId());
        assertNotNull(events);
        for (UserEvent e : events){
            assertEquals(eventInfo.get(e.getEvent().getId()).get("attending") == USER1, e.isAttending());
            assertEqualsEvent(e.getEvent(), eventInfo.get(e.getEvent().getId()));
        }
    }

    @Test
    public void updateEvent(){
        long id = insertEvent();

        eventDao.updateData(
            CITY2.getId(), 
            EVENT_DATE.plusDays(30), 
            null, 
            "EVENT_TITLE", 
            null, 
            null, 
            null, 
            id,
            IMAGE_ID1
        );

        Optional<Event> maybeEvent = jdbcTemplate.query(
            "SELECT * FROM events WHERE id = ?", EVENT_ROW_MAPPER, id)
        .stream().findFirst();

        assertNotNull(maybeEvent);
        assertTrue(maybeEvent.isPresent());
        Event event = maybeEvent.get();
        assertEqualsEvent(event, Map.of(
            "city", CITY2, 
            "date", EVENT_DATE.plusDays(30), 
            "title", "EVENT_TITLE", 
            "description", Optional.empty(), 
            "time", Optional.empty(), 
            "address", Optional.empty(),
            "limit", Optional.empty()));
    }
    @Test
    public void updateEventFull(){
        long id = insertEvent();

        eventDao.updateData(
            CITY2.getId(), 
            EVENT_DATE.plusDays(30), 
            "EVENT_DESCRIPTION", 
            "EVENT_TITLE", 
            EVENT_TIME.plusHours(1), 
            "new Address", 
            100, 
            id,
            IMAGE_ID2
        );

        Optional<Event> maybeEvent = jdbcTemplate.query(
            "SELECT * FROM events WHERE id = ?", EVENT_ROW_MAPPER, id)
        .stream().findFirst();

        assertNotNull(maybeEvent);
        assertTrue(maybeEvent.isPresent());
        Event event = maybeEvent.get();
        assertEqualsEvent(event, Map.of(
            "city", CITY2, 
            "date", EVENT_DATE.plusDays(30), 
            "title", "EVENT_TITLE", 
            "description", Optional.of("EVENT_DESCRIPTION"), 
            "time", Optional.of(EVENT_TIME.plusHours(1)), 
            "address", Optional.of("new Address"),
            "limit", Optional.of(100),
            "image", IMAGE_ID2
        ));
    }
    @Test
    public void updateEventWrongId(){
        long id = insertEvent();

        eventDao.updateData(
            CITY1.getId(), 
            EVENT_DATE.plusDays(30), 
            "EVENT_DESCRIPTION", 
            "EVENT_TITLE", 
            null, 
            null, 
            null, 
            1231234,
            IMAGE_ID1
        );

        Optional<Event> maybeEvent = jdbcTemplate.query(
            "SELECT * FROM events WHERE id = ?", EVENT_ROW_MAPPER, id)
        .stream().findFirst();

        assertNotNull(maybeEvent);
        assertTrue(maybeEvent.isPresent());
        Event event = maybeEvent.get();
        assertEqualsEvent(event);
    }

    @Test
    public void testGetRecommendedEvents(){
        Map<String, Object> event1 = Map.of("user", USER2);
        Map<String, Object> event2 = Map.of("user", USER2, "attending", USER1);
        insertEvent();
        long id1 = insertEvent(event1);
        long id2 = insertEvent(event2);
        insertEvent(Map.of("user", USER2, "deleted", true));
        insertEvent(Map.of("user", USER2, "city", CITY2));
        insertEvent(Map.of("user", USER2, "date", LocalDate.now().plusDays(-2)));
        Map<Long, Map<String, Object>> eventInfo = Map.of(id1, event1, id2, event2);

        Page<UserEvent> events = eventDao.getRecommendedEvents(USER1.getId(), 1, 100);

        assertNotNull(events);
        assertEquals(1, events.getCurrentPage());
        assertEquals(1, events.getTotalPages());
        assertNotNull(events.getContent());
        assertEquals(2, events.getContent().size());
        for (UserEvent e : events.getContent()){
            assertEquals(eventInfo.get(e.getEvent().getId()).get("attending") == USER1, e.isAttending());
            assertEqualsEvent(e.getEvent(), eventInfo.get(e.getEvent().getId()));
        }
    }
    @Test
    public void testGetRecommendedEventsNoEvents(){
        insertEvent();
        insertEvent(Map.of("user", USER2, "deleted", true));
        insertEvent(Map.of("user", USER2, "date", LocalDate.now().plusDays(-2)));

        Page<UserEvent> events = eventDao.getRecommendedEvents(USER1.getId(),1, 100);

        assertNotNull(events);
        assertEquals(1, events.getCurrentPage());
        assertEquals(0, events.getTotalPages());
        assertNotNull(events.getContent());
        assertEquals(0, events.getContent().size());
    }
    @Test
    public void testGetRecommendedEventsWrongEmail(){
        insertEvent();
        insertEvent(Map.of("user", USER2, "deleted", true));
        insertEvent(Map.of("user", USER2, "date", LocalDate.now().plusDays(-2)));

        Page<UserEvent> events = eventDao.getRecommendedEvents(USER1.getId(),1, 100);

        assertNotNull(events);
        assertEquals(1, events.getCurrentPage());
        assertEquals(0, events.getTotalPages());
        assertNotNull(events.getContent());
        assertEquals(0, events.getContent().size());
    }
    @Test
    public void testGetRecommendedEventsOrder(){
        List<Long> ids = new ArrayList<>();
        //not full, not attending, 20, soon (first)
        ids.add(insertEvent(Map.of("user", USER2, "limit", Optional.of(30), "willAttend", 20)));
        //not full, not attending, 20, later (second)
        ids.add(insertEvent(Map.of("user", USER2, "limit", Optional.of(30), "willAttend", 20, "date", LATER_DATE)));
        //not full, not attending, 10, soon (third)
        ids.add(insertEvent(Map.of("user", USER2, "limit", Optional.of(30), "willAttend", 10)));
        //not full, not attending, 10, later (fourth)
        ids.add(insertEvent(Map.of("user", USER2, "limit", Optional.of(30), "willAttend", 10, "date", LATER_DATE)));
        //not full, attending, 20, soon (fifth)
        ids.add(insertEvent(Map.of("user", USER2, "limit", Optional.of(30), "willAttend", 20, "attending", USER1)));
        //not full, attending, 20, later (sixth)
        ids.add(insertEvent(Map.of("user", USER2, "limit", Optional.of(30), "willAttend", 20, "attending", USER1, "date", LATER_DATE)));
        //not full, attending, 10, soon (seventh)
        ids.add(insertEvent(Map.of("user", USER2, "limit", Optional.of(30), "willAttend", 10, "attending", USER1)));
        //not full, attending, 10, later (eigth)
        ids.add(insertEvent(Map.of("user", USER2, "limit", Optional.of(30), "willAttend", 10, "attending", USER1, "date", LATER_DATE)));
        //full, not attending, 20, soon (ninth)
        ids.add(insertEvent(Map.of("user", USER2, "limit", Optional.of(20), "willAttend", 20)));
        //full, not attending, 20, later (tenth)
        ids.add(insertEvent(Map.of("user", USER2, "limit", Optional.of(20), "willAttend", 20, "date", LATER_DATE)));
        //full, not attending, 10, soon (eleventh)
        ids.add(insertEvent(Map.of("user", USER2, "limit", Optional.of(10), "willAttend", 10)));
        //full, not attending, 10, later (twelfth)
        ids.add(insertEvent(Map.of("user", USER2, "limit", Optional.of(10), "willAttend", 10, "date", LATER_DATE)));
        //full, attending, 20, soon (thirteenth)
        ids.add(insertEvent(Map.of("user", USER2, "limit", Optional.of(20), "willAttend", 20, "attending", USER1)));
        //full, attending, 20, later (fourteenth)
        ids.add(insertEvent(Map.of("user", USER2, "limit", Optional.of(20), "willAttend", 20, "attending", USER1, "date", LATER_DATE)));
        //full, attending, 10, soon (fifteenth)
        ids.add(insertEvent(Map.of("user", USER2, "limit", Optional.of(10), "willAttend", 10, "attending", USER1)));
        //full, attending, 10, later (sixteenth)
        ids.add(insertEvent(Map.of("user", USER2, "limit", Optional.of(10), "willAttend", 10, "attending", USER1, "date", LATER_DATE)));

        Page<UserEvent> events = eventDao.getRecommendedEvents(USER1.getId(), 1, 100);

        assertNotNull(events);
        assertNotNull(events.getContent());
        assertEquals(1, events.getCurrentPage());
        assertEquals(1, events.getTotalPages());
        assertEquals(16, events.getContent().size());
        for (int i = 0; i < ids.size(); i++){
            assertEquals(ids.get(i).longValue(), events.getContent().get(i).getEvent().getId());
        }
    }
}
