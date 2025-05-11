package ar.edu.itba.paw.persistance;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
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
    private static Journey JOURNEY_2;

    @Autowired
    private DataSource ds;

    @Autowired
    private JourneyJdbcDao journeyDao;

    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert insert;

    @Before
    public void setUp(){
        jdbcTemplate = new JdbcTemplate(ds);
        insert = new SimpleJdbcInsert(ds).withTableName(TestUtils.JOURNEY_TABLE).usingGeneratedKeyColumns("id");

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
        JOURNEY_2 = jdbcTemplate.queryForObject(TestUtils.JOURNEY_SELECT_BY_USERMAIL, TestUtils.JOURNEY_ROW_MAPPER, TestUtils.USER_2_MAIL);
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
        TestUtils.assertEqualsJourney(JOURNEY_1, journey); 
    }

    private Journey insertJourney(Map<String, Object> overrideParams){
        HashMap<String, Object> params = new HashMap<>();
        params.put("user_id", ((User)overrideParams.getOrDefault("user", USER_2)).getId());
        params.put("destination_university_id", ((University)overrideParams.getOrDefault("destination", UNI_DESTINATION)).getId());
        params.put("start_date", Date.valueOf((LocalDate)overrideParams.getOrDefault("startDate", TestUtils.JOURNEY_START_DATE)));
        params.put("end_date", Date.valueOf((LocalDate)overrideParams.getOrDefault("endDate", TestUtils.JOURNEY_END_DATE)));
        params.put("description", overrideParams.getOrDefault("description", TestUtils.JOURNEY_DESCRIPTION));
        params.put("deleted", overrideParams.getOrDefault("deleted", false));
        params.put("deleted_message", overrideParams.getOrDefault("deletedMessage", null));
        long id = insert.executeAndReturnKey(params).longValue();
        return jdbcTemplate.queryForObject(TestUtils.JOURNEY_SELECT_BY_ID, TestUtils.JOURNEY_ROW_MAPPER, id);
    }

    @Test
    public void testCreate(){
        TestUtils.deleteJourneys(jdbcTemplate);
        Journey journey = journeyDao.create(
            USER_1,
            UNI_DESTINATION,
            TestUtils.JOURNEY_START_DATE, TestUtils.JOURNEY_END_DATE, TestUtils.JOURNEY_DESCRIPTION
        );

        assertEqualsJourney(journey, Map.of("id", jdbcTemplate.queryForObject(TestUtils.JOURNEY_GET_ID_BY_USER_ID, Long.class, USER_1.getId())));
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.JOURNEY_TABLE));
    }
    @Test(expected = DataAccessException.class)
    public void testCreateInvalidUser(){
        journeyDao.create(
            new User(12341234, null, null, null, null, null, null, 0, null, false),
            UNI_DESTINATION,
            TestUtils.JOURNEY_START_DATE, TestUtils.JOURNEY_END_DATE, TestUtils.JOURNEY_DESCRIPTION
        );
    }
    @Test(expected = DataAccessException.class)
    public void testCreateInvalidUni(){
        journeyDao.create(
            USER_1,
            new University(12431234, null,null, null),
            TestUtils.JOURNEY_START_DATE, TestUtils.JOURNEY_END_DATE, TestUtils.JOURNEY_DESCRIPTION
        );
    }
    @Test
    public void testCreateDuplicated(){
        journeyDao.create(
            USER_1,
            UNI_DESTINATION,
            TestUtils.JOURNEY_START_DATE, TestUtils.JOURNEY_END_DATE, TestUtils.JOURNEY_DESCRIPTION
        );

        assertEquals(TestUtils.TOTAL_JOURNEYS, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.JOURNEY_TABLE));
        Journey journey = jdbcTemplate.queryForObject(TestUtils.JOURNEY_SELECT_BY_ID, TestUtils.JOURNEY_ROW_MAPPER, JOURNEY_1.getId());
        assertEqualsJourney(journey);
    }

    @Test
    public void testFindById(){
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
    public void testFindOverlappingWithOverlapLeft(){
        Optional<Journey> maybeJourney = journeyDao.findOverlapping(USER_1.getId(), LocalDate.now(), TestUtils.JOURNEY_START_DATE.plusDays(7));

        assertEqualsMaybeJourney(maybeJourney);
    }
    @Test
    public void testFindOverlappingWithOverlapRight(){
        Optional<Journey> maybeJourney = journeyDao.findOverlapping(USER_1.getId(), TestUtils.JOURNEY_END_DATE.plusDays(-7), TestUtils.JOURNEY_END_DATE.plusDays(7));

        assertEqualsMaybeJourney(maybeJourney);
    }
    @Test
    public void testFindOverlappingWithOverlapContained(){
        Optional<Journey> maybeJourney = journeyDao.findOverlapping(USER_1.getId(), TestUtils.JOURNEY_START_DATE.plusDays(7), TestUtils.JOURNEY_END_DATE.plusDays(-7));

        assertEqualsMaybeJourney(maybeJourney);
    }
    @Test
    public void testFindOverlappingWithOverlapContainer(){
        Optional<Journey> maybeJourney = journeyDao.findOverlapping(USER_1.getId(), TestUtils.JOURNEY_START_DATE.plusDays(-7), TestUtils.JOURNEY_END_DATE.plusDays(7));

        assertEqualsMaybeJourney(maybeJourney);
    }
    @Test
    public void testFindOverlappingWithoutOverlap(){
        Optional<Journey> maybeJourney = journeyDao.findOverlapping(USER_1.getId(), LocalDate.now(), TestUtils.JOURNEY_START_DATE.plusDays(-7));

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
    public void testDelete(){
        journeyDao.delete(JOURNEY_1.getId());

        assertEquals(TestUtils.TOTAL_JOURNEYS, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.JOURNEY_TABLE));
        assertTrue(jdbcTemplate.queryForObject(TestUtils.JOURNEY_IS_DELETED_BY_ID, Boolean.class, JOURNEY_1.getId()));
    }
    @Test
    public void testDeleteWrongId(){
        journeyDao.delete(12341243);

        assertEquals(TestUtils.TOTAL_JOURNEYS, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.JOURNEY_TABLE));
        assertFalse(jdbcTemplate.queryForObject(TestUtils.JOURNEY_IS_DELETED_BY_ID, Boolean.class, JOURNEY_1.getId()));
    }
    @Test
    public void testDeleteDeleted(){
        journeyDao.delete(JOURNEY_DELETED.getId());

        assertEquals(TestUtils.TOTAL_JOURNEYS, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.JOURNEY_TABLE));
        assertTrue(jdbcTemplate.queryForObject(TestUtils.JOURNEY_IS_DELETED_BY_ID, Boolean.class, JOURNEY_DELETED.getId()));
        assertFalse(jdbcTemplate.queryForObject(TestUtils.JOURNEY_IS_DELETED_BY_ID, Boolean.class, JOURNEY_1.getId()));
    }

    @Test
    public void testUpdateDeletionMessage(){
        journeyDao.updateDeletionMessage(JOURNEY_1.getId(), TestUtils.MESSAGE_DEFAULT);

        assertEquals(TestUtils.TOTAL_JOURNEYS, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.JOURNEY_TABLE));
        assertFalse(jdbcTemplate.queryForObject(TestUtils.JOURNEY_IS_DELETED_BY_ID, Boolean.class, JOURNEY_1.getId()));
        assertEquals(TestUtils.MESSAGE_DEFAULT, jdbcTemplate.queryForObject(TestUtils.JOURNEY_GET_DELETED_MESSAGE_BY_ID, String.class, JOURNEY_1.getId()));
    }
    @Test
    public void testUpdateDeletionMessageWrongId(){
        journeyDao.updateDeletionMessage(12341243, TestUtils.MESSAGE_DEFAULT);

        assertEquals(TestUtils.TOTAL_JOURNEYS, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.JOURNEY_TABLE));
        assertFalse(jdbcTemplate.queryForObject(TestUtils.JOURNEY_IS_DELETED_BY_ID, Boolean.class, JOURNEY_1.getId()));
    }

    @Test
    public void testFindAllPaged(){
        Page<Journey> page1 = journeyDao.findAll(new PageParams(1, 1));
        Page<Journey> page2 = journeyDao.findAll(new PageParams(2, 1));

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
        TestUtils.assertEqualsJourney(JOURNEY_1, page1.getContent().getFirst());
        TestUtils.assertEqualsJourney(JOURNEY_2, page2.getContent().getFirst());
    }
    
    @Test
    public void testFindAllPagedNoJourneys(){
        TestUtils.deleteJourneys(jdbcTemplate);

        Page<Journey> page1 = journeyDao.findAll(TestUtils.PAGE_1_DEFAULT);

        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(0, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(0, page1.getContent().size());
    }

    @Test
    public void testFindByOriginCityPaged(){
        Page<Journey> page1 = journeyDao.findByOriginCity(CITY_ORIGIN.getId(), TestUtils.PAGE_1_DEFAULT);

        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(1, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(2, page1.getContent().size());
    }
    @Test
    public void testFindByOriginCityPagedWrongCity(){
        Page<Journey> page1 = journeyDao.findByOriginCity(1241234, new PageParams(1,2));

        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(0, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(0, page1.getContent().size());
    }

    @Test
    public void testUpdate(){
        journeyDao.update(
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
    public void testUpdateNotFound(){
        journeyDao.update(
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
    public void testUpdateDeleted(){
        journeyDao.update(
            JOURNEY_DELETED.getId(), 
            UNI_3, 
            TestUtils.JOURNEY_START_DATE.plusDays(10), 
            TestUtils.JOURNEY_END_DATE.plusDays(10), 
            "New description"
        );

        assertFalse(jdbcTemplate.queryForObject(
            TestUtils.JOURNEY_IS_DELETED_BY_ID, 
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
    public void testRecommendedJourneysBasic(){
        //JOURNEY_2 should have internal score of 95 (30 match city, 50 match uni, 15 overlap)
        //should have internal score of 80 (30 match city, 50 match uni)
        Journey newJourney = insertJourney(Map.of("user", USER_3, "startDate", TestUtils.JOURNEY_END_DATE.plusDays(2), "endDate", TestUtils.JOURNEY_END_DATE.plusDays(40)));

        Page<Journey> page1 = journeyDao.findRecommended(TestUtils.USER_1_MAIL, TestUtils.PAGE_1_BIG);

        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(1, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(2, page1.getContent().size());
        TestUtils.assertEqualsJourney(JOURNEY_2, page1.getContent().get(0));
        TestUtils.assertEqualsJourney(newJourney, page1.getContent().get(1));
    }
    @Test
    public void testRecommendedJourneysNoJourneys(){
        TestUtils.deleteJourneys(jdbcTemplate);
        Page<Journey> page1 = journeyDao.findRecommended(TestUtils.USER_1_MAIL, TestUtils.PAGE_1_BIG);

        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(0, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(0, page1.getContent().size());
    }
    @Test
    public void testRecommendedJourneysGoingToMyCity(){
        TestUtils.deleteJourneys(jdbcTemplate);
        insertJourney(Map.of("user", USER_1));
        //should have internal score of 80 (30 match origin city while there, 50 match origin uni while there)
        Journey newJourney1 = insertJourney(Map.of("user", USER_3, "destination", UNI_ORIGIN, "startDate", TestUtils.JOURNEY_END_DATE.plusDays(-5), "endDate", TestUtils.JOURNEY_END_DATE.plusDays(20)));
        //should have an internal score of 15 (date overlap only)
        Journey newJourney2 = insertJourney(Map.of("user", USER_4, "destination", UNI_ORIGIN, "startDate", TestUtils.JOURNEY_END_DATE.plusDays(-10), "endDate", TestUtils.JOURNEY_END_DATE.plusDays(-2)));

        Page<Journey> page1 = journeyDao.findRecommended(TestUtils.USER_1_MAIL, TestUtils.PAGE_1_BIG);

        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(1, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(2, page1.getContent().size());
        TestUtils.assertEqualsJourney(newJourney1, page1.getContent().get(0));
        TestUtils.assertEqualsJourney(newJourney2, page1.getContent().get(1));
    }
    @Test
    public void testRecommendedJourneysWithInterests(){
        //should have internal score of 116 (50 + 30 match dest uni, 15 overlap, 21 interest match)
        Journey newJourney1 = insertJourney(Map.of("user", USER_COMMON_INTERESTS_3));
        //should have internal score of 113 (50 + 30 match dest uni, 15 overlap, 18 interest match)
        Journey newJourney2 = insertJourney(Map.of("user", USER_COMMON_INTERESTS_2));
        //should have internal score of 107 (50 + 30 match dest uni, 15 overlap, 12 interest match)
        Journey newJourney3 = insertJourney(Map.of("user", USER_COMMON_INTERESTS_1));
        //JOURNEY_2 should have internal score of 95 (50 + 30 match dest uni, 15 overlap)

        Page<Journey> page1 = journeyDao.findRecommended(TestUtils.USER_1_MAIL, TestUtils.PAGE_1_BIG);

        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(1, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(4, page1.getContent().size());
        TestUtils.assertEqualsJourney(newJourney1, page1.getContent().get(0));
        TestUtils.assertEqualsJourney(newJourney2, page1.getContent().get(1));
        TestUtils.assertEqualsJourney(newJourney3, page1.getContent().get(2));
        TestUtils.assertEqualsJourney(JOURNEY_2, page1.getContent().get(3));
    }
    @Test
    public void testRecommendedJourneysWithInterestsComplex(){
        //should have internal score of 107 (50 + 30 match dest uni, 15 overlap, 12 interest match)
        Journey newJourney1 = insertJourney(Map.of("user", USER_COMMON_INTERESTS_1));
        //should have internal score of 98 (50 + 30 match origin uni, 18 interest match)
        Journey newJourney2 = insertJourney(Map.of("user", USER_COMMON_INTERESTS_2, "destination", UNI_ORIGIN, "startDate", TestUtils.JOURNEY_END_DATE.plusDays(2), "endDate", TestUtils.JOURNEY_END_DATE.plusDays(30)));
        //should have internal score of 36 (15 overlap, 21 interest match)
        Journey newJourney3 = insertJourney(Map.of("user", USER_COMMON_INTERESTS_3, "destination", UNI_ORIGIN, "startDate", TestUtils.JOURNEY_END_DATE.plusDays(-7), "endDate", TestUtils.JOURNEY_END_DATE.plusDays(-3)));
        //JOURNEY_2 should have internal score of 95 (50 + 30 match dest uni, 15 overlap)
        //should have internal score of 80 (50 + 30 match dest uni)
        Journey newJourney4 = insertJourney(Map.of("user", USER_3, "startDate", TestUtils.JOURNEY_END_DATE.plusDays(2), "endDate", TestUtils.JOURNEY_END_DATE.plusDays(20)));
        //should have internal score of 45 (30 city match, 15 overlap)
        Journey newJourney5 = insertJourney(Map.of("user", USER_5, "destination", UNI_3));

        Page<Journey> page1 = journeyDao.findRecommended(TestUtils.USER_1_MAIL, TestUtils.PAGE_1_BIG);

        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(1, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(6, page1.getContent().size());

        TestUtils.assertEqualsJourney(newJourney1, page1.getContent().get(0));
        TestUtils.assertEqualsJourney(newJourney2, page1.getContent().get(1));
        TestUtils.assertEqualsJourney(JOURNEY_2, page1.getContent().get(2));
        TestUtils.assertEqualsJourney(newJourney4, page1.getContent().get(3));   
        TestUtils.assertEqualsJourney(newJourney5, page1.getContent().get(4));
        TestUtils.assertEqualsJourney(newJourney3, page1.getContent().get(5));
    }
}

//
//    @Test
//    public void testUpdateDates(){
//        journeyDao.updateDates(
//            JOURNEY_1.getId(),
//            TestUtils.JOURNEY_START_DATE.plusDays(10),
//            TestUtils.JOURNEY_END_DATE.plusDays(10)
//        );
//
//        Journey journey = jdbcTemplate.queryForObject(
//            TestUtils.JOURNEY_SELECT_BY_ID,
//            TestUtils.JOURNEY_ROW_MAPPER,
//            JOURNEY_1.getId());
//        assertEqualsJourney(journey, Map.of("startDate", TestUtils.JOURNEY_START_DATE.plusDays(10), "endDate", TestUtils.JOURNEY_END_DATE.plusDays(10)));
//    }
//    @Test(expected=NullPointerException.class)
//    public void testUpdateDatesMissingStartDate(){
//        journeyDao.updateDates(JOURNEY_1.getId(), null, TestUtils.JOURNEY_END_DATE);
//    }
//    @Test(expected=NullPointerException.class)
//    public void testUpdateDatesMissingEndDate(){
//        journeyDao.updateDates(JOURNEY_1.getId(), TestUtils.JOURNEY_START_DATE, null);
//    }
//    @Test
//    public void testUpdateDatesWrongId(){
//        journeyDao.updateDates(1231234, TestUtils.JOURNEY_START_DATE, TestUtils.JOURNEY_END_DATE);
//
//        Journey journey = jdbcTemplate.queryForObject(TestUtils.JOURNEY_SELECT_BY_ID, TestUtils.JOURNEY_ROW_MAPPER, JOURNEY_1.getId());
//        assertEqualsJourney(journey);
//    }
//
//    @Test
//    public void testUpdateDescription(){
//        journeyDao.updateDescription(JOURNEY_1.getId(), "JOURNEY_DESCRIPTION");
//
//        Journey journey = jdbcTemplate.queryForObject(TestUtils.JOURNEY_SELECT_BY_ID, TestUtils.JOURNEY_ROW_MAPPER, JOURNEY_1.getId());
//        assertEqualsJourney(journey, Map.of("description", "JOURNEY_DESCRIPTION"));
//    }
//    @Test
//    public void testUpdateDescriptionWrongId(){
//        journeyDao.updateDescription(12341234, "NEW DESCRIPTION");
//
//        Journey journey = jdbcTemplate.queryForObject(TestUtils.JOURNEY_SELECT_BY_ID, TestUtils.JOURNEY_ROW_MAPPER, JOURNEY_1.getId());
//        assertEqualsJourney(journey);
//    }
//
//    @Test
//    public void testUpdateDestinationUniversity(){
//        journeyDao.updateDestinationUniversity(JOURNEY_1.getId(), UNI_3.getId());
//
//        Journey journey = jdbcTemplate.queryForObject(TestUtils.JOURNEY_SELECT_BY_ID, TestUtils.JOURNEY_ROW_MAPPER, JOURNEY_1.getId());
//        assertEqualsJourney(journey, Map.of("destination", UNI_3));
//    }
//    @Test
//    public void testUpdateDestinationUniversityInvalidId(){
//        journeyDao.updateDestinationUniversity(12341234, 1234123);
//
//        assertEqualsJourney(jdbcTemplate.queryForObject(TestUtils.JOURNEY_SELECT_BY_ID, TestUtils.JOURNEY_ROW_MAPPER, JOURNEY_1.getId()));
//    }
//    @Test
//    public void testUpdateDestinationUniversityInvalidJourney(){
//        journeyDao.updateDestinationUniversity(12341234, UNI_3.getId());
//
//        Journey journey = jdbcTemplate.queryForObject(TestUtils.JOURNEY_SELECT_BY_ID, TestUtils.JOURNEY_ROW_MAPPER, JOURNEY_1.getId());
//        assertEqualsJourney(journey);
//    }
