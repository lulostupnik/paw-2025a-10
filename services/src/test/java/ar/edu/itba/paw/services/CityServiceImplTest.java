package ar.edu.itba.paw.services;

import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.eq;
import static org.junit.Assert.*;

import ar.edu.itba.paw.interfaces.persistence.CityDao;
import ar.edu.itba.paw.interfaces.services.CountryService;
import ar.edu.itba.paw.models.City;
import ar.edu.itba.paw.models.Country;
import ar.edu.itba.paw.models.Page;
import ar.edu.itba.paw.models.PageParams;
import ar.edu.itba.paw.models.exceptions.CityNotFoundException;
import ar.edu.itba.paw.models.exceptions.CountryNotFoundException;

@RunWith(MockitoJUnitRunner.class)
public class CityServiceImplTest {

    private static final String CITY_1_NAME = "city";
    private static final String COUNTRY_NAME = "cuntry";
    private static final String COUNTRY_CODE = "cu";
    private static final long CITY_1_ID = 0;
    private static final long COUNTRY_ID = 0;
    private static final Country COUNTRY = new Country(COUNTRY_ID, COUNTRY_NAME, COUNTRY_CODE);
    private static final City CITY_1 = new City(CITY_1_NAME, COUNTRY, CITY_1_ID);
    private static final Optional<City> MAYBECITY_1 = Optional.of(CITY_1);
    private static final List<City> CITIES = List.of(CITY_1);
    private static final Page<City> CITIES_PAGE = new Page<>(CITIES, 1, 1);
    private static final PageParams PAGE_PARAMS = new PageParams(1, 10);

    @InjectMocks
    CityServiceImpl cityService;

    @Mock
    CityDao cityDao;
    @Mock
    CountryService countryService;

    @Test
    public void testFindCityByName(){
        when(
            cityDao.findByName(eq(CITY_1_NAME))
        ).thenReturn(MAYBECITY_1);

        Optional<City> maybeCity = cityService.findCityByName(CITY_1_NAME);
        
        assertNotNull(maybeCity);
        assertEquals(CITY_1, maybeCity.get());
    }

    @Test
    public void testFindCityById(){
        when(
            cityDao.findById(eq(CITY_1_ID))
        ).thenReturn(MAYBECITY_1);

        Optional<City> maybeCity = cityService.findCityById(CITY_1_ID);
        
        assertNotNull(maybeCity);
        assertEquals(CITY_1, maybeCity.get());
    }

    @Test
    public void testSearchCitiesWithQuery(){
        when(
            cityDao.search(eq(CITY_1_NAME), eq(PAGE_PARAMS))
        ).thenReturn(CITIES_PAGE);

        Page<City> cities = cityService.searchCities(CITY_1_NAME, PAGE_PARAMS);

        assertNotNull(cities);
        assertEquals(CITIES_PAGE, cities);
    }
    @Test
    public void testSearchCitiesEmptyQuery(){
        when(
            cityDao.findAll(eq(PAGE_PARAMS))
        ).thenReturn(CITIES_PAGE);

        Page<City> cities = cityService.searchCities("", PAGE_PARAMS);

        assertNotNull(cities);
        assertEquals(CITIES_PAGE, cities);
    }
    @Test
    public void testSearchCitiesMissingQuery(){
        when(
            cityDao.findAll(eq(PAGE_PARAMS))
        ).thenReturn(CITIES_PAGE);

        Page<City> cities = cityService.searchCities(null, PAGE_PARAMS);

        assertNotNull(cities);
        assertEquals(CITIES_PAGE, cities);
    }

    @Test
    public void testUpdateCity(){
        City newCity = new City("CITY_1_NAME", null, CITY_1_ID);
        when(
            countryService.findCountryByName(eq(COUNTRY_NAME))
        ).thenReturn(Optional.of(COUNTRY));
        when(
            cityDao.findById(eq(CITY_1_ID))
        ).thenReturn(Optional.of(newCity));

        cityService.updateCity(CITY_1_ID, CITY_1_NAME, COUNTRY_NAME);

        assertEquals(CITY_1_NAME, newCity.getName());
        assertEquals(COUNTRY_NAME, newCity.getCountry().getName());
    }
    @Test(expected = CityNotFoundException.class)
    public void testUpdateCityNotFound(){
        when(
            countryService.findCountryByName(eq(COUNTRY_NAME))
        ).thenReturn(Optional.of(COUNTRY));
        when(
            cityDao.findById(eq(CITY_1_ID))
        ).thenReturn(Optional.empty());

        cityService.updateCity(CITY_1_ID, CITY_1_NAME, COUNTRY_NAME);
    }
    @Test(expected = CountryNotFoundException.class)
    public void testUpdateCityMissingCountry(){
        when(
            countryService.findCountryByName(eq(COUNTRY_NAME))
        ).thenReturn(Optional.empty());

        cityService.updateCity(CITY_1_ID, CITY_1_NAME, COUNTRY_NAME);
    }

    @Test
    public void testCreateCity(){
        when(
            countryService.findCountryByName(eq(COUNTRY_NAME))
        ).thenReturn(Optional.of(COUNTRY));

        cityService.createCity(CITY_1_NAME, COUNTRY_NAME);
    }

    @Test(expected = CountryNotFoundException.class)
    public void testCreateCityMissingCountry(){
        Mockito.when(
            countryService.findCountryByName(Mockito.eq(COUNTRY_NAME))
        ).thenReturn(Optional.empty());

        cityService.createCity(CITY_1_NAME, COUNTRY_NAME);
    }

    @Test
    public void testDeleteCity(){
        cityService.deleteCity(CITY_1_ID);
    }

}
