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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.itba.paw.models.Journey;
import ar.edu.itba.paw.models.Page;
import ar.edu.itba.paw.models.University;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistence.JourneyJdbcDao;

@SuppressWarnings("null")
@Transactional
@Rollback
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class JourneyJdbcDaoTest {

    private static final String JOURNEY_TABLE = "journeys";

    private static long DESTINATION_UNI_ID; 
    private static long ORIGIN_UNI_ID; 
    private static long UNI_3_ID;
    private static long USER1_ID;
    private static long USER2_ID;
    private static long USER3_ID;
    private static final LocalDate START_DATE = LocalDate.now().plusDays(7);
    private static final LocalDate END_DATE = START_DATE.plusMonths(1);
    private static final String DESCRIPTION = "Cool journey";
    private static final String USERMAIL = "user@name.com";
    private static final String USERNAME_1 = "username";
    private static final String DELETED_USER_MAIL = "deleted@name.com";
    private static final String DELETED_USER_NAME = "deletedjourney";
    private static long DESTINATION_CITY_ID;
    private static long ORIGIN_CITY_ID;
    private static long INTEREST_1_ID;
    private static long DELETED_JOURNEY_ID;
    private static long DELETED_JOURNEY_USER_ID;

    @Autowired
    private DataSource ds;

    @Autowired
    private JourneyJdbcDao journeyDao;

    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert insert;
    RowMapper<Journey> JOURNEY_ROW_MAPPER = (rs, rowNum) -> new Journey(rs.getLong("id"), new User(rs.getLong("user_id"), null, null, null, null, null, null, 0, null), rs.getDate("start_date").toLocalDate(), rs.getDate("end_date").toLocalDate(), new University(rs.getLong("destination_university_id"), null, null, null), rs.getString("description"));

    @Before
    public void setUp(){
        jdbcTemplate = new JdbcTemplate(ds);
        insert = new SimpleJdbcInsert(ds).withTableName(JOURNEY_TABLE).usingGeneratedKeyColumns("id");

        jdbcTemplate.execute("INSERT INTO countries(name, code) VALUES('Argentina', 'AR')");
        jdbcTemplate.execute("INSERT INTO countries(name, code) VALUES('Estados Unidos', 'US')");
        jdbcTemplate.execute("INSERT INTO cities(name, country_id) VALUES('Buenos Aires', (SELECT id FROM countries WHERE code = 'AR'))");
        jdbcTemplate.execute("INSERT INTO cities(name, country_id) VALUES('Massachusetts', (SELECT id FROM countries WHERE code = 'US'))");
        jdbcTemplate.execute("INSERT INTO images(content) VALUES('ffffffff')");
        jdbcTemplate.execute("INSERT INTO careers(name) VALUES('Ingenieria informatica')");
        jdbcTemplate.execute("INSERT INTO universities(name, abbreviation, city_id) VALUES('Instituto tecnologico muy largo', 'ITBA', (SELECT id FROM cities WHERE name = 'Buenos Aires'))");
        jdbcTemplate.execute("INSERT INTO universities(name, abbreviation, city_id) VALUES('Massachusetts muy largo', 'MIT', (SELECT id FROM cities WHERE name = 'Massachusetts'))");
        jdbcTemplate.execute("INSERT INTO universities(name, abbreviation, city_id) VALUES('Otra mas', 'MAS', (SELECT id FROM cities WHERE name = 'Massachusetts'))");
        jdbcTemplate.execute("INSERT INTO users(username, email, firstname, lastname, university, career_id, profile_picture_id) VALUES('username', 'user@name.com', 'user', 'name', (SELECT id FROM universities WHERE abbreviation = 'ITBA'), (SELECT id FROM careers LIMIT 1), (SELECT id FROM images LIMIT 1))");
        jdbcTemplate.execute("INSERT INTO users(username, email, firstname, lastname, university, career_id, profile_picture_id) VALUES('username2', 'user2@name.com', 'user', 'name', (SELECT id FROM universities WHERE abbreviation = 'ITBA'), (SELECT id FROM careers LIMIT 1), (SELECT id FROM images LIMIT 1))");
        jdbcTemplate.execute("INSERT INTO users(username, email, firstname, lastname, university, career_id, profile_picture_id) VALUES('anotherone', 'user3@name.com', 'user', 'name', (SELECT id FROM universities WHERE abbreviation = 'ITBA'), (SELECT id FROM careers LIMIT 1), (SELECT id FROM images LIMIT 1))");
        jdbcTemplate.execute("INSERT INTO users(username, email, firstname, lastname, university, career_id, profile_picture_id) VALUES('deletedjourney', 'deleted@name.com', 'deleted', 'journey', (SELECT id FROM universities WHERE abbreviation = 'ITBA'), (SELECT id FROM careers LIMIT 1), (SELECT id FROM images LIMIT 1))");
        jdbcTemplate.execute("INSERT INTO category(name) VALUES('Programacion')");
        jdbcTemplate.execute("INSERT INTO user_interest(user_id, category_id, score) VALUES((SELECT id FROM users WHERE username = 'username'), (SELECT id FROM category LIMIT 1), 0)");
        jdbcTemplate.execute("INSERT INTO user_interest(user_id, category_id, score) VALUES((SELECT id FROM users WHERE username = 'deletedjourney'), (SELECT id FROM category LIMIT 1), 0)");

        DESTINATION_UNI_ID = jdbcTemplate.queryForObject("SELECT id FROM universities WHERE abbreviation = 'MIT'", Long.class);
        ORIGIN_UNI_ID = jdbcTemplate.queryForObject("SELECT id FROM universities WHERE abbreviation = 'ITBA'", Long.class);
        UNI_3_ID = jdbcTemplate.queryForObject("SELECT id FROM universities WHERE abbreviation = 'MAS'", Long.class);
        USER1_ID = jdbcTemplate.queryForObject("SELECT id FROM users WHERE username = 'username'", Long.class);
        USER2_ID = jdbcTemplate.queryForObject("SELECT id FROM users WHERE username = 'username2'", Long.class);
        USER3_ID = jdbcTemplate.queryForObject("SELECT id FROM users WHERE username = 'anotherone'", Long.class);
        DELETED_JOURNEY_USER_ID = jdbcTemplate.queryForObject("SELECT id FROM users WHERE username = 'deletedjourney'", Long.class);
        DESTINATION_CITY_ID = jdbcTemplate.queryForObject("SELECT id FROM cities WHERE name = 'Massachusetts'", Long.class);
        ORIGIN_CITY_ID = jdbcTemplate.queryForObject("SELECT id FROM cities WHERE name = 'Buenos Aires'", Long.class);
        INTEREST_1_ID = jdbcTemplate.queryForObject("SELECT id FROM category WHERE name = 'Programacion'", Long.class);
        DELETED_JOURNEY_ID = insertJourneyOverride(Map.of("userId", DELETED_JOURNEY_USER_ID, "deleted", true));
    }

    private void assertEqualsJourney(Journey journey){
        assertEqualsJourney(journey, Map.of());
    }

    private void assertEqualsJourney(Journey journey, Map<String, Object> overrideParams){
        assertNotNull(journey);
        assertEquals(overrideParams.getOrDefault("userId", USER1_ID), journey.getUser().getId());
        assertEquals(overrideParams.getOrDefault("destinationId", DESTINATION_UNI_ID), journey.getDestinationUniversity().getId());
        assertEquals(overrideParams.getOrDefault("startDate", START_DATE), journey.getStartDate());
        assertEquals(overrideParams.getOrDefault("endDate", END_DATE), journey.getEndDate());
        assertEquals(overrideParams.getOrDefault("description", DESCRIPTION), journey.getDescription());
    }

    private void assertEqualsMaybeJourney(Optional<Journey> maybeJourney){
        assertNotNull(maybeJourney);
        assertTrue(maybeJourney.isPresent());
        final Journey journey = maybeJourney.get();
        assertEqualsJourney(journey); 
    }

    private long insertJourneyGeneric(){
        return insertJourneyOverride(Map.of());
    }
    private long insertJourneyOverride(Map<String, Object> overrideParams){
        HashMap<String, Object> params = new HashMap<>();
        params.put("user_id", overrideParams.getOrDefault("userId", USER1_ID));
        params.put("destination_university_id", overrideParams.getOrDefault("destinationId", DESTINATION_UNI_ID));
        params.put("start_date", Date.valueOf((LocalDate)overrideParams.getOrDefault("startDate", START_DATE)));
        params.put("end_date", Date.valueOf((LocalDate)overrideParams.getOrDefault("endDate", END_DATE)));
        params.put("description", overrideParams.getOrDefault("description", DESCRIPTION));
        params.put("deleted", overrideParams.getOrDefault("deleted", false));
        return insert.executeAndReturnKey(params).longValue();
    }

    @Test
    public void testCreate(){
        JdbcTestUtils.deleteFromTables(jdbcTemplate, JOURNEY_TABLE);
        Journey journey = journeyDao.create(
            new User(USER1_ID, null, null, null, null, null, null, 0, null),
            new University(DESTINATION_UNI_ID, null,null, null),
            START_DATE, END_DATE, DESCRIPTION
        );

        assertEqualsJourney(journey);
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, JOURNEY_TABLE));
    }
    @Test(expected = DataAccessException.class)
    public void testCreateInvalidUser(){
        Journey journey = journeyDao.create(
            new User(12341234, null, null, null, null, null, null, 0, null),
            new University(DESTINATION_UNI_ID, null,null, null),
            START_DATE, END_DATE, DESCRIPTION
        );

        assertEqualsJourney(journey);
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, JOURNEY_TABLE));
    }
    @Test(expected = DataAccessException.class)
    public void testCreateInvalidUni(){
        Journey journey = journeyDao.create(
            new User(USER1_ID, null, null, null, null, null, null, 0, null),
            new University(12431234, null,null, null),
            START_DATE, END_DATE, DESCRIPTION
        );

        assertEqualsJourney(journey);
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, JOURNEY_TABLE));
    }
    @Test
    public void testCreateDuplicated(){
        JdbcTestUtils.deleteFromTables(jdbcTemplate, JOURNEY_TABLE);
        long id = insertJourneyGeneric();

        journeyDao.create(
            new User(USER1_ID, null, null, null, null, null, null, 0, null),
            new University(DESTINATION_UNI_ID, null,null, null),
            START_DATE, END_DATE, DESCRIPTION
        );

        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, JOURNEY_TABLE));
        Optional<Journey> maybeJourney = jdbcTemplate.query("SELECT * FROM journeys WHERE id = ?", JOURNEY_ROW_MAPPER, id).stream().findFirst();
        assertNotNull(maybeJourney);
        assertTrue(maybeJourney.isPresent());
        Journey journey = maybeJourney.get();
        assertEquals(id, journey.getId());
        assertEquals(DESCRIPTION, journey.getDescription());
        assertEquals(DESTINATION_UNI_ID, journey.getDestinationUniversity().getId());
        assertEquals(START_DATE, journey.getStartDate());
        assertEquals(END_DATE, journey.getEndDate());
        assertEquals(USER1_ID, journey.getUser().getId()); 
    }
    @Test
    public void testCreateDeleted(){

        journeyDao.create(
            new User(DELETED_JOURNEY_USER_ID, null, null, null, null, null, null, 0, null),
            new University(DESTINATION_UNI_ID, null,null, null),
            START_DATE, END_DATE, DESCRIPTION
        );

        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, JOURNEY_TABLE));
        Optional<Journey> maybeJourney = jdbcTemplate.query("SELECT * FROM journeys WHERE id = ?", JOURNEY_ROW_MAPPER, DELETED_JOURNEY_ID).stream().findFirst();
        assertNotNull(maybeJourney);
        assertTrue(maybeJourney.isPresent());
        Journey journey = maybeJourney.get();
        assertEquals(DELETED_JOURNEY_ID, journey.getId());
        assertEquals(DESCRIPTION, journey.getDescription());
        assertEquals(DESTINATION_UNI_ID, journey.getDestinationUniversity().getId());
        assertEquals(START_DATE, journey.getStartDate());
        assertEquals(END_DATE, journey.getEndDate());
        assertEquals(DELETED_JOURNEY_USER_ID, journey.getUser().getId()); 
    }

    @Test
    public void testListAll(){
        Map<String, Object> user2params = Map.of("userId", USER2_ID);
        long j1 = insertJourneyGeneric();
        insertJourneyOverride(user2params);

        List<Journey> journeys = journeyDao.listAll();

        assertNotNull(journeys);
        assertEquals(2, journeys.size());
        for (Journey j : journeys){
            if (j.getId() == j1) {
                assertEqualsJourney(j);
            } else {
                assertEqualsJourney(j, user2params);
            }
        }
    }
    @Test
    public void testListAllNoJourneys(){
        List<Journey> journeys = journeyDao.listAll();

        assertNotNull(journeys);
        assertEquals(0, journeys.size());
    }

    @Test
    public void testFindById(){
        long id = insertJourneyGeneric();
        insertJourneyOverride(Map.of("userId", USER2_ID));

        Optional<Journey> maybeJourney = journeyDao.findById(id);

        assertEqualsMaybeJourney(maybeJourney);
    }
    @Test
    public void testFindByIdInvalidId(){
        insertJourneyGeneric();
        insertJourneyOverride(Map.of("userId", USER2_ID));

        Optional<Journey> maybeJourney = journeyDao.findById(12341234);

        assertNotNull(maybeJourney);
        assertFalse(maybeJourney.isPresent());
    }    
    @Test
    public void testFindByIdDeleted(){
        Optional<Journey> maybeJourney = journeyDao.findById(DELETED_JOURNEY_ID);

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
        insertJourneyGeneric();

        Optional<Journey> maybeJourney = journeyDao.findOverlappingJourney(USER1_ID, LocalDate.now(), START_DATE.plusDays(7));

        assertEqualsMaybeJourney(maybeJourney);
    }
    @Test
    public void testFindOverlappingJourneyWithOverlapRight(){
        insertJourneyGeneric();

        Optional<Journey> maybeJourney = journeyDao.findOverlappingJourney(USER1_ID, END_DATE.plusDays(-7), END_DATE.plusDays(7));

        assertEqualsMaybeJourney(maybeJourney);
    }
    @Test
    public void testFindOverlappingJourneyWithOverlapContained(){
        insertJourneyGeneric();

        Optional<Journey> maybeJourney = journeyDao.findOverlappingJourney(USER1_ID, START_DATE.plusDays(7), END_DATE.plusDays(-7));

        assertEqualsMaybeJourney(maybeJourney);
    }
    @Test
    public void testFindOverlappingJourneyWithOverlapContainer(){
        insertJourneyGeneric();

        Optional<Journey> maybeJourney = journeyDao.findOverlappingJourney(USER1_ID, START_DATE.plusDays(-7), END_DATE.plusDays(7));

        assertEqualsMaybeJourney(maybeJourney);
    }
    @Test
    public void testFindOverlappingJourneyWithoutOverlap(){
        insertJourneyGeneric();

        Optional<Journey> maybeJourney = journeyDao.findOverlappingJourney(USER1_ID, LocalDate.now(), START_DATE.plusDays(-7));

        assertNotNull(maybeJourney);
        assertFalse(maybeJourney.isPresent());
    }

    @Test
    public void testFindByOriginCity(){
        insertJourneyGeneric();

        List<Journey> journeys = journeyDao.findByOriginCity(ORIGIN_CITY_ID);
        
        assertNotNull(journeys);
        assertEquals(1, journeys.size());
        assertEquals(START_DATE, journeys.get(0).getStartDate());
        assertEquals(END_DATE, journeys.get(0).getEndDate());
        assertEquals(DESCRIPTION, journeys.get(0).getDescription());
        assertEquals(USER1_ID, journeys.get(0).getUser().getId());
        assertEquals(DESTINATION_UNI_ID, journeys.get(0).getDestinationUniversity().getId());
    }
    @Test
    public void testFindByOriginCityEmpty(){
        insertJourneyGeneric();

        List<Journey> journeys = journeyDao.findByOriginCity(DESTINATION_CITY_ID);

        assertNotNull(journeys);
        assertEquals(0, journeys.size());
    }
    @Test
    public void testFindByOriginCityDeleted(){

        List<Journey> journeys = journeyDao.findByOriginCity(ORIGIN_CITY_ID);

        assertNotNull(journeys);
        assertEquals(0, journeys.size());
    }

    @Test
    public void testFindByOriginUniversity(){
        insertJourneyGeneric();

        List<Journey> journeys = journeyDao.findByOriginUniversity(ORIGIN_UNI_ID);

        assertNotNull(journeys);
        assertEquals(1, journeys.size());
        assertEquals(START_DATE, journeys.get(0).getStartDate());
        assertEquals(END_DATE, journeys.get(0).getEndDate());
        assertEquals(DESCRIPTION, journeys.get(0).getDescription());
        assertEquals(USER1_ID, journeys.get(0).getUser().getId());
        assertEquals(DESTINATION_UNI_ID, journeys.get(0).getDestinationUniversity().getId());
    }
    @Test
    public void testFindByOriginUniversityEmpty(){
        insertJourneyGeneric();

        List<Journey> journeys = journeyDao.findByOriginUniversity(DESTINATION_UNI_ID);

        assertNotNull(journeys);
        assertEquals(0, journeys.size());
    }
    @Test
    public void testFindByOriginUniversityDeleted(){
        List<Journey> journeys = journeyDao.findByOriginUniversity(ORIGIN_UNI_ID);

        assertNotNull(journeys);
        assertEquals(0, journeys.size());
    }

    @Test
    public void testFindByUserId(){
        insertJourneyGeneric();

        Optional<Journey> maybeJourney = journeyDao.findByUserId(USER1_ID);

        assertNotNull(maybeJourney);
        assertTrue(maybeJourney.isPresent());
        Journey journey = maybeJourney.get();   
        assertEquals(START_DATE, journey.getStartDate());
        assertEquals(END_DATE, journey.getEndDate());
        assertEquals(DESCRIPTION, journey.getDescription());
        assertEquals(USER1_ID, journey.getUser().getId());
        assertEquals(DESTINATION_UNI_ID, journey.getDestinationUniversity().getId());    
    }
    @Test
    public void testFindByUserIdWrongId(){
        insertJourneyGeneric();

        Optional<Journey> maybeJourney = journeyDao.findByUserId(12341234);

        assertNotNull(maybeJourney);
        assertFalse(maybeJourney.isPresent());
    }
    @Test
    public void testFindByUserIdDeleted(){
        Optional<Journey> maybeJourney = journeyDao.findByUserId(DELETED_JOURNEY_USER_ID);

        assertNotNull(maybeJourney);
        assertFalse(maybeJourney.isPresent());
    }

    @Test
    public void testFindByUserEmail(){
        insertJourneyGeneric();

        Optional<Journey> maybeJourney = journeyDao.findByUserEmail(USERMAIL);

        assertNotNull(maybeJourney);
        assertTrue(maybeJourney.isPresent());
        Journey journey = maybeJourney.get();   
        assertEquals(START_DATE, journey.getStartDate());
        assertEquals(END_DATE, journey.getEndDate());
        assertEquals(DESCRIPTION, journey.getDescription());
        assertEquals(USER1_ID, journey.getUser().getId());
        assertEquals(DESTINATION_UNI_ID, journey.getDestinationUniversity().getId());  
    }
    @Test
    public void testFindByUserEmailDeleted(){
        insertJourneyGeneric();

        Optional<Journey> maybeJourney = journeyDao.findByUserEmail(DELETED_USER_MAIL);

        assertNotNull(maybeJourney);
        assertFalse(maybeJourney.isPresent());
    }

    @Test
    public void testGetRecommendedJourneys(){
        //TODO implement later
    }

    @Test
    public void testGetJourneysByUser(){
        long id = insertJourneyGeneric();

        List<Journey> journeys = journeyDao.getJourneysByUser(USERMAIL);

        assertNotNull(journeys);
        assertEquals(1, journeys.size());
        assertEquals(id, journeys.get(0).getId());
        assertEquals(DESCRIPTION, journeys.get(0).getDescription());
        assertEquals(DESTINATION_UNI_ID, journeys.get(0).getDestinationUniversity().getId());
        assertEquals(START_DATE, journeys.get(0).getStartDate());
        assertEquals(END_DATE, journeys.get(0).getEndDate());
        assertEquals(USER1_ID, journeys.get(0).getUser().getId());
    }
    @Test
    public void testGetJourneysByUserWrongUser(){
        insertJourneyGeneric();

        List<Journey> journeys = journeyDao.getJourneysByUser("USERMAIL");

        assertNotNull(journeys);
        assertEquals(0, journeys.size());
    }
    @Test
    public void testGetJourneysByUserDeleted(){
        insertJourneyGeneric();

        List<Journey> journeys = journeyDao.getJourneysByUser(DELETED_USER_MAIL);

        assertNotNull(journeys);
        assertEquals(0, journeys.size());
    }

    @Test
    public void testDelete(){
        JdbcTestUtils.deleteFromTables(jdbcTemplate, JOURNEY_TABLE);
        long id = insertJourneyGeneric();

        journeyDao.delete(id);

        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, JOURNEY_TABLE));
        assertTrue(jdbcTemplate.queryForObject("SELECT deleted FROM journeys WHERE id = ?", Boolean.class, id));
    }
    @Test
    public void testDeleteWrongId(){
        JdbcTestUtils.deleteFromTables(jdbcTemplate, JOURNEY_TABLE);
        long id = insertJourneyGeneric();

        journeyDao.delete(12341243);

        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, JOURNEY_TABLE));
        assertFalse(jdbcTemplate.queryForObject("SELECT deleted FROM journeys WHERE id = ?", Boolean.class, id));
    }
    @Test
    public void testDeleteDeleted(){
        JdbcTestUtils.deleteFromTables(jdbcTemplate, JOURNEY_TABLE);
        long id = insertJourneyGeneric();

        journeyDao.delete(DELETED_JOURNEY_ID);

        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, JOURNEY_TABLE));
        assertFalse(jdbcTemplate.queryForObject("SELECT deleted FROM journeys WHERE id = ?", Boolean.class, id));
    }

    @Test
    public void testDeletionMessage(){
        JdbcTestUtils.deleteFromTables(jdbcTemplate, JOURNEY_TABLE);
        long id = insertJourneyGeneric();

        journeyDao.deletionMessage(id, "WRONG");

        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, JOURNEY_TABLE));
        assertFalse(jdbcTemplate.queryForObject("SELECT deleted FROM journeys WHERE id = ?", Boolean.class, id));
        assertEquals("WRONG", jdbcTemplate.queryForObject("SELECT deleted_message FROM journeys WHERE id = ?", String.class, id));
    }
    @Test
    public void testDeletionMessageWrongId(){
        JdbcTestUtils.deleteFromTables(jdbcTemplate, JOURNEY_TABLE);
        long id = insertJourneyGeneric();

        journeyDao.deletionMessage(12341243, "WRONG");

        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, JOURNEY_TABLE));
        assertFalse(jdbcTemplate.queryForObject("SELECT deleted FROM journeys WHERE id = ?", Boolean.class, id));
    }

    @Test
    public void testGetOthersJourneys(){
        insertJourneyGeneric();
        insertJourneyOverride(Map.of("userId", USER2_ID));

        List<Journey> journeys = journeyDao.getOthersJourneys(USER2_ID);

        assertNotNull(journeys);
        assertEquals(1, journeys.size());
        assertEquals(DESCRIPTION, journeys.get(0).getDescription());
        assertEquals(DESTINATION_UNI_ID, journeys.get(0).getDestinationUniversity().getId());
        assertEquals(START_DATE, journeys.get(0).getStartDate());
        assertEquals(END_DATE, journeys.get(0).getEndDate());
        assertEquals(USER1_ID, journeys.get(0).getUser().getId());    
    }

    @Test
    public void testUpdateDates(){
        long id = insertJourneyOverride(Map.of("startDate", START_DATE.plusDays(10), "endDate", END_DATE.plusDays(10)));

        journeyDao.updateDates(id, START_DATE, END_DATE);

        Optional<Journey> maybeJourney = jdbcTemplate.query("SELECT * FROM journeys WHERE id = ?", JOURNEY_ROW_MAPPER, id).stream().findFirst();
        assertNotNull(maybeJourney);
        assertTrue(maybeJourney.isPresent());
        Journey journey = maybeJourney.get();
        assertEquals(id, journey.getId());
        assertEquals(DESCRIPTION, journey.getDescription());
        assertEquals(DESTINATION_UNI_ID, journey.getDestinationUniversity().getId());
        assertEquals(START_DATE, journey.getStartDate());
        assertEquals(END_DATE, journey.getEndDate());
        assertEquals(USER1_ID, journey.getUser().getId());    
    }
    @Test(expected=NullPointerException.class)
    public void testUpdateDatesMissingDate(){
        long id = insertJourneyOverride(Map.of("startDate", START_DATE.plusDays(10), "endDate", END_DATE.plusDays(10)));

        journeyDao.updateDates(id, null, END_DATE);
    }
    @Test
    public void testUpdateDatesWrongId(){
        long id = insertJourneyGeneric();

        journeyDao.updateDates(1231234, START_DATE, END_DATE);

        Optional<Journey> maybeJourney = jdbcTemplate.query("SELECT * FROM journeys WHERE id = ?", JOURNEY_ROW_MAPPER, id).stream().findFirst();
        assertNotNull(maybeJourney);
        assertTrue(maybeJourney.isPresent());
        Journey journey = maybeJourney.get();
        assertEquals(id, journey.getId());
        assertEquals(DESCRIPTION, journey.getDescription());
        assertEquals(DESTINATION_UNI_ID, journey.getDestinationUniversity().getId());
        assertEquals(START_DATE, journey.getStartDate());
        assertEquals(END_DATE, journey.getEndDate());
        assertEquals(USER1_ID, journey.getUser().getId());       
    }

    @Test
    public void testUpdateDescription(){
        long id = insertJourneyGeneric();

        journeyDao.updateDescription(id, "NEW DESCRIPTION");
        Optional<Journey> maybeJourney = jdbcTemplate.query("SELECT * FROM journeys WHERE id = ?", JOURNEY_ROW_MAPPER, id).stream().findFirst();
        assertNotNull(maybeJourney);
        assertTrue(maybeJourney.isPresent());
        Journey journey = maybeJourney.get();
        assertEquals(id, journey.getId());
        assertEquals("NEW DESCRIPTION", journey.getDescription());
        assertEquals(DESTINATION_UNI_ID, journey.getDestinationUniversity().getId());
        assertEquals(START_DATE, journey.getStartDate());
        assertEquals(END_DATE, journey.getEndDate());
        assertEquals(USER1_ID, journey.getUser().getId());    
    }
    @Test
    public void testUpdateDescriptionWrongId(){
        long id = insertJourneyGeneric();

        journeyDao.updateDescription(12341234, "NEW DESCRIPTION");
        Optional<Journey> maybeJourney = jdbcTemplate.query("SELECT * FROM journeys WHERE id = ?", JOURNEY_ROW_MAPPER, id).stream().findFirst();
        assertNotNull(maybeJourney);
        assertTrue(maybeJourney.isPresent());
        Journey journey = maybeJourney.get();
        assertEquals(id, journey.getId());
        assertEquals(DESCRIPTION, journey.getDescription());
        assertEquals(DESTINATION_UNI_ID, journey.getDestinationUniversity().getId());
        assertEquals(START_DATE, journey.getStartDate());
        assertEquals(END_DATE, journey.getEndDate());
        assertEquals(USER1_ID, journey.getUser().getId());    
    }
    
    @Test
    public void testUpdateDestinationUniversity(){
        long id = insertJourneyGeneric();

        journeyDao.updateDestinationUniversity(id, UNI_3_ID);

        Optional<Journey> maybeJourney = jdbcTemplate.query("SELECT * FROM journeys WHERE id = ?", JOURNEY_ROW_MAPPER, id).stream().findFirst();
        assertNotNull(maybeJourney);
        assertTrue(maybeJourney.isPresent());
        Journey journey = maybeJourney.get();
        assertEquals(id, journey.getId());
        assertEquals(DESCRIPTION, journey.getDescription());
        assertEquals(UNI_3_ID, journey.getDestinationUniversity().getId());
        assertEquals(START_DATE, journey.getStartDate());
        assertEquals(END_DATE, journey.getEndDate());
        assertEquals(USER1_ID, journey.getUser().getId()); 
    }
    @Test(expected = DataAccessException.class)
    public void testUpdateDestinationUniversityInvalidId(){
        long id = insertJourneyGeneric();

        journeyDao.updateDestinationUniversity(id, 1234123);
    }
    @Test
    public void testUpdateDestinationUniversityInvalidJourney(){
        long id = insertJourneyGeneric();

        journeyDao.updateDestinationUniversity(12341234, UNI_3_ID);

        Optional<Journey> maybeJourney = jdbcTemplate.query("SELECT * FROM journeys WHERE id = ?", JOURNEY_ROW_MAPPER, id).stream().findFirst();
        assertNotNull(maybeJourney);
        assertTrue(maybeJourney.isPresent());
        Journey journey = maybeJourney.get();
        assertEquals(id, journey.getId());
        assertEquals(DESCRIPTION, journey.getDescription());
        assertEquals(DESTINATION_UNI_ID, journey.getDestinationUniversity().getId());
        assertEquals(START_DATE, journey.getStartDate());
        assertEquals(END_DATE, journey.getEndDate());
        assertEquals(USER1_ID, journey.getUser().getId()); 
    }

    @Test
    public void testListAllPaged(){
        long id1 = insertJourneyGeneric();
        long id2 = insertJourneyOverride(Map.of("userId", USER2_ID));

        Page<Journey> page1 = journeyDao.listAll(1, 1);
        Page<Journey> page2 = journeyDao.listAll(2, 1);

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
        assertEquals(id1, page1.getContent().getFirst().getId());
        assertEquals(id2, page2.getContent().getFirst().getId());
    }
    
    @Test
    public void testListAllPagedNoJourneys(){
        Page<Journey> page1 = journeyDao.listAll(1, 10);

        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(0, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(0, page1.getContent().size());
    }

    @Test
    public void testGetOthersJourneysPaged(){
        insertJourneyGeneric();
        long id2 = insertJourneyOverride(Map.of("userId", USER2_ID));

        Page<Journey> page1 = journeyDao.getOthersJourneys(USER1_ID, 1, 1);

        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(1, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(1, page1.getContent().size());
        assertEquals(id2, page1.getContent().getFirst().getId());
    }
    @Test
    public void testGetOthersJourneysPagedNoJourneys(){
        Page<Journey> page1 = journeyDao.getOthersJourneys(USER1_ID, 1, 1);

        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(0, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(0, page1.getContent().size());
    }

    @Test
    public void testFindByOriginCityPaged(){
        insertJourneyGeneric();
        insertJourneyOverride(Map.of("userId", USER2_ID));

        Page<Journey> page1 = journeyDao.findByOriginCity(ORIGIN_CITY_ID, 1, 2);

        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(1, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(2, page1.getContent().size());
    }
    @Test
    public void testFindByOriginCityPagedWrongCity(){
        insertJourneyGeneric();
        insertJourneyOverride(Map.of("userId", USER2_ID));

        Page<Journey> page1 = journeyDao.findByOriginCity(1241234, 1, 2);

        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(0, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(0, page1.getContent().size());
    }

    @Test
    public void testUpdateData(){
        long id = insertJourneyGeneric();

        journeyDao.updateData(id, new University(UNI_3_ID, null, null, null), START_DATE.plusDays(10), END_DATE.plusDays(10), "New description");

        Optional<Journey> maybeJourney = jdbcTemplate.query("SELECT * FROM journeys WHERE id = ?", JOURNEY_ROW_MAPPER, id).stream().findFirst();
        assertNotNull(maybeJourney);
        assertTrue(maybeJourney.isPresent());
        Journey journey = maybeJourney.get();
        assertEquals(id, journey.getId());
        assertEquals("New description", journey.getDescription());
        assertEquals(UNI_3_ID, journey.getDestinationUniversity().getId());
        assertEquals(START_DATE.plusDays(10), journey.getStartDate());
        assertEquals(END_DATE.plusDays(10), journey.getEndDate());
        assertEquals(USER1_ID, journey.getUser().getId());   
    }
    @Test
    public void testUpdateDataDeleted(){

        journeyDao.updateData(DELETED_JOURNEY_ID, new University(UNI_3_ID, null, null, null), START_DATE.plusDays(10), END_DATE.plusDays(10), "New description");

        assertFalse(jdbcTemplate.queryForObject("SELECT deleted FROM journeys WHERE id = ?", Boolean.class, DELETED_JOURNEY_ID));
        Optional<Journey> maybeJourney = jdbcTemplate.query("SELECT * FROM journeys WHERE id = ?", JOURNEY_ROW_MAPPER, DELETED_JOURNEY_ID).stream().findFirst();
        assertNotNull(maybeJourney);
        assertTrue(maybeJourney.isPresent());
        Journey journey = maybeJourney.get();
        assertEquals(DELETED_JOURNEY_ID, journey.getId());
        assertEquals("New description", journey.getDescription());
        assertEquals(UNI_3_ID, journey.getDestinationUniversity().getId());
        assertEquals(START_DATE.plusDays(10), journey.getStartDate());
        assertEquals(END_DATE.plusDays(10), journey.getEndDate());
        assertEquals(DELETED_JOURNEY_USER_ID, journey.getUser().getId());   
    }

    @Test
    public void testSearchJourneys(){
        insertJourneyGeneric();
        insertJourneyOverride(Map.of("userId", USER2_ID));
        insertJourneyOverride(Map.of("userId", USER3_ID));

        Page<Journey> page = journeyDao.searchJourneys(USERNAME_1.substring(0, 6), 1, 5);

        assertNotNull(page);
        assertEquals(1,page.getCurrentPage());
        assertEquals(1,page.getTotalPages());
        assertNotNull(page.getContent());
        assertEquals(2, page.getContent().size());
    }
    @Test
    public void testSearchJourneysNoJourneys(){
        Page<Journey> page = journeyDao.searchJourneys(USERNAME_1.substring(0, 6), 1, 5);

        assertNotNull(page);
        assertEquals(1,page.getCurrentPage());
        assertEquals(0,page.getTotalPages());
        assertNotNull(page.getContent());
        assertEquals(0, page.getContent().size());
    }
    @Test
    public void testSearchJourneysDeleted(){
        Page<Journey> page = journeyDao.searchJourneys(DELETED_USER_NAME, 1, 5);

        assertNotNull(page);
        assertEquals(1,page.getCurrentPage());
        assertEquals(0,page.getTotalPages());
        assertNotNull(page.getContent());
        assertEquals(0, page.getContent().size());
    }

    @Test
    public void testFindByFiltersDestination(){
        long id1 = insertJourneyGeneric();
        long id2 = insertJourneyOverride(Map.of("userId", USER2_ID));
        insertJourneyOverride(Map.of("userId", USER3_ID, "destinationId", ORIGIN_UNI_ID));

        List<Journey> journeys = journeyDao.findByFilters(Long.toString(DESTINATION_CITY_ID), null, null, null);

        assertNotNull(journeys);
        assertEquals(2, journeys.size());
        for (Journey j : journeys){
            assertEquals(DESCRIPTION, j.getDescription());
            assertEquals(DESTINATION_UNI_ID, j.getDestinationUniversity().getId());
            assertEquals(START_DATE, j.getStartDate());
            assertEquals(END_DATE, j.getEndDate());
            if (j.getId() == id1) {
                assertEquals(USER1_ID, j.getUser().getId());
            } else {
                assertEquals(id2, j.getId());
                assertEquals(USER2_ID, j.getUser().getId());
            }
        }
    }
    @Test
    public void testFindByFiltersStartDate(){
        long id1 = insertJourneyGeneric();
        long id2 = insertJourneyOverride(Map.of("userId", USER2_ID));
        insertJourneyOverride(Map.of("userId", USER3_ID, "destinationId", ORIGIN_UNI_ID, "endDate", END_DATE.plusDays(-15)));

        List<Journey> journeys = journeyDao.findByFilters(null, START_DATE.plusDays(20), null, null);

        assertNotNull(journeys);
        assertEquals(2, journeys.size());
        for (Journey j : journeys){
            assertEquals(DESCRIPTION, j.getDescription());
            assertEquals(DESTINATION_UNI_ID, j.getDestinationUniversity().getId());
            assertEquals(START_DATE, j.getStartDate());
            assertEquals(END_DATE, j.getEndDate());
            if (j.getId() == id1) {
                assertEquals(USER1_ID, j.getUser().getId());
            } else {
                assertEquals(id2, j.getId());
                assertEquals(USER2_ID, j.getUser().getId());
            }
        }
    }
    @Test
    public void testFindByFiltersStartDateAndEmptyDestination(){
        long id1 = insertJourneyGeneric();
        long id2 = insertJourneyOverride(Map.of("userId", USER2_ID));
        insertJourneyOverride(Map.of("userId", USER3_ID, "destinationId", ORIGIN_UNI_ID, "endDate", END_DATE.plusDays(-15)));

        List<Journey> journeys = journeyDao.findByFilters("", START_DATE.plusDays(20), null, null);

        assertNotNull(journeys);
        assertEquals(2, journeys.size());
        for (Journey j : journeys){
            assertEquals(DESCRIPTION, j.getDescription());
            assertEquals(DESTINATION_UNI_ID, j.getDestinationUniversity().getId());
            assertEquals(START_DATE, j.getStartDate());
            assertEquals(END_DATE, j.getEndDate());
            if (j.getId() == id1) {
                assertEquals(USER1_ID, j.getUser().getId());
            } else {
                assertEquals(id2, j.getId());
                assertEquals(USER2_ID, j.getUser().getId());
            }
        }
    }
    @Test
    public void testFindByFiltersEndDate(){
        long id1 = insertJourneyGeneric();
        long id2 = insertJourneyOverride(Map.of("userId", USER2_ID));
        insertJourneyOverride(Map.of("userId", USER3_ID, "destinationId", ORIGIN_UNI_ID, "startDate", START_DATE.plusDays(15)));

        List<Journey> journeys = journeyDao.findByFilters(null, null, END_DATE.plusDays(-20), null);

        assertNotNull(journeys);
        assertEquals(2, journeys.size());
        for (Journey j : journeys){
            assertEquals(DESCRIPTION, j.getDescription());
            assertEquals(DESTINATION_UNI_ID, j.getDestinationUniversity().getId());
            assertEquals(START_DATE, j.getStartDate());
            assertEquals(END_DATE, j.getEndDate());
            if (j.getId() == id1) {
                assertEquals(USER1_ID, j.getUser().getId());
            } else {
                assertEquals(id2, j.getId());
                assertEquals(USER2_ID, j.getUser().getId());
            }
        }
    }
    @Test
    public void testFindByFiltersInterests(){
        long id1 = insertJourneyGeneric();
        insertJourneyOverride(Map.of("userId", USER2_ID));
        insertJourneyOverride(Map.of("userId", USER3_ID, "destinationId", ORIGIN_UNI_ID));

        List<Journey> journeys = journeyDao.findByFilters(null, null, null, Long.toString(INTEREST_1_ID));

        assertNotNull(journeys);
        assertEquals(1, journeys.size());
        assertEquals(DESCRIPTION, journeys.getFirst().getDescription());
        assertEquals(DESTINATION_UNI_ID, journeys.getFirst().getDestinationUniversity().getId());
        assertEquals(START_DATE, journeys.getFirst().getStartDate());
        assertEquals(END_DATE, journeys.getFirst().getEndDate());
        assertEquals(USER1_ID, journeys.getFirst().getUser().getId());
        assertEquals(id1, journeys.getFirst().getId());
    }
    @Test
    public void testFindByFiltersNoConditions(){
        insertJourneyGeneric();
        insertJourneyOverride(Map.of("userId", USER2_ID));
        insertJourneyOverride(Map.of("userId", USER3_ID, "destinationId", ORIGIN_UNI_ID));

        List<Journey> journeys = journeyDao.findByFilters(null, null, null, null);

        assertNotNull(journeys);
        assertEquals(3, journeys.size());
    }
    @Test
    public void testFindByFiltersInterestsAllConditions(){
        long id1 = insertJourneyGeneric();
        insertJourneyOverride(Map.of("userId", USER2_ID, "destinationId", UNI_3_ID, "startDate", START_DATE.plusDays(7)));
        insertJourneyOverride(Map.of("userId", USER3_ID, "destinationId", UNI_3_ID, "endDate", END_DATE.plusDays(-7)));

        List<Journey> journeys = journeyDao.findByFilters(Long.toString(DESTINATION_CITY_ID), START_DATE, END_DATE, Long.toString(INTEREST_1_ID));

        assertNotNull(journeys);
        assertEquals(1, journeys.size());
        assertEquals(DESCRIPTION, journeys.getFirst().getDescription());
        assertEquals(DESTINATION_UNI_ID, journeys.getFirst().getDestinationUniversity().getId());
        assertEquals(START_DATE, journeys.getFirst().getStartDate());
        assertEquals(END_DATE, journeys.getFirst().getEndDate());
        assertEquals(USER1_ID, journeys.getFirst().getUser().getId());
        assertEquals(id1, journeys.getFirst().getId());
    }
    @Test
    public void testFindByFiltersInterestsAllNoInterests(){
        long id1 = insertJourneyGeneric();
        insertJourneyOverride(Map.of("userId", USER2_ID, "destinationId", UNI_3_ID, "startDate", START_DATE.plusDays(15)));
        insertJourneyOverride(Map.of("userId", USER3_ID, "destinationId", ORIGIN_UNI_ID, "endDate", END_DATE.plusDays(-15)));

        List<Journey> journeys = journeyDao.findByFilters(Long.toString(DESTINATION_CITY_ID), START_DATE.plusDays(20), END_DATE.plusDays(-20), "");

        assertNotNull(journeys);
        assertEquals(1, journeys.size());
        assertEquals(DESCRIPTION, journeys.getFirst().getDescription());
        assertEquals(DESTINATION_UNI_ID, journeys.getFirst().getDestinationUniversity().getId());
        assertEquals(START_DATE, journeys.getFirst().getStartDate());
        assertEquals(END_DATE, journeys.getFirst().getEndDate());
        assertEquals(USER1_ID, journeys.getFirst().getUser().getId());
        assertEquals(id1, journeys.getFirst().getId());
    }

    @Test
    public void testFindByFiltersDestinationExcluding(){
        insertJourneyGeneric();
        long id2 = insertJourneyOverride(Map.of("userId", USER2_ID));
        insertJourneyOverride(Map.of("userId", USER3_ID, "destinationId", ORIGIN_UNI_ID));

        List<Journey> journeys = journeyDao.findByFilters(USER1_ID, Long.toString(DESTINATION_CITY_ID), null, null, null);

        assertNotNull(journeys);
        assertEquals(1, journeys.size());
        assertEquals(DESCRIPTION, journeys.getFirst().getDescription());
        assertEquals(DESTINATION_UNI_ID, journeys.getFirst().getDestinationUniversity().getId());
        assertEquals(START_DATE, journeys.getFirst().getStartDate());
        assertEquals(END_DATE, journeys.getFirst().getEndDate());
        assertEquals(id2, journeys.getFirst().getId());
        assertEquals(USER2_ID, journeys.getFirst().getUser().getId());
    }
    @Test
    public void testFindByFiltersStartDateExcluding(){
        insertJourneyGeneric();
        long id2 = insertJourneyOverride(Map.of("userId", USER2_ID));
        insertJourneyOverride(Map.of("userId", USER3_ID, "destinationId", ORIGIN_UNI_ID, "endDate", END_DATE.plusDays(-15)));

        List<Journey> journeys = journeyDao.findByFilters(USER1_ID, null, START_DATE.plusDays(20), null, null);

        assertNotNull(journeys);
        assertEquals(1, journeys.size());
        assertEquals(DESCRIPTION, journeys.getFirst().getDescription());
        assertEquals(DESTINATION_UNI_ID, journeys.getFirst().getDestinationUniversity().getId());
        assertEquals(START_DATE, journeys.getFirst().getStartDate());
        assertEquals(END_DATE, journeys.getFirst().getEndDate());
        assertEquals(id2, journeys.getFirst().getId());
        assertEquals(USER2_ID, journeys.getFirst().getUser().getId());
    }
    @Test
    public void testFindByFiltersStartDateAndEmptyDestinationExcluding(){
        insertJourneyGeneric();
        long id2 = insertJourneyOverride(Map.of("userId", USER2_ID));
        insertJourneyOverride(Map.of("userId", USER3_ID, "destinationId", ORIGIN_UNI_ID, "endDate", END_DATE.plusDays(-15)));

        List<Journey> journeys = journeyDao.findByFilters(USER1_ID, "", START_DATE.plusDays(20), null, null);

        assertNotNull(journeys);
        assertEquals(1, journeys.size());
        assertEquals(DESCRIPTION, journeys.getFirst().getDescription());
        assertEquals(DESTINATION_UNI_ID, journeys.getFirst().getDestinationUniversity().getId());
        assertEquals(START_DATE, journeys.getFirst().getStartDate());
        assertEquals(END_DATE, journeys.getFirst().getEndDate());
        assertEquals(id2, journeys.getFirst().getId());
        assertEquals(USER2_ID, journeys.getFirst().getUser().getId());
    }
    @Test
    public void testFindByFiltersEndDateExcluding(){
        insertJourneyGeneric();
        long id2 = insertJourneyOverride(Map.of("userId", USER2_ID));
        insertJourneyOverride(Map.of("userId", USER3_ID, "destinationId", ORIGIN_UNI_ID, "startDate", START_DATE.plusDays(15)));

        List<Journey> journeys = journeyDao.findByFilters(USER1_ID, null, null, END_DATE.plusDays(-20), null);

        assertNotNull(journeys);
        assertEquals(1, journeys.size());
        assertEquals(DESCRIPTION, journeys.getFirst().getDescription());
        assertEquals(DESTINATION_UNI_ID, journeys.getFirst().getDestinationUniversity().getId());
        assertEquals(START_DATE, journeys.getFirst().getStartDate());
        assertEquals(END_DATE, journeys.getFirst().getEndDate());
        assertEquals(id2, journeys.getFirst().getId());
        assertEquals(USER2_ID, journeys.getFirst().getUser().getId());
    }
    @Test
    public void testFindByFiltersInterestsExcluding(){
        insertJourneyGeneric();
        insertJourneyOverride(Map.of("userId", USER2_ID));
        insertJourneyOverride(Map.of("userId", USER3_ID, "destinationId", ORIGIN_UNI_ID, "endDate", END_DATE.plusDays(-7)));

        List<Journey> journeys = journeyDao.findByFilters(USER1_ID, null, null, null, Long.toString(INTEREST_1_ID));

        assertNotNull(journeys);
        assertEquals(0, journeys.size());
    }
    @Test
    public void testFindByFiltersNoConditionsExcluding(){
        insertJourneyGeneric();
        insertJourneyOverride(Map.of("userId", USER2_ID));
        insertJourneyOverride(Map.of("userId", USER3_ID, "destinationId", ORIGIN_UNI_ID, "endDate", END_DATE.plusDays(-7)));

        List<Journey> journeys = journeyDao.findByFilters(USER1_ID, null, null, null, null);

        assertNotNull(journeys);
        assertEquals(2, journeys.size());
    }
    @Test
    public void testFindByFiltersInterestsAllConditionsExcluding(){
        insertJourneyGeneric();
        insertJourneyOverride(Map.of("userId", USER2_ID, "destinationId", UNI_3_ID, "startDate", START_DATE.plusDays(7)));
        insertJourneyOverride(Map.of("userId", USER3_ID, "destinationId", UNI_3_ID, "endDate", END_DATE.plusDays(-7)));

        List<Journey> journeys = journeyDao.findByFilters(USER1_ID, Long.toString(DESTINATION_CITY_ID), START_DATE, END_DATE, Long.toString(INTEREST_1_ID));

        assertNotNull(journeys);
        assertEquals(0, journeys.size());
    }
    @Test
    public void testFindByFiltersInterestsAllNoInterestsExcluding(){
        insertJourneyGeneric();
        insertJourneyOverride(Map.of("userId", USER2_ID, "destinationId", UNI_3_ID, "startDate", START_DATE.plusDays(15)));
        insertJourneyOverride(Map.of("userId", USER3_ID, "destinationId", ORIGIN_UNI_ID, "endDate", END_DATE.plusDays(-15)));

        List<Journey> journeys = journeyDao.findByFilters(USER1_ID, Long.toString(DESTINATION_CITY_ID), START_DATE.plusDays(20), END_DATE.plusDays(-20), "");

        assertNotNull(journeys);
        assertEquals(0, journeys.size());
    }

    @Test
    public void testFindByFiltersDestinationPaged(){
        insertJourneyGeneric();
        long id2 = insertJourneyOverride(Map.of("userId", USER2_ID));
        insertJourneyOverride(Map.of("userId", USER3_ID, "destinationId", ORIGIN_UNI_ID));

        Page<Journey> journeys = journeyDao.findByFilters(USER2_ID, DESTINATION_CITY_ID, null, null, null, 1, 2);

        assertNotNull(journeys);
        assertEquals(1, journeys.getContent().size());
        assertEquals(DESCRIPTION, journeys.getContent().getFirst().getDescription());
        assertEquals(DESTINATION_UNI_ID, journeys.getContent().getFirst().getDestinationUniversity().getId());
        assertEquals(START_DATE, journeys.getContent().getFirst().getStartDate());
        assertEquals(END_DATE, journeys.getContent().getFirst().getEndDate());
        assertEquals(id2, journeys.getContent().getFirst().getId());
        assertEquals(USER2_ID, journeys.getContent().getFirst().getUser().getId());
    }
    @Test
    public void testFindByFiltersStartDatePaged(){
        insertJourneyGeneric();
        long id2 = insertJourneyOverride(Map.of("userId", USER2_ID));
        insertJourneyOverride(Map.of("userId", USER3_ID, "destinationId", ORIGIN_UNI_ID, "endDate", END_DATE.plusDays(-15)));

        Page<Journey> journeys = journeyDao.findByFilters(USER2_ID, null, START_DATE.plusDays(20), null, null, 1, 2);

        assertNotNull(journeys);
        assertEquals(1, journeys.getContent().size());
        assertEquals(DESCRIPTION, journeys.getContent().getFirst().getDescription());
        assertEquals(DESTINATION_UNI_ID, journeys.getContent().getFirst().getDestinationUniversity().getId());
        assertEquals(START_DATE, journeys.getContent().getFirst().getStartDate());
        assertEquals(END_DATE, journeys.getContent().getFirst().getEndDate());
        assertEquals(id2, journeys.getContent().getFirst().getId());
        assertEquals(USER2_ID, journeys.getContent().getFirst().getUser().getId());
    }
    @Test
    public void testFindByFiltersEndDatePaged(){
        insertJourneyGeneric();
        long id2 = insertJourneyOverride(Map.of("userId", USER2_ID));
        insertJourneyOverride(Map.of("userId", USER3_ID, "destinationId", ORIGIN_UNI_ID, "startDate", START_DATE.plusDays(15)));

        Page<Journey> journeys = journeyDao.findByFilters(USER2_ID, null, null, END_DATE.plusDays(-20), null, 1, 2);

        assertNotNull(journeys);
        assertEquals(1, journeys.getContent().size());
        assertEquals(DESCRIPTION, journeys.getContent().getFirst().getDescription());
        assertEquals(DESTINATION_UNI_ID, journeys.getContent().getFirst().getDestinationUniversity().getId());
        assertEquals(START_DATE, journeys.getContent().getFirst().getStartDate());
        assertEquals(END_DATE, journeys.getContent().getFirst().getEndDate());
        assertEquals(id2, journeys.getContent().getFirst().getId());
        assertEquals(USER2_ID, journeys.getContent().getFirst().getUser().getId());
    }
    @Test
    public void testFindByFiltersInterestsPaged(){
        insertJourneyGeneric();
        insertJourneyOverride(Map.of("userId", USER2_ID));
        insertJourneyOverride(Map.of("userId", USER3_ID, "destinationId", ORIGIN_UNI_ID, "endDate", END_DATE.plusDays(-7)));

        Page<Journey> journeys = journeyDao.findByFilters(USER2_ID, null, null, null, INTEREST_1_ID, 1, 2);

        assertNotNull(journeys);
        assertEquals(0, journeys.getContent().size());
    }
    @Test
    public void testFindByFiltersNoConditionsPaged(){
        insertJourneyGeneric();
        insertJourneyOverride(Map.of("userId", USER2_ID));
        insertJourneyOverride(Map.of("userId", USER3_ID, "destinationId", ORIGIN_UNI_ID, "endDate", END_DATE.plusDays(-7)));

        Page<Journey> journeys = journeyDao.findByFilters(null, null, null, null, null, 1, 2);

        assertNotNull(journeys);
        assertEquals(2, journeys.getContent().size());
    }
    @Test
    public void testFindByFiltersInterestsAllConditionsPaged(){
        insertJourneyGeneric();
        insertJourneyOverride(Map.of("userId", USER2_ID, "destinationId", UNI_3_ID, "startDate", START_DATE.plusDays(7)));
        insertJourneyOverride(Map.of("userId", USER3_ID, "destinationId", UNI_3_ID, "endDate", END_DATE.plusDays(-7)));

        Page<Journey> journeys = journeyDao.findByFilters(USER2_ID, DESTINATION_CITY_ID, START_DATE, END_DATE, INTEREST_1_ID, 1, 2);

        assertNotNull(journeys);
        assertEquals(0, journeys.getContent().size());
    }
    @Test
    public void testFindByFiltersInterestsAllNoInterestsPaged(){
        insertJourneyGeneric();
        insertJourneyOverride(Map.of("userId", USER2_ID, "destinationId", UNI_3_ID, "startDate", START_DATE.plusDays(15)));
        insertJourneyOverride(Map.of("userId", USER3_ID, "destinationId", ORIGIN_UNI_ID, "endDate", END_DATE.plusDays(-15)));

        Page<Journey> journeys = journeyDao.findByFilters(USER2_ID, DESTINATION_CITY_ID, START_DATE.plusDays(20), END_DATE.plusDays(-20), null, 1, 2);

        assertNotNull(journeys);
        assertEquals(0, journeys.getContent().size());
    }
}
