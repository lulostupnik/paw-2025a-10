package ar.edu.itba.paw.persistence;

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
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.itba.paw.models.Country;

@Transactional
@Rollback
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class CountryJdbcDaoTest {

    private static Country COUNTRY_1;
    private static Country COUNTRY_2;

    @Autowired
    private DataSource ds;

    @Autowired
    private CountryJdbcDao countryDao;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp(){
        jdbcTemplate = new JdbcTemplate(ds);

        COUNTRY_1 = jdbcTemplate.queryForObject(TestUtils.COUNTRY_SELECT_BY_CODE, TestUtils.COUNTRY_ROW_MAPPER, TestUtils.COUNTRY_1_CODE);
        COUNTRY_2 = jdbcTemplate.queryForObject(TestUtils.COUNTRY_SELECT_BY_CODE, TestUtils.COUNTRY_ROW_MAPPER, TestUtils.COUNTRY_2_CODE);
    }

    @Test
    public void testFindAll(){
        List<Country> countries = countryDao.findAll();

        assertNotNull(countries);
        assertEquals(TestUtils.TOTAL_COUNTRIES, countries.size());
        Map<Long, Country> countryData = Map.of(COUNTRY_1.getId(), COUNTRY_1, COUNTRY_2.getId(), COUNTRY_2);
        for (Country country : countries) {
            TestUtils.assertEqualsCountry(countryData.get(country.getId()), country);
        }
    }
    @Test
    public void testFindAllNoCountries(){
        TestUtils.deleteCountries(jdbcTemplate);

        List<Country> countries = countryDao.findAll();
        assertNotNull(countries);
        assertEquals(0, countries.size());
    }


    @Test
    public void testFindByName(){
        Optional<Country> result = countryDao.findByName(TestUtils.COUNTRY_1_NAME);

        assertNotNull(result);
        assertTrue(result.isPresent());
        TestUtils.assertEqualsCountry(COUNTRY_1, result.get());
    }
    @Test
    public void testFindByNameFakeName(){
        Optional<Country> result = countryDao.findByName("TestUtils.COUNTRY_1_NAME");

        assertNotNull(result);
        assertFalse(result.isPresent());    
    }
    @Test
    public void testFindByNameEmptyName(){
        Optional<Country> result = countryDao.findByName("");

        assertNotNull(result);
        assertFalse(result.isPresent()); 
    }
    @Test
    public void testFindByNameMissingName(){
        Optional<Country> result = countryDao.findByName(null);
        
        assertNotNull(result);
        assertFalse(result.isPresent()); 
    }
}