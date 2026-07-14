package ar.edu.itba.paw.persistence;

import static org.junit.Assert.*;
import static ar.edu.itba.paw.persistence.TestUtils.*;

import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.sql.DataSource;

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

import ar.edu.itba.paw.models.City;
import ar.edu.itba.paw.models.Country;
import ar.edu.itba.paw.models.Page;
import ar.edu.itba.paw.models.exceptions.CityAlreadyExistsException;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class CityHibernateDaoTest {

    @Autowired
    private DataSource ds;

    @Autowired
    private CityHibernateDao cityDao;

    @PersistenceContext
    private EntityManager em;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp(){
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Test
    public void testFindById(){
        Optional<City> maybeCity = cityDao.findById(CITY_1_ID);

        assertNotNull(maybeCity);
        assertTrue(maybeCity.isPresent());
        assertEqualsCity(CITY_1, maybeCity.get());
    }
    @Test
    public void testFindByIdDeleted(){
        Optional<City> maybeCity = cityDao.findById(CITY_DELETED_ID);

        assertNotNull(maybeCity);
        assertFalse(maybeCity.isPresent());
    }

    @Test
    public void testFindByName(){
        Optional<City> maybeCity = cityDao.findByName(CITY_1_NAME);

        assertNotNull(maybeCity);
        assertTrue(maybeCity.isPresent());
        assertEqualsCity(CITY_1, maybeCity.get());
    }
    @Test
    public void testFindByNameMissingCity(){
        Optional<City> maybeCity = cityDao.findByName("CITY_1_NAME");

        assertNotNull(maybeCity);
        assertFalse(maybeCity.isPresent());
    }
    @Test
    public void testFindByNameNullName(){
        Optional<City> maybeCity = cityDao.findByName(null);

        assertNotNull(maybeCity);
        assertFalse(maybeCity.isPresent());
    }
    @Test
    public void testFindByNameDeleted(){
        Optional<City> maybeCity = cityDao.findByName(CITY_DELETED_NAME);

        assertNotNull(maybeCity);
        assertFalse(maybeCity.isPresent());
    }

    @Test
    public void testSearchNoFiltering(){
        Page<City> page1 = cityDao.search(
            CITY_1_NAME.substring(0, 3), 
            PAGE_1_BIG
        );

        assertNotNull(page1);
        assertEquals(1, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(TOTAL_CITIES, page1.getContent().size());
        page1.getContent().forEach((city) -> 
            assertEqualsCity(CITY_DATA.get(city.getId()), city)
        );
    }
    @Test
    public void testSearchFiltering(){
        Page<City> page1 = cityDao.search(
            CITY_1_NAME.substring(
                CITY_1_NAME.length()-1, 
                CITY_1_NAME.length()
            ), 
            PAGE_1_BIG
        );

        assertNotNull(page1);
        assertEquals(1, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(1, page1.getContent().size());
        assertEqualsCity(CITY_1, page1.getContent().get(0));
    }
    @Test
    public void testSearchEmpty(){
        Page<City> page1 = cityDao.search("", PAGE_1_BIG);

        assertNotNull(page1);
        assertEquals(1, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(TOTAL_CITIES, page1.getContent().size());
        page1.getContent().forEach((city) -> 
            assertEqualsCity(CITY_DATA.get(city.getId()), city)
        );
    }
    @Test
    public void testSearchDeleted(){
        Page<City> page1 = cityDao.search(CITY_DELETED_NAME, PAGE_1_BIG);

        assertNotNull(page1);
        assertEquals(0, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(0, page1.getContent().size());
    }
    @Test
    public void testSearchNullQuery(){
        Page<City> page1 = cityDao.search(null, PAGE_1_BIG);

        assertNotNull(page1);
        assertEquals(1, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(TOTAL_CITIES, page1.getContent().size());
        page1.getContent().forEach((city) -> 
            assertEqualsCity(CITY_DATA.get(city.getId()), city)
        );
    }
    @Test
    public void testSearchPageOne(){
        Page<City> page1 = cityDao.search("city", PAGE_1_DEFAULT);

        assertNotNull(page1);
        assertEquals(2, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(2, page1.getContent().size());
    }
    @Test
    public void testSearchPageTwo(){
        Page<City> page2 = cityDao.search("city", PAGE_2_DEFAULT);
        assertNotNull(page2);
        assertEquals(2, page2.getTotalPages());
        assertNotNull(page2.getContent());
        assertEquals(1, page2.getContent().size());
    }

    @Test
    public void testFindAllPagedOnePage(){
        Page<City> page1 = cityDao.findAll(PAGE_1_BIG);

        assertNotNull(page1);
        assertEquals(1, page1.getTotalPages());
        assertEquals(TOTAL_CITIES, page1.getContent().size());
        for (City city : page1.getContent()){
            assertEqualsCity(CITY_DATA.get(city.getId()), city);
        }
    }

    @Test
    public void testFindAllCities(){
        Page<City> page1 = cityDao.findAll(PAGE_1_BIG);

        assertNotNull(page1);
        assertEquals(1, page1.getTotalPages());
        assertEquals(TOTAL_CITIES, page1.getContent().size());
    }
    @Test
    public void testFindAllCitiesNoCities(){
        deleteCities(jdbcTemplate);

        Page<City> page1 = cityDao.findAll(PAGE_1_DEFAULT);

        assertNotNull(page1);
        assertEquals(0, page1.getTotalPages());
        assertEquals(0, page1.getContent().size());
    }

    @Test
    public void testCreate(){
        City city = cityDao.create(NEW_CITY_NAME, COUNTRY_1);
        em.flush();

        assertEquals(
            TOTAL_CITIES + 1,
            Optional.ofNullable(
                jdbcTemplate.queryForObject(CITIES_COUNT_NOT_DELETED, Integer.class)
            ).get().intValue()
        );
        final City persisted = jdbcTemplate.queryForObject(CITY_SELECT_BY_ID, CITY_ROW_MAPPER, city.getId());
        assertEqualsCity(
            new City(NEW_CITY_NAME, COUNTRY_1, city.getId()),
            persisted
        );
    }
    @Test(expected = CityAlreadyExistsException.class)
    public void testCreateDuplicate(){
        cityDao.create(CITY_1_NAME, COUNTRY_1);
        em.flush();
    }
    @Test(expected = PersistenceException.class)
    public void testCreateEmpty(){
        cityDao.create(null, COUNTRY_1);
        em.flush();
    }
    @Test(expected = PersistenceException.class)
    public void testCreateWrongCountry(){
        cityDao.create(null, new Country(1241234l, null, null));
        em.flush();
    }
    @Test
    public void testCreateDuplicateDeleted(){
        int rowsBefore = JdbcTestUtils.countRowsInTable(jdbcTemplate, CITY_TABLE);
        em.flush();

        cityDao.create(CITY_DELETED_NAME, COUNTRY_2);
        em.flush();

        assertEquals(rowsBefore, JdbcTestUtils.countRowsInTable(jdbcTemplate, CITY_TABLE));
        assertEquals(
            TOTAL_CITIES + 1,
            Optional.ofNullable(
                jdbcTemplate.queryForObject(CITIES_COUNT_NOT_DELETED, Integer.class)
            ).get().intValue()
        );
        City city = jdbcTemplate.queryForObject(
            CITY_SELECT_BY_NAME, 
            CITY_ROW_MAPPER, 
            CITY_DELETED_NAME
        );
        assertEqualsCity(CITY_DELETED, city);
    }


}