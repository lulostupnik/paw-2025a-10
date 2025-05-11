package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.JourneyResponseDao;
import ar.edu.itba.paw.models.JourneyResponse;
import ar.edu.itba.paw.models.Page;
import ar.edu.itba.paw.models.PageParams;
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
import java.util.*;

import static ar.edu.itba.paw.persistence.JdbcDaoUtils.*;

@Repository
public class JourneyResponseJdbcDao implements JourneyResponseDao {
    private final static Logger LOGGER = LoggerFactory.getLogger(JourneyResponseJdbcDao.class);

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

    private static final String SQL_JOURNEY_RESPONSE_BASE =
            """
            SELECT jr.id AS id, jr.user_id, us.username AS username, jr.journey_id, jr.message, jr.date_time
            FROM journey_responses AS jr
            JOIN users us ON jr.user_id = us.id
            WHERE jr.deleted = FALSE
            """;

    private static final String SQL_FIND_BY_ID = SQL_JOURNEY_RESPONSE_BASE + " AND jr.id = ?";

    private static final String SQL_FIND_ALL_BY_JOURNEY = SQL_JOURNEY_RESPONSE_BASE + " AND jr.journey_id = ? ORDER BY date_time";

    private static final String SQL_FIND_ALL_BY_JOURNEY_PAGED = SQL_FIND_ALL_BY_JOURNEY + " LIMIT ? OFFSET ?";

    @Autowired
    public JourneyResponseJdbcDao(final DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("journey_responses")
                .usingGeneratedKeyColumns("id");
    }

    @Override
    public Optional<JourneyResponse> findById(final long id) {
        return jdbcTemplate.query(SQL_FIND_BY_ID, JOURNEY_RESPONSE_ROW_MAPPER, id).stream().findFirst();
    }

    @Override
    public JourneyResponse create(final long userId, final String username, final long journeyId, final String message, final LocalDateTime dateTime) {
        LOGGER.debug("Registering new journey response to journey {} from user {} ({}) saying '{}' on {}", journeyId, userId, username, message, dateTime);
        final Map<String, Object> args = new HashMap<>();

        args.put("user_id", userId);
        args.put("journey_id", journeyId);
        args.put("message", message);
        args.put("date_time", Timestamp.valueOf(dateTime));
        args.put("deleted", false);

        final Number keys = jdbcInsert.executeAndReturnKey(args);
        final JourneyResponse response = new JourneyResponse(keys.longValue(), userId, username, journeyId, message, dateTime);
        LOGGER.info("Successfully registered journey response {}", response);
        return response;
    }

    @Override
    public Page<JourneyResponse> listAllByJourneyId(final long journeyId, final PageParams pageParams) {
        return executePagedQuery(
                jdbcTemplate,
                JOURNEY_RESPONSE_ROW_MAPPER,
                "SELECT COUNT(*) FROM journey_responses WHERE journey_id = ? AND deleted = FALSE",
                SQL_FIND_ALL_BY_JOURNEY_PAGED,
                pageParams, journeyId
        );
    }

    @Override
    public void delete(final long id) {
        LOGGER.info("Setting journey response {} as deleted", id);
        final int updatedRows = jdbcTemplate.update("UPDATE journey_responses SET deleted = TRUE WHERE id = ?;", id);
        if (updatedRows == 0) {
            LOGGER.warn("No journey_response found with id {}", id);
        }
    }

    @Override
    public long findJourneyIdByResponseId(final long journeyResponseId) {
        return jdbcTemplate.query(
                "SELECT journey_id FROM journey_responses WHERE id = ?",
                (rs, rowNum) -> rs.getLong("journey_id"),
                journeyResponseId
        ).getFirst();
    }

    @Override
    public void updateDeletionMessage(final long id, final String message) {
        LOGGER.info("Setting deletion message '{}' for journey response {}", message, id);

        final int updatedRows = jdbcTemplate.update("UPDATE journey_responses SET deleted_message = ? WHERE id = ?;", message, id);
        if (updatedRows == 0) {
            LOGGER.warn("No journey_response found with id {}", id);
        }
    }

    @Override
    public void deleteByJourneyId(final long journeyId) {
        LOGGER.info("Setting responses to journey {} as deleted", journeyId);
        final int updatedRows = jdbcTemplate.update("UPDATE journey_responses SET deleted = TRUE WHERE journey_id = ?;", journeyId);
        if (updatedRows == 0) {
            LOGGER.warn("No journey_response found with id {}", journeyId);
        }
    }

    @Override
    public int countByJourneyId(final long journeyId) {
        return jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM journey_responses WHERE journey_id = ? AND deleted = FALSE",
                Integer.class,
                journeyId
        );
    }

}

