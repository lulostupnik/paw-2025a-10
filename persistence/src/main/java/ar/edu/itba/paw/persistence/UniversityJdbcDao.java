package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.UniversityDao;
import ar.edu.itba.paw.models.University;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.Optional;

@Repository
public class UniversityJdbcDao implements UniversityDao {
    private final JdbcTemplate jdbcTemplate;

    private final static RowMapper<University> UNIVERSITY_ROW_MAPPER = (rs, rowNum) -> new University(rs.getLong("id"), rs.getString("name"), rs.getString("abbreviation"));

    @Autowired
    public UniversityJdbcDao(final DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public Optional<University> findByName(String name) {
        return jdbcTemplate.query("SELECT * FROM university WHERE name = ?", UNIVERSITY_ROW_MAPPER, name).stream().findFirst();
    }

    @Override
    public Optional<University> findByAbbreviation(String abbreviation) {
        return jdbcTemplate.query("SELECT * FROM university WHERE abbreviation = ?", UNIVERSITY_ROW_MAPPER, abbreviation).stream().findFirst();
    }
}
