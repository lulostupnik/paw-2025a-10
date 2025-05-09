package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.CityDao;
import ar.edu.itba.paw.models.City;
import ar.edu.itba.paw.models.Country;
import ar.edu.itba.paw.models.Page;
import ar.edu.itba.paw.models.PageParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;
import java.util.*;
import static ar.edu.itba.paw.persistence.JdbcDaoUtils.*;

@Repository
public class CityJdbcDao implements CityDao {

    private final static Logger LOGGER = LoggerFactory.getLogger(CityJdbcDao.class);

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    private final static RowMapper<City> CITY_ROW_MAPPER = (rs, rowNum) -> new City(
            rs.getString("city_name"),
            rs.getString("country_name"),
            rs.getLong("city_id")
    );

    private final static String SQL_BASE =
            """
            SELECT ci.name as city_name, ci.id as city_id, co.name as country_name
            FROM cities ci, countries co
            WHERE ci.country_id = co.id AND ci.deleted = FALSE
            """;

    private final static String SQL_FIND_BY_NAME = SQL_BASE + " AND ci.name = ?";

    private final static String SQL_FIND_ALL = SQL_BASE + " ORDER BY ci.name";

    private final static String SQL_FIND_ALL_PAGED = SQL_FIND_ALL + " LIMIT ? OFFSET ?";
    private final static String SQL_FIND_BY_COUNTRY = SQL_BASE + "AND co.name = ?";
    private final static String SQL_SEARCH_PAGED = SQL_BASE + " AND (LOWER(ci.name) LIKE LOWER(?) OR LOWER(co.name) LIKE LOWER(?)) LIMIT ? OFFSET ? ";

    // private static final RowMappeFr<City> SIMPLE_CITY_ROW_MAPPER = (rs, rowNum) -> new City(rs.getString("name"), rs.getString("country"), rs.getLong("id"));

    @Autowired
    public CityJdbcDao(final DataSource dataSource) {

        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("cities")
                .usingGeneratedKeyColumns("id");
    }

    @Override
    public Optional<City> findBy(final Long id, final String name, final String country) {
        final StringBuilder queryBuilder = new StringBuilder();
        final List<Object> params = new ArrayList<>();

        queryBuilder.append(SQL_BASE);

        if (id != null && id > 0) {
            LOGGER.debug("Search parameter city ID: {}", id);
            queryBuilder.append(" AND ci.id = ? ");
            params.add(id);
        }

        if (name != null && !name.isEmpty()) {
            LOGGER.debug("Search parameter city name: {}", name);
            queryBuilder.append(" AND ci.name = ? ");
            params.add(name);
        }

        if (country != null && !country.isEmpty()) {
            LOGGER.debug("Search parameter country name: {}", country);
            queryBuilder.append(" AND co.name = ? ");
            params.add(country);
        }

        final Optional<City> city = jdbcTemplate.query(
                queryBuilder.toString(),
                CITY_ROW_MAPPER,
                params.toArray()
        ).stream().findFirst();

        if (city.isPresent()) {
            LOGGER.info("Found city {}", city.get());
        } else {
            LOGGER.info("City {}, {} ({}) not found", name, country, id);
        }
        return city;
    }

    @Override
    public Optional<City> findByName(final String name) {
        return jdbcTemplate.query(SQL_FIND_BY_NAME, CITY_ROW_MAPPER, name)
                .stream().findFirst();

    }

    @Override
    public List<City> getAllCities() {
        return jdbcTemplate.query(SQL_FIND_ALL, CITY_ROW_MAPPER);
    }

    @Override
    public Page<City> getAllCities(PageParams pageParams) {
        final int totalCities = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM cities WHERE deleted = FALSE ", Integer.class);
        return new Page<>(
                jdbcTemplate.query(SQL_FIND_ALL_PAGED, CITY_ROW_MAPPER, pageParams.getSize(), offset(pageParams)),
                pageParams.getPage(),
                pageCount(totalCities, pageParams.getSize())
        );
    }


    @Override
    public void updateCity(final long id, final String name, final Country country) {
        LOGGER.info("Updating city id '{}' and name '{}', country {}",id,name, country);
        final int rowsAffected = jdbcTemplate.update("UPDATE cities SET name = ?, country_id = ? WHERE id = ?", name, country.getId(), id);
        if (rowsAffected == 0) {
            LOGGER.warn("City update failed: City with ID {} not found", id);
        }
    }

    @Override
    public long createCity(final String name, final Country country) {
        LOGGER.debug("Creating or reactivating city {} in country {}", name, country.getName());

        final int rowsUpdated = jdbcTemplate.update(
                "UPDATE cities SET deleted = FALSE WHERE name = ? AND country_id = ? AND deleted = TRUE",
                name, country.getId()
        );

        if (rowsUpdated > 0) {
            LOGGER.info("City {} reactivated", name);
            return jdbcTemplate.queryForObject(
                    "SELECT id FROM cities WHERE name = ? AND country_id = ?",
                    Long.class,
                    name, country.getId()
            );
        }

        final Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("country_id", country.getId());
        params.put("deleted", false);
        final long id = jdbcInsert.executeAndReturnKey(params).longValue();
        LOGGER.info("Registered city '{}', '{}' with id {}", name, country, id);
        return id;
    }

    @Override
    public void delete(final long id) {
        LOGGER.info("Marking city with ID: {} as deleted", id);
        final int rowsAffected = jdbcTemplate.update("UPDATE cities SET deleted = TRUE WHERE id = ?", id);
        if (rowsAffected == 0) {
            LOGGER.warn("City deletion failed: City with ID {} not found", id);
        }
    }

    public List<City> findAll() {
        return jdbcTemplate.query(SQL_BASE, CITY_ROW_MAPPER);
    }

    @Override
    public List<City> findAllByCountry(final String country) {
        return jdbcTemplate.query(SQL_FIND_BY_COUNTRY, CITY_ROW_MAPPER, country);
    }

    @Override
    public Page<City> searchBySubstring(final String substring, PageParams pageParams) {
        final String searchPattern = likePattern(substring);
        final int totalItems = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM cities ci JOIN countries co ON ci.country_id = co.id
                WHERE deleted = FALSE AND (
                    LOWER(ci.name) LIKE LOWER(?)
                    OR LOWER(co.name) LIKE LOWER(?)
                    )
                """,
                Integer.class,
                searchPattern, searchPattern
        );
        return new Page<>(
                jdbcTemplate.query(SQL_SEARCH_PAGED, CITY_ROW_MAPPER, searchPattern, searchPattern, pageParams.getSize(), offset(pageParams)),
                pageParams.getPage(),
                pageCount(totalItems, pageParams.getSize())
        );
    }

}
