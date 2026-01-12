package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.models.*;
import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.*;

import static ar.edu.itba.paw.persistence.HibernateDaoUtils.fetchPageByIds;
import static ar.edu.itba.paw.persistence.HibernateDaoUtils.likePattern;

@Repository
public class UserHibernateDao implements UserDao {
    @PersistenceContext
    private EntityManager em;

        @Override
        public User create(String email, String username, String firstname, String lastname, University university, Career career, long profilePictureId, String password, Locale locale, boolean validated) {

            final User user = new User(email, username,  firstname, lastname, university,  career, profilePictureId, password, locale,validated);

            em.persist(user);

            return user;
        }
    @Override
    public Optional<User> findById(long id) {
        return Optional.ofNullable( em.find(User.class, id));
    }


    @Override
    public Optional<User> findByEmail(String email) {
        final TypedQuery<User> query = em.createQuery("from User as u where u.email= :email", User.class);
        query.setParameter("email", email);
        final List<User> list = query.getResultList();
        return list.isEmpty() ? Optional.empty() : Optional.of(list.getFirst());
    }


    @Override
    public boolean existsByUsername(String username) {
        final TypedQuery<User> query = em.createQuery("from User as u where u.username= :username", User.class);
        query.setParameter("username", username);
        final List<User> list = query.getResultList();
        return ! list.isEmpty();
    }

    @Override
    public boolean existsByEmail(String email) {
        return findByEmail(email).isPresent();
    }


    @Override
    public Page<User> findUsers(String search, final PageParams pageParams, Long attendingEventId,
                              Long universityId,
                              Long careerId,
                              Long interestId,
                             Boolean blocked) {
            // 1) Partes acumulables
            final List<String> joins = new ArrayList<>();
            final List<String> predicates = new ArrayList<>();
            final Map<String, Object> parameters = new HashMap<>();

            // 2) Filtros

            // --- SEARCH (requiere join con universities para un.name)
            if (search != null && !search.isBlank()) {
                maybeAddJoin(joins, "JOIN universities un ON u.university = un.id");

                final String pattern = likePattern(search);
                predicates.add("""
            (
                LOWER(u.firstname) LIKE LOWER(:pattern)
                OR LOWER(un.name) LIKE LOWER(:pattern)
                OR LOWER(u.email) LIKE LOWER(:pattern)
            )
        """);
                parameters.put("pattern", pattern);
            }

            // --- attendingEventId (requiere join event_attendances)
            if (attendingEventId != null) {
                maybeAddJoin(joins, "JOIN event_attendances ea ON u.id = ea.user_id");
                predicates.add("ea.event_id = :attendingEventId");
                parameters.put("attendingEventId", attendingEventId);
            }

            // --- Ejemplos de otros filtros que ya tenés (ajustá columnas/tablas reales)
            if (universityId != null) {
                // si u.university es FK directa (id), no necesitás join:
                predicates.add("u.university = :universityId");
                parameters.put("universityId", universityId);
            }

            if (blocked != null) {
                predicates.add("u.blocked = :blocked");
                parameters.put("blocked", blocked);
            }

            // Si careerId/interestId dependen de tablas puente, hacés join + predicate:
            if (careerId != null) {
                predicates.add("u.career_id = :careerId");
                parameters.put("careerId", careerId);
            }

            if (interestId != null) {
                maybeAddJoin(joins, "JOIN user_interest ui ON ui.user_id = u.id");
                predicates.add("ui.category_id = :interestId");
                parameters.put("interestId", interestId);
            }

            // 3) Build final SQL (count + ids) sin riesgo de orden
            final String baseFrom = "FROM users u\n";
            final String joinSql = joins.isEmpty() ? "" : String.join("\n", joins) + "\n";
            final String whereSql = predicates.isEmpty()
                    ? ""
                    : "WHERE " + String.join("\n  AND ", predicates) + "\n";

            final String countSql = "SELECT COUNT(*)\n" + baseFrom + joinSql + whereSql;
            final String idSql = "SELECT u.id\n" + baseFrom + joinSql + whereSql + "ORDER BY u.id ASC";

            final String jpqlFetch = """
        FROM User u
        WHERE u.id IN :ids
        ORDER BY u.id ASC
    """;

            // OJO: estabas pasando Map.of() en vez de parameters. Acá va parameters.
            return fetchPageByIds(
                    em,
                    countSql,
                    idSql,
                    parameters,
                    jpqlFetch,
                    User.class,
                    pageParams,
                    Map.of()
            );
        }



    /** Evita duplicar joins si dos filtros requieren el mismo */
    private static void maybeAddJoin(List<String> joins, String join) {
        if (!joins.contains(join)) joins.add(join);
    }



    @Override
    public Optional<Double> findAverageRatingForCreatedEvents(long userId) {
        TypedQuery<Double> query = em.createQuery("""
        SELECT AVG(r.rating)
        FROM Rating r
        WHERE r.event.user.id = :userId
          AND r.event.deleted = false
    """, Double.class);
        query.setParameter("userId", userId);

        Double result = query.getSingleResult();
        return result != null ? Optional.of(result) : Optional.empty();
    }

    @Override
    public Optional<Double> findAverageRatingForAttendedEvents(long userId) {
        TypedQuery<Double> query = em.createQuery("""
        SELECT AVG(r.rating)
        FROM Rating r
        WHERE r.event.id IN (
            SELECT ea.event.id
            FROM EventAttendance ea
            WHERE ea.user.id = :userId
        )
        AND r.event.deleted = false
    """, Double.class);
        query.setParameter("userId", userId);

        Double result = query.getSingleResult();
        return result != null ? Optional.of(result) : Optional.empty();
    }

}
