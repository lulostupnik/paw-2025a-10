package ar.edu.itba.paw.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.sql.DataSource;

import ar.edu.itba.paw.models.PageParams;
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

import ar.edu.itba.paw.models.Interest;
import ar.edu.itba.paw.models.Page;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class InterestHibernateDaoTest {

    @Autowired
    private DataSource ds;

    @Autowired
    private InterestHibernateDao interestDao;
       
    @PersistenceContext
    private EntityManager em;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp(){
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Test
    public void testFindById(){
        Optional<Interest> maybeInterest = interestDao.findById(TestUtils.INTEREST_1_ID);

        assertNotNull(maybeInterest);
        assertTrue(maybeInterest.isPresent());
        TestUtils.assertEqualsInterest(TestUtils.INTEREST_1, maybeInterest.get());
    }
    @Test
    public void testFindByIdWrongId(){
        Optional<Interest> maybeInterest = interestDao.findById(12341234l);

        assertNotNull(maybeInterest);
        assertFalse(maybeInterest.isPresent());
    }

    @Test
    public void testFindByName(){
        Optional<Interest> maybeInterest = interestDao.findByName(TestUtils.INTEREST_1_NAME);

        assertNotNull(maybeInterest);
        assertTrue(maybeInterest.isPresent());
        TestUtils.assertEqualsInterest(TestUtils.INTEREST_1, maybeInterest.get());
    }
    @Test
    public void testFindByNameWrongName(){
        Optional<Interest> maybeInterest = interestDao.findByName("asdfasdf");

        assertNotNull(maybeInterest);
        assertFalse(maybeInterest.isPresent());
    }

    @Test
    public void testCreate(){
        Interest interest = interestDao.create(TestUtils.INTEREST_NEW1_NAME);
        em.flush();

        assertNotNull(interest);
        TestUtils.assertEqualsInterest(new Interest(interest.getId(), TestUtils.INTEREST_NEW1_NAME), interest);
    }
    @Test(expected = PersistenceException.class)
    public void testCreateMissingName(){
        interestDao.create(null);
        em.flush();
    }
    @Test(expected = PersistenceException.class)
    public void testCreateDuplicate(){
        interestDao.create(TestUtils.INTEREST_1_NAME);
        em.flush();
    }

    @Test
    public void testUpdate(){
        interestDao.update(TestUtils.INTEREST_1_ID, TestUtils.INTEREST_NEW1_NAME);
        em.flush();

        Interest interest = jdbcTemplate.queryForObject(
            TestUtils.INTEREST_SELECT_BY_ID,
            TestUtils.INTEREST_ROW_MAPPER,
            TestUtils.INTEREST_1_ID
        );
        TestUtils.assertEqualsInterest(new Interest(TestUtils.INTEREST_1_ID, TestUtils.INTEREST_NEW1_NAME), interest);
    }
    @Test
    public void testUpdateNotFound(){
        interestDao.update(12341234, TestUtils.INTEREST_1_NAME);
        em.flush();

        List<Interest> interests = jdbcTemplate.query(TestUtils.INTEREST_SELECT + "ORDER BY id ASC", TestUtils.INTEREST_ROW_MAPPER);

        assertNotNull(interests);
        assertEquals(TestUtils.TOTAL_INTERESTS, interests.size());
        TestUtils.assertEqualsInterest(TestUtils.INTEREST_1, interests.get(0));
        TestUtils.assertEqualsInterest(TestUtils.INTEREST_2, interests.get(1));
        TestUtils.assertEqualsInterest(TestUtils.INTEREST_3, interests.get(2));
    }

    @Test
    public void testFindAllPage1(){
        Page<Interest> page1 = interestDao.findAll(TestUtils.PAGE_1_DEFAULT);

        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(2, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(2, page1.getContent().size());
        TestUtils.assertEqualsInterest(TestUtils.INTEREST_1, page1.getContent().get(0));
        TestUtils.assertEqualsInterest(TestUtils.INTEREST_2, page1.getContent().get(1));
    }
    @Test
    public void testFindAllPage2(){
        Page<Interest> page2 = interestDao.findAll(TestUtils.PAGE_2_DEFAULT);

        assertNotNull(page2);
        assertEquals(2, page2.getCurrentPage());
        assertEquals(2, page2.getTotalPages());
        assertNotNull(page2.getContent());
        assertEquals(1, page2.getContent().size());
        TestUtils.assertEqualsInterest(TestUtils.INTEREST_3, page2.getContent().get(0));
    }
    @Test
    public void testFindAllInterestsPagedNoInterests(){
        TestUtils.deleteInterests(jdbcTemplate);

        Page<Interest> page1 = interestDao.findAll(new PageParams(1, 2));

        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(0, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(0, page1.getContent().size());
    }

    @Test
    public void testSearchNoFiltering(){
        Page<Interest> page1 = interestDao.search(TestUtils.INTEREST_1_NAME.substring(0, 5), new PageParams(1, 3));

        assertNotNull(page1);
        assertEquals(1, page1.getTotalPages());
        assertEquals(3, page1.getContent().size());
        TestUtils.assertEqualsInterest(TestUtils.INTEREST_1, page1.getContent().get(0));
        TestUtils.assertEqualsInterest(TestUtils.INTEREST_2, page1.getContent().get(1));
        TestUtils.assertEqualsInterest(TestUtils.INTEREST_3, page1.getContent().get(2));
    }
    @Test
    public void testSearchFiltering(){
        Page<Interest> page1 = interestDao.search(TestUtils.INTEREST_1_NAME.substring(TestUtils.INTEREST_1_NAME.length()-1, TestUtils.INTEREST_1_NAME.length()), new PageParams(1, 3));

        assertNotNull(page1);
        assertEquals(1, page1.getTotalPages());
        assertEquals(1, page1.getContent().size());
        TestUtils.assertEqualsInterest(TestUtils.INTEREST_1, page1.getContent().get(0));
    }
    @Test
    public void testSearchEmpty(){
        Page<Interest> page1 = interestDao.search("", new PageParams(1, 3));

        assertNotNull(page1);
        assertEquals(1, page1.getTotalPages());
        assertEquals(3, page1.getContent().size());
        TestUtils.assertEqualsInterest(TestUtils.INTEREST_1, page1.getContent().get(0));
        TestUtils.assertEqualsInterest(TestUtils.INTEREST_2, page1.getContent().get(1));
        TestUtils.assertEqualsInterest(TestUtils.INTEREST_3, page1.getContent().get(2));
    }
    @Test
    public void testSearchMissing(){
        Page<Interest> page1 = interestDao.search(null, new PageParams(1, 3));

        assertNotNull(page1);
        assertEquals(1, page1.getTotalPages());
        assertEquals(3, page1.getContent().size());
        TestUtils.assertEqualsInterest(TestUtils.INTEREST_1, page1.getContent().get(0));
        TestUtils.assertEqualsInterest(TestUtils.INTEREST_2, page1.getContent().get(1));
        TestUtils.assertEqualsInterest(TestUtils.INTEREST_3, page1.getContent().get(2));
    }
    @Test
    public void testSearchPaging1(){
        Page<Interest> page1 = interestDao.search("", TestUtils.PAGE_1_DEFAULT);

        assertNotNull(page1);
        assertEquals(2, page1.getTotalPages());
        assertEquals(2, page1.getContent().size());
        TestUtils.assertEqualsInterest(TestUtils.INTEREST_1, page1.getContent().get(0));
        TestUtils.assertEqualsInterest(TestUtils.INTEREST_2, page1.getContent().get(1));
    }
    @Test
    public void testSearchPaging2(){
        Page<Interest> page2 = interestDao.search("", TestUtils.PAGE_2_DEFAULT);

        assertNotNull(page2);
        assertEquals(2, page2.getTotalPages());
        assertEquals(1, page2.getContent().size());
        TestUtils.assertEqualsInterest(TestUtils.INTEREST_3, page2.getContent().get(0));
    }

    @Test
    public void testDelete(){
        interestDao.delete(TestUtils.INTEREST_3_ID);
        em.flush();
        assertEquals(TestUtils.TOTAL_INTERESTS - 1, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.INTEREST_TABLE));
    }
    @Test
    public void testDeleteWrongInterest(){
        interestDao.delete(12341234);
        em.flush();
        assertEquals(TestUtils.TOTAL_INTERESTS, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.INTEREST_TABLE));
    }

}

