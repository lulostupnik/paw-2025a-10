package ar.edu.itba.paw.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import ar.edu.itba.paw.models.PageParams;
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

import ar.edu.itba.paw.models.Interest;
import ar.edu.itba.paw.models.Page;

@Transactional
@Rollback
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class InterestJdbcDaoTest {

    @Autowired
    private DataSource ds;

    @Autowired
    private InterestJdbcDao interestDao;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp(){
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Test
    public void testFindById(){
        Optional<Interest> maybeInterest = interestDao.findById(TestUtils.INTEREST_1_ID);

        assertNotNull(maybeInterest);
        assertTrue(maybeInterest.isPresent());
        TestUtils.assertEqualsInterest(TestUtils.INTEREST_1, maybeInterest.get());
    }
    @Test
    public void testFindByIdWrongId(){
        Optional<Interest> maybeInterest = interestDao.findById(12341234l);

        assertNotNull(maybeInterest);
        assertFalse(maybeInterest.isPresent());
    }

    @Test
    public void testFindAllByUserId(){
        List<Interest> interests = interestDao.findAllByUserId(TestUtils.USER_1_ID);

        assertNotNull(interests);
        assertEquals(TestUtils.USER_1_INTERESTS, interests.size());
        for (Interest i : interests){
            TestUtils.assertEqualsInterest(TestUtils.INTEREST_DATA.get(i.getId()), i);
        }
    }
    @Test
    public void testFindAllByUserIdNoUserInterests(){
        List<Interest> interests = interestDao.findAllByUserId(TestUtils.USER_2_ID);

        assertNotNull(interests);
        assertEquals(0, interests.size());
    }

    @Test
    public void testFindByName(){
        Optional<Interest> maybeInterest = interestDao.findByName(TestUtils.INTEREST_1_NAME);

        assertNotNull(maybeInterest);
        assertTrue(maybeInterest.isPresent());
        TestUtils.assertEqualsInterest(TestUtils.INTEREST_1, maybeInterest.get());
    }
    @Test
    public void testFindByNameWrongName(){
        Optional<Interest> maybeInterest = interestDao.findByName("asdfasdf");

        assertNotNull(maybeInterest);
        assertFalse(maybeInterest.isPresent());
    }

    @Test
    public void testCreate(){
        Interest interest = interestDao.create(TestUtils.INTEREST_NEW1_NAME);

        assertNotNull(interest);
        TestUtils.assertEqualsInterest(new Interest(interest.getId(), TestUtils.INTEREST_NEW1_NAME), interest);
    }
    @Test(expected = DataAccessException.class)
    public void testCreateMissingName(){
        interestDao.create(null);
    }
    @Test(expected = DataAccessException.class)
    public void testCreateDuplicate(){
        interestDao.create(TestUtils.INTEREST_1_NAME);
    }

    @Test
    public void testUpdate(){
        interestDao.update(TestUtils.INTEREST_1_ID, TestUtils.INTEREST_NEW1_NAME);

        Interest interest = jdbcTemplate.queryForObject(
            TestUtils.INTEREST_SELECT_BY_ID, 
            TestUtils.INTEREST_ROW_MAPPER, 
            TestUtils.INTEREST_1_ID
        );
        TestUtils.assertEqualsInterest(new Interest(TestUtils.INTEREST_1_ID, TestUtils.INTEREST_NEW1_NAME), interest);
    }
    @Test
    public void testUpdateNotFound(){
        interestDao.update(12341234, TestUtils.INTEREST_1_NAME);

        List<Interest> interests = jdbcTemplate.query(TestUtils.INTEREST_SELECT + "ORDER BY id ASC", TestUtils.INTEREST_ROW_MAPPER);

        assertNotNull(interests);
        assertEquals(TestUtils.TOTAL_INTERESTS, interests.size());
        TestUtils.assertEqualsInterest(TestUtils.INTEREST_1, interests.get(0));
        TestUtils.assertEqualsInterest(TestUtils.INTEREST_2, interests.get(1));
        TestUtils.assertEqualsInterest(TestUtils.INTEREST_3, interests.get(2));
    }

    @Test
    public void testCreateUserInterests(){
        interestDao.createUserInterests(TestUtils.INTEREST_DATA.keySet().stream().mapToLong(l->l).toArray(), TestUtils.USER_2_ID);

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
    @Test(expected = DataAccessException.class)
    public void testCreateUserInterestsWrongInterest(){
        long[] array = new long[3];
        array[0] = TestUtils.INTEREST_1_ID;
        array[1] = TestUtils.INTEREST_2_ID;
        array[2] = 12341234;
        
        interestDao.createUserInterests(array, TestUtils.USER_1_ID);
    }
    @Test
    public void testCreateUserInterestsEmptyInterests(){
        interestDao.createUserInterests(new long[0], TestUtils.USER_1_ID);
        
        assertEquals(TestUtils.TOTAL_INTERESTS, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.INTEREST_TABLE));
        assertEquals(TestUtils.TOTAL_USER_INTERESTS, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.USER_INTEREST_TABLE));
    }

    @Test
    public void testUpdateScoreByInterest(){
        interestDao.updateScoreByInterest(TestUtils.INTEREST_1, TestUtils.USER_1_ID);

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
    public void testUpdateScoreByInterestsMultiple(){
        List<Interest> interests = List.of(TestUtils.INTEREST_1, TestUtils.INTEREST_2);
        
        interestDao.updateScoreByInterests(interests, TestUtils.USER_1_ID);

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
    public void testFindAllPaged(){
        Page<Interest> page1 = interestDao.findAll(TestUtils.PAGE_1_DEFAULT);
        Page<Interest> page2 = interestDao.findAll(TestUtils.PAGE_2_DEFAULT);

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
        TestUtils.assertEqualsInterest(TestUtils.INTEREST_1, page1.getContent().get(0));
        TestUtils.assertEqualsInterest(TestUtils.INTEREST_2, page1.getContent().get(1));
        TestUtils.assertEqualsInterest(TestUtils.INTEREST_3, page2.getContent().get(0));
    }
    @Test
    public void testFindAllInterestsPagedNoInterests(){
        TestUtils.deleteInterests(jdbcTemplate);

        Page<Interest> page1 = interestDao.findAll(new PageParams(1, 2));

        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(0, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(0, page1.getContent().size());
    }

    @Test
    public void testSearchNoFiltering(){
        Page<Interest> page1 = interestDao.search(TestUtils.INTEREST_1_NAME.substring(0, 5), new PageParams(1, 3));

        assertNotNull(page1);
        assertEquals(1, page1.getTotalPages());
        assertEquals(3, page1.getContent().size());
        TestUtils.assertEqualsInterest(TestUtils.INTEREST_1, page1.getContent().get(0));
        TestUtils.assertEqualsInterest(TestUtils.INTEREST_2, page1.getContent().get(1));
        TestUtils.assertEqualsInterest(TestUtils.INTEREST_3, page1.getContent().get(2));
    }
    @Test
    public void testSearchFiltering(){
        Page<Interest> page1 = interestDao.search(TestUtils.INTEREST_1_NAME.substring(TestUtils.INTEREST_1_NAME.length()-1, TestUtils.INTEREST_1_NAME.length()), new PageParams(1, 3));

        assertNotNull(page1);
        assertEquals(1, page1.getTotalPages());
        assertEquals(1, page1.getContent().size());
        TestUtils.assertEqualsInterest(TestUtils.INTEREST_1, page1.getContent().get(0));
    }
    @Test
    public void testSearchEmpty(){
        Page<Interest> page1 = interestDao.search("", new PageParams(1, 3));

        assertNotNull(page1);
        assertEquals(1, page1.getTotalPages());
        assertEquals(3, page1.getContent().size());
        TestUtils.assertEqualsInterest(TestUtils.INTEREST_1, page1.getContent().get(0));
        TestUtils.assertEqualsInterest(TestUtils.INTEREST_2, page1.getContent().get(1));
        TestUtils.assertEqualsInterest(TestUtils.INTEREST_3, page1.getContent().get(2));
    }
    @Test
    public void testSearchMissing(){
        Page<Interest> page1 = interestDao.search(null, new PageParams(1, 3));

        assertNotNull(page1);
        assertEquals(1, page1.getTotalPages());
        assertEquals(3, page1.getContent().size());
        TestUtils.assertEqualsInterest(TestUtils.INTEREST_1, page1.getContent().get(0));
        TestUtils.assertEqualsInterest(TestUtils.INTEREST_2, page1.getContent().get(1));
        TestUtils.assertEqualsInterest(TestUtils.INTEREST_3, page1.getContent().get(2));
    }
    @Test
    public void testSearchPaging(){
        Page<Interest> page1 = interestDao.search("", TestUtils.PAGE_1_DEFAULT);
        Page<Interest> page2 = interestDao.search("", TestUtils.PAGE_2_DEFAULT);

        assertNotNull(page1);
        assertNotNull(page2);
        assertEquals(2, page1.getTotalPages());
        assertEquals(2, page2.getTotalPages());
        assertEquals(2, page1.getContent().size());
        assertEquals(1, page2.getContent().size());
        TestUtils.assertEqualsInterest(TestUtils.INTEREST_1, page1.getContent().get(0));
        TestUtils.assertEqualsInterest(TestUtils.INTEREST_2, page1.getContent().get(1));
        TestUtils.assertEqualsInterest(TestUtils.INTEREST_3, page2.getContent().get(0));
    }

    @Test
    public void testDelete(){
        interestDao.delete(TestUtils.INTEREST_3_ID);

        assertEquals(TestUtils.TOTAL_INTERESTS - 1, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.INTEREST_TABLE));
    }
    @Test
    public void testDeleteWrongInterest(){
        interestDao.delete(12341234);

        assertEquals(TestUtils.TOTAL_INTERESTS, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.INTEREST_TABLE));
    }

    @Test
    public void testFindAllInterestsByUserId(){
        Page<Interest> interests = interestDao.findAllByUserId(TestUtils.USER_1_ID, TestUtils.PAGE_1_BIG);

        assertNotNull(interests);
        assertEquals(1, interests.getCurrentPage());
        assertEquals(1, interests.getTotalPages());
        assertNotNull(interests.getContent());
        TestUtils.assertEqualsInterest(TestUtils.INTEREST_1, interests.getContent().get(0));
        TestUtils.assertEqualsInterest(TestUtils.INTEREST_2, interests.getContent().get(1));
        TestUtils.assertEqualsInterest(TestUtils.INTEREST_3, interests.getContent().get(2));
    }

}

