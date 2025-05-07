package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.EventDao;
import ar.edu.itba.paw.interfaces.persistence.EventResponseDao;
import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.interfaces.services.EventResponseService;
import ar.edu.itba.paw.interfaces.services.EventService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class EventResponseServiceImpl implements EventResponseService {

    private final EventResponseDao eventResponseDao;

    private final EmailService emailService;
    private final EventDao eventDao; //@TODO CAMBIAR
    private final UserService userService;

    @Autowired
    public EventResponseServiceImpl(EventResponseDao eventResponseDao, EmailService emailService, EventDao eventDao, UserService userService) {
        this.eventResponseDao = eventResponseDao;
        this.emailService = emailService;
        this.eventDao = eventDao;
        this.userService = userService;
    }
    @Transactional
    @Override
    public EventResponse create(long userId, String username, long eventId, String message, LocalDateTime dateTime) {
        return eventResponseDao.create(userId, username, eventId, message, dateTime) ;
    }

    @Transactional
    @CacheEvict(value = "eventsByResponseId", key = "#id")
    @Override
    public void delete(long id, String message) {

        EventResponse deletedComment = findById(id)
                .orElseThrow(() ->
                    new IllegalArgumentException("Event response doesn't exist"));

        Event event = eventDao.findById(deletedComment.getEventId())
                .orElseThrow(() ->  new IllegalStateException("Event from event response doesn't exist"));


        User commentAuthor = userService.findById(deletedComment.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User from event response doesn't exist"));

        emailService.sendEventCommentDeletionNotification(deletedComment,event,commentAuthor, message );

        eventResponseDao.deletionMessage(id, message);
        eventResponseDao.delete(id);
    }

    @Override
    public int getCount(long eventId){
        return eventResponseDao.getCount(eventId);
    }

    @Transactional
    @CacheEvict(value = "eventsByResponseId", allEntries = true) //FIXME: check if should CACHE EVICT
    @Override
    public void deleteByEventId(long eventId) {
        eventResponseDao.deleteByEventId(eventId);
    }


    // @Cacheable(value = "eventsByResponseId", key = "#eventResponseId")
    @Override
    public long getEventIdByResponseId(long eventResponseId) {
        return eventResponseDao.getEventIdByResponseId(eventResponseId);
    }

    @Override
    public List<EventResponse> listAllFromEvent(long eventId) {
        return eventResponseDao.listAllFromEvent(eventId);
    }

    @Override
    public Page<EventResponse> listAllFromEvent(long eventId, PageParams pageParams) {
        return eventResponseDao.listAllFromEvent(eventId,pageParams.getPage(),pageParams.getSize());
    }

    @Override
    public Optional<EventResponse> findById(long id){
        return eventResponseDao.findById(id);
    }

    @Override
    public Optional<EventResponse> findByIdDeletedOrNotDeleted(long id){
        return eventResponseDao.findByIdDeletedOrNotDeleted(id);
    }


}
