package ar.edu.itba.paw.persistance;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
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
import ar.edu.itba.paw.persistence.CountryJdbcDao;

@Transactional
@Rollback
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class CountryJdbcDaoTest {

    private static Country COUNTRY_1;

    @Autowired
    private DataSource ds;

    @Autowired
    private CountryJdbcDao countryDao;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp(){
        jdbcTemplate = new JdbcTemplate(ds);

        COUNTRY_1 = jdbcTemplate.queryForObject(TestUtils.COUNTRY_SELECT_BY_CODE, TestUtils.COUNTRY_ROW_MAPPER, TestUtils.COUNTRY_1_CODE);
    }

    @Test
    public void testFindAll(){
        List<Country> countries = countryDao.findAll();
        assertNotNull(countries);
        assertEquals(TestUtils.TOTAL_COUNTRIES, countries.size());
        List<String> countryNames = List.of(TestUtils.COUNTRY_1_NAME, TestUtils.COUNTRY_2_NAME);
        List<String> countryCodes = List.of(TestUtils.COUNTRY_1_CODE, TestUtils.COUNTRY_2_CODE);
        for (Country country : countries) {
            assertTrue(countryNames.contains(country.getName()));
            assertTrue(countryCodes.contains(country.getCode()));
            if (country.getName().equals(TestUtils.COUNTRY_1_NAME)){
                assertEquals(COUNTRY_1.getId(), country.getId());
            }
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
    public void testExistsByName(){
        boolean result = countryDao.existsByName(TestUtils.COUNTRY_1_NAME);
        assertTrue(result);
    }
    @Test
    public void testExistsByNameFakeName(){
        boolean result = countryDao.existsByName("TestUtils.COUNTRY_1_NAME");
        assertFalse(result);
    }
    @Test
    public void testExistsByNameEmptyName(){
        boolean result = countryDao.existsByName("");
        assertFalse(result);
    }
    @Test
    public void testExistsByNameMissingName(){
        boolean result = countryDao.existsByName(null);
        assertFalse(result);
    }

    
    @Test
    public void testFindByName(){
        Optional<Country> result = countryDao.findByName(TestUtils.COUNTRY_1_NAME);
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(TestUtils.COUNTRY_1_NAME, result.get().getName());
        assertEquals(TestUtils.COUNTRY_1_CODE, result.get().getCode());
        assertEquals(COUNTRY_1.getId(), result.get().getId());
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