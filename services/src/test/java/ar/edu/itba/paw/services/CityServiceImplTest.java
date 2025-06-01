// package ar.edu.itba.paw.services;


// import java.util.Optional;

// import org.junit.Test;
// import org.junit.runner.RunWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.Mockito;
// import org.mockito.junit.MockitoJUnitRunner;

// import ar.edu.itba.paw.interfaces.persistence.CityDao;
// import ar.edu.itba.paw.interfaces.services.CountryService;

// @RunWith(MockitoJUnitRunner.class)
// public class CityServiceImplTest {

//     private static final String NAME = "city";
//     private static final String COUNTRY_NAME = "cuntry";
//     private static final long ID_1 = 0;

//     @InjectMocks
//     CityServiceImpl cityService;

//     @Mock
//     CityDao cityDao;
//     @Mock
//     CountryService countryService;


//     @Test(expected = IllegalArgumentException.class)
//     public void testUpdateCityMissingCountry(){
//         Mockito.when(
//             countryService.findCountryByName(Mockito.eq(COUNTRY_NAME))
//         ).thenReturn(Optional.empty());

//         cityService.updateCity(ID_1, NAME, COUNTRY_NAME);
//     }

//     @Test(expected = IllegalArgumentException.class)
//     public void testCreateCityMissingCountry(){
//         Mockito.when(
//             countryService.findCountryByName(Mockito.eq(COUNTRY_NAME))
//         ).thenReturn(Optional.empty());

//         cityService.createCity(NAME, COUNTRY_NAME);
//     }

// }
