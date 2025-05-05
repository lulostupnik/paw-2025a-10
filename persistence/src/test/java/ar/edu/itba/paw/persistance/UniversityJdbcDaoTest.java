package ar.edu.itba.paw.persistance;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.sql.DataSource;

import ar.edu.itba.paw.models.City;
import ar.edu.itba.paw.models.Page;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.itba.paw.models.University;
import ar.edu.itba.paw.persistence.UniversityJdbcDao;

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
    private static final String UNIVERSITY_CODE_2 = "MIT";    
    private static final String DELETED_UNI_NAME = "Deleted uni";
    private static final String DELETED_UNI_CODE = "DEL";
    private static final String UNIVERSITY_NAME_4 = "Another one";
    private static final String UNIVERSITY_CODE_4 = "ONE";
    private static final int TOTAL_UNIVERSITIES = 2;
    private static final String COUNTRY_NAME = "cuntry";
    private static final String COUNTRY_CODE = "aa";
    private static final String CITY_NAME = "citi";
    private static long UNI_ID_1;
    private static long UNI_ID_2;
    private static long DELETED_UNI_ID;
    private static long COUNTRY_ID;
    private static long CITY_ID;

    @Autowired
    private DataSource ds;

    @Autowired
    private UniversityJdbcDao uniDao;

    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert insert;

    private RowMapper<University> UNIVERSITY_ROW_MAPPER = (rs, rowNum) -> new University(rs.getLong("id"), rs.getString("name"), rs.getString("abbreviation"), new City(rs.getString("city_name"), rs.getString("country_name"), rs.getLong("city_id")));

    @Before
    public void setUp(){
        jdbcTemplate = new JdbcTemplate(ds);
        insert = new SimpleJdbcInsert(ds).withTableName(UNIVERSITY_TABLE).usingGeneratedKeyColumns("id");

        COUNTRY_ID = new SimpleJdbcInsert(ds).withTableName(COUNTRY_TABLE).usingGeneratedKeyColumns("id")
            .executeAndReturnKey(Map.of("name", COUNTRY_NAME, "code", COUNTRY_CODE)).longValue();
        CITY_ID = new SimpleJdbcInsert(ds).withTableName(CITY_TABLE).usingGeneratedKeyColumns("id")
            .executeAndReturnKey(Map.of("name", CITY_NAME, "country_id", COUNTRY_ID, "deleted", false)).longValue();

        UNI_ID_1 = insert.executeAndReturnKey(Map.of("name", UNIVERSITY_NAME_1, "abbreviation", UNIVERSITY_CODE_1, "city_id", CITY_ID, "deleted", false)).longValue();
        UNI_ID_2 = insert.executeAndReturnKey(Map.of("name", UNIVERSITY_NAME_2, "abbreviation", UNIVERSITY_CODE_2, "city_id", CITY_ID, "deleted", false)).longValue();
        DELETED_UNI_ID = insert.executeAndReturnKey(Map.of("name", DELETED_UNI_NAME, "abbreviation", DELETED_UNI_CODE, "city_id", CITY_ID, "deleted", true)).longValue();
    }

    @Test
    public void testFindByName(){
        Optional<University> maybeUni = uniDao.findByName(UNIVERSITY_NAME_1);
        assertNotNull(maybeUni);
        assertTrue(maybeUni.isPresent());
        University uni = maybeUni.get();
        assertEquals(UNIVERSITY_NAME_1, uni.getName());
        assertEquals(UNIVERSITY_CODE_1, uni.getAbbreviation());
        assertEquals(UNI_ID_1, uni.getId());
        assertEquals(CITY_ID, uni.getCity().getId());
    }
    @Test
    public void testFindByName2(){
        Optional<University> maybeUni = uniDao.findByName(UNIVERSITY_NAME_2);
        assertNotNull(maybeUni);
        assertTrue(maybeUni.isPresent());
        University uni = maybeUni.get();
        assertEquals(UNIVERSITY_NAME_2, uni.getName());
        assertEquals(UNIVERSITY_CODE_2, uni.getAbbreviation());
        assertEquals(UNI_ID_2, uni.getId());
        assertEquals(CITY_ID, uni.getCity().getId());
    }
    @Test
    public void testFindByNameDeleted(){
        Optional<University> maybeUni = uniDao.findByName(DELETED_UNI_NAME);
        assertNotNull(maybeUni);
        assertFalse(maybeUni.isPresent());
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
        assertEquals(UNI_ID_1, uni.getId());
        assertEquals(CITY_ID, uni.getCity().getId());
    }
    @Test
    public void testFindByAbbreviation2(){
        Optional<University> maybeUni = uniDao.findByAbbreviation(UNIVERSITY_CODE_2);
        assertNotNull(maybeUni);
        assertTrue(maybeUni.isPresent());
        University uni = maybeUni.get();
        assertEquals(UNIVERSITY_NAME_2, uni.getName());
        assertEquals(UNIVERSITY_CODE_2, uni.getAbbreviation());
        assertEquals(UNI_ID_2, uni.getId());
        assertEquals(CITY_ID, uni.getCity().getId());
    }
    @Test
    public void testFindByAbbreviationDeleted(){
        Optional<University> maybeUni = uniDao.findByAbbreviation(DELETED_UNI_CODE);
        assertNotNull(maybeUni);
        assertFalse(maybeUni.isPresent());
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
        assertEquals(UNI_ID_1, uni.getId());
        assertEquals(CITY_ID, uni.getCity().getId());
    }
    @Test
    public void testFindByAnyUsingNameSubstring(){
        Optional<University> maybeUni = uniDao.findByAny(UNIVERSITY_NAME_2.substring(5, 15));
        assertNotNull(maybeUni);
        assertTrue(maybeUni.isPresent());
        University uni = maybeUni.get();
        assertEquals(UNIVERSITY_NAME_2, uni.getName());
        assertEquals(UNIVERSITY_CODE_2, uni.getAbbreviation());
        assertEquals(UNI_ID_2, uni.getId());
        assertEquals(CITY_ID, uni.getCity().getId());
    }
    @Test
    public void testFindByAnyWrongQuery(){
        Optional<University> maybeUni = uniDao.findByAny("UNIVERSITY_CODE_1");
        assertNotNull(maybeUni);
        assertFalse(maybeUni.isPresent());
    }
    @Test
    public void testFindByAnyDeleted(){
        Optional<University> maybeUni = uniDao.findByAny(DELETED_UNI_NAME);
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
        assertTrue(maybeUni.isPresent());
    }

    @Test
    public void testGetAllUniversities(){
        List<University> unis = uniDao.getAllUniversities();
        assertNotNull(unis);
        assertEquals(TOTAL_UNIVERSITIES, unis.size());
        for (University uni : unis) {
            assertEquals(CITY_NAME, uni.getCity().getName());
            if (uni.getId() == UNI_ID_1){
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
        Optional<University> maybeUni = uniDao.findById(UNI_ID_1);
        assertNotNull(maybeUni);
        assertTrue(maybeUni.isPresent());
        University uni = maybeUni.get();
        assertEquals(UNIVERSITY_NAME_1, uni.getName());
        assertEquals(UNIVERSITY_CODE_1, uni.getAbbreviation());
        assertEquals(UNI_ID_1, uni.getId());
        assertEquals(CITY_ID, uni.getCity().getId());
    }
    @Test
    public void testFindByIdWrongId(){
        Optional<University> maybeUni = uniDao.findById(1234123);
        assertNotNull(maybeUni);
        assertFalse(maybeUni.isPresent());
    }
    @Test
    public void testFindByIdDeleted(){
        Optional<University> maybeUni = uniDao.findById(DELETED_UNI_ID);
        assertNotNull(maybeUni);
        assertFalse(maybeUni.isPresent());
    }
    
    @Test
    public void testSearchBySubstringUsingAbbrSubstring(){
        Page<University> unis = uniDao.searchBySubstring(UNIVERSITY_CODE_1.substring(1, 3),1,10);
        assertNotNull(unis);
        assertEquals(1, unis.getCurrentPage());
        assertEquals(1, unis.getTotalPages());
        assertNotNull(unis.getContent());
        assertEquals(1, unis.getContent().size());
        University uni = unis.getContent().getFirst();
        assertEquals(UNIVERSITY_NAME_1, uni.getName());
        assertEquals(UNIVERSITY_CODE_1, uni.getAbbreviation());
        assertEquals(UNI_ID_1, uni.getId());
        assertEquals(CITY_ID, uni.getCity().getId());
    }
    @Test
    public void testSearchBySubstringUsingNameSubstring(){
        Page<University> unis = uniDao.searchBySubstring(UNIVERSITY_NAME_2.substring(5, 15), 1, 2);
        assertNotNull(unis);
        assertEquals(1, unis.getCurrentPage());
        assertEquals(1, unis.getTotalPages());
        assertNotNull(unis.getContent());
        assertEquals(1, unis.getContent().size());
        University uni = unis.getContent().getFirst();
        assertEquals(UNIVERSITY_NAME_2, uni.getName());
        assertEquals(UNIVERSITY_CODE_2, uni.getAbbreviation());
        assertEquals(UNI_ID_2, uni.getId());
        assertEquals(CITY_ID, uni.getCity().getId());
    }
    @Test
    public void testSearchBySubstringMultipleResults(){
        Page<University> unis = uniDao.searchBySubstring(UNIVERSITY_NAME_1.substring(UNIVERSITY_NAME_1.length() - 5, UNIVERSITY_NAME_1.length()), 1, 10);
        assertNotNull(unis);
        assertEquals(1, unis.getCurrentPage());
        assertEquals(1, unis.getTotalPages());
        assertNotNull(unis.getContent());
        assertEquals(TOTAL_UNIVERSITIES, unis.getContent().size());
    }
    @Test
    public void testSearchBySubstringWrongQuery(){
        Page<University> unis = uniDao.searchBySubstring("UNIVERSITY_CODE_1", 1, 10);
        assertNotNull(unis);
        assertEquals(1, unis.getCurrentPage());
        assertEquals(0, unis.getTotalPages());
        assertNotNull(unis.getContent());
        assertEquals(0, unis.getContent().size());    
    }
    @Test
    public void testSearchBySubstringDeleted(){
        Page<University> unis = uniDao.searchBySubstring(DELETED_UNI_NAME, 1, 10);
        assertNotNull(unis);
        assertEquals(1, unis.getCurrentPage());
        assertEquals(0, unis.getTotalPages());
        assertNotNull(unis.getContent());
        assertEquals(0, unis.getContent().size());    
    }
    @Test
    public void testSearchBySubstringEmptyQuery(){
        Page<University> unis = uniDao.searchBySubstring("", 1, 10);
        assertNotNull(unis);
        assertEquals(1, unis.getCurrentPage());
        assertEquals(1, unis.getTotalPages());
        assertNotNull(unis.getContent());
        assertEquals(TOTAL_UNIVERSITIES, unis.getContent().size());
    }
    @Test
    public void testSearchBySubstringMissingQuery(){
        Page<University> unis = uniDao.searchBySubstring(null, 1, 10);
        assertNotNull(unis);
        assertEquals(1, unis.getCurrentPage());
        assertEquals(1, unis.getTotalPages());
        assertNotNull(unis.getContent());
        assertEquals(2, unis.getContent().size()); 
    }

    @Test
    public void testGetAllUniversitiesPaged(){
        long bonusId = insert.executeAndReturnKey(Map.of("name", UNIVERSITY_NAME_4, "abbreviation", UNIVERSITY_CODE_4, "city_id", CITY_ID, "deleted", false)).longValue();

        Page<University> page1 = uniDao.getAllUniversities(1, 2);
        Page<University> page2 = uniDao.getAllUniversities(2, 2);

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
        assertEquals(bonusId, page1.getContent().get(0).getId());
        assertEquals(UNI_ID_1, page1.getContent().get(1).getId());
        assertEquals(UNI_ID_2, page2.getContent().get(0).getId());
    }
    @Test
    public void testGetAllUniversitiesPagedNoUniversities(){
        JdbcTestUtils.deleteFromTables(jdbcTemplate, UNIVERSITY_TABLE);
        Page<University> page = uniDao.getAllUniversities(1, 2);
        assertNotNull(page);
        assertEquals(1, page.getCurrentPage());
        assertEquals(0, page.getTotalPages());
        assertNotNull(page.getContent());
        assertEquals(0, page.getContent().size());
    }

    @Test
    public void testUpdateUniversity(){
        uniDao.updateUniversity(UNI_ID_1, UNIVERSITY_NAME_4, UNIVERSITY_CODE_4, CITY_ID);

        Optional<University> maybeUni = jdbcTemplate.query(
            "SELECT uni.id, uni.name, uni.abbreviation, country.name as country_name, city.name as city_name, city.id as city_id FROM universities uni INNER JOIN cities city ON uni.city_id = city.id INNER JOIN countries country ON city.country_id = country.id WHERE uni.id = ?",
            UNIVERSITY_ROW_MAPPER,
            UNI_ID_1
        ).stream().findFirst();
        assertNotNull(maybeUni);
        assertTrue(maybeUni.isPresent());
        University uni = maybeUni.get();
        assertEquals(UNI_ID_1, uni.getId());
        assertEquals(UNIVERSITY_NAME_4, uni.getName());
        assertEquals(UNIVERSITY_CODE_4, uni.getAbbreviation());
        assertEquals(CITY_NAME, uni.getCity().getName());
        assertEquals(CITY_ID, uni.getCity().getId());
        assertEquals(COUNTRY_NAME, uni.getCity().getCountry());
    }
    @Test(expected = DataAccessException.class)
    public void testUpdateUniversityDuplicateName(){
        uniDao.updateUniversity(UNI_ID_1, UNIVERSITY_NAME_2, UNIVERSITY_CODE_4, CITY_ID);
    }

    @Test
    public void testCreateUniversity(){
        uniDao.createUniversity(UNIVERSITY_NAME_4, UNIVERSITY_CODE_4, CITY_NAME);

        Optional<University> maybeUni = jdbcTemplate.query(
            "SELECT uni.id, uni.name, uni.abbreviation, country.name as country_name, city.name as city_name, city.id as city_id FROM universities uni INNER JOIN cities city ON uni.city_id = city.id INNER JOIN countries country ON city.country_id = country.id WHERE uni.name = ?", 
            UNIVERSITY_ROW_MAPPER,
            UNIVERSITY_NAME_4
        ).stream().findFirst();
        assertNotNull(maybeUni);
        assertTrue(maybeUni.isPresent());
        University uni = maybeUni.get();
        assertEquals(UNIVERSITY_NAME_4, uni.getName());
        assertEquals(UNIVERSITY_CODE_4, uni.getAbbreviation());
        assertEquals(CITY_NAME, uni.getCity().getName());
        assertEquals(CITY_ID, uni.getCity().getId());
        assertEquals(COUNTRY_NAME, uni.getCity().getCountry());
    }
    @Test(expected = DataAccessException.class)
    public void testCreateUniversityDuplicate(){
        uniDao.createUniversity(UNIVERSITY_NAME_1, UNIVERSITY_CODE_4, CITY_NAME);
    }
    @Test
    public void testCreateUniversityDeletedByName(){
        uniDao.createUniversity(DELETED_UNI_NAME, UNIVERSITY_CODE_4, CITY_NAME);

        Optional<University> maybeUni = jdbcTemplate.query(
            "SELECT uni.id, uni.name, uni.abbreviation, country.name as country_name, city.name as city_name, city.id as city_id FROM universities uni INNER JOIN cities city ON uni.city_id = city.id INNER JOIN countries country ON city.country_id = country.id WHERE uni.id = ?", 
            UNIVERSITY_ROW_MAPPER, 
            DELETED_UNI_ID
        ).stream().findFirst();
        assertNotNull(maybeUni);
        assertTrue(maybeUni.isPresent());
        University uni = maybeUni.get();
        assertEquals(DELETED_UNI_NAME, uni.getName());
        assertEquals(DELETED_UNI_ID, uni.getId());
        assertEquals(UNIVERSITY_CODE_4, uni.getAbbreviation());
        assertEquals(CITY_NAME, uni.getCity().getName());
        assertEquals(CITY_ID, uni.getCity().getId());
        assertEquals(COUNTRY_NAME, uni.getCity().getCountry());
    }
    @Test
    public void testCreateUniversityDeletedByAbbreviation(){
        uniDao.createUniversity(UNIVERSITY_NAME_4, DELETED_UNI_CODE, CITY_NAME);

        Optional<University> maybeUni = jdbcTemplate.query(
            "SELECT uni.id, uni.name, uni.abbreviation, country.name as country_name, city.name as city_name, city.id as city_id FROM universities uni INNER JOIN cities city ON uni.city_id = city.id INNER JOIN countries country ON city.country_id = country.id WHERE uni.id = ?", 
            UNIVERSITY_ROW_MAPPER, 
            DELETED_UNI_ID
        ).stream().findFirst();
        assertNotNull(maybeUni);
        assertTrue(maybeUni.isPresent());
        University uni = maybeUni.get();
        assertEquals(UNIVERSITY_NAME_4, uni.getName());
        assertEquals(DELETED_UNI_ID, uni.getId());
        assertEquals(DELETED_UNI_CODE, uni.getAbbreviation());
        assertEquals(CITY_NAME, uni.getCity().getName());
        assertEquals(CITY_ID, uni.getCity().getId());
        assertEquals(COUNTRY_NAME, uni.getCity().getCountry());
    }
    @Test
    public void testCreateUniversityDeletedByBoth(){
        uniDao.createUniversity(DELETED_UNI_NAME, DELETED_UNI_CODE, CITY_NAME);

        Optional<University> maybeUni = jdbcTemplate.query(
            "SELECT uni.id, uni.name, uni.abbreviation, country.name as country_name, city.name as city_name, city.id as city_id FROM universities uni INNER JOIN cities city ON uni.city_id = city.id INNER JOIN countries country ON city.country_id = country.id WHERE uni.id = ?", 
            UNIVERSITY_ROW_MAPPER, 
            DELETED_UNI_ID
        ).stream().findFirst();
        assertNotNull(maybeUni);
        assertTrue(maybeUni.isPresent());
        University uni = maybeUni.get();
        assertEquals(DELETED_UNI_NAME, uni.getName());
        assertEquals(DELETED_UNI_ID, uni.getId());
        assertEquals(DELETED_UNI_CODE, uni.getAbbreviation());
        assertEquals(CITY_NAME, uni.getCity().getName());
        assertEquals(CITY_ID, uni.getCity().getId());
        assertEquals(COUNTRY_NAME, uni.getCity().getCountry());
    }

    @SuppressWarnings("null")
    @Test
    public void testDelete(){
        uniDao.delete(UNI_ID_1);

        assertEquals(TOTAL_UNIVERSITIES - 1, jdbcTemplate.queryForObject("SELECT COUNT(*) FROM universities WHERE deleted = FALSE", Integer.class).intValue());
    }
    @SuppressWarnings("null")
    @Test
    public void testDeleteWrongId(){
        uniDao.delete(12341234);

        assertEquals(TOTAL_UNIVERSITIES, jdbcTemplate.queryForObject("SELECT COUNT(*) FROM universities WHERE deleted = FALSE", Integer.class).intValue());
    }
    @SuppressWarnings("null")
    @Test
    public void testDeleteDeleted(){
        uniDao.delete(DELETED_UNI_ID);

        assertEquals(TOTAL_UNIVERSITIES, jdbcTemplate.queryForObject("SELECT COUNT(*) FROM universities WHERE deleted = FALSE", Integer.class).intValue());
    }
    
}