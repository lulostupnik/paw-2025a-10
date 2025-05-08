package ar.edu.itba.paw.persistance;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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

import ar.edu.itba.paw.models.Career;
import ar.edu.itba.paw.models.Page;
import ar.edu.itba.paw.persistence.CareerJdbcDao;

@Transactional
@Rollback
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class CarreerJdbcDaoTest {

    private static long ID_1;
    private static long DELETED_ID;

    @Autowired
    private DataSource ds;

    @Autowired
    private CareerJdbcDao careerDao;

    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert insert;

    @Before
    public void setUp(){
        jdbcTemplate = new JdbcTemplate(ds);
        insert = new SimpleJdbcInsert(ds).withTableName(TestUtils.CAREER_TABLE).usingGeneratedKeyColumns("id");
        
        ID_1 = jdbcTemplate.queryForObject("SELECT id FROM careers WHERE name = ?", Long.class, TestUtils.CAREER_1_NAME);
        DELETED_ID = jdbcTemplate.queryForObject("SELECT id FROM careers WHERE name = ?", Long.class, TestUtils.CAREER_DELETED_NAME);
    }

    @Test
    public void testFindById(){
        Optional<Career> maybeCareer = careerDao.findById(ID_1);
        
        assertNotNull(maybeCareer);
        assertTrue(maybeCareer.isPresent());
        assertEquals(ID_1, maybeCareer.get().getId().longValue());
        assertEquals(TestUtils.CAREER_1_NAME, maybeCareer.get().getName());
    }
    @Test
    public void testFindByIdMissingCareer(){
        Optional<Career> maybeCareer = careerDao.findById(12341234);
        assertNotNull(maybeCareer);
        assertFalse(maybeCareer.isPresent());
    }
    @Test
    public void testFindByIdDeleted(){
        Optional<Career> maybeCareer = careerDao.findById(DELETED_ID);
        assertNotNull(maybeCareer);
        assertFalse(maybeCareer.isPresent());
    }
    
    @Test
    public void testFindByName(){
        Optional<Career> maybeCareer = careerDao.findByName(TestUtils.CAREER_1_NAME);

        assertNotNull(maybeCareer);
        assertTrue(maybeCareer.isPresent());
        assertEquals(ID_1, maybeCareer.get().getId().longValue());
        assertEquals(TestUtils.CAREER_1_NAME, maybeCareer.get().getName());
    }
    @Test
    public void testFindByNameMissingCareer(){
        Optional<Career> maybeCareer = careerDao.findByName("12341234");

        assertNotNull(maybeCareer);
        assertFalse(maybeCareer.isPresent());
    }
    @Test
    public void testFindByNameNullName(){
        Optional<Career> maybeCareer = careerDao.findByName(null);

        assertNotNull(maybeCareer);
        assertFalse(maybeCareer.isPresent());
    }
    @Test
    public void testFindByNameDeleted(){
        Optional<Career> maybeCareer = careerDao.findByName(TestUtils.CAREER_DELETED_NAME);

        assertNotNull(maybeCareer);
        assertFalse(maybeCareer.isPresent());
    }

    @Test
    public void testFindAll(){
        List<Career> careers = careerDao.findAll();

        assertNotNull(careers);
        assertEquals(TestUtils.TOTAL_CAREERS, careers.size());
        List<String> careerNames = List.of(TestUtils.CAREER_1_NAME, TestUtils.CAREER_2_NAME);
        for (Career career : careers){
            assertTrue(careerNames.contains(career.getName()));
        }
    }
    @Test
    public void testFindAllNoCareers(){
        JdbcTestUtils.deleteFromTables(jdbcTemplate, TestUtils.CAREER_TABLE);

        List<Career> careers = careerDao.findAll();

        assertNotNull(careers);
        assertEquals(0, careers.size());
    }

    @Test
    public void testGetAllCareers(){
        long bonusId = insert.executeAndReturnKey(Map.of("name", TestUtils.CAREER_INSERT1_NAME, "deleted", false)).longValue();

        Page<Career> page1 = careerDao.getAllCareers(1, 2);
        Page<Career> page2 = careerDao.getAllCareers(2, 2);

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
        assertEquals(TestUtils.CAREER_1_NAME, page1.getContent().get(0).getName());
        assertEquals(ID_1, page1.getContent().get(0).getId().longValue());
        assertEquals(TestUtils.CAREER_2_NAME, page1.getContent().get(1).getName());
        assertEquals(TestUtils.CAREER_INSERT1_NAME, page2.getContent().getFirst().getName());
        assertEquals(bonusId, page2.getContent().getFirst().getId().longValue());
    }
    @Test
    public void testGetAllCareersNoCareers(){
        JdbcTestUtils.deleteFromTables(jdbcTemplate, TestUtils.CAREER_TABLE);

        Page<Career> page1 = careerDao.getAllCareers(1, 2);

        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(0, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(0, page1.getContent().size());
    }

    @Test
    public void testCreate(){
        Career career = careerDao.create(TestUtils.CAREER_INSERT1_NAME);
        
        assertNotNull(career);
        assertEquals(TestUtils.CAREER_INSERT1_NAME, career.getName());
        assertTrue(career.getId() != null);
        assertTrue(career.getId() > 0);
    }
    @Test(expected = DataAccessException.class)
    public void testCreateDuplicateActive(){
        careerDao.create(TestUtils.CAREER_1_NAME);
    }
    @Test
    public void testCreateDuplicateDeleted(){
        int rowsBefore = JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.CAREER_TABLE);

        Career career = careerDao.create(TestUtils.CAREER_DELETED_NAME);

        assertNotNull(career);
        assertEquals(DELETED_ID, career.getId().longValue());
        assertEquals(TestUtils.CAREER_DELETED_NAME, career.getName());
        assertEquals(rowsBefore, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.CAREER_TABLE));
    }
    @Test(expected = DataAccessException.class)
    public void testCreateMissing(){
        careerDao.create(null);
    }

    @Test
    public void testUpdate(){
        Career career = careerDao.update(ID_1, TestUtils.CAREER_INSERT1_NAME);

        assertNotNull(career);
        assertEquals(TestUtils.CAREER_INSERT1_NAME, career.getName());
        assertEquals(ID_1, career.getId().longValue());
    }
    @Test(expected = DataAccessException.class)
    public void testUpdateDuplicate(){
        careerDao.update(ID_1, TestUtils.CAREER_2_NAME);
    }
    @Test(expected = IllegalArgumentException.class)
    public void testUpdateWrongCareer(){
        careerDao.update(12341234, TestUtils.CAREER_INSERT1_NAME);
    }

    @Test
    public void testSearchBySubstringNoFiltering(){
        Page<Career> page1 = careerDao.searchBySubstring(TestUtils.CAREER_1_NAME.substring(0, 5), 1, 3);

        assertNotNull(page1);
        assertEquals(1, page1.getTotalPages());
        assertEquals(TestUtils.TOTAL_CAREERS, page1.getContent().size());
    }
    @Test
    public void testSearchBySubstringFiltering(){
        Page<Career> page1 = careerDao.searchBySubstring(TestUtils.CAREER_1_NAME.substring(TestUtils.CAREER_1_NAME.length()-1, TestUtils.CAREER_1_NAME.length()), 1, 3);

        assertNotNull(page1);
        assertEquals(1, page1.getTotalPages());
        assertEquals(1, page1.getContent().size());
    }
    @Test
    public void testSearchBySubstringEmpty(){
        Page<Career> page1 = careerDao.searchBySubstring("", 1, 3);

        assertNotNull(page1);
        assertEquals(1, page1.getTotalPages());
        assertEquals(3, page1.getContent().size());
    }
    @Test
    public void testSearchBySubstringMissing(){
        Page<Career> page1 = careerDao.searchBySubstring(null, 1, 3);

        assertNotNull(page1);
        assertEquals(1, page1.getTotalPages());
        assertEquals(3, page1.getContent().size());
    }
    @Test
    public void testSearchBySubstringPaging(){
        Page<Career> page1 = careerDao.searchBySubstring("", 1, 2);
        Page<Career> page2 = careerDao.searchBySubstring("", 2, 2);

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
        int beforeRows = JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.CAREER_TABLE);

        careerDao.delete(ID_1);

        assertEquals(beforeRows, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.CAREER_TABLE));
        assertEquals(TestUtils.TOTAL_CAREERS - 1, jdbcTemplate.queryForObject("SELECT COUNT(*) FROM careers WHERE deleted = FALSE", Integer.class).intValue());
    }
    @SuppressWarnings("null")
    @Test
    public void testDeleteDeleted(){
        int beforeRows = JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.CAREER_TABLE);

        careerDao.delete(DELETED_ID);

        assertEquals(beforeRows, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.CAREER_TABLE));
        assertEquals(TestUtils.TOTAL_CAREERS, jdbcTemplate.queryForObject("SELECT COUNT(*) FROM careers WHERE deleted = FALSE", Integer.class).intValue());
    }
    @SuppressWarnings("null")
    @Test
    public void testDeleteWrongCareer(){
        int beforeRows = JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.CAREER_TABLE);

        careerDao.delete(12341234);

        assertEquals(beforeRows, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.CAREER_TABLE));
        assertEquals(TestUtils.TOTAL_CAREERS, jdbcTemplate.queryForObject("SELECT COUNT(*) FROM careers WHERE deleted = FALSE", Integer.class).intValue());
    }
    
}
