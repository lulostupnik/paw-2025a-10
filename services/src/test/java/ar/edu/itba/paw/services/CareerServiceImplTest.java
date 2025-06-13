package ar.edu.itba.paw.services;

import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import ar.edu.itba.paw.interfaces.persistence.CareerDao;
import ar.edu.itba.paw.models.Career;
import ar.edu.itba.paw.models.Page;
import ar.edu.itba.paw.models.PageParams;
import ar.edu.itba.paw.models.exceptions.CareerNotFoundException;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class CareerServiceImplTest {

    private static final long CAREER_ID = 0;
    private static final String CAREER_NAME = "name";
    private static final Career CAREER = new Career(CAREER_ID, CAREER_NAME);
    private static final List<Career> CAREERS = List.of(CAREER);
    private static final Page<Career> CAREER_PAGE = new Page<>(CAREERS, 1, 1, 1);
    private static final PageParams PAGE_PARAMS = new PageParams(1, 10);

    @InjectMocks
    private CareerServiceImpl careerService;

    @Mock
    private CareerDao careerDao;

    @Test
    public void testFindCareerById(){
        when(
            careerDao.findById(eq(CAREER_ID))
        ).thenReturn(Optional.of(CAREER));

        Optional<Career> maybeCareer = careerService.findCareerById(CAREER_ID);

        assertNotNull(maybeCareer);
        assertEquals(CAREER, maybeCareer.get());
    }

    @Test
    public void testFindCareerByName(){
        when(
            careerDao.findByName(eq(CAREER_NAME))
        ).thenReturn(Optional.of(CAREER));

        Optional<Career> maybeCareer = careerService.findCareerByName(CAREER_NAME);

        assertNotNull(maybeCareer);
        assertEquals(CAREER, maybeCareer.get());
    }    

    @Test
    public void testSearchCareersQuery(){
        when(
            careerDao.search(eq(CAREER_NAME), any(PageParams.class))
        ).thenReturn(CAREER_PAGE);

        Page<Career> careers = careerService.searchCareers(CAREER_NAME, PAGE_PARAMS);

        assertNotNull(careers);
        assertEquals(CAREER_PAGE, careers);
    }
    @Test
    public void testSearchCareersEmptyQuery(){
        when(
            careerDao.findAll(any(PageParams.class))
        ).thenReturn(CAREER_PAGE);

        Page<Career> careers = careerService.searchCareers("", PAGE_PARAMS);

        assertNotNull(careers);
        assertEquals(CAREER_PAGE, careers);
    }
    @Test
    public void testSearchCareersMissingQuery(){
        when(
            careerDao.findAll(any(PageParams.class))
        ).thenReturn(CAREER_PAGE);

        Page<Career> careers = careerService.searchCareers(null, PAGE_PARAMS);

        assertNotNull(careers);
        assertEquals(CAREER_PAGE, careers);
    }

    @Test
    public void testCreateCareer(){
        when(
            careerDao.create(eq(CAREER_NAME))
        ).thenReturn(CAREER);

        Career career = careerService.createCareer(CAREER_NAME);

        assertNotNull(career);
        assertEquals(CAREER, career);
    }

    @Test
    public void testUpdateCareer(){
        Career newCareer = new Career("CAREER_NAME");
        when(
            careerDao.findById(eq(CAREER_ID))
        ).thenReturn(Optional.of(newCareer));

        careerService.updateCareer(CAREER_ID, CAREER_NAME);

        assertEquals(CAREER_NAME, newCareer.getName());
    }
    @Test(expected = CareerNotFoundException.class)
    public void testUpdateCareerNotFound(){
        when(
            careerDao.findById(eq(CAREER_ID))
        ).thenReturn(Optional.empty());

        careerService.updateCareer(CAREER_ID, CAREER_NAME);
    }

    @Test
    public void testDeleteCareer(){
        careerService.deleteCareer(CAREER_ID);
    }
}
