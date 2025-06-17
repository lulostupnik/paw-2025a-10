package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Journey;
import ar.edu.itba.paw.models.Page;
import ar.edu.itba.paw.models.PageParams;
import ar.edu.itba.paw.models.Tip;
import java.util.Optional;

public interface TipDao {
    Tip create(Journey journey, String title, String content);

    void delete(long tipId);

    void deleteByJourney(long journeyId);

    Optional<Tip> findById(long tipId);

    Page<Tip> findByJourney(Journey journey, PageParams pageParams);
}
