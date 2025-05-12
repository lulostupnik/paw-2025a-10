package ar.edu.itba.paw.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.itba.paw.models.JourneyResponse;
import ar.edu.itba.paw.models.Page;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class JourneyResponseJdbcDaoTest {
    
    @Autowired
    private DataSource ds;

    @Autowired
    private JourneyResponseJdbcDao responseDao;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp(){
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Test
    public void testCreate(){
        JourneyResponse response = responseDao.create(TestUtils.USER_1_ID, TestUtils.USER_1.getUsername(), TestUtils.JOURNEY_1_ID, TestUtils.RESPONSE_MESSAGE, TestUtils.RESPONSE_TIMESTAMP);

        TestUtils.assertEqualsJourneyReply(
            new JourneyResponse(response.getId(), TestUtils.USER_1_ID, TestUtils.USER_1.getUsername(), TestUtils.JOURNEY_1_ID, TestUtils.RESPONSE_MESSAGE, TestUtils.RESPONSE_TIMESTAMP), 
            response
        );
    }
    @Test(expected = DataAccessException.class)
    public void testCreateWrongUser(){
        responseDao.create(12341234, null, TestUtils.JOURNEY_1_ID, TestUtils.RESPONSE_MESSAGE, TestUtils.RESPONSE_TIMESTAMP);
    }
    @Test(expected = DataAccessException.class)
    public void testCreateWrongJourney(){
        responseDao.create(TestUtils.USER_1_ID, null, 12341234l, TestUtils.RESPONSE_MESSAGE, TestUtils.RESPONSE_TIMESTAMP);
    }
    @Test(expected = DataAccessException.class)
    public void testCreateMissingDate(){
        responseDao.create(TestUtils.USER_1_ID, null, TestUtils.JOURNEY_1_ID, null, TestUtils.RESPONSE_TIMESTAMP);
    }

    @Test
    public void testFindAllByJourneyIdPaged(){
        Page<JourneyResponse> page1 = responseDao.findAllByJourneyId(TestUtils.JOURNEY_1_ID, TestUtils.PAGE_1_BIG);

        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(1, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(TestUtils.TOTAL_JOURNEY_RESPONSES, page1.getContent().size());
        for (JourneyResponse r : page1.getContent()){
            TestUtils.assertEqualsJourneyReply(TestUtils.RESPONSE_DATA.get(r.getId()), r);
        }
    }
    @Test
    public void testFindAllByJourneyIdPagedNoResponses(){
        JdbcTestUtils.deleteFromTableWhere(jdbcTemplate, TestUtils.JOURNEY_REPLY_TABLE, "deleted = FALSE");

        Page<JourneyResponse> page1 = responseDao.findAllByJourneyId(TestUtils.JOURNEY_1_ID, new PageParams(1, 2));

        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.JOURNEY_REPLY_TABLE));
        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(0, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(0, page1.getContent().size());
    }

    @Test
    public void testFindJourneyIdByResponseId(){
        long id = responseDao.findJourneyIdByResponseId(TestUtils.JOURNEY_RESPONSE_1_ID);

        assertEquals(TestUtils.JOURNEY_1_ID, id);
    }
    @Test(expected = NoSuchElementException.class)
    public void testFindJourneyIdByResponseIdWrongId(){
        responseDao.findJourneyIdByResponseId(12341234);
    }

    @Test
    public void testDelete(){
        int beforeRows = JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.JOURNEY_REPLY_TABLE);

        responseDao.delete(TestUtils.JOURNEY_RESPONSE_1_ID);

        assertEquals(beforeRows, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.JOURNEY_REPLY_TABLE));
        assertEquals(
            TestUtils.TOTAL_JOURNEY_RESPONSES - 1, 
            Optional.ofNullable(jdbcTemplate.queryForObject(TestUtils.JOURNEY_REPLY_COUNT_NOT_DELETED, Integer.class)).get().intValue()
        );
        assertTrue(
            jdbcTemplate.queryForObject(TestUtils.JOURNEY_REPLY_IS_DELETED_BY_ID, Boolean.class, TestUtils.JOURNEY_RESPONSE_1_ID)
        );
    }
    @Test
    public void testDeleteDeleted(){
        int beforeRows = JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.JOURNEY_REPLY_TABLE);

        responseDao.delete(TestUtils.JOURNEY_RESPONSE_DELETED_ID);

        assertEquals(beforeRows, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.JOURNEY_REPLY_TABLE));
        assertEquals(
            TestUtils.TOTAL_JOURNEY_RESPONSES, 
            Optional.ofNullable(jdbcTemplate.queryForObject(TestUtils.JOURNEY_REPLY_COUNT_NOT_DELETED, Integer.class)).get().intValue()
        );
    }
    @Test
    public void testDeleteWrongId(){
        int beforeRows = JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.JOURNEY_REPLY_TABLE);

        responseDao.delete(1235123);

        assertEquals(beforeRows, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.JOURNEY_REPLY_TABLE));
        assertEquals(
            TestUtils.TOTAL_JOURNEY_RESPONSES, 
            Optional.ofNullable(jdbcTemplate.queryForObject(TestUtils.JOURNEY_REPLY_COUNT_NOT_DELETED, Integer.class)).get().intValue()
        );
    }
    @Test
    public void testUpdateDeletionMessage(){
        int beforeRows = JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.JOURNEY_REPLY_TABLE);

        responseDao.updateDeletionMessage(TestUtils.JOURNEY_RESPONSE_1_ID, TestUtils.MESSAGE_DEFAULT);

        assertEquals(beforeRows, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.JOURNEY_REPLY_TABLE));
        assertEquals(
            TestUtils.TOTAL_JOURNEY_RESPONSES, 
            Optional.ofNullable(jdbcTemplate.queryForObject(TestUtils.JOURNEY_REPLY_COUNT_NOT_DELETED, Integer.class)).get().intValue()
        );
        assertEquals(
            TestUtils.MESSAGE_DEFAULT, 
            jdbcTemplate.queryForObject(TestUtils.JOURNEY_REPLY_DELETED_MESSAGE_BY_ID, String.class, TestUtils.JOURNEY_RESPONSE_1_ID)
        );
    }
    @Test
    public void testUpdateDeletionMessageDeleted(){
        int beforeRows = JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.JOURNEY_REPLY_TABLE);

        responseDao.updateDeletionMessage(TestUtils.JOURNEY_RESPONSE_DELETED_ID, TestUtils.MESSAGE_DEFAULT);

        assertEquals(beforeRows, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.JOURNEY_REPLY_TABLE));
        assertEquals(
            TestUtils.TOTAL_JOURNEY_RESPONSES, 
            Optional.ofNullable(jdbcTemplate.queryForObject(TestUtils.JOURNEY_REPLY_COUNT_NOT_DELETED, Integer.class)).get().intValue()
        );
        assertEquals(
            TestUtils.MESSAGE_DEFAULT, 
            jdbcTemplate.queryForObject(TestUtils.JOURNEY_REPLY_DELETED_MESSAGE_BY_ID, String.class, TestUtils.JOURNEY_RESPONSE_DELETED_ID)
        );
    }
    @Test
    public void testUpdateDeletionMessageWrongId(){
        int beforeRows = JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.JOURNEY_REPLY_TABLE);

        responseDao.updateDeletionMessage(12341234, TestUtils.MESSAGE_DEFAULT);

        assertEquals(beforeRows, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.JOURNEY_REPLY_TABLE));
        assertEquals(
            TestUtils.TOTAL_JOURNEY_RESPONSES, 
            Optional.ofNullable(jdbcTemplate.queryForObject(TestUtils.JOURNEY_REPLY_COUNT_NOT_DELETED, Integer.class)).get().intValue()
        );
    }

    @Test
    public void testDeleteByJourneyId(){
        int beforeRows = JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.JOURNEY_REPLY_TABLE);

        responseDao.deleteByJourneyId(TestUtils.JOURNEY_1_ID);

        assertEquals(beforeRows, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.JOURNEY_REPLY_TABLE));
        assertEquals(
            0, 
            Optional.ofNullable(jdbcTemplate.queryForObject(TestUtils.JOURNEY_REPLY_COUNT_NOT_DELETED, Integer.class)).get().intValue()
        );
    }
    @Test
    public void testDeleteByJourneyIdWrongId(){
        int beforeRows = JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.JOURNEY_REPLY_TABLE);

        responseDao.deleteByJourneyId(12341234);

        assertEquals(beforeRows, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.JOURNEY_REPLY_TABLE));
        assertEquals(
            TestUtils.TOTAL_JOURNEY_RESPONSES, 
            Optional.ofNullable(jdbcTemplate.queryForObject(TestUtils.JOURNEY_REPLY_COUNT_NOT_DELETED, Integer.class)).get().intValue()
        );
    }

    @Test
    public void testFindById(){
        Optional<JourneyResponse> maybeResponse = responseDao.findById(TestUtils.JOURNEY_RESPONSE_1_ID);

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
        Optional<JourneyResponse> maybeResponse = responseDao.findById(TestUtils.JOURNEY_RESPONSE_DELETED_ID);

        assertNotNull(maybeResponse);
        assertFalse(maybeResponse.isPresent());
    }

    @Test
    public void testGetCount(){
        int count = responseDao.countByJourneyId(TestUtils.JOURNEY_1_ID);

        assertEquals(TestUtils.TOTAL_JOURNEY_RESPONSES, count);
    }
}
