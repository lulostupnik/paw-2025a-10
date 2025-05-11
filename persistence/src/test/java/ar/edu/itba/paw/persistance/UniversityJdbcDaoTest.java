package ar.edu.itba.paw.persistance;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
import org.springframework.test.jdbc.JdbcTestUtils;
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

    private void assertUniversityDBDefaultState(){
        TestUtils.assertEqualsUni(UNI_1, jdbcTemplate.queryForObject(TestUtils.UNIVERSITY_SELECT_BY_ID, TestUtils.UNIVERSITY_ROW_MAPPER, UNI_1.getId()));
        TestUtils.assertEqualsUni(UNI_2, jdbcTemplate.queryForObject(TestUtils.UNIVERSITY_SELECT_BY_ID, TestUtils.UNIVERSITY_ROW_MAPPER, UNI_2.getId()));
        TestUtils.assertEqualsUni(UNI_3, jdbcTemplate.queryForObject(TestUtils.UNIVERSITY_SELECT_BY_ID, TestUtils.UNIVERSITY_ROW_MAPPER, UNI_3.getId()));
    }

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
        TestUtils.assertEqualsUni(UNI_1, maybeUni.get());
    }
    @Test
    public void testFindByName2(){
        Optional<University> maybeUni = uniDao.findByName(TestUtils.UNIVERSITY_2_NAME);

        assertNotNull(maybeUni);
        assertTrue(maybeUni.isPresent());
        TestUtils.assertEqualsUni(UNI_2, maybeUni.get());
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
    public void testFindById(){
        Optional<University> maybeUni = uniDao.findById(UNI_1.getId());

        assertNotNull(maybeUni);
        assertTrue(maybeUni.isPresent());
        TestUtils.assertEqualsUni(UNI_1, maybeUni.get());
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
    public void testSearchUsingAbbreviationSubstring(){
        Page<University> unis = uniDao.search(TestUtils.UNIVERSITY_1_CODE.substring(1, 3), TestUtils.PAGE_1_DEFAULT);

        assertNotNull(unis);
        assertEquals(1, unis.getCurrentPage());
        assertEquals(1, unis.getTotalPages());
        assertNotNull(unis.getContent());
        assertEquals(1, unis.getContent().size());
        TestUtils.assertEqualsUni(UNI_1, unis.getContent().get(0));
    }
    @Test
    public void testSearchUsingNameSubstring(){
        Page<University> unis = uniDao.search(TestUtils.UNIVERSITY_2_NAME.substring(5, 15), TestUtils.PAGE_1_DEFAULT);

        assertNotNull(unis);
        assertEquals(1, unis.getCurrentPage());
        assertEquals(1, unis.getTotalPages());
        assertNotNull(unis.getContent());
        assertEquals(1, unis.getContent().size());
        TestUtils.assertEqualsUni(UNI_2, unis.getContent().get(0));
    }
    @Test
    public void testSearchMultipleResults(){
        Page<University> unis = uniDao.search(TestUtils.UNIVERSITY_1_NAME.substring(TestUtils.UNIVERSITY_1_NAME.length() - 5, TestUtils.UNIVERSITY_1_NAME.length()), TestUtils.PAGE_1_BIG);
        
        assertNotNull(unis);
        assertEquals(1, unis.getCurrentPage());
        assertEquals(1, unis.getTotalPages());
        assertNotNull(unis.getContent());
        assertEquals(2, unis.getContent().size());
        TestUtils.assertEqualsUni(UNI_1, unis.getContent().get(0));
        TestUtils.assertEqualsUni(UNI_2, unis.getContent().get(1));
    }
    @Test
    public void testSearchWrongQuery(){
        Page<University> unis = uniDao.search("TestUtils.UNIVERSITY_1_CODE", TestUtils.PAGE_1_BIG);
        
        assertNotNull(unis);
        assertEquals(1, unis.getCurrentPage());
        assertEquals(0, unis.getTotalPages());
        assertNotNull(unis.getContent());
        assertEquals(0, unis.getContent().size());    
    }
    @Test
    public void testSearchDeleted(){
        Page<University> unis = uniDao.search(TestUtils.UNIVERSITY_DELETED_NAME, TestUtils.PAGE_1_BIG);
        
        assertNotNull(unis);
        assertEquals(1, unis.getCurrentPage());
        assertEquals(0, unis.getTotalPages());
        assertNotNull(unis.getContent());
        assertEquals(0, unis.getContent().size());    
    }
    @Test
    public void testSearchEmptyQuery(){
        Page<University> unis = uniDao.search("", TestUtils.PAGE_1_BIG);
        
        assertNotNull(unis);
        assertEquals(1, unis.getCurrentPage());
        assertEquals(1, unis.getTotalPages());
        assertNotNull(unis.getContent());
        assertEquals(TestUtils.TOTAL_UNIVERSITIES, unis.getContent().size());
    }
    @Test
    public void testSearchMissingQuery(){
        Page<University> unis = uniDao.search(null, TestUtils.PAGE_1_BIG);

        assertNotNull(unis);
        assertEquals(1, unis.getCurrentPage());
        assertEquals(1, unis.getTotalPages());
        assertNotNull(unis.getContent());
        assertEquals(TestUtils.TOTAL_UNIVERSITIES, unis.getContent().size()); 
    }

    @Test
    public void testFindAllPaged(){
        long bonusId = insert.executeAndReturnKey(Map.of("name", TestUtils.UNIVERSITY_NEW_NAME, "abbreviation", TestUtils.UNIVERSITY_NEW_CODE, "CITY_ID", CITY_1.getId(), "deleted", false)).longValue();
        University bonus = jdbcTemplate.queryForObject(TestUtils.UNIVERSITY_SELECT_BY_ID, TestUtils.UNIVERSITY_ROW_MAPPER, bonusId);
        
        Page<University> page1 = uniDao.findAll(new PageParams(1,2));
        Page<University> page2 = uniDao.findAll(new PageParams(2,2));

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
        assertNotNull(bonus);
        Map<Long, University> uniData = Map.of(UNI_1.getId(), UNI_1, UNI_2.getId(), UNI_2, UNI_3.getId(), UNI_3, bonus.getId(), bonus);
        for (University uni : page1.getContent()){
            TestUtils.assertEqualsUni(uniData.get(uni.getId()), uni);
        }
        for (University uni : page2.getContent()){
            TestUtils.assertEqualsUni(uniData.get(uni.getId()), uni);
        }
    }
    @Test
    public void testFindAllUniversitiesPagedNo(){
        TestUtils.deleteUniversities(jdbcTemplate);
        
        Page<University> page = uniDao.findAll(TestUtils.PAGE_1_DEFAULT);
       
        assertNotNull(page);
        assertEquals(1, page.getCurrentPage());
        assertEquals(0, page.getTotalPages());
        assertNotNull(page.getContent());
        assertEquals(0, page.getContent().size());
    }

    @Test
    public void testUpdateCityName(){
        uniDao.update(UNI_1.getId(), TestUtils.UNIVERSITY_NEW_NAME, TestUtils.UNIVERSITY_NEW_CODE, TestUtils.CITY_2_NAME);

        University uni = jdbcTemplate.queryForObject(
            TestUtils.UNIVERSITY_SELECT_BY_ID,
            TestUtils.UNIVERSITY_ROW_MAPPER,
            UNI_1.getId()
        );
        TestUtils.assertEqualsUni(new University(UNI_1.getId(), TestUtils.UNIVERSITY_NEW_NAME, TestUtils.UNIVERSITY_NEW_CODE, CITY_2), uni);
    }
    @Test(expected = DataAccessException.class)
    public void testUpdateCityNameDuplicateName(){
        uniDao.update(UNI_1.getId(), TestUtils.UNIVERSITY_2_NAME, TestUtils.UNIVERSITY_NEW_CODE, TestUtils.CITY_1_NAME);

        assertUniversityDBDefaultState();
    }
    @Test
    public void testUpdateCityNameNotFound(){
        uniDao.update(12341234, TestUtils.UNIVERSITY_2_NAME, TestUtils.UNIVERSITY_NEW_CODE, TestUtils.CITY_1_NAME);
                
        assertUniversityDBDefaultState();
    }

    // FIXME: el create ya no recibe el String cityName, sino City city
//    @Test
//    public void testCreate(){
//        University uni = uniDao.create(TestUtils.UNIVERSITY_NEW_NAME, TestUtils.UNIVERSITY_NEW_CODE, TestUtils.CITY_1_NAME);
//
//        TestUtils.assertEqualsUni(new University(uni.getId(), TestUtils.UNIVERSITY_NEW_NAME, TestUtils.UNIVERSITY_NEW_CODE, CITY_1), uni);
//    }
//    @Test(expected = DataAccessException.class)
//    public void testCreateDuplicate(){
//        uniDao.create(TestUtils.UNIVERSITY_1_NAME, TestUtils.UNIVERSITY_NEW_CODE, TestUtils.CITY_1_NAME);
//    }
//    @Test
//    public void testCreateDeletedByName(){
//        University uni = uniDao.create(TestUtils.UNIVERSITY_DELETED_NAME, TestUtils.UNIVERSITY_NEW_CODE, TestUtils.CITY_1_NAME);
//
//        TestUtils.assertEqualsUni(new University(UNI_DELETED.getId(), UNI_DELETED.getName(), TestUtils.UNIVERSITY_NEW_CODE, CITY_1), uni);
//    }
//    @Test
//    public void testCreateDeletedByAbbreviation(){
//        University uni = uniDao.create(TestUtils.UNIVERSITY_NEW_NAME, TestUtils.UNIVERSITY_DELETED_CODE, TestUtils.CITY_1_NAME);
//
//        TestUtils.assertEqualsUni(new University(UNI_DELETED.getId(), TestUtils.UNIVERSITY_NEW_NAME, UNI_DELETED.getAbbreviation(), CITY_1), uni);
//    }
//    @Test
//    public void testCreateDeletedByBoth(){
//        University uni = uniDao.create(TestUtils.UNIVERSITY_DELETED_NAME, TestUtils.UNIVERSITY_DELETED_CODE, TestUtils.CITY_1_NAME);
//
//        TestUtils.assertEqualsUni(UNI_DELETED, uni);
//    }

    @Test
    public void testDelete(){
        int rowsBefore = JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.UNIVERSITY_TABLE);

        uniDao.delete(UNI_1.getId());

        assertEquals(rowsBefore, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.UNIVERSITY_TABLE));
        assertEquals(
            TestUtils.TOTAL_UNIVERSITIES - 1, 
            Optional.ofNullable(
                jdbcTemplate.queryForObject(TestUtils.UNIVERSITY_COUNT_NOT_DELETED, Integer.class)
            ).get().intValue()
        );
    }
    @Test
    public void testDeleteWrongId(){
        int rowsBefore = JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.UNIVERSITY_TABLE);

        uniDao.delete(12341234);

        assertEquals(rowsBefore, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.UNIVERSITY_TABLE));
        assertEquals(
            TestUtils.TOTAL_UNIVERSITIES, 
            Optional.ofNullable(
                jdbcTemplate.queryForObject(TestUtils.UNIVERSITY_COUNT_NOT_DELETED, Integer.class)
            ).get().intValue()
        );    
    }
    @Test
    public void testDeleteDeleted(){
        int rowsBefore = JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.UNIVERSITY_TABLE);

        uniDao.delete(UNI_DELETED.getId());

        assertEquals(rowsBefore, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.UNIVERSITY_TABLE));
        assertEquals(
            TestUtils.TOTAL_UNIVERSITIES, 
            Optional.ofNullable(
                jdbcTemplate.queryForObject(TestUtils.UNIVERSITY_COUNT_NOT_DELETED, Integer.class)
            ).get().intValue()
        );    
    }
}


//@Test
//public void testFindByAbbreviation(){
//    Optional<University> maybeUni = uniDao.findByAbbreviation(TestUtils.UNIVERSITY_1_CODE);
//    assertNotNull(maybeUni);
//    assertTrue(maybeUni.isPresent());
//    University uni = maybeUni.get();
//    assertEquals(TestUtils.UNIVERSITY_1_NAME, uni.getName());
//    assertEquals(TestUtils.UNIVERSITY_1_CODE, uni.getAbbreviation());
//    assertEquals(UNI_1.getId(), uni.getId());
//    assertEquals(CITY_1.getId(), uni.getCity().getId());
//}
//@Test
//public void testFindByAbbreviation2(){
//    Optional<University> maybeUni = uniDao.findByAbbreviation(TestUtils.UNIVERSITY_2_CODE);
//    assertNotNull(maybeUni);
//    assertTrue(maybeUni.isPresent());
//    University uni = maybeUni.get();
//    assertEquals(TestUtils.UNIVERSITY_2_NAME, uni.getName());
//    assertEquals(TestUtils.UNIVERSITY_2_CODE, uni.getAbbreviation());
//    assertEquals(UNI_2.getId(), uni.getId());
//    assertEquals(CITY_2.getId(), uni.getCity().getId());
//}
//@Test
//public void testFindByAbbreviationDeleted(){
//    Optional<University> maybeUni = uniDao.findByAbbreviation(TestUtils.UNIVERSITY_DELETED_CODE);
//    assertNotNull(maybeUni);
//    assertFalse(maybeUni.isPresent());
//}
//@Test
//public void testFindByAbbreviationWrongAbbreviation(){
//    Optional<University> maybeUni = uniDao.findByAbbreviation("TestUtils.UNIVERSITY_1_CODE");
//    assertNotNull(maybeUni);
//    assertFalse(maybeUni.isPresent());
//}
//@Test
//public void testFindByAbbreviationEmptyAbbreviation(){
//    Optional<University> maybeUni = uniDao.findByAbbreviation("");
//    assertNotNull(maybeUni);
//    assertFalse(maybeUni.isPresent());
//}
//@Test
//public void testFindByAbbreviationMissingAbbreviation(){
//    Optional<University> maybeUni = uniDao.findByAbbreviation(null);
//    assertNotNull(maybeUni);
//    assertFalse(maybeUni.isPresent());
//}
//

//
//@Test
//public void testFindByAnyUsingAbbrSubstring(){
//    Optional<University> maybeUni = uniDao.findByAny(TestUtils.UNIVERSITY_1_CODE.substring(1, 3));
//    assertNotNull(maybeUni);
//    assertTrue(maybeUni.isPresent());
//    University uni = maybeUni.get();
//    assertEquals(TestUtils.UNIVERSITY_1_NAME, uni.getName());
//    assertEquals(TestUtils.UNIVERSITY_1_CODE, uni.getAbbreviation());
//    assertEquals(UNI_1.getId(), uni.getId());
//    assertEquals(CITY_1.getId(), uni.getCity().getId());
//}
//@Test
//public void testFindByAnyUsingNameSubstring(){
//    Optional<University> maybeUni = uniDao.findByAny(TestUtils.UNIVERSITY_2_NAME.substring(5, 15));
//    assertNotNull(maybeUni);
//    assertTrue(maybeUni.isPresent());
//    University uni = maybeUni.get();
//    assertEquals(TestUtils.UNIVERSITY_2_NAME, uni.getName());
//    assertEquals(TestUtils.UNIVERSITY_2_CODE, uni.getAbbreviation());
//    assertEquals(UNI_2.getId(), uni.getId());
//    assertEquals(CITY_2.getId(), uni.getCity().getId());
//}
//@Test
//public void testFindByAnyWrongQuery(){
//    Optional<University> maybeUni = uniDao.findByAny("TestUtils.UNIVERSITY_1_CODE");
//    assertNotNull(maybeUni);
//    assertFalse(maybeUni.isPresent());
//}
//@Test
//public void testFindByAnyDeleted(){
//    Optional<University> maybeUni = uniDao.findByAny(TestUtils.UNIVERSITY_DELETED_NAME);
//    assertNotNull(maybeUni);
//    assertFalse(maybeUni.isPresent());
//}
//@Test
//public void testFindByAnyEmptyQuery(){
//    Optional<University> maybeUni = uniDao.findByAny("");
//    assertNotNull(maybeUni);
//    assertTrue(maybeUni.isPresent());
//}
//@Test
//public void testFindByAnyMissingQuery(){
//    Optional<University> maybeUni = uniDao.findByAny(null);
//    assertNotNull(maybeUni);
//    assertTrue(maybeUni.isPresent());
//}
