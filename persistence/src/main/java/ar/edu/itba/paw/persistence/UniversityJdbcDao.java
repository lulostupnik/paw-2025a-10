package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.UniversityDao;
import ar.edu.itba.paw.models.University;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class UniversityJdbcDao implements UniversityDao {
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    private final static RowMapper<University> UNIVERSITY_ROW_MAPPER = (rs, rowNum) -> new University(rs.getLong("id"), rs.getString("name"), rs.getString("abbreviation"));

    @Autowired
    public UniversityJdbcDao(final DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
            .withTableName("universities")
            .usingGeneratedKeyColumns("id");
    }

    @Override
    public Optional<University> findByName(String name) {
        return jdbcTemplate.query("SELECT * FROM universities WHERE name = ?", UNIVERSITY_ROW_MAPPER, name).stream().findFirst();
    }

    @Override
    public Optional<University> findByAbbreviation(String abbreviation) {
        return jdbcTemplate.query("SELECT * FROM universities WHERE abbreviation = ?", UNIVERSITY_ROW_MAPPER, abbreviation).stream().findFirst();
    }    
    
    @Override
    public Optional<University> findByAny(String searchString) {
        return jdbcTemplate.query("SELECT * FROM universities WHERE abbreviation LIKE ? OR name LIKE ?", UNIVERSITY_ROW_MAPPER, searchString, searchString).stream().findFirst();
    }

    @Override
    public University createUniversity(final String name, final String abbreviation){
        final Map<String, Object> args = new HashMap<>();
        args.put("name", name);
        args.put("abbreviation", abbreviation);
        final Number id = jdbcInsert.executeAndReturnKey(args);
        return new University(id.longValue(), name, abbreviation);
    }

    @Override
    public List<University> getAllUniversities() {
        return jdbcTemplate.query("SELECT * FROM universities ORDER BY name", UNIVERSITY_ROW_MAPPER);
    }
}
