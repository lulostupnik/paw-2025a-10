package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.InterestDao;
import ar.edu.itba.paw.models.Interest;
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
import java.util.*;
import java.util.stream.Collectors;
import static ar.edu.itba.paw.persistence.JdbcDaoUtils.*;


@Repository
public class InterestJdbcDao implements InterestDao {
    private final static Logger LOGGER = LoggerFactory.getLogger(InterestJdbcDao.class);

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    private final static RowMapper<Interest> INTEREST_ROW_MAPPER = (rs, rowNum) -> new Interest(
            rs.getLong("id"),
            rs.getString("name")
    );

    private final static String SQL_BASE = "SELECT id, name FROM category ";

    private final static String SQL_FIND_BY_ID = SQL_BASE + " WHERE id = ?";
    private final static String SQL_FIND_BY_NAME = SQL_BASE + " WHERE name = ?";

    private final static String SQL_FIND_ALL_PAGED = SQL_BASE + " ORDER BY name ASC LIMIT ? OFFSET ?";

    private final static String SQL_SEARCH_PAGED = SQL_BASE + " WHERE name LIKE ? ORDER BY name ASC LIMIT ? OFFSET ?";

    private final static String SQL_FIND_ALL_BY_USER = SQL_BASE + " WHERE id IN (SELECT category_id FROM user_interest WHERE user_id = ?)";

    private final static String SQL_FIND_ALL_PAGED_BY_USER = SQL_FIND_ALL_BY_USER + " ORDER BY name ASC LIMIT ? OFFSET ?";
    @Autowired
    public InterestJdbcDao(final DataSource dataSource)
    {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("category")
                .usingGeneratedKeyColumns("id");
    }

    @Override
    public Optional<Interest> findById(final long id) {
        return jdbcTemplate.query(SQL_FIND_BY_ID, INTEREST_ROW_MAPPER, id).stream().findFirst();
    }

    @Override
    public List<Interest> findAllByUserId(final long id) {
        return jdbcTemplate.query(SQL_FIND_ALL_BY_USER, INTEREST_ROW_MAPPER, id);
    }

    @Override
    public Optional<Interest> findByName(final String name) {
        return jdbcTemplate.query(SQL_FIND_BY_NAME, INTEREST_ROW_MAPPER, name).stream().findFirst();
    }

    @Override
    public Interest create(final String interest) {
        LOGGER.debug("Creating new interest {}", interest);
        final Map<String, Object> params = new HashMap<>();
        params.put("name", interest);
        final Number keys = jdbcInsert.executeAndReturnKey(params);
        final Interest newInterest = new Interest(keys.longValue(), interest);
        LOGGER.info("Created interest {}", interest);
        return newInterest;
    }


    @Override
    public void update(final long id, final String interest) {
        LOGGER.info("Editing interest {} to {}", id, interest);
        final int rowsAffected = jdbcTemplate.update("UPDATE category SET name = ? WHERE id = ?", interest ,id);
        if (rowsAffected == 0) {
            LOGGER.warn("Interest update failed: Interest with ID {} not found", id);
        }
    }

    @Override
    public void saveUserInterests(final long[] interests, final long userId) {
        LOGGER.debug("Registering to DB new interests {} for user {}...", interests, userId);
        for (long interest : interests) {
            jdbcTemplate.update("INSERT INTO user_interest (user_id, category_id) VALUES (?, ?)", userId, interest);
        }
    }

    @Override
    public void updateScoreByInterest(final Interest interest, final long userId) {
        LOGGER.info("Registering to DB new interest {} score increase for user {}", interest, userId);
        jdbcTemplate.update("UPDATE user_interest SET score = score + 1 WHERE user_id = ? AND category_id = ?",
                userId, interest.getId());
    }

    @Override
    public void updateUserInterests(final long[] interestIds, final long userId) {
        LOGGER.debug("Updating interests for user {}", userId);

        final List<Long> currentInterestIds = jdbcTemplate.queryForList(
                "SELECT category_id FROM user_interest WHERE user_id = ?",
                Long.class,
                userId
        );

        final List<Long> interestsToAdd = Arrays.stream(interestIds).boxed()
                .collect(Collectors.toList());

        final List<Long> interestsToRemove = new ArrayList<>(currentInterestIds);

        interestsToRemove.removeAll(interestsToAdd);
        interestsToAdd.removeAll(currentInterestIds);

        for (Long interestId : interestsToAdd) {
            jdbcTemplate.update(
                    "INSERT INTO user_interest (user_id, category_id) VALUES (?, ?)",
                    userId, interestId
            );
        }

        if(interestsToRemove.isEmpty()){
            return;
        }

        final StringBuilder deleteQuery = new StringBuilder(
                "DELETE FROM user_interest WHERE user_id = ? AND category_id IN ("
        );
        for (int i = 0; i < interestsToRemove.size(); i++) {
            deleteQuery.append("?");
            if (i < interestsToRemove.size() - 1) {
                deleteQuery.append(", ");
            }
        }
        deleteQuery.append(")");

        Object[] params = new Object[interestsToRemove.size() + 1];
        params[0] = userId;
        for (int i = 0; i < interestsToRemove.size(); i++) {
            params[i + 1] = interestsToRemove.get(i);
        }

        jdbcTemplate.update(deleteQuery.toString(), params);


    }

    @Override
    public void updateScoreByInterests(final List<Interest> interests, final long userId) {
        LOGGER.debug("Registering to DB multiple score increases for intrests of user {}", userId);
        for (Interest interest : interests) {
            updateScoreByInterest(interest, userId);
        }
    }

    @Override
    public void saveUserInterests(final List<String> interestNames, final long userId){
       for(String interestName : interestNames) {
           jdbcTemplate.update(
                   "INSERT INTO user_interest (user_id, category_id) VALUES (?, (SELECT id FROM category WHERE name = ?))",
                   userId, interestName
           );
       }
    }

    @Override
    public Page<Interest> findAll(PageParams pageParams) {
        final int totalInterests = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM category", Integer.class);
        return new Page<>(
                jdbcTemplate.query(SQL_FIND_ALL_PAGED, INTEREST_ROW_MAPPER, pageParams.getSize(), offset(pageParams)),
                pageParams.getPage(),
                pageCount(totalInterests, pageParams.getSize())
        );
    }

    @Override
    public Page<Interest> search(final String searchTerm, PageParams pageParams) {
        final String searchPattern = likePattern(searchTerm);
        final int totalItems = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM category WHERE name LIKE ?", Integer.class, searchPattern);

        return new Page<>(
                jdbcTemplate.query(SQL_SEARCH_PAGED, INTEREST_ROW_MAPPER, searchPattern, pageParams.getSize(), offset(pageParams)),
                pageParams.getPage(),
                pageCount(totalItems, pageParams.getSize())
        );
    }

    @Override
    public void delete(final long id) {
        LOGGER.info("Deleting interest with ID: {}", id);
        final int rowsAffected = jdbcTemplate.update("DELETE FROM category WHERE id = ?", id);
        if (rowsAffected == 0) {
            LOGGER.warn("Interest delete failed: Interest with ID {} not found", id);
        }
    }
    @Override
    public Page<Interest> findAllByUserId(final long id, PageParams pageParams) {
        final int totalItems = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM user_interest WHERE user_id = ?", Integer.class, id);
        return new Page<>(
                jdbcTemplate.query(SQL_FIND_ALL_PAGED_BY_USER, INTEREST_ROW_MAPPER, id, pageParams.getSize(), offset(pageParams)),
                pageParams.getPage(),
                pageCount(totalItems, pageParams.getSize())
        );
    }


}
