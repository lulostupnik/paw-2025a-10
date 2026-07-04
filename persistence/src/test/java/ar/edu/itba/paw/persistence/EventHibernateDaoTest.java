package ar.edu.itba.paw.persistence;

import static org.junit.Assert.*;
import static ar.edu.itba.paw.persistence.TestUtils.*;

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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.itba.paw.models.City;
import ar.edu.itba.paw.models.CountryAttendeeCount;
import ar.edu.itba.paw.models.Event;
import ar.edu.itba.paw.models.Page;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.enums.SortDirection;
import ar.edu.itba.paw.models.enums.SortFieldEvent;
import ar.edu.itba.paw.persistence.config.TestConfig;

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
        Event event = eventDao.create(
            USER_1, 
            CITY_1, 
            EVENT_DATE_DEFAULT, 
            EVENT_DESCRIPTION_DEFAULT, 
            IMAGE_1_ID, 
            EVENT_TITLE_DEFAULT, 
            EVENT_TIME_DEFAULT, 
            EVENT_ADDRESS_DEFAULT, 
            EVENT_ATTENDANCE_LIMIT_DEFAULT
        );
        em.flush();

        assertEqualsEvent(event, Map.of("id", event.getId(), "attendees", 1));
        assertEquals(
            1,
            JdbcTestUtils.countRowsInTableWhere(
                jdbcTemplate, EVENT_TABLE,
                "id = " + event.getId()
                    + " AND user_id = " + USER_1_ID
                    + " AND city_id = " + CITY_1_ID
                    + " AND flyer_image_id = " + IMAGE_1_ID
                    + " AND title = '" + EVENT_TITLE_DEFAULT + "'"
                    + " AND description = '" + EVENT_DESCRIPTION_DEFAULT + "'"
                    + " AND address = '" + EVENT_ADDRESS_DEFAULT + "'"
                    + " AND attendees_limit = " + EVENT_ATTENDANCE_LIMIT_DEFAULT
                    + " AND deleted = FALSE"
            )
        );
    }
    @Test
    public void testCreateNoAddress(){
        Event event = eventDao.create(
            USER_1, 
            CITY_1, 
            EVENT_DATE_DEFAULT, 
            EVENT_DESCRIPTION_DEFAULT, 
            IMAGE_1_ID, 
            EVENT_TITLE_DEFAULT, 
            EVENT_TIME_DEFAULT, 
            null, 
            EVENT_ATTENDANCE_LIMIT_DEFAULT
        );
        em.flush();

        HashMap<String, Object> eventParams = new HashMap<>();
        eventParams.put("id", event.getId());
        eventParams.put("attendees", 1);
        eventParams.put("address", null);
        assertEqualsEvent(event, eventParams);
        assertEquals(
            1,
            JdbcTestUtils.countRowsInTableWhere(
                jdbcTemplate, EVENT_TABLE,
                "id = " + event.getId()
                    + " AND user_id = " + USER_1_ID
                    + " AND city_id = " + CITY_1_ID
                    + " AND flyer_image_id = " + IMAGE_1_ID
                    + " AND title = '" + EVENT_TITLE_DEFAULT + "'"
                    + " AND description = '" + EVENT_DESCRIPTION_DEFAULT + "'"
                    + " AND address IS NULL"
                    + " AND attendees_limit = " + EVENT_ATTENDANCE_LIMIT_DEFAULT
                    + " AND deleted = FALSE"
            )
        );
    }
    @Test
    public void testCreateNoAddressNoLimit(){
        Event event = eventDao.create(
            USER_1, 
            CITY_1, 
            EVENT_DATE_DEFAULT, 
            EVENT_DESCRIPTION_DEFAULT, 
            IMAGE_1_ID, 
            EVENT_TITLE_DEFAULT, 
            EVENT_TIME_DEFAULT, 
            null, 
            null
        );
        em.flush();

        HashMap<String, Object> eventParams = new HashMap<>();
        eventParams.put("id", event.getId());
        eventParams.put("attendees", 1);
        eventParams.put("address", null);
        eventParams.put("limit", null);
        assertEqualsEvent(event, eventParams);
        assertEquals(
            1,
            JdbcTestUtils.countRowsInTableWhere(
                jdbcTemplate, EVENT_TABLE,
                "id = " + event.getId()
                    + " AND user_id = " + USER_1_ID
                    + " AND city_id = " + CITY_1_ID
                    + " AND flyer_image_id = " + IMAGE_1_ID
                    + " AND title = '" + EVENT_TITLE_DEFAULT + "'"
                    + " AND description = '" + EVENT_DESCRIPTION_DEFAULT + "'"
                    + " AND address IS NULL"
                    + " AND attendees_limit IS NULL"
                    + " AND deleted = FALSE"
            )
        );
    }
    @Test
    public void testCreateNoAddressNoLimitNoTime(){
        Event event = eventDao.create(
            USER_1, 
            CITY_1, 
            EVENT_DATE_DEFAULT, 
            EVENT_DESCRIPTION_DEFAULT, 
            IMAGE_1_ID,
            EVENT_TITLE_DEFAULT, 
            null, 
            null, 
            null
        );
        em.flush();

        HashMap<String, Object> eventParams = new HashMap<>();
        eventParams.put("id", event.getId());
        eventParams.put("attendees", 1);
        eventParams.put("address", null);
        eventParams.put("limit", null);
        eventParams.put("time", null);
        assertEqualsEvent(event, eventParams);
        assertEquals(
            1,
            JdbcTestUtils.countRowsInTableWhere(
                jdbcTemplate, EVENT_TABLE,
                "id = " + event.getId()
                    + " AND user_id = " + USER_1_ID
                    + " AND city_id = " + CITY_1_ID
                    + " AND flyer_image_id = " + IMAGE_1_ID
                    + " AND title = '" + EVENT_TITLE_DEFAULT + "'"
                    + " AND description = '" + EVENT_DESCRIPTION_DEFAULT + "'"
                    + " AND address IS NULL"
                    + " AND attendees_limit IS NULL"
                    + " AND event_time IS NULL"
                    + " AND deleted = FALSE"
            )
        );
    }
    @Test(expected = PersistenceException.class)
    public void testCreateWrongUser(){
        eventDao.create(
            new User(
                12341234l, 
                null, 
                null, 
                null, 
                null, 
                null, 
                null, 
                0L,
                null, 
                false, 
                false), 
            CITY_1, 
            EVENT_DATE_DEFAULT, 
            EVENT_DESCRIPTION_DEFAULT, 
            0L,
            EVENT_TITLE_DEFAULT, 
            EVENT_TIME_DEFAULT, 
            null, 
            EVENT_ATTENDANCE_LIMIT_DEFAULT
        );
        em.flush();
    }
    @Test(expected = PersistenceException.class)
    public void testCreateWrongCity(){
        eventDao.create(
            USER_1, 
            new City(null, null, 12341234l), 
            EVENT_DATE_DEFAULT, 
            EVENT_DESCRIPTION_DEFAULT, 
            IMAGE_1_ID, 
            EVENT_TITLE_DEFAULT, 
            EVENT_TIME_DEFAULT, 
            null, 
            EVENT_ATTENDANCE_LIMIT_DEFAULT
        );
        em.flush();
    }
    @Test
    public void testCreateNoDescription(){
        Event event = eventDao.create(
            USER_1, 
            CITY_1, 
            EVENT_DATE_DEFAULT, 
            null, 
            IMAGE_1_ID, 
            EVENT_TITLE_DEFAULT, 
            EVENT_TIME_DEFAULT, 
            EVENT_ADDRESS_DEFAULT, 
            EVENT_ATTENDANCE_LIMIT_DEFAULT
        );
        em.flush();
        
        HashMap<String, Object> eventParams = new HashMap<>();
        eventParams.put("id", event.getId());
        eventParams.put("attendees", 1);
        eventParams.put("description", null);
        assertEqualsEvent(event, eventParams);
        assertEquals(
            1,
            JdbcTestUtils.countRowsInTableWhere(
                jdbcTemplate, EVENT_TABLE,
                "id = " + event.getId()
                    + " AND user_id = " + USER_1_ID
                    + " AND city_id = " + CITY_1_ID
                    + " AND flyer_image_id = " + IMAGE_1_ID
                    + " AND title = '" + EVENT_TITLE_DEFAULT + "'"
                    + " AND description IS NULL"
                    + " AND address = '" + EVENT_ADDRESS_DEFAULT + "'"
                    + " AND attendees_limit = " + EVENT_ATTENDANCE_LIMIT_DEFAULT
                    + " AND deleted = FALSE"
            )
        );
    }
    @Test(expected = PersistenceException.class)
    public void testCreateWrongImage(){
        eventDao.create(
            USER_1, 
            CITY_1, 
            EVENT_DATE_DEFAULT, 
            EVENT_DESCRIPTION_DEFAULT, 
            12341234L,
            EVENT_TITLE_DEFAULT, 
            EVENT_TIME_DEFAULT, 
            EVENT_ADDRESS_DEFAULT, 
            EVENT_ATTENDANCE_LIMIT_DEFAULT
        );
        em.flush();
    }
    @Test(expected = PersistenceException.class)
    public void testCreateNoTitle(){
        eventDao.create(
            USER_1, 
            CITY_1, 
            EVENT_DATE_DEFAULT, 
            EVENT_DESCRIPTION_DEFAULT, 
            IMAGE_1_ID, 
            null, 
            EVENT_TIME_DEFAULT, 
            EVENT_ADDRESS_DEFAULT, 
            EVENT_ATTENDANCE_LIMIT_DEFAULT
        );
        em.flush();
    }

    @Test
    public void testFindById(){
        Optional<Event> maybeEvent = eventDao.findById(EVENT_1_ID);

        assertNotNull(maybeEvent);
        assertTrue(maybeEvent.isPresent());
        assertEqualsEvent(EVENT_1, maybeEvent.get());
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
        Optional<Event> maybeEvent = eventDao.findById(EVENT_DELETED_ID);

        assertNotNull(maybeEvent);
        assertFalse(maybeEvent.isPresent());
    }

    @Test
    public void testFindTopByUser(){
        deleteEvents(jdbcTemplate);
        List<Map<String, Object>> maps = new ArrayList<>();
        List<Event> eventData = new ArrayList<>();
        maps.add(Map.of("limit", 30, "willAttend", 20));
        maps.add(Map.of("limit", 30, "willAttend", 20, "date", EVENT_DATE_LATER));
        maps.add(Map.of("limit", 30, "willAttend", 10));
        maps.add(Map.of("limit", 30, "willAttend", 10, "date", EVENT_DATE_LATER));
        maps.add(Map.of("limit", 30, "willAttend", 20, "attending", USER_2));
        maps.add(Map.of("limit", 30, "willAttend", 20, "attending", USER_2, "date", EVENT_DATE_LATER));
        maps.add(Map.of("limit", 30, "willAttend", 10, "attending", USER_2));
        maps.add(Map.of("limit", 30, "willAttend", 10, "attending", USER_2, "date", EVENT_DATE_LATER));
        maps.add(Map.of("limit", 20, "willAttend", 20));
        maps.add(Map.of("limit", 20, "willAttend", 20, "date", EVENT_DATE_LATER));
        maps.add(Map.of("limit", 10, "willAttend", 10));
        maps.add(Map.of("limit", 10, "willAttend", 10, "date", EVENT_DATE_LATER));
        maps.add(Map.of("limit", 20, "willAttend", 20, "attending", USER_2));
        maps.add(Map.of("limit", 20, "willAttend", 20, "attending", USER_2, "date", EVENT_DATE_LATER));
        maps.add(Map.of("limit", 10, "willAttend", 10, "attending", USER_2));
        maps.add(Map.of("limit", 10, "willAttend", 10, "attending", USER_2, "date", EVENT_DATE_LATER));
        maps.forEach((params) -> eventData.add(insertEvent(ds, params)));

        Page<Event> events = eventDao.findTopByUser(USER_2_ID, PAGE_1_BIG);

        assertNotNull(events);
        assertNotNull(events.getContent());
        assertEquals(eventData.size(), events.getContent().size());
        ListIterator<Event> iterator = eventData.listIterator();
        while (iterator.hasNext()){
            assertEqualsEvent(events.getContent().get(iterator.nextIndex()), iterator.next());
        }
    }
    @Test
    public void testFindTopByUserEventsNoEvents(){
        deleteEvents(jdbcTemplate);
        insertEvent(ds, Map.of("deleted", true));
        insertEvent(ds, Map.of("date", EVENT_DATE_DEFAULT.plusDays(-100)));

        Page<Event> page1 = eventDao.findTopByUser(USER_2_ID, PAGE_1_BIG);

        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(0, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(0, page1.getContent().size());
    }

    @Test
    public void testSearchPageOne(){
        Page<Event> page1 = eventDao.search("event", PAGE_1_DEFAULT);

        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(2, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(2, page1.getContent().size());
        page1.getContent().forEach((e) -> 
            assertEqualsEvent(EVENT_DATA.get(e.getId()), e)
        );

    }
    @Test
    public void testSearchPageTwo(){
        Page<Event> page2 = eventDao.search("event", PAGE_2_DEFAULT);

        assertNotNull(page2);
        assertEquals(2, page2.getCurrentPage());
        assertEquals(2, page2.getTotalPages());
        assertNotNull(page2.getContent());
        assertEquals(2, page2.getContent().size());

        page2.getContent().forEach((e) -> 
            assertEqualsEvent(EVENT_DATA.get(e.getId()), e)
        );
    }
    @Test
    public void testSearchEmptyQueryPageOne(){
        Page<Event> page1 = eventDao.search("", PAGE_1_DEFAULT);

        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(2, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(2, page1.getContent().size());
    }
    @Test
    public void testSearchEmptyQueryPageTwo(){
        Page<Event> page2 = eventDao.search("", PAGE_2_DEFAULT);

        assertNotNull(page2);
        assertEquals(2, page2.getCurrentPage());
        assertEquals(2, page2.getTotalPages());
        assertNotNull(page2.getContent());
        assertEquals(2, page2.getContent().size());
    }
    @Test
    public void testSearchEventsPagedWrongSearch(){
        Page<Event> page1 = eventDao.search("NOTANEVENT", PAGE_1_DEFAULT);

        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(0, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(0, page1.getContent().size());
    }
    @Test
    public void testSearchEventsPagedNoEvents(){
        deleteEvents(jdbcTemplate);

        Page<Event> page1 = eventDao.search("", PAGE_1_DEFAULT);

        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(0, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(0, page1.getContent().size());
    }

    @Test
    public void findTopAttendeeCountry(){
        Optional<CountryAttendeeCount> countryAttendee = eventDao.findTopAttendeeCountry(EVENT_1_ID);

        assertNotNull(countryAttendee);
        assertTrue(countryAttendee.isPresent());
        assertEquals(COUNTRY_1_NAME, countryAttendee.get().getCountryName());
        assertEquals(EVENT_1_ATTENDEES, countryAttendee.get().getCount());
    }
    @Test
    public void findTopAttendeeCountryNoAttendees(){
        Optional<CountryAttendeeCount> countryAttendee = eventDao.findTopAttendeeCountry(EVENT_3_ID);

        assertNotNull(countryAttendee);
        assertFalse(countryAttendee.isPresent());
    }
    @Test
    public void findTopAttendeeCountryNoEvent(){
        Optional<CountryAttendeeCount> countryAttendee = eventDao.findTopAttendeeCountry(12341234l);

        assertNotNull(countryAttendee);
        assertFalse(countryAttendee.isPresent());
    }

    @Test
    public void testFindByUserIdPageOne(){
        Page<Event> page1 = eventDao.findByUserId(USER_2_ID, PAGE_1_DEFAULT);

        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(1, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(2, page1.getContent().size());
        page1.getContent().forEach((e) -> 
            assertEqualsEvent(EVENT_DATA.get(e.getId()), e)
        );
    }
    @Test
    public void testFindByUserIdPageTwo(){
        Page<Event> page2 = eventDao.findByUserId(USER_2_ID, PAGE_2_DEFAULT);

        assertNotNull(page2);
        assertEquals(2, page2.getCurrentPage());
        assertEquals(1, page2.getTotalPages());
        assertNotNull(page2.getContent());
        assertEquals(0, page2.getContent().size());
    }
    @Test
    public void testFindByUserIdPaged2(){
        deleteEvents(jdbcTemplate);

        Page<Event> page1 = eventDao.findByUserId(USER_1_ID, PAGE_1_DEFAULT);

        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(0, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(0, page1.getContent().size());
    }
    @Test
    public void testFindByUserIdWrongMailPaged(){
        Page<Event> userEvents = eventDao.findByUserId(12341234l, PAGE_1_DEFAULT);

        assertNotNull(userEvents);
        assertEquals(1, userEvents.getCurrentPage());
        assertEquals(0, userEvents.getTotalPages());
        assertNotNull(userEvents.getContent());
        assertEquals(0, userEvents.getContent().size());
    }

    @Test
    public void testFindRecommended(){
        deleteEvents(jdbcTemplate);
        Map<String, Object> event1params = Map.of("user", USER_2);
        Map<String, Object> event2params = Map.of("user", USER_2, "attending", USER_1);
        Event event1 = insertEvent(ds, event1params);
        Event event2 = insertEvent(ds, event2params);
        insertEvent(ds, Map.of("user", USER_2, "deleted", true));
        insertEvent(ds, Map.of("user", USER_2, "city", CITY_2));
        insertEvent(ds, Map.of("user", USER_2, "date", LocalDate.now().plusDays(-2)));
        Map<Long, Event> eventInfo = Map.of(event1.getId(), event1, event2.getId(), event2);

        Page<Event> events = eventDao.findRecommended(USER_1_ID, PAGE_1_BIG);

        assertNotNull(events);
        assertEquals(1, events.getCurrentPage());
        assertEquals(1, events.getTotalPages());
        assertNotNull(events.getContent());
        assertEquals(2, events.getContent().size());
        events.getContent().forEach((e) ->
            assertEqualsEvent(eventInfo.get(e.getId()), e)
        );
    }
    @Test
    public void testFindRecommendedEventsNoEvents(){
        deleteEvents(jdbcTemplate);

        Page<Event> events = eventDao.findRecommended(USER_1_ID, PAGE_1_BIG);

        assertNotNull(events);
        assertEquals(1, events.getCurrentPage());
        assertEquals(0, events.getTotalPages());
        assertNotNull(events.getContent());
        assertEquals(0, events.getContent().size());
    }
    @Test
    public void testFindRecommendedComplex(){
        deleteEvents(jdbcTemplate);
        List<Map<String, Object>> maps = new ArrayList<>();
        List<Event> eventData = new ArrayList<>();
        maps.add(Map.of("user", USER_2, "limit", 30, "willAttend", 20));
        maps.add(Map.of("user", USER_2, "limit", 30, "willAttend", 20, "date", EVENT_DATE_LATER));
        maps.add(Map.of("user", USER_2, "limit", 30, "willAttend", 10));
        maps.add(Map.of("user", USER_2, "limit", 30, "willAttend", 10, "date", EVENT_DATE_LATER));
        maps.add(Map.of("user", USER_2, "limit", 30, "willAttend", 20, "attending", USER_1));
        maps.add(Map.of("user", USER_2, "limit", 30, "willAttend", 20, "attending", USER_1, "date", EVENT_DATE_LATER));
        maps.add(Map.of("user", USER_2, "limit", 30, "willAttend", 10, "attending", USER_1));
        maps.add(Map.of("user", USER_2, "limit", 30, "willAttend", 10, "attending", USER_1, "date", EVENT_DATE_LATER));
        maps.add(Map.of("user", USER_2, "limit", 20, "willAttend", 20));
        maps.add(Map.of("user", USER_2, "limit", 20, "willAttend", 20, "date", EVENT_DATE_LATER));
        maps.add(Map.of("user", USER_2, "limit", 10, "willAttend", 10));
        maps.add(Map.of("user", USER_2, "limit", 10, "willAttend", 10, "date", EVENT_DATE_LATER));
        maps.add(Map.of("user", USER_2, "limit", 20, "willAttend", 20, "attending", USER_1));
        maps.add(Map.of("user", USER_2, "limit", 20, "willAttend", 20, "attending", USER_1, "date", EVENT_DATE_LATER));
        maps.add(Map.of("user", USER_2, "limit", 10, "willAttend", 10, "attending", USER_1));
        maps.add(Map.of("user", USER_2, "limit", 10, "willAttend", 10, "attending", USER_1, "date", EVENT_DATE_LATER));
        maps.forEach((params) ->
            eventData.add(insertEvent(ds, params))
        );

        Page<Event> events = eventDao.findRecommended(USER_1_ID, PAGE_1_BIG);

        assertNotNull(events);
        assertNotNull(events.getContent());
        assertEquals(eventData.size(), events.getContent().size());
        ListIterator<Event> iterator = eventData.listIterator();
        while (iterator.hasNext()){
            assertEqualsEvent(events.getContent().get(iterator.nextIndex()), iterator.next());
        }
    }

    @Test
    public void testFindTop(){
        deleteEvents(jdbcTemplate);
        List<Map<String, Object>> maps = new ArrayList<>();
        List<Event> eventData = new ArrayList<>();
        maps.add(Map.of("limit", 30, "willAttend", 20));
        maps.add(Map.of("limit", 30, "willAttend", 20, "date", EVENT_DATE_LATER));
        maps.add(Map.of("limit", 30, "willAttend", 10));
        maps.add(Map.of("limit", 30, "willAttend", 10, "date", EVENT_DATE_LATER));
        maps.add(Map.of("limit", 20, "willAttend", 20));
        maps.add(Map.of("limit", 20, "willAttend", 20, "date", EVENT_DATE_LATER));
        maps.add(Map.of("limit", 10, "willAttend", 10));
        maps.add(Map.of("limit", 10, "willAttend", 10, "date", EVENT_DATE_LATER));
        maps.forEach((params)->
            eventData.add(insertEvent(ds, params))
        );

        Page<Event> events = eventDao.findTop(PAGE_1_BIG);

        assertNotNull(events);
        assertNotNull(events.getContent());
        assertEquals(eventData.size(), events.getContent().size());
        ListIterator<Event> iterator = eventData.listIterator();
        while (iterator.hasNext()){
            assertEqualsEvent(events.getContent().get(iterator.nextIndex()), iterator.next());
        }
    }
    @Test
    public void testFindTopEventsNoEvents(){
        deleteEventsValid(jdbcTemplate);

        Page<Event> page1 = eventDao.findTop(PAGE_1_BIG);

        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(0, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(0, page1.getContent().size());
    }

    @Test
    public void testFindAllPageOne(){
        Page<Event> page1 = eventDao.findAll(PAGE_1_DEFAULT);

        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(2, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(2, page1.getContent().size());

        page1.getContent().forEach((e) -> 
            assertEqualsEvent(EVENT_DATA.get(e.getId()), e)
        );
    }
    @Test
    public void testFindAllPageTwo(){
        Page<Event> page2 = eventDao.findAll(PAGE_2_DEFAULT);

        assertNotNull(page2);
        assertEquals(2, page2.getCurrentPage());
        assertEquals(2, page2.getTotalPages());
        assertNotNull(page2.getContent());
        assertEquals(2, page2.getContent().size());

        page2.getContent().forEach((e) -> 
            assertEqualsEvent(EVENT_DATA.get(e.getId()), e)
        );
    }
    @Test
    public void testFindAllNoEventsPaged(){
        deleteEvents(jdbcTemplate);

        Page<Event> events = eventDao.findAll(PAGE_1_DEFAULT);

        assertNotNull(events);
        assertEquals(1, events.getCurrentPage());
        assertEquals(0, events.getTotalPages());
        assertNotNull(events.getContent());
        assertEquals(0, events.getContent().size());
    }

    @Test
    public void testCountEventsCreatedByUser(){
        int eventCount = eventDao.countEventsCreatedByUser(USER_1_ID);

        assertEquals(USER_1_CREATED_EVENTS, eventCount);
    }
    @Test
    public void testCountEventsCreatedByUser2(){
        int eventCount = eventDao.countEventsCreatedByUser(USER_1_ID);

        assertEquals(USER_2_CREATED_EVENTS, eventCount);
    }
    @Test
    public void testCountEventsCreatedByUserNotFound(){
        int eventCount = eventDao.countEventsCreatedByUser(12341234l);

        assertEquals(0, eventCount);
    }

    @Test
    public void testFindAllBetweenDates(){
        Page<Event> events = eventDao.findAllBetweenDates(
            EVENT_DATE_OLDER, 
            EVENT_DATE_DEFAULT.plusDays(-1), 
            PAGE_1_BIG
        );

        assertNotNull(events);
        assertEquals(1, events.getCurrentPage());
        assertEquals(1, events.getTotalPages());
        assertEquals(1, events.getContent().size());
        assertEqualsEvent(EVENT_OLDER, events.getContent().getFirst());
    }
    @Test
    public void testFindAllBetweenDates2(){
        Page<Event> events = eventDao.findAllBetweenDates(
            EVENT_DATE_DEFAULT, 
            EVENT_DATE_LATER, 
            PAGE_1_BIG
        );

        assertNotNull(events);
        assertEquals(1, events.getCurrentPage());
        assertEquals(1, events.getTotalPages());
        assertEquals(TOTAL_EVENTS_UPCOMING, events.getContent().size());
    }

    @Test
    public void testFindAllWithFilters(){
        Page<Event> page = eventDao.findAllWithFilters(
            USER_1_ID,
            null,
            SortFieldEvent.ATTENDEES,
            SortDirection.ASC,
            CITY_1_NAME,
            EVENT_DATE_OLDER,
            EVENT_DATE_LATER,
            null,
            null,
            null,
            null,
            null,
            null,
            true,
            PAGE_1_BIG
        );

        assertNotNull(page);
        assertNotNull(page.getContent());
        assertEquals(1, page.getCurrentPage());
        assertEquals(1, page.getTotalPages());
        assertEquals(2, page.getContent().size());
    }
    @Test
    public void testFindAllWithFiltersComplex(){
        Page<Event> page = eventDao.findAllWithFilters(
            USER_1_ID,
            EVENT_TITLE_2,
            SortFieldEvent.ATTENDEES,
            SortDirection.ASC,
            CITY_1_NAME,
            EVENT_DATE_OLDER,
            EVENT_DATE_LATER,
            null,
            null,
            null,
            null,
            null,
            null,
            false,
            PAGE_1_BIG
        );

        assertNotNull(page);
        assertNotNull(page.getContent());
        assertEquals(1, page.getCurrentPage());
        assertEquals(0, page.getTotalPages());
        assertEquals(0, page.getContent().size());
    }
    @Test
    public void testFindAllWithFiltersComplexAttending(){
        Page<Event> page = eventDao.findAllWithFilters(
            USER_1_ID,
            EVENT_TITLE_2,
            SortFieldEvent.ATTENDEES,
            SortDirection.ASC,
            CITY_1_NAME,
            EVENT_DATE_OLDER,
            EVENT_DATE_LATER,
            null,
            null,
            null,
            null,
            null,
            null,
            false,
            PAGE_1_BIG
        );

        assertNotNull(page);
        assertNotNull(page.getContent());
        assertEquals(1, page.getCurrentPage());
        assertEquals(0, page.getTotalPages());
        assertEquals(0, page.getContent().size());
    }
    @Test
    public void testFindAllWithFiltersComplexNoDestinationPastAttendingNoUser(){
        Page<Event> page = eventDao.findAllWithFilters(
            null,
            EVENT_TITLE_DELETED,
            SortFieldEvent.ATTENDEES,
            SortDirection.ASC,
            "",
            EVENT_DATE_OLDER,
            EVENT_DATE_LATER,
            null,
            null,
            null,
            null,
            null,
            null,
            false,
            PAGE_1_BIG
        );

        assertNotNull(page);
        assertNotNull(page.getContent());
        assertEquals(1, page.getCurrentPage());
        assertEquals(0, page.getTotalPages());
        assertEquals(0, page.getContent().size());
    }
    @Test
    public void testFindAllWithFiltersComplexNoUser(){
        Page<Event> page = eventDao.findAllWithFilters(
            null,
            EVENT_TITLE_2,
            SortFieldEvent.ATTENDEES,
            SortDirection.ASC,
            "",
            EVENT_DATE_OLDER,
            EVENT_DATE_LATER,
            null,
            null,
            null,
            null,
            null,
            null,
            true,
            PAGE_1_BIG
        );

        assertNotNull(page);
        assertNotNull(page.getContent());
        assertEquals(1, page.getCurrentPage());
        assertEquals(1, page.getTotalPages());
        assertEquals(1, page.getContent().size());
        assertEqualsEvent(EVENT_2, page.getContent().getFirst());
    }
    @Test
    public void testFindAllWithFiltersComplexNoUserNoInterest(){
        Page<Event> page = eventDao.findAllWithFilters(
            null,
            EVENT_TITLE_2,
            SortFieldEvent.ATTENDEES,
            SortDirection.ASC,
            "",
            EVENT_DATE_OLDER,
            EVENT_DATE_LATER,
            null,
            null,
            "",
            null,
            null,
            null,
            true,
            PAGE_1_BIG
        );

        assertNotNull(page);
        assertNotNull(page.getContent());
        assertEquals(1, page.getCurrentPage());
        assertEquals(1, page.getTotalPages());
        assertEquals(1, page.getContent().size());
        assertEqualsEvent(EVENT_2, page.getContent().getFirst());
    }
    @Test
    public void testFindAllWithFiltersNotAttending(){
        Page<Event> page = eventDao.findAllWithFilters(
            USER_1_ID,
            null,
            SortFieldEvent.DATE,
            SortDirection.ASC,
            CITY_1_NAME,
            EVENT_DATE_OLDER,
            EVENT_DATE_LATER,
            null,
            null,
            null,
            null,
            null,
            null,
            true,
            PAGE_1_BIG
        );

        assertNotNull(page);
        assertNotNull(page.getContent());
        assertEquals(1, page.getCurrentPage());
        assertEquals(1, page.getTotalPages());
        assertEquals(2, page.getContent().size());
        assertEqualsEvent(EVENT_OLDER, page.getContent().get(0));
        assertEqualsEvent(EVENT_1, page.getContent().get(1));
    }
    @Test
    public void testFindAllWithFiltersNotAttendingReverseSort(){
        Page<Event> page = eventDao.findAllWithFilters(
            USER_1_ID,
            null,
            SortFieldEvent.ATTENDEES,
            SortDirection.DESC,
            CITY_1_NAME,
            EVENT_DATE_OLDER,
            EVENT_DATE_LATER,
            null,
            null,
            null,
            null,
            null,
            null,
            true,
            PAGE_1_BIG
        );

        assertNotNull(page);
        assertNotNull(page.getContent());
        assertEquals(1, page.getCurrentPage());
        assertEquals(1, page.getTotalPages());
        assertEquals(2, page.getContent().size());
        assertEqualsEvent(EVENT_1, page.getContent().get(0));
        assertEqualsEvent(EVENT_OLDER, page.getContent().get(1));
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
            null,
            null,
            null,
            null,
            null,
            true,
            PAGE_1_BIG
        );

        assertNotNull(page);
        assertNotNull(page.getContent());
        assertEquals(1, page.getCurrentPage());
        assertEquals(1, page.getTotalPages());
        assertEquals(TOTAL_EVENTS_NOT_DELETED, page.getContent().size());
        assertEqualsEvent(EVENT_OLDER, page.getContent().get(0));
        assertEqualsEvent(EVENT_3, page.getContent().get(1));
        assertEqualsEvent(EVENT_2, page.getContent().get(2));
        assertEqualsEvent(EVENT_1, page.getContent().get(3));
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
            null,
            null,
            "",
            null,
            null,
            null,
            true,
            PAGE_1_BIG
        );

        assertNotNull(page);
        assertNotNull(page.getContent());
        assertEquals(1, page.getCurrentPage());
        assertEquals(1, page.getTotalPages());
        assertEquals(TOTAL_EVENTS_NOT_DELETED, page.getContent().size());
        assertEqualsEvent(EVENT_1, page.getContent().get(0));
        assertEqualsEvent(EVENT_2, page.getContent().get(1));
        assertEqualsEvent(EVENT_OLDER, page.getContent().get(2));
        assertEqualsEvent(EVENT_3, page.getContent().get(3));
    }
    @Test
    public void testFindAllWithFiltersNoParamsFilter(){
        Page<Event> page = eventDao.findAllWithFilters(
            null,
            EVENT_TITLE_DEFAULT,
            SortFieldEvent.ATTENDEES,
            SortDirection.DESC,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            true,
            PAGE_1_BIG
        );

        assertNotNull(page);
        assertNotNull(page.getContent());
        assertEquals(1, page.getCurrentPage());
        assertEquals(1, page.getTotalPages());
        assertEquals(1, page.getContent().size());
        assertEqualsEvent(EVENT_1, page.getContent().get(0));
    }
    @Test
    public void testFindAllWithFiltersNoParamsInterests(){
        Page<Event> page = eventDao.findAllWithFilters(
            null,
            EVENT_TITLE_DEFAULT,
            SortFieldEvent.RATING,
            SortDirection.DESC,
            null,
            null,
            null,
            null,
            null,
            INTEREST_1_NAME,
            null,
            null,
            null,
            true,
            PAGE_1_BIG
        );

        assertNotNull(page);
        assertNotNull(page.getContent());
        assertEquals(1, page.getCurrentPage());
        assertEquals(1, page.getTotalPages());
        assertEquals(1, page.getContent().size());
        assertEqualsEvent(EVENT_1, page.getContent().get(0));
    }
    @Test
    public void testFindAllWithFiltersWithStartEndTime(){
        Page<Event> page = eventDao.findAllWithFilters(
            USER_1_ID,
            null,
            SortFieldEvent.ATTENDEES,
            SortDirection.ASC,
            CITY_1_NAME,
            EVENT_DATE_OLDER,
            EVENT_DATE_LATER,
            EVENT_TIME_DEFAULT,
            EVENT_TIME_DEFAULT,
            null,
            null,
            null,
            null,
            true,
            PAGE_1_BIG
        );

        assertNotNull(page);
        assertNotNull(page.getContent());
        assertEquals(1, page.getCurrentPage());
        assertEquals(1, page.getTotalPages());
        assertEquals(2, page.getContent().size());
    }
    @Test
    public void testFindAllWithFiltersWithStartTime(){
        Page<Event> page = eventDao.findAllWithFilters(
            USER_1_ID,
            null,
            SortFieldEvent.ATTENDEES,
            SortDirection.ASC,
            CITY_1_NAME,
            EVENT_DATE_OLDER,
            EVENT_DATE_LATER,
            EVENT_TIME_DEFAULT,
            null,
            null,
            null,
            null,
            null,
            true,
            PAGE_1_BIG
        );

        assertNotNull(page);
        assertNotNull(page.getContent());
        assertEquals(1, page.getCurrentPage());
        assertEquals(1, page.getTotalPages());
        assertEquals(2, page.getContent().size());
    }
    @Test
    public void testFindAllWithFiltersWithEndTime(){
        Page<Event> page = eventDao.findAllWithFilters(
            USER_1_ID,
            null,
            SortFieldEvent.ATTENDEES,
            SortDirection.ASC,
            CITY_1_NAME,
            EVENT_DATE_OLDER,
            EVENT_DATE_LATER,
            null,
            EVENT_TIME_DEFAULT,
            null,
            null,
            null,
            null,
            true,
            PAGE_1_BIG
        );

        assertNotNull(page);
        assertNotNull(page.getContent());
        assertEquals(1, page.getCurrentPage());
        assertEquals(1, page.getTotalPages());
        assertEquals(2, page.getContent().size());
    }
    @Test
    public void testFindAllWithFiltersAttended(){
        Page<Event> page = eventDao.findAllWithFilters(
            USER_1_ID,
            null,
            SortFieldEvent.ATTENDEES,
            SortDirection.ASC,
            CITY_1_NAME,
            EVENT_DATE_OLDER,
            EVENT_DATE_LATER,
            null,
            null,
            null,
            USER_1_ID,
            null,
            null,
            true,
            PAGE_1_BIG
        );

        assertNotNull(page);
        assertNotNull(page.getContent());
        assertEquals(1, page.getCurrentPage());
        assertEquals(1, page.getTotalPages());
        assertEquals(1, page.getContent().size());
    }
    @Test
    public void testFindAllWithFiltersUniversity(){
        Page<Event> page = eventDao.findAllWithFilters(
            USER_1_ID,
            null,
            SortFieldEvent.ATTENDEES,
            SortDirection.ASC,
            CITY_1_NAME,
            null,
            null,
            null,
            null,
            null,
            USER_1_ID,
            UNIVERSITY_1_NAME,
            null,
            true,
            PAGE_1_BIG
        );

        assertNotNull(page);
        assertNotNull(page.getContent());
        assertEquals(1, page.getCurrentPage());
        assertEquals(1, page.getTotalPages());
        assertEquals(1, page.getContent().size());
    }
    @Test
    public void testFindAllWithFiltersUniversityEmpty(){
        Page<Event> page = eventDao.findAllWithFilters(
            USER_1_ID,
            null,
            SortFieldEvent.ATTENDEES,
            SortDirection.ASC,
            CITY_1_NAME,
            null,
            null,
            null,
            null,
            null,
            USER_1_ID,
            "",
            null,
            true,
            PAGE_1_BIG
        );

        assertNotNull(page);
        assertNotNull(page.getContent());
        assertEquals(1, page.getCurrentPage());
        assertEquals(1, page.getTotalPages());
        assertEquals(1, page.getContent().size());
    }
    @Test
    public void testFindAllWithFiltersRating(){
        Page<Event> page = eventDao.findAllWithFilters(
            USER_1_ID,
            null,
            SortFieldEvent.ATTENDEES,
            SortDirection.ASC,
            CITY_1_NAME,
            null,
            null,
            null,
            null,
            null,
            USER_1_ID,
            null,
            2,
            true,
            PAGE_1_BIG
        );

        assertNotNull(page);
        assertNotNull(page.getContent());
        assertEquals(1, page.getCurrentPage());
        assertEquals(1, page.getTotalPages());
        assertEquals(1, page.getContent().size());
    }
    @Test
    public void testFindAllWithFiltersCapacity(){
        Page<Event> page = eventDao.findAllWithFilters(
            USER_1_ID,
            null,
            SortFieldEvent.ATTENDEES,
            SortDirection.ASC,
            CITY_1_NAME,
            null,
            null,
            null,
            null,
            null,
            USER_1_ID,
            null,
            null,
            false,
            PAGE_1_BIG
        );

        assertNotNull(page);
        assertNotNull(page.getContent());
        assertEquals(1, page.getCurrentPage());
        assertEquals(0, page.getTotalPages());
        assertEquals(0, page.getContent().size());
    }
    @Test
    public void testFindAllWithFiltersCapacityNull(){
        Page<Event> page = eventDao.findAllWithFilters(
            USER_1_ID,
            null,
            SortFieldEvent.ATTENDEES,
            SortDirection.ASC,
            CITY_1_NAME,
            null,
            null,
            null,
            null,
            null,
            USER_1_ID,
            null,
            null,
            null,
            PAGE_1_BIG
        );

        assertNotNull(page);
        assertNotNull(page.getContent());
        assertEquals(1, page.getCurrentPage());
        assertEquals(1, page.getTotalPages());
        assertEquals(1, page.getContent().size());
    }
    @Test
    public void testFindAllWithoutFilters(){
        Page<Event> page = eventDao.findAllWithFilters(
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            PAGE_1_BIG
        );

        assertNotNull(page);
        assertNotNull(page.getContent());
        assertEquals(1, page.getCurrentPage());
        assertEquals(1, page.getTotalPages());
        assertEquals(TOTAL_EVENTS_NOT_DELETED, page.getContent().size());
    }
}