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

import ar.edu.itba.paw.interfaces.persistence.UniversityDao;
import ar.edu.itba.paw.interfaces.services.CityService;
import ar.edu.itba.paw.models.City;
import ar.edu.itba.paw.models.Page;
import ar.edu.itba.paw.models.PageParams;
import ar.edu.itba.paw.models.University;
import ar.edu.itba.paw.models.exceptions.CityNotFoundException;

@RunWith(MockitoJUnitRunner.class)
public class UniversityServiceImplTest {

    private static final String NAME = "uni";
    private static final long ID_1 = 0;
    private static final long ID_2 = 1;
    private static final String ABBREVIATION = "ab";
    private static final String CITY_NAME = "citi";
    private static final City CITY = new City(CITY_NAME, NAME, ID_1);
    private static final University UNI_1 = new University(ID_1, NAME, ABBREVIATION, CITY);
    private static final University UNI_2 = new University(ID_2, null, null, null);
    private static final PageParams PAGE_1_DEFAULT = new PageParams(1, 2);
    private static final Page<University> UNI_PAGE = new Page<>(List.of(UNI_1, UNI_2), 1, 1);
    private static final String UNI_JSON = "[{\"id\":0,\"name\":\"uni\",\"abbreviation\":\"ab\",\"city\":\"citi\"},{\"id\":1,\"name\":\"\",\"abbreviation\":\"\",\"city\":\"\"}]";
    private static final String EMPTY_JSON = "[]";

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
        ).thenReturn(Optional.of(UNI_1));

        Optional<University> maybeUni = uniService.findByName(NAME);

        assertNotNull(maybeUni);
        assertTrue(maybeUni.isPresent());
        assertEquals(UNI_1, maybeUni.get());
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
    public void testFindById(){
        Mockito.when(
            uniDao.findById(Mockito.eq(ID_1))
        ).thenReturn(Optional.of(UNI_1));

        Optional<University> maybeUni = uniService.findById(ID_1);

        assertNotNull(maybeUni);
        assertTrue(maybeUni.isPresent());
        assertEquals(UNI_1, maybeUni.get());
    }
    @Test
    public void testFindByIdNotFound(){
        Mockito.when(
            uniDao.findById(Mockito.eq(ID_1))
        ).thenReturn(Optional.empty());

        Optional<University> maybeUni = uniService.findById(ID_1);

        assertNotNull(maybeUni);
        assertFalse(maybeUni.isPresent());
    }


    @Test
    public void testGetAllUniversitiesPagedMissingQuery(){
        Page<University> testPage = new Page<University>(List.of(UNI_1), 1, 1);
        Mockito.when(
            uniDao.findAll(Mockito.eq(PAGE_1_DEFAULT))
        ).thenReturn(testPage);

        Page<University> unis = uniService.getAllUniversities(null, PAGE_1_DEFAULT);

        assertNotNull(unis);
        assertNotNull(unis.getContent());
        assertEquals(1, unis.getCurrentPage());
        assertEquals(1, unis.getTotalPages());
        assertEquals(1, unis.getContent().size());
        assertEquals(UNI_1, unis.getContent().getFirst());
    }
    @Test
    public void testGetAllUniversitiesPagedEmptyQuery(){
        Page<University> testPage = new Page<University>(List.of(UNI_1), 1, 1);
        Mockito.when(
            uniDao.findAll(Mockito.eq(PAGE_1_DEFAULT))
        ).thenReturn(testPage);

        Page<University> unis = uniService.getAllUniversities("", PAGE_1_DEFAULT);

        assertNotNull(unis);
        assertNotNull(unis.getContent());
        assertEquals(1, unis.getCurrentPage());
        assertEquals(1, unis.getTotalPages());
        assertEquals(1, unis.getContent().size());
        assertEquals(UNI_1, unis.getContent().getFirst());
    }
    @Test
    public void testGetAllUniversitiesPagedQuery(){
        Page<University> testPage = new Page<University>(List.of(UNI_1), 1, 1);
        Mockito.when(
            uniDao.search(Mockito.eq(NAME), Mockito.eq(PAGE_1_DEFAULT))
        ).thenReturn(testPage);

        Page<University> unis = uniService.getAllUniversities(NAME, PAGE_1_DEFAULT);

        assertNotNull(unis);
        assertNotNull(unis.getContent());
        assertEquals(1, unis.getCurrentPage());
        assertEquals(1, unis.getTotalPages());
        assertEquals(1, unis.getContent().size());
        assertEquals(UNI_1, unis.getContent().getFirst());
    }

    @Test
    public void testCreateUniversity(){
        Mockito.when(
            cityService.findByName(Mockito.eq(CITY_NAME))
        ).thenReturn(Optional.of(CITY));
        Mockito.when(
            uniDao.create(Mockito.eq(NAME), Mockito.eq(ABBREVIATION), Mockito.eq(CITY))
        ).thenReturn(UNI_1);

        University uni = uniService.createUniversity(NAME, ABBREVIATION, CITY_NAME);

        assertNotNull(uni);
        assertEquals(UNI_1, uni);
    }
    @Test(expected = CityNotFoundException.class)
    public void testCreateUniversityCityNotFound(){
        Mockito.when(
            cityService.findByName(Mockito.eq(CITY_NAME))
        ).thenReturn(Optional.empty());

        uniService.createUniversity(NAME, ABBREVIATION, CITY_NAME);
    }

    @Test
    public void testUpdateUniversity(){
        uniService.updateUniversity(ID_1, NAME, ABBREVIATION, CITY_NAME);
    }

    @Test
    public void testDeleteUniversity(){
        uniService.delete(ID_1);
    }

}

//
//    @Test
//    public void testFindByAbbreviation(){
//        Mockito.when(
//            uniDao.findByAbbreviation(Mockito.eq(ABBREVIATION))
//        ).thenReturn(Optional.of(UNI));
//
//        Optional<University> maybeUni = uniService.findByAbbreviation(ABBREVIATION);
//
//        assertNotNull(maybeUni);
//        assertTrue(maybeUni.isPresent());
//        assertEquals(UNI, maybeUni.get());
//    }
//    @Test
//    public void testFindByAbbreviationNotFound(){
//        Mockito.when(
//            uniDao.findByAbbreviation(Mockito.eq(ABBREVIATION))
//        ).thenReturn(Optional.empty());
//
//        Optional<University> maybeUni = uniService.findByAbbreviation(ABBREVIATION);
//
//        assertNotNull(maybeUni);
//        assertFalse(maybeUni.isPresent());
//    }


//    @Test
//    public void testSearchUniversitiesNotFound(){
//        Page<University> testPage = new Page<University>(List.of(), 1, 1);
//        Mockito.when(
//            uniDao.searchBySubstring(Mockito.eq(NAME), Mockito.eq(PAGE_1_DEFAULT))
//        ).thenReturn(testPage);
//
//        Page<University> unis = uniService.searchUniversities(NAME, PAGE_1_DEFAULT);
//
//        assertNotNull(unis);
//        assertNotNull(unis.getContent());
//        assertEquals(1, unis.getCurrentPage());
//        assertEquals(1, unis.getTotalPages());
//        assertEquals(0, unis.getContent().size());
//    }

//
//@Test
//public void testFindByAny(){
//    Mockito.when(
//            uniDao.findByAny(Mockito.eq(NAME))
//    ).thenReturn(Optional.of(UNI));
//
//    Optional<University> maybeUni = uniService.findByAny(NAME);
//
//    assertNotNull(maybeUni);
//    assertTrue(maybeUni.isPresent());
//    assertEquals(UNI, maybeUni.get());
//}
//@Test
//public void testFindByAnyNotFound(){
//    Mockito.when(
//            uniDao.findByAny(Mockito.eq(NAME))
//    ).thenReturn(Optional.empty());
//
//    Optional<University> maybeUni = uniService.findByAny(NAME);
//
//    assertNotNull(maybeUni);
//    assertFalse(maybeUni.isPresent());
//}
//
//@Test
//public void testSearchUniversities(){
//    Page<University> testPage = new Page<University>(List.of(UNI), 1, 1);
//    Mockito.when(
//            uniDao.searchBySubstring(Mockito.eq(NAME), Mockito.eq(PAGE_1_DEFAULT))
//    ).thenReturn(testPage);
//
//    Page<University> unis = uniService.searchUniversities(NAME, PAGE_1_DEFAULT);
//
//    assertNotNull(unis);
//    assertNotNull(unis.getContent());
//    assertEquals(1, unis.getCurrentPage());
//    assertEquals(1, unis.getTotalPages());
//    assertEquals(1, unis.getContent().size());
//    assertEquals(UNI, unis.getContent().getFirst());
//}
