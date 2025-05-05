package ar.edu.itba.paw.persistence;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
import ar.edu.itba.paw.interfaces.persistence.EventDao;
import static ar.edu.itba.paw.persistence.JdbcDaoUtils.*;

@Repository
public class EventJdbcDao implements EventDao {
    private final static Logger LOGGER = LoggerFactory.getLogger(EventJdbcDao.class);

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

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
                    rs.getLong("city_id")),
            rs.getString("event_title"),
            Optional.ofNullable(rs.getTime("event_time") != null ? rs.getTime("event_time").toLocalTime() : null),
            rs.getString("event_address"),
            Optional.ofNullable(rs.getInt("event_attendees_limit") == 0 ? null : rs.getInt("event_attendees_limit")),
            rs.getInt("event_attendees_count")
            
    );

    private final static String SQL_ALIASES = """
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
            """;

    private final static String SQL_SELECT_BASE = "SELECT " + SQL_ALIASES ;


    private final static String SQL_FROM_BASE = """
            FROM events e
            JOIN users us ON e.user_id = us.id
            JOIN careers ca ON ca.id = us.career_id
            JOIN universities un ON us.university = un.id
            JOIN cities ci2 ON un.city_id = ci2.id
            JOIN countries co2 ON co2.id = ci2.country_id
            JOIN cities c ON e.city_id = c.id
            JOIN countries co ON c.country_id = co.id
            """;

    private final static String SQL_NOT_DELETED = " WHERE e.deleted = FALSE ";
    private final static String SQL_BASE = SQL_SELECT_BASE + SQL_FROM_BASE; // + SQL_NOT_DELETED;
    private static final String SQL_BASE_NOT_DELETED = SQL_BASE + SQL_NOT_DELETED;

    private final static String SQL_FIND_BY_ID = SQL_BASE_NOT_DELETED + " AND e.id = ? ";
    private final static String SQL_FIND_BY_EMAIL = SQL_BASE_NOT_DELETED + " AND us.email = ?";
    private final static String SQL_FIND_MY_EVENTS = SQL_BASE_NOT_DELETED + " AND e.user_id = ? "; // "ORDER BY e.event_date DESC"
    private final static String SQL_FIND_OTHERS_EVENTS = SQL_BASE_NOT_DELETED + " AND e.user_id != ? AND e.event_date >= CURRENT_DATE ORDER BY e.event_date DESC ";

    private final static String SQL_FIND_ALL_PAGED = SQL_BASE_NOT_DELETED + " ORDER BY e.event_date DESC LIMIT ? OFFSET ? ";
    private final static String SQL_FIND_OTHERS_PAGED = SQL_FIND_OTHERS_EVENTS + " LIMIT ? OFFSET ?";
    private final static String SQL_FIND_ALL_BY_USER_PAGED = SQL_FIND_MY_EVENTS + " ORDER BY e.event_date DESC LIMIT ? OFFSET ? ";
    private final static String SQL_FIND_ALL_BY_EMAIL_PAGED = SQL_FIND_BY_EMAIL + " ORDER BY e.event_date DESC LIMIT ? OFFSET ? ";

    private final static String SQL_SEARCH_PAGED = SQL_BASE_NOT_DELETED + """
                    AND (
                            LOWER(e.title) LIKE LOWER(?)
                            OR LOWER(e.description) LIKE LOWER(?)
                            OR LOWER(c.name) LIKE LOWER(?)
                        )
                    ORDER BY e.event_date DESC LIMIT ? OFFSET ?
                    """;

    private final static String SQL_SELECT_WITH_ATTENDANCE = "SELECT (ea.user_id IS NOT NULL) AS is_attending, " + SQL_ALIASES;
    private final static String SQL_FIND_EVENTS_WITH_ATTENDANCE = SQL_SELECT_WITH_ATTENDANCE + SQL_FROM_BASE + """
                   LEFT JOIN event_attendances ea ON e.id = ea.event_id AND ea.user_id = ?
                   WHERE e.user_id != ? AND e.deleted = FALSE AND e.event_date >= CURRENT_DATE ORDER BY e.event_date DESC
                   """;

    private final static String SQL_FIND_WITH_ATTENDANCE_PAGED = SQL_FIND_EVENTS_WITH_ATTENDANCE + " LIMIT ? OFFSET ?";


    private final static String SQL_RECOMMENDED_EVENTS = """
           WITH user_data AS (
        SELECT u.id, c.id AS city_id
        FROM users u
        JOIN universities un ON u.university = un.id
        JOIN cities c ON un.city_id = c.id
        WHERE u.email = ?
    )
    SELECT
        (ea.user_id IS NOT NULL) AS is_attending,
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
    JOIN user_data ud ON ud.city_id = e.city_id
    LEFT JOIN event_attendances ea ON e.id = ea.event_id AND ea.user_id = ud.id
    WHERE e.event_date >= CURRENT_DATE
    AND us.email != ?
    AND e.deleted = FALSE
    LIMIT ? OFFSET ?
    """;

    private final static String SQL_TOP_EVENTS = """
        WITH event_attendees as (SELECT COUNT(user_id) AS attendees, event_id FROM event_attendances GROUP BY event_id)
        SELECT
            e.id AS event_id,
            e.event_date AS event_date,
            e.description AS event_description,
            e.flyer_image_id AS event_flyer_image_id,
            e.title AS event_title,
            e.event_time AS event_time,
            e.address AS event_address,
            e.attendees_limit AS event_attendees_limit,
            e.attendees_count AS event_attendees_count,
        
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
        LEFT JOIN event_attendees a ON a.event_id = e.id
        WHERE e.event_date >= CURRENT_DATE
        AND e.deleted = FALSE
        ORDER BY COALESCE(a.attendees, 0) DESC, e.event_date
        LIMIT ? OFFSET ?
        """;

    @Autowired
    public EventJdbcDao(final DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("events")
                .usingGeneratedKeyColumns("id");
    }

    @Override
    public Event create(final User user, final City city, final LocalDate date, final String description, final long flyerImageId, final String title, final LocalTime time, final String address, final Integer attendeesLimit) {
        LOGGER.debug("Registering new event for user {} in {} (addr {}) on {} {} ( {} ) with image {}, title {}, limit {}", user, city, address, date, time, description, flyerImageId, title, attendeesLimit);
        final HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("user_id", user.getId());
        parameters.put("city_id", city.getId());
        parameters.put("event_date", Date.valueOf(date));
        parameters.put("description", description);
        parameters.put("flyer_image_id", flyerImageId);
        parameters.put("title", title);
        parameters.put("address", address);
        parameters.put("attendees_count", 0);
        if (time != null) {
            parameters.put("event_time", Time.valueOf(time));
        }
        if (attendeesLimit != null) {
            parameters.put("attendees_limit", attendeesLimit);
        }
        parameters.put("deleted", false);
        final Number keys = jdbcInsert.executeAndReturnKey(parameters);
        LOGGER.debug("Successfully registered event {}", keys.longValue());
        return new Event(keys.longValue(), user, date, description, flyerImageId, city, title, Optional.ofNullable(time), address, Optional.ofNullable(attendeesLimit), 0);
    }

    // FIXME
    @Override
    public List<Event> listByQuery(final Long cityId, final LocalDate date) {

        final StringBuilder sqlBuilder = new StringBuilder(SQL_BASE_NOT_DELETED);
        final List<Object> params = new ArrayList<>();

        if (cityId != null) {
            sqlBuilder.append(" AND city_id = ? ");
            params.add(cityId);
        }

        if (date != null) {
            sqlBuilder.append(" AND event_date >= ? ");
            params.add(Date.valueOf(date));
        }

        return jdbcTemplate.query(
                sqlBuilder.toString(),
                EVENT_ROW_MAPPER,
                params.toArray()
        );
    }

    @Override
    public Optional<Event> findById(final long eventId) {
        return jdbcTemplate.query(SQL_FIND_BY_ID, EVENT_ROW_MAPPER, eventId).stream().findFirst();
    }

    @Override
    public List<Event> listAll() {
        return jdbcTemplate.query(SQL_BASE_NOT_DELETED, EVENT_ROW_MAPPER);
    }

    @Override
    public List<Event> getEvents(final String email) {
        return jdbcTemplate.query(SQL_FIND_BY_EMAIL, EVENT_ROW_MAPPER, email);
    }

//    @Override
//    public List<UserEvent> getRecommendedEvents(final String email) {
//        return jdbcTemplate.query(SQL_RECOMMENDED_EVENTS,  (rs, rowNum) -> {
//            Event event = EVENT_ROW_MAPPER.mapRow(rs, rowNum);
//            boolean isAttending = rs.getBoolean("is_attending");
//            return new UserEvent(event, isAttending);
//        }, email, email);
//    }

    @Override
    public Page<UserEvent> getRecommendedEvents(final String email, final int page, final int size) {
        final int totalItems = jdbcTemplate.queryForObject("""
        SELECT COUNT(*) FROM events e
        JOIN users us ON e.user_id = us.id
        JOIN universities un ON us.university = un.id
        JOIN cities c ON e.city_id = c.id
        WHERE e.event_date >= CURRENT_DATE AND us.email != ? AND e.deleted = FALSE AND c.id = (
            SELECT c.id FROM users u
            JOIN universities un ON u.university = un.id
            JOIN cities c ON un.city_id = c.id
            WHERE u.email = ?
        )
        """, Integer.class, email, email);


        final List<UserEvent> events = jdbcTemplate.query(
                SQL_RECOMMENDED_EVENTS,
                (rs, rowNum) -> {
                    Event event = EVENT_ROW_MAPPER.mapRow(rs, rowNum);
                    boolean isAttending = rs.getBoolean("is_attending");
                    return new UserEvent(event, isAttending);
                },
                email, email, size, (page - 1) * size
        );

        return new Page<>(events, page, (int) Math.ceil((double) totalItems / size));
    }


    @Override
    public Page<Event> getTopEvents(final int page, final int size) {
        final int totalItems = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM events e WHERE e.deleted = FALSE AND e.event_date >= CURRENT_DATE",
                Integer.class
        );

        final List<Event> events = jdbcTemplate.query(SQL_TOP_EVENTS, EVENT_ROW_MAPPER, size, (page - 1) * size);

        return new Page<>(events, page, (int) Math.ceil((double) totalItems / size));
    }


    public Optional<Integer> getEventAttendanceLimit(final long eventId) {
        return jdbcTemplate.query("SELECT attendees_limit FROM events WHERE id = ?", (rs, rowNumber) -> rs.getInt("attendees_limit"), eventId)
                .stream().findFirst();
    }

    @Override
    public List<Event> getFullEvents() {
        // Fixme: ¿NECESITAMOS ESTO?
        return List.of();
    }

    // FIXME: ¿Los siguientes dos métodos no deberían estar en uno solo?
    @Override
    public void delete(final long id) {
        LOGGER.debug("Marking event {} as deleted", id);
        final int updatedRows = jdbcTemplate.update("UPDATE events SET deleted = TRUE WHERE id = ?;", id);
        if (updatedRows == 0) {
            LOGGER.warn("No journey_response found with id {}", id); // TODO: ¿hace falta esto?
        }
    }
    @Override
    public void deletionMessage(final long id, final String message) {
        final int updatedRows = jdbcTemplate.update("UPDATE events SET deleted_message = ? WHERE id = ?;", message, id);
        if (updatedRows == 0) {
            LOGGER.warn("No journey_response found with id {}", id);
        }
    }

    @Override
    public List<Event> getMyEvents(final long userId) {
        return jdbcTemplate.query(SQL_FIND_MY_EVENTS, EVENT_ROW_MAPPER, userId);
    }


    @Override
    public List<Event> getOthersEvents(final long userId) {
        return jdbcTemplate.query(SQL_FIND_OTHERS_EVENTS, EVENT_ROW_MAPPER, userId);
    }

    private int calculateTotalPages(final int totalItems, final int pageSize) {
        return (int) Math.ceil((double) totalItems / pageSize);
    }

    private int getTotalCount(final String countQuery, final Object... params) {
        return jdbcTemplate.queryForObject(countQuery, Integer.class, params);
    }

    @Override
    public Page<Event> getOthersEvents(final long userId, final int page, final int size) {
        final int totalItems = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM events e WHERE e.deleted = FALSE AND e.user_id != ? AND e.event_date >= CURRENT_DATE",
                Integer.class,
                userId
        );

        final List<Event> events = jdbcTemplate.query(SQL_FIND_OTHERS_PAGED, EVENT_ROW_MAPPER, userId, size, (page - 1) * size);

        return new Page<>(events, page, (int) Math.ceil((double) totalItems / size));
    }

    @Override
    public Page<Event> getMyEvents(final long userId, final int page, final int size) {
        final int totalItems = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM events e WHERE e.deleted = FALSE AND e.user_id = ? ",
                Integer.class,
                userId
        );

        final List<Event> events = jdbcTemplate.query(SQL_FIND_ALL_BY_USER_PAGED, EVENT_ROW_MAPPER, userId, size, (page - 1) * size);

        return new Page<>(events, page, (int) Math.ceil((double) totalItems / size));
    }

    //@Todo, habria que meter en el dao una validacion para int page que sea mayor a 0, o dejamos que tire una excepcion?
    @Override
    public Page<Event> getEvents(final String email, final int page, final int size) {
        LOGGER.debug("Querying DB for events for usermail {}", email);

        final String countQuery = "SELECT COUNT(*) FROM events e JOIN users us ON e.user_id = us.id WHERE e.deleted = FALSE AND us.email = ?";
        final int totalItems = getTotalCount(countQuery, email);

        final List<Event> events = jdbcTemplate.query(SQL_FIND_ALL_BY_EMAIL_PAGED, EVENT_ROW_MAPPER, email, size, (page - 1) * size);

        return new Page<>(events, page, (int) Math.ceil((double) totalItems / size));
    }

    @Override
    public Page<Event> listAll(final int page, final int size) {

        final int totalItems = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM events WHERE deleted = FALSE ",
                Integer.class
        );

        final List<Event> events = jdbcTemplate.query(SQL_FIND_ALL_PAGED, EVENT_ROW_MAPPER, size, (page - 1) * size);

        return new Page<>(events, page, (int) Math.ceil((double) totalItems / size));
    }

    @Override
    public Page<Event> searchEvents(final String search, final int page, final int size) {
        final String searchPattern = likePattern(search);
        final int totalItems = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM events e JOIN cities c ON e.city_id = c.id
                WHERE e.deleted = FALSE  AND (
                        LOWER(e.title) LIKE LOWER(?)
                        OR LOWER(e.description) LIKE LOWER(?)
                        OR LOWER(c.name) LIKE LOWER(?)
                    )
                """,
                Integer.class,
                searchPattern, searchPattern, searchPattern
        );

        final List<Event> events = jdbcTemplate.query(
                SQL_SEARCH_PAGED,
                EVENT_ROW_MAPPER,
                searchPattern, searchPattern, searchPattern,
                size,
                (page - 1) * size
        );

        return new Page<>(events, page, (int) Math.ceil((double) totalItems / size));}


    @Override
    public Page<UserEvent> getEventsWithAttendanceStatus(final long userId, final int page, final int size) {

        final int totalItems = jdbcTemplate.queryForObject("""
                   SELECT COUNT(*) FROM events e
                   LEFT JOIN event_attendances ea ON e.id = ea.event_id AND ea.user_id = ?
                   WHERE e.user_id != ? AND e.deleted = FALSE AND e.event_date >= CURRENT_DATE
                   """,
                Integer.class,
                userId, userId
        );

        final List<UserEvent> events = jdbcTemplate.query(
                SQL_FIND_WITH_ATTENDANCE_PAGED,
                (rs, rowNum) -> {
                    Event event = EVENT_ROW_MAPPER.mapRow(rs, rowNum);
                    boolean isAttending = rs.getBoolean("is_attending");
                    return new UserEvent(event, isAttending);
                },
                userId, userId, size, (page - 1) * size
        );

        return new Page<>(events, page, (int) Math.ceil((double) totalItems / size));
    }


    @Override
    public List<UserEvent> getEventsWithAttendanceStatus(final long userId) {

        return jdbcTemplate.query(
                SQL_FIND_EVENTS_WITH_ATTENDANCE,
                (rs, rowNum) -> {
                    Event event = EVENT_ROW_MAPPER.mapRow(rs, rowNum);
                    boolean isAttending = rs.getBoolean("is_attending");
                    return new UserEvent(event, isAttending);
                },
                userId, userId
        );
    }

    @Override
    public void updateData(final long cityId, final LocalDate date, final String description, final String title, final LocalTime time, final String address, final Integer attendeesLimit, final long eventId/*, long userId*/) {
        jdbcTemplate.update("""
            UPDATE events
               SET city_id = ?,
                   event_date = ?,
                   description = ?,
                   title = ?,
                   event_time = ?,
                   address = ?,
                   attendees_limit = ?
             WHERE id = ?
             """,
                cityId,
                Date.valueOf(date),
                description,
                title,
                (time != null) ? Time.valueOf(time) : null,
                address,
                attendeesLimit,
                eventId
        );
    }
}
