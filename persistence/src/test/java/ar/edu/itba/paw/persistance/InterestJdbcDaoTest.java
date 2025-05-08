package ar.edu.itba.paw.persistance;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.*;

import javax.sql.DataSource;

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
import ar.edu.itba.paw.persistence.InterestJdbcDao;

@Transactional
@Rollback
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class InterestJdbcDaoTest {

    private static Long USER_ID;
    private static Long INTEREST_1_ID;
    private static Long INTEREST_2_ID;

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

        jdbcTemplate.execute("INSERT INTO users(username, email, firstname, lastname, university, career_id, profile_picture_id) VALUES('username', 'user@name.com', 'user', 'name', (SELECT id FROM universities WHERE abbreviation = 'ITBA'), (SELECT id FROM careers LIMIT 1), (SELECT id FROM images LIMIT 1))");

        USER_ID = jdbcTemplate.queryForObject("SELECT id FROM users LIMIT 1", Long.class);
        INTEREST_1_ID = jdbcTemplate.queryForObject("SELECT id FROM category WHERE name = ?", Long.class, TestUtils.INTEREST_1_NAME);
        INTEREST_2_ID = jdbcTemplate.queryForObject("SELECT id FROM category WHERE name = ?", Long.class, TestUtils.INTEREST_2_NAME);
    }



    @Test
    public void testFindById(){
        Optional<Interest> maybeInterest = interestDao.findById(INTEREST_1_ID);

        assertNotNull(maybeInterest);
        assertTrue(maybeInterest.isPresent());
        Interest interest = maybeInterest.get();
        assertEquals(INTEREST_1_ID.longValue(), interest.getId().longValue());
        assertEquals(TestUtils.INTEREST_1_NAME, interest.getName());
    }
    @Test
    public void testFindByIdWrongId(){
        Optional<Interest> maybeInterest = interestDao.findById(12341234l);

        assertNotNull(maybeInterest);
        assertFalse(maybeInterest.isPresent());
    }

    @Test
    public void testFindAll(){
        List<Interest> interests = interestDao.findAll();

        assertNotNull(interests);
        assertEquals(2, interests.size());
        //TODO if-else
        for (Interest i : interests){
            if (i.getId() == INTEREST_1_ID) {
                assertEquals(TestUtils.INTEREST_1_NAME, i.getName());
            } else {
                assertEquals(INTEREST_2_ID.longValue(), i.getId().longValue());
                assertEquals(TestUtils.INTEREST_2_NAME, i.getName());
            }
        }
    }
    @Test    
    public void testFindAllNoInterests(){
        JdbcTestUtils.deleteFromTables(jdbcTemplate, TestUtils.INTEREST_TABLE);

        List<Interest> interests = interestDao.findAll();

        assertNotNull(interests);
        assertEquals(0, interests.size());
    }

    @Test
    public void testFindByUserId(){
        insertUserInterest.execute(Map.of("user_id", USER_ID, "category_id", INTEREST_1_ID, "score", 0));
        
        List<Interest> interests = interestDao.findByUserId(USER_ID);

        assertNotNull(interests);
        assertEquals(1, interests.size());
        assertEquals(INTEREST_1_ID.longValue(), interests.getFirst().getId().longValue());
        assertEquals(TestUtils.INTEREST_1_NAME, interests.getFirst().getName());
    }
    @Test
    public void testFindByUserIdNoUserInterests(){
        List<Interest> interests = interestDao.findByUserId(USER_ID);

        assertNotNull(interests);
        assertEquals(0, interests.size());
    }

    @Test
    public void testFindByName(){
        Optional<Interest> maybeInterest = interestDao.findByName(TestUtils.INTEREST_1_NAME);

        assertNotNull(maybeInterest);
        assertTrue(maybeInterest.isPresent());
        Interest interest = maybeInterest.get();
        assertEquals(INTEREST_1_ID.longValue(), interest.getId().longValue());
        assertEquals(TestUtils.INTEREST_1_NAME, interest.getName());
    }
    @Test
    public void testFindByNameWrongName(){
        Optional<Interest> maybeInterest = interestDao.findByName("asdfasdf");

        assertNotNull(maybeInterest);
        assertFalse(maybeInterest.isPresent());
    }

    @Test
    public void testFindIdByName(){
        insertInterest.execute(Map.of("name", TestUtils.INTEREST_NEW1_NAME));
        List<String> query = List.of( TestUtils.INTEREST_1_NAME, TestUtils.INTEREST_2_NAME);
        List<Interest> interests = interestDao.findIdByName(query);

        assertNotNull(interests);
        assertEquals(2, interests.size());
        for (Interest i : interests){
            List.of(query).contains(i.getName());
        }
    }
    @Test
    public void testFindIdByNameSomeNamesNotFound(){
        List<String> query = List.of( TestUtils.INTEREST_1_NAME, TestUtils.INTEREST_2_NAME, TestUtils.INTEREST_NEW2_NAME);
        List<Interest> interests = interestDao.findIdByName(query);

        assertNotNull(interests);
        assertEquals(2, interests.size());
    }
    @Test
    public void testFindIdByNameMissingNames(){
        List<Interest> interests = interestDao.findIdByName(null);

        assertNotNull(interests);
        assertEquals(0, interests.size());
    }
    @Test 
    public void testFindIdByNameEmptyNames(){
        List<Interest> interests = interestDao.findIdByName(new ArrayList<>());

        assertNotNull(interests);
        assertEquals(0, interests.size());
    }
    @Test(expected = IllegalArgumentException.class)
    public void testFindIdByNameMissingNameInArray(){
        List<String> query = List.of(TestUtils.INTEREST_1_NAME, TestUtils.INTEREST_2_NAME, null);

        interestDao.findIdByName(query);
    }
    @Test(expected = IllegalArgumentException.class)
    public void testFindIdByNameEmptyNameInArray(){
        List<String> query = List.of(TestUtils.INTEREST_1_NAME, TestUtils.INTEREST_2_NAME, "");

        interestDao.findIdByName(query);
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
        insertInterest.execute(Map.of("name", TestUtils.INTEREST_1_NAME));
        interestDao.createUserInterest(TestUtils.INTEREST_1_NAME);
    }

    @Test
    public void testDeleteUserInterest(){
        interestDao.deleteUserInterest(INTEREST_1_ID);

        assertEquals(TestUtils.TOTAL_INTERESTS - 1, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.INTEREST_TABLE));
    }
    @Test
    public void testDeleteUserInterestWrongInterest(){   
        interestDao.deleteUserInterest(12341234);

        assertEquals(2, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.INTEREST_TABLE));
    }

    @Test
    public void testEditUserInterest(){
        interestDao.editUserInterest(INTEREST_1_ID, TestUtils.INTEREST_NEW1_NAME);

        Optional<Interest> maybeInterest = jdbcTemplate.query(
            "SELECT * FROM category", 
            (rs, rowNum) -> new Interest(rs.getLong("id"), rs.getString("name"))
        ).stream().findFirst();

        assertNotNull(maybeInterest);
        assertTrue(maybeInterest.isPresent());
        assertEquals(INTEREST_1_ID.longValue(), maybeInterest.get().getId().longValue());
        assertEquals(TestUtils.INTEREST_NEW1_NAME, maybeInterest.get().getName());
    }
    @Test
    public void testEditUserInterestNotFound(){
        interestDao.editUserInterest(12341234, TestUtils.INTEREST_1_NAME);

        Optional<Interest> maybeInterest = jdbcTemplate.query(
            "SELECT * FROM category", 
            (rs, rowNum) -> new Interest(rs.getLong("id"), rs.getString("name"))
        ).stream().findFirst();

        assertNotNull(maybeInterest);
        assertTrue(maybeInterest.isPresent());
        assertEquals(INTEREST_1_ID.longValue(), maybeInterest.get().getId().longValue());
        assertEquals(TestUtils.INTEREST_1_NAME, maybeInterest.get().getName());
    }

    @SuppressWarnings("unlikely-arg-type")
    @Test
    public void testSaveUserInterests(){
        long[] array = new long[3];
        array[0] = INTEREST_1_ID;
        array[1] = INTEREST_2_ID;
        array[2] = insertInterest.executeAndReturnKey(Map.of("name", TestUtils.INTEREST_NEW1_NAME)).longValue();
        
        interestDao.saveUserInterests(array, USER_ID);

        assertEquals(3, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.USER_INTEREST_TABLE));
        List<Interest> interests = jdbcTemplate.query("""
            SELECT cat.id as id, cat.name as name
            FROM category cat INNER JOIN user_interest ui ON ui.category_id = cat.id 
            WHERE ui.user_id = ? """,
            (rs, rowNum) -> new Interest(rs.getLong("id"), rs.getString("name")), USER_ID
        );
        assertNotNull(interests);
        assertEquals(3, interests.size());
        for (Interest i : interests){
            Arrays.asList(array).contains(i.getId());
        }
    }
    @Test(expected = DataAccessException.class)
    public void testSaveUserInterestsWrongInterest(){
        long[] array = new long[3];
        array[0] = insertInterest.executeAndReturnKey(Map.of("name", TestUtils.INTEREST_1_NAME)).longValue();
        array[1] = insertInterest.executeAndReturnKey(Map.of("name", TestUtils.INTEREST_2_NAME)).longValue();
        array[2] = 12341234;
        
        interestDao.saveUserInterests(array, USER_ID);
    }
    @Test
    public void testSaveUserInterestsEmptyInterests(){
        long[] array = new long[0];

        interestDao.saveUserInterests(array, USER_ID);
        
        assertEquals(2, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.INTEREST_TABLE));
        assertEquals(0, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.USER_INTEREST_TABLE));
    }

    @Test
    public void testUpdateScoreByInterest(){
        insertUserInterest.execute(Map.of("user_id", USER_ID, "category_id", INTEREST_1_ID, "score", 0));
        insertUserInterest.execute(Map.of("user_id", USER_ID, "category_id", INTEREST_2_ID, "score", 3));
        
        interestDao.updateScoreByInterest(new Interest(INTEREST_1_ID, null), USER_ID);

        assertEquals(1, jdbcTemplate.queryForObject("SELECT score FROM user_interest WHERE category_id = ?", Integer.class, INTEREST_1_ID).intValue());
        assertEquals(3, jdbcTemplate.queryForObject("SELECT score FROM user_interest WHERE category_id = ?", Integer.class, INTEREST_2_ID).intValue());
    }
    @Test
    public void testUpdateScoreByInterest2(){
        insertUserInterest.execute(Map.of("user_id", USER_ID, "category_id", INTEREST_1_ID, "score", 0));
        insertUserInterest.execute(Map.of("user_id", USER_ID, "category_id", INTEREST_2_ID, "score", 3));
        
        interestDao.updateScoreByInterest(new Interest(INTEREST_2_ID, null), USER_ID);

        assertEquals(0, jdbcTemplate.queryForObject("SELECT score FROM user_interest WHERE category_id = ?", Integer.class, INTEREST_1_ID).intValue());
        assertEquals(4, jdbcTemplate.queryForObject("SELECT score FROM user_interest WHERE category_id = ?", Integer.class, INTEREST_2_ID).intValue());
    }
    @Test
    public void testUpdateScoreByInterestWrongInterest(){
        insertUserInterest.execute(Map.of("user_id", USER_ID, "category_id", INTEREST_1_ID, "score", 0));
        insertUserInterest.execute(Map.of("user_id", USER_ID, "category_id", INTEREST_2_ID, "score", 3));
        
        interestDao.updateScoreByInterest(new Interest((long)12341234, null), USER_ID);

        assertEquals(0, jdbcTemplate.queryForObject("SELECT score FROM user_interest WHERE category_id = ?", Integer.class, INTEREST_1_ID).intValue());
        assertEquals(3, jdbcTemplate.queryForObject("SELECT score FROM user_interest WHERE category_id = ?", Integer.class, INTEREST_2_ID).intValue());
    }
    @Test
    public void testUpdateScoreByInterestWrongUser(){
        insertUserInterest.execute(Map.of("user_id", USER_ID, "category_id", INTEREST_1_ID, "score", 0));
        insertUserInterest.execute(Map.of("user_id", USER_ID, "category_id", INTEREST_2_ID, "score", 3));
        
        interestDao.updateScoreByInterest(new Interest(INTEREST_1_ID, null), (long)12341234);

        assertEquals(0, jdbcTemplate.queryForObject("SELECT score FROM user_interest WHERE category_id = ?", Integer.class, INTEREST_1_ID).intValue());
        assertEquals(3, jdbcTemplate.queryForObject("SELECT score FROM user_interest WHERE category_id = ?", Integer.class, INTEREST_2_ID).intValue());
    }

    @Test
    public void testUpdateScoreByInterestsMultiple(){
        long id3 = insertInterest.executeAndReturnKey(Map.of("name", TestUtils.INTEREST_NEW1_NAME)).longValue();
        insertUserInterest.execute(Map.of("user_id", USER_ID, "category_id", INTEREST_1_ID, "score", 0));
        insertUserInterest.execute(Map.of("user_id", USER_ID, "category_id", INTEREST_2_ID, "score", 3));
        insertUserInterest.execute(Map.of("user_id", USER_ID, "category_id", id3, "score", 3));
        List<Interest> interests = List.of(new Interest(INTEREST_1_ID, null), new Interest(INTEREST_2_ID, null));
        
        interestDao.updateScoreByInterests(interests, USER_ID);

        assertEquals(3, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.INTEREST_TABLE));
        assertEquals(3, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.USER_INTEREST_TABLE));
        assertEquals(1, jdbcTemplate.queryForObject("SELECT score FROM user_interest WHERE category_id = ?", Integer.class, INTEREST_1_ID).longValue());
        assertEquals(4, jdbcTemplate.queryForObject("SELECT score FROM user_interest WHERE category_id = ?", Integer.class, INTEREST_2_ID).longValue());
        assertEquals(3, jdbcTemplate.queryForObject("SELECT score FROM user_interest WHERE category_id = ?", Integer.class, id3).longValue());
    }
    @Test
    public void testUpdateScoreByInterestsMultipleDuplicated(){
        long id3 = insertInterest.executeAndReturnKey(Map.of("name", TestUtils.INTEREST_NEW1_NAME)).longValue();
        insertUserInterest.execute(Map.of("user_id", USER_ID, "category_id", INTEREST_1_ID, "score", 0));
        insertUserInterest.execute(Map.of("user_id", USER_ID, "category_id", INTEREST_2_ID, "score", 3));
        insertUserInterest.execute(Map.of("user_id", USER_ID, "category_id", id3, "score", 3));
        List<Interest> interests = List.of(new Interest(INTEREST_1_ID, null), new Interest(INTEREST_2_ID, null), new Interest(INTEREST_2_ID, null));
        
        interestDao.updateScoreByInterests(interests, USER_ID);

        assertEquals(3, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.INTEREST_TABLE));
        assertEquals(3, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.USER_INTEREST_TABLE));
        assertEquals(1, jdbcTemplate.queryForObject("SELECT score FROM user_interest WHERE category_id = ?", Integer.class, INTEREST_1_ID).longValue());
        assertEquals(5, jdbcTemplate.queryForObject("SELECT score FROM user_interest WHERE category_id = ?", Integer.class, INTEREST_2_ID).longValue());
        assertEquals(3, jdbcTemplate.queryForObject("SELECT score FROM user_interest WHERE category_id = ?", Integer.class, id3).longValue());
    }
    @Test
    public void testUpdateScoreByInterestsMultipleEmpty(){
        long id3 = insertInterest.executeAndReturnKey(Map.of("name", TestUtils.INTEREST_NEW1_NAME)).longValue();
        insertUserInterest.execute(Map.of("user_id", USER_ID, "category_id", INTEREST_1_ID, "score", 0));
        insertUserInterest.execute(Map.of("user_id", USER_ID, "category_id", INTEREST_2_ID, "score", 3));
        insertUserInterest.execute(Map.of("user_id", USER_ID, "category_id", id3, "score", 3));
        List<Interest> interests = List.of();
        
        interestDao.updateScoreByInterests(interests, USER_ID);

        assertEquals(3, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.INTEREST_TABLE));
        assertEquals(3, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.USER_INTEREST_TABLE));
        assertEquals(0, jdbcTemplate.queryForObject("SELECT score FROM user_interest WHERE category_id = ?", Integer.class, INTEREST_1_ID).longValue());
        assertEquals(3, jdbcTemplate.queryForObject("SELECT score FROM user_interest WHERE category_id = ?", Integer.class, INTEREST_2_ID).longValue());
        assertEquals(3, jdbcTemplate.queryForObject("SELECT score FROM user_interest WHERE category_id = ?", Integer.class, id3).longValue());
    }

    @Test
    public void testGetAllInterestsPaged(){
        long id3 = insertInterest.executeAndReturnKey(Map.of("name", TestUtils.INTEREST_NEW1_NAME)).longValue();

        Page<Interest> page1 = interestDao.getAllInterests(1, 2);
        Page<Interest> page2 = interestDao.getAllInterests(2, 2);

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
        assertEquals(INTEREST_1_ID.longValue(), page1.getContent().get(0).getId().longValue());
        assertEquals(TestUtils.INTEREST_1_NAME, page1.getContent().get(0).getName());
        assertEquals(INTEREST_2_ID.longValue(), page1.getContent().get(1).getId().longValue());
        assertEquals(TestUtils.INTEREST_2_NAME, page1.getContent().get(1).getName());
        assertEquals(id3, page2.getContent().get(0).getId().longValue());
        assertEquals(TestUtils.INTEREST_NEW1_NAME, page2.getContent().get(0).getName());
    }
    @Test
    public void testGetAllInterestsPagedNoInterests(){
        JdbcTestUtils.deleteFromTables(jdbcTemplate, TestUtils.INTEREST_TABLE);
        Page<Interest> page1 = interestDao.getAllInterests(1, 2);

        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(0, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(0, page1.getContent().size());
    }

    @Test
    public void testSearchBySubstringNoFiltering(){
        insertInterest.execute(Map.of("name", TestUtils.INTEREST_NEW1_NAME));

        Page<Interest> page1 = interestDao.searchBySubstring(TestUtils.INTEREST_1_NAME.substring(0, 5), 1, 3);

        assertNotNull(page1);
        assertEquals(1, page1.getTotalPages());
        assertEquals(3, page1.getContent().size());
    }
    @Test
    public void testSearchBySubstringFiltering(){
        Page<Interest> page1 = interestDao.searchBySubstring(TestUtils.INTEREST_1_NAME.substring(TestUtils.INTEREST_1_NAME.length()-1, TestUtils.INTEREST_1_NAME.length()), 1, 3);

        assertNotNull(page1);
        assertEquals(1, page1.getTotalPages());
        assertEquals(1, page1.getContent().size());
    }
    @Test
    public void testSearchBySubstringEmpty(){
        insertInterest.execute(Map.of("name", TestUtils.INTEREST_NEW1_NAME));

        Page<Interest> page1 = interestDao.searchBySubstring("", 1, 3);

        assertNotNull(page1);
        assertEquals(1, page1.getTotalPages());
        assertEquals(3, page1.getContent().size());
    }
    @Test
    public void testSearchBySubstringMissing(){
        insertInterest.execute(Map.of("name", TestUtils.INTEREST_NEW1_NAME));

        Page<Interest> page1 = interestDao.searchBySubstring(null, 1, 3);

        assertNotNull(page1);
        assertEquals(1, page1.getTotalPages());
        assertEquals(3, page1.getContent().size());
    }
    @Test
    public void testSearchBySubstringPaging(){
        insertInterest.execute(Map.of("name", TestUtils.INTEREST_NEW1_NAME));

        Page<Interest> page1 = interestDao.searchBySubstring("", 1, 2);
        Page<Interest> page2 = interestDao.searchBySubstring("", 2, 2);

        assertNotNull(page1);
        assertNotNull(page2);
        assertEquals(2, page1.getTotalPages());
        assertEquals(2, page2.getTotalPages());
        assertEquals(2, page1.getContent().size());
        assertEquals(1, page2.getContent().size());
    }

    @SuppressWarnings("null")
    @Test
    public void testDelete(){
        long idToDelete = insertInterest.executeAndReturnKey(Map.of("name", TestUtils.INTEREST_NEW1_NAME)).longValue();

        interestDao.delete(idToDelete);

        assertEquals(2, jdbcTemplate.queryForObject("SELECT COUNT(*) FROM category",Integer.class).intValue());
    }
    @SuppressWarnings("null")
    @Test
    public void testDeleteWrongInterest(){
        interestDao.delete(12341234);

        assertEquals(2, jdbcTemplate.queryForObject("SELECT COUNT(*) FROM category",Integer.class).intValue());
    }

    @Test
    public void testFindAllInterestsByUserId(){
        insertUserInterest.execute(Map.of("user_id", USER_ID, "category_id", INTEREST_1_ID, "score", 0));

        Page<Interest> interests = interestDao.findAllInterestsByUserId(USER_ID, 1, 2);

        assertNotNull(interests);
        assertEquals(1, interests.getCurrentPage());
        assertEquals(1, interests.getTotalPages());
        assertNotNull(interests.getContent());
        assertEquals(1, interests.getContent().size());
        assertEquals(INTEREST_1_ID.longValue(), interests.getContent().getFirst().getId().longValue());
        assertEquals(TestUtils.INTEREST_1_NAME, interests.getContent().getFirst().getName());
    }

}

