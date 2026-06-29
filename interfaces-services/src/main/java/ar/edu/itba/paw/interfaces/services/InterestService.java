package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.*;
import java.util.List;
import java.util.Optional;

public interface InterestService {
    Interest createInterest(String interest);

    Interest updateInterest(long id, String interest);
    Interest patchInterest(long id, String interest);
    void deleteInterest(long id);

    void createUserInterests(List<Long> interestIds, long userId);
    void updateUserInterestScores(List<UserInterest> interests);

    Optional<Interest> findInterestById(long id);
    Optional<Interest> findInterestByName(String name);
    Page<UserInterest> findInterestsByUser(User user, PageParams pageParams);
    Optional<UserInterest> findUserInterest(long userId, long interestId);
    UserInterest addUserInterest(long userId, long interestId);
    void removeUserInterest(long userId, long interestId);
    void updateUserInterests(final long[] interestIds, final long userId); //no devuelve la lista de intereses porque esa búsqueda está paginada
    Page<Interest> findInterests(String search, PageParams pageParams);
    void updateMatchingInterestScores(long responderUserId, long journeyCreatorUserId);
}
