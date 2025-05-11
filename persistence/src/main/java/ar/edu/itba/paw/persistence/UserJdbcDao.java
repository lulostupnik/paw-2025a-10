package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.models.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;
import static ar.edu.itba.paw.persistence.JdbcDaoUtils.*;

@Repository
public class UserJdbcDao implements UserDao {
    private final static Logger LOGGER = LoggerFactory.getLogger(UserJdbcDao.class);

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    private final static RowMapper<User> USER_ROW_MAPPER = (rs, rowNum) -> new User(
            rs.getLong("user_id"),
            rs.getString("user_email"),
            rs.getString("user_username"),
            rs.getString("user_firstname"),
            rs.getString("user_lastname"),
            new University(
                    rs.getLong("user_university"),
                    rs.getString("university_name"),
                    rs.getString("university_abbreviation"),
                    new City(
                            rs.getString("city_name"),
                            rs.getString("country_name"),
                            rs.getLong("city_id")
                    )
            ),
            new Career(
                    rs.getLong("career_id"),
                    rs.getString("career_name")
            ),
            rs.getLong("user_profile_picture_id"),
            Locale.of(rs.getString("user_language")),
            rs.getBoolean("user_blocked")
    );

    private final static RowMapper<UserAuthInfo> USER_PASSWORD_ROW_MAPPER = (rs, rowNum)-> new UserAuthInfo(
            rs.getString("email"),
            rs.getString("password"),
            rs.getString("roles"),
            rs.getBoolean("blocked"),
            rs.getBoolean("verified")
    );


    private final static String SQL_SELECT_BASE =
            """
            SELECT
                u.id AS user_id,
                u.email AS user_email,
                u.firstname AS user_firstname,
                u.lastname AS user_lastname,
                u.username AS user_username,
                u.university AS user_university,
                u.language AS user_language,
                u.blocked AS user_blocked,
                c.name AS career_name,
                c.id AS career_id,
                u.profile_picture_id AS user_profile_picture_id,
                un.name AS university_name,
                un.abbreviation AS university_abbreviation,
                ci.id AS city_id,
                ci.name AS city_name,
                co.name AS country_name
            """;

    private final static String SQL_FROM_BASE =
            """
            FROM users u
            JOIN universities un ON u.university = un.id
            JOIN careers c ON c.id = u.career_id
            JOIN cities ci ON ci.id = un.city_id
            JOIN countries co ON co.id = ci.country_id
            """;

    private final static String SQL_BASE = SQL_SELECT_BASE + SQL_FROM_BASE;
    private final static String SQL_BASE_DISTINCT = "SELECT DISTINCT " + SQL_SELECT_BASE.substring(6) + SQL_FROM_BASE;

    private final static String SQL_FIND_BY_ID = SQL_BASE + " WHERE u.id = ? ";
    private final static String SQL_FIND_BY_TOKEN = SQL_BASE + " WHERE u.token = ? ";
    private final static String SQL_FIND_BY_EMAIL = SQL_BASE + " WHERE u.email = ? ";

    private final static String SQL_JOIN_JOURNEY_RESPONDERS = SQL_BASE_DISTINCT + " JOIN journey_responses jr ON jr.user_id = u.id WHERE jr.journey_id = ? ";
    private final static String SQL_JOIN_EVENT_RESPONDERS = SQL_BASE_DISTINCT + " JOIN event_responses er ON er.user_id = u.id WHERE er.event_id = ? ";

    private final static String SQL_FIND_ALL_PAGED = SQL_BASE + " ORDER BY u.id ASC LIMIT ? OFFSET ?";

    private final static String SQL_SEARCH_WHERE_CLAUSE =
            """
            WHERE LOWER(firstname) LIKE LOWER(?)
                OR LOWER(un.name) LIKE LOWER(?)
                OR LOWER(email) LIKE LOWER(?)
            """;

    private final static String SQL_SEARCH_USERS_PAGED = SQL_BASE + SQL_SEARCH_WHERE_CLAUSE + " ORDER BY u.id DESC LIMIT ? OFFSET ?";

    private final static String SQL_SEARCH_USERS_COUNT =
            "SELECT COUNT(*) FROM users us JOIN universities un ON us.university = un.id " + SQL_SEARCH_WHERE_CLAUSE;


    @Autowired
    public UserJdbcDao(final DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");
    }


    @Override
    public Optional<User> findById(final long id) {
        return jdbcTemplate.query(SQL_FIND_BY_ID, USER_ROW_MAPPER, id).stream().findFirst();
    }

    @Override
    public void updateToken(final long id, final String uid, final LocalDate date) {
        jdbcTemplate.update("UPDATE users SET token = ?, token_expiration = ? WHERE id = ?", uid, date, id);
    }

    @Override
    public boolean isValidByEmail(final String email) {
        return jdbcTemplate.queryForObject("SELECT validated FROM users WHERE email = ? ", Boolean.class, email);
    }

    @Override
    public Optional<User> findByEmail(final String email) {
        return jdbcTemplate.query(SQL_FIND_BY_EMAIL, USER_ROW_MAPPER, email).stream().findFirst();
    }

    @Override
    public Optional<UserAuthInfo> findAuthInfoByEmail(final String email) {
        return jdbcTemplate.query("SELECT email, password, roles, blocked, validated AS verified FROM users WHERE email = ?", USER_PASSWORD_ROW_MAPPER, email).stream().findFirst();
    }

    @Override
    public void updatePassword(final long id, final String password) {
        LOGGER.info("Updating password for user with ID: {} (has password {})", id, password != null && !password.isEmpty()); // todo: no entiendo el has password
        final int updatedRows = jdbcTemplate.update("UPDATE users SET password = ? WHERE id = ?", password, id);
        if (updatedRows == 0) {
            LOGGER.warn("Password change failed: user with ID: {} not found", id);
        }
    }

    @Override
    public void refreshToken(final String newToken, final LocalDate date, final String oldToken){
       jdbcTemplate.update("UPDATE users SET token = ?, token_expiration = ? WHERE token = ?", newToken, date ,oldToken);
    }

    @Override
    public boolean existsByUsername(final String username) {
        return jdbcTemplate.queryForObject("SELECT COUNT(1) FROM users WHERE username = ?", Boolean.class, username);
    }

    @Override
    public boolean existsByEmail(final String email) {
        return jdbcTemplate.queryForObject("SELECT COUNT(1) FROM users WHERE email = ?", Boolean.class, email);
    }


    @Override
    public User create(final String email, final String username, final String firstname, final String lastname, final University university,
                       final Career career, final long profilePictureId, final String password, final Locale locale,final String validateToken, final LocalDate expirationDate) {
        LOGGER.debug("Registering new user to DB");
        final Map<String, Object> args = new HashMap<>();
        args.put("email", email);
        args.put("username", username);
        args.put("firstname", firstname);
        args.put("lastname", lastname);
        args.put("university", university.getId());
        args.put("career_id", career.getId());
        args.put("profile_picture_id", profilePictureId);
        args.put("password", password);
        args.put("language", locale.getLanguage().isEmpty() ? "en":locale.getLanguage());
        args.put("roles", "user");
        args.put("blocked", false);
        args.put("token", validateToken);
        args.put("token_expiration", Date.valueOf(expirationDate));
        args.put("validated",false);
        final Number id = jdbcInsert.executeAndReturnKey(args);
        final User user = new User(id.longValue(), email, username, firstname, lastname, university, career, profilePictureId, locale,false);
        LOGGER.info("Successfully registered new user {}", user);
        return user;
    }

    @Override
    public void updatePasswordByToken(final String token, final String newPassword) {
        jdbcTemplate.update("""
        UPDATE users SET password = ? WHERE token = ?
    """, newPassword, token);
    }


    @Override
    public void update(final long userId, final String firstname, final String lastname, final String username,
                       final Long universityId, final Long careerId, final Locale locale) {
        LOGGER.info("Updating user with ID {}. New information provided: name '{}' '{}', username '{}', uniId {}, careerId {}, locale '{}'", userId, firstname, lastname, username, universityId, careerId, locale);

        final StringBuilder queryBuilder = new StringBuilder("UPDATE users SET ");
        final List<Object> parameters = new ArrayList<>();
        boolean hasUpdates = false;

        if (firstname != null) {
            queryBuilder.append("firstname = ?");
            parameters.add(firstname);
            hasUpdates = true;
        }

        if (lastname != null) {
            if (hasUpdates) queryBuilder.append(", ");
            queryBuilder.append("lastname = ?");
            parameters.add(lastname);
            hasUpdates = true;
        }

        if (username != null) {
            if (hasUpdates) queryBuilder.append(", ");
            queryBuilder.append("username = ?");
            parameters.add(username);
            hasUpdates = true;
        }

        if (universityId != null) {
            if (hasUpdates) queryBuilder.append(", ");
            queryBuilder.append("university = ?");
            parameters.add(universityId);
            hasUpdates = true;
        }

        if (careerId != null) {
            if (hasUpdates) queryBuilder.append(", ");
            queryBuilder.append("career_id = ?");
            parameters.add(careerId);
            hasUpdates = true;
        }

        if (locale != null) {
            if (hasUpdates) queryBuilder.append(", ");
            queryBuilder.append("language = ?");
            parameters.add(locale.getLanguage());
            hasUpdates = true;
        }

        if (!hasUpdates) {
            LOGGER.warn("No updates provided for user with ID: {}", userId);
            return;
        }

        queryBuilder.append(" WHERE id = ?");
        parameters.add(userId);

        final int updatedRows = jdbcTemplate.update(queryBuilder.toString(), parameters.toArray());
        if (updatedRows == 0) {
            LOGGER.warn("User was not updated: user with ID: {} not found", userId);
        }

    }

    @Override
    public Page<User> findAll(final PageParams pageParams) {
        return executePagedQuery(jdbcTemplate, USER_ROW_MAPPER, "SELECT COUNT(*) FROM users", SQL_FIND_ALL_PAGED, pageParams);
    }

    @Override
    public Page<User> search(final String search, final PageParams pageParams) {
        final String searchPattern = likePattern(search);

        return executePagedQuery(
                jdbcTemplate,
                USER_ROW_MAPPER,
                SQL_SEARCH_USERS_COUNT,
                SQL_SEARCH_USERS_PAGED,
                pageParams,
                searchPattern, searchPattern, searchPattern
        );

    }

    @Override
    public boolean isValidated(final String token) {
        String sql = """
        SELECT validated
        FROM users
        WHERE token = ?
        AND token_expiration > NOW()
    """;

        List<Boolean> results = jdbcTemplate.query(sql, (rs, rowNum) -> rs.getBoolean("validated"), token);

        if (results.isEmpty()) {
            return false;
        }

        return Boolean.TRUE.equals(results.get(0));
    }


    @Override
    public boolean isTokenValid(final String token) {
        String sql = """
        SELECT COUNT(*)
        FROM users
        WHERE token = ?
        AND token_expiration > NOW()
        AND validated = TRUE
    """;

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, token);

        return count != null && count > 0;
    }


    @Override
    public void validateToken(final String token) {
        jdbcTemplate.update(
                """
                UPDATE users
                SET token = NULL, token_expiration = NULL
                WHERE token = ?
                """,
                token
        );
    }

    @Override
    public Optional<UserAuthInfo> validateEmail(final String token) {
        Optional<UserAuthInfo> userAuthInfo = jdbcTemplate.query(
                "SELECT email, password, roles, blocked, true AS verified FROM users WHERE token = ?",
                USER_PASSWORD_ROW_MAPPER,
                token
        ).stream().findFirst();
        int updatedRows = jdbcTemplate.update(
                """
                UPDATE users
                SET validated = TRUE, token = NULL, token_expiration = NULL
                WHERE token = ?
                """,
                token
        );
        if(updatedRows == 0) {
            LOGGER.warn("Token validation failed: token {} not found", token);
            throw new RuntimeException();
        }

        LOGGER.debug("UserAuthInfo: {}", userAuthInfo);
        return userAuthInfo;
    }

    @Override
    public boolean hasExpired(final String token) { // todo: business logic? -> por ahí este método debería retornar el LocalDate y que el servicio lo compare
        int count = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM users
                WHERE token = ?
                AND token_expiration < NOW()
                """,
                Integer.class,
                token
        );
        return count > 0;
    }

    @Override
    public void updateProfilePicture(final long userId, final long profilePictureId) {
        LOGGER.debug("Updating profile picture for user ID: {} to image ID: {}", userId, profilePictureId);
        final int rowsAffected = jdbcTemplate.update("UPDATE users SET profile_picture_id = ? WHERE id = ?", profilePictureId, userId);
        if (rowsAffected == 0) {
            LOGGER.warn("User {} not found", userId);
        }
    }


    @Override
    public List<User> findAllJourneyResponders(final long journeyId/*, List<Long> userIds*/) {
        return jdbcTemplate.query(SQL_JOIN_JOURNEY_RESPONDERS, USER_ROW_MAPPER, journeyId);
    }

    @Override
    public List<User> findAllEventResponders(final long eventId) {
        return jdbcTemplate.query(SQL_JOIN_EVENT_RESPONDERS, USER_ROW_MAPPER, eventId);
    }

    @Override
    public void updateBlock(final long userId, final boolean bool){
        LOGGER.info("Blocking user with ID: {}", userId);
        final int rowsAffected = jdbcTemplate.update(
                "UPDATE users SET blocked = ? WHERE id = ?",
                bool,
                userId
        );

        if (rowsAffected == 0) {
            LOGGER.warn("User block failed: User with ID {} not found", userId);
        }
    }

    @Override
    public Optional<User> findByToken(final String token) {
        return jdbcTemplate.query(
                SQL_FIND_BY_TOKEN,
                USER_ROW_MAPPER,
                token
        ).stream().findFirst();
    }

}
