package ar.edu.itba.paw.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.sql.DataSource;

import ar.edu.itba.paw.models.Page;
import ar.edu.itba.paw.models.PageParams;
import ar.edu.itba.paw.persistence.config.TestConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.itba.paw.models.University;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class UniversityHibernateDaoTest {

    @Autowired
    private DataSource ds;

    @Autowired
    private UniversityHibernateDao uniDao;
        
    @PersistenceContext
    private EntityManager em;

    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert insert;

    @Before
    public void setUp(){
        jdbcTemplate = new JdbcTemplate(ds);
        insert = new SimpleJdbcInsert(ds).withTableName(TestUtils.UNIVERSITY_TABLE).usingGeneratedKeyColumns("id");
    }

    @Test
    public void testFindByName(){
        Optional<University> maybeUni = uniDao.findByName(TestUtils.UNIVERSITY_1_NAME);

        assertNotNull(maybeUni);
        assertTrue(maybeUni.isPresent());
        TestUtils.assertEqualsUni(TestUtils.UNI_1, maybeUni.get());
    }
    @Test
    public void testFindByName2(){
        Optional<University> maybeUni = uniDao.findByName(TestUtils.UNIVERSITY_2_NAME);

        assertNotNull(maybeUni);
        assertTrue(maybeUni.isPresent());
        TestUtils.assertEqualsUni(TestUtils.UNI_2, maybeUni.get());
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
        Optional<University> maybeUni = uniDao.findById(TestUtils.UNIVERSITY_1_ID);

        assertNotNull(maybeUni);
        assertTrue(maybeUni.isPresent());
        TestUtils.assertEqualsUni(TestUtils.UNI_1, maybeUni.get());
    }
    @Test
    public void testFindByIdWrongId(){
        Optional<University> maybeUni = uniDao.findById(1234123);

        assertNotNull(maybeUni);
        assertFalse(maybeUni.isPresent());
    }
    @Test
    public void testFindByIdDeleted(){
        Optional<University> maybeUni = uniDao.findById(TestUtils.UNIVERSITY_DELETED_ID);

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
        TestUtils.assertEqualsUni(TestUtils.UNI_1, unis.getContent().get(0));
    }
    @Test
    public void testSearchUsingNameSubstring(){
        Page<University> unis = uniDao.search(TestUtils.UNIVERSITY_2_NAME.substring(5, 15), TestUtils.PAGE_1_DEFAULT);

        assertNotNull(unis);
        assertEquals(1, unis.getCurrentPage());
        assertEquals(1, unis.getTotalPages());
        assertNotNull(unis.getContent());
        assertEquals(1, unis.getContent().size());
        TestUtils.assertEqualsUni(TestUtils.UNI_2, unis.getContent().get(0));
    }
    @Test
    public void testSearchMultipleResults(){
        Page<University> unis = uniDao.search(TestUtils.UNIVERSITY_1_NAME.substring(TestUtils.UNIVERSITY_1_NAME.length() - 5, TestUtils.UNIVERSITY_1_NAME.length()), TestUtils.PAGE_1_BIG);

        assertNotNull(unis);
        assertEquals(1, unis.getCurrentPage());
        assertEquals(1, unis.getTotalPages());
        assertNotNull(unis.getContent());
        assertEquals(2, unis.getContent().size());
        TestUtils.assertEqualsUni(TestUtils.UNI_1, unis.getContent().get(0));
        TestUtils.assertEqualsUni(TestUtils.UNI_2, unis.getContent().get(1));
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
    public void testFindAllPage1(){
        long bonusId = insert.executeAndReturnKey(Map.of("name", TestUtils.UNIVERSITY_NEW_NAME, "abbreviation", TestUtils.UNIVERSITY_NEW_CODE, "CITY_ID", TestUtils.CITY_1_ID, "deleted", false)).longValue();
        University bonus = jdbcTemplate.queryForObject(TestUtils.UNIVERSITY_SELECT_BY_ID, TestUtils.UNIVERSITY_ROW_MAPPER, bonusId);

        Page<University> page1 = uniDao.findAll(new PageParams(1,2));

        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(2, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(2, page1.getContent().size());
        assertNotNull(bonus);
        Map<Long, University> uniData = Map.of(TestUtils.UNIVERSITY_1_ID, TestUtils.UNI_1, TestUtils.UNIVERSITY_2_ID, TestUtils.UNI_2, TestUtils.UNIVERSITY_3_ID, TestUtils.UNI_3, bonus.getId(), bonus);
        for (University uni : page1.getContent()){
            TestUtils.assertEqualsUni(uniData.get(uni.getId()), uni);
        }
    }
    @Test
    public void testFindAllPage2(){
        long bonusId = insert.executeAndReturnKey(Map.of("name", TestUtils.UNIVERSITY_NEW_NAME, "abbreviation", TestUtils.UNIVERSITY_NEW_CODE, "CITY_ID", TestUtils.CITY_1_ID, "deleted", false)).longValue();
        University bonus = jdbcTemplate.queryForObject(TestUtils.UNIVERSITY_SELECT_BY_ID, TestUtils.UNIVERSITY_ROW_MAPPER, bonusId);

        Page<University> page2 = uniDao.findAll(new PageParams(2,2));

        assertNotNull(page2);
        assertEquals(2, page2.getCurrentPage());
        assertEquals(2, page2.getTotalPages());
        assertNotNull(page2.getContent());
        assertEquals(2, page2.getContent().size());
        assertNotNull(bonus);
        Map<Long, University> uniData = Map.of(TestUtils.UNIVERSITY_1_ID, TestUtils.UNI_1, TestUtils.UNIVERSITY_2_ID, TestUtils.UNI_2, TestUtils.UNIVERSITY_3_ID, TestUtils.UNI_3, bonus.getId(), bonus);
        for (University uni : page2.getContent()){
            TestUtils.assertEqualsUni(uniData.get(uni.getId()), uni);
        }
    }
    @Test
    public void testFindAllUniversitiesPagedNoPages(){
        TestUtils.deleteUniversities(jdbcTemplate);

        Page<University> page = uniDao.findAll(TestUtils.PAGE_1_DEFAULT);

        assertNotNull(page);
        assertEquals(1, page.getCurrentPage());
        assertEquals(0, page.getTotalPages());
        assertNotNull(page.getContent());
        assertEquals(0, page.getContent().size());
    }

    @Test
    public void testUpdate(){
        uniDao.update(TestUtils.UNIVERSITY_1_ID, TestUtils.UNIVERSITY_NEW_NAME, TestUtils.UNIVERSITY_NEW_CODE, TestUtils.CITY_2_NAME);
        em.flush();

        University uni = jdbcTemplate.queryForObject(
            TestUtils.UNIVERSITY_SELECT_BY_ID,
            TestUtils.UNIVERSITY_ROW_MAPPER,
            TestUtils.UNIVERSITY_1_ID
        );
        TestUtils.assertEqualsUni(new University(TestUtils.UNIVERSITY_1_ID, TestUtils.UNIVERSITY_NEW_NAME, TestUtils.UNIVERSITY_NEW_CODE, TestUtils.CITY_2), uni);
    }
    @Test(expected = PersistenceException.class)
    public void testUpdateDuplicateName(){
        uniDao.update(TestUtils.UNIVERSITY_1_ID, TestUtils.UNIVERSITY_2_NAME, TestUtils.UNIVERSITY_NEW_CODE, TestUtils.CITY_1_NAME);
        em.flush();
    }
    @Test(expected = IllegalArgumentException.class)
    public void testUpdateMissingCity(){
        uniDao.update(TestUtils.UNIVERSITY_1_ID, TestUtils.UNIVERSITY_2_NAME, TestUtils.UNIVERSITY_NEW_CODE, "Fake name");
        em.flush();
    }
    @Test
    public void testUpdateUniversityNotFound(){
        uniDao.update(12341234, TestUtils.UNIVERSITY_2_NAME, TestUtils.UNIVERSITY_NEW_CODE, TestUtils.CITY_1_NAME);
        em.flush();

        TestUtils.assertUniversityDBDefaultState(jdbcTemplate);
    }

    @Test
    public void testCreate(){
        University uni = uniDao.create(TestUtils.UNIVERSITY_NEW_NAME, TestUtils.UNIVERSITY_NEW_CODE, TestUtils.CITY_1);
        em.flush();

        TestUtils.assertEqualsUni(new University(uni.getId(), TestUtils.UNIVERSITY_NEW_NAME, TestUtils.UNIVERSITY_NEW_CODE, TestUtils.CITY_1), uni);
    }
    @Test(expected = IllegalArgumentException.class)
    public void testCreateDuplicate(){
        uniDao.create(TestUtils.UNIVERSITY_1_NAME, TestUtils.UNIVERSITY_NEW_CODE, TestUtils.CITY_1);
        em.flush();
    }
    @Test
    public void testCreateDeletedByName(){
        University uni = uniDao.create(TestUtils.UNIVERSITY_DELETED_NAME, TestUtils.UNIVERSITY_NEW_CODE, TestUtils.CITY_1);
        em.flush();

        TestUtils.assertEqualsUni(new University(TestUtils.UNIVERSITY_DELETED_ID, TestUtils.UNIVERSITY_DELETED_NAME, TestUtils.UNIVERSITY_NEW_CODE, TestUtils.CITY_1), uni);
    }
    @Test
    public void testCreateDeletedCopyBoth(){
        University uni = uniDao.create(TestUtils.UNIVERSITY_DELETED_NAME, TestUtils.UNIVERSITY_DELETED_CODE, TestUtils.CITY_1);
        em.flush();

        TestUtils.assertEqualsUni(TestUtils.UNI_DELETED, uni);
    }

    @Test
    public void testDelete(){
        int rowsBefore = JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.UNIVERSITY_TABLE);

        uniDao.delete(TestUtils.UNIVERSITY_1_ID);
        em.flush();

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
        em.flush();

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

        uniDao.delete(TestUtils.UNIVERSITY_DELETED_ID);
        em.flush();

        assertEquals(rowsBefore, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.UNIVERSITY_TABLE));
        assertEquals(
            TestUtils.TOTAL_UNIVERSITIES,
            Optional.ofNullable(
                jdbcTemplate.queryForObject(TestUtils.UNIVERSITY_COUNT_NOT_DELETED, Integer.class)
            ).get().intValue()
        );
    }
}
