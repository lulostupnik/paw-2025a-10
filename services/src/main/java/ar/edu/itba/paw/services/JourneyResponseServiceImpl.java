package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.JourneyResponseDao;
import ar.edu.itba.paw.interfaces.services.JourneyResponseService;
import ar.edu.itba.paw.models.JourneyResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
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

    @Transactional(readOnly = true)
    @Override
    public List<JourneyResponse> listAllFromJourney(long journeyId) {
        return journeyResponseDao.listAllFromJourney(journeyId);
    }

    @Transactional
    @Override
    public void delete(long id, String message) {
        journeyResponseDao.deletionMessage(id, message);
        journeyResponseDao.delete(id);
    }


    @Transactional(readOnly = true)
    @Cacheable(value = "journeysByResponseId", key = "#journeyResponseId")
    @Override
    public long getJourneyIdByResponseId(long journeyResponseId) {
        return journeyResponseDao.getJourneyIdByResponseId(journeyResponseId);
    }

}
