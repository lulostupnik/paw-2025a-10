package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.JourneyResponseDao;
import ar.edu.itba.paw.models.CursorPage;
import ar.edu.itba.paw.models.JourneyResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



//@TODO sort them in query by date
@Repository
public class JourneyResponseJdbcDao implements JourneyResponseDao {
    private static Logger LOGGER = LoggerFactory.getLogger(JourneyResponseJdbcDao.class);

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;
    private static final RowMapper<JourneyResponse> JOURNEY_RESPONSE_ROW_MAPPER = (rs, rowNum) -> new JourneyResponse(
            rs.getLong("user_id"), // Event ID from `events` table
            rs.getString("username"),
            rs.getLong("journey_id"),
            rs.getString("message"),
            rs.getTimestamp("date_time").toLocalDateTime()
    );
    private static final String QUERY_BY_JOURNEY_ID = """
            SELECT jr.user_id, us.username AS username, jr.journey_id, jr.message, jr.date_time\s
            FROM journey_responses jr\s
            JOIN users us\s
            ON jr.user_id = us.id\s
            WHERE jr.journey_id = ?""";

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

        jdbcInsert.execute(args);
        // handlear excepción?
        /*
        try {
            jdbcInsert.execute(args);
            return new JourneyResponse(userId, journeyId, message);
        } catch (DuplicateKeyException e) {
            throw new ResponseAlreadyExistsException("User " + userId + " has already responded to journey " + journeyId);
        }
        */
        return new JourneyResponse(userId, username, journeyId, message, dateTime);
    }

    @Override
    public List<JourneyResponse> listAllFromJourney(long journeyId){
        LOGGER.debug("Querying DB for replies to journey {}", journeyId);
        return jdbcTemplate.query(QUERY_BY_JOURNEY_ID + " ORDER BY jr.date_time ", JOURNEY_RESPONSE_ROW_MAPPER, journeyId);
    }

    @Override
    public String[] listAllEmailsRespondersMinusUsers(long eventId, List<Long> userIds) {
        StringBuilder query = new StringBuilder("""
        SELECT DISTINCT us.email
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

        List<String> emails = jdbcTemplate.queryForList(query.toString(), String.class, params.toArray());
        return emails.toArray(new String[0]);
    }



}
