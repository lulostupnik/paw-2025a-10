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
import ar.edu.itba.paw.models.enums.SortDirection;
import ar.edu.itba.paw.models.enums.SortFieldEvent;
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
    private final static String SQL_BASE = SQL_SELECT_BASE + SQL_FROM_BASE;
    private final static String SQL_BASE_INTEREST = SQL_BASE + " JOIN user_interest ui ON us.id = ui.user_id " ;
    private static final String SQL_BASE_NOT_DELETED = SQL_BASE + SQL_NOT_DELETED;

    private final static String SQL_FIND_BY_ID = SQL_BASE_NOT_DELETED + " AND e.id = ? ";
    private final static String SQL_FIND_BY_EMAIL = SQL_BASE_NOT_DELETED + " AND us.email = ?";
    private final static String SQL_FIND_ALL_BETWEEN_DATES = SQL_BASE_NOT_DELETED + " AND e.event_date BETWEEN ? AND ? ";

    private final static String SQL_FIND_ALL_PAGED = SQL_BASE_NOT_DELETED + " ORDER BY e.event_date DESC LIMIT ? OFFSET ? ";
    private final static String SQL_FIND_ALL_BY_EMAIL_PAGED = SQL_FIND_BY_EMAIL + " ORDER BY e.event_date DESC LIMIT ? OFFSET ? ";

    private final static String SQL_SEARCH_PAGED = SQL_BASE_NOT_DELETED + """
                    AND (
                            LOWER(e.title) LIKE LOWER(?)
                    --        OR LOWER(e.description) LIKE LOWER(?)
                            OR LOWER(c.name) LIKE LOWER(?)
                            OR LOWER(us.username) LIKE LOWER(?)
                        )
                    ORDER BY e.event_date DESC LIMIT ? OFFSET ?
                    """;

    private final static String SQL_SELECT_WITH_ATTENDANCE = "SELECT (ea.user_id IS NOT NULL) AS is_attending, " + SQL_ALIASES;
    private final static String SQL_FIND_EVENTS_WITH_ATTENDANCE = SQL_SELECT_WITH_ATTENDANCE + SQL_FROM_BASE + """
                   LEFT JOIN event_attendances ea ON e.id = ea.event_id AND ea.user_id = ?
                   WHERE e.user_id != ? AND e.deleted = FALSE AND e.event_date >= CURRENT_DATE
                   """;

    private final static String SQL_SEARCH_EVENTS_WITH_ATTENDANCE = SQL_FIND_EVENTS_WITH_ATTENDANCE + """
                   AND (
                            LOWER(e.title) LIKE LOWER(?)
                    --        OR LOWER(e.description) LIKE LOWER(?)
                            OR LOWER(c.name) LIKE LOWER(?)
                            OR LOWER(us.username) LIKE LOWER(?)
                        )
                   """;

    private final static String SQL_SELECT_WITH_USER_INFO = "SELECT (ea.user_id IS NOT NULL) AS is_attending, (e.user_id = ?) AS is_owner, " + SQL_ALIASES;

    private final static String SQL_FIND_EVENT_WITH_USER_INFO = SQL_SELECT_WITH_USER_INFO + SQL_FROM_BASE + """
                   LEFT JOIN event_attendances ea ON e.id = ea.event_id AND ea.user_id = ?
                   WHERE e.id = ?
                   """;

    private final static String SQL_SEARCH_WHERE_CLAUSE =
            """
            (
            LOWER(e.title) LIKE LOWER(?)
    OR LOWER(ci2.name) LIKE LOWER(?)
    OR LOWER(us.username) LIKE LOWER(?)
    )
    """;

    private final static String SQL_RECOMMENDED_EVENTS = """
        WITH user_data AS (
        SELECT u.id, c.id AS city_id
        FROM users u
        JOIN universities un ON u.university = un.id
        JOIN cities c ON un.city_id = c.id
        WHERE u.id = ?
    )
    SELECT
        (ea.user_id IS NOT NULL) AS is_attending,
        (e.user_id = ?) AS is_owner,
  """ + SQL_ALIASES + """
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
    AND us.id != ?
    AND e.deleted = FALSE
    ORDER BY
        (e.attendees_limit IS NOT NULL AND e.attendees_count >= e.attendees_limit) ASC,
        is_attending ASC,
        is_owner ASC,
        COALESCE(e.attendees_count, 0) DESC,
        e.event_date
    LIMIT ? OFFSET ?
    """;

    private final static String SQL_TOP_EVENTS = SQL_SELECT_BASE +
            """
                FROM events e
                JOIN users us ON e.user_id = us.id
                JOIN careers ca ON ca.id = us.career_id
                JOIN universities un ON us.university = un.id
                JOIN cities ci2 ON un.city_id = ci2.id
                JOIN countries co2 ON co2.id = ci2.country_id
                JOIN cities c ON e.city_id = c.id
                JOIN countries co ON c.country_id = co.id
                WHERE e.event_date >= CURRENT_DATE
                AND e.deleted = FALSE
                ORDER BY (e.attendees_limit IS NOT NULL AND e.attendees_count >= e.attendees_limit) ASC, COALESCE(e.attendees_count, 0) DESC, e.event_date
                LIMIT ? OFFSET ?
        """;
    private final static String SQL_TOP_EVENTS_LOGGED_USER = SQL_SELECT_BASE +
            """
                ,(ea.user_id IS NOT NULL) AS is_attending
                ,(e.user_id = ?) AS is_owner
                FROM events e
                JOIN users us ON e.user_id = us.id
                JOIN careers ca ON ca.id = us.career_id
                JOIN universities un ON us.university = un.id
                JOIN cities ci2 ON un.city_id = ci2.id
                JOIN countries co2 ON co2.id = ci2.country_id
                JOIN cities c ON e.city_id = c.id
                JOIN countries co ON c.country_id = co.id
                LEFT JOIN event_attendances ea ON ea.event_id = e.id AND ea.user_id = ?
                WHERE e.event_date >= CURRENT_DATE
                AND us.id != ?
                AND e.deleted = FALSE
                ORDER BY
                 (e.attendees_limit IS NOT NULL AND e.attendees_count >= e.attendees_limit) ASC,
                  is_attending ASC,
                  COALESCE(e.attendees_count, 0) DESC,
                  e.event_date
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
        return new Event(keys.longValue(), user, date, description, flyerImageId, city, title, Optional.ofNullable(time), address, Optional.ofNullable(attendeesLimit), 0);
    }


    @Override
    public Optional<Event> findById(final long eventId) {
        return jdbcTemplate.query(SQL_FIND_BY_ID, EVENT_ROW_MAPPER, eventId).stream().findFirst();
    }

    @Override
    public Optional<Integer> findAttendanceLimitById(final long eventId) {
        //Using optional and query for object because a rowmapper returns 0 on null integer instead of null
        return Optional.ofNullable(jdbcTemplate.queryForObject("SELECT attendees_limit FROM events WHERE id = ?", Integer.class, eventId));
    }

    @Override
    public List<Event> findAllBetweenDates(final LocalDate startDate, final LocalDate endDate) {
        return jdbcTemplate.query(SQL_FIND_ALL_BETWEEN_DATES, EVENT_ROW_MAPPER, startDate, endDate);
    }

    @Override
    public int countEventsCreatedByUser(final long userId) {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM events WHERE user_id = ? AND deleted = FALSE", Integer.class, userId);
    }

    @Override
    public int countEventsAttendedByUser(final long userId) {
        return jdbcTemplate.queryForObject("""
                        SELECT COUNT(*) FROM event_attendances ea
                        JOIN events e ON ea.event_id = e.id
                        WHERE ea.user_id = ? AND e.user_id != ? AND e.deleted = FALSE
                        """,
                Integer.class,
                userId, userId
        );

    }

    @Override
    public Optional<CountryAttendeeCount> findTopAttendeeCountry(final long eventId) {
        return jdbcTemplate.query(
                """
                SELECT co.name as country_name, COUNT(*) as attendee_count
                FROM event_attendances ea
                JOIN users u ON ea.user_id = u.id
                JOIN universities uni ON u.university = uni.id
                JOIN cities ci ON uni.city_id = ci.id
                JOIN countries co ON ci.country_id = co.id
                WHERE ea.event_id = ?
                GROUP BY co.name
                ORDER BY attendee_count DESC
                LIMIT 1
                """,
                (rs, rowNum) -> new CountryAttendeeCount(
                        rs.getString("country_name"), rs.getInt("attendee_count")
                ),
                eventId
        ).stream().findFirst();
    }

    @Override
    public Optional<EventWithStatistics> findEventWithStatistics(final Long userId, final long eventId) {
        Optional<EventWithUserInfo> maybeEvent;
        if(userId == null){
            maybeEvent = jdbcTemplate.query(
                    SQL_FIND_BY_ID,
                    (rs, rowNum) -> new EventWithUserInfo(
                            EVENT_ROW_MAPPER.mapRow(rs, rowNum),
                            false,
                            false
                    ),
                    eventId
            ).stream().findFirst();
        } else {
             maybeEvent = jdbcTemplate.query(
                    SQL_FIND_EVENT_WITH_USER_INFO,
                    (rs, rowNum) -> new EventWithUserInfo(
                            EVENT_ROW_MAPPER.mapRow(rs, rowNum),
                            rs.getBoolean("is_attending"),
                            rs.getBoolean("is_owner")
                    ),
                    userId, userId, eventId
            ).stream().findFirst();
        }
        if (maybeEvent.isEmpty()) {
            return Optional.empty();
        }

        final long creatorId = maybeEvent.get().getEvent().getUser().getId();

        final int creatorEventCount = countEventsCreatedByUser(creatorId);

        final int creatorAttendanceCount = countEventsAttendedByUser(creatorId);

        Optional<CountryAttendeeCount> maybeTopCountry = findTopAttendeeCountry(eventId);

        String topAttendeeCountry = null;
        int topAttendeeCountryCount = 0;

        if (maybeTopCountry.isPresent()) {
            topAttendeeCountry = maybeTopCountry.get().getCountryName();
            topAttendeeCountryCount = maybeTopCountry.get().getCount();
        }

        EventWithStatistics eventStatistics = new EventWithStatistics(
                maybeEvent.get().getEvent(),
                creatorEventCount,
                creatorAttendanceCount,
                topAttendeeCountry,
                topAttendeeCountryCount,
                maybeEvent.get().isAttending(),
                maybeEvent.get().isCreator()
        );
        return Optional.of(eventStatistics);
    }
    

    @Override
    public void delete(final long id) {
        final int updatedRows = jdbcTemplate.update("UPDATE events SET deleted = TRUE WHERE id = ?;", id);
        if (updatedRows == 0) {
            LOGGER.warn("Deletion failed: no event found with id {}", id);
        }
    }
    @Override
    public void updateDeletionMessage(final long id, final String message) {
        final int updatedRows = jdbcTemplate.update("UPDATE events SET deleted_message = ? WHERE id = ?;", message, id);
        if (updatedRows == 0) {
            LOGGER.warn("Deletion message failed: no event found with id {}", id);
        }
    }

    @Override
    public Page<Event> findByUserEmail(final String email, final PageParams pageParams) {
        return executePagedQuery(
                jdbcTemplate, EVENT_ROW_MAPPER,
                "SELECT COUNT(*) FROM events e JOIN users us ON e.user_id = us.id WHERE e.deleted = FALSE AND us.email = ?",
                SQL_FIND_ALL_BY_EMAIL_PAGED,
                pageParams, email
        );
    }

    @Override
    public Page<Event> findAll(final PageParams pageParams) {
        return executePagedQuery(
                jdbcTemplate, EVENT_ROW_MAPPER,
                "SELECT COUNT(*) FROM events WHERE deleted = FALSE", SQL_FIND_ALL_PAGED,
                pageParams
        );

    }

    @Override
    public Page<Event> search(final String searchTerm, final PageParams pageParams) {
        final String searchPattern = likePattern(searchTerm);

        return executePagedQuery(
                jdbcTemplate, EVENT_ROW_MAPPER, """
                SELECT COUNT(*)
                FROM events e
                JOIN cities c ON e.city_id = c.id
                JOIN users us ON e.user_id = us.id
                WHERE e.deleted = FALSE  AND (
                        LOWER(e.title) LIKE LOWER(?)
                        OR LOWER(c.name) LIKE LOWER(?)
                        OR LOWER(us.username) LIKE LOWER(?)
                    )
                """, SQL_SEARCH_PAGED, pageParams, searchPattern, searchPattern, searchPattern
        );

    }


    @Override
    public Page<Event> findAllWithFilters(final Long userId, final String search,
                                          final SortFieldEvent sortBy, final SortDirection direction, final String destination,
                                          final LocalDate startDate, final LocalDate endDate, final String interest,
                                          final boolean isPast, final boolean isUpcoming, final boolean attending, final PageParams pageParams){
        final String searchPattern = likePattern(search);

        final List<String> filters = new ArrayList<>();
        final List<Object> params = new ArrayList<>();

        final StringBuilder countQueryBuilder = new StringBuilder("SELECT COUNT(*) FROM events e");
        final StringBuilder queryBuilder = new StringBuilder((interest != null && !interest.isEmpty()) ? SQL_BASE_INTEREST : SQL_BASE);


        if (interest != null && ! interest.isEmpty()) {
            countQueryBuilder.append(" JOIN users us ON e.user_id = us.id JOIN user_interest ui ON us.id = ui.user_id JOIN category c ON c.id = ui.category_id ");
            filters.add("c.name = ?");
            params.add(interest);
        }

        if (destination != null && !destination.isEmpty()) {
            countQueryBuilder.append(" JOIN cities ci2 ON e.city_id = ci2.id ");
            filters.add("ci2.name = ?");
            params.add(destination);
        }

        if(userId != null) {
            filters.add("e.user_id != ?");
            params.add(userId);
        }


        if (endDate != null) {
            filters.add("e.event_date <= ?");
            params.add(Date.valueOf(endDate));
        }

        if (startDate != null) {
            filters.add("e.event_date >= ?");
            params.add(Date.valueOf(startDate));
        }

        if(search != null && !search.isEmpty()) {
            if(destination == null || destination.isEmpty()) {
                countQueryBuilder.append(" JOIN cities ci2 ON e.city_id = ci2.id ");
            }
            if(interest == null || interest.isEmpty()) {
                countQueryBuilder.append(" JOIN users us ON e.user_id = us.id ");
            }
            filters.add(SQL_SEARCH_WHERE_CLAUSE);
            params.add(searchPattern);
            params.add(searchPattern);
            params.add(searchPattern);
        }

        if(attending && userId != null) {
            queryBuilder.append(" LEFT JOIN event_attendances ea ON e.id = ea.event_id AND ea.user_id = ");
            queryBuilder.append( userId );
            countQueryBuilder.append(" LEFT JOIN event_attendances ea ON e.id = ea.event_id AND ea.user_id = " );
            countQueryBuilder.append( userId );
            filters.add("ea.user_id = ?");
            params.add(userId);
        } else
        if (isPast) {
            filters.add("e.event_date < CURRENT_DATE");
        } else if (isUpcoming) {
            filters.add("e.event_date >= CURRENT_DATE");
        }


        countQueryBuilder.append(" WHERE e.deleted = FALSE ");
        queryBuilder.append(" WHERE e.deleted = FALSE ");

        if(!filters.isEmpty()) {
            countQueryBuilder.append(" AND  ").append(String.join(" AND ", filters));
            queryBuilder.append(" AND ").append(String.join(" AND ", filters));
        }

        if (sortBy != null) {
            String sortByStr = "e.id";
            if(sortBy.equals(SortFieldEvent.ATTENDEES)){
                sortByStr= "e.attendees_count";
            }
            if(sortBy.equals(SortFieldEvent.DATE)){
                sortByStr= "e.event_date";
            }

            queryBuilder.append(" ORDER BY ").append(sortByStr);

            if (direction != null && direction.equals(SortDirection.DESC)) {
                queryBuilder.append(" DESC");
            } else {
                queryBuilder.append(" ASC");
            }
        } else {
            queryBuilder.append(" ORDER BY e.id");
        }

        final int totalItems = jdbcTemplate.queryForObject(countQueryBuilder.toString(), Integer.class, params.toArray());

        queryBuilder.append(" LIMIT ? OFFSET ? ");

        params.add(pageParams.getSize());
        params.add(offset(pageParams));

        List<Event> events = jdbcTemplate.query(queryBuilder.toString(), EVENT_ROW_MAPPER, params.toArray());
        return new Page<>(events, pageParams.getPage(), pageCount(totalItems, pageParams.getSize()));

    }



    @Override
    public void update(final long cityId, final LocalDate date, final String description, final String title, final LocalTime time, final String address, final Integer attendeesLimit, final long eventId, final long flyerImageId) {
        final int rowsAffected = jdbcTemplate.update("""
            UPDATE events
               SET city_id = ?,
                   event_date = ?,
                   description = ?,
                   title = ?,
                   event_time = ?,
                   address = ?,
                   attendees_limit = ?,
                   flyer_image_id = ?
             WHERE id = ?
            """,
                cityId,
                Date.valueOf(date),
                description,
                title,
                (time != null) ? Time.valueOf(time) : null,
                address,
                attendeesLimit,
                flyerImageId, 
                eventId
        );
        if (rowsAffected == 0) {
            LOGGER.warn("Event update failed: Event with ID {} not found", eventId);
        }
    }


    @Override
    public Page<Event> findRecommended(final long userId, final PageParams pageParams) {

        final int totalItems = jdbcTemplate.queryForObject("""
        SELECT COUNT(*) FROM events e
        JOIN users us ON e.user_id = us.id
        JOIN universities un ON us.university = un.id
        JOIN cities c ON e.city_id = c.id
        WHERE e.event_date >= CURRENT_DATE AND us.id != ? AND e.deleted = FALSE AND c.id = (
            SELECT c.id FROM users u
            JOIN universities un ON u.university = un.id
            JOIN cities c ON un.city_id = c.id
            WHERE u.id = ?
        )
        """, Integer.class, userId, userId);

        final List<Event> events = jdbcTemplate.query(
                SQL_RECOMMENDED_EVENTS,
                EVENT_ROW_MAPPER,
                userId, userId, userId, pageParams.getSize(), offset(pageParams)
        );


        return new Page<>(events, pageParams.getPage(), pageCount(totalItems, pageParams.getSize()));
    }


    @Override
    public Page<Event> findTop(final PageParams pageParams) {
        return executePagedQuery(
                jdbcTemplate, EVENT_ROW_MAPPER,
                "SELECT COUNT(*) FROM events e WHERE e.deleted = FALSE AND e.event_date >= CURRENT_DATE",
                SQL_TOP_EVENTS,
                pageParams
        );

    }


    @Override
    public Page<Event> findTopByUser(final long userId, final PageParams pageParams) {

        final int totalItems = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM events e WHERE e.deleted = FALSE AND e.event_date >= CURRENT_DATE AND e.user_id != ?",
                Integer.class,
                userId
        );

        final List<Event> events = jdbcTemplate.query(SQL_TOP_EVENTS_LOGGED_USER, EVENT_ROW_MAPPER,
                userId, userId, userId, pageParams.getSize(), offset(pageParams)
        );
        return new Page<>(events, pageParams.getPage(), pageCount(totalItems, pageParams.getSize()));
    }

}