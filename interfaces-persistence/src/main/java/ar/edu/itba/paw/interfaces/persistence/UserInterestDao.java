package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Interest;
import ar.edu.itba.paw.models.Page;
import ar.edu.itba.paw.models.PageParams;
import java.util.List;
import java.util.Optional;

public interface UserInterestDao {
    Interest create(String interest);
    void createAll(List<String> interests, long userId);

    Page<Interest> findAllInterestsByUserId(long id, PageParams pageParams);
    void delete(long id);


    // ¿Esto va en este DAO?
    List<Interest> findAllByUserId(long id);
    Page<Interest> findAllByUserId(long id, PageParams pageParams);

    void saveUserInterests(List<String> interests, long userId);
    void saveUserInterests(long[] interests, long userId);
    void updateScoreByInterest(Interest interest, long userId);
    void updateUserInterests(long[] interestIds, long userId);
    void updateScoreByInterests(List<Interest> interests, long userId);

}
