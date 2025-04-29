package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.EventResponseDao;
import ar.edu.itba.paw.interfaces.services.EventResponseService;
import ar.edu.itba.paw.models.EventResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EventResponseServiceImpl implements EventResponseService {

    EventResponseDao eventResponseDao;

    @Autowired
    public EventResponseServiceImpl(EventResponseDao eventResponseDao) {
        this.eventResponseDao = eventResponseDao;
    }
    @Transactional
    @Override
    public EventResponse create(long userId, String username, long eventId, String message, LocalDateTime dateTime) {
        return eventResponseDao.create(userId, username, eventId, message, dateTime) ;
    }

    @Transactional
    @Override
    public void delete(long id, String message) {
        eventResponseDao.deletionMessage(id, message);
        eventResponseDao.delete(id);
    }


    @Transactional(readOnly = true)
    @Cacheable(value = "eventsByResponseId", key = "#eventResponseId")
    @Override
    public long getEventIdByResponseId(long eventResponseId) {
        return eventResponseDao.getEventIdByResponseId(eventResponseId);
    }
    @Transactional(readOnly = true)
    @Override
    public List<EventResponse> listAllFromEvent(long eventId) {
        return eventResponseDao.listAllFromEvent(eventId);
    }
}
