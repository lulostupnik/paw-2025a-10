package ar.edu.itba.paw.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;
import java.util.Optional;

import javax.sql.DataSource;

import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.enums.SortDirection;
import ar.edu.itba.paw.models.enums.SortFieldEvent;
import ar.edu.itba.paw.models.enums.SortFieldJourney;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Rollback
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class JourneyJdbcDaoTest {

    @Autowired
    private DataSource ds;

    @Autowired
    private JourneyJdbcDao journeyDao;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp(){
        jdbcTemplate = new JdbcTemplate(ds);
    }

    private void assertEqualsMaybeJourney(Optional<Journey> maybeJourney){
        assertNotNull(maybeJourney);
        assertTrue(maybeJourney.isPresent());
        final Journey journey = maybeJourney.get();
        TestUtils.assertEqualsJourney(TestUtils.JOURNEY_1, journey); 
    }

    @Test
    public void testCreate(){
        TestUtils.deleteJourneys(jdbcTemplate);
        Journey journey = journeyDao.create(
            TestUtils.USER_1,
            TestUtils.UNI_2,
            TestUtils.JOURNEY_START_DATE, TestUtils.JOURNEY_END_DATE, TestUtils.JOURNEY_DESCRIPTION
        );

        TestUtils.assertEqualsJourney(journey, Map.of("id", jdbcTemplate.queryForObject(TestUtils.JOURNEY_GET_ID_BY_USER_ID, Long.class, TestUtils.USER_1_ID)));
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.JOURNEY_TABLE));
    }
    @Test(expected = DataAccessException.class)
    public void testCreateInvalidUser(){
        journeyDao.create(
            new User(12341234, null, null, null, null, null, null, 0, null, false),
            TestUtils.UNI_2,
            TestUtils.JOURNEY_START_DATE, TestUtils.JOURNEY_END_DATE, TestUtils.JOURNEY_DESCRIPTION
        );
    }
    @Test(expected = DataAccessException.class)
    public void testCreateInvalidUni(){
        journeyDao.create(
            TestUtils.USER_1,
            new University(12431234, null,null, null),
            TestUtils.JOURNEY_START_DATE, TestUtils.JOURNEY_END_DATE, TestUtils.JOURNEY_DESCRIPTION
        );
    }
    @Test
    public void testCreateDuplicated(){
        journeyDao.create(
            TestUtils.USER_1,
            TestUtils.UNI_2,
            TestUtils.JOURNEY_START_DATE, TestUtils.JOURNEY_END_DATE, TestUtils.JOURNEY_DESCRIPTION
        );

        assertEquals(TestUtils.TOTAL_JOURNEYS, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.JOURNEY_TABLE));
        Journey journey = jdbcTemplate.queryForObject(TestUtils.JOURNEY_SELECT_BY_ID, TestUtils.JOURNEY_ROW_MAPPER, TestUtils.JOURNEY_1_ID);
        TestUtils.assertEqualsJourney(journey);
    }

    @Test
    public void testFindById(){
        Optional<Journey> maybeJourney = journeyDao.findById(TestUtils.JOURNEY_1_ID);

        assertEqualsMaybeJourney(maybeJourney);
    }
    @Test
    public void testFindByIdInvalidId(){
        Optional<Journey> maybeJourney = journeyDao.findById(12341234);

        assertNotNull(maybeJourney);
        assertFalse(maybeJourney.isPresent());
    }    
    @Test
    public void testFindByIdDeleted(){
        Optional<Journey> maybeJourney = journeyDao.findById(TestUtils.JOURNEY_DELETED_ID);

        assertNotNull(maybeJourney);
        assertFalse(maybeJourney.isPresent());
    }
    @Test
    public void testFindByIdNoJourneys(){
        Optional<Journey> maybeJourney = journeyDao.findById(12341234);

        assertNotNull(maybeJourney);
        assertFalse(maybeJourney.isPresent());
    }


    @Test
    public void testFindByUserId(){
        Optional<Journey> maybeJourney = journeyDao.findByUserId(TestUtils.USER_1_ID);

        assertNotNull(maybeJourney);
        assertTrue(maybeJourney.isPresent());
        TestUtils.assertEqualsJourney(maybeJourney.get());      
    }
    @Test
    public void testFindByUserIdWrongId(){
        Optional<Journey> maybeJourney = journeyDao.findByUserId(12341234);

        assertNotNull(maybeJourney);
        assertFalse(maybeJourney.isPresent());
    }
    @Test
    public void testFindByUserIdDeleted(){
        Optional<Journey> maybeJourney = journeyDao.findByUserId(TestUtils.USER_4_ID);

        assertNotNull(maybeJourney);
        assertFalse(maybeJourney.isPresent());
    }

    @Test
    public void testDelete(){
        journeyDao.delete(TestUtils.JOURNEY_1_ID);

        assertEquals(TestUtils.TOTAL_JOURNEYS, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.JOURNEY_TABLE));
        assertTrue(jdbcTemplate.queryForObject(TestUtils.JOURNEY_IS_DELETED_BY_ID, Boolean.class, TestUtils.JOURNEY_1_ID));
    }
    @Test
    public void testDeleteWrongId(){
        journeyDao.delete(12341243);

        assertEquals(TestUtils.TOTAL_JOURNEYS, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.JOURNEY_TABLE));
        assertFalse(jdbcTemplate.queryForObject(TestUtils.JOURNEY_IS_DELETED_BY_ID, Boolean.class, TestUtils.JOURNEY_1_ID));
    }
    @Test
    public void testDeleteDeleted(){
        journeyDao.delete(TestUtils.JOURNEY_DELETED_ID);

        assertEquals(TestUtils.TOTAL_JOURNEYS, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.JOURNEY_TABLE));
        assertTrue(jdbcTemplate.queryForObject(TestUtils.JOURNEY_IS_DELETED_BY_ID, Boolean.class, TestUtils.JOURNEY_DELETED_ID));
        assertFalse(jdbcTemplate.queryForObject(TestUtils.JOURNEY_IS_DELETED_BY_ID, Boolean.class, TestUtils.JOURNEY_1_ID));
    }

    @Test
    public void testUpdateDeletionMessage(){
        journeyDao.updateDeletionMessage(TestUtils.JOURNEY_1_ID, TestUtils.MESSAGE_DEFAULT);

        assertEquals(TestUtils.TOTAL_JOURNEYS, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.JOURNEY_TABLE));
        assertFalse(jdbcTemplate.queryForObject(TestUtils.JOURNEY_IS_DELETED_BY_ID, Boolean.class, TestUtils.JOURNEY_1_ID));
        assertEquals(TestUtils.MESSAGE_DEFAULT, jdbcTemplate.queryForObject(TestUtils.JOURNEY_GET_DELETED_MESSAGE_BY_ID, String.class, TestUtils.JOURNEY_1_ID));
    }
    @Test
    public void testUpdateDeletionMessageWrongId(){
        journeyDao.updateDeletionMessage(12341243, TestUtils.MESSAGE_DEFAULT);

        assertEquals(TestUtils.TOTAL_JOURNEYS, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.JOURNEY_TABLE));
        assertFalse(jdbcTemplate.queryForObject(TestUtils.JOURNEY_IS_DELETED_BY_ID, Boolean.class, TestUtils.JOURNEY_1_ID));
    }

    @Test
    public void testFindAllPaged(){
        Page<Journey> page1 = journeyDao.findAll(new PageParams(1, 1));
        Page<Journey> page2 = journeyDao.findAll(new PageParams(2, 1));

        assertNotNull(page1);
        assertNotNull(page2);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(2, page2.getCurrentPage());
        assertEquals(2, page1.getTotalPages());
        assertEquals(2, page2.getTotalPages());
        assertNotNull(page1.getContent());
        assertNotNull(page2.getContent());
        assertEquals(1, page1.getContent().size());
        assertEquals(1, page2.getContent().size());
        TestUtils.assertEqualsJourney(TestUtils.JOURNEY_1, page1.getContent().getFirst());
        TestUtils.assertEqualsJourney(TestUtils.JOURNEY_2, page2.getContent().getFirst());
    }
    
    @Test
    public void testFindAllPagedNoJourneys(){
        TestUtils.deleteJourneys(jdbcTemplate);

        Page<Journey> page1 = journeyDao.findAll(TestUtils.PAGE_1_DEFAULT);

        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(0, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(0, page1.getContent().size());
    }

    @Test
    public void testFindByOriginCityPaged(){
        Page<Journey> page1 = journeyDao.findByOriginCity(TestUtils.CITY_1_ID, TestUtils.PAGE_1_DEFAULT);

        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(1, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(2, page1.getContent().size());
    }
    @Test
    public void testFindByOriginCityPagedWrongCity(){
        Page<Journey> page1 = journeyDao.findByOriginCity(1241234, new PageParams(1,2));

        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(0, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(0, page1.getContent().size());
    }

    @Test
    public void testSearchSimple(){
        Page<Journey> page = journeyDao.search(TestUtils.CITY_2_NAME, TestUtils.PAGE_1_BIG);

        assertNotNull(page);
        assertNotNull(page.getContent());
        assertEquals(1, page.getCurrentPage());
        assertEquals(1, page.getTotalPages());
        assertEquals(2, page.getContent().size());
    }
    @Test
    public void testSearchSimpleNoSearch(){
        Page<Journey> page = journeyDao.search("", TestUtils.PAGE_1_BIG);

        assertNotNull(page);
        assertNotNull(page.getContent());
        assertEquals(1, page.getCurrentPage());
        assertEquals(1, page.getTotalPages());
        assertEquals(2, page.getContent().size());
    }

    @Test
    public void testUpdate(){
        journeyDao.update(
            TestUtils.JOURNEY_1_ID, 
            TestUtils.UNI_3, 
            TestUtils.JOURNEY_START_DATE.plusDays(10), 
            TestUtils.JOURNEY_END_DATE.plusDays(10), 
            "COOL!"
        );

        Journey journey = jdbcTemplate.queryForObject(
            TestUtils.JOURNEY_SELECT_BY_ID, 
            TestUtils.JOURNEY_ROW_MAPPER, 
            TestUtils.JOURNEY_1_ID
        );
        TestUtils.assertEqualsJourney(journey, Map.of(
            "destination", TestUtils.UNI_3, 
            "startDate", TestUtils.JOURNEY_START_DATE.plusDays(10), 
            "endDate", TestUtils.JOURNEY_END_DATE.plusDays(10), 
            "description", "COOL!")
        );
    }
    @Test
    public void testUpdateNotFound(){
        journeyDao.update(
            12341234, 
            TestUtils.UNI_3, 
            TestUtils.JOURNEY_START_DATE.plusDays(10),
            TestUtils.JOURNEY_END_DATE.plusDays(10), 
            "New description"
        );

        Journey journey = jdbcTemplate.queryForObject(
            TestUtils.JOURNEY_SELECT_BY_ID, 
            TestUtils.JOURNEY_ROW_MAPPER, 
            TestUtils.JOURNEY_1_ID
        );
        TestUtils.assertEqualsJourney(journey);
    }
    @Test
    public void testUpdateDeleted(){
        journeyDao.update(
            TestUtils.JOURNEY_DELETED_ID, 
            TestUtils.UNI_3, 
            TestUtils.JOURNEY_START_DATE.plusDays(10), 
            TestUtils.JOURNEY_END_DATE.plusDays(10), 
            "New description"
        );

        assertFalse(jdbcTemplate.queryForObject(
            TestUtils.JOURNEY_IS_DELETED_BY_ID, 
            Boolean.class, 
            TestUtils.JOURNEY_DELETED_ID)
        );
        Journey journey = jdbcTemplate.queryForObject(
            TestUtils.JOURNEY_SELECT_BY_ID, 
            TestUtils.JOURNEY_ROW_MAPPER, 
            TestUtils.JOURNEY_DELETED_ID
        );
        TestUtils.assertEqualsJourney(journey, Map.of(
            "user", TestUtils.USER_4, 
            "id", TestUtils.JOURNEY_DELETED_ID,
            "destination", TestUtils.UNI_3, 
            "startDate", TestUtils.JOURNEY_START_DATE.plusDays(10), 
            "endDate", TestUtils.JOURNEY_END_DATE.plusDays(10), 
            "description", "New description")
        );
    }

    @Test
    public void testRecommendedJourneysBasic(){
        //TestUtils.JOURNEY_2 should have internal score of 95 (30 match city, 50 match uni, 15 overlap)
        //should have internal score of 80 (30 match city, 50 match uni)
        Journey newJourney = TestUtils.insertJourney(ds, Map.of("user", TestUtils.USER_3, "startDate", TestUtils.JOURNEY_END_DATE.plusDays(2), "endDate", TestUtils.JOURNEY_END_DATE.plusDays(40)));

        Page<Journey> page1 = journeyDao.findRecommended(TestUtils.USER_1_MAIL, TestUtils.PAGE_1_BIG);

        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(1, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(2, page1.getContent().size());
        TestUtils.assertEqualsJourney(TestUtils.JOURNEY_2, page1.getContent().get(0));
        TestUtils.assertEqualsJourney(newJourney, page1.getContent().get(1));
    }
    @Test
    public void testRecommendedJourneysNoJourneys(){
        TestUtils.deleteJourneys(jdbcTemplate);
        Page<Journey> page1 = journeyDao.findRecommended(TestUtils.USER_1_MAIL, TestUtils.PAGE_1_BIG);

        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(0, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(0, page1.getContent().size());
    }
    @Test
    public void testRecommendedJourneysGoingToMyCity(){
        TestUtils.deleteJourneys(jdbcTemplate);
        TestUtils.insertJourney(ds, Map.of("user", TestUtils.USER_1));
        //should have internal score of 80 (30 match origin city while there, 50 match origin uni while there)
        Journey newJourney1 = TestUtils.insertJourney(ds, Map.of("user", TestUtils.USER_3, "destination", TestUtils.UNI_1, "startDate", TestUtils.JOURNEY_END_DATE.plusDays(-5), "endDate", TestUtils.JOURNEY_END_DATE.plusDays(20)));
        //should have an internal score of 15 (date overlap only)
        Journey newJourney2 = TestUtils.insertJourney(ds, Map.of("user", TestUtils.USER_4, "destination", TestUtils.UNI_1, "startDate", TestUtils.JOURNEY_END_DATE.plusDays(-10), "endDate", TestUtils.JOURNEY_END_DATE.plusDays(-2)));

        Page<Journey> page1 = journeyDao.findRecommended(TestUtils.USER_1_MAIL, TestUtils.PAGE_1_BIG);

        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(1, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(2, page1.getContent().size());
        TestUtils.assertEqualsJourney(newJourney1, page1.getContent().get(0));
        TestUtils.assertEqualsJourney(newJourney2, page1.getContent().get(1));
    }
    @Test
    public void testRecommendedJourneysWithInterests(){
        //should have internal score of 116 (50 + 30 match dest uni, 15 overlap, 21 interest match)
        Journey newJourney1 = TestUtils.insertJourney(ds, Map.of("user", TestUtils.USER_I3));
        //should have internal score of 113 (50 + 30 match dest uni, 15 overlap, 18 interest match)
        Journey newJourney2 = TestUtils.insertJourney(ds, Map.of("user", TestUtils.USER_I2));
        //should have internal score of 107 (50 + 30 match dest uni, 15 overlap, 12 interest match)
        Journey newJourney3 = TestUtils.insertJourney(ds, Map.of("user", TestUtils.USER_I1));
        //TestUtils.JOURNEY_2 should have internal score of 95 (50 + 30 match dest uni, 15 overlap)

        Page<Journey> page1 = journeyDao.findRecommended(TestUtils.USER_1_MAIL, TestUtils.PAGE_1_BIG);

        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(1, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(4, page1.getContent().size());
        TestUtils.assertEqualsJourney(newJourney1, page1.getContent().get(0));
        TestUtils.assertEqualsJourney(newJourney2, page1.getContent().get(1));
        TestUtils.assertEqualsJourney(newJourney3, page1.getContent().get(2));
        TestUtils.assertEqualsJourney(TestUtils.JOURNEY_2, page1.getContent().get(3));
    }
    @Test
    public void testRecommendedJourneysWithInterestsComplex(){
        TestUtils.deleteJourneys(jdbcTemplate);
        TestUtils.insertJourney(ds, Map.of("user", TestUtils.USER_1));
        //should have internal score of 107 (50 + 30 match dest uni, 15 overlap, 12 interest match)
        Journey newJourney1 = TestUtils.insertJourney(ds, Map.of("user", TestUtils.USER_I1));
        //should have internal score of 98 (50 + 30 match origin uni, 18 interest match)
        Journey newJourney2 = TestUtils.insertJourney(ds, Map.of("user", TestUtils.USER_I2, "destination", TestUtils.UNI_1, "startDate", TestUtils.JOURNEY_END_DATE.plusDays(2), "endDate", TestUtils.JOURNEY_END_DATE.plusDays(30)));
        //should have internal score of 36 (15 overlap, 21 interest match)
        Journey newJourney3 = TestUtils.insertJourney(ds, Map.of("user", TestUtils.USER_I3, "destination", TestUtils.UNI_1, "startDate", TestUtils.JOURNEY_END_DATE.plusDays(-7), "endDate", TestUtils.JOURNEY_END_DATE.plusDays(-3)));
        //TestUtils.JOURNEY_2 should have internal score of 95 (50 + 30 match dest uni, 15 overlap)
        //should have internal score of 80 (50 + 30 match dest uni)
        Journey newJourney4 = TestUtils.insertJourney(ds, Map.of("user", TestUtils.USER_3, "startDate", TestUtils.JOURNEY_END_DATE.plusDays(2), "endDate", TestUtils.JOURNEY_END_DATE.plusDays(20)));
        //should have internal score of 45 (30 city match, 15 overlap)
        Journey newJourney5 = TestUtils.insertJourney(ds, Map.of("user", TestUtils.USER_4, "destination", TestUtils.UNI_3));

        Page<Journey> page1 = journeyDao.findRecommended(TestUtils.USER_1_MAIL, TestUtils.PAGE_1_BIG);

        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(1, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(5, page1.getContent().size());

        TestUtils.assertEqualsJourney(newJourney1, page1.getContent().get(0));
        TestUtils.assertEqualsJourney(newJourney2, page1.getContent().get(1));
        TestUtils.assertEqualsJourney(newJourney4, page1.getContent().get(2));   
        TestUtils.assertEqualsJourney(newJourney5, page1.getContent().get(3));
        TestUtils.assertEqualsJourney(newJourney3, page1.getContent().get(4));
    }

    @Test
    public void testFindAllWithFilters(){
        Page<Journey> page = journeyDao.search(
            null, 
            TestUtils.USER_1_ID,
            SortFieldJourney.START_DATE, 
            SortDirection.ASC, 
            TestUtils.CITY_1_NAME, 
            TestUtils.JOURNEY_START_DATE, 
            TestUtils.JOURNEY_END_DATE, 
            TestUtils.INTEREST_1_NAME, 
            false, 
            true, 
            true, 
            true,
            TestUtils.PAGE_1_BIG
        );

        assertNotNull(page);
        assertNotNull(page.getContent());
        assertEquals(1, page.getCurrentPage());
        assertEquals(0, page.getTotalPages());
        assertEquals(0, page.getContent().size());
    }
    @Test
    public void testFindAllWithFiltersComplex(){
        Page<Journey> page = journeyDao.search(
            TestUtils.USER_1_NAME, 
            TestUtils.USER_1_ID, 
            SortFieldJourney.START_DATE, 
            SortDirection.ASC, 
            TestUtils.CITY_1_NAME, 
            TestUtils.JOURNEY_START_DATE, 
            TestUtils.JOURNEY_END_DATE, 
            null, 
            false, 
            true, 
            true,
            false, 
            TestUtils.PAGE_1_BIG
        );

        assertNotNull(page);
        assertNotNull(page.getContent());
        assertEquals(1, page.getCurrentPage());
        assertEquals(0, page.getTotalPages());
        assertEquals(0, page.getContent().size());
    }
    @Test
    public void testFindAllWithFiltersComplexNoDestinationPastAttendingNoUser(){
        Page<Journey> page = journeyDao.search(
            TestUtils.USER_1_NAME,
            null, 
            SortFieldJourney.START_DATE, 
            SortDirection.ASC, 
            "", 
            TestUtils.JOURNEY_START_DATE, 
            TestUtils.JOURNEY_END_DATE, 
            null, 
            true, 
            false, 
            true, 
            false,
            TestUtils.PAGE_1_BIG
        );

        assertNotNull(page);
        assertNotNull(page.getContent());
        assertEquals(1, page.getCurrentPage());
        assertEquals(0, page.getTotalPages());
        assertEquals(0, page.getContent().size());
    }
    @Test
    public void testFindAllWithFiltersComplexNoUserUpcoming(){
        Page<Journey> page = journeyDao.search(
            TestUtils.USER_2_NAME, 
            null, 
            SortFieldJourney.START_DATE, 
            SortDirection.ASC, 
            "", 
            TestUtils.JOURNEY_START_DATE, 
            TestUtils.JOURNEY_END_DATE, 
            null, 
            false, 
            true, 
            false, 
            false,
            TestUtils.PAGE_1_BIG
        );

        assertNotNull(page);
        assertNotNull(page.getContent());
        assertEquals(1, page.getCurrentPage());
        assertEquals(1, page.getTotalPages());
        assertEquals(1, page.getContent().size());
        TestUtils.assertEqualsJourney(TestUtils.JOURNEY_2, page.getContent().get(0));
    }
    @Test
    public void testFindAllWithFiltersComplexNoUserUpcomingNoInterest(){
        Page<Journey> page = journeyDao.search(
            TestUtils.USER_1_NAME, 
            null, 
            SortFieldJourney.START_DATE, 
            SortDirection.ASC, 
            "", 
            TestUtils.JOURNEY_START_DATE, 
            TestUtils.JOURNEY_END_DATE, 
            "", 
            false, 
            true, 
            false, 
            false,
            TestUtils.PAGE_1_BIG
        );

        assertNotNull(page);
        assertNotNull(page.getContent());
        assertEquals(1, page.getCurrentPage());
        assertEquals(1, page.getTotalPages());
        assertEquals(1, page.getContent().size());
        TestUtils.assertEqualsJourney(TestUtils.JOURNEY_1, page.getContent().get(0));
    }
    @Test
    public void testFindAllWithFiltersNotUpcomingNotAttending(){
        Page<Journey> page = journeyDao.search(
            null,
            TestUtils.USER_1_ID,  
            SortFieldJourney.END_DATE, 
            SortDirection.ASC, 
            TestUtils.CITY_1_NAME, 
            TestUtils.JOURNEY_START_DATE, 
            TestUtils.JOURNEY_END_DATE, 
            null, 
            false, 
            false, 
            false,
            false, 
            TestUtils.PAGE_1_BIG
        );

        assertNotNull(page);
        assertNotNull(page.getContent());
        assertEquals(1, page.getCurrentPage());
        assertEquals(0, page.getTotalPages());
        assertEquals(0, page.getContent().size());
    }
    @Test
    public void testFindAllWithFiltersNotUpcomingNotAttendingReverseSort(){
        Page<Journey> page = journeyDao.search(
            null,
            TestUtils.USER_1_ID,  
            SortFieldJourney.START_DATE, 
            SortDirection.DESC, 
            TestUtils.CITY_1_NAME, 
            TestUtils.JOURNEY_START_DATE, 
            TestUtils.JOURNEY_END_DATE, 
            null, 
            false, 
            false, 
            false,
            false, 
            TestUtils.PAGE_1_BIG
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
            SortDirection.DESC, 
            null, 
            null, 
            null,
            null, 
            false, 
            false, 
            false, 
            false, 
            TestUtils.PAGE_1_BIG
        );

        assertNotNull(page);
        assertNotNull(page.getContent());
        assertEquals(1, page.getCurrentPage());
        assertEquals(1, page.getTotalPages());
        assertEquals(2, page.getContent().size());
        TestUtils.assertEqualsJourney(TestUtils.JOURNEY_2, page.getContent().get(0));
        TestUtils.assertEqualsJourney(TestUtils.JOURNEY_1, page.getContent().get(1));
    }
    @Test
    public void testFindAllWithFiltersNoParamsEmpty(){
        Page<Journey> page = journeyDao.search(
            "", 
            null, 
            SortFieldJourney.START_DATE, 
            SortDirection.DESC, 
            "", 
            null, 
            null,
            "", 
            false, 
            false, 
            false, 
            false, 
            TestUtils.PAGE_1_BIG
        );

        assertNotNull(page);
        assertNotNull(page.getContent());
        assertEquals(1, page.getCurrentPage());
        assertEquals(1, page.getTotalPages());
        assertEquals(2, page.getContent().size());
        TestUtils.assertEqualsJourney(TestUtils.JOURNEY_1, page.getContent().get(0));
        TestUtils.assertEqualsJourney(TestUtils.JOURNEY_2, page.getContent().get(1));
    }
    @Test
    public void testFindAllWithFiltersNoParamsFilter(){
        Page<Journey> page = journeyDao.search(
            TestUtils.USER_2_NAME, 
            null, 
            SortFieldJourney.START_DATE, 
            SortDirection.DESC, 
            null, 
            null, 
            null,
            null, 
            false, 
            false, 
            false, 
            false, 
            TestUtils.PAGE_1_BIG
        );

        assertNotNull(page);
        assertNotNull(page.getContent());
        assertEquals(1, page.getCurrentPage());
        assertEquals(1, page.getTotalPages());
        assertEquals(1, page.getContent().size());
        assertEquals(1, page.getContent().size());
        TestUtils.assertEqualsJourney(TestUtils.JOURNEY_2, page.getContent().get(0));
    }
}
