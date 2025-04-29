package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Interest;

import java.util.List;
import java.util.Optional;

public interface InterestService {
    Optional<Interest> findById(long id);
    List<Interest> findAll();
    List<Interest> findByUserId(long id);
    Optional<Interest> findByName(String name);
    List<Interest> findIdByName(String[] names);
    Interest createUserInterest(String interestEn, String interestEs);
    void saveUserInterests(long[] interests, long userId);
    void updateScoreByInterest(Interest interest, long userId);
    void updateScoreByInterests(List<Interest> interests, long userId);

}
