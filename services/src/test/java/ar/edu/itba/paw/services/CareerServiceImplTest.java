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
    private static final long ID_1 = 0;
    private static final long ID_2 = 1;
    private static final Career CAREER_1 = new Career(ID_1, NAME);
    private static final Career CAREER_2 = new Career(ID_2, null);
    private static final List<Career> CAREERS = List.of(CAREER_1, CAREER_2);
    private static final Page<Career> CAREER_PAGE = new Page<Career>(CAREERS, 1, 1);
    private static final String CAREERS_JSON = "[{\"name\":\"career\",\"id\":0},{\"name\":\"\",\"id\":1}]";
    private static final String EMPTY_JSON = "[]";
    private static final PageParams PAGE_1_DEFAULT = new PageParams(1, 2);
    @InjectMocks
    private CareerServiceImpl careerService;

    @Mock
    private CareerDao careerDao;

    @Test
    public void testFindById(){
        Mockito.when(
            careerDao.findById(Mockito.eq(ID_1))
        ).thenReturn(Optional.of(CAREER_1));

        Optional<Career> maybeCareer = careerService.findById(ID_1);

        assertNotNull(maybeCareer);
        assertTrue(maybeCareer.isPresent());
        assertEquals(CAREER_1, maybeCareer.get());
    }
    @Test
    public void testFindByIdMissing(){
        Mockito.when(
            careerDao.findById(Mockito.eq(ID_1))
        ).thenReturn(Optional.empty());

        Optional<Career> maybeCareer = careerService.findById(ID_1);

        assertNotNull(maybeCareer);
        assertFalse(maybeCareer.isPresent());
    }

    @Test
    public void testFindByName(){
        Mockito.when(
            careerDao.findByName(Mockito.eq(NAME))
        ).thenReturn(Optional.of(CAREER_1));

        Optional<Career> maybeCareer = careerService.findByName(NAME);

        assertNotNull(maybeCareer);
        assertTrue(maybeCareer.isPresent());
        assertEquals(CAREER_1, maybeCareer.get());
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
        Page<Career> testPage = new Page<Career>(List.of(CAREER_1), 1, 1);
        Mockito.when(
            careerDao.findAll(Mockito.eq(PAGE_1_DEFAULT))
        ).thenReturn(testPage);

        Page<Career> page = careerService.getAllCareers("", PAGE_1_DEFAULT);

        assertNotNull(page);
        assertEquals(testPage, page);
    }
    @Test
    public void testGetAllCareersFilter(){
        Page<Career> testPage = new Page<Career>(List.of(CAREER_1), 1, 1);
        Mockito.when(
            careerDao.search(Mockito.eq(NAME), Mockito.eq(PAGE_1_DEFAULT))
        ).thenReturn(testPage);

        Page<Career> page = careerService.getAllCareers(NAME, PAGE_1_DEFAULT);

        assertNotNull(page);
        assertEquals(testPage, page);
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
            careerDao.update(Mockito.eq(ID_1), Mockito.eq(NAME))
        ).thenReturn(CAREER_1);

        Career career = careerService.update(ID_1, NAME);

        assertNotNull(career);
        assertEquals(CAREER_1, career);
    }
    @Test(expected = DataIntegrityViolationException.class)
    public void testUpdateDuplicated(){
        Mockito.when(
            careerDao.update(Mockito.eq(ID_1), Mockito.eq(NAME))
        ).thenThrow(new DataIntegrityViolationException("NAME"));

        Career career = careerService.update(ID_1, NAME);

        assertNotNull(career);
        assertEquals(CAREER_1, career);
    }

    @Test
    public void testDelete(){
        careerService.delete(ID_1);
    }

    @Test
    public void testGetCareersJSON(){
        Mockito.when(
            careerDao.search(Mockito.eq(NAME), Mockito.eq(PAGE_1_DEFAULT))
        ).thenReturn(CAREER_PAGE);

        String json_derulo = careerService.getCareersJSON(NAME, PAGE_1_DEFAULT);

        assertNotNull(json_derulo);
        assertEquals(CAREERS_JSON, json_derulo);
    }
    @Test
    public void testGetCareersJSONMissingQuery(){
        Mockito.when(
            careerDao.findAll(Mockito.eq(PAGE_1_DEFAULT))
        ).thenReturn(CAREER_PAGE);

        String json_derulo = careerService.getCareersJSON(null, PAGE_1_DEFAULT);

        assertNotNull(json_derulo);
        assertEquals(CAREERS_JSON, json_derulo);
    }
    @Test
    public void testGetCareersJSONEmptyQuery(){
        Mockito.when(
            careerDao.findAll(Mockito.eq(PAGE_1_DEFAULT))
        ).thenReturn(CAREER_PAGE);

        String json_derulo = careerService.getCareersJSON("", PAGE_1_DEFAULT);

        assertNotNull(json_derulo);
        assertEquals(CAREERS_JSON, json_derulo);
    }
    @Test
    public void testGetCareersJSONNoCareers(){
        Mockito.when(
            careerDao.search(Mockito.eq(NAME), Mockito.eq(PAGE_1_DEFAULT))
        ).thenReturn(new Page<Career>(List.of(), 1, 0));

        String json_derulo = careerService.getCareersJSON(NAME, PAGE_1_DEFAULT);

        assertNotNull(json_derulo);
        assertEquals(EMPTY_JSON, json_derulo);
    }
}
