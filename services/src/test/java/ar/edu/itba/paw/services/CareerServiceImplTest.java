package ar.edu.itba.paw.services;

import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
//import static org.junit.Assert.assertTrue;

import java.util.List;
//import java.util.Optional;

import ar.edu.itba.paw.models.PageParams;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import ar.edu.itba.paw.interfaces.persistence.CareerDao;
import ar.edu.itba.paw.models.Career;
import ar.edu.itba.paw.models.Page;

@RunWith(MockitoJUnitRunner.class)
public class CareerServiceImplTest {
    private static final String NAME = "career";
    private static final long ID_1 = 0;
    private static final long ID_2 = 1;
    private static final Career CAREER_1 = new Career(ID_1, NAME);
    private static final Career CAREER_2 = new Career(ID_2, null);
    private static final List<Career> CAREERS = List.of(CAREER_1, CAREER_2);
    private static final Page<Career> CAREER_PAGE = new Page<Career>(CAREERS, 1, 1);
    private static final PageParams PAGE_1_DEFAULT = new PageParams(1, 2);
    @InjectMocks
    private CareerServiceImpl careerService;

    @Mock
    private CareerDao careerDao;

    // @Test
    // public void testFindById(){
    //     Mockito.when(
    //         careerDao.findById(Mockito.eq(ID_1))
    //     ).thenReturn(Optional.of(CAREER_1));

    //     Optional<Career> maybeCareer = careerService.findById(ID_1);

    //     assertNotNull(maybeCareer);
    //     assertTrue(maybeCareer.isPresent());
    //     assertEquals(CAREER_1, maybeCareer.get());
    // }
    // @Test
    // public void testFindByIdMissing(){
    //     Mockito.when(
    //         careerDao.findById(Mockito.eq(ID_1))
    //     ).thenReturn(Optional.empty());

    //     Optional<Career> maybeCareer = careerService.findById(ID_1);

    //     assertNotNull(maybeCareer);
    //     assertFalse(maybeCareer.isPresent());
    // }

    // @Test
    // public void testFindByName(){
    //     Mockito.when(
    //         careerDao.findByName(Mockito.eq(NAME))
    //     ).thenReturn(Optional.of(CAREER_1));

    //     Optional<Career> maybeCareer = careerService.findByName(NAME);

    //     assertNotNull(maybeCareer);
    //     assertTrue(maybeCareer.isPresent());
    //     assertEquals(CAREER_1, maybeCareer.get());
    // }
    // @Test
    // public void testFindByNameMissing(){
    //     Mockito.when(
    //         careerDao.findByName(Mockito.eq(NAME))
    //     ).thenReturn(Optional.empty());

    //     Optional<Career> maybeCareer = careerService.findByName(NAME);

    //     assertNotNull(maybeCareer);
    //     assertFalse(maybeCareer.isPresent());
    // }

    @Test
    public void testGetAllCareersNoFilter(){
        Page<Career> testPage = new Page<Career>(List.of(CAREER_1), 1, 1);
        Mockito.when(
            careerDao.findAll(Mockito.eq(PAGE_1_DEFAULT))
        ).thenReturn(testPage);

        Page<Career> page = careerService.getAllCareers(null, PAGE_1_DEFAULT);

        assertNotNull(page);
        assertEquals(testPage, page);
    }
    @Test
    public void testGetAllCareersEmptyFilter(){
        Mockito.when(
            careerDao.findAll(Mockito.eq(PAGE_1_DEFAULT))
        ).thenReturn(CAREER_PAGE);

        Page<Career> page = careerService.getAllCareers("", PAGE_1_DEFAULT);

        assertNotNull(page);
        assertEquals(CAREER_PAGE, page);
    }
    @Test
    public void testGetAllCareersFilter(){
        Mockito.when(
            careerDao.search(Mockito.eq(NAME), Mockito.eq(PAGE_1_DEFAULT))
        ).thenReturn(CAREER_PAGE);

        Page<Career> page = careerService.getAllCareers(NAME, PAGE_1_DEFAULT);

        assertNotNull(page);
        assertEquals(CAREER_PAGE, page);
    }

    @Test
    public void testCreate(){
        Mockito.when(
            careerDao.create(Mockito.eq(NAME))
        ).thenReturn(CAREER_1);

        Career career = careerService.create(NAME);

        assertNotNull(career);
        assertEquals(CAREER_1, career);
    }

    // @Test
    // public void testUpdate(){
    //     careerService.update(ID_1, NAME);
    // }

    // @Test
    // public void testDelete(){
    //     careerService.delete(ID_1);
    // }

}
