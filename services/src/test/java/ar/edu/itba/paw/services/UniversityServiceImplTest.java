package ar.edu.itba.paw.services;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import ar.edu.itba.paw.models.Country;
import ar.edu.itba.paw.models.Page;
import ar.edu.itba.paw.models.PageParams;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import ar.edu.itba.paw.interfaces.persistence.UniversityDao;
import ar.edu.itba.paw.interfaces.services.CityService;
import ar.edu.itba.paw.models.City;
import ar.edu.itba.paw.models.University;
import ar.edu.itba.paw.models.exceptions.InvalidReferenceException;
import ar.edu.itba.paw.models.exceptions.UniversityNotFoundException;

@RunWith(MockitoJUnitRunner.class)
public class UniversityServiceImplTest {

    private static final String NAME = "uni";
    private static final long ID_1 = 0;
    private static final String ABBREVIATION = "ab";
    private static final String CITY_NAME = "citi";
    private static final String COUNTRY_NAME = "country";
    private static final String COUNTRY_ABBREVIATION = "countryAbbreviation";
    private static final Country COUNTRY = new Country(COUNTRY_NAME, COUNTRY_ABBREVIATION);
    private static final City CITY = new City(CITY_NAME, COUNTRY, ID_1);
    private static final University UNI_1 = new University(ID_1, NAME, ABBREVIATION, CITY);
    private static final List<University> UNIS = List.of(UNI_1);
    private static final Page<University> UNI_PAGE = new Page<>(UNIS, 1, 1, 1);
    private static final PageParams PAGE_PARAMS = new PageParams(1, 10);
    @InjectMocks
    private UniversityServiceImpl uniService;

    @Mock
    private UniversityDao uniDao;

    @Mock
    private CityService cityService;

    @Test
    public void testCreateUniversity(){
        when(
            cityService.findCityByName(eq(CITY_NAME))
        ).thenReturn(Optional.of(CITY));
        when(
            uniDao.create(eq(NAME), eq(ABBREVIATION), eq(CITY))
        ).thenReturn(UNI_1);

        University uni = uniService.createUniversity(NAME, ABBREVIATION, CITY_NAME);

        assertNotNull(uni);
        assertEquals(UNI_1, uni);
    }
    @Test(expected = InvalidReferenceException.class)
    public void testCreateUniversityCityNotFound(){
        when(
            cityService.findCityByName(eq(CITY_NAME))
        ).thenReturn(Optional.empty());

        uniService.createUniversity(NAME, ABBREVIATION, CITY_NAME);
    }

    @Test
    public void testFindUniversityByName(){
        when(
            uniDao.findByName(eq(NAME))
        ).thenReturn(Optional.of(UNI_1));

        Optional<University> maybeUni = uniService.findByName(NAME);

        assertNotNull(maybeUni);
        assertEquals(UNI_1, maybeUni.get());
    }

    @Test
    public void testFindUniversityById(){
        when(
            uniDao.findById(eq(ID_1))
        ).thenReturn(Optional.of(UNI_1));

        Optional<University> maybeUni = uniService.findById(ID_1);

        assertNotNull(maybeUni);
        assertEquals(UNI_1, maybeUni.get());
    }

    @Test
    public void testFindUniversitiesQuery(){
        when(
            uniDao.search(eq(NAME), any(PageParams.class))
        ).thenReturn(UNI_PAGE);

        Page<University> unis = uniService.findUniversities(NAME, PAGE_PARAMS);

        assertNotNull(unis);
        assertEquals(UNI_PAGE, unis);
    }
    @Test
    public void testFindUniversitiesEmptyQuery(){
        when(
            uniDao.findAll(any(PageParams.class))
        ).thenReturn(UNI_PAGE);

        Page<University> unis = uniService.findUniversities("", PAGE_PARAMS);

        assertNotNull(unis);
        assertEquals(UNI_PAGE, unis);
    }
    @Test
    public void testFindUniversitiesMissingQuery(){
        when(
            uniDao.findAll(any(PageParams.class))
        ).thenReturn(UNI_PAGE);

        Page<University> unis = uniService.findUniversities(null, PAGE_PARAMS);

        assertNotNull(unis);
        assertEquals(UNI_PAGE, unis);
    }

    @Test
    public void testUpdateUniversity(){
        University uni = new University(ID_1, null, null, null);
        when(
            cityService.findCityByName(eq(CITY_NAME))
        ).thenReturn(Optional.of(CITY));
        when(
            uniDao.findById(eq(ID_1))
        ).thenReturn(Optional.of(uni));

        uniService.updateUniversity(ID_1, NAME, ABBREVIATION, CITY_NAME);

        assertEquals(NAME, uni.getName());
        assertEquals(ABBREVIATION, uni.getAbbreviation());
        assertEquals(CITY_NAME, uni.getCity().getName());
    }
    @Test(expected = UniversityNotFoundException.class)
    public void testUpdateUniversityNotFound(){
        when(
            cityService.findCityByName(eq(CITY_NAME))
        ).thenReturn(Optional.of(CITY));
        when(
            uniDao.findById(eq(ID_1))
        ).thenReturn(Optional.empty());

        uniService.updateUniversity(ID_1, NAME, ABBREVIATION, CITY_NAME);
    }
    @Test(expected = InvalidReferenceException.class)
    public void testUpdateUniversityCityNotFound(){
        when(
            cityService.findCityByName(eq(CITY_NAME))
        ).thenReturn(Optional.empty());

        uniService.updateUniversity(ID_1, NAME, ABBREVIATION, CITY_NAME);
    }

    @Test
    public void testPatchUniversity(){
        University uni = new University(ID_1, null, null, null);
        when(
            uniDao.findById(eq(ID_1))
        ).thenReturn(Optional.of(uni));
        when(
            cityService.findCityByName(eq(CITY_NAME))
        ).thenReturn(Optional.of(CITY));

        University university = uniService.patchUniversity(ID_1, NAME, ABBREVIATION, CITY_NAME);

        assertNotNull(university);
        assertEquals(ABBREVIATION, university.getAbbreviation());
        assertEquals(CITY_NAME, university.getCity().getName());
        assertEquals(NAME, university.getName());
    }
    @Test
    public void testPatchUniversityNoPatches(){
        University uni = new University(ID_1, NAME, ABBREVIATION, CITY);
        when(
            uniDao.findById(eq(ID_1))
        ).thenReturn(Optional.of(uni));

        University university = uniService.patchUniversity(ID_1, null, null, null);

        assertNotNull(university);
        assertEquals(ABBREVIATION, university.getAbbreviation());
        assertEquals(CITY_NAME, university.getCity().getName());
        assertEquals(NAME, university.getName());
    }
    @Test(expected = InvalidReferenceException.class)
    public void testPatchUniversityMissingCity(){
        University uni = new University(ID_1, null, null, null);
        when(
            uniDao.findById(eq(ID_1))
        ).thenReturn(Optional.of(uni));
        when(
            cityService.findCityByName(eq(CITY_NAME))
        ).thenReturn(Optional.empty());

        uniService.patchUniversity(ID_1, NAME, ABBREVIATION, CITY_NAME);
    }
    @Test(expected = UniversityNotFoundException.class)
    public void testPatchUniversityMissingUni(){
        when(
            uniDao.findById(eq(ID_1))
        ).thenReturn(Optional.empty());

        uniService.patchUniversity(ID_1, NAME, ABBREVIATION, CITY_NAME);
    }

    @Test
    public void testDeleteUniversity(){
        University uni = new University(ID_1, null, null, null);
        when(uniDao.findById(eq(ID_1))).thenReturn(Optional.of(uni));

        uniService.deleteUniversity(ID_1);

        assertTrue(uni.isDeleted());
    }
    @Test
    public void testDeleteUniversityNotFound(){
        University uni = new University(ID_1, null, null, null);
        when(uniDao.findById(eq(ID_1))).thenReturn(Optional.empty());

        uniService.deleteUniversity(ID_1);

        assertFalse(uni.isDeleted());
    }
}