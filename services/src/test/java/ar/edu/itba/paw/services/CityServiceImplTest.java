package ar.edu.itba.paw.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.Optional;

import ar.edu.itba.paw.models.PageParams;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import ar.edu.itba.paw.interfaces.persistence.CityDao;
import ar.edu.itba.paw.interfaces.services.CountryService;
import ar.edu.itba.paw.models.City;
import ar.edu.itba.paw.models.Country;
import ar.edu.itba.paw.models.Page;

@RunWith(MockitoJUnitRunner.class)
public class CityServiceImplTest {

    private static final String NAME = "city";
    private static final String COUNTRY_NAME = "cuntry";
    private static final long ID_1 = 0;
    private static final City CITY_1 = new City(NAME, COUNTRY_NAME, ID_1);
    private static final Country COUNTRY = new Country(ID_1, COUNTRY_NAME, COUNTRY_NAME);
    private static final PageParams PAGE_1_DEFAULT = new PageParams(1, 2);

    @InjectMocks
    CityServiceImpl cityService;

    @Mock
    CityDao cityDao;
    @Mock
    CountryService countryService;

    @Test
    public void testGetAllCitiesPagedMissingQuery(){
        Page<City> testPage = new Page<City>(List.of(CITY_1), 1, 1);
        Mockito.when(
            cityDao.findAll(Mockito.eq(PAGE_1_DEFAULT))
        ).thenReturn(testPage);

        Page<City> cities = cityService.getAllCities(null, PAGE_1_DEFAULT);

        assertNotNull(cities);
        assertEquals(testPage, cities);
    }
    @Test
    public void testGetAllCitiesPagedEmptyQuery(){
        Page<City> testPage = new Page<City>(List.of(CITY_1), 1, 1);
        Mockito.when(
            cityDao.findAll(Mockito.eq(PAGE_1_DEFAULT))
        ).thenReturn(testPage);

        Page<City> cities = cityService.getAllCities("", PAGE_1_DEFAULT);

        assertNotNull(cities);
        assertEquals(testPage, cities);
    }
    @Test
    public void testGetAllCitiesPagedQuery(){
        Page<City> testPage = new Page<City>(List.of(CITY_1), 1, 1);
        Mockito.when(
            cityDao.search(Mockito.eq(NAME), Mockito.eq(PAGE_1_DEFAULT))
        ).thenReturn(testPage);

        Page<City> cities = cityService.getAllCities(NAME, PAGE_1_DEFAULT);

        assertNotNull(cities);
        assertEquals(testPage, cities);
    }

    @Test
    public void testUpdateCity(){
        Mockito.when(
            countryService.findByName(Mockito.eq(COUNTRY_NAME))
        ).thenReturn(Optional.of(COUNTRY));

        cityService.updateCity(ID_1, NAME, COUNTRY_NAME);
    }
    @Test(expected = IllegalArgumentException.class)
    public void testUpdateCityMissingCountry(){
        Mockito.when(
            countryService.findByName(Mockito.eq(COUNTRY_NAME))
        ).thenReturn(Optional.empty());

        cityService.updateCity(ID_1, NAME, COUNTRY_NAME);
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

    // @Test
    // public void testDeleteCity(){
    //     cityService.delete(ID_1);
    // }

}
