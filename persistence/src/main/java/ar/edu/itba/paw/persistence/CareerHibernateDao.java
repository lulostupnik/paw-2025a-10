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
import java.util.Optional;

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
        final TypedQuery<Career> query = em.createQuery("from Career as c where c.deleted = false", Career.class);
        query.setFirstResult(pageParams.getPage() * pageParams.getSize());
        query.setMaxResults(pageParams.getSize());
        final List<Career> list = query.getResultList();
        final TypedQuery<Integer> countQuery = em.createQuery("select count(c) from Career as c", Integer.class);
        return new Page<>(list, pageParams.getPage(),JdbcDaoUtils.pageCount( countQuery.getSingleResult(), pageParams.getSize()) );
    }

    @Override
    public Page<Career> search(String substring, PageParams pageParams) {
        final TypedQuery<Career> query = em.createQuery("from Career as c where lower (c.name) like lower( :substring) and c.deleted = false", Career.class);
        query.setParameter("substring", "%" + likePattern( substring) + "%");
        query.setFirstResult(pageParams.getPage() * pageParams.getSize());
        query.setMaxResults(pageParams.getSize());
        final List<Career> list = query.getResultList();
        final TypedQuery<Integer> countQuery = em.createQuery("select count(c) from Career as c where lower (c.name) like lower( :substring) and c.deleted = false", Integer.class);
        countQuery.setParameter("substring", "%" + likePattern( substring) + "%");
        return new Page<>(list, pageParams.getPage(),JdbcDaoUtils.pageCount( countQuery.getSingleResult(), pageParams.getSize()) );
    }

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
