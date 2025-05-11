package ar.edu.itba.paw.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.sql.DataSource;

import ar.edu.itba.paw.models.PageParams;
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

import ar.edu.itba.paw.models.EventResponse;
import ar.edu.itba.paw.models.Page;
import ar.edu.itba.paw.models.User;

@SuppressWarnings("null")
@Transactional
@Rollback
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class EventResponseJdbcDaoTest {

    private static final String REPLY_TABLE = "event_responses";
    private static final String REPLY_MESSAGE = "message";
    private static final LocalDateTime REPLY_TIMESTAMP = LocalDateTime.now().withNano(0);

    @Autowired
    private DataSource ds;

    @Autowired
    private EventResponseJdbcDao replyDao;

    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert insert;

    private static User USER_1;
    private static long EVENT1_ID;

    @Before
    public void setUp(){
        jdbcTemplate = new JdbcTemplate(ds);
        insert = new SimpleJdbcInsert(ds).withTableName(REPLY_TABLE).usingGeneratedKeyColumns("id");
        jdbcTemplate.execute("INSERT INTO events(user_id, city_id, event_date, attendees_limit, title, deleted) VALUES((SELECT id FROM users WHERE username = 'user1'), (SELECT id FROM cities LIMIT 1), CURRENT_DATE, 2, '1', FALSE)");

        USER_1 = jdbcTemplate.queryForObject(TestUtils.USER_SELECT_BY_EMAIL, TestUtils.USER_ROW_MAPPER, TestUtils.USER_1_MAIL);
        EVENT1_ID = jdbcTemplate.queryForObject("SELECT id FROM events WHERE title = '1'", Long.class);
    }

    @Test
    public void testCreate(){
        EventResponse reply = replyDao.create(USER_1.getId(), USER_1.getUsername(), EVENT1_ID, REPLY_MESSAGE, REPLY_TIMESTAMP);

        assertNotNull(reply);
        assertEquals(REPLY_TIMESTAMP, reply.getDateTime());
        assertEquals(EVENT1_ID, reply.getEventId());
        assertEquals(REPLY_MESSAGE, reply.getMessage());
        assertEquals(USER_1.getId(), reply.getUserId());
        assertEquals(USER_1.getUsername(), reply.getUsername());
    }
    @Test(expected = DataAccessException.class)
    public void testCreateNoMessage(){
        replyDao.create(USER_1.getId(), USER_1.getUsername(), EVENT1_ID, null, REPLY_TIMESTAMP);
    }
    @Test(expected = DataAccessException.class)
    public void testCreateWrongUser(){
        replyDao.create(12341234, null, EVENT1_ID, REPLY_MESSAGE, REPLY_TIMESTAMP);
    }
    @Test(expected = DataAccessException.class)
    public void testCreateWrongEvent(){
        replyDao.create(USER_1.getId(), null, 12341234, REPLY_MESSAGE, REPLY_TIMESTAMP);
    }
    @Test(expected = NullPointerException.class)
    public void testCreateNoTimestamp(){
        replyDao.create(USER_1.getId(), null, EVENT1_ID, REPLY_MESSAGE, null);
    }

    @Test
    public void testListAllByEventIdPaged(){
        insert.execute(Map.of("user_id", USER_1.getId(), "event_id", EVENT1_ID, "message", REPLY_MESSAGE, "date_time", Timestamp.valueOf(REPLY_TIMESTAMP), "deleted", false));
        insert.execute(Map.of("user_id", USER_1.getId(), "event_id", EVENT1_ID, "message", REPLY_MESSAGE, "date_time", Timestamp.valueOf(REPLY_TIMESTAMP), "deleted", false));
        insert.execute(Map.of("user_id", USER_1.getId(), "event_id", EVENT1_ID, "message", REPLY_MESSAGE, "date_time", Timestamp.valueOf(REPLY_TIMESTAMP), "deleted", false));
        insert.execute(Map.of("user_id", USER_1.getId(), "event_id", EVENT1_ID, "message", REPLY_MESSAGE, "date_time", Timestamp.valueOf(REPLY_TIMESTAMP), "deleted", true));

        Page<EventResponse> page1 = replyDao.listAllByEventId(EVENT1_ID, new PageParams(1, 2));
        Page<EventResponse> page2 = replyDao.listAllByEventId(EVENT1_ID, new PageParams(2, 2));

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
        List<EventResponse> replies = new ArrayList<>(page1.getContent());
        replies.addAll(page2.getContent());
        assertEquals(3, replies.size());
        for (EventResponse reply : replies){
            assertNotNull(reply);
            assertEquals(REPLY_TIMESTAMP, reply.getDateTime());
            assertEquals(EVENT1_ID, reply.getEventId());
            assertEquals(REPLY_MESSAGE, reply.getMessage());
            assertEquals(USER_1.getId(), reply.getUserId());
            assertEquals(USER_1.getUsername(), reply.getUsername());
        }
    }
    @Test
    public void testListAllByEventIdPagedNoReplies(){
        insert.execute(Map.of("user_id", USER_1.getId(), "event_id", EVENT1_ID, "message", REPLY_MESSAGE, "date_time", Timestamp.valueOf(REPLY_TIMESTAMP), "deleted", true));

        Page<EventResponse> page1 = replyDao.listAllByEventId(EVENT1_ID, new PageParams(1, 2));

        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(0, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(0, page1.getContent().size());
    }
    @Test
    public void testListAllByEventPagedNoEventId(){
        Page<EventResponse> page1 = replyDao.listAllByEventId(1234234, new PageParams(1, 2));

        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(0, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(0, page1.getContent().size());
    }

    @Test
    public void testCountByEventId(){
        insert.execute(Map.of("user_id", USER_1.getId(), "event_id", EVENT1_ID, "message", REPLY_MESSAGE, "date_time", Timestamp.valueOf(REPLY_TIMESTAMP), "deleted", false));
        insert.execute(Map.of("user_id", USER_1.getId(), "event_id", EVENT1_ID, "message", REPLY_MESSAGE, "date_time", Timestamp.valueOf(REPLY_TIMESTAMP), "deleted", false));
        insert.execute(Map.of("user_id", USER_1.getId(), "event_id", EVENT1_ID, "message", REPLY_MESSAGE, "date_time", Timestamp.valueOf(REPLY_TIMESTAMP), "deleted", false));
        insert.execute(Map.of("user_id", USER_1.getId(), "event_id", EVENT1_ID, "message", REPLY_MESSAGE, "date_time", Timestamp.valueOf(REPLY_TIMESTAMP), "deleted", true));
        
        int replyCount = replyDao.countByEventId(EVENT1_ID);

        assertEquals(3, replyCount);
    }
    @Test
    public void testCountByEventIdNoReplies(){
        int replyCount = replyDao.countByEventId(EVENT1_ID);

        assertEquals(0, replyCount);
    }
    @Test
    public void testCountByEventIdWrongEvent(){
        int replyCount = replyDao.countByEventId(12341234);

        assertEquals(0, replyCount);
    }

    @Test
    public void testFindEventIdById(){
        long replyId = insert.executeAndReturnKey(Map.of("user_id", USER_1.getId(), "event_id", EVENT1_ID, "message", REPLY_MESSAGE, "date_time", Timestamp.valueOf(REPLY_TIMESTAMP), "deleted", false)).longValue();
        
        long eventId = replyDao.findEventIdById(replyId);

        assertEquals(EVENT1_ID, eventId);
    }
    @Test(expected = NoSuchElementException.class)
    public void testFindEventIdByResponseIdNo(){
        replyDao.findEventIdById(12341234);
    }

    @Test
    public void testDelete(){
        long replyId1 = insert.executeAndReturnKey(Map.of("user_id", USER_1.getId(), "event_id", EVENT1_ID, "message", REPLY_MESSAGE, "date_time", Timestamp.valueOf(REPLY_TIMESTAMP), "deleted", false)).longValue();
        long replyId2 = insert.executeAndReturnKey(Map.of("user_id", USER_1.getId(), "event_id", EVENT1_ID, "message", REPLY_MESSAGE, "date_time", Timestamp.valueOf(REPLY_TIMESTAMP), "deleted", false)).longValue();
        int rowsBefore = JdbcTestUtils.countRowsInTable(jdbcTemplate, REPLY_TABLE);

        replyDao.delete(replyId1);

        assertEquals(rowsBefore, JdbcTestUtils.countRowsInTable(jdbcTemplate, REPLY_TABLE));
        assertEquals(replyId1, jdbcTemplate.queryForObject("SELECT id FROM event_responses WHERE deleted = TRUE", Long.class).longValue());
        assertEquals(replyId2, jdbcTemplate.queryForObject("SELECT id FROM event_responses WHERE deleted = FALSE", Long.class).longValue());
    }
    @Test
    public void testDeleteDeleted(){
        long replyId1 = insert.executeAndReturnKey(Map.of("user_id", USER_1.getId(), "event_id", EVENT1_ID, "message", REPLY_MESSAGE, "date_time", Timestamp.valueOf(REPLY_TIMESTAMP), "deleted", true)).longValue();
        long replyId2 = insert.executeAndReturnKey(Map.of("user_id", USER_1.getId(), "event_id", EVENT1_ID, "message", REPLY_MESSAGE, "date_time", Timestamp.valueOf(REPLY_TIMESTAMP), "deleted", false)).longValue();
        int rowsBefore = JdbcTestUtils.countRowsInTable(jdbcTemplate, REPLY_TABLE);

        replyDao.delete(replyId1);

        assertEquals(rowsBefore, JdbcTestUtils.countRowsInTable(jdbcTemplate, REPLY_TABLE));
        assertEquals(replyId1, jdbcTemplate.queryForObject("SELECT id FROM event_responses WHERE deleted = TRUE", Long.class).longValue());
        assertEquals(replyId2, jdbcTemplate.queryForObject("SELECT id FROM event_responses WHERE deleted = FALSE", Long.class).longValue());
    }
    @Test
    public void testDeleteWrongReply(){
        replyDao.delete(1241234);
    }

    @Test
    public void testUpdateDeletionMessage(){
        long replyId1 = insert.executeAndReturnKey(Map.of("user_id", USER_1.getId(), "event_id", EVENT1_ID, "message", REPLY_MESSAGE, "date_time", Timestamp.valueOf(REPLY_TIMESTAMP), "deleted", true)).longValue();
        insert.execute(Map.of("user_id", USER_1.getId(), "event_id", EVENT1_ID, "message", REPLY_MESSAGE, "date_time", Timestamp.valueOf(REPLY_TIMESTAMP), "deleted", false));

        replyDao.updateDeletionMessage(replyId1, "REPLY_MESSAGE");

        assertEquals("REPLY_MESSAGE", jdbcTemplate.queryForObject("SELECT deleted_message FROM event_responses WHERE id = ?", String.class, replyId1));
    }
    @Test
    public void testUpdateDeletionMessageNoMessage(){
        long replyId1 = insert.executeAndReturnKey(Map.of("user_id", USER_1.getId(), "event_id", EVENT1_ID, "message", REPLY_MESSAGE, "date_time", Timestamp.valueOf(REPLY_TIMESTAMP), "deleted", true)).longValue();
        insert.execute(Map.of("user_id", USER_1.getId(), "event_id", EVENT1_ID, "message", REPLY_MESSAGE, "date_time", Timestamp.valueOf(REPLY_TIMESTAMP), "deleted", false));

        replyDao.updateDeletionMessage(replyId1, null);

        assertEquals(null, jdbcTemplate.queryForObject("SELECT deleted_message FROM event_responses WHERE id = ?", String.class, replyId1));
    }
    @Test
    public void testUpdateDeletionMessageWrongReply(){
        insert.execute(Map.of("user_id", USER_1.getId(), "event_id", EVENT1_ID, "message", REPLY_MESSAGE, "date_time", Timestamp.valueOf(REPLY_TIMESTAMP), "deleted", true));
        insert.execute(Map.of("user_id", USER_1.getId(), "event_id", EVENT1_ID, "message", REPLY_MESSAGE, "date_time", Timestamp.valueOf(REPLY_TIMESTAMP), "deleted", false));

        replyDao.updateDeletionMessage(123123, "REPLY_MESSAGE");
        //TODO asserts
    }

    @Test
    public void testDeleteAllByEventId(){
        insert.execute(Map.of("user_id", USER_1.getId(), "event_id", EVENT1_ID, "message", REPLY_MESSAGE, "date_time", Timestamp.valueOf(REPLY_TIMESTAMP), "deleted", true));
        insert.execute(Map.of("user_id", USER_1.getId(), "event_id", EVENT1_ID, "message", REPLY_MESSAGE, "date_time", Timestamp.valueOf(REPLY_TIMESTAMP), "deleted", false));

        replyDao.deleteAllByEventId(EVENT1_ID);
        
        assertEquals(0, jdbcTemplate.query("SELECT * from event_responses WHERE deleted = FALSE AND event_id = ?", (rs, rowNum) -> 1, EVENT1_ID).size());
    }
    @Test
    public void testDeleteAllByEventIdNotFound(){
        replyDao.deleteAllByEventId(12341234);
        
        assertEquals(0, jdbcTemplate.query("SELECT * from event_responses WHERE deleted = FALSE AND event_id = ?", (rs, rowNum) -> 1, EVENT1_ID).size());
    }

}
