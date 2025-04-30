package ar.edu.itba.paw.persistence;

import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;
import ar.edu.itba.paw.models.CursorPage;
import ar.edu.itba.paw.models.Page;
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
    public Page<Career> getAllCareers(int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        int totalCareers = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM careers", Integer.class);
        int totalPages = (int) Math.ceil((double) totalCareers / pageSize);
        return new Page<>(jdbcTemplate.query("SELECT * FROM careers LIMIT ? OFFSET ?", CAREER_ROW_MAPPER, page, offset),page,totalPages);

    }

}
