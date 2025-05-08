package ar.edu.itba.paw.persistance;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
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


    private static long id1;

    @Autowired
    private DataSource ds;

    @Autowired
    private ImageJdbcDao imageDao;

    private JdbcTemplate jdbcTemplate;

    private RowMapper<Image> ROW_MAPPER = (rs, rowNum) -> new Image(rs.getLong("id"), rs.getBytes("content"));

    @Before
    public void setUp(){
        jdbcTemplate = new JdbcTemplate(ds);
        id1 = jdbcTemplate.queryForObject("SELECT id FROM images WHERE content = ?", Long.class, TestUtils.IMAGE_1_DATA);
    }

    @Test
    public void testGetImageById(){
        Optional<Image> maybeImage = imageDao.getImageById(id1);

        assertNotNull(maybeImage);
        assertTrue(maybeImage.isPresent());
        Image image = maybeImage.get();
        assertEquals(id1, image.getId().longValue());
        assertEquals(TestUtils.IMAGE_1_DATA.length, image.getData().length);
        //TODO trad for-loop
        for (int i = 0; i < image.getData().length; i++) {
            assertEquals(TestUtils.IMAGE_1_DATA[i], image.getData()[i]);
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
        long id = imageDao.saveImage(TestUtils.IMAGE_2_DATA);
        Optional<Image> maybeImage = jdbcTemplate.query("SELECT * FROM images WHERE id = ?", ROW_MAPPER, id).stream().findFirst();
        assertNotNull(maybeImage);
        assertTrue(maybeImage.isPresent());
        Image image = maybeImage.get();
        assertEquals(TestUtils.IMAGE_2_DATA.length, image.getData().length);
        //TODO trad for-loop
        for (int i = 0; i < image.getData().length; i++) {
            assertEquals(TestUtils.IMAGE_2_DATA[i], image.getData()[i]);
        }
        assertEquals(TestUtils.TOTAL_IMAGES + 1, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.IMAGES_TABLE));
    }
    @Test(expected = NullPointerException.class)
    public void testSaveImageMissingData(){
        imageDao.saveImage(null);
    }

    @Test
    public void testDeleteImage(){
        imageDao.deleteImage(id1);
        assertEquals(TestUtils.TOTAL_IMAGES - 1, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.IMAGES_TABLE));
    }
    @Test
    public void testDeleteImageWrongImage(){
        imageDao.deleteImage(123123);
        assertEquals(TestUtils.TOTAL_IMAGES, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.IMAGES_TABLE));
    }

}
