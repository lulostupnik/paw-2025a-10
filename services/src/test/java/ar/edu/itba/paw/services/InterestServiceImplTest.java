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
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.UserInterest;
import ar.edu.itba.paw.models.exceptions.InterestsNotFoundException;

@RunWith(MockitoJUnitRunner.class)
public class InterestServiceImplTest {

    private static final long INTEREST_ID = 1;
    private static final int DEFAULT_SCORE = 1;
    private static final String INTEREST_NAME = "interesting";
    private static final Interest INTEREST = new Interest(INTEREST_ID, INTEREST_NAME);
    private static final User USER = new User(INTEREST_NAME, INTEREST_NAME, INTEREST_NAME, INTEREST_NAME, null, null, INTEREST_ID, null, false);

    @InjectMocks
    InterestServiceImpl interestService;

    @Mock
    InterestDao interestDao;
    @Mock
    UserInterestDao uiDao;

    @Test
    public void testUpdateUserInterestScores(){
        UserInterest interest = new UserInterest(USER, INTEREST, DEFAULT_SCORE);
        
        interestService.updateUserInterestScores(List.of(interest));

        assertEquals(DEFAULT_SCORE + 1, interest.getScore());
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
    
}
