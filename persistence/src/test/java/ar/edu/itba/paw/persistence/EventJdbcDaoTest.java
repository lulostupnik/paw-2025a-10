package ar.edu.itba.paw.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
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
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings("null")
@Transactional
@Rollback
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class EventJdbcDaoTest {

    @Autowired
    private DataSource ds;

    @Autowired
    private EventJdbcDao eventDao;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp(){
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Test
    public void testCreate(){
        Event event = eventDao.create(TestUtils.USER_1, TestUtils.CITY_1, TestUtils.EVENT_DATE_DEFAULT, TestUtils.EVENT_DESCRIPTION_DEFAULT, TestUtils.IMAGE_1_ID, TestUtils.EVENT_TITLE_DEFAULT, TestUtils.EVENT_TIME_DEFAULT, TestUtils.EVENT_ADDRESS_DEFAULT, TestUtils.EVENT_ATTENDANCE_LIMIT_DEFAULT);

        TestUtils.assertEqualsEvent(event, Map.of("id", event.getId(), "attendees", 0));
    }
    @Test
    public void testCreateNoAddress(){
        Event event = eventDao.create(TestUtils.USER_1, TestUtils.CITY_1, TestUtils.EVENT_DATE_DEFAULT, TestUtils.EVENT_DESCRIPTION_DEFAULT, TestUtils.IMAGE_1_ID, TestUtils.EVENT_TITLE_DEFAULT, TestUtils.EVENT_TIME_DEFAULT, null, TestUtils.EVENT_ATTENDANCE_LIMIT_DEFAULT);

        TestUtils.assertEqualsEvent(event, Map.of("id", event.getId(), "attendees", 0, "address", Optional.empty()));
    }
    @Test
    public void testCreateNoAddressNoLimit(){
        Event event = eventDao.create(TestUtils.USER_1, TestUtils.CITY_1, TestUtils.EVENT_DATE_DEFAULT, TestUtils.EVENT_DESCRIPTION_DEFAULT, TestUtils.IMAGE_1_ID, TestUtils.EVENT_TITLE_DEFAULT, TestUtils.EVENT_TIME_DEFAULT, null, null);

        TestUtils.assertEqualsEvent(event, Map.of("id", event.getId(), "attendees", 0, "address", Optional.empty(), "limit", Optional.empty()));
    }
    @Test
    public void testCreateNoAddressNoLimitNoTime(){
        Event event = eventDao.create(TestUtils.USER_1, TestUtils.CITY_1, TestUtils.EVENT_DATE_DEFAULT, TestUtils.EVENT_DESCRIPTION_DEFAULT, TestUtils.IMAGE_1_ID, TestUtils.EVENT_TITLE_DEFAULT, null, null, null);

        TestUtils.assertEqualsEvent(event, Map.of("id", event.getId(), "attendees", 0, "address", Optional.empty(), "limit", Optional.empty(), "time", Optional.empty()));
    }
    @Test(expected = DataAccessException.class)
    public void testCreateWrongUser(){
        eventDao.create(new User(12341234, null, null, null, null, null, null, 0, null, false), TestUtils.CITY_1, TestUtils.EVENT_DATE_DEFAULT, TestUtils.EVENT_DESCRIPTION_DEFAULT, 0, TestUtils.EVENT_TITLE_DEFAULT, TestUtils.EVENT_TIME_DEFAULT, null, TestUtils.EVENT_ATTENDANCE_LIMIT_DEFAULT);
    }
    @Test(expected = DataAccessException.class)
    public void testCreateWrongCity(){
        eventDao.create(TestUtils.USER_1, new City(null, null, 12341234), TestUtils.EVENT_DATE_DEFAULT, TestUtils.EVENT_DESCRIPTION_DEFAULT, TestUtils.IMAGE_1_ID, TestUtils.EVENT_TITLE_DEFAULT, TestUtils.EVENT_TIME_DEFAULT, null, TestUtils.EVENT_ATTENDANCE_LIMIT_DEFAULT);
    }
    @Test
    public void testCreateNoDescription(){
        Event event = eventDao.create(TestUtils.USER_1, TestUtils.CITY_1, TestUtils.EVENT_DATE_DEFAULT, null, TestUtils.IMAGE_1_ID, TestUtils.EVENT_TITLE_DEFAULT, TestUtils.EVENT_TIME_DEFAULT, TestUtils.EVENT_ADDRESS_DEFAULT, TestUtils.EVENT_ATTENDANCE_LIMIT_DEFAULT);

        TestUtils.assertEqualsEvent(event, Map.of("id", event.getId(), "attendees", 0, "description", Optional.empty()));
    }
    @Test(expected = DataAccessException.class)
    public void testCreateWrongImage(){
        eventDao.create(TestUtils.USER_1, TestUtils.CITY_1, TestUtils.EVENT_DATE_DEFAULT, TestUtils.EVENT_DESCRIPTION_DEFAULT, 12341234, TestUtils.EVENT_TITLE_DEFAULT, TestUtils.EVENT_TIME_DEFAULT, TestUtils.EVENT_ADDRESS_DEFAULT, TestUtils.EVENT_ATTENDANCE_LIMIT_DEFAULT);
    }
    @Test(expected = DataAccessException.class)
    public void testCreateNoTitle(){
        eventDao.create(TestUtils.USER_1, TestUtils.CITY_1, TestUtils.EVENT_DATE_DEFAULT, TestUtils.EVENT_DESCRIPTION_DEFAULT, TestUtils.IMAGE_1_ID, null, TestUtils.EVENT_TIME_DEFAULT, TestUtils.EVENT_ADDRESS_DEFAULT, TestUtils.EVENT_ATTENDANCE_LIMIT_DEFAULT);
    }
    @Test(expected = NullPointerException.class)
    public void testCreateNoDate(){
        eventDao.create(TestUtils.USER_1, TestUtils.CITY_1, null, TestUtils.EVENT_DESCRIPTION_DEFAULT, TestUtils.IMAGE_1_ID, TestUtils.EVENT_TITLE_DEFAULT, TestUtils.EVENT_TIME_DEFAULT, TestUtils.EVENT_ADDRESS_DEFAULT, TestUtils.EVENT_ATTENDANCE_LIMIT_DEFAULT);
    }

    @Test
    public void testFindById(){
        Optional<Event> maybeEvent = eventDao.findById(TestUtils.EVENT_1_ID);

        assertNotNull(maybeEvent);
        assertTrue(maybeEvent.isPresent());
        TestUtils.assertEqualsEvent(TestUtils.EVENT_1, maybeEvent.get());
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
        Optional<Event> maybeEvent = eventDao.findById(TestUtils.EVENT_DELETED_ID);

        assertNotNull(maybeEvent);
        assertFalse(maybeEvent.isPresent());
    }

    @Test
    public void testFindTop(){
        TestUtils.deleteEvents(jdbcTemplate);
        List<Map<String, Object>> maps = new ArrayList<>();
        List<Event> eventData = new ArrayList<>();
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
            eventData.add(TestUtils.insertEvent(ds, params));
        }

        Page<Event> events = eventDao.findTop(TestUtils.PAGE_1_BIG);

        assertNotNull(events);
        assertNotNull(events.getContent());
        assertEquals(eventData.size(), events.getContent().size());
        ListIterator<Event> iterator = eventData.listIterator(); 
        while (iterator.hasNext()){
            TestUtils.assertEqualsEvent(events.getContent().get(iterator.nextIndex()), iterator.next());
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
        List<Event> eventData = new ArrayList<>();
        //00 not full, not attending, 20, soon
        maps.add(Map.of("limit", Optional.of(30), "willAttend", 20));
        //01 not full, not attending, 20, later
        maps.add(Map.of("limit", Optional.of(30), "willAttend", 20, "date", TestUtils.EVENT_DATE_LATER));
        //02 not full, not attending, 10, soon
        maps.add(Map.of("limit", Optional.of(30), "willAttend", 10));
        //03 not full, not attending, 10, later
        maps.add(Map.of("limit", Optional.of(30), "willAttend", 10, "date", TestUtils.EVENT_DATE_LATER));
        //04 not full, attending, 20, soon
        maps.add(Map.of("limit", Optional.of(30), "willAttend", 20, "attending", TestUtils.USER_2));
        //05 not full, attending, 20, later
        maps.add(Map.of("limit", Optional.of(30), "willAttend", 20, "attending", TestUtils.USER_2, "date", TestUtils.EVENT_DATE_LATER));
        //06 not full, attending, 10, soon
        maps.add(Map.of("limit", Optional.of(30), "willAttend", 10, "attending", TestUtils.USER_2));
        //07 not full, attending, 10, later
        maps.add(Map.of("limit", Optional.of(30), "willAttend", 10, "attending", TestUtils.USER_2, "date", TestUtils.EVENT_DATE_LATER));
        //08 full, 20, not attending, soon
        maps.add(Map.of("limit", Optional.of(20), "willAttend", 20));
        //09 full, 20, not attending, later
        maps.add(Map.of("limit", Optional.of(20), "willAttend", 20, "date", TestUtils.EVENT_DATE_LATER));
        //10 full, 10, not attending, soon
        maps.add(Map.of("limit", Optional.of(10), "willAttend", 10));
        //11 full, 10, not attending, later
        maps.add(Map.of("limit", Optional.of(10), "willAttend", 10, "date", TestUtils.EVENT_DATE_LATER));
        //12 full, 20, attending, soon
        maps.add(Map.of("limit", Optional.of(20), "willAttend", 20, "attending", TestUtils.USER_2));
        //13 full, 20, attending, later
        maps.add(Map.of("limit", Optional.of(20), "willAttend", 20, "attending", TestUtils.USER_2, "date", TestUtils.EVENT_DATE_LATER));
        //14 full, 10, attending, soon
        maps.add(Map.of("limit", Optional.of(10), "willAttend", 10, "attending", TestUtils.USER_2));
        //15 full, 10, attending, later
        maps.add(Map.of("limit", Optional.of(10), "willAttend", 10, "attending", TestUtils.USER_2, "date", TestUtils.EVENT_DATE_LATER));
        for (Map<String, Object> params : maps) {
            eventData.add(TestUtils.insertEvent(ds, params));
        }

        Page<Event> events = eventDao.findTopByUser(TestUtils.USER_2_ID, TestUtils.PAGE_1_BIG);

        assertNotNull(events);
        assertNotNull(events.getContent());
        assertEquals(eventData.size(), events.getContent().size());
        ListIterator<Event> iterator = eventData.listIterator(); 
        while (iterator.hasNext()){
            TestUtils.assertEqualsEvent(events.getContent().get(iterator.nextIndex()), iterator.next());
        }
    }
    @Test
    public void testFindTopByUserEventsNoEvents(){
        TestUtils.deleteEvents(jdbcTemplate);
        TestUtils.insertEvent(ds, Map.of("deleted", true));
        TestUtils.insertEvent(ds, Map.of("date", TestUtils.EVENT_DATE_DEFAULT.plusDays(-100)));

        Page<Event> page1 = eventDao.findTopByUser(TestUtils.USER_2_ID, TestUtils.PAGE_1_BIG);

        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(0, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(0, page1.getContent().size());
    }

    @Test
    public void testFindAttendanceLimitById(){
        Optional<Integer> limit = eventDao.findAttendanceLimitById(TestUtils.EVENT_1_ID);

        assertNotNull(limit);
        assertTrue(limit.isPresent());
        assertEquals(TestUtils.EVENT_ATTENDANCE_LIMIT_DEFAULT, limit.get().intValue());
    }
    @Test
    public void testFindAttendanceLimitNoLimitById(){
        Optional<Integer> limit = eventDao.findAttendanceLimitById(TestUtils.EVENT_2_ID);

        assertNotNull(limit);
        assertFalse(limit.isPresent());
    }
    @Test(expected = DataAccessException.class)
    public void testFindAttendanceLimitById2(){
        eventDao.findAttendanceLimitById(12341234);
    }

    @Test
    public void testFindAllBetweenDates(){
        List<Event> events = eventDao.findAllBetweenDates(TestUtils.EVENT_DATE_OLDER, TestUtils.EVENT_DATE_DEFAULT.plusDays(-1));

        assertNotNull(events);
        assertEquals(1, events.size());
        TestUtils.assertEqualsEvent(TestUtils.EVENT_OLDER, events.getFirst());
    }
    @Test
    public void testFindAllBetweenDates2(){
        List<Event> events = eventDao.findAllBetweenDates(TestUtils.EVENT_DATE_DEFAULT, TestUtils.EVENT_DATE_LATER);

        assertNotNull(events);
        assertEquals(3, events.size());
    }

    @Test
    public void testCountEventsCreatedByUser(){
        int eventCount = eventDao.countEventsCreatedByUser(TestUtils.USER_1_ID);

        assertEquals(TestUtils.USER_1_CREATED_EVENTS, eventCount);
    }
    @Test
    public void testCountEventsCreatedByUser2(){
        int eventCount = eventDao.countEventsCreatedByUser(TestUtils.USER_1_ID);

        assertEquals(TestUtils.USER_2_CREATED_EVENTS, eventCount);
    }
    @Test
    public void testCountEventsCreatedByUserNotFound(){
        int eventCount = eventDao.countEventsCreatedByUser(12341234l);

        assertEquals(0, eventCount);
    }

    @Test
    public void testDelete(){
        int rowsBefore = JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.EVENT_TABLE);

        eventDao.delete(TestUtils.EVENT_1_ID);

        assertEquals(rowsBefore, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.EVENT_TABLE));
        assertEquals(
            TestUtils.TOTAL_EVENTS_UPCOMING, 
            jdbcTemplate.queryForObject(TestUtils.EVENT_COUNT_NOT_DELETED, Integer.class).intValue());
    }
    @Test
    public void testDeleteDeleted(){
        int rowsBefore = JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.EVENT_TABLE);

        eventDao.delete(TestUtils.EVENT_DELETED_ID);

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
        eventDao.updateDeletionMessage(TestUtils.EVENT_DELETED_ID, TestUtils.MESSAGE_DEFAULT);

        assertEquals(TestUtils.MESSAGE_DEFAULT, jdbcTemplate.queryForObject(TestUtils.EVENT_GET_DELETED_MESSAGE, String.class, TestUtils.EVENT_DELETED_ID));
    }
    @Test
    public void testUpdateDeletionMessageWrongId(){
        eventDao.updateDeletionMessage(123123, TestUtils.MESSAGE_DEFAULT);

        assertEquals(null, jdbcTemplate.queryForObject(TestUtils.EVENT_GET_DELETED_MESSAGE, String.class, TestUtils.EVENT_DELETED_ID));
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
        for (Event e : page1.getContent()){
            TestUtils.assertEqualsEvent(TestUtils.EVENT_DATA.get(e.getId()), e);
        }
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
        for (Event e : page1.getContent()){
            TestUtils.assertEqualsEvent(TestUtils.EVENT_DATA.get(e.getId()), e);
        }
        for (Event e : page2.getContent()){
            TestUtils.assertEqualsEvent(TestUtils.EVENT_DATA.get(e.getId()), e);
        }
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
        for (Event e : page1.getContent()){
            TestUtils.assertEqualsEvent(TestUtils.EVENT_DATA.get(e.getId()), e);
        }
        for (Event e : page2.getContent()){
            TestUtils.assertEqualsEvent(TestUtils.EVENT_DATA.get(e.getId()), e);
        }
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


    @Test
    public void updateEvent(){
        eventDao.update(
            TestUtils.CITY_2_ID, 
            TestUtils.EVENT_DATE_DEFAULT.plusDays(30), 
            null, 
            "TestUtils.EVENT_TITLE_DEFAULT", 
            null, 
            null, 
            null, 
            TestUtils.EVENT_1_ID,
            TestUtils.IMAGE_1_ID
        );

        Event event = jdbcTemplate.queryForObject(
            TestUtils.EVENT_SELECT_BY_ID, 
            TestUtils.EVENT_ROW_MAPPER, 
            TestUtils.EVENT_1_ID
        );

        TestUtils.assertEqualsEvent(event, Map.of(
            "city", TestUtils.CITY_2, 
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
            TestUtils.CITY_2_ID, 
            TestUtils.EVENT_DATE_DEFAULT.plusDays(30), 
            "TestUtils.EVENT_DESCRIPTION_DEFAULT", 
            "TestUtils.EVENT_TITLE_DEFAULT", 
            TestUtils.EVENT_TIME_DEFAULT.plusHours(1), 
            "new Address", 
            100, 
            TestUtils.EVENT_1_ID,
            TestUtils.IMAGE_2_ID
        );

        Event event = jdbcTemplate.queryForObject(
            TestUtils.EVENT_SELECT_BY_ID, 
            TestUtils.EVENT_ROW_MAPPER, 
            TestUtils.EVENT_1_ID
        );

        TestUtils.assertEqualsEvent(event, Map.of(
            "city", TestUtils.CITY_2, 
            "date", TestUtils.EVENT_DATE_DEFAULT.plusDays(30), 
            "title", "TestUtils.EVENT_TITLE_DEFAULT", 
            "description", Optional.of("TestUtils.EVENT_DESCRIPTION_DEFAULT"), 
            "time", Optional.of(TestUtils.EVENT_TIME_DEFAULT.plusHours(1)), 
            "address", Optional.of("new Address"),
            "limit", Optional.of(100),
            "image", TestUtils.IMAGE_2
        ));
    }
    @Test
    public void updateEventWrongId(){
        eventDao.update(
            TestUtils.CITY_1_ID, 
            TestUtils.EVENT_DATE_DEFAULT.plusDays(30), 
            "TestUtils.EVENT_DESCRIPTION_DEFAULT", 
            "TestUtils.EVENT_TITLE_DEFAULT", 
            null, 
            null, 
            null, 
            1231234,
            TestUtils.IMAGE_1_ID
        );

        Event event = jdbcTemplate.queryForObject(
            TestUtils.EVENT_SELECT_BY_ID, 
            TestUtils.EVENT_ROW_MAPPER, 
            TestUtils.EVENT_1_ID
        );

        TestUtils.assertEqualsEvent(event);
    }

    @Test
    public void testFindRecommended(){
        TestUtils.deleteEvents(jdbcTemplate);
        Map<String, Object> event1params = Map.of("user", TestUtils.USER_2);
        Map<String, Object> event2params = Map.of("user", TestUtils.USER_2, "attending", TestUtils.USER_1);
        Event event1 = TestUtils.insertEvent(ds, event1params);
        Event event2 = TestUtils.insertEvent(ds, event2params);
        TestUtils.insertEvent(ds, Map.of("user", TestUtils.USER_2, "deleted", true));
        TestUtils.insertEvent(ds, Map.of("user", TestUtils.USER_2, "city", TestUtils.CITY_2));
        TestUtils.insertEvent(ds, Map.of("user", TestUtils.USER_2, "date", LocalDate.now().plusDays(-2)));
        Map<Long, Event> eventInfo = Map.of(event1.getId(), event1, event2.getId(), event2);

        Page<Event> events = eventDao.findRecommended(TestUtils.USER_1_ID, TestUtils.PAGE_1_BIG);

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

        Page<Event> events = eventDao.findRecommended(TestUtils.USER_1_ID, TestUtils.PAGE_1_BIG);

        assertNotNull(events);
        assertEquals(1, events.getCurrentPage());
        assertEquals(0, events.getTotalPages());
        assertNotNull(events.getContent());
        assertEquals(0, events.getContent().size());
    }
    @Test
    public void testFindRecommendedWrongEmail(){
        TestUtils.deleteEvents(jdbcTemplate);

        Page<Event> events = eventDao.findRecommended(TestUtils.USER_1_ID, TestUtils.PAGE_1_BIG);

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
        List<Event> eventData = new ArrayList<>();
        //not full, not attending, 20, soon (first)
        maps.add(Map.of("user", TestUtils.USER_2, "limit", Optional.of(30), "willAttend", 20));
        //not full, not attending, 20, later (second)
        maps.add(Map.of("user", TestUtils.USER_2, "limit", Optional.of(30), "willAttend", 20, "date", TestUtils.EVENT_DATE_LATER));
        //not full, not attending, 10, soon (third)
        maps.add(Map.of("user", TestUtils.USER_2, "limit", Optional.of(30), "willAttend", 10));
        //not full, not attending, 10, later (fourth)
        maps.add(Map.of("user", TestUtils.USER_2, "limit", Optional.of(30), "willAttend", 10, "date", TestUtils.EVENT_DATE_LATER));
        //not full, attending, 20, soon (fifth)
        maps.add(Map.of("user", TestUtils.USER_2, "limit", Optional.of(30), "willAttend", 20, "attending", TestUtils.USER_1));
        //not full, attending, 20, later (sixth)
        maps.add(Map.of("user", TestUtils.USER_2, "limit", Optional.of(30), "willAttend", 20, "attending", TestUtils.USER_1, "date", TestUtils.EVENT_DATE_LATER));
        //not full, attending, 10, soon (seventh)
        maps.add(Map.of("user", TestUtils.USER_2, "limit", Optional.of(30), "willAttend", 10, "attending", TestUtils.USER_1));
        //not full, attending, 10, later (eigth)
        maps.add(Map.of("user", TestUtils.USER_2, "limit", Optional.of(30), "willAttend", 10, "attending", TestUtils.USER_1, "date", TestUtils.EVENT_DATE_LATER));
        //full, not attending, 20, soon (ninth)
        maps.add(Map.of("user", TestUtils.USER_2, "limit", Optional.of(20), "willAttend", 20));
        //full, not attending, 20, later (tenth)
        maps.add(Map.of("user", TestUtils.USER_2, "limit", Optional.of(20), "willAttend", 20, "date", TestUtils.EVENT_DATE_LATER));
        //full, not attending, 10, soon (eleventh)
        maps.add(Map.of("user", TestUtils.USER_2, "limit", Optional.of(10), "willAttend", 10));
        //full, not attending, 10, later (twelfth)
        maps.add(Map.of("user", TestUtils.USER_2, "limit", Optional.of(10), "willAttend", 10, "date", TestUtils.EVENT_DATE_LATER));
        //full, attending, 20, soon (thirteenth)
        maps.add(Map.of("user", TestUtils.USER_2, "limit", Optional.of(20), "willAttend", 20, "attending", TestUtils.USER_1));
        //full, attending, 20, later (fourteenth)
        maps.add(Map.of("user", TestUtils.USER_2, "limit", Optional.of(20), "willAttend", 20, "attending", TestUtils.USER_1, "date", TestUtils.EVENT_DATE_LATER));
        //full, attending, 10, soon (fifteenth)
        maps.add(Map.of("user", TestUtils.USER_2, "limit", Optional.of(10), "willAttend", 10, "attending", TestUtils.USER_1));
        //full, attending, 10, later (sixteenth)
        maps.add(Map.of("user", TestUtils.USER_2, "limit", Optional.of(10), "willAttend", 10, "attending", TestUtils.USER_1, "date", TestUtils.EVENT_DATE_LATER));
        for (Map<String, Object> params : maps) {
            eventData.add(TestUtils.insertEvent(ds, params));
        }

        Page<Event> events = eventDao.findRecommended(TestUtils.USER_1_ID, TestUtils.PAGE_1_BIG);

        assertNotNull(events);
        assertNotNull(events.getContent());
        assertEquals(eventData.size(), events.getContent().size());
        ListIterator<Event> iterator = eventData.listIterator(); 
        while (iterator.hasNext()){
            TestUtils.assertEqualsEvent(events.getContent().get(iterator.nextIndex()), iterator.next());
        }
    }

    //@TODO no se si las siguientes 3 funciones estan bien. (estaban en otro dao)
    @Test
    public void testFindAllEventsByAttendeePaged(){
        Page<Event> page1 = eventDao.findAllEventsByAttendee(TestUtils.USER_1_ID, TestUtils.PAGE_1_SINGLE);
        Page<Event> page2 = eventDao.findAllEventsByAttendee(TestUtils.USER_1_ID, TestUtils.PAGE_2_SINGLE);

        assertNotNull(page1);
        assertNotNull(page2);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(2, page2.getCurrentPage());
        assertEquals(1, page1.getTotalPages());
        assertEquals(1, page2.getTotalPages());
        assertNotNull(page1.getContent());
        assertNotNull(page2.getContent());
        assertEquals(1, page1.getContent().size());
        assertEquals(0, page2.getContent().size());
    }
    @Test
    public void testFindAllPagedByAttendee(){
        Page<Event> events = eventDao.findAllEventsByAttendee(TestUtils.USER_1_ID, TestUtils.PAGE_1_SINGLE);

        assertNotNull(events);
        assertEquals(1, events.getCurrentPage());
        assertEquals(1, events.getTotalPages());
        assertNotNull(events.getContent());
        assertEquals(1, events.getContent().size());
    }
    @Test
    public void testFindAllEventsByAttendeeWrongUserPaged(){
        Page<Event> events = eventDao.findAllEventsByAttendee(12341234, TestUtils.PAGE_1_SINGLE);

        assertNotNull(events);
        assertEquals(1, events.getCurrentPage());
        assertEquals(0, events.getTotalPages());
        assertNotNull(events.getContent());
        assertEquals(0, events.getContent().size());
    }
}

//
//    @Test
//    public void testGetOthersEventsPaged(){
//        Map<String, Object> event1 = Map.of("user", TestUtils.USER_2);
//        Map<String, Object> event2 = Map.of("user", TestUtils.USER_3, "date", TestUtils.EVENT_DATE_DEFAULT.plusDays(1));
//        Map<String, Object> event3 = Map.of("user", TestUtils.USER_3, "date", TestUtils.EVENT_DATE_DEFAULT.plusDays(2));
//        TestUtils.insertEvent();
//        TestUtils.insertEvent();
//        TestUtils.insertEvent(Map.of("title", "deleted event", "deleted", true));
//        long id1 = TestUtils.insertEvent(event1);
//        long id2 = TestUtils.insertEvent(event2);
//        long id3 = TestUtils.insertEvent(event3);
//        TestUtils.insertEvent(Map.of("user", TestUtils.USER_3, "deleted", true, "title", "DELETED"));
//        Map<Long, Map<String, Object>> eventInfo = Map.of(id1, event1, id2, event2, id3, event3);
//
//        Page<Event> page1 = eventDao.getOthersEvents(TestUtils.USER_1_ID, TestUtils.PAGE_1_DEFAULT);
//        Page<Event> page2 = eventDao.getOthersEvents(TestUtils.USER_1_ID, TestUtils.PAGE_2_DEFAULT);
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
//            TestUtils.assertEqualsEvent(e, eventInfo.get(e.getId()));
//        }
//    }
//    @Test
//    public void testGetOthersEventsPagedNoEvents(){
//        TestUtils.insertEvent();
//        TestUtils.insertEvent();
//        TestUtils.insertEvent(Map.of("title", "deleted event", "deleted", true));
//        TestUtils.insertEvent(Map.of("user", TestUtils.USER_3, "deleted", true, "title", "DELETED"));
//
//        Page<Event> page1 = eventDao.getOthersEvents(TestUtils.USER_1_ID, TestUtils.PAGE_1_DEFAULT);
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
//        TestUtils.insertEvent();
//        TestUtils.insertEvent();
//        TestUtils.insertEvent();
//        TestUtils.insertEvent(Map.of("title", "deleted event", "deleted", true));
//        TestUtils.insertEvent(Map.of("user", TestUtils.USER_2));
//        TestUtils.insertEvent(Map.of("user", TestUtils.USER_3));
//
//        Page<Event> page1 = eventDao.findByUserId(TestUtils.USER_1_ID, TestUtils.PAGE_1_DEFAULT);
//        Page<Event> page2 = eventDao.findByUserId(TestUtils.USER_1_ID, TestUtils.PAGE_2_DEFAULT);
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
//            TestUtils.assertEqualsEvent(e);
//        }
//    }
//    @Test
//    public void testFindByUserId(){
//        TestUtils.insertEvent(Map.of("title", "deleted event", "deleted", true));
//        TestUtils.insertEvent(Map.of("user", TestUtils.USER_2));
//        TestUtils.insertEvent(Map.of("user", TestUtils.USER_3));
//
//        Page<Event> page1 = eventDao.findByUserId(TestUtils.USER_1_ID, TestUtils.PAGE_1_DEFAULT);
//
//        assertNotNull(page1);
//        assertEquals(1, page1.getCurrentPage());
//        assertEquals(0, page1.getTotalPages());
//        assertNotNull(page1.getContent());
//        assertEquals(0, page1.getContent().size());
//    }
