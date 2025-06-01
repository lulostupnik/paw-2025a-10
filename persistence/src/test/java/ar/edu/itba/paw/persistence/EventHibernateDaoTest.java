package ar.edu.itba.paw.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.sql.DataSource;

import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.enums.SortDirection;
import ar.edu.itba.paw.models.enums.SortFieldEvent;

import ar.edu.itba.paw.persistence.config.TestConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class EventHibernateDaoTest {

    @Autowired
    private DataSource ds;

    @Autowired
    private EventHibernateDao eventDao;

    @PersistenceContext
    private EntityManager em;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp(){
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Test
    public void testCreate(){
        Event event = eventDao.create(TestUtils.USER_1, TestUtils.CITY_1, TestUtils.EVENT_DATE_DEFAULT, TestUtils.EVENT_DESCRIPTION_DEFAULT, TestUtils.IMAGE_1_ID, TestUtils.EVENT_TITLE_DEFAULT, TestUtils.EVENT_TIME_DEFAULT, TestUtils.EVENT_ADDRESS_DEFAULT, TestUtils.EVENT_ATTENDANCE_LIMIT_DEFAULT);
        em.flush();

        TestUtils.assertEqualsEvent(event, Map.of("id", event.getId(), "attendees", 1));
    }
    @Test
    public void testCreateNoAddress(){
        Event event = eventDao.create(TestUtils.USER_1, TestUtils.CITY_1, TestUtils.EVENT_DATE_DEFAULT, TestUtils.EVENT_DESCRIPTION_DEFAULT, TestUtils.IMAGE_1_ID, TestUtils.EVENT_TITLE_DEFAULT, TestUtils.EVENT_TIME_DEFAULT, null, TestUtils.EVENT_ATTENDANCE_LIMIT_DEFAULT);
        em.flush();

        HashMap<String, Object> eventParams = new HashMap<>();
        eventParams.put("id", event.getId());
        eventParams.put("attendees", 1);
        eventParams.put("address", null);
        TestUtils.assertEqualsEvent(event, eventParams);
    }
    @Test
    public void testCreateNoAddressNoLimit(){
        Event event = eventDao.create(TestUtils.USER_1, TestUtils.CITY_1, TestUtils.EVENT_DATE_DEFAULT, TestUtils.EVENT_DESCRIPTION_DEFAULT, TestUtils.IMAGE_1_ID, TestUtils.EVENT_TITLE_DEFAULT, TestUtils.EVENT_TIME_DEFAULT, null, null);
        em.flush();

        HashMap<String, Object> eventParams = new HashMap<>();
        eventParams.put("id", event.getId());
        eventParams.put("attendees", 1);
        eventParams.put("address", null);
        eventParams.put("limit", null);
        TestUtils.assertEqualsEvent(event, eventParams);
    }
    @Test
    public void testCreateNoAddressNoLimitNoTime(){
        Event event = eventDao.create(TestUtils.USER_1, TestUtils.CITY_1, TestUtils.EVENT_DATE_DEFAULT, TestUtils.EVENT_DESCRIPTION_DEFAULT, TestUtils.IMAGE_1_ID, TestUtils.EVENT_TITLE_DEFAULT, null, null, null);
        em.flush();

        HashMap<String, Object> eventParams = new HashMap<>();
        eventParams.put("id", event.getId());
        eventParams.put("attendees", 1);
        eventParams.put("address", null);
        eventParams.put("limit", null);
        eventParams.put("time", null);
        TestUtils.assertEqualsEvent(event, eventParams);
    }
    @Test(expected = PersistenceException.class)
    public void testCreateWrongUser(){
        eventDao.create(new User(12341234l, null, null, null, null, null, null, 0, null, false, false), TestUtils.CITY_1, TestUtils.EVENT_DATE_DEFAULT, TestUtils.EVENT_DESCRIPTION_DEFAULT, 0, TestUtils.EVENT_TITLE_DEFAULT, TestUtils.EVENT_TIME_DEFAULT, null, TestUtils.EVENT_ATTENDANCE_LIMIT_DEFAULT);
        em.flush();
    }
    @Test(expected = PersistenceException.class)
    public void testCreateWrongCity(){
        eventDao.create(TestUtils.USER_1, new City(null, null, 12341234l), TestUtils.EVENT_DATE_DEFAULT, TestUtils.EVENT_DESCRIPTION_DEFAULT, TestUtils.IMAGE_1_ID, TestUtils.EVENT_TITLE_DEFAULT, TestUtils.EVENT_TIME_DEFAULT, null, TestUtils.EVENT_ATTENDANCE_LIMIT_DEFAULT);
        em.flush();
    }
    @Test
    public void testCreateNoDescription(){
        Event event = eventDao.create(TestUtils.USER_1, TestUtils.CITY_1, TestUtils.EVENT_DATE_DEFAULT, null, TestUtils.IMAGE_1_ID, TestUtils.EVENT_TITLE_DEFAULT, TestUtils.EVENT_TIME_DEFAULT, TestUtils.EVENT_ADDRESS_DEFAULT, TestUtils.EVENT_ATTENDANCE_LIMIT_DEFAULT);
        em.flush();
        
        HashMap<String, Object> eventParams = new HashMap<>();
        eventParams.put("id", event.getId());
        eventParams.put("attendees", 1);
        eventParams.put("description", null);
        TestUtils.assertEqualsEvent(event, eventParams);
    }
    @Test(expected = PersistenceException.class)
    public void testCreateWrongImage(){
        eventDao.create(TestUtils.USER_1, TestUtils.CITY_1, TestUtils.EVENT_DATE_DEFAULT, TestUtils.EVENT_DESCRIPTION_DEFAULT, 12341234, TestUtils.EVENT_TITLE_DEFAULT, TestUtils.EVENT_TIME_DEFAULT, TestUtils.EVENT_ADDRESS_DEFAULT, TestUtils.EVENT_ATTENDANCE_LIMIT_DEFAULT);
        em.flush();
    }
    @Test(expected = PersistenceException.class)
    public void testCreateNoTitle(){
        eventDao.create(TestUtils.USER_1, TestUtils.CITY_1, TestUtils.EVENT_DATE_DEFAULT, TestUtils.EVENT_DESCRIPTION_DEFAULT, TestUtils.IMAGE_1_ID, null, TestUtils.EVENT_TIME_DEFAULT, TestUtils.EVENT_ADDRESS_DEFAULT, TestUtils.EVENT_ATTENDANCE_LIMIT_DEFAULT);
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
    // @Test
    // public void testFindByIdDeleted(){
    //     Optional<Event> maybeEvent = eventDao.findById(TestUtils.EVENT_DELETED_ID);

    //     assertNotNull(maybeEvent);
    //     assertFalse(maybeEvent.isPresent());
    // }

    @Test
    public void testFindTop(){
        TestUtils.deleteEvents(jdbcTemplate);
        List<Map<String, Object>> maps = new ArrayList<>();
        List<Event> eventData = new ArrayList<>();
        maps.add(Map.of("limit", 30, "willAttend", 20));
        maps.add(Map.of("limit", 30, "willAttend", 20, "date", TestUtils.EVENT_DATE_LATER));
        maps.add(Map.of("limit", 30, "willAttend", 10));
        maps.add(Map.of("limit", 30, "willAttend", 10, "date", TestUtils.EVENT_DATE_LATER));
        maps.add(Map.of("limit", 20, "willAttend", 20));
        maps.add(Map.of("limit", 20, "willAttend", 20, "date", TestUtils.EVENT_DATE_LATER));
        maps.add(Map.of("limit", 10, "willAttend", 10));
        maps.add(Map.of("limit", 10, "willAttend", 10, "date", TestUtils.EVENT_DATE_LATER));
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
        maps.add(Map.of("limit", 30, "willAttend", 20));
        maps.add(Map.of("limit", 30, "willAttend", 20, "date", TestUtils.EVENT_DATE_LATER));
        maps.add(Map.of("limit", 30, "willAttend", 10));
        maps.add(Map.of("limit", 30, "willAttend", 10, "date", TestUtils.EVENT_DATE_LATER));
        maps.add(Map.of("limit", 30, "willAttend", 20, "attending", TestUtils.USER_2));
        maps.add(Map.of("limit", 30, "willAttend", 20, "attending", TestUtils.USER_2, "date", TestUtils.EVENT_DATE_LATER));
        maps.add(Map.of("limit", 30, "willAttend", 10, "attending", TestUtils.USER_2));
        maps.add(Map.of("limit", 30, "willAttend", 10, "attending", TestUtils.USER_2, "date", TestUtils.EVENT_DATE_LATER));
        maps.add(Map.of("limit", 20, "willAttend", 20));
        maps.add(Map.of("limit", 20, "willAttend", 20, "date", TestUtils.EVENT_DATE_LATER));
        maps.add(Map.of("limit", 10, "willAttend", 10));
        maps.add(Map.of("limit", 10, "willAttend", 10, "date", TestUtils.EVENT_DATE_LATER));
        maps.add(Map.of("limit", 20, "willAttend", 20, "attending", TestUtils.USER_2));
        maps.add(Map.of("limit", 20, "willAttend", 20, "attending", TestUtils.USER_2, "date", TestUtils.EVENT_DATE_LATER));
        maps.add(Map.of("limit", 10, "willAttend", 10, "attending", TestUtils.USER_2));
        maps.add(Map.of("limit", 10, "willAttend", 10, "attending", TestUtils.USER_2, "date", TestUtils.EVENT_DATE_LATER));
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

    // @Test
    // public void testFindAttendanceLimitById(){
    //     Optional<Integer> limit = eventDao.findAttendanceLimitById(TestUtils.EVENT_1_ID);

    //     assertNotNull(limit);
    //     assertTrue(limit.isPresent());
    //     assertEquals(TestUtils.EVENT_ATTENDANCE_LIMIT_DEFAULT, limit.get().intValue());
    // }
    // @Test
    // public void testFindAttendanceLimitNoLimitById(){
    //     Optional<Integer> limit = eventDao.findAttendanceLimitById(TestUtils.EVENT_2_ID);

    //     assertNotNull(limit);
    //     assertFalse(limit.isPresent());
    // }
    // @Test(expected = PersistenceException.class)
    // public void testFindAttendanceLimitById2(){
    //     eventDao.findAttendanceLimitById(12341234);
    // }

    // @Test
    // public void testFindAllBetweenDates(){
    //     List<Event> events = eventDao.findAllBetweenDates(TestUtils.EVENT_DATE_OLDER, TestUtils.EVENT_DATE_DEFAULT.plusDays(-1));

    //     assertNotNull(events);
    //     assertEquals(1, events.size());
    //     TestUtils.assertEqualsEvent(TestUtils.EVENT_OLDER, events.getFirst());
    // }
    // @Test
    // public void testFindAllBetweenDates2(){
    //     List<Event> events = eventDao.findAllBetweenDates(TestUtils.EVENT_DATE_DEFAULT, TestUtils.EVENT_DATE_LATER);

    //     assertNotNull(events);
    //     assertEquals(3, events.size());
    // }

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

    // @Test
    // public void testCountEventsAttendedByUser(){
    //     int eventCount = eventDao.countEventsAttendedByUser(TestUtils.USER_1_ID);

    //     assertEquals(TestUtils.USER_1_ATTENDANCES - 1, eventCount);
    // }
    // @Test
    // public void testCountEventsAttendedByUser2(){
    //     int eventCount = eventDao.countEventsAttendedByUser(TestUtils.USER_1_ID);

    //     assertEquals(TestUtils.USER_2_ATTENDANCES, eventCount);
    // }
    // @Test
    // public void testCountEventsAttendedByUserNotFound(){
    //     int eventCount = eventDao.countEventsAttendedByUser(12341234l);

    //     assertEquals(0, eventCount);
    // }

    @Test
    public void findTopAttendeeCountry(){
        Optional<CountryAttendeeCount> countryAttendee = eventDao.findTopAttendeeCountry(TestUtils.EVENT_1_ID);

        assertNotNull(countryAttendee);
        assertTrue(countryAttendee.isPresent());
        assertEquals(TestUtils.COUNTRY_1_NAME, countryAttendee.get().getCountryName());
        assertEquals(TestUtils.EVENT_1_ATTENDEES, countryAttendee.get().getCount());
    }
    @Test
    public void findTopAttendeeCountryNoAttendees(){
        Optional<CountryAttendeeCount> countryAttendee = eventDao.findTopAttendeeCountry(TestUtils.EVENT_3_ID);

        assertNotNull(countryAttendee);
        assertFalse(countryAttendee.isPresent());
    }
    @Test
    public void findTopAttendeeCountryNoEvent(){
        Optional<CountryAttendeeCount> countryAttendee = eventDao.findTopAttendeeCountry(12341234l);

        assertNotNull(countryAttendee);
        assertFalse(countryAttendee.isPresent());
    }

    // @Test
    // public void testFindEventWithUserInfo(){
    //     Optional<EventWithUserInfo> maybeInfo = eventDao.findEventWithUserInfo(TestUtils.USER_1_ID, TestUtils.EVENT_1_ID);

    //     assertNotNull(maybeInfo);
    //     assertTrue(maybeInfo.isPresent());
    //     assertTrue(maybeInfo.get().isAttending());
    //     assertTrue(maybeInfo.get().isCreator());
    //     TestUtils.assertEqualsEvent(TestUtils.EVENT_1, maybeInfo.get().getEvent());
    // }
    // @Test
    // public void testFindEventWithUserInfo2(){
    //     Optional<EventWithUserInfo> maybeInfo = eventDao.findEventWithUserInfo(TestUtils.USER_2_ID, TestUtils.EVENT_1_ID);

    //     assertNotNull(maybeInfo);
    //     assertTrue(maybeInfo.isPresent());
    //     assertTrue(maybeInfo.get().isAttending());
    //     assertFalse(maybeInfo.get().isCreator());
    //     TestUtils.assertEqualsEvent(TestUtils.EVENT_1, maybeInfo.get().getEvent());
    // }
    // @Test
    // public void testFindEventWithUserInfo3(){
    //     Optional<EventWithUserInfo> maybeInfo = eventDao.findEventWithUserInfo(TestUtils.USER_2_ID, TestUtils.EVENT_OLDER_ID);

    //     assertNotNull(maybeInfo);
    //     assertTrue(maybeInfo.isPresent());
    //     assertFalse(maybeInfo.get().isAttending());
    //     assertFalse(maybeInfo.get().isCreator());
    //     TestUtils.assertEqualsEvent(TestUtils.EVENT_OLDER, maybeInfo.get().getEvent());
    // }

    // @Test
    // public void testDelete(){
    //     int rowsBefore = JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.EVENT_TABLE);

    //     eventDao.delete(TestUtils.EVENT_1_ID);

    //     assertEquals(rowsBefore, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.EVENT_TABLE));
    //     assertEquals(
    //         TestUtils.TOTAL_EVENTS_UPCOMING,
    //         jdbcTemplate.queryForObject(TestUtils.EVENT_COUNT_NOT_DELETED, Integer.class).intValue());
    // }
    // @Test
    // public void testDeleteDeleted(){
    //     int rowsBefore = JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.EVENT_TABLE);

    //     eventDao.delete(TestUtils.EVENT_DELETED_ID);

    //     assertEquals(rowsBefore, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.EVENT_TABLE));
    //     assertEquals(TestUtils.TOTAL_EVENTS_NOT_DELETED, jdbcTemplate.queryForObject(TestUtils.EVENT_COUNT_NOT_DELETED, Integer.class).intValue());
    // }
    // @Test
    // public void testDeleteWrongId(){
    //     int rowsBefore = JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.EVENT_TABLE);

    //     eventDao.delete(12341234);

    //     assertEquals(rowsBefore, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.EVENT_TABLE));
    //     assertEquals(TestUtils.TOTAL_EVENTS_NOT_DELETED, jdbcTemplate.queryForObject(TestUtils.EVENT_COUNT_NOT_DELETED, Integer.class).intValue());
    // }

    // @Test
    // public void testUpdateDeletionMessage(){
    //     eventDao.updateDeletionMessage(TestUtils.EVENT_DELETED_ID, TestUtils.MESSAGE_DEFAULT);

    //     assertEquals(TestUtils.MESSAGE_DEFAULT, jdbcTemplate.queryForObject(TestUtils.EVENT_GET_DELETED_MESSAGE, String.class, TestUtils.EVENT_DELETED_ID));
    // }
    // @Test
    // public void testUpdateDeletionMessageWrongId(){
    //     eventDao.updateDeletionMessage(123123, TestUtils.MESSAGE_DEFAULT);

    //     assertEquals(null, jdbcTemplate.queryForObject(TestUtils.EVENT_GET_DELETED_MESSAGE, String.class, TestUtils.EVENT_DELETED_ID));
    // }

    @Test
    public void testFindByUserEmailPageOne(){
        Page<Event> page1 = eventDao.findByUserEmail(TestUtils.USER_2_MAIL, TestUtils.PAGE_1_DEFAULT);

        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(1, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(2, page1.getContent().size());
        for (Event e : page1.getContent()){
            TestUtils.assertEqualsEvent(TestUtils.EVENT_DATA.get(e.getId()), e);
        }
    }
    @Test
    public void testFindByUserEmailPageTwo(){
        Page<Event> page2 = eventDao.findByUserEmail(TestUtils.USER_2_MAIL, TestUtils.PAGE_2_DEFAULT);

        assertNotNull(page2);
        assertEquals(2, page2.getCurrentPage());
        assertEquals(1, page2.getTotalPages());
        assertNotNull(page2.getContent());
        assertEquals(0, page2.getContent().size());
    }
    @Test
    public void testFindByUserEmailPaged2(){
        TestUtils.deleteEvents(jdbcTemplate);
        Page<Event> userEvents = eventDao.findByUserEmail(TestUtils.USER_1_MAIL, TestUtils.PAGE_1_DEFAULT);

        assertNotNull(userEvents);
        assertEquals(1, userEvents.getCurrentPage());
        assertEquals(0, userEvents.getTotalPages());
        assertNotNull(userEvents.getContent());
        assertEquals(0, userEvents.getContent().size());

    }
    @Test
    public void testFindByUserEmailWrongMailPaged(){
        Page<Event> userEvents = eventDao.findByUserEmail("TestUtils.USER_1_MAIL", TestUtils.PAGE_1_DEFAULT);

        assertNotNull(userEvents);
        assertEquals(1, userEvents.getCurrentPage());
        assertEquals(0, userEvents.getTotalPages());
        assertNotNull(userEvents.getContent());
        assertEquals(0, userEvents.getContent().size());
    }

    @Test
    public void testFindAllPageOne(){
        Page<Event> page1 = eventDao.findAll(TestUtils.PAGE_1_DEFAULT);

        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(2, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(2, page1.getContent().size());

        for (Event e : page1.getContent()){
            TestUtils.assertEqualsEvent(TestUtils.EVENT_DATA.get(e.getId()), e);
        }
    }
    @Test
    public void testFindAllPageTwo(){
        Page<Event> page2 = eventDao.findAll(TestUtils.PAGE_2_DEFAULT);

        assertNotNull(page2);
        assertEquals(2, page2.getCurrentPage());
        assertEquals(2, page2.getTotalPages());
        assertNotNull(page2.getContent());
        assertEquals(2, page2.getContent().size());

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
    public void testSearchPageOne(){
        Page<Event> page1 = eventDao.search("event", TestUtils.PAGE_1_DEFAULT);

        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(2, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(2, page1.getContent().size());
        for (Event e : page1.getContent()){
            TestUtils.assertEqualsEvent(TestUtils.EVENT_DATA.get(e.getId()), e);
        }
    }
    @Test
    public void testSearchPageTwo(){
        Page<Event> page2 = eventDao.search("event", TestUtils.PAGE_2_DEFAULT);

        assertNotNull(page2);
        assertEquals(2, page2.getCurrentPage());
        assertEquals(2, page2.getTotalPages());
        assertNotNull(page2.getContent());
        assertEquals(2, page2.getContent().size());

        for (Event e : page2.getContent()){
            TestUtils.assertEqualsEvent(TestUtils.EVENT_DATA.get(e.getId()), e);
        }
    }
    @Test
    public void testSearchEmptyQueryPageOne(){
        Page<Event> page1 = eventDao.search("", TestUtils.PAGE_1_DEFAULT);

        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(2, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(2, page1.getContent().size());
    }
    @Test
    public void testSearchEmptyQueryPageTwo(){
        Page<Event> page2 = eventDao.search("", TestUtils.PAGE_2_DEFAULT);

        assertNotNull(page2);
        assertEquals(2, page2.getCurrentPage());
        assertEquals(2, page2.getTotalPages());
        assertNotNull(page2.getContent());
        assertEquals(2, page2.getContent().size());
    }
    @Test
    public void testSearchEventsPagedWrongSearch(){
        Page<Event> page1 = eventDao.search("NOTANEVENT", TestUtils.PAGE_1_DEFAULT);

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
    public void testFindAllWithFilters(){
        Page<Event> page = eventDao.findAllWithFilters(
            TestUtils.USER_1_ID,
            null,
            SortFieldEvent.ATTENDEES,
            SortDirection.ASC,
            TestUtils.CITY_1_NAME,
            TestUtils.EVENT_DATE_OLDER,
            TestUtils.EVENT_DATE_LATER,
            null,
            false,
            true,
            true,
            TestUtils.PAGE_1_BIG
        );

        assertNotNull(page);
        assertNotNull(page.getContent());
        assertEquals(1, page.getCurrentPage());
        assertEquals(1, page.getTotalPages());
        assertEquals(1, page.getContent().size());
        TestUtils.assertEqualsEvent(TestUtils.EVENT_2, page.getContent().getFirst());
    }
    @Test
    public void testFindAllWithFiltersComplex(){
        Page<Event> page = eventDao.findAllWithFilters(
            TestUtils.USER_1_ID,
            TestUtils.EVENT_TITLE_2,
            SortFieldEvent.ATTENDEES,
            SortDirection.ASC,
            TestUtils.CITY_1_NAME,
            TestUtils.EVENT_DATE_OLDER,
            TestUtils.EVENT_DATE_LATER,
            null,
            false,
            true,
            true,
            TestUtils.PAGE_1_BIG
        );

        assertNotNull(page);
        assertNotNull(page.getContent());
        assertEquals(1, page.getCurrentPage());
        assertEquals(1, page.getTotalPages());
        assertEquals(1, page.getContent().size());
        TestUtils.assertEqualsEvent(TestUtils.EVENT_2, page.getContent().getFirst());
    }
    @Test
    public void testFindAllWithFiltersComplexNoDestinationPastAttendingNoUser(){
        Page<Event> page = eventDao.findAllWithFilters(
            null,
            TestUtils.EVENT_TITLE_2,
            SortFieldEvent.ATTENDEES,
            SortDirection.ASC,
            "",
            TestUtils.EVENT_DATE_OLDER,
            TestUtils.EVENT_DATE_LATER,
            null,
            true,
            false,
            true,
            TestUtils.PAGE_1_BIG
        );

        assertNotNull(page);
        assertNotNull(page.getContent());
        assertEquals(1, page.getCurrentPage());
        assertEquals(0, page.getTotalPages());
        assertEquals(0, page.getContent().size());
    }
    @Test
    public void testFindAllWithFiltersComplexNoUserUpcoming(){
        Page<Event> page = eventDao.findAllWithFilters(
            null,
            TestUtils.EVENT_TITLE_2,
            SortFieldEvent.ATTENDEES,
            SortDirection.ASC,
            "",
            TestUtils.EVENT_DATE_OLDER,
            TestUtils.EVENT_DATE_LATER,
            null,
            false,
            true,
            false,
            TestUtils.PAGE_1_BIG
        );

        assertNotNull(page);
        assertNotNull(page.getContent());
        assertEquals(1, page.getCurrentPage());
        assertEquals(1, page.getTotalPages());
        assertEquals(1, page.getContent().size());
        TestUtils.assertEqualsEvent(TestUtils.EVENT_2, page.getContent().getFirst());
    }
    @Test
    public void testFindAllWithFiltersComplexNoUserUpcomingNoInterest(){
        Page<Event> page = eventDao.findAllWithFilters(
            null,
            TestUtils.EVENT_TITLE_2,
            SortFieldEvent.ATTENDEES,
            SortDirection.ASC,
            "",
            TestUtils.EVENT_DATE_OLDER,
            TestUtils.EVENT_DATE_LATER,
            "",
            false,
            true,
            false,
            TestUtils.PAGE_1_BIG
        );

        assertNotNull(page);
        assertNotNull(page.getContent());
        assertEquals(1, page.getCurrentPage());
        assertEquals(1, page.getTotalPages());
        assertEquals(1, page.getContent().size());
        TestUtils.assertEqualsEvent(TestUtils.EVENT_2, page.getContent().getFirst());
    }
    @Test
    public void testFindAllWithFiltersNotUpcomingNotAttending(){
        Page<Event> page = eventDao.findAllWithFilters(
            TestUtils.USER_1_ID,
            null,
            SortFieldEvent.DATE,
            SortDirection.ASC,
            TestUtils.CITY_1_NAME,
            TestUtils.EVENT_DATE_OLDER,
            TestUtils.EVENT_DATE_LATER,
            null,
            false,
            false,
            false,
            TestUtils.PAGE_1_BIG
        );

        assertNotNull(page);
        assertNotNull(page.getContent());
        assertEquals(1, page.getCurrentPage());
        assertEquals(1, page.getTotalPages());
        assertEquals(2, page.getContent().size());
        TestUtils.assertEqualsEvent(TestUtils.EVENT_2, page.getContent().get(0));
        TestUtils.assertEqualsEvent(TestUtils.EVENT_3, page.getContent().get(1));
    }
    @Test
    public void testFindAllWithFiltersNotUpcomingNotAttendingReverseSort(){
        Page<Event> page = eventDao.findAllWithFilters(
            TestUtils.USER_1_ID,
            null,
            SortFieldEvent.ATTENDEES,
            SortDirection.DESC,
            TestUtils.CITY_1_NAME,
            TestUtils.EVENT_DATE_OLDER,
            TestUtils.EVENT_DATE_LATER,
            null,
            false,
            false,
            false,
            TestUtils.PAGE_1_BIG
        );

        assertNotNull(page);
        assertNotNull(page.getContent());
        assertEquals(1, page.getCurrentPage());
        assertEquals(1, page.getTotalPages());
        assertEquals(2, page.getContent().size());
        TestUtils.assertEqualsEvent(TestUtils.EVENT_2, page.getContent().get(0));
        TestUtils.assertEqualsEvent(TestUtils.EVENT_3, page.getContent().get(1));
    }
    @Test
    public void testFindAllWithFiltersNoParams(){
        Page<Event> page = eventDao.findAllWithFilters(
            null,
            null,
            null,
            SortDirection.DESC,
            null,
            null,
            null,
            null,
            false,
            false,
            false,
            TestUtils.PAGE_1_BIG
        );

        assertNotNull(page);
        assertNotNull(page.getContent());
        assertEquals(1, page.getCurrentPage());
        assertEquals(1, page.getTotalPages());
        assertEquals(TestUtils.TOTAL_EVENTS_NOT_DELETED, page.getContent().size());
        TestUtils.assertEqualsEvent(TestUtils.EVENT_1, page.getContent().get(0));
        TestUtils.assertEqualsEvent(TestUtils.EVENT_2, page.getContent().get(1));
        TestUtils.assertEqualsEvent(TestUtils.EVENT_3, page.getContent().get(2));
        TestUtils.assertEqualsEvent(TestUtils.EVENT_OLDER, page.getContent().get(3));
    }
    @Test
    public void testFindAllWithFiltersNoParamsEmpty(){
        Page<Event> page = eventDao.findAllWithFilters(
            null,
            "",
            SortFieldEvent.ATTENDEES,
            SortDirection.DESC,
            "",
            null,
            null,
            "",
            false,
            false,
            false,
            TestUtils.PAGE_1_BIG
        );

        assertNotNull(page);
        assertNotNull(page.getContent());
        assertEquals(1, page.getCurrentPage());
        assertEquals(1, page.getTotalPages());
        assertEquals(TestUtils.TOTAL_EVENTS_NOT_DELETED, page.getContent().size());
        TestUtils.assertEqualsEvent(TestUtils.EVENT_1, page.getContent().get(0));
        TestUtils.assertEqualsEvent(TestUtils.EVENT_2, page.getContent().get(1));
        TestUtils.assertEqualsEvent(TestUtils.EVENT_3, page.getContent().get(2));
        TestUtils.assertEqualsEvent(TestUtils.EVENT_OLDER, page.getContent().get(3));
    }
    @Test
    public void testFindAllWithFiltersNoParamsFilter(){
        Page<Event> page = eventDao.findAllWithFilters(
            null,
            TestUtils.EVENT_TITLE_DEFAULT,
            SortFieldEvent.ATTENDEES,
            SortDirection.DESC,
            null,
            null,
            null,
            null,
            false,
            false,
            false,
            TestUtils.PAGE_1_BIG
        );

        assertNotNull(page);
        assertNotNull(page.getContent());
        assertEquals(1, page.getCurrentPage());
        assertEquals(1, page.getTotalPages());
        assertEquals(1, page.getContent().size());
        TestUtils.assertEqualsEvent(TestUtils.EVENT_1, page.getContent().get(0));
    }

    // @Test
    // public void testIncrementAttendeesCount(){
    //     eventDao.incrementAttendeesCount(TestUtils.EVENT_1_ID);

    //     assertEquals(TestUtils.EVENT_1_ATTENDEES + 1, jdbcTemplate.queryForObject(TestUtils.EVENT_GET_ATTENDEES_COUNT_BY_ID, Integer.class, TestUtils.EVENT_1_ID).intValue());
    // }

    // @Test
    // public void updateEvent(){
    //     eventDao.update(
    //         TestUtils.CITY_2_ID,
    //         TestUtils.EVENT_DATE_DEFAULT.plusDays(30),
    //         null,
    //         "RANDOM_EVENT",
    //         null,
    //         null,
    //         null,
    //         TestUtils.EVENT_1_ID,
    //         TestUtils.IMAGE_1_ID
    //     );

    //     Event event = jdbcTemplate.queryForObject(
    //         TestUtils.EVENT_SELECT_BY_ID,
    //         TestUtils.EVENT_ROW_MAPPER,
    //         TestUtils.EVENT_1_ID
    //     );

    //     TestUtils.assertEqualsEvent(event, Map.of(
    //         "city", TestUtils.CITY_2,
    //         "date", TestUtils.EVENT_DATE_DEFAULT.plusDays(30),
    //         "title", "RANDOM_EVENT",
    //         "description", Optional.empty(),
    //         "time", Optional.empty(),
    //         "address", Optional.empty(),
    //         "limit", Optional.empty()));
    // }
    // @Test
    // public void updateEventFull(){
    //     eventDao.update(
    //         TestUtils.CITY_2_ID,
    //         TestUtils.EVENT_DATE_DEFAULT.plusDays(30),
    //         "RANDOM_DESC",
    //         "RANDOM_TITLE",
    //         TestUtils.EVENT_TIME_DEFAULT.plusHours(1),
    //         "RANDOM ADDRESS",
    //         100,
    //         TestUtils.EVENT_1_ID,
    //         TestUtils.IMAGE_2_ID
    //     );

    //     Event event = jdbcTemplate.queryForObject(
    //         TestUtils.EVENT_SELECT_BY_ID,
    //         TestUtils.EVENT_ROW_MAPPER,
    //         TestUtils.EVENT_1_ID
    //     );

    //     TestUtils.assertEqualsEvent(event, Map.of(
    //         "city", TestUtils.CITY_2,
    //         "date", TestUtils.EVENT_DATE_DEFAULT.plusDays(30),
    //         "title", "RANDOM_TITLE",
    //         "description", Optional.of("RANDOM_DESC"),
    //         "time", Optional.of(TestUtils.EVENT_TIME_DEFAULT.plusHours(1)),
    //         "address", Optional.of("RANDOM ADDRESS"),
    //         "limit", Optional.of(100),
    //         "image", TestUtils.IMAGE_2
    //     ));
    // }
    // @Test
    // public void updateEventWrongId(){
    //     eventDao.update(
    //         TestUtils.CITY_1_ID,
    //         TestUtils.EVENT_DATE_DEFAULT.plusDays(30),
    //         TestUtils.EVENT_DESCRIPTION_DEFAULT,
    //         TestUtils.EVENT_TITLE_DEFAULT,
    //         null,
    //         null,
    //         null,
    //         1231234,
    //         TestUtils.IMAGE_1_ID
    //     );

    //     Event event = jdbcTemplate.queryForObject(
    //         TestUtils.EVENT_SELECT_BY_ID,
    //         TestUtils.EVENT_ROW_MAPPER,
    //         TestUtils.EVENT_1_ID
    //     );

    //     TestUtils.assertEqualsEvent(event);
    // }

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
        maps.add(Map.of("user", TestUtils.USER_2, "limit", 30, "willAttend", 20));
        maps.add(Map.of("user", TestUtils.USER_2, "limit", 30, "willAttend", 20, "date", TestUtils.EVENT_DATE_LATER));
        maps.add(Map.of("user", TestUtils.USER_2, "limit", 30, "willAttend", 10));
        maps.add(Map.of("user", TestUtils.USER_2, "limit", 30, "willAttend", 10, "date", TestUtils.EVENT_DATE_LATER));
        maps.add(Map.of("user", TestUtils.USER_2, "limit", 30, "willAttend", 20, "attending", TestUtils.USER_1));
        maps.add(Map.of("user", TestUtils.USER_2, "limit", 30, "willAttend", 20, "attending", TestUtils.USER_1, "date", TestUtils.EVENT_DATE_LATER));
        maps.add(Map.of("user", TestUtils.USER_2, "limit", 30, "willAttend", 10, "attending", TestUtils.USER_1));
        maps.add(Map.of("user", TestUtils.USER_2, "limit", 30, "willAttend", 10, "attending", TestUtils.USER_1, "date", TestUtils.EVENT_DATE_LATER));
        maps.add(Map.of("user", TestUtils.USER_2, "limit", 20, "willAttend", 20));
        maps.add(Map.of("user", TestUtils.USER_2, "limit", 20, "willAttend", 20, "date", TestUtils.EVENT_DATE_LATER));
        maps.add(Map.of("user", TestUtils.USER_2, "limit", 10, "willAttend", 10));
        maps.add(Map.of("user", TestUtils.USER_2, "limit", 10, "willAttend", 10, "date", TestUtils.EVENT_DATE_LATER));
        maps.add(Map.of("user", TestUtils.USER_2, "limit", 20, "willAttend", 20, "attending", TestUtils.USER_1));
        maps.add(Map.of("user", TestUtils.USER_2, "limit", 20, "willAttend", 20, "attending", TestUtils.USER_1, "date", TestUtils.EVENT_DATE_LATER));
        maps.add(Map.of("user", TestUtils.USER_2, "limit", 10, "willAttend", 10, "attending", TestUtils.USER_1));
        maps.add(Map.of("user", TestUtils.USER_2, "limit", 10, "willAttend", 10, "attending", TestUtils.USER_1, "date", TestUtils.EVENT_DATE_LATER));
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

    @Test
    public void testFindAllEventsByAttendeePageOne(){
        Page<Event> page1 = eventDao.findAllEventsByAttendee(TestUtils.USER_1_ID, TestUtils.PAGE_1_SINGLE);

        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(1, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(1, page1.getContent().size());
    }

    @Test
    public void testFindAllEventsByAttendeePageTwo(){
        Page<Event> page2 = eventDao.findAllEventsByAttendee(TestUtils.USER_1_ID, TestUtils.PAGE_2_SINGLE);

        assertNotNull(page2);
        assertEquals(2, page2.getCurrentPage());
        assertEquals(1, page2.getTotalPages());
        assertNotNull(page2.getContent());
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