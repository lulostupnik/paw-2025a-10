package ar.edu.itba.paw.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.sql.DataSource;
import ar.edu.itba.paw.models.PageParams;
import ar.edu.itba.paw.persistence.config.TestConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;
import ar.edu.itba.paw.models.Career;
import ar.edu.itba.paw.models.Page;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class CareerJdbcDaoTest {

    @Autowired
    private  DataSource ds;

    @Autowired
    private CareerJdbcDao careerDao;

    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert insert;

    @Before
    public void setUp(){
        jdbcTemplate = new JdbcTemplate(ds);
        insert = new SimpleJdbcInsert(ds).withTableName(TestUtils.CAREER_TABLE).usingGeneratedKeyColumns("id");
    }

    @Test
    public void testFindById(){
        Optional<Career> maybeCareer = careerDao.findById(TestUtils.CAREER_1_ID);
        
        assertNotNull(maybeCareer);
        assertTrue(maybeCareer.isPresent());
        TestUtils.assertEqualsCareer(TestUtils.CAREER_1, maybeCareer.get());
    }
    @Test
    public void testFindByIdMissingCareer(){
        Optional<Career> maybeCareer = careerDao.findById(12341234);

        assertNotNull(maybeCareer);
        assertFalse(maybeCareer.isPresent());
    }
    @Test
    public void testFindByIdDeleted(){
        Optional<Career> maybeCareer = careerDao.findById(TestUtils.CAREER_DELETED_ID);

        assertNotNull(maybeCareer);
        assertFalse(maybeCareer.isPresent());
    }
    
    @Test
    public void testFindByName(){
        Optional<Career> maybeCareer = careerDao.findByName(TestUtils.CAREER_1_NAME);

        assertNotNull(maybeCareer);
        assertTrue(maybeCareer.isPresent());
        TestUtils.assertEqualsCareer(TestUtils.CAREER_1, maybeCareer.get());
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
    public void testGetAllCareersPageOne(){
        insert.executeAndReturnKey(Map.of("name", TestUtils.CAREER_INSERT1_NAME, "deleted", false)).longValue();

        Page<Career> page1 = careerDao.findAll(new PageParams(1, 2));
        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(2, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(2, page1.getContent().size());
        TestUtils.assertEqualsCareer(TestUtils.CAREER_1, page1.getContent().get(0));
        TestUtils.assertEqualsCareer(TestUtils.CAREER_2, page1.getContent().get(1));
    }
    @Test
    public void testGetAllCareersPageTwo(){
        long bonusId = insert.executeAndReturnKey(Map.of("name", TestUtils.CAREER_INSERT1_NAME, "deleted", false)).longValue();

        Page<Career> page2 = careerDao.findAll(new PageParams(2, 2));
        assertNotNull(page2);
        assertEquals(2, page2.getCurrentPage());
        assertEquals(2, page2.getTotalPages());
        assertNotNull(page2.getContent());
        assertEquals(1, page2.getContent().size());
        TestUtils.assertEqualsCareer(new Career(bonusId, TestUtils.CAREER_INSERT1_NAME), page2.getContent().get(0));
    }
    @Test
    public void testGetAllCareersNoCareers(){
        TestUtils.deleteCareers(jdbcTemplate);

        Page<Career> page1 = careerDao.findAll(new PageParams(1, 2));

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
        TestUtils.assertEqualsCareer(TestUtils.CAREER_DELETED, career);
        assertFalse(jdbcTemplate.queryForObject(TestUtils.CAREER_IS_DELETED_BY_ID, Boolean.class, TestUtils.CAREER_DELETED_ID));
        assertEquals(rowsBefore, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.CAREER_TABLE));
    }
    @Test(expected = DataAccessException.class)
    public void testCreateMissing(){
        careerDao.create(null);
    }

    @Test
    public void testUpdate(){
        careerDao.update(TestUtils.CAREER_1_ID, TestUtils.CAREER_INSERT1_NAME);

        TestUtils.assertEqualsCareer(
            new Career(TestUtils.CAREER_1_ID, TestUtils.CAREER_INSERT1_NAME), 
            jdbcTemplate.queryForObject(TestUtils.CAREER_SELECT_BY_ID, TestUtils.CAREER_ROW_MAPPER, TestUtils.CAREER_1_ID)
        );
    }

    @Test(expected = DataAccessException.class)
    public void testUpdateDuplicate(){
        careerDao.update(TestUtils.CAREER_1_ID, TestUtils.CAREER_2_NAME);
    }
    @Test
    public void testUpdateWrongCareer(){
        careerDao.update(12341234, TestUtils.CAREER_INSERT1_NAME);
        
        for (Career career : List.of(TestUtils.CAREER_1, TestUtils.CAREER_2, TestUtils.CAREER_DELETED)){
            TestUtils.assertEqualsCareer(career, jdbcTemplate.queryForObject(TestUtils.CAREER_SELECT_BY_ID, TestUtils.CAREER_ROW_MAPPER, career.getId()));
        }
    }

    @Test
    public void testSearchBySubstringNoFiltering(){
        Page<Career> page1 = careerDao.search(TestUtils.CAREER_1_NAME.substring(0, 5), new PageParams(1, 3));

        assertNotNull(page1);
        assertEquals(1, page1.getTotalPages());
        assertEquals(TestUtils.TOTAL_CAREERS, page1.getContent().size());
        TestUtils.assertEqualsCareer(TestUtils.CAREER_1, page1.getContent().get(0));

    }
    @Test
    public void testSearchBySubstringFiltering(){
        Page<Career> page1 = careerDao.search(TestUtils.CAREER_1_NAME.substring(TestUtils.CAREER_1_NAME.length()-1, TestUtils.CAREER_1_NAME.length()), new PageParams(1, 3));

        assertNotNull(page1);
        assertEquals(1, page1.getTotalPages());
        assertEquals(1, page1.getContent().size());
        TestUtils.assertEqualsCareer(TestUtils.CAREER_1, page1.getContent().get(0));
    }
    @Test
    public void testSearchBySubstringEmpty(){
        Page<Career> page1 = careerDao.search("", new PageParams(1, 3));

        assertNotNull(page1);
        assertEquals(1, page1.getTotalPages());
        assertEquals(TestUtils.TOTAL_CAREERS + 1, page1.getContent().size());
        TestUtils.assertEqualsCareer(TestUtils.CAREER_1, page1.getContent().get(0));
        TestUtils.assertEqualsCareer(TestUtils.CAREER_2, page1.getContent().get(1));
        TestUtils.assertEqualsCareer(TestUtils.CAREER_DELETED, page1.getContent().get(2));
    }
    @Test
    public void testSearchBySubstringMissing(){
        Page<Career> page1 = careerDao.search(null, new PageParams(1, 3));

        assertNotNull(page1);
        assertEquals(1, page1.getTotalPages());
        assertEquals(TestUtils.TOTAL_CAREERS + 1, page1.getContent().size());
        TestUtils.assertEqualsCareer(TestUtils.CAREER_1, page1.getContent().get(0));
        TestUtils.assertEqualsCareer(TestUtils.CAREER_2, page1.getContent().get(1));
        TestUtils.assertEqualsCareer(TestUtils.CAREER_DELETED, page1.getContent().get(2));
    }
    @Test
    public void testSearchBySubstringPageOne(){
        Page<Career> page1 = careerDao.search("", new PageParams(1, 2));

        assertNotNull(page1);
        assertEquals(2, page1.getTotalPages());
        assertEquals(2, page1.getContent().size());
        TestUtils.assertEqualsCareer(TestUtils.CAREER_1, page1.getContent().get(0));
        TestUtils.assertEqualsCareer(TestUtils.CAREER_2, page1.getContent().get(1));
    }

    @Test
    public void testSearchBySubstringPageTwo(){
        Page<Career> page2 = careerDao.search("", new PageParams(2, 2));
        assertNotNull(page2);
        assertEquals(2, page2.getTotalPages());
        assertEquals(1, page2.getContent().size());
        TestUtils.assertEqualsCareer(TestUtils.CAREER_DELETED, page2.getContent().get(0));
    }

    @Test
    public void testDelete(){
        int beforeRows = JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.CAREER_TABLE);

        careerDao.delete(TestUtils.CAREER_1_ID);

        assertEquals(beforeRows, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.CAREER_TABLE));
        assertEquals(
            TestUtils.TOTAL_CAREERS - 1, 
            Optional.ofNullable(
                jdbcTemplate.queryForObject(TestUtils.CAREER_COUNT_NOT_DELETED, Integer.class)
            ).get().intValue()
        );
        assertTrue(jdbcTemplate.queryForObject(TestUtils.CAREER_IS_DELETED_BY_ID, Boolean.class, TestUtils.CAREER_1_ID));
    }
    @Test
    public void testDeleteDeleted(){
        int beforeRows = JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.CAREER_TABLE);

        careerDao.delete(TestUtils.CAREER_DELETED_ID);

        assertEquals(beforeRows, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.CAREER_TABLE));
        assertEquals(
            TestUtils.TOTAL_CAREERS, 
            Optional.ofNullable(
                jdbcTemplate.queryForObject(TestUtils.CAREER_COUNT_NOT_DELETED, Integer.class)
            ).get().intValue()
        );    
    }
    @Test
    public void testDeleteWrongCareer(){
        int beforeRows = JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.CAREER_TABLE);

        careerDao.delete(12341234);

        assertEquals(beforeRows, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.CAREER_TABLE));
        assertEquals(
            TestUtils.TOTAL_CAREERS, 
            Optional.ofNullable(
                jdbcTemplate.queryForObject(TestUtils.CAREER_COUNT_NOT_DELETED, Integer.class)
            ).get().intValue()
        );    
    }
}
