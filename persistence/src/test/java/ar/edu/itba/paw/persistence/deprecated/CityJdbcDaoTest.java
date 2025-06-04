package ar.edu.itba.paw.persistence.deprecated;
//package ar.edu.itba.paw.persistence;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertFalse;
//import static org.junit.Assert.assertNotNull;
//import static org.junit.Assert.assertTrue;
//
//import java.util.List;
//import java.util.Optional;
//
//import javax.sql.DataSource;
//
//import ar.edu.itba.paw.persistence.config.TestConfig;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.dao.DataAccessException;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.test.annotation.Rollback;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//import org.springframework.test.jdbc.JdbcTestUtils;
//import org.springframework.transaction.annotation.Transactional;
//
//import ar.edu.itba.paw.models.City;
//import ar.edu.itba.paw.models.Country;
//import ar.edu.itba.paw.models.Page;
//
//@Transactional
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(classes = TestConfig.class)
//public class CityJdbcDaoTest {
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
//    }
//
//    @Test
//    public void testFindByName(){
//        Optional<City> maybeCity = cityDao.findByName(TestUtils.CITY_1_NAME);
//
//        assertNotNull(maybeCity);
//        assertTrue(maybeCity.isPresent());
//        TestUtils.assertEqualsCity(TestUtils.CITY_1, maybeCity.get());
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
//    public void testFindAll(){
//        List<City> cities = cityDao.getAllCities();
//
//        assertNotNull(cities);
//        assertEquals(TestUtils.TOTAL_CITIES, cities.size());
//        for (City city : cities){
//            TestUtils.assertEqualsCity(TestUtils.CITY_DATA.get(city.getId()), city);
//        }
//    }
//    @Test
//    public void testFindAllCitiesNo(){
//        TestUtils.deleteCities(jdbcTemplate);
//
//        List<City> cities = cityDao.getAllCities();
//
//        assertNotNull(cities);
//        assertEquals(0, cities.size());
//    }
//
//    @Test
//    public void testSearchNoFiltering(){
//        Page<City> page1 = cityDao.search(TestUtils.CITY_1_NAME.substring(0, 3), TestUtils.PAGE_1_BIG);
//
//        assertNotNull(page1);
//        assertEquals(1, page1.getTotalPages());
//        assertNotNull(page1.getContent());
//        assertEquals(TestUtils.TOTAL_CITIES, page1.getContent().size());
//        for (City city : page1.getContent()){
//            TestUtils.assertEqualsCity(TestUtils.CITY_DATA.get(city.getId()), city);
//        }
//    }
//    @Test
//    public void testSearchFiltering(){
//        Page<City> page1 = cityDao.search(TestUtils.CITY_1_NAME.substring(TestUtils.CITY_1_NAME.length()-1, TestUtils.CITY_1_NAME.length()), TestUtils.PAGE_1_BIG);
//
//        assertNotNull(page1);
//        assertEquals(1, page1.getTotalPages());
//        assertNotNull(page1.getContent());
//        assertEquals(1, page1.getContent().size());
//        TestUtils.assertEqualsCity(TestUtils.CITY_1, page1.getContent().get(0));
//    }
//    @Test
//    public void testSearchEmpty(){
//        Page<City> page1 = cityDao.search("", TestUtils.PAGE_1_BIG);
//
//        assertNotNull(page1);
//        assertEquals(1, page1.getTotalPages());
//        assertNotNull(page1.getContent());
//        assertEquals(TestUtils.TOTAL_CITIES, page1.getContent().size());
//        for (City city : page1.getContent()){
//            TestUtils.assertEqualsCity(TestUtils.CITY_DATA.get(city.getId()), city);
//        }
//    }
//    @Test
//    public void testSearchDeleted(){
//        Page<City> page1 = cityDao.search(TestUtils.CITY_DELETED_NAME, TestUtils.PAGE_1_BIG);
//
//        assertNotNull(page1);
//        assertEquals(0, page1.getTotalPages());
//        assertNotNull(page1.getContent());
//        assertEquals(0, page1.getContent().size());
//    }
//    @Test
//    public void testSearchMissing(){
//        Page<City> page1 = cityDao.search(null, TestUtils.PAGE_1_BIG);
//
//        assertNotNull(page1);
//        assertEquals(1, page1.getTotalPages());
//        assertNotNull(page1.getContent());
//        assertEquals(TestUtils.TOTAL_CITIES, page1.getContent().size());
//        for (City city : page1.getContent()){
//            TestUtils.assertEqualsCity(TestUtils.CITY_DATA.get(city.getId()), city);
//        }
//    }
//    @Test
//    public void testSearchPageOne(){
//        Page<City> page1 = cityDao.search("", TestUtils.PAGE_1_DEFAULT);
//
//        assertNotNull(page1);
//        assertEquals(2, page1.getTotalPages());
//        assertNotNull(page1.getContent());
//        assertEquals(2, page1.getContent().size());
//    }
//
//    @Test
//    public void testSearchPageTwo(){
//        Page<City> page2 = cityDao.search("", TestUtils.PAGE_2_DEFAULT);
//        assertNotNull(page2);
//        assertEquals(2, page2.getTotalPages());
//        assertNotNull(page2.getContent());
//        assertEquals(1, page2.getContent().size());
//    }
//
//    @Test
//    public void testFindByGenericById(){
//        Optional<City> maybeCity = cityDao.findById(TestUtils.CITY_1_ID);
//
//        assertNotNull(maybeCity);
//        assertTrue(maybeCity.isPresent());
//        TestUtils.assertEqualsCity(TestUtils.CITY_1, maybeCity.get());
//    }
//    @Test
//    public void testFindByGenericByIdDeleted(){
//        Optional<City> maybeCity = cityDao.findById(TestUtils.CITY_DELETED_ID);
//
//        assertNotNull(maybeCity);
//        assertFalse(maybeCity.isPresent());
//    }
//
//    @Test
//    public void testFindAllPagedOnePage(){
//        Page<City> page1 = cityDao.findAll(TestUtils.PAGE_1_BIG);
//
//        assertNotNull(page1);
//        assertEquals(1, page1.getTotalPages());
//        assertEquals(TestUtils.TOTAL_CITIES, page1.getContent().size());
//        for (City city : page1.getContent()){
//            TestUtils.assertEqualsCity(TestUtils.CITY_DATA.get(city.getId()), city);
//        }
//    }
//
//    @Test
//    public void testFindAllCitiesPagedNo(){
//        TestUtils.deleteCities(jdbcTemplate);
//
//        Page<City> page1 = cityDao.findAll(TestUtils.PAGE_1_DEFAULT);
//
//        assertNotNull(page1);
//        assertEquals(0, page1.getTotalPages());
//        assertEquals(0, page1.getContent().size());
//    }
//
//    @Test
//    public void testCreate(){
//        City city = cityDao.create(TestUtils.NEW_CITY_NAME, TestUtils.COUNTRY_1);
//
//        assertEquals(
//            TestUtils.TOTAL_CITIES + 1,
//            Optional.ofNullable(
//                jdbcTemplate.queryForObject(TestUtils.CITIES_COUNT_NOT_DELETED, Integer.class)
//            ).get().intValue()
//        );
//        TestUtils.assertEqualsCity(new City(TestUtils.NEW_CITY_NAME, TestUtils.COUNTRY_1_NAME, city.getId()), city);
//    }
//    @Test(expected = DataAccessException.class)
//    public void testCreateDuplicate(){
//        cityDao.create(TestUtils.CITY_1_NAME, TestUtils.COUNTRY_1);
//    }
//    @Test(expected = DataAccessException.class)
//    public void testCreateEmpty(){
//        cityDao.create(null, TestUtils.COUNTRY_1);
//    }
//    @Test(expected = DataAccessException.class)
//    public void testCreateWrongCountry(){
//        cityDao.create(null, new Country(1241234, null, null));
//    }
//    @Test
//    public void testCreateDuplicateDeleted(){
//        int rowsBefore = JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.CITY_TABLE);
//
//        cityDao.create(TestUtils.CITY_DELETED_NAME, TestUtils.COUNTRY_2);
//
//        assertEquals(rowsBefore, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.CITY_TABLE));
//        assertEquals(
//            TestUtils.TOTAL_CITIES + 1,
//            Optional.ofNullable(
//                jdbcTemplate.queryForObject(TestUtils.CITIES_COUNT_NOT_DELETED, Integer.class)
//            ).get().intValue()
//        );
//        City city = jdbcTemplate.queryForObject(TestUtils.CITY_SELECT_BY_NAME, TestUtils.CITY_ROW_MAPPER, TestUtils.CITY_DELETED_NAME);
//        TestUtils.assertEqualsCity(TestUtils.CITY_DELETED, city);
//    }
//
//    @Test
//    public void testDeleteCity(){
//        int rowsBefore = JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.CITY_TABLE);
//
//        cityDao.delete(TestUtils.CITY_1_ID);
//
//        assertEquals(rowsBefore, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.CITY_TABLE));
//        assertEquals(
//            TestUtils.TOTAL_CITIES - 1,
//            Optional.ofNullable(
//                jdbcTemplate.queryForObject(TestUtils.CITIES_COUNT_NOT_DELETED, Integer.class)
//            ).get().intValue()
//        );
//    }
//    @Test
//    public void testDeleteDeleted(){
//        int rowsBefore = JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.CITY_TABLE);
//
//        cityDao.delete(TestUtils.CITY_1_ID);
//
//        assertEquals(rowsBefore, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.CITY_TABLE));
//        assertEquals(
//            TestUtils.TOTAL_CITIES - 1,
//            Optional.ofNullable(
//                jdbcTemplate.queryForObject(TestUtils.CITIES_COUNT_NOT_DELETED, Integer.class)
//            ).get().intValue()
//        );
//    }
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
//                jdbcTemplate.queryForObject(TestUtils.CITIES_COUNT_NOT_DELETED, Integer.class)
//            ).get().intValue()
//        );
//    }
//
//    @Test
//    public void testUpdate(){
//        cityDao.update(TestUtils.CITY_1_ID, TestUtils.NEW_CITY_NAME, TestUtils.COUNTRY_1);
//
//        City city = jdbcTemplate.queryForObject(TestUtils.CITY_SELECT_BY_ID, TestUtils.CITY_ROW_MAPPER, TestUtils.CITY_1_ID);
//        TestUtils.assertEqualsCity(new City(TestUtils.NEW_CITY_NAME, TestUtils.COUNTRY_1_NAME, TestUtils.CITY_1_ID), city);
//    }
//    @Test
//    public void testUpdateWrongId(){
//        cityDao.update(12341234l, TestUtils.NEW_CITY_NAME, TestUtils.COUNTRY_1);
//
//        City city = jdbcTemplate.queryForObject(TestUtils.CITY_SELECT_BY_ID, TestUtils.CITY_ROW_MAPPER, TestUtils.CITY_1_ID);
//        TestUtils.assertEqualsCity(TestUtils.CITY_1, city);
//    }
//}