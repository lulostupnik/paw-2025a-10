package ar.edu.itba.paw.persistence;

import java.sql.Date;
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
import static ar.edu.itba.paw.persistence.JdbcDaoUtils.*;

@Repository
public class JourneyJdbcDao implements JourneyDao {
    private final static Logger LOGGER = LoggerFactory.getLogger(JourneyJdbcDao.class);

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    private final static String SQL_SELECT_BASE =
            """
            SELECT
                u.id AS user_id,
                u.email AS user_email,
                u.firstname AS user_firstname,
                u.lastname AS user_lastname,
                u.username AS user_username,
                u.university AS user_university,
                u.profile_picture_id AS user_profile_picture_id,
                u.language AS user_language,
                u.blocked AS user_blocked,
                ca.id AS career_id,
                ca.name AS career_name,
                j.id AS journey_id,
                j.user_id AS journey_user_id,
                j.destination_university_id AS journey_destination_university_id,
                j.start_date AS journey_start_date,
                j.end_date AS journey_end_date,
                j.description AS journey_description,
                ci1.id AS city_id,
                co1.name AS country_name,
                ci1.name AS city_name,
                ci2.id AS destination_city_id,
                co2.name AS destination_country_name,
                ci2.name AS destination_city_name,
                un1.id AS university_id,
                un1.name AS university_name,
                un1.abbreviation AS university_abbreviation,
                un2.id AS destination_university_id,
                un2.name AS destination_university_name,
                un2.abbreviation AS destination_university_abbreviation
            """;

    private final static String SQL_FROM_BASE =
            """
            FROM users u
            JOIN journeys j ON j.user_id = u.id
            JOIN careers ca ON u.career_id = ca.id
            JOIN universities un1 ON u.university = un1.id
            JOIN cities ci1 ON un1.city_id = ci1.id
            JOIN countries co1 ON ci1.country_id = co1.id
            JOIN universities un2 ON j.destination_university_id = un2.id
            JOIN cities ci2 ON un2.city_id = ci2.id
            JOIN countries co2 ON ci2.country_id = co2.id
            """;

    private static final String SQL_NOT_DELETED = " WHERE j.deleted = FALSE ";
    private final static String SQL_BASE = SQL_SELECT_BASE + SQL_FROM_BASE + SQL_NOT_DELETED;

    private static final String SQL_FIND_BY_ID = SQL_BASE + " AND j.id = ?";
    private final static String SQL_FIND_BY_USER_ID = SQL_BASE + " AND u.id = ?";
    private final static String SQL_FIND_BY_USER_ID_DELETED = SQL_SELECT_BASE + SQL_FROM_BASE + " WHERE u.id = ?" ; // "AND j.deleted = TRUE"; ?
    private final static String SQL_FIND_BY_USER_EMAIL = SQL_BASE + " AND u.email = ?";
    private final static String SQL_FIND_OVERLAPPING = SQL_BASE + " AND j.user_id = ? AND j.end_date >= ? AND j.start_date <= ?";
    private final static String SQL_FIND_OTHERS_BY_USER_ID = SQL_BASE + " AND u.id != ?";
    private final static String SQL_FIND_BY_ORIGIN_CITY = SQL_BASE + " AND ci1.id = ?";
    private final static String SQL_FIND_BY_ORIGIN_UNIVERSITY = SQL_BASE + " AND un1.id = ?";

    private final static String SQL_FIND_BY_ORIGIN_CITY_PAGED = SQL_FIND_BY_ORIGIN_CITY + " ORDER BY j.id ASC LIMIT ? OFFSET ?";

    private final static String SQL_PAGE = " ORDER BY j.id ASC LIMIT ? OFFSET ? ";
    private final static String SQL_FIND_ALL_PAGED = SQL_BASE + SQL_PAGE;
    private final static String SQL_FIND_OTHERS_PAGED = SQL_BASE + " AND j.user_id != ? " + SQL_PAGE;
    private final static String SQL_BASE_INTEREST = SQL_SELECT_BASE + SQL_FROM_BASE + " JOIN user_interest ui ON u.id = ui.user_id JOIN category c ON ui.category_id = c.id" + SQL_NOT_DELETED;

    private final static String SQL_SEARCH_WHERE_CLAUSE =
            """
            (
                LOWER(u.username) LIKE LOWER(?)
            --  OR LOWER(u.firstname) LIKE LOWER(?)
            --  OR LOWER(u.lastname) LIKE LOWER(?)
            --  OR LOWER(j.description) LIKE LOWER(?)
                OR LOWER(un2.name) LIKE LOWER(?)
            --  OR LOWER(un2.abbreviation) LIKE LOWER(?)
                OR LOWER(ci2.name) LIKE LOWER(?)
            )
            """;

    private final static String SQL_SEARCH_PAGED = SQL_BASE + " AND "  + " ORDER BY j.id ASC LIMIT ? OFFSET ? ";

    private final static String SQL_SEARCH_COUNT =
            """
            SELECT COUNT(*)
            FROM users u
            JOIN journeys j ON j.user_id = u.id
            JOIN universities un2 ON j.destination_university_id = un2.id
            JOIN cities ci2 ON un2.city_id = ci2.id
            """
            + SQL_NOT_DELETED + " AND " + SQL_SEARCH_WHERE_CLAUSE;


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
                    Locale.of(rs.getString("user_language")),
                    rs.getBoolean("user_blocked")
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
    public Journey create(final User user, final University destinationUniversity, final LocalDate startDate, final LocalDate endDate, final String description) {
        LOGGER.debug("Registering new journey of {} to {} from {} to {} ({})", user, destinationUniversity, startDate, endDate, description);
        // FIXME: no se si es la responsabilidad del DAO validar esto
        final Optional<Journey> journey = findByUserIdDeleted(user.getId());
        if(journey.isPresent()){
            LOGGER.debug("Journey found");
            updateData(journey.get().getId(), destinationUniversity, startDate, endDate, description);
            return findByUserId(journey.get().getUser().getId()).orElseThrow(RuntimeException::new);
        }
        final Map<String, Object> args = new HashMap<>();
        args.put("user_id", user.getId());
        args.put("destination_university_id", destinationUniversity.getId());
        args.put("start_date", Date.valueOf(startDate));
        args.put("end_date", Date.valueOf(endDate));
        args.put("description", description);
        args.put("deleted", false);
        final Number id = jdbcInsert.executeAndReturnKey(args);
        final Journey newJourney = new Journey(id.longValue(), user, startDate, endDate, destinationUniversity, description);
        LOGGER.info("Successfully registered journey {}", newJourney);
        return newJourney;
    }

    @Override
    public List<Journey> listAll() {
        return jdbcTemplate.query(SQL_BASE, JOURNEY_ROW_MAPPER);
    }


    @Override
    public Optional<Journey> findById(final long id) {
        return jdbcTemplate.query(SQL_FIND_BY_ID, JOURNEY_ROW_MAPPER, id).stream().findFirst();
    }
    
    @Override
    public Optional<Journey> findOverlappingJourney(final long userId, final LocalDate startDate, final LocalDate endDate) {
        // FIXME: ¿usar Date?
        final Date newStartDate = Date.valueOf(startDate);
        final Date newEndDate = Date.valueOf(endDate);
        return jdbcTemplate.query(SQL_FIND_OVERLAPPING, JOURNEY_ROW_MAPPER, userId, newStartDate, newEndDate).stream().findFirst();
    }


    // FIXME: usar StringBuilder
    @Override
    public List<Journey> findByFilters(final String destination, final LocalDate startDate, final LocalDate endDate, final String interest) {

        final StringBuilder queryBuilder = new StringBuilder((interest != null && !interest.isEmpty()) ? SQL_BASE_INTEREST : SQL_BASE);

        final List<Object> params = new ArrayList<>();

        if (destination != null && !destination.isEmpty()) {
            queryBuilder.append(" AND ci2.id = ? ");
            params.add(Integer.parseInt(destination)); // FIXME: ¿PORQUE ESTAMOS RECIBIENDO UN STRING CON EL ID DEL DESTINO!?!?!?
        }
        if (endDate != null) {
            queryBuilder.append(" AND j.start_date <= ? ");
            params.add(Date.valueOf(endDate));
        }
        if (startDate != null) {
            queryBuilder.append(" AND j.end_date >= ? ");
            params.add(Date.valueOf(startDate));
        }
        if (interest != null && !interest.isEmpty()) {
            queryBuilder.append(" AND c.id = ? ");
            params.add(Integer.parseInt(interest)); // FIXME: ¿PORQUE ESTAMOS RECIBIENDO UN STRING CON EL ID DEL DESTINO!?!?!?
        }

        return jdbcTemplate.query(queryBuilder.toString(), JOURNEY_ROW_MAPPER, params.toArray());
    }

    @Override
    public List<Journey> findByFilters(final long userId, final String destination, final LocalDate startDate, final LocalDate endDate, final String interest) {

        // FIXME: ¿porque el !interest.isEmpty()?
        final StringBuilder queryBuilder = new StringBuilder((interest != null && !interest.isEmpty()) ? SQL_BASE_INTEREST : SQL_BASE);

        final List<Object> params = new ArrayList<>();

        queryBuilder.append(" AND u.id != ? "); // journey.user_id != ?
        params.add(userId);

        if (destination != null && !destination.isEmpty()) {
            queryBuilder.append(" AND ci2.id = ? ");
            params.add(Integer.parseInt(destination)); // FIXME: ¿PORQUE ESTAMOS RECIBIENDO UN STRING CON EL ID DEL DESTINO!?!?!?
        }
        if (endDate != null) {
            queryBuilder.append(" AND j.start_date <= ? ");
            params.add(Date.valueOf(endDate));
        }
        if (startDate != null) {
            queryBuilder.append(" AND j.end_date >= ? ");
            params.add(Date.valueOf(startDate));
        }
        if (interest != null && !interest.isEmpty()) {
            queryBuilder.append(" AND c.id = ? ");
            params.add(Integer.parseInt(interest)); // FIXME: ¿PORQUE ESTAMOS RECIBIENDO UN STRING CON EL ID DEL DESTINO!?!?!?
        }

        return jdbcTemplate.query(queryBuilder.toString(), JOURNEY_ROW_MAPPER, params.toArray());
    }

    @Override
    public List<Journey> findByOriginCity(final long originCityId) {
        return jdbcTemplate.query(SQL_FIND_BY_ORIGIN_CITY, JOURNEY_ROW_MAPPER, originCityId);
    }

    @Override
    public List<Journey> findByOriginUniversity(final long originUniversityId) {
        return jdbcTemplate.query(SQL_FIND_BY_ORIGIN_UNIVERSITY, JOURNEY_ROW_MAPPER, originUniversityId);
    }


    @Override
    public Optional<Journey> findByUserId(final long userId) {
        return jdbcTemplate.query(SQL_FIND_BY_USER_ID, JOURNEY_ROW_MAPPER, userId).stream().findFirst();
    }

    private Optional<Journey> findByUserIdDeleted(final long userId) {
        return jdbcTemplate.query(SQL_FIND_BY_USER_ID_DELETED, JOURNEY_ROW_MAPPER, userId).stream().findFirst();
    }

    @Override
    public Optional<Journey> findByUserEmail(final String email) {
        return jdbcTemplate.query(SQL_FIND_BY_USER_EMAIL, JOURNEY_ROW_MAPPER, email).stream().findFirst();
    }


    @Override
    public List<Journey> getJourneysByUser(final String email) {
        return jdbcTemplate.query(SQL_FIND_BY_USER_EMAIL, JOURNEY_ROW_MAPPER, email);
    }

    @Override
    public void delete(final long id) {
        LOGGER.info("Setting journey {} as deleted", id);
        final int updatedRows = jdbcTemplate.update("UPDATE journeys SET deleted = TRUE WHERE id = ?;", id);
        if (updatedRows == 0) {
            LOGGER.warn("No journey found with id {}", id);
        }
    }

    @Override
    public void deletionMessage(final long id, final String message) {
        LOGGER.info("Setting deletion message {} for journey {}", message, id);
        final int updatedRows = jdbcTemplate.update("UPDATE journeys SET deleted_message = ? WHERE id = ?;", message, id);
        if (updatedRows == 0) {
            LOGGER.warn("No journey found with id {}", id);
        }

    }

    @Override
    public List<Journey> getOthersJourneys(final long userId) {
        return jdbcTemplate.query(SQL_FIND_OTHERS_BY_USER_ID, JOURNEY_ROW_MAPPER, userId);
    }

    @Override
    public void updateDates(final long journeyId, final LocalDate startDate, final LocalDate endDate) {
        LOGGER.info("Updating dates for journey ID: {} to start: {}, end: {}", journeyId, startDate, endDate);
        final int updatedRows = jdbcTemplate.update(
                "UPDATE journeys SET start_date = ?, end_date = ? WHERE id = ?",
                Date.valueOf(startDate), Date.valueOf(endDate), journeyId
        );
        if (updatedRows == 0) {
            LOGGER.warn("No journey found with id {}", journeyId);
        }
    }

    @Override
    public void updateDescription(final long journeyId, final String description) {
        LOGGER.info("Updating description for journey ID: {}", journeyId);
        final int updatedRows = jdbcTemplate.update(
                "UPDATE journeys SET description = ? WHERE id = ?",
                description, journeyId
        );
        if (updatedRows == 0) {
            LOGGER.warn("No journey found with id {}", journeyId);
        }
    }

    @Override
    public void updateDestinationUniversity(final long journeyId, final long universityId) {
        LOGGER.info("Updating destination university for journey ID: {} to university ID: {}", journeyId, universityId);
        final int updatedRows = jdbcTemplate.update(
                "UPDATE journeys SET destination_university_id = ? WHERE id = ?",
                universityId, journeyId
        );
        if (updatedRows == 0) {
            LOGGER.warn("No journey found with id {}", journeyId);
        }
    }

    @Override
    public Page<Journey> listAll(final int page, final int size) {
        final int totalItems = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM journeys WHERE deleted = FALSE", Integer.class);
        final List<Journey> list = jdbcTemplate.query(SQL_FIND_ALL_PAGED, JOURNEY_ROW_MAPPER, size, (page-1) * size);
        return new Page<>(list, page, pageCount(totalItems, size));
    }

    @Override
    public Page<Journey> getOthersJourneys(final long userId, final int page, final int size) {
        final int totalItems = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM journeys j WHERE j.deleted = FALSE AND j.user_id != ?", Integer.class, userId);
        final List<Journey> list = jdbcTemplate.query(SQL_FIND_OTHERS_PAGED, JOURNEY_ROW_MAPPER, userId, size, offset(page, size));
        return new Page<>(list, page, pageCount(totalItems, size));
    }

    @Override
    public Page<Journey> findByFilters(final Long userId, final Long cityId, final LocalDate startDate, final LocalDate endDate, final Long interest, final int page, final int size) {

        final List<String> filters = new ArrayList<>();
        final List<Object> params = new ArrayList<>();

        final StringBuilder countQueryBuilder = new StringBuilder("SELECT COUNT(*) FROM journeys j");
        final StringBuilder queryBuilder = new StringBuilder((interest != null) ? SQL_BASE_INTEREST : SQL_BASE);


        if (interest != null) {
            countQueryBuilder.append(" JOIN users u ON j.user_id = u.id JOIN user_interest ui ON u.id = ui.user_id");
            filters.add("ui.category_id = ?");
            params.add(interest);
        }

        if (cityId != null) {
            countQueryBuilder.append(" JOIN universities un ON j.destination_university_id = un.id JOIN cities ci2 ON un.city_id = ci2.id");
            filters.add("ci2.id = ?");
            params.add(cityId);
        }

        if (userId != null) {
            filters.add("j.user_id != ?");
            params.add(userId);
        }

        if (endDate != null) {
            filters.add("j.start_date <= ?");
            params.add(Date.valueOf(endDate));
        }

        if (startDate != null) {
            filters.add("j.end_date >= ?");
            params.add(Date.valueOf(startDate));
        }

        countQueryBuilder.append(" WHERE j.deleted = FALSE ");

        if(!filters.isEmpty()) {
            countQueryBuilder.append(" AND  ").append(String.join(" AND ", filters));
            queryBuilder.append(" AND ").append(String.join(" AND ", filters));
        }

        final int totalItems = jdbcTemplate.queryForObject(countQueryBuilder.toString(), Integer.class, params.toArray());

        queryBuilder.append(" ORDER BY j.id ASC LIMIT ? OFFSET ?");

        params.add(size);
        params.add(offset(page, size));

        return new Page<>(
                jdbcTemplate.query(queryBuilder.toString(), JOURNEY_ROW_MAPPER, params.toArray()),
                page,
                pageCount(totalItems, size)
        );
    }

    // TODO: preguntar a los profes cual prefieren y que onda el warning que me tira el IDE
    public Page<Journey> findByFilters3(final Long userId, final Long cityId, final LocalDate startDate, final LocalDate endDate, final Long interest, final int page, final int size) {

        final List<Object> params = new ArrayList<>();

        final StringBuilder countQueryBuilder = new StringBuilder("SELECT COUNT(*) FROM journeys j");
        final StringBuilder queryBuilder = new StringBuilder((interest != null) ? SQL_BASE_INTEREST : SQL_BASE);
        final StringBuilder filterClause = new StringBuilder(" ");

        if (interest != null) {
            countQueryBuilder.append(" JOIN users u ON j.user_id = u.id JOIN user_interest ui ON u.id = ui.user_id");
            filterClause.append(" AND ui.category_id = ? ");
            params.add(interest);
        }

        if (cityId != null) {
            countQueryBuilder.append(" JOIN universities un2 ON j.destination_university_id = un2.id JOIN cities ci2 ON un2.city_id = ci2.id");
            filterClause.append(" AND ci2.id = ? ");
            params.add(cityId);
        }

        if (userId != null) {
            filterClause.append(" AND j.user_id != ? ");
            params.add(userId);
        }

        if (endDate != null) {
            filterClause.append(" AND j.start_date <= ? ");
            params.add(Date.valueOf(endDate));
        }

        if (startDate != null) {
            filterClause.append(" AND j.end_date >= ? ");
            params.add(Date.valueOf(startDate));
        }

        countQueryBuilder.append(" WHERE j.deleted = FALSE ").append(filterClause);
        queryBuilder.append(filterClause);
        queryBuilder.append(" ORDER BY j.id ASC LIMIT ? OFFSET ?");

        final int totalItems = jdbcTemplate.queryForObject(countQueryBuilder.toString(), Integer.class, params.toArray());


        params.add(size);
        params.add(offset(page, size));

        return new Page<>(
                jdbcTemplate.query(queryBuilder.toString(), JOURNEY_ROW_MAPPER, params.toArray()),
                page,
                pageCount(totalItems, size)
        );
    }


    @Override
    public Page<Journey> findByOriginCity(final long originCityId, final int page, final int size) {
        final int totalItems = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM journeys j
                JOIN users u ON j.user_id = u.id
                JOIN universities un ON u.university = un.id
                JOIN cities c ON un.city_id = c.id
                WHERE j.deleted = FALSE AND c.id = ?
                """,
                Integer.class,
                originCityId
        );

        return new Page<>(
                jdbcTemplate.query(SQL_FIND_BY_ORIGIN_CITY_PAGED, JOURNEY_ROW_MAPPER, originCityId, size, offset(page, size)),
                page,
                pageCount(totalItems, size)
        );
    }

        @Override
        public Page<Journey> searchJourneys(final String search, Long userId, String orderBy, String direction,
                                            Long cityId, LocalDate startDate, LocalDate endDate, Long interest,
                                            boolean isPast, boolean isUpcoming, boolean isMyDestination,
                                            int page, int size) {
            final String searchPattern = likePattern(search);

            final List<String> filters = new ArrayList<>();
            final List<Object> params = new ArrayList<>();

            final StringBuilder countQueryBuilder = new StringBuilder("SELECT COUNT(*) FROM journeys j");
            final StringBuilder queryBuilder = new StringBuilder((interest != null) ? SQL_BASE_INTEREST : SQL_BASE);


            if (interest != null) {
                countQueryBuilder.append(" JOIN users u ON j.user_id = u.id JOIN user_interest ui ON u.id = ui.user_id");
                filters.add("ui.category_id = ?");
                params.add(interest);
            }

            if (cityId != null) {
                countQueryBuilder.append(" JOIN universities un2 ON j.destination_university_id = un2.id JOIN cities ci2 ON un2.city_id = ci2.id");
                filters.add("ci2.id = ?");
                params.add(cityId);
            }

            if (userId != null) {
                filters.add("j.user_id != ?");
                params.add(userId);
            }

            if (endDate != null) {
                filters.add("j.start_date <= ?");
                params.add(Date.valueOf(endDate));
            }

            if (startDate != null) {
                filters.add("j.end_date >= ?");
                params.add(Date.valueOf(startDate));
            }

            if(search != null && !search.isEmpty()) {
                if(cityId == null) {
                    countQueryBuilder.append(" JOIN universities un2 ON j.destination_university_id = un2.id JOIN cities ci2 ON un2.city_id = ci2.id ");
                }
                if(interest == null) {
                    countQueryBuilder.append(" JOIN users u ON j.user_id = u.id ");
                }
                filters.add(SQL_SEARCH_WHERE_CLAUSE);
                params.add(searchPattern);
                params.add(searchPattern);
                params.add(searchPattern);
            }
            if (isUpcoming) {
                filters.add("j.start_date >= ?");
                params.add(Date.valueOf(LocalDate.now()));
            }
            if (isPast) {
                filters.add("j.end_date <= ?");
                params.add(Date.valueOf(LocalDate.now()));
            }
            if (isMyDestination) {
                countQueryBuilder.append(" JOIN universities un2 ON j.destination_university_id = un2.id JOIN cities ci2 ON un2.city_id = ci2.id");
                filters.add(" ci2.id = ( SELECT ci2.id FROM journeys j JOIN universities un2 ON j.destination_university_id = un2.id JOIN cities ci2 ON un2.city_id = ci2.id WHERE j.user_id = ? LIMIT 1) ");
                params.add(userId);
            }

            countQueryBuilder.append(" WHERE j.deleted = FALSE ");

            if(!filters.isEmpty()) {
                countQueryBuilder.append(" AND  ").append(String.join(" AND ", filters));
                queryBuilder.append(" AND ").append(String.join(" AND ", filters));
            }

            if (orderBy != null && !orderBy.isEmpty()) {
                if(orderBy.equals("city")){
                    orderBy= "ci2.name";
                }
                if(orderBy.equals("interest")){
                    orderBy= "c.name";
                }
                queryBuilder.append(" ORDER BY ").append(orderBy);
            } else {
                queryBuilder.append(" ORDER BY j.id");
            }

            final int totalItems = jdbcTemplate.queryForObject(countQueryBuilder.toString(), Integer.class, params.toArray());

            queryBuilder.append(" LIMIT ? OFFSET ? ");

            params.add(size);
            params.add(offset(page, size));

            return new Page<>(
                    jdbcTemplate.query(queryBuilder.toString(), JOURNEY_ROW_MAPPER, params.toArray()),
                    page,
                    pageCount(totalItems, size)
            );
        }

    @Override
    public Page<Journey> searchJourneys(final String search, int page, int size){
        final String searchPattern = likePattern(search);

        final int totalItems = jdbcTemplate.queryForObject(
                SQL_SEARCH_COUNT,
                Integer.class,
                searchPattern, searchPattern, searchPattern //, searchPattern, searchPattern, searchPattern, searchPattern
        );

        final List<Journey> list = jdbcTemplate.query(
                SQL_SEARCH_PAGED,
                JOURNEY_ROW_MAPPER,
                searchPattern, searchPattern, searchPattern, /*searchPattern, searchPattern, searchPattern, searchPattern,*/ size, offset(page, size)
        );

        return new Page<>(
                list,
                page,
                pageCount(totalItems, size)
        );
    }


    @Override
    public void updateData(final long journeyId, final University destinationUniversity, final LocalDate startDate, final LocalDate endDate, final String description) {
        LOGGER.info("Updating uni {}, startDate {}, endDate {}, desc '{}' for journey {}", destinationUniversity, startDate, endDate, description, journeyId);
        final int updatedRows = jdbcTemplate.update("""
        UPDATE journeys
           SET destination_university_id = ?,
               start_date = ?,
               end_date = ?,
               description = ?,
               deleted = FALSE
         WHERE id = ?
        """,
                destinationUniversity.getId(),
                Date.valueOf(startDate),
                Date.valueOf(endDate),
                description,
                journeyId
        );
        if (updatedRows == 0) {
            LOGGER.warn("No journey found with id {}", journeyId);
        }

    }


    //@TODO hacer bien la pagina , lo deje hardcodeado
    /*@Override
    public Page<Journey> getRecommendedJourneys(final String email) {
        LOGGER.debug("Querying DB for recommended journeys for usermail {}", email);

        final String query = """
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
                            u.blocked AS user_blocked,
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
                            CASE WHEN (j.start_date, j.end_date) OVERLAPS (uj.user_start, uj.user_end) THEN 15 ELSE 0 END AS timing_match_score,
                            CASE WHEN j.destination_university_id = ud.university AND (uj.user_start IS NULL OR NOT (j.start_date, j.end_date) OVERLAPS (uj.user_start, uj.user_end)) THEN 50 ELSE 0 END AS origin_uni_match_off_travel_score,
                            CASE WHEN dest_univ.city_id = (SELECT city_id FROM universities WHERE id = ud.university) AND (uj.user_start IS NULL OR NOT (j.start_date, j.end_date) OVERLAPS (uj.user_start, uj.user_end)) THEN 30 ELSE 0 END AS origin_city_match_off_travel_score
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
                        WHERE j.user_id != ud.id AND j.deleted = FALSE
                    )
            
               SELECT
                   journey_id,
                   user_id,
                   user_language,
                   user_email,
                   user_username,
                   user_firstname,
                   user_lastname,
                   user_blocked,
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
               WHERE (university_match_score + city_match_score + interest_match_score + timing_match_score + origin_uni_match_off_travel_score + origin_city_match_off_travel_score) > 0
               ORDER BY
                   (university_match_score + city_match_score + interest_match_score + timing_match_score + origin_uni_match_off_travel_score + origin_city_match_off_travel_score) DESC;
            """;
            return jdbcTemplate.query(query, JOURNEY_ROW_MAPPER, email);
    }*/
    @Override
    public Page<Journey> getRecommendedJourneys(final String email, final int page, final int size) {
        LOGGER.debug("Querying recommended journeys for user {} - page {}, size {}", email, page, size);

        final int offset = offset(page, size);

        final String baseQuery = """
           WITH user_data AS (
               SELECT id, university
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
                   u.blocked AS user_blocked,
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
                   CASE WHEN j.destination_university_id = uj.university_id THEN 50 ELSE 0 END AS university_match_score,
                   CASE WHEN dest_univ.city_id = uj.city_id THEN 30 ELSE 0 END AS city_match_score,
                   COALESCE((
                       SELECT SUM(ui.score) * 3
                       FROM user_interest journey_ui
                       JOIN user_interests ui ON ui.category_id = journey_ui.category_id
                       WHERE journey_ui.user_id = j.user_id
                   ), 0) AS interest_match_score,
                   CASE WHEN (j.start_date, j.end_date) OVERLAPS (uj.user_start, uj.user_end) THEN 15 ELSE 0 END AS timing_match_score,
                   CASE WHEN j.destination_university_id = ud.university AND (uj.user_start IS NULL OR NOT (j.start_date, j.end_date) OVERLAPS (uj.user_start, uj.user_end)) THEN 50 ELSE 0 END AS origin_uni_match_off_travel_score,
                   CASE WHEN dest_univ.city_id = (SELECT city_id FROM universities WHERE id = ud.university) AND (uj.user_start IS NULL OR NOT (j.start_date, j.end_date) OVERLAPS (uj.user_start, uj.user_end)) THEN 30 ELSE 0 END AS origin_city_match_off_travel_score
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
               WHERE j.user_id != ud.id AND j.deleted = FALSE
           )
           SELECT *
           FROM journey_scores
           WHERE (university_match_score + city_match_score + interest_match_score + timing_match_score + origin_uni_match_off_travel_score + origin_city_match_off_travel_score) > 0
           ORDER BY (university_match_score + city_match_score + interest_match_score + timing_match_score + origin_uni_match_off_travel_score + origin_city_match_off_travel_score) DESC
           LIMIT ? OFFSET ?
           """;

        final String countQuery = """
           WITH user_data AS (
               SELECT id, university
               FROM users
               WHERE email = ?
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
               SELECT j.id
               FROM journeys j
               JOIN users u ON j.user_id = u.id
               JOIN universities dest_univ ON j.destination_university_id = dest_univ.id
               JOIN cities dest_city ON dest_univ.city_id = dest_city.id
               JOIN universities uu ON u.university = uu.id
               JOIN cities uc ON uu.city_id = uc.id
               CROSS JOIN user_journey uj
               CROSS JOIN user_data ud
               WHERE j.user_id != ud.id AND j.deleted = FALSE
           )
           SELECT COUNT(*) FROM journey_scores
           """;

        final int totalItems = jdbcTemplate.queryForObject(countQuery, Integer.class, email);

        final List<Journey> journeys = jdbcTemplate.query(baseQuery, JOURNEY_ROW_MAPPER, email, size, offset);

        return new Page<>(journeys, page, pageCount(totalItems, size));
    }


}
/*

@Override
public void updateDates(final long journeyId, final LocalDate startDate, final LocalDate endDate) {
    LOGGER.debug("Updating dates for journey ID: {} to start: {}, end: {}", journeyId, startDate, endDate);
    updateJourneyField(journeyId, "start_date = ?, end_date = ?", new Object[]{startDate, endDate}, "Journey date");
}

@Override
public void updateDescription(long journeyId, String description) {
    LOGGER.debug("Updating description for journey ID: {}", journeyId);
    updateJourneyField(journeyId, "description = ?", new Object[]{description}, "Journey description");
}

@Override
public void updateDestinationUniversity(long journeyId, long universityId) {
    LOGGER.debug("Updating destination university for journey ID: {} to university ID: {}", journeyId, universityId);
    updateJourneyField(journeyId, "destination_university_id = ?", new Object[]{universityId}, "Journey destination");
}

// Helper method to update journey fields

private void updateJourneyField(long journeyId, String setClause, Object[] params, String fieldDescription) {
    Object[] queryParams = new Object[params.length + 1];
    System.arraycopy(params, 0, queryParams, 0, params.length);
    queryParams[params.length] = journeyId;

    int rowsAffected = jdbcTemplate.update(
            "UPDATE journeys SET " + setClause + " WHERE id = ?",
            queryParams
    );

    if (rowsAffected == 0) {
        LOGGER.warn("{} update failed: Journey with ID {} not found", fieldDescription, journeyId);
    }
}
 */
