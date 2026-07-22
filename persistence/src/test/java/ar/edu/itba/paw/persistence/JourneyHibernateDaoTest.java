package ar.edu.itba.paw.persistence;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.sql.DataSource;

import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.enums.SortDirection;
import ar.edu.itba.paw.models.enums.SortFieldJourney;
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

import static org.junit.Assert.*;
import static ar.edu.itba.paw.persistence.TestUtils.*;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class JourneyHibernateDaoTest {

    @Autowired
    private DataSource ds;

    @Autowired
    private JourneyHibernateDao journeyDao;

    @PersistenceContext
    private EntityManager em;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp(){
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Test
    public void testCreate(){
        deleteJourneys(jdbcTemplate);

        Journey journey = journeyDao.create(
            USER_1,
            UNI_2,
            JOURNEY_START_DATE, JOURNEY_END_DATE, JOURNEY_DESCRIPTION
        );
        em.flush();

        assertEqualsJourney(
            journey,
            Map.of("id", jdbcTemplate.queryForObject(
                JOURNEY_GET_ID_BY_USER_ID, 
                Long.class, 
                USER_1_ID)
            )
        );
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, JOURNEY_TABLE));
    }

    @Test
    public void testHardDelete(){
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, JOURNEY_TABLE, "id = " + JOURNEY_2_ID));

        final Journey journey = em.find(Journey.class, JOURNEY_2_ID);
        journey.getUser().setJourney(null);
        journeyDao.hardDelete(journey);
        em.flush();

        assertEquals(0, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, JOURNEY_TABLE, "id = " + JOURNEY_2_ID));
    }

    @Test(expected = PersistenceException.class)
    public void testCreateInvalidUser(){
        journeyDao.create(
            new User(
                12341234l, 
                null, 
                null, 
                null, 
                null, 
                null, 
                null,
                    0L,
                null, 
                false, 
                false),
            UNI_2,
            JOURNEY_START_DATE, 
            JOURNEY_END_DATE, 
            JOURNEY_DESCRIPTION
        );
        em.flush();
    }
    @Test(expected = PersistenceException.class)
    public void testCreateMissingUser(){
        journeyDao.create(
            null,
            UNI_2,
            JOURNEY_START_DATE, 
            JOURNEY_END_DATE, 
            JOURNEY_DESCRIPTION
        );
        em.flush();
    }
    @Test(expected = PersistenceException.class)
    public void testCreateInvalidUni(){
        journeyDao.create(
            USER_1,
            new University(12341234l, null, null, null),
            JOURNEY_START_DATE, 
            JOURNEY_END_DATE, 
            JOURNEY_DESCRIPTION
        );
        em.flush();
    }

    @Test(expected = PersistenceException.class)
    public void testCreateMissingUni(){
        journeyDao.create(
            USER_1,
            null,
            JOURNEY_START_DATE, 
            JOURNEY_END_DATE, 
            JOURNEY_DESCRIPTION
        );
        em.flush();
    }
    @Test(expected = PersistenceException.class)
    public void testCreateMissingStartDate(){
        journeyDao.create(
            USER_1,
            UNI_2,
            null, 
            JOURNEY_END_DATE, 
            JOURNEY_DESCRIPTION
        );
        em.flush();
    }
    @Test(expected = PersistenceException.class)
    public void testCreateMissingEndDate(){
        journeyDao.create(
            USER_1,
            UNI_2,
            JOURNEY_START_DATE, 
            null,
            JOURNEY_DESCRIPTION
        );
        em.flush();
    }
    @Test(expected = PersistenceException.class)
    public void testCreateMissingDesc(){
        journeyDao.create(
            USER_1,
            UNI_2,
            JOURNEY_END_DATE, 
            JOURNEY_START_DATE, 
            null
        );
        em.flush();
    }

    @Test
    public void testFindById(){
        Optional<Journey> maybeJourney = journeyDao.findById(JOURNEY_1_ID);

        assertNotNull(maybeJourney);
        assertTrue(maybeJourney.isPresent());
        assertEqualsJourney(maybeJourney.get());
    }
    @Test
    public void testFindByIdInvalidId(){
        Optional<Journey> maybeJourney = journeyDao.findById(12341234l);

        assertNotNull(maybeJourney);
        assertFalse(maybeJourney.isPresent());
    }
    @Test
    public void testFindByIdDeleted(){
        Optional<Journey> maybeJourney = journeyDao.findById(JOURNEY_DELETED_ID);

        assertNotNull(maybeJourney);
        assertFalse(maybeJourney.isPresent());
    }
    @Test
    public void testFindByIdNoJourneys(){
        Optional<Journey> maybeJourney = journeyDao.findById(12341234l);

        assertNotNull(maybeJourney);
        assertFalse(maybeJourney.isPresent());
    }

    @Test
    public void testFindByUserId(){
        Optional<Journey> maybeJourney = journeyDao.findByUserId(USER_1_ID);

        assertNotNull(maybeJourney);
        assertTrue(maybeJourney.isPresent());
        assertEqualsJourney(maybeJourney.get());
    }
    @Test
    public void testFindByUserIdNoJourney(){
        Optional<Journey> maybeJourney = journeyDao.findByUserId(USER_3_ID);

        assertNotNull(maybeJourney);
        assertTrue(maybeJourney.isEmpty());
    }
    @Test
    public void testFindByUserIdNoUser(){
        Optional<Journey> maybeJourney = journeyDao.findByUserId(123412341);

        assertNotNull(maybeJourney);
        assertTrue(maybeJourney.isEmpty());
    }

    @Test
    public void testFindAllPage1(){
        Page<Journey> page1 = journeyDao.findAll(PAGE_1_SINGLE);

        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(2, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(1, page1.getContent().size());
        assertEqualsJourney(JOURNEY_1, page1.getContent().getFirst());
    }
    @Test
    public void testFindAllPage2(){
        Page<Journey> page2 = journeyDao.findAll(PAGE_2_SINGLE);

        assertNotNull(page2);
        assertEquals(2, page2.getCurrentPage());
        assertEquals(2, page2.getTotalPages());
        assertNotNull(page2.getContent());
        assertEquals(1, page2.getContent().size());
        assertEqualsJourney(JOURNEY_2, page2.getContent().getFirst());
    }
    @Test
    public void testFindAllPagedNoJourneys(){
        deleteJourneys(jdbcTemplate);

        Page<Journey> page1 = journeyDao.findAll(PAGE_1_DEFAULT);

        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(0, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(0, page1.getContent().size());
    }

    @Test
    public void testFindByOriginCityPaged(){
        Page<Journey> page1 = journeyDao.findByOriginCity(CITY_1_ID, PAGE_1_DEFAULT);

        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(1, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(2, page1.getContent().size());
    }
    @Test
    public void testFindByOriginCityPagedWrongCity(){
        Page<Journey> page1 = journeyDao.findByOriginCity(1241234l, PAGE_1_DEFAULT);

        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(0, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(0, page1.getContent().size());
    }

    @Test
    public void testRecommendedJourneysBasic(){
        Journey newJourney = insertJourney(ds, Map.of("user", USER_3, "startDate", JOURNEY_END_DATE.plusDays(2), "endDate", JOURNEY_END_DATE.plusDays(40)));

        Page<Journey> page1 = journeyDao.findRecommended(USER_1_ID, PAGE_1_BIG);

        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(1, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(2, page1.getContent().size());

        assertEqualsJourneyList(List.of(JOURNEY_2, newJourney), page1.getContent());
    }

    @Test
    public void testRecommendedJourneysNoJourneys(){
        deleteJourneys(jdbcTemplate);
        Page<Journey> page1 = journeyDao.findRecommended(USER_1_ID, PAGE_1_BIG);

        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(0, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(0, page1.getContent().size());
    }

    @Test
    public void testRecommendedJourneysGoingToMyCity(){
        deleteJourneys(jdbcTemplate);
        insertJourney(ds, Map.of("user", USER_1));
        Journey newJourney1 = insertJourney(ds, Map.of("user", USER_3, "destination", UNI_1, "startDate", JOURNEY_END_DATE.plusDays(-5), "endDate", JOURNEY_END_DATE.plusDays(20)));
        Journey newJourney2 = insertJourney(ds, Map.of("user", USER_4, "destination", UNI_1, "startDate", JOURNEY_END_DATE.plusDays(-10), "endDate", JOURNEY_END_DATE.plusDays(-2)));

        Page<Journey> page1 = journeyDao.findRecommended(USER_1_ID, PAGE_1_BIG);

        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(1, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(2, page1.getContent().size());
        assertEqualsJourneyList(List.of(newJourney1, newJourney2), page1.getContent());
    }

    @Test
    public void testRecommendedJourneysWithInterests(){
        Journey newJourney1 = insertJourney(ds, Map.of("user", USER_I3));
        Journey newJourney2 = insertJourney(ds, Map.of("user", USER_I2));
        Journey newJourney3 = insertJourney(ds, Map.of("user", USER_I1));

        Page<Journey> page1 = journeyDao.findRecommended(USER_1_ID, PAGE_1_BIG);

        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(1, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(4, page1.getContent().size());
        assertEqualsJourneyList(List.of(JOURNEY_2, newJourney1, newJourney2, newJourney3), page1.getContent());
    }

    @Test
    public void testRecommendedJourneysWithInterestsComplex(){
        deleteJourneys(jdbcTemplate);
        insertJourney(ds, Map.of("user", USER_1));
        Journey newJourney1 = insertJourney(ds, Map.of("user", USER_I1));
        Journey newJourney2 = insertJourney(ds, Map.of("user", USER_I2, "destination", UNI_1, "startDate", JOURNEY_END_DATE.plusDays(2), "endDate", JOURNEY_END_DATE.plusDays(30)));
        Journey newJourney3 = insertJourney(ds, Map.of("user", USER_I3, "destination", UNI_1, "startDate", JOURNEY_END_DATE.plusDays(-7), "endDate", JOURNEY_END_DATE.plusDays(-3)));
        Journey newJourney4 = insertJourney(ds, Map.of("user", USER_3, "startDate", JOURNEY_END_DATE.plusDays(2), "endDate", JOURNEY_END_DATE.plusDays(20)));
        Journey newJourney5 = insertJourney(ds, Map.of("user", USER_4, "destination", UNI_3));

        Page<Journey> page1 = journeyDao.findRecommended(USER_1_ID, PAGE_1_BIG);

        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(1, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(5, page1.getContent().size());
        assertEqualsJourneyList(List.of(newJourney1, newJourney2, newJourney3, newJourney4, newJourney5), page1.getContent());
    }

    @Test
    public void testRecommendedJourneysTotalElementsIgnoresZeroScoreCandidates(){
        deleteJourneys(jdbcTemplate);
        insertJourney(ds, Map.of("user", USER_1));

        jdbcTemplate.update(
            "INSERT INTO universities(id, name, abbreviation, city_id, deleted) VALUES (?, ?, ?, ?, FALSE)",
            10L,
            "No matching university",
            "NMU",
            CITY_3_ID
        );

        Journey recommendedJourney1 = insertJourney(ds, Map.of("user", USER_2));
        Journey recommendedJourney2 = insertJourney(ds, Map.of("user", USER_3));
        Journey recommendedJourney3 = insertJourney(ds, Map.of("user", USER_I1));
        insertJourney(ds, Map.of(
            "user", USER_4,
            "destination", new University(10L, "No matching university", "NMU", CITY_3),
            "startDate", JOURNEY_END_DATE.plusDays(2),
            "endDate", JOURNEY_END_DATE.plusDays(30)
        ));

        Page<Journey> page1 = journeyDao.findRecommended(USER_1_ID, new PageParams(1, 3));
        Page<Journey> page2 = journeyDao.findRecommended(USER_1_ID, new PageParams(2, 3));

        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(1, page1.getTotalPages());
        assertEquals(3, page1.getTotalElements());
        assertEqualsJourneyList(List.of(recommendedJourney1, recommendedJourney2, recommendedJourney3), page1.getContent());

        assertNotNull(page2);
        assertEquals(2, page2.getCurrentPage());
        assertEquals(1, page2.getTotalPages());
        assertEquals(3, page2.getTotalElements());
        assertTrue(page2.getContent().isEmpty());
    }

    @Test
    public void testFindAllWithFilters(){
        Page<Journey> page = journeyDao.search(
            null,
            USER_1_ID,
            CITY_2_ID,
            SortFieldJourney.START_DATE,
            SortDirection.ASC,
            CITY_1_NAME,
            UNIVERSITY_1_NAME,
            JOURNEY_START_DATE,
            JOURNEY_END_DATE,
            INTEREST_1_NAME,
            false,
            false,
            PAGE_1_BIG
        );

        assertNotNull(page);
        assertNotNull(page.getContent());
        assertEquals(1, page.getCurrentPage());
        assertEquals(0, page.getTotalPages());
        assertEquals(0, page.getContent().size());
    }
    @Test
    public void testFindAllWithFiltersNoCityMyDestination(){
        Page<Journey> page = journeyDao.search(
            null,
            USER_1_ID,
            CITY_2_ID,
            SortFieldJourney.START_DATE,
            SortDirection.ASC,
            null,
            null,
            JOURNEY_START_DATE,
            JOURNEY_END_DATE,
            INTEREST_1_NAME,
            false,
            false,
            PAGE_1_BIG
        );

        assertNotNull(page);
        assertNotNull(page.getContent());
        assertEquals(1, page.getCurrentPage());
        assertEquals(0, page.getTotalPages());
        assertEquals(0, page.getContent().size());
    }
    @Test
    public void testFindAllWithFiltersInterestNoUser(){
        Page<Journey> page = journeyDao.search(
            USER_1_NAME,
            null,
            null,
            SortFieldJourney.END_DATE,
            SortDirection.ASC,
            null,
            null,
            JOURNEY_START_DATE,
            JOURNEY_END_DATE,
            INTEREST_1_NAME,
            false,
            false,
            PAGE_1_BIG
        );

        assertNotNull(page);
        assertNotNull(page.getContent());
        assertEquals(1, page.getCurrentPage());
        assertEquals(1, page.getTotalPages());
        assertEquals(1, page.getContent().size());
        assertEqualsJourney(JOURNEY_1, page.getContent().getFirst());
    }
    @Test
    public void testFindAllWithFiltersComplex(){
        Page<Journey> page = journeyDao.search(
            USER_1_NAME,
            USER_1_ID,
            null,
            SortFieldJourney.START_DATE,
            SortDirection.ASC,
            CITY_1_NAME,
            null,
            JOURNEY_START_DATE,
            JOURNEY_END_DATE,
            null,
            false,
            false,
            PAGE_1_BIG
        );

        assertNotNull(page);
        assertNotNull(page.getContent());
        assertEquals(1, page.getCurrentPage());
        assertEquals(0, page.getTotalPages());
        assertEquals(0, page.getContent().size());
    }
    @Test
    public void testFindAllWithFiltersComplexNoDestinationNoUser(){
        Page<Journey> page = journeyDao.search(
            USER_1_NAME,
            null,
            null,
            SortFieldJourney.START_DATE,
            SortDirection.ASC,
            "",
            "",
            JOURNEY_START_DATE,
            JOURNEY_END_DATE,
            null,
            false,
            false,
            PAGE_1_BIG
        );

        assertNotNull(page);
        assertNotNull(page.getContent());
        assertEquals(1, page.getCurrentPage());
        assertEquals(1, page.getTotalPages());
        assertEquals(1, page.getContent().size());
        assertEqualsJourney(JOURNEY_1, page.getContent().getFirst());
    }
    @Test
    public void testFindAllWithFiltersComplexNoUser(){
        Page<Journey> page = journeyDao.search(
            USER_2_NAME,
            null,
            null,
            SortFieldJourney.START_DATE,
            SortDirection.ASC,
            "",
            "",
            JOURNEY_START_DATE,
            JOURNEY_END_DATE,
            null,
            false,
            false,
            PAGE_1_BIG
        );

        assertNotNull(page);
        assertNotNull(page.getContent());
        assertEquals(1, page.getCurrentPage());
        assertEquals(1, page.getTotalPages());
        assertEquals(1, page.getContent().size());
        assertEqualsJourney(JOURNEY_2, page.getContent().get(0));
    }
    @Test
    public void testFindAllWithFiltersComplexNoUsergNoInterest(){
        Page<Journey> page = journeyDao.search(
            USER_1_NAME,
            null,
            null,
            SortFieldJourney.START_DATE,
            SortDirection.ASC,
            "",
            "",
            JOURNEY_START_DATE,
            JOURNEY_END_DATE,
            "",
            false,
            false,
            PAGE_1_BIG
        );

        assertNotNull(page);
        assertNotNull(page.getContent());
        assertEquals(1, page.getCurrentPage());
        assertEquals(1, page.getTotalPages());
        assertEquals(1, page.getContent().size());
        assertEqualsJourney(JOURNEY_1, page.getContent().get(0));
    }
    @Test
    public void testFindAllWithFiltersReverseSort(){
        Page<Journey> page = journeyDao.search(
            null,
            USER_1_ID,
            null,
            SortFieldJourney.START_DATE,
            SortDirection.DESC,
            null,
            UNIVERSITY_1_NAME,
            JOURNEY_START_DATE,
            JOURNEY_END_DATE,
            null,
            false,
            false,
            PAGE_1_BIG
        );

        assertNotNull(page);
        assertNotNull(page.getContent());
        assertEquals(1, page.getCurrentPage());
        assertEquals(0, page.getTotalPages());
        assertEquals(0, page.getContent().size());
    }
    @Test
    public void testFindAllWithFiltersNoParams(){
        Page<Journey> page = journeyDao.search(
            null,
            null,
            null,
            null,
            SortDirection.DESC,
            null,
            null,
            null,
            null,
            null,
            false,
            false,
            PAGE_1_BIG
        );

        assertNotNull(page);
        assertNotNull(page.getContent());
        assertEquals(1, page.getCurrentPage());
        assertEquals(1, page.getTotalPages());
        assertEquals(2, page.getContent().size());
        assertEqualsJourneyList(List.of(JOURNEY_1, JOURNEY_2), page.getContent());
    }
    @Test
    public void testFindAllWithFiltersNoParamsEmpty(){
        Page<Journey> page = journeyDao.search(
            "",
            null,
            null,
            SortFieldJourney.START_DATE,
            SortDirection.DESC,
            "",
            "",
            null,
            null,
            "",
            false,
            false,
            PAGE_1_BIG
        );

        assertNotNull(page);
        assertNotNull(page.getContent());
        assertEquals(1, page.getCurrentPage());
        assertEquals(1, page.getTotalPages());
        assertEquals(2, page.getContent().size());
        assertEqualsJourney(JOURNEY_1, page.getContent().get(0));
        assertEqualsJourney(JOURNEY_2, page.getContent().get(1));
    }
    @Test
    public void testFindAllWithFiltersNoParamsFilter(){
        Page<Journey> page = journeyDao.search(
            USER_2_NAME,
            null,
            null,
            SortFieldJourney.START_DATE,
            SortDirection.DESC,
            null,
            null,
            null,
            null,
            null,
            false,
            false,
            PAGE_1_BIG
        );

        assertNotNull(page);
        assertNotNull(page.getContent());
        assertEquals(1, page.getCurrentPage());
        assertEquals(1, page.getTotalPages());
        assertEquals(1, page.getContent().size());
        assertEqualsJourney(JOURNEY_2, page.getContent().get(0));
    }
    @Test
    public void testFindAllWithFiltersUpcoming(){
        Page<Journey> page = journeyDao.search(
            USER_2_NAME,
            null,
            null,
            SortFieldJourney.START_DATE,
            SortDirection.DESC,
            null,
            null,
            null,
            null,
            null,
            true,
            false,
            PAGE_1_BIG
        );

        assertNotNull(page);
        assertNotNull(page.getContent());
        assertEquals(1, page.getCurrentPage());
        assertEquals(1, page.getTotalPages());
        assertEquals(1, page.getContent().size());
        assertEqualsJourney(JOURNEY_2, page.getContent().get(0));
    }
    @Test
    public void testFindAllWithFiltersPast(){
        Page<Journey> page = journeyDao.search(
            USER_2_NAME,
            null,
            null,
            SortFieldJourney.START_DATE,
            SortDirection.DESC,
            null,
            null,
            null,
            null,
            null,
            false,
            true,
            PAGE_1_BIG
        );

        assertNotNull(page);
        assertNotNull(page.getContent());
        assertEquals(1, page.getCurrentPage());
        assertEquals(0, page.getTotalPages());
        assertEquals(0, page.getContent().size());
    }
    @Test
    public void testFindAllWithFiltersUniversityAndDestinationCityNoCity(){
        Page<Journey> page = journeyDao.search(
            null,
            USER_1_ID,
            CITY_2_ID,
            SortFieldJourney.START_DATE,
            SortDirection.ASC,
            null,
            UNIVERSITY_2_NAME,
            null,
            null,
            null,
            false,
            false,
            PAGE_1_BIG
        );

        assertNotNull(page);
        assertNotNull(page.getContent());
        assertEquals(1, page.getCurrentPage());
        assertEquals(1, page.getTotalPages());
        assertEquals(1, page.getContent().size());
        assertEqualsJourney(JOURNEY_2, page.getContent().get(0));
    }
    @Test
    public void testFindAllWithFiltersUniversityAndDestinationCityMultiple(){
        Page<Journey> page = journeyDao.search(
            null,
            null,
            CITY_2_ID,
            SortFieldJourney.START_DATE,
            SortDirection.ASC,
            null,
            UNIVERSITY_2_NAME,
            null,
            null,
            null,
            false,
            false,
            PAGE_1_BIG
        );

        assertNotNull(page);
        assertNotNull(page.getContent());
        assertEquals(1, page.getCurrentPage());
        assertEquals(1, page.getTotalPages());
        assertEquals(2, page.getContent().size());
    }
}
