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

@RunWith(MockitoJUnitRunner.class)
public class InterestServiceImplTest {

    private static final String INTEREST_NAME = "interesting";
    private static final String[] INTEREST_NAMES = new String[] {INTEREST_NAME};
    private static final long ID = 0;
    private static final Interest INTEREST = new Interest(ID, INTEREST_NAME);

    @InjectMocks
    InterestServiceImpl interestService;

    @Mock
    InterestDao interestDao;

    @Test
    public void testFindById(){
        Mockito.when(
            interestDao.findById(Mockito.eq(ID))
        ).thenReturn(Optional.of(INTEREST));

        Optional<Interest> maybeInterest = interestService.findById(ID);

        assertNotNull(maybeInterest);
        assertTrue(maybeInterest.isPresent());
        assertEquals(INTEREST, maybeInterest.get());
    }
    @Test
    public void testFindByIdMissing(){
        Mockito.when(
            interestDao.findById(Mockito.eq(ID))
        ).thenReturn(Optional.empty());

        Optional<Interest> maybeInterest = interestService.findById(ID);

        assertNotNull(maybeInterest);
        assertFalse(maybeInterest.isPresent());
    }

    @Test
    public void testFindByName(){
        Mockito.when(
            interestDao.findByName(Mockito.eq(INTEREST_NAME))
        ).thenReturn(Optional.of(INTEREST));

        Optional<Interest> maybeInterest = interestService.findByName(INTEREST_NAME);

        assertNotNull(maybeInterest);
        assertTrue(maybeInterest.isPresent());
        assertEquals(INTEREST, maybeInterest.get());
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
            interestDao.findByUserId(Mockito.eq(ID))
        ).thenReturn(List.of(INTEREST));

        List<Interest> interests = interestService.findByUserId(ID);

        assertNotNull(interests);
        assertEquals(1, interests.size());
        assertEquals(INTEREST, interests.getFirst());
    }
    @Test
    public void testFindByUserIdMissing(){
        Mockito.when(
            interestDao.findByUserId(Mockito.eq(ID))
        ).thenReturn(List.of());

        List<Interest> interests = interestService.findByUserId(ID);

        assertNotNull(interests);
        assertEquals(0, interests.size());
    }

    @Test
    public void testFindAll(){
        Mockito.when(
            interestDao.findAll()
        ).thenReturn(List.of(INTEREST));

        List<Interest> interests = interestService.findAll();

        assertNotNull(interests);
        assertEquals(1, interests.size());
        assertEquals(INTEREST, interests.getFirst());
    }
    @Test
    public void testFindAllMissing(){
        Mockito.when(
            interestDao.findAll()
        ).thenReturn(List.of());

        List<Interest> interests = interestService.findAll();

        assertNotNull(interests);
        assertEquals(0, interests.size());
    }

    
    @Test
    public void testFindIdByName(){
        Mockito.when(
            interestDao.findIdByName(INTEREST_NAMES)
        ).thenReturn(List.of(INTEREST));

        List<Interest> interests = interestService.findIdByName(INTEREST_NAMES);

        assertNotNull(interests);
        assertEquals(1, interests.size());
        assertEquals(INTEREST, interests.getFirst());
    }
    @Test
    public void testFindIdByNameMissing(){
        Mockito.when(
            interestDao.findIdByName(INTEREST_NAMES)
        ).thenReturn(List.of());

        List<Interest> interests = interestService.findIdByName(INTEREST_NAMES);

        assertNotNull(interests);
        assertEquals(0, interests.size());
    }

    @Test
    public void testCreateUserInterest(){
        Mockito.when(
            interestDao.createUserInterest(INTEREST_NAME)
        ).thenReturn(INTEREST);

        Interest interest = interestService.createUserInterest(INTEREST_NAME);

        assertEquals(INTEREST, interest);
    }

    @Test
    public void testGetAllInterestsMissingQuery(){
        Page<Interest> testPage = new Page<Interest>(List.of(INTEREST), 1, 1);
        Mockito.when(
            interestDao.getAllInterests(Mockito.eq(1), Mockito.eq(2))
        ).thenReturn(testPage);

        Page<Interest> page = interestService.getAllInterests(null, 1, 2);

        assertEquals(testPage, page);
    }
    @Test
    public void testGetAllInterestsEmptyQuery(){
        Page<Interest> testPage = new Page<Interest>(List.of(INTEREST), 1, 1);
        Mockito.when(
            interestDao.getAllInterests(Mockito.eq(1), Mockito.eq(2))
        ).thenReturn(testPage);

        Page<Interest> page = interestService.getAllInterests("", 1, 2);

        assertEquals(testPage, page);
    }
    @Test
    public void testGetAllInterestsQuery(){
        Page<Interest> testPage = new Page<Interest>(List.of(INTEREST), 1, 1);
        Mockito.when(
            interestDao.searchBySubstring(Mockito.eq(INTEREST_NAME), Mockito.eq(1), Mockito.eq(2))
        ).thenReturn(testPage);

        Page<Interest> page = interestService.getAllInterests(INTEREST_NAME, 1, 2);

        assertEquals(testPage, page);
    }

    @Test 
    public void testDeleteUserInterest(){
        interestService.deleteUserInterest(ID);
    }

    @Test 
    public void testEditUserInterest(){
        interestService.editUserInterest(ID, INTEREST_NAME);
    }

    @Test 
    public void testSaveUserInterests(){
        interestService.saveUserInterests(new long[0], ID);
    }

    @Test 
    public void testUpdateScoreByInterest(){
        interestService.updateScoreByInterest(INTEREST, ID);
    }

    @Test 
    public void testUpdateScoreByInterests(){
        interestService.updateScoreByInterests(List.of(INTEREST), ID);
    }

    @Test 
    public void testDelete(){
        interestService.delete(ID);
    }
}
