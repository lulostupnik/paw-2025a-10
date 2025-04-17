package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.*;

@Repository
public class UserJdbcDao implements UserDao {

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
            Locale.of(rs.getString("user_language")));

    private final static String QUERY = "SELECT \n" +
            "    u.id AS user_id,\n" +
            "    u.email AS user_email,\n" +
            "    u.firstname AS user_firstname,\n" +
            "    u.lastname AS user_lastname,\n" +
            "    u.username AS user_username,\n" +
            "    u.university AS user_university,\n" +
            "    u.language AS user_language,\n" +
            "    c.name AS career_name,\n" +
            "    c.id AS career_id,\n" +
            "    u.profile_picture_id AS user_profile_picture_id,\n" +
            "    un.name AS university_name,\n" +
            "    un.abbreviation AS university_abbreviation, \n" +
            "    ci.id AS city_id, \n" +
            "    ci.name AS city_name, \n" +
            "    co.name AS country_name\n" +
            "FROM users u\n" +
            "JOIN universities un ON u.university = un.id\n" +
            "JOIN careers c ON c.id = u.career_id\n" +
            "JOIN cities ci ON ci.id = un.city_id\n" +
            "JOIN countries co ON co.id = ci.country_id\n";


    private final static String PASSWORD_QUERY = "SELECT \n" +
            "    u.id AS user_id,\n" +
            "    u.email AS user_email,\n" +
            "    u.firstname AS user_firstname,\n" +
            "    u.lastname AS user_lastname,\n" +
            "    u.username AS user_username,\n" +
            "    u.university AS user_university,\n" +
            "    u.language AS user_language,\n" +
            "    c.name AS career_name,\n" +
            "    c.id AS career_id,\n" +
            "    u.profile_picture_id AS user_profile_picture_id,\n" +
            "    un.name AS university_name,\n" +
            "    un.abbreviation AS university_abbreviation, \n" +
            "    ci.id AS city_id, \n" +
            "    ci.name AS city_name, \n" +
            "    co.name AS country_name,\n" +
            "    u.password AS user_password\n" +
            "FROM users u\n" +
            "JOIN universities un ON u.university = un.id\n" +
            "JOIN careers c ON c.id = u.career_id\n" +
            "JOIN cities ci ON ci.id = un.city_id\n" +
            "JOIN countries co ON co.id = ci.country_id";

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
        return jdbcTemplate.query(QUERY +
                " WHERE u.id = ?", USER_ROW_MAPPER, id).stream().findFirst();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jdbcTemplate.query(QUERY + " WHERE u.email = ?", USER_ROW_MAPPER, email).stream().findFirst();
    }

    @Override
    public Optional<UserPassword> findByEmailWithPass(String email) {
        return jdbcTemplate.query(PASSWORD_QUERY + " WHERE u.email = ?",
                USER_PASSWORD_ROW_MAPPER, email).stream().findFirst();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return jdbcTemplate.query(QUERY + " WHERE u.username = ?", USER_ROW_MAPPER, username).stream().findFirst();
    }

    @Override
    public void changePassword(String email, String password) {
        jdbcTemplate.update("UPDATE users SET password = ? WHERE email = ?", password, email);
    }


    @Override
    public User create(String email, String username, String firstname, String lastname, University university,
                       Career career, long profilePictureId, String password, Locale locale) {
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


}
