package ar.edu.itba.paw.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Optional;

import ar.edu.itba.paw.models.PageParams;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.dao.DataIntegrityViolationException;

import ar.edu.itba.paw.interfaces.persistence.CareerDao;
import ar.edu.itba.paw.models.Career;
import ar.edu.itba.paw.models.Page;

@RunWith(MockitoJUnitRunner.class)
public class CareerServiceImplTest {
    private static final String NAME = "career";
    private static final long ID = 0;
    private static final Career CAREER = new Career(ID, NAME);
    private static final PageParams PAGE_1_DEFAULT = new PageParams(1, 2);
    @InjectMocks
    private CareerServiceImpl careerService;

    @Mock
    private CareerDao careerDao;

    @Test
    public void testFindById(){
        Mockito.when(
            careerDao.findById(Mockito.eq(ID))
        ).thenReturn(Optional.of(CAREER));

        Optional<Career> maybeCareer = careerService.findById(ID);

        assertNotNull(maybeCareer);
        assertTrue(maybeCareer.isPresent());
        assertEquals(CAREER, maybeCareer.get());
    }
    @Test
    public void testFindByIdMissing(){
        Mockito.when(
            careerDao.findById(Mockito.eq(ID))
        ).thenReturn(Optional.empty());

        Optional<Career> maybeCareer = careerService.findById(ID);

        assertNotNull(maybeCareer);
        assertFalse(maybeCareer.isPresent());
    }



    @Test
    public void testFindByName(){
        Mockito.when(
            careerDao.findByName(Mockito.eq(NAME))
        ).thenReturn(Optional.of(CAREER));

        Optional<Career> maybeCareer = careerService.findByName(NAME);

        assertNotNull(maybeCareer);
        assertTrue(maybeCareer.isPresent());
        assertEquals(CAREER, maybeCareer.get());
    }
    @Test
    public void testFindByNameMissing(){
        Mockito.when(
            careerDao.findByName(Mockito.eq(NAME))
        ).thenReturn(Optional.empty());

        Optional<Career> maybeCareer = careerService.findByName(NAME);

        assertNotNull(maybeCareer);
        assertFalse(maybeCareer.isPresent());
    }

    @Test
    public void testGetAllCareersNoFilter(){
        Page<Career> testPage = new Page<Career>(List.of(CAREER), 1, 1);
        Mockito.when(
            careerDao.getAllCareers(Mockito.eq(PAGE_1_DEFAULT))  //@TODO test, le cambie a page params
        ).thenReturn(testPage);

        Page<Career> page = careerService.getAllCareers(null, PAGE_1_DEFAULT);

        assertNotNull(page);
        assertEquals(testPage, page);
    }
    @Test
    public void testGetAllCareersEmptyFilter(){
        Page<Career> testPage = new Page<Career>(List.of(CAREER), 1, 1);
        Mockito.when(
            careerDao.getAllCareers(Mockito.eq(PAGE_1_DEFAULT))
        ).thenReturn(testPage);

        Page<Career> page = careerService.getAllCareers("", PAGE_1_DEFAULT);

        assertNotNull(page);
        assertEquals(testPage, page);
    }
    @Test
    public void testGetAllCareersFilter(){
        Page<Career> testPage = new Page<Career>(List.of(CAREER), 1, 1);
        Mockito.when(
            careerDao.searchBySubstring(Mockito.eq(NAME), Mockito.eq(PAGE_1_DEFAULT))
        ).thenReturn(testPage);

        Page<Career> page = careerService.getAllCareers(NAME, PAGE_1_DEFAULT);

        assertNotNull(page);
        assertEquals(testPage, page);
    }

    @Test
    public void testCreate(){
        Mockito.when(
            careerDao.create(Mockito.eq(NAME))
        ).thenReturn(CAREER);

        Career career = careerService.create(NAME);

        assertNotNull(career);
        assertEquals(CAREER, career);
    }
    @Test(expected = DataIntegrityViolationException.class)
    public void testCreateDuplicated(){
        Mockito.when(
            careerDao.create(Mockito.eq(NAME))
        ).thenThrow(new DataIntegrityViolationException("NAME"));

        careerService.create(NAME);
    }

    @Test
    public void testUpdate(){
        Mockito.when(
            careerDao.update(Mockito.eq(ID), Mockito.eq(NAME))
        ).thenReturn(CAREER);

        Career career = careerService.update(ID, NAME);

        assertNotNull(career);
        assertEquals(CAREER, career);
    }
    @Test(expected = DataIntegrityViolationException.class)
    public void testUpdateDuplicated(){
        Mockito.when(
            careerDao.update(Mockito.eq(ID), Mockito.eq(NAME))
        ).thenThrow(new DataIntegrityViolationException("NAME"));

        Career career = careerService.update(ID, NAME);

        assertNotNull(career);
        assertEquals(CAREER, career);
    }

    @Test
    public void testDelete(){
        careerService.delete(ID);
    }
}
