package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.JourneyResponseDao;
import ar.edu.itba.paw.interfaces.services.JourneyResponseService;
import ar.edu.itba.paw.models.JourneyResponse;
import ar.edu.itba.paw.models.Page;
import ar.edu.itba.paw.models.PageParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class JourneyResponseServiceImpl implements JourneyResponseService {
    private final JourneyResponseDao journeyResponseDao;

    @Autowired
    public JourneyResponseServiceImpl(final JourneyResponseDao journeyResponseDao) {
        this.journeyResponseDao = journeyResponseDao;
    }

    @Transactional
    @Override
    public JourneyResponse create(long userId, String username, long journeyId, String message, LocalDateTime dateTime) {
        return journeyResponseDao.create(userId, username, journeyId, message, dateTime);
    }

    @Override
    public List<JourneyResponse> listAllFromJourney(long journeyId) {
        return journeyResponseDao.listAllFromJourney(journeyId);
    }

    @Transactional
    // @CacheEvict(value = "journeysByResponseId", key = "#id")
    @Override
    public void delete(long id, String message) {
        journeyResponseDao.deletionMessage(id, message);
        journeyResponseDao.delete(id);
    }


    // @Cacheable(value = "journeysByResponseId", key = "#journeyResponseId")
    @Override
    public long getJourneyIdByResponseId(long journeyResponseId) {
        return journeyResponseDao.getJourneyIdByResponseId(journeyResponseId);
    }

    @Transactional
    // @CacheEvict(value = "journeysByResponseId", allEntries = true) //FIXME: check if should CACHE EVICT
    @Override
    public void deleteByJourneyId(long journeyId) {
        journeyResponseDao.deleteByJourneyId(journeyId);
    }

    @Override
    public Page<JourneyResponse> listAllFromJourney(long journeyId, PageParams pageParams) {
        return journeyResponseDao.listAllFromJourney(journeyId, pageParams.getPage(), pageParams.getSize());
    }

    @Override
    public int getCount(long id) {
        return journeyResponseDao.getCount(id);
    }

}
