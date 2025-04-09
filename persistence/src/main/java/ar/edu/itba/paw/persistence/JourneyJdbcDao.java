package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.InterestDao;
import ar.edu.itba.paw.interfaces.persistence.JourneyDao;
import ar.edu.itba.paw.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.*;

/*


        user_id INTEGER NOT NULL,
        destination_university_id INTEGER NOT NULL,
        city VARCHAR(100) NOT NULL,
        start_date DATE NOT NULL,
        end_date DATE NOT NULL,
        description VARCHAR(2047),

        FOREIGN KEY (destination_university_id) REFERENCES university ON DELETE RESTRICT,
        FOREIGN KEY (user_id) REFERENCES "user" ON DELETE RESTRICT
        -- FOREIGN KEY (city_id) REFERENCES city
);
 */

@Repository
public class JourneyJdbcDao implements JourneyDao {
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    private static final String QUERY = "SELECT \n" +
            "    us.id AS user_id, \n" +
            "    us.email AS user_email, \n" +
            "    us.firstname AS user_firstname, \n" +
            "    us.lastname AS user_lastname, \n" +
            "    us.username AS user_username, \n" +
            "    us.university AS user_university, \n" +
            "    us.profile_picture_id AS user_profile_picture_id, \n" +
            "\n" +
            "    ca.id AS career_id, \n" +
            "    ca.name AS career_name, \n" +
            "\n" +
            "    j.id AS journey_id, \n" +
            "    j.user_id AS journey_user_id, \n" +
            "    j.destination_university_id AS journey_destination_university_id, \n" +
            "    j.start_date AS journey_start_date, \n" +
            "    j.end_date AS journey_end_date, \n" +
            "    j.description AS journey_description, \n" +
            "\n" +
            "   ci1.id AS city_id, \n" +
            "   co1.name AS country_name, \n" +
            "   ci1.name AS city_name, \n" +
            "\n" +
            "   ci2.id AS destination_city_id, \n" +
            "   co2.name AS destination_country_name, \n" +
            "   ci2.name AS destination_city_name, \n" +
            "\n" +
            "    un1.id AS university_id, \n" +
            "    un1.name AS university_name, \n" +
            "    un1.abbreviation AS university_abbreviation, \n" +
            "\n" +
            "    un2.id AS destination_university_id, \n" +
            "    un2.name AS destination_university_name, \n" +
            "    un2.abbreviation AS destination_university_abbreviation \n" +
            "\n" +
            "FROM users us \n" +
            "JOIN journeys j ON j.user_id = us.id\n" +
            "JOIN careers ca ON us.career_id = ca.id\n" +
            "JOIN universities un1 ON us.university = un1.id\n" +
            "JOIN cities ci1 ON un1.city_id = ci1.id\n" +
            "JOIN countries co1 ON ci1.country_id = co1.id\n" +
            "JOIN universities un2 ON j.destination_university_id = un2.id\n" +
            "JOIN cities ci2 ON un2.city_id = ci2.id\n" +
            "JOIN countries co2 ON ci2.country_id = co2.id\n";

    private final static String QUERY_INTEREST = QUERY + " JOIN user_interest ui ON us.id = ui.user_id\n" +
            "JOIN category c ON ui.category_id = c.id\n";

    private final static RowMapper<Journey> JOURNEY_ROW_MAPPER = (rs, rowNum) -> new Journey(
            rs.getLong("journey_id"),
            new User(
                    rs.getLong("user_id"),
                    rs.getString("user_email"),
                    rs.getString("user_username"),
                    rs.getString("user_firstname"),
                    rs.getString("user_lastname"),
                    new University(
                            rs.getLong("user_university"),
                            rs.getString("university_name"),
                            rs.getString("university_abbreviation"),
                            new City(
                                    rs.getString("city_name"),
                                    rs.getString("country_name"),
                                    rs.getLong("city_id")
                            )
                    ),
                    new Career(
                            rs.getLong("career_id"),
                            rs.getString("career_name")
                    ),
                    rs.getLong("user_profile_picture_id")
            ),
            rs.getDate("journey_start_date").toLocalDate(),
            rs.getDate("journey_end_date").toLocalDate(),
            new University(
                    rs.getLong("destination_university_id"),
                    rs.getString("destination_university_name"),
                    rs.getString("destination_university_abbreviation"),
                    new City(
                            rs.getString("destination_city_name"),
                            rs.getString("destination_country_name"),
                            rs.getLong("destination_city_id")
                    )
            ),
            rs.getString("journey_description")
    );

    /*
    private final static RowMapper<Journey> JOURNEY_ROW_MAPPER = (rs, rowNum) -> new Journey(
            rs.getLong("journey_id"),
            new User(rs.getLong("journey_user_id"), rs.getString("user_email"), rs.getString("user_username"), rs.getString("user_firstname"), rs.getString("user_lastname"), new University(...))
    );
    */



    @Autowired
    public JourneyJdbcDao(final DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("journeys")
                .usingGeneratedKeyColumns("id");
    }

    @Override
    public Journey create(User user, University destinationUniversity, LocalDate startDate, LocalDate endDate, String description) {
        final Map<String, Object> args = new HashMap<>();
        args.put("user_id", user.getId());
        args.put("destination_university_id", destinationUniversity.getId());
        args.put("start_date", startDate);
        args.put("end_date", endDate);
        args.put("description", description);
        final Number id = jdbcInsert.executeAndReturnKey(args);
        return new Journey(id.longValue(), user, startDate, endDate, destinationUniversity, description);
    }

    @Override
    public List<Journey> listAll() {
        return jdbcTemplate.query(QUERY, JOURNEY_ROW_MAPPER);
    }

    @Override
    public Optional<Journey> findById(long id) {
        return jdbcTemplate.query(QUERY + " WHERE j.id = ?", JOURNEY_ROW_MAPPER, id).stream().findFirst();
    }
    
    @Override
    public Optional<Journey> findOverlappingJourney(long id, LocalDate startDate, LocalDate endDate) {
        return jdbcTemplate.query(QUERY + " WHERE user_id = ? AND start_date >= ? AND end_date <= ?", JOURNEY_ROW_MAPPER, id, startDate, endDate).stream().findFirst();
    }

    @Override
    public List<Journey> findByFilters(String destination, LocalDate startDate, LocalDate endDate, String interest) {
        String query;

        if (interest != null && !interest.isEmpty()) {
            query = QUERY_INTEREST;
        } else {
            query = QUERY;
        }

        List<String> filters = new ArrayList<>();
        List<Object> params = new ArrayList<>();

        if (destination != null && !destination.isEmpty()) {
            filters.add("ci2.name = ?");
            params.add(destination);
        }
        if (startDate != null) {
            filters.add("j.start_date <= ?");
            params.add(startDate);
        }
        if (endDate != null) {
            filters.add("j.end_date >= ?");
            params.add(endDate);
        }
        if (interest != null && !interest.isEmpty()) {
            filters.add("c.name = ?");
            params.add(interest);
        }

        // Solo agregamos WHERE si hay filtros
        if (!filters.isEmpty()) {
            query += " WHERE " + String.join(" AND ", filters);
        }

        return jdbcTemplate.query(query, JOURNEY_ROW_MAPPER, params.toArray());
    }


    @Override
    public Optional<Journey> findByUserId(long userId) {
        return jdbcTemplate.query(QUERY + " WHERE us.id = ?", JOURNEY_ROW_MAPPER, userId).stream().findFirst();
    }

}
