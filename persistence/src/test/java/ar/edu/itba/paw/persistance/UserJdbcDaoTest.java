package ar.edu.itba.paw.persistance;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
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
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.itba.paw.models.Career;
import ar.edu.itba.paw.models.Page;
import ar.edu.itba.paw.models.University;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.UserPassword;
import ar.edu.itba.paw.persistence.UserJdbcDao;

@Transactional
@Rollback
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class UserJdbcDaoTest {

    private static final String USER_TABLE = "users";
    private static final String IMAGES_TABLE = "images";
    private static final String UNIVERSITIES_TABLE = "universities";
    private static final String CAREERS_TABLE = "careers";
    private static final String JOURNEY_TABLE = "journeys";
    private static final String EVENT_TABLE = "events";
    private static final String JOURNEY_REPLY_TABLE = "journey_responses";
    private static final String EVENT_REPLY_TABLE = "event_responses";
    private static final String USERNAME = "newUser";
    private static final String USERNAME2 = "newUser2";
    private static final String USERNAME3 = "newUser3";
    private static final String USERNAME4 = "newUser4";
    private static final String USERNAME5 = "newUser5";
    private static final String FIRSTNAME = "New";
    private static final String LASTNAME = "User";
    private static final String USERMAIL = "user@gmail.com";
    private static final String USERMAIL2 = "user2@gmail.com";
    private static final String USERMAIL3 = "user3@gmail.com";
    private static final String USERMAIL4 = "user4@gmail.com";
    private static final String USERMAIL5 = "user5@gmail.com";
    private static final String PASSWORD = "superSecret";
    private static final String LOCALE = "es";
    private static final String USERROLE = "user";
    private static University UNIVERSITY;
    private static Career CAREER;
    private static long PROFILEPICID;

    private static final String FAKEEMAIL = "totallyRealEmail@legitEmailService.com";
    private static final String FAKEUSERNAME = "totallyLegitUser";
    private static final String FAKEPASSWORD = "wrongPassword";
    private static final String FAKEFIRSTNAME = "fake";
    private static final String FAKELASTNAME = "name";
    private static final String FAKELOCALE = "en";
    private static final String WRONGLOCALE = "jp";

    private static final int PAGESIZE = 2;
    
    @Autowired
    private DataSource ds;

    @Autowired
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

    private void assertEqualsUser(User user){
        assertEqualsUser(user, Map.of());
    }
    private void assertEqualsUser(User user, Map<String, Object> overrideParams){
        assertNotNull(user);
        assertEquals(overrideParams.getOrDefault("email", USERMAIL), user.getEmail());
        assertEquals(overrideParams.getOrDefault("username", USERNAME), user.getUsername());
        assertEquals(overrideParams.getOrDefault("firstname", FIRSTNAME), user.getFirstname());
        assertEquals(overrideParams.getOrDefault("lastname", LASTNAME), user.getLastname());
        assertEquals(Locale.of((String)overrideParams.getOrDefault("locale", LOCALE)), user.getLocale());
        assertEquals(overrideParams.getOrDefault("career", CAREER.getId()), user.getCareer().getId());
        assertEquals(overrideParams.getOrDefault("university", UNIVERSITY.getId()), user.getUniversity().getId());
        assertEquals(overrideParams.getOrDefault("profilepic", PROFILEPICID), user.getProfilePictureId());
    }

    private void assertEqualsMaybeUser(Optional<User> maybeUser){
        assertNotNull(maybeUser);
        assertTrue(maybeUser.isPresent());
        final User user = maybeUser.get();
        assertEqualsUser(user); 
    }

    private long insertUserGeneric(){
        return insertUserOverride(Map.of());
    }
    private long insertUserOverride(Map<String, Object> overrideParams){
        HashMap<String, Object> params = new HashMap<>();
        params.put("email", overrideParams.getOrDefault("email", USERMAIL));
        params.put("username", overrideParams.getOrDefault("username", USERNAME));
        params.put("firstname", overrideParams.getOrDefault("firstname", FIRSTNAME));
        params.put("lastname", overrideParams.getOrDefault("lastname", LASTNAME));
        params.put("password", overrideParams.getOrDefault("password", PASSWORD));
        params.put("language", overrideParams.getOrDefault("locale", LOCALE));
        params.put("university", overrideParams.getOrDefault("university", UNIVERSITY.getId()));
        params.put("career_id", overrideParams.getOrDefault("career", CAREER.getId()));
        params.put("profile_picture_id", overrideParams.getOrDefault("profilepic", PROFILEPICID));
        params.put("roles", overrideParams.getOrDefault("roles", USERROLE));
        return insert.executeAndReturnKey(params).longValue();
    }

    @Before
    public void setUp(){
        jdbcTemplate = new JdbcTemplate(ds);
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

    @Test
    public void testCreateUser(){
        JdbcTestUtils.deleteFromTables(jdbcTemplate, USER_TABLE);
        final User user = userDao.create(USERMAIL, USERNAME, FIRSTNAME, LASTNAME, UNIVERSITY, CAREER, PROFILEPICID, PASSWORD, Locale.of(LOCALE));

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
        final long id = insertUserGeneric();

        assertEqualsMaybeUser(jdbcTemplate.query("SELECT * FROM users WHERE id = ?", USER_ROW_MAPPER, id).stream().findFirst());
    }
    @Test
    public void testFindUserByIdMissing(){
        final Optional<User> maybeUser = userDao.findById(12341234);
        assertNotNull(maybeUser);
        assertFalse(maybeUser.isPresent());
    }
    @Test
    public void testFindUserByEmail(){
        insertUserGeneric();

        final Optional<User> maybeUser = userDao.findByEmail(USERMAIL);

        assertEqualsMaybeUser(maybeUser);
    }
    @Test
    public void testFindUserByEmailMissing(){
        final Optional<User> maybeUser = userDao.findByEmail(FAKEEMAIL);
        assertNotNull(maybeUser);
        assertFalse(maybeUser.isPresent());
    }
    @Test
    public void testFindUserByEmailWithPassword(){
        insertUserGeneric();

        final Optional<UserPassword> maybeUser = userDao.findByEmailWithPass(USERMAIL);

        assertNotNull(maybeUser);
        assertTrue(maybeUser.isPresent());
        final UserPassword user = maybeUser.get();
        assertEquals(PASSWORD, user.getPassword());
        // assertEqualsUser(user);
    }
    @Test
    public void testFindUserByEmailWithPasswordMissing(){
        final Optional<UserPassword> maybeUser = userDao.findByEmailWithPass(FAKEEMAIL);
        assertNotNull(maybeUser);
        assertFalse(maybeUser.isPresent());
    }
    @Test
    public void testFindUserByUsername(){
        insertUserGeneric();

        final Optional<User> maybeUser = userDao.findByUsername(USERNAME);
        assertEqualsMaybeUser(maybeUser);
    }
    @Test
    public void testFindUserByUsernameMissing(){
        final Optional<User> maybeUser = userDao.findByUsername(FAKEUSERNAME);
        assertFalse(maybeUser.isPresent());
    }

    @Test
    public void testChangePassword(){
        final long id = insertUserOverride(Map.of("password", FAKEPASSWORD));

        userDao.changePassword(USERMAIL, PASSWORD);

        assertEqualsMaybeUser(jdbcTemplate.query("SELECT * FROM users WHERE id = ?", USER_ROW_MAPPER, id).stream().findFirst());
    }
    @Test
    public void testChangePasswordMissingUser(){
        userDao.changePassword(USERMAIL, PASSWORD);
    }
    @Test(expected = DataAccessException.class)
    public void testChangePasswordMissingPassword(){
        final long id = insertUserOverride(Map.of("password", PASSWORD));

        userDao.changePassword(USERMAIL, null);

        assertEqualsMaybeUser(jdbcTemplate.query("SELECT * FROM users WHERE id = ?", USER_ROW_MAPPER, id).stream().findFirst());
    }
    @Test
    public void testChangePasswordEmptyPassword(){
        final long id = insertUserOverride(Map.of("password", PASSWORD));

        userDao.changePassword(USERMAIL, "");

        assertEqualsMaybeUser(jdbcTemplate.query("SELECT * FROM users WHERE id = ?", USER_ROW_MAPPER, id).stream().findFirst());
    }

    @Test
    public void testExistsByUsernameDoesExist(){
        insertUserGeneric();

        final boolean exists = userDao.existsByUsername(USERNAME);

        assertTrue(exists);
    }
    @Test
    public void testExistsByUsernameDoesNotExist(){
        final boolean exists = userDao.existsByUsername(FAKEUSERNAME);

        assertFalse(exists);
    }
    @Test
    public void testExistsByEmailDoesExist(){
        insertUserGeneric();

        final boolean exists = userDao.existsByEmail(USERMAIL);

        assertTrue(exists);
    }
    @Test
    public void testExistsByEmailDoesNotExist(){
        final boolean exists = userDao.existsByEmail(FAKEEMAIL);

        assertFalse(exists);
    }

    @Test
    public void testUpdateProfileInfo(){
        final long id = insertUserOverride(Map.of("username", FAKEUSERNAME, "firstname", FAKEFIRSTNAME, "lastname", FAKELASTNAME));

        userDao.updateProfileInfo(id, FIRSTNAME, LASTNAME, USERNAME);

        assertEqualsMaybeUser(jdbcTemplate.query("SELECT * FROM users WHERE id = ?", USER_ROW_MAPPER, id).stream().findFirst());
    }
    @Test
    public void testUpdateProfileInfoUsername(){
        final long id = insertUserOverride(Map.of("username", FAKEUSERNAME, "firstname", FIRSTNAME, "lastname", LASTNAME));

        userDao.updateProfileInfo(id, null, null, USERNAME);

        assertEqualsMaybeUser(jdbcTemplate.query("SELECT * FROM users WHERE id = ?", USER_ROW_MAPPER, id).stream().findFirst());
    }
    @Test
    public void testUpdateProfileInfoFirstName(){
        final long id = insertUserOverride(Map.of("username", USERNAME, "firstname", FAKEFIRSTNAME, "lastname", LASTNAME));

        userDao.updateProfileInfo(id, FIRSTNAME, null, null);

        assertEqualsMaybeUser(jdbcTemplate.query("SELECT * FROM users WHERE id = ?", USER_ROW_MAPPER, id).stream().findFirst());
    }
    @Test
    public void testUpdateProfileInfoLastName(){
        final long id = insertUserOverride(Map.of("username", USERNAME, "firstname", FIRSTNAME, "lastname", FAKELASTNAME));

        userDao.updateProfileInfo(id, null, LASTNAME, null);

        assertEqualsMaybeUser(jdbcTemplate.query("SELECT * FROM users WHERE id = ?", USER_ROW_MAPPER, id).stream().findFirst());
    }
    @Test(expected = DataAccessException.class)
    public void testUpdateProfileInfoDuplicatedUsername(){
        insertUserOverride(Map.of("email", FAKEEMAIL, "firstname", FAKEFIRSTNAME, "lastname", FAKELASTNAME));
        final long id = insertUserOverride(Map.of("username", FAKEUSERNAME));

        userDao.updateProfileInfo(id, FIRSTNAME, LASTNAME, USERNAME);
    }
    @Test
    public void testUpdateProfileInfoNoParams(){
        final long id = insertUserGeneric();

        userDao.updateProfileInfo(id, null, null, null);

        assertEqualsMaybeUser(jdbcTemplate.query("SELECT * FROM users WHERE id = ?", USER_ROW_MAPPER, id).stream().findFirst()); 
    }
    @Test
    public void testUpdateProfileInfoMissingUser(){
        userDao.updateProfileInfo(123123, FIRSTNAME, LASTNAME, USERNAME);  
        //No checks needed, if it tried to update the empty DB, we'd get an exception and fail automatically.
    }

    @Test
    public void testUpdateLocale(){
        final long id = insertUserOverride(Map.of("locale", FAKELOCALE));

        userDao.updateLocale(id, Locale.of(LOCALE));

        assertEqualsMaybeUser(jdbcTemplate.query("SELECT * FROM users WHERE id = ?", USER_ROW_MAPPER, id).stream().findFirst()); 
    }
    @Test(expected = DataAccessException.class)
    public void testUpdateLocaleWrongLocale(){
        final long id = insertUserGeneric();

        userDao.updateLocale(id, Locale.of(WRONGLOCALE));  
    }
    @Test
    public void testUpdateLocaleMissingUser(){
        userDao.updateLocale(1321423, Locale.of(LOCALE));  
    }

    @Test
    public void testUpdateProfilePicture(){
        final long imageId = new SimpleJdbcInsert(ds).withTableName(IMAGES_TABLE).usingGeneratedKeyColumns("id")
            .executeAndReturnKey(Map.of("content", FAKEEMAIL.getBytes())).longValue();
        final long userid = insertUserOverride(Map.of("profilepic", imageId));
        
        userDao.updateProfilePicture(userid, PROFILEPICID);
        
        assertEqualsMaybeUser(jdbcTemplate.query("SELECT * FROM users WHERE id = ?", USER_ROW_MAPPER, userid).stream().findFirst());
    }
    @Test(expected = DataAccessException.class)
    public void testUpdateProfilePictureWrongPicture(){
        final long userid = insertUserGeneric();

        userDao.updateProfilePicture(userid, 123411234);
    }
    @Test
    public void testUpdateProfilePictureWrongUser(){
        userDao.updateProfilePicture(1234, PROFILEPICID);
    }

    @Test
    public void testUpdateUniversity(){
        final long universityId = new SimpleJdbcInsert(ds).withTableName(UNIVERSITIES_TABLE).usingGeneratedKeyColumns("id")
            .executeAndReturnKey(Map.of(
                "name", "Universidad de muy largo", 
                "abbreviation", "UBA", 
                "deleted", false,
                "city_id", jdbcTemplate.queryForObject("SELECT id FROM cities LIMIT 1", Long.class)
            )).longValue();
        final long userid = insertUserOverride(Map.of("university", universityId));

        userDao.updateUniversity(userid, UNIVERSITY.getId());

        assertEqualsMaybeUser(jdbcTemplate.query("SELECT * FROM users WHERE id = ?", USER_ROW_MAPPER, userid).stream().findFirst());
    }
    @Test(expected = DataAccessException.class)
    public void testUpdateUniversityWrongUniversity(){
        final long userid = insertUserGeneric();

        userDao.updateUniversity(userid, 12341234);
    }
    @Test
    public void testUpdateUniversityWrongUser(){
        userDao.updateUniversity(13241234, 12341234);
    }

    @Test
    public void testUpdateCareer(){
        final long careerId = new SimpleJdbcInsert(ds).withTableName(CAREERS_TABLE).usingGeneratedKeyColumns("id")
            .executeAndReturnKey(Map.of("name", "Abogacia", "deleted", false)).longValue();
        final long userid = insertUserOverride(Map.of("career", careerId));

        userDao.updateCareer(userid, CAREER.getId());

        assertEqualsMaybeUser(jdbcTemplate.query("SELECT * FROM users WHERE id = ?", USER_ROW_MAPPER, userid).stream().findFirst());
    }
    @Test(expected = DataAccessException.class)
    public void testUpdateCareerWrongCareer(){
        final long userid = insertUserGeneric();

        userDao.updateCareer(userid, 12341234);
    }
    @Test
    public void testUpdateCareerWrongUser(){
        userDao.updateCareer(13241234, 12341234);
    }

    @Test
    public void testUpdateGeneric(){
        final long userid = insertUserGeneric();

        userDao.update(userid, FIRSTNAME, LASTNAME, USERNAME, UNIVERSITY.getId(), CAREER.getId(), Locale.of(LOCALE));
        
        assertEqualsMaybeUser(jdbcTemplate.query("SELECT * FROM users WHERE id = ?", USER_ROW_MAPPER, userid).stream().findFirst());
    }
    @Test
    public void testUpdateFirstname(){
        final long userid = insertUserGeneric();

        userDao.update(userid, FIRSTNAME, null, null, null, null, null);
        
        assertEqualsMaybeUser(jdbcTemplate.query("SELECT * FROM users WHERE id = ?", USER_ROW_MAPPER, userid).stream().findFirst());
    }
    @Test
    public void testUpdateLastname(){
        final long userid = insertUserGeneric();

        userDao.update(userid, null, LASTNAME, null, null, null, null);
        
        assertEqualsMaybeUser(jdbcTemplate.query("SELECT * FROM users WHERE id = ?", USER_ROW_MAPPER, userid).stream().findFirst());
    }
    @Test
    public void testUpdateUsername(){
        final long userid = insertUserGeneric();

        userDao.update(userid, null, null, USERNAME, null, null, null);
        
        assertEqualsMaybeUser(jdbcTemplate.query("SELECT * FROM users WHERE id = ?", USER_ROW_MAPPER, userid).stream().findFirst());
    }
    @Test
    public void testUpdateGenericUniversity(){
        final long userid = insertUserGeneric();

        userDao.update(userid, null, null, null, UNIVERSITY.getId(), null, null);
        
        assertEqualsMaybeUser(jdbcTemplate.query("SELECT * FROM users WHERE id = ?", USER_ROW_MAPPER, userid).stream().findFirst());
    }
    @Test
    public void testUpdateCareerGeneric(){
        final long userid = insertUserGeneric();

        userDao.update(userid, null, null, null, null, CAREER.getId(), null);
        
        assertEqualsMaybeUser(jdbcTemplate.query("SELECT * FROM users WHERE id = ?", USER_ROW_MAPPER, userid).stream().findFirst());
    }
    @Test
    public void testUpdateLocaleGeneric(){
        final long userid = insertUserGeneric();

        userDao.update(userid, null, null, null, null, null, Locale.of(LOCALE));
        
        assertEqualsMaybeUser(jdbcTemplate.query("SELECT * FROM users WHERE id = ?", USER_ROW_MAPPER, userid).stream().findFirst());
    }
    @Test
    public void testUpdateWrongUser(){
        userDao.update(12341234, FIRSTNAME, LASTNAME, USERNAME, UNIVERSITY.getId(), CAREER.getId(), Locale.of(LOCALE));
    }
    @Test
    public void testUpdateNoArguments(){
        final long userid = insertUserGeneric();

        userDao.update(userid, null, null, null, null, null, null);
        
        assertEqualsMaybeUser(jdbcTemplate.query("SELECT * FROM users WHERE id = ?", USER_ROW_MAPPER, userid).stream().findFirst());    
    }

    @Test
    public void testGetAllUsers(){
        insertUserGeneric();

        List<User> users = userDao.getAllUsers();

        assertNotNull(users);
        assertEquals(1, users.size());
        assertEqualsUser(users.getFirst());
    }
    @Test 
    public void testGetAllUsersNoUsers(){
        List<User> users = userDao.getAllUsers();

        assertNotNull(users);
        assertEquals(0, users.size());
    }

    @Test
    public void testGetAllUsersPaged(){
        insertUserGeneric();
        Map<String, Object> userParams1 = Map.of("email", USERMAIL2, "username", USERNAME2);
        Map<String, Object> userParams2 = Map.of("email", USERMAIL3, "username", USERNAME3);
        Map<String, Object> userParams3 = Map.of("email", USERMAIL4, "username", USERNAME4);
        Map<String, Object> userParams4 = Map.of("email", USERMAIL5, "username", USERNAME5);
        insertUserOverride(userParams1);
        insertUserOverride(userParams2);
        insertUserOverride(userParams3);
        insertUserOverride(userParams4);

        Page<User> page1 = userDao.getAllUsers(1, PAGESIZE);
        Page<User> page2 = userDao.getAllUsers(2, PAGESIZE);
        Page<User> page3 = userDao.getAllUsers(3, PAGESIZE);

        assertNotNull(page1);
        assertNotNull(page2);
        assertNotNull(page3);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(2, page2.getCurrentPage());
        assertEquals(3, page3.getCurrentPage());
        assertNotNull(page1.getContent());
        assertNotNull(page2.getContent());
        assertNotNull(page3.getContent());
        assertEquals(PAGESIZE, page1.getContent().size());
        assertEquals(PAGESIZE, page2.getContent().size());
        assertEquals(1, page3.getContent().size());
        assertEqualsUser(page1.getContent().getFirst());
        assertEqualsUser(page1.getContent().getLast(), userParams1);
        assertEqualsUser(page2.getContent().getFirst(), userParams2);
        assertEqualsUser(page2.getContent().getLast(), userParams3);
        assertEqualsUser(page3.getContent().getFirst(), userParams4);
    }
    @Test
    public void testGetAllUsersPagedWrongPage(){
        insertUserGeneric();
        insertUserOverride(Map.of("email", USERMAIL2, "username", USERNAME2));

        Page<User> page1 = userDao.getAllUsers(1, PAGESIZE);
        Page<User> page2 = userDao.getAllUsers(2, PAGESIZE);
        Page<User> page3 = userDao.getAllUsers(3, PAGESIZE);

        assertNotNull(page1);
        assertNotNull(page2);
        assertNotNull(page3);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(2, page2.getCurrentPage());
        assertEquals(3, page3.getCurrentPage());
        assertNotNull(page1.getContent());
        assertNotNull(page2.getContent());
        assertNotNull(page3.getContent());
        assertEquals(PAGESIZE, page1.getContent().size());
        assertEquals(0, page2.getContent().size());
        assertEquals(0, page3.getContent().size());
        assertEqualsUser(page1.getContent().getFirst());
    }

    @Test
    public void testSearchUsersPaged(){
        Map<String, Object> userParams1 = Map.of("email", USERMAIL2, "username", USERNAME2);
        insertUserGeneric();
        insertUserOverride(userParams1);

        Page<User> page1 = userDao.searchUsers(FIRSTNAME, 1, PAGESIZE);

        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertNotNull(page1.getContent());
        assertEquals(PAGESIZE, page1.getContent().size());
        assertEqualsUser(page1.getContent().getFirst(), userParams1);
        assertEqualsUser(page1.getContent().getLast());
    }
    @Test
    public void testSearchUsersPaged2(){
        Map<String, Object> userParams1 = Map.of("email", USERMAIL2, "username", USERNAME2);
        insertUserGeneric();
        insertUserOverride(userParams1);

        Page<User> page1 = userDao.searchUsers(FIRSTNAME, 1, 1);
        Page<User> page2 = userDao.searchUsers(FIRSTNAME, 2, 1);

        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertNotNull(page1.getContent());
        assertEquals(1, page1.getContent().size());
        assertEqualsUser(page1.getContent().getFirst(), userParams1);
        assertEqualsUser(page2.getContent().getFirst());
    }

    @Test
    public void testListJourneyRespondersMinusUsers(){
        Map<String, Object> userParams1 = Map.of("username", USERNAME2, "email", USERMAIL2);
        Map<String, Object> userParams2 = Map.of("username", USERNAME3, "email", USERMAIL3);
        long ownerUserId = insertUserGeneric();
        long replyUserId1 = insertUserOverride(userParams1);
        long replyUserId2 = insertUserOverride(userParams2);
        long journeyId = new SimpleJdbcInsert(ds).withTableName(JOURNEY_TABLE).usingGeneratedKeyColumns("id")
            .executeAndReturnKey(Map.of(
                "user_id", ownerUserId, 
                "destination_university_id", UNIVERSITY.getId(), 
                "start_date", LocalDate.now().toString(), 
                "end_date", LocalDate.now().plusDays(10).toString(),
                "deleted", false))
            .longValue();
        SimpleJdbcInsert journeyReplyInsert = new SimpleJdbcInsert(ds).withTableName(JOURNEY_REPLY_TABLE).usingGeneratedKeyColumns("id");
        journeyReplyInsert.execute(Map.of("user_id", replyUserId1, "journey_id", journeyId, "message", USERMAIL2, "date_time", Timestamp.valueOf(LocalDateTime.now()), "deleted", false, "deleted_message", ""));
        journeyReplyInsert.execute(Map.of("user_id", replyUserId2, "journey_id", journeyId, "message", USERMAIL2, "date_time", Timestamp.valueOf(LocalDateTime.now()), "deleted", false, "deleted_message", ""));

        List<User> repliesUser = userDao.listJourneyRespondersMinusUsers(journeyId);

        assertNotNull(repliesUser);
        assertEquals(2, repliesUser.size());
        for (User user : repliesUser) {
            if (user.getId() == replyUserId1) {
                assertEqualsUser(user, userParams1);
            } else {
                assertEqualsUser(user, userParams2);
            }
        }
    }
    @Test
    public void testListEventRespondersMinusUsers(){
        Map<String, Object> userParams1 = Map.of("username", USERNAME2, "email", USERMAIL2);
        Map<String, Object> userParams2 = Map.of("username", USERNAME3, "email", USERMAIL3);
        long ownerUserId = insertUserGeneric();
        long replyUserId1 = insertUserOverride(userParams1);
        long replyUserId2 = insertUserOverride(userParams2);
        long eventId = new SimpleJdbcInsert(ds).withTableName(EVENT_TABLE).usingGeneratedKeyColumns("id")
            .executeAndReturnKey(Map.of(
                "user_id", ownerUserId, 
                "city_id", jdbcTemplate.queryForObject("SELECT id FROM cities LIMIT 1", Long.class), 
                "event_date", LocalDate.now().plusDays(10).toString(),
                "title", "title",
                "description", "event",
                "deleted", false))
            .longValue();
        SimpleJdbcInsert eventReplyInsert = new SimpleJdbcInsert(ds).withTableName(EVENT_REPLY_TABLE).usingGeneratedKeyColumns("id");
        eventReplyInsert.execute(Map.of("user_id", replyUserId1, "event_id", eventId, "message", USERMAIL2, "date_time", Timestamp.valueOf(LocalDateTime.now()), "deleted", false, "deleted_message", ""));
        eventReplyInsert.execute(Map.of("user_id", replyUserId2, "event_id", eventId, "message", USERMAIL2, "date_time", Timestamp.valueOf(LocalDateTime.now()), "deleted", false, "deleted_message", ""));

        List<User> repliesUser = userDao.listEventRespondersMinusUsers(eventId);

        assertNotNull(repliesUser);
        assertEquals(2, repliesUser.size());
        for (User user : repliesUser) {
            if (user.getId() == replyUserId1) {
                assertEqualsUser(user, userParams1);
            } else {
                assertEqualsUser(user, userParams2);
            }
        }
    }
}   
