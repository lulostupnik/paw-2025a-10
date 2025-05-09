package ar.edu.itba.paw.persistance;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.sql.Date;
import java.time.LocalDate;
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

import ar.edu.itba.paw.persistence.JourneyJdbcDao;

@Transactional
@Rollback
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class JourneyJdbcDaoTest {

    private static final String JOURNEY_TABLE = "journeys";

    private static University UNI_DESTINATION; 
    private static University UNI_ORIGIN; 
    private static University UNI_3;
    private static User USER_1;
    private static User USER_2;
    private static User USER_4;
    private static User USER_3;
    private static User USER_5;
    private static User USER_COMMON_INTERESTS_1;
    private static User USER_COMMON_INTERESTS_2;
    private static User USER_COMMON_INTERESTS_3;
    private static City CITY_DESTINATION;
    private static City CITY_ORIGIN;
    private static Interest INTEREST_1;
    private static Journey JOURNEY_DELETED;
    private static Journey JOURNEY_1;

    @Autowired
    private DataSource ds;

    @Autowired
    private JourneyJdbcDao journeyDao;

    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert insert;

    @Before
    public void setUp(){
        jdbcTemplate = new JdbcTemplate(ds);
        insert = new SimpleJdbcInsert(ds).withTableName(JOURNEY_TABLE).usingGeneratedKeyColumns("id");

        jdbcTemplate.execute("INSERT INTO users(username, email, firstname, lastname, university, career_id, profile_picture_id) VALUES('newUser1', 'newUser1@mail.com', 'user', 'name', (SELECT id FROM universities WHERE abbreviation = 'UBA'), (SELECT id FROM careers LIMIT 1), (SELECT id FROM images LIMIT 1))");

        UNI_DESTINATION = jdbcTemplate.queryForObject(TestUtils.UNIVERSITY_SELECT_BY_ABBR, TestUtils.UNIVERSITY_ROW_MAPPER, TestUtils.UNIVERSITY_2_CODE);
        UNI_ORIGIN = jdbcTemplate.queryForObject(TestUtils.UNIVERSITY_SELECT_BY_ABBR, TestUtils.UNIVERSITY_ROW_MAPPER, TestUtils.UNIVERSITY_1_CODE);
        UNI_3 = jdbcTemplate.queryForObject(TestUtils.UNIVERSITY_SELECT_BY_ABBR, TestUtils.UNIVERSITY_ROW_MAPPER, TestUtils.UNIVERSITY_3_CODE);
        USER_1 = jdbcTemplate.queryForObject(TestUtils.USER_SELECT_BY_EMAIL, TestUtils.USER_ROW_MAPPER, TestUtils.USER_1_MAIL);
        USER_2 = jdbcTemplate.queryForObject(TestUtils.USER_SELECT_BY_EMAIL, TestUtils.USER_ROW_MAPPER, TestUtils.USER_2_MAIL);
        USER_3 = jdbcTemplate.queryForObject(TestUtils.USER_SELECT_BY_EMAIL, TestUtils.USER_ROW_MAPPER, TestUtils.USER_3_MAIL);
        USER_4 = jdbcTemplate.queryForObject(TestUtils.USER_SELECT_BY_EMAIL, TestUtils.USER_ROW_MAPPER, TestUtils.USER_4_MAIL);
        USER_5 = jdbcTemplate.queryForObject(TestUtils.USER_SELECT_BY_EMAIL, TestUtils.USER_ROW_MAPPER, TestUtils.USER_NEW1_MAIL);
        USER_COMMON_INTERESTS_1 = jdbcTemplate.queryForObject(TestUtils.USER_SELECT_BY_EMAIL, TestUtils.USER_ROW_MAPPER, TestUtils.USER_COMMON_INTERESTS_1_MAIL);
        USER_COMMON_INTERESTS_2 = jdbcTemplate.queryForObject(TestUtils.USER_SELECT_BY_EMAIL, TestUtils.USER_ROW_MAPPER, TestUtils.USER_COMMON_INTERESTS_2_MAIL);
        USER_COMMON_INTERESTS_3 = jdbcTemplate.queryForObject(TestUtils.USER_SELECT_BY_EMAIL, TestUtils.USER_ROW_MAPPER, TestUtils.USER_COMMON_INTERESTS_3_MAIL);
        CITY_DESTINATION = jdbcTemplate.queryForObject(TestUtils.CITY_SELECT_BY_NAME, TestUtils.CITY_ROW_MAPPER, TestUtils.CITY_2_NAME);
        CITY_ORIGIN = jdbcTemplate.queryForObject(TestUtils.CITY_SELECT_BY_NAME, TestUtils.CITY_ROW_MAPPER, TestUtils.CITY_1_NAME);
        INTEREST_1 = jdbcTemplate.queryForObject(TestUtils.INTEREST_SELECT_BY_NAME, TestUtils.INTEREST_ROW_MAPPER, TestUtils.INTEREST_1_NAME);
        JOURNEY_DELETED = jdbcTemplate.queryForObject(TestUtils.JOURNEY_SELECT_BY_USERMAIL, TestUtils.JOURNEY_ROW_MAPPER, TestUtils.USER_4_MAIL);
        JOURNEY_1 = jdbcTemplate.queryForObject(TestUtils.JOURNEY_SELECT_BY_USERMAIL, TestUtils.JOURNEY_ROW_MAPPER, TestUtils.USER_1_MAIL);
    }

    private void assertEqualsJourney(Journey journey){
        assertEqualsJourney(journey, Map.of());
    }

    private void assertEqualsJourney(Journey journey, Map<String, Object> overrideParams){
        assertNotNull(journey);
        assertEquals(((User)overrideParams.getOrDefault("user", USER_1)).getId(), journey.getUser().getId());
        assertEquals(((University)overrideParams.getOrDefault("destination", UNI_DESTINATION)).getId(), journey.getDestinationUniversity().getId());
        assertEquals(overrideParams.getOrDefault("startDate", TestUtils.JOURNEY_START_DATE), journey.getStartDate());
        assertEquals(overrideParams.getOrDefault("endDate", TestUtils.JOURNEY_END_DATE), journey.getEndDate());
        assertEquals(overrideParams.getOrDefault("description", TestUtils.JOURNEY_DESCRIPTION), journey.getDescription());
        assertEquals(overrideParams.getOrDefault("id", JOURNEY_1.getId()), journey.getId());
    }

    private void assertEqualsMaybeJourney(Optional<Journey> maybeJourney){
        assertNotNull(maybeJourney);
        assertTrue(maybeJourney.isPresent());
        final Journey journey = maybeJourney.get();
        assertEqualsJourney(journey); 
    }

    private long insertJourney(){
        return insertJourney(Map.of());
    }
    private long insertJourney(Map<String, Object> overrideParams){
        HashMap<String, Object> params = new HashMap<>();
        params.put("user_id", ((User)overrideParams.getOrDefault("user", USER_2)).getId());
        params.put("destination_university_id", ((University)overrideParams.getOrDefault("destination", UNI_DESTINATION)).getId());
        params.put("start_date", Date.valueOf((LocalDate)overrideParams.getOrDefault("startDate", TestUtils.JOURNEY_START_DATE)));
        params.put("end_date", Date.valueOf((LocalDate)overrideParams.getOrDefault("endDate", TestUtils.JOURNEY_END_DATE)));
        params.put("description", overrideParams.getOrDefault("description", TestUtils.JOURNEY_DESCRIPTION));
        params.put("deleted", overrideParams.getOrDefault("deleted", false));
        params.put("deleted_message", overrideParams.getOrDefault("deletedMessage", null));
        return insert.executeAndReturnKey(params).longValue();
    }

    @Test
    public void testCreate(){
        JdbcTestUtils.deleteFromTables(jdbcTemplate, JOURNEY_TABLE);
        Journey journey = journeyDao.create(
            USER_1,
            UNI_DESTINATION,
            TestUtils.JOURNEY_START_DATE, TestUtils.JOURNEY_END_DATE, TestUtils.JOURNEY_DESCRIPTION
        );

        assertEqualsJourney(journey, Map.of("id", jdbcTemplate.queryForObject("SELECT id FROM journeys WHERE user_id = ?", Long.class, USER_1.getId())));
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, JOURNEY_TABLE));
    }
    @Test(expected = DataAccessException.class)
    public void testCreateInvalidUser(){
        Journey journey = journeyDao.create(
            new User(12341234, null, null, null, null, null, null, 0, null, false),
            UNI_DESTINATION,
            TestUtils.JOURNEY_START_DATE, TestUtils.JOURNEY_END_DATE, TestUtils.JOURNEY_DESCRIPTION
        );

        assertEqualsJourney(journey);
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, JOURNEY_TABLE));
    }
    @Test(expected = DataAccessException.class)
    public void testCreateInvalidUni(){
        Journey journey = journeyDao.create(
            USER_1,
            new University(12431234, null,null, null),
            TestUtils.JOURNEY_START_DATE, TestUtils.JOURNEY_END_DATE, TestUtils.JOURNEY_DESCRIPTION
        );

        assertEqualsJourney(journey);
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, JOURNEY_TABLE));
    }
    @Test
    public void testCreateDuplicated(){
        journeyDao.create(
            USER_1,
            UNI_DESTINATION,
            TestUtils.JOURNEY_START_DATE, TestUtils.JOURNEY_END_DATE, TestUtils.JOURNEY_DESCRIPTION
        );

        assertEquals(TestUtils.TOTAL_JOURNEYS, JdbcTestUtils.countRowsInTable(jdbcTemplate, JOURNEY_TABLE));
        Journey journey = jdbcTemplate.queryForObject(TestUtils.JOURNEY_SELECT_BY_ID, TestUtils.JOURNEY_ROW_MAPPER, JOURNEY_1.getId());
        assertEqualsJourney(journey);
    }

    @Test
    public void testFindById(){
        insertJourney(Map.of("user", USER_2));

        Optional<Journey> maybeJourney = journeyDao.findById(JOURNEY_1.getId());

        assertEqualsMaybeJourney(maybeJourney);
    }
    @Test
    public void testFindByIdInvalidId(){
        Optional<Journey> maybeJourney = journeyDao.findById(12341234);

        assertNotNull(maybeJourney);
        assertFalse(maybeJourney.isPresent());
    }    
    @Test
    public void testFindByIdDeleted(){
        Optional<Journey> maybeJourney = journeyDao.findById(JOURNEY_DELETED.getId());

        assertNotNull(maybeJourney);
        assertFalse(maybeJourney.isPresent());
    }
    @Test
    public void testFindByIdNoJourneys(){
        Optional<Journey> maybeJourney = journeyDao.findById(12341234);

        assertNotNull(maybeJourney);
        assertFalse(maybeJourney.isPresent());
    }

    @Test
    public void testFindOverlappingJourneyWithOverlapLeft(){
        Optional<Journey> maybeJourney = journeyDao.findOverlappingJourney(USER_1.getId(), LocalDate.now(), TestUtils.JOURNEY_START_DATE.plusDays(7));

        assertEqualsMaybeJourney(maybeJourney);
    }
    @Test
    public void testFindOverlappingJourneyWithOverlapRight(){
        Optional<Journey> maybeJourney = journeyDao.findOverlappingJourney(USER_1.getId(), TestUtils.JOURNEY_END_DATE.plusDays(-7), TestUtils.JOURNEY_END_DATE.plusDays(7));

        assertEqualsMaybeJourney(maybeJourney);
    }
    @Test
    public void testFindOverlappingJourneyWithOverlapContained(){
        Optional<Journey> maybeJourney = journeyDao.findOverlappingJourney(USER_1.getId(), TestUtils.JOURNEY_START_DATE.plusDays(7), TestUtils.JOURNEY_END_DATE.plusDays(-7));

        assertEqualsMaybeJourney(maybeJourney);
    }
    @Test
    public void testFindOverlappingJourneyWithOverlapContainer(){
        Optional<Journey> maybeJourney = journeyDao.findOverlappingJourney(USER_1.getId(), TestUtils.JOURNEY_START_DATE.plusDays(-7), TestUtils.JOURNEY_END_DATE.plusDays(7));

        assertEqualsMaybeJourney(maybeJourney);
    }
    @Test
    public void testFindOverlappingJourneyWithoutOverlap(){
        Optional<Journey> maybeJourney = journeyDao.findOverlappingJourney(USER_1.getId(), LocalDate.now(), TestUtils.JOURNEY_START_DATE.plusDays(-7));

        assertNotNull(maybeJourney);
        assertFalse(maybeJourney.isPresent());
    }

    @Test
    public void testFindByUserId(){
        Optional<Journey> maybeJourney = journeyDao.findByUserId(USER_1.getId());

        assertNotNull(maybeJourney);
        assertTrue(maybeJourney.isPresent());
        assertEqualsJourney(maybeJourney.get());      
    }
    @Test
    public void testFindByUserIdWrongId(){
        Optional<Journey> maybeJourney = journeyDao.findByUserId(12341234);

        assertNotNull(maybeJourney);
        assertFalse(maybeJourney.isPresent());
    }
    @Test
    public void testFindByUserIdDeleted(){
        Optional<Journey> maybeJourney = journeyDao.findByUserId(USER_4.getId());

        assertNotNull(maybeJourney);
        assertFalse(maybeJourney.isPresent());
    }

    @Test
    public void testFindByUserEmail(){
        Optional<Journey> maybeJourney = journeyDao.findByUserEmail(TestUtils.USER_1_MAIL);

        assertNotNull(maybeJourney);
        assertTrue(maybeJourney.isPresent());
        assertEqualsJourney(maybeJourney.get());
    }
    @Test
    public void testFindByUserEmailDeleted(){
        Optional<Journey> maybeJourney = journeyDao.findByUserEmail(TestUtils.USER_3_MAIL);

        assertNotNull(maybeJourney);
        assertFalse(maybeJourney.isPresent());
    }

    @Test
    public void testDelete(){
        journeyDao.delete(JOURNEY_1.getId());

        assertEquals(TestUtils.TOTAL_JOURNEYS, JdbcTestUtils.countRowsInTable(jdbcTemplate, JOURNEY_TABLE));
        assertTrue(jdbcTemplate.queryForObject("SELECT deleted FROM journeys WHERE id = ?", Boolean.class, JOURNEY_1.getId()));
    }
    @Test
    public void testDeleteWrongId(){
        journeyDao.delete(12341243);

        assertEquals(TestUtils.TOTAL_JOURNEYS, JdbcTestUtils.countRowsInTable(jdbcTemplate, JOURNEY_TABLE));
        assertFalse(jdbcTemplate.queryForObject("SELECT deleted FROM journeys WHERE id = ?", Boolean.class, JOURNEY_1.getId()));
    }
    @Test
    public void testDeleteDeleted(){
        journeyDao.delete(JOURNEY_DELETED.getId());

        assertEquals(TestUtils.TOTAL_JOURNEYS, JdbcTestUtils.countRowsInTable(jdbcTemplate, JOURNEY_TABLE));
        assertTrue(jdbcTemplate.queryForObject("SELECT deleted FROM journeys WHERE id = ?", Boolean.class, JOURNEY_DELETED.getId()));
        assertFalse(jdbcTemplate.queryForObject("SELECT deleted FROM journeys WHERE id = ?", Boolean.class, JOURNEY_1.getId()));
    }

    @Test
    public void testDeletionMessage(){
        journeyDao.deletionMessage(JOURNEY_1.getId(), "WRONG");

        assertEquals(TestUtils.TOTAL_JOURNEYS, JdbcTestUtils.countRowsInTable(jdbcTemplate, JOURNEY_TABLE));
        assertFalse(jdbcTemplate.queryForObject("SELECT deleted FROM journeys WHERE id = ?", Boolean.class, JOURNEY_1.getId()));
        assertEquals("WRONG", jdbcTemplate.queryForObject("SELECT deleted_message FROM journeys WHERE id = ?", String.class, JOURNEY_1.getId()));
    }
    @Test
    public void testDeletionMessageWrongId(){
        journeyDao.deletionMessage(12341243, "WRONG");

        assertEquals(TestUtils.TOTAL_JOURNEYS, JdbcTestUtils.countRowsInTable(jdbcTemplate, JOURNEY_TABLE));
        assertFalse(jdbcTemplate.queryForObject("SELECT deleted FROM journeys WHERE id = ?", Boolean.class, JOURNEY_1.getId()));
    }



    @Test
    public void testUpdateDates(){
        journeyDao.updateDates(
            JOURNEY_1.getId(), 
            TestUtils.JOURNEY_START_DATE.plusDays(10), 
            TestUtils.JOURNEY_END_DATE.plusDays(10)
        );

        Journey journey = jdbcTemplate.queryForObject(
            TestUtils.JOURNEY_SELECT_BY_ID, 
            TestUtils.JOURNEY_ROW_MAPPER, 
            JOURNEY_1.getId());
        assertEqualsJourney(journey, Map.of("startDate", TestUtils.JOURNEY_START_DATE.plusDays(10), "endDate", TestUtils.JOURNEY_END_DATE.plusDays(10)));
    }
    @Test(expected=NullPointerException.class)
    public void testUpdateDatesMissingStartDate(){
        journeyDao.updateDates(JOURNEY_1.getId(), null, TestUtils.JOURNEY_END_DATE);
    }
    @Test(expected=NullPointerException.class)
    public void testUpdateDatesMissingEndDate(){
        journeyDao.updateDates(JOURNEY_1.getId(), TestUtils.JOURNEY_START_DATE, null);
    }
    @Test
    public void testUpdateDatesWrongId(){
        journeyDao.updateDates(1231234, TestUtils.JOURNEY_START_DATE, TestUtils.JOURNEY_END_DATE);

        Journey journey = jdbcTemplate.queryForObject(TestUtils.JOURNEY_SELECT_BY_ID, TestUtils.JOURNEY_ROW_MAPPER, JOURNEY_1.getId());
        assertEqualsJourney(journey);
    }

    @Test
    public void testUpdateDescription(){
        journeyDao.updateDescription(JOURNEY_1.getId(), "JOURNEY_DESCRIPTION");

        Journey journey = jdbcTemplate.queryForObject(TestUtils.JOURNEY_SELECT_BY_ID, TestUtils.JOURNEY_ROW_MAPPER, JOURNEY_1.getId());
        assertEqualsJourney(journey, Map.of("description", "JOURNEY_DESCRIPTION")); 
    }
    @Test
    public void testUpdateDescriptionWrongId(){
        journeyDao.updateDescription(12341234, "NEW DESCRIPTION");

        Journey journey = jdbcTemplate.queryForObject(TestUtils.JOURNEY_SELECT_BY_ID, TestUtils.JOURNEY_ROW_MAPPER, JOURNEY_1.getId());
        assertEqualsJourney(journey); 
    }
    
    @Test
    public void testUpdateDestinationUniversity(){
        journeyDao.updateDestinationUniversity(JOURNEY_1.getId(), UNI_3.getId());

        Journey journey = jdbcTemplate.queryForObject(TestUtils.JOURNEY_SELECT_BY_ID, TestUtils.JOURNEY_ROW_MAPPER, JOURNEY_1.getId());
        assertEqualsJourney(journey, Map.of("destination", UNI_3)); 
    }
    @Test(expected = DataAccessException.class)
    public void testUpdateDestinationUniversityInvalidId(){
        long id = insertJourney();

        journeyDao.updateDestinationUniversity(id, 1234123);
    }
    @Test
    public void testUpdateDestinationUniversityInvalidJourney(){
        journeyDao.updateDestinationUniversity(12341234, UNI_3.getId());

        Journey journey = jdbcTemplate.queryForObject(TestUtils.JOURNEY_SELECT_BY_ID, TestUtils.JOURNEY_ROW_MAPPER, JOURNEY_1.getId());
        assertEqualsJourney(journey); 
    }

    @Test
    public void testListAllPaged(){
        long id2 = insertJourney(Map.of("user", USER_2));

        Page<Journey> page1 = journeyDao.listAll(new PageParams(1, 1));
        Page<Journey> page2 = journeyDao.listAll(new PageParams(2, 1));

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
        assertEquals(JOURNEY_1.getId(), page1.getContent().getFirst().getId());
        assertEquals(id2, page2.getContent().getFirst().getId());
    }
    
    @Test
    public void testListAllPagedNoJourneys(){
        JdbcTestUtils.deleteFromTables(jdbcTemplate, TestUtils.JOURNEY_TABLE);

        Page<Journey> page1 = journeyDao.listAll(TestUtils.PAGE_1_DEFAULT);

        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(0, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(0, page1.getContent().size());
    }

    @Test
    public void testGetOthersJourneysPaged(){
        long id = insertJourney(Map.of("user", USER_2));

        Page<Journey> page1 = journeyDao.getOthersJourneys(USER_1.getId(), new PageParams(1, 1));

        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(1, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(1, page1.getContent().size());
        assertEqualsJourney(page1.getContent().getFirst(), Map.of("id", id, "user", USER_2));
    }
    @Test
    public void testGetOthersJourneysPagedNoJourneys(){
        Page<Journey> page1 = journeyDao.getOthersJourneys(USER_1.getId(), new PageParams(1,1));

        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(0, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(0, page1.getContent().size());
    }

    @Test
    public void testFindByOriginCityPaged(){
        insertJourney(Map.of("user", USER_2));

        Page<Journey> page1 = journeyDao.findByOriginCity(CITY_ORIGIN.getId(), TestUtils.PAGE_1_DEFAULT);

        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(1, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(2, page1.getContent().size());
    }
    @Test
    public void testFindByOriginCityPagedWrongCity(){
        insertJourney(Map.of("user", USER_2));

        Page<Journey> page1 = journeyDao.findByOriginCity(1241234, new PageParams(1,2));

        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(0, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(0, page1.getContent().size());
    }

    @Test
    public void testUpdateData(){
        journeyDao.updateData(
            JOURNEY_1.getId(), 
            UNI_3, 
            TestUtils.JOURNEY_START_DATE.plusDays(10), 
            TestUtils.JOURNEY_END_DATE.plusDays(10), 
            "COOL!"
        );

        Journey journey = jdbcTemplate.queryForObject(
            TestUtils.JOURNEY_SELECT_BY_ID, 
            TestUtils.JOURNEY_ROW_MAPPER, 
            JOURNEY_1.getId()
        );
        assertEqualsJourney(journey, Map.of(
            "destination", UNI_3, 
            "startDate", TestUtils.JOURNEY_START_DATE.plusDays(10), 
            "endDate", TestUtils.JOURNEY_END_DATE.plusDays(10), 
            "description", "COOL!")
        );
    }
    @Test
    public void testUpdateDatanotFound(){
        journeyDao.updateData(
            12341234, 
            UNI_3, 
            TestUtils.JOURNEY_START_DATE.plusDays(10),
            TestUtils.JOURNEY_END_DATE.plusDays(10), 
            "New description"
        );

        Journey journey = jdbcTemplate.queryForObject(
            TestUtils.JOURNEY_SELECT_BY_ID, 
            TestUtils.JOURNEY_ROW_MAPPER, 
            JOURNEY_1.getId()
        );
        assertEqualsJourney(journey);
    }
    @Test
    public void testUpdateDataDeleted(){
        journeyDao.updateData(
            JOURNEY_DELETED.getId(), 
            UNI_3, 
            TestUtils.JOURNEY_START_DATE.plusDays(10), 
            TestUtils.JOURNEY_END_DATE.plusDays(10), 
            "New description"
        );

        assertFalse(jdbcTemplate.queryForObject(
            "SELECT deleted FROM journeys WHERE id = ?", 
            Boolean.class, 
            JOURNEY_DELETED.getId())
        );
        Journey journey = jdbcTemplate.queryForObject(
            TestUtils.JOURNEY_SELECT_BY_ID, 
            TestUtils.JOURNEY_ROW_MAPPER, 
            JOURNEY_DELETED.getId()
        );
        assertEqualsJourney(journey, Map.of(
            "user", USER_4, 
            "id", JOURNEY_DELETED.getId(),
            "destination", UNI_3, 
            "startDate", TestUtils.JOURNEY_START_DATE.plusDays(10), 
            "endDate", TestUtils.JOURNEY_END_DATE.plusDays(10), 
            "description", "New description")
        );
    }

//    @Test
//    public void testSearchJourneys(){
//        insertJourney();
//        insertJourney(Map.of("userId", USER2_ID));
//        insertJourney(Map.of("userId", USER_ANOTHER_ID));
//
//        Page<Journey> page = journeyDao.searchJourneys(USERNAME_1.substring(0, 6), 1, 5);
//
//        assertNotNull(page);
//        assertEquals(1,page.getCurrentPage());
//        assertEquals(1,page.getTotalPages());
//        assertNotNull(page.getContent());
//        assertEquals(2, page.getContent().size());
//    }
//    @Test
//    public void testSearchJourneysNoJourneys(){
//        Page<Journey> page = journeyDao.searchJourneys(USERNAME_1.substring(0, 6), 1, 5);
//
//        assertNotNull(page);
//        assertEquals(1,page.getCurrentPage());
//        assertEquals(0,page.getTotalPages());
//        assertNotNull(page.getContent());
//        assertEquals(0, page.getContent().size());
//    }
//    @Test
//    public void testSearchJourneysDeleted(){
//        Page<Journey> page = journeyDao.searchJourneys(DELETED_USER_NAME, 1, 5);
//
//        assertNotNull(page);
//        assertEquals(1,page.getCurrentPage());
//        assertEquals(0,page.getTotalPages());
//        assertNotNull(page.getContent());
//        assertEquals(0, page.getContent().size());
//    }
//
//    @Test
//    public void testFindByFiltersDestination(){
//        long id1 = insertJourney();
//        long id2 = insertJourney(Map.of("userId", USER2_ID));
//        insertJourney(Map.of("userId", USER3_ID, "destination", UNI_ORIGIN));
//
//        List<Journey> journeys = journeyDao.findByFilters(Long.toString(DESTINATION_CITY_ID), null, null, null);
//
//        assertNotNull(journeys);
//        assertEquals(2, journeys.size());
//        //TODO if-else
//        for (Journey j : journeys){
//            assertEquals(TestUtils.JOURNEY_DESCRIPTION, j.getDescription());
//            assertEquals(DESTINATION_UNI_ID, j.getDestinationUniversity().getId());
//            assertEquals(TestUtils.JOURNEY_START_DATE, j.getStartDate());
//            assertEquals(TestUtils.JOURNEY_END_DATE, j.getEndDate());
//            if (j.getId() == id1) {
//                assertEquals(USER1_ID, j.getUser().getId());
//            } else {
//                assertEquals(id2, j.getId());
//                assertEquals(USER2_ID, j.getUser().getId());
//            }
//        }
//    }

    @Test
    public void testFindByFiltersDestinationPaged(){
        long id = insertJourney(Map.of("user", USER_2));
        insertJourney(Map.of("user", USER_3, "destination", UNI_ORIGIN));

        Page<Journey> journeys = journeyDao.findByFilters(USER_1.getId(), CITY_DESTINATION.getId(), null, null, null, TestUtils.PAGE_1_DEFAULT);

        assertNotNull(journeys);
        assertEquals(1, journeys.getContent().size());
        assertEqualsJourney(journeys.getContent().getFirst(), Map.of("user", USER_2, "id", id));

    }
    @Test
    public void testFindByFiltersStartDatePaged(){
        long id = insertJourney(Map.of("user", USER_2));
        insertJourney(Map.of("user", USER_3, "destination", UNI_ORIGIN, "endDate", TestUtils.JOURNEY_END_DATE.plusDays(-15)));

        Page<Journey> journeys = journeyDao.findByFilters(USER_1.getId(), null, TestUtils.JOURNEY_START_DATE.plusDays(20), null, null, TestUtils.PAGE_1_DEFAULT);

        assertNotNull(journeys);
        assertEquals(1, journeys.getContent().size());
        assertEqualsJourney(journeys.getContent().getFirst(), Map.of("user", USER_2, "id", id));

    }
    @Test
    public void testFindByFiltersEndDatePaged(){
        long id = insertJourney(Map.of("user", USER_2));
        insertJourney(Map.of("user", USER_3, "destination", UNI_ORIGIN, "startDate", TestUtils.JOURNEY_START_DATE.plusDays(15)));

        Page<Journey> journeys = journeyDao.findByFilters(USER_1.getId(), null, null, TestUtils.JOURNEY_END_DATE.plusDays(-20), null, TestUtils.PAGE_1_DEFAULT);

        assertNotNull(journeys);
        assertEquals(1, journeys.getContent().size());
        assertEqualsJourney(journeys.getContent().getFirst(), Map.of("user", USER_2, "id", id));

    }
    @Test
    public void testFindByFiltersInterestsPaged(){
        insertJourney(Map.of("user", USER_2));
        insertJourney(Map.of("user", USER_3, "destination", UNI_ORIGIN, "endDate", TestUtils.JOURNEY_END_DATE.plusDays(-7)));

        Page<Journey> journeys = journeyDao.findByFilters(USER_1.getId(), null, null, null, INTEREST_1.getId(), TestUtils.PAGE_1_DEFAULT);

        assertNotNull(journeys);
        assertEquals(0, journeys.getContent().size());
    }
    @Test
    public void testFindByFiltersNoConditionsPaged(){
        insertJourney(Map.of("user", USER_2));
        insertJourney(Map.of("user", USER_3, "destination", UNI_ORIGIN, "endDate", TestUtils.JOURNEY_END_DATE.plusDays(-7)));

        Page<Journey> journeys = journeyDao.findByFilters(null, null, null, null, null, TestUtils.PAGE_1_DEFAULT);

        assertNotNull(journeys);
        assertEquals(2, journeys.getContent().size());
    }
    @Test
    public void testFindByFiltersInterestsAllConditionsPaged(){
        insertJourney(Map.of("user", USER_2, "destination", UNI_3, "startDate", TestUtils.JOURNEY_START_DATE.plusDays(7)));
        insertJourney(Map.of("user", USER_3, "destination", UNI_3, "endDate", TestUtils.JOURNEY_END_DATE.plusDays(-7)));

        Page<Journey> journeys = journeyDao.findByFilters(USER_1.getId(), CITY_DESTINATION.getId(), TestUtils.JOURNEY_START_DATE, TestUtils.JOURNEY_END_DATE, INTEREST_1.getId(), TestUtils.PAGE_1_DEFAULT);

        assertNotNull(journeys);
        assertEquals(0, journeys.getContent().size());
    }
    @Test
    public void testFindByFiltersInterestsAllNoInterestsPaged(){
        insertJourney(Map.of("user", USER_2, "destination", UNI_3, "startDate", TestUtils.JOURNEY_START_DATE.plusDays(15)));
        insertJourney(Map.of("user", USER_3, "destination", UNI_ORIGIN, "endDate", TestUtils.JOURNEY_END_DATE.plusDays(-15)));

        Page<Journey> journeys = journeyDao.findByFilters(USER_1.getId(), CITY_DESTINATION.getId(), TestUtils.JOURNEY_START_DATE.plusDays(20), TestUtils.JOURNEY_END_DATE.plusDays(-20), null, TestUtils.PAGE_1_DEFAULT);

        assertNotNull(journeys);
        assertEquals(0, journeys.getContent().size());
    }

    @Test
    public void testRecommendedJourneysBasic(){
        //should have internal score of 95 (30 match city, 50 match uni, 15 overlap)
        long id1 = insertJourney(Map.of("user", USER_2));
        //should have internal score of 80 (30 match city, 50 match uni)
        long id2 = insertJourney(Map.of("user", USER_3, "startDate", TestUtils.JOURNEY_END_DATE.plusDays(2), "endDate", TestUtils.JOURNEY_END_DATE.plusDays(40)));

        Page<Journey> page1 = journeyDao.getRecommendedJourneys(TestUtils.USER_1_MAIL, TestUtils.PAGE_1_BIG);

        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(1, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(2, page1.getContent().size());
        assertEquals(id1, page1.getContent().get(0).getId());
        assertEquals(id2, page1.getContent().get(1).getId());
    }
    @Test
    public void testRecommendedJourneysNoJourneys(){
        JdbcTestUtils.deleteFromTables(jdbcTemplate, TestUtils.JOURNEY_TABLE);
        Page<Journey> page1 = journeyDao.getRecommendedJourneys(TestUtils.USER_1_MAIL, TestUtils.PAGE_1_BIG);

        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(0, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(0, page1.getContent().size());
    }
    @Test
    public void testRecommendedJourneysGoingToMyCity(){
        //should have internal score of 80 (30 match origin city while there, 50 match origin uni while there)
        long id1 = insertJourney(Map.of("user", USER_2, "destination", UNI_ORIGIN, "startDate", TestUtils.JOURNEY_END_DATE.plusDays(-5), "endDate", TestUtils.JOURNEY_END_DATE.plusDays(20)));
        //should have an internal score of 15 (date overlap only)
        long id2 = insertJourney(Map.of("user", USER_3, "destination", UNI_ORIGIN, "startDate", TestUtils.JOURNEY_END_DATE.plusDays(-10), "endDate", TestUtils.JOURNEY_END_DATE.plusDays(-2)));

        Page<Journey> page1 = journeyDao.getRecommendedJourneys(TestUtils.USER_1_MAIL, TestUtils.PAGE_1_BIG);

        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(1, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(2, page1.getContent().size());
        assertEquals(id1, page1.getContent().get(0).getId());
        assertEquals(id2, page1.getContent().get(1).getId());
    }
    @Test
    public void testRecommendedJourneysWithInterests(){
        //should have internal score of 107 (50 + 30 match dest uni, 15 overlap, 12 interest match)
        long id1 = insertJourney(Map.of("user", USER_COMMON_INTERESTS_1));
        //should have internal score of 113 (50 + 30 match dest uni, 15 overlap, 18 interest match)
        long id2 = insertJourney(Map.of("user", USER_COMMON_INTERESTS_2));
        //should have internal score of 116 (50 + 30 match dest uni, 15 overlap, 21 interest match)
        long id3 = insertJourney(Map.of("user", USER_COMMON_INTERESTS_3));
        //should have internal score of 95 (50 + 30 match dest uni, 15 overlap)
        long id4 = insertJourney(Map.of("user", USER_2));

        Page<Journey> page1 = journeyDao.getRecommendedJourneys(TestUtils.USER_1_MAIL, TestUtils.PAGE_1_BIG);

        //TODO for-loop
        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(1, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(4, page1.getContent().size());
        assertEquals(id3, page1.getContent().get(0).getId());
        assertEquals(id2, page1.getContent().get(1).getId());
        assertEquals(id1, page1.getContent().get(2).getId());
        assertEquals(id4, page1.getContent().get(3).getId());
    }
    @Test
    public void testRecommendedJourneysWithInterestsComplex(){
        //should have internal score of 107 (50 + 30 match dest uni, 15 overlap, 12 interest match)
        long id1 = insertJourney(Map.of("user", USER_COMMON_INTERESTS_1));
        //should have internal score of 98 (50 + 30 match origin uni, 18 interest match)
        long id2 = insertJourney(Map.of("user", USER_COMMON_INTERESTS_2, "destination", UNI_ORIGIN, "startDate", TestUtils.JOURNEY_END_DATE.plusDays(2), "endDate", TestUtils.JOURNEY_END_DATE.plusDays(30)));
        //should have internal score of 36 (15 overlap, 21 interest match)
        long id3 = insertJourney(Map.of("user", USER_COMMON_INTERESTS_3, "destination", UNI_ORIGIN, "startDate", TestUtils.JOURNEY_END_DATE.plusDays(-7), "endDate", TestUtils.JOURNEY_END_DATE.plusDays(-3)));
        //should have internal score of 95 (50 + 30 match dest uni, 15 overlap)
        long id4 = insertJourney(Map.of("user", USER_2));
        //should have internal score of 80 (50 + 30 match dest uni)
        long id5 = insertJourney(Map.of("user", USER_3, "startDate", TestUtils.JOURNEY_END_DATE.plusDays(2), "endDate", TestUtils.JOURNEY_END_DATE.plusDays(20)));
        //should have internal score of 45 (30 city match, 15 overlap)
        long id6 = insertJourney(Map.of("user", USER_5, "destination", UNI_3));

        Page<Journey> page1 = journeyDao.getRecommendedJourneys(TestUtils.USER_1_MAIL, TestUtils.PAGE_1_BIG);

        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(1, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(6, page1.getContent().size());

        //TODO for-loop
        assertEquals(id1, page1.getContent().get(0).getId());
        assertEquals(id2, page1.getContent().get(1).getId());
        assertEquals(id4, page1.getContent().get(2).getId());
        assertEquals(id5, page1.getContent().get(3).getId());   
        assertEquals(id6, page1.getContent().get(4).getId());
        assertEquals(id3, page1.getContent().get(5).getId());
    }
}
