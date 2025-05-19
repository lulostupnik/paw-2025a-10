//package ar.edu.itba.paw.persistence;
//
//import java.util.HashMap;
//import java.util.Locale;
//import java.util.Map;
//import java.util.Optional;
//import javax.sql.DataSource;
//import ar.edu.itba.paw.models.*;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.jdbc.core.RowMapper;
//import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
//import org.springframework.stereotype.Repository;
//import ar.edu.itba.paw.interfaces.persistence.EventAttendanceDao;
//
//@Repository
//public class EventAttendanceJdbcDao implements EventAttendanceDao {
//    private final static Logger LOGGER = LoggerFactory.getLogger(EventAttendanceJdbcDao.class);
//
//    private final JdbcTemplate jdbcTemplate;
//    private final SimpleJdbcInsert jdbcInsert;
//
//    private final static RowMapper<User> USER_ROW_MAPPER = (rs, rowNum) -> new User(
//            rs.getLong("user_id"),
//            rs.getString("user_email"),
//            rs.getString("user_username"),
//            rs.getString("user_firstname"),
//            rs.getString("user_lastname"),
//            new University(rs.getLong("user_university"), rs.getString("university_name"), rs.getString("university_abbreviation"), new City(rs.getString("city_name"), rs.getString("country_name"), rs.getLong("city_id"))),
//            new Career(rs.getLong("career_id"), rs.getString("career_name")),
//            rs.getLong("user_profile_picture_id"),
//            Locale.of(rs.getString("user_language")),
//            rs.getBoolean("user_blocked"));
//
//
//    private static final RowMapper<Event> EVENT_ROW_MAPPER = (rs, rowNum) -> new Event(
//            rs.getLong("event_id"),
//            new User(
//                    rs.getLong("user_id"),
//                    rs.getString("user_email"),
//                    rs.getString("user_username"),
//                    rs.getString("user_firstname"),
//                    rs.getString("user_lastname"),
//                    new University(
//                            rs.getLong("university_id"),
//                            rs.getString("university_name"),
//                            rs.getString("university_abbreviation"),
//                            new City(
//                                    rs.getString("origin_city_name"),
//                                    rs.getString("origin_country_name"),
//                                    rs.getLong("origin_city_id")
//                            )
//                    ),
//                    new Career(
//                            rs.getLong("career_id"),
//                            rs.getString("career_name")
//                    ),
//                    rs.getLong("user_profile_picture_id"),
//                    Locale.of(rs.getString("user_language")),
//                    rs.getBoolean("user_blocked")
//            ),
//            rs.getDate("event_date").toLocalDate(),
//            rs.getString("event_description"),
//            rs.getLong( "event_flyer_image_id"),
//            new City(
//                    rs.getString("city_name"),
//                    rs.getString("country_name"),
//                    rs.getLong("city_id")
//            ),
//            rs.getString("event_title"),
//            Optional.ofNullable(rs.getTime("event_time") != null ? rs.getTime("event_time").toLocalTime() : null),
//            rs.getString("event_address"),
//            Optional.ofNullable(rs.getInt("event_attendees_limit") == 0 ? null : rs.getInt("event_attendees_limit")),
//            rs.getInt("event_attendees_count")
//    );
//
//    private final static String SQL_EVENTS_BASE =
//                    """
//                    SELECT
//                        us.id AS user_id,
//                        us.email AS user_email,
//                        us.firstname AS user_firstname,
//                        us.lastname AS user_lastname,
//                        us.username AS user_username,
//                        us.university AS user_university,
//                        us.profile_picture_id AS user_profile_picture_id,
//                        us.language AS user_language,
//                        us.blocked AS user_blocked,
//
//                        ca.id AS career_id,
//                        ca.name AS career_name,
//
//                        e.id AS event_id,
//                        e.event_date AS event_date,
//                        e.description AS event_description,
//                        e.flyer_image_id AS event_flyer_image_id,
//                        e.title AS event_title,
//                        e.event_time AS event_time,
//                        e.address AS event_address,
//                        e.attendees_limit AS event_attendees_limit,
//                        e.attendees_count AS event_attendees_count,
//
//                        un.id AS university_id,
//                        un.name AS university_name,
//                        un.abbreviation AS university_abbreviation,
//
//                       c.id AS city_id,
//                       c.name AS city_name,
//
//                       co.name AS country_name,
//
//                       ci2.id AS origin_city_id,
//                       ci2.name AS origin_city_name,
//
//                       co2.name AS origin_country_name
//                    FROM events e
//                    JOIN users us ON e.user_id = us.id
//                    JOIN careers ca ON ca.id = us.career_id
//                    JOIN universities un ON us.university = un.id
//                    JOIN cities ci2 ON un.city_id = ci2.id
//                    JOIN countries co2 ON co2.id = ci2.country_id
//                    JOIN cities c ON e.city_id = c.id
//                    JOIN countries co ON c.country_id = co.id
//                    JOIN event_attendances ea ON e.id = ea.event_id
//                    """;
//
//    private final static String SQL_LIST_ALL_BY_USER = SQL_EVENTS_BASE + " WHERE ea.user_id = ? AND e.user_id != ? AND e.deleted = FALSE ";
//
//
//
//
//    @Autowired
//    public EventAttendanceJdbcDao(final DataSource dataSource) {
//        this.jdbcTemplate = new JdbcTemplate(dataSource);
//        this.jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
//                .withTableName("event_attendances");
//    }
//
//    @Override
//    public void create(final long userId, final long eventId) {
//        final Map<String, Object> params = new HashMap<>();
//        params.put("user_id", userId);
//        params.put("event_id", eventId);
//        jdbcInsert.execute(params);
//
//    }
//
//
//    @Override
//    public void delete(final long userId, final long eventId) {
//        int rowsAffected = jdbcTemplate.update("DELETE FROM event_attendances WHERE user_id = ? AND event_id = ?", userId, eventId);
//        if (rowsAffected > 0){
//            rowsAffected = jdbcTemplate.update("UPDATE events SET attendees_count = attendees_count - 1 WHERE id = ?", eventId);
//            if (rowsAffected == 0) {
//                LOGGER.warn("Event attendance cancel failed: Event with ID {} not found", eventId);
//            }
//        }
//    }
//
//
//    @Override
//    public boolean exists(final long userId, final long eventId) {
//        return jdbcTemplate.queryForObject(
//                "SELECT COUNT(1) FROM event_attendances WHERE user_id = ? AND event_id = ?",
//                Boolean.class, userId, eventId);
//    }
//
//    @Override
//    public int countByEventId(final long eventId) {
//        return jdbcTemplate.query("SELECT attendees_count FROM events WHERE id = ?", (rs, rowNum) -> rs.getInt("attendees_count"), eventId).stream().findFirst().orElse(0);
//    }
//
//
//
//}
