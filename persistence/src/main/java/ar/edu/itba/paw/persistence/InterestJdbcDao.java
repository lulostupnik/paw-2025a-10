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
    @Autowired
    public InterestJdbcDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public Optional<Interest> findById(Long id) {
        return jdbcTemplate.query("SELECT * FROM category WHERE id = ?",
                INTEREST_ROW_MAPPER, id).stream().findFirst();
    }

    @Override
    public List<Interest> findAll() {
        return jdbcTemplate.query("SELECT * FROM category",
                INTEREST_ROW_MAPPER);
    }

    @Override
    public List<Interest> findByUserId(Long id) {
        return jdbcTemplate.query("SELECT * FROM category WHERE id IN (SELECT category_id FROM user_interest WHERE user_id = ?)",
                INTEREST_ROW_MAPPER, id);
    }

    @Override
    public Optional<Interest> findByName(String name) {
        return jdbcTemplate.query("SELECT * FROM category WHERE name = ?",
                INTEREST_ROW_MAPPER, name).stream().findFirst();
    }

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
        StringBuilder query = new StringBuilder("SELECT * FROM category WHERE name IN (");
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
