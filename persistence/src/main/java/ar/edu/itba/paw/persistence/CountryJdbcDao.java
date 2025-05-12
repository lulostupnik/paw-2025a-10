package ar.edu.itba.paw.persistence;

import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ar.edu.itba.paw.interfaces.persistence.CountryDao;
import ar.edu.itba.paw.models.Country;

@Repository
public class CountryJdbcDao implements CountryDao {

    private final JdbcTemplate jdbcTemplate;

    private final static RowMapper<Country> COUNTRY_ROW_MAPPER = (rs, rowNum) -> new Country(
            rs.getLong("id"),
            rs.getString("name"),
            rs.getString("code")
    );

    @Autowired
    public CountryJdbcDao(final DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public List<Country> findAll() {
        return jdbcTemplate.query("SELECT * FROM countries", COUNTRY_ROW_MAPPER);
    }

    @Override
    public Optional<Country> findByName(final String name) {
        return jdbcTemplate.query("SELECT * FROM countries WHERE name = ?", COUNTRY_ROW_MAPPER, name).stream().findFirst();
    }


}
