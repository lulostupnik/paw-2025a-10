package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.InterestDao;
import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static ar.edu.itba.paw.persistence.HibernateDaoUtils.fetchPageByIds;

@Repository
public class InterestHibernateDao implements InterestDao {
    @PersistenceContext
    private EntityManager em;

    private final UserDao userDao;

    @Autowired
    public InterestHibernateDao(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public Optional<Interest> findById(long id) {
        return Optional.ofNullable(em.find(Interest.class, id));
    }

    @Override
    public Optional<Interest> findByName(String name) {
        return em.createQuery("from Interest as i where i.name= :name", Interest.class)
                .setParameter("name", name)
                .getResultList()
                .stream()
                .findFirst();
    }

    @Override
    public Interest create(String interest) {
        final Interest i = new Interest(interest);
        em.persist(i);
        return i;
    }

    @Override
    public void update(long id, String interest) {
        final Interest i = em.find(Interest.class, id);
        if (i != null) {
            i.setName(interest);
        }
    }

    @Override
    public Page<Interest> findAll(PageParams pageParams) {
        final String countSql = """
                SELECT COUNT(*)
                FROM category i
                """;
        final String idSql = """
                SELECT i.id
                FROM category i
                """;
        final String jpqlFetch = """
                FROM Interest i
                WHERE i.id IN :ids
                """;

        return fetchPageByIds(
                em,
                countSql,
                idSql,
                Map.of(),
                jpqlFetch,
                Interest.class,
                pageParams
        );
    }

    @Override
    public Page<Interest> search(String searchTerm, PageParams pageParams) {
        final String pattern = JdbcDaoUtils.likePattern(searchTerm);

        final String sql = """
                SELECT i
                FROM Interest i
                WHERE LOWER(i.name) LIKE :pattern
                """;

        final String countSql = """
                SELECT COUNT(i)
                FROM Interest i
                WHERE LOWER(i.name) LIKE :pattern
                """;

        final String idSql = """
                SELECT i.id
                FROM Interest i
                WHERE LOWER(i.name) LIKE :pattern
                """;

        final String jpqlFetch = """
                FROM Interest i
                WHERE i.id IN :ids
                """;
        return fetchPageByIds(
                em,
                countSql,
                idSql,
                Map.of("pattern", pattern),
                jpqlFetch,
                Interest.class,
                pageParams
        );

    }

    @Override
    public void delete(long id) {
        final Interest i = em.find(Interest.class, id);
        if (i != null) {
            em.remove(i);
        }

    }

    @Override
    public List<Interest> findAllByUserId(long id) { // fixme: mover a User?
        User user = userDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return List.of();
    }

    @Override
    public Page<Interest> findAllByUserId(long id, PageParams pageParams) {  // fixme: mover a User?
        return null;
    }

    @Override
    public void createUserInterests(List<String> interests, long userId) {  // fixme: mover a User?
        User user = userDao.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        for (String interest : interests) {
            user.getInterests().add(new UserInterest(user, findByName( interest).orElseThrow(() -> new IllegalArgumentException("Interest not found"))));
        }
    }

    @Override
    public void createUserInterests(long[] interests, long userId) {  // fixme: mover a User?
        User user = userDao.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        for (long interest : interests) {
            user.getInterests().add(new UserInterest(user, findById(interest).orElseThrow( () -> new IllegalArgumentException("Interest not found"))));
        }
    }

    @Override
    public void updateScoreByInterest(Interest interest, long userId) {
        User user = userDao.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        for (UserInterest userInterest : user.getInterests()) { //fixme: mover esto al modelo
            if (userInterest.getInterest().getId().equals(interest.getId())) {
                userInterest.setScore(userInterest.getScore() + 1);
            }
        }

    }

    @Override
    public void updateUserInterests(long[] interestIds, long userId) {
        User user = userDao.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        for (long interestId : interestIds) { //fixme: mover esto al modelo
            Interest interest = findById(interestId)
                    .orElseThrow(() -> new IllegalArgumentException("Interest not found"));
            user.getInterests().add(new UserInterest(user, interest));
        }

    }

    @Override
    public void updateScoreByInterests(List<Interest> interests, long userId) {
        User user = userDao.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        for (Interest interest : interests) { //fixme: mover esto al modelo
            for (UserInterest userInterest : user.getInterests()) {
                if (userInterest.getInterest().getId().equals(interest.getId())) {
                    userInterest.setScore(userInterest.getScore() + 1);
                }
            }
        }

    }
}
