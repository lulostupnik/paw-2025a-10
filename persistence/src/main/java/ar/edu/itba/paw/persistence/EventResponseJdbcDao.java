package ar.edu.itba.paw.persistence;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import ar.edu.itba.paw.models.CursorPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import ar.edu.itba.paw.interfaces.persistence.EventResponseDao;
import ar.edu.itba.paw.models.EventResponse;

@Repository
public class EventResponseJdbcDao implements EventResponseDao {
    private static Logger LOGGER = LoggerFactory.getLogger(EventResponseJdbcDao.class);

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
        this.jdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("event_responses")
                                                            .usingGeneratedKeyColumns("id");
    }


    @Override
    public EventResponse create(long userId, String username, long eventId, String message, LocalDateTime dateTime) {
        LOGGER.debug("Registering new event response for event {} by user {} ({}) who says {} on {}", eventId, userId, username, message, dateTime);
        final Map<String, Object> args = new HashMap<>();
        args.put("user_id", userId);
        args.put("event_id", eventId);
        args.put("message", message);
        args.put("date_time", dateTime);

        jdbcInsert.execute(args);
        LOGGER.debug("Successfully registered event response");
        return new EventResponse(userId, username, eventId, message, dateTime);
    }

    @Override
    public List<EventResponse> listAllFromEvent(long eventId){
        LOGGER.debug("Querying DB for replies to event {}", eventId);
        return jdbcTemplate.query(QUERY_BY_EVENT_ID + " ORDER BY date_time ", EVENT_RESPONSE_ROW_MAPPER, eventId);
    }

    @Override
    public CursorPage<EventResponse, LocalDateTime> getEventsForUser(long eventId, LocalDateTime cursor, int limit) {
        String sql = QUERY_BY_EVENT_ID + (cursor != null ? " AND date_time < ?" : "") + " ORDER BY date_time DESC LIMIT ?";
        List<EventResponse> responseList;
        if(cursor != null) {
            responseList = jdbcTemplate.query(sql, EVENT_RESPONSE_ROW_MAPPER, eventId, limit + 1);
        } else {
            responseList = jdbcTemplate.query(sql, EVENT_RESPONSE_ROW_MAPPER, limit + 1);
        }
        LocalDateTime nextCursor = null;
        boolean hasNext = responseList != null && responseList.size() > limit;
        if(hasNext){
            responseList.removeLast();
            nextCursor = responseList.getLast().getDateTime();
        }
        return new CursorPage<>(responseList, nextCursor, hasNext);
    }
}
