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
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

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
        Token token = tokenDao.create(TestUtils.USER_I3, TestUtils.TOKEN_NEW_VALUE, TestUtils.TOKEN_NEW_EXPIRATION);
        em.flush();

        assertEquals(TestUtils.TOKEN_NEW_EXPIRATION, token.getExpirationDate());
        assertEquals(TestUtils.TOKEN_NEW_VALUE, token.getToken());
        TestUtils.assertEqualsUser(TestUtils.USER_I3, token.getUser());
        assertTrue(token.getTokenId() > 1);
    }
    @Test(expected = PersistenceException.class)
    public void testCreateTokenWrongUser(){
        tokenDao.create(new User(12341234l, null, null, null, null, null, null, 0, null, false, false), TestUtils.TOKEN_NEW_VALUE, TestUtils.TOKEN_NEW_EXPIRATION);
        em.flush();
    }
    @Test(expected = PersistenceException.class)
    public void testCreateTokenDuplicateToken(){
        tokenDao.create(TestUtils.USER_I3, TestUtils.TOKEN_1_VALUE, TestUtils.TOKEN_NEW_EXPIRATION);
        em.flush();
    }
    @Test(expected = PersistenceException.class)
    public void testCreateTokenMissingToken(){
        tokenDao.create(TestUtils.USER_I3, null, TestUtils.TOKEN_NEW_EXPIRATION);
        em.flush();
    }
    @Test(expected = PersistenceException.class)
    public void testCreateTokenMissingDate(){
        tokenDao.create(TestUtils.USER_I3, TestUtils.TOKEN_1_VALUE, null);
        em.flush();
    }

    @Test
    public void testFindByToken(){
        Optional<Token> maybeToken = tokenDao.findByToken(TestUtils.TOKEN_1_VALUE);

        assertTrue(maybeToken.isPresent());
        assertEquals(TestUtils.TOKEN_1_VALUE, maybeToken.get().getToken());
        assertEquals(TestUtils.TOKEN_1_ID, maybeToken.get().getTokenId().longValue());
        assertTrue(TestUtils.TOKEN_NEW_EXPIRATION.plusHours(1).isAfter(maybeToken.get().getExpirationDate()));
        assertTrue(TestUtils.TOKEN_NEW_EXPIRATION.plusHours(-1).isBefore(maybeToken.get().getExpirationDate()));
        TestUtils.assertEqualsUser(TestUtils.USER_1, maybeToken.get().getUser());
    }
    @Test
    public void testFindByTokenNotFound(){
        Optional<Token> maybeToken = tokenDao.findByToken("asdfasdfhasgb");
        
        assertFalse(maybeToken.isPresent());
    }

    @Test
    public void testFindByUserId(){
        Optional<Token> maybeToken = tokenDao.findByUserId(TestUtils.USER_1_ID);

        assertTrue(maybeToken.isPresent());
        assertEquals(TestUtils.TOKEN_1_VALUE, maybeToken.get().getToken());
        assertEquals(TestUtils.TOKEN_1_ID, maybeToken.get().getTokenId().longValue());
        assertTrue(TestUtils.TOKEN_NEW_EXPIRATION.plusHours(1).isAfter(maybeToken.get().getExpirationDate()));
        assertTrue(TestUtils.TOKEN_NEW_EXPIRATION.plusHours(-1).isBefore(maybeToken.get().getExpirationDate()));
        TestUtils.assertEqualsUser(TestUtils.USER_1, maybeToken.get().getUser());
    }
    @Test
    public void testFindByUserIdNotFound(){
        Optional<Token> maybeToken = tokenDao.findByUserId(12341234l);
        
        assertFalse(maybeToken.isPresent());
    }

    // TODO Can't get delete to work!
    // @Test
    // public void testDeleteByToken(){
    //     int rowsBefore = JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.TOKEN_TABLE);

    //     tokenDao.deleteByToken(new Token(TestUtils.TOKEN_1_ID, null, null, null));
    //     em.flush();

    //     assertEquals(rowsBefore - 1, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.TOKEN_TABLE));
    //     assertFalse(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM tokens WHERE id = ?", Boolean.class, TestUtils.TOKEN_1_ID));
    // }

    @Test
    public void testDeleteExpiredTokens(){
        tokenDao.deleteExpiredTokens();

        assertEquals(
            TestUtils.TOKENS_NOT_EXPIRED, 
            Optional.ofNullable(jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM tokens", 
                Integer.class
            )).get().intValue());
    }

}