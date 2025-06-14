package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.models.*;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.time.LocalDate;
import java.util.*;

import static ar.edu.itba.paw.persistence.HibernateDaoUtils.fetchPageByIds;
import static ar.edu.itba.paw.persistence.HibernateDaoUtils.likePattern;

@Repository
public class UserHibernateDao implements UserDao {
    @PersistenceContext
    private EntityManager em;

        @Override
        public User create(String email, String username, String firstname, String lastname, University university, Career career, long profilePictureId, String password, Locale locale, boolean validated) {
//            final User user = new User(email, username, firstname, lastname, university, career, profilePictureId, password, locale, validateToken, validateTokenExpiration);

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
    public Page<User> findAll(final PageParams pageParams) {
        final String countSql = "SELECT COUNT(*) FROM users";

        final String idSql = """
        SELECT u.id
        FROM users u
        ORDER BY u.id ASC
    """;

        final String jpqlFetch = """
        FROM User u
        WHERE u.id IN :ids
        ORDER BY u.id ASC
    """;

        return fetchPageByIds(em, countSql, idSql, Map.of(), jpqlFetch, User.class, pageParams,Map.of());
    }


    @Override
    public Page<User> search(final String search, final PageParams pageParams) {
        final String pattern = likePattern(search);

        final String countSql = """
        SELECT COUNT(*)
        FROM users u
        JOIN universities un ON u.university = un.id
        WHERE LOWER(u.firstname) LIKE LOWER( :pattern )
           OR LOWER(un.name) LIKE LOWER( :pattern )
           OR LOWER(u.email) LIKE LOWER( :pattern )
    """;

        final String idSql = """
        SELECT u.id
        FROM users u
        JOIN universities un ON u.university = un.id
        WHERE LOWER(u.firstname) LIKE LOWER( :pattern )
           OR LOWER(un.name) LIKE LOWER( :pattern )
           OR LOWER(u.email) LIKE LOWER( :pattern )
        ORDER BY u.id DESC
    """;

        final String jpqlFetch = "FROM User u WHERE u.id IN :ids ORDER BY u.id DESC";

        return fetchPageByIds(em, countSql, idSql, Map.of("pattern", pattern), jpqlFetch, User.class, pageParams,Map.of());
    }



    @Override//@TODO: mover a events
    public Page<User> findAllAttendeesByEventId(long eventId, PageParams pageParams) {
        return null;
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
