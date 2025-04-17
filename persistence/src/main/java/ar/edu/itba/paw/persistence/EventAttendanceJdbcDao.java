package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.EventAttendanceDao;
import ar.edu.itba.paw.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Repository
public class EventAttendanceJdbcDao implements EventAttendanceDao {

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

    private final static String GET_ATTENDEES_QUERY =
            "SELECT \n" +
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
                    "JOIN countries co ON co.id = ci.country_id\n" +
                    "JOIN event_attendances ea ON u.id = ea.user_id WHERE ea.event_id = ? ";


    private static final RowMapper<Event> EVENT_ROW_MAPPER = (rs, rowNum) -> new Event(
            rs.getLong("event_id"), // Event ID from `events` table
            new User(
                    rs.getLong("user_id"),
                    rs.getString("user_email"), // Correct field from `users`
                    rs.getString("user_username"),
                    rs.getString("user_firstname"),
                    rs.getString("user_lastname"),
                    new University(
                            rs.getLong("university_id"),
                            rs.getString("university_name"), // University name
                            rs.getString("university_abbreviation"),
                            new City(
                                    rs.getString("origin_city_name"),
                                    rs.getString("origin_country_name"),
                                    rs.getLong("origin_city_id")
                            )
                    ),
                    new Career(
                            rs.getLong("career_id"),
                            rs.getString("career_name")
                    ),
                    rs.getLong("user_profile_picture_id"),
                    Locale.of(rs.getString("user_language"))
            ),
            rs.getDate("event_date"),
            rs.getString("event_description"),
            rs.getLong( "event_flyer_image_id"),
            new City(
                    rs.getString("city_name"), // Va a tener conflicto con el nombre de la universidad
                    rs.getString("country_name"),
                    rs.getLong("city_id")
            )
    );

    private final static String GET_EVENTS_QUERY = "SELECT \n" +
            "    us.id AS user_id, \n" +
            "    us.email AS user_email, \n" +
            "    us.firstname AS user_firstname, \n" +
            "    us.lastname AS user_lastname, \n" +
            "    us.username AS user_username, \n" +
            "    us.university AS user_university, \n" +
            "    us.profile_picture_id AS user_profile_picture_id, \n" +
            "    us.language AS user_language,\n" +
            "\n" +
            "    ca.id AS career_id, \n" +
            "    ca.name AS career_name, \n" +
            "\n" +
            "    e.id AS event_id, \n" +
            "    e.event_date AS event_date, \n" +
            "    e.description AS event_description, \n" +
            "    e.flyer_image_id AS event_flyer_image_id, \n" +
            "\n" +
            "    un.id AS university_id, \n" +
            "    un.name AS university_name, \n" +
            "    un.abbreviation AS university_abbreviation, \n" +
            "\n" +
            "   c.id AS city_id, \n" +
            "   c.name AS city_name, \n" +
            "\n" +
            "   co.name AS country_name, \n" +
            "\n" +
            "   ci2.id AS origin_city_id, \n" +
            "   ci2.name AS origin_city_name, \n" +
            "\n" +
            "   co2.name AS origin_country_name\n" +
            "FROM events e\n" +
            "JOIN users us ON e.user_id = us.id\n" +
            "JOIN careers ca ON ca.id = us.career_id\n" +
            "JOIN universities un ON us.university = un.id\n" +
            "JOIN cities ci2 ON un.city_id = ci2.id \n" +
            "JOIN countries co2 ON co2.id = ci2.country_id\n" +
            "JOIN cities c ON e.city_id = c.id \n" +
            "JOIN countries co ON c.country_id = co.id \n" +
            "JOIN event_attendances ea ON e.id = ea.event_id WHERE ea.user_id = ?";


    @Autowired
    public EventAttendanceJdbcDao(final DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("event_attendances");
    }

    @Override
    public void attend(long userId, long eventId) {
        Map<String, Object> params = new HashMap<>();
        params.put("user_id", userId);
        params.put("event_id", eventId);
        jdbcInsert.execute(params);
    }

    @Override
    public void cancel(long userId, long eventId) {
        jdbcTemplate.update("DELETE FROM event_attendances WHERE user_id = ? AND event_id = ?", userId, eventId);
    }


    @Override
    public boolean isAttending(long userId, long eventId) {
        return jdbcTemplate.queryForObject(
                "SELECT EXISTS(SELECT 1 FROM event_attendances WHERE user_id = ? AND event_id = ?)",
                Boolean.class, userId, eventId);
    }

    @Override
    public List<User> getAttendees(long eventId) {
        return jdbcTemplate.query(GET_ATTENDEES_QUERY, USER_ROW_MAPPER, eventId);
    }

    @Override
    public int getAttendeesCount(long eventId) {
        return jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM event_attendances WHERE event_id = ?",
                Integer.class, eventId);
    }

    @Override
    public List<Event> getAttendingEvents(long userId) {
        return jdbcTemplate.query(GET_EVENTS_QUERY, EVENT_ROW_MAPPER, userId);
    }
}

/*
    @Override
    public void cancel(User user, Event event) {
        jdbcTemplate.update("DELETE FROM events_attendances WHERE user_id = ? AND event_id = ?",
                user.getId(), event.getId());
    }

    @Override
    public boolean isAttending(User user, Event event) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM events_attendances WHERE user_id = ? AND event_id = ?",
                Integer.class, user.getId(), event.getId());
        return count != null && count > 0;
    }

    @Override
    public List<User> getAttendees(Event event) {
        return jdbcTemplate.query(UserJdbcDao.QUERY +
                " JOIN events_attendances ea ON u.id = ea.user_id WHERE ea.event_id = ?",
                UserJdbcDao.USER_ROW_MAPPER, event.getId());
    }

    @Override
    public int getAttendeesCount(Event event) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM events_attendances WHERE event_id = ?",
                Integer.class, event.getId());
        return count != null ? count : 0;
    }

    @Override
    public List<Event> getAttendingEvents(User user) {
        return jdbcTemplate.query(EventJdbcDao.QUERY +
                " JOIN events_attendances ea ON e.id = ea.event_id WHERE ea.user_id = ?",
                EventJdbcDao.EVENT_ROW_MAPPER, user.getId());
    }
 */
