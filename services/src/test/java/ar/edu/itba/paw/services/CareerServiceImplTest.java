package ar.edu.itba.paw.services;

import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import ar.edu.itba.paw.interfaces.persistence.CareerDao;
import ar.edu.itba.paw.models.Career;
import ar.edu.itba.paw.models.exceptions.CareerNotFoundException;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class CareerServiceImplTest {

    private static final long CAREER_ID = 0;
    private static final String CAREER_NAME = "name";

    @InjectMocks
    private CareerServiceImpl careerService;

    @Mock
    private CareerDao careerDao;


    @Test
    public void testUpdateCareer(){
        Career newCareer = new Career("CAREER_NAME");
        when(
            careerDao.findById(eq(CAREER_ID))
        ).thenReturn(Optional.of(newCareer));

        Career career = careerService.updateCareer(CAREER_ID, CAREER_NAME);

        assertEquals(CAREER_NAME, career.getName());
    }

    @Test(expected = CareerNotFoundException.class)
    public void testUpdateCareerNotFound(){
        when(
            careerDao.findById(eq(CAREER_ID))
        ).thenReturn(Optional.empty());

        careerService.updateCareer(CAREER_ID, CAREER_NAME);
    }

    @Test
    public void testDeleteCareer() {
        Career career = new Career("Engineering");
        when(careerDao.findById(CAREER_ID)).thenReturn(Optional.of(career));

        careerService.deleteCareer(CAREER_ID);

        assertTrue(career.isDeleted());
    }
}
