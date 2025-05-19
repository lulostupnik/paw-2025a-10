//package ar.edu.itba.paw.persistence.deprecated;
//
//import ar.edu.itba.paw.interfaces.persistence.InterestDao;
//import ar.edu.itba.paw.models.Interest;
//import ar.edu.itba.paw.models.Page;
//import ar.edu.itba.paw.models.PageParams;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.jdbc.core.RowMapper;
//import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
//
//import javax.sql.DataSource;
//import java.util.*;
//import java.util.stream.Collectors;
//import static ar.edu.itba.paw.persistence.JdbcDaoUtils.*;
//
//
//@Deprecated
//public class InterestJdbcDao implements InterestDao {
//    private final static Logger LOGGER = LoggerFactory.getLogger(InterestJdbcDao.class);
//
//    private final JdbcTemplate jdbcTemplate;
//    private final SimpleJdbcInsert jdbcInsert;
//
//    private final static RowMapper<Interest> INTEREST_ROW_MAPPER = (rs, rowNum) -> new Interest(
//            rs.getLong("id"),
//            rs.getString("name")
//    );
//
//    private final static String SQL_BASE = "SELECT id, name FROM category ";
//
//    private final static String SQL_FIND_BY_ID = SQL_BASE + " WHERE id = ?";
//    private final static String SQL_FIND_BY_NAME = SQL_BASE + " WHERE name = ?";
//
//    private final static String SQL_FIND_ALL_PAGED = SQL_BASE + " ORDER BY name ASC LIMIT ? OFFSET ?";
//
//    private final static String SQL_SEARCH_PAGED = SQL_BASE + " WHERE LOWER(name) LIKE LOWER(?) ORDER BY name ASC LIMIT ? OFFSET ?";
//
//    private final static String SQL_FIND_ALL_BY_USER = SQL_BASE + " WHERE id IN (SELECT category_id FROM user_interest WHERE user_id = ?)";
//
//    private final static String SQL_FIND_ALL_PAGED_BY_USER = SQL_FIND_ALL_BY_USER + " ORDER BY name ASC LIMIT ? OFFSET ?";
//    @Autowired
//    public InterestJdbcDao(final DataSource dataSource)
//    {
//        this.jdbcTemplate = new JdbcTemplate(dataSource);
//        this.jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
//                .withTableName("category")
//                .usingGeneratedKeyColumns("id");
//    }
//
//    @Override
//    public Optional<Interest> findById(final long id) {
//        return jdbcTemplate.query(SQL_FIND_BY_ID, INTEREST_ROW_MAPPER, id).stream().findFirst();
//    }
//
//    @Override
//    public List<Interest> findAllByUserId(final long id) {
//        return jdbcTemplate.query(SQL_FIND_ALL_BY_USER, INTEREST_ROW_MAPPER, id);
//    }
//
//    @Override
//    public Optional<Interest> findByName(final String name) {
//        return jdbcTemplate.query(SQL_FIND_BY_NAME, INTEREST_ROW_MAPPER, name).stream().findFirst();
//    }
//
//    @Override
//    public Interest create(final String interest) {
//        final Map<String, Object> params = new HashMap<>();
//        params.put("name", interest);
//        final Number keys = jdbcInsert.executeAndReturnKey(params);
//        return new Interest(keys.longValue(), interest);
//    }
//
//
//    @Override
//    public void update(final long id, final String interest) {
//        final int rowsAffected = jdbcTemplate.update("UPDATE category SET name = ? WHERE id = ?", interest ,id);
//        if (rowsAffected == 0) {
//            LOGGER.warn("Interest update failed: Interest with ID {} not found", id);
//        }
//    }
//
//    @Override
//    public void createUserInterests(final long[] interests, final long userId) {
//        for (long interest : interests) {
//            jdbcTemplate.update("INSERT INTO user_interest (user_id, category_id) VALUES (?, ?)", userId, interest);
//        }
//    }
//
//    @Override
//    public void updateScoreByInterest(final Interest interest, final long userId) {
//        jdbcTemplate.update("UPDATE user_interest SET score = score + 1 WHERE user_id = ? AND category_id = ?",
//                userId, interest.getId());
//    }
//
//    @Override
//    public void updateUserInterests(final long[] interestIds, final long userId) {
//        final List<Long> currentInterestIds = jdbcTemplate.queryForList(
//                "SELECT category_id FROM user_interest WHERE user_id = ?",
//                Long.class,
//                userId
//        );
//
//        final List<Long> interestsToAdd = Arrays.stream(interestIds).boxed()
//                .collect(Collectors.toList());
//
//        final List<Long> interestsToRemove = new ArrayList<>(currentInterestIds);
//
//        interestsToRemove.removeAll(interestsToAdd);
//        interestsToAdd.removeAll(currentInterestIds);
//
//        for (Long interestId : interestsToAdd) {
//            jdbcTemplate.update(
//                    "INSERT INTO user_interest (user_id, category_id) VALUES (?, ?)",
//                    userId, interestId
//            );
//        }
//
//        if(interestsToRemove.isEmpty()){
//            return;
//        }
//
//        final StringBuilder deleteQuery = new StringBuilder(
//                "DELETE FROM user_interest WHERE user_id = ? AND category_id IN ("
//        );
//        for (int i = 0; i < interestsToRemove.size(); i++) {
//            deleteQuery.append("?");
//            if (i < interestsToRemove.size() - 1) {
//                deleteQuery.append(", ");
//            }
//        }
//        deleteQuery.append(")");
//
//        Object[] params = new Object[interestsToRemove.size() + 1];
//        params[0] = userId;
//        for (int i = 0; i < interestsToRemove.size(); i++) {
//            params[i + 1] = interestsToRemove.get(i);
//        }
//
//        jdbcTemplate.update(deleteQuery.toString(), params);
//
//
//    }
//
//    @Override
//    public void updateScoreByInterests(final List<Interest> interests, final long userId) {
//        for (Interest interest : interests) {
//            updateScoreByInterest(interest, userId);
//        }
//    }
//
//    @Override
//    public void createUserInterests(final List<String> interestNames, final long userId){
//       for(String interestName : interestNames) {
//           jdbcTemplate.update(
//                   "INSERT INTO user_interest (user_id, category_id) VALUES (?, (SELECT id FROM category WHERE name = ?))",
//                   userId, interestName
//           );
//       }
//    }
//
//    @Override
//    public Page<Interest> findAll(final PageParams pageParams) {
//        return executePagedQuery(
//                jdbcTemplate, INTEREST_ROW_MAPPER,
//                "SELECT COUNT(*) FROM category", SQL_FIND_ALL_PAGED,
//                pageParams
//        );
//    }
//
//    @Override
//    public Page<Interest> search(final String searchTerm, final PageParams pageParams) {
//        final String searchPattern = likePattern(searchTerm);
//        return executePagedQuery(
//                jdbcTemplate, INTEREST_ROW_MAPPER,
//                "SELECT COUNT(*) FROM category WHERE LOWER(name) LIKE LOWER(?)", SQL_SEARCH_PAGED,
//                pageParams, searchPattern
//        );
//    }
//
//    @Override
//    public void delete(final long id) {
//        final int rowsAffected = jdbcTemplate.update("DELETE FROM category WHERE id = ?", id);
//        if (rowsAffected == 0) {
//            LOGGER.warn("Interest delete failed: Interest with ID {} not found", id);
//        }
//    }
//
//    @Override
//    public Page<Interest> findAllByUserId(final long id, final PageParams pageParams) {
//        return executePagedQuery(
//                jdbcTemplate, INTEREST_ROW_MAPPER,
//                "SELECT COUNT(*) FROM user_interest WHERE user_id = ?", SQL_FIND_ALL_PAGED_BY_USER,
//                pageParams, id
//        );
//    }
//
//
//}
