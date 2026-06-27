package ar.edu.itba.paw.persistence;

import static org.junit.Assert.*;
import static ar.edu.itba.paw.persistence.TestUtils.*;

import java.util.Map;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;

import ar.edu.itba.paw.models.Page;
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
import ar.edu.itba.paw.models.exceptions.UniversityAlreadyExistsException;

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
        insert = new SimpleJdbcInsert(ds)
            .withTableName(UNIVERSITY_TABLE)
            .usingGeneratedKeyColumns("id");
    }

    @Test
    public void testCreate(){
        University uni = uniDao.create(UNIVERSITY_NEW_NAME, UNIVERSITY_NEW_CODE, CITY_1);
        em.flush();

        assertEqualsUni(new University(uni.getId(), UNIVERSITY_NEW_NAME, UNIVERSITY_NEW_CODE, CITY_1), uni);

        University persisted = jdbcTemplate.queryForObject(
            UNIVERSITY_SELECT_BY_ID, UNIVERSITY_ROW_MAPPER, uni.getId()
        );
        assertEqualsUni(new University(uni.getId(), UNIVERSITY_NEW_NAME, UNIVERSITY_NEW_CODE, CITY_1), persisted);
        assertEquals(
            TOTAL_UNIVERSITIES + 1,
            jdbcTemplate.queryForObject(UNIVERSITY_COUNT_NOT_DELETED, Integer.class).intValue()
        );
        assertEquals(
            1,
            JdbcTestUtils.countRowsInTableWhere(
                jdbcTemplate, UNIVERSITY_TABLE,
                "id = " + uni.getId() + " AND name = '" + UNIVERSITY_NEW_NAME + "' AND abbreviation = '" + UNIVERSITY_NEW_CODE + "' AND deleted = FALSE"
            )
        );
    }
    @Test(expected = UniversityAlreadyExistsException.class)
    public void testCreateDuplicate(){
        uniDao.create(UNIVERSITY_1_NAME, UNIVERSITY_NEW_CODE, CITY_1);
        em.flush();
    }
    @Test
    public void testCreateDeletedByName(){
        University uni = uniDao.create(UNIVERSITY_DELETED_NAME, UNIVERSITY_NEW_CODE, CITY_1);
        em.flush();

        assertEqualsUni(new University(UNIVERSITY_DELETED_ID, UNIVERSITY_DELETED_NAME, UNIVERSITY_NEW_CODE, CITY_1), uni);

        University persisted = jdbcTemplate.queryForObject(
            UNIVERSITY_SELECT_BY_ID, UNIVERSITY_ROW_MAPPER, UNIVERSITY_DELETED_ID
        );
        assertEqualsUni(new University(UNIVERSITY_DELETED_ID, UNIVERSITY_DELETED_NAME, UNIVERSITY_NEW_CODE, CITY_1), persisted);
        assertEquals(
            TOTAL_UNIVERSITIES + 1,
            jdbcTemplate.queryForObject(UNIVERSITY_COUNT_NOT_DELETED, Integer.class).intValue()
        );
        assertEquals(
            1,
            JdbcTestUtils.countRowsInTableWhere(
                jdbcTemplate, UNIVERSITY_TABLE,
                "id = " + UNIVERSITY_DELETED_ID + " AND name = '" + UNIVERSITY_DELETED_NAME + "' AND abbreviation = '" + UNIVERSITY_NEW_CODE + "' AND deleted = FALSE"
            )
        );
    }
    @Test
    public void testCreateDeletedCopyBoth(){
        University uni = uniDao.create(UNIVERSITY_DELETED_NAME, UNIVERSITY_DELETED_CODE, CITY_1);
        em.flush();

        assertEqualsUni(UNI_DELETED, uni);

        University persisted = jdbcTemplate.queryForObject(
            UNIVERSITY_SELECT_BY_ID, UNIVERSITY_ROW_MAPPER, UNIVERSITY_DELETED_ID
        );
        assertEqualsUni(UNI_DELETED, persisted);
        assertEquals(
            TOTAL_UNIVERSITIES + 1,
            jdbcTemplate.queryForObject(UNIVERSITY_COUNT_NOT_DELETED, Integer.class).intValue()
        );
        assertEquals(
            1,
            JdbcTestUtils.countRowsInTableWhere(
                jdbcTemplate, UNIVERSITY_TABLE,
                "id = " + UNIVERSITY_DELETED_ID + " AND name = '" + UNIVERSITY_DELETED_NAME + "' AND abbreviation = '" + UNIVERSITY_DELETED_CODE + "' AND deleted = FALSE"
            )
        );
    }

    @Test
    public void testFindByName(){
        Optional<University> maybeUni = uniDao.findByName(UNIVERSITY_1_NAME);

        assertNotNull(maybeUni);
        assertTrue(maybeUni.isPresent());
        assertEqualsUni(UNI_1, maybeUni.get());
    }
    @Test
    public void testFindByName2(){
        Optional<University> maybeUni = uniDao.findByName(UNIVERSITY_2_NAME);

        assertNotNull(maybeUni);
        assertTrue(maybeUni.isPresent());
        assertEqualsUni(UNI_2, maybeUni.get());
    }
    @Test
    public void testFindByNameDeleted(){
        Optional<University> maybeUni = uniDao.findByName(UNIVERSITY_DELETED_NAME);

        assertNotNull(maybeUni);
        assertFalse(maybeUni.isPresent());
    }
    @Test
    public void testFindByNameWrongName(){
        Optional<University> maybeUni = uniDao.findByName("UNIVERSITY_2_NAME");

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
        Optional<University> maybeUni = uniDao.findById(UNIVERSITY_1_ID);

        assertNotNull(maybeUni);
        assertTrue(maybeUni.isPresent());
        assertEqualsUni(UNI_1, maybeUni.get());
    }
    @Test
    public void testFindByIdWrongId(){
        Optional<University> maybeUni = uniDao.findById(1234123);

        assertNotNull(maybeUni);
        assertFalse(maybeUni.isPresent());
    }
    @Test
    public void testFindByIdDeleted(){
        Optional<University> maybeUni = uniDao.findById(UNIVERSITY_DELETED_ID);

        assertNotNull(maybeUni);
        assertFalse(maybeUni.isPresent());
    }

    @Test
    public void testSearchUsingAbbreviationSubstring(){
        Page<University> unis = uniDao.search(
            UNIVERSITY_1_CODE.substring(1, 3), PAGE_1_DEFAULT
        );

        assertNotNull(unis);
        assertEquals(1, unis.getCurrentPage());
        assertEquals(1, unis.getTotalPages());
        assertNotNull(unis.getContent());
        assertEquals(1, unis.getContent().size());
        assertEqualsUni(UNI_1, unis.getContent().get(0));
    }
    @Test
    public void testSearchUsingNameSubstring(){
        Page<University> unis = uniDao.search(
            UNIVERSITY_2_NAME.substring(5, 15), PAGE_1_DEFAULT
        );

        assertNotNull(unis);
        assertEquals(1, unis.getCurrentPage());
        assertEquals(1, unis.getTotalPages());
        assertNotNull(unis.getContent());
        assertEquals(1, unis.getContent().size());
        assertEqualsUni(UNI_2, unis.getContent().get(0));
    }
    @Test
    public void testSearchMultipleResults(){
        Page<University> unis = uniDao.search(
            UNIVERSITY_1_NAME.substring(
                UNIVERSITY_1_NAME.length() - 5, 
                UNIVERSITY_1_NAME.length()
            ), PAGE_1_BIG
        );

        assertNotNull(unis);
        assertEquals(1, unis.getCurrentPage());
        assertEquals(1, unis.getTotalPages());
        assertNotNull(unis.getContent());
        assertEquals(2, unis.getContent().size());
        assertEqualsUni(UNI_1, unis.getContent().get(0));
        assertEqualsUni(UNI_2, unis.getContent().get(1));
    }
    @Test
    public void testSearchWrongQuery(){
        Page<University> unis = uniDao.search(
            "UNIVERSITY_1_CODE", PAGE_1_BIG
        );

        assertNotNull(unis);
        assertEquals(1, unis.getCurrentPage());
        assertEquals(0, unis.getTotalPages());
        assertNotNull(unis.getContent());
        assertEquals(0, unis.getContent().size());
    }
    @Test
    public void testSearchDeleted(){
        Page<University> unis = uniDao.search(UNIVERSITY_DELETED_NAME, PAGE_1_BIG);

        assertNotNull(unis);
        assertEquals(1, unis.getCurrentPage());
        assertEquals(0, unis.getTotalPages());
        assertNotNull(unis.getContent());
        assertEquals(0, unis.getContent().size());
    }
    @Test
    public void testSearchEmptyQuery(){
        Page<University> unis = uniDao.search("", PAGE_1_BIG);

        assertNotNull(unis);
        assertEquals(1, unis.getCurrentPage());
        assertEquals(1, unis.getTotalPages());
        assertNotNull(unis.getContent());
        assertEquals(TOTAL_UNIVERSITIES, unis.getContent().size());
    }
    @Test
    public void testSearchMissingQuery(){
        Page<University> unis = uniDao.search(null, PAGE_1_BIG);

        assertNotNull(unis);
        assertEquals(1, unis.getCurrentPage());
        assertEquals(1, unis.getTotalPages());
        assertNotNull(unis.getContent());
        assertEquals(TOTAL_UNIVERSITIES, unis.getContent().size());
    }

    @Test
    public void testFindAllPage1(){
        long bonusId = insert.executeAndReturnKey(
            Map.of(
                "name", UNIVERSITY_NEW_NAME, 
                "abbreviation", UNIVERSITY_NEW_CODE, 
                "CITY_ID", CITY_1_ID, 
                "deleted", false
            )
        ).longValue();
        University bonus = jdbcTemplate.queryForObject(
            UNIVERSITY_SELECT_BY_ID, UNIVERSITY_ROW_MAPPER, bonusId
        );

        Page<University> page1 = uniDao.findAll(PAGE_1_DEFAULT);

        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(2, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(2, page1.getContent().size());
        assertNotNull(bonus);
        Map<Long, University> uniData = Map.of(
            UNIVERSITY_1_ID, UNI_1, 
            UNIVERSITY_2_ID, UNI_2, 
            UNIVERSITY_3_ID, UNI_3, 
            bonus.getId(), bonus
        );
        page1.getContent().forEach((uni) ->
            assertEqualsUni(uniData.get(uni.getId()), uni)
        );
    }
    @Test
    public void testFindAllPage2(){
        long bonusId = insert.executeAndReturnKey(
            Map.of(
                "name", UNIVERSITY_NEW_NAME, 
                "abbreviation", UNIVERSITY_NEW_CODE, 
                "CITY_ID", CITY_1_ID, 
                "deleted", false
            )
        ).longValue();
        University bonus = jdbcTemplate.queryForObject(
            UNIVERSITY_SELECT_BY_ID, 
            UNIVERSITY_ROW_MAPPER, 
            bonusId
        );

        Page<University> page2 = uniDao.findAll(PAGE_2_DEFAULT);

        assertNotNull(page2);
        assertEquals(2, page2.getCurrentPage());
        assertEquals(2, page2.getTotalPages());
        assertNotNull(page2.getContent());
        assertEquals(2, page2.getContent().size());
        assertNotNull(bonus);
        Map<Long, University> uniData = Map.of(
            UNIVERSITY_1_ID, UNI_1, 
            UNIVERSITY_2_ID, UNI_2, 
            UNIVERSITY_3_ID, UNI_3, 
            bonus.getId(), bonus
        );
        page2.getContent().forEach((uni) ->
            assertEqualsUni(uniData.get(uni.getId()), uni)
        );
    }
    @Test
    public void testFindAllUniversitiesPagedNoPages(){
        deleteUniversities(jdbcTemplate);

        Page<University> page = uniDao.findAll(PAGE_1_DEFAULT);

        assertNotNull(page);
        assertEquals(1, page.getCurrentPage());
        assertEquals(0, page.getTotalPages());
        assertNotNull(page.getContent());
        assertEquals(0, page.getContent().size());
    }
}
