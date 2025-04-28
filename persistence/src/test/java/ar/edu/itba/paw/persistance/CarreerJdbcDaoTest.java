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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.itba.paw.interfaces.persistence.CareerDao;
import ar.edu.itba.paw.models.Career;

@Sql(scripts = "classpath:schema.sql")
@Transactional
@Rollback
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class CarreerJdbcDaoTest {

    private static final String CAREER_TABLE = "careers";
    private static final String CAREER_1 = "1";
    private static final String CAREER_2 = "2";
    private static final String CAREER_3 = "3";
    private static final int TOTAL_CAREERS = 3;
    private static long id1;

    @Autowired
    private DataSource ds;

    @Autowired
    private CareerDao careerDao;

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
}
