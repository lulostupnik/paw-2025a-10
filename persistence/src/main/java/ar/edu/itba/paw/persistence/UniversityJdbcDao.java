package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.CityDao;
import ar.edu.itba.paw.interfaces.persistence.UniversityDao;
import ar.edu.itba.paw.models.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class UniversityJdbcDao implements UniversityDao {
    private static Logger LOGGER = LoggerFactory.getLogger(UniversityJdbcDao.class);

    private final CityDao cityDao;
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;

    private final static RowMapper<University> UNIVERSITY_ROW_MAPPER = (rs, rowNum) ->
            new University(rs.getLong("university_id"), rs.getString("university_name"), rs.getString("university_abbreviation"), new City(rs.getString("city_name"), rs.getString("country_name"), rs.getLong("city_id")));

    private final static String SELECT_CLAUSE = """
            SELECT 
                                un.name AS university_name,
                                un.abbreviation AS university_abbreviation,
                                un.id AS university_id,
                                ci.id AS city_id,
                                ci.name AS city_name,
                                co.name AS country_name
                                """;

    private final static String QUERY = SELECT_CLAUSE +

            """
                    FROM universities un\s
                    JOIN cities ci ON un.city_id = ci.id\s
                    JOIN countries co ON ci.country_id = co.id\s""";

    @Autowired
    public UniversityJdbcDao(CityDao cityDao, final DataSource dataSource, SimpleJdbcInsert simpleJdbcInsert){
        this.cityDao = cityDao;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.simpleJdbcInsert = simpleJdbcInsert;
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
        return jdbcTemplate.query(QUERY + " WHERE un.abbreviation LIKE ? OR un.name LIKE ?", UNIVERSITY_ROW_MAPPER, "%"+searchString+"%", "%"+searchString+"%").stream().findFirst();
    }

    @Override
    public List<University> getAllUniversities() {
        LOGGER.debug("Querying DB for all universities");
        return jdbcTemplate.query(QUERY + " ORDER BY un.name", UNIVERSITY_ROW_MAPPER);
    }

    @Override
    public List<University> searchBySubstring(String substring) {
        final String like = "%" + substring + "%";

        final String sql = QUERY + " WHERE LOWER(un.name) LIKE LOWER(?) OR LOWER(un.abbreviation) LIKE LOWER(?) ";

        return jdbcTemplate.query(sql, UNIVERSITY_ROW_MAPPER, like, like);
    }



    @Override
    public Optional<University> findById(long id) {
        LOGGER.debug("Querying DB for university with id {}", id);
        return jdbcTemplate.query(QUERY + " WHERE un.id = ?", UNIVERSITY_ROW_MAPPER, id).stream().findFirst();
    }

    @Override
    public Page<University> getAllUniversities(int page, int size) {
        int offset = (page - 1) * size;
        StringBuilder query = new StringBuilder(SELECT_CLAUSE);
        query.append(" FROM (SELECT * FROM universities LIMIT ? OFFSET ?) un")
                .append(" JOIN cities ci ON un.city_id = ci.id")
                .append(" JOIN countries co ON ci.country_id = co.id");
        return new Page<>(jdbcTemplate.query(query.toString(), UNIVERSITY_ROW_MAPPER, size, offset), page);
    }

    @Override
    public University createUniversity(String name, String abbreviation, String city) {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("name", name);
        parameters.put("abbreviation", abbreviation);
        parameters.put("city", city);
        parameters.put("deleted", false);  // Establecer el valor de 'deleted' como 'false'
        final Number keys = simpleJdbcInsert.executeAndReturnKey(parameters);
        LOGGER.debug("Successfully created uni {}", keys.longValue());
        return new University(keys.longValue(), name, abbreviation, cityDao.findByName(city).get());
    }

}

