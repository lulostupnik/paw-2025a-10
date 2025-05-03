package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.JourneyResponseDao;
import ar.edu.itba.paw.models.JourneyResponse;
import ar.edu.itba.paw.models.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.*;

//@TODO sort them in query by date
@Repository
public class JourneyResponseJdbcDao implements JourneyResponseDao {
    private static Logger LOGGER = LoggerFactory.getLogger(JourneyResponseJdbcDao.class);

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;
    private static final RowMapper<JourneyResponse> JOURNEY_RESPONSE_ROW_MAPPER = (rs, rowNum) -> new JourneyResponse(
            rs.getLong("id"),
            rs.getLong("user_id"),
            rs.getString("username"),
            rs.getLong("journey_id"),
            rs.getString("message"),
            rs.getTimestamp("date_time").toLocalDateTime()
    );


    private static final RowMapper<Long> JOURNEY_ID_ROW_MAPPER = (rs, rowNum) -> rs.getLong("journey_id");
    private static final String JOURNEY_ID_BY_RESPONSE_ID_QUERY = """
            SELECT jr.journey_id
            FROM journey_responses jr
            WHERE jr.id = ?""";

    private static final String QUERY_BY_JOURNEY_ID = """
            SELECT jr.id as id,
                   jr.user_id, us.username AS username, jr.journey_id, jr.message, jr.date_time\s
            FROM journey_responses jr\s
            JOIN users us\s
            ON jr.user_id = us.id\s
            WHERE jr.journey_id = ?""";

    private static final String PAGE_QUERY_BY_JOURNEY_ID = """
            SELECT *
            FROM (SELECT * FROM journey_responses WHERE journey_id = ? AND deleted = FALSE ORDER BY date_time LIMIT ? OFFSET ?) AS jr
            JOIN users us
            ON jr.user_id = us.id
            """;

    private static final String NOT_DELETED = " AND jr.deleted = FALSE";


    @Autowired
    public JourneyResponseJdbcDao(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("journey_responses")
                .usingGeneratedKeyColumns("id");
    }

    @Override
    public JourneyResponse create(long userId, String username, long journeyId, String message, LocalDateTime dateTime) {
        LOGGER.debug("Registering new journey response to journey {} from user {} ({}) saying '{}' on {}", journeyId, userId, username, message, dateTime);
        final Map<String, Object> args = new HashMap<>();

        args.put("user_id", userId);
        args.put("journey_id", journeyId);
        args.put("message", message);
        args.put("date_time", dateTime);
        args.put("deleted", false);

        final Number keys = jdbcInsert.executeAndReturnKey(args);
        return new JourneyResponse(keys.longValue(), userId, username, journeyId, message, dateTime);
    }

    @Override
    public List<JourneyResponse> listAllFromJourney(long journeyId){
        return jdbcTemplate.query(QUERY_BY_JOURNEY_ID + NOT_DELETED + " ORDER BY jr.date_time ", JOURNEY_RESPONSE_ROW_MAPPER, journeyId);
    }

    @Override
    public Page<JourneyResponse> listAllFromJourney(long journeyId, int pageNumber, int pageSize) {
        long totalItems = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM journey_responses WHERE journey_id = ? AND deleted = FALSE", Long.class, journeyId);

        return new Page<>(
                jdbcTemplate.query(PAGE_QUERY_BY_JOURNEY_ID, JOURNEY_RESPONSE_ROW_MAPPER, journeyId, pageSize, (pageNumber - 1) * pageSize),
                pageNumber,
                (int) Math.ceil((double) totalItems / pageSize)
        );
    }

    @Override
    public void delete(long id) {
        int updatedRows = jdbcTemplate.update("UPDATE journey_responses SET deleted = TRUE WHERE id = ?;", id);
        if (updatedRows == 0) {
            LOGGER.warn("No journey_response found with id {}", id);
        }
    }

    @Override
    public long getJourneyIdByResponseId(long journeyResponseId) {
        return jdbcTemplate.query(JOURNEY_ID_BY_RESPONSE_ID_QUERY, JOURNEY_ID_ROW_MAPPER, journeyResponseId).getFirst();
    }

    @Override
    public void deletionMessage(long id, String message) {
        int updatedRows = jdbcTemplate.update("UPDATE journey_responses SET deleted_message = ? WHERE id = ?;", message, id);
        if (updatedRows == 0) {
            LOGGER.warn("No journey_response found with id {}", id);
        }
    }

    @Override
    public void deleteByJourneyId(long journeyId) {
        int updatedRows = jdbcTemplate.update("UPDATE journey_responses SET deleted = TRUE WHERE journey_id = ?;", journeyId);
        if (updatedRows == 0) {
            LOGGER.warn("No journey_response found with id {}", journeyId);
        }
    }

}

//@ans devolver USERS - dao de users.
    /*@Override
    public List<EmailRecipient> listAllEmailsRespondersMinusUsers(long eventId, List<Long> userIds) {
        StringBuilder query = new StringBuilder("""
        SELECT DISTINCT us.email, us.language
        FROM journey_responses jr
        JOIN users us ON jr.user_id = us.id
        WHERE jr.journey_id = ?
    """);

        List<Object> params = new ArrayList<>();
        params.add(eventId);

        if (userIds != null && !userIds.isEmpty()) {
            query.append(" AND jr.user_id NOT IN (");
            query.append("?,".repeat(userIds.size()));
            query.setLength(query.length() - 1); // Remove last comma
            query.append(")");
            params.addAll(userIds);
        }

       return jdbcTemplate.query(query.toString(),EMAIL_RECIPIENT_ROW_MAPPER, params.toArray());
    }


    }*/
