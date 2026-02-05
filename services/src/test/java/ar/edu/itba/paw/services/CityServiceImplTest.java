package ar.edu.itba.paw.services;

import java.util.List;
import java.util.Optional;

import ar.edu.itba.paw.models.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import ar.edu.itba.paw.interfaces.persistence.CityDao;
import ar.edu.itba.paw.interfaces.services.CountryService;
import ar.edu.itba.paw.models.exceptions.CityNotFoundException;
import ar.edu.itba.paw.models.exceptions.InvalidReferenceException;

@RunWith(MockitoJUnitRunner.class)
public class CityServiceImplTest {

    private static final String CITY_1_NAME = "city";
    private static final String CITY_2_NAME = "city2";
    private static final String COUNTRY_1_NAME = "cuntry";
    private static final String COUNTRY_2_NAME = "cuntry2";
    private static final String COUNTRY_CODE = "cu";
    private static final long CITY_1_ID = 0;
    private static final long COUNTRY_ID = 0;
    private static final Country COUNTRY_1 = new Country(COUNTRY_ID, COUNTRY_1_NAME, COUNTRY_CODE);
    private static final Country COUNTRY_2 = new Country(COUNTRY_ID, COUNTRY_2_NAME, COUNTRY_CODE);
    private static final PageParams PAGE_PARAMS = new PageParams(1, 10);
    private static City city;
    private static Page<City> cityPage;

    @InjectMocks
    CityServiceImpl cityService;

    @Mock
    CityDao cityDao;
    @Mock
    CountryService countryService;

    @Before
    public void init(){
        city = new City(CITY_1_NAME, COUNTRY_1, CITY_1_ID);
        cityPage = new Page<>(List.of(city), 1, 10, 1);
    }

    @Test
    public void testFindCityByName(){
        when(
            cityDao.findByName(CITY_1_NAME)
        ).thenReturn(Optional.of(city));

        Optional<City> maybeCity = cityService.findCityByName(CITY_1_NAME);

        assertNotNull(maybeCity);
        assertTrue(maybeCity.isPresent());
        assertEquals(CITY_1_NAME, maybeCity.get().getName());
    }
    @Test
    public void testFindCityByNameNotFound(){
        when(
            cityDao.findByName("")
        ).thenReturn(Optional.empty());

        Optional<City> maybeCity = cityService.findCityByName("");

        assertNotNull(maybeCity);
        assertTrue(maybeCity.isEmpty());
    }

    @Test
    public void testFindCityById(){
        when(
            cityDao.findById(CITY_1_ID)
        ).thenReturn(Optional.of(city));

        Optional<City> maybeCity = cityService.findCityById(CITY_1_ID);

        assertNotNull(maybeCity);
        assertTrue(maybeCity.isPresent());
        assertEquals(CITY_1_NAME, maybeCity.get().getName());
    }
    @Test
    public void testFindCityByIdNotFound(){
        when(
            cityDao.findById(CITY_1_ID)
        ).thenReturn(Optional.empty());

        Optional<City> maybeCity = cityService.findCityById(CITY_1_ID);

        assertNotNull(maybeCity);
        assertTrue(maybeCity.isEmpty());
    }

    @Test
    public void testSearchCities(){
        when(
            cityDao.search(CITY_1_NAME, PAGE_PARAMS)
        ).thenReturn(cityPage);

        Page<City> searchPage = cityService.searchCities(CITY_1_NAME, PAGE_PARAMS);

        assertEquals(CITY_1_NAME, searchPage.getContent().getFirst().getName());
    }
    @Test
    public void testSearchCitiesEmpty(){
        when(
            cityDao.findAll(PAGE_PARAMS)
        ).thenReturn(cityPage);

        Page<City> searchPage = cityService.searchCities("", PAGE_PARAMS);

        assertEquals(CITY_1_NAME, searchPage.getContent().getFirst().getName());
    }
    @Test
    public void testSearchCitiesMissing(){
        when(
            cityDao.findAll(PAGE_PARAMS)
        ).thenReturn(cityPage);

        Page<City> searchPage = cityService.searchCities(null, PAGE_PARAMS);

        assertEquals(CITY_1_NAME, searchPage.getContent().getFirst().getName());
    }

    @Test
    public void testUpdateCity(){
        when(
            countryService.findCountryByName(eq(COUNTRY_2_NAME))
        ).thenReturn(Optional.of(COUNTRY_2));
        when(
            cityDao.findById(eq(CITY_1_ID))
        ).thenReturn(Optional.of(city));

        City updated = cityService.updateCity(CITY_1_ID, CITY_2_NAME, COUNTRY_2_NAME);

        assertEquals(CITY_2_NAME, updated.getName());
        assertEquals(COUNTRY_2_NAME, updated.getCountry().getName());
    }
    @Test(expected = CityNotFoundException.class)
    public void testUpdateCityNotFound(){
        when(
            countryService.findCountryByName(eq(COUNTRY_1_NAME))
        ).thenReturn(Optional.of(COUNTRY_1));
        when(
            cityDao.findById(eq(CITY_1_ID))
        ).thenReturn(Optional.empty());

        cityService.updateCity(CITY_1_ID, CITY_1_NAME, COUNTRY_1_NAME);
    }
    @Test(expected = InvalidReferenceException.class)
    public void testUpdateCityMissingCountry(){
        when(
            countryService.findCountryByName(eq(COUNTRY_1_NAME))
        ).thenReturn(Optional.empty());

        cityService.updateCity(CITY_1_ID, CITY_1_NAME, COUNTRY_1_NAME);
    }

    @Test
    public void testPatchCity(){
        when(
            cityDao.findById(CITY_1_ID)
        ).thenReturn(Optional.of(city));
        when(
            countryService.findCountryByName(COUNTRY_2_NAME)
        ).thenReturn(Optional.of(COUNTRY_2));

        City patched = cityService.patchCity(CITY_1_ID, CITY_2_NAME, COUNTRY_2_NAME);

        assertEquals(COUNTRY_2_NAME, patched.getCountry().getName());
        assertEquals(CITY_2_NAME, patched.getName());
    }
    @Test
    public void testPatchCityOnlyCountry(){
        when(
            cityDao.findById(CITY_1_ID)
        ).thenReturn(Optional.of(city));
        when(
            countryService.findCountryByName(COUNTRY_2_NAME)
        ).thenReturn(Optional.of(COUNTRY_2));

        City patched = cityService.patchCity(CITY_1_ID, null, COUNTRY_2_NAME);

        assertEquals(COUNTRY_2_NAME, patched.getCountry().getName());
        assertEquals(CITY_1_NAME, patched.getName());
    }
    @Test
    public void testPatchCityOnlyName(){
        when(
            cityDao.findById(CITY_1_ID)
        ).thenReturn(Optional.of(city));

        City patched = cityService.patchCity(CITY_1_ID, CITY_2_NAME, null);

        assertEquals(COUNTRY_1_NAME, patched.getCountry().getName());
        assertEquals(CITY_2_NAME, patched.getName());
    }
    @Test(expected = InvalidReferenceException.class)
    public void testPatchCityCountryNotFound(){
        when(
            cityDao.findById(CITY_1_ID)
        ).thenReturn(Optional.of(city));
        when(
            countryService.findCountryByName(COUNTRY_2_NAME)
        ).thenReturn(Optional.empty());

        cityService.patchCity(CITY_1_ID, CITY_2_NAME, COUNTRY_2_NAME);
    }
    @Test(expected = CityNotFoundException.class)
    public void testPatchCityNotFound(){
        when(
            cityDao.findById(CITY_1_ID)
        ).thenReturn(Optional.empty());

        cityService.patchCity(CITY_1_ID, CITY_2_NAME, COUNTRY_2_NAME);
    }

    @Test
    public void testCreateCity(){
        when(
            countryService.findCountryByName(COUNTRY_1_NAME)
        ).thenReturn(Optional.of(COUNTRY_1));
        when(
            cityDao.create(CITY_1_NAME, COUNTRY_1)
        ).thenReturn(city);

        City newCity = cityService.createCity(CITY_1_NAME, COUNTRY_1_NAME);

        assertNotNull(newCity);
        assertEquals(CITY_1_NAME, newCity.getName());
        assertEquals(COUNTRY_1_NAME, newCity.getCountry().getName());
    }
    @Test(expected = InvalidReferenceException.class)
    public void testCreateCityMissingCountry(){
        when(
            countryService.findCountryByName(COUNTRY_1_NAME)
        ).thenReturn(Optional.empty());

        cityService.createCity(CITY_1_NAME, COUNTRY_1_NAME);
    }

    @Test
    public void testDeleteCity(){
        when(
            cityDao.findById(CITY_1_ID)
        ).thenReturn(Optional.of(city));

        cityService.deleteCity(CITY_1_ID);

        assertTrue(city.isDeleted());
    }
    @Test
    public void testDeleteCityNotFound(){
        when(
            cityDao.findById(CITY_1_ID)
        ).thenReturn(Optional.empty());

        cityService.deleteCity(CITY_1_ID);

        assertFalse(city.isDeleted());
    }
}
