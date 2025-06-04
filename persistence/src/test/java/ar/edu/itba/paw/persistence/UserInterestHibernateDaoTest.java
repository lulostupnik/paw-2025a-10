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
        List<UserInterest> interests = interestDao.findAllByUser(TestUtils.USER_1);

        assertEquals(TestUtils.USER_1_INTERESTS, interests.size());
        for (UserInterest ui : interests) {
            TestUtils.assertEqualsInterest(TestUtils.INTEREST_DATA.get(ui.getInterest().getId()), ui.getInterest());
            TestUtils.assertEqualsUser(TestUtils.USER_1, ui.getUser());
            assertEquals(TestUtils.USER_1_INTEREST_SCORES.get(ui.getInterest().getId()).intValue(), ui.getScore());
        }
    }
    @Test
    public void testFindAllByUserNoInterests(){
        List<UserInterest> interests = interestDao.findAllByUser(TestUtils.USER_3);

        assertEquals(0, interests.size());
    }

    @Test
    public void testFindAllByUserPaged(){
        Page<UserInterest> interests = interestDao.findAllByUser(TestUtils.USER_1, TestUtils.PAGE_1_BIG);

        assertNotNull(interests);
        assertEquals(1, interests.getCurrentPage());
        assertEquals(1, interests.getTotalPages());
        assertEquals(TestUtils.USER_1_INTERESTS, interests.getContent().size());
        for (UserInterest ui : interests.getContent()) {
            TestUtils.assertEqualsInterest(TestUtils.INTEREST_DATA.get(ui.getInterest().getId()), ui.getInterest());
            TestUtils.assertEqualsUser(TestUtils.USER_1, ui.getUser());
            assertEquals(TestUtils.USER_1_INTEREST_SCORES.get(ui.getInterest().getId()).intValue(), ui.getScore());
        }
    }
    @Test
    public void testFindAllByUserPagedNoInterests(){
        Page<UserInterest> interests = interestDao.findAllByUser(TestUtils.USER_3, TestUtils.PAGE_1_BIG);
          
        assertNotNull(interests);      
        assertEquals(1, interests.getCurrentPage());
        assertEquals(0, interests.getTotalPages());
        assertEquals(0, interests.getContent().size());
    }

    @Test
    public void testUpdateMatchingInterestScores(){
        interestDao.updateMatchingInterestScores(TestUtils.USER_I1_ID, TestUtils.USER_1_ID);
        em.flush();

        //Only updates score of reply author, not event creator.
        assertEquals(TestUtils.USER_I1_INTEREST_1_SCORE + 1, Optional.ofNullable(jdbcTemplate.queryForObject(TestUtils.INTEREST_SELECT_SCORE, Integer.class, TestUtils.INTEREST_1_ID, TestUtils.USER_I1_ID)).get().intValue());
    }
    @Test
    public void testUpdateMatchingInterestScoresNoCommonInterests(){
        interestDao.updateMatchingInterestScores(TestUtils.USER_3_ID, TestUtils.USER_1_ID);
        em.flush();

        //Only updates score of reply author, not event creator.
        assertEquals(0, jdbcTemplate.query(TestUtils.INTEREST_SELECT_BY_USER_ID, TestUtils.INTEREST_ROW_MAPPER, TestUtils.USER_3_ID).size());
    }

    @Test
    public void testCreateUserInterests(){
        interestDao.createUserInterests(TestUtils.INTEREST_DATA.keySet().stream().mapToLong(l->l).toArray(), TestUtils.USER_2_ID);
        em.flush();

        assertEquals(TestUtils.TOTAL_USER_INTERESTS + 3, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.USER_INTEREST_TABLE));
        List<Interest> interests = jdbcTemplate.query(TestUtils.INTEREST_SELECT_BY_USER_ID,
            TestUtils.INTEREST_ROW_MAPPER, TestUtils.USER_2_ID
        );
        assertNotNull(interests);
        assertEquals(TestUtils.INTEREST_DATA.size(), interests.size());
        for (Interest i : interests){
            TestUtils.assertEqualsInterest(TestUtils.INTEREST_DATA.get(i.getId()), i);
        }
    }
    @Test(expected = UserNotFoundException.class)
    public void testCreateUserInterestsWrongUser(){
        interestDao.createUserInterests(TestUtils.INTEREST_DATA.keySet().stream().mapToLong(l->l).toArray(), 12341234l);
        em.flush();
    }
    @Test(expected = InterestsNotFoundException.class)
    public void testCreateUserInterestsWrongInterest(){
        long[] array = new long[3];
        array[0] = TestUtils.INTEREST_1_ID;
        array[1] = TestUtils.INTEREST_2_ID;
        array[2] = 12341234;

        interestDao.createUserInterests(array, TestUtils.USER_1_ID);
        em.flush();
    }
    @Test
    public void testCreateUserInterestsEmptyInterests(){
        interestDao.createUserInterests(new long[0], TestUtils.USER_1_ID);
        em.flush();

        assertEquals(TestUtils.TOTAL_INTERESTS, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.INTEREST_TABLE));
        assertEquals(TestUtils.TOTAL_USER_INTERESTS, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.USER_INTEREST_TABLE));
    }

    @Test
    public void testUpdateScoreByInterest(){
        interestDao.updateScoreByInterest(TestUtils.INTEREST_1, TestUtils.USER_1_ID);
        em.flush();

        assertEquals(
            TestUtils.USER_1_INTEREST_1_SCORE + 1,
            jdbcTemplate.queryForObject(
                TestUtils.INTEREST_SELECT_SCORE,
                Integer.class,
                TestUtils.INTEREST_1_ID,
                TestUtils.USER_1_ID
            ).intValue()
        );
        assertEquals(
            TestUtils.USER_1_INTEREST_2_SCORE,
            jdbcTemplate.queryForObject(
                TestUtils.INTEREST_SELECT_SCORE,
                Integer.class,
                TestUtils.INTEREST_2_ID,
                TestUtils.USER_1_ID
            ).intValue()
        );
        assertEquals(
            TestUtils.USER_1_INTEREST_3_SCORE,
            jdbcTemplate.queryForObject(
                TestUtils.INTEREST_SELECT_SCORE,
                Integer.class,
                TestUtils.INTEREST_3_ID,
                TestUtils.USER_1_ID
            ).intValue()
        );
    }
    @Test
    public void testUpdateScoreByInterest2(){
        interestDao.updateScoreByInterest(TestUtils.INTEREST_2, TestUtils.USER_1_ID);
        em.flush();

        assertEquals(
            TestUtils.USER_1_INTEREST_1_SCORE,
            jdbcTemplate.queryForObject(
                TestUtils.INTEREST_SELECT_SCORE,
                Integer.class,
                TestUtils.INTEREST_1_ID,
                TestUtils.USER_1_ID
            ).intValue()
        );
        assertEquals(
            TestUtils.USER_1_INTEREST_2_SCORE + 1,
            jdbcTemplate.queryForObject(
                TestUtils.INTEREST_SELECT_SCORE,
                Integer.class,
                TestUtils.INTEREST_2_ID,
                TestUtils.USER_1_ID
            ).intValue()
        );
        assertEquals(
            TestUtils.USER_1_INTEREST_3_SCORE,
            jdbcTemplate.queryForObject(
                TestUtils.INTEREST_SELECT_SCORE,
                Integer.class,
                TestUtils.INTEREST_3_ID,
                TestUtils.USER_1_ID
            ).intValue()
        );
    }
    @Test
    public void testUpdateScoreByInterestWrongInterest(){
        interestDao.updateScoreByInterest(new Interest(12341234l, null), TestUtils.USER_1_ID);
        em.flush();

        assertEquals(
            TestUtils.USER_1_INTEREST_1_SCORE,
            jdbcTemplate.queryForObject(
                TestUtils.INTEREST_SELECT_SCORE,
                Integer.class,
                TestUtils.INTEREST_1_ID,
                TestUtils.USER_1_ID
            ).intValue()
        );
        assertEquals(
            TestUtils.USER_1_INTEREST_2_SCORE,
            jdbcTemplate.queryForObject(
                TestUtils.INTEREST_SELECT_SCORE,
                Integer.class,
                TestUtils.INTEREST_2_ID,
                TestUtils.USER_1_ID
            ).intValue()
        );
        assertEquals(
            TestUtils.USER_1_INTEREST_3_SCORE,
            jdbcTemplate.queryForObject(
                TestUtils.INTEREST_SELECT_SCORE,
                Integer.class,
                TestUtils.INTEREST_3_ID,
                TestUtils.USER_1_ID
            ).intValue()
        );
    }
    @Test
    public void testUpdateScoreByInterestWrongUser(){
        interestDao.updateScoreByInterest(TestUtils.INTEREST_1, (long)12341234);
        em.flush();

        assertEquals(
            TestUtils.USER_1_INTEREST_1_SCORE,
            jdbcTemplate.queryForObject(
                TestUtils.INTEREST_SELECT_SCORE,
                Integer.class,
                TestUtils.INTEREST_1_ID,
                TestUtils.USER_1_ID
            ).intValue()
        );
        assertEquals(
            TestUtils.USER_1_INTEREST_2_SCORE,
            jdbcTemplate.queryForObject(
                TestUtils.INTEREST_SELECT_SCORE,
                Integer.class,
                TestUtils.INTEREST_2_ID,
                TestUtils.USER_1_ID
            ).intValue()
        );
        assertEquals(
            TestUtils.USER_1_INTEREST_3_SCORE,
            jdbcTemplate.queryForObject(
                TestUtils.INTEREST_SELECT_SCORE,
                Integer.class,
                TestUtils.INTEREST_3_ID,
                TestUtils.USER_1_ID
            ).intValue()
        );
    }

    @Test
    public void testUpdateUserInterests(){
        interestDao.updateUserInterests(new long[]{TestUtils.INTEREST_1_ID}, TestUtils.USER_1_ID);
        em.flush();

        TestUtils.assertEqualsInterest(TestUtils.INTEREST_1, jdbcTemplate.queryForObject(TestUtils.INTEREST_SELECT_BY_USER_ID, TestUtils.INTEREST_ROW_MAPPER, TestUtils.USER_1_ID));
    }
    @Test(expected = UserNotFoundException.class)
    public void testUpdateUserInterestsMissingUser(){
        interestDao.updateUserInterests(new long[]{TestUtils.INTEREST_1_ID}, 12341234l);
        em.flush();

        TestUtils.assertEqualsInterest(TestUtils.INTEREST_1, jdbcTemplate.queryForObject(TestUtils.INTEREST_SELECT_BY_USER_ID, TestUtils.INTEREST_ROW_MAPPER, TestUtils.USER_1_ID));
    }
    @Test(expected = InterestsNotFoundException.class)
    public void testUpdateUserInterestsMissingInterest(){
        interestDao.updateUserInterests(new long[]{12341234l}, TestUtils.USER_1_ID);
        em.flush();

        TestUtils.assertEqualsInterest(TestUtils.INTEREST_1, jdbcTemplate.queryForObject(TestUtils.INTEREST_SELECT_BY_USER_ID, TestUtils.INTEREST_ROW_MAPPER, TestUtils.USER_1_ID));
    }
    @Test
    public void testUpdateUserInterestsSameInterests(){
        interestDao.updateUserInterests(new long[]{TestUtils.INTEREST_1_ID, TestUtils.INTEREST_2_ID, TestUtils.INTEREST_3_ID}, TestUtils.USER_1_ID);
        em.flush();

        assertEquals(3, jdbcTemplate.query(TestUtils.INTEREST_SELECT_BY_USER_ID, TestUtils.INTEREST_ROW_MAPPER, TestUtils.USER_1_ID).size());
    }
    @Test
    public void testUpdateUserInterestsNoInterests(){
        interestDao.updateUserInterests(new long[]{}, TestUtils.USER_1_ID);
        em.flush();

        assertEquals(0, jdbcTemplate.query(TestUtils.INTEREST_SELECT_BY_USER_ID, TestUtils.INTEREST_ROW_MAPPER, TestUtils.USER_1_ID).size());
    }
    @Test
    public void testUpdateUserInterestsInsertNew(){
        interestDao.updateUserInterests(new long[]{TestUtils.INTEREST_3_ID}, TestUtils.USER_2_ID);
        em.flush();

        assertEquals(1, jdbcTemplate.query(TestUtils.INTEREST_SELECT_BY_USER_ID, TestUtils.INTEREST_ROW_MAPPER, TestUtils.USER_2_ID).size());
    }

    @Test
    public void testUpdateScoreByInterestsMultiple(){
        List<Interest> interests = List.of(TestUtils.INTEREST_1, TestUtils.INTEREST_2);

        interestDao.updateScoreByInterests(interests, TestUtils.USER_1_ID);
        em.flush();

        assertEquals(TestUtils.TOTAL_INTERESTS, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.INTEREST_TABLE));
        assertEquals(TestUtils.TOTAL_USER_INTERESTS, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.USER_INTEREST_TABLE));
        assertEquals(
            TestUtils.USER_1_INTEREST_1_SCORE + 1,
            jdbcTemplate.queryForObject(
                TestUtils.INTEREST_SELECT_SCORE,
                Integer.class,
                TestUtils.INTEREST_1_ID,
                TestUtils.USER_1_ID
            ).intValue()
        );
        assertEquals(
            TestUtils.USER_1_INTEREST_2_SCORE + 1,
            jdbcTemplate.queryForObject(
                TestUtils.INTEREST_SELECT_SCORE,
                Integer.class,
                TestUtils.INTEREST_2_ID,
                TestUtils.USER_1_ID
            ).intValue()
        );
        assertEquals(
            TestUtils.USER_1_INTEREST_3_SCORE,
            jdbcTemplate.queryForObject(
                TestUtils.INTEREST_SELECT_SCORE,
                Integer.class,
                TestUtils.INTEREST_3_ID,
                TestUtils.USER_1_ID
            ).intValue()
        );
    }
    @Test
    public void testUpdateScoreByInterestsMultipleDuplicated(){
        List<Interest> interests = List.of(TestUtils.INTEREST_1, TestUtils.INTEREST_2, TestUtils.INTEREST_2);

        interestDao.updateScoreByInterests(interests, TestUtils.USER_1_ID);
        em.flush();

        assertEquals(TestUtils.TOTAL_INTERESTS, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.INTEREST_TABLE));
        assertEquals(TestUtils.TOTAL_USER_INTERESTS, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.USER_INTEREST_TABLE));
        assertEquals(
            TestUtils.USER_1_INTEREST_1_SCORE + 1,
            jdbcTemplate.queryForObject(
                TestUtils.INTEREST_SELECT_SCORE,
                Integer.class,
                TestUtils.INTEREST_1_ID,
                TestUtils.USER_1_ID
            ).intValue()
        );
        assertEquals(
            TestUtils.USER_1_INTEREST_2_SCORE + 2,
            jdbcTemplate.queryForObject(
                TestUtils.INTEREST_SELECT_SCORE,
                Integer.class,
                TestUtils.INTEREST_2_ID,
                TestUtils.USER_1_ID
            ).intValue()
        );
        assertEquals(
            TestUtils.USER_1_INTEREST_3_SCORE,
            jdbcTemplate.queryForObject(
                TestUtils.INTEREST_SELECT_SCORE,
                Integer.class,
                TestUtils.INTEREST_3_ID,
                TestUtils.USER_1_ID
            ).intValue()
        );
    }
    @Test
    public void testUpdateScoreByInterestsMultipleEmpty(){
        List<Interest> interests = List.of();

        interestDao.updateScoreByInterests(interests, TestUtils.USER_1_ID);
        em.flush();

        assertEquals(TestUtils.TOTAL_INTERESTS, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.INTEREST_TABLE));
        assertEquals(TestUtils.TOTAL_USER_INTERESTS, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.USER_INTEREST_TABLE));
        assertEquals(
            TestUtils.USER_1_INTEREST_1_SCORE,
            jdbcTemplate.queryForObject(
                TestUtils.INTEREST_SELECT_SCORE,
                Integer.class,
                TestUtils.INTEREST_1_ID,
                TestUtils.USER_1_ID
            ).intValue()
        );
        assertEquals(
            TestUtils.USER_1_INTEREST_2_SCORE,
            jdbcTemplate.queryForObject(
                TestUtils.INTEREST_SELECT_SCORE,
                Integer.class,
                TestUtils.INTEREST_2_ID,
                TestUtils.USER_1_ID
            ).intValue()
        );
        assertEquals(
            TestUtils.USER_1_INTEREST_3_SCORE,
            jdbcTemplate.queryForObject(
                TestUtils.INTEREST_SELECT_SCORE,
                Integer.class,
                TestUtils.INTEREST_3_ID,
                TestUtils.USER_1_ID
            ).intValue()
        );
    }

    @Test
    public void testCreateUserInterestsNames(){
        interestDao.createUserInterests(List.of(TestUtils.INTEREST_1_NAME, TestUtils.INTEREST_2_NAME), TestUtils.USER_2_ID);
        em.flush();

        assertEquals(2, jdbcTemplate.query(TestUtils.INTEREST_SELECT_BY_USER_ID, TestUtils.INTEREST_ROW_MAPPER, TestUtils.USER_2_ID).size());
    }
    @Test(expected = UserNotFoundException.class)
    public void testCreateUserInterestsNamesMissingUser(){
        interestDao.createUserInterests(List.of(TestUtils.INTEREST_1_NAME, TestUtils.INTEREST_2_NAME), 12341234l);
        em.flush();
    }

    @Test
    public void testCreateUserInterestsNamesMissingInterest(){
        interestDao.createUserInterests(List.of(TestUtils.INTEREST_1_NAME, TestUtils.INTEREST_2_NAME, "COMPLETELY UNIQUE AND REVOLUTIONARY INTEREST"), TestUtils.USER_2_ID);
        em.flush();

        assertEquals(3, jdbcTemplate.query(TestUtils.INTEREST_SELECT_BY_USER_ID, TestUtils.INTEREST_ROW_MAPPER, TestUtils.USER_2_ID).size());
    }
}