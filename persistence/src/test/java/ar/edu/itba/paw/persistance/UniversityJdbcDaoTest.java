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

    private static long UNI_ID_1;
    private static long UNI_ID_2;
    private static long UNI_ID_3;
    private static long DELETED_UNI_ID;
    private static long CITY_1_ID;
    private static long CITY_2_ID;

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
        insert = new SimpleJdbcInsert(ds).withTableName(TestUtils.UNIVERSITY_TABLE).usingGeneratedKeyColumns("id");

        CITY_1_ID = jdbcTemplate.queryForObject("SELECT id FROM cities WHERE name = ?", Long.class, TestUtils.CITY_1_NAME);
        CITY_2_ID = jdbcTemplate.queryForObject("SELECT id FROM cities WHERE name = ?", Long.class, TestUtils.CITY_2_NAME);

        UNI_ID_1 = jdbcTemplate.queryForObject("SELECT id FROM universities WHERE abbreviation = ?", Long.class, TestUtils.UNIVERSITY_1_CODE);
        UNI_ID_2 = jdbcTemplate.queryForObject("SELECT id FROM universities WHERE abbreviation = ?", Long.class, TestUtils.UNIVERSITY_2_CODE);
        UNI_ID_3 = jdbcTemplate.queryForObject("SELECT id FROM universities WHERE abbreviation = ?", Long.class, TestUtils.UNIVERSITY_3_CODE);

        DELETED_UNI_ID = jdbcTemplate.queryForObject("SELECT id FROM universities WHERE abbreviation = ?", Long.class, TestUtils.UNIVERSITY_DELETED_CODE);

    }

    @Test
    public void testFindByName(){
        Optional<University> maybeUni = uniDao.findByName(TestUtils.UNIVERSITY_1_NAME);
        assertNotNull(maybeUni);
        assertTrue(maybeUni.isPresent());
        University uni = maybeUni.get();
        assertEquals(TestUtils.UNIVERSITY_1_NAME, uni.getName());
        assertEquals(TestUtils.UNIVERSITY_1_CODE, uni.getAbbreviation());
        assertEquals(UNI_ID_1, uni.getId());
        assertEquals(CITY_1_ID, uni.getCity().getId());
    }
    @Test
    public void testFindByName2(){
        Optional<University> maybeUni = uniDao.findByName(TestUtils.UNIVERSITY_2_NAME);
        assertNotNull(maybeUni);
        assertTrue(maybeUni.isPresent());
        University uni = maybeUni.get();
        assertEquals(TestUtils.UNIVERSITY_2_NAME, uni.getName());
        assertEquals(TestUtils.UNIVERSITY_2_CODE, uni.getAbbreviation());
        assertEquals(UNI_ID_2, uni.getId());
        assertEquals(CITY_2_ID, uni.getCity().getId());
    }
    @Test
    public void testFindByNameDeleted(){
        Optional<University> maybeUni = uniDao.findByName(TestUtils.UNIVERSITY_DELETED_NAME);
        assertNotNull(maybeUni);
        assertFalse(maybeUni.isPresent());
    }
    @Test
    public void testFindByNameWrongName(){
        Optional<University> maybeUni = uniDao.findByName("TestUtils.UNIVERSITY_2_NAME");
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
        Optional<University> maybeUni = uniDao.findByAbbreviation(TestUtils.UNIVERSITY_1_CODE);
        assertNotNull(maybeUni);
        assertTrue(maybeUni.isPresent());
        University uni = maybeUni.get();
        assertEquals(TestUtils.UNIVERSITY_1_NAME, uni.getName());
        assertEquals(TestUtils.UNIVERSITY_1_CODE, uni.getAbbreviation());
        assertEquals(UNI_ID_1, uni.getId());
        assertEquals(CITY_1_ID, uni.getCity().getId());
    }
    @Test
    public void testFindByAbbreviation2(){
        Optional<University> maybeUni = uniDao.findByAbbreviation(TestUtils.UNIVERSITY_2_CODE);
        assertNotNull(maybeUni);
        assertTrue(maybeUni.isPresent());
        University uni = maybeUni.get();
        assertEquals(TestUtils.UNIVERSITY_2_NAME, uni.getName());
        assertEquals(TestUtils.UNIVERSITY_2_CODE, uni.getAbbreviation());
        assertEquals(UNI_ID_2, uni.getId());
        assertEquals(CITY_2_ID, uni.getCity().getId());
    }
    @Test
    public void testFindByAbbreviationDeleted(){
        Optional<University> maybeUni = uniDao.findByAbbreviation(TestUtils.UNIVERSITY_DELETED_CODE);
        assertNotNull(maybeUni);
        assertFalse(maybeUni.isPresent());
    }
    @Test
    public void testFindByAbbreviationWrongAbbreviation(){
        Optional<University> maybeUni = uniDao.findByAbbreviation("TestUtils.UNIVERSITY_1_CODE");
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
        Optional<University> maybeUni = uniDao.findByAny(TestUtils.UNIVERSITY_1_CODE.substring(1, 3));
        assertNotNull(maybeUni);
        assertTrue(maybeUni.isPresent());
        University uni = maybeUni.get();
        assertEquals(TestUtils.UNIVERSITY_1_NAME, uni.getName());
        assertEquals(TestUtils.UNIVERSITY_1_CODE, uni.getAbbreviation());
        assertEquals(UNI_ID_1, uni.getId());
        assertEquals(CITY_1_ID, uni.getCity().getId());
    }
    @Test
    public void testFindByAnyUsingNameSubstring(){
        Optional<University> maybeUni = uniDao.findByAny(TestUtils.UNIVERSITY_2_NAME.substring(5, 15));
        assertNotNull(maybeUni);
        assertTrue(maybeUni.isPresent());
        University uni = maybeUni.get();
        assertEquals(TestUtils.UNIVERSITY_2_NAME, uni.getName());
        assertEquals(TestUtils.UNIVERSITY_2_CODE, uni.getAbbreviation());
        assertEquals(UNI_ID_2, uni.getId());
        assertEquals(CITY_2_ID, uni.getCity().getId());
    }
    @Test
    public void testFindByAnyWrongQuery(){
        Optional<University> maybeUni = uniDao.findByAny("TestUtils.UNIVERSITY_1_CODE");
        assertNotNull(maybeUni);
        assertFalse(maybeUni.isPresent());
    }
    @Test
    public void testFindByAnyDeleted(){
        Optional<University> maybeUni = uniDao.findByAny(TestUtils.UNIVERSITY_DELETED_NAME);
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
        assertEquals(TestUtils.TOTAL_UNIVERSITIES, unis.size());
        //TODO if-else
        for (University uni : unis) {
            //assertEquals(TestUtils.CITY_1_NAME, uni.getCity().getName());
            if (uni.getId() == UNI_ID_1){
                assertEquals(TestUtils.UNIVERSITY_1_NAME, uni.getName());
                assertEquals(TestUtils.UNIVERSITY_1_CODE, uni.getAbbreviation());
            } else {
                //assertEquals(TestUtils.UNIVERSITY_2_NAME, uni.getName());
                //assertEquals(TestUtils.UNIVERSITY_2_CODE, uni.getAbbreviation());
            }
        }
    }
    @Test
    public void testGetAllUniversitiesNoUniversities(){
        JdbcTestUtils.deleteFromTables(jdbcTemplate, TestUtils.UNIVERSITY_TABLE);
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
        assertEquals(TestUtils.UNIVERSITY_1_NAME, uni.getName());
        assertEquals(TestUtils.UNIVERSITY_1_CODE, uni.getAbbreviation());
        assertEquals(UNI_ID_1, uni.getId());
        assertEquals(CITY_1_ID, uni.getCity().getId());
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
        Page<University> unis = uniDao.searchBySubstring(TestUtils.UNIVERSITY_1_CODE.substring(1, 3),1,10);
        assertNotNull(unis);
        assertEquals(1, unis.getCurrentPage());
        assertEquals(1, unis.getTotalPages());
        assertNotNull(unis.getContent());
        assertEquals(1, unis.getContent().size());
        University uni = unis.getContent().getFirst();
        assertEquals(TestUtils.UNIVERSITY_1_NAME, uni.getName());
        assertEquals(TestUtils.UNIVERSITY_1_CODE, uni.getAbbreviation());
        assertEquals(UNI_ID_1, uni.getId());
        assertEquals(CITY_1_ID, uni.getCity().getId());
    }
    @Test
    public void testSearchBySubstringUsingNameSubstring(){
        Page<University> unis = uniDao.searchBySubstring(TestUtils.UNIVERSITY_2_NAME.substring(5, 15), 1, 2);
        assertNotNull(unis);
        assertEquals(1, unis.getCurrentPage());
        assertEquals(1, unis.getTotalPages());
        assertNotNull(unis.getContent());
        assertEquals(1, unis.getContent().size());
        University uni = unis.getContent().getFirst();
        assertEquals(TestUtils.UNIVERSITY_2_NAME, uni.getName());
        assertEquals(TestUtils.UNIVERSITY_2_CODE, uni.getAbbreviation());
        assertEquals(UNI_ID_2, uni.getId());
        assertEquals(CITY_2_ID, uni.getCity().getId());
    }
    @Test
    public void testSearchBySubstringMultipleResults(){
        Page<University> unis = uniDao.searchBySubstring(TestUtils.UNIVERSITY_1_NAME.substring(TestUtils.UNIVERSITY_1_NAME.length() - 5, TestUtils.UNIVERSITY_1_NAME.length()), 1, 10);
        assertNotNull(unis);
        assertEquals(1, unis.getCurrentPage());
        assertEquals(1, unis.getTotalPages());
        assertNotNull(unis.getContent());
        assertEquals(2, unis.getContent().size());
    }
    @Test
    public void testSearchBySubstringWrongQuery(){
        Page<University> unis = uniDao.searchBySubstring("TestUtils.UNIVERSITY_1_CODE", 1, 10);
        assertNotNull(unis);
        assertEquals(1, unis.getCurrentPage());
        assertEquals(0, unis.getTotalPages());
        assertNotNull(unis.getContent());
        assertEquals(0, unis.getContent().size());    
    }
    @Test
    public void testSearchBySubstringDeleted(){
        Page<University> unis = uniDao.searchBySubstring(TestUtils.UNIVERSITY_DELETED_NAME, 1, 10);
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
        assertEquals(TestUtils.TOTAL_UNIVERSITIES, unis.getContent().size());
    }
    @Test
    public void testSearchBySubstringMissingQuery(){
        Page<University> unis = uniDao.searchBySubstring(null, 1, 10);
        assertNotNull(unis);
        assertEquals(1, unis.getCurrentPage());
        assertEquals(1, unis.getTotalPages());
        assertNotNull(unis.getContent());
        assertEquals(TestUtils.TOTAL_UNIVERSITIES, unis.getContent().size()); 
    }

    @Test
    public void testGetAllUniversitiesPaged(){
        long bonusId = insert.executeAndReturnKey(Map.of("name", TestUtils.UNIVERSITY_NEW_NAME, "abbreviation", TestUtils.UNIVERSITY_NEW_CODE, "CITY_ID", CITY_1_ID, "deleted", false)).longValue();

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
        assertEquals(2, page2.getContent().size());
        assertEquals(UNI_ID_3, page1.getContent().get(0).getId());
        assertEquals(UNI_ID_1, page1.getContent().get(1).getId());
        assertEquals(UNI_ID_2, page2.getContent().get(0).getId());
        assertEquals(bonusId, page2.getContent().get(1).getId());
    }
    @Test
    public void testGetAllUniversitiesPagedNoUniversities(){
        JdbcTestUtils.deleteFromTables(jdbcTemplate, TestUtils.UNIVERSITY_TABLE);
        Page<University> page = uniDao.getAllUniversities(1, 2);
        assertNotNull(page);
        assertEquals(1, page.getCurrentPage());
        assertEquals(0, page.getTotalPages());
        assertNotNull(page.getContent());
        assertEquals(0, page.getContent().size());
    }

    @Test
    public void testUpdateUniversity(){
        uniDao.updateUniversity(UNI_ID_1, TestUtils.UNIVERSITY_NEW_NAME, TestUtils.UNIVERSITY_NEW_CODE, CITY_1_ID);

        Optional<University> maybeUni = jdbcTemplate.query(
            "SELECT uni.id, uni.name, uni.abbreviation, country.name as country_name, city.name as city_name, city.id as city_id FROM universities uni INNER JOIN cities city ON uni.city_id = city.id INNER JOIN countries country ON city.country_id = country.id WHERE uni.id = ?",
            UNIVERSITY_ROW_MAPPER,
            UNI_ID_1
        ).stream().findFirst();
        assertNotNull(maybeUni);
        assertTrue(maybeUni.isPresent());
        University uni = maybeUni.get();
        assertEquals(UNI_ID_1, uni.getId());
        assertEquals(TestUtils.UNIVERSITY_NEW_NAME, uni.getName());
        assertEquals(TestUtils.UNIVERSITY_NEW_CODE, uni.getAbbreviation());
        assertEquals(TestUtils.CITY_1_NAME, uni.getCity().getName());
        assertEquals(CITY_1_ID, uni.getCity().getId());
        assertEquals(TestUtils.COUNTRY_1_NAME, uni.getCity().getCountry());
    }
    @Test(expected = DataAccessException.class)
    public void testUpdateUniversityDuplicateName(){
        uniDao.updateUniversity(UNI_ID_1, TestUtils.UNIVERSITY_2_NAME, TestUtils.UNIVERSITY_NEW_CODE, CITY_1_ID);
    }
    @Test
    public void testUpdateUniversityNotFound(){
        //TODO asserts
        uniDao.updateUniversity(12341234, TestUtils.UNIVERSITY_2_NAME, TestUtils.UNIVERSITY_NEW_CODE, CITY_1_ID);
    }
    @Test
    public void testUpdateUniversityCityName(){
        uniDao.updateUniversity(UNI_ID_1, TestUtils.UNIVERSITY_NEW_NAME, TestUtils.UNIVERSITY_NEW_CODE, TestUtils.CITY_1_NAME);

        Optional<University> maybeUni = jdbcTemplate.query(
            "SELECT uni.id, uni.name, uni.abbreviation, country.name as country_name, city.name as city_name, city.id as city_id FROM universities uni INNER JOIN cities city ON uni.city_id = city.id INNER JOIN countries country ON city.country_id = country.id WHERE uni.id = ?",
            UNIVERSITY_ROW_MAPPER,
            UNI_ID_1
        ).stream().findFirst();
        assertNotNull(maybeUni);
        assertTrue(maybeUni.isPresent());
        University uni = maybeUni.get();
        assertEquals(UNI_ID_1, uni.getId());
        assertEquals(TestUtils.UNIVERSITY_NEW_NAME, uni.getName());
        assertEquals(TestUtils.UNIVERSITY_NEW_CODE, uni.getAbbreviation());
        assertEquals(TestUtils.CITY_1_NAME, uni.getCity().getName());
        assertEquals(CITY_1_ID, uni.getCity().getId());
        assertEquals(TestUtils.COUNTRY_1_NAME, uni.getCity().getCountry());
    }
    @Test(expected = DataAccessException.class)
    public void testUpdateUniversityCityNameDuplicateName(){
        //TODO asserts
        uniDao.updateUniversity(UNI_ID_1, TestUtils.UNIVERSITY_2_NAME, TestUtils.UNIVERSITY_NEW_CODE, TestUtils.CITY_1_NAME);
    }
    @Test
    public void testUpdateUniversityCityNameNotFound(){
        //TODO asserts
        uniDao.updateUniversity(12341234, TestUtils.UNIVERSITY_2_NAME, TestUtils.UNIVERSITY_NEW_CODE, TestUtils.CITY_1_NAME);
    }

    @Test
    public void testCreateUniversity(){
        uniDao.createUniversity(TestUtils.UNIVERSITY_NEW_NAME, TestUtils.UNIVERSITY_NEW_CODE, TestUtils.CITY_1_NAME);

        Optional<University> maybeUni = jdbcTemplate.query(
            "SELECT uni.id, uni.name, uni.abbreviation, country.name as country_name, city.name as city_name, city.id as city_id FROM universities uni INNER JOIN cities city ON uni.city_id = city.id INNER JOIN countries country ON city.country_id = country.id WHERE uni.name = ?", 
            UNIVERSITY_ROW_MAPPER,
            TestUtils.UNIVERSITY_NEW_NAME
        ).stream().findFirst();
        assertNotNull(maybeUni);
        assertTrue(maybeUni.isPresent());
        University uni = maybeUni.get();
        assertEquals(TestUtils.UNIVERSITY_NEW_NAME, uni.getName());
        assertEquals(TestUtils.UNIVERSITY_NEW_CODE, uni.getAbbreviation());
        assertEquals(TestUtils.CITY_1_NAME, uni.getCity().getName());
        assertEquals(CITY_1_ID, uni.getCity().getId());
        assertEquals(TestUtils.COUNTRY_1_NAME, uni.getCity().getCountry());
    }
    @Test(expected = DataAccessException.class)
    public void testCreateUniversityDuplicate(){
        uniDao.createUniversity(TestUtils.UNIVERSITY_1_NAME, TestUtils.UNIVERSITY_NEW_CODE, TestUtils.CITY_1_NAME);
    }
    @Test
    public void testCreateUniversityDeletedByName(){
        uniDao.createUniversity(TestUtils.UNIVERSITY_DELETED_NAME, TestUtils.UNIVERSITY_NEW_CODE, TestUtils.CITY_1_NAME);

        Optional<University> maybeUni = jdbcTemplate.query(
            "SELECT uni.id, uni.name, uni.abbreviation, country.name as country_name, city.name as city_name, city.id as city_id FROM universities uni INNER JOIN cities city ON uni.city_id = city.id INNER JOIN countries country ON city.country_id = country.id WHERE uni.id = ?", 
            UNIVERSITY_ROW_MAPPER, 
            DELETED_UNI_ID
        ).stream().findFirst();
        assertNotNull(maybeUni);
        assertTrue(maybeUni.isPresent());
        University uni = maybeUni.get();
        assertEquals(TestUtils.UNIVERSITY_DELETED_NAME, uni.getName());
        assertEquals(DELETED_UNI_ID, uni.getId());
        assertEquals(TestUtils.UNIVERSITY_NEW_CODE, uni.getAbbreviation());
        assertEquals(TestUtils.CITY_1_NAME, uni.getCity().getName());
        assertEquals(CITY_1_ID, uni.getCity().getId());
        assertEquals(TestUtils.COUNTRY_1_NAME, uni.getCity().getCountry());
    }
    @Test
    public void testCreateUniversityDeletedByAbbreviation(){
        uniDao.createUniversity(TestUtils.UNIVERSITY_NEW_NAME, TestUtils.UNIVERSITY_DELETED_CODE, TestUtils.CITY_1_NAME);

        Optional<University> maybeUni = jdbcTemplate.query(
            "SELECT uni.id, uni.name, uni.abbreviation, country.name as country_name, city.name as city_name, city.id as city_id FROM universities uni INNER JOIN cities city ON uni.city_id = city.id INNER JOIN countries country ON city.country_id = country.id WHERE uni.id = ?", 
            UNIVERSITY_ROW_MAPPER, 
            DELETED_UNI_ID
        ).stream().findFirst();
        assertNotNull(maybeUni);
        assertTrue(maybeUni.isPresent());
        University uni = maybeUni.get();
        assertEquals(TestUtils.UNIVERSITY_NEW_NAME, uni.getName());
        assertEquals(DELETED_UNI_ID, uni.getId());
        assertEquals(TestUtils.UNIVERSITY_DELETED_CODE, uni.getAbbreviation());
        assertEquals(TestUtils.CITY_1_NAME, uni.getCity().getName());
        assertEquals(CITY_1_ID, uni.getCity().getId());
        assertEquals(TestUtils.COUNTRY_1_NAME, uni.getCity().getCountry());
    }
    @Test
    public void testCreateUniversityDeletedByBoth(){
        uniDao.createUniversity(TestUtils.UNIVERSITY_DELETED_NAME, TestUtils.UNIVERSITY_DELETED_CODE, TestUtils.CITY_1_NAME);

        Optional<University> maybeUni = jdbcTemplate.query(
            "SELECT uni.id, uni.name, uni.abbreviation, country.name as country_name, city.name as city_name, city.id as city_id FROM universities uni INNER JOIN cities city ON uni.city_id = city.id INNER JOIN countries country ON city.country_id = country.id WHERE uni.id = ?", 
            UNIVERSITY_ROW_MAPPER, 
            DELETED_UNI_ID
        ).stream().findFirst();
        assertNotNull(maybeUni);
        assertTrue(maybeUni.isPresent());
        University uni = maybeUni.get();
        assertEquals(TestUtils.UNIVERSITY_DELETED_NAME, uni.getName());
        assertEquals(DELETED_UNI_ID, uni.getId());
        assertEquals(TestUtils.UNIVERSITY_DELETED_CODE, uni.getAbbreviation());
        assertEquals(TestUtils.CITY_1_NAME, uni.getCity().getName());
        assertEquals(CITY_1_ID, uni.getCity().getId());
        assertEquals(TestUtils.COUNTRY_1_NAME, uni.getCity().getCountry());
    }

    @SuppressWarnings("null")
    @Test
    public void testDelete(){
        uniDao.delete(UNI_ID_1);

        assertEquals(TestUtils.TOTAL_UNIVERSITIES - 1, jdbcTemplate.queryForObject("SELECT COUNT(*) FROM universities WHERE deleted = FALSE", Integer.class).intValue());
    }
    @SuppressWarnings("null")
    @Test
    public void testDeleteWrongId(){
        uniDao.delete(12341234);

        assertEquals(TestUtils.TOTAL_UNIVERSITIES, jdbcTemplate.queryForObject("SELECT COUNT(*) FROM universities WHERE deleted = FALSE", Integer.class).intValue());
    }
    @SuppressWarnings("null")
    @Test
    public void testDeleteDeleted(){
        uniDao.delete(DELETED_UNI_ID);

        assertEquals(TestUtils.TOTAL_UNIVERSITIES, jdbcTemplate.queryForObject("SELECT COUNT(*) FROM universities WHERE deleted = FALSE", Integer.class).intValue());
    }
    
}