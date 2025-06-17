package ar.edu.itba.paw.persistence;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.sql.DataSource;
import javax.transaction.Transactional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ar.edu.itba.paw.models.Page;
import ar.edu.itba.paw.models.Tip;
import ar.edu.itba.paw.persistence.config.TestConfig;

import static org.junit.Assert.*;

import java.util.Optional;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class TipHibernateDaoTest {

    @Autowired
    private DataSource ds;

    @Autowired
    private TipHibernateDao tipDao;
        
    @PersistenceContext
    private EntityManager em;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp(){
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Test
    public void testCreateTip(){
        tipDao.create(TestUtils.JOURNEY_2, TestUtils.TIP_NEW_TITLE, TestUtils.TIP_NEW_CONTENT);
        em.flush();

        Tip tip = jdbcTemplate.queryForObject(
            TestUtils.TIP_SELECT_BY_DATA, 
            TestUtils.TIP_ROW_MAPPER, 
            TestUtils.JOURNEY_2_ID, 
            TestUtils.TIP_NEW_TITLE, 
            TestUtils.TIP_NEW_CONTENT
        );
        assertNotNull(tip);
        assertTrue(tip.getId() > 0);
    }
    @Test(expected = PersistenceException.class)
    public void testCreateTipMissingJourney(){
        tipDao.create(null, TestUtils.TIP_NEW_TITLE, TestUtils.TIP_NEW_CONTENT);
        em.flush();
    }
    @Test(expected = PersistenceException.class)
    public void testCreateTipMissingTitle(){
        tipDao.create(TestUtils.JOURNEY_2, null, TestUtils.TIP_NEW_CONTENT);
        em.flush();
    }
    @Test(expected = PersistenceException.class)
    public void testCreateTipMissingContent(){
        tipDao.create(TestUtils.JOURNEY_2, TestUtils.TIP_NEW_TITLE, null);
        em.flush();
    }

    @Test
    public void testDeleteTip(){
        tipDao.delete(TestUtils.TIP_1_ID);
        em.flush();

        assertEquals(
            0, 
            jdbcTemplate.query(
                TestUtils.TIP_SELECT_BY_ID,
                TestUtils.TIP_ROW_MAPPER, 
                TestUtils.TIP_1_ID
            ).size()
        );
    }
    @Test (expected = NoResultException.class)
    public void testDeleteTipMissingTip(){
        tipDao.delete(12341234l);
        em.flush();
    }

    @Test
    public void testDeleteByJourney(){
        tipDao.deleteByJourney(TestUtils.JOURNEY_1_ID);
        em.flush();

        assertEquals(
            0, 
            jdbcTemplate.query(
                TestUtils.TIP_SELECT_BY_ID,
                TestUtils.TIP_ROW_MAPPER, 
                TestUtils.TIP_1_ID
            ).size()
        );
    }
    @Test
    public void testDeleteByJourneyMissingJourney(){
        tipDao.deleteByJourney(12341234l);
        em.flush();

        assertEquals(
            1, 
            jdbcTemplate.query(
                TestUtils.TIP_SELECT_BY_ID,
                TestUtils.TIP_ROW_MAPPER, 
                TestUtils.TIP_1_ID
            ).size()
        );
    }

    @Test
    public void testFindTipById(){
        Optional<Tip> maybeTip = tipDao.findById(TestUtils.TIP_1_ID);

        assertNotNull(maybeTip);
        assertTrue(maybeTip.isPresent());
        TestUtils.assertEqualsTip(TestUtils.TIP_1, maybeTip.get());
    }
    @Test
    public void testFindTipByIdMissingTip(){
        Optional<Tip> maybeTip = tipDao.findById(12341234l);

        assertNotNull(maybeTip);
        assertFalse(maybeTip.isPresent());
    }

    @Test
    public void testFindTipsByJourney(){
        Page<Tip> tips = tipDao.findByJourney(TestUtils.JOURNEY_1, TestUtils.PAGE_1_BIG);

        assertNotNull(tips);
        assertEquals(1, tips.getCurrentPage());
        assertEquals(1, tips.getTotalPages());
        assertEquals(1, tips.getContent().size());
        TestUtils.assertEqualsTip(TestUtils.TIP_1, tips.getContent().getFirst());
    }
    @Test
    public void testFindTipsByJourneyNoTips(){
        Page<Tip> tips = tipDao.findByJourney(TestUtils.JOURNEY_2, TestUtils.PAGE_1_BIG);

        assertNotNull(tips);
        assertEquals(1, tips.getCurrentPage());
        assertEquals(0, tips.getTotalPages());
        assertEquals(0, tips.getContent().size());
    }
}
