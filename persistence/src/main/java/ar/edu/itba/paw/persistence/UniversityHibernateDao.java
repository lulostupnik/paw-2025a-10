package ar.edu.itba.paw.persistence;
import ar.edu.itba.paw.interfaces.persistence.CityDao;
import ar.edu.itba.paw.interfaces.persistence.UniversityDao;
import ar.edu.itba.paw.models.City;
import ar.edu.itba.paw.models.Page;
import ar.edu.itba.paw.models.PageParams;
import ar.edu.itba.paw.models.University;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Map;
import java.util.Optional;

import static ar.edu.itba.paw.persistence.HibernateDaoUtils.fetchPageByIds;
import static ar.edu.itba.paw.persistence.HibernateDaoUtils.likePattern;


@Repository
public class UniversityHibernateDao implements UniversityDao {
    @PersistenceContext
    private EntityManager em;

    private final CityDao cityDao;

    @Autowired
    public  UniversityHibernateDao(CityDao cityDao) {
        this.cityDao = cityDao;
        // Default constructor for Spring
    }
    private Optional<University> findByNameAndCityWithDeleted(String name, String cityName) {
        return em.createQuery("from University as u where u.name= :name and u.city.name = :cityName", University.class)
                .setParameter("name", name)
                .setParameter("cityName", cityName)
                .getResultList()
                .stream()
                .findFirst();
    }

    @Override
    public University create(String name, String abbreviation, City city) {
        Optional <University> existingUniversity = findByNameAndCityWithDeleted(name, city.getName());
        if (existingUniversity.isPresent()) {
            if( !existingUniversity.get().isDeleted()) {
                throw new IllegalArgumentException("University with this name and city already exists and is not deleted."); //@TODO: change this to a custom exception
            }
            final University university = existingUniversity.get();
            university.setDeleted(false);
            university.setAbbreviation(abbreviation);
            university.setCity(city);
            em.merge(university);
            return university;
        }
        final University university = new University(name, abbreviation, city);
        em.persist(university);
        return university;
    }


    @Override
    public void delete(long id) {
        final University university = em.find(University.class, id);
        if (university != null) {
            university.setDeleted(true);
            em.merge(university);
        }

    }

    @Override
    public Optional<University> findByName(String name) {
        return em.createQuery("from University as u where u.name= :name and u.deleted = false", University.class)
                .setParameter("name", name)
                .getResultList()
                .stream()
                .findFirst();
    }

    @Override
    public Optional<University> findById(long id) {
        return em.createQuery("from University as u where u.id= :id and u.deleted = false", University.class)
                .setParameter("id", id)
                .getResultList()
                .stream()
                .findFirst();
    }

    @Override
    public Page<University> search(String searchTerm, PageParams pageParams) {
        final String pattern = likePattern(searchTerm);

        final String countSql = """
                SELECT COUNT(*) 
                FROM universities u
                WHERE (LOWER(u.name) like LOWER( :pattern )  OR LOWER (u.abbreviation) like LOWER( :pattern ) ) and u.deleted = false
                """;
        final String idSql = """
                SELECT u.id
                FROM universities u
                WHERE (LOWER(u.name) like LOWER( :pattern )  OR LOWER (u.abbreviation) like LOWER( :pattern ) ) and u.deleted = false
                """;
        final String jpqlFetch = """
                FROM University u
                WHERE u.id IN :ids
                """;
        return fetchPageByIds(
                em,
                countSql,
                idSql,
                Map.of("pattern", pattern),
                jpqlFetch,
                University.class,
                pageParams,
                Map.of()
        );
    }

    @Override
    public Page<University> findAll(PageParams pageParams) {
        final String countSql = """
                SELECT COUNT(*)
                FROM universities u
                WHERE u.deleted = false
                """;
        final String idSql = """
                SELECT u.id
                FROM universities u
                WHERE u.deleted = false
                """;

        final String jpqlFetch = """
                FROM University u
                WHERE u.id IN :ids
                """;

        return fetchPageByIds(
                em,
                countSql,
                idSql,
                Map.of(),
                jpqlFetch,
                University.class,
                pageParams,
                Map.of()
        );
    }
}
