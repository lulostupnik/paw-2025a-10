package ar.edu.itba.paw.services;

import java.util.Optional;

import ar.edu.itba.paw.models.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import ar.edu.itba.paw.interfaces.persistence.CityDao;
import ar.edu.itba.paw.interfaces.services.CountryService;
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

    @InjectMocks
    CityServiceImpl cityService;

    @Mock
    CityDao cityDao;
    @Mock
    CountryService countryService;


    @Test
    public void testUpdateCity(){
        City newCity = new City("CITY_1_NAME", null, CITY_1_ID);
        when(
            countryService.findCountryByName(eq(COUNTRY_NAME))
        ).thenReturn(Optional.of(COUNTRY));
        when(
            cityDao.findById(eq(CITY_1_ID))
        ).thenReturn(Optional.of(newCity));

        City city = cityService.updateCity(CITY_1_ID, CITY_1_NAME, COUNTRY_NAME);

        assertEquals(CITY_1_NAME, city.getName());
        assertEquals(COUNTRY_NAME, city.getCountry().getName());
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
    public void testDeleteCity(){
        City city = new City("Madrid", COUNTRY );
        when(cityDao.findById(CITY_1_ID)).thenReturn(Optional.of(city));

        cityService.deleteCity(CITY_1_ID);

        assertTrue(city.isDeleted());
    }
}
