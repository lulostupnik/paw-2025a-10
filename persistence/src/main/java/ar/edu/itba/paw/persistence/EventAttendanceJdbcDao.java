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
    private static Logger LOGGER = LoggerFactory.getLogger(EventAttendanceJdbcDao.class);

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
            """
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
                    FROM users u\s
                    JOIN universities un ON u.university = un.id\s
                    JOIN careers c ON c.id = u.career_id\s
                    JOIN cities ci ON ci.id = un.city_id\s
                    JOIN countries co ON co.id = ci.country_id\s
                    JOIN event_attendances ea ON u.id = ea.user_id WHERE ea.event_id = ?\s""";


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
                    Locale.of(rs.getString("user_language"))
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

    private final static String GET_EVENTS_QUERY =
            """
                    SELECT\s
                        us.id AS user_id,\s
                        us.email AS user_email,\s
                        us.firstname AS user_firstname,\s
                        us.lastname AS user_lastname,\s
                        us.username AS user_username,\s
                        us.university AS user_university,\s
                        us.profile_picture_id AS user_profile_picture_id,\s
                        us.language AS user_language,
                    
                        ca.id AS career_id,\s
                        ca.name AS career_name,\s
                    
                        e.id AS event_id,\s
                        e.event_date AS event_date,\s
                        e.description AS event_description,\s
                        e.flyer_image_id AS event_flyer_image_id,\s
                        e.title AS event_title,\s
                        e.event_time AS event_time,\s
                        e.address AS event_address,\s
                        e.attendees_limit AS event_attendees_limit,\s
                        e.attendees_count AS event_attendees_count,\s
                    
                        un.id AS university_id,\s
                        un.name AS university_name,\s
                        un.abbreviation AS university_abbreviation,\s
                    
                       c.id AS city_id,\s
                       c.name AS city_name,\s
                    
                       co.name AS country_name,\s
                    
                       ci2.id AS origin_city_id,\s
                       ci2.name AS origin_city_name,\s
                    
                       co2.name AS origin_country_name
                    FROM events e
                    JOIN users us ON e.user_id = us.id
                    JOIN careers ca ON ca.id = us.career_id
                    JOIN universities un ON us.university = un.id
                    JOIN cities ci2 ON un.city_id = ci2.id\s
                    JOIN countries co2 ON co2.id = ci2.country_id
                    JOIN cities c ON e.city_id = c.id\s
                    JOIN countries co ON c.country_id = co.id\s
                    JOIN event_attendances ea ON e.id = ea.event_id WHERE ea.user_id = ?""";


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
        LOGGER.debug("Querying DB for user {} attending event {}", userId, eventId);
        return jdbcTemplate.queryForObject(
                "SELECT EXISTS(SELECT 1 FROM event_attendances WHERE user_id = ? AND event_id = ?)",
                Boolean.class, userId, eventId);
    }

    @Override
    public List<User> getAttendees(long eventId) {
        LOGGER.debug("Querying DB for attendees for event {}", eventId);
        return jdbcTemplate.query(GET_ATTENDEES_QUERY, USER_ROW_MAPPER, eventId);
    }

    @Override
    public int getAttendeesCount(long eventId) {
        LOGGER.debug("Querying DB for attendee count for event {}", eventId);
        return jdbcTemplate.query("SELECT attendees_count FROM events WHERE id = ?", (rs, rowNum) -> rs.getInt("attendees_count"), eventId).stream().findFirst().orElse(0);
        // return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM event_attendances WHERE event_id = ?",  Integer.class, eventId);
    }

    @Override
    public List<Event> getAttendingEvents(long userId) {
        LOGGER.debug("Querying DB for events user {} will attend (excluding events created by user)", userId);
        return jdbcTemplate.query(GET_EVENTS_QUERY + " AND e.user_id != ?", EVENT_ROW_MAPPER, userId, userId);
    }

    @Override
    public Page<User> getAttendees(long eventId, int pageNumber, int pageSize) {
        LOGGER.debug("Querying DB for paginated attendees for event {}", eventId);

        String countQuery = "SELECT COUNT(*) FROM event_attendances WHERE event_id = ?";
        int totalItems = jdbcTemplate.queryForObject(countQuery, Integer.class, eventId);
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);

        int offset = (pageNumber - 1) * pageSize;

        String paginatedQuery = GET_ATTENDEES_QUERY + " LIMIT ? OFFSET ?";
        List<User> attendees = jdbcTemplate.query(
                paginatedQuery,
                USER_ROW_MAPPER,
                eventId, pageSize, offset
        );

        return new Page<>(attendees, pageNumber, totalPages);
    }

    @Override
    public Page<Event> getAttendingEvents(long userId, int pageNumber, int pageSize) {
        LOGGER.debug("Querying DB for paginated events user {} will attend", userId);

        String countQuery = "SELECT COUNT(*) FROM event_attendances ea JOIN events e ON ea.event_id = e.id WHERE ea.user_id = ? AND e.user_id != ? AND e.deleted = FALSE";
        int totalItems = jdbcTemplate.queryForObject(countQuery, Integer.class, userId, userId);
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);

        int offset = (pageNumber - 1) * pageSize;

        String paginatedQuery = GET_EVENTS_QUERY + " AND e.user_id != ? AND e.deleted = FALSE ORDER BY e.event_date DESC LIMIT ? OFFSET ?";
        List<Event> events = jdbcTemplate.query(
                paginatedQuery,
                EVENT_ROW_MAPPER,
                userId, userId, pageSize, offset
        );

        return new Page<>(events, pageNumber, totalPages);
    }

}
