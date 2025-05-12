package ar.edu.itba.paw.persistence;

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

        final User user = userDao.create(TestUtils.USER_1_MAIL, TestUtils.USER_1_NAME, TestUtils.USER_FIRSTNAME, TestUtils.USER_LASTNAME, TestUtils.UNI_1, TestUtils.CAREER_1, TestUtils.IMAGE_1_ID, TestUtils.USER_PASSWORD, Locale.of(TestUtils.USER_LOCALE), TestUtils.USER_VALID_TOKEN_DEFAULT, TestUtils.USER_EXPIRATION_DEFAULT);

        TestUtils.assertEqualsUser(user, Map.of("id", user.getId()));
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, TestUtils.USER_TABLE));
    }

    @Test(expected = DataAccessException.class)
    public void testCreateUserNoMail(){
        userDao.create(null, TestUtils.USER_NEW1_NAME, TestUtils.USER_FIRSTNAME, TestUtils.USER_LASTNAME, TestUtils.UNI_1, TestUtils.CAREER_1, TestUtils.IMAGE_1_ID, TestUtils.USER_PASSWORD, Locale.of(TestUtils.USER_LOCALE), TestUtils.USER_VALID_TOKEN_DEFAULT, TestUtils.USER_EXPIRATION_DEFAULT);
    }
    @Test(expected = DataAccessException.class)
    public void testCreateUserNoUsername(){
        userDao.create(TestUtils.USER_NEW1_MAIL, null, TestUtils.USER_FIRSTNAME, TestUtils.USER_LASTNAME, TestUtils.UNI_1, TestUtils.CAREER_1, TestUtils.IMAGE_1_ID, TestUtils.USER_PASSWORD, Locale.of(TestUtils.USER_LOCALE), TestUtils.USER_VALID_TOKEN_DEFAULT, TestUtils.USER_EXPIRATION_DEFAULT);
    }
    @Test(expected = DataAccessException.class)
    public void testCreateUserNoFirstName(){
        userDao.create(TestUtils.USER_NEW1_MAIL, TestUtils.USER_NEW1_NAME, null, TestUtils.USER_LASTNAME, TestUtils.UNI_1, TestUtils.CAREER_1, TestUtils.IMAGE_1_ID, TestUtils.USER_PASSWORD, Locale.of(TestUtils.USER_LOCALE), TestUtils.USER_VALID_TOKEN_DEFAULT, TestUtils.USER_EXPIRATION_DEFAULT);
    }
    @Test(expected = DataAccessException.class)
    public void testCreateUserNoLastName(){
        userDao.create(TestUtils.USER_NEW1_MAIL, TestUtils.USER_NEW1_NAME, TestUtils.USER_FIRSTNAME, null, TestUtils.UNI_1, TestUtils.CAREER_1, TestUtils.IMAGE_1_ID, TestUtils.USER_PASSWORD, Locale.of(TestUtils.USER_LOCALE), TestUtils.USER_VALID_TOKEN_DEFAULT, TestUtils.USER_EXPIRATION_DEFAULT);
    }
    @Test(expected = NullPointerException.class)
    public void testCreateUserNoUniversity(){
        userDao.create(TestUtils.USER_NEW1_MAIL, TestUtils.USER_NEW1_NAME, TestUtils.USER_FIRSTNAME, TestUtils.USER_LASTNAME, null, TestUtils.CAREER_1, TestUtils.IMAGE_1_ID, TestUtils.USER_PASSWORD, Locale.of(TestUtils.USER_LOCALE), TestUtils.USER_VALID_TOKEN_DEFAULT, TestUtils.USER_EXPIRATION_DEFAULT);
    }
    @Test(expected = DataAccessException.class)
    public void testCreateUserInvalidUniversity(){
        userDao.create(TestUtils.USER_NEW1_MAIL, TestUtils.USER_NEW1_NAME, TestUtils.USER_FIRSTNAME, TestUtils.USER_LASTNAME, new University(1034234123, null, null, null), TestUtils.CAREER_1, TestUtils.IMAGE_1_ID, TestUtils.USER_PASSWORD, Locale.of(TestUtils.USER_LOCALE), TestUtils.USER_VALID_TOKEN_DEFAULT, TestUtils.USER_EXPIRATION_DEFAULT);
    }
    @Test(expected = NullPointerException.class)
    public void testCreateUserNoCareer(){
        userDao.create(TestUtils.USER_NEW1_MAIL, TestUtils.USER_NEW1_NAME, TestUtils.USER_FIRSTNAME, TestUtils.USER_LASTNAME, TestUtils.UNI_1, null, TestUtils.IMAGE_1_ID, TestUtils.USER_PASSWORD, Locale.of(TestUtils.USER_LOCALE), TestUtils.USER_VALID_TOKEN_DEFAULT, TestUtils.USER_EXPIRATION_DEFAULT);
    }
    @Test(expected = DataAccessException.class)
    public void testCreateUserInvalidCareer(){
        userDao.create(TestUtils.USER_NEW1_MAIL, TestUtils.USER_NEW1_NAME, TestUtils.USER_FIRSTNAME, TestUtils.USER_LASTNAME, TestUtils.UNI_1, new Career((long)1313423,null), TestUtils.IMAGE_1_ID, TestUtils.USER_PASSWORD, Locale.of(TestUtils.USER_LOCALE), TestUtils.USER_VALID_TOKEN_DEFAULT, TestUtils.USER_EXPIRATION_DEFAULT);
    }
    @Test(expected = DataAccessException.class)
    public void testCreateUserInvalidPic(){
        userDao.create(TestUtils.USER_NEW1_MAIL, TestUtils.USER_NEW1_NAME, TestUtils.USER_FIRSTNAME, TestUtils.USER_LASTNAME, TestUtils.UNI_1, TestUtils.CAREER_1, 123123123, TestUtils.USER_PASSWORD, Locale.of(TestUtils.USER_LOCALE), TestUtils.USER_VALID_TOKEN_DEFAULT, TestUtils.USER_EXPIRATION_DEFAULT);
    }
    @Test(expected = DataAccessException.class)
    public void testCreateUserNoPassword(){
        userDao.create(TestUtils.USER_NEW1_MAIL, TestUtils.USER_NEW1_NAME, TestUtils.USER_FIRSTNAME, TestUtils.USER_LASTNAME, TestUtils.UNI_1, TestUtils.CAREER_1, TestUtils.IMAGE_1_ID, null, Locale.of(TestUtils.USER_LOCALE), TestUtils.USER_VALID_TOKEN_DEFAULT, TestUtils.USER_EXPIRATION_DEFAULT);
    }
    @Test(expected = NullPointerException.class)
    public void testCreateUserNoLocale(){
        userDao.create(TestUtils.USER_NEW1_MAIL, TestUtils.USER_NEW1_NAME, TestUtils.USER_FIRSTNAME, TestUtils.USER_LASTNAME, TestUtils.UNI_1, TestUtils.CAREER_1, TestUtils.IMAGE_1_ID, TestUtils.USER_PASSWORD, null, TestUtils.USER_VALID_TOKEN_DEFAULT, TestUtils.USER_EXPIRATION_DEFAULT);
    }
    @Test(expected = DataAccessException.class)
    public void testCreateUserInvalidLocale(){
        userDao.create(TestUtils.USER_NEW1_MAIL, TestUtils.USER_NEW1_NAME, TestUtils.USER_FIRSTNAME, TestUtils.USER_LASTNAME, TestUtils.UNI_1, TestUtils.CAREER_1, TestUtils.IMAGE_1_ID, TestUtils.USER_PASSWORD, Locale.of(TestUtils.USER_WRONG_LOCALE), TestUtils.USER_VALID_TOKEN_DEFAULT, TestUtils.USER_EXPIRATION_DEFAULT);
    }
    @Test(expected = DataAccessException.class)
    public void testCreateUserMissingToken(){
        userDao.create(TestUtils.USER_NEW1_MAIL, TestUtils.USER_NEW1_NAME, TestUtils.USER_FIRSTNAME, TestUtils.USER_LASTNAME, TestUtils.UNI_1, TestUtils.CAREER_1, TestUtils.IMAGE_1_ID, TestUtils.USER_PASSWORD, Locale.of(TestUtils.USER_WRONG_LOCALE), null, TestUtils.USER_EXPIRATION_DEFAULT);
    }
    @Test(expected = NullPointerException.class)
    public void testCreateUserMissingExpiration(){
        userDao.create(TestUtils.USER_NEW1_MAIL, TestUtils.USER_NEW1_NAME, TestUtils.USER_FIRSTNAME, TestUtils.USER_LASTNAME, TestUtils.UNI_1, TestUtils.CAREER_1, TestUtils.IMAGE_1_ID, TestUtils.USER_PASSWORD, Locale.of(TestUtils.USER_WRONG_LOCALE), TestUtils.USER_VALID_TOKEN_DEFAULT, null);
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
    // FIXME:
/*
    @Test
    public void testUpdatePassword(){
        userDao.updatePassword(TestUtils.USER_1_MAIL, TestUtils.USER_FAKE_PASSWORD);

        assertEqualsMaybeUser(
            jdbcTemplate.query(TestUtils.USER_SELECT_BY_ID, TestUtils.USER_ROW_MAPPER, TestUtils.USER_1_ID).stream().findFirst(), 
            Map.of("password", TestUtils.USER_FAKE_PASSWORD)
        );
    }
    @Test
    public void testUpdatePasswordMissingUser(){
        userDao.updatePassword(TestUtils.USER_FAKE_MAIL, TestUtils.USER_PASSWORD);
    }
    @Test(expected = DataAccessException.class)
    public void testUpdatePasswordMissingPassword(){
        userDao.updatePassword(TestUtils.USER_1_MAIL, null);

        assertEqualsMaybeUser(jdbcTemplate.query(TestUtils.USER_SELECT_BY_ID, TestUtils.USER_ROW_MAPPER, TestUtils.USER_1_ID).stream().findFirst());
    }
    @Test
    public void testUpdatePasswordEmptyPassword(){
        userDao.updatePassword(TestUtils.USER_1_MAIL, "");

        assertEqualsMaybeUser(jdbcTemplate.query(TestUtils.USER_SELECT_BY_ID, TestUtils.USER_ROW_MAPPER, TestUtils.USER_1_ID).stream().findFirst());
    }
*/
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

        boolean isValid = userDao.existsByTokenNotExpired(TestUtils.USER_VALID_TOKEN_DEFAULT);

        assertTrue(isValid);
    }
    @Test
    public void testExistsByTokenNotExpiredNotInUse(){
        boolean isValid = userDao.existsByTokenNotExpired(TestUtils.USER_VALID_TOKEN_DEFAULT);

        assertFalse(isValid);
    }
    @Test
    public void testExistsByTokenNotExpiredExpired(){
        TestUtils.insertUser(ds, Map.of("username", TestUtils.USER_NEW1_NAME, "email", TestUtils.USER_NEW1_MAIL, "tokenExpiration", LocalDate.now().plusDays(-1)));

        boolean isValid = userDao.existsByTokenNotExpired(TestUtils.USER_VALID_TOKEN_DEFAULT);

        assertFalse(isValid);
    }

    @Test
    public void testExistsByTokenExpired(){
        TestUtils.insertUser(ds, Map.of("username", TestUtils.USER_NEW1_NAME, "email", TestUtils.USER_NEW1_MAIL));

        boolean isExpired = userDao.existsByTokenExpired(TestUtils.USER_VALID_TOKEN_DEFAULT);

        assertFalse(isExpired);
    }
    @Test
    public void testExistsByTokenExpiredExpired(){
        TestUtils.insertUser(ds, Map.of("username", TestUtils.USER_NEW1_NAME, "email", TestUtils.USER_NEW1_MAIL, "tokenExpiration", LocalDate.now().plusDays(-1)));

        boolean isExpired = userDao.existsByTokenExpired(TestUtils.USER_VALID_TOKEN_DEFAULT);

        assertTrue(isExpired);
    }
    @Test
    public void testExistsByTokenExpiredMissing(){
        boolean isExpired = userDao.existsByTokenExpired(TestUtils.USER_VALID_TOKEN_DEFAULT);

        assertFalse(isExpired);
    }
    @Test
    public void testExistsByTokenExpiredEmpty(){
        TestUtils.insertUser(ds, Map.of("username", TestUtils.USER_NEW1_NAME, "email", TestUtils.USER_NEW1_MAIL, "tokenExpiration", LocalDate.now().plusDays(-1)));

        boolean isExpired = userDao.existsByTokenExpired("");

        assertFalse(isExpired);
    }

}


//
//    @Test
//    public void testUpdateCareerName(){
//        userDao.updateCareer(TestUtils.USER_1_ID, CAREER_2.getName());
//
//        assertEqualsMaybeUser(
//            jdbcTemplate.query(TestUtils.USER_SELECT_BY_ID, TestUtils.USER_ROW_MAPPER, TestUtils.USER_1_ID).stream().findFirst(),
//            Map.of("career", CAREER_2)
//        );
//    }
//    @Test(expected = DataAccessException.class)
//    public void testUpdateCareerWrongCareerName(){
//        userDao.updateCareer(TestUtils.USER_1_ID, "12341234");
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
//        userDao.updateCareer(TestUtils.USER_1_ID, CAREER_2.getId());
//
//        assertEqualsMaybeUser(
//            jdbcTemplate.query(TestUtils.USER_SELECT_BY_ID, TestUtils.USER_ROW_MAPPER, TestUtils.USER_1_ID).stream().findFirst(),
//            Map.of("career", CAREER_2)
//        );
//    }
//    @Test(expected = DataAccessException.class)
//    public void testUpdateCareerWrongCareer(){
//        userDao.updateCareer(TestUtils.USER_1_ID, 12341234);
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
    userDao.updateUniversity(TestUtils.USER_1_ID, UNIVERSITY_2.getName());

    assertEqualsMaybeUser(
            jdbcTemplate.query(TestUtils.USER_SELECT_BY_ID, TestUtils.USER_ROW_MAPPER, TestUtils.USER_1_ID).stream().findFirst(),
            Map.of("university", UNIVERSITY_2)
    );
}
@Test(expected = DataAccessException.class)
public void testUpdateUniversityNameWrongUniversity(){
    userDao.updateUniversity(TestUtils.USER_1_ID, "12341234");
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
        userDao.updateUniversity(TestUtils.USER_1_ID, UNIVERSITY_2.getId());

        assertEqualsMaybeUser(
            jdbcTemplate.query(TestUtils.USER_SELECT_BY_ID, TestUtils.USER_ROW_MAPPER, TestUtils.USER_1_ID).stream().findFirst(),
            Map.of("university", UNIVERSITY_2)
        );
    }
    @Test(expected = DataAccessException.class)
    public void testUpdateUniversityWrongUniversity(){
        userDao.updateUniversity(TestUtils.USER_1_ID, 12341234);
    }
    @Test
    public void testUpdateUniversityWrongUser(){
        userDao.updateUniversity(13241234, 12341234);

        assertUserDBDefaultStatus();
    }
*/




//    @Test
//    public void testUpdateProfileInfo(){
//        userDao.updateProfileInfo(TestUtils.USER_1_ID, TestUtils.USER_FAKE_FIRSTNAME, TestUtils.USER_FAKE_LASTNAME, TestUtils.USER_FAKE_NAME);
//
//        assertEqualsMaybeUser(
//            jdbcTemplate.query(TestUtils.USER_SELECT_BY_ID, TestUtils.USER_ROW_MAPPER, TestUtils.USER_1_ID).stream().findFirst(),
//            Map.of("username", TestUtils.USER_FAKE_NAME, "firstname", TestUtils.USER_FAKE_FIRSTNAME, "lastname", TestUtils.USER_FAKE_LASTNAME)
//        );
//    }
//    @Test
//    public void testUpdateProfileInfoUsername(){
//        userDao.updateProfileInfo(TestUtils.USER_1_ID, null, null, TestUtils.USER_FAKE_NAME);
//
//        assertEqualsMaybeUser(
//            jdbcTemplate.query(TestUtils.USER_SELECT_BY_ID, TestUtils.USER_ROW_MAPPER, TestUtils.USER_1_ID).stream().findFirst(),
//            Map.of("username", TestUtils.USER_FAKE_NAME)
//        );
//    }
//    @Test
//    public void testUpdateProfileInfoFirstName(){
//        userDao.updateProfileInfo(TestUtils.USER_1_ID, TestUtils.USER_FAKE_FIRSTNAME, null, null);
//
//        assertEqualsMaybeUser(
//            jdbcTemplate.query(TestUtils.USER_SELECT_BY_ID, TestUtils.USER_ROW_MAPPER, TestUtils.USER_1_ID).stream().findFirst(),
//            Map.of("firstname", TestUtils.USER_FAKE_FIRSTNAME)
//        );
//    }
//    @Test
//    public void testUpdateProfileInfoLastName(){
//        userDao.updateProfileInfo(TestUtils.USER_1_ID, null, TestUtils.USER_FAKE_LASTNAME, null);
//
//        assertEqualsMaybeUser(
//            jdbcTemplate.query(TestUtils.USER_SELECT_BY_ID, TestUtils.USER_ROW_MAPPER, TestUtils.USER_1_ID).stream().findFirst(),
//            Map.of("lastname", TestUtils.USER_FAKE_LASTNAME)
//        );
//    }
//    @Test(expected = DataAccessException.class)
//    public void testUpdateProfileInfoDuplicatedUsername(){
//        final long id = TestUtils.insertUser(ds, Map.of("username", TestUtils.USER_FAKE_NAME));
//
//        userDao.updateProfileInfo(id, TestUtils.USER_FIRSTNAME, TestUtils.USER_LASTNAME, TestUtils.USER_1_NAME);
//    }
//    @Test
//    public void testUpdateProfileInfoNoParams(){
//        userDao.updateProfileInfo(TestUtils.USER_1_ID, null, null, null);
//
//        assertEqualsMaybeUser(jdbcTemplate.query(TestUtils.USER_SELECT_BY_ID, TestUtils.USER_ROW_MAPPER, TestUtils.USER_1_ID).stream().findFirst());
//    }
//    @Test
//    public void testUpdateProfileInfoMissingUser(){
//        userDao.updateProfileInfo(123123, TestUtils.USER_FIRSTNAME, TestUtils.USER_LASTNAME, TestUtils.USER_1_NAME);
//
//        assertUserDBDefaultStatus();
//    }
//
//    @Test
//    public void testUpdateLocale(){
//        userDao.updateLocale(TestUtils.USER_1_ID, Locale.of(TestUtils.USER_FAKE_LOCALE));
//
//        assertEqualsMaybeUser(
//            jdbcTemplate.query(TestUtils.USER_SELECT_BY_ID, TestUtils.USER_ROW_MAPPER, TestUtils.USER_1_ID).stream().findFirst(),
//            Map.of("locale", TestUtils.USER_FAKE_LOCALE)
//        );
//    }
//    @Test(expected = DataAccessException.class)
//    public void testUpdateLocaleWrongLocale(){
//        userDao.updateLocale(TestUtils.USER_1_ID, Locale.of(TestUtils.USER_WRONG_LOCALE));
//    }
//    @Test
//    public void testUpdateLocaleMissingUser(){
//        userDao.updateLocale(1321423, Locale.of(TestUtils.USER_LOCALE));
//
//        assertUserDBDefaultStatus();
//    }

//
//    @Test
//    public void testUpdateProfilePicture(){
//        userDao.updateProfilePicture(TestUtils.USER_1_ID, PROFILEPIC_2.getId());
//
//        TestUtils.assertEqualsUser(
//            jdbcTemplate.queryForObject(TestUtils.USER_SELECT_BY_ID, TestUtils.USER_ROW_MAPPER, TestUtils.USER_1_ID),
//            Map.of("profilepic", PROFILEPIC_2)
//        );
//    }
//    @Test(expected = DataAccessException.class)
//    public void testUpdateProfilePictureWrongPicture(){
//        userDao.updateProfilePicture(TestUtils.USER_1_ID, 123411234);
//    }
//    @Test
//    public void testUpdateProfilePictureWrongUser(){
//        userDao.updateProfilePicture(1234, PROFILEPIC_2.getId());
//
//        assertUserDBDefaultStatus();
//    }

//
//    @Test
//    public void testClearTokenByToken(){
//        long id = TestUtils.insertUser(ds, Map.of("username", TestUtils.USER_NEW1_NAME, "email", TestUtils.USER_NEW1_MAIL, "tokenExpiration", LocalDate.now().plusDays(-1)));
//
//        userDao.clearTokenByToken(TestUtils.USER_VALID_TOKEN_DEFAULT);
//
//        assertTrue(jdbcTemplate.queryForObject(TestUtils.USER_SELECT_TOKEN_BY_ID, String.class, id) == null);
//    }
//    @Test
//    public void testClearTokenByTokenWrongToken(){
//        long id = TestUtils.insertUser(ds, Map.of("username", TestUtils.USER_NEW1_NAME, "email", TestUtils.USER_NEW1_MAIL, "tokenExpiration", LocalDate.now().plusDays(-1)));
//
//        userDao.clearTokenByToken("USER_VALID_TOKEN_DEFAULT");
//
//        assertFalse(jdbcTemplate.queryForObject(TestUtils.USER_SELECT_TOKEN_BY_ID, String.class, id) == null);
//    }


/*

    @Test
    public void testUpdateGeneric(){
        userDao.update(TestUtils.USER_1_ID, TestUtils.USER_FAKE_FIRSTNAME, TestUtils.USER_FAKE_LASTNAME, TestUtils.USER_FAKE_NAME, UNIVERSITY_2.getId(), CAREER_2.getId(), Locale.of(TestUtils.USER_FAKE_LOCALE));

        TestUtils.assertEqualsUser(
            jdbcTemplate.queryForObject(TestUtils.USER_SELECT_BY_ID, TestUtils.USER_ROW_MAPPER, TestUtils.USER_1_ID),
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
        userDao.update(TestUtils.USER_1_ID, TestUtils.USER_FIRSTNAME, null, null, null, null, null);

        TestUtils.assertEqualsUser(TestUtils.USER_1, jdbcTemplate.queryForObject(TestUtils.USER_SELECT_BY_ID, TestUtils.USER_ROW_MAPPER, TestUtils.USER_1_ID));
    }
    @Test
    public void testUpdateLastname(){
        userDao.update(TestUtils.USER_1_ID, null, TestUtils.USER_LASTNAME, null, null, null, null);

        TestUtils.assertEqualsUser(TestUtils.USER_1, jdbcTemplate.queryForObject(TestUtils.USER_SELECT_BY_ID, TestUtils.USER_ROW_MAPPER, TestUtils.USER_1_ID));
    }
    @Test
    public void testUpdateUsername(){
        userDao.update(TestUtils.USER_1_ID, null, null, TestUtils.USER_1_NAME, null, null, null);

        TestUtils.assertEqualsUser(TestUtils.USER_1, jdbcTemplate.queryForObject(TestUtils.USER_SELECT_BY_ID, TestUtils.USER_ROW_MAPPER, TestUtils.USER_1_ID));
    }
    @Test
    public void testUpdateGenericUniversity(){
        userDao.update(TestUtils.USER_1_ID, null, null, null, TestUtils.UNIVERSITY_1_ID, null, null);

        TestUtils.assertEqualsUser(TestUtils.USER_1, jdbcTemplate.queryForObject(TestUtils.USER_SELECT_BY_ID, TestUtils.USER_ROW_MAPPER, TestUtils.USER_1_ID));
    }
    @Test
    public void testUpdateCareerGeneric(){
        userDao.update(TestUtils.USER_1_ID, null, null, null, null, TestUtils.CAREER_1_ID, null);

        TestUtils.assertEqualsUser(TestUtils.USER_1, jdbcTemplate.queryForObject(TestUtils.USER_SELECT_BY_ID, TestUtils.USER_ROW_MAPPER, TestUtils.USER_1_ID));
    }
    @Test
    public void testUpdateLocaleGeneric(){
        userDao.update(TestUtils.USER_1_ID, null, null, null, null, null, Locale.of(TestUtils.USER_LOCALE));

        TestUtils.assertEqualsUser(TestUtils.USER_1, jdbcTemplate.queryForObject(TestUtils.USER_SELECT_BY_ID, TestUtils.USER_ROW_MAPPER, TestUtils.USER_1_ID));
    }
    @Test
    public void testUpdateWrongUser(){
        userDao.update(12341234, TestUtils.USER_FIRSTNAME, TestUtils.USER_LASTNAME, TestUtils.USER_1_NAME, TestUtils.UNIVERSITY_1_ID, TestUtils.CAREER_1_ID, Locale.of(TestUtils.USER_LOCALE));
    }
    @Test
    public void testUpdateNoArguments(){
        userDao.update(TestUtils.USER_1_ID, null, null, null, null, null, null);

        TestUtils.assertEqualsUser(TestUtils.USER_1, jdbcTemplate.queryForObject(TestUtils.USER_SELECT_BY_ID, TestUtils.USER_ROW_MAPPER, TestUtils.USER_1_ID));
    }

 */