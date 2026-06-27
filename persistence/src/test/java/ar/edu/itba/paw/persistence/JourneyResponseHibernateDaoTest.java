package ar.edu.itba.paw.persistence;

import static org.junit.Assert.*;
import static ar.edu.itba.paw.persistence.TestUtils.*;

import java.time.LocalDateTime;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.sql.DataSource;

import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistence.config.TestConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.itba.paw.models.Journey;
import ar.edu.itba.paw.models.JourneyResponse;
import ar.edu.itba.paw.models.Page;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class JourneyResponseHibernateDaoTest {

    @Autowired
    private DataSource ds;

    @Autowired
    private JourneyResponseHibernateDao responseDao;

    @PersistenceContext
    private EntityManager em;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp(){
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Test
    public void testFindById(){
        Optional<JourneyResponse> maybeResponse = responseDao.findById(JOURNEY_RESPONSE_1_ID);

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
        Optional<JourneyResponse> maybeResponse = responseDao.findById(JOURNEY_RESPONSE_DELETED_ID);

        assertNotNull(maybeResponse);
        assertFalse(maybeResponse.isPresent());
    }

    @Test
    public void testCreate(){
        JourneyResponse response = responseDao.create(
            USER_1, JOURNEY_1, RESPONSE_MESSAGE
        );
        em.flush();

        assertEqualsJourneyReply(
            new JourneyResponse(
                response.getId(),
                USER_1,
                JOURNEY_1,
                RESPONSE_MESSAGE,
                LocalDateTime.now()
            ),
            response
        );

        assertEquals(
            TOTAL_JOURNEY_RESPONSES + 1,
            jdbcTemplate.queryForObject(JOURNEY_REPLY_COUNT_NOT_DELETED, Integer.class).intValue()
        );
        assertEquals(
            1,
            JdbcTestUtils.countRowsInTableWhere(
                jdbcTemplate, JOURNEY_REPLY_TABLE,
                "id = " + response.getId() + " AND user_id = " + USER_1_ID + " AND journey_id = " + JOURNEY_1_ID + " AND message = '" + RESPONSE_MESSAGE + "' AND deleted = FALSE"
            )
        );
    }
    @Test(expected = PersistenceException.class)
    public void testCreateWrongUser(){
        responseDao.create(
            new User(
                12341234l, 
                null, 
                null, 
                null, 
                null, 
                null, 
                null, 
                0l, 
                null, 
                false, 
                false), 
            JOURNEY_1, 
            RESPONSE_MESSAGE
        );
        em.flush();
    }
    @Test(expected = PersistenceException.class)
    public void testCreateWrongJourney(){
        responseDao.create(
            USER_1, 
            new Journey(
                12341234l, 
                null, 
                null, 
                null, 
                null, 
                null), 
            RESPONSE_MESSAGE
        );
        em.flush();
    }
    @Test(expected = PersistenceException.class)
    public void testCreateMissingDate(){
        responseDao.create(USER_1, JOURNEY_1, null);
        em.flush();
    }

    @Test
    public void testFindAllByJourneyIdPaged(){
        Page<JourneyResponse> page1 = responseDao.findAllByJourneyId(
            JOURNEY_1_ID, PAGE_1_BIG
        );

        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(1, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(TOTAL_JOURNEY_RESPONSES, page1.getContent().size());
        page1.getContent().forEach((r) -> 
            assertEqualsJourneyReply(RESPONSE_DATA.get(r.getId()), r)
        );
    }
    @Test
    public void testFindAllByJourneyIdPagedNoResponses(){
        JdbcTestUtils.deleteFromTableWhere(
            jdbcTemplate, JOURNEY_REPLY_TABLE, "deleted = FALSE"
        );

        Page<JourneyResponse> page1 = responseDao.findAllByJourneyId(
            JOURNEY_1_ID, PAGE_1_DEFAULT
        );

        assertEquals(
            1, 
            JdbcTestUtils.countRowsInTable(jdbcTemplate, JOURNEY_REPLY_TABLE)
        );
        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(0, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(0, page1.getContent().size());
    }

    @Test
    public void testHardDeleteByJourneyId(){
        responseDao.hardDeleteByJourneyId(JOURNEY_1_ID);
        em.flush();

        assertEquals(0, JdbcTestUtils.countRowsInTable(jdbcTemplate, JOURNEY_REPLY_TABLE));
    }
    @Test
    public void testHardDeleteByJourneyIdWrongId(){
        int beforeRows = JdbcTestUtils.countRowsInTable(jdbcTemplate, JOURNEY_REPLY_TABLE);

        responseDao.hardDeleteByJourneyId(12341234l);
        em.flush();

        assertEquals(beforeRows, JdbcTestUtils.countRowsInTable(jdbcTemplate, JOURNEY_REPLY_TABLE));
    }

    @Test
    public void testGetCount(){
        int count = responseDao.countByJourneyId(JOURNEY_1_ID);

        assertEquals(TOTAL_JOURNEY_RESPONSES, count);
    }

    @Test
    public void testFindRespondersByJourneyId(){
        Page<User> responders = responseDao.findRespondersByJourneyId(
            JOURNEY_1_ID, PAGE_1_BIG
        );

        assertNotNull(responders);
        assertEquals(1, responders.getCurrentPage());
        assertEquals(1, responders.getTotalPages());
        assertEquals(TOTAL_JOURNEY_RESPONDERS, responders.getContent().size());
    }
    @Test
    public void testFindRespondersByJourneyIdWrongId(){
        Page<User> responders = responseDao.findRespondersByJourneyId(
            12341234l, PAGE_1_BIG
        );

        assertNotNull(responders);
        assertEquals(1, responders.getCurrentPage());
        assertEquals(0, responders.getTotalPages());
        assertEquals(0, responders.getContent().size());
    }
}
