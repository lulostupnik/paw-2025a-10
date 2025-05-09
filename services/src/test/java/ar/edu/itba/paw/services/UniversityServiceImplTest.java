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

import ar.edu.itba.paw.interfaces.persistence.UniversityDao;
import ar.edu.itba.paw.interfaces.services.CityService;
import ar.edu.itba.paw.models.City;
import ar.edu.itba.paw.models.Page;
import ar.edu.itba.paw.models.University;

@RunWith(MockitoJUnitRunner.class)
public class UniversityServiceImplTest {

    private static final String NAME = "uni";
    private static final long ID = 0;
    private static final String ABBREVIATION = "ab";
    private static final String CITY_NAME = "citi";
    private static final City CITY = new City(CITY_NAME, NAME, ID);
    private static final University UNI = new University(ID, NAME, ABBREVIATION, CITY);
    private static final PageParams PAGE_1_DEFAULT = new PageParams(1, 2);

    @InjectMocks
    private UniversityServiceImpl uniService;

    @Mock
    private UniversityDao uniDao;

    @Mock
    private CityService cityService;

    @Test
    public void testFindByName(){
        Mockito.when(
            uniDao.findByName(Mockito.eq(NAME))
        ).thenReturn(Optional.of(UNI));

        Optional<University> maybeUni = uniService.findByName(NAME);

        assertNotNull(maybeUni);
        assertTrue(maybeUni.isPresent());
        assertEquals(UNI, maybeUni.get());
    }
    @Test
    public void testFindByNameNotFound(){
        Mockito.when(
            uniDao.findByName(Mockito.eq(NAME))
        ).thenReturn(Optional.empty());

        Optional<University> maybeUni = uniService.findByName(NAME);

        assertNotNull(maybeUni);
        assertFalse(maybeUni.isPresent());
    }

    @Test
    public void testFindByAbbreviation(){
        Mockito.when(
            uniDao.findByAbbreviation(Mockito.eq(ABBREVIATION))
        ).thenReturn(Optional.of(UNI));

        Optional<University> maybeUni = uniService.findByAbbreviation(ABBREVIATION);

        assertNotNull(maybeUni);
        assertTrue(maybeUni.isPresent());
        assertEquals(UNI, maybeUni.get());
    }
    @Test
    public void testFindByAbbreviationNotFound(){
        Mockito.when(
            uniDao.findByAbbreviation(Mockito.eq(ABBREVIATION))
        ).thenReturn(Optional.empty());

        Optional<University> maybeUni = uniService.findByAbbreviation(ABBREVIATION);

        assertNotNull(maybeUni);
        assertFalse(maybeUni.isPresent());
    }

    @Test
    public void testFindById(){
        Mockito.when(
            uniDao.findById(Mockito.eq(ID))
        ).thenReturn(Optional.of(UNI));

        Optional<University> maybeUni = uniService.findById(ID);

        assertNotNull(maybeUni);
        assertTrue(maybeUni.isPresent());
        assertEquals(UNI, maybeUni.get());
    }
    @Test
    public void testFindByIdNotFound(){
        Mockito.when(
            uniDao.findById(Mockito.eq(ID))
        ).thenReturn(Optional.empty());

        Optional<University> maybeUni = uniService.findById(ID);

        assertNotNull(maybeUni);
        assertFalse(maybeUni.isPresent());
    }

    @Test
    public void testFindByAny(){
        Mockito.when(
            uniDao.findByAny(Mockito.eq(NAME))
        ).thenReturn(Optional.of(UNI));

        Optional<University> maybeUni = uniService.findByAny(NAME);

        assertNotNull(maybeUni);
        assertTrue(maybeUni.isPresent());
        assertEquals(UNI, maybeUni.get());
    }
    @Test
    public void testFindByAnyNotFound(){
        Mockito.when(
            uniDao.findByAny(Mockito.eq(NAME))
        ).thenReturn(Optional.empty());

        Optional<University> maybeUni = uniService.findByAny(NAME);

        assertNotNull(maybeUni);
        assertFalse(maybeUni.isPresent());
    }
    
    @Test
    public void testSearchUniversities(){
        Page<University> testPage = new Page<University>(List.of(UNI), 1, 1);
        Mockito.when(
            uniDao.searchBySubstring(Mockito.eq(NAME), Mockito.eq(PAGE_1_DEFAULT))
        ).thenReturn(testPage);

        Page<University> unis = uniService.searchUniversities(NAME, PAGE_1_DEFAULT);

        assertNotNull(unis);
        assertNotNull(unis.getContent());
        assertEquals(1, unis.getCurrentPage());
        assertEquals(1, unis.getTotalPages());
        assertEquals(1, unis.getContent().size());
        assertEquals(UNI, unis.getContent().getFirst());
    }
    @Test
    public void testSearchUniversitiesNotFound(){
        Page<University> testPage = new Page<University>(List.of(), 1, 1);
        Mockito.when(
            uniDao.searchBySubstring(Mockito.eq(NAME), Mockito.eq(PAGE_1_DEFAULT))
        ).thenReturn(testPage);

        Page<University> unis = uniService.searchUniversities(NAME, PAGE_1_DEFAULT);

        assertNotNull(unis);
        assertNotNull(unis.getContent());
        assertEquals(1, unis.getCurrentPage());
        assertEquals(1, unis.getTotalPages());
        assertEquals(0, unis.getContent().size());
    }


    @Test
    public void testGetAllUniversitiesPagedMissingQuery(){
        Page<University> testPage = new Page<University>(List.of(UNI), 1, 1);
        Mockito.when(
            uniDao.getAllUniversities(Mockito.eq(PAGE_1_DEFAULT))
        ).thenReturn(testPage);

        Page<University> unis = uniService.getAllUniversities(null, PAGE_1_DEFAULT);

        assertNotNull(unis);
        assertNotNull(unis.getContent());
        assertEquals(1, unis.getCurrentPage());
        assertEquals(1, unis.getTotalPages());
        assertEquals(1, unis.getContent().size());
        assertEquals(UNI, unis.getContent().getFirst());
    }
    @Test
    public void testGetAllUniversitiesPagedEmptyQuery(){
        Page<University> testPage = new Page<University>(List.of(UNI), 1, 1);
        Mockito.when(
            uniDao.getAllUniversities(Mockito.eq(PAGE_1_DEFAULT))
        ).thenReturn(testPage);

        Page<University> unis = uniService.getAllUniversities("", PAGE_1_DEFAULT);

        assertNotNull(unis);
        assertNotNull(unis.getContent());
        assertEquals(1, unis.getCurrentPage());
        assertEquals(1, unis.getTotalPages());
        assertEquals(1, unis.getContent().size());
        assertEquals(UNI, unis.getContent().getFirst());
    }
    @Test
    public void testGetAllUniversitiesPagedQuery(){
        Page<University> testPage = new Page<University>(List.of(UNI), 1, 1);
        Mockito.when(
            uniDao.searchBySubstring(Mockito.eq(NAME), Mockito.eq(PAGE_1_DEFAULT))
        ).thenReturn(testPage);

        Page<University> unis = uniService.getAllUniversities(NAME, PAGE_1_DEFAULT);

        assertNotNull(unis);
        assertNotNull(unis.getContent());
        assertEquals(1, unis.getCurrentPage());
        assertEquals(1, unis.getTotalPages());
        assertEquals(1, unis.getContent().size());
        assertEquals(UNI, unis.getContent().getFirst());
    }

    @Test
    public void testCreateUniversity(){
        uniService.createUniversity(NAME, ABBREVIATION, CITY_NAME);
    }
    @Test(expected = DataIntegrityViolationException.class)
    public void testCreateUniversityDuplicated(){
        Mockito.doThrow(new DataIntegrityViolationException("error")).when(uniDao).createUniversity(NAME, ABBREVIATION, CITY_NAME);
        
        uniService.createUniversity(NAME, ABBREVIATION, CITY_NAME);
    }

    @Test
    public void testUpdateUniversity(){
        uniService.updateUniversity(ID, NAME, ABBREVIATION, CITY_NAME);
    }

    @Test
    public void testDeleteUniversity(){
        uniService.delete(ID);
    }
}
