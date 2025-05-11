package ar.edu.itba.paw.persistance;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import javax.sql.DataSource;

import ar.edu.itba.paw.models.PageParams;
import ar.edu.itba.paw.models.User;

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

import ar.edu.itba.paw.models.Journey;
import ar.edu.itba.paw.models.JourneyResponse;
import ar.edu.itba.paw.models.Page;
import ar.edu.itba.paw.persistence.JourneyResponseJdbcDao;

@Transactional
@Rollback
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class JourneyResponseJdbcDaoTest {

    private static User USER_1;
    private static User USER_2;
    private static User USER_3;
    private static Journey JOURNEY_1;
    private static JourneyResponse REPLY_1;
    private static JourneyResponse REPLY_2;
    private static JourneyResponse REPLY_3;
    private static JourneyResponse REPLY_DELETED;
    private static Map<Long, JourneyResponse> responseData;
    
    @Autowired
    private DataSource ds;

    @Autowired
    private JourneyResponseJdbcDao responseDao;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp(){
        jdbcTemplate = new JdbcTemplate(ds);
        
        USER_1 = jdbcTemplate.queryForObject(TestUtils.USER_SELECT_BY_EMAIL, TestUtils.USER_ROW_MAPPER, TestUtils.USER_1_MAIL);
        USER_2 = jdbcTemplate.queryForObject(TestUtils.USER_SELECT_BY_EMAIL, TestUtils.USER_ROW_MAPPER, TestUtils.USER_2_MAIL);
        USER_3 = jdbcTemplate.queryForObject(TestUtils.USER_SELECT_BY_EMAIL, TestUtils.USER_ROW_MAPPER, TestUtils.USER_3_MAIL);
        JOURNEY_1 = jdbcTemplate.queryForObject(TestUtils.JOURNEY_SELECT_BY_USERMAIL, TestUtils.JOURNEY_ROW_MAPPER, TestUtils.USER_1_MAIL);
        REPLY_1 = jdbcTemplate.queryForObject(TestUtils.JOURNEY_REPLY_SELECT_BY_USER_JOURNEY, TestUtils.JOURNEY_REPLY_ROW_MAPPER, USER_1.getId(), JOURNEY_1.getId());
        REPLY_2 = jdbcTemplate.queryForObject(TestUtils.JOURNEY_REPLY_SELECT_BY_USER_JOURNEY, TestUtils.JOURNEY_REPLY_ROW_MAPPER, USER_2.getId(), JOURNEY_1.getId());
        REPLY_3 = jdbcTemplate.queryForObject(TestUtils.JOURNEY_REPLY_SELECT_BY_USER_JOURNEY + "AND deleted = FALSE", TestUtils.JOURNEY_REPLY_ROW_MAPPER, USER_3.getId(), JOURNEY_1.getId());
        REPLY_DELETED = jdbcTemplate.queryForObject(TestUtils.JOURNEY_REPLY_SELECT_BY_USER_JOURNEY + "AND deleted = TRUE", TestUtils.JOURNEY_REPLY_ROW_MAPPER, USER_3.getId(), JOURNEY_1.getId());
        responseData = Map.of(REPLY_1.getId(), REPLY_1, REPLY_2.getId(), REPLY_2, REPLY_3.getId(), REPLY_3);
    }

    @Test
    public void testCreate(){
        JourneyResponse response = responseDao.create(USER_1.getId(), USER_1.getUsername(), JOURNEY_1.getId(), TestUtils.RESPONSE_MESSAGE, TestUtils.RESPONSE_TIMESTAMP);

        TestUtils.assertEqualsJourneyReply(
            new JourneyResponse(response.getId(), USER_1.getId(), USER_1.getUsername(), JOURNEY_1.getId(), TestUtils.RESPONSE_MESSAGE, TestUtils.RESPONSE_TIMESTAMP), 
            response
        );
    }
    @Test(expected = DataAccessException.class)
    public void testCreateWrongUser(){
        responseDao.create(12341234, null, JOURNEY_1.getId(), TestUtils.RESPONSE_MESSAGE, TestUtils.RESPONSE_TIMESTAMP);
    }
    @Test(expected = DataAccessException.class)
    public void testCreateWrongJourney(){
        responseDao.create(USER_1.getId(), null, 12341234l, TestUtils.RESPONSE_MESSAGE, TestUtils.RESPONSE_TIMESTAMP);
    }
    @Test(expected = DataAccessException.class)
    public void testCreateMissingDate(){
        responseDao.create(USER_1.getId(), null, JOURNEY_1.getId(), null, TestUtils.RESPONSE_TIMESTAMP);
    }

    @Test
    public void testListAllByJourneyIdPaged(){
        Page<JourneyResponse> page1 = responseDao.listAllByJourneyId(JOURNEY_1.getId(), TestUtils.PAGE_1_BIG);

        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(1, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(TestUtils.TOTAL_JOURNEY_RESPONSES, page1.getContent().size());
        for (JourneyResponse r : page1.getContent()){
            TestUtils.assertEqualsJourneyReply(responseData.get(r.getId()), r);
        }
    }
    @Test
    public void testListAllByJourneyIdPagedNoResponses(){
        JdbcTestUtils.deleteFromTableWhere(jdbcTemplate, TestUtils.JOURNEY_REPLY_TABLE, "deleted = FALSE");

        Page<JourneyResponse> page1 = responseDao.listAllByJourneyId(JOURNEY_1.getId(), new PageParams(1, 2));

        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.JOURNEY_REPLY_TABLE));
        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(0, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(0, page1.getContent().size());
    }

    @Test
    public void testFindJourneyIdByResponseId(){
        long id = responseDao.findJourneyIdByResponseId(REPLY_1.getId());

        assertEquals(JOURNEY_1.getId(), id);
    }
    @Test(expected = NoSuchElementException.class)
    public void testFindJourneyIdByResponseIdWrongId(){
        responseDao.findJourneyIdByResponseId(12341234);
    }

    @Test
    public void testDelete(){
        int beforeRows = JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.JOURNEY_REPLY_TABLE);

        responseDao.delete(REPLY_1.getId());

        assertEquals(beforeRows, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.JOURNEY_REPLY_TABLE));
        assertEquals(
            TestUtils.TOTAL_JOURNEY_RESPONSES - 1, 
            Optional.ofNullable(jdbcTemplate.queryForObject(TestUtils.JOURNEY_REPLY_COUNT_NOT_DELETED, Integer.class)).get().intValue()
        );
        assertTrue(
            jdbcTemplate.queryForObject(TestUtils.JOURNEY_REPLY_IS_DELETED_BY_ID, Boolean.class, REPLY_1.getId())
        );
    }
    @Test
    public void testDeleteDeleted(){
        int beforeRows = JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.JOURNEY_REPLY_TABLE);

        responseDao.delete(REPLY_DELETED.getId());

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

        responseDao.updateDeletionMessage(REPLY_1.getId(), TestUtils.MESSAGE_DEFAULT);

        assertEquals(beforeRows, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.JOURNEY_REPLY_TABLE));
        assertEquals(
            TestUtils.TOTAL_JOURNEY_RESPONSES, 
            Optional.ofNullable(jdbcTemplate.queryForObject(TestUtils.JOURNEY_REPLY_COUNT_NOT_DELETED, Integer.class)).get().intValue()
        );
        assertEquals(
            TestUtils.MESSAGE_DEFAULT, 
            jdbcTemplate.queryForObject(TestUtils.JOURNEY_REPLY_DELETED_MESSAGE_BY_ID, String.class, REPLY_1.getId())
        );
    }
    @Test
    public void testUpdateDeletionMessageDeleted(){
        int beforeRows = JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.JOURNEY_REPLY_TABLE);

        responseDao.updateDeletionMessage(REPLY_DELETED.getId(), TestUtils.MESSAGE_DEFAULT);

        assertEquals(beforeRows, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.JOURNEY_REPLY_TABLE));
        assertEquals(
            TestUtils.TOTAL_JOURNEY_RESPONSES, 
            Optional.ofNullable(jdbcTemplate.queryForObject(TestUtils.JOURNEY_REPLY_COUNT_NOT_DELETED, Integer.class)).get().intValue()
        );
        assertEquals(
            TestUtils.MESSAGE_DEFAULT, 
            jdbcTemplate.queryForObject(TestUtils.JOURNEY_REPLY_DELETED_MESSAGE_BY_ID, String.class, REPLY_DELETED.getId())
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

        responseDao.deleteByJourneyId(JOURNEY_1.getId());

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
        Optional<JourneyResponse> maybeResponse = responseDao.findById(REPLY_1.getId());

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
        Optional<JourneyResponse> maybeResponse = responseDao.findById(REPLY_DELETED.getId());

        assertNotNull(maybeResponse);
        assertFalse(maybeResponse.isPresent());
    }

    @Test
    public void testGetCount(){
        int count = responseDao.countByJourneyId(JOURNEY_1.getId());

        assertEquals(TestUtils.TOTAL_JOURNEY_RESPONSES, count);
    }
}
