package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.CityDao;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.exceptions.CityAlreadyExistsException;
import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Map;
import java.util.Optional;
import static ar.edu.itba.paw.persistence.HibernateDaoUtils.fetchPageByIds;
import static ar.edu.itba.paw.persistence.HibernateDaoUtils.likePattern;


@Repository
public class CityHibernateDao implements CityDao {
    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<City> findById(long id) {

        return em.createQuery("FROM City as c where c.id = :id and c.deleted = false", City.class)
                .setParameter("id", id)
                .getResultList()
                .stream()
                .findFirst();
    }

    @Override
    public Optional<City> findByName(String name) {
        return em.createQuery("FROM City as c where c.name= :name and c.deleted = false", City.class)
                .setParameter("name", name)
                .getResultList()
                .stream()
                .findFirst();
    }

    @Override
    public Page<City> search(String substring, PageParams pageParams) {
        final String pattern = likePattern(substring);

        final String countSql = """
                SELECT COUNT(*)
                FROM cities c JOIN countries co ON c.country_id = co.id
                WHERE (LOWER(c.name) like :pattern  OR LOWER (co.name) like :pattern ) and c.deleted = false
                """;

        final String idSql = """
                SELECT c.id
                FROM cities c JOIN countries co ON c.country_id = co.id
                WHERE (LOWER(c.name) like  :pattern   OR LOWER (co.name) like :pattern  ) and c.deleted = false
                """;

        final String jpqlFetch = """
                FROM City c
                WHERE c.id IN :ids
                """;

        return fetchPageByIds(
                em,
                countSql,
                idSql,
                Map.of("pattern", pattern),
                jpqlFetch,
                City.class,
                pageParams,
                Map.of()
        );
    }

    @Override
    public Page<City> findAll(PageParams pageParams) {
        final String countSql = """
                SELECT COUNT(*) 
                FROM cities c
                WHERE c.deleted = false
                """;

        final String idSql = """
                SELECT c.id
                FROM cities c
                WHERE c.deleted = false
                """;
        final String jpqlFetch = """
                FROM City c
                WHERE c.id IN :ids 
                """;
        return fetchPageByIds(
                em,
                countSql,
                idSql,
                Map.of(),
                jpqlFetch,
                City.class,
                pageParams,
                Map.of()
        );


    }

    private Optional<City> findByNameWithDeleted(String name) {
        return em.createQuery("from City as c where c.name = :name", City.class)
                .setParameter("name", name)
                .getResultList()
                .stream()
                .findFirst();
    }
    @Override
    public City create(String nameEn, Country country) {
        final Optional<City> existingCity = findByNameWithDeleted(nameEn);
        if (existingCity.isPresent()) {
            final City city = existingCity.get();
            if (city.isDeleted()) {
                city.setDeleted(false);
                city.setCountry(country);
                em.merge(city);
                return city;
            } else {
                throw new CityAlreadyExistsException(nameEn, country.getName());
            }
        }
        final City city = new City(nameEn, country);
        em.persist(city);
        return city;
    }


}
