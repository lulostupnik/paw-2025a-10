package ar.edu.itba.paw.persistence;
import ar.edu.itba.paw.interfaces.persistence.EventDao;
import ar.edu.itba.paw.interfaces.persistence.UserDao;
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

import static java.util.Arrays.stream;


@Repository
public class EventJdbcDao implements EventDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    private static final RowMapper<Event> EVENT_ROW_MAPPER = (rs, rowNum) -> new Event(
            rs.getLong("id"), // Event ID from `events` table
            new User(
                    rs.getLong("user_id"),
                    rs.getString("email"), // Correct field from `users`
                    rs.getString("username"),
                    rs.getString("firstname"),
                    rs.getString("lastname"),
                    new University(
                            rs.getLong("university"),
                            rs.getString("name"), // University name
                            rs.getString("abbreviation")
                    ),
                    rs.getString("career"),
                    rs.getObject("profile_picture_id") != null ? rs.getLong("profile_picture_id") : null
            ),
            rs.getDate("event_date"),
            rs.getString("description"),
            rs.getObject("flyer_image_id") != null ? rs.getLong( "flyer_image_id") : null,
            new City(
                    rs.getString("name"), // City name
                    rs.getString("country"),
                    rs.getLong("city_id")
            )
    );



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
                "event_description", description,
                "flyer_image_id", flyerImageId
                );
        final Number keys = jdbcInsert.executeAndReturnKey(parameters);
        return new Event(keys.longValue(), user, date, description, flyerImageId, city);
    }

    @Override
    public List<Event> listByQuery(Long cityId, Date date) {
        StringBuilder sqlBuilder = new StringBuilder(
                "SELECT * FROM events e"
        );
        List<Object> params = new ArrayList<>();
        boolean firstCondition = true;
        if (cityId != null) {
            sqlBuilder.append("WHERE")
                    .append(" e.city_id = ?");
            params.add(cityId);
            firstCondition = false;
        }

        if (date != null) {
            sqlBuilder.append(firstCondition ? " WHERE" : " AND")
                    .append(" e.date AFTER ?");
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
        return jdbcTemplate.query("SELECT * FROM events e " +
                "JOIN users u ON e.user_id = u.id " +
                "JOIN universities un ON u.university = un.id " +
                "JOIN cities c ON e.city_id = c.id " +
                "WHERE e.id = ?",
                EVENT_ROW_MAPPER, eventId).stream().findFirst();
    }

    @Override
    public List<Event> listAll() {
        return jdbcTemplate.query("SELECT * FROM events e " +
                "JOIN users u ON e.user_id = u.id " +
                "JOIN universities un ON u.university = un.id " +
                "JOIN cities c ON e.city_id = c.id",
                EVENT_ROW_MAPPER);
    }

}
