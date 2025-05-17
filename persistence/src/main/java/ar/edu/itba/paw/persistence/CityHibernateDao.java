package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.CityDao;
import ar.edu.itba.paw.models.*;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

import static ar.edu.itba.paw.persistence.JdbcDaoUtils.likePattern;


@Repository
public class CityHibernateDao implements CityDao {
    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<City> findById(long id) {
        return Optional.ofNullable(em.find(City.class, id));
    }

    @Override
    public Optional<City> findByName(String name) {
        return em.createQuery("from City as c where c.name= :name", City.class)
                .setParameter("name", name)
                .getResultList()
                .stream()
                .findFirst();
    }

    @Override
    public Page<City> search(String substring, PageParams pageParams) {
        final TypedQuery<City> query = em.createQuery("from City as ci where (lower (ci.name) like lower( :substring)  OR lower (ci.country.name) like lower( :substring) )and ci.deleted = false", City.class);
        query.setParameter("substring", "%" + likePattern( substring) + "%");
        query.setFirstResult(pageParams.getPage() * pageParams.getSize());
        query.setMaxResults(pageParams.getSize());
        final List<City> list = query.getResultList();
        final TypedQuery<Integer> countQuery = em.createQuery("select count(ci) from City as ci where (lower (ci.name) like lower( :substring)  OR lower (ci.country.name) like lower( :substring) ) and c.deleted = false", Integer.class);
        countQuery.setParameter("substring", "%" + likePattern( substring) + "%");
        return new Page<>(list, pageParams.getPage(),JdbcDaoUtils.pageCount( countQuery.getSingleResult(), pageParams.getSize()) );
    }

    @Override
    public Page<City> findAll(PageParams pageParams) {
        final TypedQuery<City> query = em.createQuery("from City as c where c.deleted = false", City.class);
        query.setFirstResult(pageParams.getPage() * pageParams.getSize());
        query.setMaxResults(pageParams.getSize());
        final List<City> list = query.getResultList();
        final TypedQuery<Integer> countQuery = em.createQuery("select count(c) from City as c", Integer.class);
        return new Page<>(list, pageParams.getPage(),JdbcDaoUtils.pageCount( countQuery.getSingleResult(), pageParams.getSize()) );
    }

    @Override
    public void update(long id, String name, Country country) {
        final City city = em.find(City.class, id);
        if (city != null) {
            city.setName(name);
            city.setCountry(country);
            em.merge(city);
        }


    }

    @Override
    public City create(String nameEn, Country country) {
        final City city = new City(nameEn, country);
        em.persist(city);
        return city;
    }

    @Override
    public void delete(long id) {
        final City city = em.find(City.class, id);
        if (city != null) {
            city.setDeleted(true);
            em.merge(city);
        }
    }
}
