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

import static ar.edu.itba.paw.persistence.HibernateDaoUtils.fetchPageByIds;
import static ar.edu.itba.paw.persistence.HibernateDaoUtils.pageCount;
import static ar.edu.itba.paw.persistence.JdbcDaoUtils.likePattern;

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
        return Optional.ofNullable(em.find(Event.class, eventId));
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
        LEFT JOIN event_attendances ea ON ea.event_id = e.id AND ea.user_id = :userId
        WHERE e.deleted = FALSE
          AND e.event_date >= CURRENT_DATE
          AND e.user_id != :userId
        ORDER BY
            (e.attendees_limit IS NOT NULL AND e.attendees_count >= e.attendees_limit),
            (ea.user_id IS NOT NULL),
            COALESCE(e.attendees_count, 0) DESC,
            e.event_date
    """;

        final String jpqlFetch = "FROM Event e WHERE e.id IN :ids"; //fixme falta orderBy.

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

        final String jpqlFetch = "FROM Event e WHERE e.id IN :ids ORDER BY e.date DESC"; //@todo check orderBY.

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
    }       //@TODO preuntar. !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! JDBC ! MODELO EVENT RESPONSE???!! :/


    @Override
    public Page<Event> findByUserEmail(String email, PageParams pageParams) {
        final String countSql = """
        SELECT COUNT(*)
        FROM events e
        JOIN users us ON e.user_id = us.id
        WHERE e.deleted = FALSE AND us.email = :email
    """;

        final String idSql = """
        SELECT e.id
        FROM events e
        JOIN users us ON e.user_id = us.id
        WHERE e.deleted = FALSE AND us.email = :email
        ORDER BY e.event_date DESC
    """;

        final String jpqlFetch = """
        FROM Event e WHERE e.id IN :ids ORDER BY e.date DESC
    """;  //fixme falta el order by.

        Map<String, Object> params = Map.of("email", email);

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
        SELECT e.id
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
        ORDER BY e.event_date
    """;

        final String jpqlFetch = """
        FROM Event e
        WHERE e.id IN :ids
    """;  //fixme falta el order by.

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
        SELECT id
        FROM events
        WHERE deleted = FALSE AND event_date >= CURRENT_DATE
        ORDER BY
            (attendees_limit IS NOT NULL AND attendees_count >= attendees_limit) ASC,
            COALESCE(attendees_count, 0) DESC,
            event_date
    """;

        final String jpqlFetch = """
        FROM Event e WHERE e.id IN :ids
    """;    //fixme falta el order by.

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
    """;  //fixme falta el order by.

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
    """;  //fixme falta el order by.

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
    public Page<Event> findUpcomingEventsByAttendee(long userId, PageParams pageParams) {
        return findAllWithFilters(
                userId,
                null, // searchTerm
                null, // sortBy
                SortDirection.DESC, // direction
                null, // destination
                null, // startDate
                null, // endDate
                null, // interest
                false, // isPast
                true, // isUpcoming
                true, // attending
                pageParams
        );
    }

    @Override
    public Page<Event> findFinishedEventsByAttendee(long userId, PageParams pageParams) {
        return  findAllWithFilters(
                userId,
                null, // searchTerm
                null, // sortBy
                SortDirection.DESC, // direction
                null, // destination
                null, // startDate
                null, // endDate
                null, // interest
                true, // isPast
                false, // isUpcoming
                true, // attending
                pageParams
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

    private String getSortColumn(SortFieldEvent sortBy) {
        if (sortBy == null) {
            return "e.id";
        }
        return switch (sortBy) {
            case ATTENDEES -> "e.attendees_count";
            case DATE      -> "e.event_date";
            default        -> "e.id";
        };
    }
    @Override
    public Page<Event> findAllWithFilters(Long userId, String searchTerm,
                                          SortFieldEvent sortBy, SortDirection direction, String destination,
                                          LocalDate startDate, LocalDate endDate, String interest,
                                          boolean isPast, boolean isUpcoming, boolean attending, PageParams pageParams) {

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
            filters.add("e.user_id != :userId");
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
            paramMap.put("userId", userId); // ya estaba puesto arriba
        }

        if (isPast) {
            filters.add("e.event_date < CURRENT_DATE");
        } else if (isUpcoming) {
            filters.add("e.event_date >= CURRENT_DATE");
        }

        // Build WHERE clause
        countSql.append(" WHERE e.deleted = FALSE ");
        idSql.append(" WHERE e.deleted = FALSE ");
        if (!filters.isEmpty()) {
            String clause =  String.join(" AND ", filters);

            countSql.append(" AND ").append(clause);
            idSql.append(" AND " ).append(clause);
        }

        //idSql.append(" GROUP BY e.id");

        String sortColumn = getSortColumn(sortBy);
        String dir = (direction == SortDirection.DESC) ? "DESC" : "ASC";

        idSql.append(" ORDER BY ").append(sortColumn).append(" ").append(dir);

        // JPQL fetch
        final String jpqlFetch = "FROM Event e WHERE e.id IN :ids";

        // Delegamos al helper
        return fetchPageByIds(em, countSql.toString(), idSql.toString(), paramMap, jpqlFetch, Event.class, pageParams, Map.of());
    }







}
