package ar.edu.itba.paw.persistance;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.sql.DataSource;

import ar.edu.itba.paw.models.PageParams;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.itba.paw.models.Interest;
import ar.edu.itba.paw.models.Page;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistence.InterestJdbcDao;

@Transactional
@Rollback
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class InterestJdbcDaoTest {

    private static User USER_1;
    private static User USER_2;
    private static Interest INTEREST_1;
    private static Interest INTEREST_2;
    private static Interest INTEREST_3;
    Map<Long, String> interestParams;


    @Autowired
    private DataSource ds;

    @Autowired
    private InterestJdbcDao interestDao;

    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert insertInterest;
    private SimpleJdbcInsert insertUserInterest;

    @Before
    public void setUp(){
        jdbcTemplate = new JdbcTemplate(ds);
        insertInterest = new SimpleJdbcInsert(ds).withTableName(TestUtils.INTEREST_TABLE).usingGeneratedKeyColumns("id");
        insertUserInterest = new SimpleJdbcInsert(ds).withTableName(TestUtils.USER_INTEREST_TABLE);   

        USER_1 = jdbcTemplate.queryForObject(TestUtils.USER_SELECT_BY_EMAIL, TestUtils.USER_ROW_MAPPER, TestUtils.USER_1_MAIL);
        USER_2 = jdbcTemplate.queryForObject(TestUtils.USER_SELECT_BY_EMAIL, TestUtils.USER_ROW_MAPPER, TestUtils.USER_2_MAIL);
        INTEREST_1 = jdbcTemplate.queryForObject(TestUtils.INTEREST_SELECT_BY_NAME, TestUtils.INTEREST_ROW_MAPPER, TestUtils.INTEREST_1_NAME);
        INTEREST_2 = jdbcTemplate.queryForObject(TestUtils.INTEREST_SELECT_BY_NAME, TestUtils.INTEREST_ROW_MAPPER, TestUtils.INTEREST_2_NAME);
        INTEREST_3 = jdbcTemplate.queryForObject(TestUtils.INTEREST_SELECT_BY_NAME, TestUtils.INTEREST_ROW_MAPPER, TestUtils.INTEREST_3_NAME);
        interestParams = Map.of(INTEREST_1.getId(), INTEREST_1.getName(), INTEREST_2.getId(), INTEREST_2.getName(), INTEREST_3.getId(), INTEREST_3.getName());
    }


    @Test
    public void testFindById(){
        Optional<Interest> maybeInterest = interestDao.findById(INTEREST_1.getId());

        assertNotNull(maybeInterest);
        assertTrue(maybeInterest.isPresent());
        Interest interest = maybeInterest.get();
        assertEquals(INTEREST_1.getId(), interest.getId());
        assertEquals(TestUtils.INTEREST_1_NAME, interest.getName());
    }
    @Test
    public void testFindByIdWrongId(){
        Optional<Interest> maybeInterest = interestDao.findById(12341234l);

        assertNotNull(maybeInterest);
        assertFalse(maybeInterest.isPresent());
    }



    @Test
    public void testFindByUserId(){        
        List<Interest> interests = interestDao.findByUserId(USER_1.getId());

        assertNotNull(interests);
        assertEquals(TestUtils.USER_1_INTERESTS, interests.size());
        for (Interest i : interests){
            assertEquals(interestParams.get(i.getId()), i.getName());
        }
    }
    @Test
    public void testFindByUserIdNoUserInterests(){
        List<Interest> interests = interestDao.findByUserId(USER_2.getId());

        assertNotNull(interests);
        assertEquals(0, interests.size());
    }

    @Test
    public void testFindByName(){
        Optional<Interest> maybeInterest = interestDao.findByName(TestUtils.INTEREST_1_NAME);

        assertNotNull(maybeInterest);
        assertTrue(maybeInterest.isPresent());
        Interest interest = maybeInterest.get();
        assertEquals(INTEREST_1.getId(), interest.getId());
        assertEquals(TestUtils.INTEREST_1_NAME, interest.getName());
    }
    @Test
    public void testFindByNameWrongName(){
        Optional<Interest> maybeInterest = interestDao.findByName("asdfasdf");

        assertNotNull(maybeInterest);
        assertFalse(maybeInterest.isPresent());
    }



    @Test
    public void testCreateUserInterest(){
        Interest interest = interestDao.createUserInterest(TestUtils.INTEREST_NEW1_NAME);

        assertNotNull(interest);
        assertEquals(TestUtils.INTEREST_NEW1_NAME, interest.getName());
    }
    @Test(expected = DataAccessException.class)
    public void testCreateUserInterestMissingName(){
        interestDao.createUserInterest(null);
    }
    @Test(expected = DataAccessException.class)
    public void testCreateUserInterestDuplicate(){
        interestDao.createUserInterest(TestUtils.INTEREST_1_NAME);
    }

    @Test
    public void testDeleteUserInterest(){
        interestDao.deleteUserInterest(INTEREST_1.getId());

        assertEquals(TestUtils.TOTAL_INTERESTS - 1, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.INTEREST_TABLE));
    }
    @Test
    public void testDeleteUserInterestWrongInterest(){   
        interestDao.deleteUserInterest(12341234);

        assertEquals(TestUtils.TOTAL_INTERESTS, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.INTEREST_TABLE));
    }

    @Test
    public void testEditUserInterest(){
        interestDao.editUserInterest(INTEREST_1.getId(), TestUtils.INTEREST_NEW1_NAME);

        Interest interest = jdbcTemplate.queryForObject(
            TestUtils.INTEREST_SELECT_BY_ID, 
            TestUtils.INTEREST_ROW_MAPPER, 
            INTEREST_1.getId()
        );
        assertNotNull(interest);
        assertEquals(INTEREST_1.getId(), interest.getId());
        assertEquals(TestUtils.INTEREST_NEW1_NAME, interest.getName());
    }
    @Test
    public void testEditUserInterestNotFound(){
        interestDao.editUserInterest(12341234, TestUtils.INTEREST_1_NAME);

        List<Interest> interests = jdbcTemplate.query(TestUtils.INTEREST_SELECT + "ORDER BY id ASC", TestUtils.INTEREST_ROW_MAPPER);

        assertNotNull(interests);
        assertEquals(TestUtils.TOTAL_INTERESTS, interests.size());
        assertEquals(INTEREST_1.getId(), interests.get(0).getId());
        assertEquals(TestUtils.INTEREST_1_NAME, interests.get(0).getName());
        assertEquals(INTEREST_2.getId(), interests.get(1).getId());
        assertEquals(TestUtils.INTEREST_2_NAME, interests.get(1).getName());
    }

    @Test
    public void testSaveUserInterests(){
        List<Long> ids = new ArrayList<>();
        ids.add(INTEREST_1.getId());
        ids.add(INTEREST_2.getId());
        ids.add(INTEREST_3.getId());
        
        interestDao.saveUserInterests(ids.stream().mapToLong(l->l).toArray(), USER_2.getId());

        assertEquals(TestUtils.TOTAL_USER_INTERESTS + 3, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.USER_INTEREST_TABLE));
        List<Interest> interests = jdbcTemplate.query("""
            SELECT cat.id as interest_id, cat.name as interest_name
            FROM category cat JOIN user_interest ui ON ui.category_id = cat.id 
            WHERE ui.user_id = ? """,
            TestUtils.INTEREST_ROW_MAPPER, USER_2.getId()
        );
        assertNotNull(interests);
        assertEquals(ids.size(), interests.size());
        for (Interest i : interests){
            ids.contains(i.getId());
        }
    }
    @Test(expected = DataAccessException.class)
    public void testSaveUserInterestsWrongInterest(){
        long[] array = new long[3];
        array[0] = insertInterest.executeAndReturnKey(Map.of("name", TestUtils.INTEREST_1_NAME)).longValue();
        array[1] = insertInterest.executeAndReturnKey(Map.of("name", TestUtils.INTEREST_2_NAME)).longValue();
        array[2] = 12341234;
        
        interestDao.saveUserInterests(array, USER_1.getId());
    }
    @Test
    public void testSaveUserInterestsEmptyInterests(){
        interestDao.saveUserInterests(new long[0], USER_1.getId());
        
        assertEquals(TestUtils.TOTAL_INTERESTS, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.INTEREST_TABLE));
        assertEquals(TestUtils.TOTAL_USER_INTERESTS, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.USER_INTEREST_TABLE));
    }

    @Test
    public void testUpdateScoreByInterest(){
        insertUserInterest.execute(Map.of("user_id", USER_2.getId(), "category_id", INTEREST_1.getId(), "score", 0));
        insertUserInterest.execute(Map.of("user_id", USER_2.getId(), "category_id", INTEREST_2.getId(), "score", 3));
        
        interestDao.updateScoreByInterest(INTEREST_1, USER_2.getId());

        assertEquals(1, jdbcTemplate.queryForObject("SELECT score FROM user_interest WHERE category_id = ? AND user_id = ?", Integer.class, INTEREST_1.getId(), USER_2.getId()).intValue());
        assertEquals(3, jdbcTemplate.queryForObject("SELECT score FROM user_interest WHERE category_id = ? AND user_id = ?", Integer.class, INTEREST_2.getId(), USER_2.getId()).intValue());
    }
    @Test
    public void testUpdateScoreByInterest2(){
        insertUserInterest.execute(Map.of("user_id", USER_2.getId(), "category_id", INTEREST_1.getId(), "score", 0));
        insertUserInterest.execute(Map.of("user_id", USER_2.getId(), "category_id", INTEREST_2.getId(), "score", 3));
        
        interestDao.updateScoreByInterest(INTEREST_2, USER_2.getId());

        assertEquals(0, jdbcTemplate.queryForObject("SELECT score FROM user_interest WHERE category_id = ? AND user_id = ?", Integer.class, INTEREST_1.getId(), USER_2.getId()).intValue());
        assertEquals(4, jdbcTemplate.queryForObject("SELECT score FROM user_interest WHERE category_id = ? AND user_id = ?", Integer.class, INTEREST_2.getId(), USER_2.getId()).intValue());
    }
    @Test
    public void testUpdateScoreByInterestWrongInterest(){
        insertUserInterest.execute(Map.of("user_id", USER_2.getId(), "category_id", INTEREST_1.getId(), "score", 0));
        insertUserInterest.execute(Map.of("user_id", USER_2.getId(), "category_id", INTEREST_2.getId(), "score", 3));
        
        interestDao.updateScoreByInterest(new Interest((long)12341234, null), USER_1.getId());

        assertEquals(0, jdbcTemplate.queryForObject("SELECT score FROM user_interest WHERE category_id = ? AND user_id = ?", Integer.class, INTEREST_1.getId(), USER_2.getId()).intValue());
        assertEquals(3, jdbcTemplate.queryForObject("SELECT score FROM user_interest WHERE category_id = ? AND user_id = ?", Integer.class, INTEREST_2.getId(), USER_2.getId()).intValue());
    }
    @Test
    public void testUpdateScoreByInterestWrongUser(){
        interestDao.updateScoreByInterest(INTEREST_1, (long)12341234);

        assertEquals(TestUtils.USER_1_INTEREST_1_SCORE, jdbcTemplate.queryForObject("SELECT score FROM user_interest WHERE category_id = ? AND user_id = ?", Integer.class, INTEREST_1.getId(), USER_1.getId()).intValue());
        assertEquals(TestUtils.USER_1_INTEREST_2_SCORE, jdbcTemplate.queryForObject("SELECT score FROM user_interest WHERE category_id = ? AND user_id = ?", Integer.class, INTEREST_2.getId(), USER_1.getId()).intValue());
        assertEquals(TestUtils.USER_1_INTEREST_3_SCORE, jdbcTemplate.queryForObject("SELECT score FROM user_interest WHERE category_id = ? AND user_id = ?", Integer.class, INTEREST_3.getId(), USER_1.getId()).intValue());
    }

    @Test
    public void testUpdateScoreByInterestsMultiple(){
        insertUserInterest.execute(Map.of("user_id", USER_2.getId(), "category_id", INTEREST_1.getId(), "score", 0));
        insertUserInterest.execute(Map.of("user_id", USER_2.getId(), "category_id", INTEREST_2.getId(), "score", 3));
        insertUserInterest.execute(Map.of("user_id", USER_2.getId(), "category_id", INTEREST_3.getId(), "score", 3));
        List<Interest> interests = List.of(INTEREST_1, INTEREST_2);
        
        interestDao.updateScoreByInterests(interests, USER_2.getId());

        assertEquals(TestUtils.TOTAL_INTERESTS, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.INTEREST_TABLE));
        assertEquals(TestUtils.TOTAL_USER_INTERESTS + 3, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.USER_INTEREST_TABLE));

        assertEquals(1, jdbcTemplate.queryForObject("SELECT score FROM user_interest WHERE category_id = ? AND user_id = ?", Integer.class, INTEREST_1.getId(), USER_2.getId()).intValue());
        assertEquals(4, jdbcTemplate.queryForObject("SELECT score FROM user_interest WHERE category_id = ? AND user_id = ?", Integer.class, INTEREST_2.getId(), USER_2.getId()).intValue());
        assertEquals(3, jdbcTemplate.queryForObject("SELECT score FROM user_interest WHERE category_id = ? AND user_id = ?", Integer.class, INTEREST_3.getId(), USER_2.getId()).intValue());
    }
    @Test
    public void testUpdateScoreByInterestsMultipleDuplicated(){
        insertUserInterest.execute(Map.of("user_id", USER_2.getId(), "category_id", INTEREST_1.getId(), "score", 0));
        insertUserInterest.execute(Map.of("user_id", USER_2.getId(), "category_id", INTEREST_2.getId(), "score", 3));
        insertUserInterest.execute(Map.of("user_id", USER_2.getId(), "category_id", INTEREST_3.getId(), "score", 3));
        List<Interest> interests = List.of(INTEREST_1, INTEREST_2, INTEREST_2);
        
        interestDao.updateScoreByInterests(interests, USER_2.getId());

        assertEquals(TestUtils.TOTAL_INTERESTS, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.INTEREST_TABLE));
        assertEquals(TestUtils.TOTAL_USER_INTERESTS + 3, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.USER_INTEREST_TABLE));

        assertEquals(1, jdbcTemplate.queryForObject("SELECT score FROM user_interest WHERE category_id = ? AND user_id = ?", Integer.class, INTEREST_1.getId(), USER_2.getId()).intValue());
        assertEquals(5, jdbcTemplate.queryForObject("SELECT score FROM user_interest WHERE category_id = ? AND user_id = ?", Integer.class, INTEREST_2.getId(), USER_2.getId()).intValue());
        assertEquals(3, jdbcTemplate.queryForObject("SELECT score FROM user_interest WHERE category_id = ? AND user_id = ?", Integer.class, INTEREST_3.getId(), USER_2.getId()).intValue());
    }
    @Test
    public void testUpdateScoreByInterestsMultipleEmpty(){
        insertUserInterest.execute(Map.of("user_id", USER_2.getId(), "category_id", INTEREST_1.getId(), "score", 0));
        insertUserInterest.execute(Map.of("user_id", USER_2.getId(), "category_id", INTEREST_2.getId(), "score", 3));
        insertUserInterest.execute(Map.of("user_id", USER_2.getId(), "category_id", INTEREST_3.getId(), "score", 3));
        List<Interest> interests = List.of();
        
        interestDao.updateScoreByInterests(interests, USER_2.getId());

        assertEquals(TestUtils.TOTAL_INTERESTS, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.INTEREST_TABLE));
        assertEquals(TestUtils.TOTAL_USER_INTERESTS + 3, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.USER_INTEREST_TABLE));

        assertEquals(0, jdbcTemplate.queryForObject("SELECT score FROM user_interest WHERE category_id = ? AND user_id = ?", Integer.class, INTEREST_1.getId(), USER_2.getId()).intValue());
        assertEquals(3, jdbcTemplate.queryForObject("SELECT score FROM user_interest WHERE category_id = ? AND user_id = ?", Integer.class, INTEREST_2.getId(), USER_2.getId()).intValue());
        assertEquals(3, jdbcTemplate.queryForObject("SELECT score FROM user_interest WHERE category_id = ? AND user_id = ?", Integer.class, INTEREST_3.getId(), USER_2.getId()).intValue());
    }

    @Test
    public void testGetAllInterestsPaged(){
        Page<Interest> page1 = interestDao.getAllInterests(TestUtils.PAGE_1_DEFAULT);
        Page<Interest> page2 = interestDao.getAllInterests(TestUtils.PAGE_2_DEFAULT);

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
        assertEquals(INTEREST_1.getId(), page1.getContent().get(0).getId());
        assertEquals(TestUtils.INTEREST_1_NAME, page1.getContent().get(0).getName());
        assertEquals(INTEREST_2.getId(), page1.getContent().get(1).getId());
        assertEquals(TestUtils.INTEREST_2_NAME, page1.getContent().get(1).getName());
        assertEquals(INTEREST_3.getId(), page2.getContent().get(0).getId());
        assertEquals(TestUtils.INTEREST_3_NAME, page2.getContent().get(0).getName());
    }
    @Test
    public void testGetAllInterestsPagedNoInterests(){
        TestUtils.deleteInterests(jdbcTemplate);

        Page<Interest> page1 = interestDao.getAllInterests(new PageParams(1, 2));

        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(0, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(0, page1.getContent().size());
    }

    @Test
    public void testSearchBySubstringNoFiltering(){
        Page<Interest> page1 = interestDao.searchBySubstring(TestUtils.INTEREST_1_NAME.substring(0, 5), new PageParams(1, 3));

        assertNotNull(page1);
        assertEquals(1, page1.getTotalPages());
        assertEquals(3, page1.getContent().size());
    }
    @Test
    public void testSearchBySubstringFiltering(){
        Page<Interest> page1 = interestDao.searchBySubstring(TestUtils.INTEREST_1_NAME.substring(TestUtils.INTEREST_1_NAME.length()-1, TestUtils.INTEREST_1_NAME.length()), new PageParams(1, 3));

        assertNotNull(page1);
        assertEquals(1, page1.getTotalPages());
        assertEquals(1, page1.getContent().size());
    }
    @Test
    public void testSearchBySubstringEmpty(){
        Page<Interest> page1 = interestDao.searchBySubstring("", new PageParams(1, 3));

        assertNotNull(page1);
        assertEquals(1, page1.getTotalPages());
        assertEquals(3, page1.getContent().size());
    }
    @Test
    public void testSearchBySubstringMissing(){
        Page<Interest> page1 = interestDao.searchBySubstring(null, new PageParams(1, 3));

        assertNotNull(page1);
        assertEquals(1, page1.getTotalPages());
        assertEquals(3, page1.getContent().size());
    }
    @Test
    public void testSearchBySubstringPaging(){
        Page<Interest> page1 = interestDao.searchBySubstring("", TestUtils.PAGE_1_DEFAULT);
        Page<Interest> page2 = interestDao.searchBySubstring("", TestUtils.PAGE_2_DEFAULT);

        assertNotNull(page1);
        assertNotNull(page2);
        assertEquals(2, page1.getTotalPages());
        assertEquals(2, page2.getTotalPages());
        assertEquals(2, page1.getContent().size());
        assertEquals(1, page2.getContent().size());
    }

    @Test
    public void testDelete(){
        interestDao.delete(INTEREST_3.getId());

        assertEquals(TestUtils.TOTAL_INTERESTS - 1, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.INTEREST_TABLE));
    }
    @Test
    public void testDeleteWrongInterest(){
        interestDao.delete(12341234);

        assertEquals(TestUtils.TOTAL_INTERESTS, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.INTEREST_TABLE));
    }

    @Test
    public void testFindAllInterestsByUserId(){
        Page<Interest> interests = interestDao.findAllInterestsByUserId(USER_1.getId(), TestUtils.PAGE_1_BIG);

        assertNotNull(interests);
        assertEquals(1, interests.getCurrentPage());
        assertEquals(1, interests.getTotalPages());
        assertNotNull(interests.getContent());
        assertEquals(TestUtils.USER_1_INTERESTS, interests.getContent().size());
        assertEquals(INTEREST_1.getId(), interests.getContent().get(0).getId());
        assertEquals(TestUtils.INTEREST_1_NAME, interests.getContent().get(0).getName());
        assertEquals(INTEREST_2.getId(), interests.getContent().get(1).getId());
        assertEquals(TestUtils.INTEREST_2_NAME, interests.getContent().get(1).getName());
        assertEquals(INTEREST_3.getId(), interests.getContent().get(2).getId());
        assertEquals(TestUtils.INTEREST_3_NAME, interests.getContent().get(2).getName());
    }

}

