package ar.edu.itba.paw.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.Before;
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

@RunWith(MockitoJUnitRunner.class)
public class CareerServiceImplTest {

    private static final long CAREER_ID = 1;
    private static final String CAREER_NAME = "name";
    private static final String CAREER_NAME_2 = "name2";
    private static final PageParams PAGE_PARAMS = new PageParams(1, 10);
    private static Career career;
    private static Page<Career> career_page;

    @InjectMocks
    private CareerServiceImpl careerService;

    @Mock
    private CareerDao careerDao;

    @Before
    public void init(){
        career = new Career(CAREER_ID, CAREER_NAME);
        career_page = new Page<>(List.of(career), 1, 10, 1); 
    }

    @Test
    public void testFindCareerById(){
        when(
            careerDao.findById(CAREER_ID)
        ).thenReturn(Optional.of(career));

        Optional<Career> maybeCareer = careerService.findCareerById(CAREER_ID);

        assertTrue(maybeCareer.isPresent());
    }
    @Test
    public void testFindCareerByIdMissing(){
        when(
            careerDao.findById(CAREER_ID)
        ).thenReturn(Optional.empty());

        Optional<Career> maybeCareer = careerService.findCareerById(CAREER_ID);

        assertTrue(maybeCareer.isEmpty());
    }

    @Test
    public void testFindCareerByName(){
        when(
            careerDao.findByName(CAREER_NAME)
        ).thenReturn(Optional.of(career));

        Optional<Career> maybeCareer = careerService.findCareerByName(CAREER_NAME);

        assertTrue(maybeCareer.isPresent());
    }
    @Test
    public void testFindCareerByNameEmpty(){
        when(
            careerDao.findByName("")
        ).thenReturn(Optional.empty());

        Optional<Career> maybeCareer = careerService.findCareerByName("");

        assertTrue(maybeCareer.isEmpty());
    }
    @Test
    public void testFindCareerByNameMissing(){
        when(
            careerDao.findByName(null)
        ).thenReturn(Optional.empty());

        Optional<Career> maybeCareer = careerService.findCareerByName(null);

        assertTrue(maybeCareer.isEmpty());
    }

    @Test
    public void testSearchCareers(){
        when(
            careerDao.search(CAREER_NAME, PAGE_PARAMS)
        ).thenReturn(career_page);

        Page<Career> page = careerService.searchCareers(CAREER_NAME, PAGE_PARAMS);

        assertEquals(CAREER_NAME, page.getContent().getFirst().getName());
    }
    @Test
    public void testSearchCareersEmptySearch(){
        when(
            careerDao.findAll(PAGE_PARAMS)
        ).thenReturn(career_page);

        Page<Career> page = careerService.searchCareers("", PAGE_PARAMS);

        assertEquals(CAREER_NAME, page.getContent().getFirst().getName());
    }
    @Test
    public void testSearchCareersMissingSearch(){
        when(
            careerDao.findAll(PAGE_PARAMS)
        ).thenReturn(career_page);

        Page<Career> page = careerService.searchCareers(null, PAGE_PARAMS);

        assertEquals(CAREER_NAME, page.getContent().getFirst().getName());
    }

    @Test
    public void testCreateCareer(){
        when(
            careerDao.create(CAREER_NAME)
        ).thenReturn(career);

        Career newCareer = careerService.createCareer(CAREER_NAME);

        assertNotNull(newCareer);
        assertEquals(CAREER_NAME, newCareer.getName());
        assertEquals(CAREER_ID, newCareer.getId().longValue());
    }

    @Test
    public void testDeleteCareer() {
        when(
            careerDao.findById(CAREER_ID)
        ).thenReturn(Optional.of(career));

        careerService.deleteCareer(CAREER_ID);

        assertTrue(career.isDeleted());
    }
    @Test
    public void testDeleteCareerNotFound() {
        when(
            careerDao.findById(CAREER_ID)
        ).thenReturn(Optional.empty());

        careerService.deleteCareer(CAREER_ID);

        assertFalse(career.isDeleted());
    }

    @Test
    public void testPatchCareer(){
        when(
            careerDao.findById(CAREER_ID)
        ).thenReturn(Optional.of(career));

        Career patched = careerService.patchCareer(CAREER_ID, CAREER_NAME_2);

        assertNotNull(patched);
        assertEquals(CAREER_NAME_2, patched.getName());
        assertEquals(CAREER_ID, patched.getId().longValue());
    }
    @Test
    public void testPatchCareerNoName(){
        when(
            careerDao.findById(CAREER_ID)
        ).thenReturn(Optional.of(career));

        Career patched = careerService.patchCareer(CAREER_ID, null);

        assertNotNull(patched);
        assertEquals(CAREER_NAME, patched.getName());
        assertEquals(CAREER_ID, patched.getId().longValue());
    }
    @Test(expected = CareerNotFoundException.class)
    public void testPatchCareerMissing(){
        when(
            careerDao.findById(CAREER_ID)
        ).thenReturn(Optional.empty());

        careerService.patchCareer(CAREER_ID, CAREER_NAME_2);
    }
}
