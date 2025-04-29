package ar.edu.itba.paw.persistence;
import ar.edu.itba.paw.interfaces.persistence.CityDao;
import ar.edu.itba.paw.models.City;

import ar.edu.itba.paw.models.CursorPage;
import ar.edu.itba.paw.models.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.*;

//FIXME: Not yet tested

@Repository
public class CityJdbcDao implements CityDao {

    private static Logger LOGGER = LoggerFactory.getLogger(CityJdbcDao.class);

    private final JdbcTemplate jdbcTemplate;

    private final static RowMapper<City> CITY_ROW_MAPPER = (rs, rowNum) -> new City(
            rs.getString("city_name"),
            rs.getString("country_name"),
            rs.getLong("city_id")
    );

    private final static String SELECT_CLAUSE = "SELECT ci.name as city_name, ci.id as city_id, co.name as country_name";
    private final static String QUERY = SELECT_CLAUSE + " FROM cities ci, countries co WHERE ci.country_id = co.id ";

    // private static final RowMapper<City> SIMPLE_CITY_ROW_MAPPER = (rs, rowNum) -> new City(rs.getString("name"), rs.getString("country"), rs.getLong("id"));

    @Autowired
    public CityJdbcDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
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
                QUERY + "AND ci.name = ?",
                CITY_ROW_MAPPER,
                name
        ).stream().findFirst();

    }

    @Override
    public List<City> getAllCities() {
        LOGGER.debug("Querying DB for all cities");
        return jdbcTemplate.query(QUERY + " ORDER BY city_name", CITY_ROW_MAPPER);
    }

    @Override
    public Page<City> getAllCities(int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        StringBuilder query = new StringBuilder(SELECT_CLAUSE);
        query.append(" FROM (SELECT * FROM cities ci LIMIT ? OFFSET ?) as ci, countries co WHERE ci.country_id = co.id ");
        return new Page<>(jdbcTemplate.query(query.toString(),CITY_ROW_MAPPER,page,offset),page);
    }

    @Override
    public List<City> findAll() {
        LOGGER.debug("Querying DB for all cities");
        return jdbcTemplate.query(QUERY, CITY_ROW_MAPPER);
    }

    @Override
    public List<City> findAllByCountry(String country) {
        LOGGER.debug("Querying DB for cities in country {}");
        return jdbcTemplate.query(QUERY + "AND co.name = ?", CITY_ROW_MAPPER, country);
    }

    @Override
    public List<City> findAllBySubstring(String substring) {
        LOGGER.debug("Querying DB for cities like {}", substring);
        return jdbcTemplate.query(QUERY + "AND ci.name LIKE ?", CITY_ROW_MAPPER, "%" + substring + "%");
    }

}
