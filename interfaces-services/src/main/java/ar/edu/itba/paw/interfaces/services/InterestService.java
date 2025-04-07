package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Interest;

import java.util.List;
import java.util.Optional;

public interface InterestService {
    Optional<Interest> findById(Long id);
    List<Interest> findAll();
    List<Interest> findByUserId(Long id);
    Optional<Interest> findByName(String name);
    List<Interest> findIdByName(String[] names);
    Optional<Interest> createUserInterest(Interest interest, Long userId);
    List<Interest> createUserInterests(String[] interests, Long userId);
}
