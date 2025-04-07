package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.CareerDao;
import ar.edu.itba.paw.models.Career;
import ar.edu.itba.paw.models.City;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.stream;

@Repository
public class CareerJdbcDao implements CareerDao {
    private final JdbcTemplate jdbcTemplate;

    private final static RowMapper<Career> CAREER_ROW_MAPPER = (rs, rowNum) -> new Career(
            rs.getLong("career_id"),
            rs.getString("career_name")
    );

    @Autowired
    public CareerJdbcDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public Optional<Career> findById(long id) {
        return jdbcTemplate.query("SELECT * FROM careers WHERE id = ?",
                CAREER_ROW_MAPPER, id).stream().findFirst();
    }

    @Override
    public List<Career> findAll() {
        return jdbcTemplate.query("SELECT * FROM careers",
                CAREER_ROW_MAPPER);
    }

    @Override
    public Optional<Career> findByName(String name) {
        return jdbcTemplate.query("SELECT * FROM careers WHERE name = ?",
                CAREER_ROW_MAPPER, name).stream().findFirst();
    }
}
