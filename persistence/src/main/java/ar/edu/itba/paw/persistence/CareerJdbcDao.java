package ar.edu.itba.paw.persistence;

import java.util.HashMap;
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
    public Optional<Career> findByName(final String name) {
        return jdbcTemplate.query("SELECT * FROM careers WHERE deleted = FALSE AND name = ?", CAREER_ROW_MAPPER, name)
                .stream().findFirst();
    }

    @Override
    public Page<Career> findAll(final PageParams pageParams) {
        final int totalItems = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM careers WHERE deleted = FALSE", Integer.class);

        return new Page<>(
                jdbcTemplate.query("SELECT * FROM careers WHERE deleted = FALSE LIMIT ? OFFSET ?", CAREER_ROW_MAPPER, pageParams.getSize(), offset(pageParams)),
                pageParams.getPage(),
                pageCount(totalItems, pageParams.getSize())
        );

    }

    @Override
    public Page<Career> search(final String substring, final PageParams pageParams) {
        final String searchPattern = likePattern(substring);
        final int totalCareers = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM careers WHERE LOWER(name) LIKE LOWER(?)", Integer.class, searchPattern);
        return new Page<>(
                jdbcTemplate.query("SELECT * FROM careers WHERE LOWER(name) LIKE LOWER(?) LIMIT ? OFFSET ?", CAREER_ROW_MAPPER, searchPattern, pageParams.getSize(), offset(pageParams)),
                pageParams.getPage(),
                pageCount(totalCareers, pageParams.getSize())
        );
    }

    @Override
    public Career create(final String name) {
        final int rowsUpdated = jdbcTemplate.update(
                "UPDATE careers SET deleted = FALSE WHERE name = ? AND deleted = TRUE",
                name
        );
        if (rowsUpdated > 0) {
            return findByName(name).orElseThrow(() -> new RuntimeException("Failed to retrieve reactivated career"));
        }

        final Map<String, Object> args = new HashMap<>();
        args.put("name", name);
        args.put("deleted", false);

        final Number key = jdbcInsert.executeAndReturnKey(args);
        return new Career(key.longValue(), name);
    }

    @Override
    public Career update(final long id, final String name) {
        final int rowsAffected = jdbcTemplate.update("UPDATE careers SET name = ? WHERE id = ?", name, id);
        if (rowsAffected == 0) {
            LOGGER.warn("Career update failed: Career with ID {} not found", id);
        }
        return findById(id).orElseThrow(() -> new IllegalArgumentException("Career not found"));
    }

    @Override
    public void delete(final long id) {
        final int rowsAffected = jdbcTemplate.update("UPDATE careers SET deleted = TRUE WHERE id = ?", id);
        if (rowsAffected == 0) {
            LOGGER.warn("Career deletion failed: Career with ID {} not found", id);
        }
    }

}
