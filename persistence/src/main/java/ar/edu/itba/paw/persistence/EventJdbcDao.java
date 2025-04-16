package ar.edu.itba.paw.persistence;
import ar.edu.itba.paw.interfaces.persistence.EventDao;
import ar.edu.itba.paw.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.*;

@Repository
public class EventJdbcDao implements EventDao {

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
                    rs.getString("user_password")
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
            "    us.password AS user_password, \n" +
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
        final Map<String, Object> parameters = Map.of(
                "user_id", user.getId(),
                "city_id", city.getId(),
                "event_date", date,
                "description", description,
                "flyer_image_id", flyerImageId
                );
        final Number keys = jdbcInsert.executeAndReturnKey(parameters);
        return new Event(keys.longValue(), user, date, description, flyerImageId, city);
    }

    // FIXME
    @Override
    public List<Event> listByQuery(Long cityId, Date date) {
        StringBuilder sqlBuilder = new StringBuilder(QUERY);
        List<Object> params = new ArrayList<>();
        boolean firstCondition = true;
        if (cityId != null) {
            sqlBuilder.append("WHERE city_id = ?");
            params.add(cityId);
            firstCondition = false;
        }

        if (date != null) {
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
        return jdbcTemplate.query(QUERY + "WHERE e.id = ?",
                EVENT_ROW_MAPPER, eventId).stream().findFirst();
    }

    @Override
    public List<Event> listAll() {
        return jdbcTemplate.query(QUERY, EVENT_ROW_MAPPER);
    }

    @Override
    public List<Event> getRecommendedEvents(String email) {
        return jdbcTemplate.query("""
                WITH user_data AS (
                    SELECT city_id
                    FROM users
                    JOIN universities ON users.university = universities.id
                    JOIN cities ON universities.city_id = id
                    WHERE email = ?
                )
                SELECT *
                FROM events
                JOIN user_data ON user_data.city_id = events.city_id
                WHERE event_date >= CURRENT_DATE
    """, EVENT_ROW_MAPPER, email);
    }
}
