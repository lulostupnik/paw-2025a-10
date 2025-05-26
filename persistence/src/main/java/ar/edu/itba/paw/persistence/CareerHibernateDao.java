package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.CareerDao;
import ar.edu.itba.paw.models.Career;
import ar.edu.itba.paw.models.Page;
import ar.edu.itba.paw.models.PageParams;
import ar.edu.itba.paw.models.User;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static ar.edu.itba.paw.persistence.HibernateDaoUtils.fetchPageByIds;
import static ar.edu.itba.paw.persistence.JdbcDaoUtils.likePattern;

@Repository
public class CareerHibernateDao implements CareerDao {
    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<Career> findById(long id) { //fixme agregar lo de not deleted

        return Optional.ofNullable(em.find(Career.class, id));
    }

    @Override
    public Optional<Career> findByName(String name) { ///fixme agregar lo de not deleted
        return em.createQuery("from Career as c where c.name= :name", Career.class)
                .setParameter("name", name)
                .getResultList()
                .stream()
                .findFirst();
    }

    @Override
    public Page<Career> findAll(PageParams pageParams) {
        final String countSql = """
                SELECT COUNT(*)
                FROM careers c
                WHERE c.deleted = false
                """;

        final String idSql = """
                SELECT c.id
                FROM careers c
                WHERE c.deleted = false
                """; // todo falta ORDER BY

        final String jpqlFetch = """
                FROM Career c
                WHERE c.id IN :ids
                """;
        return fetchPageByIds(
                em,
                countSql,
                idSql,
                Map.of(),
                jpqlFetch,
                Career.class,
                pageParams,
                Map.of()
        );
    }

    @Override
    public Page<Career> search(final String searchTerm, final PageParams pageParams) {
        final String pattern = likePattern(searchTerm);

        final String countSql = """
                SELECT COUNT(*) 
                FROM careers c
                WHERE LOWER(c.name) LIKE LOWER( :pattern )
                and c.deleted = false
                """;

        final String idSql = """
                SELECT c.id
                FROM careers c
                WHERE LOWER(c.name) LIKE LOWER( :pattern )
                and c.deleted = false
                """;
        final String jpqlFetch = """
                FROM Career c
                WHERE c.id IN :ids 
                """;

        return fetchPageByIds(
                em,
                countSql,
                idSql,
                Map.of("pattern", pattern),
                jpqlFetch,
                Career.class,
                pageParams,
                Map.of()
        );    }

    @Override
    public Career create(String name) {
        final Career career = new Career(name);
        em.persist(career);
        return career;
    }

    @Override
    public void update(long id, String name) {
        final Career career = em.find(Career.class, id);
        if (career != null) {
            career.setName(name);
            career.setDeleted(false);
            em.merge(career);
        }
    }

    @Override
    public void delete(long id) {
        final Career career = em.find(Career.class, id);
        if (career != null) {
            career.setDeleted(true);
            em.merge(career);
        }
    }
}
