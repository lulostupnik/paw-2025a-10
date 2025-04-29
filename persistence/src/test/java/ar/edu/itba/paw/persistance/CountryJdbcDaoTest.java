package ar.edu.itba.paw.persistance;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

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

import ar.edu.itba.paw.models.Country;
import ar.edu.itba.paw.persistence.CountryJdbcDao;

@Sql(scripts = "classpath:schema.sql")
@Transactional
@Rollback
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class CountryJdbcDaoTest {

    private static final String COUNTRY_TABLE = "countries";
    private static final String COUNTRY_NAME_1 = "cuntry1";
    private static final String COUNTRY_CODE_1 = "aa";    
    private static final String COUNTRY_NAME_2 = "cuntry2";
    private static final String COUNTRY_CODE_2 = "bb";
    private static final int TOTAL_COUNTRIES = 2;
    private static long id1;

    @Autowired
    private DataSource ds;

    @Autowired
    private CountryJdbcDao countryDao;

    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert insert;

    @Before
    public void setUp(){
        jdbcTemplate = new JdbcTemplate(ds);
        insert = new SimpleJdbcInsert(ds).withTableName(COUNTRY_TABLE).usingGeneratedKeyColumns("id");
        id1 = insert.executeAndReturnKey(Map.of("name", COUNTRY_NAME_1, "code", COUNTRY_CODE_1)).longValue();
        insert.execute(Map.of("name", COUNTRY_NAME_2, "code", COUNTRY_CODE_2));
    }

    @Test
    public void testFindAll(){
        List<Country> countries = countryDao.findAll();
        assertNotNull(countries);
        assertEquals(TOTAL_COUNTRIES, countries.size());
        List<String> countryNames = List.of(COUNTRY_NAME_1, COUNTRY_NAME_2);
        List<String> countryCodes = List.of(COUNTRY_CODE_1, COUNTRY_CODE_2);
        for (Country country : countries) {
            assertTrue(countryNames.contains(country.getName()));
            assertTrue(countryCodes.contains(country.getCode()));
            if (country.getName().equals(COUNTRY_NAME_1)){
                assertEquals(id1, country.getId());
            }
        }
    }
    @Test
    public void testFindAllNoCountries(){
        JdbcTestUtils.deleteFromTables(jdbcTemplate, COUNTRY_TABLE);
        List<Country> countries = countryDao.findAll();
        assertNotNull(countries);
        assertEquals(0, countries.size());
    }

    @Test
    public void testExistsByName(){
        boolean result = countryDao.existsByName(COUNTRY_NAME_1);
        assertTrue(result);
    }
    @Test
    public void testExistsByNameFakeName(){
        boolean result = countryDao.existsByName("COUNTRY_NAME_1");
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
}