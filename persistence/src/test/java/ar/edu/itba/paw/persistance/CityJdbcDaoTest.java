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
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.itba.paw.models.City;
import ar.edu.itba.paw.models.Country;
import ar.edu.itba.paw.models.Page;
import ar.edu.itba.paw.persistence.CityJdbcDao;

@SuppressWarnings("null")
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
    private static final String DELETED_CITY = "deleted_city";
    private static final String NEW_CITY = "new!";
    private static final String COUNTRY_NAME_1 = "cuntry";
    private static final String COUNTRY_CODE_1 = "aa";    
    private static final String COUNTRY_NAME_2 = "cuntry2";
    private static final String COUNTRY_CODE_2 = "bb";
    private static final int TOTAL_CITIES = 3; //City 4 will be logic deleted
    private static long CITY_ID_1;
    private static long DELETED_CITY_ID;
    private static long COUNTRY_ID_1;
    private static long COUNTRY_ID_2;

    @Autowired
    private DataSource ds;

    @Autowired
    private CityJdbcDao cityDao;

    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert insert;

    private RowMapper<City> CITY_ROW_MAPPER = (rs, n) -> new City(rs.getString("name"), rs.getString("country_name"), rs.getLong("id"));

    @Before
    public void setUp(){
        jdbcTemplate = new JdbcTemplate(ds);
        insert = new SimpleJdbcInsert(ds).withTableName(CITY_TABLE).usingGeneratedKeyColumns("id");

        SimpleJdbcInsert countryInsert = new SimpleJdbcInsert(ds).withTableName(COUNTRY_TABLE).usingGeneratedKeyColumns("id");
        COUNTRY_ID_1 = countryInsert.executeAndReturnKey(Map.of("name", COUNTRY_NAME_1, "code", COUNTRY_CODE_1)).longValue();
        COUNTRY_ID_2 = countryInsert.executeAndReturnKey(Map.of("name", COUNTRY_NAME_2, "code", COUNTRY_CODE_2)).longValue();

        CITY_ID_1 = insert.executeAndReturnKey(Map.of("name", CITY_1, "country_id", COUNTRY_ID_1, "deleted", false)).longValue();
        insert.execute(Map.of("name", CITY_2, "country_id", COUNTRY_ID_1, "deleted", false));
        insert.execute(Map.of("name", CITY_3, "country_id", COUNTRY_ID_2, "deleted", false));
        DELETED_CITY_ID = insert.executeAndReturnKey(Map.of("name", DELETED_CITY, "country_id", COUNTRY_ID_2, "deleted", true)).longValue();
    }
    
    @Test
    public void testFindByName(){
        Optional<City> maybeCity = cityDao.findByName(CITY_1);

        assertNotNull(maybeCity);
        assertTrue(maybeCity.isPresent());
        assertEquals(CITY_ID_1, maybeCity.get().getId());
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
    public void testFindByNameDeleted(){
        Optional<City> maybeCity = cityDao.findByName(DELETED_CITY);

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
    public void testSearchBySubstringNoFiltering(){
        Page<City> page1 = cityDao.searchBySubstring(CITY_1.substring(0, 3), 1, 3);

        assertNotNull(page1);
        assertEquals(1, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(3, page1.getContent().size());
    }
    @Test
    public void testSearchBySubstringFiltering(){
        Page<City> page1 = cityDao.searchBySubstring(CITY_1.substring(CITY_1.length()-1, CITY_1.length()), 1, 3);

        assertNotNull(page1);
        assertEquals(1, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(1, page1.getContent().size());
    }
    @Test
    public void testSearchBySubstringEmpty(){
        Page<City> page1 = cityDao.searchBySubstring("", 1, 3);

        assertNotNull(page1);
        assertEquals(1, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(3, page1.getContent().size());
    }
    @Test
    public void testSearchBySubstringDeleted(){
        Page<City> page1 = cityDao.searchBySubstring(DELETED_CITY, 1, 3);

        assertNotNull(page1);
        assertEquals(0, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(0, page1.getContent().size());
    }
    @Test
    public void testSearchBySubstringMissing(){
        Page<City> page1 = cityDao.searchBySubstring(null, 1, 3);

        assertNotNull(page1);
        assertEquals(1, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(3, page1.getContent().size());
    }
    @Test
    public void testSearchBySubstringPaging(){
        Page<City> page1 = cityDao.searchBySubstring("", 1, 2);
        Page<City> page2 = cityDao.searchBySubstring("", 2, 2);

        assertNotNull(page1);
        assertNotNull(page2);
        assertEquals(2, page1.getTotalPages());
        assertEquals(2, page2.getTotalPages());
        assertNotNull(page1.getContent());
        assertNotNull(page2.getContent());
        assertEquals(2, page1.getContent().size());
        assertEquals(1, page2.getContent().size());
    }

    @Test
    public void testFindByAll(){
        Optional<City> maybeCity = cityDao.findBy(CITY_ID_1, CITY_1, COUNTRY_NAME_1);
        assertNotNull(maybeCity);
        assertTrue(maybeCity.isPresent());
        City city = maybeCity.get();
        assertEquals(CITY_ID_1, city.getId());
        assertEquals(CITY_1, city.getName());
        assertEquals(COUNTRY_NAME_1, city.getCountry());
    }
    @Test
    public void testFindByGenericById(){
        Optional<City> maybeCity = cityDao.findBy(CITY_ID_1, null, null);
        assertNotNull(maybeCity);
        assertTrue(maybeCity.isPresent());
        City city = maybeCity.get();
        assertEquals(CITY_ID_1, city.getId());
        assertEquals(CITY_1, city.getName());
        assertEquals(COUNTRY_NAME_1, city.getCountry());
    }
    @Test
    public void testFindByGenericByIdDeleted(){
        Optional<City> maybeCity = cityDao.findBy(DELETED_CITY_ID, null, null);
        assertNotNull(maybeCity);
        assertFalse(maybeCity.isPresent());
    }
    @Test
    public void testFindByGenericByName(){
        Optional<City> maybeCity = cityDao.findBy(null, CITY_1, null);
        assertNotNull(maybeCity);
        assertTrue(maybeCity.isPresent());
        City city = maybeCity.get();
        assertEquals(CITY_ID_1, city.getId());
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
    public void testFindByGenericByNameDeleted(){
        Optional<City> maybeCity = cityDao.findBy(null, DELETED_CITY, null);
        assertNotNull(maybeCity);
        assertFalse(maybeCity.isPresent());
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

    @Test
    public void testGetAllCitiesPagedOnePage(){
        Page<City> page1 = cityDao.getAllCities(1, 3);

        assertNotNull(page1);
        assertEquals(1, page1.getTotalPages());
        assertEquals(3, page1.getContent().size());
    }
    @Test
    public void testGetAllCitiesPagedMultiplePages(){
        Page<City> page1 = cityDao.getAllCities(1, 2);
        Page<City> page2 = cityDao.getAllCities(2, 2);
        
        assertNotNull(page1);
        assertEquals(2, page1.getTotalPages());
        assertEquals(2, page1.getContent().size());
        assertNotNull(page1);
        assertEquals(2, page2.getTotalPages());
        assertEquals(1, page2.getContent().size());
    }
    @Test
    public void testGetAllCitiesPagedNoCities(){
        JdbcTestUtils.deleteFromTables(jdbcTemplate, CITY_TABLE);

        Page<City> page1 = cityDao.getAllCities(1, 2);
        
        assertNotNull(page1);
        assertEquals(0, page1.getTotalPages());
        assertEquals(0, page1.getContent().size());
    }

    @Test
    public void testCreate(){
        cityDao.createCity(NEW_CITY, new Country(COUNTRY_ID_1, null, null));

        assertEquals(TOTAL_CITIES + 1, jdbcTemplate.queryForObject("SELECT COUNT(*) FROM cities WHERE deleted = FALSE", Integer.class).intValue());
        Optional<City> maybeCity = jdbcTemplate.query("SELECT ci.id as id, ci.name as name, co.name as country_name FROM cities ci JOIN countries co ON ci.country_id = co.id WHERE deleted = FALSE AND name = ?", CITY_ROW_MAPPER, NEW_CITY).stream().findFirst();
        assertNotNull(maybeCity);
        assertTrue(maybeCity.isPresent());
        assertEquals(NEW_CITY, maybeCity.get().getName());
        assertEquals(COUNTRY_NAME_1, maybeCity.get().getCountry());
    }
    @Test(expected = DataAccessException.class)
    public void testCreateDuplicate(){
        cityDao.createCity(CITY_1, new Country(COUNTRY_ID_1, null, null));
    }
    @Test(expected = DataAccessException.class)
    public void testCreateEmpty(){
        cityDao.createCity(null, new Country(COUNTRY_ID_1, null, null));
    }
    @Test(expected = DataAccessException.class)
    public void testCreateWrongCountry(){
        cityDao.createCity(null, new Country(1241234, null, null));
    }
    @Test
    public void testCreateDuplicateDeleted(){
        int rowsBefore = JdbcTestUtils.countRowsInTable(jdbcTemplate, CITY_TABLE);

        cityDao.createCity(DELETED_CITY, new Country(COUNTRY_ID_2, null, null));

        assertEquals(rowsBefore, JdbcTestUtils.countRowsInTable(jdbcTemplate, CITY_TABLE));
        assertEquals(TOTAL_CITIES + 1, jdbcTemplate.queryForObject("SELECT COUNT(*) FROM cities WHERE deleted = FALSE", Integer.class).intValue());
        Optional<City> maybeCity = jdbcTemplate.query("SELECT ci.id as id, ci.name as name, co.name as country_name FROM cities ci JOIN countries co ON ci.country_id = co.id WHERE deleted = FALSE AND name = ?", CITY_ROW_MAPPER, DELETED_CITY).stream().findFirst();
        assertNotNull(maybeCity);
        assertTrue(maybeCity.isPresent());
        assertEquals(DELETED_CITY, maybeCity.get().getName());
        assertEquals(COUNTRY_NAME_2, maybeCity.get().getCountry());
        assertEquals(DELETED_CITY_ID, maybeCity.get().getId());
    }

    @Test
    public void testDeleteCity(){
        int rowsBefore = JdbcTestUtils.countRowsInTable(jdbcTemplate, CITY_TABLE);

        cityDao.delete(CITY_ID_1);

        assertEquals(rowsBefore, JdbcTestUtils.countRowsInTable(jdbcTemplate, CITY_TABLE));
        assertEquals(TOTAL_CITIES - 1, jdbcTemplate.queryForObject("SELECT COUNT(*) FROM cities WHERE deleted = FALSE", Integer.class).intValue());
    }
    @Test
    public void testDeleteDeleted(){
        int rowsBefore = JdbcTestUtils.countRowsInTable(jdbcTemplate, CITY_TABLE);

        cityDao.delete(CITY_ID_1);

        assertEquals(rowsBefore, JdbcTestUtils.countRowsInTable(jdbcTemplate, CITY_TABLE));
        assertEquals(TOTAL_CITIES - 1, jdbcTemplate.queryForObject("SELECT COUNT(*) FROM cities WHERE deleted = FALSE", Integer.class).intValue());
    }
    @Test
    public void testDeleteWrong(){
        int rowsBefore = JdbcTestUtils.countRowsInTable(jdbcTemplate, CITY_TABLE);

        cityDao.delete(12341234);

        assertEquals(rowsBefore, JdbcTestUtils.countRowsInTable(jdbcTemplate, CITY_TABLE));
        assertEquals(TOTAL_CITIES, jdbcTemplate.queryForObject("SELECT COUNT(*) FROM cities WHERE deleted = FALSE", Integer.class).intValue());
    }

    @Test
    public void testUpdate(){
        cityDao.updateCity(CITY_ID_1, NEW_CITY, new Country(COUNTRY_ID_1, null, null));

        Optional<City> maybeCity = jdbcTemplate.query("SELECT ci.id as id, ci.name as name, co.name as country_name FROM cities ci JOIN countries co ON ci.country_id = co.id WHERE deleted = FALSE AND id = ?", CITY_ROW_MAPPER, CITY_ID_1).stream().findFirst();
        assertNotNull(maybeCity);
        assertTrue(maybeCity.isPresent());
        assertEquals(NEW_CITY, maybeCity.get().getName());
        assertEquals(COUNTRY_NAME_1, maybeCity.get().getCountry());
    }
}

// Optional<City> findBy(Long id, String name, String country);
