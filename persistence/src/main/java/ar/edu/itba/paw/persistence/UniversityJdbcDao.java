package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.UniversityDao;
import ar.edu.itba.paw.models.City;
import ar.edu.itba.paw.models.CursorPage;
import ar.edu.itba.paw.models.University;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class UniversityJdbcDao implements UniversityDao {
    private static Logger LOGGER = LoggerFactory.getLogger(UniversityJdbcDao.class);

    private final JdbcTemplate jdbcTemplate;

    private final static RowMapper<University> UNIVERSITY_ROW_MAPPER = (rs, rowNum) ->
            new University(rs.getLong("university_id"), rs.getString("university_name"), rs.getString("university_abbreviation"), new City(rs.getString("city_name"), rs.getString("country_name"), rs.getLong("city_id")));

    private final static String QUERY =
            """
                    SELECT\s
                    un.name AS university_name,\s
                    un.abbreviation AS university_abbreviation,\s
                    un.id AS university_id,\s
                    ci.id AS city_id,\s
                    ci.name AS city_name,\s
                    co.name AS country_name\s
                    FROM universities un\s
                    JOIN cities ci ON un.city_id = ci.id\s
                    JOIN countries co ON ci.country_id = co.id\s""";

    @Autowired
    public UniversityJdbcDao(final DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public Optional<University> findByName(String name) {
        LOGGER.debug("Querying DB for university name {}", name);
        return jdbcTemplate.query(QUERY + " WHERE un.name = ?", UNIVERSITY_ROW_MAPPER, name).stream().findFirst();
    }

    @Override
    public Optional<University> findByAbbreviation(String abbreviation) {
        LOGGER.debug("Querying DB for university abbreviation {}", abbreviation);
        return jdbcTemplate.query(QUERY + " WHERE un.abbreviation = ?", UNIVERSITY_ROW_MAPPER, abbreviation).stream().findFirst();
    }    
    
    @Override
    public Optional<University> findByAny(String searchString) {
        LOGGER.debug("Querying DB for university like {}", searchString);
        return jdbcTemplate.query(QUERY + " WHERE un.abbreviation LIKE ? OR un.name LIKE ?", UNIVERSITY_ROW_MAPPER, searchString, searchString).stream().findFirst();
    }

    @Override
    public List<University> getAllUniversities() {
        LOGGER.debug("Querying DB for all universities");
        return jdbcTemplate.query(QUERY + " ORDER BY un.name", UNIVERSITY_ROW_MAPPER);
    }

    @Override
    public List<University> searchBySubstring(String substring) {
        final String like = "%" + substring + "%";

        final String sql = QUERY + " WHERE (un.name ILIKE ? OR un.abbreviation ILIKE ?) ";

        return jdbcTemplate.query(sql, UNIVERSITY_ROW_MAPPER, like, like);
    }

    @Override
    public Optional<University> findById(long id) {
        LOGGER.debug("Querying DB for university with id {}", id);
        return jdbcTemplate.query(QUERY + " WHERE un.id = ?", UNIVERSITY_ROW_MAPPER, id).stream().findFirst();
    }

}
