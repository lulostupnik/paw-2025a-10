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
    private static Logger LOGGER = LoggerFactory.getLogger(UserJdbcDao.class);

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    private final static RowMapper<User> USER_ROW_MAPPER = (rs, rowNum) -> new User(
            rs.getLong("user_id"),
            rs.getString("user_email"),
            rs.getString("user_username"),
            rs.getString("user_firstname"),
            rs.getString("user_lastname"),
            new University(rs.getLong("user_university"), rs.getString("university_name"), rs.getString("university_abbreviation"), new City(rs.getString("city_name"), rs.getString("country_name"), rs.getLong("city_id"))),
            new Career(rs.getLong("career_id"), rs.getString("career_name")),
            rs.getLong("user_profile_picture_id"),
            Locale.of(rs.getString("user_language")));


    private final static RowMapper<UserPassword> USER_PASSWORD_ROW_MAPPER = (rs, rowNum)-> new UserPassword(
                 rs.getLong("user_id"),
            rs.getString("user_email"),
            rs.getString("user_username"),
            rs.getString("user_firstname"),
            rs.getString("user_lastname"),
            new University(rs.getLong("user_university"), rs.getString("university_name"), rs.getString("university_abbreviation"), new City(rs.getString("city_name"), rs.getString("country_name"), rs.getLong("city_id"))),
            new Career(rs.getLong("career_id"), rs.getString("career_name")),
            rs.getLong("user_profile_picture_id"),
            rs.getString("user_password"),
            Locale.of(rs.getString("user_language")),
            rs.getString("user_role"));

    private final static String SELECT_CLAUSE = """
        SELECT\s
                    u.id AS user_id,\s
                    u.email AS user_email,\s
                    u.firstname AS user_firstname,\s
                    u.lastname AS user_lastname,\s
                    u.username AS user_username,\s
                    u.university AS user_university,\s
                    u.language AS user_language,\s
                    c.name AS career_name,\s
                    c.id AS career_id,\s
                    u.profile_picture_id AS user_profile_picture_id,\s
                    un.name AS university_name,\s
                    un.abbreviation AS university_abbreviation,\s
                    ci.id AS city_id,\s
                    ci.name AS city_name,\s
                    co.name AS country_name\s
""";

    private final static String QUERY = SELECT_CLAUSE + """
            FROM users u\s
            JOIN universities un ON u.university = un.id\s
            JOIN careers c ON c.id = u.career_id\s
            JOIN cities ci ON ci.id = un.city_id\s
            JOIN countries co ON co.id = ci.country_id
            """;

    private final static String QUERY_DISTINCT = """
            SELECT DISTINCT\s
                u.id AS user_id,\s
                u.email AS user_email,\s
                u.firstname AS user_firstname,\s
                u.lastname AS user_lastname,\s
                u.username AS user_username,\s
                u.university AS user_university,\s
                u.language AS user_language,\s
                c.name AS career_name,\s
                c.id AS career_id,\s
                u.profile_picture_id AS user_profile_picture_id,\s
                un.name AS university_name,\s
                un.abbreviation AS university_abbreviation,\s
                ci.id AS city_id,\s
                ci.name AS city_name,\s
                co.name AS country_name\s
            FROM users u\s
            JOIN universities un ON u.university = un.id\s
            JOIN careers c ON c.id = u.career_id\s
            JOIN cities ci ON ci.id = un.city_id\s
            JOIN countries co ON co.id = ci.country_id
            """;


    private final static String PASSWORD_QUERY = SELECT_CLAUSE + """
            , u.password AS user_password\s,
              u.roles AS user_role
            FROM users u\s
            JOIN universities un ON u.university = un.id\s
            JOIN careers c ON c.id = u.career_id\s
            JOIN cities ci ON ci.id = un.city_id\s
            JOIN countries co ON co.id = ci.country_id""";

    private String getPagedQuery(String whereClause, String orderByClause) {
        return  "FROM (SELECT * FROM users u " + whereClause + orderByClause + " LIMIT ? OFFSET ?) " +
                """ 
                AS u
                JOIN universities un ON u.university = un.id
                JOIN careers c ON c.id = u.career_id
                JOIN cities ci ON ci.id = un.city_id
                JOIN countries co ON co.id = ci.country_id
                """;
    }


    @Autowired
    public UserJdbcDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");
    }

    // TODO: methods findById, findByEmail and findByUsername are very similar, we should refactor them
    // -> maybe use enum to specify the column to search by

    @Override
    public Optional<User> findById(long id) {
        LOGGER.debug("Querying DB for user id {}", id);
        return jdbcTemplate.query(QUERY +
                " WHERE u.id = ?", USER_ROW_MAPPER, id).stream().findFirst();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        LOGGER.debug("Querying DB for user mail {}", email);
        return jdbcTemplate.query(QUERY + " WHERE u.email = ?", USER_ROW_MAPPER, email).stream().findFirst();
    }

    @Override
    public Optional<UserPassword> findByEmailWithPass(String email) {
        LOGGER.debug("Querying DB for user mail {} (with password)", email);
        return jdbcTemplate.query(PASSWORD_QUERY + " WHERE u.email = ?",
                USER_PASSWORD_ROW_MAPPER, email).stream().findFirst();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        LOGGER.debug("Querying DB for username {}", username);
        return jdbcTemplate.query(QUERY + " WHERE u.username = ?", USER_ROW_MAPPER, username).stream().findFirst();
    }

    @Override
    public void changePassword(String email, String password) {
        LOGGER.debug("Updating password for user email {} (has password {})", email, password != null && !password.isEmpty());
        if (password == null || password.isEmpty()) return;
        int rows = jdbcTemplate.update("UPDATE users SET password = ? WHERE email = ?", password, email);
        if (rows == 0) {
            LOGGER.warn("Password change failed: User not found");
        }
    }

    @Override
    public boolean existsByUsername(String username) {
        LOGGER.debug("Querying DB for existance of username {}", username);
        return jdbcTemplate.queryForObject("SELECT COUNT(1) FROM users WHERE username = ?", Boolean.class, username);
    }

    @Override
    public boolean existsByEmail(String email) {
        LOGGER.debug("Querying DB for existance of user with email {}", email);
        return jdbcTemplate.queryForObject("SELECT COUNT(1) FROM users WHERE email = ?", Boolean.class, email);
    }


    @Override
    public User create(String email, String username, String firstname, String lastname, University university,
                       Career career, long profilePictureId, String password, Locale locale) {
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
        final Number id = jdbcInsert.executeAndReturnKey(args);
        return new User(id.longValue(), email, username, firstname, lastname, university/*.toString()*/, career, profilePictureId, locale);
    }

    @Override
    public void update(long userId, String firstname, String lastname, String username,
                       Long universityId, Long careerId, Locale locale) {
        LOGGER.debug("Updating user with ID: {}", userId);

        StringBuilder queryBuilder = new StringBuilder("UPDATE users SET ");
        List<Object> parameters = new ArrayList<>();
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

        int rowsAffected = jdbcTemplate.update(queryBuilder.toString(), parameters.toArray());

        if (rowsAffected == 0) {
            LOGGER.warn("User update failed: User with ID {} not found", userId);
        }

    }

    @Override
    public List<User> getAllUsers() {
        return jdbcTemplate.query(QUERY, USER_ROW_MAPPER);
    }

    @Override
    public Page<User> getAllUsers(int page, int size) {
        LOGGER.debug("Querying DB for all users with pagination: page {}, size {}", page, size);
        int offset = (page - 1) * size;
        String whereClause = "";
        String orderByClause = " ORDER BY u.id ASC";
        return new Page<>(jdbcTemplate.query(SELECT_CLAUSE + getPagedQuery(whereClause,orderByClause),USER_ROW_MAPPER,size,offset),page);
    }

    @Override
    public long getAllUsersPageCount(int pageSize) {
        LOGGER.debug("Querying DB for all users page count with page size {}", pageSize);
        long elementCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users", Long.class);
        return (long) Math.ceil((double) elementCount / pageSize);
    }

    @Override
    public Page<User> searchUsers(String search, int page, int size) {
            LOGGER.debug("Querying DB for events with search {}", search);
            int offset = (page - 1) * size;
            String whereClause = " WHERE (LOWER(u.firstname) LIKE LOWER(?))";
            String searchPattern = "%" + search + "%";
            String orderByClause = "ORDER BY u.firstname DESC ";

            return new Page<>(jdbcTemplate.query(
                    SELECT_CLAUSE + getPagedQuery(whereClause, orderByClause),
                    USER_ROW_MAPPER,
                    searchPattern,
                    size,
                    offset
            ), page);
    }

    @Override
    public long searchUsersPageCount(String search, int pageSize) {
        LOGGER.debug("Querying DB for events page count with search {}", search);
        String whereClause = " WHERE (LOWER(u.firstname) LIKE LOWER(?))";
        String searchPattern = "%" + search + "%";
        long elementCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users" + whereClause, Long.class, searchPattern);
        return (long) Math.ceil((double) elementCount / pageSize);
    }

    @Override
    public void updateProfilePicture(long userId, long profilePictureId) {
        LOGGER.debug("Updating profile picture for user ID: {} to image ID: {}", userId, profilePictureId);

        int rowsAffected = jdbcTemplate.update(
                "UPDATE users SET profile_picture_id = ? WHERE id = ?",
                profilePictureId,
                userId
        );

        if (rowsAffected == 0) {
            LOGGER.warn("Profile picture update failed: User with ID {} not found", userId);
        }
    }

    @Override
    public void updateProfileInfo(long userId, String firstname, String lastname, String username) {
        LOGGER.debug("Updating profile info for user ID: {}", userId);

        StringBuilder queryBuilder = new StringBuilder("UPDATE users SET ");
        List<Object> parameters = new ArrayList<>();
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

        int rowsAffected = jdbcTemplate.update(queryBuilder.toString(), parameters.toArray());
        if (rowsAffected == 0) {
            LOGGER.warn("User profile info update failed: User with ID {} not found", userId);
        }
    }

    @Override
    public void updateLocale(long userId, Locale locale) {
        LOGGER.debug("Updating locale for user ID: {} to {}", userId, locale);

        if (locale == null) {
            LOGGER.warn("Locale update skipped: null locale for user ID {}", userId);
            return;
        }

        int rowsAffected = jdbcTemplate.update(
                "UPDATE users SET language = ? WHERE id = ?",
                locale.getLanguage(),
                userId
        );

        if (rowsAffected == 0) {
            LOGGER.warn("Locale update failed: User with ID {} not found", userId);
        }
    }

    @Override
    public void updateUniversity(long userId, long universityId) {
        LOGGER.debug("Updating university for user ID: {} to university ID: {}", userId, universityId);

        int rowsAffected = jdbcTemplate.update(
                "UPDATE users SET university = ? WHERE id = ?",
                universityId,
                userId
        );

        if (rowsAffected == 0) {
            LOGGER.warn("University update failed: User with ID {} not found", userId);
        }
    }

    @Override
    public void updateCareer(long userId, long careerId) {
        LOGGER.debug("Updating career for user ID: {} to career ID: {}", userId, careerId);

        int rowsAffected = jdbcTemplate.update(
                "UPDATE users SET career_id = ? WHERE id = ?",
                careerId,
                userId
        );

        if (rowsAffected == 0) {
            LOGGER.warn("Career update failed: User with ID {} not found", userId);
        }
    }

    @Override
    public List<User> listJourneyRespondersMinusUsers(long journeyId/*, List<Long> userIds*/) {
        String query = QUERY_DISTINCT + "JOIN journey_responses jr ON jr.user_id = u.id WHERE jr.journey_id = ?";
        List<Object> params = new ArrayList<>();
        params.add(journeyId);

        return jdbcTemplate.query(query,USER_ROW_MAPPER, params.toArray());
    }

    @Override
    public List<User> listEventRespondersMinusUsers(long eventId) {
        String query = QUERY_DISTINCT + "JOIN event_responses er ON er.user_id = u.id WHERE er.event_id = ?";
        List<Object> params = new ArrayList<>();
        params.add(eventId);


        return jdbcTemplate.query(query,USER_ROW_MAPPER, params.toArray());
    }

}
