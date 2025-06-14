package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.EventDao;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.enums.SortDirection;
import ar.edu.itba.paw.models.enums.SortFieldEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static ar.edu.itba.paw.persistence.HibernateDaoUtils.*;

@Repository
public class EventHibernateDao implements EventDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventHibernateDao.class);

    @PersistenceContext
    private EntityManager em;

    @Override
    public Event create(User user, City city, LocalDate date, String description, long flyerImageId,
                        String title, LocalTime time, String address, Integer attendeesLimit) {
        Event event = new Event(user, date, description, flyerImageId, city, title, time, address, attendeesLimit);
        em.persist(event);
        return event;
    }

    @Override
    public Optional<Event> findById(long eventId) {

        return  em.createQuery("FROM Event e WHERE e.id = :eventId AND e.deleted = FALSE", Event.class)
                .setParameter("eventId", eventId)
                .getResultList()
                .stream()
                .findFirst();
    }

    @Override
    public Page<Event> findTopByUser(final long userId, final PageParams pageParams) {
        final String countSql = """
        SELECT COUNT(*)
        FROM events e
        WHERE e.deleted = FALSE
          AND e.event_date >= CURRENT_DATE
          AND e.user_id != :userId
    """;

        final String idSql = """
        SELECT e.id
        FROM events e
        LEFT JOIN event_attendances ea ON ea.event_id = e.id
        LEFT JOIN event_attendances ea2 ON ea2.event_id = e.id AND ea2.user_id = :userId
        WHERE e.deleted = FALSE
          AND e.event_date >= CURRENT_DATE
          AND e.user_id != :userId
        GROUP BY e.id, ea2.user_id, e.attendees_limit, e.event_date
        ORDER BY
            (e.attendees_limit IS NOT NULL AND COUNT(ea.user_id) >= e.attendees_limit),
            (ea2.user_id IS NOT NULL),
            COUNT(ea.user_id) DESC,
            e.event_date
    """;


        final String jpqlFetch = "FROM Event e WHERE e.id IN :ids";

        return fetchPageByIds(
                em,
                countSql,
                idSql,
                Map.of("userId", userId),
                jpqlFetch,
                Event.class,
                pageParams,
                Map.of()
        );
    }

    @Override
    public Page<Event> search(final String searchTerm, final PageParams pageParams) {
        final String pattern = likePattern(searchTerm);

        final String countSql = """
        SELECT COUNT(*)
        FROM events e
        JOIN cities c ON e.city_id = c.id
        JOIN users us ON e.user_id = us.id
        WHERE e.deleted = FALSE AND (
            LOWER(e.title) LIKE LOWER( :pattern )
            OR LOWER(c.name) LIKE LOWER( :pattern )
            OR LOWER(us.username) LIKE LOWER( :pattern )
        )
    """;

        final String idSql = """
        SELECT e.id
        FROM events e
        JOIN cities c ON e.city_id = c.id
        JOIN users us ON e.user_id = us.id
        WHERE e.deleted = FALSE AND (
            LOWER(e.title) LIKE LOWER( :pattern )
            OR LOWER(c.name) LIKE LOWER( :pattern )
            OR LOWER(us.username) LIKE LOWER( :pattern )
        )
        ORDER BY e.event_date DESC
    """;

        final String jpqlFetch = "FROM Event e WHERE e.id IN :ids ORDER BY e.date DESC";

        return fetchPageByIds(
                em,
                countSql,
                idSql,
                Map.of("pattern", pattern),
                jpqlFetch,
                Event.class,
                pageParams,
                Map.of()
        );
    }


    @Override
    public Optional<CountryAttendeeCount> findTopAttendeeCountry(final long eventId) {
        Query query = em.createNativeQuery("""
        SELECT co.name AS country_name, COUNT(*) AS attendee_count
        FROM event_attendances ea
        JOIN users u ON ea.user_id = u.id
        JOIN universities uni ON u.university = uni.id
        JOIN cities ci ON uni.city_id = ci.id
        JOIN countries co ON ci.country_id = co.id
        WHERE ea.event_id = :eventId
        GROUP BY co.name
        ORDER BY attendee_count DESC
        LIMIT 1
    """);
        query.setParameter("eventId", eventId);

        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();

        return results.stream()
                .findFirst()
                .map(row -> new CountryAttendeeCount((String) row[0], ((Number) row[1]).intValue()));
    }



    @Override
    public Page<Event> findByUserId(long userId, PageParams pageParams) {
        final String countSql = """
        SELECT COUNT(*)
        FROM events e
        WHERE e.deleted = FALSE AND e.user_id = :userId
    """;

        final String idSql = """
        SELECT e.id
        FROM events e
        WHERE e.deleted = FALSE AND e.user_id = :userId
        ORDER BY e.event_date DESC
    """;

        final String jpqlFetch = """
        FROM Event e WHERE e.id IN :ids ORDER BY e.date DESC
    """;

        Map<String, Object> params = Map.of("userId", userId);

        return fetchPageByIds(em, countSql, idSql, params, jpqlFetch, Event.class, pageParams, Map.of());
    }



    @Override
    public Page<Event> findRecommended(final long userId, final PageParams pageParams) {

        final String countSql = """
        SELECT COUNT(*)
        FROM events e
        JOIN users us ON e.user_id = us.id
        JOIN universities un ON us.university = un.id
        JOIN cities c ON e.city_id = c.id
        WHERE e.event_date >= CURRENT_DATE
          AND us.id != :userId
          AND e.deleted = FALSE
          AND c.id = (
              SELECT c2.id
              FROM users u
              JOIN universities un2 ON u.university = un2.id
              JOIN cities c2 ON un2.city_id = c2.id
              WHERE u.id = :userId
          )
    """;

        final String idSql = """
                WITH user_data AS (
                    SELECT u.id, c.id AS city_id
                    FROM users u
                    JOIN universities un ON u.university = un.id
                    JOIN cities c ON un.city_id = c.id
                    WHERE u.id = :userId
                )
                SELECT e.id
                FROM events e
                JOIN users us ON e.user_id = us.id
                JOIN universities un ON us.university = un.id
                JOIN cities c ON e.city_id = c.id
                JOIN user_data ud ON ud.city_id = e.city_id
                LEFT JOIN event_attendances ea_all ON ea_all.event_id = e.id
                LEFT JOIN event_attendances ea_user ON ea_user.event_id = e.id AND ea_user.user_id = ud.id
                WHERE e.event_date >= CURRENT_DATE
                  AND us.id != :userId
                  AND e.deleted = FALSE
                  AND c.id = (
                      SELECT c2.id
                      FROM users u
                      JOIN universities un2 ON u.university = un2.id
                      JOIN cities c2 ON un2.city_id = c2.id
                      WHERE u.id = :userId
                  )
                GROUP BY e.id, e.attendees_limit, ea_user.user_id, e.user_id, e.event_date
                ORDER BY
                    (e.attendees_limit IS NOT NULL AND COUNT(ea_all.user_id) >= e.attendees_limit),
                    (ea_user.user_id IS NOT NULL), 
                    (e.user_id = :userId),
                    COUNT(ea_all.user_id) DESC,
                    e.event_date  
                """;

        final String jpqlFetch = """
        FROM Event e
        WHERE e.id IN :ids
        """;

        Map<String, Object> params = Map.of("userId", userId);

        return fetchPageByIds(em, countSql, idSql, params, jpqlFetch, Event.class, pageParams, Map.of());
    }


    @Override
    public Page<Event> findTop(final PageParams pageParams) {
        final String countSql = """
        SELECT COUNT(*)
        FROM events
        WHERE deleted = FALSE AND event_date >= CURRENT_DATE
    """;

        final String idSql = """
            SELECT e.id
            FROM events e
            LEFT JOIN event_attendances ea ON ea.event_id = e.id
            WHERE e.deleted = FALSE AND e.event_date >= CURRENT_DATE
            GROUP BY e.id, e.attendees_limit, e.event_date
            ORDER BY
                (e.attendees_limit IS NOT NULL AND COUNT(ea.user_id) >= e.attendees_limit),
                COUNT(ea.user_id) DESC,
                e.event_date
    """;



        final String jpqlFetch = """
        FROM Event e WHERE e.id IN :ids
        """;

        return fetchPageByIds(em, countSql, idSql, Map.of(), jpqlFetch, Event.class, pageParams, Map.of());
    }

    @Override
    public Page<Event> findAll(PageParams pageParams) {
        final String countSql = """
        SELECT COUNT(*) FROM events WHERE deleted = FALSE
    """;

        final String idSql = """
        SELECT id
        FROM events
        WHERE deleted = FALSE
        ORDER BY event_date DESC
    """;

        final String jpqlFetch = """
        FROM Event e WHERE e.id IN :ids
        ORDER BY e.date DESC
    """;

        return fetchPageByIds(em, countSql, idSql, Map.of(), jpqlFetch, Event.class, pageParams, Map.of());
    }


    @Override
    public Page<Event> findAllEventsByAttendee(long userId, PageParams pageParams) {
        final String countSql = """
        SELECT COUNT(*)
        FROM event_attendances ea
        JOIN events e ON ea.event_id = e.id
        WHERE ea.user_id = :userId AND e.user_id != :userId AND e.deleted = FALSE
    """;

        final String idSql = """
        SELECT e.id
        FROM event_attendances ea
        JOIN events e ON ea.event_id = e.id
        WHERE ea.user_id = :userId AND e.user_id != :userId AND e.deleted = FALSE
        ORDER BY e.event_date DESC
    """;

        final String jpqlFetch = """
        FROM Event e WHERE e.id IN :ids ORDER BY e.date DESC
    """;
        return fetchPageByIds(
                em,
                countSql,
                idSql,
                Map.of("userId", userId),
                jpqlFetch,
                Event.class,
                pageParams,
                Map.of()
        );
    }


    @Override
    public int countEventsCreatedByUser(long userId) {
        final String sql = """
        SELECT COUNT(*)
        FROM events e
        WHERE e.user_id = :userId AND e.deleted = FALSE
    """; //cuento los borrados o no?

        Query countQuery = em.createNativeQuery(sql);
        countQuery.setParameter("userId", userId);
        return ((Number) countQuery.getSingleResult()).intValue();
    }

    // EventHibernateDao.java

    @Override
    public Page<Event> findAllBetweenDates(LocalDate startDate, LocalDate endDate, PageParams pageParams) {
        final String countSql = """
        SELECT COUNT(*)
        FROM events e
        WHERE e.event_date BETWEEN :startDate AND :endDate
          AND e.deleted = FALSE
    """;

        final String idSql = """
        SELECT e.id
        FROM events e
        WHERE e.event_date BETWEEN :startDate AND :endDate
          AND e.deleted = FALSE
        ORDER BY e.event_date, e.event_time
    """;

        final String jpqlFetch = """
        FROM Event e
        WHERE e.id IN :ids
        ORDER BY e.date, e.time
    """;

        Map<String, Object> params = Map.of(
                "startDate", startDate,
                "endDate", endDate
        );

        return fetchPageByIds(em, countSql, idSql, params, jpqlFetch, Event.class, pageParams, Map.of());
    }

    private String getSortColumn(SortFieldEvent sortBy, boolean jql) {
        if (sortBy == null) {
            return jql? "id":"e.id";
        }
        return switch (sortBy) {
            case ATTENDEES -> jql? "attendeesCount" : "(SELECT COUNT(*) FROM event_attendances ea where ea.event_id = e.id) ";
            case DATE      -> jql ? "date":"e.event_date";
            case RATING    -> jql ? "rating":"COALESCE((SELECT AVG(r.rating) FROM ratings r WHERE r.event_id = e.id),0)";
            default        -> jql ? "id":"e.id";
        };
    }

    @Override
    public Page<Event> findAllWithFilters(Long userId, String searchTerm, SortFieldEvent sortBy,
                                          SortDirection direction, String destination, LocalDate startDate,
                                          LocalDate endDate, String interest,
                                          boolean attending, boolean isCreator, PageParams pageParams) {

        final String search = likePattern(searchTerm);

        List<String> filters = new ArrayList<>();
        Map<String, Object> paramMap = new HashMap<>();

        StringBuilder countSql = new StringBuilder("""
        SELECT COUNT(DISTINCT e.id)
        FROM events e
        JOIN users us ON e.user_id = us.id
        JOIN universities un ON us.university = un.id
        JOIN cities ci ON un.city_id = ci.id
    """);

        StringBuilder idSql = new StringBuilder("""
        SELECT e.id
        FROM events e
        JOIN users us ON e.user_id = us.id
        JOIN universities un ON us.university = un.id
        JOIN cities ci ON un.city_id = ci.id
    """);

        if (interest != null && !interest.isEmpty()) {
            countSql.append(" JOIN user_interest ui ON us.id = ui.user_id JOIN category c ON ui.category_id = c.id ");
            idSql.append(" JOIN user_interest ui ON us.id = ui.user_id JOIN category c ON ui.category_id = c.id ");
            filters.add("c.name = :interest");
            paramMap.put("interest", interest);
        }

        if (destination != null && !destination.isEmpty()) {
            countSql.append(" JOIN cities cd ON e.city_id = cd.id ");
            idSql.append(" JOIN cities cd ON e.city_id = cd.id ");
            filters.add("cd.name = :destination");
            paramMap.put("destination", destination);
        }

        if (searchTerm != null && !searchTerm.isEmpty()) {
            filters.add("""
            (
                LOWER(e.title) LIKE :search
                OR LOWER(us.username) LIKE :search
                OR LOWER(ci.name) LIKE :search
            )
        """);
            paramMap.put("search", search);
        }

        if (userId != null) {
            if(isCreator){
                filters.add("e.user_id = :userId");
            } else {
                filters.add("e.user_id != :userId");
            }
            paramMap.put("userId", userId);
        }

        if (startDate != null) {
            filters.add("e.event_date >= :startDate");
            paramMap.put("startDate", Date.valueOf(startDate)); //@TODO check, se puede sacar el valueOf?
        }

        if (endDate != null) {
            filters.add("e.event_date <= :endDate");
            paramMap.put("endDate", Date.valueOf(endDate));
        }

        if (attending && userId != null) {
            countSql.append(" LEFT JOIN event_attendances ea ON ea.event_id = e.id AND ea.user_id = :userId ");
            idSql.append(" LEFT JOIN event_attendances ea ON ea.event_id = e.id AND ea.user_id = :userId ");
            filters.add("ea.user_id IS NOT NULL");
        }


        // Build WHERE clause
        countSql.append(" WHERE e.deleted = FALSE ");
        idSql.append(" WHERE e.deleted = FALSE ");
        if (!filters.isEmpty()) {
            String clause =  String.join(" AND ", filters);

            countSql.append(" AND ").append(clause);
            idSql.append(" AND " ).append(clause);
        }

        String sortColumn = getSortColumn(sortBy, false);
        String dir = (direction == SortDirection.DESC) ? "DESC" : "ASC";

        idSql.append(" ORDER BY ").append(sortColumn).append(" ").append(dir);

        // JPQL fetch
        final String jpqlFetch = "FROM Event e WHERE e.id IN :ids ORDER BY " + getSortColumn(sortBy, true) + " " + dir;

        // Delegamos al helper
        return fetchPageByIds(em, countSql.toString(), idSql.toString(), paramMap, jpqlFetch, Event.class, pageParams, Map.of());
    }
}
