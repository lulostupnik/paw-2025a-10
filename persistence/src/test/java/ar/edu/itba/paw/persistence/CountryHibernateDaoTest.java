package ar.edu.itba.paw.persistence;

import static org.junit.Assert.*;
import static ar.edu.itba.paw.persistence.TestUtils.*;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;

import ar.edu.itba.paw.persistence.config.TestConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.itba.paw.models.Country;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class CountryHibernateDaoTest {

    @Autowired
    private DataSource ds;

    @Autowired
    private CountryHibernateDao countryDao;

    @PersistenceContext
    private EntityManager em;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp(){
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Test
    public void testFindAll(){
        List<Country> countries = countryDao.findAll();

        assertNotNull(countries);
        assertEquals(TOTAL_COUNTRIES, countries.size());
        countries.forEach((country) ->
            assertEqualsCountry(COUNTRY_DATA.get(country.getId()), country)
        );
    }
    @Test
    public void testFindAllNoCountries(){
        deleteCountries(jdbcTemplate);

        List<Country> countries = countryDao.findAll();
        assertNotNull(countries);
        assertEquals(0, countries.size());
    }

}