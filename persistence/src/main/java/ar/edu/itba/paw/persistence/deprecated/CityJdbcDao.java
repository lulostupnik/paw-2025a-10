//package ar.edu.itba.paw.persistence;
//
//import ar.edu.itba.paw.interfaces.persistence.CityDao;
//import ar.edu.itba.paw.models.City;
//import ar.edu.itba.paw.models.Country;
//import ar.edu.itba.paw.models.Page;
//import ar.edu.itba.paw.models.PageParams;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.jdbc.core.RowMapper;
//import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
//import org.springframework.stereotype.Repository;
//import javax.sql.DataSource;
//import java.util.*;
//import static ar.edu.itba.paw.persistence.JdbcDaoUtils.*;
//
//@Deprecated
//public class CityJdbcDao implements CityDao {
//
//    private final static Logger LOGGER = LoggerFactory.getLogger(CityJdbcDao.class);
//
//    private final JdbcTemplate jdbcTemplate;
//    private final SimpleJdbcInsert jdbcInsert;
//
//    private final static RowMapper<City> CITY_ROW_MAPPER = (rs, rowNum) -> new City(
//            rs.getString("city_name"),
//            rs.getString("country_name"),
//            rs.getLong("city_id")
//    );
//
//    private final static String SQL_BASE =
//            """
//            SELECT ci.name as city_name, ci.id as city_id, co.name as country_name
//            FROM cities ci, countries co
//            WHERE ci.country_id = co.id AND ci.deleted = FALSE
//            """;
//
//    private final static String SQL_FIND_BY_NAME = SQL_BASE + " AND ci.name = ?";
//    private final static String SQL_FIND_BY_ID = SQL_BASE + " AND ci.id = ?";
//
//    private final static String SQL_FIND_ALL = SQL_BASE + " ORDER BY ci.name";
//
//    private final static String SQL_FIND_ALL_PAGED = SQL_FIND_ALL + " LIMIT ? OFFSET ?";
//    private final static String SQL_SEARCH_PAGED = SQL_BASE + " AND (LOWER(ci.name) LIKE LOWER(?) OR LOWER(co.name) LIKE LOWER(?)) LIMIT ? OFFSET ? ";
//
//
//    @Autowired
//    public CityJdbcDao(final DataSource dataSource) {
//
//        this.jdbcTemplate = new JdbcTemplate(dataSource);
//        this.jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
//                .withTableName("cities")
//                .usingGeneratedKeyColumns("id");
//    }
//
//    @Override
//    public Optional<City> findById(final long id) {
//        return jdbcTemplate.query(SQL_FIND_BY_ID, CITY_ROW_MAPPER, id)
//                .stream().findFirst();
//
//    }
//
//    @Override
//    public Optional<City> findByName(final String name) {
//        return jdbcTemplate.query(SQL_FIND_BY_NAME, CITY_ROW_MAPPER, name)
//                .stream().findFirst();
//
//    }
//
//    public List<City> getAllCities() {
//        return jdbcTemplate.query(SQL_FIND_ALL, CITY_ROW_MAPPER);
//    }
//
//    @Override
//    public Page<City> findAll(final PageParams pageParams) {
//        final int totalCities = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM cities WHERE deleted = FALSE ", Integer.class);
//        return new Page<>(
//                jdbcTemplate.query(SQL_FIND_ALL_PAGED, CITY_ROW_MAPPER, pageParams.getSize(), offset(pageParams)),
//                pageParams.getPage(),
//                pageCount(totalCities, pageParams.getSize())
//        );
//    }
//
//
//    @Override
//    public void update(final long id, final String name, final Country country) {
//        final int rowsAffected = jdbcTemplate.update("UPDATE cities SET name = ?, country_id = ? WHERE id = ?", name, country.getId(), id);
//        if (rowsAffected == 0) {
//            LOGGER.warn("City update failed: City with ID {} not found", id);
//        }
//    }
//
//    @Override
//    public City create(final String name, final Country country) {
//        final int rowsUpdated = jdbcTemplate.update(
//                "UPDATE cities SET deleted = FALSE WHERE name = ? AND country_id = ? AND deleted = TRUE",
//                name, country.getId()
//        );
//        if (rowsUpdated > 0) {
//            return findByName(name).orElseThrow(() -> {
//                LOGGER.error("City with name {} and country ID {} not found", name, country.getId());
//                return new RuntimeException("Failed to retrieve reactivated city");
//            });
//        }
//
//        final Map<String, Object> params = new HashMap<>();
//        params.put("name", name);
//        params.put("country_id", country.getId());
//        params.put("deleted", false);
//        final long id = jdbcInsert.executeAndReturnKey(params).longValue();
//        return new City(name, country.getName(), id);
//    }
//
//    @Override
//    public void delete(final long id) {
//        final int rowsAffected = jdbcTemplate.update("UPDATE cities SET deleted = TRUE WHERE id = ?", id);
//        if (rowsAffected == 0) {
//            LOGGER.warn("City deletion failed: City with ID {} not found", id);
//        }
//    }
//
//
//    @Override
//    public Page<City> search(final String searchTerm, final PageParams pageParams) {
//        final String searchPattern = likePattern(searchTerm);
//        return executePagedQuery(
//                jdbcTemplate, CITY_ROW_MAPPER, """
//                SELECT COUNT(*)
//                FROM cities ci JOIN countries co ON ci.country_id = co.id
//                WHERE deleted = FALSE AND (
//                    LOWER(ci.name) LIKE LOWER(?)
//                    OR LOWER(co.name) LIKE LOWER(?)
//                    )
//                """, SQL_SEARCH_PAGED, pageParams, searchPattern, searchPattern
//        );
//    }
//
//}
