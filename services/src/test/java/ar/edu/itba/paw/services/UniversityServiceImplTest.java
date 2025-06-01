// package ar.edu.itba.paw.services;

// import static org.junit.Assert.assertEquals;
// import static org.junit.Assert.assertNotNull;

// import java.util.Optional;

// import ar.edu.itba.paw.models.Country;
// import org.junit.Test;
// import org.junit.runner.RunWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.Mockito;
// import org.mockito.junit.MockitoJUnitRunner;

// import ar.edu.itba.paw.interfaces.persistence.UniversityDao;
// import ar.edu.itba.paw.interfaces.services.CityService;
// import ar.edu.itba.paw.models.City;
// import ar.edu.itba.paw.models.University;
// import ar.edu.itba.paw.models.exceptions.CityNotFoundException;

// @RunWith(MockitoJUnitRunner.class)
// public class UniversityServiceImplTest {

//     private static final String NAME = "uni";
//     private static final long ID_1 = 0;
//     private static final String ABBREVIATION = "ab";
//     private static final String CITY_NAME = "citi";
//     private static final String COUNTRY_NAME = "country";
//     private static final String COUNTRY_ABBREVIATION = "countryAbbreviation";
//     private static final String COUNTRY_ID = "countryId";
//     private static final Country COUNTRY = new Country(COUNTRY_NAME, COUNTRY_ABBREVIATION );
//     private static final City CITY = new City(CITY_NAME, COUNTRY, ID_1);
//     private static final University UNI_1 = new University(ID_1, NAME, ABBREVIATION, CITY);

//     @InjectMocks
//     private UniversityServiceImpl uniService;

//     @Mock
//     private UniversityDao uniDao;

//     @Mock
//     private CityService cityService;

//     @Test
//     public void testCreateUniversity(){
//         Mockito.when(
//             cityService.findCityByName(Mockito.eq(CITY_NAME))
//         ).thenReturn(Optional.of(CITY));
//         Mockito.when(
//             uniDao.create(Mockito.eq(NAME), Mockito.eq(ABBREVIATION), Mockito.eq(CITY))
//         ).thenReturn(UNI_1);

//         University uni = uniService.createUniversity(NAME, ABBREVIATION, CITY_NAME);

//         assertNotNull(uni);
//         assertEquals(UNI_1, uni);
//     }
//     @Test(expected = CityNotFoundException.class)
//     public void testCreateUniversityCityNotFound(){
//         Mockito.when(
//             cityService.findCityByName(Mockito.eq(CITY_NAME))
//         ).thenReturn(Optional.empty());

//         uniService.createUniversity(NAME, ABBREVIATION, CITY_NAME);
//     }

// }