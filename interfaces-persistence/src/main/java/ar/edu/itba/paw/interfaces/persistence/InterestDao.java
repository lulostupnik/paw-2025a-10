package ar.edu.itba.paw.interfaces.persistence;

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
     Interest createUserInterest(String interest);
     void deleteUserInterest(long id);
     void saveUserInterests(List<String> interests, long userId);
     void editUserInterest(long id, String interest);
     void saveUserInterests(long[] interests, long userId);
     void updateScoreByInterest(Interest interest, Long userId);

     void updateUserInterests(List<Long> interestIds, long userId);

     void updateScoreByInterests(List<Interest> interests, Long userId);
     Page<Interest> getAllInterests(int page, int pageSize);
     Page<Interest> searchBySubstring(String search, int page, int pageSize);
     Page<Interest> findAllInterestsByUserId(long id, int page, int pageSize);
     void delete(long id);
}
