package ar.edu.itba.paw.persistence;
import ar.edu.itba.paw.interfaces.persistence.UniversityDao;
import ar.edu.itba.paw.models.City;
import ar.edu.itba.paw.models.Page;
import ar.edu.itba.paw.models.PageParams;
import ar.edu.itba.paw.models.University;
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


    @Override
    public University create(String name, String abbreviation, City city) {
        final University university = new University(name, abbreviation, city);
        em.persist(university);
        return university;
    }

    @Override
    public void update(long id, String newName, String newAbbreviation, String newCityName) {
        final University university = em.find(University.class, id);
        if (university != null) {
            university.setName(newName);
            university.setAbbreviation(newAbbreviation);
            university.getCity().setName(newCityName);
            em.merge(university);
        }
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
        return em.createQuery("from University as u where u.name= :name", University.class)
                .setParameter("name", name)
                .getResultList()
                .stream()
                .findFirst();
    }

    @Override
    public Optional<University> findById(long id) {
        return em.createQuery("from University as u where u.id= :id", University.class)
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
