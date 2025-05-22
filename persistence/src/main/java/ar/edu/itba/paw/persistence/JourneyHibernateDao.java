package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.JourneyDao;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.enums.SortDirection;
import ar.edu.itba.paw.models.enums.SortFieldJourney;
import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;
import java.util.*;
import static ar.edu.itba.paw.persistence.HibernateDaoUtils.fetchPageByIds;
import static ar.edu.itba.paw.persistence.HibernateDaoUtils.likePattern;

@Repository
public class JourneyHibernateDao implements JourneyDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Journey create(User user, University university, LocalDate startDate, LocalDate endDate, String description) {
        final Journey journey = new Journey(user, startDate, endDate, university, description);
        em.persist(journey);
        return journey;
    }

    @Override
    public Optional<Journey> findById(long id) {

        return Optional.ofNullable(em.find(Journey.class, id));
    }




    @Override
    public Page<Journey> findAll(final PageParams pageParams) {
        final String countSql = """
        SELECT COUNT(*) FROM journeys j
        WHERE j.deleted = FALSE
    """;

        final String idSql = """
        SELECT j.id
        FROM journeys j
        WHERE j.deleted = FALSE
        ORDER BY j.id ASC
    """;

        final String jpqlFetch = """
        FROM Journey j
        WHERE j.id IN :ids
    """;

        return fetchPageByIds(em, countSql, idSql, Map.of(), jpqlFetch, Journey.class, pageParams, Map.of());
    }

    @Override
    public Page<Journey> findByOriginCity(final long originCityId, final PageParams pageParams) {
        final String countSql = """
        SELECT COUNT(*)
        FROM journeys j
        JOIN users u ON j.user_id = u.id
        JOIN universities un ON u.university = un.id
        JOIN cities c ON un.city_id = c.id
        WHERE j.deleted = FALSE AND c.id = :originCityId
    """;

        final String idSql = """
        SELECT j.id
        FROM journeys j
        JOIN users u ON j.user_id = u.id
        JOIN universities un ON u.university = un.id
        JOIN cities c ON un.city_id = c.id
        WHERE j.deleted = FALSE AND c.id = :originCityId
        ORDER BY j.id ASC
    """;

        final String jpqlFetch = """
        FROM Journey j
        WHERE j.id IN :ids
        ORDER BY j.id ASC
    """;

        return fetchPageByIds(
                em,
                countSql,
                idSql,
                Map.of("originCityId", originCityId),
                jpqlFetch,
                Journey.class,
                pageParams,
                Map.of()
        );
    }

    @Override
    public Page<Journey> search(String search, PageParams pageParams) {
        return null;
    }

    private String getOrderByColumn(SortFieldJourney orderBy, boolean jql) {
        if(orderBy == null){
            return "j.id";
        }
        return switch (orderBy) {
            case START_DATE -> jql? "startDate":"start_date";
            case END_DATE   -> jql? "endDate":"end_date";
            default         -> "j.id";
        };
    }
    @Override
    public Page<Journey> search(final String searchTerm, final Long userId, final SortFieldJourney orderBy, final SortDirection direction,
                                final String city, final LocalDate startDate, final LocalDate endDate, final String interest,
                                final boolean isPast, final boolean isUpcoming, final boolean isMyDestination, final boolean isOngoing,
                                final PageParams pageParams) {

        final String pattern = likePattern(searchTerm);

        final List<String> filters = new ArrayList<>();
        final Map<String, Object> paramMap = new HashMap<>();

        StringBuilder countSql = new StringBuilder("SELECT COUNT(DISTINCT j.id) FROM journeys j");

//        StringBuilder idSql = new StringBuilder(" SELECT DISTINCT j.id FROM journeys j");
        StringBuilder idSql = new StringBuilder("SELECT j.id FROM journeys j");

        boolean joinedUsers = false;
        boolean joinedUnis = false;

        if (interest != null && !interest.isEmpty()) {
            countSql.append(" JOIN users u ON j.user_id = u.id")
                    .append(" JOIN user_interest ui ON u.id = ui.user_id")
                    .append(" JOIN category c ON ui.category_id = c.id ");
            idSql.append(" JOIN users u ON j.user_id = u.id")
                    .append(" JOIN user_interest ui ON u.id = ui.user_id")
                    .append(" JOIN category c ON ui.category_id = c.id ");
            filters.add("c.name = :interest");
            paramMap.put("interest", interest);
            joinedUsers = true;
        }

        if (city != null && !city.isEmpty()) {
            countSql.append(" JOIN universities un2 ON j.destination_university_id = un2.id")
                    .append(" JOIN cities ci2 ON un2.city_id = ci2.id ");
            idSql.append(" JOIN universities un2 ON j.destination_university_id = un2.id")
                    .append(" JOIN cities ci2 ON un2.city_id = ci2.id ");
            filters.add("ci2.name = :city");
            paramMap.put("city", city);
            joinedUnis = true;
        }

        if (userId != null) {
            filters.add("j.user_id != :userId");
            paramMap.put("userId", userId);
        }

        if (startDate != null) {
            filters.add("j.end_date >= :startDate");
            paramMap.put("startDate", startDate);
        }

        if (endDate != null) {
            filters.add("j.start_date <= :endDate");
            paramMap.put("endDate", endDate);
        }

        if (searchTerm != null && !searchTerm.isEmpty()) {
            if (!joinedUnis && !isMyDestination) {
                countSql.append(" JOIN universities un2 ON j.destination_university_id = un2.id")
                        .append(" JOIN cities ci2 ON un2.city_id = ci2.id ");
                idSql.append(" JOIN universities un2 ON j.destination_university_id = un2.id")
                        .append(" JOIN cities ci2 ON un2.city_id = ci2.id ");
                joinedUnis = true;
            }
            if (!joinedUsers) {
                countSql.append(" JOIN users u ON j.user_id = u.id ");
                idSql.append(" JOIN users u ON j.user_id = u.id ");
                joinedUsers = true;
            }
            filters.add("""
            (
                LOWER(u.username) LIKE :pattern
                OR LOWER(un2.name) LIKE :pattern
                OR LOWER(ci2.name) LIKE :pattern
            )
        """);
            paramMap.put("pattern", pattern);
        }

        if (isOngoing) {
            filters.add("j.start_date <= :now AND j.end_date >= :now");
            paramMap.put("now", LocalDate.now());
        } else {
            if (isUpcoming) {
                filters.add("j.start_date > :now");
                paramMap.put("now", LocalDate.now());
            }
            if (isPast) {
                filters.add("j.end_date < :now");
                paramMap.put("now", LocalDate.now());
            }
        }

        if (isMyDestination && userId != null) {
            countSql.append(" JOIN universities un2 ON j.destination_university_id = un2.id")
                    .append(" JOIN cities ci2 ON un2.city_id = ci2.id ");
            idSql.append(" JOIN universities un2 ON j.destination_university_id = un2.id")
                    .append(" JOIN cities ci2 ON un2.city_id = ci2.id ");
            filters.add("""
            ci2.id = (
                SELECT ci3.id
                FROM journeys j2
                JOIN universities un3 ON j2.destination_university_id = un3.id
                JOIN cities ci3 ON un3.city_id = ci3.id
                WHERE j2.user_id = :userId
                LIMIT 1
            )
        """);
            paramMap.put("userId", userId);
        }

        countSql.append(" WHERE j.deleted = FALSE ");
        idSql.append(" WHERE j.deleted = FALSE ");
        if (!filters.isEmpty()) {
            String whereClause = " AND " + String.join(" AND ", filters);
            countSql.append(whereClause);
            idSql.append(whereClause);
        }

        String orderColumn = getOrderByColumn(orderBy, false);
        String dir = (direction == SortDirection.DESC) ? "DESC" : "ASC";


        idSql.append(" GROUP BY j.id");

        idSql.append(" ORDER BY ").append(orderColumn).append(" ").append(dir);

        String jpqlFetch = "FROM Journey j WHERE j.id IN :ids ORDER BY j." + getOrderByColumn(orderBy, true)  + " " + dir;

        return fetchPageByIds(em, countSql.toString(), idSql.toString(), paramMap, jpqlFetch, Journey.class, pageParams,Map.of());
    }




    @Override
    public Page<Journey> findRecommended(final String email, final PageParams pageParams) {
        final String countSql = """
        WITH user_data AS (
            SELECT id, university
            FROM users
            WHERE email = :email
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

        final String idSql = """
        WITH user_data AS (
            SELECT id, university
            FROM users
            WHERE email = :email
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
                j.id,
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
        SELECT j.id
        FROM journey_scores j
        WHERE (
            university_match_score +
            city_match_score +
            interest_match_score +
            timing_match_score +
            origin_uni_match_off_travel_score +
            origin_city_match_off_travel_score
        ) > 0
        ORDER BY (
            university_match_score +
            city_match_score +
            interest_match_score +
            timing_match_score +
            origin_uni_match_off_travel_score +
            origin_city_match_off_travel_score
        ) DESC
    """; //fixme ES POSIBLE QUE HAYAN JOINS innecesarios porque estaban por el ROWMAPPER.

        final String jpqlFetch = "FROM Journey j WHERE j.id IN :ids";

        return fetchPageByIds(em, countSql, idSql, Map.of("email", email), jpqlFetch, Journey.class, pageParams,Map.of());
    }

}



//
//@Override
//public void delete(long id) {
//    final Journey journey = em.find(Journey.class, id);
//    if (journey != null) {
//        journey.setDeleted(true);
//        em.merge(journey);
//    }
//
//}

//
//@Override
//public void update(long journeyId, University destinationUniversity, LocalDate startDate, LocalDate endDate, String description) {
//    final Journey journey = em.find(Journey.class, journeyId);
//    if (journey != null) {
//        journey.setDestinationUniversity(destinationUniversity);
//        journey.setStartDate(startDate);
//        journey.setEndDate(endDate);
//        journey.setDescription(description);
//    }
//}

//
//    @Override
//    public void updateDeletionMessage(long id, String message) {
//        final Journey journey = em.find(Journey.class, id);
//        if (journey != null) {
//            journey.setDeletionMessage(message);
//            em.merge(journey);
//        }
//    }
