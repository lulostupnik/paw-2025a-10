package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.ImageDao;
import ar.edu.itba.paw.models.Image;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.Map;
import java.util.Optional;

@Repository
public class ImageJdbcDao implements ImageDao {
    private static Logger LOGGER = LoggerFactory.getLogger(ImageJdbcDao.class);

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    private final static RowMapper<Image> IMAGE_ROW_MAPPER = (rs, rowNum) ->  new Image(rs.getLong("id"), rs.getBytes("content"));

    @Autowired
    public ImageJdbcDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("images")
                .usingGeneratedKeyColumns("id");
    }

    @Override
    public long saveImage(byte[] imageData) {
        LOGGER.debug("Registering new image of size {}", imageData.length);
        Number key = jdbcInsert.executeAndReturnKey(Map.of("content", imageData)).longValue();
        LOGGER.debug("Successfully registered image {}", key.longValue());
        return key.longValue();
    }

    @Override
    public Optional<Image> getImageById(long id) {
        LOGGER.debug("Querying DB for image {}", id);
        try {
            return jdbcTemplate.query("SELECT * FROM images WHERE id = ?", IMAGE_ROW_MAPPER, id).stream().findFirst();
        } catch (DataAccessException e) {
            LOGGER.error("Error accessing image {}", e);
            throw new RuntimeException("Error while saving image", e);
        }
    }

    @Override
    public void deleteImage(long id) {
        LOGGER.debug("Deleting image {} from DB", id);
        jdbcTemplate.update("DELETE FROM images WHERE id = ?", id);
    }

    @Override
    public void updateImage(long id, byte[] newContent) {
        LOGGER.debug("Updating image {} with new content of size {}", id, newContent.length);
        jdbcTemplate.update("UPDATE images SET content = ? WHERE id = ?", newContent, id);
    }

}
