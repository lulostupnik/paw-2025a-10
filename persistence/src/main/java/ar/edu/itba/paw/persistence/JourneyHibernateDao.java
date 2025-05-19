package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.JourneyDao;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.enums.SortDirection;
import ar.edu.itba.paw.models.enums.SortFieldJourney;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class JourneyHibernateDao implements JourneyDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Journey create(User user, University university, LocalDate startDate, LocalDate endDate, String description) {
        final Journey journey = new Journey(user, startDate, endDate, university, description);
        em.persist(journey);
        return journey;
    }

    @Override
    public Optional<Journey> findById(long id) {

        return Optional.ofNullable(em.find(Journey.class, id));
    }

    @Override
    public Optional<Journey> findByUserId(long userId) {
        final TypedQuery<Journey> query = em.createQuery("from Journey as j where j.user.id = :userId", Journey.class);
        query.setParameter("userId", userId);
        final List<Journey> list = query.getResultList();
        return list.isEmpty() ? Optional.empty() : Optional.of(list.getFirst());
    }


    @Override
    public Page<Journey> findAll(PageParams pageParams) {
//        final TypedQuery<Journey> query = em.createQuery("from Journey as j", Journey.class);
//        query.setFirstResult(pageParams.getPage() * pageParams.getSize());
//        query.setMaxResults(pageParams.getSize());
//        final List<Journey> list = query.getResultList();
//        final TypedQuery<Integer> countQuery = em.createQuery("select count(j) from Journey as j", Integer.class);
//        return new Page<>(list, pageParams.getPage(),JdbcDaoUtils.pageCount( countQuery.getSingleResult(), pageParams.getSize()) );
        return null;
    }

    @Override
    public Page<Journey> findByOriginCity(long originCityId, PageParams pageParams) {
//        final TypedQuery<Journey> query = em.createQuery("from Journey as j where j.user.university.city.id = :originCityId", Journey.class);
//        query.setParameter("originCityId", originCityId);
//        query.setFirstResult(pageParams.getPage() * pageParams.getSize());
//        query.setMaxResults(pageParams.getSize());
//        final List<Journey> list = query.getResultList();
//        final TypedQuery<Integer> countQuery = em.createQuery("select count(j) from Journey as j ", Integer.class);
//        return new Page<>(list, pageParams.getPage(),JdbcDaoUtils.pageCount( countQuery.getSingleResult(), pageParams.getSize()) );
        return null;
    }

    @Override
    public Page<Journey> search(String search, PageParams pageParams) {
        return null;
    }

    @Override
    public Page<Journey> search(String search, Long userId, SortFieldJourney orderBy, SortDirection direction, String city, LocalDate startDate, LocalDate endDate, String interest, boolean isPast, boolean isUpcoming, boolean isMyDestination, boolean isOngoing, PageParams pageParams) {
        return null;
    }


    @Override
    public Page<Journey> findRecommended(String email, PageParams pageParams) {
        return null;
    }
}



//
//@Override
//public void delete(long id) {
//    final Journey journey = em.find(Journey.class, id);
//    if (journey != null) {
//        journey.setDeleted(true);
//        em.merge(journey);
//    }
//
//}

//
//@Override
//public void update(long journeyId, University destinationUniversity, LocalDate startDate, LocalDate endDate, String description) {
//    final Journey journey = em.find(Journey.class, journeyId);
//    if (journey != null) {
//        journey.setDestinationUniversity(destinationUniversity);
//        journey.setStartDate(startDate);
//        journey.setEndDate(endDate);
//        journey.setDescription(description);
//    }
//}

//
//    @Override
//    public void updateDeletionMessage(long id, String message) {
//        final Journey journey = em.find(Journey.class, id);
//        if (journey != null) {
//            journey.setDeletionMessage(message);
//            em.merge(journey);
//        }
//    }
