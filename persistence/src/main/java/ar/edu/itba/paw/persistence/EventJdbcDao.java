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


//FIXME: Not yet tested
@Repository
public class EventJdbcDao implements EventDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    private final static RowMapper<Event> EVENT_ROW_MAPPER = (rs, rowNum) -> new Event(
            rs.getLong("event_id"),
            new User(rs.getLong("user_id"),
                    rs.getString("user_email"),
                    rs.getString("user_username"),
                    rs.getString("user_firstname"),
                    rs.getString("user_lastname"),
                    new University(rs.getLong("user_university"), rs.getString("university_name"), rs.getString("university_abbreviation")),
                    rs.getString("user_career"),
                    rs.getLong("user_profile_picture_id")),
            rs.getDate("event_date"),
            rs.getString("event_description"),
            rs.getLong("event_flyer_image_id"),
            new City(rs.getString("city_name"), rs.getString("city_country"), rs.getLong("city_id"))
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
