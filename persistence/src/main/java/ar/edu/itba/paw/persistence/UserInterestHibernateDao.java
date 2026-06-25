package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.InterestDao;
import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.persistence.UserInterestDao;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.exceptions.InterestsNotFoundException;
import ar.edu.itba.paw.models.exceptions.UserNotFoundException;
import ar.edu.itba.paw.models.exceptions.UserInterestNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static ar.edu.itba.paw.persistence.HibernateDaoUtils.fetchPageByIds;

@Repository
public class UserInterestHibernateDao implements UserInterestDao {
    @PersistenceContext
    private EntityManager em;
    private final UserDao userDao;
    private final InterestDao interestDao;

    @Autowired
    public UserInterestHibernateDao(UserDao userDao, InterestDao interestDao) {
        this.interestDao = interestDao;
        this.userDao = userDao;
    }
    private UserInterest create(User user, Interest interest) {
        final UserInterest userInterest = new UserInterest(user, interest);
        em.persist(userInterest);
        return userInterest;
    }

    @Override
    public Optional<UserInterest> findById(long userId, long interestId) {
        List<UserInterest> results = em.createQuery(
                "FROM UserInterest ui WHERE ui.user.id = :userId AND ui.interest.id = :interestId",
                UserInterest.class
        )
                .setParameter("userId", userId)
                .setParameter("interestId", interestId)
                .getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public UserInterest create(long userId, long interestId) {
        User user = userDao.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        Interest interest = interestDao.findById(interestId)
                .orElseThrow(() -> new InterestsNotFoundException(interestId));
        return create(user, interest);
    }

    @Override
    public void delete(long userId, long interestId) {
        UserInterest userInterest = findById(userId, interestId)
                .orElseThrow(() -> new UserInterestNotFoundException(userId, interestId));
        em.remove(userInterest);
    }

    @Override
    public List<UserInterest> findAllByUser(User user) {
        return em.createQuery("FROM UserInterest ui WHERE ui.user.id = :id", UserInterest.class)
                .setParameter("id", user.getId())
                .getResultList();
    }
    private List<UserInterest> findAllByUserId(long userId) {
        return em.createQuery("FROM UserInterest ui WHERE ui.user.id = :id", UserInterest.class)
                .setParameter("id", userId)
                .getResultList();
    }
    @Override
    public Page<UserInterest> findAllByUser(User user, PageParams pageParams) {
        final String countSql = """
                SELECT COUNT(*)
                FROM user_interest ui
                WHERE ui.user_id = :id
                """;

        final String idSql = """
                SELECT ui.category_id
                FROM user_interest ui
                WHERE ui.user_id = :id
                ORDER BY ui.category_id
                """;

        final String jpqlFetch = """
                FROM UserInterest ui
                WHERE ui.interest.id IN :ids and ui.user.id = :id
                """;

        return fetchPageByIds(em,  countSql,idSql, Map.of("id", user.getId()), jpqlFetch, UserInterest.class,pageParams,Map.of("id", user.getId()));
    }

    @Override
    public void createUserInterests(List<String> interests, long userId) {
        User user = userDao.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        for (String interest : interests) {
            Interest i = interestDao.findByName(interest)
                    .orElseGet(() -> interestDao.create(interest));
            create(user, i);
        }
    }

    @Override
    public void createUserInterests(long[] interests, long userId) {
        User user = userDao.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        for( long interestId : interests) {
            Interest i = interestDao.findById(interestId)
                    .orElseThrow(() -> new InterestsNotFoundException(interestId));
            create(user, i);
        }
    }


    /**
     * Nota: Este método está en el DAO porque obtener todos los intereses del usuario
     * desde el servicio podría ser ineficiente si tiene muchos intereses.
     */
    @Override
    public void updateUserInterests(long[] interestIds, long userId) {
        List<UserInterest> userInterests = findAllByUserId(userId);
        List<Long> interestsToAdd = new java.util.ArrayList<>(Arrays.stream(interestIds).boxed().toList());
        for (UserInterest userInterest : userInterests) {
          if(!interestsToAdd.contains(userInterest.getInterest().getId())) {
              em.remove(userInterest);
          } else {
              interestsToAdd.remove(userInterest.getInterest().getId());
          }
        }
        for (Long interestId : interestsToAdd) {
            Interest i = interestDao.findById(interestId)
                    .orElseThrow(() -> new InterestsNotFoundException(interestId));
            create(userDao.findById(userId).orElseThrow(() -> new UserNotFoundException(userId)), i);
        }

    }


    /**
     * Nota: Este update se hace en un DAO porque hacerlo en servicios
     * requeriría iterar a través de páginas (ya que no hay límite en la cantidad de intereses),
     * y generar N updates individuales. Con esta query nativa logramos:
     * - Código más limpio (1 query vs loops anidados con lógica de paginación compleja)
     * - Mayor eficiencia (1 UPDATE bulk vs N updates individuales)
     */
    @Override
    public void updateMatchingInterestScores(long responderUserId, long journeyCreatorUserId) {

        em.createNativeQuery("""
        UPDATE user_interest
        SET score = score + 1
        WHERE user_id = :responderUserId
          AND category_id IN (
              SELECT category_id
              FROM user_interest
              WHERE user_id = :journeyCreatorUserId
          )
    """)
                .setParameter("responderUserId", responderUserId)
                .setParameter("journeyCreatorUserId", journeyCreatorUserId)
                .executeUpdate();
    }


}
