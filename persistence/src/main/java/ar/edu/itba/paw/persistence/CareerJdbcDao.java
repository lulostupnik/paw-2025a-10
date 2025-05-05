package ar.edu.itba.paw.persistence;

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
import static ar.edu.itba.paw.persistence.JdbcDaoUtils.*;

@Repository
public class CareerJdbcDao implements CareerDao {

    private final static Logger LOGGER = LoggerFactory.getLogger(CareerJdbcDao.class);

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    private final static RowMapper<Career> CAREER_ROW_MAPPER = (rs, rowNum) -> new Career(
            rs.getLong("id"),
            rs.getString("name")
    );

    @Autowired
    public CareerJdbcDao(final DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.jdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("careers")
                .usingGeneratedKeyColumns("id");
    }

    @Override
    public Optional<Career> findById(final long id) {
        return jdbcTemplate.query("SELECT * FROM careers WHERE deleted = FALSE AND id = ? ", CAREER_ROW_MAPPER, id)
                .stream().findFirst();
    }

    @Override
    public List<Career> findAll() {
        return jdbcTemplate.query("SELECT * FROM careers WHERE deleted = FALSE", CAREER_ROW_MAPPER);
    }

    @Override
    public Optional<Career> findByName(final String name) {
        return jdbcTemplate.query("SELECT * FROM careers WHERE deleted = FALSE AND name = ?", CAREER_ROW_MAPPER, name)
                .stream().findFirst();
    }

    @Override
    public Page<Career> getAllCareers(final int page, final int pageSize) {
        final int totalItems = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM careers WHERE deleted = FALSE", Integer.class);

        return new Page<>(
                jdbcTemplate.query("SELECT * FROM careers WHERE deleted = FALSE LIMIT ? OFFSET ?", CAREER_ROW_MAPPER, pageSize, (page - 1) * pageSize),
                page,
                pageCount(totalItems, pageSize)
        );

    }

    @Override
    public Page<Career> searchBySubstring(final String substring, final int page, final int size) {
        final String searchPattern = likePattern(substring);
        final int totalCareers = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM careers WHERE LOWER(name) LIKE LOWER(?)", Integer.class, searchPattern);
        return new Page<>(
                jdbcTemplate.query("SELECT * FROM careers WHERE LOWER(name) LIKE LOWER(?) LIMIT ? OFFSET ?", CAREER_ROW_MAPPER, searchPattern, size, (page - 1) * size),
                page,
                pageCount(totalCareers, size)
        );
    }

    @Override
    public Career create(final String name) {
        LOGGER.debug("Creating or reactivating career with name: {}", name);

        final int rowsUpdated = jdbcTemplate.update(
                "UPDATE careers SET deleted = FALSE WHERE name = ? AND deleted = TRUE",
                name
        );

        if (rowsUpdated > 0) {
            LOGGER.debug("Reactivated existing deleted career");
            return findByName(name).orElseThrow(() -> new RuntimeException("Failed to retrieve reactivated career"));
        }

        final Map<String, Object> args = new HashMap<>();
        args.put("name", name);
        args.put("deleted", false);

        final Number key = jdbcInsert.executeAndReturnKey(args);
        LOGGER.debug("Created career with id: {}", key);
        return new Career(key.longValue(), name);
    }

    @Override
    public Career update(final long id, final String name) {
        LOGGER.debug("Updating career id '{}' and name '{}'",id,name);
        jdbcTemplate.update("UPDATE careers SET name = ? WHERE id = ?", name, id);
        return findById(id).orElseThrow(() -> new IllegalArgumentException("Career not found"));
    }

    @Override
    public void delete(final long id) {
        LOGGER.debug("Marking career with ID: {} as deleted", id);
        final int rowsAffected = jdbcTemplate.update("UPDATE careers SET deleted = TRUE WHERE id = ?", id);
        if (rowsAffected == 0) {
            LOGGER.warn("Career deletion failed: Career with ID {} not found", id);
        }
    }

}
