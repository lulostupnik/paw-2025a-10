package ar.edu.itba.paw.persistence;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import javax.sql.DataSource;
import ar.edu.itba.paw.models.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ar.edu.itba.paw.interfaces.persistence.EventResponseDao;

import static ar.edu.itba.paw.persistence.JdbcDaoUtils.*;

@Repository
public class EventResponseJdbcDao implements EventResponseDao {
    private final static Logger LOGGER = LoggerFactory.getLogger(EventResponseJdbcDao.class);

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    private static final RowMapper<EventResponse> EVENT_RESPONSE_ROW_MAPPER = (rs, rowNum) -> new EventResponse(
            rs.getLong("id"),
            rs.getLong("user_id"),
            rs.getString("username"),
            rs.getLong("event_id"),
            rs.getString("message"),
            rs.getTimestamp("date_time").toLocalDateTime()
    );
    private static final String SQL_EVENT_RESPONSE_BASE =
            """
            SELECT er.id AS id, er.user_id, us.username AS username, er.event_id, er.message, er.date_time
            FROM event_responses er
            JOIN users us ON er.user_id = us.id
            """;
    private static final String SQL_FIND_EVENT_RESPONSE = SQL_EVENT_RESPONSE_BASE + " WHERE er.deleted = FALSE AND er.id = ?";
    private static final String SQL_LIST_ALL_BY_EVENT = SQL_EVENT_RESPONSE_BASE + " WHERE er.deleted = FALSE AND er.event_id = ? ORDER BY date_time";
    private static final String SQL_FIND_EVENT_RESPONSE_DELETED_OR_NOT = SQL_EVENT_RESPONSE_BASE + " WHERE er.id = ?";

    private static final String SQL_LIST_ALL_BY_EVENT_PAGED = SQL_LIST_ALL_BY_EVENT + " LIMIT ? OFFSET ?";





    @Autowired
    public EventResponseJdbcDao(final DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("event_responses")
                .usingGeneratedKeyColumns("id");
    }

    @Override
    public Optional<EventResponse> findById(final long id) {
        return jdbcTemplate.query(SQL_FIND_EVENT_RESPONSE, EVENT_RESPONSE_ROW_MAPPER, id).stream().findFirst();
    }


    @Override
    public EventResponse create(final long userId, final String username, final long eventId, final String message, final LocalDateTime dateTime) {
        final Map<String, Object> args = new HashMap<>();
        args.put("user_id", userId);
        args.put("event_id", eventId);
        args.put("message", message);
        args.put("date_time", dateTime != null ? Timestamp.valueOf(dateTime):null);
        args.put("deleted", false);

        final Number keys = jdbcInsert.executeAndReturnKey(args);
        return new EventResponse(keys.longValue(), userId, username, eventId, message, dateTime);
    }


    @Override
    public int countByEventId(final long eventId) {
        return jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM event_responses WHERE event_id = ? AND deleted = FALSE",
                Integer.class,
                eventId
        );
    }

    @Override
    public long findEventIdById(final long eventResponseId) {
        return jdbcTemplate.query(
                "SELECT event_id FROM event_responses WHERE id = ? ORDER BY date_time ",
                (rs, rowNum) -> rs.getLong("event_id"),
                eventResponseId
        ).getFirst();
    }

    @Override
    public void updateDeletionMessage(final long id, final String message) {
        final int updatedRows = jdbcTemplate.update("UPDATE event_responses SET deleted_message = ? WHERE id = ?;", message, id);
        if (updatedRows == 0) {
            LOGGER.warn("No event_response found with id {}", id);
        }
    }

    @Override
    public void deleteAllByEventId(final long eventId) {
        final int updatedRows = jdbcTemplate.update("UPDATE event_responses SET deleted = TRUE WHERE event_id = ?;", eventId);
        if (updatedRows == 0) {
            LOGGER.warn("No event found with id {}", eventId);
        }
    }

    @Override
    public void delete(final long id) {
        final int updatedRows = jdbcTemplate.update("UPDATE event_responses SET deleted = TRUE WHERE id = ?;", id);
        if (updatedRows == 0) {
            LOGGER.warn("No event_response found with id {}", id);
        }
    }

    @Override
    public Page<EventResponse> listAllByEventId(final long eventId, final PageParams pageParams) {
        return executePagedQuery(
                jdbcTemplate, EVENT_RESPONSE_ROW_MAPPER,
                "SELECT COUNT(*) FROM event_responses WHERE event_id = ? AND deleted = FALSE",
                SQL_LIST_ALL_BY_EVENT_PAGED, pageParams, eventId
        );
    }

}