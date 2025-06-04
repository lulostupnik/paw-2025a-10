package ar.edu.itba.paw.services;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import ar.edu.itba.paw.interfaces.persistence.InterestDao;
import ar.edu.itba.paw.interfaces.persistence.UserInterestDao;
import ar.edu.itba.paw.models.Interest;
import ar.edu.itba.paw.models.Page;
import ar.edu.itba.paw.models.PageParams;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.UserInterest;
import ar.edu.itba.paw.models.exceptions.InterestsNotFoundException;

@RunWith(MockitoJUnitRunner.class)
public class InterestServiceImplTest {

    private static final long INTEREST_ID = 1;
    private static final long USER_ID = 2;
    private static final int DEFAULT_SCORE = 1;
    private static final String INTEREST_NAME = "interesting";
    private static final Interest INTEREST = new Interest(INTEREST_ID, INTEREST_NAME);
    private static final List<Interest> INTERESTS = List.of(INTEREST);
    private static final Page<Interest> INTEREST_PAGE = new Page<>(INTERESTS, 1, 1);
    private static final List<String> INTEREST_NAMES = List.of(INTEREST_NAME);
    private static final User USER = new User(INTEREST_NAME, INTEREST_NAME, INTEREST_NAME, INTEREST_NAME, null, null, INTEREST_ID, null, false);
    private static final UserInterest USER_INTEREST = new UserInterest(USER, INTEREST, DEFAULT_SCORE);
    private static final List<UserInterest> USER_INTERESTS = List.of(USER_INTEREST);
    private static final Page<UserInterest> UI_PAGE = new Page<>(USER_INTERESTS, 1, 1);
    private static final PageParams PAGE_PARAMS = new PageParams(1, 10);

    @InjectMocks
    InterestServiceImpl interestService;

    @Mock
    InterestDao interestDao;
    @Mock
    UserInterestDao uiDao;

    @Test
    public void testFindInterestById(){
        when(
            interestDao.findById(eq(INTEREST_ID))
        ).thenReturn(Optional.of(INTEREST));

        Optional<Interest> maybeInterest = interestService.findInterestById(INTEREST_ID);

        assertNotNull(maybeInterest);
        assertEquals(INTEREST, maybeInterest.get());
    }

    @Test
    public void testFindInterestsByUser(){
        when(
            uiDao.findAllByUser(eq(USER))
        ).thenReturn(USER_INTERESTS);

        List<UserInterest> interests = interestService.findInterestsByUser(USER);

        assertNotNull(interests);
        assertEquals(USER_INTERESTS, interests);
    }

    @Test
    public void testUpdateUserInterestScores(){
        UserInterest interest = new UserInterest(USER, INTEREST, DEFAULT_SCORE);
        
        interestService.updateUserInterestScores(List.of(interest));

        assertEquals(DEFAULT_SCORE + 1, interest.getScore());
    }

    @Test
    public void testFindInterestByName(){
        when(
            interestDao.findByName(eq(INTEREST_NAME))
        ).thenReturn(Optional.of(INTEREST));

        Optional<Interest> maybeInterest = interestService.findInterestByName(INTEREST_NAME);

        assertNotNull(maybeInterest);
        assertEquals(INTEREST, maybeInterest.get());
    }

    @Test
    public void testFindInterestsByUserPaged(){
        when(
            uiDao.findAllByUser(eq(USER), any(PageParams.class))
        ).thenReturn(UI_PAGE);

        Page<UserInterest> ui = interestService.findInterestsByUser(USER, PAGE_PARAMS);

        assertNotNull(ui);
        assertEquals(UI_PAGE, ui);
    }

    @Test
    public void testCreateInterest(){
        when(
            interestDao.create(eq(INTEREST_NAME))
        ).thenReturn(INTEREST);

        Interest interest = interestService.createInterest(INTEREST_NAME);

        assertNotNull(interest);
        assertEquals(INTEREST, interest);
    }

    @Test
    public void testUpdateInterest(){
        Interest newInterest = new Interest("INTEREST_NAME");
        when(
            interestDao.findById(eq(INTEREST_ID))
        ).thenReturn(Optional.of(newInterest));

        interestService.updateInterest(INTEREST_ID, INTEREST_NAME);

        assertEquals(INTEREST_NAME, newInterest.getName());
    }
    @Test(expected = InterestsNotFoundException.class)
    public void testUpdateInterestNotFound(){
        when(
            interestDao.findById(eq(INTEREST_ID))
        ).thenReturn(Optional.empty());

        interestService.updateInterest(INTEREST_ID, INTEREST_NAME);
    }

    @Test
    public void testCreateUserInterests(){
        interestService.createUserInterests(INTEREST_NAMES, USER_ID);
    }

    @Test
    public void testUpdateUserInterests(){
        interestService.updateUserInterests(new long[0], USER_ID);
    }

    @Test
    public void testFindInterestsQuery(){
        when(
            interestDao.search(eq(INTEREST_NAME), any(PageParams.class))
        ).thenReturn(INTEREST_PAGE);

        Page<Interest> interests = interestService.findInterests(INTEREST_NAME, PAGE_PARAMS);

        assertNotNull(interests);
        assertEquals(INTEREST_PAGE, interests);
    }
    @Test
    public void testFindInterestsEmptyQuery(){
        when(
            interestDao.findAll(any(PageParams.class))
        ).thenReturn(INTEREST_PAGE);

        Page<Interest> interests = interestService.findInterests("", PAGE_PARAMS);

        assertNotNull(interests);
        assertEquals(INTEREST_PAGE, interests);
    }
    @Test
    public void testFindInterestsMissingQuery(){
        when(
            interestDao.findAll(any(PageParams.class))
        ).thenReturn(INTEREST_PAGE);

        Page<Interest> interests = interestService.findInterests(null, PAGE_PARAMS);

        assertNotNull(interests);
        assertEquals(INTEREST_PAGE, interests);
    }

    @Test
    public void testUpdateMatchingInterestScores(){
        interestService.updateMatchingInterestScores(USER_ID, USER_ID);
    }

    @Test
    public void testDeleteInterest(){
        interestService.deleteInterest(INTEREST_ID);
    }   
    
}
