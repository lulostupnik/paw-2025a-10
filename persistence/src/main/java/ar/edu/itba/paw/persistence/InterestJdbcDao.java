package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.InterestDao;
import ar.edu.itba.paw.models.Interest;

import ar.edu.itba.paw.models.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.*;


@Repository
public class InterestJdbcDao implements InterestDao {
    private static Logger LOGGER = LoggerFactory.getLogger(InterestJdbcDao.class);

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    private final static RowMapper<Interest> INTEREST_ROW_MAPPER = (rs, rowNum) -> new Interest(
            rs.getLong("id"),
            rs.getString("name")
    );

    private final static String SELECT_CLAUSE = "SELECT c.id AS id, c.name AS name ";
    private final static String QUERY = SELECT_CLAUSE + "FROM category c ";

    @Autowired
    public InterestJdbcDao(DataSource dataSource)
    {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("category")
                .usingGeneratedKeyColumns("id");
    }

    @Override
    public Optional<Interest> findById(Long id) {
        return jdbcTemplate.query(QUERY + "WHERE id = ?",
                INTEREST_ROW_MAPPER, id).stream().findFirst();
    }

    @Override
    public List<Interest> findAll() {
        return jdbcTemplate.query(QUERY, INTEREST_ROW_MAPPER);
    }

    @Override
    public List<Interest> findByUserId(Long id) {
        return jdbcTemplate.query(QUERY + " WHERE id IN (SELECT category_id FROM user_interest WHERE user_id = ?)",
                INTEREST_ROW_MAPPER, id);
    }

    @Override
    public Optional<Interest> findByName(String name) {
        return jdbcTemplate.query(QUERY + " WHERE name = ?",
                INTEREST_ROW_MAPPER, name).stream().findFirst();
    }

    // FIXME: No se si esto se está usando en algún lado o no.
    @Override
    public List<Interest> findIdByName(String[] names) {
        if(names == null || names.length == 0) {
            return new ArrayList<>();
        }
        for(String name : names) {
            if (name == null || name.isEmpty()) {
                throw new IllegalArgumentException("Interest name cannot be null or empty");
            }
        }
        StringBuilder query = new StringBuilder(QUERY + " WHERE name IN (");
        for (int i = 0; i < names.length; i++) {
            query.append("?");
            if (i < names.length - 1) {
                query.append(", ");
            }
        }
        query.append(")");
        List<Interest> interests = jdbcTemplate.query(query.toString(),
                INTEREST_ROW_MAPPER, (Object[]) names);
        if (interests.size() < names.length) {
            //TODO See if this is an actual error to throw (or if normal flow can continue)
            LOGGER.warn("Couldn't find IDs for all provided interests ({} vs {})", interests.size(), names.length);
        }
        LOGGER.debug("Found interests {}", interests);
        return interests;
    }

    @Override
    public Interest createUserInterest(String interest) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", interest);
        final Number keys = jdbcInsert.execute(params);
        return new Interest(keys.longValue(), interest);
    }

    @Override
    public void deleteUserInterest(long id) {
        LOGGER.debug("Deleting interest with id {}", id);
        jdbcTemplate.update("DELETE FROM category WHERE id = ?", id);
    }

    @Override
    public void editUserInterest(long id, String interest) {
        LOGGER.debug("Editing interest {} to {}", id, interest);
        jdbcTemplate.update("UPDATE category SET name = ? WHERE id = ?", interest ,id);
    }

    @Override
    public void saveUserInterests(long[] interests, Long userId) {
        LOGGER.debug("Registering to DB new interests for user {}...", userId);
        for (long interest : interests) {
            jdbcTemplate.update("INSERT INTO user_interest (user_id, category_id) VALUES (?, ?)", userId, interest);
        }
    }

    @Override
    public void updateScoreByInterest(Interest interest, Long userId) {
        LOGGER.debug("Registering to DB new interest {} score increase for user {}", interest, userId);
        jdbcTemplate.update("UPDATE user_interest SET score = score + 1 WHERE user_id = ? AND category_id = ?",
                userId, interest.getId());
    }

    @Override
    public void updateScoreByInterests(List<Interest> interests, Long userId) {
        LOGGER.debug("Registering to DB multiple score increases for intrests of user {}", userId);
        for (Interest interest : interests) {
            updateScoreByInterest(interest, userId);
        }
    }

    @Override
    public Page<Interest> getAllInterests(int page, int pageSize) {
        StringBuilder query = new StringBuilder(SELECT_CLAUSE);
        query.append(" FROM category c ");
        query.append(" ORDER BY c.name ASC LIMIT ? OFFSET ?");
        int totalInterests = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM category", Integer.class);
        return new Page<>(
                jdbcTemplate.query(query.toString(), INTEREST_ROW_MAPPER, pageSize, (page - 1) * pageSize),
                page,
                (int) Math.ceil((double) totalInterests / pageSize)
        );
    }

    @Override
    public Page<Interest> searchBySubstring(String search, int page, int pageSize) {
        StringBuilder query = new StringBuilder(SELECT_CLAUSE);
        query.append(" FROM category c ");
        query.append(" WHERE c.name LIKE ? ");
        query.append(" ORDER BY c.name ASC LIMIT ? OFFSET ?");
        String like = "%" + search + "%";
        int totalItems = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM category WHERE name LIKE ?", Integer.class, like);

        return new Page<>(
                jdbcTemplate.query(query.toString(),INTEREST_ROW_MAPPER, like, pageSize, (page - 1) * pageSize),
                page,
                (int) Math.ceil((double) totalItems / pageSize)
        );
    }

    @Override
    public void delete(long id) {
        LOGGER.debug("Deleting interest with ID: {}", id);
        int rowsAffected = jdbcTemplate.update("DELETE FROM category WHERE id = ?", id);
        if (rowsAffected == 0) {
            LOGGER.warn("Interest delete failed: Interest with ID {} not found", id);
        }
    }


}
