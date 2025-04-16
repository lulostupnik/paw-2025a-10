package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Interest;

import java.util.List;
import java.util.Optional;

public interface InterestDao {

     Optional<Interest> findById(Long id);
     List<Interest> findAll();
     List<Interest> findByUserId(Long id); // method to find an interest by its name
     Optional<Interest> findByName(String name);
     List<Interest> findIdByName(String[] names);
     Optional<Interest> createUserInterest(Interest interest, Long userId);
     List<Interest> createUserInterests(String[] interests, Long userId);
     void updateScoreByInterest(Interest interest, Long userId);
     void updateScoreByInterests(List<Interest> interests, Long userId);

}
