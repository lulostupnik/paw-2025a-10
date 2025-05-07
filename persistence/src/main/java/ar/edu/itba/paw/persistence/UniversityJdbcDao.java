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
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import static ar.edu.itba.paw.persistence.JdbcDaoUtils.*;

@Repository
public class UniversityJdbcDao implements UniversityDao {
    private final static Logger LOGGER = LoggerFactory.getLogger(UniversityJdbcDao.class);

    private final CityDao cityDao;
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    private final static RowMapper<University> UNIVERSITY_ROW_MAPPER = (rs, rowNum) -> new University(
            rs.getLong("university_id"),
            rs.getString("university_name"),
            rs.getString("university_abbreviation"),
            new City(
                    rs.getString("city_name"),
                    rs.getString("country_name"),
                    rs.getLong("city_id")
            )
    );

    private final static String SQL_SELECT_BASE =
            """
            SELECT
                un.name AS university_name,
                un.abbreviation AS university_abbreviation,
                un.id AS university_id,
                ci.id AS city_id,
                ci.name AS city_name,
                co.name AS country_name
            """;

    private final static String SQL_FROM_BASE =
            """
            FROM universities un
            JOIN cities ci ON un.city_id = ci.id
            JOIN countries co ON ci.country_id = co.id
            """;

    private final static String SQL_BASE = SQL_SELECT_BASE + SQL_FROM_BASE + " WHERE un.deleted = FALSE";

    private final static String SQL_FIND_BY_NAME = SQL_BASE + " AND un.name = ?";
    private final static String SQL_FIND_BY_ABBREVIATION = SQL_BASE + " AND un.abbreviation = ?";
    private final static String SQL_FIND_BY_ID = SQL_BASE + " AND un.id = ?";

    private final static String SQL_FIND_ALL = SQL_BASE + " ORDER BY un.name ";
    private final static String SQL_FIND_ALL_PAGED = SQL_FIND_ALL + " LIMIT ? OFFSET ?";

    private final static String SQL_SEARCH_CONDITIONS =
            """
             AND (
                LOWER(un.name) LIKE LOWER(?)
                OR LOWER(un.abbreviation) LIKE LOWER(?)
                OR LOWER(ci.name) LIKE LOWER(?)
                OR LOWER(co.name) LIKE LOWER(?)
             )
            """;

    private final static String SQL_SEARCH = SQL_BASE + SQL_SEARCH_CONDITIONS;
    private final static String SQL_SEARCH_PAGED = SQL_SEARCH + " ORDER BY un.id LIMIT ? OFFSET ?";

    private final static String SQL_SEARCH_COUNT = " SELECT COUNT(*) " + SQL_FROM_BASE + " WHERE un.deleted = FALSE " + SQL_SEARCH_CONDITIONS;


    @Autowired
    public UniversityJdbcDao(final CityDao cityDao, final DataSource dataSource){
        this.cityDao = cityDao;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("universities")
                .usingGeneratedKeyColumns("id");
    }

    @Override
    public Optional<University> findByName(final String name) {
        return jdbcTemplate.query(SQL_FIND_BY_NAME, UNIVERSITY_ROW_MAPPER, name).stream().findFirst();
    }

    @Override
    public Optional<University> findByAbbreviation(final String abbreviation) {
        return jdbcTemplate.query(SQL_FIND_BY_ABBREVIATION, UNIVERSITY_ROW_MAPPER, abbreviation).stream().findFirst();
    }    
    
    @Override
    public Optional<University> findByAny(final String searchString) {
        final String searchPattern = likePattern(searchString);
        return jdbcTemplate.query(SQL_SEARCH, UNIVERSITY_ROW_MAPPER, searchPattern, searchPattern, searchPattern, searchPattern).stream().findFirst();
    }

    @Override
    public List<University> getAllUniversities() {
        return jdbcTemplate.query(SQL_FIND_ALL, UNIVERSITY_ROW_MAPPER);
    }

    @Override
    public Page<University> searchBySubstring(final String substring, final int page, final int size) {
        final String searchPattern = likePattern(substring);
        final int totalItems = jdbcTemplate.queryForObject(
                SQL_SEARCH_COUNT,
                Integer.class,
                searchPattern, searchPattern, searchPattern, searchPattern
        );
        return new Page<>(
                jdbcTemplate.query(SQL_SEARCH_PAGED, UNIVERSITY_ROW_MAPPER, searchPattern, searchPattern, searchPattern, searchPattern, size, offset(page, size)),
                page,
                pageCount(totalItems, size)
        );
    }

    @Override
    public Optional<University> findById(final long id) {
        return jdbcTemplate.query(SQL_FIND_BY_ID, UNIVERSITY_ROW_MAPPER, id).stream().findFirst();
    }

    @Override
    public Page<University> getAllUniversities(final int page, final int size) {
        final int totalItems = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM universities WHERE deleted = FALSE", Integer.class);

        return new Page<>(
                jdbcTemplate.query(SQL_FIND_ALL_PAGED, UNIVERSITY_ROW_MAPPER, size, offset(page, size)),
                page,
                pageCount(totalItems, size)
        );
    }

    //FIXME: consultar con los profes -> ¿debería recibir City city o String city? ¿O que?

    @Override
    public University createUniversity(final String name, final String abbreviation, final String city) {
        LOGGER.info("Creating or reactivating university {} ({})", name, abbreviation);

        final City cityObj = cityDao.findByName(city).orElseThrow(IllegalArgumentException::new);

        int rowsUpdated = jdbcTemplate.update(
                "UPDATE universities SET deleted = FALSE, abbreviation = ?, city_id = ? WHERE name = ? AND deleted = TRUE",
                abbreviation, cityObj.getId(), name
        );

        if (rowsUpdated == 0) {
            rowsUpdated = jdbcTemplate.update(
                    "UPDATE universities SET deleted = FALSE, name = ?, city_id = ? WHERE abbreviation = ? AND deleted = TRUE",
                    name, cityObj.getId(), abbreviation
            );
        }

        if (rowsUpdated > 0) {
            LOGGER.debug("Reactivated existing deleted university");
            return findByName(name).orElseThrow(() -> new RuntimeException("Failed to retrieve reactivated university")); 
            // FIXME: extra query, use RETURNING in update -> That'll break testing because hsql doesn't like it
        }

        LOGGER.debug("No deleted university found, creating new university entry");
        final HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("name", name);
        parameters.put("abbreviation", abbreviation);
        parameters.put("city_id", cityObj.getId());
        parameters.put("deleted", false);
        final Number keys = jdbcInsert.executeAndReturnKey(parameters);
        final University uni = new University(keys.longValue(), name, abbreviation, cityObj);
        LOGGER.info("Successfully created university {}", uni);
        return uni;
    }

    @Override
    public void updateUniversity(final long id, final String name, final String abbreviation, final long cityId) {
        jdbcTemplate.update("UPDATE universities SET name = ?, abbreviation = ?, city_id = ? WHERE id = ? ", name, abbreviation, cityId, id);
        LOGGER.info("Successfully updated uni {}", id);
    }

    @Override
    public void updateUniversity(long id, String name, String abbreviation, String cityName) {
        jdbcTemplate.update("""
        UPDATE universities
        SET name = ?, abbreviation = ?, city_id = (SELECT id FROM cities WHERE name = ?)
        WHERE id = ?
        """, name, abbreviation, cityName, id);
        LOGGER.info("Successfully updated uni {}", id);
    }

    @Override
    public void delete(final long id) {
        LOGGER.info("Marking university with ID: {} as deleted", id);
        final int rowsAffected = jdbcTemplate.update("UPDATE universities SET deleted = TRUE WHERE id = ?", id);
        if (rowsAffected == 0) {
            LOGGER.warn("University deletion failed: University with ID {} not found", id);
        }
    }

}
