package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.UniversityDao;
import ar.edu.itba.paw.models.City;
import ar.edu.itba.paw.models.University;
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
    private final JdbcTemplate jdbcTemplate;

    private final static RowMapper<University> UNIVERSITY_ROW_MAPPER = (rs, rowNum) ->
            new University(rs.getLong("university_id"), rs.getString("university_name"), rs.getString("university_abbreviation"), new City(rs.getString("city_name"), rs.getString("country_name"), rs.getLong("city_id")));

    private final static String QUERY =
                    "SELECT " +
                    "un.name AS university_name, \n" +
                    "un.abbreviation AS university_abbreviation, \n" +
                    "un.id AS university_id, \n" +
                    "ci.id AS city_id, \n" +
                    "ci.name AS city_name, \n" +
                    "co.name AS country_name \n" +
                    "FROM universities un \n" +
                    "JOIN cities ci ON un.city_id = ci.id \n" +
                    "JOIN countries co ON ci.country_id = co.id ";

    @Autowired
    public UniversityJdbcDao(final DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public Optional<University> findByName(String name) {
        return jdbcTemplate.query(QUERY + " WHERE un.name = ?", UNIVERSITY_ROW_MAPPER, name).stream().findFirst();
    }

    @Override
    public Optional<University> findByAbbreviation(String abbreviation) {
        return jdbcTemplate.query(QUERY + " WHERE un.abbreviation = ?", UNIVERSITY_ROW_MAPPER, abbreviation).stream().findFirst();
    }    
    
    @Override
    public Optional<University> findByAny(String searchString) {
        return jdbcTemplate.query(QUERY + " WHERE un.abbreviation LIKE ? OR un.name LIKE ?", UNIVERSITY_ROW_MAPPER, searchString, searchString).stream().findFirst();
    }

    @Override
    public List<University> getAllUniversities() {
        return jdbcTemplate.query(QUERY + " ORDER BY un.name", UNIVERSITY_ROW_MAPPER);
    }
}
