package ar.edu.itba.paw.persistence;

import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.sql.DataSource;

import ar.edu.itba.paw.models.*;
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

import static org.junit.Assert.*;
import static ar.edu.itba.paw.persistence.TestUtils.*;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class TokenHibernateDaoTest {

    @Autowired
    private DataSource ds;

    @Autowired
    private TokenHibernateDao tokenDao;
        
    @PersistenceContext
    private EntityManager em;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp(){
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Test
    public void testCreateToken(){
        Token token = tokenDao.create(
            USER_I3, 
            TOKEN_NEW_VALUE, 
            TOKEN_NEW_EXPIRATION
        );
        em.flush();

        assertEquals(TOKEN_NEW_EXPIRATION, token.getExpirationDate());
        assertEquals(TOKEN_NEW_VALUE, token.getToken());
        assertEqualsUser(USER_I3, token.getUser());
        assertTrue(token.getId() > 1);

        assertEquals(
            TOTAL_TOKENS + 1,
            JdbcTestUtils.countRowsInTable(jdbcTemplate, TOKEN_TABLE)
        );
        assertEquals(
            1,
            JdbcTestUtils.countRowsInTableWhere(
                jdbcTemplate, TOKEN_TABLE,
                "id = " + token.getId() + " AND user_id = " + USER_I3_ID + " AND token = '" + TOKEN_NEW_VALUE + "'"
            )
        );
    }
    @Test(expected = PersistenceException.class)
    public void testCreateTokenWrongUser(){
        tokenDao.create(
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
            TOKEN_NEW_VALUE, 
            TOKEN_NEW_EXPIRATION
        );
        em.flush();
    }
    @Test(expected = PersistenceException.class)
    public void testCreateTokenDuplicateToken(){
        tokenDao.create(USER_I3, TOKEN_1_VALUE, TOKEN_NEW_EXPIRATION);
        em.flush();
    }
    @Test(expected = PersistenceException.class)
    public void testCreateTokenMissingToken(){
        tokenDao.create(USER_I3, null, TOKEN_NEW_EXPIRATION);
        em.flush();
    }
    @Test(expected = PersistenceException.class)
    public void testCreateTokenMissingDate(){
        tokenDao.create(USER_I3, TOKEN_1_VALUE, null);
        em.flush();
    }

    @Test
    public void testFindByToken(){
        Optional<Token> maybeToken = tokenDao.findByToken(TOKEN_1_VALUE);

        assertTrue(maybeToken.isPresent());
        assertEquals(TOKEN_1_VALUE, maybeToken.get().getToken());
        assertEquals(TOKEN_1_ID, maybeToken.get().getId().longValue());
        assertTrue(TOKEN_NEW_EXPIRATION.plusHours(1)
            .isAfter(maybeToken.get().getExpirationDate())
        );
        assertTrue(TOKEN_NEW_EXPIRATION.plusHours(-1)
            .isBefore(maybeToken.get().getExpirationDate())
        );
        assertEqualsUser(USER_1, maybeToken.get().getUser());
    }
    @Test
    public void testFindByTokenNotFound(){
        Optional<Token> maybeToken = tokenDao.findByToken("asdfasdfhasgb");

        assertFalse(maybeToken.isPresent());
    }

    @Test
    public void testFindByTokenExpiredReturnsEmpty(){
        assertEquals(
            1,
            JdbcTestUtils.countRowsInTableWhere(
                jdbcTemplate, TOKEN_TABLE,
                "id = " + TOKEN_4_ID + " AND token = '" + TOKEN_4_VALUE + "'"
            )
        );

        Optional<Token> maybeToken = tokenDao.findByToken(TOKEN_4_VALUE);

        assertFalse(maybeToken.isPresent());
    }

    @Test
    public void testFindByUserId(){
        Optional<Token> maybeToken = tokenDao.findByUserId(USER_1_ID);

        assertTrue(maybeToken.isPresent());
        assertEquals(TOKEN_1_VALUE, maybeToken.get().getToken());
        assertEquals(TOKEN_1_ID, maybeToken.get().getId().longValue());
        assertTrue(TOKEN_NEW_EXPIRATION.plusHours(1)
            .isAfter(maybeToken.get().getExpirationDate())
        );
        assertTrue(TOKEN_NEW_EXPIRATION.plusHours(-1)
            .isBefore(maybeToken.get().getExpirationDate())
        );
        assertEqualsUser(USER_1, maybeToken.get().getUser());
    }
    @Test
    public void testFindByUserIdNotFound(){
        Optional<Token> maybeToken = tokenDao.findByUserId(12341234l);
        
        assertFalse(maybeToken.isPresent());
    }

    @Test
    public void testDeleteByToken(){
        int rowsBefore = JdbcTestUtils.countRowsInTable(jdbcTemplate, TOKEN_TABLE);

        tokenDao.deleteByToken(
            new Token(TOKEN_1_ID, null, null, null)
        );
        em.flush();

        assertEquals(
            rowsBefore - 1, 
            JdbcTestUtils.countRowsInTable(jdbcTemplate, TOKEN_TABLE)
        );
        assertEquals(
            0, 
            jdbcTemplate.queryForObject(
                TOKEN_SELECT_EXISTS_BY_ID, 
                Integer.class, 
                TOKEN_1_ID
            ).intValue()
        );
    }
    @Test
    public void testDeleteByTokenMissingToken(){
        int rowsBefore = JdbcTestUtils.countRowsInTable(jdbcTemplate, TOKEN_TABLE);

        tokenDao.deleteByToken(new Token(12341234l, null, null, null));
        em.flush();

        assertEquals(
            rowsBefore, 
            JdbcTestUtils.countRowsInTable(jdbcTemplate, TOKEN_TABLE)
        );
    }

    @Test
    public void testDeleteExpiredTokens(){
        tokenDao.deleteExpiredTokens();

        assertEquals(
            TOKENS_NOT_EXPIRED, 
            Optional.ofNullable(jdbcTemplate.queryForObject(
                TOKEN_SELECT_COUNT, 
                Integer.class
            )).get().intValue());
    }

}