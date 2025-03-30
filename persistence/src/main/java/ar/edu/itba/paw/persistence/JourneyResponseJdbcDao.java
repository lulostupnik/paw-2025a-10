package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.JourneyResponseDao;
import ar.edu.itba.paw.models.JourneyResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Repository
public class JourneyResponseJdbcDao implements JourneyResponseDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    @Autowired
    public JourneyResponseJdbcDao(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.jdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("journey_responses");
    }

    @Override
    public JourneyResponse create(long userId, long journeyId, String message) {
        final Map<String, Object> args = new HashMap<>();
        args.put("user_id", userId);
        args.put("journey_id", journeyId);
        args.put("message", message);
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
        return new JourneyResponse(userId, journeyId, message);
    }
}
