package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.models.*;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Repository
public class UserHibernateDao implements UserDao {
    @PersistenceContext
    private EntityManager em;

    @Override
    public User create(String email, String username, String firstname, String lastname, University university, Career career, long profilePictureId, String password, Locale locale, String validateToken, LocalDate validateTokenExpiration) {
        final User user = new User(email, username, firstname, lastname, university, career, profilePictureId, password, locale, validateToken, validateTokenExpiration);
        em.persist(user);
        return user;
    }

    @Override
    public void updatePasswordAndClearTokenByToken(String token, String newPassword) {

    }

    @Override
    public Optional<User> findById(long id) {
        return Optional.ofNullable( em.find(User.class, id));
    }

    @Override
    public void updateToken(long id, String uuid, LocalDate date) {

    }

    @Override
    public boolean findValidationStatusByEmail(String email) {
        return false;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        final TypedQuery<User> query = em.createQuery("from User as u where u.email= :email", User.class);
        query.setParameter("email", email);
        final List<User> list = query.getResultList();
        return list.isEmpty() ? Optional.empty() : Optional.of(list.getFirst());
    }

    @Override
    public Optional<UserAuthInfo> updateValidationAndFindAuthInfoByToken(String token) {
        return Optional.empty();
    }

    @Override
    public Optional<UserAuthInfo> findAuthInfoByEmail(String email) {
        return Optional.empty();
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
    public void updatePassword(long id, String password) {

    }

    @Override
    public void updateBlock(long id, boolean bool) {

    }

    @Override
    public Optional<Boolean> findValidatedByTokenNotExpired(String token) {
        return Optional.empty();
    }

    @Override
    public Optional<User> findByToken(String token) {
        return Optional.empty();
    }

    @Override
    public Page<User> findAll(PageParams pageParams) {
        final TypedQuery<User> query = em.createQuery("from User as u", User.class);
        query.setFirstResult(pageParams.getPage() * pageParams.getSize());
        query.setMaxResults(pageParams.getSize());
        final List<User> list = query.getResultList();
        final TypedQuery<Integer> countQuery = em.createQuery("select count(u) from User as u", Integer.class);
        return new Page<>(list, pageParams.getPage(),JdbcDaoUtils.pageCount( countQuery.getSingleResult(), pageParams.getSize()) );

    }

    @Override
    public Page<User> search(String search, PageParams pageParams) {
            final String pattern = "%" + search.toLowerCase() + "%";

            // Conteo total
            Long total = em.createQuery("""
        SELECT COUNT(u)
        FROM User u
        WHERE LOWER(u.email) LIKE :pattern
           OR LOWER(u.username) LIKE :pattern
           OR LOWER(u.firstname) LIKE :pattern
        """, Long.class)
                    .setParameter("pattern", pattern)
                    .getSingleResult();

            // Datos paginados
            List<User> users = em.createQuery("""
        SELECT u
        FROM User u
        WHERE LOWER(u.email) LIKE :pattern
           OR LOWER(u.username) LIKE :pattern
           OR LOWER(u.firstname) LIKE :pattern
        ORDER BY u.id
        """, User.class)
                    .setParameter("pattern", pattern)
                    .setFirstResult(pageParams.getPage() * pageParams.getSize())
                    .setMaxResults(pageParams.getSize())
                    .getResultList();

            return new Page<>(users, pageParams.getPage(), JdbcDaoUtils.pageCount(total.intValue(), pageParams.getSize()));
        }

    @Override
    public boolean existsByTokenNotExpired(String token) {
        return false;
    }

    @Override
    public boolean existsByTokenExpired(String token) {
        return false;
    }

    @Override
    public void updateTokenAndExpirationByToken(String newToken, LocalDate date, String oldToken) {

    }

    @Override //@TODO: mover a journey
    public List<User> findAllJourneyResponders(long journeyId) {
        return List.of();
    }

    @Override//@TODO: mover a events
    public List<User> findAllEventResponders(long eventId) {
        return List.of();
    }

    @Override//@TODO: mover a events
    public Page<User> findAllAttendeesByEventId(long eventId, PageParams pageParams) {
        return null;
    }

    @Override //@TODO: mover a events
    public List<User> findAllAttendeesByEventId(long eventId) {
        return List.of();
    }
}
