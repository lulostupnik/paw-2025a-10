package ar.edu.itba.paw.persistence;
import ar.edu.itba.paw.interfaces.persistence.CityDao;
import ar.edu.itba.paw.interfaces.persistence.EventDao;
import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.models.City;
import ar.edu.itba.paw.models.Event;
import ar.edu.itba.paw.models.University;
import ar.edu.itba.paw.models.User;
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

    private final JdbcTemplate jdbcTemplate;

    private final static RowMapper<City> CITY_ROW_MAPPER = (rs, rowNum) -> new City(
            rs.getString("city_name"),
            rs.getString("country_name"),
            rs.getLong("city_id")
    );

    private final static String QUERY = "SELECT ci.name as city_name, ci.id as city_id, co.name as country_name FROM cities ci, countries co WHERE ci.country_id = co.id ";

    // private static final RowMapper<City> SIMPLE_CITY_ROW_MAPPER = (rs, rowNum) -> new City(rs.getString("name"), rs.getString("country"), rs.getLong("id"));

    @Autowired
    public CityJdbcDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public Optional<City> findBy(Long id, String name, String country) {
        StringBuilder queryBuilder = new StringBuilder();
        List<Object> params = new ArrayList<>();

        queryBuilder.append(QUERY);

        if (id > 0) {
            queryBuilder.append("AND ci.id = ? ");
            params.add(id);
        }

        if (name != null && !name.isEmpty()) {
            queryBuilder.append("AND ci.name = ? ");
            params.add(name);
        }

        if (country != null && !country.isEmpty()) {
            queryBuilder.append("AND co.name = ? ");
            params.add(country);
        }

        return jdbcTemplate.query(
                queryBuilder.toString(),
                CITY_ROW_MAPPER,
                params.toArray()
        ).stream().findFirst();
    }

    @Override
    public Optional<City> findByName(String name) {
        return jdbcTemplate.query(
                QUERY + "AND ci.name = ?",
                CITY_ROW_MAPPER,
                name
        ).stream().findFirst();
    }
    /*
    @Override
    public List<City> getAllCities() {
        return jdbcTemplate.query("SELECT * FROM cities ORDER BY name", SIMPLE_CITY_ROW_MAPPER);
    }
    */

    @Override
    public List<City> findAll() {
        return jdbcTemplate.query(QUERY, CITY_ROW_MAPPER);
    }

    @Override
    public List<City> findAllByCountry(String country) {
        return jdbcTemplate.query(QUERY + "AND co.name = ?", CITY_ROW_MAPPER, country);
    }

    @Override
    public List<City> findAllBySubstring(String substring) {
        return jdbcTemplate.query(QUERY + "AND ci.name LIKE ?", CITY_ROW_MAPPER, "%" + substring + "%");
    }


}
