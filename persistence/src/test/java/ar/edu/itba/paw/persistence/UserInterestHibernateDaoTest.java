package ar.edu.itba.paw.persistence;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;

import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.exceptions.InterestsNotFoundException;
import ar.edu.itba.paw.models.exceptions.UserNotFoundException;
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
public class UserInterestHibernateDaoTest {

    @Autowired
    private DataSource ds;

    @Autowired
    private UserInterestHibernateDao interestDao;
        
    @PersistenceContext
    private EntityManager em;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp(){
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Test
    public void testFindAllByUser(){
        List<UserInterest> interests = interestDao.findAllByUser(USER_1);

        assertEquals(USER_1_INTERESTS, interests.size());
        interests.forEach((ui) -> {
            assertEqualsInterest(
                INTEREST_DATA.get(ui.getInterest().getId()), 
                ui.getInterest()
            );
            assertEqualsUser(USER_1, ui.getUser());
            assertEquals(
                USER_1_INTEREST_SCORES.get(ui.getInterest().getId()).intValue(), 
                ui.getScore()
            );
        });
    }
    @Test
    public void testFindAllByUserNoInterests(){
        List<UserInterest> interests = interestDao.findAllByUser(USER_3);

        assertEquals(0, interests.size());
    }

    @Test
    public void testFindAllByUserPaged(){
        Page<UserInterest> interests = interestDao.findAllByUser(USER_1, PAGE_1_BIG);

        assertNotNull(interests);
        assertEquals(1, interests.getCurrentPage());
        assertEquals(1, interests.getTotalPages());
        assertEquals(USER_1_INTERESTS, interests.getContent().size());
        interests.getContent().forEach((ui) -> {
            assertEqualsInterest(
                INTEREST_DATA.get(ui.getInterest().getId()), 
                ui.getInterest()
            );
            assertEqualsUser(USER_1, ui.getUser());
            assertEquals(
                USER_1_INTEREST_SCORES.get(ui.getInterest().getId()).intValue(), 
                ui.getScore()
            );
        });
    }
    @Test
    public void testFindAllByUserPagedNoInterests(){
        Page<UserInterest> interests = interestDao.findAllByUser(USER_3, PAGE_1_BIG);
          
        assertNotNull(interests);      
        assertEquals(1, interests.getCurrentPage());
        assertEquals(0, interests.getTotalPages());
        assertEquals(0, interests.getContent().size());
    }

    @Test
    public void testUpdateMatchingInterestScores(){
        interestDao.updateMatchingInterestScores(USER_I1_ID, USER_1_ID);
        em.flush();

        //Only updates score of reply author, not event creator.
        assertEquals(
            USER_I1_INTEREST_1_SCORE + 1, 
            Optional.ofNullable(jdbcTemplate.queryForObject(
                INTEREST_SELECT_SCORE, 
                Integer.class, 
                INTEREST_1_ID, 
                USER_I1_ID)
            ).get().intValue()
        );
    }
    @Test
    public void testUpdateMatchingInterestScoresNoCommonInterests(){
        interestDao.updateMatchingInterestScores(USER_3_ID, USER_1_ID);
        em.flush();

        //Only updates score of reply author, not event creator.
        assertEquals(
            0, 
            jdbcTemplate.query(
                INTEREST_SELECT_BY_USER_ID, 
                INTEREST_ROW_MAPPER, 
                USER_3_ID
            ).size()
        );
    }

    @Test
    public void testCreateUserInterests(){
        interestDao.createUserInterests(
            INTEREST_DATA.keySet().stream().mapToLong(l->l).toArray(), 
            USER_2_ID
        );
        em.flush();

        assertEquals(
            TOTAL_USER_INTERESTS + 3, 
            JdbcTestUtils.countRowsInTable(jdbcTemplate, USER_INTEREST_TABLE)
        );
        List<Interest> interests = jdbcTemplate.query(INTEREST_SELECT_BY_USER_ID,
            INTEREST_ROW_MAPPER, USER_2_ID
        );
        assertNotNull(interests);
        assertEquals(INTEREST_DATA.size(), interests.size());
        interests.forEach((i) ->
            assertEqualsInterest(INTEREST_DATA.get(i.getId()), i)
        );
    }
    @Test(expected = UserNotFoundException.class)
    public void testCreateUserInterestsWrongUser(){
        interestDao.createUserInterests(
            INTEREST_DATA.keySet().stream().mapToLong(l->l).toArray(), 
            12341234l
        );
        em.flush();
    }
    @Test(expected = InterestsNotFoundException.class)
    public void testCreateUserInterestsWrongInterest(){
        long[] array = new long[3];
        array[0] = INTEREST_1_ID;
        array[1] = INTEREST_2_ID;
        array[2] = 12341234;

        interestDao.createUserInterests(array, USER_1_ID);
        em.flush();
    }
    @Test
    public void testCreateUserInterestsEmptyInterests(){
        interestDao.createUserInterests(new long[0], USER_1_ID);
        em.flush();

        assertEquals(
            TOTAL_INTERESTS, 
            JdbcTestUtils.countRowsInTable(jdbcTemplate, INTEREST_TABLE)
        );
        assertEquals(
            TOTAL_USER_INTERESTS, 
            JdbcTestUtils.countRowsInTable(jdbcTemplate, USER_INTEREST_TABLE)
        );
    }

    @Test
    public void testUpdateUserInterests(){
        interestDao.updateUserInterests(new long[]{INTEREST_1_ID}, USER_1_ID);
        em.flush();

        assertEqualsInterest(
            INTEREST_1, 
            jdbcTemplate.queryForObject(
                INTEREST_SELECT_BY_USER_ID, 
                INTEREST_ROW_MAPPER, 
                USER_1_ID)
            );
    }
    @Test(expected = UserNotFoundException.class)
    public void testUpdateUserInterestsMissingUser(){
        interestDao.updateUserInterests(new long[]{INTEREST_1_ID}, 12341234l);
        em.flush();

        assertEqualsInterest(
            INTEREST_1, 
            jdbcTemplate.queryForObject(
                INTEREST_SELECT_BY_USER_ID, 
                INTEREST_ROW_MAPPER, 
                USER_1_ID)
            );
    }
    @Test(expected = InterestsNotFoundException.class)
    public void testUpdateUserInterestsMissingInterest(){
        interestDao.updateUserInterests(new long[]{12341234l}, USER_1_ID);
        em.flush();

        assertEqualsInterest(
            INTEREST_1, 
            jdbcTemplate.queryForObject(
                INTEREST_SELECT_BY_USER_ID, 
                INTEREST_ROW_MAPPER, 
                USER_1_ID
            )
        );
    }
    @Test
    public void testUpdateUserInterestsSameInterests(){
        interestDao.updateUserInterests(
            new long[]{INTEREST_1_ID, INTEREST_2_ID, INTEREST_3_ID}, 
            USER_1_ID
        );
        em.flush();

        assertEquals(
            3, 
            jdbcTemplate.query(
                INTEREST_SELECT_BY_USER_ID, 
                INTEREST_ROW_MAPPER, 
                USER_1_ID
            ).size()
        );
    }
    @Test
    public void testUpdateUserInterestsNoInterests(){
        interestDao.updateUserInterests(new long[]{}, USER_1_ID);
        em.flush();

        assertEquals(
            0, 
            jdbcTemplate.query(
                INTEREST_SELECT_BY_USER_ID, 
                INTEREST_ROW_MAPPER, 
                USER_1_ID
            ).size()
        );
    }
    @Test
    public void testUpdateUserInterestsInsertNew(){
        interestDao.updateUserInterests(new long[]{INTEREST_3_ID}, USER_2_ID);
        em.flush();

        assertEquals(
            1, 
            jdbcTemplate.query(
                INTEREST_SELECT_BY_USER_ID, 
                INTEREST_ROW_MAPPER, 
                USER_2_ID
            ).size()
        );
    }

    @Test
    public void testCreateUserInterestsNames(){
        interestDao.createUserInterests(
            List.of(INTEREST_1_NAME, INTEREST_2_NAME), 
            USER_2_ID
        );
        em.flush();

        assertEquals(
            2, 
            jdbcTemplate.query(
                INTEREST_SELECT_BY_USER_ID,
                INTEREST_ROW_MAPPER, 
                USER_2_ID
            ).size()
        );
    }
    @Test(expected = UserNotFoundException.class)
    public void testCreateUserInterestsNamesMissingUser(){
        interestDao.createUserInterests(
            List.of(INTEREST_1_NAME, INTEREST_2_NAME), 
            12341234l
        );
        em.flush();
    }

    @Test
    public void testCreateUserInterestsNamesMissingInterest(){
        interestDao.createUserInterests(
            List.of(
                INTEREST_1_NAME, 
                INTEREST_2_NAME, 
                "COMPLETELY UNIQUE AND REVOLUTIONARY INTEREST"
            ), USER_2_ID
        );
        em.flush();

        assertEquals(
            3, 
            jdbcTemplate.query(
                INTEREST_SELECT_BY_USER_ID, 
                INTEREST_ROW_MAPPER, 
                USER_2_ID
            ).size()
        );
    }
}