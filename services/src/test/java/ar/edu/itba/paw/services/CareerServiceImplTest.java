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
import org.springframework.dao.DataIntegrityViolationException;

import ar.edu.itba.paw.interfaces.persistence.CareerDao;
import ar.edu.itba.paw.models.Career;
import ar.edu.itba.paw.models.Page;

@RunWith(MockitoJUnitRunner.class)
public class CareerServiceImplTest {
    private static final String NAME = "career";
    private static final long ID = 0;
    private static final Career CAREER = new Career(ID, NAME);
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
    public void testFindAll(){
        Mockito.when(
            careerDao.findAll()
        ).thenReturn(List.of(CAREER));

        List<Career> careers = careerService.findAll();

        assertNotNull(careers);
        assertEquals(1, careers.size());
        assertEquals(CAREER, careers.getFirst());
    }
    @Test
    public void testFindAllNoCareers(){
        Mockito.when(
            careerDao.findAll()
        ).thenReturn(List.of());

        List<Career> careers = careerService.findAll();

        assertNotNull(careers);
        assertEquals(0, careers.size());
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
            careerDao.getAllCareers(Mockito.eq(1), Mockito.eq(2))
        ).thenReturn(testPage);

        Page<Career> page = careerService.getAllCareers(null, 1, 2);

        assertNotNull(page);
        assertEquals(testPage, page);
    }
    @Test
    public void testGetAllCareersEmptyFilter(){
        Page<Career> testPage = new Page<Career>(List.of(CAREER), 1, 1);
        Mockito.when(
            careerDao.getAllCareers(Mockito.eq(1), Mockito.eq(2))
        ).thenReturn(testPage);

        Page<Career> page = careerService.getAllCareers("", 1, 2);

        assertNotNull(page);
        assertEquals(testPage, page);
    }
    @Test
    public void testGetAllCareersFilter(){
        Page<Career> testPage = new Page<Career>(List.of(CAREER), 1, 1);
        Mockito.when(
            careerDao.searchBySubstring(Mockito.eq(NAME), Mockito.eq(1), Mockito.eq(2))
        ).thenReturn(testPage);

        Page<Career> page = careerService.getAllCareers(NAME, 1, 2);

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
    public void update(){
        Mockito.when(
            careerDao.update(Mockito.eq(ID), Mockito.eq(NAME))
        ).thenReturn(CAREER);

        Career career = careerService.update(ID, NAME);

        assertNotNull(career);
        assertEquals(CAREER, career);
    }
    @Test(expected = DataIntegrityViolationException.class)
    public void updateDuplicated(){
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
