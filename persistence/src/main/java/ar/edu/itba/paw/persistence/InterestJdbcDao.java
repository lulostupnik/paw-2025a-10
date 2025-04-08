package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.InterestDao;
import ar.edu.itba.paw.models.Interest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Repository
public class InterestJdbcDao implements InterestDao {
    private final JdbcTemplate jdbcTemplate;

    private final static RowMapper<Interest> INTEREST_ROW_MAPPER = (rs, rowNum) -> new Interest(
            rs.getLong("id"),
            rs.getString("name")
    );

    private final static String ES_QUERY = "SELECT c.id AS id, c.es_name AS name FROM category c ";
    private final static String EN_QUERY = "SELECT c.id AS id, c.en_name AS name FROM category c ";

    @Autowired
    public InterestJdbcDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public Optional<Interest> findById(Long id) {
        Boolean spanishOrEnglish = true;
        String query = spanishOrEnglish ? ES_QUERY : EN_QUERY;
        return jdbcTemplate.query(query + "WHERE id = ?",
                INTEREST_ROW_MAPPER, id).stream().findFirst();
    }

    @Override
    public List<Interest> findAll() {
        Boolean spanishOrEnglish = true;
        String query = spanishOrEnglish ? ES_QUERY : EN_QUERY;
        return jdbcTemplate.query(query, INTEREST_ROW_MAPPER);
    }

    @Override
    public List<Interest> findByUserId(Long id) {
        Boolean spanishOrEnglish = true;
        String query = spanishOrEnglish ? ES_QUERY : EN_QUERY;
        return jdbcTemplate.query(query + " WHERE id IN (SELECT category_id FROM user_interest WHERE user_id = ?)",
                INTEREST_ROW_MAPPER, id);
    }

    @Override
    public Optional<Interest> findByName(String name) {
        Boolean spanishOrEnglish = true;
        String query = spanishOrEnglish ? ES_QUERY : EN_QUERY;
        return jdbcTemplate.query(query + " WHERE name = ?",
                INTEREST_ROW_MAPPER, name).stream().findFirst();
    }

    @Override
    public List<Interest> findIdByName(String[] names) {
        Boolean spanishOrEnglish = true;
        if(names == null || names.length == 0) {
            return new ArrayList<>();
        }
        for(String name : names) {
            if (name == null || name.isEmpty()) {
                throw new IllegalArgumentException("Interest name cannot be null or empty");
            }

        }
        String tmp = spanishOrEnglish ? ES_QUERY : EN_QUERY;
        if(spanishOrEnglish){
            tmp += " WHERE es_name IN (";
        } else {
            tmp += " WHERE en_name IN (";
        }
        StringBuilder query = new StringBuilder(tmp);
        for (int i = 0; i < names.length; i++) {
            query.append("?");
            if (i < names.length - 1) {
                query.append(", ");
            }
        }
        query.append(")");
        return jdbcTemplate.query(query.toString(),
                INTEREST_ROW_MAPPER, (Object[]) names);
    }

    @Override
    public Optional<Interest> createUserInterest(Interest interest, Long userId) {
        return Optional.empty();
    }

    @Override
    public List<Interest> createUserInterests(String[] interests, Long userId) {
        List<Interest> interestList = findIdByName(interests);
        for (Interest interest : interestList) {
            jdbcTemplate.update("INSERT INTO user_interest (user_id, category_id) VALUES (?, ?)", userId, interest.getId());
        }
        return interestList;
    }


}
