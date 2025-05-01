package ar.edu.itba.paw.persistance;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;
import java.util.Optional;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.itba.paw.models.Image;
import ar.edu.itba.paw.persistence.ImageJdbcDao;

@Transactional
@Rollback
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class ImageJdbcDaoTest {

    private static final String IMAGES_TABLE = "images";
    private static final byte[] IMAGE_1 = "something".getBytes();
    private static final byte[] IMAGE_2 = "something else".getBytes();
    private static long id1;

    @Autowired
    private DataSource ds;

    @Autowired
    private ImageJdbcDao imageDao;

    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert insert;

    private RowMapper<Image> ROW_MAPPER = (rs, rowNum) -> new Image(rs.getLong("id"), rs.getBytes("content"));

    @Before
    public void setUp(){
        jdbcTemplate = new JdbcTemplate(ds);
        insert = new SimpleJdbcInsert(ds).withTableName(IMAGES_TABLE).usingGeneratedKeyColumns("id");
        id1 = insert.executeAndReturnKey(Map.of("content", IMAGE_1)).longValue();
    }

    @Test
    public void testGetImageById(){
        Optional<Image> maybeImage = imageDao.getImageById(id1);

        assertNotNull(maybeImage);
        assertTrue(maybeImage.isPresent());
        Image image = maybeImage.get();
        assertEquals(id1, image.getId().longValue());
        assertEquals(IMAGE_1.length, image.getData().length);
        for (int i = 0; i < image.getData().length; i++) {
            assertEquals(IMAGE_1[i], image.getData()[i]);
        }
    }
    @Test
    public void testGetImageByIdWrongId(){
        Optional<Image> maybeImage = imageDao.getImageById(12341234);
        assertNotNull(maybeImage);
        assertFalse(maybeImage.isPresent());
    }

    @Test
    public void testSaveImage(){
        long id = imageDao.saveImage(IMAGE_2);
        Optional<Image> maybeImage = jdbcTemplate.query("SELECT * FROM images WHERE id = ?", ROW_MAPPER, id).stream().findFirst();
        assertNotNull(maybeImage);
        assertTrue(maybeImage.isPresent());
        Image image = maybeImage.get();
        assertEquals(IMAGE_2.length, image.getData().length);
        for (int i = 0; i < image.getData().length; i++) {
            assertEquals(IMAGE_2[i], image.getData()[i]);
        }
        assertEquals(2, JdbcTestUtils.countRowsInTable(jdbcTemplate, IMAGES_TABLE));
    }
    @Test(expected = NullPointerException.class)
    public void testSaveImageMissingData(){
        imageDao.saveImage(null);
    }

    @Test
    public void testDeleteImage(){
        imageDao.deleteImage(id1);
        assertEquals(0, JdbcTestUtils.countRowsInTable(jdbcTemplate, IMAGES_TABLE));
    }
    @Test
    public void testDeleteImageWrongImage(){
        imageDao.deleteImage(123123);
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, IMAGES_TABLE));
    }

    @Test 
    public void testUpdateImage(){
        imageDao.updateImage(id1, IMAGE_2);
        Optional<Image> maybeImage = jdbcTemplate.query("SELECT * FROM images WHERE id = ?", ROW_MAPPER, id1).stream().findFirst();
        assertNotNull(maybeImage);
        assertTrue(maybeImage.isPresent());
        Image image = maybeImage.get();
        assertEquals(IMAGE_2.length, image.getData().length);
        for (int i = 0; i < image.getData().length; i++) {
            assertEquals(IMAGE_2[i], image.getData()[i]);
        }
    }
    @Test(expected = NullPointerException.class)
    public void testUpdateImageMissingContent(){
        imageDao.updateImage(id1, null);
    }
}
