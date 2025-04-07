package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.JourneyDao;
import ar.edu.itba.paw.models.City;
import ar.edu.itba.paw.models.Journey;
import ar.edu.itba.paw.models.University;
import ar.edu.itba.paw.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

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
            "    us.career AS user_career, \n" +
            "    us.profile_picture_id AS user_profile_picture_id, \n" +
            "\n" +
            "    j.id AS journey_id, \n" +
            "    j.user_id AS journey_user_id, \n" +
            "    j.destination_university_id AS journey_destination_university_id, \n" +
            "    j.city_id AS journey_city_id, \n" +
            "    j.start_date AS journey_start_date, \n" +
            "    j.end_date AS journey_end_date, \n" +
            "    j.description AS journey_description, \n" +
            "\n" +
            "   ci.id AS city_id, \n" +
            "   co.name AS country_name, \n" +
            "   ci.name AS city_name, \n" +
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
            "JOIN cities ci ON j.city_id = ci.id\n" +
            "JOIN countries co ON ci.country_id = co.id\n" +
            "JOIN universities un1 ON us.university = un1.id\n" +
            "JOIN universities un2 ON j.destination_university_id = un2.id\n";

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
                            rs.getString("university_abbreviation")
                    ),
                    rs.getString("user_career"),
                    rs.getLong("user_profile_picture_id")
            ),
            new City(
                    rs.getString("city_name"),
                    rs.getString("country_name"),
                    rs.getLong("journey_city_id")
            ),
            rs.getDate("journey_start_date").toLocalDate(),
            rs.getDate("journey_end_date").toLocalDate(),
            new University(
                    rs.getLong("destination_university_id"),
                    rs.getString("destination_university_name"),
                    rs.getString("destination_university_abbreviation")
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
    public Journey create(User user, University destinationUniversity, City destinationCity, LocalDate startDate, LocalDate endDate, String description) {
        final Map<String, Object> args = new HashMap<>();
        args.put("user_id", user.getId());
        args.put("destination_university_id", destinationUniversity.getId());
        args.put("city_id", destinationCity.getId());
        args.put("start_date", startDate);
        args.put("end_date", endDate);
        args.put("description", description);
        final Number id = jdbcInsert.executeAndReturnKey(args);
        return new Journey(id.longValue(), user, destinationCity, startDate, endDate, destinationUniversity, description);
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
        String query = QUERY;
        List<String> filters = new ArrayList<>();
        List<Object> params = new ArrayList<>();

        if (destination != null || startDate != null || endDate != null || interest != null) {
            query += " WHERE ";

            if (destination != null) {
                filters.add("j.city = ?");
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
            if (interest != null) {
                filters.add("j.description LIKE ?");
                params.add("%" + interest + "%"); // Agrega los % para el LIKE
            }

            query += String.join(" AND ", filters);
        }

        return jdbcTemplate.query(query, JOURNEY_ROW_MAPPER, params.toArray());
    }

}
