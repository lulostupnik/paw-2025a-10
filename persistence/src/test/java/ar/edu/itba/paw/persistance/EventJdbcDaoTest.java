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

    private static User USER_1;
    private static User USER_2;
    private static City CITY_1;
    private static City CITY_2;
    private static Image IMAGE_1;
    private static Image IMAGE_2;
    private static Event EVENT_1;
    private static Event EVENT_2;
    private static Event EVENT_3;
    private static Event EVENT_PAST;
    private static Event EVENT_DELETED;

    @Autowired
    private DataSource ds;

    @Autowired
    private EventJdbcDao eventDao;

    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert insert;
    private SimpleJdbcInsert insertAttendance;
    
    @SuppressWarnings("unchecked")
    private Event insertEvent(Map<String, Object> overrides){
        HashMap<String, Object> params = new HashMap<>();
        params.put("user_id", ((User)overrides.getOrDefault("user", USER_1)).getId());
        params.put("city_id", ((City)overrides.getOrDefault("city", CITY_1)).getId());
        params.put("event_date", Date.valueOf((LocalDate)overrides.getOrDefault("date", TestUtils.EVENT_DATE_DEFAULT)));
        Optional<LocalTime> time = (Optional<LocalTime>)overrides.getOrDefault("time", Optional.of(TestUtils.EVENT_TIME_DEFAULT));
        params.put("event_time", time.isPresent() ? Time.valueOf(time.get()) : null);
        params.put("address", ((Optional<String>)overrides.getOrDefault("address", Optional.of(TestUtils.EVENT_ADDRESS_DEFAULT))).orElse(null));
        params.put("attendees_limit", ((Optional<Integer>)overrides.getOrDefault("limit", Optional.of(TestUtils.EVENT_ATTENDANCE_LIMIT_DEFAULT))).orElse(null));
        params.put("attendees_count", ((Integer)overrides.getOrDefault("willAttend", TestUtils.EVENT_ATTENDANCE_DEFAULT)));
        params.put("description", ((Optional<String>)overrides.getOrDefault("description", Optional.of(TestUtils.EVENT_DESCRIPTION_DEFAULT))).orElse(null));
        params.put("title", overrides.getOrDefault("title", TestUtils.EVENT_TITLE_DEFAULT));
        params.put("flyer_image_id", ((Image)overrides.getOrDefault("image", IMAGE_1)).getId());
        params.put("deleted", overrides.getOrDefault("deleted", false));
        params.put("deleted_message", overrides.getOrDefault("deletedMessage", null));

        long key = insert.executeAndReturnKey(params).longValue();
        if (overrides.get("attending") != null){
            insertAttendance.execute(Map.of("user_id", ((User)overrides.get("attending")).getId(), "event_id", key));
        }
        return new Event(
            key, 
            (User)overrides.getOrDefault("user", USER_1),
            ((Date)params.get("event_date")).toLocalDate(), 
            (String)params.get("description"),
            (long)params.get("flyer_image_id"),
            (City)overrides.getOrDefault("city", CITY_1),
            (String)params.get("title"),
            Optional.ofNullable(params.get("event_time") != null ? ((Time)params.get("event_time")).toLocalTime() : null),
            (String)params.get("address"),
            Optional.ofNullable((Integer)params.get("attendees_limit")),
            (int)params.get("attendees_count")
        );
    }

    private void assertEqualsEvent(Event event){
        assertEqualsEvent(event, Map.of());
    }
    
    @SuppressWarnings("unchecked")
    private void assertEqualsEvent(Event event, Map<String, Object> overrides){
        assertNotNull(event);
        assertEquals(((User)overrides.getOrDefault("user", USER_1)).getId(), event.getUser().getId());
        assertEquals(((City)overrides.getOrDefault("city", CITY_1)).getId(), event.getEventCity().getId());
        assertEquals(overrides.getOrDefault("date", TestUtils.EVENT_DATE_DEFAULT), event.getDate());
        assertEquals(overrides.getOrDefault("time", Optional.of(TestUtils.EVENT_TIME_DEFAULT)), event.getTime());
        assertEquals(((Optional<String>)overrides.getOrDefault("address", Optional.of(TestUtils.EVENT_ADDRESS_DEFAULT))).orElse(null), event.getAddress());
        assertEquals(overrides.getOrDefault("limit", Optional.of(TestUtils.EVENT_ATTENDANCE_LIMIT_DEFAULT)), event.getAttendeesLimit());
        assertEquals(((Optional<String>)overrides.getOrDefault("description", Optional.of(TestUtils.EVENT_DESCRIPTION_DEFAULT))).orElse(null), event.getDescription());
        assertEquals(overrides.getOrDefault("title", TestUtils.EVENT_TITLE_DEFAULT), event.getTitle());
        assertEquals(((Image)overrides.getOrDefault("image", IMAGE_1)).getId(), event.getFlyerImageId());
    }

    @Before
    public void setUp(){
        jdbcTemplate = new JdbcTemplate(ds);
        insert = new SimpleJdbcInsert(ds).withTableName(TestUtils.EVENT_TABLE).usingGeneratedKeyColumns("id");
        insertAttendance = new SimpleJdbcInsert(ds).withTableName(TestUtils.EVENT_ATTENDANCE_TABLE);

        USER_1 = jdbcTemplate.queryForObject(TestUtils.USER_SELECT_BY_EMAIL, TestUtils.USER_ROW_MAPPER, TestUtils.USER_1_MAIL);
        USER_2 = jdbcTemplate.queryForObject(TestUtils.USER_SELECT_BY_EMAIL, TestUtils.USER_ROW_MAPPER, TestUtils.USER_2_MAIL);
        CITY_1 = jdbcTemplate.queryForObject(TestUtils.CITY_SELECT_BY_NAME, TestUtils.CITY_ROW_MAPPER, TestUtils.CITY_1_NAME);
        CITY_2 = jdbcTemplate.queryForObject(TestUtils.CITY_SELECT_BY_NAME, TestUtils.CITY_ROW_MAPPER, TestUtils.CITY_2_NAME);
        IMAGE_1 = jdbcTemplate.queryForObject(TestUtils.IMAGE_SELECT_BY_DATA, TestUtils.IMAGE_ROW_MAPPER, TestUtils.IMAGE_1_DATA);
        IMAGE_2 = jdbcTemplate.queryForObject(TestUtils.IMAGE_SELECT_BY_DATA, TestUtils.IMAGE_ROW_MAPPER, TestUtils.IMAGE_2_DATA);
        EVENT_1 = jdbcTemplate.queryForObject(TestUtils.EVENT_SELECT_BY_TITLE, TestUtils.EVENT_ROW_MAPPER, TestUtils.EVENT_TITLE_DEFAULT);
        EVENT_2 = jdbcTemplate.queryForObject(TestUtils.EVENT_SELECT_BY_TITLE, TestUtils.EVENT_ROW_MAPPER, TestUtils.EVENT_TITLE_2);
        EVENT_3 = jdbcTemplate.queryForObject(TestUtils.EVENT_SELECT_BY_TITLE, TestUtils.EVENT_ROW_MAPPER, TestUtils.EVENT_TITLE_3);
        EVENT_PAST = jdbcTemplate.queryForObject(TestUtils.EVENT_SELECT_BY_TITLE, TestUtils.EVENT_ROW_MAPPER, TestUtils.EVENT_TITLE_PAST);
        EVENT_DELETED = jdbcTemplate.queryForObject(TestUtils.EVENT_SELECT_BY_TITLE, TestUtils.EVENT_ROW_MAPPER, TestUtils.EVENT_TITLE_DELETED);
    }

    @Test
    public void testCreate(){
        Event event = eventDao.create(USER_1, CITY_1, TestUtils.EVENT_DATE_DEFAULT, TestUtils.EVENT_DESCRIPTION_DEFAULT, IMAGE_1.getId(), TestUtils.EVENT_TITLE_DEFAULT, TestUtils.EVENT_TIME_DEFAULT, TestUtils.EVENT_ADDRESS_DEFAULT, TestUtils.EVENT_ATTENDANCE_LIMIT_DEFAULT);

        assertEqualsEvent(event);
    }
    @Test
    public void testCreateNoAddress(){
        Event event = eventDao.create(USER_1, CITY_1, TestUtils.EVENT_DATE_DEFAULT, TestUtils.EVENT_DESCRIPTION_DEFAULT, IMAGE_1.getId(), TestUtils.EVENT_TITLE_DEFAULT, TestUtils.EVENT_TIME_DEFAULT, null, TestUtils.EVENT_ATTENDANCE_LIMIT_DEFAULT);

        assertEqualsEvent(event, Map.of("address", Optional.empty()));
    }
    @Test
    public void testCreateNoAddressNoLimit(){
        Event event = eventDao.create(USER_1, CITY_1, TestUtils.EVENT_DATE_DEFAULT, TestUtils.EVENT_DESCRIPTION_DEFAULT, IMAGE_1.getId(), TestUtils.EVENT_TITLE_DEFAULT, TestUtils.EVENT_TIME_DEFAULT, null, null);

        assertEqualsEvent(event, Map.of("address", Optional.empty(), "limit", Optional.empty()));
    }
    @Test
    public void testCreateNoAddressNoLimitNoTime(){
        Event event = eventDao.create(USER_1, CITY_1, TestUtils.EVENT_DATE_DEFAULT, TestUtils.EVENT_DESCRIPTION_DEFAULT, IMAGE_1.getId(), TestUtils.EVENT_TITLE_DEFAULT, null, null, null);

        assertEqualsEvent(event, Map.of("address", Optional.empty(), "limit", Optional.empty(), "time", Optional.empty()));
    }
    @Test(expected = DataAccessException.class)
    public void testCreateWrongUser(){
        eventDao.create(new User(12341234, null, null, null, null, null, null, 0, null, false), CITY_1, TestUtils.EVENT_DATE_DEFAULT, TestUtils.EVENT_DESCRIPTION_DEFAULT, 0, TestUtils.EVENT_TITLE_DEFAULT, TestUtils.EVENT_TIME_DEFAULT, null, TestUtils.EVENT_ATTENDANCE_LIMIT_DEFAULT);
    }
    @Test(expected = DataAccessException.class)
    public void testCreateWrongCity(){
        eventDao.create(USER_1, new City(null, null, 12341234), TestUtils.EVENT_DATE_DEFAULT, TestUtils.EVENT_DESCRIPTION_DEFAULT, IMAGE_1.getId(), TestUtils.EVENT_TITLE_DEFAULT, TestUtils.EVENT_TIME_DEFAULT, null, TestUtils.EVENT_ATTENDANCE_LIMIT_DEFAULT);
    }
    @Test
    public void testCreateNoDescription(){
        Event event = eventDao.create(USER_1, CITY_1, TestUtils.EVENT_DATE_DEFAULT, null, IMAGE_1.getId(), TestUtils.EVENT_TITLE_DEFAULT, TestUtils.EVENT_TIME_DEFAULT, TestUtils.EVENT_ADDRESS_DEFAULT, TestUtils.EVENT_ATTENDANCE_LIMIT_DEFAULT);

        assertEqualsEvent(event, Map.of("description", Optional.empty()));
    }
    @Test(expected = DataAccessException.class)
    public void testCreateWrongImage(){
        eventDao.create(USER_1, CITY_1, TestUtils.EVENT_DATE_DEFAULT, TestUtils.EVENT_DESCRIPTION_DEFAULT, 12341234, TestUtils.EVENT_TITLE_DEFAULT, TestUtils.EVENT_TIME_DEFAULT, TestUtils.EVENT_ADDRESS_DEFAULT, TestUtils.EVENT_ATTENDANCE_LIMIT_DEFAULT);
    }
    @Test(expected = DataAccessException.class)
    public void testCreateNoTitle(){
        eventDao.create(USER_1, CITY_1, TestUtils.EVENT_DATE_DEFAULT, TestUtils.EVENT_DESCRIPTION_DEFAULT, IMAGE_1.getId(), null, TestUtils.EVENT_TIME_DEFAULT, TestUtils.EVENT_ADDRESS_DEFAULT, TestUtils.EVENT_ATTENDANCE_LIMIT_DEFAULT);
    }
    @Test(expected = NullPointerException.class)
    public void testCreateNoDate(){
        eventDao.create(USER_1, CITY_1, null, TestUtils.EVENT_DESCRIPTION_DEFAULT, IMAGE_1.getId(), TestUtils.EVENT_TITLE_DEFAULT, TestUtils.EVENT_TIME_DEFAULT, TestUtils.EVENT_ADDRESS_DEFAULT, TestUtils.EVENT_ATTENDANCE_LIMIT_DEFAULT);
    }

    @Test
    public void testFindById(){
        Optional<Event> maybeEvent = eventDao.findById(EVENT_1.getId());

        assertNotNull(maybeEvent);
        assertTrue(maybeEvent.isPresent());
        TestUtils.assertEqualsEvent(EVENT_1, maybeEvent.get());
    }
    @Test
    public void testFindByIdWrongId(){
        Optional<Event> maybeEvent = eventDao.findById(12341234);

        assertNotNull(maybeEvent);
        assertFalse(maybeEvent.isPresent());
    }
    @Test
    public void testFindByIdNoEvents(){
        Optional<Event> maybeEvent = eventDao.findById(12341234);

        assertNotNull(maybeEvent);
        assertFalse(maybeEvent.isPresent());
    }    
    @Test
    public void testFindByIdDeleted(){
        Optional<Event> maybeEvent = eventDao.findById(EVENT_DELETED.getId());

        assertNotNull(maybeEvent);
        assertFalse(maybeEvent.isPresent());
    }

    @Test
    public void testFindTop(){
        TestUtils.deleteEvents(jdbcTemplate);
        List<Map<String, Object>> maps = new ArrayList<>();
        Map<Long, Event> eventData = new HashMap<>();
        //not full, 20, soon
        maps.add(Map.of("limit", Optional.of(30), "willAttend", 20));
        //not full, 20, later
        maps.add(Map.of("limit", Optional.of(30), "willAttend", 20, "date", TestUtils.EVENT_DATE_LATER));
        //not full, 10, soon
        maps.add(Map.of("limit", Optional.of(30), "willAttend", 10));
        //not full, 10, later
        maps.add(Map.of("limit", Optional.of(30), "willAttend", 10, "date", TestUtils.EVENT_DATE_LATER));
        //full, 20, soon
        maps.add(Map.of("limit", Optional.of(20), "willAttend", 20));
        //full, 20, later
        maps.add(Map.of("limit", Optional.of(20), "willAttend", 20, "date", TestUtils.EVENT_DATE_LATER));
        //full, 10, soon
        maps.add(Map.of("limit", Optional.of(10), "willAttend", 10));
        //full, 10, later
        maps.add(Map.of("limit", Optional.of(10), "willAttend", 10, "date", TestUtils.EVENT_DATE_LATER));
        for (Map<String, Object> params : maps) {
            Event temp = insertEvent(params);
            eventData.put(temp.getId(), temp);
        }

        Page<Event> events = eventDao.findTop(TestUtils.PAGE_1_BIG);

        assertNotNull(events);
        assertNotNull(events.getContent());
        assertEquals(eventData.size(), events.getContent().size());
        for (Event e : events.getContent()){
            TestUtils.assertEqualsEvent(eventData.get(e.getId()), e);
        }
    }
    @Test
    public void testFindTopEventsNoEvents(){
        TestUtils.deleteEventsValid(jdbcTemplate);        
        Page<Event> page1 = eventDao.findTop(TestUtils.PAGE_1_BIG);

        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(0, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(0, page1.getContent().size());
    }

    @Test
    public void testFindTopByUser(){
        TestUtils.deleteEvents(jdbcTemplate);
        List<Map<String, Object>> maps = new ArrayList<>();
        Map<Long, Event> eventData = new HashMap<>();
        //00 not full, not attending, 20, soon
        maps.add(Map.of("limit", Optional.of(30), "willAttend", 20));
        //01 not full, not attending, 20, later
        maps.add(Map.of("limit", Optional.of(30), "willAttend", 20, "date", TestUtils.EVENT_DATE_LATER));
        //02 not full, not attending, 10, soon
        maps.add(Map.of("limit", Optional.of(30), "willAttend", 10));
        //03 not full, not attending, 10, later
        maps.add(Map.of("limit", Optional.of(30), "willAttend", 10, "date", TestUtils.EVENT_DATE_LATER));
        //04 not full, attending, 20, soon
        maps.add(Map.of("limit", Optional.of(30), "willAttend", 20, "attending", USER_2));
        //05 not full, attending, 20, later
        maps.add(Map.of("limit", Optional.of(30), "willAttend", 20, "attending", USER_2, "date", TestUtils.EVENT_DATE_LATER));
        //06 not full, attending, 10, soon
        maps.add(Map.of("limit", Optional.of(30), "willAttend", 10, "attending", USER_2));
        //07 not full, attending, 10, later
        maps.add(Map.of("limit", Optional.of(30), "willAttend", 10, "attending", USER_2, "date", TestUtils.EVENT_DATE_LATER));
        //08 full, 20, not attending, soon
        maps.add(Map.of("limit", Optional.of(20), "willAttend", 20));
        //09 full, 20, not attending, later
        maps.add(Map.of("limit", Optional.of(20), "willAttend", 20, "date", TestUtils.EVENT_DATE_LATER));
        //10 full, 10, not attending, soon
        maps.add(Map.of("limit", Optional.of(10), "willAttend", 10));
        //11 full, 10, not attending, later
        maps.add(Map.of("limit", Optional.of(10), "willAttend", 10, "date", TestUtils.EVENT_DATE_LATER));
        //12 full, 20, attending, soon
        maps.add(Map.of("limit", Optional.of(20), "willAttend", 20, "attending", USER_2));
        //13 full, 20, attending, later
        maps.add(Map.of("limit", Optional.of(20), "willAttend", 20, "attending", USER_2, "date", TestUtils.EVENT_DATE_LATER));
        //14 full, 10, attending, soon
        maps.add(Map.of("limit", Optional.of(10), "willAttend", 10, "attending", USER_2));
        //15 full, 10, attending, later
        maps.add(Map.of("limit", Optional.of(10), "willAttend", 10, "attending", USER_2, "date", TestUtils.EVENT_DATE_LATER));
        for (Map<String, Object> params : maps) {
            Event temp = insertEvent(params);
            eventData.put(temp.getId(), temp);
        }

        Page<Event> events = eventDao.findTopByUser(USER_2.getId(), TestUtils.PAGE_1_BIG);

        assertNotNull(events);
        assertNotNull(events.getContent());
        assertEquals(eventData.size(), events.getContent().size());
        for (Event e : events.getContent()){
            TestUtils.assertEqualsEvent(eventData.get(e.getId()), e);
        }
    }
    @Test
    public void testFindTopByUserEventsNoEvents(){
        TestUtils.deleteEvents(jdbcTemplate);
        insertEvent(Map.of("deleted", true));
        insertEvent(Map.of("date", TestUtils.EVENT_DATE_DEFAULT.plusDays(-100)));

        Page<Event> page1 = eventDao.findTopByUser(USER_2.getId(), TestUtils.PAGE_1_BIG);

        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(0, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(0, page1.getContent().size());
    }

    @Test
    public void testFindAttendanceLimitById(){
        Optional<Integer> limit = eventDao.findAttendanceLimitById(EVENT_1.getId());

        assertNotNull(limit);
        assertTrue(limit.isPresent());
        assertEquals(TestUtils.EVENT_ATTENDANCE_LIMIT_DEFAULT, limit.get().intValue());
    }
    @Test
    public void testFindAttendanceLimitNoLimitById(){
        Optional<Integer> limit = eventDao.findAttendanceLimitById(EVENT_2.getId());

        assertNotNull(limit);
        assertFalse(limit.isPresent());
    }
    @Test(expected = DataAccessException.class)
    public void testFindAttendanceLimitById2(){
        eventDao.findAttendanceLimitById(12341234);
    }

    @Test
    public void testDelete(){
        int rowsBefore = JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.EVENT_TABLE);

        eventDao.delete(EVENT_1.getId());

        assertEquals(rowsBefore, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.EVENT_TABLE));
        assertEquals(
            TestUtils.TOTAL_EVENTS_UPCOMING, 
            jdbcTemplate.queryForObject(TestUtils.EVENT_COUNT_NOT_DELETED, Integer.class).intValue());
    }
    @Test
    public void testDeleteDeleted(){
        int rowsBefore = JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.EVENT_TABLE);

        eventDao.delete(EVENT_DELETED.getId());

        assertEquals(rowsBefore, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.EVENT_TABLE));
        assertEquals(TestUtils.TOTAL_EVENTS_NOT_DELETED, jdbcTemplate.queryForObject(TestUtils.EVENT_COUNT_NOT_DELETED, Integer.class).intValue());
    }
    @Test
    public void testDeleteWrongId(){
        int rowsBefore = JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.EVENT_TABLE);

        eventDao.delete(12341234);

        assertEquals(rowsBefore, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.EVENT_TABLE));
        assertEquals(TestUtils.TOTAL_EVENTS_NOT_DELETED, jdbcTemplate.queryForObject(TestUtils.EVENT_COUNT_NOT_DELETED, Integer.class).intValue());
    }

    @Test
    public void testUpdateDeletionMessage(){
        eventDao.updateDeletionMessage(EVENT_DELETED.getId(), TestUtils.MESSAGE_DEFAULT);

        assertEquals(TestUtils.MESSAGE_DEFAULT, jdbcTemplate.queryForObject(TestUtils.EVENT_GET_DELETED_MESSAGE, String.class, EVENT_DELETED.getId()));
    }
    @Test
    public void testUpdateDeletionMessageWrongId(){
        eventDao.updateDeletionMessage(123123, TestUtils.MESSAGE_DEFAULT);

        assertEquals(null, jdbcTemplate.queryForObject(TestUtils.EVENT_GET_DELETED_MESSAGE, String.class, EVENT_DELETED.getId()));
    }



    @Test
    public void testFindByUserEmailPaged(){
        //get events by user email
        Page<Event> page1 = eventDao.findByUserEmail(TestUtils.USER_2_MAIL, TestUtils.PAGE_1_DEFAULT);
        Page<Event> page2 = eventDao.findByUserEmail(TestUtils.USER_2_MAIL, TestUtils.PAGE_2_DEFAULT);
    
        assertNotNull(page1);
        assertNotNull(page2);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(2, page2.getCurrentPage());
        assertEquals(1, page1.getTotalPages());
        assertEquals(1, page2.getTotalPages());
        assertNotNull(page1.getContent());
        assertNotNull(page2.getContent());
        assertEquals(2, page1.getContent().size());
        assertEquals(0, page2.getContent().size());
        // for (Event e : events){
        //     assertEqualsEvent(e, eventInfo.get(e.getId()));
        // }
    }
    @Test
    public void testFindByUserEmailPaged2(){
        TestUtils.deleteEvents(jdbcTemplate);
        //get events by user email    
        Page<Event> userEvents = eventDao.findByUserEmail(TestUtils.USER_1_MAIL, TestUtils.PAGE_1_DEFAULT);
    
        assertNotNull(userEvents);
        assertEquals(1, userEvents.getCurrentPage());
        assertEquals(0, userEvents.getTotalPages());
        assertNotNull(userEvents.getContent());
        assertEquals(0, userEvents.getContent().size());

    }
    @Test
    public void testFindByUserEmailWrongMailPaged(){
        //get events by user email    
        Page<Event> userEvents = eventDao.findByUserEmail("TestUtils.USER_1_MAIL", TestUtils.PAGE_1_DEFAULT);
    
        assertNotNull(userEvents);
        assertEquals(1, userEvents.getCurrentPage());
        assertEquals(0, userEvents.getTotalPages());
        assertNotNull(userEvents.getContent());
        assertEquals(0, userEvents.getContent().size());
    }

    @Test
    public void testFindAllPaged(){        
        Page<Event> page1 = eventDao.findAll(TestUtils.PAGE_1_DEFAULT);
        Page<Event> page2 = eventDao.findAll(TestUtils.PAGE_2_DEFAULT);
        
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
        List<Event> events = new ArrayList<>();
        events.addAll(page1.getContent());
        events.addAll(page2.getContent());
        // for (Event e : events){
        //     assertEqualsEvent(e, eventInfo.get(e.getId()));
        // }
    }
    @Test
    public void testFindAllNoEventsPaged(){
        TestUtils.deleteEvents(jdbcTemplate);
        Page<Event> events = eventDao.findAll(TestUtils.PAGE_1_DEFAULT);
        
        assertNotNull(events);
        assertEquals(1, events.getCurrentPage());
        assertEquals(0, events.getTotalPages());
        assertNotNull(events.getContent());
        assertEquals(0, events.getContent().size());
    }

    @Test
    public void testSearchPaged(){
        Page<Event> page1 = eventDao.search("event", TestUtils.PAGE_1_DEFAULT);
        Page<Event> page2 = eventDao.search("event", TestUtils.PAGE_2_DEFAULT);

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
        // for (Event e : events){
        //     assertEqualsEvent(e);
        // }
    }
    @Test
    public void testSearchPagedEmptyQuery(){
        Page<Event> page1 = eventDao.search("", TestUtils.PAGE_1_DEFAULT);
        Page<Event> page2 = eventDao.search("", TestUtils.PAGE_2_DEFAULT);

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
        Page<Event> page1 = eventDao.search("TestUtils.EVENT_TITLE_DEFAULT", TestUtils.PAGE_1_DEFAULT);

        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(0, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(0, page1.getContent().size());
    }
    @Test
    public void testSearchEventsPagedNoEvents(){
        TestUtils.deleteEvents(jdbcTemplate);
        Page<Event> page1 = eventDao.search("", TestUtils.PAGE_1_DEFAULT);

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
        eventDao.update(
            CITY_2.getId(), 
            TestUtils.EVENT_DATE_DEFAULT.plusDays(30), 
            null, 
            "TestUtils.EVENT_TITLE_DEFAULT", 
            null, 
            null, 
            null, 
            EVENT_1.getId(),
            IMAGE_1.getId()
        );

        Event event = jdbcTemplate.queryForObject(
            TestUtils.EVENT_SELECT_BY_ID, 
            TestUtils.EVENT_ROW_MAPPER, 
            EVENT_1.getId()
        );

        assertEqualsEvent(event, Map.of(
            "city", CITY_2, 
            "date", TestUtils.EVENT_DATE_DEFAULT.plusDays(30), 
            "title", "TestUtils.EVENT_TITLE_DEFAULT", 
            "description", Optional.empty(), 
            "time", Optional.empty(), 
            "address", Optional.empty(),
            "limit", Optional.empty()));
    }
    @Test
    public void updateEventFull(){
        eventDao.update(
            CITY_2.getId(), 
            TestUtils.EVENT_DATE_DEFAULT.plusDays(30), 
            "TestUtils.EVENT_DESCRIPTION_DEFAULT", 
            "TestUtils.EVENT_TITLE_DEFAULT", 
            TestUtils.EVENT_TIME_DEFAULT.plusHours(1), 
            "new Address", 
            100, 
            EVENT_1.getId(),
            IMAGE_2.getId()
        );

        Event event = jdbcTemplate.queryForObject(
            TestUtils.EVENT_SELECT_BY_ID, 
            TestUtils.EVENT_ROW_MAPPER, 
            EVENT_1.getId()
        );

        assertEqualsEvent(event, Map.of(
            "city", CITY_2, 
            "date", TestUtils.EVENT_DATE_DEFAULT.plusDays(30), 
            "title", "TestUtils.EVENT_TITLE_DEFAULT", 
            "description", Optional.of("TestUtils.EVENT_DESCRIPTION_DEFAULT"), 
            "time", Optional.of(TestUtils.EVENT_TIME_DEFAULT.plusHours(1)), 
            "address", Optional.of("new Address"),
            "limit", Optional.of(100),
            "image", IMAGE_2
        ));
    }
    @Test
    public void updateEventWrongId(){
        eventDao.update(
            CITY_1.getId(), 
            TestUtils.EVENT_DATE_DEFAULT.plusDays(30), 
            "TestUtils.EVENT_DESCRIPTION_DEFAULT", 
            "TestUtils.EVENT_TITLE_DEFAULT", 
            null, 
            null, 
            null, 
            1231234,
            IMAGE_1.getId()
        );

        Event event = jdbcTemplate.queryForObject(
            TestUtils.EVENT_SELECT_BY_ID, 
            TestUtils.EVENT_ROW_MAPPER, 
            EVENT_1.getId()
        );

        assertEqualsEvent(event);
    }

    @Test
    public void testFindRecommended(){
        TestUtils.deleteEvents(jdbcTemplate);
        Map<String, Object> event1params = Map.of("user", USER_2);
        Map<String, Object> event2params = Map.of("user", USER_2, "attending", USER_1);
        Event event1 = insertEvent(event1params);
        Event event2 = insertEvent(event2params);
        insertEvent(Map.of("user", USER_2, "deleted", true));
        insertEvent(Map.of("user", USER_2, "city", CITY_2));
        insertEvent(Map.of("user", USER_2, "date", LocalDate.now().plusDays(-2)));
        Map<Long, Event> eventInfo = Map.of(event1.getId(), event1, event2.getId(), event2);

        Page<Event> events = eventDao.findRecommended(USER_1.getId(), TestUtils.PAGE_1_BIG);

        assertNotNull(events);
        assertEquals(1, events.getCurrentPage());
        assertEquals(1, events.getTotalPages());
        assertNotNull(events.getContent());
        assertEquals(2, events.getContent().size());
        for (Event e : events.getContent()){
            TestUtils.assertEqualsEvent(eventInfo.get(e.getId()), e);
        }
    }
    @Test
    public void testFindRecommendedEventsNoEvents(){
        TestUtils.deleteEvents(jdbcTemplate);

        Page<Event> events = eventDao.findRecommended(USER_1.getId(), TestUtils.PAGE_1_BIG);

        assertNotNull(events);
        assertEquals(1, events.getCurrentPage());
        assertEquals(0, events.getTotalPages());
        assertNotNull(events.getContent());
        assertEquals(0, events.getContent().size());
    }
    @Test
    public void testFindRecommendedWrongEmail(){
        TestUtils.deleteEvents(jdbcTemplate);

        Page<Event> events = eventDao.findRecommended(USER_1.getId(), TestUtils.PAGE_1_BIG);

        assertNotNull(events);
        assertEquals(1, events.getCurrentPage());
        assertEquals(0, events.getTotalPages());
        assertNotNull(events.getContent());
        assertEquals(0, events.getContent().size());
    }
    @Test
    public void testFindRecommendedOrder(){
        TestUtils.deleteEvents(jdbcTemplate);
        List<Map<String, Object>> maps = new ArrayList<>();
        Map<Long, Event> eventData = new HashMap<>();
        //not full, not attending, 20, soon (first)
        maps.add(Map.of("user", USER_2, "limit", Optional.of(30), "willAttend", 20));
        //not full, not attending, 20, later (second)
        maps.add(Map.of("user", USER_2, "limit", Optional.of(30), "willAttend", 20, "date", TestUtils.EVENT_DATE_LATER));
        //not full, not attending, 10, soon (third)
        maps.add(Map.of("user", USER_2, "limit", Optional.of(30), "willAttend", 10));
        //not full, not attending, 10, later (fourth)
        maps.add(Map.of("user", USER_2, "limit", Optional.of(30), "willAttend", 10, "date", TestUtils.EVENT_DATE_LATER));
        //not full, attending, 20, soon (fifth)
        maps.add(Map.of("user", USER_2, "limit", Optional.of(30), "willAttend", 20, "attending", USER_1));
        //not full, attending, 20, later (sixth)
        maps.add(Map.of("user", USER_2, "limit", Optional.of(30), "willAttend", 20, "attending", USER_1, "date", TestUtils.EVENT_DATE_LATER));
        //not full, attending, 10, soon (seventh)
        maps.add(Map.of("user", USER_2, "limit", Optional.of(30), "willAttend", 10, "attending", USER_1));
        //not full, attending, 10, later (eigth)
        maps.add(Map.of("user", USER_2, "limit", Optional.of(30), "willAttend", 10, "attending", USER_1, "date", TestUtils.EVENT_DATE_LATER));
        //full, not attending, 20, soon (ninth)
        maps.add(Map.of("user", USER_2, "limit", Optional.of(20), "willAttend", 20));
        //full, not attending, 20, later (tenth)
        maps.add(Map.of("user", USER_2, "limit", Optional.of(20), "willAttend", 20, "date", TestUtils.EVENT_DATE_LATER));
        //full, not attending, 10, soon (eleventh)
        maps.add(Map.of("user", USER_2, "limit", Optional.of(10), "willAttend", 10));
        //full, not attending, 10, later (twelfth)
        maps.add(Map.of("user", USER_2, "limit", Optional.of(10), "willAttend", 10, "date", TestUtils.EVENT_DATE_LATER));
        //full, attending, 20, soon (thirteenth)
        maps.add(Map.of("user", USER_2, "limit", Optional.of(20), "willAttend", 20, "attending", USER_1));
        //full, attending, 20, later (fourteenth)
        maps.add(Map.of("user", USER_2, "limit", Optional.of(20), "willAttend", 20, "attending", USER_1, "date", TestUtils.EVENT_DATE_LATER));
        //full, attending, 10, soon (fifteenth)
        maps.add(Map.of("user", USER_2, "limit", Optional.of(10), "willAttend", 10, "attending", USER_1));
        //full, attending, 10, later (sixteenth)
        maps.add(Map.of("user", USER_2, "limit", Optional.of(10), "willAttend", 10, "attending", USER_1, "date", TestUtils.EVENT_DATE_LATER));
        for (Map<String, Object> params : maps) {
            Event temp = insertEvent(params);
            eventData.put(temp.getId(), temp);
        }

        Page<Event> events = eventDao.findRecommended(USER_1.getId(), TestUtils.PAGE_1_BIG);

        assertNotNull(events);
        assertNotNull(events.getContent());
        assertEquals(eventData.size(), events.getContent().size());
        for (Event e : events.getContent()){
            TestUtils.assertEqualsEvent(eventData.get(e.getId()), e);
        }
    }
}

//
//    @Test
//    public void testGetOthersEventsPaged(){
//        Map<String, Object> event1 = Map.of("user", USER_2);
//        Map<String, Object> event2 = Map.of("user", USER_3, "date", TestUtils.EVENT_DATE_DEFAULT.plusDays(1));
//        Map<String, Object> event3 = Map.of("user", USER_3, "date", TestUtils.EVENT_DATE_DEFAULT.plusDays(2));
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
