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
import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.persistence.UserInterestDao;
import ar.edu.itba.paw.models.Interest;
import ar.edu.itba.paw.models.Page;
import ar.edu.itba.paw.models.PageParams;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.UserInterest;
import ar.edu.itba.paw.models.exceptions.InterestsNotFoundException;
import ar.edu.itba.paw.models.exceptions.UserNotFoundException;

@RunWith(MockitoJUnitRunner.class)
public class InterestServiceImplTest {

    private static final long INTEREST_ID = 1;
    private static final long USER_ID = 23423423;
    private static final int DEFAULT_SCORE = 1;
    private static final String INTEREST_NAME = "interesting";
    private static final Interest INTEREST = new Interest(INTEREST_ID, INTEREST_NAME);
    private static final User USER = new User(USER_ID, INTEREST_NAME, INTEREST_NAME, INTEREST_NAME, INTEREST_NAME, null, null, null, null, null, false, true);
    private static final UserInterest USER_INTEREST = new UserInterest(USER, INTEREST);
    private static final PageParams PAGE_1_DEFAULT = new PageParams(1, 10);
    @InjectMocks
    InterestServiceImpl interestService;

    @Mock
    InterestDao interestDao;
    @Mock
    UserInterestDao uiDao;
    @Mock
    UserDao userDao;


    @Test
    public void testFindInterestById(){
        when(interestDao.findById(eq(INTEREST_ID))).thenReturn(Optional.of(INTEREST));
        
        Optional<Interest> maybeInterest = interestService.findInterestById(INTEREST_ID);

        assertNotNull(maybeInterest);
        assertTrue(maybeInterest.isPresent());
    }

    @Test
    public void testUpdateUserInterestScores(){
        UserInterest interest = new UserInterest(USER, INTEREST, DEFAULT_SCORE);
        
        interestService.updateUserInterestScores(List.of(interest));

        assertEquals(DEFAULT_SCORE + 1, interest.getScore());
    }

    @Test
    public void testFindInterestByName(){
        when(interestDao.findByName(eq(INTEREST_NAME))).thenReturn(Optional.of(INTEREST));

        Optional<Interest> maybeInterest = interestService.findInterestByName(INTEREST_NAME);

        assertNotNull(maybeInterest);
        assertTrue(maybeInterest.isPresent());
    }

    @Test
    public void testFindInterestsByUser(){
        when(
            userDao.findById(eq(USER_ID))
        ).thenReturn(Optional.of(USER));
        when(
            uiDao.findAllByUser(eq(USER), any(PageParams.class))
        ).thenReturn(new Page<>(List.of(USER_INTEREST), 1, 1, 1));

        Page<UserInterest> interests = interestService.findInterestsByUser(USER_ID, PAGE_1_DEFAULT);

        assertNotNull(interests);
    }

    @Test(expected = UserNotFoundException.class)
    public void testFindInterestsByUserNotFound(){
        when(
            userDao.findById(eq(USER_ID))
        ).thenReturn(Optional.empty());

        interestService.findInterestsByUser(USER_ID, PAGE_1_DEFAULT);
    }

    @Test
    public void testFindUserInterest(){
        when(uiDao.findById(eq(USER_ID), eq(INTEREST_ID))).thenReturn(Optional.of(USER_INTEREST));

        Optional<UserInterest> maybeUserInterest = interestService.findUserInterest(USER_ID, INTEREST_ID);

        assertNotNull(maybeUserInterest);
        assertTrue(maybeUserInterest.isPresent());
    }

    @Test
    public void testAddUserInterest(){
        when(
            uiDao.create(eq(USER_ID), eq(INTEREST_ID))
        ).thenReturn(USER_INTEREST);

        UserInterest ui = interestService.addUserInterest(USER_ID, INTEREST_ID);
        
        assertNotNull(ui);
    }

    @Test
    public void testCreateInterest(){
        when(interestDao.create(eq(INTEREST_NAME))).thenReturn(INTEREST);

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
    public void testPatchInterest(){
        Interest newInterest = new Interest("INTEREST_NAME");
        when(
            interestDao.findById(INTEREST_ID)
        ).thenReturn(Optional.of(newInterest));

        Interest interest = interestService.patchInterest(INTEREST_ID, INTEREST_NAME);

        assertEquals(INTEREST_NAME, interest.getName());
    }
    @Test
    public void testPatchInterestNoUpdate(){
        Interest newInterest = new Interest("INTEREST_NAME");
        when(
            interestDao.findById(INTEREST_ID)
        ).thenReturn(Optional.of(newInterest));

        Interest interest = interestService.patchInterest(INTEREST_ID, null);

        assertEquals("INTEREST_NAME", interest.getName());
    }
    @Test(expected = InterestsNotFoundException.class)
    public void testPatchInterestNotFound(){
        when(
            interestDao.findById(INTEREST_ID)
        ).thenReturn(Optional.empty());

        interestService.patchInterest(INTEREST_ID, null);
    }

    @Test
    public void testFindInterests(){
        when(
            interestDao.search(eq(INTEREST_NAME), any(PageParams.class))
        ).thenReturn(new Page<>(List.of(INTEREST), 1,1 ,1 ));

        Page<Interest> interests = interestService.findInterests(INTEREST_NAME, PAGE_1_DEFAULT);

        assertNotNull(interests);
    }
    @Test
    public void testFindInterestsEmptySearch(){
        when(
            interestDao.findAll(any(PageParams.class))
        ).thenReturn(new Page<>(List.of(INTEREST), 1,1 ,1 ));

        Page<Interest> interests = interestService.findInterests("", PAGE_1_DEFAULT);

        assertNotNull(interests);
    }
    @Test
    public void testFindInterestsMissingSearch(){
        when(
            interestDao.findAll(any(PageParams.class))
        ).thenReturn(new Page<>(List.of(INTEREST), 1,1 ,1 ));

        Page<Interest> interests = interestService.findInterests(null, PAGE_1_DEFAULT);

        assertNotNull(interests);
    }
}
