package ar.edu.itba.paw.persistance;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.time.LocalDate;
import java.util.Locale;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.jdbc.JdbcTestUtils;

import ar.edu.itba.paw.models.Career;
import ar.edu.itba.paw.models.City;
import ar.edu.itba.paw.models.Country;
import ar.edu.itba.paw.models.Image;
import ar.edu.itba.paw.models.Interest;
import ar.edu.itba.paw.models.Journey;
import ar.edu.itba.paw.models.PageParams;
import ar.edu.itba.paw.models.University;
import ar.edu.itba.paw.models.User;

public class TestUtils {

    //CONSTANTS
    public static final String CAREER_TABLE = "careers";
    public static final String CITY_TABLE = "cities";
    public static final String COUNTRY_TABLE = "countries";
    public static final String UNIVERSITY_TABLE = "universities";
    public static final String IMAGE_TABLE = "images";
    public static final String INTEREST_TABLE = "category";
    public static final String USER_INTEREST_TABLE = "user_interest";
    public static final String USER_TABLE = "users";
    public static final String JOURNEY_TABLE = "journeys";
    public static final String EVENT_TABLE = "events";
    public static final String JOURNEY_REPLY_TABLE = "journey_responses";
    public static final String EVENT_REPLY_TABLE = "event_responses";

    public static final int PAGE_SIZE_DEFAULT = 2;
    public static final int PAGE_SIZE_BIG = 100;
    public static final PageParams PAGE_1_DEFAULT = new PageParams(1, PAGE_SIZE_DEFAULT);
    public static final PageParams PAGE_2_DEFAULT = new PageParams(2, PAGE_SIZE_DEFAULT);
    public static final PageParams PAGE_1_BIG = new PageParams(1, PAGE_SIZE_BIG);
    public static final PageParams PAGE_2_BIG = new PageParams(2, PAGE_SIZE_BIG);
    public static final String MESSAGE_DEFAULT = "message";

    public static final String CAREER_1_NAME = "career 1";
    public static final String CAREER_2_NAME = "career 2";
    public static final String CAREER_INSERT1_NAME = "career 3";
    public static final String CAREER_DELETED_NAME = "deleted";
    public static final int TOTAL_CAREERS = 2;
    
    public static final String CITY_1_NAME = "city1";
    public static final String CITY_2_NAME = "city2";
    public static final String CITY_3_NAME = "city3";
    public static final String CITY_DELETED_NAME = "deleted city";
    public static final String NEW_CITY_NAME = "new!";
    public static final int TOTAL_CITIES = 3;

    public static final String COUNTRY_1_NAME = "cuntry";
    public static final String COUNTRY_1_CODE = "aa";    
    public static final String COUNTRY_2_NAME = "cuntry2";
    public static final String COUNTRY_2_CODE = "bb";
    public static final int TOTAL_COUNTRIES = 2;

    public static final String UNIVERSITY_1_NAME = "Instituto de muy largo";
    public static final String UNIVERSITY_1_CODE = "ITBA";    
    public static final String UNIVERSITY_2_NAME = "Universidad de muy largo";
    public static final String UNIVERSITY_2_CODE = "UBA";  
    public static final String UNIVERSITY_3_NAME = "Another one";
    public static final String UNIVERSITY_3_CODE = "MAS";   
    public static final String UNIVERSITY_DELETED_NAME = "Deleted uni";
    public static final String UNIVERSITY_DELETED_CODE = "DEL";
    public static final String UNIVERSITY_NEW_NAME = "Yet another one";
    public static final String UNIVERSITY_NEW_CODE = "ONE";
    public static final int TOTAL_UNIVERSITIES = 3;

    public static final byte[] IMAGE_1_DATA = new byte[]{-1, -1, -1, -1};
    public static final byte[] IMAGE_2_DATA = new byte[]{-1, -1, -1, -1, -1, -1, -1, -1};
    public static final int TOTAL_IMAGES = 2;

    public static final String INTEREST_1_NAME = "interest 1";
    public static final String INTEREST_2_NAME = "interest 2";
    public static final String INTEREST_3_NAME = "interest 3";
    public static final String INTEREST_NEW1_NAME = "interest new 1";
    public static final String INTEREST_NEW2_NAME = "interest new 2";
    public static final int TOTAL_INTERESTS = 3;

    public static final String USER_1_NAME = "user1";
    public static final String USER_2_NAME = "user2";
    public static final String USER_3_NAME = "user3";
    public static final String USER_4_NAME = "user3";
    public static final String USER_NEW1_NAME = "newUser1";
    public static final String USER_NEW2_NAME = "newUser2";
    public static final String USER_COMMON_INTERESTS_1_NAME = "1interest";
    public static final String USER_COMMON_INTERESTS_2_NAME = "2interest";
    public static final String USER_COMMON_INTERESTS_3_NAME = "3interest";
    public static final String USER_FIRSTNAME = "user";
    public static final String USER_LASTNAME = "user";
    public static final String USER_1_MAIL = "user1@mail.com";
    public static final String USER_2_MAIL = "user2@mail.com";
    public static final String USER_3_MAIL = "user3@mail.com";
    public static final String USER_4_MAIL = "user4@mail.com";
    public static final String USER_COMMON_INTERESTS_1_MAIL = "1interest@mail.com";
    public static final String USER_COMMON_INTERESTS_2_MAIL = "2interest@mail.com";
    public static final String USER_COMMON_INTERESTS_3_MAIL = "3interest@mail.com";
    public static final String USER_NEW1_MAIL = "newUser1@mail.com";
    public static final String USER_NEW2_MAIL = "newUser2@mail.com";
    public static final String USER_PASSWORD = "superSecret";
    public static final String USER_LOCALE = "es";
    public static final String USER_LOCALE_DEFAULT = "en";
    public static final String USER_ROLE = "user";
    public static final boolean USER_BLOCKED = false;
    public static final String USER_FAKE_MAIL = "totallyRealEmail@legitEmailService.com";
    public static final String USER_FAKE_NAME = "totallyLegitUser";
    public static final String USER_FAKE_PASSWORD = "wrongPassword";
    public static final String USER_FAKE_FIRSTNAME = "fake";
    public static final String USER_FAKE_LASTNAME = "name";
    public static final String USER_FAKE_LOCALE = "en";
    public static final String USER_WRONG_LOCALE = "jp";
    public static final int USER_1_INTEREST_1_SCORE = 4;
    public static final int USER_1_INTEREST_2_SCORE = 2;
    public static final int USER_1_INTEREST_3_SCORE = 1;
    public static final int TOTAL_USERS = 7;
    public static final int USER_1_INTERESTS = 3;
    public static final Map<String, Object> USER_2_PARAMS = Map.of("email", TestUtils.USER_2_MAIL, "username", TestUtils.USER_2_NAME);
    public static final Map<String, Object> USER_3_PARAMS = Map.of("email", TestUtils.USER_3_MAIL, "username", TestUtils.USER_3_NAME);

    public static final int TOTAL_USER_INTERESTS = 9;

    public static final String JOURNEY_DESCRIPTION = "Cool journey";
    public static final LocalDate JOURNEY_START_DATE = LocalDate.now().plusDays(7);
    public static final LocalDate JOURNEY_END_DATE = JOURNEY_START_DATE.plusMonths(1);

    public static final int TOTAL_JOURNEYS = 3;

    //QUERIES
    public static final String USER_SELECT = """
    SELECT 
        u.id AS user_id, 
        u.email AS email, 
        u.username AS username, 
        u.firstname AS firstname, 
        u.lastname AS lastname, 
        u.password AS password, 
        u.language AS language, 
        un.id AS university_id, 
        un.name AS university_name, 
        un.abbreviation AS university_abbreviation,
        c.id AS city_id,
        c.name AS city_name,
        co.name AS country_name,
        ca.name AS career_name,
        ca.id AS career_id,
        u.profile_picture_id AS profile_picture_id,
        u.roles AS roles, 
        u.blocked AS blocked
        FROM users u
            JOIN universities un ON un.id = u.university
            JOIN cities c ON c.id = un.city_id
            JOIN countries co ON co.id = c.country_id
            JOIN careers ca ON ca.id = u.career_id
        """;

    public static final String USER_SELECT_BY_ID = USER_SELECT + "WHERE id = ?";
    public static final String USER_SELECT_BY_EMAIL = USER_SELECT + "WHERE email = ?";

    public static final String UNIVERSITY_SELECT = """
    SELECT
        u.id AS university_id,
        u.name AS university_name,
        u.abbreviation AS university_abbreviation,
        ci.name AS city_name,
        ci.id AS city_id,
        co.name AS country_name
    FROM
        universities u
        JOIN cities ci ON u.city_id = ci.id
        JOIN countries co ON ci.country_id = co.id
    """;

    public static final String UNIVERSITY_SELECT_BY_ABBR = UNIVERSITY_SELECT + "WHERE u.abbreviation = ?";
    public static final String UNIVERSITY_SELECT_BY_NAME = UNIVERSITY_SELECT + "WHERE u.name = ?";
    public static final String UNIVERSITY_SELECT_BY_ID = UNIVERSITY_SELECT + "WHERE u.id = ?";

    public static final String COUNTRY_SELECT = "SELECT id AS country_id, name AS country_name, code AS country_code FROM countries ";
    public static final String COUNTRY_SELECT_BY_ID = COUNTRY_SELECT + "WHERE id = ?";
    public static final String COUNTRY_SELECT_BY_NAME = COUNTRY_SELECT + "WHERE name = ?";
    public static final String COUNTRY_SELECT_BY_CODE = COUNTRY_SELECT + "WHERE code = ?";

    public static final String CITY_SELECT = "SELECT c.id AS city_id, c.name AS city_name, co.name AS country_name FROM cities c JOIN countries co ON co.id = c.country_id ";
    public static final String CITY_SELECT_BY_ID = CITY_SELECT + "WHERE c.id = ?";
    public static final String CITY_SELECT_BY_NAME = CITY_SELECT + "WHERE c.name = ?";

    public static final String CAREER_SELECT = "SELECT id AS career_id, name AS career_name FROM careers ";
    public static final String CAREER_SELECT_BY_ID = CAREER_SELECT + "WHERE id = ?";
    public static final String CAREER_SELECT_BY_NAME = CAREER_SELECT + "WHERE name = ?";

    public static final String IMAGE_SELECT = "SELECT id AS image_id, content AS image_data FROM images ";
    public static final String IMAGE_SELECT_BY_ID = IMAGE_SELECT + "WHERE id = ?";
    public static final String IMAGE_SELECT_BY_DATA= IMAGE_SELECT + "WHERE content = ?";

    public static final String INTEREST_SELECT = "SELECT id AS interest_id, name AS interest_name FROM category ";
    public static final String INTEREST_SELECT_BY_ID = INTEREST_SELECT + "WHERE id = ?";
    public static final String INTEREST_SELECT_BY_NAME = INTEREST_SELECT + "WHERE name = ?";

    public static final String JOURNEY_SELECT = """
    SELECT
        j.id AS journey_id,
        j.start_date AS start_date,
        j.end_date AS end_date,
        j.description AS description,
        ud.id AS dest_university_id,
        ud.name AS dest_university_name,
        ud.abbreviation AS dest_university_abbreviation,
        cd.name AS dest_city_name,
        cd.id AS dest_city_id,
        cod.name AS dest_country_name,
        u.id AS user_id,
        u.email AS email,
        u.username AS username, 
        u.firstname AS firstname, 
        u.lastname AS lastname, 
        u.password AS password, 
        u.language AS language, 
        un.id AS university_id, 
        un.name AS university_name, 
        un.abbreviation AS university_abbreviation,
        c.id AS city_id,
        c.name AS city_name,
        co.name AS country_name,
        ca.name AS career_name,
        ca.id AS career_id,
        u.profile_picture_id AS profile_picture_id,
        u.roles AS roles, 
        u.blocked AS blocked
    FROM
        journeys j
        JOIN universities ud ON ud.id = j.destination_university_id
        JOIN cities cd ON ud.city_id = cd.id
        JOIN countries cod ON cod.id = cd.country_id
        JOIN users u ON j.user_id = u.id
        JOIN universities un ON un.id = u.university
        JOIN cities c ON c.id = un.city_id
        JOIN countries co ON co.id = c.country_id
        JOIN careers ca ON ca.id = u.career_id
    """;

    public static final String JOURNEY_SELECT_BY_USERMAIL = JOURNEY_SELECT + "WHERE u.email = ?";
    public static final String JOURNEY_SELECT_BY_ID = JOURNEY_SELECT + "WHERE j.id = ?";


    //ROWMAPPERS
    public static final RowMapper<Interest> INTEREST_ROW_MAPPER = (rs, n) ->
    new Interest(
        rs.getLong("interest_id"),
        rs.getString("interest_name")
    );

    public static final RowMapper<City> CITY_ROW_MAPPER = (rs, n) -> 
        new City(
            rs.getString("city_name"), 
            rs.getString("country_name"), 
            rs.getLong("city_id")
        );
    public static final RowMapper<City> CITY_DESTINATION_ROW_MAPPER = (rs, n) -> 
        new City(
            rs.getString("dest_city_name"), 
            rs.getString("dest_country_name"), 
            rs.getLong("dest_city_id")
        );

    public static final RowMapper<University> UNIVERSITY_ROW_MAPPER = (rs, n) ->
        new University(
            rs.getLong("university_id"), 
            rs.getString("university_name"), 
            rs.getString("university_abbreviation"), 
            CITY_ROW_MAPPER.mapRow(rs, n)
        );
    public static final RowMapper<University> UNIVERSITY_DESTINATION_ROW_MAPPER = (rs, n) ->
    new University(
        rs.getLong("dest_university_id"), 
        rs.getString("dest_university_name"), 
        rs.getString("dest_university_abbreviation"), 
        CITY_DESTINATION_ROW_MAPPER.mapRow(rs, n)
    );

    public static final RowMapper<Career> CAREER_ROW_MAPPER = (rs, n) ->
        new Career(
            rs.getLong("career_id"), 
            rs.getString("career_name")
        );

    public static final RowMapper<User> USER_ROW_MAPPER = (rs, n) -> 
    new User(rs.getLong("user_id"), 
        rs.getString("email"), 
        rs.getString("username"), 
        rs.getString("firstname"), 
        rs.getString("lastname"), 
        UNIVERSITY_ROW_MAPPER.mapRow(rs, n),
        CAREER_ROW_MAPPER.mapRow(rs, n), 
        rs.getLong("profile_picture_id"), 
        Locale.of(rs.getString("language")),
        rs.getBoolean("blocked")
    );

    public static final RowMapper<Image> IMAGE_ROW_MAPPER = (rs, n) ->
    new Image(
        rs.getLong("image_id"), 
        rs.getBytes("image_data")
    );

    public static final RowMapper<Country> COUNTRY_ROW_MAPPER = (rs, n) ->
    new Country(
        rs.getLong("country_id"),
        rs.getString("country_name"),
        rs.getString("country_code")
    );

    public static final RowMapper<Journey> JOURNEY_ROW_MAPPER = (rs, n) -> 
    new Journey(
        rs.getLong("journey_id"), 
        USER_ROW_MAPPER.mapRow(rs, n),
        rs.getDate("start_date").toLocalDate(), 
        rs.getDate("end_date").toLocalDate(), 
        UNIVERSITY_DESTINATION_ROW_MAPPER.mapRow(rs, n),
        rs.getString("description")
    );


    //DELETES
    public static final void deleteJourneyReplies(JdbcTemplate template){
        JdbcTestUtils.deleteFromTables(template, JOURNEY_REPLY_TABLE);
    }
    public static final void deleteJourneys(JdbcTemplate template){
        deleteJourneyReplies(template);
        JdbcTestUtils.deleteFromTables(template, JOURNEY_TABLE);   
    }
    public static final void deleteUserInterests(JdbcTemplate template){
        JdbcTestUtils.deleteFromTables(template, USER_INTEREST_TABLE);
    }
    public static final void deleteUsers(JdbcTemplate template){
        deleteJourneys(template);
        deleteUserInterests(template);
        JdbcTestUtils.deleteFromTables(template, USER_TABLE);
    }
    public static final void deleteInterests(JdbcTemplate template){
        deleteUsers(template);
        JdbcTestUtils.deleteFromTables(template, INTEREST_TABLE);
    }
    public static final void deleteUniversities(JdbcTemplate template){
        deleteJourneys(template);
        deleteUsers(template);
        JdbcTestUtils.deleteFromTables(template, UNIVERSITY_TABLE);
    }
    public static final void deleteCareers(JdbcTemplate template){ 
        deleteUsers(template);
        JdbcTestUtils.deleteFromTables(template, CAREER_TABLE);
    }
    public static final void deleteCities(JdbcTemplate template){
        deleteUniversities(template);
        JdbcTestUtils.deleteFromTables(template, CITY_TABLE);
    }
    public static final void deleteCountries(JdbcTemplate template){
        deleteCities(template);
        JdbcTestUtils.deleteFromTables(template, COUNTRY_TABLE);
    }


    //COMPARATORS

    public static void assertEqualsCareer(Career expected, Career actual){
        assertNotNull(expected);
        assertNotNull(actual);
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
    }
    public static void assertEqualsCity(City expected, City actual){
        assertNotNull(expected);
        assertNotNull(actual);
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getCountry(), actual.getCountry());
    }

    public static void assertEqualsUni(University expected, University actual){
        assertNotNull(expected);
        assertNotNull(actual);
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getAbbreviation(), actual.getAbbreviation());
        assertEqualsCity(expected.getCity(), actual.getCity());
    }

    public static void assertEqualsUser(User expected, User actual){
        assertNotNull(actual);
        assertNotNull(expected);
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getEmail(), actual.getEmail());
        assertEquals(expected.getUsername(), actual.getUsername());
        assertEquals(expected.getFirstname(), actual.getFirstname());
        assertEquals(expected.getLastname(), actual.getLastname());
        assertEquals(expected.getProfilePictureId(), actual.getProfilePictureId());
        assertEqualsCareer(expected.getCareer(), actual.getCareer());
        assertEqualsUni(expected.getUniversity(), actual.getUniversity());
        assertEquals(expected.getLocale(), actual.getLocale());
        assertEquals(expected.isBlocked(), actual.isBlocked());

    }
    public static void assertEqualsJourney(Journey expected, Journey actual){
        assertNotNull(actual);
        assertNotNull(expected);
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getStartDate(), actual.getStartDate());
        assertEquals(expected.getEndDate(), actual.getEndDate());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEqualsUser(expected.getUser(), actual.getUser());
        assertEqualsUni(expected.getDestinationUniversity(), actual.getDestinationUniversity());
    }

}
