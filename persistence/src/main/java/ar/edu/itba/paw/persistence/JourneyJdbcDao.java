package ar.edu.itba.paw.persistence;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import javax.sql.DataSource;
import ar.edu.itba.paw.models.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ar.edu.itba.paw.interfaces.persistence.JourneyDao;

@Repository
public class JourneyJdbcDao implements JourneyDao {
    private static Logger LOGGER = LoggerFactory.getLogger(JourneyJdbcDao.class);

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    private static final String ORDER_BY = " ORDER BY j.id ASC ";
    private static final String CURSOR_CONDITION = " j.id > ? ";

    private static final String SELECT_CLAUSE = """
            SELECT\s
                us.id AS user_id,\s
                us.email AS user_email,\s
                us.firstname AS user_firstname,\s
                us.lastname AS user_lastname,\s
                us.username AS user_username,\s
                us.university AS user_university,\s
                us.profile_picture_id AS user_profile_picture_id,\s
                us.language AS user_language,
                ca.id AS career_id,\s
                ca.name AS career_name,\s
                j.id AS journey_id,\s
                j.user_id AS journey_user_id,\s
                j.destination_university_id AS journey_destination_university_id,\s
                j.start_date AS journey_start_date,\s
                j.end_date AS journey_end_date,\s
                j.description AS journey_description,\s
                ci1.id AS city_id,\s
                co1.name AS country_name,\s
                ci1.name AS city_name,\s
                ci2.id AS destination_city_id,\s
                co2.name AS destination_country_name,\s
                ci2.name AS destination_city_name,\s
                un1.id AS university_id,\s
                un1.name AS university_name,\s
                un1.abbreviation AS university_abbreviation,\s
                un2.id AS destination_university_id,\s
                un2.name AS destination_university_name,\s
                un2.abbreviation AS destination_university_abbreviation\s
            """;

    private static final String QUERY = SELECT_CLAUSE +
            """
            FROM users us\s
            JOIN journeys j ON j.user_id = us.id
            JOIN careers ca ON us.career_id = ca.id
            JOIN universities un1 ON us.university = un1.id
            JOIN cities ci1 ON un1.city_id = ci1.id
            JOIN countries co1 ON ci1.country_id = co1.id
            JOIN universities un2 ON j.destination_university_id = un2.id
            JOIN cities ci2 ON un2.city_id = ci2.id
            JOIN countries co2 ON ci2.country_id = co2.id
            """;
    private static final String NOT_DELETED = " WHERE j.deleted = FALSE";
    private static final String PAGE_QUERY = SELECT_CLAUSE +
        """
        FROM (
            SELECT * FROM journeys ORDER BY id ASC LIMIT ? OFFSET ?
        ) AS j
        JOIN users us ON j.user_id = us.id
        JOIN careers ca ON us.career_id = ca.id
        JOIN universities un1 ON us.university = un1.id
        JOIN cities ci1 ON un1.city_id = ci1.id
        JOIN countries co1 ON ci1.country_id = co1.id
        JOIN universities un2 ON j.destination_university_id = un2.id
        JOIN cities ci2 ON un2.city_id = ci2.id
        JOIN countries co2 ON ci2.country_id = co2.id
        """;


    private static final String PAGE_JOURNEY_BY_NOT_USER_ID = SELECT_CLAUSE +
            """
            FROM (\s
                SELECT *\s
                FROM journeys\s
                WHERE user_id != ? \s
                ORDER BY id ASC LIMIT ? OFFSET ?\s
            ) AS j\s
            JOIN users us ON j.user_id = us.id\s
            JOIN careers ca ON us.career_id = ca.id\s
            JOIN universities un1 ON us.university = un1.id\s
            JOIN cities ci1 ON un1.city_id = ci1.id\s
            JOIN countries co1 ON ci1.country_id = co1.id\s
            JOIN universities un2 ON j.destination_university_id = un2.id\s
            JOIN cities ci2 ON un2.city_id = ci2.id\s
            JOIN countries co2 ON ci2.country_id = co2.id\s
            """;

    private final static String QUERY_INTEREST = QUERY + " JOIN user_interest ui ON us.id = ui.user_id JOIN category c ON ui.category_id = c.id \n";

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
                    rs.getLong("user_profile_picture_id"),
                    Locale.of(rs.getString("user_language"))
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


    @Autowired
    public JourneyJdbcDao(final DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("journeys")
                .usingGeneratedKeyColumns("id");
    }

    @Override
    public Journey create(User user, University destinationUniversity, LocalDate startDate, LocalDate endDate, String description) {
        LOGGER.debug("Registering new journey of {} to {} from {} to {} ({})", user, destinationUniversity, startDate, endDate, description);
        Optional<Journey> journey = findByUserEmail(user.getEmail());
        final Map<String, Object> args = new HashMap<>();
        args.put("user_id", user.getId());
        args.put("destination_university_id", destinationUniversity.getId());
        args.put("start_date", startDate);
        args.put("end_date", endDate);
        args.put("description", description);
        final Number id = jdbcInsert.executeAndReturnKey(args);
        LOGGER.info("Successfully registered journey {}", id.longValue());
        return new Journey(id.longValue(), user, startDate, endDate, destinationUniversity, description);
    }

    @Override
    public List<Journey> listAll() {
        LOGGER.debug("Querying DB for all journeys");
        return jdbcTemplate.query(QUERY + NOT_DELETED, JOURNEY_ROW_MAPPER);
    }


    @Override
    public Optional<Journey> findById(long id) {
        LOGGER.debug("Querying DB for journey {}", id);
        return jdbcTemplate.query(QUERY + " WHERE j.id = ?", JOURNEY_ROW_MAPPER, id).stream().findFirst();
    }
    
    @Override
    public Optional<Journey> findOverlappingJourney(long userId, LocalDate startDate, LocalDate endDate) {
        LOGGER.debug("Querying DB for overlapping journeys for user {} from {} to {}", userId, startDate, endDate);
        return jdbcTemplate.query(QUERY + NOT_DELETED+  " AND user_id = ? AND start_date >= ? AND end_date <= ?", JOURNEY_ROW_MAPPER, userId, startDate, endDate).stream().findFirst();
    }

    @Override
    public List<Journey> findByFilters(String destination, LocalDate startDate, LocalDate endDate, String interest) {
        LOGGER.debug("Querying DB for journeys with filters");

        String query;

        if (interest != null && !interest.isEmpty()) {
            LOGGER.debug("Interest present, using interest query");
            query = QUERY_INTEREST + NOT_DELETED;
        } else {
            LOGGER.debug("Interest not present, using regular query");
            query = QUERY + NOT_DELETED;
        }

        List<String> filters = new ArrayList<>();
        List<Object> params = new ArrayList<>();

        if (destination != null && !destination.isEmpty()) {
            LOGGER.debug("Filter added: destination {}", destination);
            filters.add("ci2.id = ?");
            params.add(Integer.parseInt(destination));
        }
        if (startDate != null) {
            LOGGER.debug("Filter added: start date {}", startDate);
            filters.add("j.start_date <= ?");
            params.add(endDate);
        }
        if (endDate != null) {
            LOGGER.debug("Filter added: end date {}", endDate);
            filters.add("j.end_date >= ?");
            params.add(startDate);
        }
        if (interest != null && !interest.isEmpty()) {
            LOGGER.debug("Filter added: interest {}", interest);
            filters.add("c.id = ?");
            params.add(Integer.parseInt( interest));
        }

        // Solo agregamos WHERE si hay filtros
        if (!filters.isEmpty()) {
            LOGGER.debug("Filters present, adding to query");
            query += " AND " + String.join(" AND ", filters);
        }

        return jdbcTemplate.query(query, JOURNEY_ROW_MAPPER, params.toArray());
    }

    @Override
    public List<Journey> findByFilters(long userId, String destination, LocalDate startDate, LocalDate endDate, String interest) {
        LOGGER.debug("Querying DB for journeys with filters excluding user {}", userId);

        String query;

        if (interest != null && !interest.isEmpty()) {
            LOGGER.debug("Interest present, using interest query");
            query = QUERY_INTEREST + NOT_DELETED;
        } else {
            LOGGER.debug("Interest not present, using regular query");
            query = QUERY + NOT_DELETED;
        }

        List<String> filters = new ArrayList<>();
        List<Object> params = new ArrayList<>();

        // Add filter to exclude user's journeys
        filters.add("us.id != ?");
        params.add(userId);

        if (destination != null && !destination.isEmpty()) {
            LOGGER.debug("Filter added: destination {}", destination);
            filters.add("ci2.id = ?");
            params.add(Integer.parseInt(destination));
        }
        if (startDate != null) {
            LOGGER.debug("Filter added: start date {}", startDate);
            filters.add("j.start_date <= ?");
            params.add(endDate);
        }
        if (endDate != null) {
            LOGGER.debug("Filter added: end date {}", endDate);
            filters.add("j.end_date >= ?");
            params.add(startDate);
        }
        if (interest != null && !interest.isEmpty()) {
            LOGGER.debug("Filter added: interest {}", interest);
            filters.add("c.id = ?");
            params.add(Integer.parseInt(interest));
        }

        // Add WHERE clause with all filters
        query += " AND " + String.join(" AND ", filters);

        return jdbcTemplate.query(query, JOURNEY_ROW_MAPPER, params.toArray());
    }


    @Override
    public List<Journey> findByOriginCity(long originCityId) {
        return jdbcTemplate.query(QUERY + NOT_DELETED+ " AND ci1.id = ?", JOURNEY_ROW_MAPPER, originCityId);
    }

    @Override
    public List<Journey> findByOriginUniversity(long originUniversityId) {
        return jdbcTemplate.query(QUERY + NOT_DELETED+ " AND un1.id = ?", JOURNEY_ROW_MAPPER, originUniversityId);
    }


    @Override
    public Optional<Journey> findByUserId(long userId) {
        LOGGER.debug("Querying DB for journeys from user {}", userId);
        return jdbcTemplate.query(QUERY + NOT_DELETED+ " AND us.id = ?", JOURNEY_ROW_MAPPER, userId).stream().findFirst();
    }

    private Optional<Journey> findByUserIdDeleted(long userId) {
        LOGGER.debug("Querying DB for journeys from user {}", userId);
        return jdbcTemplate.query(QUERY + " WHERE us.id = ?", JOURNEY_ROW_MAPPER, userId).stream().findFirst();
    }


    @Override
    public Optional<Journey> findByUserEmail(String email) {
        LOGGER.debug("Querying DB for journeys from user {}", email);
        return jdbcTemplate.query(QUERY + NOT_DELETED+ " AND us.email = ?", JOURNEY_ROW_MAPPER, email).stream().findFirst();
    }

    @Override
    public List<Journey> getRecommendedJourneys(String email) {
        LOGGER.debug("Querying DB for recommended journeys for usermail {}", email);

        String query = """
               WITH user_data AS (
                   SELECT id, university, language AS user_university, career_id
                   FROM users
                   WHERE email = ?
               ),
                    user_interests AS (
                        SELECT category_id, score
                        FROM user_interest
                                 JOIN user_data ud ON user_interest.user_id = ud.id
                    ),
                    user_journey AS (
                        SELECT
                            j.destination_university_id AS university_id,
                            univ.city_id,
                            j.start_date AS user_start,
                            j.end_date AS user_end
                        FROM journeys j
                                 JOIN universities univ ON j.destination_university_id = univ.id
                                 JOIN user_data ud ON ud.id = j.user_id
                                     WHERE j.deleted = false
                        LIMIT 1
                    ),
                    journey_scores AS (
                        SELECT
                            j.id AS journey_id,
                            u.id AS user_id,
                            u.language AS user_language,
                            u.email AS user_email,
                            u.username AS user_username,
                            u.firstname AS user_firstname,
                            u.lastname AS user_lastname,
                            uu.id AS user_university,
                            uu.name AS university_name,
                            uu.abbreviation AS university_abbreviation,
                            uc.name AS city_name,
                            co.name AS country_name,
                            uc.id AS city_id,
                            c.id AS career_id,
                            c.name AS career_name,
                            u.profile_picture_id AS user_profile_picture_id,
                            j.start_date AS journey_start_date,
                            j.end_date AS journey_end_date,
                            dest_univ.id AS destination_university_id,
                            dest_univ.name AS destination_university_name,
                            dest_univ.abbreviation AS destination_university_abbreviation,
                            dest_city.name AS destination_city_name,
                            dest_country.name AS destination_country_name,
                            dest_city.id AS destination_city_id,
                            j.description AS journey_description,
               
               
                            -- Scores
                            CASE WHEN j.destination_university_id = uj.university_id THEN 50 ELSE 0 END AS university_match_score,
                            CASE WHEN dest_univ.city_id = uj.city_id THEN 30 ELSE 0 END AS city_match_score,
                            COALESCE((
                                         SELECT SUM(ui.score) * 3
                                         FROM user_interest journey_ui
                                                  JOIN user_interests ui ON ui.category_id = journey_ui.category_id
                                         WHERE journey_ui.user_id = j.user_id
                                     ), 0) AS interest_match_score,
                            CASE WHEN (j.start_date, j.end_date) OVERLAPS (uj.user_start, uj.user_end) THEN 15 ELSE 0 END AS timing_match_score
                        FROM journeys j
                                 JOIN users u ON j.user_id = u.id
                                 JOIN universities dest_univ ON j.destination_university_id = dest_univ.id
                                 JOIN cities dest_city ON dest_univ.city_id = dest_city.id
                                 JOIN countries dest_country ON dest_city.country_id = dest_country.id
                                 JOIN universities uu ON u.university = uu.id
                                 JOIN cities uc ON uu.city_id = uc.id
                                 JOIN countries co ON uc.country_id = co.id
                                 LEFT JOIN careers c ON u.career_id = c.id
                                 CROSS JOIN user_journey uj
                                 CROSS JOIN user_data ud
                        WHERE j.user_id != ud.id
                    )
               
               SELECT
                   journey_id,
                   user_id,
                   user_language,
                   user_email,
                   user_username,
                   user_firstname,
                   user_lastname,
                   user_profile_picture_id,
                   user_university,
                   university_name,
                   university_abbreviation,
                   city_name,
                   city_id,
                   country_name,
                   career_id,
                   career_name,
                   journey_start_date,
                   journey_end_date,
                   destination_university_id,
                   destination_university_name,
                   destination_university_abbreviation,
                   destination_city_name,
                   destination_country_name,
                   destination_city_id,
                   journey_description
               FROM journey_scores
               ORDER BY
                   (university_match_score + city_match_score + interest_match_score + timing_match_score) DESC
            """;
        return jdbcTemplate.query(query, JOURNEY_ROW_MAPPER, email);
    }

    @Override
    public List<Journey> getJourneysByUser(String email) {
        return jdbcTemplate.query(QUERY + NOT_DELETED+ " AND us.email = ?", JOURNEY_ROW_MAPPER, email);
    }
    @Override
    public void delete(long id) {
        final String query = "UPDATE journeys SET deleted = TRUE WHERE id = ?;";
        int updatedRows = jdbcTemplate.update(query, id);

        if (updatedRows == 0) {
            // Optionally log or throw an exception if no rows were updated
            LOGGER.warn("No journey_response found with id {}", id);
        }
    }

    @Override
    public List<Journey> getOthersJourneys(long userId) {
        LOGGER.debug("Querying DB for journeys from users other than user ID: {}", userId);
        return jdbcTemplate.query(QUERY + NOT_DELETED+ " AND us.id != ? ", JOURNEY_ROW_MAPPER, userId);
    }

    @Override
    public void updateDates(long journeyId, LocalDate startDate, LocalDate endDate) {
        LOGGER.debug("Updating dates for journey ID: {} to start: {}, end: {}", journeyId, startDate, endDate);

        int rowsAffected = jdbcTemplate.update(
                "UPDATE journeys SET start_date = ?, end_date = ? WHERE id = ?",
                startDate, endDate, journeyId
        );

        if (rowsAffected == 0) {
            LOGGER.warn("Journey date update failed: Journey with ID {} not found", journeyId);
        }
    }

    @Override
    public void updateDescription(long journeyId, String description) {
        LOGGER.debug("Updating description for journey ID: {}", journeyId);

        int rowsAffected = jdbcTemplate.update(
                "UPDATE journeys SET description = ? WHERE id = ?",
                description, journeyId
        );

        if (rowsAffected == 0) {
            LOGGER.warn("Journey description update failed: Journey with ID {} not found", journeyId);
        }
    }

    @Override
    public void updateDestinationUniversity(long journeyId, long universityId) {
        LOGGER.debug("Updating destination university for journey ID: {} to university ID: {}", journeyId, universityId);

        int rowsAffected = jdbcTemplate.update(
                "UPDATE journeys SET destination_university_id = ? WHERE id = ?",
                universityId, journeyId
        );

        if (rowsAffected == 0) {
            LOGGER.warn("Journey destination update failed: Journey with ID {} not found", journeyId);
        }
    }

    @Override
    public Page<Journey> listAll(int page, int size) {
        List<Journey> list = jdbcTemplate.query(PAGE_QUERY, JOURNEY_ROW_MAPPER, size, page * size);
        return new Page<>(list, page);
    }

        // SELECT_CLAUSE +
        // FROM ( SELECT * FROM
        // innerClause +
        // outerClause

    @Override
    public Page<Journey> getOthersJourneys(long userId, int page, int size) {
        List<Journey> list = jdbcTemplate.query(PAGE_JOURNEY_BY_NOT_USER_ID, JOURNEY_ROW_MAPPER, userId, size, page * size);
        return new Page<>(list, page);    }

    @Override
    public Page<Journey> findByFilters(Long userId, Long cityId, LocalDate startDate, LocalDate endDate, Long interest, int page, int size) {
        String query;

        List<String> filters = new ArrayList<>();
        List<Object> params = new ArrayList<>();

        if (userId != null) {
            filters.add("us.id = ?");
            params.add(userId);
        }

        if (cityId != null) {
            filters.add("ci2.id = ?");
            params.add(cityId);
        }

        if (endDate != null) {
            filters.add("j.start_date <= ?");
            params.add(endDate);
        }

        if (startDate != null) {
            filters.add("j.end_date >= ?");
            params.add(startDate);
        }

        if (interest != null) {
            query = QUERY_INTEREST;
            filters.add("c.id = ?");
            params.add(interest);
        } else {
            query = QUERY;
        }


        if (!filters.isEmpty()) {
            query += " WHERE " + String.join(" AND ", filters);
        }

        query += " ORDER BY j.id ASC LIMIT ? OFFSET ?";
        params.add(size);
        params.add(page * size);

        return new Page<>(jdbcTemplate.query(query, JOURNEY_ROW_MAPPER, params.toArray()), page);
    }

    @Override
    public Page<Journey> findByOriginCity(long originCityId, int page, int size) {
        List<Journey> list = jdbcTemplate.query(QUERY + NOT_DELETED + "AND ci1.id = ? ORDER BY j.id ASC LIMIT ? OFFSET ?", JOURNEY_ROW_MAPPER, originCityId, size, page * size);
        return new Page<>(list, page);
    }

}
