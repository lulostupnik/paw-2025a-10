package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.ImageDao;
import ar.edu.itba.paw.models.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;
import java.util.Map;
import java.util.Optional;

@Repository
public class ImageJdbcDao implements ImageDao {
    private final static Logger LOGGER = LoggerFactory.getLogger(ImageJdbcDao.class);

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    private final static RowMapper<Image> IMAGE_ROW_MAPPER = (rs, rowNum) ->  new Image(rs.getLong("id"), rs.getBytes("content"));

    @Autowired
    public ImageJdbcDao(final DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("images")
                .usingGeneratedKeyColumns("id");
    }

    @Override
    public long create(final byte[] imageData) {
        final long id = jdbcInsert.executeAndReturnKey(Map.of("content", imageData)).longValue();
        return id;
    }

    @Override
    public Optional<Image> findById(final long id) {
        return jdbcTemplate.query("SELECT * FROM images WHERE id = ?", IMAGE_ROW_MAPPER, id).stream().findFirst();
    }

    @Override
    public void delete(final long id) {
        final int rowsAffected = jdbcTemplate.update("DELETE FROM images WHERE id = ?", id);
        if (rowsAffected == 0) {
            LOGGER.warn("Image deletion failed: Image with ID {} not found", id);
        }
    }


}
