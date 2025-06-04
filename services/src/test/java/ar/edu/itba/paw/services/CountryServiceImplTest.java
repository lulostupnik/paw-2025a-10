package ar.edu.itba.paw.services;

import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import ar.edu.itba.paw.interfaces.persistence.CountryDao;
import ar.edu.itba.paw.models.Country;

@RunWith(MockitoJUnitRunner.class)
public class CountryServiceImplTest {

    private static final String COUNTRY_NAME = "cuntry";
    private static final String COUNTRY_CODE = "cu";
    private static final long COUNTRY_ID = 0;
    private static final Country COUNTRY = new Country(COUNTRY_ID, COUNTRY_NAME, COUNTRY_CODE);
    private static final List<Country> COUNTRIES = List.of(COUNTRY);

    @InjectMocks
    CountryServiceImpl countryService;

    @Mock
    CountryDao countryDao;

    @Test
    public void testFindCountries(){
        when(
            countryDao.findAll()
        ).thenReturn(COUNTRIES);

        List<Country> countries = countryService.findCountries();

        assertNotNull(countries);
        assertEquals(COUNTRIES, countries);
    }

    @Test
    public void testFindCountryByName(){
        when(
            countryDao.findByName(eq(COUNTRY_NAME))
        ).thenReturn(Optional.of(COUNTRY));

        Optional<Country> maybeCountry = countryService.findCountryByName(COUNTRY_NAME);

        assertNotNull(maybeCountry);
        assertEquals(COUNTRY, maybeCountry.get());
    }
}
