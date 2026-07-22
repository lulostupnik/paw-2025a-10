package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.CountryDao;
import ar.edu.itba.paw.models.Country;
import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Repository
public class CountryHibernateDao implements CountryDao {
    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Country> findAll() {
        return em.createQuery("from Country as c", Country.class)
                .getResultList();
    }

    @Override
    public Optional<Country> findById(long id) {
        return Optional.ofNullable(em.find(Country.class, id));
    }
}
