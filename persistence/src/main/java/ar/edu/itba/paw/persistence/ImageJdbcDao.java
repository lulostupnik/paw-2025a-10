package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.ImageDao;
import ar.edu.itba.paw.models.Image;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Repository
public class ImageJdbcDao implements ImageDao {
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
    public Image saveImage(byte[] imageData) {
        final Number id = jdbcInsert.executeAndReturnKey(Map.of("content", imageData));
        /*
        try {
            key = insertImage.executeAndReturnKey(Map.of("data", imageData));
        } catch (DataAccessException e) {
            throw new RuntimeException("Error while saving image", e);
        }

        if (key == null) {
            throw new RuntimeException("Failed to retrieve generated ID for image");
        }
        */
        return new Image(id.longValue(), imageData);
    }

    @Override
    public Optional<Image> getImageById(long id) {
        String sql = "SELECT * FROM images WHERE id = ?";

        try {
            return jdbcTemplate.query("SELECT * FROM images WHERE id = ?", IMAGE_ROW_MAPPER, id).stream().findFirst();
        } catch (DataAccessException e) {
            throw new RuntimeException("Error while saving image", e);        }
    }


    /*
    @Override
    public Image saveImage(byte[] imageData) {
        String sql = "INSERT INTO images (data) VALUES (?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setBytes(1, imageData);
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key == null) {
            throw new RuntimeException("Failed to retrieve generated ID for image");
        }
        return new Image(key.longValue(), imageData);
    }
    */
}







/*
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.io.IOException;

@Repository
public class ImageJdbcDao implements ImageDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ImageJdbcDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public Image saveImage(String name, String contentType, InputStream imageInputStream, long size) {
        String sql = "INSERT INTO images (name, content_type, data) VALUES (?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, name);
            ps.setString(2, contentType);
            // Pasamos directamente el InputStream sin convertirlo a byte[]
            ps.setBinaryStream(3, imageInputStream, size);
            return ps;
        }, keyHolder);

        // Crear y retornar la instancia de Image, sin los datos binarios
        Image image = new Image();
        image.setId((Long) keyHolder.getKeys().get("id"));
        image.setName(name);
        image.setContentType(contentType);
        // No guardamos los datos en el objeto Image para evitar ocupar memoria
        image.setData(null);
        image.setUploadDate(LocalDateTime.now());

        return image;
    }

    // Sobrecarga opcional para mantener compatibilidad con byte[]
    @Override
    public Image saveImage(String name, String contentType, byte[] imageData) {
        String sql = "INSERT INTO images (name, content_type, data) VALUES (?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, name);
            ps.setString(2, contentType);
            ps.setBytes(3, imageData);
            return ps;
        }, keyHolder);

        // Crear y retornar la instancia de Image
        Image image = new Image();
        image.setId((Long) keyHolder.getKeys().get("id"));
        image.setName(name);
        image.setContentType(contentType);
        image.setData(imageData);
        image.setUploadDate(LocalDateTime.now());

        return image;
    }

    @Override
    public Image getImageById(Long id) {
        String sql = "SELECT id, name, content_type, data, upload_date FROM images WHERE id = ?";

        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{id}, imageRowMapper());
        } catch (DataAccessException e) {
            return null;
        }
    }

    @Override
    public List<Image> getAllImages() {
        // Para listar imágenes, no necesitamos devolver los datos binarios
        String sql = "SELECT id, name, content_type, upload_date FROM images";
        return jdbcTemplate.query(sql, lightImageRowMapper());
    }

    @Override
    public void deleteImage(Long id) {
        String sql = "DELETE FROM images WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    // RowMapper que incluye los datos binarios
    private RowMapper<Image> imageRowMapper() {
        return (ResultSet rs, int rowNum) -> {
            Image image = new Image();
            image.setId(rs.getLong("id"));
            image.setName(rs.getString("name"));
            image.setContentType(rs.getString("content_type"));
            image.setData(rs.getBytes("data"));

            Timestamp timestamp = rs.getTimestamp("upload_date");
            if (timestamp != null) {
                image.setUploadDate(timestamp.toLocalDateTime());
            }

            return image;
        };
    }

    // RowMapper ligero sin datos binarios para listados
    private RowMapper<Image> lightImageRowMapper() {
        return (ResultSet rs, int rowNum) -> {
            Image image = new Image();
            image.setId(rs.getLong("id"));
            image.setName(rs.getString("name"));
            image.setContentType(rs.getString("content_type"));

            Timestamp timestamp = rs.getTimestamp("upload_date");
            if (timestamp != null) {
                image.setUploadDate(timestamp.toLocalDateTime());
            }

            return image;
        };
    }
}
*/
