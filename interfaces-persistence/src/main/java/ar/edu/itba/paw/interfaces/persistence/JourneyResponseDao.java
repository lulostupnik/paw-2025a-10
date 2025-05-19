package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.JourneyResponse;
import ar.edu.itba.paw.models.Page;
import ar.edu.itba.paw.models.PageParams;
import java.util.Optional;

public interface JourneyResponseDao {
    Page<JourneyResponse> findAllByJourneyId(long journeyId, PageParams pageParams);
    Optional<JourneyResponse> findById(long id);
}

