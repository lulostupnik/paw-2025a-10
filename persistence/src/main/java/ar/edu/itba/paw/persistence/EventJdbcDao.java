package ar.edu.itba.paw.persistence;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
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

    private static final String QUERY = "SELECT \n" +
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
            "JOIN countries co ON c.country_id = co.id \n";



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
    public Event create(User user, City city, Date date, String description, long flyerImageId) {
        LOGGER.debug("Registering new event for user {} in {} on {} ({}) with image {}", user, city, date, description, flyerImageId);
        final Map<String, Object> parameters = Map.of(
                "user_id", user.getId(),
                "city_id", city.getId(),
                "event_date", date,
                "description", description,
                "flyer_image_id", flyerImageId
                );
        final Number keys = jdbcInsert.executeAndReturnKey(parameters);
        LOGGER.debug("Successfully registered event {}", keys.longValue());
        return new Event(keys.longValue(), user, date, description, flyerImageId, city);
    }

    // FIXME
    @Override
    public List<Event> listByQuery(Long cityId, Date date) {
        LOGGER.debug("Querying DB for event");

        StringBuilder sqlBuilder = new StringBuilder(QUERY);
        List<Object> params = new ArrayList<>();
        boolean firstCondition = true;
        if (cityId != null) {
            LOGGER.debug("Event condition: in city {}");
            sqlBuilder.append("WHERE city_id = ?");
            params.add(cityId);
            firstCondition = false;
        }

        if (date != null) {
            LOGGER.debug("Event condition: date after {}");
            sqlBuilder.append(firstCondition ? " WHERE" : " AND")
                    .append(" event_date AFTER ?");
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
        return jdbcTemplate.query(QUERY, EVENT_ROW_MAPPER);
    }

    @Override
    public List<Event> getRecommendedEvents(String email) {
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
            us.id AS user_id, 
            us.email AS user_email, 
            us.firstname AS user_firstname, 
            us.lastname AS user_lastname, 
            us.username AS user_username, 
            us.university AS user_university, 
            us.profile_picture_id AS user_profile_picture_id, 
            us.language AS user_language,
            
            ca.id AS career_id, 
            ca.name AS career_name, 
            
            e.id AS event_id, 
            e.event_date AS event_date, 
            e.description AS event_description, 
            e.flyer_image_id AS event_flyer_image_id, 
            
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
        WHERE e.event_date >= CURRENT_DATE
    """, EVENT_ROW_MAPPER, email);
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
                
            us.id AS user_id, 
            us.email AS user_email, 
            us.firstname AS user_firstname, 
            us.lastname AS user_lastname, 
            us.username AS user_username, 
            us.university AS user_university, 
            us.profile_picture_id AS user_profile_picture_id, 
            us.language AS user_language,
                
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
        GROUP BY(e.id, us.id, ca.id, un.id, c.id, co.name, ci2.id, co2.name)
        ORDER BY(COUNT(ea.user_id), e.event_date) DESC
        LIMIT 3

    """, EVENT_ROW_MAPPER);
    }

    @Override
    public CursorPage<Event, Long> listAll(Long cursor, int limit) {
        String sql = QUERY + (cursor != null ? " WHERE e.id > ? " :"") + " ORDER BY e.id LIMIT ?";
        List<Event> eventList;
        if(cursor != null) {
            eventList = jdbcTemplate.query(sql, EVENT_ROW_MAPPER, cursor, limit + 1);
        } else {
            eventList = jdbcTemplate.query(sql, EVENT_ROW_MAPPER, limit + 1);
        }
        Long nextCursor = null;
        boolean hasNext = eventList != null && eventList.size() > limit;
        if(hasNext){
            eventList.removeLast();
            nextCursor = eventList.getLast().getId();
        }
        return new CursorPage<>(eventList, nextCursor, hasNext);

    }

    @Override
    public CursorPage<Event, Long> listByCity(City city, Long cursor, int limit) {
        LOGGER.debug("Querying DB for events in city {} with cursor {} and limit {}", city, cursor, limit);

        String sql = QUERY + " WHERE e.city_id = ? " + (cursor != null ? " AND e.id > ? " : "") + " ORDER BY e.id LIMIT ?";

        List<Event> eventList;
        if (cursor != null) {
            eventList = jdbcTemplate.query(sql, EVENT_ROW_MAPPER, city.getId(), cursor, limit + 1);
        } else {
            eventList = jdbcTemplate.query(sql, EVENT_ROW_MAPPER, city.getId(), limit + 1);
        }

        boolean hasNext = eventList != null && eventList.size() > limit;
        Long nextCursor = null;

        if (hasNext) {
            eventList.removeLast();
            nextCursor = eventList.getLast().getId();
        }

        return new CursorPage<>(eventList, nextCursor, hasNext);
    }


}
