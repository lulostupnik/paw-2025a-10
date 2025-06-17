package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.*;
import java.util.Optional;

public interface JourneyResponseDao {
    Page<JourneyResponse> findAllByJourneyId(long journeyId, PageParams pageParams);
    Optional<JourneyResponse> findById(long id);
    JourneyResponse create(User user, Journey journey, String message);
    int countByJourneyId(long journeyId);
    Page<User> findRespondersByJourneyId(long journeyId, PageParams pageParams);

    void hardDeleteByJourneyId(long journeyId);
}

