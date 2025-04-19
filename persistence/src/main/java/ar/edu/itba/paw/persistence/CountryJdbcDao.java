package ar.edu.itba.paw.persistence;

import java.util.List;

import javax.sql.DataSource;

import ar.edu.itba.paw.models.CursorPage;
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


        @Override
        public CursorPage<Country, Long> findAll(Long cursor, int limit) {
            LOGGER.debug("Querying DB for all countries with cursor {} and limit {}", cursor, limit);

            String sql = QUERY + (cursor != null ? " WHERE id > ? " : "") + " ORDER BY id LIMIT ?";

            List<Country> countryList;
            if (cursor != null) {
                countryList = jdbcTemplate.query(sql, COUNTRY_ROW_MAPPER, cursor, limit + 1);
            } else {
                countryList = jdbcTemplate.query(sql, COUNTRY_ROW_MAPPER, limit + 1);
            }

            boolean hasNext = countryList != null && countryList.size() > limit;
            Long nextCursor = null;

            if (hasNext) {
                countryList.removeLast();
                nextCursor = countryList.getLast().getId();
            }

            return new CursorPage<>(countryList, nextCursor, hasNext);
        }

        @Override
        public CursorPage<Country, Long> findBySubstring(String substring, Long cursor, int limit) {
            LOGGER.debug("Querying DB for countries with name containing '{}' with cursor {} and limit {}", substring, cursor, limit);

            String searchPattern = "%" + substring + "%";
            String sql = QUERY + " WHERE name ILIKE ?" +
                    (cursor != null ? " AND id > ? " : "") +
                    " ORDER BY id LIMIT ?";

            List<Country> countryList;
            if (cursor != null) {
                countryList = jdbcTemplate.query(sql, COUNTRY_ROW_MAPPER, searchPattern, cursor, limit + 1);
            } else {
                countryList = jdbcTemplate.query(sql, COUNTRY_ROW_MAPPER, searchPattern, limit + 1);
            }

            boolean hasNext = countryList != null && countryList.size() > limit;
            Long nextCursor = null;

            if (hasNext) {
                countryList.removeLast();
                nextCursor = countryList.getLast().getId();
            }

            return new CursorPage<>(countryList, nextCursor, hasNext);
        }
}
