package ar.edu.itba.paw.persistence;
import ar.edu.itba.paw.interfaces.persistence.CityDao;
import ar.edu.itba.paw.models.City;

import ar.edu.itba.paw.models.Country;
import ar.edu.itba.paw.models.CursorPage;
import ar.edu.itba.paw.models.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.*;

//FIXME: Not yet tested

@Repository
public class CityJdbcDao implements CityDao {

    private static Logger LOGGER = LoggerFactory.getLogger(CityJdbcDao.class);

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    private final static RowMapper<City> CITY_ROW_MAPPER = (rs, rowNum) -> new City(
            rs.getString("city_name"),
            rs.getString("country_name"),
            rs.getLong("city_id")
    );

    private final static String SELECT_CLAUSE = "SELECT ci.name as city_name, ci.id as city_id, co.name as country_name";
    private final static String QUERY = SELECT_CLAUSE + " FROM cities ci, countries co WHERE ci.country_id = co.id AND ci.deleted = FALSE ";

    private final static String SQL_FIND_ALL_PAGED = QUERY + " ORDER BY ci.name LIMIT ? OFFSET ?";
    private final static String SQL_FIND_BY_COUNTRY = QUERY + "AND co.name = ?";
    private final static String SQL_SEARCH_PAGED = QUERY + " AND ci.name ILIKE ? LIMIT ? OFFSET ? ";

    // private static final RowMappeFr<City> SIMPLE_CITY_ROW_MAPPER = (rs, rowNum) -> new City(rs.getString("name"), rs.getString("country"), rs.getLong("id"));

    @Autowired
    public CityJdbcDao(DataSource dataSource) {

        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("cities")
                .usingGeneratedKeyColumns("id");
    }

    @Override
    public Optional<City> findBy(Long id, String name, String country) {
        LOGGER.debug("Querying DB for city advanced");
    
        StringBuilder queryBuilder = new StringBuilder();
        List<Object> params = new ArrayList<>();

        queryBuilder.append(QUERY);

        if (id != null && id > 0) {
            LOGGER.debug("Search parameter city ID: {}", id);
            queryBuilder.append("AND ci.id = ? ");
            params.add(id);
        }

        if (name != null && !name.isEmpty()) {
            LOGGER.debug("Search parameter city name: {}", name);
            queryBuilder.append("AND ci.name = ? ");
            params.add(name);
        }

        if (country != null && !country.isEmpty()) {
            LOGGER.debug("Search parameter country name: {}", country);
            queryBuilder.append("AND co.name = ? ");
            params.add(country);
        }

        Optional<City> city = jdbcTemplate.query(
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
    public Optional<City> findByName(String name) {
        LOGGER.debug("Querying DB for city entry with name {}", name);
        return jdbcTemplate.query(
                QUERY + " AND ci.name = ?",
                CITY_ROW_MAPPER,
                name
        ).stream().findFirst();

    }

    @Override
    public List<City> getAllCities() {
        return jdbcTemplate.query(QUERY + " ORDER BY city_name ", CITY_ROW_MAPPER);
    }

    @Override
    public Page<City> getAllCities(int page, int pageSize) {
        int totalCities = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM cities WHERE deleted = FALSE ", Integer.class);
        return new Page<>(
                jdbcTemplate.query(SQL_FIND_ALL_PAGED, CITY_ROW_MAPPER, pageSize, (page - 1) * pageSize),
                page,
                (int) Math.ceil((double) totalCities / pageSize)
        );
    }


    @Override
    public void updateCity(long id, String name, Country country) {
        String sql = "UPDATE cities SET name = ?, country_id = ? WHERE id = ?";
        jdbcTemplate.update(sql, name, country.getId(), id);
    }

    @Override
    public void createCity(String name, Country country) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("country_id", country.getId());
        jdbcInsert.execute(params);
    }

    @Override
    public void delete(long id) {
        LOGGER.debug("Marking city with ID: {} as deleted", id);
        int rowsAffected = jdbcTemplate.update("UPDATE cities SET deleted = TRUE WHERE id = ?", id);
        if (rowsAffected == 0) {
            LOGGER.warn("City deletion failed: City with ID {} not found", id);
        }
    }

    @Override
    public List<City> findAll() {
        return jdbcTemplate.query(QUERY, CITY_ROW_MAPPER);
    }

    @Override
    public List<City> findAllByCountry(String country) {
        LOGGER.debug("Querying DB for cities in country {}");
        return jdbcTemplate.query(SQL_FIND_BY_COUNTRY, CITY_ROW_MAPPER, country);
    }

    @Override
    public Page<City> searchBySubstring(String substring, int page, int size) {
        String searchPattern = "%" + substring + "%";
        int totalItems = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM cities WHERE deleted = FALSE AND name ILIKE ? ", Integer.class, searchPattern);
        return new Page<>(
                jdbcTemplate.query(SQL_SEARCH_PAGED, CITY_ROW_MAPPER, searchPattern, size, (page - 1) * size),
                page,
                (int) Math.ceil((double) totalItems / size));
    }

}
