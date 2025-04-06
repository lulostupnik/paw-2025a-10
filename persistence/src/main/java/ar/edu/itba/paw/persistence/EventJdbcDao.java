package ar.edu.itba.paw.persistence;
import ar.edu.itba.paw.interfaces.persistence.EventDao;
import ar.edu.itba.paw.models.City;
import ar.edu.itba.paw.models.Event;
import ar.edu.itba.paw.models.University;
import ar.edu.itba.paw.models.User;
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
                            rs.getString("university_abbreviation")
                    ),
                    rs.getString("user_career"),
                    rs.getLong("user_profile_picture_id")
            ),
            rs.getDate("event_date"),
            rs.getString("event_description"),
            rs.getLong( "event_flyer_image_id"),
            new City(
                    rs.getString("city_name"), // Va a tener conflicto con el nombre de la universidad
                    rs.getString("city_country"),
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
            "    us.career AS user_career, \n" +
            "    us.profile_picture_id AS user_profile_picture_id, \n" +
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
            "   c.country AS city_country \n" +
            "\n" +
            "FROM events e\n" +
            "JOIN users us ON e.user_id = us.id\n" +
            "JOIN universities un ON us.university = un.id\n" +
            "JOIN cities c ON e.city_id = c.id \n";



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
                "description", description, // ¿?
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
}
