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

import ar.edu.itba.paw.models.City;
import ar.edu.itba.paw.persistence.CityJdbcDao;

@Sql(scripts = "classpath:schema.sql")
@Transactional
@Rollback
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class CityJdbcDaoTest {

    private static final String CITY_TABLE = "cities";
    private static final String COUNTRY_TABLE = "countries";
    
    private static final String CITY_1 = "city1";
    private static final String CITY_2 = "city2";
    private static final String CITY_3 = "city3";
    private static final String COUNTRY_NAME_1 = "cuntry1";
    private static final String COUNTRY_CODE_1 = "aa";    
    private static final String COUNTRY_NAME_2 = "cuntry2";
    private static final String COUNTRY_CODE_2 = "bb";
    private static final int TOTAL_CITIES = 3;
    private static long city_id1;
    private static long country_id1;
    private static long country_id2;

    @Autowired
    private DataSource ds;

    @Autowired
    private CityJdbcDao cityDao;

    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert insert;

    @Before
    public void setUp(){
        jdbcTemplate = new JdbcTemplate(ds);
        insert = new SimpleJdbcInsert(ds).withTableName(CITY_TABLE).usingGeneratedKeyColumns("id");

        SimpleJdbcInsert countryInsert = new SimpleJdbcInsert(ds).withTableName(COUNTRY_TABLE).usingGeneratedKeyColumns("id");
        country_id1 = countryInsert.executeAndReturnKey(Map.of("name", COUNTRY_NAME_1, "code", COUNTRY_CODE_1)).longValue();
        country_id2 = countryInsert.executeAndReturnKey(Map.of("name", COUNTRY_NAME_2, "code", COUNTRY_CODE_2)).longValue();

        city_id1 = insert.executeAndReturnKey(Map.of("name", CITY_1, "country_id", country_id1)).longValue();
        insert.execute(Map.of("name", CITY_2, "country_id", country_id1));
        insert.execute(Map.of("name", CITY_3, "country_id", country_id2));
    }
    
    @Test
    public void testFindByName(){
        Optional<City> maybeCity = cityDao.findByName(CITY_1);

        assertNotNull(maybeCity);
        assertTrue(maybeCity.isPresent());
        assertEquals(city_id1, maybeCity.get().getId());
        assertEquals(CITY_1, maybeCity.get().getName());
        assertEquals(COUNTRY_NAME_1, maybeCity.get().getCountry());
    }
    @Test
    public void testFindByNameMissingCity(){
        Optional<City> maybeCity = cityDao.findByName("CITY_1");

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
    public void testFindAll(){
        List<City> cities = cityDao.findAll();

        assertNotNull(cities);
        assertEquals(TOTAL_CITIES, cities.size());
        List<String> cityNames = List.of(CITY_1, CITY_2, CITY_3);
        for (City city : cities){
            assertTrue(cityNames.contains(city.getName()));
        }
    }
    @Test
    public void testFindAllNoCities(){
        JdbcTestUtils.deleteFromTables(jdbcTemplate, CITY_TABLE);

        List<City> cities = cityDao.findAll();

        assertNotNull(cities);
        assertEquals(0, cities.size());
    }
    @Test
    public void testGetAllCities(){
        List<City> cities = cityDao.getAllCities();

        assertNotNull(cities);
        assertEquals(TOTAL_CITIES, cities.size());
        List<String> cityNames = List.of(CITY_1, CITY_2, CITY_3);
        for (City city : cities){
            assertTrue(cityNames.contains(city.getName()));
        }
    }
    @Test
    public void testGetAllCitiesNoCities(){
        JdbcTestUtils.deleteFromTables(jdbcTemplate, CITY_TABLE);

        List<City> cities = cityDao.getAllCities();

        assertNotNull(cities);
        assertEquals(0, cities.size());
    }

    @Test
    public void testFindAllByCountry1(){
        List<City> cities = cityDao.findAllByCountry(COUNTRY_NAME_1);

        assertNotNull(cities);
        assertEquals(2, cities.size());
        List<String> cityNames = List.of(CITY_1, CITY_2);
        for (City city : cities){
            assertTrue(cityNames.contains(city.getName()));
            assertEquals(COUNTRY_NAME_1, city.getCountry());
        }
    }
    @Test
    public void testFindAllByCountry2(){
        List<City> cities = cityDao.findAllByCountry(COUNTRY_NAME_2);

        assertNotNull(cities);
        assertEquals(1, cities.size());
        assertEquals(CITY_3, cities.getFirst().getName());
        assertEquals(COUNTRY_NAME_2, cities.getFirst().getCountry());
    }
    @Test
    public void testFindAllByCountryWrongCountry(){
        List<City> cities = cityDao.findAllByCountry("COUNTRY_NAME_2");

        assertNotNull(cities);
        assertEquals(0, cities.size());
    }
    @Test
    public void testFindAllByCountryMissingCountry(){
        List<City> cities = cityDao.findAllByCountry(null);

        assertNotNull(cities);
        assertEquals(0, cities.size());
    }

    @Test
    public void testFindAllBySubstring(){
//        List<City> cities = cityDao.findAllBySubstring(CITY_1);
//        assertNotNull(cities);
//        assertEquals(1, cities.size());
    }
    @Test
    public void testFindAllBySubstring2(){
//        List<City> cities = cityDao.findAllBySubstring("1");
//        assertNotNull(cities);
//        assertEquals(1, cities.size());
    }
    @Test
    public void testFindAllBySubstring3(){
//        List<City> cities = cityDao.findAllBySubstring("city");
//        assertNotNull(cities);
//        assertEquals(3, cities.size());
    }
    @Test
    public void testFindAllBySubstringWrongCity(){
//        List<City> cities = cityDao.findAllBySubstring("fake");
//        assertNotNull(cities);
//        assertEquals(0, cities.size());
    }
    @Test
    public void testFindAllBySubstringEmptyCity(){
//        List<City> cities = cityDao.findAllBySubstring("");
//        assertNotNull(cities);
//        assertEquals(3, cities.size());
    }
    @Test
    public void testFindAllBySubstringMissingCity(){
//        List<City> cities = cityDao.findAllBySubstring(null);
//        assertNotNull(cities);
//        assertEquals(0, cities.size());
    }

    @Test
    public void testFindByAll(){
        Optional<City> maybeCity = cityDao.findBy(city_id1, CITY_1, COUNTRY_NAME_1);
        assertNotNull(maybeCity);
        assertTrue(maybeCity.isPresent());
        City city = maybeCity.get();
        assertEquals(city_id1, city.getId());
        assertEquals(CITY_1, city.getName());
        assertEquals(COUNTRY_NAME_1, city.getCountry());
    }
    @Test
    public void testFindByGenericById(){
        Optional<City> maybeCity = cityDao.findBy(city_id1, null, null);
        assertNotNull(maybeCity);
        assertTrue(maybeCity.isPresent());
        City city = maybeCity.get();
        assertEquals(city_id1, city.getId());
        assertEquals(CITY_1, city.getName());
        assertEquals(COUNTRY_NAME_1, city.getCountry());
    }
    @Test
    public void testFindByGenericByName(){
        Optional<City> maybeCity = cityDao.findBy(null, CITY_1, null);
        assertNotNull(maybeCity);
        assertTrue(maybeCity.isPresent());
        City city = maybeCity.get();
        assertEquals(city_id1, city.getId());
        assertEquals(CITY_1, city.getName());
        assertEquals(COUNTRY_NAME_1, city.getCountry());
    }
    @Test
    public void testFindByGenericByName2(){
        Optional<City> maybeCity = cityDao.findBy(null, CITY_3, null);
        assertNotNull(maybeCity);
        assertTrue(maybeCity.isPresent());
        City city = maybeCity.get();
        assertEquals(CITY_3, city.getName());
        assertEquals(COUNTRY_NAME_2, city.getCountry());
    }
    @Test
    public void testFindByGenericByCountry(){
        Optional<City> maybeCity = cityDao.findBy(null, null, COUNTRY_NAME_1);
        assertNotNull(maybeCity);
        assertTrue(maybeCity.isPresent());
        City city = maybeCity.get();
        assertTrue(List.of(CITY_1, CITY_2).contains(city.getName()));
        assertEquals(COUNTRY_NAME_1, city.getCountry());
    }
    @Test
    public void testFindByGenericByCountry2(){
        Optional<City> maybeCity = cityDao.findBy(null, null, COUNTRY_NAME_2);
        assertNotNull(maybeCity);
        assertTrue(maybeCity.isPresent());
        City city = maybeCity.get();
        assertEquals(CITY_3, city.getName());
        assertEquals(COUNTRY_NAME_2, city.getCountry());
    }
    @Test
    public void testFindByGenericWrongId(){
        Optional<City> maybeCity = cityDao.findBy((long)141234, null, COUNTRY_NAME_2);
        assertNotNull(maybeCity);
        assertFalse(maybeCity.isPresent());
    }
    @Test
    public void testFindByGenericMissingId(){
        Optional<City> maybeCity = cityDao.findBy((long)0, null, COUNTRY_NAME_2);
        assertNotNull(maybeCity);
        assertTrue(maybeCity.isPresent());
        City city = maybeCity.get();
        assertEquals(CITY_3, city.getName());
        assertEquals(COUNTRY_NAME_2, city.getCountry());
    }
    @Test
    public void testFindByGenericNoParams(){
        Optional<City> maybeCity = cityDao.findBy(null, null, null);
        assertNotNull(maybeCity);
        assertTrue(maybeCity.isPresent());
    }
    @Test
    public void testFindByGenericEmptyName(){
        Optional<City> maybeCity = cityDao.findBy(null, "", null);
        assertNotNull(maybeCity);
        assertTrue(maybeCity.isPresent());
    }
    @Test
    public void testFindByGenericEmptyCountry(){
        Optional<City> maybeCity = cityDao.findBy(null, null, "");
        assertNotNull(maybeCity);
        assertTrue(maybeCity.isPresent());
    }
}

// Optional<City> findBy(Long id, String name, String country);
