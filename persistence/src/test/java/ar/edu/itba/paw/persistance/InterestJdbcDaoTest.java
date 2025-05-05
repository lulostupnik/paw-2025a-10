package ar.edu.itba.paw.persistance;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    private static final String INTEREST_TABLE = "category";
    private static final String USER_INTEREST_TABLE = "user_interest";
    private static final String INTEREST_1 = "interest 1";
    private static final String INTEREST_2 = "interest 2";
    private static final String INTEREST_3 = "interest 3";
    private static final String INTEREST_4 = "interest 3";
    private static Long USER_ID;

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
        insertInterest = new SimpleJdbcInsert(ds).withTableName(INTEREST_TABLE).usingGeneratedKeyColumns("id");
        insertUserInterest = new SimpleJdbcInsert(ds).withTableName(USER_INTEREST_TABLE);   

        jdbcTemplate.execute("INSERT INTO countries(name, code) VALUES('Argentina', 'AR')");
        jdbcTemplate.execute("INSERT INTO cities(name, country_id) VALUES('Buenos Aires', (SELECT id FROM countries WHERE code = 'AR'))");
        jdbcTemplate.execute("INSERT INTO universities(name, abbreviation, city_id) VALUES('Instituto tecnologico muy largo', 'ITBA', (SELECT id FROM cities WHERE name = 'Buenos Aires'))");
        jdbcTemplate.execute("INSERT INTO images(content) VALUES('ffffffff')");
        jdbcTemplate.execute("INSERT INTO careers(name) VALUES('Ingenieria informatica')");
        jdbcTemplate.execute("INSERT INTO users(username, email, firstname, lastname, university, career_id, profile_picture_id) VALUES('username', 'user@name.com', 'user', 'name', (SELECT id FROM universities WHERE abbreviation = 'ITBA'), (SELECT id FROM careers LIMIT 1), (SELECT id FROM images LIMIT 1))");

        USER_ID = jdbcTemplate.queryForObject("SELECT id FROM users LIMIT 1", Long.class);
    }



    @Test
    public void testFindById(){
        long id = insertInterest.executeAndReturnKey(Map.of("name", INTEREST_1)).longValue();
        insertInterest.execute(Map.of("name", INTEREST_2));

        Optional<Interest> maybeInterest = interestDao.findById(id);

        assertNotNull(maybeInterest);
        assertTrue(maybeInterest.isPresent());
        Interest interest = maybeInterest.get();
        assertEquals(id, interest.getId().longValue());
        assertEquals(INTEREST_1, interest.getName());
    }
    @Test
    public void testFindByIdWrongId(){
        insertInterest.execute(Map.of("name", INTEREST_1));
        insertInterest.execute(Map.of("name", INTEREST_2));

        Optional<Interest> maybeInterest = interestDao.findById((long)12341234);

        assertNotNull(maybeInterest);
        assertFalse(maybeInterest.isPresent());
    }

    @Test
    public void testFindAll(){
        long id1 = insertInterest.executeAndReturnKey(Map.of("name", INTEREST_1)).longValue();
        long id2 = insertInterest.executeAndReturnKey(Map.of("name", INTEREST_2)).longValue();

        List<Interest> interests = interestDao.findAll();

        assertNotNull(interests);
        assertEquals(2, interests.size());
        for (Interest i : interests){
            if (i.getId() == id1) {
                assertEquals(INTEREST_1, i.getName());
            } else {
                assertEquals(id2, i.getId().longValue());
                assertEquals(INTEREST_2, i.getName());
            }
        }
    }
    @Test    
    public void testFindAllNoInterests(){
        List<Interest> interests = interestDao.findAll();

        assertNotNull(interests);
        assertEquals(0, interests.size());
    }

    @Test
    public void testFindByUserId(){
        long id1 = insertInterest.executeAndReturnKey(Map.of("name", INTEREST_1)).longValue();
        insertInterest.execute(Map.of("name", INTEREST_2));
        insertUserInterest.execute(Map.of("user_id", USER_ID, "category_id", id1, "score", 0));
        
        List<Interest> interests = interestDao.findByUserId(USER_ID);

        assertNotNull(interests);
        assertEquals(1, interests.size());
        assertEquals(id1, interests.getFirst().getId().longValue());
        assertEquals(INTEREST_1, interests.getFirst().getName());
    }
    @Test
    public void testFindByUserIdNoUserInterests(){
        insertInterest.execute(Map.of("name", INTEREST_1));
        insertInterest.execute(Map.of("name", INTEREST_2));
        
        List<Interest> interests = interestDao.findByUserId(USER_ID);

        assertNotNull(interests);
        assertEquals(0, interests.size());
    }

    @Test
    public void testFindByName(){
        long id1 = insertInterest.executeAndReturnKey(Map.of("name", INTEREST_1)).longValue();
        insertInterest.execute(Map.of("name", INTEREST_2));
        
        Optional<Interest> maybeInterest = interestDao.findByName(INTEREST_1);

        assertNotNull(maybeInterest);
        assertTrue(maybeInterest.isPresent());
        Interest interest = maybeInterest.get();
        assertEquals(id1, interest.getId().longValue());
        assertEquals(INTEREST_1, interest.getName());
    }
    @Test
    public void testFindByNameWrongName(){
        insertInterest.execute(Map.of("name", INTEREST_1));

        Optional<Interest> maybeInterest = interestDao.findByName("asdfasdf");

        assertNotNull(maybeInterest);
        assertFalse(maybeInterest.isPresent());
    }

    @Test
    public void testFindIdByName(){
        insertInterest.execute(Map.of("name", INTEREST_1));
        insertInterest.execute(Map.of("name", INTEREST_2));
        insertInterest.execute(Map.of("name", INTEREST_3));
        String[] query = {INTEREST_1, INTEREST_2};
        List<Interest> interests = interestDao.findIdByName(query);

        assertNotNull(interests);
        assertEquals(2, interests.size());
        for (Interest i : interests){
            List.of(query).contains(i.getName());
        }
    }
    @Test
    public void testFindIdByNameSomeNamesNotFound(){
        insertInterest.execute(Map.of("name", INTEREST_1));
        insertInterest.execute(Map.of("name", INTEREST_2));

        String[] query = {INTEREST_1, INTEREST_2, INTEREST_4};
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
        List<Interest> interests = interestDao.findIdByName(new String[0]);

        assertNotNull(interests);
        assertEquals(0, interests.size());
    }
    @Test(expected = IllegalArgumentException.class)
    public void testFindIdByNameMissingNameInArray(){
        String[] query = {INTEREST_1, INTEREST_2, null};

        interestDao.findIdByName(query);
    }
    @Test(expected = IllegalArgumentException.class)
    public void testFindIdByNameEmptyNameInArray(){
        String[] query = {INTEREST_1, INTEREST_2, ""};

        interestDao.findIdByName(query);
    }

    @Test
    public void testCreateUserInterest(){
        Interest interest = interestDao.createUserInterest(INTEREST_1);

        assertNotNull(interest);
        assertEquals(INTEREST_1, interest.getName());
    }
    @Test(expected = DataAccessException.class)
    public void testCreateUserInterestMissingName(){
        interestDao.createUserInterest(null);
    }
    @Test(expected = DataAccessException.class)
    public void testCreateUserInterestDuplicate(){
        insertInterest.execute(Map.of("name", INTEREST_1));
        interestDao.createUserInterest(INTEREST_1);
    }

    @Test
    public void testDeleteUserInterest(){
        insertInterest.execute(Map.of("name", INTEREST_1));
        long id = insertInterest.executeAndReturnKey(Map.of("name", INTEREST_2)).longValue();
        
        interestDao.deleteUserInterest(id);

        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, INTEREST_TABLE));
    }
    @Test
    public void testDeleteUserInterestWrongInterest(){
        insertInterest.execute(Map.of("name", INTEREST_1));
        insertInterest.execute(Map.of("name", INTEREST_2));
        
        interestDao.deleteUserInterest(12341234);

        assertEquals(2, JdbcTestUtils.countRowsInTable(jdbcTemplate, INTEREST_TABLE));
    }

    @Test
    public void testEditUserInterest(){
        long id = insertInterest.executeAndReturnKey(Map.of("name", INTEREST_2)).longValue();

        interestDao.editUserInterest(id, INTEREST_1);

        Optional<Interest> maybeInterest = jdbcTemplate.query(
            "SELECT * FROM category", 
            (rs, rowNum) -> new Interest(rs.getLong("id"), rs.getString("name"))
        ).stream().findFirst();

        assertNotNull(maybeInterest);
        assertTrue(maybeInterest.isPresent());
        assertEquals(id, maybeInterest.get().getId().longValue());
        assertEquals(INTEREST_1, maybeInterest.get().getName());
    }

    @SuppressWarnings("unlikely-arg-type")
    @Test
    public void testSaveUserInterests(){
        long[] array = new long[3];
        array[0] = insertInterest.executeAndReturnKey(Map.of("name", INTEREST_1)).longValue();
        array[1] = insertInterest.executeAndReturnKey(Map.of("name", INTEREST_2)).longValue();
        array[2] = insertInterest.executeAndReturnKey(Map.of("name", INTEREST_3)).longValue();
        
        interestDao.saveUserInterests(array, USER_ID);

        assertEquals(3, JdbcTestUtils.countRowsInTable(jdbcTemplate, USER_INTEREST_TABLE));
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
        array[0] = insertInterest.executeAndReturnKey(Map.of("name", INTEREST_1)).longValue();
        array[1] = insertInterest.executeAndReturnKey(Map.of("name", INTEREST_2)).longValue();
        array[2] = 12341234;
        
        interestDao.saveUserInterests(array, USER_ID);
    }
    @Test
    public void testSaveUserInterestsEmptyInterests(){
        long[] array = new long[0];
        insertInterest.execute(Map.of("name", INTEREST_1));
        insertInterest.execute(Map.of("name", INTEREST_2));

        interestDao.saveUserInterests(array, USER_ID);
        
        assertEquals(2, JdbcTestUtils.countRowsInTable(jdbcTemplate, INTEREST_TABLE));
        assertEquals(0, JdbcTestUtils.countRowsInTable(jdbcTemplate, USER_INTEREST_TABLE));
    }

    @Test
    public void testUpdateScoreByInterest(){
        long id1 = insertInterest.executeAndReturnKey(Map.of("name", INTEREST_1)).longValue();
        long id2 = insertInterest.executeAndReturnKey(Map.of("name", INTEREST_2)).longValue();
        insertUserInterest.execute(Map.of("user_id", USER_ID, "category_id", id1, "score", 0));
        insertUserInterest.execute(Map.of("user_id", USER_ID, "category_id", id2, "score", 3));
        
        interestDao.updateScoreByInterest(new Interest(id1, null), USER_ID);

        assertEquals(1, jdbcTemplate.queryForObject("SELECT score FROM user_interest WHERE category_id = ?", Integer.class, id1).intValue());
        assertEquals(3, jdbcTemplate.queryForObject("SELECT score FROM user_interest WHERE category_id = ?", Integer.class, id2).intValue());
    }
    @Test
    public void testUpdateScoreByInterest2(){
        long id1 = insertInterest.executeAndReturnKey(Map.of("name", INTEREST_1)).longValue();
        long id2 = insertInterest.executeAndReturnKey(Map.of("name", INTEREST_2)).longValue();
        insertUserInterest.execute(Map.of("user_id", USER_ID, "category_id", id1, "score", 0));
        insertUserInterest.execute(Map.of("user_id", USER_ID, "category_id", id2, "score", 3));
        
        interestDao.updateScoreByInterest(new Interest(id2, null), USER_ID);

        assertEquals(0, jdbcTemplate.queryForObject("SELECT score FROM user_interest WHERE category_id = ?", Integer.class, id1).intValue());
        assertEquals(4, jdbcTemplate.queryForObject("SELECT score FROM user_interest WHERE category_id = ?", Integer.class, id2).intValue());
    }
    @Test
    public void testUpdateScoreByInterestWrongInterest(){
        long id1 = insertInterest.executeAndReturnKey(Map.of("name", INTEREST_1)).longValue();
        long id2 = insertInterest.executeAndReturnKey(Map.of("name", INTEREST_2)).longValue();
        insertUserInterest.execute(Map.of("user_id", USER_ID, "category_id", id1, "score", 0));
        insertUserInterest.execute(Map.of("user_id", USER_ID, "category_id", id2, "score", 3));
        
        interestDao.updateScoreByInterest(new Interest((long)12341234, null), USER_ID);

        assertEquals(0, jdbcTemplate.queryForObject("SELECT score FROM user_interest WHERE category_id = ?", Integer.class, id1).intValue());
        assertEquals(3, jdbcTemplate.queryForObject("SELECT score FROM user_interest WHERE category_id = ?", Integer.class, id2).intValue());
    }
    @Test
    public void testUpdateScoreByInterestWrongUser(){
        long id1 = insertInterest.executeAndReturnKey(Map.of("name", INTEREST_1)).longValue();
        long id2 = insertInterest.executeAndReturnKey(Map.of("name", INTEREST_2)).longValue();
        insertUserInterest.execute(Map.of("user_id", USER_ID, "category_id", id1, "score", 0));
        insertUserInterest.execute(Map.of("user_id", USER_ID, "category_id", id2, "score", 3));
        
        interestDao.updateScoreByInterest(new Interest(id1, null), (long)12341234);

        assertEquals(0, jdbcTemplate.queryForObject("SELECT score FROM user_interest WHERE category_id = ?", Integer.class, id1).intValue());
        assertEquals(3, jdbcTemplate.queryForObject("SELECT score FROM user_interest WHERE category_id = ?", Integer.class, id2).intValue());
    }

    @Test
    public void testUpdateScoreByInterestsMultiple(){
        long id1 = insertInterest.executeAndReturnKey(Map.of("name", INTEREST_1)).longValue();
        long id2 = insertInterest.executeAndReturnKey(Map.of("name", INTEREST_2)).longValue();
        long id3 = insertInterest.executeAndReturnKey(Map.of("name", INTEREST_3)).longValue();
        insertUserInterest.execute(Map.of("user_id", USER_ID, "category_id", id1, "score", 0));
        insertUserInterest.execute(Map.of("user_id", USER_ID, "category_id", id2, "score", 3));
        insertUserInterest.execute(Map.of("user_id", USER_ID, "category_id", id3, "score", 3));
        List<Interest> interests = List.of(new Interest(id1, null), new Interest(id2, null));
        
        interestDao.updateScoreByInterests(interests, USER_ID);

        assertEquals(3, JdbcTestUtils.countRowsInTable(jdbcTemplate, INTEREST_TABLE));
        assertEquals(3, JdbcTestUtils.countRowsInTable(jdbcTemplate, USER_INTEREST_TABLE));
        assertEquals(1, jdbcTemplate.queryForObject("SELECT score FROM user_interest WHERE category_id = ?", Integer.class, id1).longValue());
        assertEquals(4, jdbcTemplate.queryForObject("SELECT score FROM user_interest WHERE category_id = ?", Integer.class, id2).longValue());
        assertEquals(3, jdbcTemplate.queryForObject("SELECT score FROM user_interest WHERE category_id = ?", Integer.class, id3).longValue());
    }
    @Test
    public void testUpdateScoreByInterestsMultipleDuplicated(){
        long id1 = insertInterest.executeAndReturnKey(Map.of("name", INTEREST_1)).longValue();
        long id2 = insertInterest.executeAndReturnKey(Map.of("name", INTEREST_2)).longValue();
        long id3 = insertInterest.executeAndReturnKey(Map.of("name", INTEREST_3)).longValue();
        insertUserInterest.execute(Map.of("user_id", USER_ID, "category_id", id1, "score", 0));
        insertUserInterest.execute(Map.of("user_id", USER_ID, "category_id", id2, "score", 3));
        insertUserInterest.execute(Map.of("user_id", USER_ID, "category_id", id3, "score", 3));
        List<Interest> interests = List.of(new Interest(id1, null), new Interest(id2, null), new Interest(id2, null));
        
        interestDao.updateScoreByInterests(interests, USER_ID);

        assertEquals(3, JdbcTestUtils.countRowsInTable(jdbcTemplate, INTEREST_TABLE));
        assertEquals(3, JdbcTestUtils.countRowsInTable(jdbcTemplate, USER_INTEREST_TABLE));
        assertEquals(1, jdbcTemplate.queryForObject("SELECT score FROM user_interest WHERE category_id = ?", Integer.class, id1).longValue());
        assertEquals(5, jdbcTemplate.queryForObject("SELECT score FROM user_interest WHERE category_id = ?", Integer.class, id2).longValue());
        assertEquals(3, jdbcTemplate.queryForObject("SELECT score FROM user_interest WHERE category_id = ?", Integer.class, id3).longValue());
    }
    @Test
    public void testUpdateScoreByInterestsMultipleEmpty(){
        long id1 = insertInterest.executeAndReturnKey(Map.of("name", INTEREST_1)).longValue();
        long id2 = insertInterest.executeAndReturnKey(Map.of("name", INTEREST_2)).longValue();
        long id3 = insertInterest.executeAndReturnKey(Map.of("name", INTEREST_3)).longValue();
        insertUserInterest.execute(Map.of("user_id", USER_ID, "category_id", id1, "score", 0));
        insertUserInterest.execute(Map.of("user_id", USER_ID, "category_id", id2, "score", 3));
        insertUserInterest.execute(Map.of("user_id", USER_ID, "category_id", id3, "score", 3));
        List<Interest> interests = List.of();
        
        interestDao.updateScoreByInterests(interests, USER_ID);

        assertEquals(3, JdbcTestUtils.countRowsInTable(jdbcTemplate, INTEREST_TABLE));
        assertEquals(3, JdbcTestUtils.countRowsInTable(jdbcTemplate, USER_INTEREST_TABLE));
        assertEquals(0, jdbcTemplate.queryForObject("SELECT score FROM user_interest WHERE category_id = ?", Integer.class, id1).longValue());
        assertEquals(3, jdbcTemplate.queryForObject("SELECT score FROM user_interest WHERE category_id = ?", Integer.class, id2).longValue());
        assertEquals(3, jdbcTemplate.queryForObject("SELECT score FROM user_interest WHERE category_id = ?", Integer.class, id3).longValue());
    }

    @Test
    public void testGetAllInterestsPaged(){
        long id1 = insertInterest.executeAndReturnKey(Map.of("name", INTEREST_1)).longValue();
        long id2 = insertInterest.executeAndReturnKey(Map.of("name", INTEREST_2)).longValue();
        long id3 = insertInterest.executeAndReturnKey(Map.of("name", INTEREST_3)).longValue();

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
        assertEquals(id1, page1.getContent().get(0).getId().longValue());
        assertEquals(INTEREST_1, page1.getContent().get(0).getName());
        assertEquals(id2, page1.getContent().get(1).getId().longValue());
        assertEquals(INTEREST_2, page1.getContent().get(1).getName());
        assertEquals(id3, page2.getContent().get(0).getId().longValue());
        assertEquals(INTEREST_3, page2.getContent().get(0).getName());
    }
    @Test
    public void testGetAllInterestsPagedNoInterests(){
        Page<Interest> page1 = interestDao.getAllInterests(1, 2);

        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(0, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(0, page1.getContent().size());
    }

    @Test
    public void testSearchBySubstringNoFiltering(){
        insertInterest.executeAndReturnKey(Map.of("name", INTEREST_1)).longValue();
        insertInterest.executeAndReturnKey(Map.of("name", INTEREST_2)).longValue();
        insertInterest.executeAndReturnKey(Map.of("name", INTEREST_3)).longValue();

        Page<Interest> page1 = interestDao.searchBySubstring(INTEREST_1.substring(0, 5), 1, 3);

        assertNotNull(page1);
        assertEquals(1, page1.getTotalPages());
        assertEquals(3, page1.getContent().size());
    }
    @Test
    public void testSearchBySubstringFiltering(){
        insertInterest.executeAndReturnKey(Map.of("name", INTEREST_1)).longValue();
        insertInterest.executeAndReturnKey(Map.of("name", INTEREST_2)).longValue();
        insertInterest.executeAndReturnKey(Map.of("name", INTEREST_3)).longValue();

        Page<Interest> page1 = interestDao.searchBySubstring(INTEREST_1.substring(INTEREST_1.length()-1, INTEREST_1.length()), 1, 3);

        assertNotNull(page1);
        assertEquals(1, page1.getTotalPages());
        assertEquals(1, page1.getContent().size());
    }
    @Test
    public void testSearchBySubstringEmpty(){
        insertInterest.executeAndReturnKey(Map.of("name", INTEREST_1)).longValue();
        insertInterest.executeAndReturnKey(Map.of("name", INTEREST_2)).longValue();
        insertInterest.executeAndReturnKey(Map.of("name", INTEREST_3)).longValue();

        Page<Interest> page1 = interestDao.searchBySubstring("", 1, 3);

        assertNotNull(page1);
        assertEquals(1, page1.getTotalPages());
        assertEquals(3, page1.getContent().size());
    }
    @Test
    public void testSearchBySubstringMissing(){
        insertInterest.executeAndReturnKey(Map.of("name", INTEREST_1)).longValue();
        insertInterest.executeAndReturnKey(Map.of("name", INTEREST_2)).longValue();
        insertInterest.executeAndReturnKey(Map.of("name", INTEREST_3)).longValue();

        Page<Interest> page1 = interestDao.searchBySubstring(null, 1, 3);

        assertNotNull(page1);
        assertEquals(1, page1.getTotalPages());
        assertEquals(3, page1.getContent().size());
    }
    @Test
    public void testSearchBySubstringPaging(){
        insertInterest.executeAndReturnKey(Map.of("name", INTEREST_1)).longValue();
        insertInterest.executeAndReturnKey(Map.of("name", INTEREST_2)).longValue();
        insertInterest.executeAndReturnKey(Map.of("name", INTEREST_3)).longValue();

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
        insertInterest.executeAndReturnKey(Map.of("name", INTEREST_1));
        insertInterest.executeAndReturnKey(Map.of("name", INTEREST_2));
        long idToDelete = insertInterest.executeAndReturnKey(Map.of("name", INTEREST_3)).longValue();

        interestDao.delete(idToDelete);

        assertEquals(2, jdbcTemplate.queryForObject("SELECT COUNT(*) FROM category",Integer.class).intValue());
    }
    @SuppressWarnings("null")
    @Test
    public void testDeleteWrongInterest(){
        insertInterest.executeAndReturnKey(Map.of("name", INTEREST_1));
        insertInterest.executeAndReturnKey(Map.of("name", INTEREST_2));

        interestDao.delete(12341234);

        assertEquals(2, jdbcTemplate.queryForObject("SELECT COUNT(*) FROM category",Integer.class).intValue());
    }

}

