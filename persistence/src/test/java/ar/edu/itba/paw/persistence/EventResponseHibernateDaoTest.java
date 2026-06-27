package ar.edu.itba.paw.persistence;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Map;
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
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.itba.paw.models.Event;
import ar.edu.itba.paw.models.EventResponse;
import ar.edu.itba.paw.models.Page;

import static org.junit.Assert.*;
import static ar.edu.itba.paw.persistence.TestUtils.*;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class EventResponseHibernateDaoTest {

    @Autowired
    private DataSource ds;

    @Autowired
    private EventResponseHibernateDao replyDao;

    @PersistenceContext
    private EntityManager em;

    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert insert;

    @Before
    public void setUp(){
        jdbcTemplate = new JdbcTemplate(ds);
        insert = new SimpleJdbcInsert(ds)
            .withTableName(EVENT_REPLY_TABLE)
            .usingGeneratedKeyColumns("id");
    }

    @Test
    public void testFindById(){
        Optional<EventResponse> response = replyDao.findById(EVENT_RESPONSE_1_ID);

        assertNotNull(response);
        assertTrue(response.isPresent());
        assertEqualsEventReply(EVENT_RESPONSE_1, response.get());
    }
    @Test
    public void testFindByIdNotFound(){
        Optional<EventResponse> response = replyDao.findById(12341234l);

        assertNotNull(response);
        assertFalse(response.isPresent());
    }

    @Test
    public void testCreate(){
        EventResponse reply = replyDao.create(
            USER_1, 
            EVENT_1, 
            RESPONSE_MESSAGE
        );
        em.flush();

        assertEqualsEventReply(
            new EventResponse(
                reply.getId(),
                USER_1,
                EVENT_1,
                RESPONSE_MESSAGE,
                LocalDateTime.now()
                ),
            reply
        );

        assertEquals(
            1,
            JdbcTestUtils.countRowsInTableWhere(
                jdbcTemplate, EVENT_REPLY_TABLE,
                "id = " + reply.getId() + " AND user_id = " + USER_1_ID + " AND event_id = " + EVENT_1_ID + " AND message = '" + RESPONSE_MESSAGE + "' AND deleted = FALSE"
            )
        );
    }
    @Test(expected = PersistenceException.class)
    public void testCreateNoMessage(){
        replyDao.create(USER_1, EVENT_1, null);
        em.flush();
    }
    @Test(expected = PersistenceException.class)
    public void testCreateWrongUser(){
        replyDao.create(
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
            EVENT_1, 
            RESPONSE_MESSAGE
        );
        em.flush();
    }
    @Test(expected = PersistenceException.class)
    public void testCreateWrongEvent(){
        replyDao.create(
            USER_1, 
            new Event(
                12341234l, 
                null, 
                null, 
                null, 
                0L,
                null, 
                null, 
                null, 
                null, 
                null), 
            RESPONSE_MESSAGE
        );
        em.flush();
    }

    @Test
    public void testCountByEventId(){
        int replyCount = replyDao.countByEventId(EVENT_1_ID);

        assertEquals(EVENT_1_REPLIES, replyCount);
    }
    @Test
    public void testCountByEventIdNoReplies(){
        deleteEventReplies(jdbcTemplate);

        int replyCount = replyDao.countByEventId(EVENT_1_ID);

        assertEquals(0, replyCount);
    }
    @Test
    public void testCountByEventIdWrongEvent(){
        int replyCount = replyDao.countByEventId(12341234);

        assertEquals(0, replyCount);
    }

    @Test
    public void testListAllByEventIdPage1(){
        Page<EventResponse> page1 = replyDao.listAllByEventId(
            EVENT_1_ID, 
            PAGE_1_DEFAULT
        );

        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(2, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(2, page1.getContent().size());
        page1.getContent().forEach((reply) ->
            assertEqualsEventReply(EVENT_RESPONSE_DATA.get(reply.getId()), reply)
        );
    }
    @Test
    public void testListAllByEventIdPage2(){
        Page<EventResponse> page2 = replyDao.listAllByEventId(
            EVENT_1_ID, 
            PAGE_2_DEFAULT
        );

        assertNotNull(page2);
        assertEquals(2, page2.getCurrentPage());
        assertEquals(2, page2.getTotalPages());
        assertNotNull(page2.getContent());
        assertEquals(1, page2.getContent().size());
        page2.getContent().forEach((reply) ->
            assertEqualsEventReply(EVENT_RESPONSE_DATA.get(reply.getId()), reply)
        );
    }
    @Test
    public void testListAllByEventIdPagedNoReplies(){
        deleteEventReplies(jdbcTemplate);
        insert.execute(Map.of("user_id", USER_1_ID, "event_id", EVENT_1_ID, "message", RESPONSE_MESSAGE, "date_time", Timestamp.valueOf(RESPONSE_TIMESTAMP), "deleted", true));

        Page<EventResponse> page1 = replyDao.listAllByEventId(
            EVENT_1_ID, 
            PAGE_1_BIG
        );

        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(0, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(0, page1.getContent().size());
    }
    @Test
    public void testListAllByEventPagedNoEventId(){
        Page<EventResponse> page1 = replyDao.listAllByEventId(
            1234234l, 
            PAGE_1_BIG
        );

        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(0, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(0, page1.getContent().size());
    }

    @Test
    public void testFindRespondersByEventId(){
        Page<User> repliers = replyDao.findRespondersByEventId(EVENT_1_ID, PAGE_1_BIG);

        assertNotNull(repliers);
        assertEquals(1, repliers.getCurrentPage());
        assertEquals(1, repliers.getTotalPages());
        assertEquals(EVENT_1_REPLIERS, repliers.getContent().size());

    }
}
