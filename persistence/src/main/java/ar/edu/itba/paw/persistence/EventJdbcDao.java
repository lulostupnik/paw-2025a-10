package ar.edu.itba.paw.persistence;
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

@Repository
public class EventJdbcDao implements EventDao {
    private static Logger LOGGER = LoggerFactory.getLogger(EventJdbcDao.class);

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

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
                    Locale.of(rs.getString("user_language")),
                    rs.getBoolean("user_blocked")
            ),
            rs.getDate("event_date").toLocalDate(),
            rs.getString("event_description"),
            rs.getLong( "event_flyer_image_id"),
            new City(
                    rs.getString("city_name"), // Va a tener conflicto con el nombre de la universidad
                    rs.getString("country_name"),
                    rs.getLong("city_id")),
            rs.getString("event_title"),
            Optional.ofNullable(rs.getTime("event_time") != null ? rs.getTime("event_time").toLocalTime() : null),
            rs.getString("event_address"),
            Optional.ofNullable(rs.getInt("event_attendees_limit") == 0 ? null : rs.getInt("event_attendees_limit")),
            rs.getInt("event_attendees_count")
            
    );

    private static final String SELECT_CLAUSE =
            """
                    SELECT\s
                        us.id AS user_id,\s
                        us.email AS user_email,\s
                        us.firstname AS user_firstname,\s
                        us.lastname AS user_lastname,\s
                        us.username AS user_username,\s
                        us.university AS user_university,\s
                        us.profile_picture_id AS user_profile_picture_id,\s
                        us.language AS user_language,\s
                        us.blocked AS user_blocked,\s
                    
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
                    """;

    private static final String QUERY = SELECT_CLAUSE +
            """
                    FROM events e
                    JOIN users us ON e.user_id = us.id
                    JOIN careers ca ON ca.id = us.career_id
                    JOIN universities un ON us.university = un.id
                    JOIN cities ci2 ON un.city_id = ci2.id 
                    JOIN countries co2 ON co2.id = ci2.country_id
                    JOIN cities c ON e.city_id = c.id
                    JOIN countries co ON c.country_id = co.id
                    """;
    private static final String NOT_DELETED = " WHERE e.deleted = FALSE ";

    private String getPageQuery(String whereClause, String orderByClause) {
        return "FROM (SELECT * FROM events e " + whereClause +" "+ orderByClause + " LIMIT ? OFFSET ?)" +
                """ 
                AS e
                JOIN users us ON e.user_id = us.id
                JOIN careers ca ON ca.id = us.career_id
                JOIN universities un ON us.university = un.id
                JOIN cities ci2 ON un.city_id = ci2.id
                JOIN countries co2 ON co2.id = ci2.country_id
                JOIN cities c ON e.city_id = c.id
                JOIN countries co ON c.country_id = co.id
                """;
    }



    @Autowired
    public EventJdbcDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("events")
                .usingGeneratedKeyColumns("id");
    }

    public EventJdbcDao(JdbcTemplate jdbcTemplate, SimpleJdbcInsert jdbcInsert) {
        this.jdbcTemplate = jdbcTemplate;
        this.jdbcInsert = jdbcInsert;
    }

    @Override
    public Event create(User user, City city, LocalDate date, String description, long flyerImageId, String title, LocalTime time, String address, Integer attendeesLimit) {
        LOGGER.debug("Registering new event for user {} in {} (addr {}) on {} {} ( {} ) with image {}, title {}, limit {}", user, city, address, date, time, description, flyerImageId, title, attendeesLimit);
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("user_id", user.getId());
        parameters.put("city_id", city.getId());
        parameters.put("event_date", date);
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
        parameters.put("deleted", false);  // Establecer el valor de 'deleted' como 'false'
        final Number keys = jdbcInsert.executeAndReturnKey(parameters);
        LOGGER.debug("Successfully registered event {}", keys.longValue());
        return new Event(keys.longValue(), user, date, description, flyerImageId, city, title, Optional.ofNullable(time), address, Optional.ofNullable(attendeesLimit), 0);
    }

    // FIXME
    @Override
    public List<Event> listByQuery(Long cityId, LocalDate date) {
        LOGGER.debug("Querying DB for event");

        StringBuilder sqlBuilder = new StringBuilder(QUERY);
        sqlBuilder.append(NOT_DELETED);
        List<Object> params = new ArrayList<>();
        if (cityId != null) {
            LOGGER.debug("Event condition: in city {}");
            sqlBuilder.append("AND city_id = ?");
            params.add(cityId);
        }

        if (date != null) {
            LOGGER.debug("Event condition: date after {}");
            sqlBuilder.append("AND event_date AFTER ?");
            params.add(date);
        }

        return jdbcTemplate.query(
                sqlBuilder.toString(),
                EVENT_ROW_MAPPER,
                params.toArray()
        );
    }

    @Override
    public Optional<Event> findById(long eventId) {
        LOGGER.debug("Querying DB for event {}", eventId);
        return jdbcTemplate.query(QUERY + "WHERE e.id = ?",
                EVENT_ROW_MAPPER, eventId).stream().findFirst();
    }

    @Override
    public List<Event> listAll() {
        LOGGER.debug("Querying DB for all events");
        return jdbcTemplate.query(QUERY + NOT_DELETED, EVENT_ROW_MAPPER);
    }

    @Override
    public List<Event> getEvents(String email) {
        return jdbcTemplate.query(QUERY + NOT_DELETED + "AND us.email = ?", EVENT_ROW_MAPPER, email);
    }

    @Override
    public List<UserEvent> getRecommendedEvents(String email) {
        LOGGER.debug("Querying DB for recommended events for usermail {}", email);
        return jdbcTemplate.query("""
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
    """,  (rs, rowNum) -> {
            Event event = EVENT_ROW_MAPPER.mapRow(rs, rowNum);
            boolean isAttending = rs.getBoolean("is_attending");
            return new UserEvent(event, isAttending);
        }, email, email);
    }

    public List<Event> getTopEvents(){
        LOGGER.debug("Querying DB for top events");
        //Events happening soon and having many attendees.
        return jdbcTemplate.query("""
        SELECT
            e.id AS event_id, 
            e.event_date AS event_date, 
            e.description AS event_description, 
            e.flyer_image_id AS event_flyer_image_id, 
            e.title AS event_title,\
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
                
            co2.name AS origin_country_name,

            COUNT(ea.user_id) AS attendees

        FROM events e
        JOIN users us ON e.user_id = us.id
        JOIN careers ca ON ca.id = us.career_id
        JOIN universities un ON us.university = un.id
        JOIN cities ci2 ON un.city_id = ci2.id 
        JOIN countries co2 ON co2.id = ci2.country_id
        JOIN cities c ON e.city_id = c.id 
        JOIN countries co ON c.country_id = co.id
        LEFT JOIN event_attendances ea ON ea.event_id = e.id
        WHERE e.event_date >= CURRENT_DATE
        AND e.deleted = FALSE
        GROUP BY(e.id, us.id, ca.id, un.id, c.id, co.name, ci2.id, co2.name)
        ORDER BY(COUNT(ea.user_id), e.event_date) DESC
        LIMIT 3

    """, EVENT_ROW_MAPPER);
    }

    public Optional<Integer> getEventAttendanceLimit(long eventId) {
        LOGGER.debug("Querying DB for attendance limit of event {}", eventId);
        Optional<Event> event = findById(eventId);
        if (event.isPresent()){
            return event.get().getAttendeesLimit();
        }
        return Optional.empty();
    }

    @Override
    public List<Event> getFullEvents() {
        return List.of();
    }

    @Override
    public void delete(long id) {
        final String query = "UPDATE events SET deleted = TRUE WHERE id = ?;";
        int updatedRows = jdbcTemplate.update(query, id);

        if (updatedRows == 0) {
            // Optionally log or throw an exception if no rows were updated
            LOGGER.warn("No journey_response found with id {}", id);
        }
    }

    @Override
    public void deletionMessage(long id, String message) {
        final String query = "UPDATE events SET deleted_message = ? WHERE id = ?;";
        int updatedRows = jdbcTemplate.update(query, message, id);
        if (updatedRows == 0) {
            // Optionally log or throw an exception if no rows were updated
            LOGGER.warn("No journey_response found with id {}", id);
        }

    }

    @Override
    public List<Event> getMyEvents(long userId) {
        LOGGER.debug("Querying DB for events created by user {}", userId);
        return jdbcTemplate.query(QUERY + NOT_DELETED + " AND e.user_id = ? ORDER BY e.event_date DESC",
                EVENT_ROW_MAPPER, userId);
    }


    @Override
    public List<Event> getOthersEvents(long userId) {
        LOGGER.debug("Querying DB for events not created by user {}", userId);
        return jdbcTemplate.query(QUERY + NOT_DELETED + " AND e.user_id != ? AND e.event_date >= CURRENT_DATE ORDER BY e.event_date",
                EVENT_ROW_MAPPER, userId);
    }

    private int calculateTotalPages(int totalItems, int pageSize) {
        return (int) Math.ceil((double) totalItems / pageSize);
    }

    private int getTotalCount(String countQuery, Object... params) {
        return jdbcTemplate.queryForObject(countQuery, Integer.class, params);
    }

    @Override
    public Page<Event> getOthersEvents(long userId, int page, int size) {
        LOGGER.debug("Querying DB for events not created by user {}", userId);

        String countQuery = "SELECT COUNT(*) FROM events e WHERE e.deleted = FALSE " +
                "AND e.user_id != ? AND e.event_date >= CURRENT_DATE";
        int totalItems = getTotalCount(countQuery, userId);
        int totalPages = calculateTotalPages(totalItems, size);

        int offset = (page - 1) * size;
        String whereClause = NOT_DELETED + " AND e.user_id != ? AND e.event_date >= CURRENT_DATE ";
        String orderByClause = "ORDER BY e.event_date DESC ";

        List<Event> events = jdbcTemplate.query(
                SELECT_CLAUSE + getPageQuery(whereClause, orderByClause),
                EVENT_ROW_MAPPER,
                userId, size, offset);

        return new Page<>(events, page, totalPages);
    }

    @Override
    public Page<Event> getMyEvents(long userId, int page, int size) {
        LOGGER.debug("Querying DB for events created by user {}", userId);

        String countQuery = "SELECT COUNT(*) FROM events e WHERE e.deleted = FALSE AND e.user_id = ?";
        int totalItems = getTotalCount(countQuery, userId);
        int totalPages = calculateTotalPages(totalItems, size);

        int offset = (page - 1) * size;
        String whereClause = NOT_DELETED + " AND e.user_id = ? ";
        String orderByClause = "ORDER BY e.event_date DESC ";

        List<Event> events = jdbcTemplate.query(
                SELECT_CLAUSE + getPageQuery(whereClause, orderByClause),
                EVENT_ROW_MAPPER,
                userId, size, offset);

        return new Page<>(events, page, totalPages);
    }

    @Override
    public Page<Event> getEvents(String email, int page, int size) {
        LOGGER.debug("Querying DB for events for usermail {}", email);

        String countQuery = "SELECT COUNT(*) FROM events e JOIN users us ON e.user_id = us.id WHERE e.deleted = FALSE AND us.email = ?";
        int totalItems = getTotalCount(countQuery, email);
        int totalPages = calculateTotalPages(totalItems, size);

        int offset = (page - 1) * size;
        String whereClause = NOT_DELETED + " AND us.email = ? ";
        String orderByClause = "ORDER BY e.event_date DESC ";

        List<Event> events = jdbcTemplate.query(
                SELECT_CLAUSE + getPageQuery(whereClause, orderByClause),
                EVENT_ROW_MAPPER,
                email, size, offset);

        return new Page<>(events, page, totalPages);
    }

    @Override
    public Page<Event> listAll(int page, int size) {
        LOGGER.debug("Querying DB for all events");

        String countQuery = "SELECT COUNT(*) FROM events e WHERE e.deleted = FALSE";
        int totalItems = getTotalCount(countQuery);
        int totalPages = calculateTotalPages(totalItems, size);

        int offset = (page - 1) * size;
        String orderByClause = "ORDER BY e.event_date DESC ";

        List<Event> events = jdbcTemplate.query(
                SELECT_CLAUSE + getPageQuery(NOT_DELETED, orderByClause),
                EVENT_ROW_MAPPER,
                size, offset);

        return new Page<>(events, page, totalPages);
    }

    @Override
    public Page<Event> searchEvents(String search, int page, int size) {
        LOGGER.debug("Querying DB for events with search {}", search);

        String searchPattern = "%" + search + "%";
        String countQuery = "SELECT COUNT(*) FROM events e WHERE e.deleted = FALSE  AND (LOWER(e.title) LIKE LOWER(?))";
        int totalItems = getTotalCount(countQuery, searchPattern);
        int totalPages = calculateTotalPages(totalItems, size);

        int offset = (page - 1) * size;
        String whereClause = NOT_DELETED + " AND (LOWER(e.title) LIKE LOWER(?))";
        String orderByClause = "ORDER BY e.event_date DESC ";

        List<Event> events = jdbcTemplate.query(
                SELECT_CLAUSE + getPageQuery(whereClause, orderByClause),
                EVENT_ROW_MAPPER,
                searchPattern,
                size,
                offset);

        return new Page<>(events, page, totalPages);
    }

    /*
    @Override
    public List<EventWithAttendanceStatus> getEventsWithAttendanceStatus(long userId) {
        LOGGER.debug("Querying DB for events with attendance status for user {} (excluding events created by this user)", userId);

        String sql = QUERY + "LEFT JOIN event_attendances ea ON e.id = ea.event_id AND ea.user_id = ?  WHERE e.user_id != ?  ORDER BY e.event_date DESC";

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> {
                    Event event = EVENT_ROW_MAPPER.mapRow(rs, rowNum);
                    boolean isAttending = rs.getObject("user_id", Long.class) != null;
                    return new EventWithAttendanceStatus(event, isAttending);
                },
                userId, userId
        );
    }
    */

    @Override
    public List<UserEvent> getEventsWithAttendanceStatus(long userId) {
        LOGGER.debug("Querying DB for events with attendance status for user {} (excluding events created by this user)", userId);

        String sql = QUERY.replace("SELECT ", "SELECT (ea.user_id IS NOT NULL) AS is_attending, ") +
                "LEFT JOIN event_attendances ea ON e.id = ea.event_id AND ea.user_id = ?  WHERE e.user_id != ? AND e.deleted = FALSE  ORDER BY e.event_date DESC";

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> {
                    Event event = EVENT_ROW_MAPPER.mapRow(rs, rowNum);
                    boolean isAttending = rs.getBoolean("is_attending");
                    return new UserEvent(event, isAttending);
                },
                userId, userId
        );
    }

@Override
public void updateData(long cityId, LocalDate date, String description, String title, LocalTime time, String address, Integer attendeesLimit, long eventId/*, long userId*/) {
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
            date,
            description,
            title,
            (time != null) ? Time.valueOf(time) : null,
            address,
            attendeesLimit,
            eventId
    );



}




}
