package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.EventResponseDao;
import ar.edu.itba.paw.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Repository
public class EventResponseJdbcDao implements EventResponseDao {
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    private static final RowMapper<EventResponse> EVENT_RESPONSE_ROW_MAPPER = (rs, rowNum) -> new EventResponse(
            rs.getLong("user_id"), // Event ID from `events` table
            rs.getString("username"),
            rs.getLong("event_id"),
            rs.getString("message"),
            rs.getTimestamp("date_time").toLocalDateTime()
    );
    private static final String QUERY_BY_EVENT_ID =
            "SELECT er.user_id, us.username as username, er.event_id, er.message, er.date_time " +
                    "FROM event_responses er " +
                    "JOIN users us " +
                    "ON er.user_id = us.id " +
                    "WHERE er.event_id = ?";

    @Autowired
    public EventResponseJdbcDao(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.jdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("event_responses");
    }


    @Override
    public EventResponse create(long userId, String username, long eventId, String message, LocalDateTime dateTime) {
        final Map<String, Object> args = new HashMap<>();
        args.put("user_id", userId);
        args.put("event_id", eventId);
        args.put("message", message);
        args.put("date_time", dateTime);

        jdbcInsert.execute(args);
        return new EventResponse(userId, username, eventId, message, dateTime);
    }

    @Override
    public List<EventResponse> listAllFromEvent(long eventId)
    {
        return jdbcTemplate.query(QUERY_BY_EVENT_ID, EVENT_RESPONSE_ROW_MAPPER, eventId);
    }
}
