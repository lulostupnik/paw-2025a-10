package ar.edu.itba.paw.persistence;

import static org.junit.Assert.*;
import static ar.edu.itba.paw.persistence.TestUtils.*;

import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
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
        Optional<Interest> maybeInterest = interestDao.findById(INTEREST_1_ID);

        assertNotNull(maybeInterest);
        assertTrue(maybeInterest.isPresent());
        assertEqualsInterest(INTEREST_1, maybeInterest.get());
    }
    @Test
    public void testFindByIdWrongId(){
        Optional<Interest> maybeInterest = interestDao.findById(12341234l);

        assertNotNull(maybeInterest);
        assertFalse(maybeInterest.isPresent());
    }

    @Test
    public void testFindByName(){
        Optional<Interest> maybeInterest = interestDao.findByName(INTEREST_1_NAME);

        assertNotNull(maybeInterest);
        assertTrue(maybeInterest.isPresent());
        assertEqualsInterest(INTEREST_1, maybeInterest.get());
    }
    @Test
    public void testFindByNameWrongName(){
        Optional<Interest> maybeInterest = interestDao.findByName("asdfasdf");

        assertNotNull(maybeInterest);
        assertFalse(maybeInterest.isPresent());
    }

    @Test
    public void testCreate(){
        Interest interest = interestDao.create(INTEREST_NEW1_NAME);
        em.flush();

        assertNotNull(interest);
        assertEqualsInterest(
            new Interest(interest.getId(), INTEREST_NEW1_NAME),
            interest
        );

        Interest persisted = jdbcTemplate.queryForObject(
            INTEREST_SELECT_BY_ID, INTEREST_ROW_MAPPER, interest.getId()
        );
        assertEqualsInterest(new Interest(interest.getId(), INTEREST_NEW1_NAME), persisted);
        assertEquals(
            TOTAL_INTERESTS + 1,
            JdbcTestUtils.countRowsInTable(jdbcTemplate, INTEREST_TABLE)
        );
        assertEquals(
            1,
            JdbcTestUtils.countRowsInTableWhere(
                jdbcTemplate, INTEREST_TABLE,
                "id = " + interest.getId() + " AND name = '" + INTEREST_NEW1_NAME + "'"
            )
        );
    }
    @Test(expected = PersistenceException.class)
    public void testCreateMissingName(){
        interestDao.create(null);
        em.flush();
    }
    @Test(expected = PersistenceException.class)
    public void testCreateDuplicate(){
        interestDao.create(INTEREST_1_NAME);
        em.flush();
    }

    @Test
    public void testFindAllPage1(){
        Page<Interest> page1 = interestDao.findAll(PAGE_1_DEFAULT);

        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(2, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(2, page1.getContent().size());
        assertEqualsInterest(INTEREST_1, page1.getContent().get(0));
        assertEqualsInterest(INTEREST_2, page1.getContent().get(1));
    }
    @Test
    public void testFindAllPage2(){
        Page<Interest> page2 = interestDao.findAll(PAGE_2_DEFAULT);

        assertNotNull(page2);
        assertEquals(2, page2.getCurrentPage());
        assertEquals(2, page2.getTotalPages());
        assertNotNull(page2.getContent());
        assertEquals(1, page2.getContent().size());
        assertEqualsInterest(INTEREST_3, page2.getContent().get(0));
    }
    @Test
    public void testFindAllInterestsPagedNoInterests(){
        deleteInterests(jdbcTemplate);

        Page<Interest> page1 = interestDao.findAll(PAGE_1_DEFAULT);

        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(0, page1.getTotalPages());
        assertNotNull(page1.getContent());
        assertEquals(0, page1.getContent().size());
    }

    @Test
    public void testSearchNoFiltering(){
        Page<Interest> page1 = interestDao.search(
            INTEREST_1_NAME.substring(0, 5), PAGE_1_BIG
        );

        assertNotNull(page1);
        assertEquals(1, page1.getTotalPages());
        assertEquals(3, page1.getContent().size());
        assertEqualsInterest(INTEREST_1, page1.getContent().get(0));
        assertEqualsInterest(INTEREST_2, page1.getContent().get(1));
        assertEqualsInterest(INTEREST_3, page1.getContent().get(2));
    }
    @Test
    public void testSearchFiltering(){
        Page<Interest> page1 = interestDao.search(
            INTEREST_1_NAME.substring(
                INTEREST_1_NAME.length()-1, 
                INTEREST_1_NAME.length()), 
            PAGE_1_DEFAULT
        );

        assertNotNull(page1);
        assertEquals(1, page1.getTotalPages());
        assertEquals(1, page1.getContent().size());
        assertEqualsInterest(INTEREST_1, page1.getContent().get(0));
    }
    @Test
    public void testSearchEmpty(){
        Page<Interest> page1 = interestDao.search("", PAGE_1_BIG);

        assertNotNull(page1);
        assertEquals(1, page1.getTotalPages());
        assertEquals(3, page1.getContent().size());
        assertEqualsInterest(INTEREST_1, page1.getContent().get(0));
        assertEqualsInterest(INTEREST_2, page1.getContent().get(1));
        assertEqualsInterest(INTEREST_3, page1.getContent().get(2));
    }
    @Test
    public void testSearchMissing(){
        Page<Interest> page1 = interestDao.search(null, PAGE_1_BIG);

        assertNotNull(page1);
        assertEquals(1, page1.getTotalPages());
        assertEquals(3, page1.getContent().size());
        assertEqualsInterest(INTEREST_1, page1.getContent().get(0));
        assertEqualsInterest(INTEREST_2, page1.getContent().get(1));
        assertEqualsInterest(INTEREST_3, page1.getContent().get(2));
    }
    @Test
    public void testSearchPaging1(){
        Page<Interest> page1 = interestDao.search("", PAGE_1_DEFAULT);

        assertNotNull(page1);
        assertEquals(2, page1.getTotalPages());
        assertEquals(2, page1.getContent().size());
        assertEqualsInterest(INTEREST_1, page1.getContent().get(0));
        assertEqualsInterest(INTEREST_2, page1.getContent().get(1));
    }
    @Test
    public void testSearchPaging2(){
        Page<Interest> page2 = interestDao.search("", PAGE_2_DEFAULT);

        assertNotNull(page2);
        assertEquals(2, page2.getTotalPages());
        assertEquals(1, page2.getContent().size());
        assertEqualsInterest(INTEREST_3, page2.getContent().get(0));
    }

    @Test
    public void testDelete(){
        interestDao.delete(INTEREST_3_ID);
        em.flush();

        assertEquals(
            TOTAL_INTERESTS - 1, 
            JdbcTestUtils.countRowsInTable(jdbcTemplate, INTEREST_TABLE)
        );
    }
    @Test
    public void testDeleteWrongInterest(){
        interestDao.delete(12341234);
        em.flush();

        assertEquals(
            TOTAL_INTERESTS,
            JdbcTestUtils.countRowsInTable(jdbcTemplate, INTEREST_TABLE)
        );
    }

}

