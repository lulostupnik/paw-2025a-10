package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.*;
import java.util.List;
import java.util.Optional;

public interface UserInterestDao {
    List<UserInterest> findAllByUser(User user);
    Page<UserInterest> findAllByUser(User user, PageParams pageParams);
    Optional<UserInterest> findById(long userId, long interestId);
    UserInterest create(long userId, long interestId);
    void delete(long userId, long interestId);
    void createUserInterests(List<String> interests, long userId);
    void createUserInterests(long[] interests, long userId);
    void updateUserInterests(long[] interestIds, long userId);
    void updateMatchingInterestScores(long responderUserId, long journeyCreatorUserId);
}
