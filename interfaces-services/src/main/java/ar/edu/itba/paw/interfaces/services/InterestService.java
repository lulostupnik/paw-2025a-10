package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Interest;
import ar.edu.itba.paw.models.Page;
import ar.edu.itba.paw.models.PageParams;

import java.util.List;
import java.util.Optional;

public interface InterestService {
    Optional<Interest> findById(long id);
    List<Interest> findAll();
    List<Interest> findByUserId(long id);
    Optional<Interest> findByName(String name);
    List<Interest> findIdByName(List<String> names);
    Page<Interest> findAllInterestsByUserId(long id, PageParams pageParams);
    Interest createUserInterest(String interest);
    void deleteUserInterest(long id);
    void editUserInterest(long id, String interest);
    void saveUserInterests(long[] interests, long userId);
    void saveUserInterests(List<String> interests, long userId);
    void updateScoreByInterest(Interest interest, long userId);
    void updateScoreByInterests(List<Interest> interests, long userId);
    String getInterestsJSON(String search);

    void updateUserInterests(final long[] interestIds, final long userId);
    Page<Interest> getAllInterests(String search, PageParams pageParams);
    void delete(long id);
}
