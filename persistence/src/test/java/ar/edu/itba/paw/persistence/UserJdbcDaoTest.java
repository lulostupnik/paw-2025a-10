package ar.edu.itba.paw.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
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
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Rollback
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class UserJdbcDaoTest {

    @Autowired
    private DataSource ds;

    @Autowired
    private UserJdbcDao userDao;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp(){
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Test
    public void testCreateUser(){
        TestUtils.deleteUsers(jdbcTemplate);

        final User user = userDao.create(TestUtils.USER_1_MAIL, TestUtils.USER_1_NAME, TestUtils.USER_FIRSTNAME, TestUtils.USER_LASTNAME, TestUtils.UNI_1, TestUtils.CAREER_1, TestUtils.IMAGE_1_ID, TestUtils.USER_PASSWORD, Locale.of(TestUtils.USER_LOCALE), TestUtils.USER_TOKEN_NEW, TestUtils.USER_EXPIRATION_DEFAULT);

        TestUtils.assertEqualsUser(user, Map.of("id", user.getId()));
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.USER_TABLE));
    }

    @Test(expected = DataAccessException.class)
    public void testCreateUserNoMail(){
        userDao.create(null, TestUtils.USER_NEW1_NAME, TestUtils.USER_FIRSTNAME, TestUtils.USER_LASTNAME, TestUtils.UNI_1, TestUtils.CAREER_1, TestUtils.IMAGE_1_ID, TestUtils.USER_PASSWORD, Locale.of(TestUtils.USER_LOCALE), TestUtils.USER_TOKEN_NEW, TestUtils.USER_EXPIRATION_DEFAULT);
    }
    @Test(expected = DataAccessException.class)
    public void testCreateUserNoUsername(){
        userDao.create(TestUtils.USER_NEW1_MAIL, null, TestUtils.USER_FIRSTNAME, TestUtils.USER_LASTNAME, TestUtils.UNI_1, TestUtils.CAREER_1, TestUtils.IMAGE_1_ID, TestUtils.USER_PASSWORD, Locale.of(TestUtils.USER_LOCALE), TestUtils.USER_TOKEN_NEW, TestUtils.USER_EXPIRATION_DEFAULT);
    }
    @Test(expected = DataAccessException.class)
    public void testCreateUserNoFirstName(){
        userDao.create(TestUtils.USER_NEW1_MAIL, TestUtils.USER_NEW1_NAME, null, TestUtils.USER_LASTNAME, TestUtils.UNI_1, TestUtils.CAREER_1, TestUtils.IMAGE_1_ID, TestUtils.USER_PASSWORD, Locale.of(TestUtils.USER_LOCALE), TestUtils.USER_TOKEN_NEW, TestUtils.USER_EXPIRATION_DEFAULT);
    }
    @Test(expected = DataAccessException.class)
    public void testCreateUserNoLastName(){
        userDao.create(TestUtils.USER_NEW1_MAIL, TestUtils.USER_NEW1_NAME, TestUtils.USER_FIRSTNAME, null, TestUtils.UNI_1, TestUtils.CAREER_1, TestUtils.IMAGE_1_ID, TestUtils.USER_PASSWORD, Locale.of(TestUtils.USER_LOCALE), TestUtils.USER_TOKEN_NEW, TestUtils.USER_EXPIRATION_DEFAULT);
    }
    @Test(expected = NullPointerException.class)
    public void testCreateUserNoUniversity(){
        userDao.create(TestUtils.USER_NEW1_MAIL, TestUtils.USER_NEW1_NAME, TestUtils.USER_FIRSTNAME, TestUtils.USER_LASTNAME, null, TestUtils.CAREER_1, TestUtils.IMAGE_1_ID, TestUtils.USER_PASSWORD, Locale.of(TestUtils.USER_LOCALE), TestUtils.USER_TOKEN_NEW, TestUtils.USER_EXPIRATION_DEFAULT);
    }
    @Test(expected = DataAccessException.class)
    public void testCreateUserInvalidUniversity(){
        userDao.create(TestUtils.USER_NEW1_MAIL, TestUtils.USER_NEW1_NAME, TestUtils.USER_FIRSTNAME, TestUtils.USER_LASTNAME, new University(1034234123, null, null, null), TestUtils.CAREER_1, TestUtils.IMAGE_1_ID, TestUtils.USER_PASSWORD, Locale.of(TestUtils.USER_LOCALE), TestUtils.USER_TOKEN_NEW, TestUtils.USER_EXPIRATION_DEFAULT);
    }
    @Test(expected = NullPointerException.class)
    public void testCreateUserNoCareer(){
        userDao.create(TestUtils.USER_NEW1_MAIL, TestUtils.USER_NEW1_NAME, TestUtils.USER_FIRSTNAME, TestUtils.USER_LASTNAME, TestUtils.UNI_1, null, TestUtils.IMAGE_1_ID, TestUtils.USER_PASSWORD, Locale.of(TestUtils.USER_LOCALE), TestUtils.USER_TOKEN_NEW, TestUtils.USER_EXPIRATION_DEFAULT);
    }
    @Test(expected = DataAccessException.class)
    public void testCreateUserInvalidCareer(){
        userDao.create(TestUtils.USER_NEW1_MAIL, TestUtils.USER_NEW1_NAME, TestUtils.USER_FIRSTNAME, TestUtils.USER_LASTNAME, TestUtils.UNI_1, new Career((long)1313423,null), TestUtils.IMAGE_1_ID, TestUtils.USER_PASSWORD, Locale.of(TestUtils.USER_LOCALE), TestUtils.USER_TOKEN_NEW, TestUtils.USER_EXPIRATION_DEFAULT);
    }
    @Test(expected = DataAccessException.class)
    public void testCreateUserInvalidPic(){
        userDao.create(TestUtils.USER_NEW1_MAIL, TestUtils.USER_NEW1_NAME, TestUtils.USER_FIRSTNAME, TestUtils.USER_LASTNAME, TestUtils.UNI_1, TestUtils.CAREER_1, 123123123, TestUtils.USER_PASSWORD, Locale.of(TestUtils.USER_LOCALE), TestUtils.USER_TOKEN_NEW, TestUtils.USER_EXPIRATION_DEFAULT);
    }
    @Test(expected = DataAccessException.class)
    public void testCreateUserNoPassword(){
        userDao.create(TestUtils.USER_NEW1_MAIL, TestUtils.USER_NEW1_NAME, TestUtils.USER_FIRSTNAME, TestUtils.USER_LASTNAME, TestUtils.UNI_1, TestUtils.CAREER_1, TestUtils.IMAGE_1_ID, null, Locale.of(TestUtils.USER_LOCALE), TestUtils.USER_TOKEN_NEW, TestUtils.USER_EXPIRATION_DEFAULT);
    }
    @Test(expected = NullPointerException.class)
    public void testCreateUserNoLocale(){
        userDao.create(TestUtils.USER_NEW1_MAIL, TestUtils.USER_NEW1_NAME, TestUtils.USER_FIRSTNAME, TestUtils.USER_LASTNAME, TestUtils.UNI_1, TestUtils.CAREER_1, TestUtils.IMAGE_1_ID, TestUtils.USER_PASSWORD, null, TestUtils.USER_TOKEN_NEW, TestUtils.USER_EXPIRATION_DEFAULT);
    }
    @Test(expected = DataAccessException.class)
    public void testCreateUserInvalidLocale(){
        userDao.create(TestUtils.USER_NEW1_MAIL, TestUtils.USER_NEW1_NAME, TestUtils.USER_FIRSTNAME, TestUtils.USER_LASTNAME, TestUtils.UNI_1, TestUtils.CAREER_1, TestUtils.IMAGE_1_ID, TestUtils.USER_PASSWORD, Locale.of(TestUtils.USER_WRONG_LOCALE), TestUtils.USER_TOKEN_NEW, TestUtils.USER_EXPIRATION_DEFAULT);
    }
    @Test(expected = DataAccessException.class)
    public void testCreateUserMissingToken(){
        userDao.create(TestUtils.USER_NEW1_MAIL, TestUtils.USER_NEW1_NAME, TestUtils.USER_FIRSTNAME, TestUtils.USER_LASTNAME, TestUtils.UNI_1, TestUtils.CAREER_1, TestUtils.IMAGE_1_ID, TestUtils.USER_PASSWORD, Locale.of(TestUtils.USER_WRONG_LOCALE), null, TestUtils.USER_EXPIRATION_DEFAULT);
    }
    @Test(expected = NullPointerException.class)
    public void testCreateUserMissingExpiration(){
        userDao.create(TestUtils.USER_NEW1_MAIL, TestUtils.USER_NEW1_NAME, TestUtils.USER_FIRSTNAME, TestUtils.USER_LASTNAME, TestUtils.UNI_1, TestUtils.CAREER_1, TestUtils.IMAGE_1_ID, TestUtils.USER_PASSWORD, Locale.of(TestUtils.USER_WRONG_LOCALE), TestUtils.USER_TOKEN_NEW, null);
    }

    @Test
    public void testFindUserById(){
        Optional<User> maybeUser = userDao.findById(TestUtils.USER_1_ID);

        assertNotNull(maybeUser);
        assertTrue(maybeUser.isPresent());
        TestUtils.assertEqualsUser(TestUtils.USER_1, maybeUser.get());     
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

        assertNotNull(maybeUser);
        assertTrue(maybeUser.isPresent());
        TestUtils.assertEqualsUser(TestUtils.USER_1, maybeUser.get());     
    }
    @Test
    public void testFindUserByEmailMissing(){
        final Optional<User> maybeUser = userDao.findByEmail(TestUtils.USER_FAKE_MAIL);

        assertNotNull(maybeUser);
        assertFalse(maybeUser.isPresent());
    }
    @Test
    public void testFindUserByEmailWithPassword(){
        final Optional<UserAuthInfo> maybeUser = userDao.findAuthInfoByEmail(TestUtils.USER_1_MAIL);

        assertNotNull(maybeUser);
        assertTrue(maybeUser.isPresent());
        final UserAuthInfo user = maybeUser.get();
        TestUtils.assertEqualsUserPassword(user);
    }
    @Test
    public void testFindUserByEmailWithPasswordMissing(){
        final Optional<UserAuthInfo> maybeUser = userDao.findAuthInfoByEmail(TestUtils.USER_FAKE_MAIL);
        assertNotNull(maybeUser);
        assertFalse(maybeUser.isPresent());
    }

    @Test
    public void testUpdateToken(){
        userDao.updateToken(TestUtils.USER_1_ID, TestUtils.USER_PASSWORD, TestUtils.USER_EXPIRATION_DEFAULT);

        assertEquals(TestUtils.USER_PASSWORD, jdbcTemplate.queryForObject(TestUtils.USER_SELECT_TOKEN_BY_ID, String.class, TestUtils.USER_1_ID));
        assertEquals(TestUtils.USER_EXPIRATION_DEFAULT, jdbcTemplate.queryForObject(TestUtils.USER_SELECT_EXPIRATION_BY_ID, LocalDate.class, TestUtils.USER_1_ID));
    }
    @Test
    public void testUpdateTokenNotFound(){
        userDao.updateToken(12341234, TestUtils.USER_PASSWORD, TestUtils.USER_EXPIRATION_DEFAULT);

        TestUtils.assertUserDBDefaultStatus(jdbcTemplate);
    }

    @Test
    public void testfindValidationStatusByEmail(){
        boolean status = userDao.findValidationStatusByEmail(TestUtils.USER_1_MAIL);

        assertTrue(status);
    }
    @Test
    public void testfindValidationStatusByEmailNotValidated(){
        boolean status = userDao.findValidationStatusByEmail(TestUtils.USER_4_MAIL);

        assertFalse(status);
    }

    @Test
    public void testUpdatePassword(){
        userDao.updatePassword(TestUtils.USER_1_ID, TestUtils.USER_FAKE_PASSWORD);

        TestUtils.assertEqualsUser(
            jdbcTemplate.queryForObject(TestUtils.USER_SELECT_BY_ID, TestUtils.USER_ROW_MAPPER, TestUtils.USER_1_ID), 
            Map.of("password", TestUtils.USER_FAKE_PASSWORD)
        );
    }
    @Test
    public void testUpdatePasswordMissingUser(){
        userDao.updatePassword(12341234l, TestUtils.USER_PASSWORD);
    }
    @Test(expected = DataAccessException.class)
    public void testUpdatePasswordMissingPassword(){
        userDao.updatePassword(TestUtils.USER_1_ID, null);

        TestUtils.assertEqualsUser(jdbcTemplate.queryForObject(TestUtils.USER_SELECT_BY_ID, TestUtils.USER_ROW_MAPPER, TestUtils.USER_1_ID));
    }
    @Test
    public void testUpdatePasswordEmptyPassword(){
        userDao.updatePassword(TestUtils.USER_1_ID, "");

        TestUtils.assertEqualsUser(jdbcTemplate.queryForObject(TestUtils.USER_SELECT_BY_ID, TestUtils.USER_ROW_MAPPER, TestUtils.USER_1_ID));
    }

    @Test
    public void testUpdateTokenAndExpirationByToken(){
        userDao.updateTokenAndExpirationByToken(TestUtils.USER_PASSWORD, TestUtils.USER_EXPIRATION_DEFAULT, TestUtils.USER_TOKEN_DEFAULT);

        assertEquals(TestUtils.USER_PASSWORD, jdbcTemplate.queryForObject(TestUtils.USER_SELECT_TOKEN_BY_ID, String.class, TestUtils.USER_3_ID));
        assertEquals(TestUtils.USER_EXPIRATION_DEFAULT, jdbcTemplate.queryForObject(TestUtils.USER_SELECT_EXPIRATION_BY_ID, LocalDate.class, TestUtils.USER_3_ID));
    }
    @Test
    public void testUpdateTokenAndExpirationByTokenInvalidToken(){
        userDao.updateTokenAndExpirationByToken(TestUtils.USER_PASSWORD, TestUtils.USER_EXPIRATION_DEFAULT, "USER_TOKEN_DEFAULT");

        assertEquals(TestUtils.USER_TOKEN_DEFAULT, jdbcTemplate.queryForObject(TestUtils.USER_SELECT_TOKEN_BY_ID, String.class, TestUtils.USER_3_ID));
    }

    @Test
    public void testUpdatePasswordAndClearTokenByToken(){
        userDao.updatePasswordAndClearTokenByToken(TestUtils.USER_TOKEN_DEFAULT, TestUtils.USER_FAKE_PASSWORD);

        assertEquals(null, jdbcTemplate.queryForObject(TestUtils.USER_SELECT_TOKEN_BY_ID, String.class, TestUtils.USER_3_ID));
        assertEquals(null, jdbcTemplate.queryForObject(TestUtils.USER_SELECT_EXPIRATION_BY_ID, LocalDate.class, TestUtils.USER_3_ID));
        assertEquals(TestUtils.USER_FAKE_PASSWORD, jdbcTemplate.queryForObject(TestUtils.USER_SELECT_PASSWORD_BY_ID, String.class, TestUtils.USER_3_ID));
    }
    @Test
    public void testUpdatePasswordAndClearTokenByTokenInvalidToken(){
        userDao.updatePasswordAndClearTokenByToken("USER_TOKEN_DEFAULT", TestUtils.USER_FAKE_PASSWORD);

        assertEquals(TestUtils.USER_TOKEN_DEFAULT, jdbcTemplate.queryForObject(TestUtils.USER_SELECT_TOKEN_BY_ID, String.class, TestUtils.USER_3_ID));
        assertEquals(TestUtils.USER_EXPIRATION_DEFAULT, jdbcTemplate.queryForObject(TestUtils.USER_SELECT_EXPIRATION_BY_ID, LocalDate.class, TestUtils.USER_3_ID));
        assertEquals(TestUtils.USER_PASSWORD, jdbcTemplate.queryForObject(TestUtils.USER_SELECT_PASSWORD_BY_ID, String.class, TestUtils.USER_3_ID));
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
    public void testFindAllPaged(){
        Page<User> page1 = userDao.findAll(TestUtils.PAGE_1_DEFAULT);
        Page<User> page2 = userDao.findAll(TestUtils.PAGE_2_DEFAULT);

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
        for (User u : page1.getContent()){
            TestUtils.assertEqualsUser(TestUtils.USER_DATA.get(u.getId()), u);
        }
        for (User u : page2.getContent()){
            TestUtils.assertEqualsUser(TestUtils.USER_DATA.get(u.getId()), u);
        }
    }
    @Test
    public void testFindAllPagedWrongPage(){
        Page<User> page1 = userDao.findAll(TestUtils.PAGE_1_BIG);
        Page<User> page2 = userDao.findAll(TestUtils.PAGE_2_BIG);

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
        for (User u : page1.getContent()){
            TestUtils.assertEqualsUser(TestUtils.USER_DATA.get(u.getId()), u);
        }
    }

    @Test
    public void testSearchPaged(){
        Page<User> page1 = userDao.search(TestUtils.USER_FIRSTNAME, TestUtils.PAGE_1_DEFAULT);
        Page<User> page2 = userDao.search(TestUtils.USER_FIRSTNAME, TestUtils.PAGE_2_DEFAULT);

        assertNotNull(page1);
        assertNotNull(page2);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(4, page1.getTotalPages());
        assertEquals(2, page2.getCurrentPage());
        assertEquals(4, page2.getTotalPages());
        assertNotNull(page1.getContent());
        assertNotNull(page2.getContent());
        assertEquals(TestUtils.PAGE_SIZE_DEFAULT, page1.getContent().size());
        assertEquals(TestUtils.PAGE_SIZE_DEFAULT, page2.getContent().size());
        for (User u : page1.getContent()){
            TestUtils.assertEqualsUser(TestUtils.USER_DATA.get(u.getId()), u);
        }
        for (User u : page2.getContent()){
            TestUtils.assertEqualsUser(TestUtils.USER_DATA.get(u.getId()), u);
        }
    }
    @Test
    public void testSearchPaged2(){
        Page<User> page1 = userDao.search(TestUtils.USER_1_NAME, TestUtils.PAGE_1_DEFAULT);
        Page<User> page2 = userDao.search(TestUtils.USER_1_NAME, TestUtils.PAGE_2_DEFAULT);

        assertNotNull(page1);
        assertEquals(1, page1.getCurrentPage());
        assertEquals(2, page2.getCurrentPage());
        assertEquals(1, page1.getTotalPages());
        assertEquals(1, page2.getTotalPages());
        assertNotNull(page1.getContent());
        assertNotNull(page2.getContent());
        assertEquals(1, page1.getContent().size());
        assertEquals(0, page2.getContent().size());
        TestUtils.assertEqualsUser(TestUtils.USER_1, page1.getContent().get(0));
    }

   @Test
   public void testFindAllJourneyResponders(){
        List<User> repliesUser = userDao.findAllJourneyResponders(TestUtils.JOURNEY_1_ID);

        assertNotNull(repliesUser);
        assertEquals(TestUtils.TOTAL_JOURNEY_RESPONSES, repliesUser.size());
        for (User user : repliesUser) {
            TestUtils.assertEqualsUser(TestUtils.USER_DATA.get(user.getId()), user);
        }
   }
    @Test
    public void testFindAllEventResponders(){
        List<User> repliesUser = userDao.findAllEventResponders(TestUtils.EVENT_1_ID);

        assertNotNull(repliesUser);
        assertEquals(1, repliesUser.size());
        for (User user : repliesUser) {
            TestUtils.assertEqualsUser(TestUtils.USER_DATA.get(user.getId()), user);
        }
    }

    @Test
    public void testBlockUser(){
        userDao.updateBlock(TestUtils.USER_1_ID, true);

        User user = jdbcTemplate.queryForObject(TestUtils.USER_SELECT_BY_ID, TestUtils.USER_ROW_MAPPER, TestUtils.USER_1_ID);
        TestUtils.assertEqualsUser(user, Map.of("blocked", true));
    }
    @Test
    public void testBlockUserBlocked(){
        Map<String, Object> params = new HashMap<>(Map.of("email", TestUtils.USER_NEW1_MAIL, "username", TestUtils.USER_NEW1_NAME, "blocked", true));
        User user = TestUtils.insertUser(ds, params);
        params.put("id", user.getId());

        userDao.updateBlock(user.getId(), true);

        TestUtils.assertEqualsUser(jdbcTemplate.queryForObject(TestUtils.USER_SELECT_BY_ID, TestUtils.USER_ROW_MAPPER, user.getId()), params);
    }
    @Test
    public void testBlockUserWrongId(){
        userDao.updateBlock(12341234l, true);

        TestUtils.assertUserDBDefaultStatus(jdbcTemplate);
    }

    @Test
    public void testUnblockUser(){
        User user = TestUtils.insertUser(ds, Map.of("email", TestUtils.USER_NEW1_MAIL, "username", TestUtils.USER_NEW1_NAME, "blocked", true));

        userDao.updateBlock(user.getId(), false);

        TestUtils.assertEqualsUser(jdbcTemplate.queryForObject(TestUtils.USER_SELECT_BY_ID, TestUtils.USER_ROW_MAPPER, user.getId()), Map.of("email", TestUtils.USER_NEW1_MAIL, "username", TestUtils.USER_NEW1_NAME, "id", user.getId()));
    }
    @Test
    public void testUnblockUserUnblocked(){
        userDao.updateBlock(TestUtils.USER_1_ID, false);
        
        TestUtils.assertEqualsUser(jdbcTemplate.queryForObject(TestUtils.USER_SELECT_BY_ID, TestUtils.USER_ROW_MAPPER, TestUtils.USER_1_ID));
    }
    @Test
    public void testUnblockUserWrongId(){
        userDao.updateBlock(12341234l, false);
        
        TestUtils.assertUserDBDefaultStatus(jdbcTemplate);
    }

    @Test
    public void testExistsByTokenNotExpired(){
        TestUtils.insertUser(ds, Map.of("username", TestUtils.USER_NEW1_NAME, "email", TestUtils.USER_NEW1_MAIL));

        boolean isValid = userDao.existsByTokenNotExpired(TestUtils.USER_TOKEN_NEW);

        assertTrue(isValid);
    }
    @Test
    public void testExistsByTokenNotExpiredNotInUse(){
        boolean isValid = userDao.existsByTokenNotExpired("USER_VALID_TOKEN_DEFAULT");

        assertFalse(isValid);
    }
    @Test
    public void testExistsByTokenNotExpiredExpired(){
        TestUtils.insertUser(ds, Map.of("username", TestUtils.USER_NEW1_NAME, "email", TestUtils.USER_NEW1_MAIL, "tokenExpiration", LocalDate.now().plusDays(-1)));

        boolean isValid = userDao.existsByTokenNotExpired(TestUtils.USER_TOKEN_NEW);

        assertFalse(isValid);
    }

    @Test
    public void testExistsByTokenExpired(){
        TestUtils.insertUser(ds, Map.of("username", TestUtils.USER_NEW1_NAME, "email", TestUtils.USER_NEW1_MAIL));

        boolean isExpired = userDao.existsByTokenExpired(TestUtils.USER_TOKEN_NEW);

        assertFalse(isExpired);
    }
    @Test
    public void testExistsByTokenExpiredExpired(){
        TestUtils.insertUser(ds, Map.of("username", TestUtils.USER_NEW1_NAME, "email", TestUtils.USER_NEW1_MAIL, "tokenExpiration", LocalDate.now().plusDays(-1)));

        boolean isExpired = userDao.existsByTokenExpired(TestUtils.USER_TOKEN_NEW);

        assertTrue(isExpired);
    }
    @Test
    public void testExistsByTokenExpiredMissing(){
        boolean isExpired = userDao.existsByTokenExpired(TestUtils.USER_TOKEN_NEW);

        assertFalse(isExpired);
    }
    @Test
    public void testExistsByTokenExpiredEmpty(){
        TestUtils.insertUser(ds, Map.of("username", TestUtils.USER_NEW1_NAME, "email", TestUtils.USER_NEW1_MAIL, "tokenExpiration", LocalDate.now().plusDays(-1)));

        boolean isExpired = userDao.existsByTokenExpired("");

        assertFalse(isExpired);
    }

    @Test
    public void testFindValidatedTokenNotExpired(){
        Optional<Boolean> maybeValidated = userDao.findValidatedByTokenNotExpired(TestUtils.USER_TOKEN_DEFAULT);

        assertNotNull(maybeValidated);
        assertTrue(maybeValidated.isPresent());
        assertTrue(maybeValidated.get());
    }
    @Test
    public void testFindValidatedTokenNotExpiredNoToken(){
        Optional<Boolean> maybeValidated = userDao.findValidatedByTokenNotExpired(null);

        assertNotNull(maybeValidated);
        assertFalse(maybeValidated.isPresent());
    }

    //HSQL does not support 'RETURNING'
    // @Test
    // public void testUpdateValidationAndFindAuthInfoByToken(){
    //     Optional<UserAuthInfo> maybeInfo = userDao.updateValidationAndFindAuthInfoByToken(TestUtils.USER_TOKEN_DEFAULT);

    //     assertNotNull(maybeInfo);
    //     assertTrue(maybeInfo.isPresent());
    //     assertTrue(maybeInfo.get().isVerified());
    // }

    @Test
    public void findByToken(){
        Optional<User> maybeUser = userDao.findByToken(TestUtils.USER_TOKEN_DEFAULT);

        assertNotNull(maybeUser);
        assertTrue(maybeUser.isPresent());
        assertEquals(TestUtils.USER_TOKEN_DEFAULT, jdbcTemplate.queryForObject(TestUtils.USER_SELECT_TOKEN_BY_ID, String.class, maybeUser.get().getId()));
        assertEquals(TestUtils.USER_EXPIRATION_DEFAULT, jdbcTemplate.queryForObject(TestUtils.USER_SELECT_EXPIRATION_BY_ID, LocalDate.class, maybeUser.get().getId()));
        TestUtils.assertEqualsUser(TestUtils.USER_3, maybeUser.get());
    }
    @Test
    public void findByTokenMissingToken(){
        Optional<User> maybeUser = userDao.findByToken("USER_TOKEN_DEFAULT");

        assertNotNull(maybeUser);
        assertFalse(maybeUser.isPresent());
    }

    @Test
    public void testFindAllAttendeesByEventId(){
        List<User> attendees = userDao.findAllAttendeesByEventId(TestUtils.EVENT_1_ID);

        assertNotNull(attendees);
        assertEquals(TestUtils.EVENT_1_ATTENDEES, attendees.size());
        Map<Long, User> userData = Map.of(TestUtils.USER_1_ID, TestUtils.USER_1, TestUtils.USER_2_ID, TestUtils.USER_2, TestUtils.USER_3_ID, TestUtils.USER_3);
        for (User user : attendees){
            TestUtils.assertEqualsUser(userData.get(user.getId()), user);
        }
    }
    @Test
    public void testFindAllAttendeesByEventIdNoAttendees(){
        List<User> attendees = userDao.findAllAttendeesByEventId(TestUtils.EVENT_3_ID);

        assertNotNull(attendees);
        assertEquals(0, attendees.size());
    }
    @Test
    public void testFindAllAttendeesByEventIdMissingEvent(){
        List<User> attendees = userDao.findAllAttendeesByEventId(412341234);

        assertNotNull(attendees);
        assertEquals(0, attendees.size());
    }

    @Test
    public void testFindAllAttendeesByEventIdPaged(){
        Page<User> attendees = userDao.findAllAttendeesByEventId(TestUtils.EVENT_1_ID, TestUtils.PAGE_1_BIG);

        assertNotNull(attendees);
        assertNotNull(attendees.getContent());
        assertEquals(1, attendees.getCurrentPage());
        assertEquals(1, attendees.getTotalPages());
        assertEquals(TestUtils.EVENT_1_ATTENDEES, attendees.getContent().size());
        Map<Long, User> userData = Map.of(TestUtils.USER_1_ID, TestUtils.USER_1, TestUtils.USER_2_ID, TestUtils.USER_2, TestUtils.USER_3_ID, TestUtils.USER_3);
        for (User user : attendees.getContent()){
            TestUtils.assertEqualsUser(userData.get(user.getId()), user);
        }
    }
    @Test
    public void testFindAllAttendeesByEventIdPagedNoAttendees(){
        Page<User> attendees = userDao.findAllAttendeesByEventId(TestUtils.EVENT_3_ID, TestUtils.PAGE_1_DEFAULT);

        assertNotNull(attendees);
        assertNotNull(attendees.getContent());
        assertEquals(1, attendees.getCurrentPage());
        assertEquals(0, attendees.getTotalPages());
        assertEquals(0, attendees.getContent().size());
    }
    @Test
    public void testFindAllAttendeesByEventIdPagedMissingEvent(){
        Page<User> attendees = userDao.findAllAttendeesByEventId(412341234, TestUtils.PAGE_1_DEFAULT);

        assertNotNull(attendees);
        assertNotNull(attendees.getContent());
        assertEquals(1, attendees.getCurrentPage());
        assertEquals(0, attendees.getTotalPages());
        assertEquals(0, attendees.getContent().size());
    }
}