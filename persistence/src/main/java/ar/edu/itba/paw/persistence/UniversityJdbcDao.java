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
import java.util.Optional;
import static ar.edu.itba.paw.persistence.JdbcDaoUtils.*;

@Repository
public class UniversityJdbcDao implements UniversityDao {
    private final static Logger LOGGER = LoggerFactory.getLogger(UniversityJdbcDao.class);

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
    public UniversityJdbcDao(final DataSource dataSource){
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
    public Page<University> search(final String searchTerm, final PageParams pageParams) {
        final String searchPattern = likePattern(searchTerm);
        return executePagedQuery(
                jdbcTemplate, UNIVERSITY_ROW_MAPPER,
                SQL_SEARCH_COUNT, SQL_SEARCH_PAGED,
                pageParams,
                searchPattern, searchPattern, searchPattern, searchPattern
        );
    }

    @Override
    public Optional<University> findById(final long id) {
        return jdbcTemplate.query(SQL_FIND_BY_ID, UNIVERSITY_ROW_MAPPER, id).stream().findFirst();
    }

    @Override
    public Page<University> findAll(final PageParams pageParams) {
        return executePagedQuery(
                jdbcTemplate, UNIVERSITY_ROW_MAPPER,
                "SELECT COUNT(*) FROM universities WHERE deleted = FALSE", SQL_FIND_ALL_PAGED,
                pageParams
        );
    }


    @Override
    public University create(final String name, final String abbreviation, final City city) {

        int rowsUpdated = jdbcTemplate.update(
                "UPDATE universities SET deleted = FALSE, abbreviation = ?, city_id = ? WHERE name = ? AND deleted = TRUE",
                abbreviation, city.getId(), name
        );

        if (rowsUpdated > 0) {
            LOGGER.info("Reactivated existing deleted university");
            return findByName(name).get();
        }

        final HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("name", name);
        parameters.put("abbreviation", abbreviation);
        parameters.put("city_id", city.getId());
        parameters.put("deleted", false);
        final long id = jdbcInsert.executeAndReturnKey(parameters).longValue();
        return new University(id, name, abbreviation, city);
    }


    @Override
    public void update(final long id, final String name, final String abbreviation, final String cityName) {
        final int updatedRows = jdbcTemplate.update("""
                UPDATE universities
                SET name = ?, abbreviation = ?, city_id = (SELECT id FROM cities WHERE name = ?)
                WHERE id = ?
                """, name, abbreviation, cityName, id
        );
        if (updatedRows == 0) {
            LOGGER.warn("University update failed: University with ID {} not found", id);
        }
    }

    @Override
    public void delete(final long id) {
        final int rowsAffected = jdbcTemplate.update("UPDATE universities SET deleted = TRUE WHERE id = ?", id);
        if (rowsAffected == 0) {
            LOGGER.warn("University deletion failed: University with ID {} not found", id);
        }
    }

}
