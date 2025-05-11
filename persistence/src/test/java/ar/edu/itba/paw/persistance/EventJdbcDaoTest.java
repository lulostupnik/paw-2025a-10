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

import ar.edu.itba.paw.models.*;
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

    private static User USER_1;
    private static User USER_2;
    private static User USER_3;
    private static City CITY_1;
    private static City CITY_2;
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
        params.put("user_id", ((User)overrides.getOrDefault("user", USER_1)).getId());
        params.put("city_id", ((City)overrides.getOrDefault("city", CITY_1)).getId());
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
        assertEquals(((User)overrides.getOrDefault("user", USER_1)).getId(), event.getUser().getId());
        assertEquals(((City)overrides.getOrDefault("city", CITY_1)).getId(), event.getEventCity().getId());
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

        USER_1 = jdbcTemplate.queryForObject(TestUtils.USER_SELECT_BY_EMAIL, TestUtils.USER_ROW_MAPPER, TestUtils.USER_1_MAIL);
        USER_2 = jdbcTemplate.queryForObject(TestUtils.USER_SELECT_BY_EMAIL, TestUtils.USER_ROW_MAPPER, TestUtils.USER_2_MAIL);
        USER_3 = jdbcTemplate.queryForObject(TestUtils.USER_SELECT_BY_EMAIL, TestUtils.USER_ROW_MAPPER, TestUtils.USER_3_MAIL);
        CITY_1 = jdbcTemplate.queryForObject(TestUtils.CITY_SELECT_BY_NAME, TestUtils.CITY_ROW_MAPPER, TestUtils.CITY_1_NAME);
        CITY_2 = jdbcTemplate.queryForObject(TestUtils.CITY_SELECT_BY_NAME, TestUtils.CITY_ROW_MAPPER, TestUtils.CITY_2_NAME);
        IMAGE_ID1 = jdbcTemplate.queryForObject("SELECT id FROM images LIMIT 1", Long.class).longValue();
        IMAGE_ID2 = jdbcTemplate.queryForObject("SELECT id FROM images WHERE id != ? LIMIT 1", Long.class, IMAGE_ID1).longValue();
    }

    @Test
    public void testCreate(){
        Event event = eventDao.create(USER_1, CITY_1, EVENT_DATE, EVENT_DESCRIPTION, IMAGE_ID1, EVENT_TITLE, EVENT_TIME, EVENT_ADDRESS, EVENT_ATTENDANCE_LIMIT);

        assertEqualsEvent(event);
    }
    @Test
    public void testCreateNoAddress(){
        Event event = eventDao.create(USER_1, CITY_1, EVENT_DATE, EVENT_DESCRIPTION, IMAGE_ID1, EVENT_TITLE, EVENT_TIME, null, EVENT_ATTENDANCE_LIMIT);

        assertEqualsEvent(event, Map.of("address", Optional.empty()));
    }
    @Test
    public void testCreateNoAddressNoLimit(){
        Event event = eventDao.create(USER_1, CITY_1, EVENT_DATE, EVENT_DESCRIPTION, IMAGE_ID1, EVENT_TITLE, EVENT_TIME, null, null);

        assertEqualsEvent(event, Map.of("address", Optional.empty(), "limit", Optional.empty()));
    }
    @Test
    public void testCreateNoAddressNoLimitNoTime(){
        Event event = eventDao.create(USER_1, CITY_1, EVENT_DATE, EVENT_DESCRIPTION, IMAGE_ID1, EVENT_TITLE, null, null, null);

        assertEqualsEvent(event, Map.of("address", Optional.empty(), "limit", Optional.empty(), "time", Optional.empty()));
    }
    @Test(expected = DataAccessException.class)
    public void testCreateWrongUser(){
        eventDao.create(new User(12341234, null, null, null, null, null, null, 0, null, false), CITY_1, EVENT_DATE, EVENT_DESCRIPTION, 0, EVENT_TITLE, EVENT_TIME, null, EVENT_ATTENDANCE_LIMIT);
    }
    @Test(expected = DataAccessException.class)
    public void testCreateWrongCity(){
        eventDao.create(USER_1, new City(null, null, 12341234), EVENT_DATE, EVENT_DESCRIPTION, IMAGE_ID1, EVENT_TITLE, EVENT_TIME, null, EVENT_ATTENDANCE_LIMIT);
    }
    @Test
    public void testCreateNoDescription(){
        Event event = eventDao.create(USER_1, CITY_1, EVENT_DATE, null, IMAGE_ID1, EVENT_TITLE, EVENT_TIME, EVENT_ADDRESS, EVENT_ATTENDANCE_LIMIT);

        assertEqualsEvent(event, Map.of("description", Optional.empty()));
    }
    @Test(expected = DataAccessException.class)
    public void testCreateWrongImage(){
        eventDao.create(USER_1, CITY_1, EVENT_DATE, EVENT_DESCRIPTION, 12341234, EVENT_TITLE, EVENT_TIME, EVENT_ADDRESS, EVENT_ATTENDANCE_LIMIT);
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
    public void testFindTop(){
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

        Page<Event> events = eventDao.findTop(TestUtils.PAGE_1_BIG);

        assertNotNull(events);
        assertNotNull(events.getContent());
        //TODO trad for-loop
        for (int i = 0; i < ids.size(); i++){
            assertEquals(ids.get(i).longValue(), events.getContent().get(i).getId());
        }
    }
    @Test
    public void testFindTopEventsNo(){
        insertEvent(Map.of("deleted", true));
        insertEvent(Map.of("date", EVENT_DATE.plusDays(-100)));

        Page<Event> page1 = eventDao.findTop(new PageParams(1,3));

        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(0, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(0, page1.getContent().size());
    }

    @Test
    public void testFindTopByUser(){
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
        ids.add(insertEvent(Map.of("limit", Optional.of(30), "willAttend", 20, "attending", USER_2)));
        //05 not full, attending, 20, later
        ids.add(insertEvent(Map.of("limit", Optional.of(30), "willAttend", 20, "attending", USER_2, "date", LATER_DATE)));
        //06 not full, attending, 10, soon
        ids.add(insertEvent(Map.of("limit", Optional.of(30), "willAttend", 10, "attending", USER_2)));
        //07 not full, attending, 10, later
        ids.add(insertEvent(Map.of("limit", Optional.of(30), "willAttend", 10, "attending", USER_2, "date", LATER_DATE)));
        //08 full, 20, not attending, soon
        ids.add(insertEvent(Map.of("limit", Optional.of(20), "willAttend", 20)));
        //09 full, 20, not attending, later
        ids.add(insertEvent(Map.of("limit", Optional.of(20), "willAttend", 20, "date", LATER_DATE)));
        //10 full, 10, not attending, soon
        ids.add(insertEvent(Map.of("limit", Optional.of(10), "willAttend", 10)));
        //11 full, 10, not attending, later
        ids.add(insertEvent(Map.of("limit", Optional.of(10), "willAttend", 10, "date", LATER_DATE)));
        //12 full, 20, attending, soon
        ids.add(insertEvent(Map.of("limit", Optional.of(20), "willAttend", 20, "attending", USER_2)));
        //13 full, 20, attending, later
        ids.add(insertEvent(Map.of("limit", Optional.of(20), "willAttend", 20, "attending", USER_2, "date", LATER_DATE)));
        //14 full, 10, attending, soon
        ids.add(insertEvent(Map.of("limit", Optional.of(10), "willAttend", 10, "attending", USER_2)));
        //15 full, 10, attending, later
        ids.add(insertEvent(Map.of("limit", Optional.of(10), "willAttend", 10, "attending", USER_2, "date", LATER_DATE)));

        Page<Event> events = eventDao.findTopByUser(USER_2.getId(), TestUtils.PAGE_1_BIG);

        assertNotNull(events);
        assertNotNull(events.getContent());
        assertEquals(16, events.getContent().size());

        //TODO trad for-loop
        for (int i = 0; i < ids.size(); i++){
            assertEquals(ids.get(i).longValue(), events.getContent().get(i).getId());
        }
    }
    @Test
    public void testFindTopByUserEventsNo(){
        insertEvent(Map.of("deleted", true));
        insertEvent(Map.of("date", EVENT_DATE.plusDays(-100)));

        Page<Event> page1 = eventDao.findTopByUser(USER_2.getId(), new PageParams(1, 3));

        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(0, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(0, page1.getContent().size());
    }

    @Test
    public void testFindAttendanceLimitById(){
        long id = insertEvent();

        Optional<Integer> limit = eventDao.findAttendanceLimitById(id);

        assertNotNull(limit);
        assertTrue(limit.isPresent());
        assertEquals(EVENT_ATTENDANCE_LIMIT, limit.get().intValue());
    }
    @Test
    public void testFindAttendanceLimitNoLimitById(){
        long id = insertEvent(Map.of("limit", Optional.empty()));

        Optional<Integer> limit = eventDao.findAttendanceLimitById(id);

        assertNotNull(limit);
        assertFalse(limit.isPresent());
    }
    @Test(expected = DataAccessException.class)
    public void testFindAttendanceLimitById2(){
        insertEvent();

        eventDao.findAttendanceLimitById(12341234);
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
    public void testUpdateDeletionMessage(){
        insertEvent();
        long id = insertEvent(Map.of("deleted", true));

        eventDao.updateDeletionMessage(id, "DELETED");

        assertEquals("DELETED", jdbcTemplate.queryForObject("SELECT deleted_message FROM events WHERE id = ?", String.class, id));
    }
    @Test
    public void testUpdateDeletionMessageWrongId(){
        insertEvent();
        long id = insertEvent(Map.of("deleted", true));

        eventDao.updateDeletionMessage(123123, "DELETED");

        assertEquals(null, jdbcTemplate.queryForObject("SELECT deleted_message FROM events WHERE id = ?", String.class, id));
    }



    @Test
    public void testFindByUserEmailPaged(){
        //get events by user email
        Map<String, Object> event1 = Map.of();
        Map<String, Object> event2 = Map.of("title", "Another cool event", "date", EVENT_DATE.plusDays(40), "limit", Optional.empty());
        Map<String, Object> event3 = Map.of("title", "Also a cool event", "date", EVENT_DATE.plusDays(50), "limit", Optional.of(50), "time", Optional.empty());
        long id1 = insertEvent(event1);
        long id2 = insertEvent(event2);
        long id3 = insertEvent(event3);
        Map<Long, Map<String, Object>> eventInfo = Map.of(id1, event1, id2, event2, id3, event3);
        insertEvent(Map.of("user", USER_2));
        insertEvent(Map.of("user", USER_2));
        
        Page<Event> page1 = eventDao.findByUserEmail(USER1_EMAIL, new PageParams(1, 2));
        Page<Event> page2 = eventDao.findByUserEmail(USER1_EMAIL, new PageParams(2,2));
    
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
    public void testFindByUserEmailPaged2(){
        //get events by user email    
        Page<Event> userEvents = eventDao.findByUserEmail(USER1_EMAIL,new PageParams( 1, 2));
    
        assertNotNull(userEvents);
        assertEquals(1, userEvents.getCurrentPage());
        assertEquals(0, userEvents.getTotalPages());
        assertNotNull(userEvents.getContent());
        assertEquals(0, userEvents.getContent().size());

    }
    @Test
    public void testFindByUserEmailWrongMailPaged(){
        //get events by user email    
        insertEvent();

        Page<Event> userEvents = eventDao.findByUserEmail("USER1_EMAIL", new PageParams(1, 2));
    
        assertNotNull(userEvents);
        assertEquals(1, userEvents.getCurrentPage());
        assertEquals(0, userEvents.getTotalPages());
        assertNotNull(userEvents.getContent());
        assertEquals(0, userEvents.getContent().size());
    }

    @Test
    public void testFindAllPaged(){
        Map<String, Object> event1 = Map.of();
        Map<String, Object> event2 = Map.of("title", "anotherEvent");
        Map<String, Object> event3 = Map.of("title", "oneMore", "date", EVENT_DATE.plusDays(10));
        long id1 = insertEvent(event1);
        long id2 = insertEvent(event2);
        long id3 = insertEvent(event3);
        Map<Long, Map<String, Object>> eventInfo = Map.of(id1, event1, id2, event2, id3, event3);
        
        Page<Event> page1 = eventDao.findAll(new PageParams(1, 2));
        Page<Event> page2 = eventDao.findAll(new PageParams(2,2));
        
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
    public void testFindAllNoEventsPaged(){
        Page<Event> events = eventDao.findAll(new PageParams(1,2));
        
        assertNotNull(events);
        assertEquals(1, events.getCurrentPage());
        assertEquals(0, events.getTotalPages());
        assertNotNull(events.getContent());
        assertEquals(0, events.getContent().size());
    }

    @Test
    public void testSearchPaged(){
        insertEvent();
        insertEvent();
        insertEvent();
        insertEvent(Map.of("deleted", true));
        insertEvent(Map.of("title", "another one"));
        insertEvent(Map.of("title", "another one"));

        Page<Event> page1 = eventDao.search(EVENT_TITLE.substring(0, 4), new PageParams(1,2));
        Page<Event> page2 = eventDao.search(EVENT_TITLE.substring(0, 4), new PageParams(2, 2));

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
    public void testSearchPagedEmptyQuery(){
        insertEvent();
        insertEvent();
        insertEvent(Map.of("deleted", true));
        insertEvent(Map.of("title", "another one"));
        insertEvent(Map.of("title", "another one"));

        Page<Event> page1 = eventDao.search("", new PageParams(1,2));
        Page<Event> page2 = eventDao.search("", new PageParams(2, 2));

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

        Page<Event> page1 = eventDao.search("EVENT_TITLE", new PageParams(1,2));

        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(0, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(0, page1.getContent().size());
    }
    @Test
    public void testSearchEventsPagedNo(){
        Page<Event> page1 = eventDao.search("", new PageParams(1,2));

        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(0, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(0, page1.getContent().size());
    }

//    @Test
//    public void testGetEventsWithAttendanceStatusPaged(){
//        Map<String, Object> event1 = Map.of("user", USER2, "attending", USER1);
//        Map<String, Object> event2 = Map.of("user", USER2, "title", "another one", "attending", USER1);
//        Map<String, Object> event3 = Map.of("user", USER2, "title", "best one");
//        long id1 = insertEvent(event1);
//        insertEvent(Map.of("user", USER2, "deleted", true));
//        long id2 = insertEvent(event2);
//        long id3 = insertEvent(event3);
//        Map<Long, Map<String, Object>> eventInfo = Map.of(id1, event1, id2, event2, id3, event3);
//
//        Page<UserEvent> page1 = eventDao.getEventsWithAttendanceStatus(USER1.getId(), 1, 2);
//        Page<UserEvent> page2 = eventDao.getEventsWithAttendanceStatus(USER1.getId(), 2, 2);
//        assertNotNull(page1);
//        assertNotNull(page2);
//        assertEquals(1, page1.getCurrentPage());
//        assertEquals(2, page2.getCurrentPage());
//        assertEquals(2, page1.getTotalPages());
//        assertEquals(2, page2.getTotalPages());
//        assertNotNull(page1.getContent());
//        assertNotNull(page2.getContent());
//        assertEquals(2, page1.getContent().size());
//        assertEquals(1, page2.getContent().size());
//        List<UserEvent> events = new ArrayList<>();
//        events.addAll(page1.getContent());
//        events.addAll(page2.getContent());
//        for (UserEvent e : events){
//            assertEquals(eventInfo.get(e.getEvent().getId()).get("attending") == USER1, e.isAttending());
//            assertEqualsEvent(e.getEvent(), eventInfo.get(e.getEvent().getId()));
//        }
//    }

//    @Test
//    public void testGetEventsWithAttendanceStatusPagedSearch(){
//        Map<String, Object> event1 = Map.of("user", USER2, "attending", USER1);
//        Map<String, Object> event2 = Map.of("user", USER2, "title", "another one", "attending", USER1);
//        Map<String, Object> event3 = Map.of("user", USER2, "title", "best one");
//        long id1 = insertEvent(event1);
//        insertEvent(Map.of("user", USER2, "deleted", true));
//        long id2 = insertEvent(event2);
//        long id3 = insertEvent(event3);
//        Map<Long, Map<String, Object>> eventInfo = Map.of(id1, event1, id2, event2, id3, event3);
//
//        Page<UserEvent> page1 = eventDao.getEventsWithAttendanceStatus(USER1.getId(), null, 1, 2);
//        Page<UserEvent> page2 = eventDao.getEventsWithAttendanceStatus(USER1.getId(), null, 2, 2);
//        assertNotNull(page1);
//        assertNotNull(page2);
//        assertEquals(1, page1.getCurrentPage());
//        assertEquals(2, page2.getCurrentPage());
//        assertEquals(2, page1.getTotalPages());
//        assertEquals(2, page2.getTotalPages());
//        assertNotNull(page1.getContent());
//        assertNotNull(page2.getContent());
//        assertEquals(2, page1.getContent().size());
//        assertEquals(1, page2.getContent().size());
//        List<UserEvent> events = new ArrayList<>();
//        events.addAll(page1.getContent());
//        events.addAll(page2.getContent());
//        for (UserEvent e : events){
//            assertEquals(eventInfo.get(e.getEvent().getId()).get("attending") == USER1, e.isAttending());
//            assertEqualsEvent(e.getEvent(), eventInfo.get(e.getEvent().getId()));
//        }
//    }
//    @Test
//    public void testGetEventsWithAttendanceStatusPagedSearchNoUser(){
//        Map<String, Object> event1 = Map.of("user", USER2, "attending", USER1);
//        Map<String, Object> event2 = Map.of("user", USER2, "title", "another one", "attending", USER1);
//        Map<String, Object> event3 = Map.of("user", USER2, "title", "best one");
//        long id1 = insertEvent(event1);
//        insertEvent(Map.of("user", USER2, "deleted", true));
//        long id2 = insertEvent(event2);
//        long id3 = insertEvent(event3);
//        Map<Long, Map<String, Object>> eventInfo = Map.of(id1, event1, id2, event2, id3, event3);
//
//        Page<UserEvent> page1 = eventDao.getEventsWithAttendanceStatus(null, null, 1, 2);
//        Page<UserEvent> page2 = eventDao.getEventsWithAttendanceStatus(null, null, 2, 2);
//        assertNotNull(page1);
//        assertNotNull(page2);
//        assertEquals(1, page1.getCurrentPage());
//        assertEquals(2, page2.getCurrentPage());
//        assertEquals(2, page1.getTotalPages());
//        assertEquals(2, page2.getTotalPages());
//        assertNotNull(page1.getContent());
//        assertNotNull(page2.getContent());
//        assertEquals(2, page1.getContent().size());
//        assertEquals(1, page2.getContent().size());
//        List<UserEvent> events = new ArrayList<>();
//        events.addAll(page1.getContent());
//        events.addAll(page2.getContent());
//        for (UserEvent e : events){
//            assertEqualsEvent(e.getEvent(), eventInfo.get(e.getEvent().getId()));
//        }
//    }

//    @Test
//    public void testGetEventsWithAttendanceStatus(){
//        Map<String, Object> event1 = Map.of("user", USER2, "attending", USER1);
//        Map<String, Object> event2 = Map.of("user", USER2, "title", "another one", "attending", USER1);
//        Map<String, Object> event3 = Map.of("user", USER2, "title", "best one");
//        long id1 = insertEvent(event1);
//        insertEvent(Map.of("user", USER2, "deleted", true));
//        long id2 = insertEvent(event2);
//        long id3 = insertEvent(event3);
//        Map<Long, Map<String, Object>> eventInfo = Map.of(id1, event1, id2, event2, id3, event3);
//
//        List<Event> events = eventDao.getEventsWithAttendanceStatus(USER1.getId());
//        assertNotNull(events);
//        for (Event e : events){
//            assertEquals(eventInfo.get(e.getId()).get("attending") == USER1, e.isAttending());
//            assertEqualsEvent(e.getEvent(), eventInfo.get(e.getEvent().getId()));
//        }
//    }

    @Test
    public void updateEvent(){
        long id = insertEvent();

        eventDao.update(
            CITY_2.getId(), 
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
            "city", CITY_2, 
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

        eventDao.update(
            CITY_2.getId(), 
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
            "city", CITY_2, 
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

        eventDao.update(
            CITY_1.getId(), 
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
    public void testFindRecommended(){
        Map<String, Object> event1 = Map.of("user", USER_2);
        Map<String, Object> event2 = Map.of("user", USER_2, "attending", USER_1);
        insertEvent();
        long id1 = insertEvent(event1);
        long id2 = insertEvent(event2);
        insertEvent(Map.of("user", USER_2, "deleted", true));
        insertEvent(Map.of("user", USER_2, "city", CITY_2));
        insertEvent(Map.of("user", USER_2, "date", LocalDate.now().plusDays(-2)));
        Map<Long, Map<String, Object>> eventInfo = Map.of(id1, event1, id2, event2);

        Page<Event> events = eventDao.findRecommended(USER_1.getId(), TestUtils.PAGE_1_BIG);

        assertNotNull(events);
        assertEquals(1, events.getCurrentPage());
        assertEquals(1, events.getTotalPages());
        assertNotNull(events.getContent());
        assertEquals(2, events.getContent().size());
        for (Event e : events.getContent()){
            assertEqualsEvent(e, eventInfo.get(e.getId()));
        }
    }
    @Test
    public void testFindRecommendedEventsNo(){
        insertEvent();
        insertEvent(Map.of("user", USER_2, "deleted", true));
        insertEvent(Map.of("user", USER_2, "date", LocalDate.now().plusDays(-2)));

        Page<Event> events = eventDao.findRecommended(USER_1.getId(), TestUtils.PAGE_1_BIG);

        assertNotNull(events);
        assertEquals(1, events.getCurrentPage());
        assertEquals(0, events.getTotalPages());
        assertNotNull(events.getContent());
        assertEquals(0, events.getContent().size());
    }
    @Test
    public void testFindRecommendedWrongEmail(){
        insertEvent();
        insertEvent(Map.of("user", USER_2, "deleted", true));
        insertEvent(Map.of("user", USER_2, "date", LocalDate.now().plusDays(-2)));

        Page<Event> events = eventDao.findRecommended(USER_1.getId(), TestUtils.PAGE_1_BIG);

        assertNotNull(events);
        assertEquals(1, events.getCurrentPage());
        assertEquals(0, events.getTotalPages());
        assertNotNull(events.getContent());
        assertEquals(0, events.getContent().size());
    }
    @Test
    public void testFindRecommendedOrder(){
        List<Long> ids = new ArrayList<>();
        //not full, not attending, 20, soon (first)
        ids.add(insertEvent(Map.of("user", USER_2, "limit", Optional.of(30), "willAttend", 20)));
        //not full, not attending, 20, later (second)
        ids.add(insertEvent(Map.of("user", USER_2, "limit", Optional.of(30), "willAttend", 20, "date", LATER_DATE)));
        //not full, not attending, 10, soon (third)
        ids.add(insertEvent(Map.of("user", USER_2, "limit", Optional.of(30), "willAttend", 10)));
        //not full, not attending, 10, later (fourth)
        ids.add(insertEvent(Map.of("user", USER_2, "limit", Optional.of(30), "willAttend", 10, "date", LATER_DATE)));
        //not full, attending, 20, soon (fifth)
        ids.add(insertEvent(Map.of("user", USER_2, "limit", Optional.of(30), "willAttend", 20, "attending", USER_1)));
        //not full, attending, 20, later (sixth)
        ids.add(insertEvent(Map.of("user", USER_2, "limit", Optional.of(30), "willAttend", 20, "attending", USER_1, "date", LATER_DATE)));
        //not full, attending, 10, soon (seventh)
        ids.add(insertEvent(Map.of("user", USER_2, "limit", Optional.of(30), "willAttend", 10, "attending", USER_1)));
        //not full, attending, 10, later (eigth)
        ids.add(insertEvent(Map.of("user", USER_2, "limit", Optional.of(30), "willAttend", 10, "attending", USER_1, "date", LATER_DATE)));
        //full, not attending, 20, soon (ninth)
        ids.add(insertEvent(Map.of("user", USER_2, "limit", Optional.of(20), "willAttend", 20)));
        //full, not attending, 20, later (tenth)
        ids.add(insertEvent(Map.of("user", USER_2, "limit", Optional.of(20), "willAttend", 20, "date", LATER_DATE)));
        //full, not attending, 10, soon (eleventh)
        ids.add(insertEvent(Map.of("user", USER_2, "limit", Optional.of(10), "willAttend", 10)));
        //full, not attending, 10, later (twelfth)
        ids.add(insertEvent(Map.of("user", USER_2, "limit", Optional.of(10), "willAttend", 10, "date", LATER_DATE)));
        //full, attending, 20, soon (thirteenth)
        ids.add(insertEvent(Map.of("user", USER_2, "limit", Optional.of(20), "willAttend", 20, "attending", USER_1)));
        //full, attending, 20, later (fourteenth)
        ids.add(insertEvent(Map.of("user", USER_2, "limit", Optional.of(20), "willAttend", 20, "attending", USER_1, "date", LATER_DATE)));
        //full, attending, 10, soon (fifteenth)
        ids.add(insertEvent(Map.of("user", USER_2, "limit", Optional.of(10), "willAttend", 10, "attending", USER_1)));
        //full, attending, 10, later (sixteenth)
        ids.add(insertEvent(Map.of("user", USER_2, "limit", Optional.of(10), "willAttend", 10, "attending", USER_1, "date", LATER_DATE)));

        Page<Event> events = eventDao.findRecommended(USER_1.getId(), TestUtils.PAGE_1_BIG);

        assertNotNull(events);
        assertNotNull(events.getContent());
        assertEquals(1, events.getCurrentPage());
        assertEquals(1, events.getTotalPages());
        assertEquals(16, events.getContent().size());

        //TODO trad for-loop
        for (int i = 0; i < ids.size(); i++){
            assertEquals(ids.get(i).longValue(), events.getContent().get(i).getId());
        }
    }
}

//
//    @Test
//    public void testGetOthersEventsPaged(){
//        Map<String, Object> event1 = Map.of("user", USER_2);
//        Map<String, Object> event2 = Map.of("user", USER_3, "date", EVENT_DATE.plusDays(1));
//        Map<String, Object> event3 = Map.of("user", USER_3, "date", EVENT_DATE.plusDays(2));
//        insertEvent();
//        insertEvent();
//        insertEvent(Map.of("title", "deleted event", "deleted", true));
//        long id1 = insertEvent(event1);
//        long id2 = insertEvent(event2);
//        long id3 = insertEvent(event3);
//        insertEvent(Map.of("user", USER_3, "deleted", true, "title", "DELETED"));
//        Map<Long, Map<String, Object>> eventInfo = Map.of(id1, event1, id2, event2, id3, event3);
//
//        Page<Event> page1 = eventDao.getOthersEvents(USER_1.getId(), TestUtils.PAGE_1_DEFAULT);
//        Page<Event> page2 = eventDao.getOthersEvents(USER_1.getId(), TestUtils.PAGE_2_DEFAULT);
//
//        assertNotNull(page1);
//        assertNotNull(page2);
//        assertEquals(1, page1.getCurrentPage());
//        assertEquals(2, page2.getCurrentPage());
//        assertEquals(2, page1.getTotalPages());
//        assertEquals(2, page2.getTotalPages());
//        assertNotNull(page1.getContent());
//        assertNotNull(page2.getContent());
//        assertEquals(2, page1.getContent().size());
//        assertEquals(1, page2.getContent().size());
//        List<Event> events = new ArrayList<>();
//        events.addAll(page1.getContent());
//        events.addAll(page2.getContent());
//        for (Event e : events){
//            assertEqualsEvent(e, eventInfo.get(e.getId()));
//        }
//    }
//    @Test
//    public void testGetOthersEventsPagedNoEvents(){
//        insertEvent();
//        insertEvent();
//        insertEvent(Map.of("title", "deleted event", "deleted", true));
//        insertEvent(Map.of("user", USER_3, "deleted", true, "title", "DELETED"));
//
//        Page<Event> page1 = eventDao.getOthersEvents(USER_1.getId(), TestUtils.PAGE_1_DEFAULT);
//
//        assertNotNull(page1);
//        assertEquals(1, page1.getCurrentPage());
//        assertEquals(0, page1.getTotalPages());
//        assertNotNull(page1.getContent());
//        assertEquals(0, page1.getContent().size());
//    }


//
//    @Test
//    public void testFindByUserIdPaged(){
//        insertEvent();
//        insertEvent();
//        insertEvent();
//        insertEvent(Map.of("title", "deleted event", "deleted", true));
//        insertEvent(Map.of("user", USER_2));
//        insertEvent(Map.of("user", USER_3));
//
//        Page<Event> page1 = eventDao.findByUserId(USER_1.getId(), TestUtils.PAGE_1_DEFAULT);
//        Page<Event> page2 = eventDao.findByUserId(USER_1.getId(), TestUtils.PAGE_2_DEFAULT);
//
//        assertNotNull(page1);
//        assertNotNull(page2);
//        assertEquals(1, page1.getCurrentPage());
//        assertEquals(2, page2.getCurrentPage());
//        assertEquals(2, page1.getTotalPages());
//        assertEquals(2, page2.getTotalPages());
//        assertNotNull(page1.getContent());
//        assertNotNull(page2.getContent());
//        assertEquals(2, page1.getContent().size());
//        assertEquals(1, page2.getContent().size());
//        List<Event> events = new ArrayList<>();
//        events.addAll(page1.getContent());
//        events.addAll(page2.getContent());
//        for (Event e : events){
//            assertEqualsEvent(e);
//        }
//    }
//    @Test
//    public void testFindByUserId(){
//        insertEvent(Map.of("title", "deleted event", "deleted", true));
//        insertEvent(Map.of("user", USER_2));
//        insertEvent(Map.of("user", USER_3));
//
//        Page<Event> page1 = eventDao.findByUserId(USER_1.getId(), TestUtils.PAGE_1_DEFAULT);
//
//        assertNotNull(page1);
//        assertEquals(1, page1.getCurrentPage());
//        assertEquals(0, page1.getTotalPages());
//        assertNotNull(page1.getContent());
//        assertEquals(0, page1.getContent().size());
//    }
