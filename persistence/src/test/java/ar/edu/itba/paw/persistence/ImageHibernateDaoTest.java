package ar.edu.itba.paw.persistence;

import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;

import ar.edu.itba.paw.persistence.config.TestConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.itba.paw.models.Image;

import static org.junit.Assert.*;
import static ar.edu.itba.paw.persistence.TestUtils.*;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class ImageHibernateDaoTest {

    @Autowired
    private DataSource ds;

    @Autowired
    private ImageHibernateDao imageDao;

    @PersistenceContext
    private EntityManager em;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp(){
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Test
    public void testCreate(){
        long id = imageDao.create(IMAGE_2_DATA);
        em.flush();

        Image image = jdbcTemplate.queryForObject(
            IMAGE_SELECT_BY_ID, 
            IMAGE_ROW_MAPPER, id
        );
        assertNotNull(image);
        assertArrayEquals(IMAGE_2_DATA, image.getData());
        assertEquals(
            TOTAL_IMAGES + 1, 
            JdbcTestUtils.countRowsInTable(jdbcTemplate, IMAGE_TABLE)
        );
    }

    @Test
    public void testFindById(){
        Optional<Image> maybeImage = imageDao.findById(IMAGE_1_ID);

        assertNotNull(maybeImage);
        assertTrue(maybeImage.isPresent());
        Image image = maybeImage.get();
        assertEquals(IMAGE_1_ID, image.getId().longValue());
        assertArrayEquals(IMAGE_1_DATA, image.getData());
    }
    @Test
    public void testFindByIdWrongId(){
        Optional<Image> maybeImage = imageDao.findById(12341234);

        assertNotNull(maybeImage);
        assertFalse(maybeImage.isPresent());
    }

    @Test
    public void testDelete(){
        imageDao.delete(IMAGE_2_ID);
        em.flush();

        assertEquals(TOTAL_IMAGES - 1, JdbcTestUtils.countRowsInTable(jdbcTemplate, IMAGE_TABLE));
    }
    @Test
    public void testDeleteImageWrong(){
        imageDao.delete(123123);
        em.flush();

        assertEquals(TOTAL_IMAGES, JdbcTestUtils.countRowsInTable(jdbcTemplate, IMAGE_TABLE));
    }

}
