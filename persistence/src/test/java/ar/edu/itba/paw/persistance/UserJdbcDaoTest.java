package ar.edu.itba.paw.persistance;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.itba.paw.models.Career;
import ar.edu.itba.paw.models.University;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.UserPassword;
import ar.edu.itba.paw.persistence.UserJdbcDao;

@Sql(scripts = "classpath:schema.sql")
@Transactional
@Rollback
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class UserJdbcDaoTest {

    private static final String USER_TABLE = "users";
    private static final String USERNAME = "newUser";
    private static final String FIRSTNAME = "New";
    private static final String LASTNAME = "User";
    private static final String USERMAIL = "user@gmail.com";
    private static final String PASSWORD = "superSecret";
    private static final String LOCALE = "es";
    private static University UNIVERSITY;
    private static Career CAREER;
    private static long PROFILEPICID;

    @Autowired
    private DataSource ds;

    private UserJdbcDao userDao;

    private JdbcTemplate jdbcTemplate;

    private SimpleJdbcInsert insert;

    private RowMapper<User> USER_ROW_MAPPER = (rs, rowNum) -> 
    new User(rs.getLong("id"), 
        rs.getString("email"), 
        rs.getString("username"), 
        rs.getString("firstname"), 
        rs.getString("lastname"), 
        new University(rs.getLong("university"), null, null, null), 
        new Career(rs.getLong("career_id"), null), 
        rs.getLong("profile_picture_id"), Locale.of(rs.getString("language")));

    @Before
    public void setUp(){
        jdbcTemplate = new JdbcTemplate(ds);
        userDao = new UserJdbcDao(ds);
        insert = new SimpleJdbcInsert(ds).withTableName(USER_TABLE).usingGeneratedKeyColumns("id");

        jdbcTemplate.execute("INSERT INTO images(content) VALUES('ffffffffff')");
        jdbcTemplate.execute("INSERT INTO countries(name, code) VALUES('Argentina', 'AR')");
        jdbcTemplate.execute("INSERT INTO cities(name, country_id) VALUES('Buenos Aires', (SELECT MIN(id) FROM countries))");
        jdbcTemplate.execute("INSERT INTO universities(name, city_id, abbreviation) VALUES('Instituto Tecnologico muy largo', (SELECT MIN(id) FROM cities), 'ITBA')");
        jdbcTemplate.execute("INSERT INTO careers(name) VALUES('Ingenieria Informatica')");

        UNIVERSITY = jdbcTemplate.query("SELECT id FROM universities LIMIT 1", (rs, rowNum) -> new University(rs.getLong("id"), null, null, null)).stream().findFirst().get();
        CAREER = jdbcTemplate.query("SELECT id FROM careers LIMIT 1", (rs, rowNum) -> new Career(rs.getLong("id"), null)).stream().findFirst().get();
        PROFILEPICID = jdbcTemplate.query("SELECT id FROM images LIMIT 1", (rs, rowNum) -> rs.getLong("id")).stream().findFirst().get();
    }

    private void assertEqualsUser(User user){
        assertEquals(USERMAIL, user.getEmail());
        assertEquals(USERNAME, user.getUsername());
        assertEquals(FIRSTNAME, user.getFirstname());
        assertEquals(LASTNAME, user.getLastname());
        assertEquals(Locale.of(LOCALE), user.getLocale());
        assertEquals(CAREER.getId(), user.getCareer().getId());
        assertEquals(UNIVERSITY.getId(), user.getUniversity().getId());
        assertEquals(PROFILEPICID, user.getProfilePictureId());
    }

    @Test
    public void testCreateUser(){
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "journeys", USER_TABLE); //journeys included due to deletion constraints
        final User user = userDao.create(USERMAIL, USERNAME, FIRSTNAME, LASTNAME, UNIVERSITY, CAREER, PROFILEPICID, PASSWORD, Locale.of(LOCALE));

        assertNotNull(user);
        assertEqualsUser(user);
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, USER_TABLE));
    }

    @Test(expected = DataAccessException.class)
    public void testCreateUserNoMail(){
        userDao.create(null, USERNAME, FIRSTNAME, LASTNAME, UNIVERSITY, CAREER, PROFILEPICID, PASSWORD, Locale.of(LOCALE));
    }
    @Test(expected = DataAccessException.class)
    public void testCreateUserNoUsername(){
        userDao.create(USERMAIL, null, FIRSTNAME, LASTNAME, UNIVERSITY, CAREER, PROFILEPICID, PASSWORD, Locale.of(LOCALE));
    }
    @Test(expected = DataAccessException.class)
    public void testCreateUserNoFirstName(){
        userDao.create(USERMAIL, USERNAME, null, LASTNAME, UNIVERSITY, CAREER, PROFILEPICID, PASSWORD, Locale.of(LOCALE));
    }
    @Test(expected = DataAccessException.class)
    public void testCreateUserNoLastName(){
        userDao.create(USERMAIL, USERNAME, FIRSTNAME, null, UNIVERSITY, CAREER, PROFILEPICID, PASSWORD, Locale.of(LOCALE));
    }
    @Test(expected = NullPointerException.class)
    public void testCreateUserNoUniversity(){
        userDao.create(USERMAIL, USERNAME, FIRSTNAME, LASTNAME, null, CAREER, PROFILEPICID, PASSWORD, Locale.of(LOCALE));
    }
    @Test(expected = DataAccessException.class)
    public void testCreateUserInvalidUniversity(){
        userDao.create(USERMAIL, USERNAME, FIRSTNAME, LASTNAME, new University(1034234123, null, null, null), CAREER, PROFILEPICID, PASSWORD, Locale.of(LOCALE));
    }
    @Test(expected = NullPointerException.class)
    public void testCreateUserNoCareer(){
        userDao.create(USERMAIL, USERNAME, FIRSTNAME, LASTNAME, UNIVERSITY, null, PROFILEPICID, PASSWORD, Locale.of(LOCALE));
    }
    @Test(expected = DataAccessException.class)
    public void testCreateUserInvalidCareer(){
        userDao.create(USERMAIL, USERNAME, FIRSTNAME, LASTNAME, UNIVERSITY, new Career((long)1313423,null), PROFILEPICID, PASSWORD, Locale.of(LOCALE));
    }
    @Test(expected = DataAccessException.class)
    public void testCreateUserInvalidPic(){
        userDao.create(USERMAIL, USERNAME, FIRSTNAME, LASTNAME, UNIVERSITY, CAREER, 123123123, PASSWORD, Locale.of(LOCALE));
    }
    @Test(expected = DataAccessException.class)
    public void testCreateUserNoPassword(){
        userDao.create(USERMAIL, USERNAME, FIRSTNAME, LASTNAME, UNIVERSITY, CAREER, PROFILEPICID, null, Locale.of(LOCALE));
    }
    @Test(expected = DataAccessException.class)
    public void testCreateUserNoLocale(){
        userDao.create(USERMAIL, USERNAME, FIRSTNAME, LASTNAME, UNIVERSITY, CAREER, PROFILEPICID, PASSWORD, null);
    }

    @Test
    public void testFindUserById(){
        final Map<String, Object> params = Map.of(
            "username", USERNAME,
            "email", USERMAIL,
            "firstname", FIRSTNAME,
            "lastname", LASTNAME,
            "university", UNIVERSITY.getId(),
            "career_id", CAREER.getId(),
            "profile_picture_id", PROFILEPICID,
            "password", PASSWORD,
            "language", LOCALE);
        final long id = insert.executeAndReturnKey(params).longValue();

        final Optional<User> maybeUser = userDao.findById(id);

        assertNotNull(maybeUser);
        assertTrue(maybeUser.isPresent());
        final User user = maybeUser.get();
        assertEqualsUser(user);
    }
    @Test
    public void testFindUserByIdMissing(){
        final Optional<User> maybeUser = userDao.findById(12341234);
        assertNotNull(maybeUser);
        assertFalse(maybeUser.isPresent());
    }
    @Test
    public void testFindUserByEmail(){
        final Map<String, Object> params = Map.of(
            "username", USERNAME,
            "email", USERMAIL,
            "firstname", FIRSTNAME,
            "lastname", LASTNAME,
            "university", UNIVERSITY.getId(),
            "career_id", CAREER.getId(),
            "profile_picture_id", PROFILEPICID,
            "password", PASSWORD,
            "language", LOCALE,
        "roles", "user");
        insert.execute(params);

        final Optional<User> maybeUser = userDao.findByEmail(USERMAIL);

        assertNotNull(maybeUser);
        assertTrue(maybeUser.isPresent());
        final User user = maybeUser.get();
        assertEqualsUser(user);
    }
    @Test
    public void testFindUserByEmailMissing(){
        final Optional<User> maybeUser = userDao.findByEmail("totallyRealEmail@legitEmailService.com");
        assertNotNull(maybeUser);
        assertFalse(maybeUser.isPresent());
    }
    @Test
    public void testFindUserByEmailWithPassword(){
        final Map<String, Object> params = Map.of(
            "username", USERNAME,
            "email", USERMAIL,
            "firstname", FIRSTNAME,
            "lastname", LASTNAME,
            "university", UNIVERSITY.getId(),
            "career_id", CAREER.getId(),
            "profile_picture_id", PROFILEPICID,
            "password", PASSWORD,
            "language", LOCALE);
        insert.execute(params);

        final Optional<UserPassword> maybeUser = userDao.findByEmailWithPass(USERMAIL);

        assertNotNull(maybeUser);
        assertTrue(maybeUser.isPresent());
        final User user = maybeUser.get();
        assertEqualsUser(user);
    }
    @Test
    public void testFindUserByEmailWithPasswordMissing(){
        final Optional<UserPassword> maybeUser = userDao.findByEmailWithPass("totallyRealEmail@legitEmailService.com");
        assertNotNull(maybeUser);
        assertFalse(maybeUser.isPresent());
    }
    @Test
    public void testFindUserByUsername(){
        final Map<String, Object> params = Map.of(
            "username", USERNAME,
            "email", USERMAIL,
            "firstname", FIRSTNAME,
            "lastname", LASTNAME,
            "university", UNIVERSITY.getId(),
            "career_id", CAREER.getId(),
            "profile_picture_id", PROFILEPICID,
            "password", PASSWORD,
            "language", LOCALE);
        insert.execute(params);

        final Optional<User> maybeUser = userDao.findByUsername(USERNAME);

        assertNotNull(maybeUser);
        assertTrue(maybeUser.isPresent());
        final User user = maybeUser.get();
        assertEqualsUser(user);
    }
    @Test
    public void testFindUserByUsernameMissing(){
        final Optional<User> maybeUser = userDao.findByUsername("totallyLegitUser");
        assertFalse(maybeUser.isPresent());
    }
    @Test
    public void testChangePassword(){
        final Map<String, Object> params = Map.of(
            "username", USERNAME,
            "email", USERMAIL,
            "firstname", FIRSTNAME,
            "lastname", LASTNAME,
            "university", UNIVERSITY.getId(),
            "career_id", CAREER.getId(),
            "profile_picture_id", PROFILEPICID,
            "password", "wrongPassword",
            "language", LOCALE);
        insert.execute(params);

        userDao.changePassword(USERMAIL, PASSWORD);

        final Optional<String> newPassword = jdbcTemplate.query("SELECT PASSWORD FROM users WHERE email = ?", (rs, rowNum) -> rs.getString("password"), USERMAIL).stream().findFirst();
        assertNotNull(newPassword);
        assertTrue(newPassword.isPresent());
        assertEquals(PASSWORD, newPassword.get());
    }

    @Test
    public void testExistsByUsernameDoesExist(){
        final Map<String, Object> params = Map.of(
            "username", USERNAME,
            "email", USERMAIL,
            "firstname", FIRSTNAME,
            "lastname", LASTNAME,
            "university", UNIVERSITY.getId(),
            "career_id", CAREER.getId(),
            "profile_picture_id", PROFILEPICID,
            "password", PASSWORD,
            "language", LOCALE);
        insert.execute(params);

        final boolean exists = userDao.existsByUsername(USERNAME);

        assertTrue(exists);
    }
    @Test
    public void testExistsByUsernameDoesNotExist(){
        final boolean exists = userDao.existsByUsername("totallyLegitUser");

        assertFalse(exists);
    }
    @Test
    public void testExistsByEmailDoesExist(){
        final Map<String, Object> params = Map.of(
            "username", USERNAME,
            "email", USERMAIL,
            "firstname", FIRSTNAME,
            "lastname", LASTNAME,
            "university", UNIVERSITY.getId(),
            "career_id", CAREER.getId(),
            "profile_picture_id", PROFILEPICID,
            "password", PASSWORD,
            "language", LOCALE);
        insert.execute(params);

        final boolean exists = userDao.existsByEmail(USERMAIL);

        assertTrue(exists);
    }
    @Test
    public void testExistsByEmailDoesNotExist(){
        final boolean exists = userDao.existsByEmail("totallyRealEmail@legitEmailService.com");

        assertFalse(exists);
    }

    @Test
    public void testUpdateProfileInfo(){
        final Map<String, Object> params = Map.of(
            "username", "wrongUsername",
            "email", USERMAIL,
            "firstname", "wrongFirstname",
            "lastname", "WrongLastName",
            "university", UNIVERSITY.getId(),
            "career_id", CAREER.getId(),
            "profile_picture_id", PROFILEPICID,
            "password", PASSWORD,
            "language", LOCALE);
        final long id = insert.executeAndReturnKey(params).longValue();

        userDao.updateProfileInfo(id, FIRSTNAME, LASTNAME, USERNAME);

        final Optional<User> maybeUser = jdbcTemplate.query("SELECT * FROM users WHERE id = ?", USER_ROW_MAPPER, id).stream().findFirst();

        assertNotNull(maybeUser);
        assertTrue(maybeUser.isPresent());
        final User user = maybeUser.get();
        assertEqualsUser(user);    
    }
    @Test(expected = DataAccessException.class)
    public void testUpdateProfileInfoDuplicatedUsername(){
        final Map<String, Object> params = Map.of(
            "username", USERNAME,
            "email", "anotherMail@gmail.com",
            "firstname", "wrongFirstname",
            "lastname", "WrongLastName",
            "university", UNIVERSITY.getId(),
            "career_id", CAREER.getId(),
            "profile_picture_id", PROFILEPICID,
            "password", PASSWORD,
            "language", LOCALE);
        insert.execute(params);
        final Map<String, Object> params2 = Map.of(
            "username", "wrongUsername",
            "email", USERMAIL,
            "firstname", "wrongFirstname",
            "lastname", "WrongLastName",
            "university", UNIVERSITY.getId(),
            "career_id", CAREER.getId(),
            "profile_picture_id", PROFILEPICID,
            "password", PASSWORD,
            "language", LOCALE);
        final long id = insert.executeAndReturnKey(params2).longValue();

        userDao.updateProfileInfo(id, FIRSTNAME, LASTNAME, USERNAME);
    }
    @Test
    public void testUpdateProfileInfoNoParams(){
        final Map<String, Object> params2 = Map.of(
            "username", USERNAME,
            "email", USERMAIL,
            "firstname", FIRSTNAME,
            "lastname", LASTNAME,
            "university", UNIVERSITY.getId(),
            "career_id", CAREER.getId(),
            "profile_picture_id", PROFILEPICID,
            "password", PASSWORD,
            "language", LOCALE);
        final long id = insert.executeAndReturnKey(params2).longValue();

        userDao.updateProfileInfo(id, null, null, null);

        final Optional<User> maybeUser = jdbcTemplate.query("SELECT * FROM users WHERE id = ?", USER_ROW_MAPPER, id).stream().findFirst();

        assertNotNull(maybeUser);
        assertTrue(maybeUser.isPresent());
        final User user = maybeUser.get();
        assertEqualsUser(user);   
    }
    @Test
    public void testUpdateProfileInfoMissingUser(){
        userDao.updateProfileInfo(123123, null, null, null);  
        //No checks needed, if it tried to update the empty DB, we'd get an exception and fail automatically.
    }

    @Test
    public void testUpdateLocale(){
        final Map<String, Object> params2 = Map.of(
            "username", USERNAME,
            "email", USERMAIL,
            "firstname", FIRSTNAME,
            "lastname", LASTNAME,
            "university", UNIVERSITY.getId(),
            "career_id", CAREER.getId(),
            "profile_picture_id", PROFILEPICID,
            "password", PASSWORD,
            "language", "en");
        final long id = insert.executeAndReturnKey(params2).longValue();

        userDao.updateLocale(id, Locale.of(LOCALE));

        final Optional<User> maybeUser = jdbcTemplate.query("SELECT * FROM users WHERE id = ?", USER_ROW_MAPPER, id).stream().findFirst();

        assertNotNull(maybeUser);
        assertTrue(maybeUser.isPresent());
        final User user = maybeUser.get();
        assertEqualsUser(user);   
    }
    @Test(expected = DataAccessException.class)
    public void testUpdateLocaleWrongLocale(){
        final Map<String, Object> params2 = Map.of(
            "username", USERNAME,
            "email", USERMAIL,
            "firstname", FIRSTNAME,
            "lastname", LASTNAME,
            "university", UNIVERSITY.getId(),
            "career_id", CAREER.getId(),
            "profile_picture_id", PROFILEPICID,
            "password", PASSWORD,
            "language", "en");
        final long id = insert.executeAndReturnKey(params2).longValue();

        userDao.updateLocale(id, Locale.of("jp"));  
    }
    @Test
    public void testUpdateLocaleMissingLocale(){
        final Map<String, Object> params2 = Map.of(
            "username", USERNAME,
            "email", USERMAIL,
            "firstname", FIRSTNAME,
            "lastname", LASTNAME,
            "university", UNIVERSITY.getId(),
            "career_id", CAREER.getId(),
            "profile_picture_id", PROFILEPICID,
            "password", PASSWORD,
            "language", LOCALE);
        final long id = insert.executeAndReturnKey(params2).longValue();

        userDao.updateLocale(id, null);

        final Optional<User> maybeUser = jdbcTemplate.query("SELECT * FROM users WHERE id = ?", USER_ROW_MAPPER, id).stream().findFirst();

        assertNotNull(maybeUser);
        assertTrue(maybeUser.isPresent());
        final User user = maybeUser.get();
        assertEqualsUser(user);   
    }
    @Test
    public void testUpdateLocaleMissingUser(){
        userDao.updateLocale(1321423, Locale.of(LOCALE));  
    }

    // // Academic affiliations
    // void updateUniversity(long userId, long universityId);
    // void updateCareer(long userId, long careerId);

    // // Profile picture
    // void updateProfilePicture(long userId, long profilePictureId);

    // void update(long userId, String firstname, String lastname, String username, Long universityId, Long careerId, Locale locale);
}   
