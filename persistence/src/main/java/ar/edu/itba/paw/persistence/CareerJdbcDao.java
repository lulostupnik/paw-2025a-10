package ar.edu.itba.paw.persistence;

import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;
import ar.edu.itba.paw.models.CursorPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ar.edu.itba.paw.interfaces.persistence.CareerDao;
import ar.edu.itba.paw.models.Career;


@Repository
public class CareerJdbcDao implements CareerDao {

    private static Logger LOGGER = LoggerFactory.getLogger(CareerJdbcDao.class);

    private final JdbcTemplate jdbcTemplate;

    private final static RowMapper<Career> CAREER_ROW_MAPPER = (rs, rowNum) -> new Career(
            rs.getLong("id"),
            rs.getString("name")
    );

    @Autowired
    public CareerJdbcDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public Optional<Career> findById(long id) {
        LOGGER.debug("Querying DB for career {}", id);
        return jdbcTemplate.query("SELECT * FROM careers WHERE id = ?",
                CAREER_ROW_MAPPER, id).stream().findFirst();
    }

    @Override
    public List<Career> findAll() {
        LOGGER.debug("Querying DB for all careers");
        return jdbcTemplate.query("SELECT * FROM careers",
                CAREER_ROW_MAPPER);
    }

    @Override
    public Optional<Career> findByName(String name) {
        LOGGER.debug("Querying DB for carreer {}", name);
        return jdbcTemplate.query("SELECT * FROM careers WHERE name = ?",
                CAREER_ROW_MAPPER, name).stream().findFirst();
    }

    @Override
    public CursorPage<Career, Long> findAll(Long cursor, int limit) {
        LOGGER.debug("Querying DB for all careers with cursor {} and limit {}", cursor, limit);

        String sql = "SELECT * FROM careers" +
                (cursor != null ? " WHERE id > ? " : "") +
                " ORDER BY id LIMIT ?";

        List<Career> careerList;
        if (cursor != null) {
            careerList = jdbcTemplate.query(sql, CAREER_ROW_MAPPER, cursor, limit + 1);
        } else {
            careerList = jdbcTemplate.query(sql, CAREER_ROW_MAPPER, limit + 1);
        }

        boolean hasNext = careerList != null && careerList.size() > limit;
        Long nextCursor = null;

        if (hasNext) {
            careerList.removeLast();
            nextCursor = careerList.getLast().getId();
        }

        return new CursorPage<>(careerList, nextCursor, hasNext);
    }

    @Override
    public CursorPage<Career, Long> findBySubstring(String substring, Long cursor, int limit) {
        LOGGER.debug("Querying DB for careers with name containing '{}' with cursor {} and limit {}", substring, cursor, limit);

        String searchPattern = "%" + substring + "%";
        String sql = "SELECT * FROM careers WHERE name ILIKE ?" +
                (cursor != null ? " AND id > ? " : "") +
                " ORDER BY id LIMIT ?";

        List<Career> careerList;
        if (cursor != null) {
            careerList = jdbcTemplate.query(sql, CAREER_ROW_MAPPER, searchPattern, cursor, limit + 1);
        } else {
            careerList = jdbcTemplate.query(sql, CAREER_ROW_MAPPER, searchPattern, limit + 1);
        }

        boolean hasNext = careerList != null && careerList.size() > limit;
        Long nextCursor = null;

        if (hasNext) {
            careerList.removeLast();
            nextCursor = careerList.getLast().getId();
        }

        return new CursorPage<>(careerList, nextCursor, hasNext);
    }
}
