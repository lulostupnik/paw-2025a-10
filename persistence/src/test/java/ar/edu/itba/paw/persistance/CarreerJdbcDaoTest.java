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
import ar.edu.itba.paw.models.Interest;
import ar.edu.itba.paw.models.Page;
import ar.edu.itba.paw.persistence.CareerJdbcDao;

@Transactional
@Rollback
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class CarreerJdbcDaoTest {

    private static final String CAREER_TABLE = "careers";
    private static final String CAREER_1 = "career 1";
    private static final String CAREER_2 = "career 2";
    private static final String CAREER_3 = "career 3";
    private static final String CAREER_4 = "career 4";
    private static final int TOTAL_CAREERS = 3;
    private static long id1;

    @Autowired
    private DataSource ds;

    @Autowired
    private CareerJdbcDao careerDao;

    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert insert;

    @Before
    public void setUp(){
        jdbcTemplate = new JdbcTemplate(ds);
        insert = new SimpleJdbcInsert(ds).withTableName(CAREER_TABLE).usingGeneratedKeyColumns("id");
        id1 = insert.executeAndReturnKey(Map.of("name", CAREER_1)).longValue();
        insert.execute(Map.of("name", CAREER_2));
        insert.execute(Map.of("name", CAREER_3));
    }

    @Test
    public void testFindById(){
        Optional<Career> maybeCareer = careerDao.findById(id1);
        
        assertNotNull(maybeCareer);
        assertTrue(maybeCareer.isPresent());
        assertEquals(id1, maybeCareer.get().getId().longValue());
        assertEquals(CAREER_1, maybeCareer.get().getName());
    }
    @Test
    public void testFindByIdMissingCareer(){
        Optional<Career> maybeCareer = careerDao.findById(12341234);
        assertNotNull(maybeCareer);
        assertFalse(maybeCareer.isPresent());
    }
    
    @Test
    public void testFindByName(){
        Optional<Career> maybeCareer = careerDao.findByName(CAREER_1);

        assertNotNull(maybeCareer);
        assertTrue(maybeCareer.isPresent());
        assertEquals(id1, maybeCareer.get().getId().longValue());
        assertEquals(CAREER_1, maybeCareer.get().getName());
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
    public void testFindAll(){
        List<Career> careers = careerDao.findAll();

        assertNotNull(careers);
        assertEquals(TOTAL_CAREERS, careers.size());
        List<String> careerNames = List.of(CAREER_1, CAREER_2, CAREER_3);
        for (Career career : careers){
            assertTrue(careerNames.contains(career.getName()));
        }
    }
    @Test
    public void testFindAllNoCareers(){
        JdbcTestUtils.deleteFromTables(jdbcTemplate, CAREER_TABLE);

        List<Career> careers = careerDao.findAll();

        assertNotNull(careers);
        assertEquals(0, careers.size());
    }

    @Test
    public void testGetAllCareers(){
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
        assertEquals(CAREER_1, page1.getContent().get(0).getName());
        assertEquals(id1, page1.getContent().get(0).getId().longValue());
        assertEquals(CAREER_2, page1.getContent().get(1).getName());
        assertEquals(CAREER_3, page2.getContent().get(0).getName());
    }
    @Test
    public void testGetAllCareersNoCareers(){
        JdbcTestUtils.deleteFromTables(jdbcTemplate, CAREER_TABLE);

        Page<Career> page1 = careerDao.getAllCareers(1, 2);

        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(0, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(0, page1.getContent().size());
    }

    @Test
    public void testCreate(){
        Career career = careerDao.create(CAREER_4);
        
        assertNotNull(career);
        assertEquals(CAREER_4, career.getName());
        assertTrue(career.getId() != null);
        assertTrue(career.getId() > 0);
    }
    @Test(expected = DataAccessException.class)
    public void testCreateDuplicate(){
        careerDao.create(CAREER_1);
    }
    @Test(expected = DataAccessException.class)
    public void testCreateMissing(){
        careerDao.create(null);
    }

    @Test
    public void testUpdate(){
//        Career career = careerDao.update(CAREER_1, CAREER_4);
//
//        assertNotNull(career);
//        assertEquals(CAREER_4, career.getName());
//        assertEquals(id1, career.getId().longValue());
    }
    @Test(expected = DataAccessException.class)
    public void testUpdateDuplicate(){
//        careerDao.update(CAREER_1, CAREER_2);
    }
//    @Test(expected = IllegalArgumentException.class)
//    public void testUpdateWrongCareer(){
//        careerDao.update("CAREER_1", CAREER_4);
//    }
//    @Test(expected=DataAccessException.class)
//    public void testUpdateMissingCareer(){
//        careerDao.update(CAREER_1, null);
//    }

    
    @Test
    public void testSearchBySubstringNoFiltering(){
        Page<Career> page1 = careerDao.searchBySubstring(CAREER_1.substring(0, 5), 1, 3);

        assertNotNull(page1);
        assertEquals(1, page1.getTotalPages());
        assertEquals(3, page1.getContent().size());
    }
    @Test
    public void testSearchBySubstringFiltering(){
        Page<Career> page1 = careerDao.searchBySubstring(CAREER_1.substring(CAREER_1.length()-1, CAREER_1.length()), 1, 3);

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
        assertEquals(0, page1.getTotalPages());
        assertEquals(0, page1.getContent().size());
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
}
