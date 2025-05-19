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
        public User create(String email, String username, String firstname, String lastname, University university, Career career, long profilePictureId, String password, Locale locale, String validateToken, LocalDate validateTokenExpiration) {
//            final User user = new User(email, username, firstname, lastname, university, career, profilePictureId, password, locale, validateToken, validateTokenExpiration);

            final User user = new User(email, username,  firstname, lastname, university,  career, profilePictureId, password, locale);

            em.persist(user);

            return user;
        }
    @Override
    public Optional<User> findById(long id) {
        return Optional.ofNullable( em.find(User.class, id));
    }

    @Override
    public void updatePasswordAndClearTokenByToken(String token, String newPassword) {
            //@todo
    }

    @Override
    public void updateToken(long id, String uuid, LocalDate date) {
            //@todo
    }

    @Override
    public boolean findValidationStatusByEmail(String email) {
        return true; //@todo
    }

    @Override
    public Optional<UserAuthInfo> updateValidationAndFindAuthInfoByToken(String token) {
        return Optional.empty(); //@todo
    }

    //@Todo
    @Override
    public Optional<UserAuthInfo> findAuthInfoByEmail(final String email) {
        Query query = em.createNativeQuery("""
        SELECT email, password, roles, blocked, validated
        FROM users
        WHERE email = :email
    """);
        query.setParameter("email", email);

        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();

        return results.stream().findFirst().map(row ->
                new UserAuthInfo(
                        (String) row[0],  // email
                        (String) row[1],  // password
                        (String) row[2],  // roles
                        (Boolean) row[3], // blocked
                        (Boolean) row[4]  // validated → verified
                )
        ); //@TOdo preguntar. se puede hacer sin nativeQuery?
    }
/*
    @Override
    public Optional<UserAuthInfo> findAuthInfoByEmail(final String email) {
        TypedQuery<UserAuthInfo> query = em.createQuery("""
        SELECT new ar.edu.itba.paw.models.UserAuthInfo(
            u.email, u.password, u.roles, u.blocked, u.validated
        )
        FROM User u
        WHERE u.email = :email
    """, UserAuthInfo.class);
        query.setParameter("email", email);

        return query.getResultList().stream().findFirst();
    } //@TOdo esta es otra opcion capaz. PREGUNTAR !
*/


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
    public Optional<Boolean> findValidatedByTokenNotExpired(String token) {
        return Optional.empty(); //todo
    }

    @Override
    public Optional<User> findByToken(String token) {
        return Optional.empty(); //todo
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

        return fetchPageByIds(em, countSql, idSql, Map.of(), jpqlFetch, User.class, pageParams);
    }


    @Override
    public Page<User> search(final String search, final PageParams pageParams) {
        final String pattern = likePattern(search);

        final String countSql = """
        SELECT COUNT(*)
        FROM users u
        JOIN universities un ON u.university = un.id
        WHERE LOWER(u.firstname) LIKE :pattern
           OR LOWER(un.name) LIKE :pattern
           OR LOWER(u.email) LIKE :pattern
    """;

        final String idSql = """
        SELECT u.id
        FROM users u
        JOIN universities un ON u.university = un.id
        WHERE LOWER(u.firstname) LIKE :pattern
           OR LOWER(un.name) LIKE :pattern
           OR LOWER(u.email) LIKE :pattern
        ORDER BY u.id DESC
    """;

        final String jpqlFetch = "FROM User u WHERE u.id IN :ids ORDER BY u.id DESC";

        return fetchPageByIds(em, countSql, idSql, Map.of("pattern", pattern), jpqlFetch, User.class, pageParams);
    }


    @Override
    public boolean existsByTokenNotExpired(String token) {
        return true; //todo
    }

    @Override
    public boolean existsByTokenExpired(String token) {
        return true; //todo
    }

    @Override
    public void updateTokenAndExpirationByToken(String newToken, LocalDate date, String oldToken) {
        //todo
    }



    @Override//@TODO: mover a events
    public Page<User> findAllAttendeesByEventId(long eventId, PageParams pageParams) {
        return null;
    }


}
