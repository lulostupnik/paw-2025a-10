package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Interest;
import ar.edu.itba.paw.models.Page;
import ar.edu.itba.paw.models.PageParams;

import java.util.List;
import java.util.Optional;

public interface InterestService {
    Interest createInterest(String interest);

    void updateInterest(long id, String interest);
    void deleteInterest(long id);

    void createUserInterests(List<String> interests, long userId);


    Optional<Interest> findInterestById(long id);
    List<Interest> findInterestsByUserId(long id);
    Optional<Interest> findInterestByName(String name);
    Page<Interest> findInterestsByUserId(long id, PageParams pageParams);
    void updateUserInterestScores(List<Interest> interests, long userId);
    void updateUserInterests(final long[] interestIds, final long userId);
    Page<Interest> findInterests(String search, PageParams pageParams);
}
