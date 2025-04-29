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

import ar.edu.itba.paw.models.University;
import ar.edu.itba.paw.persistence.UniversityJdbcDao;

@Sql(scripts = "classpath:schema.sql")
@Transactional
@Rollback
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class UniversityJdbcDaoTest {

    private static final String UNIVERSITY_TABLE = "universities";
    private static final String COUNTRY_TABLE = "countries";
    private static final String CITY_TABLE = "cities";
    private static final String UNIVERSITY_NAME_1 = "Instituto de muy largo";
    private static final String UNIVERSITY_CODE_1 = "ITBA";    
    private static final String UNIVERSITY_NAME_2 = "Universidad de muy largo";
    private static final String UNIVERSITY_CODE_2 = "UBA";
    private static final int TOTAL_UNIVERSITIES = 2;
    private static final String COUNTRY_NAME = "cuntry";
    private static final String COUNTRY_CODE = "aa";
    private static final String CITY_NAME = "citi";
    private static long uniId1;
    private static long uniId2;
    private static long countryId;
    private static long cityId;

    @Autowired
    private DataSource ds;

    @Autowired
    private UniversityJdbcDao uniDao;

    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert insert;

    @Before
    public void setUp(){
        jdbcTemplate = new JdbcTemplate(ds);
        insert = new SimpleJdbcInsert(ds).withTableName(UNIVERSITY_TABLE).usingGeneratedKeyColumns("id");

        countryId = new SimpleJdbcInsert(ds).withTableName(COUNTRY_TABLE).usingGeneratedKeyColumns("id")
            .executeAndReturnKey(Map.of("name", COUNTRY_NAME, "code", COUNTRY_CODE)).longValue();
        cityId = new SimpleJdbcInsert(ds).withTableName(CITY_TABLE).usingGeneratedKeyColumns("id")
            .executeAndReturnKey(Map.of("name", CITY_NAME, "country_id", countryId)).longValue();

        uniId1 = insert.executeAndReturnKey(Map.of("name", UNIVERSITY_NAME_1, "abbreviation", UNIVERSITY_CODE_1, "city_id", cityId)).longValue();
        uniId2 = insert.executeAndReturnKey(Map.of("name", UNIVERSITY_NAME_2, "abbreviation", UNIVERSITY_CODE_2, "city_id", cityId)).longValue();
    }

    @Test
    public void testFindByName(){
        Optional<University> maybeUni = uniDao.findByName(UNIVERSITY_NAME_1);
        assertNotNull(maybeUni);
        assertTrue(maybeUni.isPresent());
        University uni = maybeUni.get();
        assertEquals(UNIVERSITY_NAME_1, uni.getName());
        assertEquals(UNIVERSITY_CODE_1, uni.getAbbreviation());
        assertEquals(uniId1, uni.getId());
        assertEquals(cityId, uni.getCity().getId());
    }
    @Test
    public void testFindByName2(){
        Optional<University> maybeUni = uniDao.findByName(UNIVERSITY_NAME_2);
        assertNotNull(maybeUni);
        assertTrue(maybeUni.isPresent());
        University uni = maybeUni.get();
        assertEquals(UNIVERSITY_NAME_2, uni.getName());
        assertEquals(UNIVERSITY_CODE_2, uni.getAbbreviation());
        assertEquals(uniId2, uni.getId());
        assertEquals(cityId, uni.getCity().getId());
    }
    @Test
    public void testFindByNameWrongName(){
        Optional<University> maybeUni = uniDao.findByName("UNIVERSITY_NAME_2");
        assertNotNull(maybeUni);
        assertFalse(maybeUni.isPresent());
    }
    @Test
    public void testFindByNameEmptyName(){
        Optional<University> maybeUni = uniDao.findByName("");
        assertNotNull(maybeUni);
        assertFalse(maybeUni.isPresent());
    }
    @Test
    public void testFindByNameMissingName(){
        Optional<University> maybeUni = uniDao.findByName(null);
        assertNotNull(maybeUni);
        assertFalse(maybeUni.isPresent());
    }

    @Test
    public void testFindByAbbreviation(){
        Optional<University> maybeUni = uniDao.findByAbbreviation(UNIVERSITY_CODE_1);
        assertNotNull(maybeUni);
        assertTrue(maybeUni.isPresent());
        University uni = maybeUni.get();
        assertEquals(UNIVERSITY_NAME_1, uni.getName());
        assertEquals(UNIVERSITY_CODE_1, uni.getAbbreviation());
        assertEquals(uniId1, uni.getId());
        assertEquals(cityId, uni.getCity().getId());
    }
    @Test
    public void testFindByAbbreviation2(){
        Optional<University> maybeUni = uniDao.findByAbbreviation(UNIVERSITY_CODE_2);
        assertNotNull(maybeUni);
        assertTrue(maybeUni.isPresent());
        University uni = maybeUni.get();
        assertEquals(UNIVERSITY_NAME_2, uni.getName());
        assertEquals(UNIVERSITY_CODE_2, uni.getAbbreviation());
        assertEquals(uniId2, uni.getId());
        assertEquals(cityId, uni.getCity().getId());
    }
    @Test
    public void testFindByAbbreviationWrongAbbreviation(){
        Optional<University> maybeUni = uniDao.findByAbbreviation("UNIVERSITY_CODE_1");
        assertNotNull(maybeUni);
        assertFalse(maybeUni.isPresent());
    }
    @Test
    public void testFindByAbbreviationEmptyAbbreviation(){
        Optional<University> maybeUni = uniDao.findByAbbreviation("");
        assertNotNull(maybeUni);
        assertFalse(maybeUni.isPresent());
    }
    @Test
    public void testFindByAbbreviationMissingAbbreviation(){
        Optional<University> maybeUni = uniDao.findByAbbreviation(null);
        assertNotNull(maybeUni);
        assertFalse(maybeUni.isPresent());
    }
    
    @Test
    public void testFindByAnyUsingAbbrSubstring(){
        Optional<University> maybeUni = uniDao.findByAny(UNIVERSITY_CODE_1.substring(1, 3));
        assertNotNull(maybeUni);
        assertTrue(maybeUni.isPresent());
        University uni = maybeUni.get();
        assertEquals(UNIVERSITY_NAME_1, uni.getName());
        assertEquals(UNIVERSITY_CODE_1, uni.getAbbreviation());
        assertEquals(uniId1, uni.getId());
        assertEquals(cityId, uni.getCity().getId());
    }
    @Test
    public void testFindByAnyUsingNameSubstring(){
        Optional<University> maybeUni = uniDao.findByAny(UNIVERSITY_NAME_2.substring(5, 15));
        assertNotNull(maybeUni);
        assertTrue(maybeUni.isPresent());
        University uni = maybeUni.get();
        assertEquals(UNIVERSITY_NAME_2, uni.getName());
        assertEquals(UNIVERSITY_CODE_2, uni.getAbbreviation());
        assertEquals(uniId2, uni.getId());
        assertEquals(cityId, uni.getCity().getId());
    }
    @Test
    public void testFindByAnyWrongQuery(){
        Optional<University> maybeUni = uniDao.findByAny("UNIVERSITY_CODE_1");
        assertNotNull(maybeUni);
        assertFalse(maybeUni.isPresent());
    }
    @Test
    public void testFindByAnyEmptyQuery(){
        Optional<University> maybeUni = uniDao.findByAny("");
        assertNotNull(maybeUni);
        assertTrue(maybeUni.isPresent());
    }
    @Test
    public void testFindByAnyMissingQuery(){
        Optional<University> maybeUni = uniDao.findByAny(null);
        assertNotNull(maybeUni);
        assertFalse(maybeUni.isPresent());
    }

    @Test
    public void testGetAllUniversities(){
        List<University> unis = uniDao.getAllUniversities();
        assertNotNull(unis);
        assertEquals(TOTAL_UNIVERSITIES, unis.size());
        for (University uni : unis) {
            assertEquals(CITY_NAME, uni.getCity().getName());
            if (uni.getId() == uniId1){
                assertEquals(UNIVERSITY_NAME_1, uni.getName());
                assertEquals(UNIVERSITY_CODE_1, uni.getAbbreviation());
            } else {
                assertEquals(UNIVERSITY_NAME_2, uni.getName());
                assertEquals(UNIVERSITY_CODE_2, uni.getAbbreviation());
            }
        }
    }
    @Test
    public void testGetAllUniversitiesNoUniversities(){
        JdbcTestUtils.deleteFromTables(jdbcTemplate, UNIVERSITY_TABLE);
        List<University> unis = uniDao.getAllUniversities();
        assertNotNull(unis);
        assertEquals(0, unis.size());
    }

    @Test
    public void testFindById(){
        Optional<University> maybeUni = uniDao.findById(uniId1);
        assertNotNull(maybeUni);
        assertTrue(maybeUni.isPresent());
        University uni = maybeUni.get();
        assertEquals(UNIVERSITY_NAME_1, uni.getName());
        assertEquals(UNIVERSITY_CODE_1, uni.getAbbreviation());
        assertEquals(uniId1, uni.getId());
        assertEquals(cityId, uni.getCity().getId());
    }
    @Test
    public void testFindByIdWrongId(){
        Optional<University> maybeUni = uniDao.findById(1234123);
        assertNotNull(maybeUni);
        assertFalse(maybeUni.isPresent());
    }
    
    @Test
    public void testSearchBySubstringUsingAbbrSubstring(){
        List<University> unis = uniDao.searchBySubstring(UNIVERSITY_CODE_1.substring(1, 3));
        assertNotNull(unis);
        assertEquals(1, unis.size());
        University uni = unis.getFirst();
        assertEquals(UNIVERSITY_NAME_1, uni.getName());
        assertEquals(UNIVERSITY_CODE_1, uni.getAbbreviation());
        assertEquals(uniId1, uni.getId());
        assertEquals(cityId, uni.getCity().getId());
    }
    @Test
    public void testSearchBySubstringUsingNameSubstring(){
        List<University> unis = uniDao.searchBySubstring(UNIVERSITY_NAME_2.substring(5, 15));
        assertNotNull(unis);
        assertEquals(1, unis.size());
        University uni = unis.getFirst();
        assertEquals(UNIVERSITY_NAME_2, uni.getName());
        assertEquals(UNIVERSITY_CODE_2, uni.getAbbreviation());
        assertEquals(uniId2, uni.getId());
        assertEquals(cityId, uni.getCity().getId());
    }
    @Test
    public void testSearchBySubstringMultipleResults(){
        List<University> unis = uniDao.searchBySubstring(UNIVERSITY_NAME_1.substring(UNIVERSITY_NAME_1.length() - 5, UNIVERSITY_NAME_1.length()));
        assertNotNull(unis);
        assertEquals(TOTAL_UNIVERSITIES, unis.size());
    }
    @Test
    public void testSearchBySubstringWrongQuery(){
        List<University> unis = uniDao.searchBySubstring("UNIVERSITY_CODE_1");
        assertNotNull(unis);
        assertEquals(0, unis.size());
    }
    @Test
    public void testSearchBySubstringEmptyQuery(){
        List<University> unis = uniDao.searchBySubstring("");
        assertNotNull(unis);
        assertEquals(TOTAL_UNIVERSITIES, unis.size());
    }
    @Test
    public void testSearchBySubstringMissingQuery(){
        List<University> unis = uniDao.searchBySubstring(null);
        assertNotNull(unis);
        assertEquals(0, unis.size());
    }
}