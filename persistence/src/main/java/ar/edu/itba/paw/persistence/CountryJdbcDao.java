package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.CountryDao;
import ar.edu.itba.paw.models.Country;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class CountryJdbcDao implements CountryDao {

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
            return jdbcTemplate.query(QUERY, COUNTRY_ROW_MAPPER);
        }

        @Override
        public Boolean existsByName(String name) {
            return jdbcTemplate.queryForObject("SELECT EXISTS(SELECT 1 FROM countries WHERE name = ?)", Boolean.class, name);
        }
}
