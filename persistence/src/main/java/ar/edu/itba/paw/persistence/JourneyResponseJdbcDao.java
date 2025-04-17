package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.JourneyResponseDao;
import ar.edu.itba.paw.models.EventResponse;
import ar.edu.itba.paw.models.JourneyResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class JourneyResponseJdbcDao implements JourneyResponseDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;
    private static final RowMapper<JourneyResponse> JOURNEY_RESPONSE_ROW_MAPPER = (rs, rowNum) -> new JourneyResponse(
            rs.getLong("user_id"), // Event ID from `events` table
            rs.getString("username"),
            rs.getLong("journey_id"),
            rs.getString("message"),
            rs.getTimestamp("date_time").toLocalDateTime()
    );
    private static final String QUERY_BY_JOURNEY_ID =
            "SELECT jr.user_id, us.username as username, jr.journey_id, jr.message, jr.date_time " +
                    "FROM journey_responses jr " +
                    "JOIN users us " +
                    "ON jr.user_id = us.id " +
                    "WHERE jr.journey_id = ?";
    @Autowired
    public JourneyResponseJdbcDao(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.jdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("journey_responses");
    }

    @Override
    public JourneyResponse create(long userId, String username, long journeyId, String message, LocalDateTime dateTime) {
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
    public List<JourneyResponse> listAllFromJourney(long journeyId)
    {
        return jdbcTemplate.query(QUERY_BY_JOURNEY_ID, JOURNEY_RESPONSE_ROW_MAPPER, journeyId);
    }

}
