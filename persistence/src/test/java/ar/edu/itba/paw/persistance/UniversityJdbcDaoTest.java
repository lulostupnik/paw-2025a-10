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
import ar.edu.itba.paw.models.PageParams;
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
import org.springframework.transaction.annotation.Transactional;

import ar.edu.itba.paw.models.University;
import ar.edu.itba.paw.persistence.UniversityJdbcDao;

@Transactional
@Rollback
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class UniversityJdbcDaoTest {

    private static University UNI_1;
    private static University UNI_2;
    private static University UNI_3;
    private static University UNI_DELETED;
    private static City CITY_1;
    private static City CITY_2;

    @Autowired
    private DataSource ds;

    @Autowired
    private UniversityJdbcDao uniDao;

    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert insert;

    @Before
    public void setUp(){
        jdbcTemplate = new JdbcTemplate(ds);
        insert = new SimpleJdbcInsert(ds).withTableName(TestUtils.UNIVERSITY_TABLE).usingGeneratedKeyColumns("id");

        CITY_1 = jdbcTemplate.queryForObject(TestUtils.CITY_SELECT_BY_NAME, TestUtils.CITY_ROW_MAPPER, TestUtils.CITY_1_NAME);
        CITY_2 = jdbcTemplate.queryForObject(TestUtils.CITY_SELECT_BY_NAME, TestUtils.CITY_ROW_MAPPER, TestUtils.CITY_2_NAME);

        UNI_1 = jdbcTemplate.queryForObject(TestUtils.UNIVERSITY_SELECT_BY_ABBR, TestUtils.UNIVERSITY_ROW_MAPPER, TestUtils.UNIVERSITY_1_CODE);
        UNI_2 = jdbcTemplate.queryForObject(TestUtils.UNIVERSITY_SELECT_BY_ABBR, TestUtils.UNIVERSITY_ROW_MAPPER, TestUtils.UNIVERSITY_2_CODE);
        UNI_3 = jdbcTemplate.queryForObject(TestUtils.UNIVERSITY_SELECT_BY_ABBR, TestUtils.UNIVERSITY_ROW_MAPPER, TestUtils.UNIVERSITY_3_CODE);

        UNI_DELETED = jdbcTemplate.queryForObject(TestUtils.UNIVERSITY_SELECT_BY_ABBR, TestUtils.UNIVERSITY_ROW_MAPPER, TestUtils.UNIVERSITY_DELETED_CODE);

    }

    @Test
    public void testFindByName(){
        Optional<University> maybeUni = uniDao.findByName(TestUtils.UNIVERSITY_1_NAME);
        assertNotNull(maybeUni);
        assertTrue(maybeUni.isPresent());
        University uni = maybeUni.get();
        assertEquals(TestUtils.UNIVERSITY_1_NAME, uni.getName());
        assertEquals(TestUtils.UNIVERSITY_1_CODE, uni.getAbbreviation());
        assertEquals(UNI_1.getId(), uni.getId());
        assertEquals(CITY_1.getId(), uni.getCity().getId());
    }
    @Test
    public void testFindByName2(){
        Optional<University> maybeUni = uniDao.findByName(TestUtils.UNIVERSITY_2_NAME);
        assertNotNull(maybeUni);
        assertTrue(maybeUni.isPresent());
        University uni = maybeUni.get();
        assertEquals(TestUtils.UNIVERSITY_2_NAME, uni.getName());
        assertEquals(TestUtils.UNIVERSITY_2_CODE, uni.getAbbreviation());
        assertEquals(UNI_2.getId(), uni.getId());
        assertEquals(CITY_2.getId(), uni.getCity().getId());
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
        assertEquals(UNI_1.getId(), uni.getId());
        assertEquals(CITY_1.getId(), uni.getCity().getId());
    }
    @Test
    public void testFindByAbbreviation2(){
        Optional<University> maybeUni = uniDao.findByAbbreviation(TestUtils.UNIVERSITY_2_CODE);
        assertNotNull(maybeUni);
        assertTrue(maybeUni.isPresent());
        University uni = maybeUni.get();
        assertEquals(TestUtils.UNIVERSITY_2_NAME, uni.getName());
        assertEquals(TestUtils.UNIVERSITY_2_CODE, uni.getAbbreviation());
        assertEquals(UNI_2.getId(), uni.getId());
        assertEquals(CITY_2.getId(), uni.getCity().getId());
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
        assertEquals(UNI_1.getId(), uni.getId());
        assertEquals(CITY_1.getId(), uni.getCity().getId());
    }
    @Test
    public void testFindByAnyUsingNameSubstring(){
        Optional<University> maybeUni = uniDao.findByAny(TestUtils.UNIVERSITY_2_NAME.substring(5, 15));
        assertNotNull(maybeUni);
        assertTrue(maybeUni.isPresent());
        University uni = maybeUni.get();
        assertEquals(TestUtils.UNIVERSITY_2_NAME, uni.getName());
        assertEquals(TestUtils.UNIVERSITY_2_CODE, uni.getAbbreviation());
        assertEquals(UNI_2.getId(), uni.getId());
        assertEquals(CITY_2.getId(), uni.getCity().getId());
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
            if (uni.getId() == UNI_1.getId()){
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
        TestUtils.deleteUniversities(jdbcTemplate);
        List<University> unis = uniDao.getAllUniversities();
        assertNotNull(unis);
        assertEquals(0, unis.size());
    }

    @Test
    public void testFindById(){
        Optional<University> maybeUni = uniDao.findById(UNI_1.getId());
        assertNotNull(maybeUni);
        assertTrue(maybeUni.isPresent());
        University uni = maybeUni.get();
        assertEquals(TestUtils.UNIVERSITY_1_NAME, uni.getName());
        assertEquals(TestUtils.UNIVERSITY_1_CODE, uni.getAbbreviation());
        assertEquals(UNI_1.getId(), uni.getId());
        assertEquals(CITY_1.getId(), uni.getCity().getId());
    }
    @Test
    public void testFindByIdWrongId(){
        Optional<University> maybeUni = uniDao.findById(1234123);
        assertNotNull(maybeUni);
        assertFalse(maybeUni.isPresent());
    }
    @Test
    public void testFindByIdDeleted(){
        Optional<University> maybeUni = uniDao.findById(UNI_DELETED.getId());
        assertNotNull(maybeUni);
        assertFalse(maybeUni.isPresent());
    }
    
    @Test
    public void testSearchBySubstringUsingAbbrSubstring(){
        Page<University> unis = uniDao.searchBySubstring(TestUtils.UNIVERSITY_1_CODE.substring(1, 3),new PageParams(1,10));
        assertNotNull(unis);
        assertEquals(1, unis.getCurrentPage());
        assertEquals(1, unis.getTotalPages());
        assertNotNull(unis.getContent());
        assertEquals(1, unis.getContent().size());
        University uni = unis.getContent().getFirst();
        assertEquals(TestUtils.UNIVERSITY_1_NAME, uni.getName());
        assertEquals(TestUtils.UNIVERSITY_1_CODE, uni.getAbbreviation());
        assertEquals(UNI_1.getId(), uni.getId());
        assertEquals(CITY_1.getId(), uni.getCity().getId());
    }
    @Test
    public void testSearchBySubstringUsingNameSubstring(){
        Page<University> unis = uniDao.searchBySubstring(TestUtils.UNIVERSITY_2_NAME.substring(5, 15), new PageParams(1,2));
        assertNotNull(unis);
        assertEquals(1, unis.getCurrentPage());
        assertEquals(1, unis.getTotalPages());
        assertNotNull(unis.getContent());
        assertEquals(1, unis.getContent().size());
        University uni = unis.getContent().getFirst();
        assertEquals(TestUtils.UNIVERSITY_2_NAME, uni.getName());
        assertEquals(TestUtils.UNIVERSITY_2_CODE, uni.getAbbreviation());
        assertEquals(UNI_2.getId(), uni.getId());
        assertEquals(CITY_2.getId(), uni.getCity().getId());
    }
    @Test
    public void testSearchBySubstringMultipleResults(){
        Page<University> unis = uniDao.searchBySubstring(TestUtils.UNIVERSITY_1_NAME.substring(TestUtils.UNIVERSITY_1_NAME.length() - 5, TestUtils.UNIVERSITY_1_NAME.length()), new PageParams(1,10));
        assertNotNull(unis);
        assertEquals(1, unis.getCurrentPage());
        assertEquals(1, unis.getTotalPages());
        assertNotNull(unis.getContent());
        assertEquals(2, unis.getContent().size());
    }
    @Test
    public void testSearchBySubstringWrongQuery(){
        Page<University> unis = uniDao.searchBySubstring("TestUtils.UNIVERSITY_1_CODE", new PageParams(1,10));
        assertNotNull(unis);
        assertEquals(1, unis.getCurrentPage());
        assertEquals(0, unis.getTotalPages());
        assertNotNull(unis.getContent());
        assertEquals(0, unis.getContent().size());    
    }
    @Test
    public void testSearchBySubstringDeleted(){
        Page<University> unis = uniDao.searchBySubstring(TestUtils.UNIVERSITY_DELETED_NAME, new PageParams(1,10));
        assertNotNull(unis);
        assertEquals(1, unis.getCurrentPage());
        assertEquals(0, unis.getTotalPages());
        assertNotNull(unis.getContent());
        assertEquals(0, unis.getContent().size());    
    }
    @Test
    public void testSearchBySubstringEmptyQuery(){
        Page<University> unis = uniDao.searchBySubstring("", new PageParams(1,10));
        assertNotNull(unis);
        assertEquals(1, unis.getCurrentPage());
        assertEquals(1, unis.getTotalPages());
        assertNotNull(unis.getContent());
        assertEquals(TestUtils.TOTAL_UNIVERSITIES, unis.getContent().size());
    }
    @Test
    public void testSearchBySubstringMissingQuery(){
        Page<University> unis = uniDao.searchBySubstring(null, new PageParams(1,10));
        assertNotNull(unis);
        assertEquals(1, unis.getCurrentPage());
        assertEquals(1, unis.getTotalPages());
        assertNotNull(unis.getContent());
        assertEquals(TestUtils.TOTAL_UNIVERSITIES, unis.getContent().size()); 
    }

    @Test
    public void testGetAllUniversitiesPaged(){
        long bonusId = insert.executeAndReturnKey(Map.of("name", TestUtils.UNIVERSITY_NEW_NAME, "abbreviation", TestUtils.UNIVERSITY_NEW_CODE, "CITY_ID", CITY_1.getId(), "deleted", false)).longValue();

        Page<University> page1 = uniDao.getAllUniversities(new PageParams(1,2));
        Page<University> page2 = uniDao.getAllUniversities(new PageParams(2,2));
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
        assertEquals(UNI_3.getId(), page1.getContent().get(0).getId());
        assertEquals(UNI_1.getId(), page1.getContent().get(1).getId());
        assertEquals(UNI_2.getId(), page2.getContent().get(0).getId());
        assertEquals(bonusId, page2.getContent().get(1).getId());
    }
    @Test
    public void testGetAllUniversitiesPagedNoUniversities(){
        TestUtils.deleteUniversities(jdbcTemplate);
        Page<University> page = uniDao.getAllUniversities(TestUtils.PAGE_1_DEFAULT);
        assertNotNull(page);
        assertEquals(1, page.getCurrentPage());
        assertEquals(0, page.getTotalPages());
        assertNotNull(page.getContent());
        assertEquals(0, page.getContent().size());
    }

    @Test
    public void testUpdateUniversity(){
        uniDao.updateUniversity(UNI_1.getId(), TestUtils.UNIVERSITY_NEW_NAME, TestUtils.UNIVERSITY_NEW_CODE, CITY_1.getId());

        Optional<University> maybeUni = jdbcTemplate.query(
            TestUtils.UNIVERSITY_SELECT_BY_ID,
            TestUtils.UNIVERSITY_ROW_MAPPER,
            UNI_1.getId()
        ).stream().findFirst();
        assertNotNull(maybeUni);
        assertTrue(maybeUni.isPresent());
        University uni = maybeUni.get();
        assertEquals(UNI_1.getId(), uni.getId());
        assertEquals(TestUtils.UNIVERSITY_NEW_NAME, uni.getName());
        assertEquals(TestUtils.UNIVERSITY_NEW_CODE, uni.getAbbreviation());
        assertEquals(TestUtils.CITY_1_NAME, uni.getCity().getName());
        assertEquals(CITY_1.getId(), uni.getCity().getId());
        assertEquals(TestUtils.COUNTRY_1_NAME, uni.getCity().getCountry());
    }
    @Test(expected = DataAccessException.class)
    public void testUpdateUniversityDuplicateName(){
        uniDao.updateUniversity(UNI_1.getId(), TestUtils.UNIVERSITY_2_NAME, TestUtils.UNIVERSITY_NEW_CODE, CITY_1.getId());
    }
    @Test
    public void testUpdateUniversityNotFound(){
        //TODO asserts
        uniDao.updateUniversity(12341234, TestUtils.UNIVERSITY_2_NAME, TestUtils.UNIVERSITY_NEW_CODE, CITY_1.getId());
    }
    @Test
    public void testUpdateUniversityCityName(){
        uniDao.updateUniversity(UNI_1.getId(), TestUtils.UNIVERSITY_NEW_NAME, TestUtils.UNIVERSITY_NEW_CODE, TestUtils.CITY_1_NAME);

        Optional<University> maybeUni = jdbcTemplate.query(
            TestUtils.UNIVERSITY_SELECT_BY_ID,
            TestUtils.UNIVERSITY_ROW_MAPPER,
            UNI_1.getId()
        ).stream().findFirst();
        assertNotNull(maybeUni);
        assertTrue(maybeUni.isPresent());
        University uni = maybeUni.get();
        assertEquals(UNI_1.getId(), uni.getId());
        assertEquals(TestUtils.UNIVERSITY_NEW_NAME, uni.getName());
        assertEquals(TestUtils.UNIVERSITY_NEW_CODE, uni.getAbbreviation());
        assertEquals(TestUtils.CITY_1_NAME, uni.getCity().getName());
        assertEquals(CITY_1.getId(), uni.getCity().getId());
        assertEquals(TestUtils.COUNTRY_1_NAME, uni.getCity().getCountry());
    }
    @Test(expected = DataAccessException.class)
    public void testUpdateUniversityCityNameDuplicateName(){
        //TODO asserts
        uniDao.updateUniversity(UNI_1.getId(), TestUtils.UNIVERSITY_2_NAME, TestUtils.UNIVERSITY_NEW_CODE, TestUtils.CITY_1_NAME);
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
            TestUtils.UNIVERSITY_SELECT_BY_NAME,
            TestUtils.UNIVERSITY_ROW_MAPPER,
            TestUtils.UNIVERSITY_NEW_NAME
        ).stream().findFirst();
        assertNotNull(maybeUni);
        assertTrue(maybeUni.isPresent());
        University uni = maybeUni.get();
        assertEquals(TestUtils.UNIVERSITY_NEW_NAME, uni.getName());
        assertEquals(TestUtils.UNIVERSITY_NEW_CODE, uni.getAbbreviation());
        assertEquals(TestUtils.CITY_1_NAME, uni.getCity().getName());
        assertEquals(CITY_1.getId(), uni.getCity().getId());
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
            TestUtils.UNIVERSITY_SELECT_BY_ID,
            TestUtils.UNIVERSITY_ROW_MAPPER, 
            UNI_DELETED.getId()
        ).stream().findFirst();
        assertNotNull(maybeUni);
        assertTrue(maybeUni.isPresent());
        University uni = maybeUni.get();
        assertEquals(TestUtils.UNIVERSITY_DELETED_NAME, uni.getName());
        assertEquals(UNI_DELETED.getId(), uni.getId());
        assertEquals(TestUtils.UNIVERSITY_NEW_CODE, uni.getAbbreviation());
        assertEquals(TestUtils.CITY_1_NAME, uni.getCity().getName());
        assertEquals(CITY_1.getId(), uni.getCity().getId());
        assertEquals(TestUtils.COUNTRY_1_NAME, uni.getCity().getCountry());
    }
    @Test
    public void testCreateUniversityDeletedByAbbreviation(){
        uniDao.createUniversity(TestUtils.UNIVERSITY_NEW_NAME, TestUtils.UNIVERSITY_DELETED_CODE, TestUtils.CITY_1_NAME);

        Optional<University> maybeUni = jdbcTemplate.query(
            TestUtils.UNIVERSITY_SELECT_BY_ID,
            TestUtils.UNIVERSITY_ROW_MAPPER, 
            UNI_DELETED.getId()
        ).stream().findFirst();
        assertNotNull(maybeUni);
        assertTrue(maybeUni.isPresent());
        University uni = maybeUni.get();
        assertEquals(TestUtils.UNIVERSITY_NEW_NAME, uni.getName());
        assertEquals(UNI_DELETED.getId(), uni.getId());
        assertEquals(TestUtils.UNIVERSITY_DELETED_CODE, uni.getAbbreviation());
        assertEquals(TestUtils.CITY_1_NAME, uni.getCity().getName());
        assertEquals(CITY_1.getId(), uni.getCity().getId());
        assertEquals(TestUtils.COUNTRY_1_NAME, uni.getCity().getCountry());
    }
    @Test
    public void testCreateUniversityDeletedByBoth(){
        uniDao.createUniversity(TestUtils.UNIVERSITY_DELETED_NAME, TestUtils.UNIVERSITY_DELETED_CODE, TestUtils.CITY_1_NAME);

        Optional<University> maybeUni = jdbcTemplate.query(
            TestUtils.UNIVERSITY_SELECT_BY_ID,
            TestUtils.UNIVERSITY_ROW_MAPPER, 
            UNI_DELETED.getId()
        ).stream().findFirst();
        assertNotNull(maybeUni);
        assertTrue(maybeUni.isPresent());
        University uni = maybeUni.get();
        assertEquals(TestUtils.UNIVERSITY_DELETED_NAME, uni.getName());
        assertEquals(UNI_DELETED.getId(), uni.getId());
        assertEquals(TestUtils.UNIVERSITY_DELETED_CODE, uni.getAbbreviation());
        assertEquals(TestUtils.CITY_1_NAME, uni.getCity().getName());
        assertEquals(CITY_1.getId(), uni.getCity().getId());
        assertEquals(TestUtils.COUNTRY_1_NAME, uni.getCity().getCountry());
    }

    @Test
    public void testDelete(){
        uniDao.delete(UNI_1.getId());

        assertEquals(
            TestUtils.TOTAL_UNIVERSITIES - 1, 
            Optional.ofNullable(
                jdbcTemplate.queryForObject("SELECT COUNT(*) FROM universities WHERE deleted = FALSE", Integer.class)
            ).get().intValue()
        );
    }
    @Test
    public void testDeleteWrongId(){
        uniDao.delete(12341234);

        assertEquals(
            TestUtils.TOTAL_UNIVERSITIES, 
            Optional.ofNullable(
                jdbcTemplate.queryForObject("SELECT COUNT(*) FROM universities WHERE deleted = FALSE", Integer.class)
            ).get().intValue()
        );    
    }
    @Test
    public void testDeleteDeleted(){
        uniDao.delete(UNI_DELETED.getId());

        assertEquals(
            TestUtils.TOTAL_UNIVERSITIES, 
            Optional.ofNullable(
                jdbcTemplate.queryForObject("SELECT COUNT(*) FROM universities WHERE deleted = FALSE", Integer.class)
            ).get().intValue()
        );    
    }
}