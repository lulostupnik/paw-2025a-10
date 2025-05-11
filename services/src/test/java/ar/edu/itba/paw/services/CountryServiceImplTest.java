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

import ar.edu.itba.paw.interfaces.persistence.CountryDao;
import ar.edu.itba.paw.models.Country;

@RunWith(MockitoJUnitRunner.class)
public class CountryServiceImplTest {
    private static final long ID = 0;
    private static final String NAME = "cuntry";
    private static final String CODE = "cu";
    private static final Country COUNTRY = new Country(ID, NAME, CODE);

    @InjectMocks
    private CountryServiceImpl countryService;

    @Mock
    private CountryDao countryDao;

    @Test
    public void testGetAllCountries(){
        Mockito.when(
            countryDao.findAll()
        ).thenReturn(List.of(COUNTRY));

        List<Country> countries = countryService.getAllCountries();

        assertNotNull(countries);
        assertEquals(1, countries.size());
        assertEquals(COUNTRY, countries.getFirst());
    }
    @Test
    public void testGetAllCountriesNoCountries(){
        Mockito.when(
            countryDao.findAll()
        ).thenReturn(List.of());

        List<Country> countries = countryService.getAllCountries();

        assertNotNull(countries);
        assertEquals(0, countries.size());
    }
    
    @Test
    public void testFindByName(){
        Mockito.when(
            countryDao.findByName(Mockito.eq(NAME))
        ).thenReturn(Optional.of(COUNTRY));

        Optional<Country> maybeCountry = countryService.findByName(NAME);

        assertNotNull(maybeCountry);
        assertTrue(maybeCountry.isPresent());
        assertEquals(COUNTRY, maybeCountry.get());
    }
    @Test
    public void testFindByNameNotFound(){
        Mockito.when(
            countryDao.findByName(Mockito.eq(NAME))
        ).thenReturn(Optional.empty());

        Optional<Country> maybeCountry = countryService.findByName(NAME);

        assertNotNull(maybeCountry);
        assertFalse(maybeCountry.isPresent());
    }
}
