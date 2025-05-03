package ar.edu.itba.paw.persistence;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import javax.sql.DataSource;
import ar.edu.itba.paw.models.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ar.edu.itba.paw.interfaces.persistence.EventAttendanceDao;

@Repository
public class EventAttendanceJdbcDao implements EventAttendanceDao {
    private final static Logger LOGGER = LoggerFactory.getLogger(EventAttendanceJdbcDao.class);

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
            Locale.of(rs.getString("user_language")),
            rs.getBoolean("user_blocked"));

    private final static String GET_ATTENDEES_QUERY = """
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
                    FROM users u
                    JOIN universities un ON u.university = un.id
                    JOIN careers c ON c.id = u.career_id
                    JOIN cities ci ON ci.id = un.city_id
                    JOIN countries co ON co.id = ci.country_id
                    JOIN event_attendances ea ON u.id = ea.user_id WHERE ea.event_id = ?
                    """;


    private static final RowMapper<Event> EVENT_ROW_MAPPER = (rs, rowNum) -> new Event(
            rs.getLong("event_id"),
            new User(
                    rs.getLong("user_id"),
                    rs.getString("user_email"),
                    rs.getString("user_username"),
                    rs.getString("user_firstname"),
                    rs.getString("user_lastname"),
                    new University(
                            rs.getLong("university_id"),
                            rs.getString("university_name"),
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
                    Locale.of(rs.getString("user_language")),
                    rs.getBoolean("user_blocked")
            ),
            rs.getDate("event_date").toLocalDate(),
            rs.getString("event_description"),
            rs.getLong( "event_flyer_image_id"),
            new City(
                    rs.getString("city_name"),
                    rs.getString("country_name"),
                    rs.getLong("city_id")
            ),
            rs.getString("event_title"),
            Optional.ofNullable(rs.getTime("event_time") != null ? rs.getTime("event_time").toLocalTime() : null),
            rs.getString("event_address"),
            Optional.ofNullable(rs.getInt("event_attendees_limit") == 0 ? null : rs.getInt("event_attendees_limit")),
            rs.getInt("event_attendees_count")
    );

    private final static String GET_EVENTS_QUERY = """
                    SELECT
                        us.id AS user_id,
                        us.email AS user_email,
                        us.firstname AS user_firstname,
                        us.lastname AS user_lastname,
                        us.username AS user_username,
                        us.university AS user_university,
                        us.profile_picture_id AS user_profile_picture_id,
                        us.language AS user_language,
                        us.blocked AS user_blocked,
                    
                        ca.id AS career_id,
                        ca.name AS career_name,
                    
                        e.id AS event_id,
                        e.event_date AS event_date,
                        e.description AS event_description,
                        e.flyer_image_id AS event_flyer_image_id,
                        e.title AS event_title,
                        e.event_time AS event_time,
                        e.address AS event_address,
                        e.attendees_limit AS event_attendees_limit,
                        e.attendees_count AS event_attendees_count,
                    
                        un.id AS university_id,
                        un.name AS university_name,
                        un.abbreviation AS university_abbreviation,
                    
                       c.id AS city_id,
                       c.name AS city_name,
                    
                       co.name AS country_name,
                    
                       ci2.id AS origin_city_id,
                       ci2.name AS origin_city_name,
                    
                       co2.name AS origin_country_name
                    FROM events e
                    JOIN users us ON e.user_id = us.id
                    JOIN careers ca ON ca.id = us.career_id
                    JOIN universities un ON us.university = un.id
                    JOIN cities ci2 ON un.city_id = ci2.id
                    JOIN countries co2 ON co2.id = ci2.country_id
                    JOIN cities c ON e.city_id = c.id
                    JOIN countries co ON c.country_id = co.id
                    JOIN event_attendances ea ON e.id = ea.event_id WHERE ea.user_id = ?
                    """;


    @Autowired
    public EventAttendanceJdbcDao(final DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("event_attendances");
    }

    @Override
    public void attend(long userId, long eventId) {
        LOGGER.debug("Registering user {} will attend event {}", userId, eventId);
        Map<String, Object> params = new HashMap<>();
        params.put("user_id", userId);
        params.put("event_id", eventId);
        jdbcInsert.execute(params);
        jdbcTemplate.update("UPDATE events SET attendees_count = attendees_count + 1 WHERE id = ?", eventId);
    }

    @Override
    public void cancel(long userId, long eventId) {
        LOGGER.debug("Registering user {} will cancel attendance to event {}", userId, eventId);
        jdbcTemplate.update("DELETE FROM event_attendances WHERE user_id = ? AND event_id = ?", userId, eventId);
        jdbcTemplate.update("UPDATE events SET attendees_count = attendees_count - 1 WHERE id = ?", eventId);
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
        return jdbcTemplate.query("SELECT attendees_count FROM events WHERE id = ?", (rs, rowNum) -> rs.getInt("attendees_count"), eventId).stream().findFirst().orElse(0);
        // return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM event_attendances WHERE event_id = ?",  Integer.class, eventId);
    }

    @Override
    public List<Event> getAttendingEvents(long userId) {
        return jdbcTemplate.query(GET_EVENTS_QUERY + " AND e.user_id != ?", EVENT_ROW_MAPPER, userId, userId);
    }

    @Override
    public Page<User> getAttendees(long eventId, int pageNumber, int pageSize) {
        int totalItems = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM event_attendances WHERE event_id = ?",
                Integer.class,
                eventId
        );

        String paginatedQuery = GET_ATTENDEES_QUERY + " LIMIT ? OFFSET ?";

        return new Page<>(
                jdbcTemplate.query(paginatedQuery, USER_ROW_MAPPER, eventId, pageSize, (pageNumber - 1) * pageSize),
                pageNumber,
                (int) Math.ceil((double) totalItems / pageSize)
        );
    }

    @Override
    public Page<Event> getAttendingEvents(long userId, int pageNumber, int pageSize) {
        int totalItems = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM event_attendances ea JOIN events e ON ea.event_id = e.id WHERE ea.user_id = ? AND e.user_id != ? AND e.deleted = FALSE",
                Integer.class,
                userId, userId
        );
        
        String paginatedQuery = GET_EVENTS_QUERY + " AND e.user_id != ? AND e.deleted = FALSE ORDER BY e.event_date DESC LIMIT ? OFFSET ?";

        return new Page<>(
                jdbcTemplate.query(paginatedQuery, EVENT_ROW_MAPPER, userId, userId, pageSize, (pageNumber - 1) * pageSize),
                pageNumber,
                (int) Math.ceil((double) totalItems / pageSize)
        );
    }

}
