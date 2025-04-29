package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.CursorPage;
import ar.edu.itba.paw.models.Interest;
import ar.edu.itba.paw.models.Page;

import java.util.List;
import java.util.Optional;

public interface InterestDao {

     Optional<Interest> findById(Long id);
     List<Interest> findAll();
     List<Interest> findByUserId(Long id);
     Optional<Interest> findByName(String name);
     List<Interest> findIdByName(String[] names);
     Optional<Interest> createUserInterest(Interest interest, Long userId);
     List<Interest> createUserInterests(String[] interests, Long userId);
     void updateScoreByInterest(Interest interest, Long userId);
     void updateScoreByInterests(List<Interest> interests, Long userId);
     Page<Interest> getAllInterests(int page, int pageSize);


}
