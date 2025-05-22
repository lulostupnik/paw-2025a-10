package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.InterestDao;
import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.persistence.UserInterestDao;
import ar.edu.itba.paw.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


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
    public List<UserInterest> findAllByUser(User user) { // fixme: mover a User? O crear un UserInterestDao
        return em.createQuery("FROM UserInterest ui WHERE ui.user.id = :id", UserInterest.class)
                .setParameter("id", user.getId())
                .getResultList();
    }
    private List<UserInterest> findAllByUserId(long userId) { //fixme: mover a User? O crear un UserInterestDao
        return em.createQuery("FROM UserInterest ui WHERE ui.user.id = :id", UserInterest.class)
                .setParameter("id", userId)
                .getResultList();
    }
    @Override
    public Page<UserInterest> findAllByUser(User user, PageParams pageParams) {  //fixme: mover a User? O crear un UserInterestDao
        final String countSql = """
                SELECT COUNT(*)
                FROM user_interest ui
                WHERE ui.user_id = :id
                """;

        final String idSql = """
                SELECT ui.category_id
                FROM user_interest ui
                WHERE ui.user_id = :id
                """; // todo falta ORDER BY

        final String jpqlFetch = """
                FROM UserInterest ui
                WHERE ui.interest.id IN :ids and ui.user.id = :id
                """;

        return fetchPageByIds(em,  countSql,idSql, Map.of("id", user.getId()), jpqlFetch, UserInterest.class,pageParams,Map.of("id", user.getId()));
    }

    @Override
    public void createUserInterests(List<String> interests, long userId) {
        User user = userDao.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        for (String interest : interests) {
            Interest i = interestDao.findByName(interest)
                    .orElseGet(() -> interestDao.create(interest));
            create(user, i);
        }
    }

    @Override
    public void createUserInterests(long[] interests, long userId) {  // fixme: mover a User? O crear un UserInterestDao
        User user = userDao.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        for( long interestId : interests) {
            Interest i = interestDao.findById(interestId)
                    .orElseThrow(() -> new IllegalArgumentException("Interest not found"));
            create(user, i);
        }
    }

    @Override
    public void updateScoreByInterest(Interest interest, long userId) { //fixme: mover a User? O crear un UserInterestDao
        for (UserInterest userInterest : findAllByUserId(userId)) { //fixme: mover esto al modelo
            if (userInterest.getInterest().getId().equals(interest.getId())) {
                userInterest.setScore(userInterest.getScore() + 1);
            }
        }

    }

    @Override
    public void updateUserInterests(long[] interestIds, long userId) { // fixme: revisar eficiencia
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
                    .orElseThrow(() -> new IllegalArgumentException("Interest not found"));
            create(userDao.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found")), i);
        }

    }

    @Override
    public void updateScoreByInterests(List<Interest> interests, long userId) { // fixme: mover a User? O crear un UserInterestDao
        for (Interest interest : interests) { //fixme: mover esto al modelo
            for (UserInterest userInterest : findAllByUserId(userId)) {
                if (userInterest.getInterest().getId().equals(interest.getId())) {
                    userInterest.setScore(userInterest.getScore() + 1);
                }
            }
        }

    }


}
