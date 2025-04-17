package ar.edu.itba.paw.persistence;

import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import ar.edu.itba.paw.interfaces.persistence.CountryDao;
import ar.edu.itba.paw.models.Country;

@Repository
public class CountryJdbcDao implements CountryDao {

        private static Logger LOGGER = LoggerFactory.getLogger(CountryJdbcDao.class);

        private final JdbcTemplate jdbcTemplate;

        private final static RowMapper<Country> COUNTRY_ROW_MAPPER = (rs, rowNum) -> new Country(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("code")
        );

        private final static String QUERY = "SELECT * FROM countries";

        @Autowired
        public CountryJdbcDao(DataSource dataSource) {
            this.jdbcTemplate = new JdbcTemplate(dataSource);
        }

        @Override
        public List<Country> findAll() {
            LOGGER.debug("Querying DB for all countries");
            return jdbcTemplate.query(QUERY, COUNTRY_ROW_MAPPER);
        }

        @Override
        public Boolean existsByName(String name) {
            LOGGER.debug("Querying DB for country {}", name);
            return jdbcTemplate.queryForObject("SELECT EXISTS(SELECT 1 FROM countries WHERE name = ?)", Boolean.class, name);
        }
}
