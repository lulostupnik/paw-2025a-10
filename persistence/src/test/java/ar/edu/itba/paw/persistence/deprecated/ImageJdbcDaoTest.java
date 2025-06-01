package ar.edu.itba.paw.persistence.deprecated;
//package ar.edu.itba.paw.persistence;
//
//import java.util.Optional;
//
//import javax.sql.DataSource;
//
//import ar.edu.itba.paw.persistence.config.TestConfig;
//import ar.edu.itba.paw.persistence.deprecated.ImageJdbcDao;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//import org.springframework.test.jdbc.JdbcTestUtils;
//import org.springframework.transaction.annotation.Transactional;
//
//import ar.edu.itba.paw.models.Image;
//
//import static org.junit.Assert.*;
//
//@Transactional
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(classes = TestConfig.class)
//public class ImageJdbcDaoTest {
//
//    @Autowired
//    private DataSource ds;
//
//    @Autowired
//    private ImageJdbcDao imageDao;
//
//    private JdbcTemplate jdbcTemplate;
//
//    @Before
//    public void setUp(){
//        jdbcTemplate = new JdbcTemplate(ds);
//    }
//
//    @Test
//    public void testFindById(){
//        Optional<Image> maybeImage = imageDao.findById(TestUtils.IMAGE_1_ID);
//
//        assertNotNull(maybeImage);
//        assertTrue(maybeImage.isPresent());
//        Image image = maybeImage.get();
//        assertEquals(TestUtils.IMAGE_1_ID, image.getId());
//        assertArrayEquals(TestUtils.IMAGE_1_DATA, image.getData());
//    }
//    @Test
//    public void testFindByIdWrongId(){
//        Optional<Image> maybeImage = imageDao.findById(12341234);
//
//        assertNotNull(maybeImage);
//        assertFalse(maybeImage.isPresent());
//    }
//
//    @Test
//    public void testCreate(){
//        long id = imageDao.create(TestUtils.IMAGE_2_DATA);
//
//        Image image = jdbcTemplate.queryForObject(TestUtils.IMAGE_SELECT_BY_ID, TestUtils.IMAGE_ROW_MAPPER, id);
//        assertNotNull(image);
//        assertArrayEquals(TestUtils.IMAGE_2_DATA, image.getData());
//        assertEquals(TestUtils.TOTAL_IMAGES + 1, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.IMAGE_TABLE));
//    }
//
//    @Test
//    public void testDelete(){
//        imageDao.delete(TestUtils.IMAGE_2_ID);
//
//        assertEquals(TestUtils.TOTAL_IMAGES - 1, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.IMAGE_TABLE));
//    }
//    @Test
//    public void testDeleteImageWrong(){
//        imageDao.delete(123123);
//
//        assertEquals(TestUtils.TOTAL_IMAGES, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.IMAGE_TABLE));
//    }
//
//}
