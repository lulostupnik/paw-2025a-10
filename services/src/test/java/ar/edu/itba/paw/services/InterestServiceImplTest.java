package ar.edu.itba.paw.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import ar.edu.itba.paw.interfaces.persistence.InterestDao;
import ar.edu.itba.paw.models.Interest;
import ar.edu.itba.paw.models.Page;
import ar.edu.itba.paw.models.PageParams;

@RunWith(MockitoJUnitRunner.class)
public class InterestServiceImplTest {

    private static final String INTEREST_NAME = "interesting";
    private static final List<String> INTEREST_NAMES = List.of(INTEREST_NAME);
    private static final long ID_1 = 0;
    private static final long ID_2 = 1;
    private static final long USER_ID = 2;
    private static final Interest INTEREST_1 = new Interest(ID_1, INTEREST_NAME);
    private static final Interest INTEREST_2 = new Interest(ID_2, null);
    private static final PageParams PAGE_1_DEFAULT = new PageParams(1, 2);
    private static final Page<Interest> INTEREST_PAGE = new Page<Interest>(List.of(INTEREST_1, INTEREST_2), 1, 1);
    private static final String INTEREST_JSON = "[{\"name\":\"interesting\", \"id\":0}, {\"name\":\"\", \"id\":1}]";
    private static final String EMPTY_JSON = "[]";


    @InjectMocks
    InterestServiceImpl interestService;

    @Mock
    InterestDao interestDao;

    @Test
    public void testFindById(){
        Mockito.when(
            interestDao.findById(Mockito.eq(ID_1))
        ).thenReturn(Optional.of(INTEREST_1));

        Optional<Interest> maybeInterest = interestService.findById(ID_1);

        assertNotNull(maybeInterest);
        assertTrue(maybeInterest.isPresent());
        assertEquals(INTEREST_1, maybeInterest.get());
    }
    @Test
    public void testFindByIdMissing(){
        Mockito.when(
            interestDao.findById(Mockito.eq(ID_1))
        ).thenReturn(Optional.empty());

        Optional<Interest> maybeInterest = interestService.findById(ID_1);

        assertNotNull(maybeInterest);
        assertFalse(maybeInterest.isPresent());
    }

    @Test
    public void testFindByName(){
        Mockito.when(
            interestDao.findByName(Mockito.eq(INTEREST_NAME))
        ).thenReturn(Optional.of(INTEREST_1));

        Optional<Interest> maybeInterest = interestService.findByName(INTEREST_NAME);

        assertNotNull(maybeInterest);
        assertTrue(maybeInterest.isPresent());
        assertEquals(INTEREST_1, maybeInterest.get());
    }
    @Test
    public void testFindByNameMissing(){
        Mockito.when(
            interestDao.findByName(Mockito.eq(INTEREST_NAME))
        ).thenReturn(Optional.empty());

        Optional<Interest> maybeInterest = interestService.findByName(INTEREST_NAME);

        assertNotNull(maybeInterest);
        assertFalse(maybeInterest.isPresent());
    }

    @Test
    public void testFindByUserId(){
        Mockito.when(
            interestDao.findAllByUserId(Mockito.eq(ID_1))
        ).thenReturn(List.of(INTEREST_1));

        List<Interest> interests = interestService.findByUserId(ID_1);

        assertNotNull(interests);
        assertEquals(1, interests.size());
        assertEquals(INTEREST_1, interests.getFirst());
    }
    @Test
    public void testFindByUserIdMissing(){
        Mockito.when(
            interestDao.findAllByUserId(Mockito.eq(ID_1))
        ).thenReturn(List.of());

        List<Interest> interests = interestService.findByUserId(ID_1);

        assertNotNull(interests);
        assertEquals(0, interests.size());
    }


    @Test
    public void testCreateUserInterest(){
        Mockito.when(
            interestDao.create(INTEREST_NAME)
        ).thenReturn(INTEREST_1);

        Interest interest = interestService.createUserInterest(INTEREST_NAME);

        assertEquals(INTEREST_1, interest);
    }

    @Test
    public void testGetAllInterestsMissingQuery(){
        Page<Interest> testPage = new Page<Interest>(List.of(INTEREST_1), 1, 1);
        Mockito.when(
            interestDao.findAll(Mockito.eq(PAGE_1_DEFAULT))
        ).thenReturn(testPage);

        Page<Interest> page = interestService.getAllInterests(null, PAGE_1_DEFAULT);

        assertEquals(testPage, page);
    }
    @Test
    public void testGetAllInterestsEmptyQuery(){
        Page<Interest> testPage = new Page<Interest>(List.of(INTEREST_1), 1, 1);
        Mockito.when(
            interestDao.findAll(Mockito.eq(PAGE_1_DEFAULT))
        ).thenReturn(testPage);

        Page<Interest> page = interestService.getAllInterests("", PAGE_1_DEFAULT);

        assertEquals(testPage, page);
    }
    @Test
    public void testGetAllInterestsQuery(){
        Page<Interest> testPage = new Page<Interest>(List.of(INTEREST_1), 1, 1);
        Mockito.when(
            interestDao.search(Mockito.eq(INTEREST_NAME), Mockito.eq(PAGE_1_DEFAULT))
        ).thenReturn(testPage);

        Page<Interest> page = interestService.getAllInterests(INTEREST_NAME, PAGE_1_DEFAULT);

        assertEquals(testPage, page);
    }

    @Test 
    public void testEditUserInterest(){
        interestService.editUserInterest(ID_1, INTEREST_NAME);
    }

    @Test 
    public void testSaveUserInterests(){
        interestService.saveUserInterests(new long[0], ID_1);
    }

    @Test 
    public void testSaveUserInterestsList(){
        interestService.saveUserInterests(INTEREST_NAMES, ID_1);
    }

    @Test 
    public void testUpdateScoreByInterest(){
        interestService.updateScoreByInterest(INTEREST_1, ID_1);
    }

    @Test 
    public void testUpdateScoreByInterests(){
        interestService.updateScoreByInterests(List.of(INTEREST_1), ID_1);
    }

    @Test
    public void testUpdateUserInterest(){
        interestService.updateUserInterests(new long[0], USER_ID);
    }

    @Test
    public void testFindAllInterestsByUserId(){
        Page<Interest> testPage = new Page<Interest>(List.of(INTEREST_1), 1, 1);
        Mockito.when(
            interestDao.findAllByUserId(Mockito.eq(USER_ID), Mockito.eq(PAGE_1_DEFAULT))
        ).thenReturn(testPage);

        Page<Interest> page = interestService.findAllInterestsByUserId(USER_ID, PAGE_1_DEFAULT);

        assertNotNull(page);
        assertEquals(testPage, page);
    }

    @Test 
    public void testDelete(){
        interestService.delete(ID_1);
    }
}
