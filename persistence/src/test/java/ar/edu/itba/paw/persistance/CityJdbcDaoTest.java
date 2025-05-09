package ar.edu.itba.paw.persistance;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import ar.edu.itba.paw.models.PageParams;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.itba.paw.models.City;
import ar.edu.itba.paw.models.Country;
import ar.edu.itba.paw.models.Page;
//import ar.edu.itba.paw.persistence.CityJdbcDao;
//
//@Sql(scripts = "classpath:schema.sql")
//@Transactional
//@Rollback
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(classes = TestConfig.class)
//public class CityJdbcDaoTest {
//
//
//    private static City CITY_1;
//    private static City CITY_DELETED;
//    private static Country COUNTRY_1;
//    private static Country COUNTRY_2;
//
//    @Autowired
//    private DataSource ds;
//
//    @Autowired
//    private CityJdbcDao cityDao;
//
//    private JdbcTemplate jdbcTemplate;
//
//    @Before
//    public void setUp(){
//        jdbcTemplate = new JdbcTemplate(ds);
//
//        COUNTRY_1 = jdbcTemplate.queryForObject(TestUtils.COUNTRY_SELECT_BY_CODE, TestUtils.COUNTRY_ROW_MAPPER, TestUtils.COUNTRY_1_CODE);
//        COUNTRY_2 = jdbcTemplate.queryForObject(TestUtils.COUNTRY_SELECT_BY_CODE, TestUtils.COUNTRY_ROW_MAPPER, TestUtils.COUNTRY_2_CODE);
//
//        CITY_1 = jdbcTemplate.queryForObject(TestUtils.CITY_SELECT_BY_NAME, TestUtils.CITY_ROW_MAPPER, TestUtils.CITY_1_NAME);
//        CITY_DELETED = jdbcTemplate.queryForObject(TestUtils.CITY_SELECT_BY_NAME, TestUtils.CITY_ROW_MAPPER, TestUtils.CITY_DELETED_NAME);
//    }
//
//    @Test
//    public void testFindByName(){
//        Optional<City> maybeCity = cityDao.findByName(TestUtils.CITY_1_NAME);
//
//        assertNotNull(maybeCity);
//        assertTrue(maybeCity.isPresent());
//        assertEquals(CITY_1.getId(), maybeCity.get().getId());
//        assertEquals(TestUtils.CITY_1_NAME, maybeCity.get().getName());
//        assertEquals(TestUtils.COUNTRY_1_NAME, maybeCity.get().getCountry());
//    }
//    @Test
//    public void testFindByNameMissingCity(){
//        Optional<City> maybeCity = cityDao.findByName("TestUtils.CITY_1_NAME");
//
//        assertNotNull(maybeCity);
//        assertFalse(maybeCity.isPresent());
//    }
//    @Test
//    public void testFindByNameNullName(){
//        Optional<City> maybeCity = cityDao.findByName(null);
//
//        assertNotNull(maybeCity);
//        assertFalse(maybeCity.isPresent());
//    }
//    @Test
//    public void testFindByNameDeleted(){
//        Optional<City> maybeCity = cityDao.findByName(TestUtils.CITY_DELETED_NAME);
//
//        assertNotNull(maybeCity);
//        assertFalse(maybeCity.isPresent());
//    }
//
//    @Test
//    public void testGetAllCities(){
//        List<City> cities = cityDao.getAllCities();
//
//        assertNotNull(cities);
//        assertEquals(TestUtils.TOTAL_CITIES, cities.size());
//        List<String> cityNames = List.of(TestUtils.CITY_1_NAME, TestUtils.CITY_2_NAME, TestUtils.CITY_3_NAME);
//        for (City city : cities){
//            assertTrue(cityNames.contains(city.getName()));
//        }
//    }
//    @Test
//    public void testGetAllCitiesNoCities(){
//        JdbcTestUtils.deleteFromTables(jdbcTemplate, TestUtils.JOURNEY_TABLE, TestUtils.USER_INTEREST_TABLE, TestUtils.USER_TABLE, TestUtils.UNIVERSITY_TABLE, TestUtils.CITY_TABLE);
//
//        List<City> cities = cityDao.getAllCities();
//
//        assertNotNull(cities);
//        assertEquals(0, cities.size());
//    }
//
//
//    @Test
//    public void testSearchBySubstringNoFiltering(){
//        Page<City> page1 = cityDao.searchBySubstring(TestUtils.CITY_1_NAME.substring(0, 3), new PageParams(1, 3));
//
//        assertNotNull(page1);
//        assertEquals(1, page1.getTotalPages());
//        assertNotNull(page1.getContent());
//        assertEquals(3, page1.getContent().size());
//    }
//    @Test
//    public void testSearchBySubstringFiltering(){
//        Page<City> page1 = cityDao.searchBySubstring(TestUtils.CITY_1_NAME.substring(TestUtils.CITY_1_NAME.length()-1, TestUtils.CITY_1_NAME.length()), new PageParams(1,3));
//
//        assertNotNull(page1);
//        assertEquals(1, page1.getTotalPages());
//        assertNotNull(page1.getContent());
//        assertEquals(1, page1.getContent().size());
//    }
//    @Test
//    public void testSearchBySubstringEmpty(){
//        Page<City> page1 = cityDao.searchBySubstring("", new PageParams(1, 3));
//
//        assertNotNull(page1);
//        assertEquals(1, page1.getTotalPages());
//        assertNotNull(page1.getContent());
//        assertEquals(3, page1.getContent().size());
//    }
//    @Test
//    public void testSearchBySubstringDeleted(){
//        Page<City> page1 = cityDao.searchBySubstring(TestUtils.CITY_DELETED_NAME, new PageParams(1, 3));
//
//        assertNotNull(page1);
//        assertEquals(0, page1.getTotalPages());
//        assertNotNull(page1.getContent());
//        assertEquals(0, page1.getContent().size());
//    }
//    @Test
//    public void testSearchBySubstringMissing(){
//        Page<City> page1 = cityDao.searchBySubstring(null, new PageParams(1, 3));
//
//        assertNotNull(page1);
//        assertEquals(1, page1.getTotalPages());
//        assertNotNull(page1.getContent());
//        assertEquals(3, page1.getContent().size());
//    }
//    @Test
//    public void testSearchBySubstringPaging(){
//        Page<City> page1 = cityDao.searchBySubstring("", new PageParams(1, 2));
//        Page<City> page2 = cityDao.searchBySubstring("", new PageParams(2, 2));
//
//        assertNotNull(page1);
//        assertNotNull(page2);
//        assertEquals(2, page1.getTotalPages());
//        assertEquals(2, page2.getTotalPages());
//        assertNotNull(page1.getContent());
//        assertNotNull(page2.getContent());
//        assertEquals(2, page1.getContent().size());
//        assertEquals(1, page2.getContent().size());
//    }
//
//    @Test
//    public void testFindByAll(){
//        Optional<City> maybeCity = cityDao.findBy(CITY_1.getId(), TestUtils.CITY_1_NAME, TestUtils.COUNTRY_1_NAME);
//        assertNotNull(maybeCity);
//        assertTrue(maybeCity.isPresent());
//        City city = maybeCity.get();
//        assertEquals(CITY_1.getId(), city.getId());
//        assertEquals(TestUtils.CITY_1_NAME, city.getName());
//        assertEquals(TestUtils.COUNTRY_1_NAME, city.getCountry());
//    }
//    @Test
//    public void testFindByGenericById(){
//        Optional<City> maybeCity = cityDao.findBy(CITY_1.getId(), null, null);
//        assertNotNull(maybeCity);
//        assertTrue(maybeCity.isPresent());
//        City city = maybeCity.get();
//        assertEquals(CITY_1.getId(), city.getId());
//        assertEquals(TestUtils.CITY_1_NAME, city.getName());
//        assertEquals(TestUtils.COUNTRY_1_NAME, city.getCountry());
//    }
//    @Test
//    public void testFindByGenericByIdDeleted(){
//        Optional<City> maybeCity = cityDao.findBy(CITY_DELETED.getId(), null, null);
//        assertNotNull(maybeCity);
//        assertFalse(maybeCity.isPresent());
//    }
//    @Test
//    public void testFindByGenericByName(){
//        Optional<City> maybeCity = cityDao.findBy(null, TestUtils.CITY_1_NAME, null);
//        assertNotNull(maybeCity);
//        assertTrue(maybeCity.isPresent());
//        City city = maybeCity.get();
//        assertEquals(CITY_1.getId(), city.getId());
//        assertEquals(TestUtils.CITY_1_NAME, city.getName());
//        assertEquals(TestUtils.COUNTRY_1_NAME, city.getCountry());
//    }
//    @Test
//    public void testFindByGenericByName2(){
//        Optional<City> maybeCity = cityDao.findBy(null, TestUtils.CITY_3_NAME, null);
//        assertNotNull(maybeCity);
//        assertTrue(maybeCity.isPresent());
//        City city = maybeCity.get();
//        assertEquals(TestUtils.CITY_3_NAME, city.getName());
//        assertEquals(TestUtils.COUNTRY_2_NAME, city.getCountry());
//    }
//    @Test
//    public void testFindByGenericByNameDeleted(){
//        Optional<City> maybeCity = cityDao.findBy(null, TestUtils.CITY_DELETED_NAME, null);
//        assertNotNull(maybeCity);
//        assertFalse(maybeCity.isPresent());
//    }
//    @Test
//    public void testFindByGenericByCountry(){
//        Optional<City> maybeCity = cityDao.findBy(null, null, TestUtils.COUNTRY_1_NAME);
//        assertNotNull(maybeCity);
//        assertTrue(maybeCity.isPresent());
//        City city = maybeCity.get();
//        assertTrue(List.of(TestUtils.CITY_1_NAME, TestUtils.CITY_2_NAME).contains(city.getName()));
//        assertEquals(TestUtils.COUNTRY_1_NAME, city.getCountry());
//    }
//    @Test
//    public void testFindByGenericByCountry2(){
//        Optional<City> maybeCity = cityDao.findBy(null, null, TestUtils.COUNTRY_2_NAME);
//        assertNotNull(maybeCity);
//        assertTrue(maybeCity.isPresent());
//        City city = maybeCity.get();
//        assertEquals(TestUtils.CITY_3_NAME, city.getName());
//        assertEquals(TestUtils.COUNTRY_2_NAME, city.getCountry());
//    }
//    @Test
//    public void testFindByGenericWrongId(){
//        Optional<City> maybeCity = cityDao.findBy((long)141234, null, TestUtils.COUNTRY_2_NAME);
//        assertNotNull(maybeCity);
//        assertFalse(maybeCity.isPresent());
//    }
//    @Test
//    public void testFindByGenericMissingId(){
//        Optional<City> maybeCity = cityDao.findBy((long)0, null, TestUtils.COUNTRY_2_NAME);
//        assertNotNull(maybeCity);
//        assertTrue(maybeCity.isPresent());
//        City city = maybeCity.get();
//        assertEquals(TestUtils.CITY_3_NAME, city.getName());
//        assertEquals(TestUtils.COUNTRY_2_NAME, city.getCountry());
//    }
//    @Test
//    public void testFindByGenericNoParams(){
//        Optional<City> maybeCity = cityDao.findBy(null, null, null);
//        assertNotNull(maybeCity);
//        assertTrue(maybeCity.isPresent());
//    }
//    @Test
//    public void testFindByGenericEmptyName(){
//        Optional<City> maybeCity = cityDao.findBy(null, "", null);
//        assertNotNull(maybeCity);
//        assertTrue(maybeCity.isPresent());
//    }
//    @Test
//    public void testFindByGenericEmptyCountry(){
//        Optional<City> maybeCity = cityDao.findBy(null, null, "");
//        assertNotNull(maybeCity);
//        assertTrue(maybeCity.isPresent());
//    }
//
//    @Test
//    public void testGetAllCitiesPagedOnePage(){
//        Page<City> page1 = cityDao.getAllCities(new PageParams(1, 3));
//
//        assertNotNull(page1);
//        assertEquals(1, page1.getTotalPages());
//        assertEquals(3, page1.getContent().size());
//    }
//    @Test
//    public void testGetAllCitiesPagedMultiplePages(){
//        Page<City> page1 = cityDao.getAllCities(new PageParams(1, 2));
//        Page<City> page2 = cityDao.getAllCities(new PageParams(2, 2));
//
//        assertNotNull(page1);
//        assertEquals(2, page1.getTotalPages());
//        assertEquals(2, page1.getContent().size());
//        assertNotNull(page1);
//        assertEquals(2, page2.getTotalPages());
//        assertEquals(1, page2.getContent().size());
//    }
//    @Test
//    public void testGetAllCitiesPagedNoCities(){
//        JdbcTestUtils.deleteFromTables(jdbcTemplate, TestUtils.JOURNEY_TABLE, TestUtils.USER_INTEREST_TABLE, TestUtils.USER_TABLE, TestUtils.UNIVERSITY_TABLE, TestUtils.CITY_TABLE);
//
//        Page<City> page1 = cityDao.getAllCities(new PageParams(1, 2));
//
//        assertNotNull(page1);
//        assertEquals(0, page1.getTotalPages());
//        assertEquals(0, page1.getContent().size());
//    }
//
//    @Test
//    public void testCreate(){
//        long id = cityDao.createCity(TestUtils.NEW_CITY_NAME, new Country(COUNTRY_1.getId(), null, null));
//
//        assertEquals(
//            TestUtils.TOTAL_CITIES + 1,
//            Optional.ofNullable(
//                jdbcTemplate.queryForObject("SELECT COUNT(*) FROM cities WHERE deleted = FALSE", Integer.class)
//            ).get().intValue()
//        );
//        Optional<City> maybeCity = jdbcTemplate.query(TestUtils.CITY_SELECT_BY_NAME, TestUtils.CITY_ROW_MAPPER, TestUtils.NEW_CITY_NAME).stream().findFirst();
//        assertNotNull(maybeCity);
//        assertTrue(maybeCity.isPresent());
//        assertEquals(id, maybeCity.get().getId());
//        assertEquals(TestUtils.NEW_CITY_NAME, maybeCity.get().getName());
//        assertEquals(TestUtils.COUNTRY_1_NAME, maybeCity.get().getCountry());
//    }
//    @Test(expected = DataAccessException.class)
//    public void testCreateDuplicate(){
//        cityDao.createCity(TestUtils.CITY_1_NAME, new Country(COUNTRY_1.getId(), null, null));
//    }
//    @Test(expected = DataAccessException.class)
//    public void testCreateEmpty(){
//        cityDao.createCity(null, new Country(COUNTRY_1.getId(), null, null));
//    }
//    @Test(expected = DataAccessException.class)
//    public void testCreateWrongCountry(){
//        cityDao.createCity(null, new Country(1241234, null, null));
//    }
//    @Test
//    public void testCreateDuplicateDeleted(){
//        int rowsBefore = JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.CITY_TABLE);
//
//        cityDao.createCity(TestUtils.CITY_DELETED_NAME, new Country(COUNTRY_2.getId(), null, null));
//
//        assertEquals(rowsBefore, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.CITY_TABLE));
//        assertEquals(
//            TestUtils.TOTAL_CITIES + 1,
//            Optional.ofNullable(
//                jdbcTemplate.queryForObject("SELECT COUNT(*) FROM cities WHERE deleted = FALSE", Integer.class)
//            ).get().intValue()
//        );        Optional<City> maybeCity = jdbcTemplate.query(TestUtils.CITY_SELECT_BY_NAME, TestUtils.CITY_ROW_MAPPER, TestUtils.CITY_DELETED_NAME).stream().findFirst();
//        assertNotNull(maybeCity);
//        assertTrue(maybeCity.isPresent());
//        assertEquals(TestUtils.CITY_DELETED_NAME, maybeCity.get().getName());
//        assertEquals(TestUtils.COUNTRY_2_NAME, maybeCity.get().getCountry());
//        assertEquals(CITY_DELETED.getId(), maybeCity.get().getId());
//    }
//
//    @Test
//    public void testDeleteCity(){
//        int rowsBefore = JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.CITY_TABLE);
//
//        cityDao.delete(CITY_1.getId());
//
//        assertEquals(rowsBefore, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.CITY_TABLE));
//        assertEquals(
//            TestUtils.TOTAL_CITIES - 1,
//            Optional.ofNullable(
//                jdbcTemplate.queryForObject("SELECT COUNT(*) FROM cities WHERE deleted = FALSE", Integer.class)
//            ).get().intValue()
//        );    }
//    @Test
//    public void testDeleteDeleted(){
//        int rowsBefore = JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.CITY_TABLE);
//
//        cityDao.delete(CITY_1.getId());
//
//        assertEquals(rowsBefore, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.CITY_TABLE));
//        assertEquals(
//            TestUtils.TOTAL_CITIES - 1,
//            Optional.ofNullable(
//                jdbcTemplate.queryForObject("SELECT COUNT(*) FROM cities WHERE deleted = FALSE", Integer.class)
//            ).get().intValue()
//        );    }
//    @Test
//    public void testDeleteWrong(){
//        int rowsBefore = JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.CITY_TABLE);
//
//        cityDao.delete(12341234);
//
//        assertEquals(rowsBefore, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.CITY_TABLE));
//        assertEquals(
//            TestUtils.TOTAL_CITIES,
//            Optional.ofNullable(
//                jdbcTemplate.queryForObject("SELECT COUNT(*) FROM cities WHERE deleted = FALSE", Integer.class)
//            ).get().intValue()
//        );    }
//
//    @Test
//    public void testUpdate(){
//        cityDao.updateCity(CITY_1.getId(), TestUtils.NEW_CITY_NAME, new Country(COUNTRY_1.getId(), null, null));
//
//        Optional<City> maybeCity = jdbcTemplate.query(TestUtils.CITY_SELECT_BY_ID, TestUtils.CITY_ROW_MAPPER, CITY_1.getId()).stream().findFirst();
//        assertNotNull(maybeCity);
//        assertTrue(maybeCity.isPresent());
//        assertEquals(TestUtils.NEW_CITY_NAME, maybeCity.get().getName());
//        assertEquals(TestUtils.COUNTRY_1_NAME, maybeCity.get().getCountry());
//    }
//}