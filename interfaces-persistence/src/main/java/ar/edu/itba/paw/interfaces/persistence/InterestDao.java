package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Interest;
import ar.edu.itba.paw.models.Page;
import ar.edu.itba.paw.models.PageParams;
import java.util.List;
import java.util.Optional;

public interface InterestDao {
     Optional<Interest> findById(long id);
     Optional<Interest> findByName(String name);
     Interest create(String interest);

     Page<Interest> findAll(PageParams pageParams);
     Page<Interest> search(String searchTerm, PageParams pageParams);
     void delete(long id);



}
