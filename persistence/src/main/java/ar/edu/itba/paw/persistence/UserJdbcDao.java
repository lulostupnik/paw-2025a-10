package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.models.University;
import ar.edu.itba.paw.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.Optional;

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
            new University(rs.getLong("user_university"), rs.getString("university_name"), rs.getString("university_abbreviation")),
            rs.getString("user_career"),
            rs.getLong("user_profile_picture_id")
    );


    @Autowired
    public UserJdbcDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");
    }


    @Override
    public Optional<User> findById(long id) {
        return jdbcTemplate.query("SELECT \n" +
                "    u.id AS user_id,\n" +
                "    u.email AS user_email,\n" +
                "    u.firstname AS user_firstname,\n" +
                "    u.lastname AS user_lastname,\n" +
                "    u.username AS user_username,\n" +
                "    u.university AS user_university,\n" +
                "    u.career AS user_career,\n" +
                "    u.profile_picture_id AS user_profile_picture_id,\n" +
                "    un.name AS university_name,\n" +
                "    un.abbreviation AS university_abbreviation\n" +
                "FROM users u\n" +
                "JOIN university un ON u.university = un.id\n" +
                "WHERE u.id = ?", USER_ROW_MAPPER, id).stream().findFirst();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jdbcTemplate.query("SELECT \n" +
                "    u.id AS user_id,\n" +
                "    u.email AS user_email,\n" +
                "    u.firstname AS user_firstname,\n" +
                "    u.lastname AS user_lastname,\n" +
                "    u.username AS user_username,\n" +
                "    u.university AS user_university,\n" +
                "    u.career AS user_career,\n" +
                "    u.profile_picture_id AS user_profile_picture_id,\n" +
                "    un.name AS university_name,\n" +
                "    un.abbreviation AS university_abbreviation\n" +
                "FROM users u\n" +
                "JOIN university un ON u.university = un.id\n" +
                "WHERE u.email = ?", USER_ROW_MAPPER, email).stream().findFirst();
    }


    @Override
    public User create(String email, String username, String firstname, String lastname, long universityId, String career, long profilePictureId) {
        return null;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return Optional.empty();
    }
}
