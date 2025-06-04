package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.*;

import java.util.List;

public interface UserInterestDao {
    List<UserInterest> findAllByUser(User user);
     Page<UserInterest> findAllByUser(User user, PageParams pageParams);
     void createUserInterests(List<String> interests, long userId);
     void createUserInterests(long[] interests, long userId);
     void updateScoreByInterest(Interest interest, long userId);
     void updateUserInterests(long[] interestIds, long userId);
     void updateScoreByInterests(List<Interest> interests, long userId);
     void updateMatchingInterestScores(long responderUserId, long journeyCreatorUserId);
}
