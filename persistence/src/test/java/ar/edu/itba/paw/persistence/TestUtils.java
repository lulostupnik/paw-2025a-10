package ar.edu.itba.paw.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.test.jdbc.JdbcTestUtils;

import ar.edu.itba.paw.models.Career;
import ar.edu.itba.paw.models.City;
import ar.edu.itba.paw.models.Country;
import ar.edu.itba.paw.models.Event;
import ar.edu.itba.paw.models.EventResponse;
import ar.edu.itba.paw.models.Image;
import ar.edu.itba.paw.models.Interest;
import ar.edu.itba.paw.models.Journey;
import ar.edu.itba.paw.models.JourneyResponse;
import ar.edu.itba.paw.models.PageParams;
import ar.edu.itba.paw.models.Report;
import ar.edu.itba.paw.models.Tip;
import ar.edu.itba.paw.models.University;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.enums.ReportReason;
import ar.edu.itba.paw.models.enums.ReportStatus;

class TestUtils {

    private TestUtils() {
        throw new AssertionError("Utility class should not be instantiated");
    }

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
    public static final String EVENT_ATTENDANCE_TABLE = "event_attendances";
    public static final String TOKEN_TABLE = "tokens";
    public static final String RATING_TABLE = "ratings";
    public static final String REPORT_TABLE = "reports";

    public static final int PAGE_SIZE_DEFAULT = 2;
    public static final int PAGE_SIZE_SINGLE = 1;
    public static final int PAGE_SIZE_BIG = 100;
    public static final PageParams PAGE_1_DEFAULT = new PageParams(1, PAGE_SIZE_DEFAULT);
    public static final PageParams PAGE_2_DEFAULT = new PageParams(2, PAGE_SIZE_DEFAULT);
    public static final PageParams PAGE_1_BIG = new PageParams(1, PAGE_SIZE_BIG);
    public static final PageParams PAGE_2_BIG = new PageParams(2, PAGE_SIZE_BIG);
    public static final PageParams PAGE_1_SINGLE = new PageParams(1, PAGE_SIZE_SINGLE);
    public static final PageParams PAGE_2_SINGLE = new PageParams(2, PAGE_SIZE_SINGLE);
    public static final String MESSAGE_DEFAULT = "message";

    public static final String CAREER_1_NAME = "career 1";
    public static final String CAREER_2_NAME = "career 2";
    public static final String CAREER_INSERT1_NAME = "career 3";
    public static final String CAREER_DELETED_NAME = "deleted";
    public static final long CAREER_1_ID = 1;
    public static final long CAREER_2_ID = 2;
    public static final long CAREER_DELETED_ID = 3;
    public static final Career CAREER_1 = new Career(CAREER_1_ID, CAREER_1_NAME);
    public static final Career CAREER_2 = new Career(CAREER_2_ID, CAREER_2_NAME);
    public static final Career CAREER_DELETED = new Career(CAREER_DELETED_ID, CAREER_DELETED_NAME);

    public static final int TOTAL_CAREERS = 2;

    public static final String COUNTRY_1_NAME = "cuntry";
    public static final String COUNTRY_1_CODE = "aa";
    public static final long COUNTRY_1_ID = 1;
    public static final long COUNTRY_2_ID = 2;
    public static final String COUNTRY_2_NAME = "cuntry2";
    public static final String COUNTRY_2_CODE = "bb";
    public static final Country COUNTRY_1 = new Country(COUNTRY_1_ID, COUNTRY_1_NAME, COUNTRY_1_CODE);
    public static final Country COUNTRY_2 = new Country(COUNTRY_2_ID, COUNTRY_2_NAME, COUNTRY_2_CODE);
    public static final Map<Long, Country> COUNTRY_DATA = Map.of(COUNTRY_1_ID, COUNTRY_1, COUNTRY_2_ID, COUNTRY_2);

    public static final int TOTAL_COUNTRIES = 2;

    public static final String CITY_1_NAME = "city1";
    public static final String CITY_2_NAME = "city2";
    public static final String CITY_3_NAME = "city3";
    public static final String CITY_DELETED_NAME = "deleted city";
    public static final String NEW_CITY_NAME = "new!";
    public static final long CITY_1_ID = 1;
    public static final long CITY_2_ID = 2;
    public static final long CITY_3_ID = 3;
    public static final long CITY_DELETED_ID = 4;
    public static final City CITY_1 = new City(CITY_1_NAME, COUNTRY_1, CITY_1_ID);
    public static final City CITY_2 = new City(CITY_2_NAME, COUNTRY_1, CITY_2_ID);
    public static final City CITY_3 = new City(CITY_3_NAME, COUNTRY_2, CITY_3_ID);
    public static final City CITY_DELETED = new City(CITY_DELETED_NAME, COUNTRY_2, CITY_DELETED_ID);
    public static final Map<Long, City> CITY_DATA = Map.of(CITY_1_ID, CITY_1, CITY_2_ID, CITY_2, CITY_3_ID, CITY_3);

    public static final int TOTAL_CITIES = 3;

    public static final String UNIVERSITY_1_NAME = "Instituto de muy largo";
    public static final String UNIVERSITY_1_CODE = "ITBA";
    public static final long UNIVERSITY_1_ID = 1;
    public static final String UNIVERSITY_2_NAME = "Universidad de muy largo";
    public static final String UNIVERSITY_2_CODE = "UBA";
    public static final long UNIVERSITY_2_ID = 2;
    public static final String UNIVERSITY_3_NAME = "Another one";
    public static final String UNIVERSITY_3_CODE = "MAS";
    public static final long UNIVERSITY_3_ID = 3;
    public static final String UNIVERSITY_DELETED_NAME = "Deleted uni";
    public static final String UNIVERSITY_DELETED_CODE = "DEL";
    public static final long UNIVERSITY_DELETED_ID = 4;
    public static final String UNIVERSITY_NEW_NAME = "Yet another one";
    public static final String UNIVERSITY_NEW_CODE = "ONE";
    public static final University UNI_1 = new University(UNIVERSITY_1_ID, UNIVERSITY_1_NAME, UNIVERSITY_1_CODE, CITY_1);
    public static final University UNI_2 = new University(UNIVERSITY_2_ID, UNIVERSITY_2_NAME, UNIVERSITY_2_CODE, CITY_2);
    public static final University UNI_3 = new University(UNIVERSITY_3_ID, UNIVERSITY_3_NAME, UNIVERSITY_3_CODE, CITY_2);
    public static final University UNI_DELETED = new University(UNIVERSITY_DELETED_ID, UNIVERSITY_DELETED_NAME, UNIVERSITY_DELETED_CODE, CITY_1);

    public static final int TOTAL_UNIVERSITIES = 3;

    public static final byte[] IMAGE_1_DATA = new byte[]{-1, -1, -1, -1};
    public static final byte[] IMAGE_2_DATA = new byte[]{-1, -1, -1, -1, -1, -1, -1, -1};
    public static final long IMAGE_1_ID = 1;
    public static final long IMAGE_2_ID = 2;
    public static final Image IMAGE_1 = new Image(IMAGE_1_ID, IMAGE_1_DATA);
    public static final Image IMAGE_2 = new Image(IMAGE_2_ID, IMAGE_2_DATA);

    public static final int TOTAL_IMAGES = 2;

    public static final String INTEREST_1_NAME = "interest 1";
    public static final String INTEREST_2_NAME = "interest 2";
    public static final String INTEREST_3_NAME = "interest 3";
    public static final String INTEREST_NEW1_NAME = "interest new 1";
    public static final String INTEREST_NEW2_NAME = "interest new 2";
    public static final long INTEREST_1_ID = 1;
    public static final long INTEREST_2_ID = 2;
    public static final long INTEREST_3_ID = 3;
    public static final Interest INTEREST_1 = new Interest(INTEREST_1_ID, INTEREST_1_NAME);
    public static final Interest INTEREST_2 = new Interest(INTEREST_2_ID, INTEREST_2_NAME);
    public static final Interest INTEREST_3 = new Interest(INTEREST_3_ID, INTEREST_3_NAME);
    public static final Map<Long, Interest> INTEREST_DATA = Map.of(INTEREST_1_ID, INTEREST_1, INTEREST_2_ID, INTEREST_2, INTEREST_3_ID, INTEREST_3);
    public static final int TOTAL_INTERESTS = 3;

    public static final String USER_1_NAME = "user1";
    public static final String USER_2_NAME = "user2";
    public static final String USER_3_NAME = "user3";
    public static final String USER_4_NAME = "user4";
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
    public static final String USER_TOKEN_NEW = "newtoken";
    public static final LocalDate USER_EXPIRATION_DEFAULT = LocalDate.now().plusDays(1);
    public static final String USER_ROLE = "user";
    public static final boolean USER_BLOCKED = false;
    public static final String USER_FAKE_MAIL = "totallyRealEmail@legitEmailService.com";
    public static final String USER_FAKE_NAME = "totallyLegitUser";
    public static final String USER_FAKE_PASSWORD = "wrongPassword";
    public static final String USER_FAKE_FIRSTNAME = "fake";
    public static final String USER_FAKE_LASTNAME = "name";
    public static final String USER_FAKE_LOCALE = "en";
    public static final String USER_WRONG_LOCALE = "jp";
    public static final String USER_TOKEN_DEFAULT = "token";
    public static final int USER_1_INTEREST_1_SCORE = 4;
    public static final int USER_I1_INTEREST_1_SCORE = 1;
    public static final int USER_1_INTEREST_2_SCORE = 2;
    public static final int USER_1_INTEREST_3_SCORE = 1;
    public static final Map<Long, Integer> USER_1_INTEREST_SCORES = Map.of(INTEREST_1_ID, USER_1_INTEREST_1_SCORE, INTEREST_2_ID, USER_1_INTEREST_2_SCORE, INTEREST_3_ID, USER_1_INTEREST_3_SCORE);
    public static final int USER_1_CREATED_EVENTS = 2;
    public static final int USER_2_CREATED_EVENTS = 2;
    public static final double USER_1_ATTENDED_EVENTS_RATING = 3.5;
    public static final double USER_1_CREATED_EVENTS_RATING = 4.5;
    public static final long USER_2_REPORTS = 1;
    public static final long USER_3_REPORTS = 4;
    public static final long USER_1_ID = 1;
    public static final long USER_2_ID = 2;
    public static final long USER_3_ID = 3;
    public static final long USER_4_ID = 4;
    public static final long USER_I1_ID = 5;
    public static final long USER_I2_ID = 6;
    public static final long USER_I3_ID = 7;
    public static final User USER_1 = new User(USER_1_ID, USER_1_MAIL, USER_1_NAME, USER_FIRSTNAME, USER_LASTNAME, UNI_1, CAREER_1, IMAGE_1_ID, Locale.of(USER_LOCALE), false, true);
    public static final User USER_2 = new User(USER_2_ID, USER_2_MAIL, USER_2_NAME, USER_FIRSTNAME, USER_LASTNAME, UNI_1, CAREER_1, IMAGE_1_ID, Locale.of(USER_LOCALE), false, true);
    public static final User USER_3 = new User(USER_3_ID, USER_3_MAIL, USER_3_NAME, USER_FIRSTNAME, USER_LASTNAME, UNI_1, CAREER_1, IMAGE_1_ID, Locale.of(USER_LOCALE), false, true);
    public static final User USER_4 = new User(USER_4_ID, USER_4_MAIL, USER_4_NAME, USER_FIRSTNAME, USER_LASTNAME, UNI_2, CAREER_1, IMAGE_1_ID, Locale.of(USER_LOCALE), false, true);
    public static final User USER_I1 = new User(USER_I1_ID, USER_COMMON_INTERESTS_1_MAIL, USER_COMMON_INTERESTS_1_NAME, USER_FIRSTNAME, USER_LASTNAME, UNI_1, CAREER_1, IMAGE_1_ID, Locale.of(USER_LOCALE_DEFAULT), false, true);
    public static final User USER_I2 = new User(USER_I2_ID, USER_COMMON_INTERESTS_2_MAIL, USER_COMMON_INTERESTS_2_NAME, USER_FIRSTNAME, USER_LASTNAME, UNI_1, CAREER_1, IMAGE_1_ID, Locale.of(USER_LOCALE_DEFAULT), false, true);
    public static final User USER_I3 = new User(USER_I3_ID, USER_COMMON_INTERESTS_3_MAIL, USER_COMMON_INTERESTS_3_NAME, USER_FIRSTNAME, USER_LASTNAME, UNI_1, CAREER_1, IMAGE_1_ID, Locale.of(USER_LOCALE_DEFAULT), false, true);
    public static final Map<Long, User> USER_DATA = Map.of(USER_1_ID, USER_1, USER_2_ID, USER_2, USER_3_ID, USER_3, USER_4_ID, USER_4, USER_I1_ID, USER_I1, USER_I2_ID, USER_I2, USER_I3_ID, USER_I3);

    public static final int TOTAL_USERS = 7;
    public static final int USER_1_INTERESTS = 3;
    public static final int TOTAL_USER_INTERESTS = 9;

    public static final String JOURNEY_DESCRIPTION = "Cool journey";
    public static final String JOURNEY_DESCRIPTION_DELETED = "deleted";
    public static final LocalDate JOURNEY_DELETED_DATE = LocalDate.now();
    public static final LocalDate JOURNEY_START_DATE = LocalDate.now().plusDays(7);
    public static final LocalDate JOURNEY_END_DATE = JOURNEY_START_DATE.plusMonths(1);
    public static final long JOURNEY_DELETED_ID = 3;
    public static final long JOURNEY_1_ID = 1;
    public static final long JOURNEY_2_ID = 2;
    public static final Journey JOURNEY_DELETED = new Journey(JOURNEY_DELETED_ID, USER_4, JOURNEY_DELETED_DATE, JOURNEY_DELETED_DATE, UNI_1, JOURNEY_DESCRIPTION_DELETED);
    public static final Journey JOURNEY_1 = new Journey(JOURNEY_1_ID, USER_1, JOURNEY_START_DATE, JOURNEY_END_DATE, UNI_2, JOURNEY_DESCRIPTION);
    public static final Journey JOURNEY_2 = new Journey(JOURNEY_2_ID, USER_2, JOURNEY_START_DATE, JOURNEY_END_DATE, UNI_2, JOURNEY_DESCRIPTION);

    public static final int TOTAL_JOURNEYS = 3;

    public static final String RESPONSE_MESSAGE = "COOL!";
    public static final LocalDateTime RESPONSE_TIMESTAMP = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
    public static final LocalDateTime RESPONSE_TIMESTAMP_2 = RESPONSE_TIMESTAMP.plusHours(1);
    public static final LocalDateTime RESPONSE_TIMESTAMP_3 = RESPONSE_TIMESTAMP.plusHours(2);
    public static final long JOURNEY_RESPONSE_1_ID = 1;
    public static final long JOURNEY_RESPONSE_2_ID = 2;
    public static final long JOURNEY_RESPONSE_3_ID = 3;
    public static final long JOURNEY_RESPONSE_DELETED_ID = 4;
    public static final JourneyResponse JOURNEY_RESPONSE_1 = new JourneyResponse(JOURNEY_RESPONSE_1_ID, USER_1, JOURNEY_1, RESPONSE_MESSAGE, RESPONSE_TIMESTAMP);
    public static final JourneyResponse JOURNEY_RESPONSE_2 = new JourneyResponse(JOURNEY_RESPONSE_2_ID, USER_2, JOURNEY_1, RESPONSE_MESSAGE, RESPONSE_TIMESTAMP_2);
    public static final JourneyResponse JOURNEY_RESPONSE_3 = new JourneyResponse(JOURNEY_RESPONSE_3_ID, USER_3, JOURNEY_1, RESPONSE_MESSAGE, RESPONSE_TIMESTAMP_3);
    public static final JourneyResponse JOURNEY_RESPONSE_DELETED = new JourneyResponse(JOURNEY_RESPONSE_DELETED_ID, USER_3, JOURNEY_1, RESPONSE_MESSAGE, RESPONSE_TIMESTAMP);
    public static final Map<Long, JourneyResponse> RESPONSE_DATA = Map.of(JOURNEY_RESPONSE_1_ID, JOURNEY_RESPONSE_1, JOURNEY_RESPONSE_2_ID, JOURNEY_RESPONSE_2, JOURNEY_RESPONSE_3_ID, JOURNEY_RESPONSE_3);

    public static final int TOTAL_JOURNEY_RESPONSES = 3;
    public static final int TOTAL_JOURNEY_RESPONDERS = 3;

    public static final String EVENT_TITLE_DEFAULT = "warm event";
    public static final String EVENT_TITLE_2 = "another event";
    public static final String EVENT_TITLE_3 = "one more event";
    public static final String EVENT_TITLE_DELETED = "deleted";
    public static final String EVENT_TITLE_PAST = "older event";
    public static final String EVENT_DESCRIPTION_DEFAULT = "cool event";
    public static final String EVENT_ADDRESS_DEFAULT = "cool place";
    public static final LocalTime EVENT_TIME_DEFAULT = LocalTime.of(0, 0, 0);
    public static final LocalDate EVENT_DATE_DEFAULT = LocalDate.now().plusDays(7);
    public static final LocalDate EVENT_DATE_OLDER = LocalDate.now().plusDays(-100);
    public static final LocalDate EVENT_DATE_LATER = EVENT_DATE_DEFAULT.plusDays(10);
    public static final int EVENT_ATTENDANCE_LIMIT_DEFAULT = 30;
    public static final int EVENT_ATTENDANCE_DEFAULT = 10;
    public static final long EVENT_1_ID = 1;
    public static final long EVENT_2_ID = 2;
    public static final long EVENT_3_ID = 3;
    public static final long EVENT_OLDER_ID = 4;
    public static final long EVENT_DELETED_ID = 5;
    public static final int EVENT_1_ATTENDEES = 3;
    public static final int EVENT_2_ATTENDEES = 1;
    public static final int EVENT_3_ATTENDEES = 0;
    public static final int EVENT_OLDER_ATTENDEES = 0;
    public static final int EVENT_DELETED_ATTENDEES = 0;
    public static final int USER_1_ATTENDANCES = 2;
    public static final int USER_1_ATTENDANCES_UPCOMING_NOT_OWN = 1;
    public static final int USER_2_ATTENDANCES_UPCOMING_NOT_OWN = 1;
    public static final int USER_2_ATTENDANCES = 2;
    public static final int USER_3_ATTENDANCES = 1;
    public static final int EVENT_1_REPLIES = 3;
    public static final int EVENT_1_REPLIERS = 1;
    public static final Event EVENT_1 = new Event(EVENT_1_ID, USER_1, EVENT_DATE_DEFAULT, EVENT_DESCRIPTION_DEFAULT, IMAGE_1_ID, CITY_1, EVENT_TITLE_DEFAULT, EVENT_TIME_DEFAULT, EVENT_ADDRESS_DEFAULT, EVENT_ATTENDANCE_LIMIT_DEFAULT);
    public static final Event EVENT_2 = new Event(EVENT_2_ID, USER_2, EVENT_DATE_DEFAULT, EVENT_DESCRIPTION_DEFAULT, IMAGE_1_ID, CITY_1, EVENT_TITLE_2, EVENT_TIME_DEFAULT, EVENT_ADDRESS_DEFAULT, null);
    public static final Event EVENT_3 = new Event(EVENT_3_ID, USER_2, EVENT_DATE_DEFAULT, EVENT_DESCRIPTION_DEFAULT, IMAGE_1_ID, CITY_1, EVENT_TITLE_3, EVENT_TIME_DEFAULT, EVENT_ADDRESS_DEFAULT, EVENT_ATTENDANCE_LIMIT_DEFAULT);
    public static final Event EVENT_OLDER = new Event(EVENT_OLDER_ID, USER_1, EVENT_DATE_OLDER, EVENT_DESCRIPTION_DEFAULT, IMAGE_1_ID, CITY_1, EVENT_TITLE_PAST, EVENT_TIME_DEFAULT, EVENT_ADDRESS_DEFAULT, EVENT_ATTENDANCE_LIMIT_DEFAULT);
    public static final Event EVENT_DELETED = new Event(EVENT_DELETED_ID, USER_2, EVENT_DATE_DEFAULT, EVENT_DESCRIPTION_DEFAULT, IMAGE_1_ID, CITY_1, EVENT_TITLE_DELETED, EVENT_TIME_DEFAULT, EVENT_ADDRESS_DEFAULT, EVENT_ATTENDANCE_LIMIT_DEFAULT);
    public static final Map<Long, Event> EVENT_DATA = Map.of(EVENT_1_ID, EVENT_1, EVENT_2_ID, EVENT_2, EVENT_3_ID, EVENT_3, EVENT_OLDER_ID, EVENT_OLDER);
    public static final long EVENT_RESPONSE_1_ID = 1;
    public static final long EVENT_RESPONSE_2_ID = 2;
    public static final long EVENT_RESPONSE_3_ID = 3;
    public static final long EVENT_RESPONSE_DELETED_ID = 4;
    public static final EventResponse EVENT_RESPONSE_1 = new EventResponse(EVENT_RESPONSE_1_ID, USER_1, EVENT_1, RESPONSE_MESSAGE, RESPONSE_TIMESTAMP);
    public static final EventResponse EVENT_RESPONSE_2 = new EventResponse(EVENT_RESPONSE_2_ID, USER_1, EVENT_1, RESPONSE_MESSAGE, RESPONSE_TIMESTAMP);
    public static final EventResponse EVENT_RESPONSE_3 = new EventResponse(EVENT_RESPONSE_3_ID, USER_1, EVENT_1, RESPONSE_MESSAGE, RESPONSE_TIMESTAMP);
    public static final EventResponse EVENT_RESPONSE_DELETED = new EventResponse(EVENT_RESPONSE_DELETED_ID, USER_1, EVENT_1, RESPONSE_MESSAGE, RESPONSE_TIMESTAMP);
    public static final Map<Long, EventResponse> EVENT_RESPONSE_DATA = Map.of(EVENT_RESPONSE_1_ID, EVENT_RESPONSE_1, EVENT_RESPONSE_2_ID, EVENT_RESPONSE_2, EVENT_RESPONSE_3_ID, EVENT_RESPONSE_3);
    public static final double EVENT_1_RATING = 4.5;
    public static final int EVENT_1_RATING_COUNT = 2;
    public static final double EVENT_1_USER_1_RATING = 5;

    public static final int TOTAL_EVENTS_NOT_DELETED = 4;
    public static final int TOTAL_EVENTS_UPCOMING = 3;
    public static final int TOTAL_EVENT_ATTENDANCES = 5;
    public static final int TOTAL_EVENT_REPLIES = 3;

    public static final int TOTAL_TOKENS = 4;
    public static final int TOKENS_NOT_EXPIRED = 3;
    public static final LocalDateTime TOKEN_NEW_EXPIRATION = LocalDateTime.now().plusDays(1);
    public static final String TOKEN_NEW_VALUE = "1234";
    public static final long TOKEN_1_ID = 1;
    public static final long TOKEN_2_ID = 2;
    public static final long TOKEN_3_ID = 3;
    public static final long TOKEN_4_ID = 4;
    public static final String TOKEN_1_VALUE = "asdf";
    public static final String TOKEN_2_VALUE = "zxcv";
    public static final String TOKEN_3_VALUE = "qwer";
    public static final String TOKEN_4_VALUE = "tyui";

    public static final long REPORT_USER_ID = 1;
    public static final long REPORT_JOURNEY_ID = 2;
    public static final long REPORT_EVENT_ID = 3;
    public static final long REPORT_USER_REVIEW_ID = 4;
    public static final long REPORT_USER_RESOLVED_ID = 5;
    public static final long REPORT_USER_DISMISSED_ID = 6;
    public static final int USER_1_REPORTS_AUTHORED = 6;
    public static final String REPORT_USER_DESC = "illegal";
    public static final String REPORT_JOURNEY_DESC = "illegaljourney";
    public static final String REPORT_JOURNEY_RESPONSE_DESC = "illegaljourneyresponse";
    public static final String REPORT_EVENT_DESC = "illegalevent";
    public static final String REPORT_EVENT_RESPONSE_DESC = "illegaleventresponse";
    public static final User REPORTING_USER = USER_1;
    public static final User REPORT_USER_REPORTED_USER = USER_3;
    public static final User REPORT_JOURNEY_REPORTED_USER = USER_4;
    public static final User REPORT_EVENT_REPORTED_USER = USER_2;
    public static final Journey REPORT_JOURNEY_REPORTED_JOURNEY = JOURNEY_DELETED;
    public static final Event REPORT_EVENT_REPORTED_EVENT = EVENT_3;
    public static final int REPORTS_PENDING = 3;
    public static final int REPORTS_UNDER_REVIEW = 1;
    public static final int REPORTS_DISMISSED = 1;
    public static final int REPORTS_RESOLVED = 1;

    public static final int TOTAL_REPORTS = 6;
    public static final ReportReason HARASSMENT = ReportReason.HARASSMENT;
    public static final Report REPORT_USER = new Report(REPORT_USER_ID, REPORT_USER_REPORTED_USER, REPORTING_USER, REPORT_USER_DESC, HARASSMENT, null, null, null, null, false, ReportStatus.PENDING);
    public static final Report REPORT_JOURNEY = new Report(REPORT_JOURNEY_ID, REPORT_JOURNEY_REPORTED_USER, REPORTING_USER, REPORT_JOURNEY_DESC, HARASSMENT, REPORT_JOURNEY_REPORTED_JOURNEY, null, null, null, false, ReportStatus.PENDING);
    public static final Report REPORT_EVENT = new Report(REPORT_EVENT_ID, REPORT_EVENT_REPORTED_USER, REPORTING_USER, REPORT_EVENT_DESC, HARASSMENT, null, REPORT_EVENT_REPORTED_EVENT, null, null, false, ReportStatus.PENDING);
    public static final Report REPORT_USER_UNDER_REVIEW = new Report(REPORT_USER_REVIEW_ID, REPORT_USER_REPORTED_USER, REPORTING_USER, REPORT_USER_DESC, HARASSMENT, null, null, null, null, false, ReportStatus.UNDER_REVIEW);
    public static final Report REPORT_USER_DISMISSED = new Report(REPORT_USER_DISMISSED_ID, REPORT_USER_REPORTED_USER, REPORTING_USER, REPORT_USER_DESC, HARASSMENT, null, null, null, null, false, ReportStatus.DISMISSED);
    public static final Report REPORT_USER_RESOLVED = new Report(REPORT_USER_RESOLVED_ID, REPORT_USER_REPORTED_USER, REPORTING_USER, REPORT_USER_DESC, HARASSMENT, null, null, null, null, false, ReportStatus.RESOLVED);

    public static final Map<Long, Report> REPORT_PENDING_DATA = Map.of(REPORT_USER_ID, REPORT_USER, REPORT_JOURNEY_ID, REPORT_JOURNEY, REPORT_EVENT_ID, REPORT_EVENT);

    public static final long TIP_1_ID = 1;
    public static final String TIP_1_TITLE = "title";
    public static final String TIP_1_CONTENT = "content";
    public static final LocalDateTime TIP_1_TIMESTAMP = LocalDateTime.now();
    public static final Tip TIP_1 = new Tip(TIP_1_ID, TIP_1_TITLE, TIP_1_CONTENT, JOURNEY_1, TIP_1_TIMESTAMP);
    public static final String TIP_NEW_TITLE = "newTitle";
    public static final String TIP_NEW_CONTENT = "newContent";
    

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
    public static final String USER_SELECT_TOKEN_BY_ID = "SELECT token FROM users WHERE id = ?";
    public static final String USER_SELECT_EXPIRATION_BY_ID = "SELECT token_expiration FROM users WHERE id = ?";
    public static final String USER_SELECT_PASSWORD_BY_ID = "SELECT password FROM users WHERE id = ?";

    public static final String UNIVERSITY_SELECT = """
    SELECT
        u.id AS university_id,
        u.name AS university_name,
        u.abbreviation AS university_abbreviation,
        ci.name AS city_name,
        ci.id AS city_id,
        co.name AS country_name,
        co.code AS country_code,
        co.id AS country_id
    FROM
        universities u
        JOIN cities ci ON u.city_id = ci.id
        JOIN countries co ON ci.country_id = co.id
    """;

    public static final String UNIVERSITY_SELECT_BY_ABBR = UNIVERSITY_SELECT + "WHERE u.abbreviation = ?";
    public static final String UNIVERSITY_SELECT_BY_NAME = UNIVERSITY_SELECT + "WHERE u.name = ?";
    public static final String UNIVERSITY_SELECT_BY_ID = UNIVERSITY_SELECT + "WHERE u.id = ?";
    public static final String UNIVERSITY_COUNT_NOT_DELETED = "SELECT COUNT(*) FROM universities WHERE deleted = FALSE";

    public static final String COUNTRY_SELECT = "SELECT id AS country_id, name AS country_name, code AS country_code FROM countries ";
    public static final String COUNTRY_SELECT_BY_ID = COUNTRY_SELECT + "WHERE id = ?";
    public static final String COUNTRY_SELECT_BY_NAME = COUNTRY_SELECT + "WHERE name = ?";
    public static final String COUNTRY_SELECT_BY_CODE = COUNTRY_SELECT + "WHERE code = ?";

    public static final String CITY_SELECT = "SELECT c.id AS city_id, c.name AS city_name, co.name AS country_name, co.code AS country_code, co.id AS country_id FROM cities c JOIN countries co ON co.id = c.country_id ";
    public static final String CITY_SELECT_BY_ID = CITY_SELECT + "WHERE c.id = ?";
    public static final String CITY_SELECT_BY_NAME = CITY_SELECT + "WHERE c.name = ?";
    public static final String CITIES_COUNT_NOT_DELETED = "SELECT COUNT(*) FROM cities WHERE deleted = FALSE";

    public static final String CAREER_SELECT = "SELECT id AS career_id, name AS career_name FROM careers ";
    public static final String CAREER_SELECT_BY_ID = CAREER_SELECT + "WHERE id = ?";
    public static final String CAREER_SELECT_BY_NAME = CAREER_SELECT + "WHERE name = ?";
    public static final String CAREER_IS_DELETED_BY_ID = "SELECT deleted FROM careers WHERE id = ?";
    public static final String CAREER_COUNT_NOT_DELETED = "SELECT COUNT(*) FROM careers WHERE deleted = FALSE";

    public static final String IMAGE_SELECT = "SELECT id AS image_id, content AS image_data FROM images ";
    public static final String IMAGE_SELECT_BY_ID = IMAGE_SELECT + "WHERE id = ?";
    public static final String IMAGE_SELECT_BY_DATA= IMAGE_SELECT + "WHERE content = ?";

    public static final String INTEREST_SELECT = "SELECT id AS interest_id, name AS interest_name FROM category ";
    public static final String INTEREST_SELECT_BY_ID = INTEREST_SELECT + "WHERE id = ?";
    public static final String INTEREST_SELECT_BY_NAME = INTEREST_SELECT + "WHERE name = ?";
    public static final String INTEREST_SELECT_SCORE = "SELECT score FROM user_interest WHERE category_id = ? AND user_id = ?";
    public static final String INTEREST_SELECT_BY_USER_ID = """
        SELECT cat.id as interest_id, cat.name as interest_name
        FROM category cat JOIN user_interest ui ON ui.category_id = cat.id
        WHERE ui.user_id = ?
    """;
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
    public static final String JOURNEY_GET_ID_BY_USER_ID = "SELECT id FROM journeys WHERE user_id = ?";
    public static final String JOURNEY_IS_DELETED_BY_ID = "SELECT deleted FROM journeys WHERE id = ?";
    public static final String JOURNEY_GET_DELETED_MESSAGE_BY_ID = "SELECT deleted_message FROM journeys WHERE id = ?";

    public static final String JOURNEY_REPLY_SELECT = """
    SELECT
        r.id AS id,
        r.user_id AS user_id,
        r.journey_id AS journey_id,
        r.message AS message,
        r.date_time AS date_time,
        u.username
    FROM
        journey_responses r
        JOIN users u ON r.user_id = u.id
    """;

    public static final String JOURNEY_REPLY_SELECT_BY_ID = JOURNEY_REPLY_SELECT + "WHERE id = ?";
    public static final String JOURNEY_REPLY_SELECT_BY_USER_JOURNEY = JOURNEY_REPLY_SELECT + "WHERE user_id = ? AND journey_id = ?";
    public static final String JOURNEY_REPLY_COUNT_NOT_DELETED = "SELECT COUNT(*) FROM journey_responses WHERE deleted = FALSE";
    public static final String JOURNEY_REPLY_IS_DELETED_BY_ID = "SELECT deleted FROM journey_responses WHERE id = ?";
    public static final String JOURNEY_REPLY_DELETED_MESSAGE_BY_ID = "SELECT deleted_message FROM journey_responses WHERE id = ?";

    public static final String EVENT_SELECT = """
    SELECT
        e.id AS id,
        e.event_date AS event_date,
        e.event_time AS event_time,
        e.address AS address,
        e.attendees_limit AS attendees_limit,
        e.description AS description,
        e.title AS title,
        e.flyer_image_id AS flyer_image_id,
        e.deleted AS deleted,
        e.deleted_message AS deleted_message,
        cd.id AS dest_city_id,
        cd.name AS dest_city_name,
        cod.name AS dest_country_name,
        u.id AS user_id,
        u.email AS email,
        u.username AS username,
        u.firstname AS firstname,
        u.lastname AS lastname,
        u.language AS language,
        u.profile_picture_id AS profile_picture_id,
        u.blocked AS blocked,
        un.id AS university_id,
        un.name AS university_name,
        un.abbreviation AS university_abbreviation,
        ci.id AS city_id,
        ci.name AS city_name,
        co.name AS country_name,
        ca.name AS career_name,
        ca.id AS career_id
    FROM
        events e
        JOIN cities cd ON e.city_id = cd.id
        JOIN countries cod ON cd.country_id = cod.id
        JOIN users u ON e.user_id = u.id
        JOIN universities un ON u.university = un.id
        JOIN cities ci ON ci.id = un.city_id
        JOIN countries co ON cd.country_id = co.id
        JOIN careers ca ON ca.id = u.career_id
    """;

    public static final String EVENT_SELECT_BY_ID = EVENT_SELECT + "WHERE e.id = ?";
    public static final String EVENT_SELECT_BY_TITLE = EVENT_SELECT + "WHERE e.title = ?";
    public static final String EVENT_COUNT_NOT_DELETED = "SELECT COUNT(*) FROM events WHERE deleted=FALSE";
    public static final String EVENT_GET_DELETED_MESSAGE = "SELECT deleted_message FROM events WHERE id = ?";

    public static final String EVENT_ATTENDANCE_EXISTS = "SELECT COUNT(*) FROM event_attendances WHERE user_id = ? AND event_id = ?";
    public static final String EVENT_GET_ATTENDEES_BY_ID = "SELECT COUNT(*) FROM event_attendances WHERE event_id = ?";
    public static final String EVENT_GET_ATTENDEES_COUNT_BY_ID = "SELECT attendees_count FROM events WHERE id = ?";
    public static final String USER_GET_ATTENDANCES_COUNT_BY_ID = "SELECT COUNT(*) FROM event_attendances WHERE user_id = ?";

    public static final String EVENT_RESPONSE_IS_DELETED = "SELECT deleted FROM event_responses WHERE id = ?";
    public static final String EVENT_GET_DELETED_ID = "SELECT id FROM event_responses WHERE deleted = TRUE";
    public static final String EVENT_RESPONSE_GET_DELETE_MESSAGE = "SELECT deleted_message FROM event_responses WHERE id = ?";
    public static final String EVENT_RESPONSE_SELECT = """
    SELECT
        r.id AS id,
        r.user_id AS user_id,
        r.event_id AS event_id,
        r.message AS message,
        r.date_time AS date_time,
        u.username AS username
    FROM
        event_responses r
        JOIN users u ON r.user_id = u.id
    """;
    public static final String EVENT_RESPONSE_SELECT_BY_ID_NOT_DELETED = EVENT_RESPONSE_SELECT + "WHERE deleted = FALSE AND event_id = ?";

    public static final String TIP_SELECT = """
    SELECT 
        t.id AS tip_id,
        t.title AS tip_title,
        t.content AS tip_content,
        t.date_time AS tip_timestamp,
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
        cod.id AS dest_country_id,
        cod.code AS dest_country_code,
        u.id AS user_id,
        u.email AS email,
        u.username AS username,
        u.firstname AS firstname,
        u.lastname AS lastname,
        u.password AS password,
        u.language AS language,
        u.blocked AS blocked,
        u.validated AS validated,
        un.id AS university_id,
        un.name AS university_name,
        un.abbreviation AS university_abbreviation,
        c.id AS city_id,
        c.name AS city_name,
        co.name AS country_name,
        co.id AS country_id,
        co.code AS country_code,
        ca.name AS career_name,
        ca.id AS career_id,
        u.profile_picture_id AS profile_picture_id,
        u.roles AS roles,
        u.blocked AS blocked
        FROM tips t
        JOIN journeys j on t.journey_id = j.id
        JOIN universities ud ON ud.id = j.destination_university_id
        JOIN cities cd ON ud.city_id = cd.id
        JOIN countries cod ON cod.id = cd.country_id
        JOIN users u ON j.user_id = u.id
        JOIN universities un ON un.id = u.university
        JOIN cities c ON c.id = un.city_id
        JOIN countries co ON co.id = c.country_id
        JOIN careers ca ON ca.id = u.career_id
            """;

    public static final String TIP_SELECT_BY_DATA = TIP_SELECT + "WHERE t.journey_id = ? AND t.title = ? AND t.content = ?";
    public static final String TIP_SELECT_BY_ID = TIP_SELECT + "WHERE t.id = ?";


    //ROWMAPPERS
    public static final RowMapper<Interest> INTEREST_ROW_MAPPER = (rs, n) ->
    new Interest(
        rs.getLong("interest_id"),
        rs.getString("interest_name")
    );
    
    public static final RowMapper<Country> COUNTRY_ROW_MAPPER = (rs, n) ->
    new Country(
        rs.getLong("country_id"),
        rs.getString("country_name"),
        rs.getString("country_code")
    );
    public static final RowMapper<Country> COUNTRY_DESTINATION_ROW_MAPPER = (rs, n) ->
    new Country(
        rs.getLong("dest_country_id"),
        rs.getString("dest_country_name"),
        rs.getString("dest_country_code")
    );

    public static final RowMapper<City> CITY_ROW_MAPPER = (rs, n) ->
        new City(
            rs.getString("city_name"),
            COUNTRY_ROW_MAPPER.mapRow(rs, n),
            rs.getLong("city_id")
        );
    public static final RowMapper<City> CITY_DESTINATION_ROW_MAPPER = (rs, n) ->
        new City(
            rs.getString("dest_city_name"),
            COUNTRY_ROW_MAPPER.mapRow(rs, n),
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
        rs.getBoolean("blocked"),
        rs.getBoolean("validated")
    );

    public static final RowMapper<Image> IMAGE_ROW_MAPPER = (rs, n) ->
    new Image(
        rs.getLong("image_id"),
        rs.getBytes("image_data")
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

    public static final RowMapper<JourneyResponse> JOURNEY_REPLY_ROW_MAPPER = (rs, n) ->
    new JourneyResponse(
        rs.getLong("id"),
        USER_ROW_MAPPER.mapRow(rs, n),
        JOURNEY_ROW_MAPPER.mapRow(rs, n),
        rs.getString("message"),
        rs.getTimestamp("date_time").toLocalDateTime()
    );

    public static final RowMapper<Event> EVENT_ROW_MAPPER = (rs, n) ->
    new Event(
        rs.getLong("id"),
        USER_ROW_MAPPER.mapRow(rs, n),
        rs.getDate("event_date").toLocalDate(),
        rs.getString("description"),
        rs.getInt("flyer_image_id"),
        CITY_DESTINATION_ROW_MAPPER.mapRow(rs, n),
        rs.getString("title"),
        rs.getTime("event_time") != null ? rs.getTime("event_time").toLocalTime() : null,
        rs.getString("address"),
        rs.getInt("attendees_limit") != 0 ? rs.getInt("attendees_limit") : null
    );

    public static final RowMapper<EventResponse> EVENT_RESPONSE_ROW_MAPPER = (rs, n) ->
    new EventResponse(
        rs.getLong("id"),
        USER_ROW_MAPPER.mapRow(rs, n),
        EVENT_ROW_MAPPER.mapRow(rs, n),
        rs.getString("message"),
        rs.getTimestamp("date_time").toLocalDateTime()
    );

    public static final RowMapper<Tip> TIP_ROW_MAPPER = (rs, n) ->
    new Tip(
        rs.getLong("tip_id"), 
        rs.getString("tip_title"),
        rs.getString("tip_content"),
        JOURNEY_ROW_MAPPER.mapRow(rs, n),
        rs.getTimestamp("tip_timestamp").toLocalDateTime()
    );

    //DELETES
    public static final void deleteEventAttendances(JdbcTemplate template){
        JdbcTestUtils.deleteFromTables(template, EVENT_ATTENDANCE_TABLE);
    }
    public static final void deleteEventReplies(JdbcTemplate template){
        JdbcTestUtils.deleteFromTables(template, EVENT_REPLY_TABLE);
    }
    public static final void deleteEvents(JdbcTemplate template){
        deleteEventAttendances(template);
        deleteEventReplies(template);
        JdbcTestUtils.deleteFromTables(template, EVENT_TABLE);
    }
    public static final void deleteEventsValid(JdbcTemplate template){
        deleteEventAttendances(template);
        deleteEventReplies(template);
        JdbcTestUtils.deleteFromTableWhere(template, EVENT_TABLE, "deleted = FALSE AND event_date >= CURRENT_DATE");
    }
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
        deleteEvents(template);
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
        deleteEvents(template);
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
        assertEqualsCountry(expected.getCountry(), actual.getCountry());
    }

    public static void assertEqualsCountry(Country expected, Country actual){
        assertNotNull(expected);
        assertNotNull(actual);
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getCode(), actual.getCode());
    }

    public static void assertEqualsInterest(Interest expected, Interest actual){
        assertNotNull(expected);
        assertNotNull(actual);
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
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

    public static void assertEqualsJourneyList(List<Journey> expected, List<Journey> actual){
        assertEquals(expected.size(), actual.size());
        for (Journey j : actual){
            assertTrue(expected.contains(j));
        }
    }

    public static void assertEqualsJourneyReply(JourneyResponse expected, JourneyResponse actual){
        assertNotNull(actual);
        assertNotNull(expected);
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getDateTime().withNano(0), actual.getDateTime().withNano(0));
        assertEqualsJourney(expected.getJourney(), actual.getJourney());
        assertEquals(expected.getMessage(), actual.getMessage());
        assertEqualsUser(expected.getUser(), actual.getUser());
    }

    public static void assertEqualsEvent(Event expected, Event actual){
        assertNotNull(actual);
        assertNotNull(expected);
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getAddress(), actual.getAddress());
        assertEquals(expected.getAttendeesLimit(), actual.getAttendeesLimit());
        assertEquals(expected.getDate(), actual.getDate());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getFlyerImageId(), actual.getFlyerImageId());
        assertEquals(expected.getFull(), actual.getFull());
        assertEquals(expected.getIsFuture(), actual.getIsFuture());
        assertEquals(expected.getTime(), actual.getTime());
        assertEquals(expected.getTitle(), actual.getTitle());
        assertEqualsUser(expected.getUser(), actual.getUser());
        assertEqualsCity(expected.getCity(), actual.getCity());
    }

    public static void assertEqualsEventReply(EventResponse expected, EventResponse actual){
        assertNotNull(actual);
        assertNotNull(expected);
        assertEquals(expected.getId(), actual.getId());
        assertTrue(expected.getDateTime().withNano(0).plusMinutes(1).isAfter(actual.getDateTime().withNano(0)));
        assertTrue(expected.getDateTime().withNano(0).plusMinutes(-1).isBefore(actual.getDateTime().withNano(0)));
        assertEqualsEvent(expected.getEvent(), actual.getEvent());
        assertEquals(expected.getMessage(), actual.getMessage());
        assertEqualsUser(expected.getUser(), actual.getUser());
    }

    public static void assertEqualsEvent(Event event, Map<String, Object> overrides){
        assertNotNull(event);
        long id = (long)overrides.getOrDefault("id", EVENT_1_ID);
        User user = (User)overrides.getOrDefault("user", USER_1);
        City city = (City)overrides.getOrDefault("city", CITY_1);
        LocalDate date = (LocalDate)overrides.getOrDefault("date", EVENT_DATE_DEFAULT);
        LocalTime time = (LocalTime)overrides.getOrDefault("time", EVENT_TIME_DEFAULT);
        String address = (String)overrides.getOrDefault("address", EVENT_ADDRESS_DEFAULT);
        Integer limit = (Integer)overrides.getOrDefault("limit", EVENT_ATTENDANCE_LIMIT_DEFAULT);
        String description = (String)overrides.getOrDefault("description", EVENT_DESCRIPTION_DEFAULT);
        String title = (String)overrides.getOrDefault("title", EVENT_TITLE_DEFAULT);
        Image image = (Image)overrides.getOrDefault("image", IMAGE_1);
        Event newEvent = new Event(id, user, date, description, image.getId(), city, title, time, address, limit);
        assertEqualsEvent(newEvent, event);
    }

    public static void assertEqualsEvent(Event event){
        assertEqualsEvent(EVENT_1, event);
    }

    public static void assertEqualsJourney(Journey journey){
        assertEqualsJourney(JOURNEY_1, journey);
    }

    public static void assertEqualsJourney(Journey journey, Map<String, Object> overrideParams){
        assertNotNull(journey);
        User user = ((User)overrideParams.getOrDefault("user", USER_1));
        University uni = ((University)overrideParams.getOrDefault("destination", UNI_2));
        LocalDate startDate = (LocalDate)overrideParams.getOrDefault("startDate", JOURNEY_START_DATE);
        LocalDate endDate = (LocalDate)overrideParams.getOrDefault("endDate", JOURNEY_END_DATE);
        String description = (String)overrideParams.getOrDefault("description", JOURNEY_DESCRIPTION);
        long id = (long)overrideParams.getOrDefault("id", JOURNEY_1_ID);
        Journey newJourney = new Journey(id, user, startDate, endDate, uni, description);

        assertEqualsJourney(newJourney, journey);
    }

    public static void assertUniversityDBDefaultState(JdbcTemplate template){
        assertEqualsUni(UNI_1, template.queryForObject(UNIVERSITY_SELECT_BY_ID, UNIVERSITY_ROW_MAPPER, UNIVERSITY_1_ID));
        assertEqualsUni(UNI_2, template.queryForObject(UNIVERSITY_SELECT_BY_ID, UNIVERSITY_ROW_MAPPER, UNIVERSITY_2_ID));
        assertEqualsUni(UNI_3, template.queryForObject(UNIVERSITY_SELECT_BY_ID, UNIVERSITY_ROW_MAPPER, UNIVERSITY_3_ID));
    }

    public static void assertEqualsUser(User user){
        assertEqualsUser(user, Map.of());
    }

    public static void assertEqualsUser(User user, Map<String, Object> overrideParams){
        assertNotNull(user);
        long id = (long)overrideParams.getOrDefault("id", USER_1_ID);
        String email = (String)overrideParams.getOrDefault("email", USER_1_MAIL);
        String username = (String)overrideParams.getOrDefault("username", USER_1_NAME);
        String firstname = (String)overrideParams.getOrDefault("firstname", USER_FIRSTNAME);
        String lastname = (String)overrideParams.getOrDefault("lastname", USER_LASTNAME);
        Locale locale = Locale.of((String)overrideParams.getOrDefault("locale", USER_LOCALE));
        Career career = (Career)overrideParams.getOrDefault("career", CAREER_1);
        University uni = (University)overrideParams.getOrDefault("university", UNI_1);
        Image image = (Image)overrideParams.getOrDefault("profilepic", IMAGE_1);
        boolean blocked = (boolean)overrideParams.getOrDefault("blocked", false);
        boolean validated = (boolean)overrideParams.getOrDefault("validated", true);
        User newUser = new User(id, email, username, firstname, lastname, uni, career, image.getId(), locale, blocked, validated);
        assertEqualsUser(newUser, user);
    }

    public static void assertUserDBDefaultStatus(JdbcTemplate template){
        assertEquals(TOTAL_USERS, JdbcTestUtils.countRowsInTable(template, USER_TABLE));
        assertEqualsUser(USER_1, template.queryForObject(USER_SELECT_BY_ID, USER_ROW_MAPPER, USER_1_ID));
        assertEqualsUser(USER_2, template.queryForObject(USER_SELECT_BY_ID, USER_ROW_MAPPER, USER_2_ID));
        assertEqualsUser(USER_3, template.queryForObject(USER_SELECT_BY_ID, USER_ROW_MAPPER, USER_3_ID));
        assertEqualsUser(USER_4, template.queryForObject(USER_SELECT_BY_ID, USER_ROW_MAPPER, USER_4_ID));
        assertEqualsUser(USER_I1,template.queryForObject(USER_SELECT_BY_ID, USER_ROW_MAPPER, USER_I1_ID));
        assertEqualsUser(USER_I2,template.queryForObject(USER_SELECT_BY_ID, USER_ROW_MAPPER, USER_I2_ID));
        assertEqualsUser(USER_I3,template.queryForObject(USER_SELECT_BY_ID, USER_ROW_MAPPER, USER_I3_ID));
    }

    public static void assertEqualsReport(Report expected, Report actual){
        assertNotNull(expected);
        assertNotNull(actual);
        assertNotNull(actual.getId());
        if (expected.getId() != null)
            assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getDescription(), expected.getDescription());
        assertEquals(expected.getReason(), actual.getReason());
        assertEquals(expected.isDeleted(), actual.isDeleted());
        assertEquals(expected.getStatus(), actual.getStatus());
        assertTrue(expected.getCreatedAt().plusMinutes(1).isAfter(actual.getCreatedAt()));
        assertTrue(expected.getCreatedAt().plusMinutes(-1).isBefore(actual.getCreatedAt()));        
        assertTrue(expected.getUpdatedAt().plusMinutes(1).isAfter(actual.getUpdatedAt()));
        assertTrue(expected.getUpdatedAt().plusMinutes(-1).isBefore(actual.getUpdatedAt()));
        if (expected.getEvent() != null && actual.getEvent() != null){
            assertEqualsEvent(expected.getEvent(), actual.getEvent());
        }
        if (expected.getJourney() != null && actual.getJourney() != null){
            assertEqualsJourney(expected.getJourney(), actual.getJourney());
        }
        if (expected.getEventResponse() != null && actual.getEventResponse() != null){
            assertEqualsEventReply(expected.getEventResponse(), actual.getEventResponse());
        }
        if (expected.getJourneyResponse() != null && actual.getJourneyResponse() != null){
            assertEqualsJourneyReply(expected.getJourneyResponse(), actual.getJourneyResponse());
        }        
    }

    public static void assertEqualsTip(Tip expected, Tip actual){
        assertNotNull(expected);
        assertNotNull(actual);
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getContent(), actual.getContent());
        assertEquals(expected.getTitle(), actual.getTitle());
        assertEqualsJourney(expected.getJourney(), actual.getJourney());
        assertTrue(expected.getDateTime().plusMinutes(1).isAfter(actual.getDateTime()));
        assertTrue(expected.getDateTime().plusMinutes(-1).isBefore(actual.getDateTime()));
    }

    //INSERTERS
    public static Event insertEvent(DataSource ds, Map<String, Object> overrides){
        SimpleJdbcInsert insert = new SimpleJdbcInsert(ds).withTableName(EVENT_TABLE).usingGeneratedKeyColumns("id");
        SimpleJdbcInsert insertAttendance = new SimpleJdbcInsert(ds).withTableName(EVENT_ATTENDANCE_TABLE);

        HashMap<String, Object> params = new HashMap<>();
        params.put("user_id", ((User)overrides.getOrDefault("user", USER_1)).getId());
        params.put("city_id", ((City)overrides.getOrDefault("city", CITY_1)).getId());
        params.put("event_date", Date.valueOf((LocalDate)overrides.getOrDefault("date", EVENT_DATE_DEFAULT)));
        params.put("event_time", Time.valueOf((LocalTime)overrides.getOrDefault("time", EVENT_TIME_DEFAULT)));
        params.put("address", (String)overrides.getOrDefault("address", EVENT_ADDRESS_DEFAULT));
        params.put("attendees_limit", (Integer)overrides.getOrDefault("limit", EVENT_ATTENDANCE_LIMIT_DEFAULT));
        params.put("attendees_count", (Integer)overrides.getOrDefault("willAttend", EVENT_ATTENDANCE_DEFAULT));
        params.put("description", (String)overrides.getOrDefault("description", EVENT_DESCRIPTION_DEFAULT));
        params.put("title", (String)overrides.getOrDefault("title", EVENT_TITLE_DEFAULT));
        params.put("flyer_image_id", ((Image)overrides.getOrDefault("image", IMAGE_1)).getId());
        params.put("deleted", (Boolean)overrides.getOrDefault("deleted", false));
        params.put("deleted_message", (String)overrides.getOrDefault("deletedMessage", null));

        long key = insert.executeAndReturnKey(params).longValue();
        if (overrides.get("attending") != null){
            insertAttendance.execute(Map.of("user_id", ((User)overrides.get("attending")).getId(), "event_id", key));
        }
        return new Event(
            key,
            (User)overrides.getOrDefault("user", USER_1),
            ((Date)params.get("event_date")).toLocalDate(),
            (String)params.get("description"),
            (long)params.get("flyer_image_id"),
            (City)overrides.getOrDefault("city", CITY_1),
            (String)params.get("title"),
            params.get("event_time") != null ? ((Time)params.get("event_time")).toLocalTime() : null,
            (String)params.get("address"),
            (Integer)params.get("attendees_limit")
        );
    }

    public static Journey insertJourney(DataSource ds, Map<String, Object> overrideParams){
        SimpleJdbcInsert insert = new SimpleJdbcInsert(ds).withTableName(JOURNEY_TABLE).usingGeneratedKeyColumns("id");

        HashMap<String, Object> params = new HashMap<>();
        params.put("user_id", ((User)overrideParams.getOrDefault("user", USER_2)).getId());
        params.put("destination_university_id", ((University)overrideParams.getOrDefault("destination", UNI_2)).getId());
        params.put("start_date", Date.valueOf((LocalDate)overrideParams.getOrDefault("startDate", JOURNEY_START_DATE)));
        params.put("end_date", Date.valueOf((LocalDate)overrideParams.getOrDefault("endDate", JOURNEY_END_DATE)));
        params.put("description", overrideParams.getOrDefault("description", JOURNEY_DESCRIPTION));
        params.put("deleted", overrideParams.getOrDefault("deleted", false));
        params.put("deleted_message", overrideParams.getOrDefault("deletedMessage", null));
        long id = insert.executeAndReturnKey(params).longValue();

        return new Journey(
            id,
            ((User)overrideParams.getOrDefault("user", USER_2)),
            (LocalDate)overrideParams.getOrDefault("startDate", JOURNEY_START_DATE),
            (LocalDate)overrideParams.getOrDefault("endDate", JOURNEY_END_DATE),
            (University)overrideParams.getOrDefault("destination", UNI_2),
            (String)overrideParams.getOrDefault("description", JOURNEY_DESCRIPTION)
        );
    }

    public static User insertUser(DataSource ds, Map<String, Object> overrideParams){
        SimpleJdbcInsert insert = new SimpleJdbcInsert(ds).withTableName(TestUtils.USER_TABLE).usingGeneratedKeyColumns("id");

        HashMap<String, Object> params = new HashMap<>();
        params.put("email", overrideParams.getOrDefault("email", USER_1_MAIL));
        params.put("username", overrideParams.getOrDefault("username", USER_1_NAME));
        params.put("firstname", overrideParams.getOrDefault("firstname", USER_FIRSTNAME));
        params.put("lastname", overrideParams.getOrDefault("lastname", USER_LASTNAME));
        params.put("password", overrideParams.getOrDefault("password", USER_PASSWORD));
        params.put("language", overrideParams.getOrDefault("locale", USER_LOCALE));
        params.put("university", overrideParams.getOrDefault("university", UNIVERSITY_1_ID));
        params.put("career_id", overrideParams.getOrDefault("career", CAREER_1_ID));
        params.put("profile_picture_id", overrideParams.getOrDefault("profilepic", IMAGE_1_ID));
        params.put("roles", overrideParams.getOrDefault("roles", USER_ROLE));
        params.put("blocked", overrideParams.getOrDefault("blocked", false));
        params.put("token", overrideParams.getOrDefault("token", USER_TOKEN_NEW));
        params.put("token_expiration", Date.valueOf((LocalDate)overrideParams.getOrDefault("tokenExpiration", USER_EXPIRATION_DEFAULT)));
        params.put("validated",overrideParams.getOrDefault("validated", true));
        long id = insert.executeAndReturnKey(params).longValue();

        return new User(
            id,
            (String)overrideParams.getOrDefault("email", USER_1_MAIL),
            (String)overrideParams.getOrDefault("username", USER_1_NAME),
            (String)overrideParams.getOrDefault("firstname", USER_FIRSTNAME),
            (String)overrideParams.getOrDefault("lastname", USER_LASTNAME),
            (University)overrideParams.getOrDefault("university", UNI_1),
            (Career)overrideParams.getOrDefault("career", CAREER_1),
            (long)overrideParams.getOrDefault("profilepic", IMAGE_1_ID),
            Locale.of((String)overrideParams.getOrDefault("locale", USER_LOCALE)),
            (boolean)overrideParams.getOrDefault("blocked", USER_BLOCKED),
            (boolean)overrideParams.getOrDefault("validated", true)
        );
    }
}
