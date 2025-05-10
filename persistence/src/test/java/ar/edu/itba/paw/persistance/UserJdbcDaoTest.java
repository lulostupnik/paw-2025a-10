package ar.edu.itba.paw.persistance;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import javax.sql.DataSource;

import ar.edu.itba.paw.models.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.itba.paw.persistence.UserJdbcDao;

@Transactional
@Rollback
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class UserJdbcDaoTest {

    private static University UNIVERSITY_1;
    private static University UNIVERSITY_2;
    private static Career CAREER_1;
    private static Career CAREER_2;
    private static Image PROFILEPIC_1;
    private static Image PROFILEPIC_2;

    private User USER_1;
    private User USER_2;
    private User USER_3;
    private User USER_4;
    private User USER_I1;
    private User USER_I2;
    private User USER_I3;

    private Journey JOURNEY_1;

    @Autowired
    private DataSource ds;

    @Autowired
    private UserJdbcDao userDao;

    private JdbcTemplate jdbcTemplate;

    private SimpleJdbcInsert insert;

    private static void assertEqualsUser(User user){
        assertEqualsUser(user, Map.of());
    }

    private static void assertEqualsUser(User user, Map<String, Object> overrideParams){
        assertNotNull(user);
        assertEquals(overrideParams.getOrDefault("email", TestUtils.USER_1_MAIL), user.getEmail());
        assertEquals(overrideParams.getOrDefault("username", TestUtils.USER_1_NAME), user.getUsername());
        assertEquals(overrideParams.getOrDefault("firstname", TestUtils.USER_FIRSTNAME), user.getFirstname());
        assertEquals(overrideParams.getOrDefault("lastname", TestUtils.USER_LASTNAME), user.getLastname());
        assertEquals(Locale.of((String)overrideParams.getOrDefault("locale", TestUtils.USER_LOCALE)), user.getLocale());
        assertEquals(((Career)overrideParams.getOrDefault("career", CAREER_1)).getName(), user.getCareer().getName());
        assertEquals(((Career)overrideParams.getOrDefault("career", CAREER_1)).getId(), user.getCareer().getId());
        assertEquals(((University)overrideParams.getOrDefault("university", UNIVERSITY_1)).getId(), user.getUniversity().getId());
        assertEquals(((University)overrideParams.getOrDefault("university", UNIVERSITY_1)).getName(), user.getUniversity().getName());
        assertEquals(((University)overrideParams.getOrDefault("university", UNIVERSITY_1)).getAbbreviation(), user.getUniversity().getAbbreviation());
        assertEquals(((University)overrideParams.getOrDefault("university", UNIVERSITY_1)).getCity().getId(), user.getUniversity().getCity().getId());
        assertEquals(((University)overrideParams.getOrDefault("university", UNIVERSITY_1)).getCity().getName(), user.getUniversity().getCity().getName());
        assertEquals(((University)overrideParams.getOrDefault("university", UNIVERSITY_1)).getCity().getCountry(), user.getUniversity().getCity().getCountry());
        assertEquals(((Image)overrideParams.getOrDefault("profilepic", PROFILEPIC_1)).getId(), user.getProfilePictureId());
        assertEquals(overrideParams.getOrDefault("blocked", false), user.isBlocked());
    }

    private void assertUserDBDefaultStatus(){
        assertEquals(TestUtils.TOTAL_USERS, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.USER_TABLE));
        TestUtils.assertEqualsUser(USER_1, jdbcTemplate.queryForObject(TestUtils.USER_SELECT_BY_ID, TestUtils.USER_ROW_MAPPER, USER_1.getId()));
        TestUtils.assertEqualsUser(USER_2, jdbcTemplate.queryForObject(TestUtils.USER_SELECT_BY_ID, TestUtils.USER_ROW_MAPPER, USER_2.getId()));
        TestUtils.assertEqualsUser(USER_3, jdbcTemplate.queryForObject(TestUtils.USER_SELECT_BY_ID, TestUtils.USER_ROW_MAPPER, USER_3.getId()));
        TestUtils.assertEqualsUser(USER_4, jdbcTemplate.queryForObject(TestUtils.USER_SELECT_BY_ID, TestUtils.USER_ROW_MAPPER, USER_4.getId()));
        TestUtils.assertEqualsUser(USER_I1, jdbcTemplate.queryForObject(TestUtils.USER_SELECT_BY_ID, TestUtils.USER_ROW_MAPPER, USER_I1.getId()));
        TestUtils.assertEqualsUser(USER_I2, jdbcTemplate.queryForObject(TestUtils.USER_SELECT_BY_ID, TestUtils.USER_ROW_MAPPER, USER_I2.getId()));
        TestUtils.assertEqualsUser(USER_I3, jdbcTemplate.queryForObject(TestUtils.USER_SELECT_BY_ID, TestUtils.USER_ROW_MAPPER, USER_I3.getId()));
    }

    private static void assertEqualsMaybeUser(Optional<User> maybeUser){
        assertEqualsMaybeUser(maybeUser, Map.of());
    }

    private static void assertEqualsMaybeUser(Optional<User> maybeUser, Map<String, Object> overrideParams){
        assertNotNull(maybeUser);
        assertTrue(maybeUser.isPresent());
        final User user = maybeUser.get();
        assertEqualsUser(user, overrideParams); 
    }

    private static void assertEqualsUserPassword(UserAuthInfo up){
        assertNotNull(up);
        assertEquals(TestUtils.USER_1_MAIL, up.getEmail());
        assertEquals(TestUtils.USER_PASSWORD, up.getPassword());
        assertEquals(TestUtils.USER_ROLE, up.getRole());
        assertEquals(TestUtils.USER_BLOCKED, up.isBlocked());
    }

    private long insertUser(Map<String, Object> overrideParams){
        HashMap<String, Object> params = new HashMap<>();
        params.put("email", overrideParams.getOrDefault("email", TestUtils.USER_1_MAIL));
        params.put("username", overrideParams.getOrDefault("username", TestUtils.USER_1_NAME));
        params.put("firstname", overrideParams.getOrDefault("firstname", TestUtils.USER_FIRSTNAME));
        params.put("lastname", overrideParams.getOrDefault("lastname", TestUtils.USER_LASTNAME));
        params.put("password", overrideParams.getOrDefault("password", TestUtils.USER_PASSWORD));
        params.put("language", overrideParams.getOrDefault("locale", TestUtils.USER_LOCALE));
        params.put("university", overrideParams.getOrDefault("university", UNIVERSITY_1.getId()));
        params.put("career_id", overrideParams.getOrDefault("career", CAREER_1.getId()));
        params.put("profile_picture_id", overrideParams.getOrDefault("profilepic", PROFILEPIC_1.getId()));
        params.put("roles", overrideParams.getOrDefault("roles", TestUtils.USER_ROLE));
        params.put("blocked", overrideParams.getOrDefault("blocked", false));
        params.put("validate_token", overrideParams.getOrDefault("token", TestUtils.USER_VALID_TOKEN_DEFAULT));
        params.put("validate_token_expiration_date", Date.valueOf((LocalDate)overrideParams.getOrDefault("tokenExpiration", TestUtils.USER_EXPIRATION_DEFAULT)));
        return insert.executeAndReturnKey(params).longValue();
    }

    @Before
    public void setUp(){
        jdbcTemplate = new JdbcTemplate(ds);
        insert = new SimpleJdbcInsert(ds).withTableName(TestUtils.USER_TABLE).usingGeneratedKeyColumns("id");

        UNIVERSITY_1 = jdbcTemplate.queryForObject(TestUtils.UNIVERSITY_SELECT_BY_ABBR, TestUtils.UNIVERSITY_ROW_MAPPER, TestUtils.UNIVERSITY_1_CODE);
        UNIVERSITY_2 = jdbcTemplate.queryForObject(TestUtils.UNIVERSITY_SELECT_BY_ABBR, TestUtils.UNIVERSITY_ROW_MAPPER, TestUtils.UNIVERSITY_2_CODE);
        CAREER_1 = jdbcTemplate.queryForObject(TestUtils.CAREER_SELECT_BY_NAME, TestUtils.CAREER_ROW_MAPPER, TestUtils.CAREER_1_NAME);
        CAREER_2 = jdbcTemplate.queryForObject(TestUtils.CAREER_SELECT_BY_NAME, TestUtils.CAREER_ROW_MAPPER, TestUtils.CAREER_2_NAME);
        PROFILEPIC_1 = jdbcTemplate.queryForObject(TestUtils.IMAGE_SELECT_BY_DATA, TestUtils.IMAGE_ROW_MAPPER, TestUtils.IMAGE_1_DATA);
        PROFILEPIC_2 = jdbcTemplate.queryForObject(TestUtils.IMAGE_SELECT_BY_DATA, TestUtils.IMAGE_ROW_MAPPER, TestUtils.IMAGE_2_DATA);
        USER_1 = jdbcTemplate.queryForObject(TestUtils.USER_SELECT_BY_EMAIL, TestUtils.USER_ROW_MAPPER, TestUtils.USER_1_MAIL);
        USER_2 = jdbcTemplate.queryForObject(TestUtils.USER_SELECT_BY_EMAIL, TestUtils.USER_ROW_MAPPER, TestUtils.USER_2_MAIL);
        USER_3 = jdbcTemplate.queryForObject(TestUtils.USER_SELECT_BY_EMAIL, TestUtils.USER_ROW_MAPPER, TestUtils.USER_3_MAIL);
        USER_4 = jdbcTemplate.queryForObject(TestUtils.USER_SELECT_BY_EMAIL, TestUtils.USER_ROW_MAPPER, TestUtils.USER_4_MAIL);
        USER_I1 = jdbcTemplate.queryForObject(TestUtils.USER_SELECT_BY_EMAIL, TestUtils.USER_ROW_MAPPER, TestUtils.USER_COMMON_INTERESTS_1_MAIL);
        USER_I2 = jdbcTemplate.queryForObject(TestUtils.USER_SELECT_BY_EMAIL, TestUtils.USER_ROW_MAPPER, TestUtils.USER_COMMON_INTERESTS_2_MAIL);
        USER_I3 = jdbcTemplate.queryForObject(TestUtils.USER_SELECT_BY_EMAIL, TestUtils.USER_ROW_MAPPER, TestUtils.USER_COMMON_INTERESTS_3_MAIL);
        JOURNEY_1 = jdbcTemplate.queryForObject(TestUtils.JOURNEY_SELECT_BY_USERMAIL, TestUtils.JOURNEY_ROW_MAPPER, TestUtils.USER_1_MAIL);
    }

    @Test
    public void testCreateUser(){
        TestUtils.deleteUsers(jdbcTemplate);

        final User user = userDao.create(TestUtils.USER_1_MAIL, TestUtils.USER_1_NAME, TestUtils.USER_FIRSTNAME, TestUtils.USER_LASTNAME, UNIVERSITY_1, CAREER_1, PROFILEPIC_1.getId(), TestUtils.USER_PASSWORD, Locale.of(TestUtils.USER_LOCALE), TestUtils.USER_VALID_TOKEN_DEFAULT, TestUtils.USER_EXPIRATION_DEFAULT);

        assertEqualsUser(user);
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.USER_TABLE));
    }

    @Test(expected = DataAccessException.class)
    public void testCreateUserNoMail(){
        userDao.create(null, TestUtils.USER_NEW1_NAME, TestUtils.USER_FIRSTNAME, TestUtils.USER_LASTNAME, UNIVERSITY_1, CAREER_1, PROFILEPIC_1.getId(), TestUtils.USER_PASSWORD, Locale.of(TestUtils.USER_LOCALE), TestUtils.USER_VALID_TOKEN_DEFAULT, TestUtils.USER_EXPIRATION_DEFAULT);
    }
    @Test(expected = DataAccessException.class)
    public void testCreateUserNoUsername(){
        userDao.create(TestUtils.USER_NEW1_MAIL, null, TestUtils.USER_FIRSTNAME, TestUtils.USER_LASTNAME, UNIVERSITY_1, CAREER_1, PROFILEPIC_1.getId(), TestUtils.USER_PASSWORD, Locale.of(TestUtils.USER_LOCALE), TestUtils.USER_VALID_TOKEN_DEFAULT, TestUtils.USER_EXPIRATION_DEFAULT);
    }
    @Test(expected = DataAccessException.class)
    public void testCreateUserNoFirstName(){
        userDao.create(TestUtils.USER_NEW1_MAIL, TestUtils.USER_NEW1_NAME, null, TestUtils.USER_LASTNAME, UNIVERSITY_1, CAREER_1, PROFILEPIC_1.getId(), TestUtils.USER_PASSWORD, Locale.of(TestUtils.USER_LOCALE), TestUtils.USER_VALID_TOKEN_DEFAULT, TestUtils.USER_EXPIRATION_DEFAULT);
    }
    @Test(expected = DataAccessException.class)
    public void testCreateUserNoLastName(){
        userDao.create(TestUtils.USER_NEW1_MAIL, TestUtils.USER_NEW1_NAME, TestUtils.USER_FIRSTNAME, null, UNIVERSITY_1, CAREER_1, PROFILEPIC_1.getId(), TestUtils.USER_PASSWORD, Locale.of(TestUtils.USER_LOCALE), TestUtils.USER_VALID_TOKEN_DEFAULT, TestUtils.USER_EXPIRATION_DEFAULT);
    }
    @Test(expected = NullPointerException.class)
    public void testCreateUserNoUniversity(){
        userDao.create(TestUtils.USER_NEW1_MAIL, TestUtils.USER_NEW1_NAME, TestUtils.USER_FIRSTNAME, TestUtils.USER_LASTNAME, null, CAREER_1, PROFILEPIC_1.getId(), TestUtils.USER_PASSWORD, Locale.of(TestUtils.USER_LOCALE), TestUtils.USER_VALID_TOKEN_DEFAULT, TestUtils.USER_EXPIRATION_DEFAULT);
    }
    @Test(expected = DataAccessException.class)
    public void testCreateUserInvalidUniversity(){
        userDao.create(TestUtils.USER_NEW1_MAIL, TestUtils.USER_NEW1_NAME, TestUtils.USER_FIRSTNAME, TestUtils.USER_LASTNAME, new University(1034234123, null, null, null), CAREER_1, PROFILEPIC_1.getId(), TestUtils.USER_PASSWORD, Locale.of(TestUtils.USER_LOCALE), TestUtils.USER_VALID_TOKEN_DEFAULT, TestUtils.USER_EXPIRATION_DEFAULT);
    }
    @Test(expected = NullPointerException.class)
    public void testCreateUserNoCareer(){
        userDao.create(TestUtils.USER_NEW1_MAIL, TestUtils.USER_NEW1_NAME, TestUtils.USER_FIRSTNAME, TestUtils.USER_LASTNAME, UNIVERSITY_1, null, PROFILEPIC_1.getId(), TestUtils.USER_PASSWORD, Locale.of(TestUtils.USER_LOCALE), TestUtils.USER_VALID_TOKEN_DEFAULT, TestUtils.USER_EXPIRATION_DEFAULT);
    }
    @Test(expected = DataAccessException.class)
    public void testCreateUserInvalidCareer(){
        userDao.create(TestUtils.USER_NEW1_MAIL, TestUtils.USER_NEW1_NAME, TestUtils.USER_FIRSTNAME, TestUtils.USER_LASTNAME, UNIVERSITY_1, new Career((long)1313423,null), PROFILEPIC_1.getId(), TestUtils.USER_PASSWORD, Locale.of(TestUtils.USER_LOCALE), TestUtils.USER_VALID_TOKEN_DEFAULT, TestUtils.USER_EXPIRATION_DEFAULT);
    }
    @Test(expected = DataAccessException.class)
    public void testCreateUserInvalidPic(){
        userDao.create(TestUtils.USER_NEW1_MAIL, TestUtils.USER_NEW1_NAME, TestUtils.USER_FIRSTNAME, TestUtils.USER_LASTNAME, UNIVERSITY_1, CAREER_1, 123123123, TestUtils.USER_PASSWORD, Locale.of(TestUtils.USER_LOCALE), TestUtils.USER_VALID_TOKEN_DEFAULT, TestUtils.USER_EXPIRATION_DEFAULT);
    }
    @Test(expected = DataAccessException.class)
    public void testCreateUserNoPassword(){
        userDao.create(TestUtils.USER_NEW1_MAIL, TestUtils.USER_NEW1_NAME, TestUtils.USER_FIRSTNAME, TestUtils.USER_LASTNAME, UNIVERSITY_1, CAREER_1, PROFILEPIC_1.getId(), null, Locale.of(TestUtils.USER_LOCALE), TestUtils.USER_VALID_TOKEN_DEFAULT, TestUtils.USER_EXPIRATION_DEFAULT);
    }
    @Test(expected = NullPointerException.class)
    public void testCreateUserNoLocale(){
        userDao.create(TestUtils.USER_NEW1_MAIL, TestUtils.USER_NEW1_NAME, TestUtils.USER_FIRSTNAME, TestUtils.USER_LASTNAME, UNIVERSITY_1, CAREER_1, PROFILEPIC_1.getId(), TestUtils.USER_PASSWORD, null, TestUtils.USER_VALID_TOKEN_DEFAULT, TestUtils.USER_EXPIRATION_DEFAULT);
    }
    @Test(expected = DataAccessException.class)
    public void testCreateUserInvalidLocale(){
        userDao.create(TestUtils.USER_NEW1_MAIL, TestUtils.USER_NEW1_NAME, TestUtils.USER_FIRSTNAME, TestUtils.USER_LASTNAME, UNIVERSITY_1, CAREER_1, PROFILEPIC_1.getId(), TestUtils.USER_PASSWORD, Locale.of(TestUtils.USER_WRONG_LOCALE), TestUtils.USER_VALID_TOKEN_DEFAULT, TestUtils.USER_EXPIRATION_DEFAULT);
    }
    @Test(expected = DataAccessException.class)
    public void testCreateUserMissingToken(){
        userDao.create(TestUtils.USER_NEW1_MAIL, TestUtils.USER_NEW1_NAME, TestUtils.USER_FIRSTNAME, TestUtils.USER_LASTNAME, UNIVERSITY_1, CAREER_1, PROFILEPIC_1.getId(), TestUtils.USER_PASSWORD, Locale.of(TestUtils.USER_WRONG_LOCALE), null, TestUtils.USER_EXPIRATION_DEFAULT);
    }
    @Test(expected = NullPointerException.class)
    public void testCreateUserMissingExpiration(){
        userDao.create(TestUtils.USER_NEW1_MAIL, TestUtils.USER_NEW1_NAME, TestUtils.USER_FIRSTNAME, TestUtils.USER_LASTNAME, UNIVERSITY_1, CAREER_1, PROFILEPIC_1.getId(), TestUtils.USER_PASSWORD, Locale.of(TestUtils.USER_WRONG_LOCALE), TestUtils.USER_VALID_TOKEN_DEFAULT, null);
    }

    @Test
    public void testFindUserById(){
        Optional<User> maybeUser = userDao.findById(USER_1.getId());

        assertEqualsMaybeUser(maybeUser);
    }
    @Test
    public void testFindUserByIdMissing(){
        final Optional<User> maybeUser = userDao.findById(12341234);

        assertNotNull(maybeUser);
        assertFalse(maybeUser.isPresent());
    }
    @Test
    public void testFindUserByEmail(){
        final Optional<User> maybeUser = userDao.findByEmail(TestUtils.USER_1_MAIL);

        assertEqualsMaybeUser(maybeUser);
    }
    @Test
    public void testFindUserByEmailMissing(){
        final Optional<User> maybeUser = userDao.findByEmail(TestUtils.USER_FAKE_MAIL);

        assertNotNull(maybeUser);
        assertFalse(maybeUser.isPresent());
    }
    @Test
    public void testFindUserByEmailWithPassword(){
        final Optional<UserAuthInfo> maybeUser = userDao.findByEmailWithPass(TestUtils.USER_1_MAIL);

        assertNotNull(maybeUser);
        assertTrue(maybeUser.isPresent());
        final UserAuthInfo user = maybeUser.get();
        assertEqualsUserPassword(user);
    }
    @Test
    public void testFindUserByEmailWithPasswordMissing(){
        final Optional<UserAuthInfo> maybeUser = userDao.findByEmailWithPass(TestUtils.USER_FAKE_MAIL);
        assertNotNull(maybeUser);
        assertFalse(maybeUser.isPresent());
    }

    @Test
    public void testChangePassword(){
        userDao.changePassword(TestUtils.USER_1_MAIL, TestUtils.USER_FAKE_PASSWORD);

        assertEqualsMaybeUser(
            jdbcTemplate.query(TestUtils.USER_SELECT_BY_ID, TestUtils.USER_ROW_MAPPER, USER_1.getId()).stream().findFirst(), 
            Map.of("password", TestUtils.USER_FAKE_PASSWORD)
        );
    }
    @Test
    public void testChangePasswordMissingUser(){
        userDao.changePassword(TestUtils.USER_FAKE_MAIL, TestUtils.USER_PASSWORD);
    }
    @Test(expected = DataAccessException.class)
    public void testChangePasswordMissingPassword(){
        userDao.changePassword(TestUtils.USER_1_MAIL, null);

        assertEqualsMaybeUser(jdbcTemplate.query(TestUtils.USER_SELECT_BY_ID, TestUtils.USER_ROW_MAPPER, USER_1.getId()).stream().findFirst());
    }
    @Test
    public void testChangePasswordEmptyPassword(){
        userDao.changePassword(TestUtils.USER_1_MAIL, "");

        assertEqualsMaybeUser(jdbcTemplate.query(TestUtils.USER_SELECT_BY_ID, TestUtils.USER_ROW_MAPPER, USER_1.getId()).stream().findFirst());
    }

    @Test
    public void testExistsByUsernameDoesExist(){
        final boolean exists = userDao.existsByUsername(TestUtils.USER_1_NAME);

        assertTrue(exists);
    }
    @Test
    public void testExistsByUsernameDoesNotExist(){
        final boolean exists = userDao.existsByUsername(TestUtils.USER_FAKE_NAME);

        assertFalse(exists);
    }
    @Test
    public void testExistsByEmailDoesExist(){
        final boolean exists = userDao.existsByEmail(TestUtils.USER_1_MAIL);

        assertTrue(exists);
    }
    @Test
    public void testExistsByEmailDoesNotExist(){
        final boolean exists = userDao.existsByEmail(TestUtils.USER_FAKE_MAIL);

        assertFalse(exists);
    }

    @Test
    public void testUpdateProfileInfo(){
        userDao.updateProfileInfo(USER_1.getId(), TestUtils.USER_FAKE_FIRSTNAME, TestUtils.USER_FAKE_LASTNAME, TestUtils.USER_FAKE_NAME);

        assertEqualsMaybeUser(
            jdbcTemplate.query(TestUtils.USER_SELECT_BY_ID, TestUtils.USER_ROW_MAPPER, USER_1.getId()).stream().findFirst(),
            Map.of("username", TestUtils.USER_FAKE_NAME, "firstname", TestUtils.USER_FAKE_FIRSTNAME, "lastname", TestUtils.USER_FAKE_LASTNAME)
        );
    }
    @Test
    public void testUpdateProfileInfoUsername(){
        userDao.updateProfileInfo(USER_1.getId(), null, null, TestUtils.USER_FAKE_NAME);

        assertEqualsMaybeUser(
            jdbcTemplate.query(TestUtils.USER_SELECT_BY_ID, TestUtils.USER_ROW_MAPPER, USER_1.getId()).stream().findFirst(),
            Map.of("username", TestUtils.USER_FAKE_NAME)
        );
    }
    @Test
    public void testUpdateProfileInfoFirstName(){
        userDao.updateProfileInfo(USER_1.getId(), TestUtils.USER_FAKE_FIRSTNAME, null, null);

        assertEqualsMaybeUser(
            jdbcTemplate.query(TestUtils.USER_SELECT_BY_ID, TestUtils.USER_ROW_MAPPER, USER_1.getId()).stream().findFirst(),
            Map.of("firstname", TestUtils.USER_FAKE_FIRSTNAME)
        );
    }
    @Test
    public void testUpdateProfileInfoLastName(){
        userDao.updateProfileInfo(USER_1.getId(), null, TestUtils.USER_FAKE_LASTNAME, null);

        assertEqualsMaybeUser(
            jdbcTemplate.query(TestUtils.USER_SELECT_BY_ID, TestUtils.USER_ROW_MAPPER, USER_1.getId()).stream().findFirst(),
            Map.of("lastname", TestUtils.USER_FAKE_LASTNAME)
        );
    }
    @Test(expected = DataAccessException.class)
    public void testUpdateProfileInfoDuplicatedUsername(){
        final long id = insertUser(Map.of("username", TestUtils.USER_FAKE_NAME));

        userDao.updateProfileInfo(id, TestUtils.USER_FIRSTNAME, TestUtils.USER_LASTNAME, TestUtils.USER_1_NAME);
    }
    @Test
    public void testUpdateProfileInfoNoParams(){
        userDao.updateProfileInfo(USER_1.getId(), null, null, null);

        assertEqualsMaybeUser(jdbcTemplate.query(TestUtils.USER_SELECT_BY_ID, TestUtils.USER_ROW_MAPPER, USER_1.getId()).stream().findFirst()); 
    }
    @Test
    public void testUpdateProfileInfoMissingUser(){
        userDao.updateProfileInfo(123123, TestUtils.USER_FIRSTNAME, TestUtils.USER_LASTNAME, TestUtils.USER_1_NAME);  

        assertUserDBDefaultStatus();
    }

    @Test
    public void testUpdateLocale(){
        userDao.updateLocale(USER_1.getId(), Locale.of(TestUtils.USER_FAKE_LOCALE));

        assertEqualsMaybeUser(
            jdbcTemplate.query(TestUtils.USER_SELECT_BY_ID, TestUtils.USER_ROW_MAPPER, USER_1.getId()).stream().findFirst(),
            Map.of("locale", TestUtils.USER_FAKE_LOCALE)
        ); 
    }
    @Test(expected = DataAccessException.class)
    public void testUpdateLocaleWrongLocale(){
        userDao.updateLocale(USER_1.getId(), Locale.of(TestUtils.USER_WRONG_LOCALE));  
    }
    @Test
    public void testUpdateLocaleMissingUser(){
        userDao.updateLocale(1321423, Locale.of(TestUtils.USER_LOCALE));  

        assertUserDBDefaultStatus();  
    }

    @Test
    public void testUpdateProfilePicture(){        
        userDao.updateProfilePicture(USER_1.getId(), PROFILEPIC_2.getId());
        
        assertEqualsMaybeUser(
            jdbcTemplate.query(TestUtils.USER_SELECT_BY_ID, TestUtils.USER_ROW_MAPPER, USER_1.getId()).stream().findFirst(),
            Map.of("profilepic", PROFILEPIC_2)
        );
    }
    @Test(expected = DataAccessException.class)
    public void testUpdateProfilePictureWrongPicture(){
        userDao.updateProfilePicture(USER_1.getId(), 123411234);
    }
    @Test
    public void testUpdateProfilePictureWrongUser(){
        userDao.updateProfilePicture(1234, PROFILEPIC_2.getId());

        assertUserDBDefaultStatus();
    }



    @Test
    public void testUpdateGeneric(){
        userDao.update(USER_1.getId(), TestUtils.USER_FAKE_FIRSTNAME, TestUtils.USER_FAKE_LASTNAME, TestUtils.USER_FAKE_NAME, UNIVERSITY_2.getId(), CAREER_2.getId(), Locale.of(TestUtils.USER_FAKE_LOCALE));
        
        assertEqualsMaybeUser(
            jdbcTemplate.query(TestUtils.USER_SELECT_BY_ID, TestUtils.USER_ROW_MAPPER, USER_1.getId()).stream().findFirst(),
            Map.of(
                "firstname", TestUtils.USER_FAKE_FIRSTNAME, 
                "lastname", TestUtils.USER_FAKE_LASTNAME, 
                "username", TestUtils.USER_FAKE_NAME, 
                "university", UNIVERSITY_2, 
                "career", CAREER_2, 
                "locale", TestUtils.USER_FAKE_LOCALE
            )
        );
    }
    @Test
    public void testUpdateFirstname(){
        userDao.update(USER_1.getId(), TestUtils.USER_FIRSTNAME, null, null, null, null, null);
        
        assertEqualsMaybeUser(jdbcTemplate.query(TestUtils.USER_SELECT_BY_ID, TestUtils.USER_ROW_MAPPER, USER_1.getId()).stream().findFirst());
    }
    @Test
    public void testUpdateLastname(){
        userDao.update(USER_1.getId(), null, TestUtils.USER_LASTNAME, null, null, null, null);
        
        assertEqualsMaybeUser(jdbcTemplate.query(TestUtils.USER_SELECT_BY_ID, TestUtils.USER_ROW_MAPPER, USER_1.getId()).stream().findFirst());
    }
    @Test
    public void testUpdateUsername(){
        userDao.update(USER_1.getId(), null, null, TestUtils.USER_1_NAME, null, null, null);
        
        assertEqualsMaybeUser(jdbcTemplate.query(TestUtils.USER_SELECT_BY_ID, TestUtils.USER_ROW_MAPPER, USER_1.getId()).stream().findFirst());
    }
    @Test
    public void testUpdateGenericUniversity(){
        userDao.update(USER_1.getId(), null, null, null, UNIVERSITY_1.getId(), null, null);
        
        assertEqualsMaybeUser(jdbcTemplate.query(TestUtils.USER_SELECT_BY_ID, TestUtils.USER_ROW_MAPPER, USER_1.getId()).stream().findFirst());
    }
    @Test
    public void testUpdateCareerGeneric(){
        userDao.update(USER_1.getId(), null, null, null, null, CAREER_1.getId(), null);
        
        assertEqualsMaybeUser(jdbcTemplate.query(TestUtils.USER_SELECT_BY_ID, TestUtils.USER_ROW_MAPPER, USER_1.getId()).stream().findFirst());
    }
    @Test
    public void testUpdateLocaleGeneric(){
        userDao.update(USER_1.getId(), null, null, null, null, null, Locale.of(TestUtils.USER_LOCALE));
        
        assertEqualsMaybeUser(jdbcTemplate.query(TestUtils.USER_SELECT_BY_ID, TestUtils.USER_ROW_MAPPER, USER_1.getId()).stream().findFirst());
    }
    @Test
    public void testUpdateWrongUser(){
        userDao.update(12341234, TestUtils.USER_FIRSTNAME, TestUtils.USER_LASTNAME, TestUtils.USER_1_NAME, UNIVERSITY_1.getId(), CAREER_1.getId(), Locale.of(TestUtils.USER_LOCALE));
    }
    @Test
    public void testUpdateNoArguments(){
        userDao.update(USER_1.getId(), null, null, null, null, null, null);
        
        assertEqualsMaybeUser(jdbcTemplate.query(TestUtils.USER_SELECT_BY_ID, TestUtils.USER_ROW_MAPPER, USER_1.getId()).stream().findFirst());    
    }

    @Test
    public void testGetAllUsers(){
        List<User> users = userDao.getAllUsers();

        assertNotNull(users);
        assertEquals(TestUtils.TOTAL_USERS, users.size());
        //TODO loop
        TestUtils.assertEqualsUser(USER_1, users.get(0));
        TestUtils.assertEqualsUser(USER_2, users.get(1));
        TestUtils.assertEqualsUser(USER_3, users.get(2));
        TestUtils.assertEqualsUser(USER_4, users.get(3));
        TestUtils.assertEqualsUser(USER_I1, users.get(4));
        TestUtils.assertEqualsUser(USER_I2, users.get(5));
        TestUtils.assertEqualsUser(USER_I3, users.get(6));
    }
    @Test 
    public void testGetAllUsersNoUsers(){
        JdbcTestUtils.deleteFromTables(jdbcTemplate, TestUtils.JOURNEY_TABLE, TestUtils.USER_INTEREST_TABLE, TestUtils.USER_TABLE);
        List<User> users = userDao.getAllUsers();

        assertNotNull(users);
        assertEquals(0, users.size());
    }

    @Test
    public void testGetAllUsersPaged(){
        Page<User> page1 = userDao.getAllUsers(TestUtils.PAGE_1_DEFAULT);
        Page<User> page2 = userDao.getAllUsers(TestUtils.PAGE_2_DEFAULT);

        assertNotNull(page1);
        assertNotNull(page2);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(2, page2.getCurrentPage());
        assertEquals(4, page1.getTotalPages());
        assertEquals(4, page2.getTotalPages());
        assertNotNull(page1.getContent());
        assertNotNull(page2.getContent());
        assertEquals(2, page1.getContent().size());
        assertEquals(2, page2.getContent().size());
        assertEqualsUser(page1.getContent().getFirst());
        assertEqualsUser(page1.getContent().getLast(), TestUtils.USER_2_PARAMS);
        assertEqualsUser(page2.getContent().getFirst(), TestUtils.USER_3_PARAMS);
    }
    @Test
    public void testGetAllUsersPagedWrongPage(){
        Page<User> page1 = userDao.getAllUsers(TestUtils.PAGE_1_BIG);
        Page<User> page2 = userDao.getAllUsers(TestUtils.PAGE_2_BIG);

        assertNotNull(page1);
        assertNotNull(page2);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(2, page2.getCurrentPage());
        assertEquals(1, page1.getTotalPages());
        assertEquals(1, page2.getTotalPages());
        assertNotNull(page1.getContent());
        assertNotNull(page2.getContent());
        assertEquals(TestUtils.TOTAL_USERS, page1.getContent().size());
        assertEquals(0, page2.getContent().size());
        assertEqualsUser(page1.getContent().get(0));
        assertEqualsUser(page1.getContent().get(1), TestUtils.USER_2_PARAMS);
        assertEqualsUser(page1.getContent().get(2), TestUtils.USER_3_PARAMS);
    }

    @Test
    public void testSearchUsersPaged(){
        Page<User> page1 = userDao.searchUsers(TestUtils.USER_FIRSTNAME, TestUtils.PAGE_1_DEFAULT);
        Page<User> page2 = userDao.searchUsers(TestUtils.USER_FIRSTNAME, TestUtils.PAGE_2_DEFAULT);

        assertNotNull(page1);
        assertNotNull(page2);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(4, page1.getTotalPages());
        assertEquals(2, page2.getCurrentPage());
        assertEquals(4, page2.getTotalPages());
        assertNotNull(page1.getContent());
        assertNotNull(page2.getContent());
        assertEquals(TestUtils.PAGE_SIZE_DEFAULT, page1.getContent().size());
        assertEquals(2, page2.getContent().size());
        TestUtils.assertEqualsUser(USER_I3, page1.getContent().getFirst());
        TestUtils.assertEqualsUser(USER_I2, page1.getContent().getLast());
        TestUtils.assertEqualsUser(USER_I1, page2.getContent().getFirst());
    }
    @Test
    public void testSearchUsersPaged2(){
        Page<User> page1 = userDao.searchUsers(TestUtils.USER_1_NAME, TestUtils.PAGE_1_DEFAULT);
        Page<User> page2 = userDao.searchUsers(TestUtils.USER_1_NAME, TestUtils.PAGE_2_DEFAULT);

        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(2, page2.getCurrentPage());
        assertEquals(1, page1.getTotalPages());
        assertEquals(1, page2.getTotalPages());
        assertNotNull(page1.getContent());
        assertNotNull(page2.getContent());
        assertEquals(1, page1.getContent().size());
        assertEquals(0, page2.getContent().size());
        assertEqualsUser(page1.getContent().getFirst());
    }

   @Test
   public void testListJourneyRespondersMinusUsers(){
        //TODO replace reply insert
        SimpleJdbcInsert journeyReplyInsert = new SimpleJdbcInsert(ds).withTableName(TestUtils.JOURNEY_REPLY_TABLE).usingGeneratedKeyColumns("id");
        journeyReplyInsert.execute(Map.of("user_id", USER_2.getId(), "journey_id", JOURNEY_1.getId(), "message", TestUtils.MESSAGE_DEFAULT, "date_time", Timestamp.valueOf(LocalDateTime.now()), "deleted", false, "deleted_message", ""));
        journeyReplyInsert.execute(Map.of("user_id", USER_3.getId(), "journey_id", JOURNEY_1.getId(), "message", TestUtils.MESSAGE_DEFAULT, "date_time", Timestamp.valueOf(LocalDateTime.now()), "deleted", false, "deleted_message", ""));

        List<User> repliesUser = userDao.listJourneyRespondersMinusUsers(JOURNEY_1.getId());

        assertNotNull(repliesUser);
        assertEquals(2, repliesUser.size());
        Map<Long, User> userData = Map.of(USER_2.getId(), USER_2, USER_3.getId(), USER_3);
        for (User user : repliesUser) {
            TestUtils.assertEqualsUser(userData.get(user.getId()), user);
        }
   }
    @Test
    public void testListEventRespondersMinusUsers(){
        //TODO replace event insert
        long eventId = new SimpleJdbcInsert(ds).withTableName(TestUtils.EVENT_TABLE).usingGeneratedKeyColumns("id")
            .executeAndReturnKey(Map.of(
                "user_id", USER_1.getId(), 
                "city_id", jdbcTemplate.queryForObject("SELECT id FROM cities LIMIT 1", Long.class), 
                "event_date", LocalDate.now().plusDays(10).toString(),
                "title", "title",
                "description", "event",
                "deleted", false))
            .longValue();
        SimpleJdbcInsert eventReplyInsert = new SimpleJdbcInsert(ds).withTableName(TestUtils.EVENT_REPLY_TABLE).usingGeneratedKeyColumns("id");
        eventReplyInsert.execute(Map.of("user_id", USER_2.getId(), "event_id", eventId, "message", TestUtils.MESSAGE_DEFAULT, "date_time", Timestamp.valueOf(LocalDateTime.now()), "deleted", false, "deleted_message", ""));
        eventReplyInsert.execute(Map.of("user_id", USER_3.getId(), "event_id", eventId, "message", TestUtils.MESSAGE_DEFAULT, "date_time", Timestamp.valueOf(LocalDateTime.now()), "deleted", false, "deleted_message", ""));

        List<User> repliesUser = userDao.listEventRespondersMinusUsers(eventId);

        assertNotNull(repliesUser);
        assertEquals(2, repliesUser.size());
        Map<Long, User> userData = Map.of(USER_2.getId(), USER_2, USER_3.getId(), USER_3);
        for (User user : repliesUser) {
            TestUtils.assertEqualsUser(userData.get(user.getId()), user);
        }
    }

    @Test
    public void testBlockUser(){
        userDao.blockUser(USER_1.getId());

        Optional<User> maybeUser = jdbcTemplate.query(TestUtils.USER_SELECT_BY_ID, TestUtils.USER_ROW_MAPPER, USER_1.getId()).stream().findFirst();
        assertEqualsMaybeUser(maybeUser, Map.of("blocked", true));
    }
    @Test
    public void testBlockUserBlocked(){
        Map<String, Object> params = Map.of("email", TestUtils.USER_NEW1_MAIL, "username", TestUtils.USER_NEW1_NAME, "blocked", true);
        long id = insertUser(params);

        userDao.blockUser(id);

        Optional<User> maybeUser = jdbcTemplate.query(TestUtils.USER_SELECT_BY_ID, TestUtils.USER_ROW_MAPPER, id).stream().findFirst();
        assertEqualsMaybeUser(maybeUser, params);
    }
    @Test
    public void testBlockUserWrongId(){
        userDao.blockUser(12341234);

        assertUserDBDefaultStatus();
    }

    @Test
    public void testUnblockUser(){
        long id = insertUser(Map.of("email", TestUtils.USER_NEW1_MAIL, "username", TestUtils.USER_NEW1_NAME, "blocked", true));

        userDao.unblockUser(id);

        Optional<User> maybeUser = jdbcTemplate.query(TestUtils.USER_SELECT_BY_ID, TestUtils.USER_ROW_MAPPER, id).stream().findFirst();
        assertEqualsMaybeUser(maybeUser, Map.of("email", TestUtils.USER_NEW1_MAIL, "username", TestUtils.USER_NEW1_NAME));
    }
    @Test
    public void testUnblockUserUnblocked(){
        userDao.unblockUser(USER_1.getId());
        
        Optional<User> maybeUser = jdbcTemplate.query(TestUtils.USER_SELECT_BY_ID, TestUtils.USER_ROW_MAPPER, USER_1.getId()).stream().findFirst();
        assertNotNull(maybeUser);
        assertTrue(maybeUser.isPresent());
        User user = maybeUser.get();
        assertEqualsUser(user);
    }
    @Test
    public void testUnblockUserWrongId(){
        userDao.unblockUser(12341234);
        
        assertUserDBDefaultStatus();
    }

    @Test
    public void testIsValid(){
        insertUser(Map.of("username", TestUtils.USER_NEW1_NAME, "email", TestUtils.USER_NEW1_MAIL));

        boolean isValid = userDao.isValid(TestUtils.USER_VALID_TOKEN_DEFAULT);

        assertTrue(isValid);
    }
    @Test
    public void testIsValidNotInUse(){
        boolean isValid = userDao.isValid(TestUtils.USER_VALID_TOKEN_DEFAULT);

        assertFalse(isValid);
    }
    @Test
    public void testIsValidExpired(){
        insertUser(Map.of("username", TestUtils.USER_NEW1_NAME, "email", TestUtils.USER_NEW1_MAIL, "tokenExpiration", LocalDate.now().plusDays(-1)));

        boolean isValid = userDao.isValid(TestUtils.USER_VALID_TOKEN_DEFAULT);

        assertFalse(isValid);
    }

    @Test
    public void testValidateToken(){
        long id = insertUser(Map.of("username", TestUtils.USER_NEW1_NAME, "email", TestUtils.USER_NEW1_MAIL, "tokenExpiration", LocalDate.now().plusDays(-1)));

        userDao.validateToken(TestUtils.USER_VALID_TOKEN_DEFAULT);

        assertTrue(jdbcTemplate.queryForObject("SELECT validate_token FROM users WHERE id = ?", String.class, id) == null);
    }
    @Test
    public void testValidateTokenWrongToken(){
        long id = insertUser(Map.of("username", TestUtils.USER_NEW1_NAME, "email", TestUtils.USER_NEW1_MAIL, "tokenExpiration", LocalDate.now().plusDays(-1)));

        userDao.validateToken("USER_VALID_TOKEN_DEFAULT");

        assertFalse(jdbcTemplate.queryForObject("SELECT validate_token FROM users WHERE id = ?", String.class, id) == null);
    }

    @Test
    public void testHasExpired(){
        insertUser(Map.of("username", TestUtils.USER_NEW1_NAME, "email", TestUtils.USER_NEW1_MAIL));

        boolean isExpired = userDao.hasExpired(TestUtils.USER_VALID_TOKEN_DEFAULT);

        assertFalse(isExpired);
    }
    @Test
    public void testHasExpiredExpired(){
        insertUser(Map.of("username", TestUtils.USER_NEW1_NAME, "email", TestUtils.USER_NEW1_MAIL, "tokenExpiration", LocalDate.now().plusDays(-1)));

        boolean isExpired = userDao.hasExpired(TestUtils.USER_VALID_TOKEN_DEFAULT);

        assertTrue(isExpired);
    }
    @Test
    public void testHasExpiredMissing(){
        boolean isExpired = userDao.hasExpired(TestUtils.USER_VALID_TOKEN_DEFAULT);

        assertFalse(isExpired);
    }
    @Test
    public void testHasExpiredEmpty(){
        insertUser(Map.of("username", TestUtils.USER_NEW1_NAME, "email", TestUtils.USER_NEW1_MAIL, "tokenExpiration", LocalDate.now().plusDays(-1)));

        boolean isExpired = userDao.hasExpired("");

        assertFalse(isExpired);
    }
}


//
//    @Test
//    public void testUpdateCareerName(){
//        userDao.updateCareer(USER_1.getId(), CAREER_2.getName());
//
//        assertEqualsMaybeUser(
//            jdbcTemplate.query(TestUtils.USER_SELECT_BY_ID, TestUtils.USER_ROW_MAPPER, USER_1.getId()).stream().findFirst(),
//            Map.of("career", CAREER_2)
//        );
//    }
//    @Test(expected = DataAccessException.class)
//    public void testUpdateCareerWrongCareerName(){
//        userDao.updateCareer(USER_1.getId(), "12341234");
//    }
//    @Test
//    public void testUpdateCareerWrongUserName(){
//        userDao.updateCareer(13241234, "12341234");
//
//        assertUserDBDefaultStatus();
//    }


//
//    @Test
//    public void testUpdateCareer(){
//        userDao.updateCareer(USER_1.getId(), CAREER_2.getId());
//
//        assertEqualsMaybeUser(
//            jdbcTemplate.query(TestUtils.USER_SELECT_BY_ID, TestUtils.USER_ROW_MAPPER, USER_1.getId()).stream().findFirst(),
//            Map.of("career", CAREER_2)
//        );
//    }
//    @Test(expected = DataAccessException.class)
//    public void testUpdateCareerWrongCareer(){
//        userDao.updateCareer(USER_1.getId(), 12341234);
//    }
//    @Test
//    public void testUpdateCareerWrongUser(){
//        userDao.updateCareer(13241234, 12341234);
//
//        assertUserDBDefaultStatus();
//    }

/*
@Test
public void testUpdateUniversityName(){
    userDao.updateUniversity(USER_1.getId(), UNIVERSITY_2.getName());

    assertEqualsMaybeUser(
            jdbcTemplate.query(TestUtils.USER_SELECT_BY_ID, TestUtils.USER_ROW_MAPPER, USER_1.getId()).stream().findFirst(),
            Map.of("university", UNIVERSITY_2)
    );
}
@Test(expected = DataAccessException.class)
public void testUpdateUniversityNameWrongUniversity(){
    userDao.updateUniversity(USER_1.getId(), "12341234");
}
@Test
public void testUpdateUniversityNameWrongUser(){
    userDao.updateUniversity(13241234, "12341234");

    assertUserDBDefaultStatus();
}
*/

/*


    @Test
    public void testUpdateUniversity(){
        userDao.updateUniversity(USER_1.getId(), UNIVERSITY_2.getId());

        assertEqualsMaybeUser(
            jdbcTemplate.query(TestUtils.USER_SELECT_BY_ID, TestUtils.USER_ROW_MAPPER, USER_1.getId()).stream().findFirst(),
            Map.of("university", UNIVERSITY_2)
        );
    }
    @Test(expected = DataAccessException.class)
    public void testUpdateUniversityWrongUniversity(){
        userDao.updateUniversity(USER_1.getId(), 12341234);
    }
    @Test
    public void testUpdateUniversityWrongUser(){
        userDao.updateUniversity(13241234, 12341234);

        assertUserDBDefaultStatus();
    }
*/