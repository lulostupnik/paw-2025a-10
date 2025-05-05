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
import java.util.*;

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

    private final static RowMapper<UserPassword> USER_PASSWORD_ROW_MAPPER = (rs, rowNum)-> new UserPassword(
            rs.getString("email"),
            rs.getString("password"),
            rs.getString("roles"),
            rs.getBoolean("blocked")
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
    private final static String SQL_FIND_BY_EMAIL = SQL_BASE + " WHERE u.email = ? ";
    private final static String SQL_FIND_BY_USERNAME = SQL_BASE + " WHERE u.username = ? ";

    private final static String SQL_JOIN_JOURNEY_RESPONDERS = SQL_BASE_DISTINCT + " JOIN journey_responses jr ON jr.user_id = u.id WHERE jr.journey_id = ? ";
    private final static String SQL_JOIN_EVENT_RESPONDERS = SQL_BASE_DISTINCT + " JOIN event_responses er ON er.user_id = u.id WHERE er.event_id = ? ";

    private final static String SQL_FIND_ALL_PAGED = SQL_BASE + " ORDER BY u.id ASC LIMIT ? OFFSET ?";

    private final static String SQL_SEARCH_USERS_PAGED = SQL_BASE +
            """
            WHERE LOWER(u.firstname) LIKE LOWER(?)
                OR LOWER(u.lastname) LIKE LOWER(?)
                OR LOWER(u.username) LIKE LOWER(?)
                OR LOWER(u.email) LIKE LOWER(?)
            ORDER BY u.id DESC LIMIT ? OFFSET ?
            """;


    @Autowired
    public UserJdbcDao(final DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");
    }

    // TODO: methods findById, findByEmail and findByUsername are very similar, we should refactor them
    // -> maybe use enum to specify the column to search by
    // private simpleFindBy(String query, RowMapper<User> rowMapper, Object... args);
    // -> de hecho creería que el RowMapper mucho sentido no tiene
    // private Optional<User> simpleFindBy(QUERY, Object... args){
    //      return jdbcTemplate.query(query, args).stream().findFirst();
    // }

    @Override
    public Optional<User> findById(final long id) {
        return jdbcTemplate.query(SQL_FIND_BY_ID, USER_ROW_MAPPER, id).stream().findFirst();
    }

    @Override
    public Optional<User> findByEmail(final String email) {
        return jdbcTemplate.query(SQL_FIND_BY_EMAIL, USER_ROW_MAPPER, email).stream().findFirst();
    }

    @Override
    public Optional<UserPassword> findByEmailWithPass(final String email) {
        return jdbcTemplate.query("SELECT email, password, roles, blocked FROM users WHERE email = ?", USER_PASSWORD_ROW_MAPPER, email).stream().findFirst();
    }

    @Override
    public Optional<User> findByUsername(final String username) {
        return jdbcTemplate.query(SQL_FIND_BY_USERNAME, USER_ROW_MAPPER, username).stream().findFirst();
    }

    @Override
    public void changePassword(final String email, final String password) {
        LOGGER.debug("Updating password for user email {} (has password {})", email, password != null && !password.isEmpty());
        jdbcTemplate.update("UPDATE users SET password = ? WHERE email = ?", password, email);
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
                       final Career career, final long profilePictureId, final String password, final Locale locale) {
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
        args.put("language", locale);
        args.put("roles", "user");
        args.put("blocked", false);
        final Number id = jdbcInsert.executeAndReturnKey(args);
        return new User(id.longValue(), email, username, firstname, lastname, university, career, profilePictureId, locale,false );
    }

    @Override
    public void update(final long userId, final String firstname, final String lastname, final String username,
                       final Long universityId, final Long careerId, final Locale locale) {
        LOGGER.debug("Updating user with ID: {}", userId);

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

        jdbcTemplate.update(queryBuilder.toString(), parameters.toArray());


    }

    @Override
    public List<User> getAllUsers() {
        return jdbcTemplate.query(SQL_BASE, USER_ROW_MAPPER);
    }

    @Override
    public Page<User> getAllUsers(final int page, final int size) {
        final List<User> list = jdbcTemplate.query(SQL_FIND_ALL_PAGED, USER_ROW_MAPPER, size, (page - 1) * size);
        final int elementCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users", Integer.class);
        return new Page<>(list, page, (int) Math.ceil((double) elementCount / size));
    }

    @Override
    public Page<User> searchUsers(final String search, final int page, final int size) {
        final String searchPattern = "%" + search + "%";
        final List<User> list = jdbcTemplate.query(
                SQL_SEARCH_USERS_PAGED,
                USER_ROW_MAPPER,
                searchPattern, searchPattern, searchPattern, searchPattern, size, (page - 1) * size
        );

        final int elementCount = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM users
                WHERE LOWER(firstname) LIKE LOWER(?)
                OR LOWER(lastname) LIKE LOWER(?)
                OR LOWER(username) LIKE LOWER(?)
                OR LOWER(email) LIKE LOWER(?)
                """,
                Integer.class,
                searchPattern, searchPattern, searchPattern, searchPattern
        );

        return new Page<>(
                list,
                page,
                (int) Math.ceil((double) elementCount / size)
        );
    }

    @Override
    public void updateProfilePicture(long userId, long profilePictureId) {
        LOGGER.debug("Updating profile picture for user ID: {} to image ID: {}", userId, profilePictureId);
        jdbcTemplate.update("UPDATE users SET profile_picture_id = ? WHERE id = ?", profilePictureId, userId);

    }

    @Override
    public void updateProfileInfo(final long userId, final String firstname, final String lastname, final String username) {
        LOGGER.debug("Updating profile info for user ID: {}", userId);

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

        if (!hasUpdates) {
            LOGGER.warn("No profile info updates provided for user with ID: {}", userId);
            return;
        }

        queryBuilder.append(" WHERE id = ?");
        parameters.add(userId);

        jdbcTemplate.update(queryBuilder.toString(), parameters.toArray());
        // return update(userId, firstname, lastname, username, null, null, null);
    }

    @Override
    public void updateLocale(final long userId, final Locale locale) {
        LOGGER.debug("Updating locale for user ID: {} to {}", userId, locale);
        jdbcTemplate.update("UPDATE users SET language = ? WHERE id = ?", locale.getLanguage(), userId);
        // return update(userId, null, null, null, null, null, locale);
    }

    @Override
    public void updateUniversity(final long userId, final long universityId) {
        LOGGER.debug("Updating university for user ID: {} to university ID: {}", userId, universityId);
        jdbcTemplate.update("UPDATE users SET university = ? WHERE id = ?", universityId, userId);
        // return update(userId, null, null, null, universityId, null, null);
    }

    @Override
    public void updateCareer(final long userId, final long careerId) {
        LOGGER.debug("Updating career for user ID: {} to career ID: {}", userId, careerId);
        jdbcTemplate.update("UPDATE users SET career_id = ? WHERE id = ?", careerId, userId);
        // return update(userId, null, null, null, null, careerId, null);
    }

    @Override
    public List<User> listJourneyRespondersMinusUsers(final long journeyId/*, List<Long> userIds*/) {
        return jdbcTemplate.query(SQL_JOIN_JOURNEY_RESPONDERS, USER_ROW_MAPPER, journeyId);
    }

    @Override
    public List<User> listEventRespondersMinusUsers(final long eventId) {
        return jdbcTemplate.query(SQL_JOIN_EVENT_RESPONDERS, USER_ROW_MAPPER, eventId);
    }

    @Override
    public void blockUser(final long userId){
        LOGGER.debug("Blocking user with ID: {}", userId);
        final int rowsAffected = jdbcTemplate.update(
                "UPDATE users SET blocked = TRUE WHERE id = ?",
                userId
        );

        if (rowsAffected == 0) {
            LOGGER.warn("User block failed: User with ID {} not found", userId);
        }
    }
    @Override
    public void unblockUser(final long userId){
        LOGGER.debug("Unblocking user with ID: {}", userId);
        final int rowsAffected = jdbcTemplate.update(
                "UPDATE users SET blocked = FALSE WHERE id = ?",
                userId
        );

        if (rowsAffected == 0) {
            LOGGER.warn("User unblock failed: User with ID {} not found", userId);
        }
    }

}
