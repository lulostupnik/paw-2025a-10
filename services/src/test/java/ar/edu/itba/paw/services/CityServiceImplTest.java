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

import ar.edu.itba.paw.interfaces.persistence.CityDao;
import ar.edu.itba.paw.interfaces.services.CountryService;
import ar.edu.itba.paw.models.City;
import ar.edu.itba.paw.models.Country;
import ar.edu.itba.paw.models.Page;

@RunWith(MockitoJUnitRunner.class)
public class CityServiceImplTest {

    private static final String NAME = "city";
    private static final String COUNTRY_NAME = "cuntry";
    private static final long ID = 0;
    private static final City CITY = new City(NAME, COUNTRY_NAME, ID);
    private static final Country COUNTRY = new Country(ID, COUNTRY_NAME, COUNTRY_NAME);
    private static final PageParams PAGE_1_DEFAULT = new PageParams(1, 2);


    @InjectMocks
    CityServiceImpl cityService;

    @Mock
    CityDao cityDao;
    @Mock
    CountryService countryService;

    @Test
    public void testFindByName(){
        Mockito.when(
            cityDao.findByName(Mockito.eq(NAME))
        ).thenReturn(Optional.of(CITY));

        Optional<City> maybeCity = cityService.findByName(NAME);

        assertNotNull(maybeCity);
        assertTrue(maybeCity.isPresent());
        assertEquals(CITY, maybeCity.get());
    }
    @Test
    public void testFindByNameNotFound(){
        Mockito.when(
            cityDao.findByName(Mockito.eq(NAME))
        ).thenReturn(Optional.empty());

        Optional<City> maybeCity = cityService.findByName(NAME);

        assertNotNull(maybeCity);
        assertFalse(maybeCity.isPresent());
    }

    @Test
    public void testFindById(){
        Mockito.when(
            cityDao.findBy(Mockito.eq(ID), Mockito.eq(null), Mockito.eq(null))
        ).thenReturn(Optional.of(CITY));

        Optional<City> maybeCity = cityService.findById(ID);

        assertNotNull(maybeCity);
        assertTrue(maybeCity.isPresent());
        assertEquals(CITY, maybeCity.get());
    }
    @Test
    public void testFindByIdNotFound(){
        Mockito.when(
            cityDao.findBy(Mockito.eq(ID), Mockito.eq(null), Mockito.eq(null))
        ).thenReturn(Optional.empty());

        Optional<City> maybeCity = cityService.findById(ID);

        assertNotNull(maybeCity);
        assertFalse(maybeCity.isPresent());
    }

    @Test
    public void testGetAllCitiesPagedMissingQuery(){
        Page<City> testPage = new Page<City>(List.of(CITY), 1, 1);
        Mockito.when(
            cityDao.getAllCities(Mockito.eq(PAGE_1_DEFAULT))
        ).thenReturn(testPage);

        Page<City> cities = cityService.getAllCities(null, PAGE_1_DEFAULT);

        assertNotNull(cities);
        assertEquals(testPage, cities);
    }
    @Test
    public void testGetAllCitiesPagedEmptyQuery(){
        Page<City> testPage = new Page<City>(List.of(CITY), 1, 1);
        Mockito.when(
            cityDao.getAllCities(Mockito.eq(PAGE_1_DEFAULT))
        ).thenReturn(testPage);

        Page<City> cities = cityService.getAllCities("", PAGE_1_DEFAULT);

        assertNotNull(cities);
        assertEquals(testPage, cities);
    }
    @Test
    public void testGetAllCitiesPagedQuery(){
        Page<City> testPage = new Page<City>(List.of(CITY), 1, 1);
        Mockito.when(
            cityDao.searchBySubstring(Mockito.eq(NAME), Mockito.eq(PAGE_1_DEFAULT))
        ).thenReturn(testPage);

        Page<City> cities = cityService.getAllCities(NAME, PAGE_1_DEFAULT);

        assertNotNull(cities);
        assertEquals(testPage, cities);
    }

    @Test
    public void testSearchBySubstring(){
        Page<City> testPage = new Page<City>(List.of(CITY), 1, 1);
        Mockito.when(
            cityDao.searchBySubstring(Mockito.eq(NAME), Mockito.eq(PAGE_1_DEFAULT))
        ).thenReturn(testPage);

        Page<City> cities = cityService.searchBySubstring(NAME, PAGE_1_DEFAULT);

        assertNotNull(cities);
        assertEquals(testPage, cities);
    }

    @Test
    public void testUpdateCity(){
        Mockito.when(
            countryService.findByName(Mockito.eq(COUNTRY_NAME))
        ).thenReturn(Optional.of(COUNTRY));

        cityService.updateCity(ID, NAME, COUNTRY_NAME);
    }
    @Test(expected = IllegalArgumentException.class)
    public void testUpdateCityMissingCountry(){
        Mockito.when(
            countryService.findByName(Mockito.eq(COUNTRY_NAME))
        ).thenReturn(Optional.empty());

        cityService.updateCity(ID, NAME, COUNTRY_NAME);
    }
    @Test(expected = DataIntegrityViolationException.class)
    public void testUpdateCityDuplicated(){
        Mockito.when(
            countryService.findByName(Mockito.eq(COUNTRY_NAME))
        ).thenReturn(Optional.of(COUNTRY));
        Mockito.doThrow(new DataIntegrityViolationException("data"))
            .when(cityDao).updateCity(Mockito.eq(ID), Mockito.eq(NAME), Mockito.eq(COUNTRY));

        cityService.updateCity(ID, NAME, COUNTRY_NAME);
    }

    @Test
    public void testCreateCity(){
        Mockito.when(
            countryService.findByName(Mockito.eq(COUNTRY_NAME))
        ).thenReturn(Optional.of(COUNTRY));

        cityService.createCity(NAME, COUNTRY_NAME);
    }
    @Test(expected = IllegalArgumentException.class)
    public void testCreateCityMissingCountry(){
        Mockito.when(
            countryService.findByName(Mockito.eq(COUNTRY_NAME))
        ).thenReturn(Optional.empty());

        cityService.createCity(NAME, COUNTRY_NAME);
    }
    @Test(expected = DataIntegrityViolationException.class)
    public void testCreateCityDuplicated(){
        Mockito.when(
            countryService.findByName(Mockito.eq(COUNTRY_NAME))
        ).thenReturn(Optional.of(COUNTRY));
        Mockito.doThrow(new DataIntegrityViolationException("data"))
            .when(cityDao).createCity(Mockito.eq(NAME), Mockito.eq(COUNTRY));

        cityService.createCity(NAME, COUNTRY_NAME);
    }

    @Test
    public void testDeleteCity(){
        cityService.delete(ID);
    }
}
