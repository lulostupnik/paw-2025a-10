package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.InterestDao;
import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Map;
import java.util.Optional;

import static ar.edu.itba.paw.persistence.HibernateDaoUtils.fetchPageByIds;
import static ar.edu.itba.paw.persistence.HibernateDaoUtils.likePattern;

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
    public Page<Interest> findAll(PageParams pageParams) {
        final String countSql = """
                SELECT COUNT(*)
                FROM category i
                """;
        final String idSql = """
                SELECT i.id
                FROM category i
                ORDER BY i.id
                """;
        final String jpqlFetch = """
                FROM Interest i
                WHERE i.id IN :ids
                ORDER BY i.id
                """;

        return fetchPageByIds(
                em,
                countSql,
                idSql,
                Map.of(),
                jpqlFetch,
                Interest.class,
                pageParams,
                Map.of()
        );
    }

    @Override
    public Page<Interest> search(String searchTerm, PageParams pageParams) {
        final String pattern = likePattern(searchTerm);

        final String sql = """
                SELECT i
                FROM Interest i
                WHERE LOWER(i.name) LIKE LOWER( :pattern )
                """;

        final String countSql = """
                SELECT COUNT(*)
                FROM Category i
                WHERE LOWER(i.name) LIKE LOWER( :pattern )
                """;

        final String idSql = """
                SELECT i.id
                FROM category i
                WHERE LOWER(i.name) LIKE LOWER( :pattern )
                ORDER BY i.id
                """;

        final String jpqlFetch = """
                FROM Interest i
                WHERE i.id IN :ids
                ORDER BY i.id
                """;
        return fetchPageByIds(
                em,
                countSql,
                idSql,
                Map.of("pattern", pattern),
                jpqlFetch,
                Interest.class,
                pageParams,
                Map.of()
        );

    }

    @Override
    public void delete(long id) {
        final Interest i = em.find(Interest.class, id);
        if (i != null) {
            em.remove(i);
        }

    }
}
