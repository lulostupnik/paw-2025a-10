package ar.edu.itba.paw.persistence;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.sql.DataSource;

import ar.edu.itba.paw.models.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ar.edu.itba.paw.interfaces.persistence.CareerDao;


@Repository
public class CareerJdbcDao implements CareerDao {

    private static Logger LOGGER = LoggerFactory.getLogger(CareerJdbcDao.class);

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    private final static RowMapper<Career> CAREER_ROW_MAPPER = (rs, rowNum) -> new Career(
            rs.getLong("id"),
            rs.getString("name")
    );

    @Autowired
    public CareerJdbcDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.jdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("careers")
                .usingGeneratedKeyColumns("id");
    }

    @Override
    public Optional<Career> findById(long id) {
        LOGGER.debug("Querying DB for career {}", id);
        return jdbcTemplate.query("SELECT * FROM careers WHERE deleted = FALSE AND id = ? ",
                CAREER_ROW_MAPPER, id).stream().findFirst();
    }

    @Override
    public List<Career> findAll() {
        LOGGER.debug("Querying DB for all careers");
        return jdbcTemplate.query("SELECT * FROM careers WHERE deleted = FALSE",
                CAREER_ROW_MAPPER);
    }

    @Override
    public Optional<Career> findByName(String name) {
        LOGGER.debug("Querying DB for carreer {}", name);
        return jdbcTemplate.query("SELECT * FROM careers WHERE deleted = FALSE AND name = ?",
                CAREER_ROW_MAPPER, name).stream().findFirst();
    }

    @Override
    public Page<Career> getAllCareers(int page, int pageSize) {
        int totalCareers = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM careers WHERE deleted = FALSE", Integer.class);

        return new Page<>(
                jdbcTemplate.query("SELECT * FROM careers WHERE deleted = FALSE LIMIT ? OFFSET ?", CAREER_ROW_MAPPER, pageSize, (page - 1) * pageSize),
                page,
                (int) Math.ceil((double) totalCareers / pageSize)
        );

    }

    @Override
    public Page<Career> searchBySubstring(String substring, int page, int size) {
        final String searchPattern = "%" + substring + "%";
        int totalCareers = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM careers WHERE LOWER(name) LIKE LOWER(?)", Integer.class, searchPattern);
        return new Page<>(
                jdbcTemplate.query("SELECT * FROM careers WHERE LOWER(name) LIKE LOWER(?) LIMIT ? OFFSET ?", CAREER_ROW_MAPPER, searchPattern, size, (page - 1) * size),
                page,
                (int) Math.ceil((double) totalCareers / size)
        );
    }

    @Override
    public Career create(String name) {
        LOGGER.debug("Creating new career with name: {}", name);

        final Map<String, Object> args = new HashMap<>();
        args.put("name", name);

        final Number key = jdbcInsert.executeAndReturnKey(args);

        LOGGER.debug("Created career with id: {}", key);
        return new Career(key.longValue(), name);
    }

    @Override
    public Career update(long id, String name) {
        LOGGER.debug("Updating career id '{}' and name '{}'",id,name);
        jdbcTemplate.update("UPDATE careers SET name = ? WHERE id = ?", name, id);
        return findById(id).orElseThrow(() -> new IllegalArgumentException("Career not found"));
    }

    @Override
    public void delete(long id) {
        LOGGER.debug("Marking career with ID: {} as deleted", id);
        int rowsAffected = jdbcTemplate.update("UPDATE careers SET deleted = TRUE WHERE id = ?", id);
        if (rowsAffected == 0) {
            LOGGER.warn("Career deletion failed: Career with ID {} not found", id);
        }
    }

}
