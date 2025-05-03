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

@Repository
public class UniversityJdbcDao implements UniversityDao {
    private final static Logger LOGGER = LoggerFactory.getLogger(UniversityJdbcDao.class);

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

    private final static String QUERY = SELECT_CLAUSE + """
                    FROM universities un
                    JOIN cities ci ON un.city_id = ci.id
                    JOIN countries co ON ci.country_id = co.id
                    WHERE un.deleted = FALSE
                    """;

    @Autowired
    public UniversityJdbcDao(CityDao cityDao, final DataSource dataSource){
        this.cityDao = cityDao;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("universities")
                .usingGeneratedKeyColumns("id");
    }

    @Override
    public Optional<University> findByName(String name) {
        return jdbcTemplate.query(QUERY + " AND un.name = ?", UNIVERSITY_ROW_MAPPER, name).stream().findFirst();
    }

    @Override
    public Optional<University> findByAbbreviation(String abbreviation) {
        return jdbcTemplate.query(QUERY + " AND un.abbreviation = ?", UNIVERSITY_ROW_MAPPER, abbreviation).stream().findFirst();
    }    
    
    @Override
    public Optional<University> findByAny(String searchString) {
        return jdbcTemplate.query(QUERY + " AND (un.abbreviation LIKE ? OR un.name LIKE ?)", UNIVERSITY_ROW_MAPPER, "%"+searchString+"%", "%"+searchString+"%").stream().findFirst();
    }

    @Override
    public List<University> getAllUniversities() {
        return jdbcTemplate.query(QUERY + " ORDER BY un.name", UNIVERSITY_ROW_MAPPER);
    }

    @Override
    public Page<University> searchBySubstring(String substring, int page, int size) {
        final String like = "%" + substring + "%";

        final String sql = QUERY + " AND (LOWER(un.name) LIKE LOWER(?) OR LOWER(un.abbreviation) LIKE LOWER(?)) LIMIT ? OFFSET ?";

        int totalItems = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM universities WHERE deleted = FALSE AND (LOWER(name) LIKE LOWER(?) OR LOWER(abbreviation) LIKE LOWER(?))", Integer.class, like, like);
        return new Page<>(
                jdbcTemplate.query(sql, UNIVERSITY_ROW_MAPPER, like, like, size, (page - 1) * size),
                page,
                (int) Math.ceil((double) totalItems / size)
        );
    }



    @Override
    public Optional<University> findById(long id) {
        return jdbcTemplate.query(QUERY + " AND un.id = ?", UNIVERSITY_ROW_MAPPER, id).stream().findFirst();
    }

    @Override
    public Page<University> getAllUniversities(int page, int size) {
        StringBuilder query = new StringBuilder(SELECT_CLAUSE);
        query.append(" FROM (SELECT * FROM universities WHERE deleted = FALSE LIMIT ? OFFSET ?) un")
                .append(" JOIN cities ci ON un.city_id = ci.id")
                .append(" JOIN countries co ON ci.country_id = co.id");
        int totalItems = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM universities WHERE deleted = FALSE", Integer.class);

        return new Page<>(
                jdbcTemplate.query(query.toString(), UNIVERSITY_ROW_MAPPER, size, (page - 1) * size),
                page,
                (int) Math.ceil((double) totalItems / size)
        );
    }

    //FIXME: consultar con los profes -> ¿debería recibir City city o String city? ¿O que?

    @Override
    public University createUniversity(String name, String abbreviation, String city) {
        LOGGER.debug("Creating or reactivating university {} ({})", name, abbreviation);

        City cityObj = cityDao.findByName(city).orElseThrow(IllegalArgumentException::new);

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
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("name", name);
        parameters.put("abbreviation", abbreviation);
        parameters.put("city_id", cityObj.getId());
        parameters.put("deleted", false);
        Number keys = simpleJdbcInsert.executeAndReturnKey(parameters);
        LOGGER.debug("Successfully created university {}", keys.longValue());
        return new University(keys.longValue(), name, abbreviation, cityObj);
    }

    @Override
    public void updateUniversity(long id, String name, String abbreviation, long cityId) {
        String sql = "UPDATE universities SET name = ?, abbreviation = ?, city_id = ? WHERE id = ? ";
        jdbcTemplate.update(sql, name, abbreviation, cityId, id);
        LOGGER.debug("Successfully updated uni {}", id);
    }

    @Override
    public void delete(long id) {
        LOGGER.debug("Marking university with ID: {} as deleted", id);
        int rowsAffected = jdbcTemplate.update("UPDATE universities SET deleted = TRUE WHERE id = ?", id);
        if (rowsAffected == 0) {
            LOGGER.warn("University deletion failed: University with ID {} not found", id);
        }
    }

}
