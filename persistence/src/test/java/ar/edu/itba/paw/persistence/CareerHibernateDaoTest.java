package ar.edu.itba.paw.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.sql.DataSource;
import ar.edu.itba.paw.models.exceptions.CareerAlreadyExistsException;
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
import ar.edu.itba.paw.models.Career;
import ar.edu.itba.paw.models.Page;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class CareerHibernateDaoTest {

    @Autowired
    private DataSource ds;

    @Autowired
    private CareerHibernateDao careerDao;

    @PersistenceContext
    private EntityManager em;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp(){
        jdbcTemplate = new JdbcTemplate(ds);
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
    public void testFindAllCareersPageOne(){
        Page<Career> page1 = careerDao.findAll(TestUtils.PAGE_1_SINGLE);

        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(2, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(1, page1.getContent().size());
        TestUtils.assertEqualsCareer(TestUtils.CAREER_1, page1.getContent().get(0));
    }
    @Test
    public void testFindAllCareersPageTwo(){
        Page<Career> page2 = careerDao.findAll(TestUtils.PAGE_2_SINGLE);

        assertNotNull(page2);
        assertEquals(2, page2.getCurrentPage());
        assertEquals(2, page2.getTotalPages());
        assertNotNull(page2.getContent());
        assertEquals(1, page2.getContent().size());
        TestUtils.assertEqualsCareer(TestUtils.CAREER_2, page2.getContent().get(0));
    }
    @Test
    public void testFindAllCareersNoCareers(){
        TestUtils.deleteCareers(jdbcTemplate);

        Page<Career> page1 = careerDao.findAll(TestUtils.PAGE_1_DEFAULT);

        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(0, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(0, page1.getContent().size());
    }

    @Test
    public void testSearchBySubstringNoFiltering(){
        Page<Career> page1 = careerDao.search(
            TestUtils.CAREER_1_NAME.substring(0, 5), 
            TestUtils.PAGE_1_DEFAULT
        );

        assertNotNull(page1);
        assertEquals(1, page1.getTotalPages());
        assertEquals(TestUtils.TOTAL_CAREERS, page1.getContent().size());
        TestUtils.assertEqualsCareer(TestUtils.CAREER_1, page1.getContent().get(0));
    }
    @Test
    public void testSearchBySubstringFiltering(){
        Page<Career> page1 = careerDao.search(
            TestUtils.CAREER_1_NAME.substring(
                TestUtils.CAREER_1_NAME.length()-1, 
                TestUtils.CAREER_1_NAME.length()
            ), 
            TestUtils.PAGE_1_DEFAULT
        );

        assertNotNull(page1);
        assertEquals(1, page1.getTotalPages());
        assertEquals(1, page1.getContent().size());
        TestUtils.assertEqualsCareer(TestUtils.CAREER_1, page1.getContent().get(0));
    }
    @Test
    public void testSearchBySubstringEmpty(){
        Page<Career> page1 = careerDao.search("", TestUtils.PAGE_1_DEFAULT);

        assertNotNull(page1);
        assertEquals(1, page1.getTotalPages());
        assertEquals(TestUtils.TOTAL_CAREERS, page1.getContent().size());
        TestUtils.assertEqualsCareer(TestUtils.CAREER_1, page1.getContent().get(0));
        TestUtils.assertEqualsCareer(TestUtils.CAREER_2, page1.getContent().get(1));
    }
    @Test
    public void testSearchBySubstringMissing(){
        Page<Career> page1 = careerDao.search(null, TestUtils.PAGE_1_DEFAULT);

        assertNotNull(page1);
        assertEquals(1, page1.getTotalPages());
        assertEquals(TestUtils.TOTAL_CAREERS, page1.getContent().size());
        TestUtils.assertEqualsCareer(TestUtils.CAREER_1, page1.getContent().get(0));
        TestUtils.assertEqualsCareer(TestUtils.CAREER_2, page1.getContent().get(1));
    }
    @Test
    public void testSearchBySubstringPageOne(){
        Page<Career> page1 = careerDao.search("1", TestUtils.PAGE_1_DEFAULT);

        assertNotNull(page1);
        assertEquals(1, page1.getTotalPages());
        assertEquals(1, page1.getContent().size());
        TestUtils.assertEqualsCareer(TestUtils.CAREER_1, page1.getContent().get(0));
    }

    @Test
    public void testCreate(){
        Career career = careerDao.create(TestUtils.CAREER_INSERT1_NAME);
        em.flush();

        assertNotNull(career);
        assertEquals(TestUtils.CAREER_INSERT1_NAME, career.getName());
        assertTrue(career.getId() > 0);
    }
    @Test(expected = CareerAlreadyExistsException.class)
    public void testCreateDuplicateActive(){
        careerDao.create(TestUtils.CAREER_1_NAME);
        em.flush();
    }
    @Test
    public void testCreateDuplicateDeleted(){
        int rowsBefore = JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.CAREER_TABLE);
        
        Career career = careerDao.create(TestUtils.CAREER_DELETED_NAME);
        em.flush();

        assertNotNull(career);
        TestUtils.assertEqualsCareer(TestUtils.CAREER_DELETED, career);
        assertFalse(jdbcTemplate.queryForObject(
            TestUtils.CAREER_IS_DELETED_BY_ID, 
            Boolean.class, 
            TestUtils.CAREER_DELETED_ID)
        );
        assertEquals(rowsBefore, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.CAREER_TABLE));
    }
    @Test(expected = PersistenceException.class)
    public void testCreateMissing(){
        careerDao.create(null);
        em.flush();
    }
}
