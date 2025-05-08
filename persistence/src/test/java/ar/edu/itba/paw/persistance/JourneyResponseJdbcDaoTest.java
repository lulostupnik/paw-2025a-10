package ar.edu.itba.paw.persistance;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

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

import ar.edu.itba.paw.models.JourneyResponse;
import ar.edu.itba.paw.models.Page;
import ar.edu.itba.paw.persistence.JourneyResponseJdbcDao;

@SuppressWarnings("null")
@Transactional
@Rollback
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class JourneyResponseJdbcDaoTest {

    private static final String RESPONSE_TABLE = "journey_responses";
    private static final String RESPONSE_MESSAGE = "COOL!";
    private static final String USER1_NAME = "username";
    private static final String USER2_NAME = "username2";
    private static final String USER3_NAME = "username3";
    private static final String DELETION_MESSAGE = "get deleted";
    private static final LocalDateTime RESPONSE_TIMESTAMP = LocalDateTime.now().withNano(0);
    private static final LocalDateTime RESPONSE_TIMESTAMP_2 = RESPONSE_TIMESTAMP.plusHours(1);
    private static final LocalDateTime RESPONSE_TIMESTAMP_3 = RESPONSE_TIMESTAMP.plusHours(2);

    private static long USER1_ID;
    private static long USER2_ID;
    private static long USER3_ID;
    private static long JOURNEY1_ID;
    private static long RESPONSE1_ID;
    private static long RESPONSE2_ID;
    private static long RESPONSE3_ID;
    private static long RESPONSE_DELETED_ID;
    private static final long TOTAL_RESPONSES = 3;
    
    @Autowired
    private DataSource ds;

    @Autowired
    private JourneyResponseJdbcDao responseDao;

    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert insert;

    @Before
    public void setUp(){
        jdbcTemplate = new JdbcTemplate(ds);
        insert = new SimpleJdbcInsert(ds).withTableName(RESPONSE_TABLE).usingGeneratedKeyColumns("id");

        jdbcTemplate.execute("INSERT INTO users(username, email, firstname, lastname, university, career_id, profile_picture_id) VALUES('username', 'user@name.com', 'user', 'name', (SELECT id FROM universities WHERE abbreviation = 'ITBA'), (SELECT id FROM careers LIMIT 1), (SELECT id FROM images LIMIT 1))");
        jdbcTemplate.execute("INSERT INTO users(username, email, firstname, lastname, university, career_id, profile_picture_id) VALUES('username2', 'user2@name.com', 'user', 'name', (SELECT id FROM universities WHERE abbreviation = 'ITBA'), (SELECT id FROM careers LIMIT 1), (SELECT id FROM images LIMIT 1))");
        jdbcTemplate.execute("INSERT INTO users(username, email, firstname, lastname, university, career_id, profile_picture_id) VALUES('username3', 'user3@name.com', 'user', 'name', (SELECT id FROM universities WHERE abbreviation = 'ITBA'), (SELECT id FROM careers LIMIT 1), (SELECT id FROM images LIMIT 1))");
        jdbcTemplate.execute("INSERT INTO journeys(user_id, destination_university_id, start_date, end_date, description, deleted) VALUES((SELECT id FROM users WHERE username = 'username'),  (SELECT id FROM universities WHERE abbreviation =  'ITBA'), '2000-01-01', '2000-04-01', 'cool', FALSE)");
        jdbcTemplate.execute("INSERT INTO journeys(user_id, destination_university_id, start_date, end_date, description, deleted) VALUES((SELECT id FROM users WHERE username = 'username2'), (SELECT id FROM universities WHERE abbreviation = 'ITBA'),  '2000-01-01', '2000-04-01', 'cool', FALSE)");
        jdbcTemplate.execute("INSERT INTO journeys(user_id, destination_university_id, start_date, end_date, description, deleted) VALUES((SELECT id FROM users WHERE username = 'username3'), (SELECT id FROM universities WHERE abbreviation = 'ITBA'),  '2000-01-01', '2000-04-01', 'cool', FALSE)");
        
        USER1_ID = jdbcTemplate.queryForObject("SELECT id FROM users WHERE username = 'username'", Long.class);
        USER2_ID = jdbcTemplate.queryForObject("SELECT id FROM users WHERE username = 'username2'", Long.class);
        USER3_ID = jdbcTemplate.queryForObject("SELECT id FROM users WHERE username = 'username3'", Long.class);        
        JOURNEY1_ID = jdbcTemplate.queryForObject("SELECT id FROM journeys WHERE user_id = ?", Long.class, USER1_ID);
        RESPONSE1_ID = insert.executeAndReturnKey(Map.of("user_id", USER1_ID, "journey_id", JOURNEY1_ID, "message", RESPONSE_MESSAGE, "date_time", Timestamp.valueOf(RESPONSE_TIMESTAMP), "deleted", false)).longValue();
        RESPONSE2_ID = insert.executeAndReturnKey(Map.of("user_id", USER2_ID, "journey_id", JOURNEY1_ID, "message", RESPONSE_MESSAGE, "date_time", Timestamp.valueOf(RESPONSE_TIMESTAMP_2), "deleted", false)).longValue();
        RESPONSE3_ID = insert.executeAndReturnKey(Map.of("user_id", USER3_ID, "journey_id", JOURNEY1_ID, "message", RESPONSE_MESSAGE, "date_time", Timestamp.valueOf(RESPONSE_TIMESTAMP_3), "deleted", false)).longValue();
        RESPONSE_DELETED_ID = insert.executeAndReturnKey(Map.of("user_id", USER3_ID, "journey_id", JOURNEY1_ID, "message", RESPONSE_MESSAGE, "date_time", Timestamp.valueOf(RESPONSE_TIMESTAMP), "deleted", true)).longValue();
    }

    @Test
    public void testCreate(){
        JourneyResponse response = responseDao.create(USER1_ID, null, JOURNEY1_ID, RESPONSE_MESSAGE, RESPONSE_TIMESTAMP);

        assertNotNull(response);
        assertEquals(RESPONSE_TIMESTAMP, response.getDateTime());
        assertEquals(JOURNEY1_ID, response.getJourneyId());
        assertEquals(RESPONSE_MESSAGE, response.getMessage());
        assertEquals(USER1_ID, response.getUserId());
        assertNull(response.getUsername());
    }
    @Test(expected = DataAccessException.class)
    public void testCreateWrongUser(){
        responseDao.create(12341234, null, JOURNEY1_ID, RESPONSE_MESSAGE, RESPONSE_TIMESTAMP);
    }
    @Test(expected = DataAccessException.class)
    public void testCreateWrongJourney(){
        responseDao.create(USER1_ID, null, 12341234, RESPONSE_MESSAGE, RESPONSE_TIMESTAMP);
    }
    @Test(expected = DataAccessException.class)
    public void testCreateMissingDate(){
        responseDao.create(USER1_ID, null, JOURNEY1_ID, null, RESPONSE_TIMESTAMP);
    }

    @Test
    public void testListAllFromJourney(){
        List<JourneyResponse> responses = responseDao.listAllFromJourney(JOURNEY1_ID);

        assertNotNull(responses);
        assertEquals(TOTAL_RESPONSES, responses.size());
    }
    @Test
    public void testListAllFromJourneyNoResponses(){
        JdbcTestUtils.deleteFromTables(jdbcTemplate, RESPONSE_TABLE);

        List<JourneyResponse> responses = responseDao.listAllFromJourney(JOURNEY1_ID);

        assertNotNull(responses);
        assertEquals(0, responses.size());
    }

    @Test
    public void testListAllFromJourneyPaged(){
        Page<JourneyResponse> page1 = responseDao.listAllFromJourney(JOURNEY1_ID, 1, 2);
        Page<JourneyResponse> page2 = responseDao.listAllFromJourney(JOURNEY1_ID, 2, 2);

        //TODO awful
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
        assertEquals(RESPONSE1_ID, page1.getContent().get(0).getId());
        assertEquals(RESPONSE_TIMESTAMP, page1.getContent().get(0).getDateTime());
        assertEquals(JOURNEY1_ID, page1.getContent().get(0).getJourneyId());
        assertEquals(RESPONSE_MESSAGE, page1.getContent().get(0).getMessage());
        assertEquals(USER1_ID, page1.getContent().get(0).getUserId());
        assertEquals(USER1_NAME, page1.getContent().get(0).getUsername());
        assertEquals(RESPONSE2_ID, page1.getContent().get(1).getId());
        assertEquals(RESPONSE_TIMESTAMP_2, page1.getContent().get(1).getDateTime());
        assertEquals(JOURNEY1_ID, page1.getContent().get(1).getJourneyId());
        assertEquals(RESPONSE_MESSAGE, page1.getContent().get(1).getMessage());
        assertEquals(USER2_ID, page1.getContent().get(1).getUserId());
        assertEquals(USER2_NAME, page1.getContent().get(1).getUsername());
        assertEquals(RESPONSE3_ID, page2.getContent().get(0).getId());
        assertEquals(RESPONSE_TIMESTAMP_3, page2.getContent().get(0).getDateTime());
        assertEquals(JOURNEY1_ID, page2.getContent().get(0).getJourneyId());
        assertEquals(RESPONSE_MESSAGE, page2.getContent().get(0).getMessage());
        assertEquals(USER3_ID, page2.getContent().get(0).getUserId());
        assertEquals(USER3_NAME, page2.getContent().get(0).getUsername());
    }
    @Test
    public void testListAllFromJourneyPagedNoResponses(){
        JdbcTestUtils.deleteFromTableWhere(jdbcTemplate, RESPONSE_TABLE, "deleted = FALSE");

        Page<JourneyResponse> page1 = responseDao.listAllFromJourney(JOURNEY1_ID, 1, 2);

        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, RESPONSE_TABLE));
        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(0, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(0, page1.getContent().size());
    }

    @Test
    public void testGetJourneyIdByResponseId(){
        long id = responseDao.getJourneyIdByResponseId(RESPONSE1_ID);

        assertEquals(JOURNEY1_ID, id);
    }
    @Test(expected = NoSuchElementException.class)
    public void testGetJourneyIdByResponseIdWrongId(){
        responseDao.getJourneyIdByResponseId(12341234);
    }

    @Test
    public void testDelete(){
        int beforeRows = JdbcTestUtils.countRowsInTable(jdbcTemplate, RESPONSE_TABLE);

        responseDao.delete(RESPONSE1_ID);

        assertEquals(beforeRows, JdbcTestUtils.countRowsInTable(jdbcTemplate, RESPONSE_TABLE));
        assertEquals(TOTAL_RESPONSES - 1, jdbcTemplate.queryForObject("SELECT COUNT(*) FROM journey_responses WHERE deleted = FALSE", Integer.class).intValue());
    }
    @Test
    public void testDeleteDeleted(){
        int beforeRows = JdbcTestUtils.countRowsInTable(jdbcTemplate, RESPONSE_TABLE);

        responseDao.delete(RESPONSE_DELETED_ID);

        assertEquals(beforeRows, JdbcTestUtils.countRowsInTable(jdbcTemplate, RESPONSE_TABLE));
        assertEquals(TOTAL_RESPONSES, jdbcTemplate.queryForObject("SELECT COUNT(*) FROM journey_responses WHERE deleted = FALSE", Integer.class).intValue());
    }
    @Test
    public void testDeleteWrongId(){
        int beforeRows = JdbcTestUtils.countRowsInTable(jdbcTemplate, RESPONSE_TABLE);

        responseDao.delete(1235123);

        assertEquals(beforeRows, JdbcTestUtils.countRowsInTable(jdbcTemplate, RESPONSE_TABLE));
        assertEquals(TOTAL_RESPONSES, jdbcTemplate.queryForObject("SELECT COUNT(*) FROM journey_responses WHERE deleted = FALSE", Integer.class).intValue());
    }
    @Test
    public void testDeletionMessage(){
        int beforeRows = JdbcTestUtils.countRowsInTable(jdbcTemplate, RESPONSE_TABLE);

        responseDao.deletionMessage(RESPONSE1_ID, DELETION_MESSAGE);

        assertEquals(beforeRows, JdbcTestUtils.countRowsInTable(jdbcTemplate, RESPONSE_TABLE));
        assertEquals(TOTAL_RESPONSES, jdbcTemplate.queryForObject("SELECT COUNT(*) FROM journey_responses WHERE deleted = FALSE", Integer.class).intValue());
        assertEquals(DELETION_MESSAGE, jdbcTemplate.queryForObject("SELECT deleted_message FROM journey_responses WHERE id = ?", String.class, RESPONSE1_ID));
    }
    @Test
    public void testDeletionMessageDeleted(){
        int beforeRows = JdbcTestUtils.countRowsInTable(jdbcTemplate, RESPONSE_TABLE);

        responseDao.deletionMessage(RESPONSE_DELETED_ID, DELETION_MESSAGE);

        assertEquals(beforeRows, JdbcTestUtils.countRowsInTable(jdbcTemplate, RESPONSE_TABLE));
        assertEquals(TOTAL_RESPONSES, jdbcTemplate.queryForObject("SELECT COUNT(*) FROM journey_responses WHERE deleted = FALSE", Integer.class).intValue());
        assertEquals(DELETION_MESSAGE, jdbcTemplate.queryForObject("SELECT deleted_message FROM journey_responses WHERE id = ?", String.class, RESPONSE_DELETED_ID));
    }
    @Test
    public void testDeletionMessageWrongId(){
        int beforeRows = JdbcTestUtils.countRowsInTable(jdbcTemplate, RESPONSE_TABLE);

        responseDao.deletionMessage(12341234, DELETION_MESSAGE);

        assertEquals(beforeRows, JdbcTestUtils.countRowsInTable(jdbcTemplate, RESPONSE_TABLE));
        assertEquals(TOTAL_RESPONSES, jdbcTemplate.queryForObject("SELECT COUNT(*) FROM journey_responses WHERE deleted = FALSE", Integer.class).intValue());
    }

    @Test
    public void testDeleteByJourneyId(){
        int beforeRows = JdbcTestUtils.countRowsInTable(jdbcTemplate, RESPONSE_TABLE);

        responseDao.deleteResponsesByJourneyId(JOURNEY1_ID);

        assertEquals(beforeRows, JdbcTestUtils.countRowsInTable(jdbcTemplate, RESPONSE_TABLE));
        assertEquals(0, jdbcTemplate.queryForObject("SELECT COUNT(*) FROM journey_responses WHERE deleted = FALSE", Integer.class).intValue());
    }
    @Test
    public void testDeleteByJourneyIdWrongId(){
        int beforeRows = JdbcTestUtils.countRowsInTable(jdbcTemplate, RESPONSE_TABLE);

        responseDao.deleteResponsesByJourneyId(12341234);

        assertEquals(beforeRows, JdbcTestUtils.countRowsInTable(jdbcTemplate, RESPONSE_TABLE));
        assertEquals(TOTAL_RESPONSES, jdbcTemplate.queryForObject("SELECT COUNT(*) FROM journey_responses WHERE deleted = FALSE", Integer.class).intValue());
    }

    @Test
    public void testFindById(){
        Optional<JourneyResponse> maybeResponse = responseDao.findById(RESPONSE1_ID);

        assertNotNull(maybeResponse);
        assertTrue(maybeResponse.isPresent());
    }
    @Test
    public void testFindByIdMissing(){
        Optional<JourneyResponse> maybeResponse = responseDao.findById(12341234);

        assertNotNull(maybeResponse);
        assertFalse(maybeResponse.isPresent());
    }
    @Test
    public void testFindByIdDeleted(){
        Optional<JourneyResponse> maybeResponse = responseDao.findById(RESPONSE_DELETED_ID);

        assertNotNull(maybeResponse);
        assertFalse(maybeResponse.isPresent());
    }

    @Test
    public void testGetCount(){
        int count = responseDao.getJourneyResponseCount(JOURNEY1_ID);

        assertEquals(3, count);
    }
}
