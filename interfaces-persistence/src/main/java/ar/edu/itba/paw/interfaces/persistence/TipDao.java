package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Journey;
import ar.edu.itba.paw.models.Page;
import ar.edu.itba.paw.models.PageParams;
import ar.edu.itba.paw.models.Tip;
import java.util.Optional;

public interface TipDao {
    void createTip(Journey journey, String title, String content);

    void deleteTip(long tipId);

    Optional<Tip> findTipById(long tipId);

    Page<Tip> findTipsByJourney(Journey journey, PageParams pageParams);
}
