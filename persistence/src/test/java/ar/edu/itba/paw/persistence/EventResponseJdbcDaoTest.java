package ar.edu.itba.paw.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.sql.Timestamp;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import javax.sql.DataSource;

import ar.edu.itba.paw.models.PageParams;
import ar.edu.itba.paw.persistence.config.TestConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.itba.paw.models.EventResponse;
import ar.edu.itba.paw.models.Page;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class EventResponseJdbcDaoTest {

    @Autowired
    private DataSource ds;

    @Autowired
    private EventResponseJdbcDao replyDao;

    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert insert;

    @Before
    public void setUp(){
        jdbcTemplate = new JdbcTemplate(ds);
        insert = new SimpleJdbcInsert(ds).withTableName(TestUtils.EVENT_REPLY_TABLE).usingGeneratedKeyColumns("id");
    }
    

    @Test
    public void testFindById(){
        Optional<EventResponse> response = replyDao.findById(TestUtils.EVENT_RESPONSE_1_ID);

        assertNotNull(response);
        assertTrue(response.isPresent());
        TestUtils.assertEqualsEventReply(TestUtils.EVENT_RESPONSE_1, response.get());
    }
    @Test
    public void testFindByIdNotFound(){
        Optional<EventResponse> response = replyDao.findById(12341234l);

        assertNotNull(response);
        assertFalse(response.isPresent());
    }

    @Test
    public void testCreate(){
        EventResponse reply = replyDao.create(TestUtils.USER_1_ID, TestUtils.USER_1_NAME, TestUtils.EVENT_1_ID, TestUtils.RESPONSE_MESSAGE, TestUtils.RESPONSE_TIMESTAMP);

        TestUtils.assertEqualsEventReply(new EventResponse(reply.getId(), TestUtils.USER_1_ID, TestUtils.USER_1_NAME, TestUtils.EVENT_1_ID, TestUtils.RESPONSE_MESSAGE, TestUtils.RESPONSE_TIMESTAMP), reply);
    }
    @Test(expected = DataAccessException.class)
    public void testCreateNoMessage(){
        replyDao.create(TestUtils.USER_1_ID, TestUtils.USER_1_NAME, TestUtils.EVENT_1_ID, null, TestUtils.RESPONSE_TIMESTAMP);
    }
    @Test(expected = DataAccessException.class)
    public void testCreateWrongUser(){
        replyDao.create(12341234, null, TestUtils.EVENT_1_ID, TestUtils.RESPONSE_MESSAGE, TestUtils.RESPONSE_TIMESTAMP);
    }
    @Test(expected = DataAccessException.class)
    public void testCreateWrongEvent(){
        replyDao.create(TestUtils.USER_1_ID, null, 12341234, TestUtils.RESPONSE_MESSAGE, TestUtils.RESPONSE_TIMESTAMP);
    }
    @Test(expected = NullPointerException.class)
    public void testCreateNoTimestamp(){
        replyDao.create(TestUtils.USER_1_ID, null, TestUtils.EVENT_1_ID, TestUtils.RESPONSE_MESSAGE, null);
    }

    @Test
    public void testListAllByEventIdPage1(){
        Page<EventResponse> page1 = replyDao.listAllByEventId(TestUtils.EVENT_1_ID, new PageParams(1, 2));

        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(2, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(2, page1.getContent().size());
        for (EventResponse reply : page1.getContent()){
            TestUtils.assertEqualsEventReply(TestUtils.EVENT_RESPONSE_DATA.get(reply.getId()), reply);
        }
    }
    @Test
    public void testListAllByEventIdPage2(){
        Page<EventResponse> page2 = replyDao.listAllByEventId(TestUtils.EVENT_1_ID, new PageParams(2, 2));

        assertNotNull(page2);
        assertEquals(2, page2.getCurrentPage());
        assertEquals(2, page2.getTotalPages());
        assertNotNull(page2.getContent());
        assertEquals(1, page2.getContent().size());
        for (EventResponse reply : page2.getContent()){
            TestUtils.assertEqualsEventReply(TestUtils.EVENT_RESPONSE_DATA.get(reply.getId()), reply);
        }
    }
    @Test
    public void testListAllByEventIdPagedNoReplies(){
        TestUtils.deleteEventReplies(jdbcTemplate);
        insert.execute(Map.of("user_id", TestUtils.USER_1_ID, "event_id", TestUtils.EVENT_1_ID, "message", TestUtils.RESPONSE_MESSAGE, "date_time", Timestamp.valueOf(TestUtils.RESPONSE_TIMESTAMP), "deleted", true));

        Page<EventResponse> page1 = replyDao.listAllByEventId(TestUtils.EVENT_1_ID, new PageParams(1, 2));

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
        int replyCount = replyDao.countByEventId(TestUtils.EVENT_1_ID);

        assertEquals(TestUtils.EVENT_1_REPLIES, replyCount);
    }
    @Test
    public void testCountByEventIdNoReplies(){
        TestUtils.deleteEventReplies(jdbcTemplate);

        int replyCount = replyDao.countByEventId(TestUtils.EVENT_1_ID);

        assertEquals(0, replyCount);
    }
    @Test
    public void testCountByEventIdWrongEvent(){
        int replyCount = replyDao.countByEventId(12341234);

        assertEquals(0, replyCount);
    }

    @Test
    public void testFindEventIdById(){        
        long eventId = replyDao.findEventIdById(TestUtils.EVENT_RESPONSE_1_ID);

        assertEquals(TestUtils.EVENT_1_ID, eventId);
    }
    @Test(expected = NoSuchElementException.class)
    public void testFindEventIdByResponseIdNo(){
        replyDao.findEventIdById(12341234);
    }

    @Test
    public void testDelete(){
        int rowsBefore = JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.EVENT_REPLY_TABLE);

        replyDao.delete(TestUtils.EVENT_RESPONSE_1_ID);

        assertEquals(rowsBefore, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.EVENT_REPLY_TABLE));
        assertTrue(jdbcTemplate.queryForObject(TestUtils.EVENT_RESPONSE_IS_DELETED, Boolean.class, TestUtils.EVENT_RESPONSE_1_ID));
    }
    @Test
    public void testDeleteDeleted(){
        int rowsBefore = JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.EVENT_REPLY_TABLE);

        replyDao.delete(TestUtils.EVENT_RESPONSE_DELETED_ID);

        assertEquals(rowsBefore, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.EVENT_REPLY_TABLE));
        assertEquals(TestUtils.EVENT_RESPONSE_DELETED_ID, Optional.ofNullable(jdbcTemplate.queryForObject(TestUtils.EVENT_GET_DELETED_ID, Long.class)).get().longValue());
        assertTrue(jdbcTemplate.queryForObject("SELECT deleted FROM event_responses WHERE id = ?", Boolean.class, TestUtils.EVENT_RESPONSE_DELETED_ID));
    }
    @Test
    public void testDeleteWrongReply(){
        replyDao.delete(1241234);
    }

    @Test
    public void testUpdateDeletionMessage(){
        replyDao.updateDeletionMessage(TestUtils.EVENT_RESPONSE_1_ID, "TestUtils.RESPONSE_MESSAGE");

        assertEquals("TestUtils.RESPONSE_MESSAGE", jdbcTemplate.queryForObject(TestUtils.EVENT_RESPONSE_GET_DELETE_MESSAGE, String.class, TestUtils.EVENT_RESPONSE_1_ID));
    }
    @Test
    public void testUpdateDeletionMessageNoMessage(){
        replyDao.updateDeletionMessage(TestUtils.EVENT_RESPONSE_DELETED_ID, null);

        assertEquals(null, jdbcTemplate.queryForObject(TestUtils.EVENT_RESPONSE_GET_DELETE_MESSAGE, String.class, TestUtils.EVENT_RESPONSE_1_ID));
    }
    @Test
    public void testUpdateDeletionMessageWrongReply(){
        replyDao.updateDeletionMessage(123123, "TestUtils.RESPONSE_MESSAGE");
        
        assertEquals(null, jdbcTemplate.queryForObject(TestUtils.EVENT_RESPONSE_GET_DELETE_MESSAGE, String.class, TestUtils.EVENT_RESPONSE_1_ID));
        assertEquals(null, jdbcTemplate.queryForObject(TestUtils.EVENT_RESPONSE_GET_DELETE_MESSAGE, String.class, TestUtils.EVENT_RESPONSE_2_ID));
        assertEquals(null, jdbcTemplate.queryForObject(TestUtils.EVENT_RESPONSE_GET_DELETE_MESSAGE, String.class, TestUtils.EVENT_RESPONSE_3_ID));
        assertEquals(null, jdbcTemplate.queryForObject(TestUtils.EVENT_RESPONSE_GET_DELETE_MESSAGE, String.class, TestUtils.EVENT_RESPONSE_DELETED_ID));
    }

    @Test
    public void testDeleteAllByEventId(){
        replyDao.deleteAllByEventId(TestUtils.EVENT_1_ID);
        
        assertEquals(0, jdbcTemplate.query(TestUtils.EVENT_RESPONSE_SELECT_BY_ID_NOT_DELETED, TestUtils.EVENT_RESPONSE_ROW_MAPPER, TestUtils.EVENT_1_ID).size());
    }
    @Test
    public void testDeleteAllByEventIdNotFound(){
        replyDao.deleteAllByEventId(12341234);
        
        assertEquals(TestUtils.TOTAL_EVENT_REPLIES, jdbcTemplate.query(TestUtils.EVENT_RESPONSE_SELECT_BY_ID_NOT_DELETED, TestUtils.EVENT_RESPONSE_ROW_MAPPER, TestUtils.EVENT_1_ID).size());
    }

}
