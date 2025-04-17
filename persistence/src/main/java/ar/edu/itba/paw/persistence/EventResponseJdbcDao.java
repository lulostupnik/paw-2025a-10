package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.EventResponseDao;
import ar.edu.itba.paw.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
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
            rs.getLong("event_id"),
            rs.getString("message")
    );
    private static final String QUERY_BY_EVENT_ID =
            "SELECT er.user_id, er.event_id, er.message FROM event_responses er WHERE er.event_id = ?";
//    private static final String QUERY = "SELECT \n" +
//            "    er.user_id, \n" +
//            "    er.event_id, \n" +
//            "    er.message    \n"+
//
//            "FROM event_responses er\n";

    @Autowired
    public EventResponseJdbcDao(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.jdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("event_responses");
    }


    @Override
    public EventResponse create(long userId, long eventId, String message) {
        final Map<String, Object> args = new HashMap<>();
        args.put("user_id", userId);
        args.put("event_id", eventId);
        args.put("message", message);
        jdbcInsert.execute(args);
        return new EventResponse(userId, eventId, message);
    }

    @Override
    public List<EventResponse> listAllFromEvent(long eventId)
    {
        return jdbcTemplate.query(QUERY_BY_EVENT_ID, EVENT_RESPONSE_ROW_MAPPER, eventId);
    }
}
